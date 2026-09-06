package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

internal enum class BattleRewardChoiceResult {
    NotVisible,
    Handled
}

private const val REWARD_CHOICE_CARD_X = 386
private const val REWARD_CHOICE_CARD_Y = 380
private const val REWARD_CHOICE_POLL_INTERVAL_MS = 150
private const val REWARD_CHOICE_TAP_INTERVAL_MS = 800
private const val RESOURCE_REWARD_REFERENCE_WIDTH = 1280
private const val RESOURCE_REWARD_REFERENCE_HEIGHT = 720
private const val RESOURCE_REWARD_MIN_MATCH_PIXELS = 120
private const val RESOURCE_REWARD_RED_MIN = 210
private const val RESOURCE_REWARD_RED_GREEN_BLUE_MIN = 74
private const val RESOURCE_REWARD_RED_GREEN_BLUE_MAX = 164
private const val RESOURCE_REWARD_WHITE_MIN = 235
private const val RESOURCE_REWARD_WHITE_MAX_SPREAD = 20

private enum class ResourceRewardDigitState {
    Full,
    Available,
    Unknown
}

private data class ResourceRewardCard(
    val label: String,
    val numberLeft: Int,
    val numberTop: Int,
    val numberRight: Int,
    val numberBottom: Int,
    val tapX: Int,
    val tapY: Int,
    val priority: Int
)

private data class ResourceRewardDigitCounts(
    val red: Int,
    val white: Int
)

private val homeResourceRewardCards = listOf(
    ResourceRewardCard("金币", 300, 405, 445, 430, 390, 350, priority = 2),
    ResourceRewardCard("圣水", 575, 405, 705, 430, 640, 350, priority = 1),
    ResourceRewardCard("黑油", 855, 405, 960, 430, 895, 350, priority = 0)
)

/**
 * Reward choices are only meaningful when the script keeps the battle open.
 * Restart-after-deploy deliberately preserves the existing fast path and never captures this dialog.
 */
internal fun isBattleRewardChoiceHandlingEnabled(): Boolean {
    return !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESTART_GAME.key)
}

private suspend fun isRewardChoiceDialogPresent(): Boolean {
    return findMultiColors(schema = MyColors.BattleRewardChoiceDialog, increment = 1) != null ||
        findMultiColors(schema = MyColors.MainBaseSelectReward, increment = 1) != null
}

private suspend fun isRewardChoiceCardReady(): Boolean {
    return findMultiColors(schema = MyColors.BattleRewardChoiceReady, increment = 1) != null ||
        findMultiColors(schema = MyColors.MainBaseSelectReward, increment = 1) != null
}

private fun scaleResourceRewardX(value: Int, frame: ScreenCaptureManager.CaptureResult): Int {
    return (value.toLong() * frame.width / RESOURCE_REWARD_REFERENCE_WIDTH).toInt()
}

private fun scaleResourceRewardY(value: Int, frame: ScreenCaptureManager.CaptureResult): Int {
    return (value.toLong() * frame.height / RESOURCE_REWARD_REFERENCE_HEIGHT).toInt()
}

private fun countResourceRewardDigitColors(
    frame: ScreenCaptureManager.CaptureResult,
    card: ResourceRewardCard
): ResourceRewardDigitCounts {
    if (frame.width <= 0 || frame.height <= 0 || frame.pixelStride < 4 || frame.rowStride <= 0) {
        return ResourceRewardDigitCounts(red = 0, white = 0)
    }

    val left = scaleResourceRewardX(card.numberLeft, frame).coerceIn(0, frame.width - 1)
    val right = scaleResourceRewardX(card.numberRight, frame).coerceIn(0, frame.width - 1)
    val top = scaleResourceRewardY(card.numberTop, frame).coerceIn(0, frame.height - 1)
    val bottom = scaleResourceRewardY(card.numberBottom, frame).coerceIn(0, frame.height - 1)
    val buffer = frame.buffer
    var red = 0
    var white = 0

    for (y in top..bottom) {
        val rowStart = y * frame.rowStride
        for (x in left..right) {
            val offset = rowStart + x * frame.pixelStride
            if (offset + 2 >= buffer.limit()) continue

            val r = buffer.get(offset).toInt() and 0xFF
            val g = buffer.get(offset + 1).toInt() and 0xFF
            val b = buffer.get(offset + 2).toInt() and 0xFF
            if (r >= RESOURCE_REWARD_RED_MIN &&
                g in RESOURCE_REWARD_RED_GREEN_BLUE_MIN..RESOURCE_REWARD_RED_GREEN_BLUE_MAX &&
                b in RESOURCE_REWARD_RED_GREEN_BLUE_MIN..RESOURCE_REWARD_RED_GREEN_BLUE_MAX
            ) {
                red++
            } else if (r >= RESOURCE_REWARD_WHITE_MIN &&
                g >= RESOURCE_REWARD_WHITE_MIN &&
                b >= RESOURCE_REWARD_WHITE_MIN &&
                maxOf(r, g, b) - minOf(r, g, b) <= RESOURCE_REWARD_WHITE_MAX_SPREAD
            ) {
                white++
            }
        }
    }
    return ResourceRewardDigitCounts(red = red, white = white)
}

