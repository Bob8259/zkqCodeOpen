package com.coc.zkqcode.jar.code.universal.buildings

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.recognizer.RecognizedText
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.recognizer.cleanOcrDigits
import java.io.File
import java.io.FileOutputStream

/**
 * Data class representing the worker information (available/total).
 */
data class WorkerInfo(val available: Int, val total: Int)

enum class BaseType {
    Builder, Main
}

fun mainBaseBuildingUpgradeEntrySchemas() = if (InGameVars.currentGameVersion == GameVersion.GLOBAL) {
    listOf(MyColors.MainBaseBuildingUpgradeEntryGlobal)
} else {
    MyColors.MainBaseWorkerSchemas
}

suspend fun findMainBaseBuildingUpgradeEntry() = findMultiColorsUntil(
    schemas = mainBaseBuildingUpgradeEntrySchemas(), duration = 1000
)

/**
 * Unified worker and research detection logic for both Main Base and Builder Base.
 * Dispatches on [BaseType] to handle base-specific differences (colour schemas, goblin checks, messages).
 */
object WorkerAndResearch {

    /**
     * Detects the number of workers for the given base type.
     * @return A [WorkerInfo] object. If detection fails, returns (0, 0).
     */
    suspend fun detectWorkerNumber(baseType: BaseType): WorkerInfo {
        // Must double-check to wait for a little bit, otherwise the detection will fail.
        if (InGameVars.currentGameVersion == GameVersion.GLOBAL) {
            val startX = 1186
            val startY = 531
            val endX = 1247
            val endY = 572

            val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)
            val cropWidth = endX - startX
            val cropHeight = endY - startY
            if (startX + cropWidth <= screenBuffer.width && startY + cropHeight <= screenBuffer.height) {
                val croppedBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, cropWidth, cropHeight)
                val colorFiltered = TextRecognizer.applyColorFilter(
                    croppedBitmap,
                    minRedChannelValue = 100,
                    minGreenChannelValue = 170,
                    minBlueChannelValue = 70
                )
                val preprocessed = TextRecognizer.preprocess(colorFiltered, threshold = 235, invertBinarization = true)

                ScreenCaptureManager.getContext()?.let { context ->
                    try {
                        val file = File(context.filesDir, "test.png")
                        FileOutputStream(file).use { out ->
                            preprocessed.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }
                        showDebugInfo("Image saved to: ${file.absolutePath}")
                    } catch (e: Exception) {
                        showDebugInfo("Failed to save image: ${e.message}")
                    }
                }

                val oneSchema = ColorSchema.rescope(MyColors.BinaryOne3, 0, 0, cropWidth, cropHeight)
                if (findMultiColors(bitmap = preprocessed, schema = oneSchema) != null) {
                    return WorkerInfo(1, 2)
                }
            }

            val results = TextRecognizer.recognize(
                startX = startX,
                startY = startY,
                endX = endX,
                endY = endY,
                threshold = 235,
                useChinese = false,
                saveImage = true,
                applyPreprocess = true
            )

            val combinedText = results.sortedBy { it.position?.left ?: 0 }.joinToString("") { it.text }

