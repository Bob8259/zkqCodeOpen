package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar

suspend fun playClanCapital(): Boolean {
    if (!collectCapitalCoins()) return false
    if (!performClanCapitalRaid()) return false
    return true
}

/**
 * Execute the Clan Capital raid loop: validate prerequisites, train troops,
 * check cooldown, and attack up to 6 times.
 */
suspend fun performClanCapitalRaid(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_RAID.key) || InGameVars.townHallLevel < 3) return true
    // All clan capital actions are only relevant during raid weekend
    if (!isWithinRaidWeekend()) {
        accountMessage("当前不在都城突袭时间段，跳过都城对战")
        return true
    }
    if (!clanCapitalTrainTroops()) {
        accountMessage("都城练兵失败，尝试返回主世界")
        return enterMainScreen()
    }
    // Guard: skip if the last attack was less than 3 days (4320 minutes) ago
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAN_CAPITAL_ATTACK, InGameVars.currentAccountNumber
    )
    if (!checkMemoryFile(storageKey, 4320)) {
        return enterMainScreen()
    }
    for (i in 0 until 6) {
        if (!clanCapitalAttack()) break
    }
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
    if (findMultiColors(MyColors.CapitalCoin) != null) return true
    zoomSmallMainBase()
    clickRightBottom(1)
    TouchActions.tap(495, 560, delayTime = 10)
    TouchActions.tap(540, 560, delayTime = 10)
    TouchActions.tap(445, 580, delayTime = 800)
    val nextRaid = findMultiColors(MyColors.NextRaid)
    if (nextRaid != null) {
        TouchActions.tap(nextRaid.x, nextRaid.y)
    }
    return findMultiColorsUntil(schemas = listOf(MyColors.CapitalCoin), duration = 500) != null
}