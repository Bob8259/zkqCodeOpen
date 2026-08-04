package com.coc.zkqcode.jar.code.universal.push

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Helper for sending push notifications via AstrBot's HTTP API (AstrBot >= 4.27).
 *
 * Endpoints (Bearer auth with the scoped API key, e.g. "abk_..."):
 *   GET  {base}/api/v1/im/bots       -> discover enabled bot (platform) ids
 *   POST {base}/api/v1/im/messages   -> send message, body { "umo": ..., "message": ... }
 *   POST {base}/api/v1/file          -> upload an image, returns { "data": { "attachment_id": ... } }
 *
 * The "umo" (unified message origin) selects the target session:
 *   "<platformId>:<FriendMessage|GroupMessage>:<target>"
 *   - platformId comes from /im/bots (e.g. "default" for a single qq_official bot)
 *   - target is the session id: openid for qq_official, QQ number for aiocqhttp, etc.
 *
 * Config keys:
 *   - ASTRBOT_BASE_URL  : server address, e.g. http://192.168.1.100:6185
 *   - ASTRBOT_API_KEY   : AstrBot scoped API key
 *   - ASTRBOT_TARGET_ID : target session id (openid / QQ number / group id)
 *   - ASTRBOT_MSG_TYPE  : "0" = private (FriendMessage), "1" = group (GroupMessage)
 */
object AstrBotHelper {

    private const val BOTS_PATH = "/api/v1/im/bots"
    private const val MESSAGES_PATH = "/api/v1/im/messages"
    private const val FILE_UPLOAD_PATH = "/api/v1/file"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val MEDIA_TYPE_JSON = "application/json;charset=utf-8".toMediaTypeOrNull()
    private val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()

    // Cache the discovered platform (bot) id; cleared implicitly on process restart.
    @Volatile
    private var cachedPlatformId: String? = null

    /** @return null on success, error string on failure. */
    suspend fun sendNotification(
        message: PushMessage,
        imageBytes: ByteArray? = null,
        brailleArt: String? = null
    ): String? {
        val baseUrl = getBaseUrl() ?: return "AstrBot 地址未配置"
        val apiKey = getApiKey() ?: return "API Key 未配置"
        val target = getTargetId() ?: return "目标ID 未配置"
        val base = buildApiBase(baseUrl) ?: return "AstrBot 地址格式错误"

        val platformId = getPlatformId(base, apiKey) ?: return "AstrBot 未配置可用平台"
        val msgType = if (GlobalVars.configStates[GLOBAL_SETTINGS.ASTRBOT_MSG_TYPE.key]?.value == "1") {
            "GroupMessage"
        } else {
            "FriendMessage"
        }
        val umo = "$platformId:$msgType:$target"

        val jsonBody = if (imageBytes != null) {
            val attachmentId = uploadImage(base, apiKey, imageBytes) ?: return "图片上传失败"
            JSONObject().apply {
                put("umo", umo)
                put("message", buildImageParts(message.content, attachmentId))
            }
        } else {
            JSONObject().apply {
                put("umo", umo)
                put("message", effectiveText(message, brailleArt))
            }
        }

        return postJson(base, apiKey, MESSAGES_PATH, jsonBody)
    }

    /**
     * Text payload: plain string, appending braille art in a code block when provided.
     */
    private fun effectiveText(message: PushMessage, brailleArt: String?): String {
        if (brailleArt == null || brailleArt.isBlank()) return message.content
        val codeBlock = "```\n$brailleArt\n```"
        return if (message.content.isBlank()) codeBlock else "${message.content}\n\n$codeBlock"
    }

    /**
     * Image payload: optional text caption part, then an image part referencing an uploaded attachment.
     */
    private fun buildImageParts(caption: String, attachmentId: String): JSONArray {
        val parts = JSONArray()
        if (caption.isNotBlank()) {
            parts.put(JSONObject().apply {
                put("type", "plain")
                put("text", caption)
            })
        }
        parts.put(JSONObject().apply {
            put("type", "image")
            put("attachment_id", attachmentId)
        })
        return parts
    }

