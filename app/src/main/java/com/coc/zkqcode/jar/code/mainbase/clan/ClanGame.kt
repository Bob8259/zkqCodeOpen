package com.coc.zkqcode.jar.code.mainbase.clan

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.aidetection.AiDetector
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory

private const val CLAN_GAME_MEMORY_KEY = "clan_game_last_run"
private const val CLAN_GAME_INTERVAL_MINUTES = 60
private const val CLAN_GAME_DETECT_LEFT = 470f
private const val CLAN_GAME_DETECT_TOP = 125f
private const val CLAN_GAME_DETECT_RIGHT = 1100f
private const val CLAN_GAME_DETECT_BOTTOM = 520f
private const val CLAN_GAME_NORMAL_TASK_CLASS = 0
private const val CLAN_GAME_PRO_TASK_CLASS = 1

suspend fun clanGame(): Boolean {
    val calendar = findMultiColors(MyColors.Calendar)

    if (calendar != null) {
        TouchActions.tap(calendar.x, calendar.y)

        var openClanGame = findMultiColorsUntil(schemas = listOf(MyColors.OpenClanGame), duration = 100)
        if (openClanGame == null) {
            TouchActions.swipe(700, 500, 600, 20, delayAfter = 300)
            openClanGame = findMultiColorsUntil(schemas = listOf(MyColors.OpenClanGame), duration = 100)
        }
        if (openClanGame != null) {
            TouchActions.tap(openClanGame.x, openClanGame.y, delayTime = 1000)
            val normalTask = findMultiColorsUntil(schemas = listOf(MyColors.NormalTasks), duration = 3000)
            if (normalTask != null) {
                delayWithMultiplier(300)
                if (findMultiColors(MyColors.FinishedTask) != null) {
                    accountMessage("已有任务完成，等待2秒")
                    delayWithMultiplier(2000)
                }
                if (findMultiColors(MyColors.OnGoingTask) != null) {
                    accountMessage("已有任务进行中")
                    clickRightBottom(2)
                    return enterMainScreen()
                }
            }
            detectAndAcceptATask()

            TouchActions.swipe(700, 500, 600, 20, delayAfter = 300)
            // Upload a screenshot to the server again
            detectAndAcceptATask()
            delayWithMultiplier(1000000)
            TouchActions.swipe(600, 150, 600, 600, delayAfter = 300)
        }

        writeMemory(CLAN_GAME_MEMORY_KEY, (System.currentTimeMillis() / 60_000).toString())
    }
    return enterMainScreen()
}

suspend fun detectAndAcceptATask() {
    val screenshot = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
    if (screenshot == null) {
        accountMessage("部落竞赛截图失败，无法检测任务")
        return
    }

    if (!AiDetector.loadWeights("clan-game")) return
    try {
        val detections = AiDetector.detect(
            bitmap = screenshot,
            clearWeightsAfter = false,
            threshold = 0.3f
        ).filter { detection ->
            val centerX = detection.boundingBox.centerX()
            val centerY = detection.boundingBox.centerY()
            detection.classIndex in CLAN_GAME_NORMAL_TASK_CLASS..CLAN_GAME_PRO_TASK_CLASS &&
                    centerX in CLAN_GAME_DETECT_LEFT..CLAN_GAME_DETECT_RIGHT &&
                    centerY in CLAN_GAME_DETECT_TOP..CLAN_GAME_DETECT_BOTTOM
        }

        if (detections.isEmpty()) {
            accountMessage("未检测到部落竞赛任务")
            return
        }
        val annotatedSummary = detections.map { detection ->
            val centerX = detection.boundingBox.centerX().toInt()
            val centerY = detection.boundingBox.centerY().toInt()
            val taskType = when (detection.classIndex) {
                CLAN_GAME_NORMAL_TASK_CLASS -> "普通任务"
                CLAN_GAME_PRO_TASK_CLASS -> "高级任务"
                else -> "未知任务"
            }
            val baseType = detectClanGameBaseType(screenshot, centerX, centerY)
            "$taskType[$baseType]: $centerX, $centerY"
        }.joinToString(separator = "\n")
        accountMessage("部落竞赛任务检测结果：\n$annotatedSummary")
    } finally {
        AiDetector.clearWeights()
    }
}

private suspend fun detectClanGameBaseType(screenshot: Bitmap, x: Int, y: Int): String {
    val builderBaseSchema = ColorSchema.rescope(MyColors.BuilderBaseClanGame, x - 10, y + 20, x + 10, y + 100)
    if (findMultiColors(builderBaseSchema, bitmap = screenshot) != null) return "夜世界"

    val mainBaseSchema = ColorSchema.rescope(MyColors.MainBaseClanGame, x - 10, y + 20, x + 10, y + 100)
    if (findMultiColors(mainBaseSchema, bitmap = screenshot) != null) return "主世界"

    return "未知世界"
}