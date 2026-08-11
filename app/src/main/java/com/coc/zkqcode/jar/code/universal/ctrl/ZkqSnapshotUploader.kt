package com.coc.zkqcode.jar.code.universal.ctrl

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Snapshot relay (channel A): periodically POSTs the device status snapshot to
 * the AstrBot plugin's receiver (doc §13.2).
 *
 * Enable condition: ZKQ_SERVER_URL non-empty. The plugin's "enabled" switch is
 * the final gate (a disabled plugin rejects the handshake; we just retry).
 * The upload interval follows the plugin's recommended_interval (fallback 60s).
 */
object ZkqSnapshotUploader {

    private const val DEFAULT_INTERVAL_MS = 60_000L
    private const val NONCE_KEY = "zkq_device_nonce"

    @Volatile
    private var cachedIntervalMs: Long = DEFAULT_INTERVAL_MS

    /**
     * Log retention days pushed by the plugin (doc §13.2 extension).
     * Consumed by LogHelper.FileLoggingTree; 0 = not yet received.
     */
    @Volatile
    var logRetentionDays: Int = 0

    @Volatile
    private var lastAccount: Int = -1

    @Volatile
    private var lastEvent: String? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    private val MEDIA_TYPE_JSON = "application/json;charset=utf-8".toMediaTypeOrNull()

    /** Runs while the script engine coroutine is active; failures retry next cycle. */
    suspend fun run() {
        while (currentCoroutineContext().isActive) {
            if (!isEnabled()) return // switch off -> stop immediately
            try {
                uploadOnce()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.w("ZkqUpload", "snapshot upload error: ${e.message}")
            }
            delay(cachedIntervalMs)
        }
    }

    /** The plugin comm switch (GlobalSettings.ZKQ_ENABLED, "0" = off). */
    fun isEnabled(): Boolean =
        GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_ENABLED.key]?.value != "0"

    private suspend fun uploadOnce() {
        val serverUrl = GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_SERVER_URL.key]?.value.orEmpty()
        val base = normalize(serverUrl) ?: return
        val url = "$base/zkq_snapshot"

        val account = InGameVars.currentAccountNumber
        if (account != lastAccount) {
            lastEvent = "上号 #$account 成功"
            lastAccount = account
        }

        val body = JSONObject().apply {
            put("deviceId", StatusSnapshot.deviceId())
            put("nonce", getOrCreateNonce())
            put("snapshot", StatusSnapshot.build(lastEvent))
            put("ts", System.currentTimeMillis() / 1000)
        }

        // Shared secret travels in a request header (not the URL/body) so it
        // stays out of gateway access logs.
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_TOKEN.key]?.value.orEmpty()
        val request = Request.Builder()
            .url(url)
            .apply { if (token.isNotBlank()) addHeader("X-Zkq-Token", token) }
            .post(body.toString().toRequestBody(MEDIA_TYPE_JSON))
            .build()

        withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body.string()
                    val json = try {
                        JSONObject(respBody)
                    } catch (_: Exception) {
                        null
                    }
                    // Follow the plugin's recommended interval (doc §13.2)
                    if (json != null && json.optString("status") == "ok") {
                        val interval = json.optLong("recommended_interval")
                        if (interval > 0) cachedIntervalMs = interval * 1000
                        val retention = json.optInt("log_retention_days", 0)
                        if (retention > 0) logRetentionDays = retention
                    } else if (json != null && json.optString("code") == "device_conflict") {
                        // Name conflict with a genuinely different device (doc §13.1):
                        // auto-rename ("<name>-2", ...) and retry next cycle.
                        StatusSnapshot.bumpDeviceIdOnConflict()
                    }
                }
                // Failures are silent here; retried on the next cycle.
            }
        }
    }

    private suspend fun getOrCreateNonce(): String {
        val existing = readMemory(NONCE_KEY)
        if (existing.isNotBlank()) return existing
        val generated = UUID.randomUUID().toString().replace("-", "").substring(0, 16)
        writeMemory(NONCE_KEY, generated)
        return generated
    }

    /**
     * Normalizes the server url (doc §3.6): adds http:// when missing,
     * rejects other protocols, strips trailing "/".
     */
    private fun normalize(url: String): String? {
        if (url.isBlank()) return null
        val normalized = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else if (url.contains("://")) {
            return null
        } else {
            "http://$url"
        }
        return normalized.trimEnd('/')
    }
}
