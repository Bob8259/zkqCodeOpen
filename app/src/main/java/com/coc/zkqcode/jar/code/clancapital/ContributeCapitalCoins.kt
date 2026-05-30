package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun contributeCapitalCoins(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CONTRIBUTE_CAPITAL_COINS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CONTRIBUTE_CAPITAL_COINS, InGameVars.currentAccountNumber
    )
    // Guard: only contribute once per 2 days (2880 minutes)
    if (!checkMemoryFile(storageKey, 2880)) {
        accountMessage("2天内已捐赠过都城币，跳过")
        return true
    }

    accountMessage("准备捐都城币")
    if (!enterClanCapital()) {
        accountMessage("进入都城失败，返回主页面")
        return enterMainScreen()
    }
    delayWithMultiplier(1000)
    if (findMultiColors(MyColors.SufficientCapitalCoin) == null) {
        accountMessage("都城币太少，等币多一点后再捐")
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        return enterMainScreen()
    }
    // Try contributing at the capital overview building list
    TouchActions.tap(570, 50)
    if (tryContributeAtBuildingList()) {
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        return enterMainScreen()
    }

    // Iterate all districts
    for (district in CAPITAL_DISTRICTS) {
        accountMessage("检查区域: ${district.name}")

        // Enter the district
        if (!enterDistrict(district)) continue

        if (findMultiColorsUntil(listOf(MyColors.GemForCapital), 60000) == null) {
            accountMessage("进入子城失败")
            return enterMainScreen()
        }

        // Try contributing at the district building list
        TouchActions.tap(490, 15)
        if (tryContributeAtBuildingList()) {
            writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
            return enterMainScreen()
        }

        // No coin found in this district, dismiss
        TouchActions.tap(90, 620, delayTime = 1000)
    }

    return enterMainScreen()
}

/**
 * Attempt to contribute capital coins at the current building-list view.
 * Taps the purple coin icon if visible, then looks for an upgrade button.
 * If an upgrade is found, taps it and the contribute button.
 *
 * @return true if a contribution was made (caller should exit), false otherwise.
 */
private suspend fun tryContributeAtBuildingList(): Boolean {
    val miniPurpleCoin = findMultiColorsUntil(listOf(MyColors.MiniPurpleCoinIcon), 1000) ?: return false
    TouchActions.tap(miniPurpleCoin.x, miniPurpleCoin.y)
    // Check for upgrade button
    val upgradeButtonPoint = findMultiColorsUntil(
        listOf(MyColors.UpgradeHammer, MyColors.DoubleHammer), 10000
    )
    if (upgradeButtonPoint != null) {
        TouchActions.tap(upgradeButtonPoint.x, upgradeButtonPoint.y, delayTime = 1000)
        TouchActions.tap(950, 640) // contribute
        return true
    }
    clickRightBottom(1)
    return false
}

/**
 * Tap into a specific capital district.
 * Goblin Mines (哥布矿井) uses a special tap coordinate due to its position at the bottom of the map.
 */
private suspend fun enterDistrict(district: CapitalDistrict): Boolean {
    accountMessage("正在进入区域: ${district.name}")
    if (district.name == "哥布矿井") {
        TouchActions.tap(840, 715)
    } else {
        val centerX = (district.x1 + district.x2) / 2
        val centerY = (district.y1 + district.y2) / 2
        val tapY = minOf(centerY + 80, 719)
        TouchActions.tap(centerX - 80, tapY)
    }
    delayWithMultiplier(800)

    // Check if district is locked using colour schema
    val lockedPoint = findMultiColors(MyColors.CapitalDistrictLocked)
    if (lockedPoint != null) {
        accountMessage("区域 ${district.name} 未解锁，跳过")
        return false
    }

    accountMessage("成功进入区域: ${district.name}")
    delayWithMultiplier(1000)
    return true
}