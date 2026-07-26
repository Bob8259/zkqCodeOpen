package com.coc.zkqcode.jar.code.builderbase.attack

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.pinchIn
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.swipe
import com.coc.zkqcode.jar.code.builderbase.others.collectBuilderBaseResources
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.auth.displayAds
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.walls.calculateResourcesPercentage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.clangame.hasAcceptedClanGameTask
import com.coc.zkqcode.jar.code.universal.smalltools.data.CLAN_GAME_TASK_BASE_BUILDER_BASE
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.readClanGameAcceptedTaskBase
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.enterBuilderBase
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlin.random.Random

// Elevated from local nested function to private top-level for reusability
private suspend fun tapRepeat(x: Int, y: Int, times: Int = 12) {
    repeat(times) {
        TouchActions.tap(x, y, delayTime = 80)
    }
}

suspend fun builderBaseAttack(): Boolean {
    // 1. Check if Builder Base farming is enabled
    if (!getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key)) {
        return true
    }

    // 2. Read shared configs (unchanged across battles)
    val stopIfFull = getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_STOP_BATTLE_AFTER_FULL_RESOURCES.key)
    val stopNoStarConfig = getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_STOP_BATTLE_WHEN_NO_STAR.key)
    val acceptedTaskBase = readClanGameAcceptedTaskBase()
    val hasBuilderBaseTask = acceptedTaskBase == CLAN_GAME_TASK_BASE_BUILDER_BASE

    // 3. Evaluate battle times
    val battleTimesSetting = if (hasBuilderBaseTask) {
        Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES_WITH_TASKS
    } else {
        Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES
    }
    val battleTimes = getConfigRuntime(battleTimesSetting.key).toIntOrNull() ?: logAndRestart("${battleTimesSetting.displayName} 必须是数字，请检查配置")
    val normalBattleTimes =
        getConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES.key).toIntOrNull() ?: logAndRestart("${Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES.displayName} 必须是数字，请检查配置")
    val shouldCheckClanGameTask = battleTimesSetting == Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES_WITH_TASKS && battleTimes > normalBattleTimes

    // Shared stop-condition check used before the first battle and after each completed battle
    suspend fun shouldStopBuilderBaseBattle(): Boolean {
        val resourcePercentage = calculateResourcesPercentage(BaseType.Builder)
        accountMessage("金币百分比: ${resourcePercentage.gold}%, 圣水百分比: ${resourcePercentage.elixir}%")
        val stopResourcesEnabled = stopIfFull && !hasBuilderBaseTask
        val stopNoStarEnabled = stopNoStarConfig && !hasBuilderBaseTask
        val shouldStopResources = stopResourcesEnabled && resourcePercentage.gold >= 96 && resourcePercentage.elixir >= 96
        val shouldStopNoStars = stopNoStarEnabled && findMultiColors(schema = MyColors.BuilderBaseStarsAvailable) == null
        // Combined decision: when both are enabled, both must be satisfied; when only one is enabled, that one decides
        val finalStop = when {
            stopResourcesEnabled && stopNoStarEnabled -> shouldStopResources && shouldStopNoStars
            stopResourcesEnabled -> shouldStopResources
            stopNoStarEnabled -> shouldStopNoStars
            else -> false
        }
        if (finalStop) {
            val reasons = mutableListOf<String>()
            if (shouldStopResources) reasons.add("资源已满")
            if (shouldStopNoStars) reasons.add("无胜利之星")
            accountMessage("${reasons.joinToString("，")}，停止对战")
        }
        return finalStop
    }

    // Check once before starting any battle.
    if (shouldStopBuilderBaseBattle()) return enterMainScreen()

    // Recheck after each completed battle so later battles are skipped immediately.
    run loop@{
        repeat(battleTimes) { index ->
            // Recalculate resources every battle for attack strategy selection.
            val resourcePercentage = calculateResourcesPercentage(BaseType.Builder)
            accountMessage("金币百分比: ${resourcePercentage.gold}%, 圣水百分比: ${resourcePercentage.elixir}%")

            // ---- Determine attack strategy based on current resource state ----
            // Auto-detect Victory Star: if stars are available, force trophy-pushing mode to claim them fast
            val hasVictoryStar = findMultiColors(schema = MyColors.BuilderBaseStarsAvailable) != null
            val attackType = when {
                hasVictoryStar -> {
                    accountMessage("检测到胜利之星，自动切换上分模式快速完成")
                    "gold"
                }

                getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.TROPHY_PUSHING_MODE.key) -> {
                    accountMessage("已勾选上分模式")
                    "gold"
                }

                getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.ELIXIR_CART_FARMING.key) -> {
                    accountMessage("已勾选刷圣水车模式")
                    "exile"
                }

                resourcePercentage.gold < 96 -> "gold"
                else -> "exile"
            }

            if (!realAttack(attackType, hasBuilderBaseTask, index + 1, battleTimes)) return false
            val completedBattleTimes = index + 1
            if (shouldStopBuilderBaseBattle()) return@loop
            if (shouldCheckClanGameTask && completedBattleTimes >= normalBattleTimes && !hasAcceptedClanGameTask(BaseType.Builder)) return true

            if ((index + 1) % 5 == 0) {
                if (!enterMainScreen()) return false
                displayAds()
                collectBuilderBaseResources()
            }
        }
    }
    // 5. Return to main screen
    return enterMainScreen()
}

