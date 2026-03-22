package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.yolo.YoloDetector
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

/**
 * Detects the instant build cost by recognizing numbers near the upgrade gem icon.
 * Uses YOLO model to detect individual digits and combines them into the final cost value.
 *
 * @return The detected cost as an integer, or null if detection fails
 */
suspend fun detectInstantBuildCost(): Int? {
    // Find the upgrade gem icon to locate the cost text area
    val upgradeGemIcon = findMultiColorsUntil(
        schemas = listOf(MyColors.UpgradeGemIcon, MyColors.UpgradeGemIcon2, MyColors.UpgradeGemIcon3),
        duration = 500
    ) ?: return null

    // Define the region of interest around the gem icon where the cost number is displayed
    val startX = upgradeGemIcon.x - 80
    val startY = upgradeGemIcon.y - 45
    val endX = upgradeGemIcon.x + 72
    val endY = upgradeGemIcon.y
    val width = endX - startX
    val height = endY - startY

    delayWithMultiplier(100)

    // Capture the screen and extract the region containing the cost number
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
        ?: return null

    // Validate that the crop region is within screen bounds
    if (startX < 0 || startY < 0 || endX > screenBuffer.width || endY > screenBuffer.height) {
        return null
    }

    val cropBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)

    // Load model and run detection via remote YOLO service; abort if loading fails
    if (!YoloDetector.loadWeights("numbers")) return null
    try {
        val detections = YoloDetector.detect(
            bitmap = cropBitmap,
            clearWeightsAfter = false,
            threshold = 0.3f
        )

        // Return fallback value if no digits were detected
        if (detections.isEmpty()) {
            return 9999
        }

        // Sort detections from left to right to maintain correct digit order
        val sortedDetections = detections.sortedBy { it.boundingBox.centerX() }

        // Combine detected digits into the final number
        val result = sortedDetections.joinToString("") { detection ->
            detection.classIndex.toString()
        }

        return result.toIntOrNull()
    } finally {
        // Always clear model weights after detection to free memory
        YoloDetector.clearWeights()
    }
}