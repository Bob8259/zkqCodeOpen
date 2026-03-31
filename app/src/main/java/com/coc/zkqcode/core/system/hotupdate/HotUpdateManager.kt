package com.coc.zkqcode.core.system.hotupdate

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.crypto.solvePoW
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.loadjar.Loadjar
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

object HotUpdateManager {

    private const val MAX_RETRY = 3

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Collects from the shared update signal flow. Each emission triggers
     * an update check. This suspends indefinitely and should be launched
     * in a long-lived coroutine scope (e.g. the service scope).
     */
    suspend fun listenForSignal(context: Context) {
        GlobalVars.updateCheckSignal.collect { deferred ->
            try {
                checkAndUpdate(context)
            } finally {
                // Always unblock the caller, even if checkAndUpdate threw
                deferred.complete(Unit)
            }
        }
    }

    private suspend fun checkAndUpdate(context: Context) {
        ShowMessage("准备检查新版本")
        val baseUrl = BuildConfig.BASE_URL
        val assetsDir = File(context.filesDir, "assets")

        // --- Step A: Determine the local JAR version ---
        val localVersion = assetsDir.listFiles()
            ?.filter { it.name.startsWith("encrypted_") && it.name.endsWith(".jar") }
            ?.mapNotNull { it.name.removePrefix("encrypted_").removeSuffix(".jar").toLongOrNull() }
            ?.maxOrNull() ?: 0L

        // --- Step B: Check server for a newer version ---
        val serverMd5: String
        val serverFileName: String
        val serverVersion: Long
        try {
            val checkRequest = Request.Builder()
                .url("${baseUrl}api/hot-update-md5")
                .get()
                .build()
            val responseStr = withContext(Dispatchers.IO) {
                httpClient.newCall(checkRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("Version check failed: ${response.code}")
                    }
                    response.body.string()
                }
            }
            val json = JSONObject(responseStr)
            serverMd5 = json.getString("md5")
            serverFileName = json.getString("fileName")
            serverVersion = serverFileName
                .removePrefix("encrypted_")
                .removeSuffix(".jar")
                .toLongOrNull() ?: 0L
        } catch (e: Exception) {
            LogHelper.showDebugInfo("HotUpdateManager: Version check error: ${e.message}")
            return
        }

        if (serverVersion <= localVersion) {
            ShowMessage("当前已是最新版本 (服务器版本号=$serverVersion, 本地版本号=$localVersion)")
            return
        }
        ShowMessage("检测到新版 (服务器版本号=$serverVersion, 本地版本号=$localVersion)")

        // --- Step C: Download with PoW authentication, retry up to MAX_RETRY times ---
        val email = GlobalVars.configStates["email"]?.value.orEmpty()
        val password = GlobalVars.configStates["password"]?.value.orEmpty()
        if (email.isBlank() || password.isBlank()) {
            repeat(5) {
                ShowMessage("请登录账号后，再使用自动更新\n(自动更新可免费使用，仅需登录即可)")
                delay(2000)
            }
            return
        }

        val targetFile = File(assetsDir, serverFileName)
        var downloadSuccess = false

        for (attempt in 1..MAX_RETRY) {
            ShowMessage("下载中，第$attempt/$MAX_RETRY 次尝试")
            try {
                // Fetch PoW challenge (nonce valid for 10s, single-use)
                val powNonce = fetchPowNonce(baseUrl)
                val powSalt = solvePoW(powNonce)

                // Build JSON request body for download endpoint
                val jsonBody = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                    put("powNonce", powNonce)
                    put("powSalt", powSalt)
                }
                val requestBody = jsonBody.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())
                val downloadRequest = Request.Builder()
                    .url("${baseUrl}api/hot-update-download")
                    .post(requestBody)
                    .build()

                // Stream response bytes to file
                withContext(Dispatchers.IO) {
                    httpClient.newCall(downloadRequest).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IllegalStateException("Download failed: ${response.code}")
                        }
                        // Ensure the file is writable before writing
                        if (targetFile.exists()) {
                            targetFile.setWritable(true)
                        }
                        targetFile.outputStream().use { out ->
                            response.body.byteStream().copyTo(out)
                        }
                    }
                }

                // Verify MD5 of downloaded file
                val actualMd5 = computeFileMd5(targetFile)
                if (actualMd5.equals(serverMd5, ignoreCase = true)) {
                    ShowMessage("新版下载成功")
                    downloadSuccess = true
                    break
                } else {
                    ShowMessage("下载失败，可能是网络问题，导致下载中断。若反复出现此问题，则建议去官网手动下载新版。")
                    delay(2000)
                    targetFile.setWritable(true)
                    targetFile.delete()
                }
            } catch (e: Exception) {
                ShowMessage("下载更新失败: 尝试次数 $attempt\n错误信息: ${e.message}")
                delay(2000)
                if (targetFile.exists()) {
                    targetFile.setWritable(true)
                    targetFile.delete()
                }
            }
        }

        if (!downloadSuccess) {
            ShowMessage("已尝试 $MAX_RETRY 此，但依然更新失败\n即将停止自动更新")
            return
        }

        // --- Step D: Clean up old JARs and reload ---
        assetsDir.listFiles()
            ?.filter {
                it.name.startsWith("encrypted_") && it.name.endsWith(".jar")
                        && it.name != serverFileName
            }
            ?.forEach { oldJar ->
                oldJar.setWritable(true)
                oldJar.delete()
            }

        // Android 16+ requires DEX files to be non-writable
        targetFile.setReadOnly()

        ShowMessage("已成功下载新版，正在重启中")
        // Reuse DebugReloadReceiver pattern: stop bot, reload, restart
        AppStateManager.setMode(AppMode.Main)
        val loader = Loadjar(context)
        loader.startLoading { status ->
            LogHelper.showDebugInfo("HotUpdateManager: $status")
            if (status == "Plugin loaded successfully") {
                GlobalVars.isPlaying.value = true
                Handler(Looper.getMainLooper()).postDelayed({
                    AppStateManager.setMode(AppMode.Run)
                    ShowMessage("重启成功")
                }, 500)
            }
        }
    }

    /**
     * Fetches a fresh PoW nonce from the server challenge endpoint.
     */
    private suspend fun fetchPowNonce(baseUrl: String): String {
        val request = Request.Builder()
            .url("${baseUrl}api/pow/challenge")
            .get()
            .build()
        val responseStr = withContext(Dispatchers.IO) {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("PoW challenge failed: ${response.code}")
                }
                response.body.string()
            }
        }
        return JSONObject(responseStr).getString("nonce")
    }

    /**
     * Computes the MD5 hex digest of a file.
     */
    private fun computeFileMd5(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