private suspend fun realAttack(mode: String, hasBuilderBaseTask: Boolean, battleNumber: Int = 1, battleTimes: Int): Boolean {
    val startTime = System.currentTimeMillis()
    val exitOnOneStar = getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.EXIT_ON_ONE_STAR_BUILDER.key)
    var troopsDeployed = false
    // Track first detection of SwitchTroopButton for shorter initial delay
    var isFirstSwitchTroop = true
    while (true) {
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed > 8 * 60 * 1000L) {
            accountMessage("战斗超过8分钟，强制退出")
            break
        }
        val remainingMin = (8 * 60 * 1000L - elapsed) / 60000.0
        accountMessage("夜世界对战中，第${battleNumber}/${battleTimes}局\n若${"%.1f".format(remainingMin)}分钟内未完成对战，则强制重启")
        if (!checkReconnections()) return false
        // Capture a single screenshot and reuse it for all state checks in this iteration
        val capturedScreen = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)

        val trainTroopButton = findMultiColors(byteBuffer = capturedScreen, schema = MyColors.TrainTroops)
        if (trainTroopButton != null) {
            TouchActions.tap(85, 640, delayTime = 800)
            continue // State matched, skip remaining checks
        }
        val builderBaseStarBonus = findMultiColors(byteBuffer = capturedScreen, schema = MyColors.BuilderBaseStarBonus)
        if (builderBaseStarBonus != null) {
            TouchActions.tap(builderBaseStarBonus.x + 10, builderBaseStarBonus.y + 10, delayTime = 200)
            continue
        }
        val attackNow = findMultiColors(byteBuffer = capturedScreen, schema = MyColors.AttackNow)
        if (attackNow != null) {
            TouchActions.tap(attackNow.x, attackNow.y, delayTime = 200)
            val warning = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroopsWarning), duration = 500)
            if (warning != null) {
                clickRightBottom(2)
                builderBaseTrainTroops()
            }
            val greenButton = findMultiColorsUntil(listOf(MyColors.GreenConfirm, MyColors.MiddleGreenYes), 100)
            if (greenButton != null) {
                TouchActions.tap(greenButton.x, greenButton.y)
            }
            continue
        }
        val search = findMultiColors(byteBuffer = capturedScreen, schema = MyColors.CancelAttackSearch)
        if (search != null) {
            waitLoop()
            continue
        }
        // Detect 1-star result and exit immediately
        if (exitOnOneStar && troopsDeployed && !hasBuilderBaseTask) {
            val oneStar = findMultiColorsUntil(
                schemas = listOf(MyColors.BuilderBaseOneStar1, MyColors.BuilderBaseOneStar2), duration = 200, byteBuffer = capturedScreen
            )
            if (oneStar != null) {
                accountMessage("检测到一星，退出对战")
                val exitButton = findMultiColorsUntil(schemas = listOf(MyColors.ExitBattleButton), duration = 2000)
                if (exitButton != null) {
                    TouchActions.tap(exitButton.x, exitButton.y, delayTime = 200)
                    TouchActions.tap(775, 470)
                }
                break
            }
        }
        val builderBaseEndBattle = findMultiColors(byteBuffer = capturedScreen, schema = MyColors.BuilderBackToCamp)
        if (builderBaseEndBattle != null) {
            TouchActions.tap(builderBaseEndBattle.x, builderBaseEndBattle.y, delayTime = 200)
            break
        }
        val switchTroopButton = findMultiColorsUntil(schemas = listOf(MyColors.TroopsWithSkills, MyColors.TroopsWithOutSkills), duration = 200, byteBuffer = capturedScreen)
        if (switchTroopButton != null) {
            // Use shorter delay on first detection, normal delay afterward
            if (isFirstSwitchTroop) {
                delayWithMultiplier(500)
                isFirstSwitchTroop = false
            } else {
                accountMessage("已进入第二区域")
                delayWithMultiplier(2000)
            }
            // Last round + trophy pushing not enabled: battle, kill & restart game, then return
            if (battleNumber == battleTimes && !getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.TROPHY_PUSHING_MODE.key)) {
                normalBattle(skipSkills = true)
                killGame()
                runGame()
                return true
            }
            if (hasBuilderBaseTask) {
                normalBattle()
            } else {
                when (mode) {
                    "gold" -> normalBattle()
                    "exile" -> deployAndExit()
                }
            }
            troopsDeployed = true
            continue
        }

        val machineSkills = findMultiColors(byteBuffer = capturedScreen, schema = MyColors.MachineSkills)

        if (machineSkills != null) {
            TouchActions.tap(machineSkills.x, machineSkills.y + 100, delayTime = 200)
        }
        delayWithMultiplier(600)
    }
    if (!enterMainScreen()) return false
    return enterBuilderBase(false)
}

