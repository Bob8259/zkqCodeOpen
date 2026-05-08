package com.coc.zkqcode.jar.code.universal.clangame

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar
import java.util.TimeZone

private data class RewardTap(val x: Int, val y: Int)

private val firstRewardTaps = listOf(
    RewardTap(490, 430),
    RewardTap(610, 340),
    RewardTap(720, 250),
    RewardTap(840, 430),
    RewardTap(950, 330),
    RewardTap(1075, 250),
)

private val finalRewardTap = RewardTap(1070, 425)
private val clanGameEntryTap = RewardTap(349, 137)
private val collectConfirmTap = RewardTap(990, 515)
private val beijingTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Shanghai")

private const val REWARD_WINDOW_START_DAY = 28
private const val REWARD_WINDOW_END_DAY = 4
private const val REWARD_WINDOW_HOUR = 16

suspend fun collectClanGameReward(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key)) return true

    val rewardPeriod = getCurrentRewardPeriod() ?: run {
        accountMessage("当前不在竞赛奖励领取时间，暂不检测")
        return true
    }
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.CLAN_GAME_REWARD_CLAIMED_PERIOD, InGameVars.currentAccountNumber)
    if (readMemory(storageKey) == rewardPeriod) {
        accountMessage("该账号本期竞赛奖励已领取，暂不重复领取")
        return true
    }

    accountMessage("准备领取竞赛奖励")
    zoomSmallMainBase(isForAttack = true)
    TouchActions.tap(clanGameEntryTap.x, clanGameEntryTap.y)
    val clanGameRewards = findMultiColorsUntil(listOf(MyColors.ClanGameRewards), 4000)
    if (clanGameRewards != null) {
        firstRewardTaps.forEach { tapReward(it) }
        TouchActions.tap(collectConfirmTap.x, collectConfirmTap.y, delayTime = 5000)
        tapReward(finalRewardTap)
        TouchActions.tap(collectConfirmTap.x, collectConfirmTap.y)
        writeMemory(storageKey, rewardPeriod)
        accountMessage("本期竞赛奖励领取完成，已记录账号状态")
    }
    return enterMainScreen()
}

private fun getCurrentRewardPeriod(): String? {
    val now = Calendar.getInstance(beijingTimeZone)
    val start = Calendar.getInstance(beijingTimeZone).apply {
        timeInMillis = now.timeInMillis
        set(Calendar.DAY_OF_MONTH, REWARD_WINDOW_START_DAY)
        set(Calendar.HOUR_OF_DAY, REWARD_WINDOW_HOUR)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val end = Calendar.getInstance(beijingTimeZone).apply {
        timeInMillis = start.timeInMillis
        add(Calendar.MONTH, 1)
        set(Calendar.DAY_OF_MONTH, REWARD_WINDOW_END_DAY)
    }

    if (now.before(start)) {
        start.add(Calendar.MONTH, -1)
        end.add(Calendar.MONTH, -1)
    }

    if (now.before(start) || !now.before(end)) return null

    val year = start.get(Calendar.YEAR)
    val month = (start.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    return "$year-$month"
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
