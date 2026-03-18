package com.coc.zkqcode.jar.code.universal.recognizer

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
        BaseType.Main -> listOf(800, 620, 950, 655)
        BaseType.Builder -> listOf(540, 595, 695, 665)
    }

    val results = TextRecognizer.recognize(
        startX = startX, startY = startY,
        endX = endX, endY = endY,
        useChinese = false,
        applyPreprocess = true,
        threshold = 230,
    )

    // Join all recognized text lines and extract the numeric value
    val combinedText = results.joinToString("") { it.text }
    return extractValue(combinedText)
}
