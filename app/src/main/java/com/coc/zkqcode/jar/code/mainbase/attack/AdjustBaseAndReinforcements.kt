package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun adjustBaseAndReinforcements(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CHANGE_BASE.key) || !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LADDER.key)) return true
    val startTime = System.currentTimeMillis()
    val maxDuration = 20_000L
    while (System.currentTimeMillis() - startTime < maxDuration) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(80, 630)
        }
        val setBaseIcon = findMultiColorsUntil(listOf(MyColors.SetBaseIcon, MyColors.SetBaseIcon2), 10)
        if (setBaseIcon != null) {
            TouchActions.tap(560, 160)
        }
        val innerSetBaseIcon = findMultiColors(schema = MyColors.InnerSetBase)
        if (innerSetBaseIcon != null) {
            TouchActions.tap(640, 300)
            TouchActions.tap(1170, 500)//Clear reinforcements
            TouchActions.tap(590, 590, delayTime = 1200)//Add backups
            TouchActions.swipe(1210, 560, -1000, 560, delayAfter = 400)
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
        val remainingSeconds = (maxDuration - (System.currentTimeMillis() - startTime)) / 1000.0
        accountMessage("调整阵型中，剩余${"%.1f".format(remainingSeconds)}秒后强制结束")
        delayWithMultiplier(100)
    }
    return enterMainScreen()
}