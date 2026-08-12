package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.data.CLAN_GAME_TASK_BASE_MAIN_WORLD
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.readClanGameAcceptedTaskBase
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.checkReconnections
import com.coc.zkqcode.jar.ui.schema.Schema

// Timeout constants
private const val SEARCH_TIMEOUT_MS = 8 * 60 * 1000L
private const val TUTORIAL_TIMEOUT_MS = 3 * 60 * 1000L
private const val AUTO_BATTLE_DETECT_MS = 3000L

// X-coordinate thresholds for detecting full storage bars on screen
private const val GOLD_FULL_X_THRESHOLD = 1017
private const val ELIXIR_FULL_X_THRESHOLD = 1017
private const val DARK_ELIXIR_FULL_X_THRESHOLD = 1080

suspend fun searchOpponentsAndDeployTroops(battleNumber: Int = 1, battleTimes: Int = 1): Boolean {

    var targetGold = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.displayName} 必须是数字，请检查配置")
    var targetElixir = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.displayName} 必须是数字，请检查配置")
    var targetDarkElixir = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.displayName} 必须是数字，请检查配置")

    val darkElixirIcon = findMultiColors(schema = MyColors.DarkElixirIcon)
    if (darkElixirIcon == null) {
        accountMessage("暂未解锁暗黑重油")
        targetDarkElixir = 0
    }
    val isDynamicAdjust = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key)
    // Pre-compute per-account memory keys for dynamic adjustment
    val goldKey = StorageKeys.withAccountNumber(StorageKeys.DYNAMIC_GOLD, InGameVars.currentAccountNumber)
    val elixirKey = StorageKeys.withAccountNumber(StorageKeys.DYNAMIC_ELIXIR, InGameVars.currentAccountNumber)
    val darkElixirKey = StorageKeys.withAccountNumber(StorageKeys.DYNAMIC_DARK_ELIXIR, InGameVars.currentAccountNumber)

    // Track whether each target was restored from memory (affects running average formula)
    var goldFromMemory = false
    var elixirFromMemory = false
    var darkElixirFromMemory = false

    // If dynamic adjustment is on, try to restore previously saved thresholds from memory.
    // This lets the bot resume from the last session's average instead of restarting from config values.
    if (isDynamicAdjust) {
        // Only overwrite if the storage is not already marked as full (target > 0)
        readMemory(goldKey).toIntOrNull()?.let {
            if (targetGold > 0 && it > 0) {
                targetGold = it; goldFromMemory = true
            }
        }
        readMemory(elixirKey).toIntOrNull()?.let {
            if (targetElixir > 0 && it > 0) {
                targetElixir = it; elixirFromMemory = true
            }
        }
        readMemory(darkElixirKey).toIntOrNull()?.let {
            if (targetDarkElixir > 0 && it > 0) {
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
        accountMessage("${fullResources.joinToString("、")}已满")
    }
    val acceptedTaskBase = readClanGameAcceptedTaskBase()
    val hasMainWorldTask = acceptedTaskBase == CLAN_GAME_TASK_BASE_MAIN_WORLD
    // Evaluate each stop condition independently
    val stopResourcesEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key) && !hasMainWorldTask
    val stopNoStarEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.STOP_BATTLE_WHEN_NO_STAR.key) && !hasMainWorldTask
    val shouldStopResources = stopResourcesEnabled && targetGold == 0 && targetElixir == 0 && targetDarkElixir == 0
    var shouldStopNoStars = false
    if (stopNoStarEnabled) {
        val starsAvailable = findMultiColors(schema = MyColors.MainBaseStarsAvailable)
            ?: findMultiColors(schema = MyColors.MainBaseStarsAvailable2)
        shouldStopNoStars = starsAvailable == null
    }
    // Combined decision: when both are enabled, both must be satisfied; when only one is enabled, that one decides
    val finalStop = when {
        stopResourcesEnabled && stopNoStarEnabled -> shouldStopResources && shouldStopNoStars
        stopResourcesEnabled                       -> shouldStopResources
        stopNoStarEnabled                          -> shouldStopNoStars
        else                                       -> false
    }
    if (finalStop) {
        val reasons = mutableListOf<String>()
        if (shouldStopResources) reasons.add("资源已满")
        if (shouldStopNoStars) reasons.add("无胜利之星")
        accountMessage("${reasons.joinToString("，")}，停止对战")
        return false
    }
    var searchTimes = 0
    var battleStarted = false
    val battleStartTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - battleStartTime < SEARCH_TIMEOUT_MS) {
        val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
        if (battleIcon != null) {
            TouchActions.tap(80, 630)
        }
        // Detect attack cooldown screen; abort search so mainBaseAttack() can skip battle logic
        val battlePage = findMultiColors(MyColors.BattlePage)
        if (battlePage != null) {
            val waitForBattle = findMultiColors(schema = MyColors.WaitForBattle)
            if (waitForBattle != null) {
                accountMessage("进攻需等待冷却")
                val shouldWaitForBattle = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key)
                val shouldWaitAfterMainWorldTask = hasMainWorldTask && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE_AFTER_ACCEPT_MAIN_WORLD_TASK.key)
                if (!shouldWaitForBattle && !shouldWaitAfterMainWorldTask) return false
            }
        }
        val canNotAttack = findMultiColorsUntil(schemas = listOf(MyColors.CanNotAttack, MyColors.CanNotAttack2, MyColors.CanNotAttack3), duration = 10)
        if (canNotAttack != null) {
            return false
        }
        val villagerSpeaking = findMultiColors(schema = MyColors.SpeakingVillager)

        if (villagerSpeaking != null) {
            if (!mainBaseBattleTutorial()) return false
        }
        listOf(MyColors.SearchOpponents, MyColors.AttackButton, MyColors.IncreaseRank, MyColors.EnterLeague).forEach { schema ->
            findMultiColors(schema = schema)?.let { TouchActions.tap(it.x, it.y) }
        }
        val insufficientGold = findMultiColors(schema = MyColors.InsufficientGold)
        if (insufficientGold != null) {
            break
        }

        val nextOpponent = findMultiColors(schema = MyColors.NextOpponent)
        if (nextOpponent != null) {
            // Fast path: all targets below 10 → attack immediately without OCR
            if (targetGold < 10 && targetElixir < 10 && targetDarkElixir < 10) {
                battleStarted = true
                tryAutoBattleOrDeploy()
                break
            }

            searchTimes++
            delayWithMultiplier(100)
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

            accountMessage("搜索次数：$searchTimes\n对手资源：\n${res.gold}金, ${res.elixir}水, ${res.darkElixir}黑\n目标资源：\n${targetGold}金, ${targetElixir}水, ${targetDarkElixir}黑")

            // Skip the first search result when dynamic adjustment is enabled, so the average has at least one data point
            val allTargetsZero = targetGold == 0 && targetElixir == 0 && targetDarkElixir == 0
            val meetsCriteria = allTargetsZero || ((!isDynamicAdjust || searchTimes > 2) && res.gold >= targetGold && res.elixir >= targetElixir && res.darkElixir >= targetDarkElixir)
            if (meetsCriteria) {
                // Persist the final averaged thresholds so the next session can start from this value.
                if (isDynamicAdjust) {
                    writeMemory(goldKey, targetGold.toString())
                    writeMemory(elixirKey, targetElixir.toString())
                    writeMemory(darkElixirKey, targetDarkElixir.toString())
                }

                battleStarted = true
                tryAutoBattleOrDeploy()
                break
            } else {
                TouchActions.tap(nextOpponent.x, nextOpponent.y, delayTime = 2000)
            }
        }
        val remainingMinutes = (SEARCH_TIMEOUT_MS - (System.currentTimeMillis() - battleStartTime)) / 60000.0
        accountMessage("主世界搜索中... 第${battleNumber}/${battleTimes}局\n${"%.1f".format(remainingMinutes)}分钟后强制退出")
        if (!checkReconnections()) return false
        delayWithMultiplier(200)
    }
    return battleStarted
}

