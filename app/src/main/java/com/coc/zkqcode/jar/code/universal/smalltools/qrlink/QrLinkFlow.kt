package com.coc.zkqcode.jar.code.universal.smalltools.qrlink

import android.graphics.Point
import android.graphics.Rect
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.ctrl.StatusSnapshot
import com.coc.zkqcode.jar.code.universal.ctrl.ZkqWsClient
import com.coc.zkqcode.jar.code.universal.smalltools.data.extractGameSave
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killApp
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.launchGameAndHandleDataNumberLogin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.math.abs
import org.json.JSONObject

/** Marks a coroutine as "the QR extract flow" (扫码提取). */
class QrFlowCoroutineContext : CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> = Key
    companion object Key : CoroutineContext.Key<QrFlowCoroutineContext>
}

/**
 * 扫码提取（国服）状态机。App 侧流程独占游戏期间的状态快照，
 * 由 ZkqWsClient 的 qr_status/qr_confirm/qr_cancel 查询读写，
 * 由 QrLinkFlow.run 驱动。
 */
object QrLinkState {
    enum class Phase {
        IDLE, NAVIGATING, AWAITING_LOGIN_CHOICE, WAITING_SCAN, LINKED, AWAITING_CONFIRM, EXTRACTING, AWAITING_NEXT, DONE, FAILED, CANCELLED
    }

    @Volatile
    var phase: Phase = Phase.IDLE

    @Volatile
    var slot: Int = 0

    /** 最近一次上传的截图文件名（插件据此从服务器取图发到对话）。 */
    @Volatile
    var screenshotFilename: String? = null

    @Volatile
    var error: String? = null

    // Set by the qr_confirm / qr_cancel query handlers while the flow awaits user confirmation.
    @Volatile
    internal var confirmRequested: Boolean = false

    @Volatile
    internal var cancelRequested: Boolean = false

    // Set by the qr_choose query handler while the flow awaits the QQ/WeChat login-method choice.
    @Volatile
    internal var loginChoiceRequested: String? = null

    /** 本轮结果说明：对话门提示（插件轮询 AWAITING_NEXT 时带出）展示的文本。 */
    @Volatile
    var lastRoundMessage: String? = null

    // 对话门（AWAITING_NEXT）：由 qr_continue / qr_finish 查询处理设置
    @Volatile
    internal var nextSlotRequested: Int? = null

    @Volatile
    internal var nextLoginRequested: String? = null

    @Volatile
    internal var finishRequested: Boolean = false

    fun isActive(): Boolean = phase in setOf(
        Phase.NAVIGATING, Phase.AWAITING_LOGIN_CHOICE, Phase.WAITING_SCAN, Phase.LINKED, Phase.AWAITING_CONFIRM, Phase.EXTRACTING, Phase.AWAITING_NEXT
    )

    /** 用户是否发过取消指令（EXTRACTING 阶段同样要响应取消，不能继续提取）。 */
    fun isCancelRequested(): Boolean = cancelRequested

    fun reset() {
        phase = Phase.IDLE
        slot = 0
        screenshotFilename = null
        error = null
        confirmRequested = false
        cancelRequested = false
        loginChoiceRequested = null
        lastRoundMessage = null
        nextSlotRequested = null
        nextLoginRequested = null
        finishRequested = false
    }

    /** Status payload for the plugin's qr_status polling. */
    fun toStatusJson(): String = JSONObject().apply {
        put("phase", phase.name)
        put("slot", slot)
        put("screenshot", screenshotFilename ?: JSONObject.NULL)
        put("error", error ?: JSONObject.NULL)
        put("roundMessage", lastRoundMessage ?: JSONObject.NULL)
    }.toString()
}

/**
 * 扫码提取会话（ADR-0003/0004）。每轮：清数据冷启动 → 过登录条款 → 点入口按钮 →
 * 点 QQ/微信登录方式按钮（命令参数指定）→ 截图发码 → 监控二维码界面消失（图色检测）→
 * 进村庄截图确认 → 人工确认后提取存档；轮间对话门（继续/结束）由 QrLinkFlow.run 驱动。
 */
