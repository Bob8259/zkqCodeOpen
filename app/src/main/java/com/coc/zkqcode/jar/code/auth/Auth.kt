package com.coc.zkqcode.jar.code.auth

import android.content.Context
import androidx.core.content.edit
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.crypto.solvePoW
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.jar.ui.Strings
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

/**
 * If the zkq_international flag in auth_prefs SharedPreferences is true,
 * block forever showing a login prompt every 2 seconds.
 * If the flag is false or absent, return immediately so the caller keeps its current logic.
 */
private suspend fun blockIfInternational() {
    val authPrefs = ZKQApplication.appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val isInternational = try {
        authPrefs.getBoolean("zkq_international", false)
    } catch (_: Exception) {
        false
    }
    if (!isInternational) return
    while (true) {
        ShowMessage("Please login to use.")
        delay(2000)
    }
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

suspend fun userAuth() {
    authMutex.withLock {
        // Early exit if gem_count is insufficient
        val gemCount = GlobalVars.configStates["gem_count"]?.value?.toDoubleOrNull() ?: 0.0
        if (gemCount <= 0.00001) {
            GlobalVars.isShowAd = true
            blockIfInternational()
            return
        }
        ShowMessage("准备连接服务器")
        val email = GlobalVars.configStates[GLOBAL_SETTINGS.EMAIL.key]?.value.orEmpty()
        val password = GlobalVars.configStates[GLOBAL_SETTINGS.PASSWORD.key]?.value.orEmpty()
        if (email.isBlank() || password.isBlank()) {
            // Flag ad display on missing credentials
            GlobalVars.isShowAd = true
            blockIfInternational()
            return
        }

        // --- Session token check and refresh workflow ---
        val authPrefs = ZKQApplication.appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        fun clearStoredSessionKey() {
            try {
                authPrefs.edit { remove("session_key") }
            } catch (_: Exception) {
            }
        }

        fun isInvalidSessionMessage(message: String): Boolean {
            return message.contains("会话") ||
                message.contains("过期") ||
                message.contains("401") ||
                message.contains("Session key not active", ignoreCase = true)
        }

        suspend fun loginAndStoreSessionKey(showFailureMessage: Boolean): String? {
            return try {
                val loginResult = performSessionLogin(email, password)
                try {
                    authPrefs.edit { putString("session_key", loginResult.sessionKey) }
                } catch (_: Exception) {
                }
                GlobalVars.configStates["gem_count"]?.value = loginResult.gem
                loginResult.sessionKey
            } catch (e: Exception) {
                if (showFailureMessage) {
                    ShowMessage("Session login failed / 会话登录失败")
                }
                null
            }
        }

        suspend fun failAuth() {
            GlobalVars.isShowAd = true
            blockIfInternational()
            // Send Server酱³ push notification on login failure if enabled
            val loginNotify = GlobalVars.configStates[GLOBAL_SETTINGS.SERVERCHAN_LOGIN_NOTIFY.key]?.value == "1"
            if (loginNotify) {
                ServerChanHelper.sendNotification(
                    title = Strings.serverchanLoginFailedTitle,
                    desp = Strings.serverchanLoginFailedMsg
                )
            }
        }

        suspend fun refreshSessionKeyAfterInvalidSession(): String? {
            clearStoredSessionKey()
            return loginAndStoreSessionKey(showFailureMessage = false)
        }

        var sessionKey = authPrefs.getString("session_key", null)
        if (sessionKey.isNullOrBlank()) {
            sessionKey = loginAndStoreSessionKey(showFailureMessage = true)
            if (sessionKey.isNullOrBlank()) {
                failAuth()
                return
            }
        }

        var hasRetriedWithNewSession = false
        while (true) {
            val activeSessionKey = sessionKey
            if (activeSessionKey.isNullOrBlank()) {
                failAuth()
                return
            }

            val timestamp = getNetworkTimestamp()
            // Retrieve last_time from native Rust storage (auto-initializes on first call)
            val lastTime = RustTools.getLastTime()

            val payload = "session_key=${urlEncode(activeSessionKey)}" + "&timestamp=$timestamp" + "&last_time=$lastTime"

            val encryptionParts = try {
                RustTools.encryptLoginPayload(payload).split(",", limit = 3)
            } catch (_: Exception) {
                // Flag ad display on encryption failure
                failAuth()
                return
            }
            if (encryptionParts.size != 3) {
                // Flag ad display on abnormal encryption result
                failAuth()
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
                if (isInvalidSessionMessage(errorMsg) && !hasRetriedWithNewSession) {
                    hasRetriedWithNewSession = true
                    sessionKey = refreshSessionKeyAfterInvalidSession()
                    if (!sessionKey.isNullOrBlank()) {
                        continue
                    }
                }
                if (isInvalidSessionMessage(errorMsg)) {
                    ShowMessage("会话无效或已过期，请重新登录")
                    clearStoredSessionKey()
                }
                // Flag ad display on PoW challenge or deduct request failure
                failAuth()
                return
            }

            if (responseCode != 200) {
                if (isInvalidSessionMessage(responseBody) && !hasRetriedWithNewSession) {
                    hasRetriedWithNewSession = true
                    sessionKey = refreshSessionKeyAfterInvalidSession()
                    if (!sessionKey.isNullOrBlank()) {
                        continue
                    }
                }
                if (isInvalidSessionMessage(responseBody)) {
                    ShowMessage("会话无效或已过期，请重新登录")
                    clearStoredSessionKey()
                } else {
                    ShowMessage("登录失败，错误信息：$responseBody")
                    delayWithMultiplier(2000)
                }
                // Flag ad display on non-200 deduct response
                failAuth()
                return
            }

            val decrypted = try {
                RustTools.decryptLoginResponse(responseBody)
            } catch (e: Exception) {
                val errorMsg = e.message ?: ""
                if (isInvalidSessionMessage(errorMsg) && !hasRetriedWithNewSession) {
                    hasRetriedWithNewSession = true
                    sessionKey = refreshSessionKeyAfterInvalidSession()
                    if (!sessionKey.isNullOrBlank()) {
                        continue
                    }
                }
                // Flag ad display on response decryption failure
                failAuth()
                return
            }
            if (decrypted.startsWith("Error")) {
                if (isInvalidSessionMessage(decrypted) && !hasRetriedWithNewSession) {
                    hasRetriedWithNewSession = true
                    sessionKey = refreshSessionKeyAfterInvalidSession()
                    if (!sessionKey.isNullOrBlank()) {
                        continue
                    }
                }
                if (isInvalidSessionMessage(decrypted)) {
                    ShowMessage("会话无效或已过期，请重新登录")
                    clearStoredSessionKey()
                }
                // Flag ad display on decrypted error response
                failAuth()
                return
            }

            val result = parseUrlEncoded(decrypted)
            val newTimestamp = result["timestamp"]?.toLongOrNull()
            when {
                newTimestamp != null -> {
                    // Persist updated timestamp back into native Rust storage
                    RustTools.updateLastTime(newTimestamp)
                    GlobalVars.isShowAd = false
                    return
                }

                isInvalidSessionMessage(decrypted) -> {
                    if (!hasRetriedWithNewSession) {
                        hasRetriedWithNewSession = true
                        sessionKey = refreshSessionKeyAfterInvalidSession()
                        if (!sessionKey.isNullOrBlank()) {
                            continue
                        }
                    }
                    ShowMessage("会话无效或已过期，请重新登录")
                    clearStoredSessionKey()
                    failAuth()
                    return
                }

                result["msg"] == "gem_not_enough" || decrypted.contains("邮箱或密码错误") -> {
                    // Reset gem_count to zero in config file and UI state
                    GlobalVars.serverActions?.writeToConfigFile("gem_count", "0")
                    GlobalVars.configStates["gem_count"]?.value = "0"
                    // Flag ad display on insufficient gems or wrong credentials
                    failAuth()
                    return
                }

                else -> {
                    // Flag ad display on unparseable deduct response
                    failAuth()
                    return
                }
            }
        }
    }
}