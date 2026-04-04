package com.coc.zkqcode.jar.code.universal.remove

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.yolo.DetectionResult
import com.coc.zkqcode.jar.code.universal.yolo.YoloDetector
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
    val obstacles = detectObstacles()
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

suspend fun detectObstacles(): List<DetectionResult> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        ?: logAndRestart("in BuilderBaseRemoveObstacles, screen capture failed.")

    if (screenBuffer.width != 1280 || screenBuffer.height != 720) {
        return emptyList()
    }

    // Early return if model weights fail to load
    if (!YoloDetector.loadWeights("remove-obstacle")) return emptyList()

    val detections = mutableListOf<DetectionResult>()
    val parts = listOf(0, 320, 640)

    try {
        for (i in parts.indices) {
            val startX = parts[i]
            val crop = Bitmap.createBitmap(screenBuffer, startX, 0, 640, 640)

            val partDetections = YoloDetector.detect(
                crop,
                clearWeightsAfter = false
            )

            for (detection in partDetections) {
                val box = detection.boundingBox
                val centerX = box.centerX() // Relative to crop

                var shouldKeep = false
                if (i == 0) {
                    // Part 1: x < 380
                    if (centerX < 380) shouldKeep = true
                } else if (i == 1) {
                    // Part 2: x between 60 and 580
                    if (centerX > 60 && centerX < 580) shouldKeep = true
                } else {
                    // Part 3: x position greater then 700 (Absolute)
                    // Absolute X = startX + centerX = 640 + centerX
                    if ((startX + centerX) > 700) shouldKeep = true
                }

                if (shouldKeep) {
                    // Map to original coordinates
                    box.offset(startX.toFloat(), 0f)
                    detections.add(detection)
                }
            }
        }

        // Filter detections outside [100, 1180] x-range
        return detections.filter { it.boundingBox.centerX() in 100f..1180f }
    } finally {
        // Always clear model weights after detection to free memory
        YoloDetector.clearWeights()
    }
}
