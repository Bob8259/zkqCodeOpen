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

object ServerChanHelper {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val MEDIA_TYPE = "application/x-www-form-urlencoded;charset=utf-8".toMediaTypeOrNull()

    suspend fun send(message: PushMessage): String? {
        return send(message, null)
    }

    suspend fun send(message: PushMessage, brailleArt: String?): String? {
        val sendKey = getSendKey() ?: return "SendKey 未配置"
        val url = buildApiUrl(sendKey) ?: return "SendKey 格式错误"

        // Append braille art to desp if provided, wrapped in code block for monospace alignment
        val effectiveDesp = if (brailleArt != null && brailleArt.isNotBlank()) {
            val codeBlock = "```\n$brailleArt\n```"
            if (message.desp.isBlank()) codeBlock else "${message.desp}\n\n$codeBlock"
        } else {
            message.desp
        }
        val postBody = buildPostBody(message.title, effectiveDesp)

        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(url)
                    .post(postBody.toRequestBody(MEDIA_TYPE))
                    .build()

                var error: String? = null
                client.newCall(request).execute().use { response ->
                    val body = response.body.string()
                    if (response.isSuccessful) {
                        val json = JSONObject(body)
                        val code = json.optInt("code", -1)
                        if (code != 0) {
                            error = json.optString("message", "unknown error")
                            android.util.Log.w("ServerChan", "Push failed: code=$code, msg=$error")
                        }
                    } else {
                        error = "HTTP ${response.code}: $body"
                    }
                }
                error
            }
        } catch (e: Exception) {
            android.util.Log.w("ServerChan", "Push error: ${e.message}")
            e.message ?: "网络异常"
        }
    }

    private fun getSendKey(): String? {
        val sendKey = GlobalVars.configStates[GLOBAL_SETTINGS.SERVERCHAN_SENDKEY.key]?.value.orEmpty()
        return sendKey.ifBlank { null }
    }

    private fun buildApiUrl(sendKey: String): String? {
        return if (sendKey.startsWith("sctp")) {
            val match = Regex("""sctp(\d+)t""").find(sendKey)
            val num = match?.groupValues?.get(1) ?: return null
            "https://${num}.push.ft07.com/send/${sendKey}.send"
        } else {
            "https://sctapi.ftqq.com/${sendKey}.send"
        }
    }

    private fun buildPostBody(title: String, desp: String): String {
        val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
        val encodedDesp = java.net.URLEncoder.encode(desp, "UTF-8")
        return "title=$encodedTitle&desp=$encodedDesp"
    }
}
