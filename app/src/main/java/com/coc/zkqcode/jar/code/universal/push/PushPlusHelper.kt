package com.coc.zkqcode.jar.code.universal.push

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Helper for sending push notifications via PushPlus (https://www.pushplus.plus).
 *
 * API: POST https://www.pushplus.plus/send
 * Body: { "token": "...", "title": "...", "content": "...", "template": "txt" }
 *
 * Credentials are read from global config:
 *   - PUSHPLUS_TOKEN : the user/message token
 */
object PushPlusHelper {

    private const val API_URL = "https://www.pushplus.plus/send"

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
        val token = getToken() ?: return "Token 未配置"

        val jsonBody = JSONObject().apply {
            put("token", token)
            put("title", message.title)
            put("content", message.content)
            put("template", "txt")
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
                        android.util.Log.w("PushPlus", "Push failed: code=${response.code}, body=$body")
                    }
                }
                error
            }
        } catch (e: Exception) {
            android.util.Log.w("PushPlus", "Push error: ${e.message}")
            e.message ?: "网络异常"
        }
    }

    private fun getToken(): String? {
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.PUSHPLUS_TOKEN.key]?.value.orEmpty()
        return token.ifBlank { null }
    }
}
