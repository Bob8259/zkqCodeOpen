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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
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
    private const val WATCHDOG_TIMEOUT_MS = 8 * 3600_000L       // 8 hours
    private const val WATCHDOG_CHECK_INTERVAL_MS = 30 * 60_000L // 30 minutes
    private const val UPDATE_TIMEOUT_MS = 10 * 60_000L          // 10 minutes max for entire update
    private const val SIGNAL_LISTENER_RESTART_DELAY_MS = 5000L  // Delay before restarting signal listener after error

    // Timestamp of the last update signal received from the JAR side
    private var lastSignalTime: Long = System.currentTimeMillis()

    // Prevents concurrent checkAndUpdate calls from signal listener and watchdog
    private val updateMutex = Mutex()

    private val httpClient = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build()

    /**
     * Collects from the shared update signal flow. Each emission triggers
     * an update check. This suspends indefinitely and should be launched
     * in a long-lived coroutine scope (e.g. the service scope).
     * Automatically restarts on non-cancellation errors so the listener
     * is never permanently killed by a transient failure.
     */
    suspend fun listenForSignal(context: Context) {
        while (true) {
            try {
                GlobalVars.updateCheckSignal.collect { deferred ->
                    try {
                        lastSignalTime = System.currentTimeMillis()
                        updateMutex.withLock {
                            checkAndUpdate(context)
                        }
                    } finally {
                        // Always unblock the caller, even if checkAndUpdate threw
                        deferred.complete(Unit)
                    }
                }
            } catch (e: CancellationException) {
                throw e // Respect coroutine cancellation
            } catch (e: Exception) {
                LogHelper.showDebugInfo("HotUpdateManager: Signal listener error, restarting in ${SIGNAL_LISTENER_RESTART_DELAY_MS}ms: ${e.message}")
                delay(SIGNAL_LISTENER_RESTART_DELAY_MS)
            }
        }
    }

    /**
     * Watchdog that forces an update check when updateOption is 2 and no
     * signal has been received from the JAR side for 8 hours. This handles
     * the case where the JAR code is stuck or broken.
     * Each iteration is individually guarded so a single failure never kills
     * the watchdog loop.
     */
    suspend fun startWatchdog(context: Context) {
        while (true) {
            delay(WATCHDOG_CHECK_INTERVAL_MS)
            try {
                if (!GlobalVars.isConfigLoaded) continue
                val updateOption = GlobalVars.configStates["auto_update"]?.value?.toIntOrNull() ?: 0
                if (updateOption == 2 && System.currentTimeMillis() - lastSignalTime >= WATCHDOG_TIMEOUT_MS) {
                    ShowMessage("超过8小时未收到更新信号，强制检查更新")
                    updateMutex.withLock {
                        checkAndUpdate(context)
                    }
                    lastSignalTime = System.currentTimeMillis()
                }
            } catch (e: CancellationException) {
                throw e // Respect coroutine cancellation
            } catch (e: Exception) {
                LogHelper.showDebugInfo("HotUpdateManager: Watchdog error: ${e.message}")
            }
        }
    }

    private suspend fun checkAndUpdate(context: Context) = withTimeout(UPDATE_TIMEOUT_MS) {
        ShowMessage("准备检查新版本")
        val baseUrl = BuildConfig.BASE_URL
        val assetsDir = File(context.filesDir, "assets")
        // Ensure assets directory exists (e.g. on fresh install)
        if (!assetsDir.exists()) assetsDir.mkdirs()

        // --- Step A: Determine the local JAR version ---
        val localVersion =
            assetsDir.listFiles()?.filter { it.name.startsWith("encrypted_") && it.name.endsWith(".jar") }?.mapNotNull { it.name.removePrefix("encrypted_").removeSuffix(".jar").toLongOrNull() }?.maxOrNull() ?: 0L

        // --- Step B: Check server for a newer version ---
        val serverMd5: String
        val serverFileName: String
        val serverVersion: Long
        try {
            val checkRequest = Request.Builder().url("${baseUrl}api/hot-update-md5").get().build()
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
            serverVersion = serverFileName.removePrefix("encrypted_").removeSuffix(".jar").toLongOrNull() ?: 0L
        } catch (e: CancellationException) {
            throw e // Respect coroutine cancellation
        } catch (e: Exception) {
            LogHelper.showDebugInfo("HotUpdateManager: Version check error: ${e.message}")
            return@withTimeout
        }

        if (serverVersion <= localVersion) {
            ShowMessage("当前已是最新版本 (服务器版本号=$serverVersion, 本地版本号=$localVersion)")
            return@withTimeout
        }

        // --- Step C: Download with PoW authentication, retry up to MAX_RETRY times ---
        val email = GlobalVars.configStates["email"]?.value.orEmpty()
        val password = GlobalVars.configStates["password"]?.value.orEmpty()
        if (email.isBlank() || password.isBlank()) {
            repeat(5) {
                ShowMessage("请登录账号后，再使用自动更新\n(自动更新可免费使用，仅需登录即可)")
                delay(2000)
            }
            return@withTimeout
        }

        val targetFile = File(assetsDir, serverFileName)
        // Download to a temp file first to avoid leaving a corrupt JAR on network failure
        val tempFile = File(assetsDir, "${serverFileName}.tmp")
        var downloadSuccess = false

        for (attempt in 1..MAX_RETRY) {
            ShowMessage("检测到新版 (服务器版本号=$serverVersion, 本地版本号=$localVersion)\n下载中，第$attempt/$MAX_RETRY 次尝试")
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
                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val downloadRequest = Request.Builder().url("${baseUrl}api/hot-update-download").post(requestBody).build()

                // Stream response bytes to temp file with progress reporting
                withContext(Dispatchers.IO) {
                    httpClient.newCall(downloadRequest).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IllegalStateException("Download failed: ${response.code}")
                        }
                        val contentLength = response.body.contentLength()
                        if (tempFile.exists()) {
                            tempFile.setWritable(true)
                        }
                        tempFile.outputStream().use { out ->
                            val buffer = ByteArray(8192)
                            var totalBytesRead = 0L
                            var lastProgressTime = 0L
                            val inputStream = response.body.byteStream()
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                out.write(buffer, 0, bytesRead)
                                totalBytesRead += bytesRead
                                val now = System.currentTimeMillis()
                                // Throttle progress updates to avoid flooding ShowMessage
                                if (now - lastProgressTime >= 500L) {
                                    lastProgressTime = now
                                    val progressText = formatDownloadProgress(totalBytesRead, contentLength)
                                    Handler(Looper.getMainLooper()).post {
                                        ShowMessage(progressText)
                                    }
                                }
                            }
                        }
                    }
                }

                // Verify MD5 of downloaded temp file
                val actualMd5 = computeFileMd5(tempFile)
                if (actualMd5.equals(serverMd5, ignoreCase = true)) {
                    // Atomically move verified temp file to final target
                    if (targetFile.exists()) {
                        targetFile.setWritable(true)
                        targetFile.delete()
                    }
                    if (!tempFile.renameTo(targetFile)) {
                        // Fallback: copy + delete if rename fails (e.g. cross-filesystem)
                        tempFile.copyTo(targetFile, overwrite = true)
                        tempFile.delete()
                    }
                    ShowMessage("新版下载成功")
                    downloadSuccess = true
                    break
                } else {
                    ShowMessage("下载失败，可能是网络问题，导致下载中断。若反复出现此问题，则建议去官网手动下载新版。")
                    delay(2000)
                    tempFile.setWritable(true)
                    tempFile.delete()
                }
            } catch (e: CancellationException) {
                // Clean up temp file before propagating cancellation
                if (tempFile.exists()) { tempFile.setWritable(true); tempFile.delete() }
                throw e
            } catch (e: Exception) {
                ShowMessage("下载更新失败: 尝试次数 $attempt\n错误信息: ${e.message}")
                delay(2000)
                if (tempFile.exists()) {
                    tempFile.setWritable(true)
                    tempFile.delete()
                }
            }
        }

        if (!downloadSuccess) {
            ShowMessage("已尝试 $MAX_RETRY 此，但依然更新失败\n即将停止自动更新")
            return@withTimeout
        }

        // --- Step D: Clean up old JARs and reload ---
        assetsDir.listFiles()?.filter {
            it.name.startsWith("encrypted_") && it.name.endsWith(".jar") && it.name != serverFileName
        }?.forEach { oldJar ->
            try {
                oldJar.setWritable(true)
                if (!oldJar.delete()) {
                    LogHelper.showDebugInfo("HotUpdateManager: Failed to delete old JAR: ${oldJar.name}")
                }
            } catch (e: Exception) {
                LogHelper.showDebugInfo("HotUpdateManager: Error deleting old JAR ${oldJar.name}: ${e.message}")
            }
        }

        // Android 16+ requires DEX files to be non-writable
        targetFile.setReadOnly()

        // Reload the new JAR; wrapped in try-catch so a load failure doesn't crash the app
        try {
            ShowMessage("已成功下载新版，正在重启中")
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
        } catch (e: CancellationException) {
            throw e // Respect coroutine cancellation
        } catch (e: Exception) {
            LogHelper.showDebugInfo("HotUpdateManager: Reload failed: ${e.message}")
            ShowMessage("热更新重载失败: ${e.message}，请手动重启应用")
        }
    }

    /**
     * Formats download progress into a human-readable KB string.
     * Falls back to showing only downloaded size when total is unknown.
     */
    private fun formatDownloadProgress(downloaded: Long, total: Long): String {
        val dlKB = downloaded / 1024
        return if (total > 0) {
            val pct = (downloaded * 100 / total).toInt()
            val totalKB = total / 1024
            "下载中: $pct% (${dlKB}KB / ${totalKB}KB)"
        } else {
            "下载中: ${dlKB}KB"
        }
    }

    /**
     * Fetches a fresh PoW nonce from the server challenge endpoint.
     */
    private suspend fun fetchPowNonce(baseUrl: String): String {
        val request = Request.Builder().url("${baseUrl}api/pow/challenge").get().build()
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
