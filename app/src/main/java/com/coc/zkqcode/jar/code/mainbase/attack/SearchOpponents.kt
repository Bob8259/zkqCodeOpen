package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

// Timeout constants
private const val SEARCH_TIMEOUT_MS = 8 * 60 * 1000L
private const val TUTORIAL_TIMEOUT_MS = 3 * 60 * 1000L

// X-coordinate thresholds for detecting full storage bars on screen
private const val GOLD_FULL_X_THRESHOLD = 1017
private const val ELIXIR_FULL_X_THRESHOLD = 1017
private const val DARK_ELIXIR_FULL_X_THRESHOLD = 1080

suspend fun searchOpponentsAndDeployTroops(): Boolean {

    var targetGold = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.displayName} 必须是数字，请检查配置")
    var targetElixir = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.displayName} 必须是数字，请检查配置")
    var targetDarkElixir = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.displayName} 必须是数字，请检查配置")

    val darkElixirIcon = findMultiColors(schema = MyColors.DarkElixirIcon)
    if (darkElixirIcon == null) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，暂未解锁暗黑重油")
        targetDarkElixir = 0
    }
    val isDynamicAdjust = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key)
    // Pre-compute per-account memory keys for dynamic adjustment
    val goldKey = StorageKeys.withAccountNumber(StorageKeys.DYNAMIC_GOLD, InGamesVars.currentAccountNumber)
    val elixirKey = StorageKeys.withAccountNumber(StorageKeys.DYNAMIC_ELIXIR, InGamesVars.currentAccountNumber)
    val darkElixirKey = StorageKeys.withAccountNumber(StorageKeys.DYNAMIC_DARK_ELIXIR, InGamesVars.currentAccountNumber)

    // Track whether each target was restored from memory (affects running average formula)
    var goldFromMemory = false
    var elixirFromMemory = false
    var darkElixirFromMemory = false

    // If dynamic adjustment is on, try to restore previously saved thresholds from memory.
    // This lets the bot resume from the last session's average instead of restarting from config values.
    if (isDynamicAdjust) {
        // Only overwrite if the storage is not already marked as full (target > 0)
        readMemory(goldKey).toIntOrNull()?.let {
            if (targetGold > 0) {
                targetGold = it; goldFromMemory = true
            }
        }
        readMemory(elixirKey).toIntOrNull()?.let {
            if (targetElixir > 0) {
                targetElixir = it; elixirFromMemory = true
            }
        }
        readMemory(darkElixirKey).toIntOrNull()?.let {
            if (targetDarkElixir > 0) {
                targetDarkElixir = it; darkElixirFromMemory = true
            }
        }
    }
    val goldPercentage = findMultiColors(schema = MyColors.GoldColor)
    if (goldPercentage != null && goldPercentage.x < GOLD_FULL_X_THRESHOLD) {
        targetGold = 0
    }
    val elixirPercentage = findMultiColors(schema = MyColors.ElixirColor)
    if (elixirPercentage != null && elixirPercentage.x < ELIXIR_FULL_X_THRESHOLD) {
        targetElixir = 0
    }
    val darkElixirPercentage = findMultiColors(schema = MyColors.DarkElixirColor)
    if (darkElixirPercentage != null && darkElixirPercentage.x < DARK_ELIXIR_FULL_X_THRESHOLD) {
        targetDarkElixir = 0
    }
    // Combine resource-full status into a single message
    val fullResources = mutableListOf<String>()
    if (targetGold == 0) fullResources.add("金币")
    if (targetElixir == 0) fullResources.add("圣水")
    if (targetDarkElixir == 0) fullResources.add("黑油")
    if (fullResources.isNotEmpty()) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，${fullResources.joinToString("、")}已满")
    }
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key)) {
        if (targetGold == 0 && targetElixir == 0 && targetDarkElixir == 0) return false
    }
    var searchTimes = 0
    var battleStarted = false
    val battleStartTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - battleStartTime < SEARCH_TIMEOUT_MS) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(83, 631, delayTime = 500)
        }
        // Detect attack cooldown screen; abort search so mainBaseAttack() can skip battle logic
        val waitForBattle = findMultiColors(schema = MyColors.WaitForBattle)
        if (waitForBattle != null) {
            ShowMessage("账号${InGamesVars.currentAccountNumber}，进攻需等待冷却")
            if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key))
                return false
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
        if (!checkReconnections()) return false
        val nextOpponent = findMultiColors(schema = MyColors.NextOpponent)
        if (nextOpponent != null) {
            searchTimes++
            delayWithMultiplier(200)
            val res = recognizeResources(true)

            // Cap recognized resource values to their in-game maximums to filter out OCR misreads
            val cappedGold = res.gold.coerceAtMost(2_000_000)
            val cappedElixir = res.elixir.coerceAtMost(2_000_000)
            val cappedDarkElixir = res.darkElixir.coerceAtMost(12_000)

            // Incrementally update target resource thresholds using a running average.
            // When restored from memory, the value already encodes past sessions,
            // so weight it as an existing data point (searchTimes) instead of (searchTimes - 1).
            if (isDynamicAdjust) {
                if (targetGold > 0) {
                    targetGold = if (goldFromMemory) {
                        (targetGold * searchTimes + cappedGold) / (searchTimes + 1)
                    } else {
                        (targetGold * (searchTimes - 1) + cappedGold) / searchTimes
                    }
                }
                if (targetElixir > 0) {
                    targetElixir = if (elixirFromMemory) {
                        (targetElixir * searchTimes + cappedElixir) / (searchTimes + 1)
                    } else {
                        (targetElixir * (searchTimes - 1) + cappedElixir) / searchTimes
                    }
                }
                if (targetDarkElixir > 0) {
                    targetDarkElixir = if (darkElixirFromMemory) {
                        (targetDarkElixir * searchTimes + cappedDarkElixir) / (searchTimes + 1)
                    } else {
                        (targetDarkElixir * (searchTimes - 1) + cappedDarkElixir) / searchTimes
                    }
                }
            }

            ShowMessage("账号${InGamesVars.currentAccountNumber}，搜索次数：$searchTimes\n对手资源：\n${res.gold}金, ${res.elixir}水, ${res.darkElixir}黑\n目标资源：\n${targetGold}金, ${targetElixir}水, ${targetDarkElixir}黑")

            // Skip the first search result when dynamic adjustment is enabled, so the average has at least one data point
            val meetsCriteria = (!isDynamicAdjust || searchTimes > 2) && res.gold >= targetGold && res.elixir >= targetElixir && res.darkElixir >= targetDarkElixir
            if (meetsCriteria) {
                // Persist the final averaged thresholds so the next session can start from this value.
                if (isDynamicAdjust) {
                    writeMemory(goldKey, targetGold.toString())
                    writeMemory(elixirKey, targetElixir.toString())
                    writeMemory(darkElixirKey, targetDarkElixir.toString())
                }

                battleStarted = true
                mainBaseDeployTroops()
                break
            } else {
                TouchActions.tap(nextOpponent.x, nextOpponent.y, delayTime = 2000)
            }
        }
        val remainingMinutes = (SEARCH_TIMEOUT_MS - (System.currentTimeMillis() - battleStartTime)) / 60000.0
        ShowMessage("账号${InGamesVars.currentAccountNumber}，搜索中... ${"%.1f".format(remainingMinutes)}分钟后强制退出")
        delayWithMultiplier(100)
    }
    return battleStarted
}

private suspend fun mainBaseBattleTutorial(): Boolean {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < TUTORIAL_TIMEOUT_MS) {
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