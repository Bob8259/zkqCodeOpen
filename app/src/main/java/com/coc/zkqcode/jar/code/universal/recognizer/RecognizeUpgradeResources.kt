package com.coc.zkqcode.jar.code.universal.recognizer

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.universal.aidetection.AiDetector
import com.coc.zkqcode.jar.code.universal.buildings.BaseType

/**
 * Recognizes the upgrade cost number from the upgrade dialog on screen.
 *
 * - Main base: crops region (800, 620, 950, 655).
 * - Builder base: crops region (540, 595, 695, 665).
 *
 * @param currentBase The current base type, determines the detection area.
 * @return The recognized upgrade cost as an Int, or 0 if recognition fails.
 */
suspend fun recognizeUpgradeResources(currentBase: BaseType): Int {
    // Select detection area based on base type
    val (startX, startY, endX, endY) = when (currentBase) {
        BaseType.Main -> listOf(750, 620, 950, 655)
        BaseType.Builder -> listOf(540, 595, 695, 665)
    }
    val width = endX - startX
    val height = endY - startY

    // Capture the screen first so the number can be recognized from the cropped bitmap.
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap ?: return 0

    // Return 0 when the crop area is invalid or outside the current screen bounds.
    if (startX < 0 || startY < 0 || endX > screenBuffer.width || endY > screenBuffer.height) {
        return 0
    }

    val cropBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)

    // Return 0 when the AI model cannot be prepared for numeric detection.
    if (!AiDetector.loadWeights("numbers")) return 0
    try {
        val detections = AiDetector.detect(
            bitmap = cropBitmap,
            clearWeightsAfter = false,
            threshold = 0.3f
        )

        // Return 0 explicitly when no digits are detected in the target area.
        if (detections.isEmpty()) return 0

        // Sort detections from left to right before combining them into a number string.
        val result = detections.sortedBy { it.boundingBox.centerX() }
            .joinToString("") { detection -> detection.classIndex.toString() }

        return result.toIntOrNull() ?: 0
    } finally {
        // Always clear the model weights after detection to avoid leaking resources.
        AiDetector.clearWeights()
    }
}
