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
    Handled,
    Failed
}

private const val REWARD_CHOICE_CARD_X = 386
private const val REWARD_CHOICE_CARD_Y = 380
private const val REWARD_CHOICE_READY_ATTEMPTS = 12
private const val REWARD_CHOICE_READY_INTERVAL_MS = 150
private const val REWARD_CHOICE_DISMISS_ATTEMPTS = 20
private const val REWARD_CHOICE_DISMISS_INTERVAL_MS = 250

/**
 * Reward choices are only meaningful when the script keeps the battle open.
 * Restart-after-deploy deliberately preserves the existing fast path and never captures this dialog.
 */
internal fun isBattleRewardChoiceHandlingEnabled(): Boolean {
    return !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESTART_GAME.key)
}

internal suspend fun isBattleRewardChoiceVisible(): Boolean {
    if (!isBattleRewardChoiceHandlingEnabled()) return false
    return findMultiColors(schema = MyColors.BattleRewardChoiceDialog, increment = 1) != null
}

/**
 * Picks the left reward card from the 1280x720 three-choice dialog and waits until the dialog closes.
 */
internal suspend fun resolveBattleRewardChoiceIfVisible(continueDeployment: Boolean = true): BattleRewardChoiceResult {
    if (!isBattleRewardChoiceHandlingEnabled()) return BattleRewardChoiceResult.NotVisible
    if (findMultiColors(schema = MyColors.BattleRewardChoiceDialog, increment = 1) == null) {
        return BattleRewardChoiceResult.NotVisible
    }

    repeat(REWARD_CHOICE_READY_ATTEMPTS) {
        if (findMultiColors(schema = MyColors.BattleRewardChoiceReady, increment = 1) != null) {
            accountMessage("检测到对战奖励三选一，选择左侧奖励")
            TouchActions.tap(REWARD_CHOICE_CARD_X, REWARD_CHOICE_CARD_Y, delayTime = 200)

            repeat(REWARD_CHOICE_DISMISS_ATTEMPTS) {
                delayWithMultiplier(REWARD_CHOICE_DISMISS_INTERVAL_MS)
                if (findMultiColors(schema = MyColors.BattleRewardChoiceDialog, increment = 1) == null) {
                    accountMessage(if (continueDeployment) "对战奖励已选择，继续下兵" else "对战奖励已选择，准备退出对战")
                    return BattleRewardChoiceResult.Handled
                }
            }

            accountMessage("对战奖励弹窗未关闭，终止本局下兵")
            return BattleRewardChoiceResult.Failed
        }

        delayWithMultiplier(REWARD_CHOICE_READY_INTERVAL_MS)
        if (findMultiColors(schema = MyColors.BattleRewardChoiceDialog, increment = 1) == null) {
            return BattleRewardChoiceResult.NotVisible
        }
    }

    accountMessage("对战奖励弹窗未就绪，终止本局下兵")
    return BattleRewardChoiceResult.Failed
}
