package com.coc.zkqcode.jar.code.universal.ctrl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.provider.Settings
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.universal.smalltools.qrlink.QrFlowCoroutineContext
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.loadSchedule
import com.coc.zkqcode.jar.code.universal.smalltools.qrlink.QrLinkFlow
import com.coc.zkqcode.jar.code.universal.smalltools.qrlink.QrLinkState
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
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
import java.util.concurrent.atomic.AtomicBoolean

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

    // A stuck capture pipeline must not hang the query frame forever.
    private const val SCREENSHOT_TIMEOUT_MS = 15_000L

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

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // WS is long-lived; pingInterval monitors liveness
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    // File uploads are plain HTTP calls; they must not inherit the WS readTimeout(0),
    // otherwise a stuck plugin response would hang the upload coroutine forever.
    private val uploadClient = client.newBuilder()
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * 扫码提取流程的独立协程 scope：不随脚本引擎重启/设置更新而取消。
     * 引擎重启（保存设置触发"重新运行"）会取消引擎 scope，若流程挂在引擎 scope 上
     * 会被一起杀掉——表现为"qr_start 回 ok 但流程凭空消失"（实测踩坑，见 ADR-0003）。
     */
    private val qrFlowScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** 并发 qr_start 帧互斥：保证"检查-启动"原子，同一时刻只允许一个扫码流程启动。 */
    private val qrStarting = AtomicBoolean(false)

    /** 当前活动的 WS 连接（App 主动推送扫码提取结果用）。 */
    @Volatile
    private var activeWs: WebSocket? = null

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
        val requestBuilder = Request.Builder()
            .url("$url?$query")
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_TOKEN.key]?.value.orEmpty()
        if (token.isNotBlank()) requestBuilder.header("X-Zkq-Token", token)
        val request = requestBuilder.build()

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
                GlobalVars.isPlaying.value = true
                GlobalVars.updateWindowPosition = true
                try {
                    ConfigManager.saveConfigs()
                } catch (e: Exception) {
                    AppStateManager.setMode(AppMode.Run)
                }
                notifyStateChanged("远程启动运行")
                val reply = JSONObject().apply {
                    put("requestId", requestId)
                    put("ok", true)
                    put("data", "已远程保存并启动脚本")
                }
                webSocket.send(reply.toString())
                return
            }
            if (query == "stop" || query == "pause") {
                GlobalVars.isPlaying.value = false
                notifyStateChanged("远程暂停运行")
                val reply = JSONObject().apply {
                    put("requestId", requestId)
                    put("ok", true)
                    put("data", "已远程暂停脚本")
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
                val days = frame.optInt("days", 0).coerceIn(0, 365)
                handleLogfile(webSocket, requestId, lines, hours, days)
                return
            }
            if (query == "clearlogs") {
                handleClearLogs(webSocket, requestId, frame.optInt("days", 0).coerceIn(0, 30))
                return
            }
            if (query == "qr_start") {
                handleQrStart(webSocket, requestId, frame)
                return
            }
            if (query == "qr_choose") {
                handleQrChoose(webSocket, requestId, frame)
                return
            }
            if (query == "qr_status") {
                handleQrStatus(webSocket, requestId)
                return
            }
            if (query == "qr_confirm") {
                handleQrConfirm(webSocket, requestId)
                return
            }
            if (query == "qr_cancel") {
                handleQrCancel(webSocket, requestId)
                return
            }
            if (query == "qr_continue") {
                handleQrContinue(webSocket, requestId, frame)
                return
            }
            if (query == "qr_finish") {
                handleQrFinish(webSocket, requestId)
                return
            }
            val lines = frame.optInt("lines", 200).coerceIn(1, 100_000)
            val maxChars = frame.optInt("maxChars", 4000).coerceIn(100, 10_000_000)
            val days = frame.optInt("days", 0).coerceIn(0, 365)

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
     * 扫码提取结果主动推送（ADR-0003）：App 在提取成功/失败后主动发通知帧给插件，
     * 不等插件轮询——这样即使 App 随后重启/断线，通知也已经发出。
     * 成功通知带重试（最多约 30 秒，等待 WS 重连后补发），失败通知只尝试一次。
     * 插件收到后转发到发起扫码提取的对话会话（type=qr_notify 帧，无 requestId）。
     */
    /**
     * 扫码提取会话通知（ADR-0004）：App 主动推送帧给插件，插件转发到发起会话。
     * 默认单次发送（不阻塞流程）；[retry] 设为 true 且 [success] 时才重试
     * （最多约 30 秒，等待 WS 重连补发）。sessionEnd=true 让插件结束轮询任务。
     */
    suspend fun sendQrNotify(
        slot: Int,
        success: Boolean,
        message: String,
        retry: Boolean = false,
        sessionEnd: Boolean = false
    ): Boolean {
        val payload = JSONObject().apply {
            put("type", "qr_notify")
            put("event", if (success) "extracted" else "failed")
            put("slot", slot)
            put("message", message)
            if (sessionEnd) put("sessionEnd", true)
        }.toString()
        val attempts = if (retry && success) 15 else 1
        repeat(attempts) {
            val ws = activeWs
            if (ws != null && ws.send(payload)) return true
            delay(2000)
        }
        android.util.Log.w("ZkqWs", "qr_notify send failed: success=$success slot=$slot")
        return false
    }

    /** 推送"截图已就绪"给插件（纯推送发图，ADR-0004）：插件收到后直接把图发给会话，不等轮询。 */
    suspend fun sendQrImage(filename: String, caption: String): Boolean {
        val payload = JSONObject().apply {
            put("type", "qr_image")
            put("filename", filename)
            put("caption", caption)
        }.toString()
        val ws = activeWs
        if (ws != null && ws.send(payload)) return true
        android.util.Log.w("ZkqWs", "qr_image send failed: $filename")
        return false
    }

    /**
     * Screenshot query (doc §13.4 extension): captures the screen, watermarks it
     * with the device name, uploads it losslessly (PNG, no scaling) to the plugin,
     * which packs it into a zip and sends the original file to QQ (files are not
     * re-compressed by QQ).
     */
    private suspend fun handleScreenshot(webSocket: WebSocket, requestId: Long) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        try {
            // Bounded so a stuck capture pipeline answers an explicit error
            // instead of hanging until the plugin-side query timeout.
            val filename = withTimeout(SCREENSHOT_TIMEOUT_MS) {
                captureAndUploadWatermarked(requestId)
            }
            if (filename == null) {
                reply.put("ok", false).put("error", "截图上传失败")
            } else {
                reply.put("ok", true).put("data", filename)
            }
        } catch (e: TimeoutCancellationException) {
            android.util.Log.w("ZkqWs", "screenshot timed out")
            reply.put("ok", false).put("error", "截图超时")
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "screenshot error: ${e.message}")
            reply.put("ok", false).put("error", e.message ?: "截图失败")
        }
        webSocket.send(reply.toString())
    }

    /**
     * 扫码提取（ADR-0003）异步两阶段的第一段：qr_start 只做校验并启动流程，
     * 立即回执（不阻塞 10s 查询超时）。流程在引擎 scope 后台跑，
     * 插件通过轮询 qr_status 推进（发码/村庄截图/确认/结果）。
     */
    private suspend fun handleQrStart(webSocket: WebSocket, requestId: Long, frame: JSONObject) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        // compareAndSet 让"检查-启动"原子化：并发到达的 qr_start 帧只有一个能通过，
        // 避免两个流程同时清数据/点屏幕互相踩踏（L4）。
        if (QrLinkState.isActive() || !qrStarting.compareAndSet(false, true)) {
            reply.put("ok", false).put("error", "该设备已有扫码提取流程进行中")
        } else {
            val slot = frame.optInt("slot", 0)
            // 登录方式可选：空 = 流程走到登录方式选择界面时停下来等对话选择
            val login = frame.optString("login").lowercase().takeIf { it == "qq" || it == "wechat" }
            if (slot <= 0) {
                qrStarting.set(false)
                reply.put("ok", false).put("error", "槽位参数无效")
            } else {
                qrFlowScope.launch(QrFlowCoroutineContext()) {
                    try {
                        QrLinkFlow.run(slot, login)
                    } finally {
                        qrStarting.set(false)
                    }
                }
                reply.put("ok", true).put("data", "已开始扫码提取（槽位 $slot" +
                    (login?.let { "，登录方式 $it" } ?: "，登录方式待对话选择") + "）")
            }
        }
        webSocket.send(reply.toString())
    }

    /** qr_choose：对话里选择 QQ/微信 登录方式（仅 AWAITING_LOGIN_CHOICE 阶段有效）。 */
    private suspend fun handleQrChoose(webSocket: WebSocket, requestId: Long, frame: JSONObject) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        val login = frame.optString("login").lowercase()
        if (login != "qq" && login != "wechat") {
            reply.put("ok", false).put("error", "登录方式参数无效（应为 qq 或 wechat）")
        } else if (QrLinkState.phase != QrLinkState.Phase.AWAITING_LOGIN_CHOICE) {
            reply.put("ok", false).put("error", "当前不在等待选择登录方式")
        } else {
            QrLinkState.loginChoiceRequested = login
            reply.put("ok", true).put("data", "已选择 $login 登录，正在继续")
        }
        webSocket.send(reply.toString())
    }

    /** qr_status：返回状态机快照（phase/slot/screenshot/error），插件据此推进对话。 */
    private suspend fun handleQrStatus(webSocket: WebSocket, requestId: Long) {
        val reply = JSONObject().apply {
            put("requestId", requestId)
            put("ok", true)
            put("data", QrLinkState.toStatusJson())
        }
        webSocket.send(reply.toString())
    }

    /** qr_confirm：村庄截图后的人工确认，触发提取。 */
    private suspend fun handleQrConfirm(webSocket: WebSocket, requestId: Long) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        if (QrLinkState.phase == QrLinkState.Phase.AWAITING_CONFIRM) {
            QrLinkState.confirmRequested = true
            reply.put("ok", true).put("data", "已确认，正在提取")
        } else {
            reply.put("ok", false).put("error", "当前无待确认的提取流程")
        }
        webSocket.send(reply.toString())
    }

    /** qr_cancel：中止流程，不提取。 */
    private suspend fun handleQrCancel(webSocket: WebSocket, requestId: Long) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        if (QrLinkState.isActive()) {
            QrLinkState.cancelRequested = true
            reply.put("ok", true).put("data", "已取消，不会提取")
        } else {
            reply.put("ok", false).put("error", "当前无进行中的提取流程")
        }
        webSocket.send(reply.toString())
    }

    /** qr_continue：对话门里继续提取下一个账号（ADR-0004）。 */
    private suspend fun handleQrContinue(webSocket: WebSocket, requestId: Long, frame: JSONObject) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        if (QrLinkState.phase != QrLinkState.Phase.AWAITING_NEXT) {
            reply.put("ok", false).put("error", "当前不在等待继续提取（需要先进入会话对话门）")
        } else {
            val slot = frame.optInt("slot", 0)
            if (slot <= 0) {
                reply.put("ok", false).put("error", "槽位参数无效")
            } else {
                QrLinkState.nextSlotRequested = slot
                QrLinkState.nextLoginRequested = frame.optString("login").lowercase().takeIf { it == "qq" || it == "wechat" }
                reply.put("ok", true).put("data", "已安排继续提取槽位 $slot")
            }
        }
        webSocket.send(reply.toString())
    }

    /** qr_finish：结束提取会话，设备杀进程重启恢复（ADR-0004）。 */
    private suspend fun handleQrFinish(webSocket: WebSocket, requestId: Long) {
        val reply = JSONObject().apply { put("requestId", requestId) }
        if (QrLinkState.isActive()) {
            QrLinkState.finishRequested = true
            reply.put("ok", true).put("data", "已结束提取会话，设备即将重启挂机")
        } else {
            reply.put("ok", false).put("error", "当前无进行中的提取会话")
        }
        webSocket.send(reply.toString())
    }

    /**
     * Captures the current screen, draws the device name in the top-right corner
     * and uploads the PNG to the plugin. Shared by the /zkq 截图 query and the
     * 扫码提取 flow so every uploaded screenshot carries the device name.
     */
    suspend fun captureAndUploadWatermarked(requestId: Long): String? {
        val bitmap = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
            ?: throw IllegalStateException("截图失败（无画面帧）")
        applyDeviceNameWatermark(bitmap)
        return uploadScreenshot(encodePng(bitmap), requestId)
    }

    /** Draws the device name (DEVICE_REMARK) in the top-right corner of the screenshot. */
    private suspend fun applyDeviceNameWatermark(bitmap: Bitmap) {
        try {
            val deviceName = StatusSnapshot.deviceId()
            if (deviceName.isBlank()) return
            val canvas = Canvas(bitmap)
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 42f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.RIGHT
            }
            val pad = 18f
            val textWidth = textPaint.measureText(deviceName)
            val rect = RectF(
                bitmap.width - textWidth - pad * 3, pad,
                bitmap.width - pad, pad * 3.2f + 42f
            )
            canvas.drawRoundRect(
                rect, 12f, 12f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(160, 0, 0, 0) }
            )
            canvas.drawText(deviceName, bitmap.width - pad * 2, pad * 2 + 42f, textPaint)
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "watermark failed: ${e.message}")
        }
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
     * Packs log lines into a zip archive, streaming line-by-line to a temp file
     * so a full retention window (e.g. a year of logs) never buffers in memory.
     * Range rules:
     *  - lines > 0: last N lines across the per-day files
     *  - otherwise hours/days > 0: lines whose timestamp falls in the window
     *  - otherwise: everything (full retention window)
     * Returns a temp zip File (caller deletes after upload), or null when empty.
     */
    private fun packLogsToZip(lines: Int, hours: Int, days: Int): File? {
        val logDir = File(ZKQApplication.appContext.filesDir, "logs")
        if (!logDir.exists()) return null
        val files = logDir.listFiles { f ->
            f.isFile && f.name.startsWith("info") && f.name.endsWith(".log")
        }?.sortedByDescending { it.name.removePrefix("info").removeSuffix(".log") }
            ?: emptyList()
        if (files.isEmpty()) return null

        val tmp = File(ZKQApplication.appContext.cacheDir, "zkq_logs_${System.currentTimeMillis()}.zip")
        try {
            java.util.zip.ZipOutputStream(tmp.outputStream()).use { zos ->
                val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                zos.putNextEntry(java.util.zip.ZipEntry("logs_$stamp.txt"))
                fun emit(line: String) {
                    zos.write(line.toByteArray(Charsets.UTF_8))
                    zos.write('\n'.code)
                }
                if (lines > 0) {
                    // Tail: newest files first, stop once the quota is filled. Each
                    // file is streamed through a bounded buffer so memory stays O(lines).
                    val result = ArrayDeque<String>()
                    var remaining = lines
                    for (f in files) {
                        if (remaining <= 0) break
                        val fileTail = ArrayDeque<String>()
                        try {
                            f.forEachLine { line ->
                                if (fileTail.size == remaining) fileTail.removeFirst()
                                fileTail.addLast(line)
                            }
                        } catch (e: Exception) {
                            continue
                        }
                        if (fileTail.isEmpty()) continue
                        val take = minOf(remaining, fileTail.size)
                        result.addAll(0, fileTail.takeLast(take))
                        remaining -= take
                    }
                    result.forEach { emit(it) }
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
                    for (f in files.reversed()) {  // oldest -> newest, chronological
                        try {
                            f.forEachLine { line ->
                                if (dayCutoff.isEmpty() || (line.length >= 10 && line.substring(0, 10) >= dayCutoff)) {
                                    emit(line)
                                }
                            }
                        } catch (e: Exception) {
                            // skip unreadable file
                        }
                    }
                }
                zos.closeEntry()
            }
        } catch (e: Exception) {
            android.util.Log.w("ZkqWs", "pack logs error: ${e.message}")
            tmp.delete()
            return null
        }
        if (tmp.length() == 0L) {
            tmp.delete()
            return null
        }
        return tmp
    }

    /** Uploads the log archive to the plugin's /zkq_logfile endpoint. */
    private suspend fun uploadLogfile(zip: File, requestId: Long): String? {
        val base = serverBase() ?: return null
        val deviceId = StatusSnapshot.deviceId()
        val nonce = getOrCreateNonce()
        val url = "$base/zkq_logfile?deviceId=${URLEncoder.encode(deviceId, "UTF-8")}" +
            "&nonce=$nonce&requestId=$requestId"
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_TOKEN.key]?.value.orEmpty()
        return withContext(Dispatchers.IO) {
            try {
                val multipart = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "logs.zip", zip.asRequestBody("application/zip".toMediaTypeOrNull()))
                    .build()
                val requestBuilder = Request.Builder()
                    .url(url)
                    .post(multipart)
                if (token.isNotBlank()) requestBuilder.header("X-Zkq-Token", token)
                val request = requestBuilder.build()
                uploadClient.newCall(request).execute().use { response ->
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
    suspend fun uploadScreenshot(png: ByteArray, requestId: Long): String? {
        val base = serverBase() ?: return null
        val deviceId = StatusSnapshot.deviceId()
        val nonce = getOrCreateNonce()
        val url = "$base/zkq_screenshot?deviceId=${URLEncoder.encode(deviceId, "UTF-8")}" +
            "&nonce=$nonce&requestId=$requestId"
        val token = GlobalVars.configStates[GLOBAL_SETTINGS.ZKQ_TOKEN.key]?.value.orEmpty()
        return withContext(Dispatchers.IO) {
            try {
                val multipart = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "screenshot.png", png.toRequestBody("image/png".toMediaTypeOrNull()))
                    .build()
                val requestBuilder = Request.Builder()
                    .url(url)
                    .post(multipart)
                if (token.isNotBlank()) requestBuilder.header("X-Zkq-Token", token)
                val request = requestBuilder.build()
                uploadClient.newCall(request).execute().use { response ->
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
        // Stream through a bounded deque so memory stays O(lines) even when the
        // retention window spans many large per-day files. Files are sorted
        // newest-first; iterate oldest-first so the deque keeps the newest
        // `lines` lines in chronological order (same result as the old
        // readLines()+takeLast approach, without materialising whole files).
        val kept = ArrayDeque<String>()
        for (f in files.asReversed()) {
            try {
                f.useLines { seq ->
                    for (line in seq) {
                        if (days > 0 && (line.length < 10 || line.substring(0, 10) < dayCutoff)) continue
                        if (lines > 0 && kept.size == lines) kept.removeFirst()
                        kept.addLast(line)
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
        val collected = kept.toList()
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

    private suspend fun getOrCreateNonce(): String {
        val existing = readMemory(NONCE_KEY)
        if (existing.isNotBlank()) return existing
        // Bind the nonce prefix to ANDROID_ID so re-installs keep the same
        // session and never trigger a 409 conflict with AstrBot.
        val androidId = try {
            Settings.Secure.getString(ZKQApplication.appContext.contentResolver, Settings.Secure.ANDROID_ID).orEmpty()
        } catch (e: Exception) {
            ""
        }
        val generated = if (androidId.isNotBlank()) {
            androidId.take(16)
        } else {
            UUID.randomUUID().toString().replace("-", "").substring(0, 16)
        }
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
