package com.coc.zkqcode.jar.code.universal.buildings

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.recognizer.RecognizedText
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import java.io.File
import java.io.FileOutputStream

/**
 * Detects building names from the screen.
 *
 * Scans the area (480, 100) to (900, 530) for Chinese text (building names).
 *
 * @param excludeNewBuildings If true, new buildings (新-prefixed) are excluded from the result.
 * @param excludeGearUpBuildings If true, gear-up buildings (改装-prefixed) are excluded; if false, returns only the first gear-up building.
 * @return A list of [DetectedBuilding] objects.
 */
suspend fun detectBuildingList(baseType: BaseType, excludeNewBuildings: Boolean = false, excludeGearUpBuildings: Boolean = true): BuildingDetectionResult {
    val candidateBuildings = getBuildingNames(baseType)
    val startX = 400
    val startY = 100
    val endX = 900
    val endY = 560

    // Recognize text in the specified area after removing the bright green marker noise.
    val results = recognizeBuildingListWithGreenMask(startX, startY, endX, endY, threshold = 130)

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
    // Keep the debug message short so it stays readable in logs.
    accountMessage("原始检测结果：\n${rawSummary.take(150)}")
    // Take a screenshot for colour checking (needed for post-process and later filtering)
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at building detection")

    // Post-process: Check for green indicator (new building marker) and gear-up icon
    // If green pixels > 20 in area (x-15, y-3, x, y+20), add "新" prefix
    // If gear-up icon detected in area (x-55, y-10, x+55, y+25), add "改装" prefix
    val shouldPurchaseBotoShack = baseType != BaseType.Builder || getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_PURCHASE_BOTO_SHACK.key)
    val processedResults = results.mapNotNull { item ->
        val pos = item.position ?: return@mapNotNull item
        val x = pos.left + startX
        val y = pos.top + startY

        var text = item.text

        // Check for green "new building" indicator
        val greenCount = countGreenPixelsInArea(screenBuffer, x - 15, y - 3, x, y + 20)
        if (greenCount > 20 && !text.startsWith("新")) {
            text = "新$text"
        }

        if (!shouldPurchaseBotoShack) {
            val gemSchema = ColorSchema.rescope(MyColors.GemInBuildingList, x + 155, y - 3, x + 250, y + 30)
            val gemDetected = findMultiColors(schema = gemSchema, byteBuffer = screenBuffer, increment = 1) != null
            if (gemDetected) return@mapNotNull null
        }

        // Check for gear-up icon
        val gearUpSchema = ColorSchema.rescope(MyColors.UpgradeListGearUpIcon, x - 55, y - 10, x + 55, y + 25)
        val gearUpDetected = findMultiColors(schema = gearUpSchema, byteBuffer = screenBuffer, increment = 1) != null
        if (gearUpDetected && !text.startsWith("改装")) {
            text = "改装$text"
        }

        item.copy(text = text)
    }
    // If "建议升级" is detected, only keep results below it (greater y)
    val upgradeY = processedResults.mapNotNull { item ->
        val pos = item.position ?: return@mapNotNull null
        val cleaned = cleanBuildingName(item.text, candidateBuildings)
        if (cleaned == "建议升级") {
            pos.top + startY
        } else null
    }.minOrNull()

    // Filter, clean, and return building names with positions
    // Exclude buildings with insufficient resources: if more than 10 pixels of FF887F (RGB, the red colour
    // used by the game for unaffordable upgrade costs) are found to the right of the building name, skip it.
    // Map results to DetectedBuilding with flags indicating if it was marked as "New" or "GearUp"
    data class BuildingWithFlags(val building: DetectedBuilding, val isNew: Boolean, val isGearUp: Boolean)

    val allBuildings = processedResults.mapNotNull { item ->
        val pos = item.position ?: return@mapNotNull null
        if (!chineseRegex.containsMatchIn(item.text)) return@mapNotNull null

        // Absolute position to the screen
        val x = pos.left + startX
        val y = pos.top + startY
        if (upgradeY != null && y <= upgradeY) return@mapNotNull null

        val count = countPixelsInArea(screenBuffer, x + 200, y - 20, x + 430, y + 10, 0xFF887F)
        if (count > 10) return@mapNotNull null

        val cleanedName = cleanBuildingName(item.text, candidateBuildings)
        val isNew = cleanedName.startsWith("新")
        val isGearUp = cleanedName.startsWith("改装")

        if (isNew || isGearUp || cleanedName in candidateBuildings) {
            BuildingWithFlags(DetectedBuilding(name = cleanedName, x = x, y = y), isNew, isGearUp)
        } else {
            null
        }
    }

    // Filter based on excludeGearUpBuildings parameter
    // If excludeGearUpBuildings is false, return only the first gear-up building
    if (!excludeGearUpBuildings) {
        val gearUpBuilding = allBuildings.firstOrNull { it.isGearUp }
        if (gearUpBuilding != null) {
            return BuildingDetectionResult(listOf(gearUpBuilding.building))
        }
    }

    // Filter out gear-up buildings if excludeGearUpBuildings is true
    val filteredBuildings = if (excludeGearUpBuildings) {
        allBuildings.filter { !it.isGearUp }
    } else {
        allBuildings
    }

    // When excludeNewBuildings is true, filter out new buildings and return only existing ones
    if (excludeNewBuildings) {
        val existingBuildings = filteredBuildings.filter { !it.isNew }.map { it.building }
        return BuildingDetectionResult(existingBuildings)
    }

    // Filter to get only the buildings marked as "New"
    val newBuildings = filteredBuildings.filter { it.isNew }.map { it.building }

    // If any "New" building is detected, return only those
    if (newBuildings.isNotEmpty()) {
        return BuildingDetectionResult(newBuildings)
    }

    return BuildingDetectionResult(filteredBuildings.map { it.building })
}

