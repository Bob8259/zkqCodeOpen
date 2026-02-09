package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

/**
 * Data class representing the worker information (available/total).
 */
data class WorkerInfo(val available: Int, val total: Int)

object NightBaseWorkerAndResearch {

    /**
     * Detects the number of workers in the Night Base.
     * @return A [WorkerInfo] object. If detection fails, returns (0, 0).
     */
    suspend fun detectWorkerNumber(): WorkerInfo {
        val worker = findMultiColors(schema = MyColors.NightBaseWorker)
        if (worker != null) {
            // Define the crop region for the worker number text
            val startX = worker.x
            val startY = 0
            val endX = worker.x + 120
            val endY = 60

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, threshold = 290)
            val combinedText = results.joinToString("") { it.text }
            return parseWorkerInfo(combinedText)
        }
        ShowMessage("未检测到夜世界工人")
        delayWithMultiplier(500)
        return WorkerInfo(0, 0)
    }

    /**
     * Parses the recognized text into WorkerInfo.
     */
    private fun parseWorkerInfo(text: String): WorkerInfo {
        // Clean text and handle common OCR misrecognitions
        val cleaned = text.replace(" ", "").replace("o", "0").replace("O", "0").replace("I", "1").replace("l", "1")
            .replace("Z", "2").replace("z", "2").replace("S", "5").replace("s", "5").replace("G", "6")

        val match = Regex("""(\d+)/(\d+)""").find(cleaned)
        ShowMessage("text $text")
        if (match != null) {
            val (available, total) = match.destructured
            ShowMessage("夜世界工人数量：${available}/${total}")
            return WorkerInfo(available.toInt(), total.toInt())
        }
        return WorkerInfo(0, 0)
    }

    suspend fun detectResearch() {

    }
}