package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.bugreporter.BugReporter
import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch.parseWorkerInfo
import com.coc.zkqcode.jar.code.builderbase.others.WorkerInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer

object MainBaseWorkerAndResearch {
    suspend fun detectWorkerNumber(): WorkerInfo {
        if (!enterMainScreen()) return WorkerInfo(0, 0)
        if (findMultiColors(schema = MyColors.GoblinWorker) != null) {
            return WorkerInfo(0, 6)
        }
        val worker = findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker, MyColors.MainBaseWorker2), duration = 200)
        if (worker != null) {
            // Define the crop region for the worker number text
            val startX = worker.x - 50
            val startY = 0
            val endX = worker.x + 500
            val endY = 70

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = true, threshold = 200)
            val combinedText = results.joinToString("") { it.text }
            return parseWorkerInfo(combinedText)
        }
        ShowMessage("未检测到主世界工人，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("Main_Base_Worker_Not_Detected")
        return WorkerInfo(0, 0)
    }

    /**
     * Detects if research is available in the Main Base.
     * @return true if research is available, false otherwise (including when Goblin Researcher is detected).
     */
    suspend fun detectResearch(): Boolean {
        if (!enterMainScreen()) return false
        // First check for Goblin Researcher - if detected, return false
        if (findMultiColors(schema = MyColors.GoblinResearcher) != null) {
            ShowMessage("检测到哥布林研究人员，无法进行研究")
            return false
        }

        // Then detect research icon and count researchers
        val research = findMultiColors(schema = MyColors.ResearchIcon)
        if (research != null) {
            // Define the crop region for the researcher number text
            val startX = research.x - 350
            val startY = 0
            val endX = research.x + 120
            val endY = 70

            val results = TextRecognizer.recognize(startX, startY, endX, endY, useChinese = false, applyPreprocess = true, threshold = 200)
            val combinedText = results.joinToString("") { it.text }
            val researcherInfo = parseWorkerInfo(combinedText)
            ShowMessage("主世界研究数量：${researcherInfo.available}/${researcherInfo.total}")
            return researcherInfo.available > 0
        }
        ShowMessage("未检测到主世界研究，已将错误截图保存到/sdcard/zkqFiles/bugReporter\n请反馈给作者")
        BugReporter.takeScreenshot("Main_Base_Research_Not_Detected")
        return false
    }
}