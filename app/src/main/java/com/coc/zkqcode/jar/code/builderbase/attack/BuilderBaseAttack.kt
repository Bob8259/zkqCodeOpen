package com.coc.zkqcode.jar.code.builderbase.attack

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar
import kotlin.random.Random

suspend fun builderBaseAttack(): Boolean {
    // 1. Check if Builder Base farming is enabled
    val isEnabled = getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key)
    if (!isEnabled) {
        ShowMessage("未开启打夜世界")
        return true
    }

    // 2. Locate resource indicators
    val goldPos = findMultiColors(schema = MyColors.BuilderBaseGold)
    val exilePos = findMultiColors(schema = MyColors.BuilderBaseExiler)
    ShowMessage("金币坐标: $goldPos, 圣水坐标: $exilePos")
    // Define resource fullness (threshold: < 1016 indicates full/near full based on original logic)
    val isGoldFull = goldPos != null && goldPos.x < 1016

    val isExileFull = exilePos != null && exilePos.x < 1016
    val stopIfFull = getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.key)

    // 3. Determine action based on resource state and settings
    if (isGoldFull && isExileFull && stopIfFull) {
        ShowMessage("资源已满，停止对战")
    } else {
        // Evaluate attack strategy
        val attackType = when {
            getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.TROPHY_PUSHING_MODE.key) -> {
                ShowMessage("已勾选上分模式")
                "gold"
            }

            getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.ELIXIR_CART_FARMING.key) -> {
                ShowMessage("已勾选刷圣水车模式")
                "exile"
            }
            // If gold is not full (x > 1016), prioritize gold; otherwise, default to exile
            goldPos == null || goldPos.x > 1016 -> "gold"
            else -> "exile"
        }
        val battleTimes = getConfigRuntime(Schema.BUILDER_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLES.key).toInt()
        repeat(battleTimes) { index ->
            if (!realAttack(attackType, index + 1, battleTimes)) return false
        }
    }
    // 4. Return to main screen
    return enterMainScreen()
}

private suspend fun realAttack(mode: String, battleNumber: Int = 1, battleTimes: Int): Boolean {
    val startTime = System.currentTimeMillis()
    // Track first detection of SwitchTroopButton for shorter initial delay
    var isFirstSwitchTroop = true
    while (true) {
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed > 8 * 60 * 1000L) {
            ShowMessage("战斗超过8分钟，强制退出")
            break
        }
        val remainingMin = (8 * 60 * 1000L - elapsed) / 60000.0
        ShowMessage("对战中，第${battleNumber}/${battleTimes}局\n若${"%.1f".format(remainingMin)}分钟内未完成对战，则强制重启")
        val trainTroopButton = findMultiColors(schema = MyColors.TrainTroops)
        if (trainTroopButton != null) {
            TouchActions.tap(86, 638, delayTime = 500)//Attack
        }
        checkReconnections()
        val builderBaseStarBonus = findMultiColors(schema = MyColors.BuilderBaseStarBonus)
        if (builderBaseStarBonus != null) {
            TouchActions.tap(builderBaseStarBonus.x, builderBaseStarBonus.y, delayTime = 200)
        }
        val attackNow = findMultiColors(schema = MyColors.AttackNow)
        if (attackNow != null) {
            TouchActions.tap(attackNow.x, attackNow.y, delayTime = 200)
            val warning = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroopsWarning), duration = 500)
            if (warning != null) {
                clickRightBottom(2)
                builderBaseTrainTroops()
            }
        }
        val search = findMultiColors(schema = MyColors.CancelAttackSearch)
        if (search != null) {
            waitLoop()
        }
        val switchTroopButton = findMultiColors(schema = MyColors.SwitchTroopButton)
        if (switchTroopButton != null) {
            // Use shorter delay on first detection, normal delay afterward
            if (isFirstSwitchTroop) {
                delayWithMultiplier(500)
                isFirstSwitchTroop = false
            } else {
                ShowMessage("已进入第二区域")
                delayWithMultiplier(2000)
            }
            if (mode == "gold") {
                normalBattle()
            } else if (mode == "exile") {
                deployAndExit()
            }
        }
        val builderBaseEndBattle = findMultiColors(schema = MyColors.BuilderBackToCamp)
        if (builderBaseEndBattle != null) {
            TouchActions.tap(builderBaseEndBattle.x, builderBaseEndBattle.y, delayTime = 200)
            break
        }
        val machineSkills = findMultiColors(schema = MyColors.MachineSkills)
        if (machineSkills != null) {
            TouchActions.tap(machineSkills.x, machineSkills.y + 100, delayTime = 200)
        }
        delayWithMultiplier(100)
    }
    return enterMainScreen()
}

private suspend fun deployAndExit() {
    normalBattle(false)
    val exitButton = findMultiColorsUntil(schemas = listOf(MyColors.ExitBattleButton), duration = 2000)
    if (exitButton != null) {
        TouchActions.tap(exitButton.x, exitButton.y, delayTime = 200)
        TouchActions.tap(775, 469, delayTime = 100)
    }
}

