package com.coc.zkqcode.jar.code.universal.yolo

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart

/**
 * Performs tiled YOLO detection on a 1280x720 screenshot.
 * The image is split into three overlapping 640x640 crops so the model's
 * square input covers the full width, with overlap filtering to avoid
 * counting the same object twice.
 *
 * @param modelName    weight file to load (e.g. "remove-obstacle", "building-detect")
 * @param callerTag    context string for error messages on capture failure
 * @param classIndex   if non-null, only keep detections matching this class
 * @param threshold    confidence threshold forwarded to YoloDetector.detect()
 * @param distanceThreshold  distance threshold forwarded to YoloDetector.detect()
 * @param xRange       final x-center filter range (default 100f..1180f)
 * @return list of DetectionResult; sorted by score descending when classIndex is specified
 */
suspend fun tiledYoloDetect(
    modelName: String,
    callerTag: String,
    classIndex: Int? = null,
    threshold: Float = 0.3f,
    distanceThreshold: Double = 5.0,
    xRange: ClosedFloatingPointRange<Float> = 100f..1180f
): List<DetectionResult> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        ?: logAndRestart("in $callerTag, screen capture failed.")

    if (screenBuffer.width != 1280 || screenBuffer.height != 720) {
        return emptyList()
    }

    // Early return if model weights fail to load
    if (!YoloDetector.loadWeights(modelName)) return emptyList()

    val detections = mutableListOf<DetectionResult>()
    val parts = listOf(0, 320, 640)

    try {
        for (i in parts.indices) {
            val startX = parts[i]
            val crop = Bitmap.createBitmap(screenBuffer, startX, 0, 640, 640)

            val partDetections = YoloDetector.detect(
                crop,
                clearWeightsAfter = false,
                threshold = threshold,
                distanceThreshold = distanceThreshold
            )

            for (detection in partDetections) {
                val box = detection.boundingBox
                val centerX = box.centerX()

                // Filter by tile region to avoid duplicates in overlapping areas
                var shouldKeep = false
                if (i == 0) {
                    if (centerX < 380) shouldKeep = true
                } else if (i == 1) {
                    if (centerX > 60 && centerX < 580) shouldKeep = true
                } else {
                    // Right tile: use absolute x position
                    if ((startX + centerX) > 700) shouldKeep = true
                }

                if (shouldKeep) {
                    // Map crop-relative coordinates back to full-screen coordinates
                    box.offset(startX.toFloat(), 0f)
                    detections.add(detection)
                }
            }
        }

        // Apply x-range filter, optional class filter, and optional sorting
        var result = detections.filter { it.boundingBox.centerX() in xRange }
        if (classIndex != null) {
            result = result
                .filter { it.classIndex == classIndex }
                .sortedByDescending { it.score }
        }
        return result
    } finally {
        // Always clear model weights after detection to free memory
        YoloDetector.clearWeights()
    }
}