/**
 * Crops the building list area, turns bright green pixels into white, then sends it to TextRecognizer.
 */
private suspend fun recognizeBuildingListWithGreenMask(
    startX: Int,
    startY: Int,
    endX: Int,
    endY: Int,
    threshold: Int,
    saveImage: Boolean = false,
): List<RecognizedText> {
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap ?: logAndRestart("in building list recognition, screen capture failed.")
    val width = endX - startX
    val height = endY - startY

    if (width <= 0 || height <= 0) {
        logAndRestart("Invalid building list crop area: width=$width, height=$height")
    }

    waitForPlay()

    if (startX + width > screenBuffer.width || startY + height > screenBuffer.height) {
        return emptyList()
    }

    val cropped = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)
    val masked = maskBrightGreenPixelsToBlack(cropped)
    if (masked !== cropped) {
        cropped.recycle()
    }

    if (saveImage) {
        ScreenCaptureManager.getContext()?.let { context ->
            runCatching {
                val file = File(context.filesDir, "test_masked.png")
                FileOutputStream(file).use { out ->
                    masked.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                showDebugInfo("Masked image saved to: ${file.absolutePath}")
            }.onFailure { exception ->
                showDebugInfo("Failed to save masked image: ${exception.message}")
            }
        }
    }

    return try {
        TextRecognizer.recognize(
            0,
            0,
            width,
            height,
            useChinese = true,
            threshold = threshold,
            saveImage = saveImage,
            screenBuffer = masked,
        )
    } finally {
        masked.recycle()
    }
}

/**
 * Replaces pixels matching R < 30, G > 250, B < 30 with white pixels.
 */
private fun maskBrightGreenPixelsToBlack(src: Bitmap): Bitmap {
    val width = src.width
    val height = src.height
    val out = src.copy(Bitmap.Config.ARGB_8888, true) ?: return src
    val pixels = IntArray(width * height)
    out.getPixels(pixels, 0, width, 0, 0, width, height)
    val black = 0xFF000000.toInt()

    for (i in pixels.indices) {
        val color = pixels[i]
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        if (r < 30 && g > 170 && b < 30) {
            pixels[i] = black
        }
    }

    out.setPixels(pixels, 0, width, 0, 0, width, height)
    return out
}

/**
 * Iterates through the building upgrade list in Builder Base.
 * Performs swipes and calls [onDetect] for each detection result.
 * If [onDetect] returns true, the iteration stops immediately.
 */
suspend fun iterateBuilderBaseBuildingUpgradeList(
    baseType: BaseType, excludeNewBuildings: Boolean = false, excludeGearUpBuildings: Boolean = true, forceReverse: Boolean? = null, onDetect: suspend (BuildingDetectionResult) -> Boolean
) {
    var previousBuildingNames: List<String>? = null
    // forceReverse overrides the config-based decision (used by wall upgrades:
    // gold walls always reverse, elixir walls never reverse).
    val isReverseUpgrade = forceReverse ?: getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.REVERSE_UPGRADE.key)
    if (isReverseUpgrade) {
        TouchActions.swipe(666, 500, 666, -2000, delayTime = 200, delayAfter = 300)
    }
    loop@ for (i in 1..14) {
        val result = detectBuildingList(baseType, excludeNewBuildings, excludeGearUpBuildings)
        if (onDetect(result)) return

        val currentBuildingNames = result.buildings.map { it.name }.sorted()
        if (previousBuildingNames != null && currentBuildingNames == previousBuildingNames) {
            repeat(2) {
                if (isReverseUpgrade) {
                    TouchActions.swipe(666, 500, 666, 370, delayTime = 200, delayAfter = 400)
                } else {
                    TouchActions.swipe(666, 170, 666, 300, delayTime = 200, delayAfter = 400)
                }
                val extraResult = detectBuildingList(baseType, excludeNewBuildings, excludeGearUpBuildings)
                if (onDetect(extraResult)) return
            }
            break@loop
        }
        previousBuildingNames = currentBuildingNames
        if (isReverseUpgrade) {
            TouchActions.swipe(666, 170, 666, 550, delayTime = 200, delayAfter = 400)
        } else {
            TouchActions.swipe(666, 500, 666, 120, delayTime = 200, delayAfter = 400)
        }

    }
}