object QrLinkFlow {
    private const val NAVIGATE_TIMEOUT_MS = 90_000L
    private const val SCAN_TIMEOUT_MS = 10 * 60_000L
    private const val CONFIRM_TIMEOUT_MS = 5 * 60_000L
    private const val MONITOR_INTERVAL_MS = 3_000L
    private const val MISS_COUNT_THRESHOLD = 2

    // 二维码界面"静止放弃"检测：连续这么久黑色占比无变化（用户没扫码、界面没动静）
    // 即视为放弃提取，自动退出流程。扫码成功时界面必变（消失/弹窗），
    // 所以"界面没动"与"用户没扫码"等价。需要调时间改这里即可。
    private const val QR_IDLE_ABANDON_MS = 5 * 60_000L
    // 黑色占比变化超过该比例才算"界面在动"（刷新/弹窗/切换），用于重置静止计时
    private const val QR_IDLE_CHANGE_THRESHOLD = 0.02f

    // 登录方式选择界面上 QQ/微信 两个按钮的点击坐标（用户按国服实际界面提供，MuMu 720x1280）。
    // null = 未配置，流程会报错提示而不是误点。
    private val QQ_LOGIN_BUTTON: Point? = Point(1011, 385)
    private val WECHAT_LOGIN_BUTTON: Point? = Point(1096, 384)

    // ── 二维码界面检测（QrLinkScreen 的替代实现）──
    // 国服"链接设备"二维码界面（QQ 授权页 / 微信扫码页）的二维码都位于屏幕中央
    // （两张实际截图校准：QQ (461,180)-(819,522)、微信 (448,202)-(832,562)）。
    // 二维码内容每次刷新/每个账号都不同，图色 schema 无法匹配具体图案，改用
    // 区域像素统计：黑色占比 20%-60% **且** 白色占比 > 40%。
    // 实测：QQ 码 黑34%/白60%，微信码 黑33%/白63%；村庄主界面 黑34% 但白仅 0.6%，
    // 白色占比用于区分"二维码界面"与"村庄/普通界面"（后者白像素极少）。
    private val QR_REGION = Rect(430, 170, 850, 570)
    private const val QR_BLACK_MIN_RATIO = 0.20f
    private const val QR_BLACK_MAX_RATIO = 0.60f
    private const val QR_WHITE_MIN_RATIO = 0.40f

