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

object TelegramHelper {

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
        val botToken = getBotToken() ?: return "BotToken 未配置"
        val chatId = getChatId() ?: return "ChatID 未配置"

        val apiUrl = "https://api.telegram.org/bot$botToken/sendMessage"

        val jsonBody = JSONObject().apply {
            put("chat_id", chatId)
            put("text", message.telegramText)
            put("parse_mode", "Markdown")
        }

        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(apiUrl)
                    .post(jsonBody.toString().toRequestBody(MEDIA_TYPE_JSON))
                    .build()

                var error: String? = null
                client.newCall(request).execute().use { response ->
                    val body = response.body.string()
                    if (response.isSuccessful) {
                        val json = JSONObject(body)
                        if (!json.optBoolean("ok", false)) {
                            error = json.optString("description", "unknown")
                        }
                    } else {
                        // Try to extract description from Telegram error body
                        val desc = try {
                            JSONObject(body).optString("description", "")
                        } catch (_: Exception) { "" }
                        error = if (desc.isNotBlank()) "HTTP ${response.code}: $desc" else "HTTP ${response.code}"
                    }
                    if (error != null) {
                        android.util.Log.w("Telegram", "Push failed: $error | body=$body")
                    }
                }
                error
            }
        } catch (e: Exception) {
            android.util.Log.w("Telegram", "Push error: ${e.message}")
            e.message ?: "网络异常"
        }
    }

    private fun getBotToken(): String? {
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.TELEGRAM_BOT_TOKEN.key]?.value.orEmpty()
        return token.ifBlank { null }
    }

    private fun getChatId(): String? {
        val id = GlobalVars.configStates[GLOBAL_SETTINGS.TELEGRAM_CHAT_ID.key]?.value.orEmpty()
        return id.ifBlank { null }
    }
}
