package com.coc.zkqcode.jar.code.auth

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
 * Unified push notification router.
 *
 * Dispatches to the provider selected in config (push_provider):
 *   "0" → Server酱³
 *   "1" → WxPusher
 *   "2" → PushPlus
 *   "3" → Telegram
 *
 * All call sites use ServerChanHelper.sendNotification() — the routing
 * is transparent and no call-site changes are needed.
 */
object ServerChanHelper {

    /**
     * Send a push notification through the currently configured provider.
     *
     * @param title   Message title.
     * @param desp    Message body (optional).
     * @return null on success, or an error message string on failure.
     */
    suspend fun sendNotification(title: String, desp: String = ""): String? {
        val provider = GlobalVars.configStates[GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
        return when (provider) {
            "1" -> WxPusherHelper.sendNotification(title, desp)
            "2" -> PushPlusHelper.sendNotification(title, desp)
            "3" -> TelegramHelper.sendNotification(title, desp)
            else -> sendViaServerChan(title, desp)
        }
    }

    // ── Server酱³ implementation ──────────────────────────────────────────

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val MEDIA_TYPE = "application/x-www-form-urlencoded;charset=utf-8".toMediaTypeOrNull()

    /** @return null on success, error string on failure. */
    private suspend fun sendViaServerChan(title: String, desp: String = ""): String? {
        val sendKey = getSendKey() ?: return "SendKey 未配置"
        val url = buildApiUrl(sendKey) ?: return "SendKey 格式错误"

        val postBody = buildPostBody(title, desp)

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
