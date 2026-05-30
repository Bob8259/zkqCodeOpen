package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun contributeCapitalCoins(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CONTRIBUTE_CAPITAL_COINS.key)) return true
    if (!enterClanCapital()) {
        accountMessage("进入都城失败，返回主页面")
        return enterMainScreen()
    }

    // Iterate all districts
    for (district in CAPITAL_DISTRICTS) {
        accountMessage("检查区域: ${district.name}")

        // Enter the district
        if (!enterDistrict(district)) {
            continue
        }

        // Wait for district page to load
        delayWithMultiplier(1000)
    }

    return enterMainScreen()
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

    // Check if district is locked using color schema
    val lockedPoint = findMultiColors(MyColors.CapitalDistrictLocked)
    if (lockedPoint != null) {
        accountMessage("区域 ${district.name} 未解锁，跳过")
        return false
    }

    accountMessage("成功进入区域: ${district.name}")
    return true
}