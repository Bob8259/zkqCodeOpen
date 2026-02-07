package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import kotlin.math.abs

/**
 * Data class representing a detected building with its associated cost.
 */
data class BuildingWithCost(
    val name: String,
    val cost: String
)

// Regex to detect Chinese characters
private val chineseRegex = Regex("[\u4e00-\u9fff]")

// Regex to detect purely numeric text (may contain commas or spaces)
private val numericRegex = Regex("^[\\d,. ]+$")

/**
 * Detects building names and their corresponding costs from the screen.
 *
 * Scans the area (481, 101) to (900, 532) for Chinese text (building names)
 * and numbers (costs). Matches them by Y-axis proximity (±3 pixels in
 * absolute screen coordinates).
 *
 * @return A list of [BuildingWithCost] with matched building names and costs.
 */
suspend fun detectBuildingList(): List<BuildingWithCost> {
    val startX = 480
    val startY = 100
    val endX = 900
    val endY = 530

    // Recognize text in the specified area (positions are cropped-relative)
    val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = true)

    // Separate building names (Chinese text) and costs (numeric text)
    // Convert cropped-relative positions to absolute screen coordinates
    data class TextWithAbsoluteY(val text: String, val absoluteCenterY: Int)

    val buildingNames = mutableListOf<TextWithAbsoluteY>()
    val costs = mutableListOf<TextWithAbsoluteY>()

    for (item in results) {
        val rect = item.position ?: continue
        val absoluteCenterY = (rect.top + rect.bottom) / 2 + startY

        when {
            chineseRegex.containsMatchIn(item.text) -> {
                buildingNames.add(TextWithAbsoluteY(item.text, absoluteCenterY))
            }
            numericRegex.matches(item.text.trim()) -> {
                costs.add(TextWithAbsoluteY(item.text.trim(), absoluteCenterY))
            }
        }
    }

    // Match each building name to a cost by Y-axis proximity (±3 pixels)
    val yTolerance = 3
    return buildingNames.map { building ->
        val matchedCost = costs.firstOrNull { cost ->
            abs(building.absoluteCenterY - cost.absoluteCenterY) <= yTolerance
        }
        BuildingWithCost(
            name = building.text,
            cost = matchedCost?.text ?: "N/A"
        )
    }
}