private suspend fun deployAndExit() {
    normalBattle(false)
    val exitButton = findMultiColorsUntil(schemas = listOf(MyColors.ExitBattleButton), duration = 2000)
    if (exitButton != null) {
        TouchActions.tap(exitButton.x, exitButton.y, delayTime = 200)
        TouchActions.tap(775, 470)//Confirm exit
    }
}

private suspend fun normalBattle(isNormal: Boolean = true, skipSkills: Boolean = false) {
    // Define possible deploy positions for each swipe direction
    val deployPositions = listOf(
        Pair(605, 553), Pair(108, 183), Pair(330, 358), Pair(855, 371)
    )

    val alternativeDeployPositions = listOf(
        Pair(663, 205), Pair(1160, 564), Pair(913, 385), Pair(433, 374), Pair(758, 274)
    )

    pinchIn(141, 423, 1052, 352, 638, 365, duration = 200)

    // Choose swipe direction and corresponding deploy positions
    val useRightSwipe = Random.nextBoolean()
    val positions = if (useRightSwipe) deployPositions else alternativeDeployPositions
    delayWithMultiplier(100)
    if (useRightSwipe) {
        swipe(981, 485, 0, 0, delayTime = 120)
    } else {
        swipe(100, 117, 1280, 720, delayTime = 120)
    }

    // Deploy machine and troops to the same random position
    val deployPos = positions.random()
    TouchActions.tap(125, 610, delayTime = 200) // Battle Machine
    TouchActions.tap(deployPos.first, deployPos.second, delayTime = 200) // Deploy the Machine
    if (!isNormal) return

    val generalTroops = findMultiColorsUntil(schemas = listOf(MyColors.TroopsWithSkills, MyColors.TroopsWithOutSkills), duration = 200)
    if (generalTroops != null) {
        accountMessage("准备点击部队，点击坐标${generalTroops.x + 1}, 610\n当前部署位置${deployPos.first}, ${deployPos.second}")
        delayWithMultiplier(1500)
        TouchActions.tap(generalTroops.x + 15, 620, delayTime = 200) // Troops
        val nightWitch = findMultiColors(schema = MyColors.NightWitch)
        if (nightWitch != null) {
            TouchActions.touchDown((deployPos.first + Random.nextInt(1, 4)).toFloat(), (deployPos.second + Random.nextInt(1, 4)).toFloat(), 1)
            delayWithMultiplier(3000)
            TouchActions.touchUp(1)
            if (!skipSkills) {
                accountMessage("等女巫走一会")
                delayWithMultiplier(Random.nextInt(5000, 10000))

                repeat(6) {
                    val skillsPos = findMultiColors(
                        schema = ColorSchema.rescope(
                            MyColors.TroopSkills, MyColors.TroopSkills.x1, MyColors.TroopSkills.y1, MyColors.TroopSkills.x2, MyColors.TroopSkills.y2, direction = Random.nextInt(0, 2)
                        )
                    )

                    if (skillsPos != null) {
                        TouchActions.tap(skillsPos.x, 620)
                        delayWithMultiplier(Random.nextInt(500, 2000))
                    }
                }
            }
        } else {
            attemptLoop@ for (attempt in 0 until 5) {
                // Step 1: Touch down at a random position
                var currentPos = deployPositions.random()
                TouchActions.touchDown(deployPos.first.toFloat(), deployPos.second.toFloat(), 1)
                delayWithMultiplier(600)

                // Steps 2-3: Move smoothly to random positions until barbarian is gone
                var moveCount = 0
                while (true) {
                    val nextPos = deployPositions.random()
                    TouchActions.moveSmoothly(
                        fromX = currentPos.first.toFloat(), fromY = currentPos.second.toFloat(), toX = nextPos.first.toFloat(), toY = nextPos.second.toFloat(), duration = Random.nextInt(200, 500)
                    )
                    currentPos = nextPos
                    moveCount++
                    // Only check barbarian presence every 3 moves to reduce findMultiColors calls
                    if (moveCount % 3 == 0) {
                        val barbarian = findMultiColors(schema = MyColors.BuilderBaseBarbarian)
                        if (barbarian == null) {
                            // Release finger and exit outer loop — barbarian is gone
                            TouchActions.touchUp(1)
                            break@attemptLoop
                        }
                    }
                }
            }
        }
    }
}

