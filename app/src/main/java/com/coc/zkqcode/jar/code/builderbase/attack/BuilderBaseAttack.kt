package com.coc.zkqcode.jar.code.builderbase.attack

import android.graphics.Point
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
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

    // Define resource fullness (threshold: < 1015 indicates full/near full based on original logic)
    val isGoldFull = goldPos != null && goldPos.x < 1015
    val isExileFull = exilePos != null && exilePos.x < 1015
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
            // If gold is not full (x > 1014), prioritize gold; otherwise, default to exile
            goldPos != null && goldPos.x > 1014 -> "gold"
            else -> "exile"
        }
        realAttack(attackType)
    }
    // 4. Return to main screen
    return enterMainScreen()
}

private suspend fun realAttack(mode: String) {
    var startTime = System.currentTimeMillis()
    var elapsedTime = 0
    while (true) {
        val trainTroopButton = findMultiColors(schema = MyColors.TrainTroops)
        if (trainTroopButton != null) {

            TouchActions.tap(86, 638)
            delayWithMultiplier(500)
        }
        checkReconnections()
        val attackNow = findMultiColors(schema = MyColors.AttackNow)
        if (attackNow != null) {
            TouchActions.tap(attackNow.x, attackNow.y)
            delayWithMultiplier(200)
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
            if (mode == "gold") {
                normalBattle()
            }
        }
    }
}

private suspend fun normalBattle() {
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(200)
    if (Random.nextBoolean()) {
        swipe(981, 485, 0, 0, delayTime = 120)
    } else {
        swipe(100, 117, 1280, 720, delayTime = 120)
    }
    
}

private suspend fun waitLoop() {
    val totalDuration = 15_000L
    val startTime = System.currentTimeMillis()
    var lastPosition: Point? = null

    while (true) {
        val elapsed = System.currentTimeMillis() - startTime
        val remainingMs = totalDuration - elapsed

        val pos = findMultiColors(schema = MyColors.CancelAttackSearch)
        if (pos != null) lastPosition = pos

        if (remainingMs <= 0) break

        val remainingSeconds = remainingMs / 1000.0
        ShowMessage("搜索中，剩余 ${"%.1f".format(remainingSeconds)} 秒")
        delayWithMultiplier(100)
    }

    if (lastPosition != null) {
        TouchActions.tap(lastPosition.x, lastPosition.y)
        delayWithMultiplier(200)
    }
}

private suspend fun builderBaseTrainTroops() {
    // Attempt to locate the initial training button
    val trainingButton = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1500)

    if (trainingButton == null) {
        ShowMessage("夜世界练兵失败")
        return
    }

    // Enter training menu
    TouchActions.tap(trainingButton.x, trainingButton.y)
    delayWithMultiplier(400)

    // Clear existing troops if the clear button is present
    val cleanTroops = findMultiColorsUntil(schemas = listOf(MyColors.RedCleanButton), duration = 1000)
    if (cleanTroops != null) {
        TouchActions.tap(cleanTroops.x, cleanTroops.y)
        delayWithMultiplier(500)
    }

    /**
     * Helper to perform repeated taps on a specific coordinate
     */
    suspend fun tapRepeat(x: Int, y: Int, times: Int = 10) {
        repeat(times) {
            TouchActions.tap(x, y)
            delayWithMultiplier(50)
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
    TouchActions.tap(1152, 102)
    delayWithMultiplier(300)
}

suspend fun builderBaseTrainWithConditions(): Boolean {
    val storageKey = "BuilderBaseTrainTroops${InGamesVars.currentAccountNumber}"
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