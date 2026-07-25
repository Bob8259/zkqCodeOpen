package com.coc.zkqcode.core.system.appupdate

import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
 * Represents the current state of the app-update check, displayed on the
 * [com.coc.zkqcode.core.system.checkpermissions.CheckRootScreen] startup UI.
 */
sealed class AppUpdateState {
    /** No update check has run yet. */
    data object Idle : AppUpdateState()

    /** The installed version was confirmed current by the update server. */
    data object UpToDate : AppUpdateState()

    /** An update check or download failed and the next attempt is scheduled. */
    data object Retrying : AppUpdateState()

    /** An update is available and the APK is being downloaded. [progress] is 0–100. */
    data class Downloading(val progress: Int) : AppUpdateState()

    /** The APK has been downloaded and is being installed. */
    data object Installing : AppUpdateState()
}

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
 * Progress is exposed via [state] (a [StateFlow] of [AppUpdateState]) so the UI can
 * display status messages and download progress without using [ShowMessage].
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
        "https://gitee.com/azikaban/zkqCodeOpen/releases/download/发行版/app-release.apk"

    // APK filename within the app's private cache directory
    private const val APK_FILENAME = "zkqcode_update.apk"

    private const val RETRY_DELAY_MS = 10_000L

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val updateMutex = Mutex()
    private var updateInstallStarted = false
    private val _state = MutableStateFlow<AppUpdateState>(AppUpdateState.Idle)

    /** Observable update-check state for the UI. */
    val state: StateFlow<AppUpdateState> = _state.asStateFlow()

    /**
     * Checks for a new app version until the installed version is confirmed current.
     *
     * Any check or download failure is logged, displayed as [AppUpdateState.Retrying],
     * and retried after ten seconds. The mutex serializes the entire operation so a
     * recreated screen or another caller cannot start a second check or download.
     */
    suspend fun checkForUpdate() = updateMutex.withLock {
        if (_state.value is AppUpdateState.UpToDate || updateInstallStarted) return@withLock

        withContext(Dispatchers.IO) {
            while (true) {
                try {
                    checkForUpdateOnce()
                    return@withContext
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    showDebugInfo("AppUpdateChecker: Update check/download failed: ${e.message}")
                    _state.value = AppUpdateState.Retrying
                    delay(RETRY_DELAY_MS)
                }
            }
        }
    }

    private suspend fun checkForUpdateOnce() {
        // 1. Generate ephemeral X25519 keypair and derive session key (stored in native layer)
        val publicKeyHex = RustTools.encryptVersionCheckRequest()
        if (publicKeyHex.startsWith("Error")) {
            throw IOException("Failed to generate update-check key: $publicKeyHex")
        }

        // 2. POST public_key to the server (with URL fallback)
        val postData = "public_key=$publicKeyHex"
        val responseBody = executePostWithFallbackAndReadBody(ENDPOINT, postData)

        // 3. Decrypt the response: nonce=<hex>&data=<hex> → version string
        val decrypted = RustTools.decryptLoginResponse(responseBody)
        if (decrypted.startsWith("Error")) {
            throw IOException("Failed to decrypt update-check response: $decrypted")
        }

        // 4. Parse version number (supports decimals like 1.1, 1.2)
        val version = decrypted.trim().toDoubleOrNull()
            ?: throw IOException("Invalid version response: $decrypted")

        showDebugInfo("AppUpdateChecker: Server version = $version")

        // 5. If version > 1, download and install the new APK; otherwise startup can continue.
        if (version > 1) {
            downloadAndInstall()
        } else {
            _state.value = AppUpdateState.UpToDate
        }
    }

    /**
     * Downloads the APK from Gitee to the app's private cache directory, then
     * force-installs it via a detached `setsid` session that also deletes the
     * temp file and relaunches the app.
     *
     * The APK is streamed into `pm install` via `cat` to bypass SELinux
     * file-access restrictions (PackageManagerService cannot read files from
     * the app's private storage), following the same pattern as AiDetector's
     * plugin installation.
     *
     * Download progress is reported through [state] as [AppUpdateState.Downloading].
     */
    private suspend fun downloadAndInstall() {
        // Use the app's private cache directory (writable by the app process)
        val cacheDir = ZKQApplication.appContext.cacheDir
        val apkFile = File(cacheDir, APK_FILENAME)
        try {
            _state.value = AppUpdateState.Downloading(0)

            // Download APK from Gitee with progress reporting
            val request = Request.Builder().url(APK_DOWNLOAD_URL).get().build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("APK download failed with HTTP ${response.code}")
                }
                val responseBody = response.body
                val totalBytes = responseBody.contentLength()
                responseBody.byteStream().use { input ->
                    apkFile.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var totalBytesRead = 0L
                        var lastProgress = -1
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            // Report progress only when the integer percentage changes
                            if (totalBytes > 0) {
                                val progress = ((totalBytesRead * 100) / totalBytes).toInt().coerceIn(0, 100)
                                if (progress != lastProgress) {
                                    lastProgress = progress
                                    _state.value = AppUpdateState.Downloading(progress)
                                }
                            }
                        }
                        output.flush()
                    }
                }
            }

            if (!apkFile.exists() || apkFile.length() == 0L) {
                throw IOException("Downloaded APK file is empty")
            }

            _state.value = AppUpdateState.Installing

            // Force install via setsid: stream the APK into pm install via cat
            // (bypasses SELinux file-access restrictions on private storage),
            // delete the temp APK, then relaunch the app.
            // The setsid creates a new session so the install+launch survives the
            // current process termination (same pattern as LogHelper/ControlWindowService restart).
            val apkPath = apkFile.absolutePath
            val apkSize = apkFile.length()
            val cmd = "setsid sh -c '" +
                "cat \"$apkPath\" | pm install -S $apkSize; " +
                "rm -f \"$apkPath\"; " +
                "sleep 1; " +
                "am start -n com.coc.zkqcode/.MainActivity" +
                "' > /dev/null 2>&1 &"
            RunShell.runNoOutput(cmd, checkIsPlaying = false)
            updateInstallStarted = true
        } catch (e: CancellationException) {
            if (apkFile.exists()) apkFile.delete()
            throw e
        } catch (e: Exception) {
            if (apkFile.exists()) apkFile.delete()
            throw e
        }
    }

    /**
     * Executes a POST request with primary→backup URL fallback and returns the
     * response body as a string. Mirrors [UrlFallbackUtil] logic but is self-contained
     * because the JAR (and thus UrlFallbackUtil) is not loaded at startup time.
     */
    private fun executePostWithFallbackAndReadBody(
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
