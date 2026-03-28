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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
        // Early exit if gem_count is insufficient
        val gemCount = GlobalVars.configStates["gem_count"]?.value?.toDoubleOrNull() ?: 0.0
        if (gemCount <= 0.00001) {
            GlobalVars.isShowAd = true
            return
        }
        ShowMessage("准备连接服务器")
        val email = GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]?.value.orEmpty()
        val password = GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]?.value.orEmpty()
        if (email.isBlank() || password.isBlank()) {
            // Flag ad display on missing credentials
            GlobalVars.isShowAd = true
            return
        }

        val baseUrl = BuildConfig.BASE_URL
        val timestamp = System.currentTimeMillis()
        // Retrieve last_time from native Rust storage (auto-initializes on first call)
        val lastTime = RustTools.getLastTime()

        // Format timestamps to UTC+8 human-readable time for debugging
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+8")
        }
        ShowMessage("timestamp: $timestamp -> ${sdf.format(Date(timestamp))}")
        ShowMessage("last_time: $lastTime -> ${sdf.format(Date(lastTime))}")

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
            // Flag ad display on PoW challenge failure
            GlobalVars.isShowAd = true
            return
        }

        val powSalt = try {
            solvePoW(powNonce)
        } catch (e: Exception) {
            // Flag ad display on PoW solve failure
            GlobalVars.isShowAd = true
            return
        }

        val payload = "email=${urlEncode(email)}" +
                "&password=${urlEncode(password)}" +
                "&timestamp=$timestamp" +
                "&last_time=$lastTime"

        val encryptionParts = try {
            RustTools.encryptLoginPayload(payload).split(",", limit = 3)
        } catch (e: Exception) {
            // Flag ad display on encryption failure
            GlobalVars.isShowAd = true
            return
        }
        if (encryptionParts.size != 3) {
            // Flag ad display on abnormal encryption result
            GlobalVars.isShowAd = true
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
            // Flag ad display on deduct request failure
            GlobalVars.isShowAd = true
            return
        }

        if (responseCode != 200) {
            // Flag ad display on non-200 deduct response
            GlobalVars.isShowAd = true
            return
        }

        val decrypted = try {
            RustTools.decryptLoginResponse(responseBody)
        } catch (e: Exception) {
            // Flag ad display on response decryption failure
            GlobalVars.isShowAd = true
            return
        }
        if (decrypted.startsWith("Error")) {
            // Flag ad display on decrypted error response
            GlobalVars.isShowAd = true
            return
        }

        val result = parseUrlEncoded(decrypted)
        val newTimestamp = result["timestamp"]?.toLongOrNull()
        when {
            newTimestamp != null -> {
                // Persist updated timestamp back into native Rust storage
                RustTools.updateLastTime(newTimestamp)
                GlobalVars.isShowAd = false
            }

            result["msg"] == "gem_not_enough" -> {
                // Reset gem_count to zero in config file and UI state
                GlobalVars.serverActions?.writeToConfigFile("gem_count", "0")
                GlobalVars.configStates["gem_count"]?.value = "0"
                // Flag ad display on insufficient gems
                GlobalVars.isShowAd = true
            }

            else -> {
                // Flag ad display on unparseable deduct response
                GlobalVars.isShowAd = true
            }
        }
    }
}