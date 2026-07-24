package com.coc.zkqcode.jar.code.universal.clangame

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar
import java.util.TimeZone

private data class RewardTap(val x: Int, val y: Int)

// Each of the six reward cards has three vertically aligned buttons.
private val rewardCardPositions = listOf(
    listOf(RewardTap(490, 250), RewardTap(490, 340), RewardTap(490, 430)),
    listOf(RewardTap(610, 250), RewardTap(610, 340), RewardTap(610, 430)),
    listOf(RewardTap(720, 250), RewardTap(720, 340), RewardTap(720, 430)),
    listOf(RewardTap(840, 250), RewardTap(840, 340), RewardTap(840, 430)),
    listOf(RewardTap(950, 250), RewardTap(950, 340), RewardTap(950, 430)),
    listOf(RewardTap(1075, 250), RewardTap(1075, 340), RewardTap(1075, 430)),
)

private val clanGameEntryTap = RewardTap(349, 137)
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
    TouchActions.tap(clanGameEntryTap.x, clanGameEntryTap.y, delayTime = 1500)
    accountMessage("已点击竞赛入口，等待检测奖励页面...")
    val clanGameRewards = findMultiColorsUntil(listOf(MyColors.ClanGameRewards), 4000)
    accountMessage("竞赛奖励页面检测结果: ${if (clanGameRewards != null) "已找到(${clanGameRewards.x},${clanGameRewards.y})" else "未找到"}")
    if (clanGameRewards != null) {
        val lastEligibleIndex = detectLastEligibleCard()
        accountMessage("检测达标情况，最后一个达标卡片索引: $lastEligibleIndex")

        if (lastEligibleIndex < 0) {
            accountMessage("未检测到达标卡片，跳过领取")
        } else {
            val configKeys = listOf(
                Schema.MAIN_BASE_SETTINGS.CLAIM_CG_REWARD_1.key,
                Schema.MAIN_BASE_SETTINGS.CLAIM_CG_REWARD_2.key,
                Schema.MAIN_BASE_SETTINGS.CLAIM_CG_REWARD_3.key,
                Schema.MAIN_BASE_SETTINGS.CLAIM_CG_REWARD_4.key,
                Schema.MAIN_BASE_SETTINGS.CLAIM_CG_REWARD_5.key,
                Schema.MAIN_BASE_SETTINGS.CLAIM_CG_REWARD_6.key,
            )
            configKeys.forEachIndexed { cardIndex, configKey ->
                if (cardIndex > lastEligibleIndex) {
                    accountMessage("卡片${cardIndex + 1}未达标，跳过领取")
                    return@forEachIndexed
                }
                val buttonIndex = getConfigRuntime(configKey).toIntOrNull() ?: 0
                if (buttonIndex in 1..3) {
                    val position = rewardCardPositions[cardIndex][buttonIndex - 1]
                    accountMessage("领取卡片${cardIndex + 1} · 按钮$buttonIndex (${position.x}, ${position.y})")
                    tapReward(position)
                }
            }
            TouchActions.tap(990, 515, delayTime = 5000)
            val extraButtonIndex =
                getConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_CG_FINAL_REWARD.key).toIntOrNull() ?: 0
            if (extraButtonIndex in 1..3) {
                val position = rewardCardPositions[lastEligibleIndex][extraButtonIndex - 1]
                accountMessage(
                    "领取额外奖励 · 映射到卡片${lastEligibleIndex + 1} · 按钮$extraButtonIndex " +
                        "(${position.x}, ${position.y})"
                )
                tapReward(position)
            }
            TouchActions.tap(990, 515)
            writeMemory(storageKey, rewardPeriod)
            accountMessage("本期竞赛奖励领取完成，已记录账号状态")
        }
    }
    return enterMainScreen()
}

// The first visually unclaimed card marks the end of the eligible reward range.
private suspend fun detectLastEligibleCard(): Int {
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: run {
            accountMessage("截图失败，无法检测达标情况")
            return -1
        }
    val unclaimedSchemas = listOf(
        MyColors.UnclaimedReward1,
        MyColors.UnclaimedReward2,
        MyColors.UnclaimedReward3,
        MyColors.UnclaimedReward4,
        MyColors.UnclaimedReward5,
        MyColors.UnclaimedReward6,
    )
    for (index in unclaimedSchemas.indices) {
        if (findMultiColors(unclaimedSchemas[index], byteBuffer = screenBuffer) != null) {
            return index - 1
        }
    }
    return 5
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
