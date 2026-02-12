package com.coc.zkqcode.jar.code.nightbase

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.core.yolo.DetectionResult
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.RecognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sqrt


suspend fun nightBaseRemoveObstacles(): Boolean {
    val worker = NightBaseWorkerAndResearch.detectWorkerNumber()
    val resources = RecognizeResources.recognizeMyResources()
    val storageKey = "NightBaseRemoveObstacles${InGamesVars.currentAccountNumber}"
    val lastCleaningTime = readMemory(storageKey).toIntOrNull()
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    // Simplified time check as requested
//    if (lastCleaningTime != null && abs(lastCleaningTime - currentHour) < 8) {
//        ShowMessage("距离上次除草不足8小时，暂不除草")
//        return true
//    }

    // Resource threshold check
    if (worker.total == 2) {
        if (resources.gold < 600000 && resources.elixir < 600000) {
            ShowMessage("检测金：${resources.gold}，检测水：${resources.elixir}\n不足60万，暂不除草")
            return true
        }
    } else {
        if (resources.gold < 300000 && resources.elixir < 300000) {
            ShowMessage("检测金：${resources.gold}，检测水：${resources.elixir}\n不足30万，暂不除草")
            return true
        }
    }

    ShowMessage("第一区域准备移除障碍物")
    zoomSmallNightBase()
    enterEditMode()
    zoomSmallNightBase()
    // First Area Operations
    removeObstacles()
    swipe(1036, 78, 1100, 455, 700)
    removeObstacles()

    // Second Area Operations (conditional on worker count)
    if (worker.total == 2) {
        ShowMessage("当前已解锁第二区域")
        swipe(672, 159, 1206, 420, 700)
        TouchActions.tap(1228, 316)
        delayWithMultiplier(500)
        removeAllBuildings()
        removeObstacles()
        swipe(867, 163, 1211, 450, 700)
        removeObstacles()
    }

    // Update the storage with the current hour after completion
    writeMemory(storageKey, currentHour.toString())
    return enterMainScreen()
}


private suspend fun enterEditMode() {
    // Select the initial schema based on the game package version
    val initialSchema = if (InGamesVars.currentGamePackage == 0) {
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

    // 2. Locate and click the confirmation (Yes) button
    findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1000)?.let {
        TouchActions.tap(it.x, it.y)
    }
    removeAllBuildings()
}

private suspend fun removeAllBuildings() {
    // 3. Locate "Remove All", confirm the action, and perform final layout taps
    findMultiColorsUntil(schemas = listOf(MyColors.EditModeRemoveAll), duration = 1000)?.let {
        TouchActions.tap(it.x, it.y)

        // Re-confirm deletion
        findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1000)?.let { yesPoint ->
            TouchActions.tap(yesPoint.x, yesPoint.y)
        }

        // Post-action delays and fixed-coordinate taps to finalize state
        delayWithMultiplier(500)
        TouchActions.tap(1005, 265)
        delayWithMultiplier(500)
    }
}

private suspend fun removeObstacles() {
    delayWithMultiplier(200)
    val obstacles = detectObstacles()
    obstacles.forEach { obstacle ->
        val box = obstacle.boundingBox
        val centerX = box.centerX().toInt()
        val centerY = box.centerY().toInt()
        ShowMessage("x: $centerX, y: $centerY")
        TouchActions.tap(centerX, centerY)
        delayWithMultiplier(500)
        // Tap confirmation/action button
        TouchActions.tap(616, 488)
        delayWithMultiplier(100)
        repeat(2) {
            TouchActions.tap(14, 558)
            if (it == 0) delayWithMultiplier(100) else delayWithMultiplier(500)
        }
    }
}

private suspend fun detectObstacles(): List<DetectionResult> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        ?: logAndStop("in TextRecognizer, screen capture failed.")

    if (screenBuffer.width != 1280 || screenBuffer.height != 720) {
        return emptyList()
    }

    YoloDetector.loadWeights("remove-obstacle")

    val detections = mutableListOf<DetectionResult>()

    try {
        val parts = listOf(0, 320, 640)

        for (i in parts.indices) {
            val startX = parts[i]
            val crop = Bitmap.createBitmap(screenBuffer, startX, 0, 640, 640)

            // Don't clear weights for the first two parts
            val clearWeights = (i == parts.size - 1)
            val partDetections = YoloDetector.detect(crop, clearWeightsAfter = clearWeights)

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
    } catch (e: Exception) {
        YoloDetector.clearWeights()
        throw e
    }

    // Filter detections outside [100, 1180] x-range
    val initialFiltered = detections.filter { it.boundingBox.centerX() in 100f..1180f }

    // Filter close detections: if distance < 5 pixels, keep only the one with higher score
    val filteredDetections = mutableListOf<DetectionResult>()
    for (detection in initialFiltered) {
        var isTooClose = false
        val iterator = filteredDetections.listIterator()
        while (iterator.hasNext()) {
            val existing = iterator.next()

            val dx = detection.boundingBox.centerX() - existing.boundingBox.centerX()
            val dy = detection.boundingBox.centerY() - existing.boundingBox.centerY()
            val distance = sqrt((dx * dx + dy * dy).toDouble())

            if (distance < 5.0) {
                isTooClose = true
                if (detection.score > existing.score) {
                    iterator.remove()
                    iterator.add(detection)
                }
                break
            }
        }
        if (!isTooClose) {
            filteredDetections.add(detection)
        }
    }

    return filteredDetections
}