private suspend fun waitLoop() {
    val totalDuration = 60_000L
    val startTime = System.currentTimeMillis()
    // Guaranteed non-null at use site due to early return guard below
    var lastPosition: Point?

    while (true) {
        val elapsed = System.currentTimeMillis() - startTime
        val remainingMs = totalDuration - elapsed

        val pos = findMultiColors(schema = MyColors.CancelAttackSearch)
        if (pos != null) lastPosition = pos
        else return
        if (remainingMs <= 0) break

        val remainingSeconds = remainingMs / 1000.0
        accountMessage("夜世界搜索中，剩余 ${"%.1f".format(remainingSeconds)} 秒")
        delayWithMultiplier(1000)
    }
    TouchActions.tap(lastPosition.x, lastPosition.y, delayTime = 200)
}

private suspend fun builderBaseTrainTroops() {
    // Attempt to locate the initial training button
    val trainingButton = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1500)

    if (trainingButton == null) {
        accountMessage("夜世界练兵失败")
        return
    }

    // Enter training menu
    TouchActions.tap(trainingButton.x, trainingButton.y)

    // Clear existing troops if the clear button is present
    val cleanTroops = findMultiColorsUntil(schemas = listOf(MyColors.RedCleanButton), duration = 1000)
    if (cleanTroops != null) {
        TouchActions.tap(cleanTroops.x, cleanTroops.y)
    }

    // Identify troop type and train
    val trainNightWitch = findMultiColors(schema = MyColors.TrainNightWitch)
    if (trainNightWitch != null) {
        // Train Night Witches based on detected location
        accountMessage("练暗夜女巫")
        tapRepeat(trainNightWitch.x, trainNightWitch.y)
    } else {
        // Fallback to Barbarians using original hardcoded coordinates
        accountMessage("未检测到暗夜女巫，练野蛮人\n（有暗夜女巫后会练暗夜女巫）")
        tapRepeat(278, 491)
    }

    // Close the training interface
    TouchActions.tap(1152, 102, delayTime = 300)
}

suspend fun builderBaseTrainWithConditions(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.BUILDER_BASE_TRAIN_TROOPS, InGameVars.currentAccountNumber)

    if (checkMemoryFile(storageKey, 1440)) {
        builderBaseTrainTroops()
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        return enterMainScreen()
    }

    // Training was already completed within 24 hours
    return true
}
