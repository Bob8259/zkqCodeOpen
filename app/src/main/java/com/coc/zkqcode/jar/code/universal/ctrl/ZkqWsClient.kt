package com.coc.zkqcode.jar.code.universal.ctrl

import android.graphics.Bitmap
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.loadSchedule
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * WS long connection client (channel B', doc §13.3/§13.4).
 *
 * Connects outbound to the AstrBot plugin's /zkq_ws endpoint (device only
 * dials out; no inbound exposure). Handshake carries token / deviceId / nonce
 * as query params; a 409 (device_conflict) triggers an automatic rename
 * ("<name>-2", "-3"...) via StatusSnapshot and a reconnect.
 *
 * Incoming query frames ({requestId, query, lines, maxChars}) are executed by
 * the read-only interpreter and answered with a result frame on the same
 * connection. Reconnects use exponential backoff; okhttp pingInterval keeps
 * the connection alive (plugin heartbeat=30s).
 */
object ZkqWsClient {

    private const val NONCE_KEY = "zkq_device_nonce"

    private const val BACKOFF_INITIAL_MS = 1_000L
    private const val BACKOFF_MAX_MS = 60_000L

    private const val DEFAULT_SNAPSHOT_INTERVAL_MS = 60_000L

    @Volatile
    private var cachedSnapshotIntervalMs: Long = DEFAULT_SNAPSHOT_INTERVAL_MS

    @Volatile
    private var lastCleanupDay = ""

    @Volatile
    private var lastAccount: Int = -1

    @Volatile
    private var lastEvent: String? = null

