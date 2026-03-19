package com.coc.zkqcode.jar.code.universal.buildings

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

/**
 * Data class representing the worker information (available/total).
 */
data class WorkerInfo(val available: Int, val total: Int)

enum class BaseType {
    Builder, Main
}

/**
 * Unified worker and research detection logic for both Main Base and Builder Base.
 * Dispatches on [BaseType] to handle base-specific differences (color schemas, goblin checks, messages).
 */
object WorkerAndResearch {

    /**
     * Detects the number of workers for the given base type.
     * @return A [WorkerInfo] object. If detection fails, returns (0, 0).
     */
    suspend fun detectWorkerNumber(baseType: BaseType): WorkerInfo {
        // Must double-check to wait for a little bit, otherwise the detection will fail.
        if (!enterMainScreen(true)) return WorkerInfo(0, 0)
        // MainBase-only: goblin worker pre-check
        if (baseType == BaseType.Main && findMultiColors(schema = MyColors.GoblinWorker) != null) {
            return WorkerInfo(0, 6)
        }

        val workerSchemas = when (baseType) {
            BaseType.Builder -> listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2)
            BaseType.Main -> listOf(MyColors.MainBaseWorker, MyColors.MainBaseWorker2)
        }
        val baseName = when (baseType) {
            BaseType.Builder -> "夜世界"; BaseType.Main -> "主世界"
        }
        val bugTag = when (baseType) {
            BaseType.Builder -> "Builder_Base_Worker"; BaseType.Main -> "Main_Base_Worker"
        }

        val worker = findMultiColorsUntil(schemas = workerSchemas, duration = 200)
        if (worker != null) {
            // Define the crop region for the worker number text
            val startX = worker.x - 50
            val startY = 0
            val endX = worker.x + 250
            val endY = 70

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = true, threshold = 240)

            // Sort by x-axis position to ensure left-to-right reading order
            val combinedText = results.sortedBy { it.position?.left ?: 0 }.joinToString("") { it.text }
            val workerInfo = parseWorkerInfo(combinedText)
            if (workerInfo != WorkerInfo(0, 0)) return workerInfo
            // Fallback: OCR failed, use color-based pattern matching on the preprocessed bitmap
            return detectWorkerByColorMatch(startX, startY, endX, endY)
        }
        ShowMessage("未检测到${baseName}工人，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("${bugTag}_Not_Detected")
        return WorkerInfo(0, 0)
    }

    /**
     * Detects if research is available for the given base type.
     * @return true if research is available, false otherwise.
     */
    suspend fun detectResearch(baseType: BaseType): Boolean {
        // Must double-check to wait for a little bit, otherwise the detection will fail.
        if (!enterMainScreen(true)) return false

        val baseName = when (baseType) {
            BaseType.Builder -> "夜世界"; BaseType.Main -> "主世界"
        }
        val bugTag = when (baseType) {
            BaseType.Builder -> "Builder_Base_Research"; BaseType.Main -> "Main_Base_Research"
        }

        // MainBase-only: goblin researcher pre-check
        if (baseType == BaseType.Main && findMultiColors(schema = MyColors.GoblinResearcher) != null) {
            ShowMessage("检测到哥布林研究人员，无法进行研究")
            return false
        }

        // Detect research icon and count researchers
        val research = findMultiColors(schema = MyColors.ResearchIcon)
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
                ShowMessage("${baseName}研究数量：${researcherInfo.available}/${researcherInfo.total}")
                return researcherInfo.available > 0
            }

            // Fallback: OCR failed, use color-based pattern matching
            val fallback = detectWorkerByColorMatch(startX, startY, endX, endY)
            if (fallback != WorkerInfo(0, 0)) {
                ShowMessage("${baseName}研究数量：${fallback.available}/${fallback.total}")
                return fallback.available > 0
            }
            ShowMessage("${baseName}研究数量：0/0")
            return false
        }
        ShowMessage("未检测到${baseName}研究，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("${bugTag}_Not_Detected")
        return false
    }

    /**
     * Fallback detection: use findMultiColors on a preprocessed (binarized) bitmap
     * to match BinarySlash and BinaryOne patterns when OCR fails.
     */
    private suspend fun detectWorkerByColorMatch(
        startX: Int, startY: Int, endX: Int, endY: Int
    ): WorkerInfo {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
            ?: return WorkerInfo(0, 0)

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
        val slashPoint = findMultiColors(bitmap = preprocessed, schema = slashSchema)
            ?: return WorkerInfo(0, 0)

        // Step 2: find the digit "1" to the left of the slash
        val oneSchema = ColorSchema.rescope(MyColors.BinaryOne, 0, 0, slashPoint.x, cropHeight)
        val onePoint = findMultiColors(bitmap = preprocessed, schema = oneSchema)
        if (onePoint != null) {
            return WorkerInfo(1, 1)
        }

        return WorkerInfo(0, 0)
    }

    /**
     * Parses the recognized text into WorkerInfo.
     */
    internal fun parseWorkerInfo(text: String): WorkerInfo {
        // Clean text and handle common OCR misrecognitions
        val cleaned = text.replace(" ", "").replace("o", "0").replace("O", "0").replace("I", "1").replace("l", "1")
            .replace("Z", "2").replace("z", "2").replace("S", "5").replace("s", "5").replace("G", "6")

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

            return WorkerInfo(availableStr.toInt(), totalStr.toInt())
        }
        return WorkerInfo(0, 0)
    }
}
