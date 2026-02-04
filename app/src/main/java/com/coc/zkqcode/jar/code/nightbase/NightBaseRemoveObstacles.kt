package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.core.yolo.DetectionResult
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import android.graphics.Bitmap
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop


suspend fun nightBaseRemoveObstacles() {
    zoomSmallNightBase()
    swipe(587, 420, 587, 670)
    val worker = NightBaseWorkerAndResearch.detectWorkerNumber()
    if (worker.total == 2) {

    }

}

suspend fun removeObstacles(): List<DetectionResult> {
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

    return detections
}