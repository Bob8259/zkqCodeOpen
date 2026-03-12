package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.core.util.basic.delayWithMultiplier

suspend fun searchOpponents(): Boolean {
    // Loop for up to 8 minutes to find and tap the battle icon
    val battleStartTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - battleStartTime < 8 * 60 * 1000L) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(83, 631, delayTime = 500)
        }
        val villagerSpeaking = findMultiColors(schema = MyColors.SpeakingVillager)
        val setBaseIcon = findMultiColors(schema = MyColors.SetBaseIcon)
        if (villagerSpeaking != null || setBaseIcon != null) {
            if (!mainBaseBattleTutorial()) return false
        }
        val searchOpponents = findMultiColors(schema = MyColors.SearchOpponents)
        if (searchOpponents != null) {
            TouchActions.tap(searchOpponents.x, searchOpponents.y, delayTime = 500)
        }
        val attackButton = findMultiColors(schema = MyColors.AttackButton)
        if (attackButton != null) {
            TouchActions.tap(attackButton.x, attackButton.y, delayTime = 500)
        }
        delayWithMultiplier(100)
    }
    return true
}

private suspend fun mainBaseBattleTutorial(): Boolean {
    // Loop for up to 3 minutes to handle the battle tutorial
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 3 * 60 * 1000L) {
        val villagerSpeaking = findMultiColors(schema = MyColors.SpeakingVillager)
        if (villagerSpeaking != null) {
            TouchActions.tap(villagerSpeaking.x, villagerSpeaking.y, delayTime = 500)
        }
        val setBaseIcon = findMultiColors(schema = MyColors.SetBaseIcon)
        if (setBaseIcon != null) {
            TouchActions.tap(557, 159, delayTime = 500)
        }
        val innerSetBaseIcon = findMultiColors(schema = MyColors.InnerSetBase)
        if (innerSetBaseIcon != null) {
            TouchActions.tap(590, 293, delayTime = 500)
            TouchActions.tap(844, 290, delayTime = 500)
            TouchActions.tap(1085, 283, delayTime = 500)//Try three bases.
            delayWithMultiplier(200)
            TouchActions.tap(593, 591, delayTime = 1200)//Add backups
            TouchActions.swipe(1210, 562, -1000, 559)
            delayWithMultiplier(400)
            TouchActions.tap(150, 494, delayTime = 200)//Furnace
            TouchActions.tap(150, 494, delayTime = 500)
            TouchActions.swipe(86, 562, 2500, 500)
            delayWithMultiplier(400)
            // Repeatedly find and tap the archer training icon
            repeat(15) {
                findMultiColors(schema = MyColors.TrainArcher)?.let { point ->
                    repeat(10) {
                        TouchActions.tap(point.x, point.y)
                    }
                    delayWithMultiplier(200)
                }
            }
            break
        }
        delayWithMultiplier(100)
    }
    return enterMainScreen()
}