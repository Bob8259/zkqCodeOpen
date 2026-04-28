package com.coc.zkqcode.jar.code.universal.clangame

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

private data class RewardTap(val x: Int, val y: Int)

private val firstRewardTaps = listOf(
    RewardTap(490, 430),
    RewardTap(610, 340),
    RewardTap(720, 430),
    RewardTap(840, 430),
    RewardTap(950, 330),
    RewardTap(1075, 250),
)

private val finalRewardTap = RewardTap(1070, 425)
private val clanGameEntryTap = RewardTap(349, 137)
private val collectConfirmTap = RewardTap(990, 515)

suspend fun collectClanGameReward(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key)) return true
    accountMessage("准备领取竞赛奖励")
    zoomSmallMainBase(isForAttack = true)
    TouchActions.tap(clanGameEntryTap.x, clanGameEntryTap.y)
    val clanGameRewards = findMultiColorsUntil(listOf(MyColors.ClanGameRewards), 4000)
    if (clanGameRewards != null) {
        firstRewardTaps.forEach { tapReward(it) }
        TouchActions.tap(collectConfirmTap.x, collectConfirmTap.y, delayTime = 5000)
        tapReward(finalRewardTap)
        TouchActions.tap(collectConfirmTap.x, collectConfirmTap.y)
    }
    return enterMainScreen()
}

private suspend fun tapReward(rewardTap: RewardTap) {
    TouchActions.tap(rewardTap.x, rewardTap.y)
    confirmToConvert()
}

private suspend fun confirmToConvert() {
    findMultiColors(MyColors.MiddleGreenYes)?.let { confirmButton ->
        TouchActions.tap(confirmButton.x, confirmButton.y)
    }
}
