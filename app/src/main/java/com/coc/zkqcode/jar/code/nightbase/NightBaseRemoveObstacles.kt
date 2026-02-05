package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.core.yolo.DetectionResult
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import android.graphics.Bitmap
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.ShowMessage.invoke
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.RecognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.sqrt


suspend fun nightBaseRemoveObstacles(): Boolean {
    zoomSmallNightBase()
    swipe(587, 420, 587, 670)

//    val worker = NightBaseWorkerAndResearch.detectWorkerNumber()
//    if (worker.total == 2) {
//
//    }
    return enterMainScreen()
}

private suspend fun enterFirstArea() {
    val resources = RecognizeResources.recognizeMyResources()
    val storageKey = "NightBaseRemoveObstacles${InGamesVars.currentAccountNumber}"
    val lastCleaningTime = readMemory(storageKey).toIntOrNull()
    val calendar = Calendar.getInstance()
    val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    if (lastCleaningTime != null && abs(lastCleaningTime - hourOfDay) < 8) {
        ShowMessage("距离上次除草不足8小时，暂不除草")
        return
    }
    if (resources.gold < 400000 && resources.elixir < 400000) {
        ShowMessage("检测金：${resources.gold}，检测水：${resources.elixir}\n不足40万，暂不除草")
        return
    }
    ShowMessage("第一区域准备移除障碍物")
    zoomSmallNightBase()

}

private suspend fun enterEditMode(){
    if(InGamesVars.currentGamePackage==0){
        val point =
    }
}

private suspend fun removeObstacles() {
    val obstacles = detectObstacles()
    obstacles.forEach { obstacle ->
        val box = obstacle.boundingBox
        ShowMessage("x: ${box.centerX().toInt()}, y: ${box.centerY().toInt()}")
        TouchActions.tap(box.centerX().toInt(), box.centerY().toInt())
        delay(2000)
    }
}

private suspend fun detectObstacles(): List<DetectionResult> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        ?: logAndStop("in TextRecognizer, screen capture failed.")

    if (screenBuffer.width != 1280 || screenBuffer.height != 720) {
        return emptyList()
    }

    YoloDetector.loadWeights()

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