    /**
     * Discovers the platform (bot) id via /api/v1/im/bots and caches the first one.
     * @return platform id, or null when no bot is available.
     */
    private suspend fun getPlatformId(base: String, apiKey: String): String? {
        cachedPlatformId?.let { if (it.isNotBlank()) return it }
        val id = withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$base$BOTS_PATH")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    val json = JSONObject(response.body.string())
                    if (json.optString("status") != "ok") return@use null
                    val botIds = json.optJSONObject("data")?.optJSONArray("bot_ids") ?: return@use null
                    if (botIds.length() == 0) return@use null
                    botIds.optString(0).ifBlank { null }
                }
            } catch (e: Exception) {
                android.util.Log.w("AstrBot", "Get platform id error: ${e.message}")
                null
            }
        }
        if (id != null) cachedPlatformId = id
        return id
    }

    /**
     * Uploads the image via /api/v1/file (multipart "file" field).
     * @return attachment id, or null on failure.
     */
    private suspend fun uploadImage(base: String, apiKey: String, imageBytes: ByteArray): String? {
        return withContext(Dispatchers.IO) {
            try {
                val multipart = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg", imageBytes.toRequestBody(MEDIA_TYPE_JPEG))
                    .build()
                val request = Request.Builder()
                    .url("$base$FILE_UPLOAD_PATH")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .post(multipart)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    val json = JSONObject(response.body.string())
                    if (json.optString("status") != "ok") return@use null
                    json.optJSONObject("data")?.optString("attachment_id")
                }
            } catch (e: Exception) {
                android.util.Log.w("AstrBot", "Upload image error: ${e.message}")
                null
            }
        }
    }

    /**
     * Posts a JSON body with Bearer auth and parses the AstrBot envelope
     * ({ "status": "ok", ... } = success; { "status": "error", "message": ... } = failure).
     * @return null on success, error string on failure.
     */
    private suspend fun postJson(
        base: String,
        apiKey: String,
        path: String,
        jsonBody: JSONObject
    ): String? {
        return try {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("$base$path")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .post(jsonBody.toString().toRequestBody(MEDIA_TYPE_JSON))
                    .build()

                var error: String? = null
                client.newCall(request).execute().use { response ->
                    val body = response.body.string()
                    if (response.isSuccessful) {
                        val json = try {
                            JSONObject(body)
                        } catch (_: Exception) {
                            null
                        }
                        if (json != null && json.optString("status") != "ok") {
                            error = json.optString("message").ifBlank { "未知错误" }
                        }
                    } else {
                        error = "HTTP ${response.code}: $body"
                    }
                    if (error != null) {
                        android.util.Log.w("AstrBot", "Push failed: $error | body=$body")
                    }
                }
                error
            }
        } catch (e: Exception) {
            android.util.Log.w("AstrBot", "Push error: ${e.message}")
            e.message ?: "网络异常"
        }
    }

    private fun getBaseUrl(): String? {
        val url = GlobalVars.configStates[GLOBAL_SETTINGS.ASTRBOT_BASE_URL.key]?.value.orEmpty()
        return url.ifBlank { null }
    }

    private fun getApiKey(): String? {
        val key = GlobalVars.configStates[GLOBAL_SETTINGS.ASTRBOT_API_KEY.key]?.value.orEmpty()
        return key.ifBlank { null }
    }

    private fun getTargetId(): String? {
        val id = GlobalVars.configStates[GLOBAL_SETTINGS.ASTRBOT_TARGET_ID.key]?.value.orEmpty()
        return id.ifBlank { null }
    }

    /**
     * Normalizes the base url per doc §3.4:
     * - missing http:// / https:// prefix -> prepend http://
     * - other protocols (ftp:// etc.) -> null (format error)
     * - trailing "/" stripped
     */
    private fun buildApiBase(baseUrl: String): String? {
        val normalized = if (baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) {
            baseUrl
        } else if (baseUrl.contains("://")) {
            return null
        } else {
            "http://$baseUrl"
        }
        return normalized.trimEnd('/')
    }
}
