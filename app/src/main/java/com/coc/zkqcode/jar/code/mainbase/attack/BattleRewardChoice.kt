package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
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
private const val HOME_REWARD_CHOICE_CARD_X = 390
private const val HOME_REWARD_CHOICE_CARD_Y = 350

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

internal suspend fun isBattleRewardChoiceVisible(): Boolean {
    if (!isBattleRewardChoiceHandlingEnabled()) return false
    return isRewardChoiceDialogPresent()
}

/**
 * Resolves the resource-choice dialog shown while the app is entering the main village.
 * This dialog blocks the home-page markers, so it must be handled even when restart-after-deploy is enabled.
 */
internal suspend fun resolveHomeRewardChoiceIfVisible(): Boolean {
    if (findMultiColors(schema = MyColors.MainBaseHomeRewardChoiceDialog, increment = 1) == null) {
        return false
    }

    accountMessage("检测到主世界资源三选一，选择左侧奖励")
    var lastTapAt = 0L
    while (findMultiColors(schema = MyColors.MainBaseHomeRewardChoiceDialog, increment = 1) != null) {
        val now = System.currentTimeMillis()
        if (now - lastTapAt >= REWARD_CHOICE_TAP_INTERVAL_MS) {
            TouchActions.tap(HOME_REWARD_CHOICE_CARD_X, HOME_REWARD_CHOICE_CARD_Y, delayTime = 200)
            lastTapAt = now
        }
        delayWithMultiplier(REWARD_CHOICE_POLL_INTERVAL_MS)
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
