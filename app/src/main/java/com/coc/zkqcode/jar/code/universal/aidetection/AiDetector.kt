package com.coc.zkqcode.jar.code.universal.aidetection

import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.archive.ArchiveUtils
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
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
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit

data class DetectionResult(
    val boundingBox: RectF,
    val score: Float,
    val classIndex: Int
)

object AiDetector {
    private const val BASE_URL = "http://localhost:13462"
    private const val PLUGIN_PACKAGE = "com.building.plugin"
    private const val SERVICE_START_CMD =
        "am start-foreground-service -n $PLUGIN_PACKAGE/.service.DetectorService"
    private const val MAIN_ACTIVITY_CMD = "am start -n $PLUGIN_PACKAGE/.MainActivity"
    // The plugin APK is published as a split zip archive (two parts). The parts are
    // downloaded in order and appended into a single zip file, which is then extracted.
    private val PLUGIN_DOWNLOAD_URLS = listOf(
        "https://gitee.com/azikaban/building_plugin/releases/download/split/app-release.zip.001",
        "https://gitee.com/azikaban/building_plugin/releases/download/split/app-release.zip.002"
    )
    private const val PLUGIN_DOWNLOAD_DIR = "plugin"
    private const val PLUGIN_ZIP_NAME = "app-release.zip"
    private const val PLUGIN_EXTRACT_DIR_NAME = "extracted"
    private const val PREFERRED_APK_NAME = "app-release.apk"
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
     * previously stored by ShowMessage and writes the temporary zip/apk files into private storage.
     * @return the plugin download directory, or null if it cannot be created.
     */
    private fun getPluginDownloadDir(): File? {
        val context = ZKQApplication.appContext
        val downloadDir = File(context.filesDir, PLUGIN_DOWNLOAD_DIR)
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            accountMessage("建筑插件安装失败：无法创建下载目录")
            return null
        }
        return downloadDir
    }

    private fun postMessage(text: String) {
        mainHandler.post {
            accountMessage(text)
        }
    }

    /**
     * Progress updates are throttled to avoid overwhelming the floating message overlay.
     * @param partIndex 1-based index of the current split part
     * @param partCount total number of split parts
     */
    private fun formatDownloadProgress(downloaded: Long, total: Long, partIndex: Int, partCount: Int): String {
        val downloadedMb = downloaded / 1024.0 / 1024.0
        if (total > 0) {
            val totalMb = total / 1024.0 / 1024.0
            val percent = (downloaded * 100 / total).coerceAtMost(100)
            return "建筑插件下载中($partIndex/$partCount)：$percent% (${String.format(Locale.US, "%.2f", downloadedMb)}MB/${String.format(Locale.US, "%.2f", totalMb)}MB)"
        }
        return "建筑插件下载中($partIndex/$partCount)：${String.format(Locale.US, "%.2f", downloadedMb)}MB"
    }

    /**
     * Downloads all split parts in order and appends them into a single zip file.
     * Any failure deletes the partial zip so a later retry starts clean.
     */
    private suspend fun downloadPluginZip(targetFile: File): Boolean = withContext(Dispatchers.IO) {
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val partCount = PLUGIN_DOWNLOAD_URLS.size
        PLUGIN_DOWNLOAD_URLS.forEachIndexed { index, url ->
            val partIndex = index + 1
            try {
                val request = Request.Builder().url(url).get().build()
                downloadClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        postMessage("建筑插件下载失败(第${partIndex}部分)：HTTP ${response.code}")
                        targetFile.delete()
                        return@withContext false
                    }
                    val body = response.body
                    val totalBytes = body.contentLength()
                    // Append mode: split parts are concatenated into one archive.
                    body.byteStream().use { input ->
                        FileOutputStream(targetFile, partIndex > 1).use { output ->
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
                                    postMessage(formatDownloadProgress(totalBytesRead, totalBytes, partIndex, partCount))
                                }
                            }
                            output.flush()
                            postMessage(formatDownloadProgress(totalBytesRead, totalBytes, partIndex, partCount))
                        }
                    }
                }
                showDebugInfo("建筑插件：第${partIndex}/${partCount}部分下载完成，累计 ${targetFile.length()} 字节")
            } catch (e: Exception) {
                targetFile.delete()
                postMessage("建筑插件下载失败(第${partIndex}部分)：${e.message}")
                return@withContext false
            }
        }
        true
    }

    /**
     * Extracts the downloaded zip and locates the APK inside it.
     * The archive layout is not guaranteed, so the preferred name is tried first
     * and any other *.apk found in the tree is accepted as a fallback.
     */
    private suspend fun extractPluginApk(zipFile: File, extractDir: File): File? =
        withContext(Dispatchers.IO) {
            // deleteRecursively is a no-op when the directory is absent.
            extractDir.deleteRecursively()
            if (!extractDir.mkdirs()) {
                postMessage("建筑插件解压失败：无法创建解压目录")
                return@withContext null
            }
            val ok = ArchiveUtils.unzipToDirectory(zipFile, extractDir)
            if (!ok) {
                postMessage("建筑插件解压失败：压缩包损坏或格式错误")
                showDebugInfo("建筑插件：解压失败 zip=${zipFile.absolutePath} size=${zipFile.length()}")
                return@withContext null
            }
            val apk = findApkInDir(extractDir)
            if (apk == null) {
                postMessage("建筑插件解压失败：压缩包内未找到 APK")
                showDebugInfo("建筑插件：解压完成但未找到 APK，dir=${extractDir.absolutePath}")
            } else {
                showDebugInfo("建筑插件：解压完成，APK=${apk.absolutePath} size=${apk.length()}")
            }
            apk
        }

    /** Searches the extracted tree for the APK, preferring the canonical plugin name. */
    private fun findApkInDir(dir: File): File? {
        val allApks = dir.walkTopDown().filter { it.isFile && it.name.endsWith(".apk", ignoreCase = true) }.toList()
        return allApks.firstOrNull { it.name.equals(PREFERRED_APK_NAME, ignoreCase = true) }
            ?: allApks.firstOrNull()
    }

    /**
     * Runs a shell command and classifies its output via [hasFailed].
     * On failure, joins the output lines into a user-facing message prefixed with [failPrefix].
     */
    private suspend fun runShellChecked(
        cmd: String,
        failPrefix: String,
        blankMessage: String,
        hasFailed: (List<String>) -> Boolean
    ): Boolean {
        val output = RunShell.run(cmd, checkIsPlaying = false)
        if (hasFailed(output)) {
            val errorMessage = output.joinToString("\n").ifBlank { blankMessage }
            accountMessage("$failPrefix$errorMessage")
            return false
        }
        return true
    }

    private suspend fun installPlugin(apkFile: File): Boolean {
        // Stream the APK into pm install via stdin to bypass SELinux file-access restrictions.
        // The PackageManagerService cannot read files from the app's private storage or
        // /data/local/tmp without the correct SELinux context, but streaming avoids both issues.
        val size = apkFile.length()
        return runShellChecked(
            cmd = "cat \"${apkFile.absolutePath}\" | pm install -r -S $size",
            failPrefix = "建筑插件安装失败：",
            blankMessage = "未返回安装结果",
            // pm install reports success only through the "Success" keyword.
            hasFailed = { lines -> lines.none { it.contains("Success", ignoreCase = true) } }
        )
    }

    private suspend fun launchPluginMainActivity(): Boolean = runShellChecked(
        cmd = MAIN_ACTIVITY_CMD,
        failPrefix = "建筑插件启动失败：",
        blankMessage = "未返回启动结果",
        // am start reports problems through Error/Exception lines in its output.
        hasFailed = { lines ->
            lines.any {
                it.contains("Error", ignoreCase = true) || it.contains("Exception", ignoreCase = true)
            }
        }
    )

    /**
     * Launches the plugin's MainActivity to trigger its initialization,
     * waits for it to complete, then returns to the game.
     * Used as a recovery mechanism when the background service fails to start,
     * and after a fresh install/upgrade to finish plugin initialization.
     *
     * @param requireSuccess when true, aborts without waiting or returning to the game
     *   if the launch command reports an error; when false both always run.
     * @return true if the launch command reported no error.
     */
    private suspend fun launchPluginAndReturnToGame(requireSuccess: Boolean = false): Boolean {
        val launched = launchPluginMainActivity()
        if (!launched && requireSuccess) {
            return false
        }
        delay(PLUGIN_LAUNCH_WAIT_MS)
        runGame()
        return launched
    }

    /**
     * Restarts the detector foreground service and waits one poll interval.
     */
    private suspend fun restartDetectorService() {
        RunShell.runNoOutput(SERVICE_START_CMD, checkIsPlaying = false)
        delay(POLL_INTERVAL_MS)
    }

    /** Deletes the temporary zip archive and extraction directory; missing files are ignored. */
    private suspend fun deletePluginFiles(zipFile: File, extractDir: File) = withContext(Dispatchers.IO) {
        zipFile.delete()
        extractDir.deleteRecursively()
    }

    /**
     * Downloads the split archive from gitee, extracts the APK, installs, and launches the plugin.
     * Used both on first install and for forced version upgrades.
     * The plugin needs one foreground launch so its app-side initialization can complete
     * before the detector returns to the game and polls the local server again.
     */
    private suspend fun downloadAndInstallPlugin(): Boolean {
        val downloadDir = getPluginDownloadDir() ?: return false
        val zipFile = File(downloadDir, PLUGIN_ZIP_NAME)
        val extractDir = File(downloadDir, PLUGIN_EXTRACT_DIR_NAME)
        try {
            // Uninstall legacy plugin package before downloading the new one
            RunShell.runNoOutput("pm uninstall com.coc.zkqyolo", checkIsPlaying = false)
            if (!downloadPluginZip(zipFile)) {
                return false
            }
            accountMessage("建筑插件下载完成，正在解压")
            val apkFile = extractPluginApk(zipFile, extractDir) ?: return false
            accountMessage("建筑插件解压完成，开始安装")
            if (!installPlugin(apkFile)) {
                return false
            }
            accountMessage("建筑插件安装成功，正在启动插件")
            return launchPluginAndReturnToGame(requireSuccess = true)
        } finally {
            deletePluginFiles(zipFile, extractDir)
        }
    }

    /**
     * Ensures the plugin APK is present on the device; downloads and installs it if missing.
     */
    private suspend fun ensurePluginInstalled(): Boolean {
        if (isPluginInstalled()) {
            return true
        }
        accountMessage("未检测到建筑插件，开始自动下载安装")
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
        var serviceStartAttempts = 0
        var activityRecoveryAttempted = false
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
                        if (ver < 1.10) {
                            versionTooLow = true
                        } else {
                            return true
                        }
                    }
                }
            } catch (_: Exception) {
                serviceStartAttempts++
                if (serviceStartAttempts >= 2 && !activityRecoveryAttempted) {
                    // Service command failed twice; launch plugin activity to trigger init
                    activityRecoveryAttempted = true
                    serviceStartAttempts = 0
                    launchPluginAndReturnToGame()
                    delay(POLL_INTERVAL_MS)
                    continue
                }
                // If the service is unreachable, try starting it again and wait briefly.
                restartDetectorService()
                continue
            }

            if (versionTooLow) {
                if (updateAttempted) {
                    // Already tried once; avoid an infinite update loop.
                    accountMessage("建筑插件更新后版本仍然过低，请手动更新")
                    return false
                }
                accountMessage("建筑插件版本过低，正在尝试自动下载更新")
                updateAttempted = true
                if (!downloadAndInstallPlugin()) {
                    accountMessage("建筑插件自动更新失败，请手动下载安装最新版")
                    return false
                }
                // Reset the polling timer so the full window is available after the update.
                startTime = System.currentTimeMillis()
                restartDetectorService()
            }
        }
        accountMessage("建筑插件启动失败，请检查插件是否安装成功并已正常启动")
        return false
    }

    /**
     * Loads model weights via the detection service /load endpoint.
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
                    accountMessage("插件加载失败: HTTP ${response.code}")
                    return false
                }
                val responseBody = response.body.string()
                val parsed = gson.fromJson(responseBody, LoadResponse::class.java)
                if (parsed.success) {
                    true
                } else {
                    accountMessage("插件加载失败: ${parsed.error ?: "未知错误"}")
                    false
                }
            }
        } catch (e: Exception) {
            accountMessage("插件加载异常: ${e.message}")
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
        accountMessage("检测中，请耐心等待。")
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
                accountMessage("检测请求失败: HTTP ${response.code}, $responseBody")
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
            accountMessage("检测异常: ${e.message}")
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
