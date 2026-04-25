package com.coc.zkqcode.jar.code.mainbase.clan

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.ClanGameAcceptedTaskBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.aidetection.AiDetector
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

private const val CLAN_GAME_MEMORY_KEY = "clan_game_last_run"
private const val CLAN_GAME_INTERVAL_MINUTES = 60
private const val CLAN_GAME_DETECT_LEFT = 470f
private const val CLAN_GAME_DETECT_TOP = 125f
private const val CLAN_GAME_DETECT_RIGHT = 1100f
private const val CLAN_GAME_DETECT_BOTTOM = 520f
private const val CLAN_GAME_NORMAL_TASK_CLASS = 0
private const val CLAN_GAME_PRO_TASK_CLASS = 1

suspend fun clanGame(): Boolean {
    InGameVars.clanGameAcceptedTaskBase = ClanGameAcceptedTaskBase.NONE
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
            if (detectAndAcceptATask()) return enterMainScreen()

            TouchActions.swipe(700, 500, 600, 20, delayAfter = 300)
            // Upload a screenshot to the server again
            if (detectAndAcceptATask()) return enterMainScreen()
            delayWithMultiplier(1000000)
            TouchActions.swipe(600, 150, 600, 600, delayAfter = 300)
        }

        writeMemory(CLAN_GAME_MEMORY_KEY, (System.currentTimeMillis() / 60_000).toString())
    }
    return enterMainScreen()
}

suspend fun detectAndAcceptATask(): Boolean {
    val screenshot = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
    if (screenshot == null) {
        accountMessage("部落竞赛截图失败，无法检测任务")
        return false
    }

    if (!AiDetector.loadWeights("clan-game")) return false
    try {
        val detections = AiDetector.detect(
            bitmap = screenshot, clearWeightsAfter = false, threshold = 0.3f
        ).filter { detection ->
            val centerX = detection.boundingBox.centerX()
            val centerY = detection.boundingBox.centerY()
            detection.classIndex in CLAN_GAME_NORMAL_TASK_CLASS..CLAN_GAME_PRO_TASK_CLASS && centerX in CLAN_GAME_DETECT_LEFT..CLAN_GAME_DETECT_RIGHT && centerY in CLAN_GAME_DETECT_TOP..CLAN_GAME_DETECT_BOTTOM
        }.sortedWith(compareBy({ it.boundingBox.centerY() }, { it.boundingBox.centerX() }))

        if (detections.isEmpty()) {
            accountMessage("未检测到部落竞赛任务")
            return false
        }

        val taskDecisions = detections.map { detection ->
            val centerX = detection.boundingBox.centerX().toInt()
            val centerY = detection.boundingBox.centerY().toInt()
            val taskType = when (detection.classIndex) {
                CLAN_GAME_NORMAL_TASK_CLASS -> "普通任务"
                CLAN_GAME_PRO_TASK_CLASS -> "高级任务"
                else -> "未知任务"
            }
            val baseType = detectClanGameBaseType(screenshot, centerX, centerY)
            ClanGameTaskDecision(
                x = centerX,
                y = centerY,
                taskType = taskType,
                baseType = baseType,
                shouldAccept = shouldAcceptClanGameTask(detection.classIndex, baseType)
            )
        }

        val annotatedSummary = taskDecisions.joinToString(separator = "\n") { decision ->
            val acceptStatus = if (decision.shouldAccept) "符合设置" else "跳过"
            "${decision.taskType}[${decision.baseType}]: ${decision.x}, ${decision.y} ($acceptStatus)"
        }
        accountMessage("部落竞赛任务检测结果：\n$annotatedSummary")

        val targetTask = taskDecisions.firstOrNull { it.shouldAccept }
        if (targetTask == null) {
            accountMessage("未找到符合设置的部落竞赛任务")
            return false
        }

        if (acceptTask(targetTask.x, targetTask.y)) {
            InGameVars.clanGameAcceptedTaskBase = targetTask.acceptedTaskBase
            accountMessage("已接取部落竞赛${targetTask.baseType}${targetTask.taskType}")
            return true
        }

        accountMessage("未找到部落竞赛接取按钮")
        return false
    } finally {
        AiDetector.clearWeights()
    }
}

private suspend fun acceptTask(x: Int, y: Int): Boolean {
    TouchActions.tap(x, y, delayTime = 500)
    val acceptTaskButton = findMultiColors(MyColors.AcceptTask)
    if (acceptTaskButton != null) {
        TouchActions.tap(acceptTaskButton.x, acceptTaskButton.y, delayTime = 1000)
        return true
    }
    return false
}

private fun toAcceptedTaskBase(baseType: String): ClanGameAcceptedTaskBase {
    return when (baseType) {
        "主世界" -> ClanGameAcceptedTaskBase.MAIN_WORLD
        "夜世界" -> ClanGameAcceptedTaskBase.NIGHT_WORLD
        else -> ClanGameAcceptedTaskBase.NONE
    }
}

private fun shouldAcceptClanGameTask(classIndex: Int, baseType: String): Boolean {
    val settingKey = when (baseType) {
        "主世界" -> when (classIndex) {
            CLAN_GAME_NORMAL_TASK_CLASS -> Schema.MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_NORMAL_TASK.key
            CLAN_GAME_PRO_TASK_CLASS -> Schema.MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_ADVANCED_TASK.key
            else -> return false
        }

        "夜世界" -> when (classIndex) {
            CLAN_GAME_NORMAL_TASK_CLASS -> Schema.MAIN_BASE_SETTINGS.ACCEPT_NIGHT_WORLD_NORMAL_TASK.key
            CLAN_GAME_PRO_TASK_CLASS -> Schema.MAIN_BASE_SETTINGS.ACCEPT_NIGHT_WORLD_ADVANCED_TASK.key
            else -> return false
        }

        else -> return false
    }
    return getBooleanConfigRuntime(settingKey)
}

private suspend fun detectClanGameBaseType(screenshot: Bitmap, x: Int, y: Int): String {
    val builderBaseSchema = ColorSchema.rescope(MyColors.BuilderBaseClanGame, x - 10, y + 20, x + 10, y + 100)
    if (findMultiColors(builderBaseSchema, bitmap = screenshot) != null) return "夜世界"

    val mainBaseSchema = ColorSchema.rescope(MyColors.MainBaseClanGame, x - 10, y + 20, x + 10, y + 100)
    if (findMultiColors(mainBaseSchema, bitmap = screenshot) != null) return "主世界"

    return "未知世界"
}

private data class ClanGameTaskDecision(
    val x: Int,
    val y: Int,
    val taskType: String,
    val baseType: String,
    val shouldAccept: Boolean,
    val acceptedTaskBase: ClanGameAcceptedTaskBase = toAcceptedTaskBase(baseType)
)