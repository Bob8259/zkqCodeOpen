package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import java.util.Calendar

suspend fun playClanCapital(): Boolean {
    // All clan capital actions are only relevant during raid weekend
    if (!isWithinRaidWeekend()) {
        ShowMessage("当前不在都城突袭时间段（周五16:00~周一16:00），跳过都城对战")
        return true
    }
    if (!clanCapitalTrainTroops()) return false
    if (!clanCapitalAttack()) return false
    return enterMainScreen()
}

/**
 * Check if the current device time falls within the Clan Capital raid weekend:
 * Friday 16:00 (inclusive) ~ Monday 16:00 (exclusive).
 */
private fun isWithinRaidWeekend(): Boolean {
    val cal = Calendar.getInstance()
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    return when (dayOfWeek) {
        Calendar.FRIDAY -> hour >= 16
        Calendar.SATURDAY, Calendar.SUNDAY -> true
        Calendar.MONDAY -> hour < 16
        else -> false
    }
}


suspend fun enterClanCapital(): Boolean {
    zoomSmallMainBase()
    clickRightBottom(1)
    TouchActions.tap(443, 582, delayTime = 800)
    return findMultiColorsUntil(schemas = listOf(MyColors.CapitalCoin), duration = 500) != null
}