    /** The plugin comm switch (GlobalSettings.ZKQ_ENABLED, "0" = off). */
    fun isEnabled(): Boolean =
        GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_ENABLED.key]?.value != "0"

    /** Returns true if a non-empty, valid server URL is configured. */
    fun hasValidServerUrl(): Boolean = serverBase() != null

    /**
     * Triggered by scripts or state transitions to immediately notify AstrBot of a state change.
     */
    fun notifyStateChanged(event: String? = null) {
        if (!event.isNullOrBlank()) {
            lastEvent = event
        }
        val ws = activeWs ?: return
        CoroutineScope(Dispatchers.IO).launch {
            sendSnapshot(ws, lastEvent)
        }
    }

    /**
     * Builds and sends a snapshot frame over WebSocket.
     */
    suspend fun sendSnapshot(webSocket: WebSocket? = activeWs, event: String? = lastEvent): Boolean {
        val ws = webSocket ?: activeWs ?: return false
        val body = JSONObject().apply {
            put("type", "snapshot")
            put("deviceId", StatusSnapshot.deviceId())
            put("nonce", getOrCreateNonce())
            put("snapshot", StatusSnapshot.build(event))
            put("ts", System.currentTimeMillis() / 1000)
        }
        return ws.send(body.toString())
    }

    private fun launchSnapshotLoop(webSocket: WebSocket, scope: CoroutineScope) {
        scope.launch {
            cleanupOldLogs()
            while (isActive && activeWs === webSocket) {
                sendSnapshot(webSocket)
                delay(cachedSnapshotIntervalMs)
            }
        }
    }

    /**
     * Deletes log files older than the configured retention (once per day).
     * Priority: AstrBot plugin pushed retention > local GLOBAL_SETTINGS.LOG_RETENTION_DAYS > default 3.
     */
    fun cleanupOldLogs(pushedRetention: Int = 0) {
        val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        if (lastCleanupDay == today && pushedRetention <= 0) return
        lastCleanupDay = today

        val days = when {
            pushedRetention > 0 -> pushedRetention
            else -> GlobalVars.configStates[GLOBAL_SETTINGS.LOG_RETENTION_DAYS.key]
                ?.value?.toIntOrNull() ?: 3
        }
        if (days <= 0) return // <= 0 = keep forever

        try {
            val logDir = File(ZKQApplication.appContext.filesDir, "logs")
            if (!logDir.exists() || !logDir.isDirectory) return
            val cutoff = System.currentTimeMillis() - days * 86_400_000L
            logDir.listFiles()?.forEach { f ->
                val isLogFile = f.isFile && f.name.endsWith(".log") &&
                    (f.name.startsWith("info") || f.name.startsWith("error"))
                if (isLogFile && f.lastModified() < cutoff) {
                    f.delete()
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "cleanupOldLogs error: ${e.message}")
        }
    }

    /** 当前活动的 WS 连接（App 主动推送扫码提取结果用）。 */
    @Volatile
    private var activeWs: WebSocket? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // WS is long-lived; pingInterval monitors liveness
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    /** Runs as a persistent background daemon; serves queries whenever configured. */
    suspend fun run() {
        var backoffMs = BACKOFF_INITIAL_MS
        while (currentCoroutineContext().isActive) {
            if (!isEnabled() || !hasValidServerUrl()) {
                delay(5_000)
                continue
            }
            val url = buildWsUrl()
            if (url == null) {
                delay(5_000)
                continue
            }
            val reason = connectAndServe(url)
            if (!currentCoroutineContext().isActive) return
            when (reason) {
                Reason.CONFLICT -> {
                    // 409: our deviceId belongs to a genuinely different device.
                    StatusSnapshot.bumpDeviceIdOnConflict()
                    backoffMs = BACKOFF_INITIAL_MS
                }
                Reason.BROKEN -> {
                    android.util.Log.w("ZkqWs", "ws broken, retry in ${backoffMs}ms")
                    delay(backoffMs)
                    backoffMs = (backoffMs * 2).coerceAtMost(BACKOFF_MAX_MS)
                }
                Reason.DISABLED -> {
                    // 403/404: plugin disabled or not deployed -> slow retry, keep trying
                    android.util.Log.w("ZkqWs", "ws rejected (disabled?), retry in 30s")
                    delay(30_000)
                    backoffMs = BACKOFF_INITIAL_MS
                }
            }
        }
    }

    private enum class Reason { BROKEN, CONFLICT, DISABLED }

    /** Connects and serves query frames until the socket closes. */
    private suspend fun connectAndServe(url: String): Reason {
        val closed = CompletableDeferred<Reason>()
        val deviceId = StatusSnapshot.deviceId()
        val query = buildHandshakeQuery(deviceId)
        val token = zkqToken()

        // Shared secret travels in a request header (not the URL) so it stays
        // out of gateway access logs.
        val request = Request.Builder()
            .url("$url?$query")
            .apply { if (token.isNotBlank()) addHeader("X-Zkq-Token", token) }
            .build()

        val scope = CoroutineScope(currentCoroutineContext())
        val wsRef = java.util.concurrent.atomic.AtomicReference<WebSocket?>(null)
        wsRef.set(client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                android.util.Log.i("ZkqWs", "connected as $deviceId")
                activeWs = webSocket
                launchSnapshotLoop(webSocket, scope)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch { handleFrame(webSocket, text) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                if (activeWs === webSocket) activeWs = null
                val code = response?.code
                val reason = when {
                    code == 409 -> Reason.CONFLICT
                    code == 403 || code == 404 || code == 400 -> Reason.DISABLED
                    else -> Reason.BROKEN
                }
                android.util.Log.w("ZkqWs", "failure code=$code: ${t.message}")
                if (!closed.isCompleted) closed.complete(reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (activeWs === webSocket) activeWs = null
                android.util.Log.w("ZkqWs", "closed code=$code reason=$reason")
                if (!closed.isCompleted) closed.complete(Reason.BROKEN)
            }
        }))

        return try {
            closed.await()
        } catch (e: CancellationException) {
            wsRef.get()?.cancel()
            throw e
        }
    }

    // ── Query interpreter (doc §13.4) ─────────────────────────────────
    private suspend fun handleFrame(webSocket: WebSocket, text: String) {
        try {
            val frame = JSONObject(text)
            val type = frame.optString("type")
            if (type == "snapshot_ack") {
                val interval = frame.optLong("recommended_interval", 0)
                if (interval > 0) cachedSnapshotIntervalMs = interval * 1000
                val retention = frame.optInt("log_retention_days", 0)
                cleanupOldLogs(retention)
                return
            }

            val requestId = frame.optLong("requestId", -1)
            if (requestId < 0) return
            val query = frame.optString("query")
            if (query == "start" || query == "run") {
                AppStateManager.setMode(AppMode.Run)
                val reply = JSONObject().apply {
                    put("requestId", requestId)
                    put("ok", true)
                    put("data", "已远程启动脚本")
                }
                webSocket.send(reply.toString())
                return
            }
            if (query == "stop" || query == "pause") {
                AppStateManager.setMode(AppMode.Main)
                val reply = JSONObject().apply {
                    put("requestId", requestId)
                    put("ok", true)
                    put("data", "已远程停止脚本（进入待机模式）")
                }
                webSocket.send(reply.toString())
                return
            }
            if (query == "screenshot") {
                handleScreenshot(webSocket, requestId)
                return
            }
            if (query == "logfile") {
                val lines = frame.optInt("lines", 0).coerceIn(0, 500_000)
                val hours = frame.optInt("hours", 0).coerceIn(0, 720)
                val days = frame.optInt("days", 0).coerceIn(0, 30)
                handleLogfile(webSocket, requestId, lines, hours, days)
                return
            }
            if (query == "clearlogs") {
                handleClearLogs(webSocket, requestId, frame.optInt("days", 0).coerceIn(0, 30))
                return
            }
            val lines = frame.optInt("lines", 200).coerceIn(1, 100_000)
            val maxChars = frame.optInt("maxChars", 4000).coerceIn(100, 10_000_000)
            val days = frame.optInt("days", 0).coerceIn(0, 30)

            val (ok, payload) = executeQuery(query, lines, maxChars, days)
            val reply = JSONObject().apply {
                put("requestId", requestId)
                put("ok", ok)
                if (ok) put("data", payload) else put("error", payload)
            }
            webSocket.send(reply.toString())
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "frame handling error: ${e.message}")
        }
    }

    /**
     * Screenshot query (doc §13.4 extension): captures the screen, uploads it
     * losslessly (PNG, no scaling) to the plugin, which packs it into a zip
     * and sends the original file to QQ (files are not re-compressed by QQ).
     */
    private suspend fun handleScreenshot(webSocket: WebSocket, requestId: Long) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        try {
            val bitmap = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
                ?: throw IllegalStateException("截图失败（无画面帧）")
            val png = encodePng(bitmap)
            val filename = uploadScreenshot(png, requestId)
            if (filename == null) {
                reply.put("ok", false).put("error", "截图上传失败")
            } else {
                reply.put("ok", true).put("data", filename)
            }
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "screenshot error: ${e.message}")
            reply.put("ok", false).put("error", e.message ?: "截图失败")
        }
        webSocket.send(reply.toString())
    }

    /**
     * Log archive query: zips log files filtered by the requested range
     * (lines / hours / days) and uploads the archive to the plugin.
     */
    private suspend fun handleLogfile(webSocket: WebSocket, requestId: Long, lines: Int, hours: Int, days: Int) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        try {
            val zip = packLogsToZip(lines, hours, days)
                ?: throw IllegalStateException("无日志文件")
            val filename = uploadLogfile(zip, requestId)
            if (filename == null) {
                reply.put("ok", false).put("error", "日志上传失败")
            } else {
                reply.put("ok", true).put("data", filename)
            }
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "logfile error: ${e.message}")
            reply.put("ok", false).put("error", e.message ?: "打包失败")
        }
        webSocket.send(reply.toString())
    }

    /**
     * Packs log lines into a zip archive. Range rules:
     *  - lines > 0: last N lines across the per-day files
     *  - otherwise hours/days > 0: lines whose timestamp falls in the window
     *  - otherwise: everything (full retention window)
     */
    private fun packLogsToZip(lines: Int, hours: Int, days: Int): ByteArray? {
        val logDir = File(ZKQApplication.appContext.filesDir, "logs")
        if (!logDir.exists()) return null
        val files = logDir.listFiles { f ->
            f.isFile && f.name.startsWith("info") && f.name.endsWith(".log")
        }?.sortedByDescending { it.name.removePrefix("info").removeSuffix(".log") }
            ?: emptyList()
        if (files.isEmpty()) return null

        val collected = mutableListOf<String>()
        if (lines > 0) {
            var remaining = lines
            for (f in files) {
                if (remaining <= 0) break
                val fileLines = try {
                    f.readLines()
                } catch (e: Exception) {
                    continue
                }
                val take = minOf(remaining, fileLines.size)
                collected.addAll(0, fileLines.takeLast(take))
                remaining -= take
            }
        } else {
            val cutoff = when {
                days > 0 -> System.currentTimeMillis() - days * 86_400_000L
                hours > 0 -> System.currentTimeMillis() - hours * 3_600_000L
                else -> 0L
            }
            val dayCutoff = if (cutoff > 0) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(cutoff))
            } else {
                ""
            }
            for (f in files) {
                val fileLines = try {
                    f.readLines()
                } catch (e: Exception) {
                    continue
                }
                if (dayCutoff.isEmpty()) {
                    collected.addAll(0, fileLines)
                } else {
                    collected.addAll(0, fileLines.filter { it.length >= 10 && it.substring(0, 10) >= dayCutoff })
                }
            }
        }
        if (collected.isEmpty()) return null

        val bos = ByteArrayOutputStream()
        try {
            java.util.zip.ZipOutputStream(bos).use { zos ->
                val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                zos.putNextEntry(java.util.zip.ZipEntry("logs_$stamp.txt"))
                zos.write(collected.joinToString("\n").toByteArray(Charsets.UTF_8))
                zos.closeEntry()
            }
            return bos.toByteArray()
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "pack logs error: ${e.message}")
            return null
        }
    }

    /** Uploads the log archive to the plugin's /zkq_logfile endpoint. */
    private suspend fun uploadLogfile(zip: ByteArray, requestId: Long): String? {
        val base = serverBase() ?: return null
        val deviceId = StatusSnapshot.deviceId()
        val nonce = getOrCreateNonce()
        val url = "$base/zkq_logfile?deviceId=${URLEncoder.encode(deviceId, "UTF-8")}" +
            "&nonce=$nonce&requestId=$requestId"
        return withContext(Dispatchers.IO) {
            try {
                val multipart = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "logs.zip", zip.toRequestBody("application/zip".toMediaTypeOrNull()))
                    .build()
                val token = zkqToken()
                val request = Request.Builder()
                    .url(url)
                    .apply { if (token.isNotBlank()) addHeader("X-Zkq-Token", token) }
                    .post(multipart)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    val json = try {
                        JSONObject(response.body.string())
                    } catch (_: Exception) {
                        null
                    }
                    if (json?.optString("status") == "ok") {
                        json.optString("filename").ifBlank { null }
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("ZkqWs", "upload logfile error: ${e.message}")
                null
            }
        }
    }

    /**
     * Clear logs query (destructive, private-chat gated on the plugin side):
     * deletes the retained log FILES (days <= 0 = all; days > 0 = files
     * touched within the last N days).
     */
    private suspend fun handleClearLogs(webSocket: WebSocket, requestId: Long, days: Int) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        try {
            val logDir = File(ZKQApplication.appContext.filesDir, "logs")
            var deleted = 0
            logDir.listFiles()?.forEach { f ->
                val isLog = f.isFile && f.name.endsWith(".log") &&
                    (f.name.startsWith("info") || f.name.startsWith("error"))
                if (!isLog) return@forEach
                val recent = days > 0 && f.lastModified() >= System.currentTimeMillis() - days * 86_400_000L
                if (days <= 0 || recent) {
                    if (f.delete()) deleted++
                }
            }
            reply.put("ok", true).put("data", "已删除 $deleted 个日志文件")
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "clearlogs error: ${e.message}")
            reply.put("ok", false).put("error", e.message ?: "删除失败")
        }
        webSocket.send(reply.toString())
    }

    /** Lossless PNG encoding at the original resolution (no scaling). */
    private fun encodePng(bitmap: Bitmap): ByteArray {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
        return bos.toByteArray()
    }

    /** Uploads the PNG to the plugin's /zkq_screenshot endpoint. */
    private suspend fun uploadScreenshot(png: ByteArray, requestId: Long): String? {
        val base = serverBase() ?: return null
        val deviceId = StatusSnapshot.deviceId()
        val nonce = getOrCreateNonce()
        val url = "$base/zkq_screenshot?deviceId=${URLEncoder.encode(deviceId, "UTF-8")}" +
            "&nonce=$nonce&requestId=$requestId"
        return withContext(Dispatchers.IO) {
            try {
                val multipart = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "screenshot.png", png.toRequestBody("image/png".toMediaTypeOrNull()))
                    .build()
                val token = zkqToken()
                val request = Request.Builder()
                    .url(url)
                    .apply { if (token.isNotBlank()) addHeader("X-Zkq-Token", token) }
                    .post(multipart)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use null
                    val json = try {
                        JSONObject(response.body.string())
                    } catch (_: Exception) {
                        null
                    }
                    if (json?.optString("status") == "ok") {
                        json.optString("filename").ifBlank { null }
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("ZkqWs", "upload screenshot error: ${e.message}")
                null
            }
        }
    }

    private suspend fun executeQuery(
        query: String,
        lines: Int,
        maxChars: Int,
        days: Int = 0
    ): Pair<Boolean, String> {
        return try {
            when (query) {
                "status" -> true to StatusSnapshot.build(currentLastEvent()).toString()
                "upgrade" -> true to buildUpgradeSummary()
                "logs" -> true to readLogTail("info", lines, maxChars, days)
                "errors" -> true to readLogTail("error", lines, maxChars, days)
                "ping" -> true to "连接正常（状态数据更新于 " +
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()) + "）"
                else -> false to "unknown query: $query"
            }
        } catch (e: Exception) {
            false to "interpreter error: ${e.message}"
        }
    }

    /** Tracks the most recent account-switch event, same rule as the uploader. */
    private fun currentLastEvent(): String? {
        val account = InGameVars.currentAccountNumber
        if (account != lastAccount) {
            lastEvent = "上号 #$account 成功"
            lastAccount = account
        }
        return lastEvent
    }

    private suspend fun buildUpgradeSummary(): String {
        val schedule = loadSchedule()
        if (schedule.isEmpty()) return "升级时间表（空）"
        val now = System.currentTimeMillis() / 1000
        val sb = StringBuilder("升级时间表（${schedule.size} 项）")
        for (item in schedule.sortedBy { it.completionTs }) {
            val remain = (item.completionTs - now).coerceAtLeast(0)
            sb.append("\n#").append(item.account)
                .append(" ").append(item.name)
                .append(" · ").append(fmtDuration(remain))
        }
        return sb.toString()
    }

    /**
     * Reads log tail across the per-day rolling files (info-20260803.log,
     * legacy info.log counts as the oldest). When days > 0 the lines are
     * filtered by their timestamp first; oversized data is sampled down so
     * the result stays consumable by the LLM.
     */
    private fun readLogTail(prefix: String, lines: Int, maxChars: Int, days: Int = 0): String {
        val logDir = File(ZKQApplication.appContext.filesDir, "logs")
        if (!logDir.exists()) return "(日志目录不存在)"
        val files = logDir.listFiles { f ->
            f.isFile && f.name.startsWith(prefix) && f.name.endsWith(".log")
        }?.sortedByDescending { it.name.removePrefix(prefix).removeSuffix(".log") }
            ?: emptyList()
        if (files.isEmpty()) return "($prefix.log 不存在)"

        val cutoff = if (days > 0) {
            System.currentTimeMillis() - days * 86_400_000L
        } else {
            0L
        }
        val dayCutoff = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(cutoff))
        val collected = mutableListOf<String>()
        var remaining = lines
        for (f in files) {
            if (remaining <= 0) break
            try {
                val fileLines = f.readLines()
                val filtered = if (days > 0) {
                    // line format: "yyyy-MM-dd HH:mm:ss.SSS [tag] msg" - lexicographic compare works
                    fileLines.filter { it.length >= 10 && it.substring(0, 10) >= dayCutoff }
                } else {
                    fileLines
                }
                val take = minOf(remaining, filtered.size)
                collected.addAll(0, filtered.takeLast(take))
                remaining -= take
            } catch (e: Exception) {
                continue
            }
        }
        val sampled = sampleForLlm(collected)
        val text = sampled.joinToString("\n")
        val masked = maskSecrets(text)
        return if (masked.length > maxChars) masked.take(maxChars) + "\n…(截断)" else masked
    }

    /**
     * Keeps the payload within LLM-friendly bounds: all error/warning and
     * key event lines survive; ordinary lines are sampled (1 of every 8).
     */
    private fun sampleForLlm(lines: List<String>): List<String> {
        if (lines.size <= 8000) return lines
        val keys = listOf(
            "ERROR", "WARN", "CRASH", "错误", "失败", "成功", "上号", "切号",
            "部落战", "联赛", "升级", "战斗", "重启", "异常", "暂停", "恢复"
        )
        val kept = mutableListOf<String>()
        var skipped = 0
        for (line in lines) {
            if (keys.any { line.contains(it) }) {
                kept.add(line)
                skipped = 0
            } else {
                skipped++
                if (skipped % 8 == 0) kept.add(line)
            }
        }
        return kept
    }

    /**
     * Log sanitization (doc §13.4): configured EMAIL/PASSWORD values plus
     * common patterns (IPv4, CN mobile number, standalone digit runs).
     */
    private fun maskSecrets(text: String): String {
        var out = text
        for (key in listOf(GLOBAL_SETTINGS.EMAIL.key, GLOBAL_SETTINGS.PASSWORD.key)) {
            val value = GlobalVars.configStates[key]?.value.orEmpty()
            if (value.length >= 3) out = out.replace(value, "***")
        }
        out = out.replace(Patterns.IPV4, "***")
        out = out.replace(Patterns.MOBILE, "***")
        out = out.replace(Patterns.DIGIT_RUN, "***")
        return out
    }

    // ── URL / handshake helpers ─────────────────────────────────────
    private fun serverBase(): String? {
        val serverUrl = GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_SERVER_URL.key]?.value.orEmpty()
        if (serverUrl.isBlank()) return null
        return normalize(serverUrl)
    }

    private fun buildWsUrl(): String? {
        val base = serverBase() ?: return null
        val wsBase = if (base.startsWith("https://")) {
            "wss://" + base.removePrefix("https://")
        } else {
            "ws://" + base.removePrefix("http://")
        }
        return "$wsBase/zkq_ws"
    }

    private suspend fun buildHandshakeQuery(deviceId: String): String {
        val params = mutableListOf(
            "deviceId=" + URLEncoder.encode(deviceId, "UTF-8"),
            "nonce=" + getOrCreateNonce(),
        )
        return params.joinToString("&")
    }

    /** The shared comm secret; blank when not configured. */
    private fun zkqToken(): String =
        GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_TOKEN.key]?.value.orEmpty()

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

    private fun fmtDuration(seconds: Long): String {
        if (seconds <= 0) return "已到期"
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        return if (h > 0) "${h}小时${m}分" else "${m}分"
    }

    private object Patterns {
        val IPV4 = Regex("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b")
        val MOBILE = Regex("1[3-9]\\d{9}")
        val DIGIT_RUN = Regex("(?<!\\d)\\d{5,12}(?!\\d)")
    }
}
