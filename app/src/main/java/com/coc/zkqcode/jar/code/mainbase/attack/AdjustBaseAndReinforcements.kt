package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
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

suspend fun adjustBaseAndReinforcements(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CHANGE_BASE.key) || !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_TOURNAMENT.key)) return true

    // Guard: only adjust base and reinforcements once per 3 days (4320 minutes)
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.ADJUST_BASE_AND_REINFORCEMENTS, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 4320)) {
        accountMessage("3天内已调整过阵型和援军，暂不重复执行")
        return true
    }
    val startTime = System.currentTimeMillis()
    val maxDuration = 20_000L
    while (System.currentTimeMillis() - startTime < maxDuration) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(80, 630)
        }
        val setBaseIcon = findMultiColorsUntil(listOf(MyColors.SetBaseIcon, MyColors.SetBaseIcon2, MyColors.SetBaseIcon3), 10)
        if (setBaseIcon != null) {
            TouchActions.tap(560, 160)
        }
        val innerSetBaseIcon = findMultiColors(schema = MyColors.InnerSetBase)
        if (innerSetBaseIcon != null) {
            TouchActions.tap(640, 300)
            TouchActions.tap(930, 300)
            TouchActions.tap(260, 300)
            TouchActions.tap(1170, 500)//Clear reinforcements
            TouchActions.tap(590, 590, delayTime = 1200)//Add backups
            TouchActions.swipe(1110, 560, -1100, 560, delayAfter = 400)
            TouchActions.tap(280, 630, delayTime = 200)//Furnace
            TouchActions.tap(280, 630)
            TouchActions.swipe(85, 560, 2500, 500, delayAfter = 400)
            // Repeatedly find and tap the archer training icon
            repeat(15) {
                findMultiColors(schema = MyColors.TrainArcher)?.let { point ->
                    repeat(10) {
                        TouchActions.tap(point.x, point.y, delayTime = 50)
                    }
                    delayWithMultiplier(200)
                }
                findMultiColors(schema = MyColors.TrainBarbarian)?.let { point ->
                    repeat(10) {
                        TouchActions.tap(point.x, point.y, delayTime = 50)
                    }
                    delayWithMultiplier(200)
                }
            }
            break
        }
        if (findMultiColors(MyColors.TournamentNotAvailable) != null) {
            writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
            break
        }
        val remainingSeconds = (maxDuration - (System.currentTimeMillis() - startTime)) / 1000.0
        accountMessage("调整阵型中，剩余${"%.1f".format(remainingSeconds)}秒后强制结束")
        delayWithMultiplier(100)
    }
    // Record the timestamp so the guard activates for the next 3 days
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}