    /**
     * Runs the whole 扫码提取 session. Launched from the qr_start query handler into
     * a [QrFlowCoroutineContext]-marked coroutine; the bot main loop stays frozen
     * (waitForPlay) for the whole session. One round per account; between rounds the
     * flow parks in AWAITING_NEXT and waits for the conversation gate (qr_continue /
     * qr_finish / 3-minute timeout). A session ends only by killing the app process,
     * which the root daemon restarts fresh (ADR-0004).
     *
     * @param login "qq"/"wechat" when the user specified the login method in
     *              the command; null/blank waits for the user to choose it in
     *              the conversation (AWAITING_LOGIN_CHOICE) before tapping.
     */
    suspend fun run(initialSlot: Int, login: String?) {
        if (initialSlot <= 0) {
            fail("槽位必须是正整数")
            return
        }
        log("扫码提取会话开始 slot=$initialSlot login=${login ?: "待对话选择"}（挂起 bot 主循环）")
        InGameVars.qrLinkFlowActiveCount += 1
        QrLinkState.reset()
        var slot = initialSlot
        var loginMethod = login
        try {
            // 会话主循环：每轮提取一个账号，轮间对话门由用户决定继续/结束
            while (true) {
                runOneRound(slot, loginMethod)
                if (QrLinkState.finishRequested) {
                    finishAndRestart("用户结束")
                    return
                }
                // 每轮结束（成功/失败/取消）→ 对话门，主循环继续保持冻结。
                // 本轮已消费的取消/上轮残留的结束标志不带到对话门
                // （否则会立刻误判成"结束会话"）；对话门期间用户再发会重新置位。
                QrLinkState.cancelRequested = false
                QrLinkState.finishRequested = false
                QrLinkState.phase = QrLinkState.Phase.AWAITING_NEXT
                val decision = waitForNextDecision()
                when (decision) {
                    Decision.CONTINUE -> {
                        val nextSlot = QrLinkState.nextSlotRequested
                        val nextLogin = QrLinkState.nextLoginRequested
                        QrLinkState.nextSlotRequested = null
                        QrLinkState.nextLoginRequested = null
                        if (nextSlot == null || nextSlot <= 0) {
                            finishAndRestart("下一轮槽位无效")
                            return
                        }
                        slot = nextSlot
                        loginMethod = nextLogin
                    }
                    else -> {
                        val reason = when (decision) {
                            Decision.FINISH -> "用户结束"
                            Decision.TIMEOUT -> "3 分钟未回复"
                            else -> "已取消"
                        }
                        finishAndRestart(reason)
                        return
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e  // 协程取消必须向上传递，不能误当会话异常去杀进程
        } catch (e: Exception) {
            log("会话异常：${e.message}")
            finishAndRestart("会话异常")
        } finally {
            InGameVars.qrLinkFlowActiveCount = (InGameVars.qrLinkFlowActiveCount - 1).coerceAtLeast(0)
            log("扫码提取会话结束（${QrLinkState.phase.name}）")
            // 回到空闲：释放"会话进行中"状态。正常路径 force-stop 会杀进程（reset 无影响）；
            // 若 force-stop 失败进程存活，必须复位，否则 isActive() 永远为真、下次扫码提取会被拒
            QrLinkState.reset()
        }
    }

    /** 一轮提取（ADR-0003 步骤 1-8）：清数据冷启动 → 二维码 → 扫码 → 村庄确认 → 提取。 */
    private suspend fun runOneRound(slot: Int, login: String?) {
        // 每轮起始重置本轮标记（上一轮残留的确认/取消/登录方式选择不带到下一轮）
        QrLinkState.slot = slot
        QrLinkState.confirmRequested = false
        QrLinkState.cancelRequested = false
        QrLinkState.loginChoiceRequested = null
        QrLinkState.screenshotFilename = null
        QrLinkState.error = null
        QrLinkState.lastRoundMessage = null
        try {
            // 1. 先杀死国际服/国服残留进程，再清国服登录数据 + 冷启动（点完条款即停，不点登录）
            QrLinkState.phase = QrLinkState.Phase.NAVIGATING
            log("第 $slot 轮：先杀死残留进程，清数据并冷启动国服（${GameVersion.CN.packageName}）")
            killApp(GameVersion.GLOBAL.packageName)
            killApp(GameVersion.CN.packageName)
            RunShell.runNoOutput("rm -rf \"/data/data/${GameVersion.CN.packageName}/shared_prefs\"/*", false)
            launchGameAndHandleDataNumberLogin(GameVersion.CN, stopAfterTerms = true)

            // 2. 进二维码界面：入口按钮 →（若未带登录方式，先对话等用户选）→ 点登录按钮 → 二维码界面出现
            if (!navigateToQrScreen(login)) {
                QrLinkState.lastRoundMessage = QrLinkState.error ?: "导航失败"
                return
            }

            // 3. 发码：整屏截图（右上角设备名水印）→ 上传 → 进入等待扫码
            QrLinkState.phase = QrLinkState.Phase.WAITING_SCAN
            log("二维码界面已出现，截屏发码")
            val qrFilename = ZkqWsClient.captureAndUploadWatermarked(requestId = -1)
            if (qrFilename == null) {
                QrLinkState.phase = QrLinkState.Phase.FAILED
                QrLinkState.lastRoundMessage = "二维码截图上传失败"
                return
            }
            QrLinkState.screenshotFilename = qrFilename
            log("二维码截图已上传：$qrFilename，等待用户扫码")
            // 纯推送发图：上传完立刻推帧，插件直接发图，不等轮询
            ZkqWsClient.sendQrImage(qrFilename, "二维码已发送，请扫码")

            // 4. 盯屏：图色检测二维码界面，连续 2 次检测不到 = 链接完成（10 分钟超时）
            if (!monitorQrDisappear()) {
                QrLinkState.lastRoundMessage = QrLinkState.error ?: "等待扫码失败"
                return
            }
            QrLinkState.phase = QrLinkState.Phase.LINKED
            log("检测到二维码界面消失，链接完成")
            // 推送"扫描成功"，让用户立刻知道链接成功（等进村庄后还需人工确认）
            ZkqWsClient.sendQrNotify(QrLinkState.slot, true, "✅ 扫描成功，正在进入村庄…", retry = false)

            // 5. 链接后等进入村庄（复用上号流程的进入检测 + 弹窗处理，与正常上号一致）
            if (!enterMainScreen()) {
                QrLinkState.lastRoundMessage = QrLinkState.error ?: "未进入村庄"
                return
            }
            log("已进入村庄，截屏发送确认")

            // 6. 村庄截图发来人工确认（先清空截图文件名，状态帧里不残留上一张二维码图）
            QrLinkState.screenshotFilename = null
            QrLinkState.phase = QrLinkState.Phase.AWAITING_CONFIRM
            val villageFilename = ZkqWsClient.captureAndUploadWatermarked(requestId = -1)
            if (villageFilename == null) {
                QrLinkState.phase = QrLinkState.Phase.FAILED
                QrLinkState.lastRoundMessage = "村庄截图上传失败"
                return
            }
            QrLinkState.screenshotFilename = villageFilename
            log("村庄截图已上传：$villageFilename，等待对话确认")
            // 纯推送发图：上传完立刻推帧，插件直接发图，不等轮询
            val device = try { StatusSnapshot.deviceId() } catch (e: Exception) { "设备" }
            ZkqWsClient.sendQrImage(
                villageFilename,
                "已进入村庄，请确认是否是这个账号：\n· /zkq 确认提取 $device\n· /zkq 取消提取 $device"
            )

            // 7. 等用户在对话里确认（5 分钟）
            if (!waitForConfirm()) {
                QrLinkState.lastRoundMessage = if (isCancelled()) "已取消" else (QrLinkState.error ?: "等待确认超时")
                return
            }
            log("用户已确认，开始提取存档")

            // 8. 提取到指定槽位（先关游戏再拷贝，避免复制到未刷盘文件；拷贝期间取消会被中止）
            QrLinkState.phase = QrLinkState.Phase.EXTRACTING
            killApp(GameVersion.CN.packageName)
            delay(1000)
            if (isCancelled()) {
                QrLinkState.lastRoundMessage = "已取消"
                return
            }
            if (!extractGameSave(GameVersion.CN, slot.toString())) {
                if (isCancelled()) {
                    QrLinkState.lastRoundMessage = "已取消"
                    return
                }
                QrLinkState.phase = QrLinkState.Phase.FAILED
                QrLinkState.lastRoundMessage = "提取失败（拷贝出错）"
                return
            }
            if (isCancelled()) {
                QrLinkState.lastRoundMessage = "已取消"
                return
            }
            QrLinkState.phase = QrLinkState.Phase.DONE
            QrLinkState.error = null
            QrLinkState.lastRoundMessage = "槽位 $slot 提取成功"
            log("提取成功：槽位 $slot")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log("流程异常：${e.message}")
            QrLinkState.phase = QrLinkState.Phase.FAILED
            QrLinkState.lastRoundMessage = e.message ?: "扫码提取流程异常"
        }
    }

    /** 对话门决策。 */
    private sealed class Decision {
        object CONTINUE : Decision()
        object FINISH : Decision()
        object TIMEOUT : Decision()
        object CANCELLED : Decision()
    }

    private const val NEXT_GATE_TIMEOUT_MS = 3 * 60_000L
    private const val NEXT_GATE_WARN_MS = 2 * 60_000L + 30_000L // 提前 30 秒提醒

    /** 对话门：等 qr_continue（下一轮）/ qr_finish（结束）/ 3 分钟超时 / 取消。 */
    private suspend fun waitForNextDecision(): Decision {
        val startTime = System.currentTimeMillis()
        var warned = false
        while (true) {
            if (isCancelled()) return Decision.CANCELLED
            if (QrLinkState.nextSlotRequested != null) return Decision.CONTINUE
            if (QrLinkState.finishRequested) return Decision.FINISH
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed >= NEXT_GATE_TIMEOUT_MS) return Decision.TIMEOUT
            if (!warned && elapsed >= NEXT_GATE_WARN_MS) {
                warned = true
                log("对话门即将超时，提醒用户")
                ZkqWsClient.sendQrNotify(
                    QrLinkState.slot, true,
                    "⚠️ 30 秒后未回复将自动结束。\n/zkq 继续提取 <设备> <槽位> 或 /zkq 结束提取 <设备>",
                    retry = false
                )
            }
            delay(1000)
        }
    }

    /** 结束会话：先推送结束通知，再杀 App 并立即重新启动（全新进程；守护脚本作为兜底）。 */
    private suspend fun finishAndRestart(reason: String) {
        log("会话结束（$reason），即将重启 App")
        val device = try { StatusSnapshot.deviceId() } catch (e: Exception) { "设备" }
        ZkqWsClient.sendQrNotify(
            QrLinkState.slot, true,
            "📦 设备「$device」提取会话已结束（$reason），即将重启挂机",
            retry = false, sessionEnd = true
        )
        // 留 1.5 秒让结束通知发出，再杀进程并立即拉起。
        // 两命令拼成一条由 root shell 依次执行：App 进程被 force-stop 杀掉后，
        // 存活的是 shell（libsu 独立进程），能继续执行 am start 完成重启。
        delay(1500)
        RunShell.runNoOutput(
            "am force-stop com.coc.zkqcode; sleep 1; am start -n com.coc.zkqcode/.MainActivity >>/dev/null 2>&1",
            checkIsPlaying = false
        )
    }

    /**
     * 点完条款后到二维码界面之间的导航，按阶段顺序执行（用户确认的顺序）：
     * ① 过条款：点掉同意隐私/同意条款，直到界面不再出现它们；
     * ② 点完条款后才开始检测入口按钮（[MyColors.QrLinkEntryButton]）并点击；
     * ③ 命令未带登录方式时等对话选择（qr_choose），再点 QQ/微信 按钮
     *    （坐标 [QQ_LOGIN_BUTTON]/[WECHAT_LOGIN_BUTTON]）；
     * ④ 检测二维码界面（[isQrScreen]）出现。
     * 每阶段之间都留出界面切换时间，避免在界面切换帧上漏检。
     */
    private suspend fun navigateToQrScreen(login: String?): Boolean {
        val startTime = System.currentTimeMillis()
        var entryClicked = false
        var loginClicked = false
        var loginButton: Point? = loginButtonFor(login)
        while (System.currentTimeMillis() - startTime < NAVIGATE_TIMEOUT_MS) {
            if (isCancelled()) return false
            val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
            // ① 过条款：点掉同意隐私/同意条款（点完为止，界面切换后不再出现）
            var entryScreenHandled = false
            findMultiColors(byteBuffer = screenBuffer, schema = MyColors.AgreePrivacy, increment = 1)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 800)
                entryScreenHandled = true
            }
            if (!entryScreenHandled) {
                findMultiColors(byteBuffer = screenBuffer, schema = MyColors.AgreeTerms, increment = 1)?.let {
                    TouchActions.tap(it.x, it.y, delayTime = 200)
                    entryScreenHandled = true
                }
            }
            if (entryScreenHandled) {
                log("已点掉同意隐私/同意条款")
                delay(300)
                continue
            }

            // ④ 二维码界面已出现 → 完成导航（放前面：点完条款后可能直接就是二维码界面）
            if (isQrScreen(screenBuffer)) {
                return true
            }

            // ② 点完条款后：检测入口按钮 → 点击（两种界面样式任一命中；登录方式选择界面出现前）
            if (!entryClicked) {
                val entryPoint = findMultiColors(byteBuffer = screenBuffer, schema = MyColors.QrLinkEntryButton, increment = 1)
                    ?: findMultiColors(byteBuffer = screenBuffer, schema = MyColors.QrLinkEntryButton2, increment = 1)
                if (entryPoint != null) {
                    log("已找到入口按钮（${entryPoint.x},${entryPoint.y}），点击")
                    TouchActions.tap(entryPoint.x, entryPoint.y, delayTime = 1000)
                    entryClicked = true
                    continue
                }
            }

            // ③ 登录方式选择：命令未带参数时停下来等对话选择（qr_choose 设置 loginChoiceRequested）
            if (entryClicked && !loginClicked && loginButton == null) {
                QrLinkState.phase = QrLinkState.Phase.AWAITING_LOGIN_CHOICE
                loginButton = waitForLoginChoice()
                if (loginButton.x < 0) return false  // waitForLoginChoice 内部已 fail
            }
            // ③ 点 QQ/微信 按钮（坐标由用户提供）
            if (entryClicked && !loginClicked && loginButton != null) {
                log("点击登录方式按钮（${loginButton.x},${loginButton.y}）")
                TouchActions.tap(loginButton.x, loginButton.y, delayTime = 1000)
                loginClicked = true
                continue
            }
            delay(300)
        }
        fail("自动导航失败：90 秒内未进入链接设备二维码界面（请确认 QrLinkEntryButton 图色已按国服界面校准）")
        return false
    }