private fun classifyResourceRewardDigit(
    frame: ScreenCaptureManager.CaptureResult,
    card: ResourceRewardCard
): ResourceRewardDigitState {
    val counts = countResourceRewardDigitColors(frame, card)
    return when {
        counts.white >= RESOURCE_REWARD_MIN_MATCH_PIXELS && counts.white > counts.red * 2 -> ResourceRewardDigitState.Available
        counts.red >= RESOURCE_REWARD_MIN_MATCH_PIXELS && counts.red > counts.white * 2 -> ResourceRewardDigitState.Full
        else -> ResourceRewardDigitState.Unknown
    }
}

private fun resourceRewardStateLabel(state: ResourceRewardDigitState): String {
    return when (state) {
        ResourceRewardDigitState.Full -> "满"
        ResourceRewardDigitState.Available -> "未满"
        ResourceRewardDigitState.Unknown -> "识别中"
    }
}

private fun chooseHomeResourceReward(
    frame: ScreenCaptureManager.CaptureResult
): Pair<ResourceRewardCard, List<Pair<ResourceRewardCard, ResourceRewardDigitState>>>? {
    val states = homeResourceRewardCards.map { card -> card to classifyResourceRewardDigit(frame, card) }
    val available = states.filter { it.second == ResourceRewardDigitState.Available }
    if (available.isNotEmpty()) {
        return available.minBy { it.first.priority }.first to states
    }
    if (states.all { it.second == ResourceRewardDigitState.Full }) {
        return homeResourceRewardCards.first { it.label == "金币" } to states
    }
    return null
}

private fun resourceRewardStateSummary(states: List<Pair<ResourceRewardCard, ResourceRewardDigitState>>): String {
    return states.joinToString("，") { (card, state) -> "${card.label}${resourceRewardStateLabel(state)}" }
}

private suspend fun isHomeResourceRewardDialogPresent(frame: ScreenCaptureManager.CaptureResult): Boolean {
    return findMultiColors(
        schema = MyColors.MainBaseHomeRewardChoiceDialog,
        byteBuffer = frame,
        increment = 1
    ) != null
}

internal suspend fun isBattleRewardChoiceVisible(): Boolean {
    if (!isBattleRewardChoiceHandlingEnabled()) return false
    return isRewardChoiceDialogPresent()
}

/**
 * Resolves the resource-choice dialog shown while the app is entering the main village.
 * This dialog blocks the home-page markers, so it must be handled even when restart-after-deploy is enabled.
 */
internal suspend fun resolveHomeRewardChoiceIfVisible(): Boolean {
    var frame = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    if (!isHomeResourceRewardDialogPresent(frame)) return false

    accountMessage("检测到主世界资源三选一，按资源未满状态选择奖励")
    var lastTapAt = 0L
    var lastTarget: ResourceRewardCard? = null
    while (isHomeResourceRewardDialogPresent(frame)) {
        val selection = chooseHomeResourceReward(frame)
        if (selection != null) {
            val (target, states) = selection
            val now = System.currentTimeMillis()
            if (target != lastTarget) {
                val allFull = states.all { it.second == ResourceRewardDigitState.Full }
                val decision = if (allFull) {
                    "三种资源均满，选择${target.label}"
                } else {
                    "${resourceRewardStateSummary(states)}，选择${target.label}"
                }
                accountMessage("主世界资源奖励状态：$decision")
                lastTarget = target
            }
            if (now - lastTapAt >= REWARD_CHOICE_TAP_INTERVAL_MS) {
                TouchActions.tap(
                    scaleResourceRewardX(target.tapX, frame),
                    scaleResourceRewardY(target.tapY, frame),
                    delayTime = 200
                )
                lastTapAt = now
            }
        }
        delayWithMultiplier(REWARD_CHOICE_POLL_INTERVAL_MS)
        frame = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    }

    accountMessage("主世界资源奖励已选择，等待主界面刷新")
    return true
}


/**
 * Picks the left reward card from the 1280x720 three-choice dialog and waits until the dialog closes.
 */
internal suspend fun resolveBattleRewardChoiceIfVisible(
    continueDeployment: Boolean = true,
    appearanceWaitMs: Int = 0
): BattleRewardChoiceResult {
    if (!isBattleRewardChoiceHandlingEnabled()) return BattleRewardChoiceResult.NotVisible

    var appearanceWaitedMs = 0
    while (!isRewardChoiceDialogPresent()) {
        if (appearanceWaitedMs >= appearanceWaitMs) return BattleRewardChoiceResult.NotVisible
        delayWithMultiplier(REWARD_CHOICE_POLL_INTERVAL_MS)
        appearanceWaitedMs += REWARD_CHOICE_POLL_INTERVAL_MS
    }

    accountMessage("检测到对战奖励三选一，选择左侧奖励")
    var lastTapAt = 0L
    while (isRewardChoiceDialogPresent()) {
        val now = System.currentTimeMillis()
        if (isRewardChoiceCardReady() && now - lastTapAt >= REWARD_CHOICE_TAP_INTERVAL_MS) {
            TouchActions.tap(REWARD_CHOICE_CARD_X, REWARD_CHOICE_CARD_Y, delayTime = 200)
            lastTapAt = now
        }
        delayWithMultiplier(REWARD_CHOICE_POLL_INTERVAL_MS)
    }

    accountMessage(if (continueDeployment) "对战奖励已选择，继续下兵" else "对战奖励已选择，准备退出对战")
    return BattleRewardChoiceResult.Handled
}
