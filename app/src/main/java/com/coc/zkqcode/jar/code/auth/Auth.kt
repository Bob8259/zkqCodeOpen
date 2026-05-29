package com.coc.zkqcode.jar.code.auth

import android.content.Context
import androidx.core.content.edit
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.crypto.solvePoW
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val authMutex = Mutex()

// Dedicated client with short timeouts for network time fetching
private val timeClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).build()

/**
 * Retries [action] indefinitely on connection timeout (SocketTimeoutException),
 * waiting a random delay between 2–5 seconds between attempts and showing a message.
 * Any other exception is rethrown immediately.
 */
private suspend fun <T> retryOnTimeout(action: suspend () -> T): T {
    while (true) {
        try {
            return action()
        } catch (_: SocketTimeoutException) {
            val delayMs = Random.nextLong(1000L, 3001L)
            ShowMessage("连接超时，${delayMs / 1000}秒后重试...")
            delay(delayMs)
        }
    }
}

// Fetch epoch millis from QQ video checktime API (returns seconds, multiply by 1000)
private suspend fun fetchTimestampFromQQ(): Long? = withContext(Dispatchers.IO) {
    val request = Request.Builder().url("http://vv.video.qq.com/checktime?otype=json").get().build()
    timeClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return@withContext null
        val body = response.body.string()
        val match = Regex(""""t"\s*:\s*(\d+)""").find(body)
        match?.groupValues?.get(1)?.toLongOrNull()?.times(1000)
    }
}

// Fetch epoch millis from Suning time API (already returns millis)
private suspend fun fetchTimestampFromSuning(): Long? = withContext(Dispatchers.IO) {
    val request = Request.Builder().url("https://f.m.suning.com/api/ct.do").get().build()
    timeClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return@withContext null
        val json = JSONObject(response.body.string())
        if (json.has("currentTime")) json.getLong("currentTime") else null
    }
}

// Fetch epoch millis from zkqcoc.store HTTP Date header
private suspend fun fetchTimestampFromZkqcoc(): Long? = withContext(Dispatchers.IO) {
    val request = Request.Builder().url("https://zkqcoc.store/").head().build()
    timeClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) return@withContext null
        val dateStr = response.header("Date") ?: return@withContext null
        val fmt = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        fmt.timeZone = TimeZone.getTimeZone("GMT")
        fmt.parse(dateStr)?.time
    }
}

/**
 * Cascading network timestamp retrieval (epoch millis):
 * 1. QQ checktime API
 * 2. Suning time API
 * 3. zkqcoc.store HTTP Date header
 * 4. Local system time (fallback)
 */