            return parseWorkerInfo(combinedText)
        }
        if (baseType == BaseType.Main && (findMultiColors(schema = MyColors.GoblinWorker) != null || findMultiColors(schema = MyColors.GoblinWorker2) != null)) {
            return WorkerInfo(0, 6)
        }

        val workerSchemas = when (baseType) {
            BaseType.Builder -> listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2, MyColors.BuilderBaseWorker3)
            BaseType.Main -> MyColors.MainBaseWorkerSchemas
        }

        val worker = findMultiColorsUntil(schemas = workerSchemas, duration = 200)
        if (worker != null) {
            // Define the crop region for the worker number text
            val startX = worker.x - 50
            val startY = 0
            val endX = worker.x + 250
            val endY = 70

            val keepLeft = worker.x + 30 - startX
            val keepTop = worker.y - 15 - startY
            val keepRight = worker.x + 100 - startX
            val keepBottom = worker.y + 24 - startY

            val results = recognizeWorkerNumberWithMask(
                startX, startY, endX, endY, keepLeft, keepTop, keepRight, keepBottom
            )

            // Sort by x-axis position to ensure left-to-right reading order
            val combinedText = results.sortedBy { it.position?.left ?: 0 }.joinToString("") { it.text }
            val workerInfo = parseWorkerInfo(combinedText)
            if (workerInfo != WorkerInfo(0, 0)) {
                return workerInfo
            }
            // Fallback: OCR failed, use colour-based pattern matching on the preprocessed bitmap
            return detectWorkerByColorMatch(startX, startY, endX, endY)
        }
        return WorkerInfo(0, 0)
    }

    /**
     * Detects if research is available for the given base type.
     * @return true if research is available, false otherwise.
     */
    suspend fun detectResearch(baseType: BaseType): Boolean {
        // Must double-check to wait for a little bit, otherwise the detection will fail.
        val baseName = when (baseType) {
            BaseType.Builder -> "夜世界"; BaseType.Main -> "主世界"
        }

        // MainBase-only: goblin researcher pre-check
        if (baseType == BaseType.Main && findMultiColors(schema = MyColors.GoblinResearcher) != null) {
            accountMessage("检测到哥布林研究人员，无法进行研究")
            return false
        }

        // Detect research icon and count researchers
        // Fallback to ResearchIcon2 if ResearchIcon is not found
        val research = findMultiColors(schema = MyColors.ResearchIcon) ?: findMultiColors(schema = MyColors.ResearchIcon2)
        if (research != null) {
            // Define the crop region for the researcher number text
            val startX = research.x - 100
            val startY = 0
            val endX = research.x + 200
            val endY = 70

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = true, threshold = 240)
            // Sort by x-axis position to ensure left-to-right reading order
            val combinedText = results.sortedBy { it.position?.left ?: 0 }.joinToString("") { it.text }
            val researcherInfo = parseWorkerInfo(combinedText)
            if (researcherInfo != WorkerInfo(0, 0)) {
                accountMessage("${baseName}研究数量：${researcherInfo.available}/${researcherInfo.total}")
                return researcherInfo.available > 0
            }

            // Fallback: OCR failed, use colour-based pattern matching
            val fallback = detectWorkerByColorMatch(startX, startY, endX, endY)
            if (fallback != WorkerInfo(0, 0)) {
                accountMessage("${baseName}研究数量：${fallback.available}/${fallback.total}")
                return fallback.available > 0
            }
            accountMessage("${baseName}研究数量：0/0")
            return false
        }
        return false
    }

    /**
     * OCR for worker count: crop, mask outside the worker-number window, then
     * [TextRecognizer.preprocess] and ML Kit (same pipeline as [TextRecognizer.recognize]).
     */
    private suspend fun recognizeWorkerNumberWithMask(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        keepLeft: Int,
        keepTop: Int,
        keepRight: Int,
        keepBottom: Int,
        saveImage: Boolean = false,
    ): List<RecognizedText> {
        val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)
        val width = endX - startX
        val height = endY - startY
        if (width <= 0 || height <= 0) return emptyList()

        waitForPlay()

        if (startX + width > screenBuffer.width || startY + height > screenBuffer.height) {
            return emptyList()
        }

        val cropped = Bitmap.createBitmap(screenBuffer, startX, startY, width, height)
        val clampedLeft = keepLeft.coerceIn(0, width)
        val clampedTop = keepTop.coerceIn(0, height)
        val clampedRight = keepRight.coerceIn(0, width)
        val clampedBottom = keepBottom.coerceIn(0, height)
        val masked = maskOutsideRegion(cropped, clampedLeft, clampedTop, clampedRight, clampedBottom)
        if (masked !== cropped) {
            cropped.recycle()
        }
        return try {
            TextRecognizer.recognize(
                0,
                0,
                width,
                height,
                useChinese = false,
                threshold = 240,
                saveImage = saveImage,
                screenBuffer = masked,
            )
        } finally {
            masked.recycle()
        }
    }

    /**
     * Sets pixels outside the half-open rect [left, right) x [top, bottom) to black.
     */
    private fun maskOutsideRegion(
        src: Bitmap,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ): Bitmap {
        val width = src.width
        val height = src.height
        val out = src.copy(Bitmap.Config.ARGB_8888, true) ?: return src
        val pixels = IntArray(width * height)
        out.getPixels(pixels, 0, width, 0, 0, width, height)
        val black = 0xFF000000.toInt()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (x !in left..<right || y < top || y >= bottom) {
                    pixels[y * width + x] = black
                }
            }
        }
        out.setPixels(pixels, 0, width, 0, 0, width, height)
        return out
    }

    /**
     * Fallback detection: use findMultiColors on a preprocessed (binarized) bitmap
     * to match BinarySlash and BinaryOne patterns when OCR fails.
     */
    private suspend fun detectWorkerByColorMatch(
        startX: Int, startY: Int, endX: Int, endY: Int
    ): WorkerInfo {
        val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)

        val cropWidth = endX - startX
        val cropHeight = endY - startY
        if (startX + cropWidth > screenBuffer.width || startY + cropHeight > screenBuffer.height) {
            return WorkerInfo(0, 0)
        }

        val croppedBitmap = Bitmap.createBitmap(screenBuffer, startX, startY, cropWidth, cropHeight)
        // Apply the same preprocess as TextRecognizer.recognize (grayscale + binarization)
        val preprocessed = TextRecognizer.preprocess(croppedBitmap, threshold = 240, invertBinarization = true)

        // Step 1: find the slash character "/" in the preprocessed bitmap
        val slashSchema = ColorSchema.rescope(MyColors.BinarySlash, 0, 0, cropWidth, cropHeight)
        val slashPoint = findMultiColors(bitmap = preprocessed, schema = slashSchema) ?: return WorkerInfo(0, 0)
        val zeroSchema = ColorSchema.rescope(MyColors.BinaryZero, 0, 0, slashPoint.x, cropHeight)
        val zeroPoint = findMultiColors(bitmap = preprocessed, schema = zeroSchema)
        if (zeroPoint != null) return WorkerInfo(0, 0)
        // Step 2: find the digit "1" to the left of the slash
        val oneSchema = ColorSchema.rescope(MyColors.BinaryOne, 0, 0, slashPoint.x, cropHeight)
        val onePoint = findMultiColors(bitmap = preprocessed, schema = oneSchema)
        if (onePoint != null) {
            return WorkerInfo(1, 1)
        }
        // Step 3: try BinaryOne2 as an alternative pattern for "1"
        val oneSchema2 = ColorSchema.rescope(MyColors.BinaryOne2, 0, 0, slashPoint.x, cropHeight)
        val onePoint2 = findMultiColors(bitmap = preprocessed, schema = oneSchema2)
        if (onePoint2 != null) {
            return WorkerInfo(1, 1)
        }

        return WorkerInfo(0, 0)
    }

    /**
     * Parses the recognized text into WorkerInfo.
     */
    internal fun parseWorkerInfo(text: String): WorkerInfo {
        // Clean text and handle common OCR misrecognitions
        val cleaned = cleanOcrDigits(text)

        val match = Regex("""(\d+)/(\d+)""").find(cleaned)

        if (match != null) {
            var (availableStr, totalStr) = match.destructured

            // Filter: If xx/xx, keep the last digit of the first part and the first digit of the second part
            // For example: 12/53 -> 2/5, 1/22 -> 1/2
            if (availableStr.length > 1) {
                availableStr = availableStr.last().toString()
            }
            if (totalStr.length > 1) {
                totalStr = totalStr.first().toString()
            }

            val available = availableStr.toInt()
            val total = totalStr.toInt()

            // Swap if available > total, since total must always be >= available
            if (available > total) {
                return WorkerInfo(total, available)
            }
            return WorkerInfo(available, total)
        }
        return WorkerInfo(0, 0)
    }
}
