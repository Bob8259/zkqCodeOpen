package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions

suspend fun playClanCapital(): Boolean {
    if (!collectCapitalCoins()) return false
    if (!contributeCapitalCoins()) return false
    if (!startRaid()) return false
    if (!performClanCapitalRaid()) return false
    return true
}


suspend fun enterClanCapital(): Boolean {
    if (findMultiColors(MyColors.CapitalCoin) != null || findMultiColors(MyColors.CapitalCoin2) != null) return true
    zoomSmallMainBase()
    clickRightBottom(1)
    TouchActions.tap(495, 560, delayTime = 10)
    TouchActions.tap(440, 560, delayTime = 10)
    TouchActions.tap(445, 580, delayTime = 800)
    val nextRaid = findMultiColors(MyColors.NextRaid)
    if (nextRaid != null) {
        TouchActions.tap(nextRaid.x, nextRaid.y)
    }
    if (findMultiColorsUntil(schemas = listOf(MyColors.CapitalCoin), duration = 500) != null) return true

    accountMessage("进入都城失败")
    delayWithMultiplier(1000)
    return false
}