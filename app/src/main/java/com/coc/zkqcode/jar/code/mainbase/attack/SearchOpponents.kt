package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.core.util.basic.delayWithMultiplier

suspend fun searchOpponents() {
    // Loop for up to 8 minutes to find and tap the battle icon
    val battleStartTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - battleStartTime < 8 * 60 * 1000L) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(83, 631, delayTime = 500)
        }
        val villagerSpeaking = findMultiColors(schema = MyColors.SpeakingVillager)
        if (villagerSpeaking != null) {
            mainBaseBattleTutorial()
        }
        delayWithMultiplier(100)
    }
}

private suspend fun mainBaseBattleTutorial() {
    // Loop for up to 3 minutes to handle the battle tutorial
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 3 * 60 * 1000L) {
        val villagerSpeaking = findMultiColors(schema = MyColors.SpeakingVillager)
        if (villagerSpeaking != null) {
            TouchActions.tap(villagerSpeaking.x, villagerSpeaking.y, delayTime = 500)
        }
        val setBaseIcon = findMultiColors(schema = MyColors.SetBaseIcon)
        if (setBaseIcon!= null) {
            TouchActions.tap(setBaseIcon.x, setBaseIcon.y, delayTime = 500)
        }
        delayWithMultiplier(100)
    }
}