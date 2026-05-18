package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

suspend fun startClanWar(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_WAR.key)) return true
    val trainTroopButton = findMultiColorsUntil(listOf(MyColors.TrainTroops, MyColors.TrainTroops2), 100)
    if (trainTroopButton != null) {
        TouchActions.tap(60, 420)
    } else return enterMainScreen()
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 15_000L) {
        val startClanWar = findMultiColors(MyColors.StartClanWar)
        if (startClanWar != null) {
            TouchActions.tap(startClanWar.x, startClanWar.y)
        }
        val innerStartClanWar = findMultiColors(MyColors.InnerStartClanWar)
        if (innerStartClanWar != null) {
            selectClanWarParticipant()
            return enterMainScreen()
        }
        delay(100)
    }
    return enterMainScreen()
}

suspend fun selectClanWarParticipant() {
    when (getConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_WAR_METHOD.key)) {
        "0" -> ShowMessage("111")
        "1" -> ShowMessage("222")
        else -> ShowMessage("Invalid Start Clan War method configuration")
    }
}