    /** 登录方式代码 -> 登录按钮坐标（未配置返回 null，调用方决定怎么处理）。 */
    private fun loginButtonFor(code: String?): Point? = when (code) {
        "qq" -> QQ_LOGIN_BUTTON
        "wechat" -> WECHAT_LOGIN_BUTTON
        else -> null
    }

    /** 等用户在对话里选择登录方式（qr_choose），5 分钟超时。 */
    private suspend fun waitForLoginChoice(): Point {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < CONFIRM_TIMEOUT_MS) {
            if (isCancelled()) return Point(-1, -1)
            val choice = QrLinkState.loginChoiceRequested
            if (choice != null) {
                return loginButtonFor(choice) ?: run {
                    fail("登录方式按钮坐标未配置（QrLinkFlow.kt 的 QQ_LOGIN_BUTTON/WECHAT_LOGIN_BUTTON TODO）")
                    return Point(-1, -1)
                }
            }
            delay(1000)
        }
        fail("等待选择登录方式超时（5 分钟）")
        return Point(-1, -1)
    }

    /**
     * 盯屏：每 3 秒图色检测"链接设备二维码界面"是否还在。
     * 连续 [MISS_COUNT_THRESHOLD] 次检测不到 = 链接完成（二维码随链接成功而消失）。
     * 二维码界面连续 [QR_IDLE_ABANDON_MS] 无变化（黑色占比不变 = 用户没扫码）=
     * 视为放弃提取，自动退出（避免干等 10 分钟）。
     */
    private suspend fun monitorQrDisappear(): Boolean {
        val startTime = System.currentTimeMillis()
        var missCount = 0
        var everSeen = false
        var lastBlackRatio = -1f
        var idleStartMs = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < SCAN_TIMEOUT_MS) {
            if (isCancelled()) return false
            val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
            val found = isQrScreen(screenBuffer)
            if (found) {
                everSeen = true
                missCount = 0
                // 界面"动"检测：黑色占比变化超过阈值（二维码刷新/弹窗/切换）→ 重置静止计时
                val blackRatio = blackRatioOf(screenBuffer)
                if (lastBlackRatio < 0f || abs(blackRatio - lastBlackRatio) > QR_IDLE_CHANGE_THRESHOLD) {
                    idleStartMs = System.currentTimeMillis()
                }
                lastBlackRatio = blackRatio
                if (System.currentTimeMillis() - idleStartMs >= QR_IDLE_ABANDON_MS) {
                    fail("二维码界面已 ${QR_IDLE_ABANDON_MS / 60_000} 分钟无变化，视为放弃提取")
                    return false
                }
            } else if (everSeen) {
                missCount++
                if (missCount >= MISS_COUNT_THRESHOLD) return true
            }
            delay(MONITOR_INTERVAL_MS)
        }
        fail("等待扫码超时（10 分钟）：未检测到二维码界面消失")
        return false
    }

    /** 二维码区域内黑色像素占比（用于"界面是否在动"的对比）。 */
    private fun blackRatioOf(buffer: ScreenCaptureManager.CaptureResult): Float {
        val region = Rect(
            QR_REGION.left.coerceIn(0, buffer.width),
            QR_REGION.top.coerceIn(0, buffer.height),
            QR_REGION.right.coerceAtMost(buffer.width),
            QR_REGION.bottom.coerceAtMost(buffer.height)
        )
        val area = (region.width() * region.height()).toFloat()
        if (area <= 0f) return 0f
        val (black, _) = countBlackWhitePixels(buffer, region)
        return black / area
    }

    /**
     * 二维码界面检测：统计屏幕中央 [QR_REGION] 区域内黑色/白色像素占比。
     * 判定条件：黑 20%-60% 且 白 > 40%（二维码黑白模块 + 浅色背景的特征组合）。
     * 两种国服界面（QQ 授权页/微信扫码页）共用此区域，二维码内容变化不影响判定；
     * 村庄等普通界面中央即使黑色比例接近，白色占比也极低（<2%），不会误判。
     */
    private fun isQrScreen(buffer: ScreenCaptureManager.CaptureResult): Boolean {
        val region = Rect(
            QR_REGION.left.coerceIn(0, buffer.width),
            QR_REGION.top.coerceIn(0, buffer.height),
            QR_REGION.right.coerceAtMost(buffer.width),
            QR_REGION.bottom.coerceAtMost(buffer.height)
        )
        val area = (region.width() * region.height()).toFloat()
        if (area <= 0f) return false
        val (black, white) = countBlackWhitePixels(buffer, region)
        val blackRatio = black / area
        val whiteRatio = white / area
        return blackRatio in QR_BLACK_MIN_RATIO..QR_BLACK_MAX_RATIO && whiteRatio > QR_WHITE_MIN_RATIO
    }

    /** 统计区域内黑色像素（RGB 均 < 80）与白色像素（RGB 均 > 235）的数量。 */
    private fun countBlackWhitePixels(buffer: ScreenCaptureManager.CaptureResult, region: Rect): Pair<Int, Int> {
        val buf = buffer.buffer
        val pixelStride = buffer.pixelStride
        val rowStride = buffer.rowStride
        var black = 0
        var white = 0
        for (y in region.top until region.bottom) {
            for (x in region.left until region.right) {
                val offset = y * rowStride + x * pixelStride
                val r = buf.get(offset).toInt() and 0xFF
                val g = buf.get(offset + 1).toInt() and 0xFF
                val b = buf.get(offset + 2).toInt() and 0xFF
                if (r < 80 && g < 80 && b < 80) {
                    black++
                } else if (r > 235 && g > 235 && b > 235) {
                    white++
                }
            }
        }
        return black to white
    }

    /** 等用户在对话里确认提取（qr_confirm）或取消（qr_cancel），5 分钟超时。 */
    private suspend fun waitForConfirm(): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < CONFIRM_TIMEOUT_MS) {
            if (isCancelled()) return false
            if (QrLinkState.confirmRequested) return true
            delay(1000)
        }
        fail("等待确认超时（5 分钟）")
        return false
    }

    /**
     * 检查用户是否已取消（qr_cancel 设置了 cancelRequested）。已取消则把状态机
     * 置为 CANCELLED 并返回 true；所有等待循环（导航/监控/进村庄/确认/选登录方式）
     * 都必须周期调用，否则取消标志无人消费、流程会继续跑到超时。
     */
    private fun isCancelled(): Boolean {
        if (QrLinkState.finishRequested) {
            QrLinkState.phase = QrLinkState.Phase.IDLE
            log("收到结束指令，终止当前阶段并退出会话")
            return true
        }
        if (QrLinkState.cancelRequested) {
            QrLinkState.phase = QrLinkState.Phase.CANCELLED
            QrLinkState.error = "用户取消"
            log("收到取消指令，本轮终止（会话继续，进对话门）")
            return true
        }
        return false
    }

    private suspend fun fail(message: String) {
        QrLinkState.error = message
        QrLinkState.phase = QrLinkState.Phase.FAILED
        log("失败：$message")
        // 会话模型下每轮结果由对话门提示带出（roundMessage），不单独推送
    }

    /** 流程日志：logcat（ZkqWs 标签，方便 adb logcat 抓取）+ 悬浮窗提示双通道。 */
    private fun log(message: String) {
        android.util.Log.i("QrLinkFlow", message)
        ShowMessage("扫码提取：$message")
    }
}
