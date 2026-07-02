package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

private data class PassRewardTap(val x: Int, val y: Int)

private val passRewardEntryTap = PassRewardTap(214, 652)
private val passRewardLeftTabTap = PassRewardTap(389, 298)
private val passRewardRightTabTap = PassRewardTap(1221, 299)

private const val PASS_REWARD_PAGE_TIMEOUT = 2500
private const val PASS_REWARD_CLAIM_TIMEOUT = 1500
private const val PASS_REWARD_CLAIM_OFFSET_X = 40
private const val PASS_REWARD_CLAIM_OFFSET_Y = 40
private const val PASS_REWARD_CHEST_DONE_TIMEOUT = 80000
private const val PASS_REWARD_SWIPE_START_X = 640
private const val PASS_REWARD_SWIPE_START_Y = 360
private const val PASS_REWARD_SWIPE_END_X = 1240
private const val PASS_REWARD_SWIPE_END_Y = 360

suspend fun collectPassRewards(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_PASS_REWARDS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(StorageKeys.CLAIM_PASS_REWARDS, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("24小时内已检查过令牌奖励，跳过本次领取")
        return true
    }

    accountMessage("准备领取令牌奖励")
    TouchActions.tap(passRewardEntryTap.x, passRewardEntryTap.y, delayTime = 1200)

    val pageMarker = findMultiColorsUntil(listOf(MyColors.PassAwardAd), PASS_REWARD_PAGE_TIMEOUT)
    if (pageMarker == null) {
        accountMessage("未检测到令牌奖励页面，返回主页面")
        return finishPassRewardCheck(storageKey)
    }

    var claimed = tryClaim(passRewardLeftTabTap, "左")
    claimed = tryClaim(passRewardRightTabTap, "右") || claimed

    if (!claimed) {
        accountMessage("左右均未检测到领取按钮，尝试右滑")
        TouchActions.swipe(
            PASS_REWARD_SWIPE_START_X, PASS_REWARD_SWIPE_START_Y,
            PASS_REWARD_SWIPE_END_X, PASS_REWARD_SWIPE_END_Y,
            delayTime = 300, isJitter = false, delayAfter = 800
        )
        val claimButton = findMultiColorsUntil(listOf(MyColors.PassRewardClaimButton), PASS_REWARD_CLAIM_TIMEOUT)
        if (claimButton != null) {
            tapClaim(claimButton.x, claimButton.y)
            handlePassRewardBranches()
            claimed = true
        } else {
            accountMessage("右滑后仍未检测到领取按钮")
        }
    }

    accountMessage("令牌奖励领取流程结束，返回主页面")
    return finishPassRewardCheck(storageKey)
}

private suspend fun tryClaim(tab: PassRewardTap, side: String): Boolean {
    TouchActions.tap(tab.x, tab.y, delayTime = 2000)
    val claimButton = findMultiColorsUntil(listOf(MyColors.PassRewardClaimButton), PASS_REWARD_CLAIM_TIMEOUT) ?: return false
    tapClaim(claimButton.x, claimButton.y)
    handlePassRewardBranches()
    // Wait for PassAwardAd to disappear -> entered chest screen
    val enteredChest = findMultiColorsUntil(listOf(MyColors.PassAwardAd), 2000)
    if (enteredChest != null) {
        accountMessage("令牌奖励${side}侧未进入宝箱界面，跳过砸宝箱")
    } else {
        // In chest screen, tap to break
        repeat(4) {
            TouchActions.tap(639, 598, delayTime = 200)
        }
        delayWithMultiplier(2000)
        // Detect and tap chest continue button
        val continueBtn = findMultiColorsUntil(listOf(MyColors.PassRewardChestContinue), 5000)
        if (continueBtn != null) {
            TouchActions.tap(continueBtn.x, continueBtn.y, delayTime = 1000)
        }
    }
    // Wait for chest animation to finish and return to pass page
    val pageMarker = findMultiColorsUntil(listOf(MyColors.PassAwardAd), PASS_REWARD_CHEST_DONE_TIMEOUT)
    if (pageMarker == null) {
        accountMessage("令牌奖励${side}侧令牌页面未重新出现")
        return false
    }
    return true
}

private suspend fun tapClaim(btnX: Int, btnY: Int) {
    val tapX = btnX + PASS_REWARD_CLAIM_OFFSET_X
    val tapY = btnY + PASS_REWARD_CLAIM_OFFSET_Y
    accountMessage("点击领取按钮，偏移坐标：$tapX, $tapY")
    TouchActions.tap(tapX, tapY, delayTime = 2000)
    // Poll until button disappears, then retry once if it does not
    val start = System.currentTimeMillis()
    while (System.currentTimeMillis() - start < 3000) {
        if (findMultiColors(MyColors.PassRewardClaimButton) == null) return
        delayWithMultiplier(200)
    }
    // Still visible after 3 seconds, retry once
    TouchActions.tap(tapX, tapY, delayTime = 2000)
}

private suspend fun handlePassRewardBranches() {
    findMultiColors(MyColors.PassRewardChoiceButton)?.let { choiceButton ->
        accountMessage("检测到令牌奖励抉择，尝试点击")
        TouchActions.tap(choiceButton.x, choiceButton.y, delayTime = 1000)
    }

    findMultiColors(MyColors.PassRewardSellGemsButton)?.let { sellGemsButton ->
        accountMessage("检测到令牌奖励变卖宝石，尝试点击")
        TouchActions.tap(sellGemsButton.x, sellGemsButton.y, delayTime = 1000)
    }
}

private suspend fun finishPassRewardCheck(storageKey: String): Boolean {
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    // Click PassAwardAd to return to main world page
    val pageMarker = findMultiColorsUntil(listOf(MyColors.PassAwardAd), PASS_REWARD_PAGE_TIMEOUT)
    if (pageMarker != null) {
        TouchActions.tap(pageMarker.x, pageMarker.y, delayTime = 1200)
    }
    return true
}
