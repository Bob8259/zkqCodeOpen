package com.coc.zkqcode.jar.code.universal.remove

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.yolo.DetectionResult
import com.coc.zkqcode.jar.code.universal.yolo.tiledYoloDetect
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

suspend fun enterEditMode() {
    // Select the initial schema based on the game package version
    val initialSchema = if (InGamesVars.currentGameVersion == GameVersion.CN) {
        MyColors.CNEditBaseButton
    } else {
        MyColors.GlobalEditBaseButton
    }

    // Attempt to locate the initial edit button
    findMultiColorsUntil(schemas = listOf(initialSchema), duration = 1500)?.let {
        TouchActions.tap(it.x, it.y)
    } ?: return

    // Sequence of interactions to navigate through the edit menus
    // 1. Locate and click the specific Green Edit Button
    findMultiColorsUntil(schemas = listOf(MyColors.GreenEditBaseButton), duration = 1500)?.let {
        TouchActions.tap(it.x, it.y)
    }

    removeAllBuildings()
}

suspend fun removeAllBuildings() {
    findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes, MyColors.MiddleGreenConfirm), duration = 300)?.let { yesPoint ->
        TouchActions.tap(yesPoint.x, yesPoint.y)
    }
    // Locate "Remove All", confirm the action, and perform final layout taps
    findMultiColorsUntil(schemas = listOf(MyColors.EditModeRemoveAll, MyColors.EditModeRemoveAll2), duration = 1000)?.let {
        TouchActions.tap(it.x, it.y)

        // Re-confirm deletion
        findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 200)?.let { yesPoint ->
            TouchActions.tap(yesPoint.x, yesPoint.y)
        }

        // Post-action delays and fixed-coordinate taps to finalize state
        delayWithMultiplier(500)
        TouchActions.tap(1005, 265, delayTime = 500)
    }

}

suspend fun removeObstacles() {
    delayWithMultiplier(200)
    val obstacles = tiledYoloDetect(
        modelName = "remove-obstacle", callerTag = "BuilderBaseRemoveObstacles"
    )
    obstacles.forEach { obstacle ->
        val box = obstacle.boundingBox
        val centerX = box.centerX().toInt()
        val centerY = box.centerY().toInt()
        ShowMessage("x: $centerX, y: $centerY")
        TouchActions.tap(centerX, centerY, delayTime = 120)
        // Tap confirmation/action button
        TouchActions.tap(616, 488, delayTime = 100)
        repeat(2) {
            TouchActions.tap(14, 558, delayTime = 100)
        }
    }
}