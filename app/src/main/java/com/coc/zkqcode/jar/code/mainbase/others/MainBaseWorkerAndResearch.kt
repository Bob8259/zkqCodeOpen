package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.ShowMessage.invoke
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch.parseWorkerInfo
import com.coc.zkqcode.jar.code.builderbase.others.WorkerInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

object MainBaseWorkerAndResearch {
    suspend fun detectWorkerNumber(): WorkerInfo {
        val worker = findMultiColors(schema = MyColors.MainBaseWorker)
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
        ShowMessage("未检测到主世界工人，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("Main_Base_Worker_Not_Detected")
        return WorkerInfo(0, 0)
    }
}