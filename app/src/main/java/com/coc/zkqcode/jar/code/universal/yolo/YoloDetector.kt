package com.coc.zkqcode.jar.code.universal.yolo

import android.graphics.Bitmap
import android.graphics.RectF
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

data class DetectionResult(
    val boundingBox: RectF,
    val score: Float,
    val classIndex: Int
)

object YoloDetector {
    private const val BASE_URL = "http://localhost:13462"
    private const val SERVICE_START_CMD =
        "am start-foreground-service -n com.coc.zkqyolo/.service.YoloService"
    private const val READY_TIMEOUT_MS = 10_000L
    private const val POLL_INTERVAL_MS = 1000L

    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Polls /status every ~1 second, restarting the service each iteration if unreachable.
     * Returns true once the server responds, or false after 10 seconds.
     * Returns false immediately if com.coc.zkqyolo is not installed.
     */
    private suspend fun ensureServerReady(): Boolean {
        // Check if the YOLO package is installed before attempting to start
        val installed = RunShell.run("pm list packages com.coc.zkqyolo", isCheckIsPlaying = false)
        if (installed.none { it.contains("com.coc.zkqyolo") }) {
            ShowMessage("AI插件未安装，请去网盘手动下载后，才能使用AI功能")
            return false
        }

        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < READY_TIMEOUT_MS) {
            try {
                val request = Request.Builder().url("$BASE_URL/status").get().build()
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body.string()
                        val status = gson.fromJson(body, StatusResponse::class.java)
                        // Verify the plugin version meets the minimum requirement
                        val ver = status.version.toDoubleOrNull() ?: 0.0
                        if (ver < 1.01) {
                            ShowMessage("AI插件版本过低，请手动下载最新版")
                            return false
                        }
                        return true
                    }
                }
            } catch (_: Exception) {
                // Server not reachable, attempt to start the service
                RunShell.runNoOutput(SERVICE_START_CMD, isCheckIsPlaying = false)
                delay(POLL_INTERVAL_MS)
            }
        }
        ShowMessage("AI插件启动失败，请确保手动安装并启动")
        return false
    }

    /**
     * Loads model weights via the YOLO service /load endpoint.
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

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val imageBytes = stream.toByteArray()

        val body = imageBytes.toRequestBody("application/octet-stream".toMediaType())
        val url = "$BASE_URL/detect?threshold=$threshold&distanceThreshold=$distanceThreshold"
        val request = Request.Builder().url(url).post(body).build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body.string()

            // Surface HTTP errors with status code and body
            if (!response.isSuccessful) {
                ShowMessage("检测请求失败: HTTP ${response.code}, $responseBody")
                return@detect emptyList()
            }

            val parsed = gson.fromJson(responseBody, DetectResponse::class.java)
            parsed.detections.map { d ->
                DetectionResult(
                    boundingBox = RectF(d.x1, d.y1, d.x2, d.y2),
                    score = d.score,
                    classIndex = d.classIndex
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

    // JSON response models for Gson deserialization
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
