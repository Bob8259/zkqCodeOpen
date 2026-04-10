package com.coc.zkqcode.jar.code.universal.aidetection

import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.smalltools.runGame
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DetectionResult(
    val boundingBox: RectF,
    val score: Float,
    val classIndex: Int
)

object AiDetector {
    private const val BASE_URL = "http://localhost:13462"
    private const val PLUGIN_PACKAGE = "com.coc.zkqyolo"
    private const val SERVICE_START_CMD =
        "am start-foreground-service -n $PLUGIN_PACKAGE/.service.YoloService"
    private const val MAIN_ACTIVITY_CMD = "am start -n $PLUGIN_PACKAGE/.MainActivity"
    private const val PLUGIN_DOWNLOAD_URL =
        "https://gitee.com/azikaban/zkqYOLO/releases/download/AIplugin/AI.apk"
    private const val PLUGIN_DOWNLOAD_DIR = "ai-plugin"
    private const val PLUGIN_APK_NAME = "AI.apk"
    private const val READY_TIMEOUT_MS = 10_000L
    private const val POLL_INTERVAL_MS = 1000L
    private const val DOWNLOAD_PROGRESS_INTERVAL_MS = 500L
    private const val PLUGIN_LAUNCH_WAIT_MS = 4000L

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val downloadClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.MINUTES)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val gson = Gson()
    private val mainHandler = Handler(Looper.getMainLooper())

    private suspend fun isPluginInstalled(): Boolean {
        val installed = RunShell.run("pm list packages $PLUGIN_PACKAGE", checkIsPlaying = false)
        return installed.any { it.contains(PLUGIN_PACKAGE) }
    }

    /**
     * The jar side does not own a Context directly, so it reuses the application Context
     * previously stored by ShowMessage and writes the temporary APK into private storage.
     */
    private fun getPluginApkFile(): File? {
        val context = ShowMessage.getContext()
        if (context == null) {
            ShowMessage("AI插件安装失败：上下文未初始化")
            return null
        }
        val downloadDir = File(context.filesDir, PLUGIN_DOWNLOAD_DIR)
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            ShowMessage("AI插件安装失败：无法创建下载目录")
            return null
        }
        return File(downloadDir, PLUGIN_APK_NAME)
    }

    private fun postMessage(text: String) {
        mainHandler.post {
            ShowMessage(text)
        }
    }

    /**
     * Progress updates are throttled to avoid overwhelming the floating message overlay.
     */
    private fun formatDownloadProgress(downloaded: Long, total: Long): String {
        val downloadedMb = downloaded / 1024.0 / 1024.0
        if (total > 0) {
            val totalMb = total / 1024.0 / 1024.0
            val percent = (downloaded * 100 / total).coerceAtMost(100)
            return "AI插件下载中：$percent% (${String.format(Locale.US, "%.2f", downloadedMb)}MB/${String.format(Locale.US, "%.2f", totalMb)}MB)"
        }
        return "AI插件下载中：${String.format(Locale.US, "%.2f", downloadedMb)}MB"
    }

    private suspend fun downloadPluginApk(targetFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            val request = Request.Builder().url(PLUGIN_DOWNLOAD_URL).get().build()
            downloadClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    postMessage("AI插件下载失败：HTTP ${response.code}")
                    return@withContext false
                }
                val body = response.body
                val totalBytes = body.contentLength()
                body.byteStream().use { input ->
                    targetFile.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var totalBytesRead = 0L
                        var lastProgressTime = 0L
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            val now = System.currentTimeMillis()
                            if (now - lastProgressTime >= DOWNLOAD_PROGRESS_INTERVAL_MS) {
                                lastProgressTime = now
                                postMessage(formatDownloadProgress(totalBytesRead, totalBytes))
                            }
                        }
                        output.flush()
                        postMessage(formatDownloadProgress(totalBytesRead, totalBytes))
                    }
                }
            }
            true
        } catch (e: Exception) {
            if (targetFile.exists()) {
                targetFile.delete()
            }
            postMessage("AI插件下载失败：${e.message}")
            false
        }
    }

    private suspend fun installPlugin(apkFile: File): Boolean {
        val installResult = RunShell.run(
            "pm install -r \"${apkFile.absolutePath}\"",
            checkIsPlaying = false
        )
        if (installResult.any { it.contains("Success", ignoreCase = true) }) {
            return true
        }
        val errorMessage = installResult.joinToString("\n").ifBlank { "未返回安装结果" }
        ShowMessage("AI插件安装失败：$errorMessage")
        return false
    }

    private suspend fun launchPluginMainActivity(): Boolean {
        val launchResult = RunShell.run(MAIN_ACTIVITY_CMD, checkIsPlaying = false)
        val hasError = launchResult.any {
            it.contains("Error", ignoreCase = true) || it.contains("Exception", ignoreCase = true)
        }
        if (hasError) {
            val errorMessage = launchResult.joinToString("\n").ifBlank { "未返回启动结果" }
            ShowMessage("AI插件启动失败：$errorMessage")
            return false
        }
        return true
    }

    private suspend fun deletePluginApk(targetFile: File) = withContext(Dispatchers.IO) {
        if (targetFile.exists()) {
            targetFile.delete()
        }
    }

    /**
     * Downloads from gitee, installs, and launches the plugin.
     * Used both on first install and for forced version upgrades.
     * The plugin needs one foreground launch so its app-side initialization can complete
     * before the detector returns to the game and polls the local server again.
     */
    private suspend fun downloadAndInstallPlugin(): Boolean {
        val apkFile = getPluginApkFile() ?: return false
        try {
            if (!downloadPluginApk(apkFile)) {
                return false
            }
            ShowMessage("AI插件下载完成，开始安装")
            if (!installPlugin(apkFile)) {
                return false
            }
            ShowMessage("AI插件安装成功，正在启动插件")
            if (!launchPluginMainActivity()) {
                return false
            }
            delay(PLUGIN_LAUNCH_WAIT_MS)
            runGame()
            return true
        } finally {
            deletePluginApk(apkFile)
        }
    }

    /**
     * Ensures the plugin APK is present on the device; downloads and installs it if missing.
     */
    private suspend fun ensurePluginInstalled(): Boolean {
        if (isPluginInstalled()) {
            return true
        }
        ShowMessage("未检测到AI插件，开始自动下载安装")
        return downloadAndInstallPlugin()
    }

    /**
     * Polls /status every ~1 second, restarting the service each iteration if unreachable.
     * Returns true once the server responds with an acceptable version, or false after the
     * timeout. If the installed version is too low, automatically downloads and reinstalls
     * the latest APK from gitee (at most once per call to avoid infinite loops).
     */
    private suspend fun ensureServerReady(): Boolean {
        if (!ensurePluginInstalled()) {
            return false
        }

        var updateAttempted = false
        var startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < READY_TIMEOUT_MS) {
            var versionTooLow = false
            try {
                val request = Request.Builder().url("$BASE_URL/status").get().build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body.string()
                        val status = gson.fromJson(body, StatusResponse::class.java)
                        // Verify the plugin version meets the minimum requirement.
                        val ver = status.version.toDoubleOrNull() ?: 0.0
                        if (ver < 1.04) {
                            versionTooLow = true
                        } else {
                            return true
                        }
                    }
                }
            } catch (_: Exception) {
                // If the service is unreachable, try starting it again and wait briefly.
                RunShell.runNoOutput(SERVICE_START_CMD, checkIsPlaying = false)
                delay(POLL_INTERVAL_MS)
                continue
            }

            if (versionTooLow) {
                if (updateAttempted) {
                    // Already tried once; avoid an infinite update loop.
                    ShowMessage("AI插件更新后版本仍然过低，请手动更新")
                    return false
                }
                ShowMessage("AI插件版本过低，正在尝试自动下载更新")
                updateAttempted = true
                if (!downloadAndInstallPlugin()) {
                    ShowMessage("AI插件自动更新失败，请手动下载安装最新版")
                    return false
                }
                // Reset the polling timer so the full window is available after the update.
                startTime = System.currentTimeMillis()
                RunShell.runNoOutput(SERVICE_START_CMD, checkIsPlaying = false)
                delay(POLL_INTERVAL_MS)
            }
        }
        ShowMessage("AI插件启动失败，请检查插件是否安装成功并已正常启动")
        return false
    }

    /**
     * Loads model weights via the AI detection service /load endpoint.
     * @return true if weights loaded successfully, false on any failure.
     */
    suspend fun loadWeights(modelType: String? = null): Boolean {
        if (!ensureServerReady()) return false
        val json = if (modelType != null) """{"modelType":"$modelType"}""" else "{}"
        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url("$BASE_URL/load").post(body).build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    ShowMessage("模型加载失败: HTTP ${response.code}")
                    return false
                }
                val responseBody = response.body.string()
                val parsed = gson.fromJson(responseBody, LoadResponse::class.java)
                if (parsed.success) {
                    true
                } else {
                    ShowMessage("模型加载失败: ${parsed.error ?: "未知错误"}")
                    false
                }
            }
        } catch (e: Exception) {
            ShowMessage("模型加载异常: ${e.message}")
            false
        }
    }

    suspend fun detect(
        bitmap: Bitmap,
        clearWeightsAfter: Boolean = true,
        threshold: Float = 0.3f,
        distanceThreshold: Double = 5.0
    ): List<DetectionResult> {
        if (!ensureServerReady()) return emptyList()
        ShowMessage("AI检测中，请耐心等待。\n检测速度取决于设备性能，低性能设备可能需要5-10秒。")
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()

        val body = imageBytes.toRequestBody("application/octet-stream".toMediaType())
        val url = "$BASE_URL/detect?threshold=$threshold&distanceThreshold=$distanceThreshold"
        val request = Request.Builder().url(url).post(body).build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body.string()

            // Surface HTTP errors with status code and body for easier debugging.
            if (!response.isSuccessful) {
                ShowMessage("检测请求失败: HTTP ${response.code}, $responseBody")
                return emptyList()
            }

            val parsed = gson.fromJson(responseBody, DetectResponse::class.java)
            parsed.detections.map { detection ->
                DetectionResult(
                    boundingBox = RectF(detection.x1, detection.y1, detection.x2, detection.y2),
                    score = detection.score,
                    classIndex = detection.classIndex
                )
            }
        } catch (e: Exception) {
            ShowMessage("检测异常: ${e.message}")
            emptyList()
        } finally {
            if (clearWeightsAfter) {
                clearWeights()
            }
        }
    }

    suspend fun clearWeights() {
        if (!ensureServerReady()) return
        val body = "".toRequestBody()
        val request = Request.Builder().url("$BASE_URL/clear").post(body).build()
        client.newCall(request).execute().close()
    }

    // JSON response models for Gson deserialization.
    private data class StatusResponse(val status: String, val version: String)
    private data class LoadResponse(val success: Boolean, val error: String? = null)

    private data class RawDetection(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float,
        val score: Float,
        val classIndex: Int
    )

    private data class DetectResponse(val detections: List<RawDetection>)
}
