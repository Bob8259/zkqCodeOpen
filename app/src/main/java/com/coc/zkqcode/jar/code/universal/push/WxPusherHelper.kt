package com.coc.zkqcode.jar.code.universal.push

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object WxPusherHelper {

    private const val API_URL = "https://wxpusher.zjiecode.com/api/send/message"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val MEDIA_TYPE_JSON = "application/json;charset=utf-8".toMediaTypeOrNull()

    /** @return null on success, error string on failure. */
    suspend fun sendNotification(title: String, desp: String = ""): String? {
        return sendNotification(PushMessage(title = title, desp = desp))
    }

    suspend fun sendNotification(message: PushMessage): String? {
        val appToken = getAppToken() ?: return "AppToken 未配置"
        val uid = getUid() ?: return "UID 未配置"

        val jsonBody = JSONObject().apply {
            put("appToken", appToken)
            put("content", message.content)
            put("contentType", 1)
            put("uids", JSONArray().put(uid))
        }

        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(API_URL)
                    .post(jsonBody.toString().toRequestBody(MEDIA_TYPE_JSON))
                    .build()

                var error: String? = null
                client.newCall(request).execute().use { response ->
                    val body = response.body.string()
                    if (!response.isSuccessful) {
                        error = "HTTP ${response.code}: $body"
                        android.util.Log.w("WxPusher", "Push failed: code=${response.code}, body=$body")
                    }
                }
                error
            }
        } catch (e: Exception) {
            android.util.Log.w("WxPusher", "Push error: ${e.message}")
            e.message ?: "网络异常"
        }
    }

    /**
     * Sends a screenshot push as HTML (contentType=2) with the image embedded
     * as a base64 data URI. The bitmap is scaled via
     * [PixelBlockEncoder.encodeJpegBase64] to keep the payload small.
     *
     * Falls back to a plain-text push (contentType=1) when the image cannot be
     * encoded or the resulting content would exceed WxPusher's 40000-char limit.
     */
    suspend fun sendNotification(message: PushMessage, imageBytes: ByteArray): String? {
        val appToken = getAppToken() ?: return "AppToken 未配置"
        val uid = getUid() ?: return "UID 未配置"

        val dataUri = try {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (bitmap != null) PixelBlockEncoder.encodeJpegBase64(bitmap) else null
        } catch (e: Exception) {
            android.util.Log.w("WxPusher", "Image encode error: ${e.message}")
            null
        }

        val content = if (dataUri != null && dataUri.length < 40000) {
            "<p>${escapeHtml(message.content)}</p><br/><img src=\"$dataUri\" style=\"max-width:100%;\"/>"
        } else {
            message.content
        }

        val jsonBody = JSONObject().apply {
            put("appToken", appToken)
            put("content", content)
            put("contentType", if (dataUri != null && dataUri.length < 40000) 2 else 1)
            put("uids", JSONArray().put(uid))
        }

        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(API_URL)
                    .post(jsonBody.toString().toRequestBody(MEDIA_TYPE_JSON))
                    .build()

                var error: String? = null
                client.newCall(request).execute().use { response ->
                    val body = response.body.string()
                    if (!response.isSuccessful) {
                        error = "HTTP ${response.code}: $body"
                        android.util.Log.w("WxPusher", "Push failed: code=${response.code}, body=$body")
                    }
                }
                error
            }
        } catch (e: Exception) {
            android.util.Log.w("WxPusher", "Push error: ${e.message}")
            e.message ?: "网络异常"
        }
    }

    /** Escapes HTML-special characters in user-supplied text. */
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
    }

    private fun getAppToken(): String? {
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.WXPUSHER_APP_TOKEN.key]?.value.orEmpty()
        return token.ifBlank { null }
    }

    private fun getUid(): String? {
        val uid = GlobalVars.configStates[GLOBAL_SETTINGS.WXPUSHER_UID.key]?.value.orEmpty()
        return uid.ifBlank { null }
    }
}
