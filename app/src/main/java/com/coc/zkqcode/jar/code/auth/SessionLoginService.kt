package com.coc.zkqcode.jar.code.auth

import com.coc.zkqcode.core.util.crypto.solvePoW
import com.coc.zkqcode.jar.code.universal.network.UrlFallbackUtil
import com.coc.zkqcode.nativehelper.RustTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Result of a successful session login.
 */
data class SessionLoginResult(
    val sessionKey: String,
    val gem: String
)

/**
 * Encodes a value for use in URL query / form parameters.
 */
internal fun urlEncode(value: String): String {
    return URLEncoder.encode(value, Charsets.UTF_8.name())
}

/**
 * Performs the full session-login API flow:
 *  1. Fetch PoW challenge from `api/pow/challenge`
 *  2. Solve PoW locally
 *  3. Get network timestamp
 *  4. Build and encrypt payload (email, password, timestamp, optional session_key) via native layer
 *  5. POST to `api/mobile-session-login`
 *  6. Decrypt and parse the response
 *
 * If [existingSessionKey] is provided it is included in the encrypted payload so the server can
 * reuse or replace it.  The caller must **always** overwrite its stored key with the one returned
 * by the server because the server may create a new key even when an existing one was sent.
 *
 * @param email                User email.
 * @param password             User password.
 * @param existingSessionKey   Previously stored session key (null or blank on first login).
 * @return [SessionLoginResult] on success.
 * @throws Exception on any network, encryption, or server error.
 */
suspend fun performSessionLogin(
    email: String,
    password: String,
    existingSessionKey: String? = null
): SessionLoginResult = withContext(Dispatchers.IO) {
    // 1. Fetch PoW challenge
    val powNonce = UrlFallbackUtil
        .executeGetWithFallbackAndReadBody("api/pow/challenge")
        .let { JSONObject(it).getString("nonce") }

    // 2. Solve PoW
    val powSalt = solvePoW(powNonce)

    // 3. Get network timestamp
    val timestamp = getNetworkTimestamp()

    // 4. Build payload and encrypt via native layer
    var payload = "email=${urlEncode(email)}&password=${urlEncode(password)}&timestamp=$timestamp"
    if (!existingSessionKey.isNullOrBlank()) {
        payload += "&session_key=${urlEncode(existingSessionKey)}"
    }
    val encryptionParts = RustTools.encryptLoginPayload(payload).split(",", limit = 3)
    require(encryptionParts.size == 3) { "Encryption returned unexpected format" }

    // 5. POST to server
    val postData = "public_key=${encryptionParts[0]}" +
        "&nonce=${encryptionParts[1]}" +
        "&data=${encryptionParts[2]}" +
        "&pow_nonce=$powNonce" +
        "&pow_salt=$powSalt"

    val responseBody = UrlFallbackUtil.executeWithFallbackAndReadBody(
        endpoint = "api/mobile-session-login",
        requestBuilder = { baseUrl ->
            Request.Builder()
                .url("${baseUrl}api/mobile-session-login")
                .post(postData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull()))
                .build()
        }
    )

    // 6. Decrypt and parse
    val decrypted = RustTools.decryptLoginResponse(responseBody)
    if (decrypted.startsWith("Error")) {
        throw IllegalStateException(decrypted)
    }

    val fields = parseUrlEncoded(decrypted)
    val sessionKey = fields["session_key"]
    val gem = fields["gem"]

    if (sessionKey.isNullOrBlank() || gem.isNullOrBlank()) {
        throw IllegalStateException("Unexpected response: $decrypted")
    }

    SessionLoginResult(sessionKey = sessionKey, gem = gem)
}

/**
 * Parses a URL-encoded string into a map of key-value pairs.
 */
private fun parseUrlEncoded(raw: String): Map<String, String> {
    if (raw.isBlank()) return emptyMap()
    val result = mutableMapOf<String, String>()
    raw.split("&").forEach { part ->
        val key = part.substringBefore("=", "")
        if (key.isEmpty()) return@forEach
        val value = part.substringAfter("=", "")
        result[key] = value
    }
    return result
}
