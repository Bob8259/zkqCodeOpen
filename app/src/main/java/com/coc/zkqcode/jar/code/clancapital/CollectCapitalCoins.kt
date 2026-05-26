package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun collectCapitalCoins(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.COLLECT_CAPITAL_COINS.key) || InGameVars.townHallLevel < 3) return true
    zoomSmallMainBase()
    TouchActions.tap(560, 620)
    val collectCapitalCoins = findMultiColorsUntil(listOf(MyColors.CollectCapitalCoins), 1000)
    if (collectCapitalCoins != null) {
        TouchActions.tap(collectCapitalCoins.x, collectCapitalCoins.y)
    }
    clickRightBottom(1)
    return enterMainScreen()
}