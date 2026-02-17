package com.coc.zkqcode.jar.code.builderbase.attack

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterBuilderBase
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun builderBaseAttack(): Boolean {
    if (!getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key)) {
        ShowMessage("未开启打夜世界")
        return true
    }
    val goldPosition = findMultiColors(schema = MyColors.BuilderBaseGold)
    val exilePosition = findMultiColors(schema = MyColors.BuilderBaseExiler)
    val isResourcesFull = goldPosition != null && exilePosition != null && goldPosition.x < 1015 && exilePosition.x < 1015
    if (isResourcesFull && getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.key)) {
        ShowMessage("资源已满，停止对战")
    } else {
        if (getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.TROPHY_PUSHING_MODE.key))

            if (goldPosition != null && goldPosition.x < 1015) {
                realAttack("gold")
            } else {
                realAttack("exile")
            }
    }
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

