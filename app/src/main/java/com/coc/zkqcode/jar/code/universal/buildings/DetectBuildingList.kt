package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

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
    val startX = 480
    val startY = 100
    val endX = 900
    val endY = 560

    // Recognize text in the specified area
    val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = true)

    // Filter, clean, and return only building names (Chinese text)
    return results
        .filter { item -> item.position != null && chineseRegex.containsMatchIn(item.text) }
        .map { cleanBuildingName(it.text) }
}

// Characters commonly misread by OCR that should be replaced with "新"
private val ocrMisreadPrefixes = listOf("斬", "靳", "鼾")

/**
 * Cleans a raw building name:
 * - Removes all spaces
 * - Replaces OCR-misread prefixes ("斬", "靳", "鼾") with "新"
 * - Replaces OCR-misread "减" with "城"
 * - Strips trailing "x" / "X" followed by digits (e.g. "储金罐x2" → "储金罐")
 */
private fun cleanBuildingName(raw: String): String {
    var name = raw.replace(" ", "")
    for (char in ocrMisreadPrefixes) {
        if (name.startsWith(char)) {
            name = "新" + name.removePrefix(char)
            break
        }
    }
    name = name.replace("减", "城")
    name = trailingCountRegex.replace(name, "")
    return name
}
