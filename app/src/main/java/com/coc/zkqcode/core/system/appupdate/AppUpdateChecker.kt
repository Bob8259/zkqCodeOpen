package com.coc.zkqcode.core.system.appupdate

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Checks for a new app (APK) version on startup.
 *
 * The server endpoint `api/get-new-version` uses the same X25519 + Blake2b + ChaCha20
 * crypto scheme as session login. The client sends only its ephemeral public key; the
 * server responds with an encrypted version number in `nonce=<hex>&data=<hex>` format.
 *
 * If the decrypted version is greater than 1, the new APK is downloaded from Gitee,
 * force-installed via `setsid` (so the install survives the current process termination),
 * and the app is relaunched.
 *
 * This runs in the core module (before the JAR is loaded) and therefore has its own
 * URL fallback logic mirroring [com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil].
 */
object AppUpdateChecker {

    private const val PRIMARY_URL = "https://zkqcoc.store/"
    private const val BACKUP_URL = "http://103.85.226.101/"
    private const val ENDPOINT = "api/get-new-version"

    // Gitee release download URL (URL-decoded form of the encoded path)
    private const val APK_DOWNLOAD_URL =
        "https://gitee.com/azikaban/zkqCodeOpen/releases/download/发行版/紫孔雀.apk"

    // Root-accessible temp path for the downloaded APK
    private const val APK_TEMP_PATH = "/data/local/tmp/zkqcode_update.apk"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Checks for a new app version. If the server returns a version > 1,
     * downloads the APK from Gitee, force-installs it via setsid, and launches it.
     *
     * This call is non-blocking (runs on [Dispatchers.IO]) and fails silently —
     * any error is logged via [showDebugInfo] and never prevents normal startup.
     */
    suspend fun checkForUpdate() = withContext(Dispatchers.IO) {
        try {
            // 1. Generate ephemeral X25519 keypair and derive session key (stored in native layer)
            val publicKeyHex = RustTools.encryptVersionCheckRequest()
            if (publicKeyHex.startsWith("Error")) {
                showDebugInfo("AppUpdateChecker: Failed to generate key: $publicKeyHex")
                return@withContext
            }

            // 2. POST public_key to the server (with URL fallback)
            val postData = "public_key=$publicKeyHex"
            val responseBody = executePostWithFallbackAndReadBody(ENDPOINT, postData)

            // 3. Decrypt the response: nonce=<hex>&data=<hex> → version string
            val decrypted = RustTools.decryptLoginResponse(responseBody)
            if (decrypted.startsWith("Error")) {
                showDebugInfo("AppUpdateChecker: Decryption failed: $decrypted")
                return@withContext
            }

            // 4. Parse version number
            val version = decrypted.trim().toIntOrNull()
            if (version == null) {
                showDebugInfo("AppUpdateChecker: Invalid version response: $decrypted")
                return@withContext
            }

            showDebugInfo("AppUpdateChecker: Server version = $version")

            // 5. If version > 1, download and install the new APK
            if (version > 1) {
                ShowMessage("检测到新版本，正在下载更新... / New version detected, downloading update...", isChecking = false)
                downloadAndInstall()
            }
        } catch (e: Exception) {
            showDebugInfo("AppUpdateChecker: Update check failed: ${e.message}")
        }
    }

    /**
     * Downloads the APK from Gitee to a temp path, then force-installs it via a
     * detached `setsid` session that also deletes the temp file and relaunches the app.
     */
    private suspend fun downloadAndInstall() {
        val apkFile = File(APK_TEMP_PATH)
        try {
            // Download APK from Gitee
            val request = Request.Builder().url(APK_DOWNLOAD_URL).get().build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    ShowMessage("下载更新失败：HTTP ${response.code} / Download failed: HTTP ${response.code}", isChecking = false)
                    return
                }
                response.body.byteStream().use { input ->
                    apkFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            if (!apkFile.exists() || apkFile.length() == 0L) {
                ShowMessage("下载更新失败：APK文件为空 / Download failed: APK file is empty", isChecking = false)
                return
            }

            ShowMessage("下载完成，正在安装更新... / Download complete, installing update...", isChecking = false)

            // Force install via setsid, delete the temp APK, then relaunch the app.
            // The setsid creates a new session so the install+launch survives the
            // current process termination (same pattern as LogHelper/ControlWindowService restart).
            val cmd = "setsid sh -c '" +
                "pm install -r $APK_TEMP_PATH; " +
                "rm -f $APK_TEMP_PATH; " +
                "sleep 1; " +
                "am start -n com.coc.zkqcode/.MainActivity" +
                "' > /dev/null 2>&1 &"
            RunShell.runNoOutput(cmd, checkIsPlaying = false)

        } catch (e: Exception) {
            showDebugInfo("AppUpdateChecker: Download/install failed: ${e.message}")
            ShowMessage("更新安装失败：${e.message} / Update install failed: ${e.message}", isChecking = false)
            if (apkFile.exists()) {
                apkFile.delete()
            }
        }
    }

    /**
     * Executes a POST request with primary→backup URL fallback and returns the
     * response body as a string. Mirrors [UrlFallbackUtil] logic but is self-contained
     * because the JAR (and thus UrlFallbackUtil) is not loaded at startup time.
     */
    private suspend fun executePostWithFallbackAndReadBody(
        endpoint: String,
        postData: String
    ): String {
        val urls = listOf(PRIMARY_URL, BACKUP_URL)
        var lastException: Exception? = null

        for (baseUrl in urls) {
            try {
                val request = Request.Builder()
                    .url("${baseUrl}${endpoint}")
                    .post(postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull()))
                    .build()
                httpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        return response.body.string()
                    } else {
                        val code = response.code
                        val errorBody = try { response.body.string() } catch (_: Exception) { "" }
                        showDebugInfo("AppUpdateChecker: $baseUrl$endpoint → $code: $errorBody")
                    }
                }
            } catch (e: SocketTimeoutException) {
                lastException = e
            } catch (e: IOException) {
                lastException = e
            }
        }
        throw IOException("Update check: all servers failed: ${lastException?.message}", lastException)
    }
}
