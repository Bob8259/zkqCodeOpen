package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

private data class BadgeEventTap(val x: Int, val y: Int)

private const val BADGE_EVENT_REWARD_INTERVAL_MINUTES = 1440
private const val MAX_BADGE_EVENT_ROUNDS = 20
private const val BADGE_EVENT_CONTINUE_TIMEOUT = 1000
private const val BADGE_EVENT_PAGE_TIMEOUT = 3000
private const val BADGE_EVENT_CLAIM_TIMEOUT = 1000

private val badgeEventEntryTap = BadgeEventTap(315, 663)
private val badgeEventEnterTap = BadgeEventTap(948, 370)
private val badgeEventLeftTabTap = BadgeEventTap(200, 418)
private val badgeEventRightTabTap = BadgeEventTap(1087, 416)

// Event Shop constants for the optional badge-event sub-flow.
private const val EVENT_SHOP_ENTRY_X = 1080
private const val EVENT_SHOP_ENTRY_Y = 236
private const val NEW_EQUIPMENT_X = 487
private const val NEW_EQUIPMENT_Y = 272
private const val EVENT_SHOP_ENTER_TIMEOUT = 5000
private const val EVENT_SHOP_CONFIRM_TIMEOUT = 2000

suspend fun collectBadgeEventRewards(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_BADGE_EVENT_REWARDS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAIM_BADGE_EVENT_REWARDS,
        InGameVars.currentAccountNumber
    )
    if (!checkMemoryFile(storageKey, BADGE_EVENT_REWARD_INTERVAL_MINUTES)) {
        accountMessage("24小时内已检查过奖章活动奖励，跳过本次领取")
        return true
    }

    accountMessage("准备领取奖章活动奖励")
    zoomSmallMainBase(isForAttack = true)
    TouchActions.tap(badgeEventEntryTap.x, badgeEventEntryTap.y, delayTime = 500)
    TouchActions.tap(badgeEventEnterTap.x, badgeEventEnterTap.y, delayTime = 1000)

    val continueButton = findMultiColorsUntil(listOf(MyColors.BadgeEventContinueButton), BADGE_EVENT_CONTINUE_TIMEOUT)
    if (continueButton != null) {
        accountMessage("检测到奖章活动继续按钮，点击继续")
        TouchActions.tap(continueButton.x, continueButton.y, delayTime = 1000)
    }

    val entered = findMultiColorsUntil(listOf(MyColors.BadgeEventPageMarker), BADGE_EVENT_PAGE_TIMEOUT)
    if (entered == null) {
        accountMessage("未检测到奖章活动页面标记，返回主页面")
        return finishBadgeEventRewardCheck(storageKey)
    }

    repeat(MAX_BADGE_EVENT_ROUNDS) {
        val leftClaimed = claimBadgeReward(badgeEventLeftTabTap)
        val rightClaimed = claimBadgeReward(badgeEventRightTabTap)
        if (!leftClaimed && !rightClaimed) {
            accountMessage("未检测到可领取的奖章活动奖励")
            return finishBadgeEventRewardCheck(storageKey)
        }
    }

    accountMessage("奖章活动奖励达到最大轮次保护，返回主页面")
    return finishBadgeEventRewardCheck(storageKey)
}

private suspend fun finishBadgeEventRewardCheck(storageKey: String): Boolean {
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())

    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_EVENT_SHOP_REWARDS.key)) {
        accountMessage("活动领取开关已开启，尝试进入活动商店")
        if (enterEventShop()) {
            val alreadyPurchased = findMultiColors(MyColors.EventShopAlreadyPurchased)
            if (alreadyPurchased != null) {
                accountMessage("新装备已购买过，跳过")
            } else {
                buyEventShopItems()
            }
        } else {
            accountMessage("进入活动商店失败")
        }
    }

    return enterMainScreen()
}

private suspend fun claimBadgeReward(tabTap: BadgeEventTap): Boolean {
    TouchActions.tap(tabTap.x, tabTap.y, delayTime = 500)
    val claimButton = findMultiColorsUntil(listOf(MyColors.BadgeEventClaimReward), BADGE_EVENT_CLAIM_TIMEOUT)
    if (claimButton == null) return false
    TouchActions.tap(claimButton.x, claimButton.y, delayTime = 1000)
    return true
}

private suspend fun enterEventShop(): Boolean {
    TouchActions.tap(EVENT_SHOP_ENTRY_X, EVENT_SHOP_ENTRY_Y, delayTime = 500)

    val pageMarker = findMultiColorsUntil(listOf(MyColors.EventShopPageMarker), EVENT_SHOP_ENTER_TIMEOUT)
    if (pageMarker != null) {
        accountMessage("已进入活动商店")
        return true
    }
    accountMessage("未检测到活动商店页面标记")
    return false
}

private suspend fun buyEventShopItems() {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL.key)) {
        buyNewEquipment()
    }
}

private suspend fun buyNewEquipment() {
    val alreadyPurchased = findMultiColors(MyColors.EventShopAlreadyPurchased)
    if (alreadyPurchased != null) {
        accountMessage("新装备已购买过，跳过")
        return
    }

    accountMessage("检测到未购买新装备，点击购买")
    TouchActions.tap(NEW_EQUIPMENT_X, NEW_EQUIPMENT_Y, delayTime = 500)

    val confirmButton = findMultiColorsUntil(listOf(MyColors.EventShopConfirmBuy), EVENT_SHOP_CONFIRM_TIMEOUT)
    if (confirmButton != null) {
        TouchActions.tap(confirmButton.x, confirmButton.y, delayTime = 500)
        accountMessage("已确认购买新装备")
    } else {
        accountMessage("未检测到确认购买按钮，跳过")
    }
}
