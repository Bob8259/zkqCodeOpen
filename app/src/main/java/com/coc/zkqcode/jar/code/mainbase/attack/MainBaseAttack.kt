package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.clangame.hasAcceptedClanGameTask
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.CLAN_GAME_TASK_BASE_MAIN_WORLD
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.readClanGameAcceptedTaskBase
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.enterMainBase
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseRemainingAttack(): Boolean {
    val battleTimesSetting = if (readClanGameAcceptedTaskBase() == CLAN_GAME_TASK_BASE_MAIN_WORLD) {
        Schema.MAIN_BASE_SETTINGS.BATTLE_TIMES_WITH_TASKS
    } else {
        Schema.MAIN_BASE_SETTINGS.BATTLE_TIMES
    }
    val totalBattleTimes = getConfigRuntime(battleTimesSetting.key).toIntOrNull() ?: logAndRestart("${battleTimesSetting.displayName}必须是数字。")
    val normalBattleTimes = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.BATTLE_TIMES.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.BATTLE_TIMES.displayName}必须是数字。")
    val shouldCheckClanGameTask = battleTimesSetting == Schema.MAIN_BASE_SETTINGS.BATTLE_TIMES_WITH_TASKS && totalBattleTimes > normalBattleTimes
    val remainingTimes = (totalBattleTimes - 1).coerceAtLeast(0)
    repeat(remainingTimes) { index ->
        val completedBattleTimes = index + 2
        if (shouldCheckClanGameTask && completedBattleTimes >= normalBattleTimes && !hasAcceptedClanGameTask(BaseType.Main)) return true
        if (!mainBaseAttack(battleNumber = completedBattleTimes, battleTimes = totalBattleTimes)) return false
    }
    return true
}

suspend fun mainBaseAttack(battleNumber: Int = 1, battleTimes: Int = 1): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_ATTACK.key)) return true
    // Skip post-battle logic if no battle was started (e.g. cooldown, timeout, insufficient gold)
    if (!enterMainBase()) return false
    val battleStarted = searchOpponentsAndDeployTroops(battleNumber, battleTimes)
    if (!battleStarted) return enterMainScreen()
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLE.key)) return false
    val maxDurationMs = 3 * 60 * 1000L // Maximum battle wait time: 3 minutes
    val startTime = System.currentTimeMillis()
    val isRestartGame = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESTART_GAME.key)
    val hasMainWorldTask = readClanGameAcceptedTaskBase() == CLAN_GAME_TASK_BASE_MAIN_WORLD
    if (isRestartGame) {
        accountMessage("部署完成，准备重启游戏")
        killGame()
        delayWithMultiplier(1000)
        runGame()
    } else {
        val exitOnOneStar = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.EXIT_ON_ONE_STAR_MAIN.key)
        val ocrExitEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.OCR_EXIT_ENABLED.key)
        val destructionOcrState = BattleDestructionOcrState()
        // Show disabled message once at the beginning if has task
        var taskDisableMessageShown = false
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            // Force exit if battle has exceeded the maximum allowed duration
            if (elapsed >= maxDurationMs) {
                killGame()
                break
            }

            // Show task disabled message once
            if (hasMainWorldTask && !taskDisableMessageShown) {
                val disabledFeatures = mutableListOf<String>()
                if (exitOnOneStar) disabledFeatures.add("一星退出")
                if (ocrExitEnabled) disabledFeatures.add("摧毁率退出")
                if (disabledFeatures.isNotEmpty()) {
                    accountMessage("已接取任务 不生效${disabledFeatures.joinToString("/")}")
                }
                taskDisableMessageShown = true
            }

            // Detect 1-star result and exit immediately (disabled when has clan game task)
            if (exitOnOneStar && !hasMainWorldTask) {
                val oneStar1 = findMultiColors(schema = MyColors.MainBaseOneStar1)
                val oneStar2 = findMultiColors(schema = MyColors.MainBaseOneStar2)
                if (oneStar1 != null || oneStar2 != null) {
                    surrenderMainBaseBattle("检测到一星，退出对战")
                    break
                }
            }

            // OCR destruction-% exit detection (only when enabled and no task)
            if (ocrExitEnabled && !hasMainWorldTask) {
                val (_, ocrResult) = checkBattleDestructionOcrExit(destructionOcrState)
                when (ocrResult) {
                    BattleDestructionOcrExitResult.ExitTargetMet -> {
                        surrenderMainBaseBattle("破坏百分比已达目标，退出对战")
                        break
                    }

                    BattleDestructionOcrExitResult.ExitStale -> {
                        surrenderMainBaseBattle("破坏百分比长时间未变化，退出对战")
                        break
                    }

                    BattleDestructionOcrExitResult.ExitOcrFailed -> {
                        surrenderMainBaseBattle("破坏百分比识别失败超时，退出对战")
                        break
                    }

                    BattleDestructionOcrExitResult.Continue -> { /* keep polling */ }
                }
            }


            val endBattleButton = findMultiColors(schema = MyColors.EndBattle)
            if (endBattleButton == null) {
                accountMessage("未找到放弃按钮，对战结束")
                delayWithMultiplier(1000)
                TouchActions.tap(640, 610, delayTime = 1500)
                break
            }

            delayWithMultiplier(200) // Poll every 200ms
        }
    }
    return enterMainScreen(isRestartGame)
}

/** Surrender via EndBattle and return to camp (shared by one-star and OCR destruction-% exit). */
private suspend fun surrenderMainBaseBattle(reason: String) {
    accountMessage(reason)
    val endBattleBtn = findMultiColors(schema = MyColors.EndBattle)
    if (endBattleBtn != null) {
        TouchActions.tap(endBattleBtn.x, endBattleBtn.y, delayTime = 200)
        val confirmBtn = findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1000)
        if (confirmBtn != null) {
            TouchActions.tap(confirmBtn.x, confirmBtn.y, delayTime = 200)
        }
    }
    delayWithMultiplier(1000)
    TouchActions.tap(640, 610, delayTime = 1500)
}