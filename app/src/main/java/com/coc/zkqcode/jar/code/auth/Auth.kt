package com.coc.zkqcode.jar.code.auth

import com.coc.zkqcode.BuildConfig
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.coc.zkqcode.jar.ui.pages.single.solvePoW
import java.net.URLDecoder
import java.net.URLEncoder

private val authMutex = Mutex()
private val httpClient = OkHttpClient()

private fun parseUrlEncoded(raw: String): Map<String, String> {
    if (raw.isBlank()) return emptyMap()
    val result = mutableMapOf<String, String>()
    val charset = Charsets.UTF_8.name()
    raw.split("&").forEach { part ->
        val key = part.substringBefore("=", "")
        if (key.isEmpty()) return@forEach
        val value = part.substringAfter("=", "")
        result[URLDecoder.decode(key, charset)] = URLDecoder.decode(value, charset)
    }
    return result
}

private fun urlEncode(value: String): String {
    return URLEncoder.encode(value, Charsets.UTF_8.name())
}

suspend fun userAuth() {
    authMutex.withLock {
        val email = GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]?.value.orEmpty()
        val password = GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]?.value.orEmpty()
        if (email.isBlank() || password.isBlank()) {
            ShowMessage("请先填写邮箱和密码")
            return
        }

        val baseUrl = BuildConfig.BASE_URL
        val timestamp = System.currentTimeMillis()
        // Retrieve last_time from native Rust storage (auto-initializes on first call)
        val lastTime = RustTools.getLastTime()

        val powNonce = try {
            val challengeRequest = Request.Builder().url("${baseUrl}api/pow/challenge").get().build()
            val challengeResponse = withContext(Dispatchers.IO) {
                httpClient.newCall(challengeRequest).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IllegalStateException("PoW challenge failed: ${response.code}")
                    }
                    response.body.string()
                }
            }
            JSONObject(challengeResponse).getString("nonce")
        } catch (e: Exception) {
            ShowMessage("获取PoW失败: ${e.message}")
            return
        }

        val powSalt = try {
            solvePoW(powNonce)
        } catch (e: Exception) {
            ShowMessage("PoW计算失败: ${e.message}")
            return
        }

        val payload = "email=${urlEncode(email)}" +
            "&password=${urlEncode(password)}" +
            "&timestamp=$timestamp" +
            "&last_time=$lastTime"

        val encryptionParts = try {
            RustTools.encryptLoginPayload(payload).split(",", limit = 3)
        } catch (e: Exception) {
            ShowMessage("请求加密失败: ${e.message}")
            return
        }
        if (encryptionParts.size != 3) {
            ShowMessage("请求加密结果异常")
            return
        }

        val postData = "public_key=${encryptionParts[0]}" +
            "&nonce=${encryptionParts[1]}" +
            "&data=${encryptionParts[2]}" +
            "&pow_nonce=$powNonce" +
            "&pow_salt=$powSalt"

        val responseCode: Int
        val responseBody: String
        try {
            val requestBody = postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
            val deductRequest = Request.Builder()
                .url("${baseUrl}api/mobile-deduct")
                .post(requestBody)
                .build()
            val response = withContext(Dispatchers.IO) {
                httpClient.newCall(deductRequest).execute()
            }
            response.use {
                responseCode = it.code
                responseBody = it.body.string()
            }
        } catch (e: Exception) {
            ShowMessage("扣费请求失败: ${e.message}")
            return
        }

        if (responseCode != 200) {
            ShowMessage("扣费失败($responseCode): $responseBody")
            return
        }

        val decrypted = try {
            RustTools.decryptLoginResponse(responseBody)
        } catch (e: Exception) {
            ShowMessage("响应解密失败: ${e.message}")
            return
        }
        if (decrypted.startsWith("Error")) {
            ShowMessage("响应解密失败: $decrypted")
            return
        }

        val result = parseUrlEncoded(decrypted)
        val newTimestamp = result["timestamp"]?.toLongOrNull()
        when {
            newTimestamp != null -> {
                // Persist updated timestamp back into native Rust storage
                RustTools.updateLastTime(newTimestamp)
                ShowMessage("宝石扣费成功")
            }

            result["msg"] == "gem_not_enough" -> {
                ShowMessage("宝石不足，无法继续扣费")
            }

            else -> {
                ShowMessage("扣费响应无法解析: $decrypted")
            }
        }
    }
}