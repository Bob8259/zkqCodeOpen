package com.coc.zkqcode.jar.code.mainbase.attack

import android.util.Log
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun searchOpponents(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_ATTACK.key)) return true
    var targetGold = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key).toIntOrNull() ?: logAndStop("Gold requirement is not a number")
    var targetElixir = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key).toIntOrNull() ?: logAndStop("Elixir requirement is not a number")
    var targetDarkElixir = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key).toIntOrNull() ?: logAndStop("Dark Elixir requirement is not a number")

    val darkElixirIcon = findMultiColors(schema = MyColors.DarkElixirIcon)
    if (darkElixirIcon == null) {
        ShowMessage("暂未解锁暗黑重油")
        targetDarkElixir = 0
    }
    val isDynamicAdjust = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key)
    val goldPercentage = findMultiColors(schema = MyColors.GoldColor)
    if (goldPercentage != null && goldPercentage.x < 1078) {
        ShowMessage("金币已满，坐标：${goldPercentage.x}, ${goldPercentage.y}")
        targetGold = 0
    }
    val elixirPercentage = findMultiColors(schema = MyColors.ElixirColor)
    if (elixirPercentage != null && elixirPercentage.x < 1078) {
        ShowMessage("圣水已满，坐标：${elixirPercentage.x}, ${elixirPercentage.y}")
        targetElixir = 0
    }
    val darkElixirPercentage = findMultiColors(schema = MyColors.DarkElixirColor)
    if (darkElixirPercentage != null && darkElixirPercentage.x < 1127) {
        ShowMessage("黑油已满，坐标：${darkElixirPercentage.x}, ${darkElixirPercentage.y}")
        targetDarkElixir = 0

    }
    var searchTimes = 0
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
        val insufficientGold = findMultiColors(schema = MyColors.InsufficientGold)
        if (insufficientGold != null) {
            break
        }
        delayWithMultiplier(100)
        val nextOpponent = findMultiColors(schema = MyColors.NextOpponent)
        if (nextOpponent != null) {
            searchTimes++
            val res = recognizeResources(true)

            // 1. Update target resources dynamically if enabled
            if (isDynamicAdjust) {
                // Use local variable to maintain precision and clarity during calculation
                val weight = searchTimes
                if (targetGold > 0) {
                    targetGold = (targetGold * (weight - 1) + res.gold) / weight
                }
                if (targetElixir > 0) {
                    targetElixir = (targetElixir * (weight - 1) + res.elixir) / weight
                }
                if (targetDarkElixir > 0) {
                    targetDarkElixir = (targetDarkElixir * (weight - 1) + res.darkElixir) / weight
                }
            }

            // 2. Display status message (Maintaining original Chinese formatting)
            ShowMessage("搜索次数：$searchTimes\n对手资源：\n${res.gold}金, ${res.elixir}水, ${res.darkElixir}黑\n目标资源：\n${targetGold}金, ${targetElixir}水, ${targetDarkElixir}黑")

            // 3. Consolidated Deployment Logic
            // Combined the redundant 'isDynamicAdjust' branches to reduce code duplication
            val canProceed = !isDynamicAdjust || searchTimes > 1
            if (canProceed) {
                val meetsCriteria = res.gold > targetGold && res.elixir > targetElixir && res.darkElixir > targetDarkElixir

                if (meetsCriteria) {
                    mainBaseDeployTroops()
                    break
                } else {
                    // No changes to original interaction logic or delay
                    TouchActions.tap(nextOpponent.x, nextOpponent.y, delayTime = 1000)
                }
            } else {
                // No changes to original interaction logic or delay
                TouchActions.tap(nextOpponent.x, nextOpponent.y, delayTime = 1000)
            }
        }
        // Calculate remaining time in minutes with 2 decimal places
        val remainingMinutes = (8 * 60 * 1000L - (System.currentTimeMillis() - battleStartTime)) / 60000.0
        ShowMessage("搜索中... ${"%.1f".format(remainingMinutes)}分钟后强制退出")
        delayWithMultiplier(100)
    }
    return enterMainScreen()
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