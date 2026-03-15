package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

/**
 * Data class representing the worker information (available/total).
 */
data class WorkerInfo(val available: Int, val total: Int)

object BuilderBaseWorkerAndResearch {

    /**
     * Detects the number of workers in the Builder Base.
     * @return A [WorkerInfo] object. If detection fails, returns (0, 0).
     */
    suspend fun detectWorkerNumber(): WorkerInfo {
        if (!enterMainScreen()) return WorkerInfo(0, 0)
        val worker = findMultiColors(schema = MyColors.BuilderBaseWorker)
        if (worker != null) {
            // Define the crop region for the worker number text
            val startX = worker.x - 50
            val startY = 0
            val endX = worker.x + 500
            val endY = 70

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = false)
            val combinedText = results.joinToString("") { it.text }
            return parseWorkerInfo(combinedText)
        }
        ShowMessage("未检测到夜世界工人，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("Builder_Base_Worker_Not_Detected")
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

    suspend fun detectResearch(): Boolean {
        if (!enterMainScreen()) return false
        val research = findMultiColors(schema = MyColors.ResearchIcon)
        if (research != null) {
            // Define the crop region for the worker number text
            val startX = research.x - 500
            val startY = 0
            val endX = research.x + 120
            val endY = 70

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = false)
            val combinedText = results.joinToString("") { it.text }
            val researcherInfo = parseWorkerInfo(combinedText)
            ShowMessage("夜世界研究数量：${researcherInfo.available}/${researcherInfo.total}")
            return researcherInfo.available > 0
        }
        ShowMessage("未检测到夜世界研究，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("Builder_Base_Research_Not_Detected")
        return false
    }
}