/**
 * If AUTO_BATTLE_CN is enabled (CN version only), tries to detect the auto-battle
 * button on the deploy preparation screen. If the button is found, clicks it and
 * uses the game's built-in auto-battle feature. Otherwise, falls back to the
 * standard troop deployment logic.
 */
private suspend fun tryAutoBattleOrDeploy() {
    val autoBattleEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_BATTLE_CN.key)
    // Auto-battle is CN-exclusive; skip detection for global version
    if (autoBattleEnabled && InGameVars.currentGameVersion == GameVersion.CN) {
        val autoBtn = findMultiColorsUntil(
            schemas = listOf(MyColors.AutoBattleBtn, MyColors.AutoBattleBtn2),
            duration = AUTO_BATTLE_DETECT_MS.toInt()
        )
        if (autoBtn != null) {
            TouchActions.tap(autoBtn.x, autoBtn.y)
            accountMessage("检测到自动战斗按钮，已点击启用")
            return
        }
        accountMessage("未检测到自动战斗按钮，使用原有下兵方式兜底")
    }
    // Fallback: Mode "0" = original single-side deploy; otherwise 4-side deploy
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.MANUAL_TRAINING.key) &&
        getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DEPLOY_MODE.key) != "0"
    ) {
        cnMainBaseDeployTroops()
    } else {
        mainBaseDeployTroops()
    }
}

private suspend fun mainBaseBattleTutorial(): Boolean {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < TUTORIAL_TIMEOUT_MS) {
        val villagerSpeaking = findMultiColors(schema = MyColors.SpeakingVillager)
        if (villagerSpeaking != null) {
            TouchActions.tap(villagerSpeaking.x, villagerSpeaking.y)
        }
        val barbarianKingTutorial = findMultiColors(schema = MyColors.BarbarianKingTutorial)
        if (barbarianKingTutorial != null) {
            TouchActions.tap(135, 405)
        }
        val setBaseIcon = findMultiColorsUntil(listOf(MyColors.SetBaseIcon, MyColors.SetBaseIcon2, MyColors.SetBaseIcon3, MyColors.SetBaseIcon4), 10)
        if (setBaseIcon != null) {
            TouchActions.tap(550, 160)
        }
        val innerSetBaseIcon = findMultiColors(schema = MyColors.InnerSetBase)
        if (innerSetBaseIcon != null) {
            TouchActions.tap(640, 300)
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
        val remainingSeconds = (TUTORIAL_TIMEOUT_MS - (System.currentTimeMillis() - startTime)) / 1000.0
        accountMessage("主世界对战教程中，剩余${"%.1f".format(remainingSeconds)}秒后强制结束")
        delayWithMultiplier(100)
    }
    return enterMainScreen()
}
