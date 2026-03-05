package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

/**
 * Detects building names from the screen.
 *
 * Scans the area (480, 100) to (900, 530) for Chinese text (building names).
 *
 * @return A list of [DetectedBuilding] objects.
 */
suspend fun detectBuildingList(): BuildingDetectionResult {
    val startX = 400
    val startY = 100
    val endX = 900
    val endY = 560

    // Recognize text in the specified area
    val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = true, threshold = 160)

    if (results.isEmpty()) return BuildingDetectionResult(emptyList())

    // Show raw detected results for debugging
    val rawSummary = results.joinToString(separator = " | ") { item ->
        val pos = item.position
        if (pos == null) {
            item.text
        } else {
            val x = pos.left + startX
            val y = pos.top + startY
            "${item.text}($x,$y)"
        }
    }
    ShowMessage("Building raw list $rawSummary")

    // Take a screenshot for color checking (needed for post-process and later filtering)
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: logAndStop("failed to take screenshot at building detection")

    // Post-process: Check for green indicator (new building marker)
    // If green pixels > 40 in area (x-15, y-3, x, y+20), add "新" prefix
    val processedResults = results.map { item ->
        val pos = item.position ?: return@map item
        val x = pos.left + startX
        val y = pos.top + startY

        val greenCount = countGreenPixelsInArea(screenBuffer, x - 15, y - 3, x, y + 20)
        if (greenCount > 30 && !item.text.startsWith("新")) {
            item.copy(text = "新${item.text}")
        } else {
            item
        }
    }

    // If "建议升级" is detected, only keep results below it (greater y)
    val upgradeY = processedResults.mapNotNull { item ->
        val pos = item.position ?: return@mapNotNull null
        val cleaned = cleanBuildingName(item.text)
        if (cleaned == "建议升级") {
            pos.top + startY
        } else null
    }.minOrNull()

    // Filter, clean, and return building names with positions
    // For each detected text, exclude it if more than 10 pixels of FF887F (RGB) are found in the specified area
    // Map results to DetectedBuilding and a flag indicating if it was marked as "New"
    val allBuildings = processedResults.mapNotNull { item ->
        val pos = item.position ?: return@mapNotNull null
        if (!chineseRegex.containsMatchIn(item.text)) return@mapNotNull null

        // Absolute position to the screen
        val x = pos.left + startX
        val y = pos.top + startY
        if (upgradeY != null && y <= upgradeY) return@mapNotNull null

        val count = countPixelsInArea(screenBuffer, x + 200, y - 20, x + 430, y + 10, 0xFF887F)
        if (count > 10) return@mapNotNull null

        val cleanedName = cleanBuildingName(item.text)
        val isNew = cleanedName.startsWith("新")

        if (isNew || cleanedName in ALL_BUILDINGS) {
            DetectedBuilding(name = cleanedName, x = x, y = y) to isNew
        } else {
            null
        }
    }

    // Filter to get only the buildings marked as "New"
    val newBuildings = allBuildings.filter { it.second }.map { it.first }

    // If any "New" building is detected, return only those
    if (newBuildings.isNotEmpty()) {
        return BuildingDetectionResult(newBuildings)
    }

    return BuildingDetectionResult(allBuildings.map { it.first })
}

/**
 * Iterates through the building upgrade list in Builder Base.
 * Performs swipes and calls [onDetect] for each detection result.
 * If [onDetect] returns true, the iteration stops immediately.
 */
suspend fun iterateBuilderBaseBuildingUpgradeList(onDetect: suspend (BuildingDetectionResult) -> Boolean) {
    var previousBuildingNames: List<String>? = null
    loop@ for (i in 1..12) {
        val result = detectBuildingList()
        if (onDetect(result)) return

        val currentBuildingNames = result.buildings.map { it.name }.sorted()
        if (previousBuildingNames != null && currentBuildingNames == previousBuildingNames) {
            repeat(2) {
                TouchActions.swipe(666, 170, 666, 300, delayTime = 600)
                delayWithMultiplier(200)
                val extraResult = detectBuildingList()
                if (onDetect(extraResult)) return
            }
            break@loop
        }
        previousBuildingNames = currentBuildingNames
        TouchActions.swipe(666, 500, 666, 120, delayTime = 600)
        delayWithMultiplier(200)
    }
}