suspend fun getNetworkTimestamp(): Long {
    val sources: List<suspend () -> Long?> = listOf(
        ::fetchTimestampFromQQ, ::fetchTimestampFromSuning, ::fetchTimestampFromZkqcoc
    )
    for (source in sources) {
        try {
            val ts = source()
            if (ts != null) return ts
        } catch (_: Exception) {
        }
    }
    return System.currentTimeMillis()
}

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

        // --- Session token check: skip login if session_key exists in SharedPreferences ---
        val authPrefs = ZKQApplication.appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        if (!authPrefs.contains("session_key")) {
            // Perform session login (mirrors Login.kt login() flow)
            try {
                // Fetch PoW challenge
                val powNonceForSession = withContext(Dispatchers.IO) {
                    UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/pow/challenge").let { responseBody ->
                        JSONObject(responseBody).getString("nonce")
                    }
                }

                // Solve PoW locally
                val powSaltForSession = solvePoW(powNonceForSession)

                // Get timestamp and build payload (no last_time for session login)
                val timestampForSession = getNetworkTimestamp()
                val sessionPayload = "email=${urlEncode(email)}&password=${urlEncode(password)}&timestamp=$timestampForSession"

                // Encrypt payload via native layer
                val sessionEncryptionParts = try {
                    RustTools.encryptLoginPayload(sessionPayload).split(",", limit = 3)
                } catch (_: Exception) {
                    GlobalVars.isShowAd = true
                    return
                }
                if (sessionEncryptionParts.size != 3) {
                    GlobalVars.isShowAd = true
                    return
                }

                // Build and send session login request
                val sessionPostData =
                    "public_key=${sessionEncryptionParts[0]}" + "&nonce=${sessionEncryptionParts[1]}" + "&data=${sessionEncryptionParts[2]}" + "&pow_nonce=$powNonceForSession" + "&pow_salt=$powSaltForSession"

                val sessionResponseBody = withContext(Dispatchers.IO) {
                    UrlFallbackUtil.executeWithFallbackAndReadBody(
                        endpoint = "api/mobile-session-login", requestBuilder = { baseUrl ->
                            Request.Builder().url("${baseUrl}api/mobile-session-login").post(sessionPostData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())).build()
                        })
                }

                // Decrypt response
                val sessionDecrypted = try {
                    RustTools.decryptLoginResponse(sessionResponseBody)
                } catch (_: Exception) {
                    GlobalVars.isShowAd = true
                    return
                }
                if (sessionDecrypted.startsWith("Error")) {
                    GlobalVars.isShowAd = true
                    return
                }

                // Parse response: expected session_key=xxx&gem=yyy
                val sessionResult = parseUrlEncoded(sessionDecrypted)
                val sessionKey = sessionResult["session_key"]
                val sessionGem = sessionResult["gem"]

                if (sessionKey != null && sessionGem != null) {
                    // Store session key in SharedPreferences
                    try {
                        ZKQApplication.appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit { putString("session_key", sessionKey) }
                    } catch (_: Exception) {
                    }
                    // Update gem count
                    GlobalVars.configStates["gem_count"]?.value = sessionGem
                } else {
                    ShowMessage("Session login failed: unexpected response / 会话登录失败：响应异常")
                    GlobalVars.isShowAd = true
                    return
                }
            } catch (_: Exception) {
                ShowMessage("Session login failed / 会话登录失败")
                GlobalVars.isShowAd = true
                return
            }
        }

        // --- Session deduct flow ---
        val sessionKey = authPrefs.getString("session_key", null)
        if (sessionKey.isNullOrBlank()) {
            GlobalVars.isShowAd = true
            return
        }

        val timestamp = getNetworkTimestamp()
        // Retrieve last_time from native Rust storage (auto-initializes on first call)
        val lastTime = RustTools.getLastTime()

        val payload = "session_key=${urlEncode(sessionKey)}" + "&timestamp=$timestamp" + "&last_time=$lastTime"

        val encryptionParts = try {
            RustTools.encryptLoginPayload(payload).split(",", limit = 3)
        } catch (_: Exception) {
            // Flag ad display on encryption failure
            GlobalVars.isShowAd = true
            return
        }
        if (encryptionParts.size != 3) {
            // Flag ad display on abnormal encryption result
            GlobalVars.isShowAd = true
            return
        }

        // A PoW challenge is only valid for 30 s, so fetch a fresh one for every deduct attempt.
        // The entire challenge→solve→deduct sequence is retried together on connection timeout.
        val (responseCode, responseBody) = try {
            retryOnTimeout {
                // Fetch a fresh PoW challenge and parse the nonce in one step with fallback
                val powNonce = withContext(Dispatchers.IO) {
                    UrlFallbackUtil.executeGetWithFallbackAndReadBody("api/pow/challenge").let { responseBody ->
                        JSONObject(responseBody).getString("nonce")
                    }
                }

                // Solve the PoW locally
                val powSalt = solvePoW(powNonce)

                // Build and send the deduct request with the fresh PoW
                val postData = "public_key=${encryptionParts[0]}" + "&nonce=${encryptionParts[1]}" + "&data=${encryptionParts[2]}" + "&pow_nonce=$powNonce" + "&pow_salt=$powSalt"
                withContext(Dispatchers.IO) {
                    UrlFallbackUtil.executeWithFallbackAndReadBody(
                        endpoint = "api/mobile-session-deduct", requestBuilder = { baseUrl ->
                            Request.Builder().url("${baseUrl}api/mobile-session-deduct").post(postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())).build()
                        }).let { responseBody ->
                        Pair(200, responseBody)
                    }
                }
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("会话") || errorMsg.contains("过期") || errorMsg.contains("401")) {
                ShowMessage("会话无效或已过期，请重新登录")
                try {
                    authPrefs.edit { remove("session_key") }
                } catch (_: Exception) {
                }
            }
            // Flag ad display on PoW challenge or deduct request failure
            GlobalVars.isShowAd = true
            return
        }

        if (responseCode != 200) {
            // Flag ad display on non-200 deduct response
            ShowMessage("登录失败，错误信息：$responseBody")
            delayWithMultiplier(2000)
            GlobalVars.isShowAd = true
            return
        }

        val decrypted = try {
            RustTools.decryptLoginResponse(responseBody)
        } catch (_: Exception) {
            // Flag ad display on response decryption failure
            GlobalVars.isShowAd = true
            return
        }
        if (decrypted.startsWith("Error")) {
            if (decrypted.contains("会话") || decrypted.contains("过期") || decrypted.contains("401")) {
                try {
                    authPrefs.edit { remove("session_key") }
                } catch (_: Exception) {
                }
            }
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

            result["msg"] == "gem_not_enough" || decrypted.contains("邮箱或密码错误") || decrypted.contains("会话无效") -> {
                if (decrypted.contains("会话") || decrypted.contains("过期")) {
                    try {
                        authPrefs.edit { remove("session_key") }
                    } catch (_: Exception) {
                    }
                }
                // Reset gem_count to zero in config file and UI state
                GlobalVars.serverActions?.writeToConfigFile("gem_count", "0")
                GlobalVars.configStates["gem_count"]?.value = "0"
                // Flag ad display on insufficient gems or wrong credentials
                GlobalVars.isShowAd = true
            }

            else -> {
                // Flag ad display on unparseable deduct response
                GlobalVars.isShowAd = true
            }
        }
    }
}