private suspend fun normalBattle(isNormal: Boolean = true) {
    // Define possible deploy positions for each swipe direction
    val deployPositions = listOf(
        Pair(605, 553), Pair(108, 183), Pair(330, 358), Pair(855, 371), Pair(1090, 180), Pair(271, 64)
    )

    val alternativeDeployPositions = listOf(
        Pair(663, 205), Pair(1160, 564), Pair(913, 385), Pair(433, 374), Pair(758, 274)
    )

    pinchIn(141, 423, 1052, 352, 638, 365, duration = 200)

    // Choose swipe direction and corresponding deploy positions
    val (swipeParams, positions) = if (Random.nextBoolean()) {
        Triple(981, 485, 0) to deployPositions
    } else {
        Triple(100, 117, 1280) to alternativeDeployPositions
    }
    delayWithMultiplier(100)
    swipe(swipeParams.first, swipeParams.second, swipeParams.third, if (swipeParams.third == 0) 0 else 720, delayTime = 120)

    // Deploy machine and troops to the same random position
    val deployPos = positions.random()
    TouchActions.tap(126, 638, delayTime = 200) // Battle Machine
    TouchActions.tap(deployPos.first, deployPos.second, delayTime = 200) // Deploy the Machine
    if (!isNormal) return
    val generalTroops = findMultiColorsUntil(schemas = listOf(MyColors.TroopsWithSkills, MyColors.TroopsWithSkills), duration = 200)
    if (generalTroops != null) {
        TouchActions.tap(generalTroops.x + 15, 646, delayTime = 200) // Troops
        val nightWitch = findMultiColors(schema = MyColors.NightWitch)
        if (nightWitch != null) {
            TouchActions.touchDown((deployPos.first + Random.nextInt(1, 4)).toFloat(), (deployPos.second + Random.nextInt(1, 4)).toFloat(), 1)
            delayWithMultiplier(4000)
            TouchActions.touchUp(1)
            ShowMessage("等女巫走一会")
            delayWithMultiplier(Random.nextInt(5000, 10000))
            repeat(6) {
                val skillsPos = findMultiColors(
                    schema = ColorSchema.rescope(
                        MyColors.TroopSkills, MyColors.TroopSkills.x1, MyColors.TroopSkills.y1, MyColors.TroopSkills.x2, MyColors.TroopSkills.y2, direction = Random.nextInt(0, 2)
                    )
                )
                if (skillsPos != null) {
                    TouchActions.tap(skillsPos.x, skillsPos.y + 100)
                    delayWithMultiplier(Random.nextInt(500, 4000))
                }
            }
        } else {
            attemptLoop@ for (attempt in 0 until 5) {
                // Step 1: Touch down at a random position
                var currentPos = deployPositions.random()
                TouchActions.touchDown(currentPos.first.toFloat(), currentPos.second.toFloat(), 1)
                delayWithMultiplier(700)

                // Steps 2-3: Move smoothly to random positions until barbarian is gone
                while (true) {
                    val nextPos = deployPositions.random()
                    TouchActions.moveSmoothly(
                        fromX = currentPos.first.toFloat(), fromY = currentPos.second.toFloat(), toX = nextPos.first.toFloat(), toY = nextPos.second.toFloat(), duration = Random.nextInt(200, 500)
                    )
                    currentPos = nextPos
                    val barbarian = findMultiColors(schema = MyColors.BuilderBaseBarbarian)
                    if (barbarian == null) {
                        // Step 4: Release finger and exit outer loop — barbarian is gone
                        TouchActions.touchUp(1)
                        break@attemptLoop
                    }
                }
            }
        }
    }
}

private suspend fun waitLoop() {
    val totalDuration = 15_000L
    val startTime = System.currentTimeMillis()
    var lastPosition: Point?

    while (true) {
        val elapsed = System.currentTimeMillis() - startTime
        val remainingMs = totalDuration - elapsed

        val pos = findMultiColors(schema = MyColors.CancelAttackSearch)
        if (pos != null) lastPosition = pos
        else return
        if (remainingMs <= 0) break

        val remainingSeconds = remainingMs / 1000.0
        ShowMessage("搜索中，剩余 ${"%.1f".format(remainingSeconds)} 秒")
        delayWithMultiplier(1000)
    }
    TouchActions.tap(lastPosition.x, lastPosition.y, delayTime = 200)
}

private suspend fun builderBaseTrainTroops() {
    // Attempt to locate the initial training button
    val trainingButton = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1500)

    if (trainingButton == null) {
        ShowMessage("夜世界练兵失败")
        return
    }

    // Enter training menu
    TouchActions.tap(trainingButton.x, trainingButton.y, delayTime = 400)

    // Clear existing troops if the clear button is present
    val cleanTroops = findMultiColorsUntil(schemas = listOf(MyColors.RedCleanButton), duration = 1000)
    if (cleanTroops != null) {
        TouchActions.tap(cleanTroops.x, cleanTroops.y, delayTime = 500)
    }

    /**
     * Helper to perform repeated taps on a specific coordinate
     */
    suspend fun tapRepeat(x: Int, y: Int, times: Int = 10) {
        repeat(times) {
            TouchActions.tap(x, y, delayTime = 50)
        }
    }

    // Identify troop type and train
    val trainNightWitch = findMultiColors(schema = MyColors.TrainNightWitch)
    if (trainNightWitch != null) {
        // Train Night Witches based on detected location
        ShowMessage("练暗夜女巫")
        tapRepeat(trainNightWitch.x, trainNightWitch.y)
    } else {
        // Fallback to Barbarians using original hardcoded coordinates
        ShowMessage("未检测到暗夜女巫，练野蛮人\n（有暗夜女巫后会练暗夜女巫）")
        tapRepeat(278, 491)
    }

    // Close the training interface
    TouchActions.tap(1152, 102, delayTime = 300)
}

suspend fun builderBaseTrainWithConditions(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.BUILDER_BASE_TRAIN_TROOPS, InGamesVars.currentAccountNumber)
    val lastTrainingTime = readMemory(storageKey).toIntOrNull()

    // Use Calendar only once to retrieve the current day of the month
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    if (lastTrainingTime != currentDay) {
        builderBaseTrainTroops()
        writeMemory(storageKey, currentDay.toString())
        return enterMainScreen()
    }

    // Returns true if training was already completed today
    return true
}