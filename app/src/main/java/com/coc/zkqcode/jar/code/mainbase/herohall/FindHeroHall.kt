package com.coc.zkqcode.jar.code.mainbase.herohall

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.yolo.DetectionResult
import com.coc.zkqcode.jar.code.universal.yolo.YoloDetector

// Detect hero halls on the main base screen using tiled YOLO inference.
// The 1280x720 screenshot is split into three overlapping 640x640 crops
// so the model's square input covers the full width, with overlap filtering
// to avoid counting the same building twice.
suspend fun findHeroHall(): List<DetectionResult> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        ?: logAndRestart("in FindHeroHall, screen capture failed.")

    if (screenBuffer.width != 1280 || screenBuffer.height != 720) {
        return emptyList()
    }

    // Early return if model weights fail to load
    if (!YoloDetector.loadWeights("building-detect")) return emptyList()

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
                val centerX = box.centerX()

                // Filter by tile region to avoid duplicates in overlapping areas
                var shouldKeep = false
                if (i == 0) {
                    // Left tile: keep detections with center x < 380
                    if (centerX < 380) shouldKeep = true
                } else if (i == 1) {
                    // Middle tile: keep detections with center x between 60 and 580
                    if (centerX > 60 && centerX < 580) shouldKeep = true
                } else {
                    // Right tile: keep detections with absolute x > 700
                    if ((startX + centerX) > 700) shouldKeep = true
                }

                if (shouldKeep) {
                    // Map crop-relative coordinates back to full-screen coordinates
                    box.offset(startX.toFloat(), 0f)
                    detections.add(detection)
                }
            }
        }

        val result = detections
            .filter { it.boundingBox.centerX() in 100f..1180f }
            .sortedByDescending { it.score }

        ShowMessage("找到 ${result.size} 个英雄殿堂")
        return result
    } finally {
        // Always clear model weights after detection to free memory
        YoloDetector.clearWeights()
    }
}