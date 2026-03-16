package com.coc.zkqcode.jar.code.universal.recognizer

import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo

/**
 * Data class representing the game's core resources.
 */
data class Resources(
    val gold: Int = 0,
    val elixir: Int = 0,
    val darkElixir: Int = 0
)

/**
 * Recognizes resources from the game screen.
 *
 * - When [isOpponent] is false (default), reads from the own-base area (1050, 26, 1245, 201).
 * - When [isOpponent] is true, reads from the opponent's area (50, 98, 293, 209).
 *
 * Resource identification logic:
 * - Smallest Y axis position is Gold.
 * - Next smallest is Elixir.
 * - Largest is Dark Elixir.
 *
 * @param isOpponent Whether to recognize the opponent's resources instead of own.
 * @return A [Resources] object containing the detected values.
 */
suspend fun recognizeResources(isOpponent: Boolean = false): Resources {
    // Select crop area based on target
    val (startX, startY, endX, endY) = if (isOpponent) {
        listOf(50, 98, 293, 209)
    } else {
        listOf(990, 20, 1270, 175)
    }
    val threshold = if (isOpponent) 180 else 240
    val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = true, threshold = threshold)

    // Sort by the top coordinate of the bounding box
    val sortedResults = results.sortedBy { it.position?.top ?: Int.MAX_VALUE }

    var gold = 0
    var elixir = 0
    var darkElixir = 0

    sortedResults.forEachIndexed { index, recognizedText ->
        val cleanValue = extractValue(recognizedText.text)
        when (index) {
            0 -> gold = cleanValue
            1 -> elixir = cleanValue
            2 -> darkElixir = cleanValue
        }
    }

    return Resources(gold, elixir, darkElixir)
}

/**
 * Cleans the recognized text and extracts an Int value.
 * Removes spaces, commas, and any non-numeric characters.
 */
private fun extractValue(text: String): Int {
    // Remove all non-digit characters and handle common misrecognitions
    val digitsOnly = text.replace("G", "6")
        .replace("o", "0").replace("O", "0")
        .replace("s", "5").replace("S", "5")
        .replace("z", "2").replace("Z", "2")
        .replace("I", "1").replace("l", "1")
        .replace(Regex("[^0-9]"), "")
    return digitsOnly.toIntOrNull() ?: 0
}
