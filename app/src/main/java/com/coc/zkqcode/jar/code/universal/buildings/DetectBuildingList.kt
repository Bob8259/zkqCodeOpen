package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlin.math.max
import kotlin.math.min

// Regex to detect Chinese characters
private val chineseRegex = Regex("[\u4e00-\u9fff]")

// Regex to match "x" (or "X") followed by digits at the end, e.g. "储金罐x2"
private val trailingCountRegex = Regex("[xX]\\d+$")

/**
 * Detects building names from the screen.
 *
 * Scans the area (480, 100) to (900, 530) for Chinese text (building names).
 *
 * @return A list of building name strings.
 */
suspend fun detectBuildingList(): List<String> {
    val startX = 400
    val startY = 100
    val endX = 900
    val endY = 560

    // Recognize text in the specified area
    val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = true)

    if (results.isEmpty()) return emptyList()

    // Take a screenshot for color checking
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: logAndStop("failed to take screenshot at night base upgrade")

    // Filter, clean, and return only building names (Chinese text)
    // For each detected text, exclude it if more than 10 pixels of FF887F are found in the specified area
    return results
        .filter { item ->
            val pos = item.position ?: return@filter false
            if (!chineseRegex.containsMatchIn(item.text)) return@filter false

            // Absolute position to the screen
            val x = pos.left + startX
            val y = pos.top + startY

            val count = countPixelsInArea(screenBuffer, x + 200, y - 20, x + 430, y + 10, 0xFF887F)
            count <= 10
        }
        .map { cleanBuildingName(it.text) }
}

/**
 * Counts pixels of a target color within a specified rectangle in a capture result.
 */
private fun countPixelsInArea(
    result: ScreenCaptureManager.CaptureResult,
    x1: Int, y1: Int, x2: Int, y2: Int,
    targetColor: Int
): Int {
    val buf = result.buffer
    val width = result.width
    val height = result.height
    val pixelStride = result.pixelStride
    val rowStride = result.rowStride

    val left = max(0, min(x1, x2))
    val right = min(width - 1, max(x1, x2))
    val top = max(0, min(y1, y2))
    val bottom = min(height - 1, max(y1, y2))

    val targetR = (targetColor shr 16) and 0xFF
    val targetG = (targetColor shr 8) and 0xFF
    val targetB = targetColor and 0xFF

    var count = 0
    for (y in top..bottom) {
        val rowStart = y * rowStride
        for (x in left..right) {
            val offset = rowStart + x * pixelStride
            if (offset + 2 >= buf.capacity()) continue

            // RGBA_8888 format
            val r = buf.get(offset).toInt() and 0xFF
            val g = buf.get(offset + 1).toInt() and 0xFF
            val b = buf.get(offset + 2).toInt() and 0xFF

            if (r == targetR && g == targetG && b == targetB) {
                count++
            }
        }
    }
    return count
}

// Characters commonly misread by OCR that should be replaced with "新"
private val ocrMisreadPrefixes = listOf("斬", "靳", "鼾")

/**
 * Cleans a raw building name:
 * - Removes all spaces
 * - Replaces OCR-misread prefixes ("斬", "靳", "鼾") with "新"
 * - Replaces OCR-misread "减墙" with "城墙"
 * - Strips trailing "x" / "X" followed by digits (e.g. "储金罐x2" → "储金罐")
 */
private fun cleanBuildingName(raw: String): String {
    var name = raw.replace(" ", "").replace("|", "")
    for (char in ocrMisreadPrefixes) {
        if (name.startsWith(char)) {
            name = "新" + name.removePrefix(char)
            break
        }
    }
    name = name.replace("减墙", "城墙")
    name = name.replace("部落械堡", "部落城堡")

    name = trailingCountRegex.replace(name, "")
    return name
}
