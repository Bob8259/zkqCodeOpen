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
 * Object for recognizing and extracting resources from the game screen.
 */
object RecognizeResources {

    /**
     * Recognizes resources in the specified crop area (1050, 26, 1245, 201).
     *
     * Resource identification logic:
     * - Smallest Y axis position is Gold.
     * - Next smallest is Elixir.
     * - Largest is Dark Elixir.
     *
     * @return A [Resources] object containing the detected values.
     */
    suspend fun recognizeMyResources(): Resources {
        val startX = 1050
        val startY = 26
        val endX = 1245
        val endY = 201

        val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = false)

        // Sort by the top coordinate of the bounding box
        val sortedResults = results.sortedBy { it.position?.top ?: Int.MAX_VALUE }

        var gold = 0
        var elixir = 0
        var darkElixir = 0

        sortedResults.forEachIndexed { index, recognizedText ->
            val cleanValue = extractValue(recognizedText.text)
            showDebugInfo("Resource Index $index: Raw='${recognizedText.text}', Clean='$cleanValue', Top=${recognizedText.position?.top}")

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
}