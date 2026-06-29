package com.coc.zkqcode.jar.code.universal.clangame

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.aidetection.AiDetector
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.CLAN_GAME_TASK_BASE_BUILDER_BASE
import com.coc.zkqcode.jar.code.universal.smalltools.data.CLAN_GAME_TASK_BASE_MAIN_WORLD
import com.coc.zkqcode.jar.code.universal.smalltools.data.CLAN_GAME_TASK_BASE_NONE
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeClanGameAcceptedTaskBase
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar
import java.util.TimeZone

private const val CLAN_GAME_DETECT_LEFT = 470f
private const val CLAN_GAME_DETECT_TOP = 125f
private const val CLAN_GAME_DETECT_RIGHT = 1100f
private const val CLAN_GAME_DETECT_BOTTOM = 520f
private const val CLAN_GAME_START_DAY = 22
private const val CLAN_GAME_END_DAY = 28
private const val CLAN_GAME_WINDOW_HOUR = 16
private const val CLAN_GAME_OPEN_BUTTON_WAIT_MS = 100
private const val CLAN_GAME_OPEN_BUTTON_MAX_SWIPES = 2
private const val CLAN_GAME_SWIPE_DELAY_MS = 300
private const val CLAN_GAME_FIRST_TASK_X = 550
private const val CLAN_GAME_FIRST_TASK_Y = 200
private const val CLAN_GAME_BASE_ICON_DETECT_HALF_WIDTH = 10
private const val CLAN_GAME_BASE_ICON_DETECT_TOP_OFFSET = 20
private const val CLAN_GAME_BASE_ICON_DETECT_BOTTOM_OFFSET = 100

suspend fun clanGame(targetBaseType: BaseType): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key)) return true
    if (!shouldCheckClanGameNow()) return true

    if (openClanGameTaskPage()) {
        if (prepareClanGameTaskState()) {
            when (getCurrentClanGameTaskState(checkFourThousandPoints = false)) {
                ClanGameTaskState.FourThousandPointsReached -> {
                    markClanGameFinishedThisMonth()
                    return closeClanGamePageAndEnterMainScreen()
                }

                ClanGameTaskState.OnGoing, ClanGameTaskState.CoolDown -> return closeClanGamePageAndEnterMainScreen()
                ClanGameTaskState.Failed -> clearFailedTask()
                ClanGameTaskState.None -> Unit
            }
            if (detectAndAcceptTaskWithRetry(targetBaseType)) return enterMainScreen()
            if (targetBaseType == BaseType.Main) {
                writeClanGameAcceptedTaskBase(CLAN_GAME_TASK_BASE_NONE)
                giveUpATask()
            }
        }
    }

    return enterMainScreen()
}

suspend fun hasAcceptedClanGameTask(currentBaseType: BaseType): Boolean {
    if (!shouldCheckClanGameNow()) return false
    if (!openClanGameTaskPage()) return false
    prepareClanGameTaskState()
    val result = findMultiColors(MyColors.OnGoingTask) != null
    accountMessage("接取任务状态：$result")
    if (!result) {
        when (getCurrentClanGameTaskState(checkFourThousandPoints = false)) {
            ClanGameTaskState.CoolDown -> return closeClanGamePageAndEnterMainScreen()
            ClanGameTaskState.Failed -> clearFailedTask()
            ClanGameTaskState.FourThousandPointsReached, ClanGameTaskState.OnGoing, ClanGameTaskState.None -> Unit
        }
        if (currentBaseType == BaseType.Main) {
            if (detectAndAcceptTaskWithRetry(BaseType.Builder)) return enterMainScreen()
        }
        writeClanGameAcceptedTaskBase(CLAN_GAME_TASK_BASE_NONE)
        if (currentBaseType == BaseType.Main) {
            giveUpATask()
        }
    }
    closeClanGamePage()
    return result
}

private suspend fun shouldCheckClanGameNow(): Boolean {
    val now = getBeijingCalendar()
    if (!isInClanGamePeriod(now)) {
        accountMessage("当前不在部落竞赛时间，暂不检测")
        writeClanGameAcceptedTaskBase(CLAN_GAME_TASK_BASE_NONE)
        return false
    }

    if (readMemory(getClanGameFinishedMonthStorageKey()) == getClanGameMonth(now)) {
        accountMessage("本月部落竞赛已完成，等待下个月")
        return false
    }

    return true
}

private suspend fun markClanGameFinishedThisMonth() {
    writeMemory(getClanGameFinishedMonthStorageKey(), getClanGameMonth(getBeijingCalendar()))
    accountMessage("本月部落竞赛已完成，等待下个月")
}

private fun getClanGameFinishedMonthStorageKey(): String {
    return StorageKeys.withAccountNumber(StorageKeys.CLAN_GAME_FINISHED_MONTH, InGameVars.currentAccountNumber)
}

private fun getBeijingCalendar(): Calendar {
    return Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
}

private fun isInClanGamePeriod(calendar: Calendar): Boolean {
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return when (day) {
        !in CLAN_GAME_START_DAY..CLAN_GAME_END_DAY -> false
        CLAN_GAME_START_DAY -> hour >= CLAN_GAME_WINDOW_HOUR
        CLAN_GAME_END_DAY -> hour < CLAN_GAME_WINDOW_HOUR
        else -> true
    }
}

private fun getClanGameMonth(calendar: Calendar): String {
    val year = calendar.get(Calendar.YEAR)
    val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
    return "$year-$month"
}

private suspend fun openClanGameTaskPage(): Boolean {
    val calendar = findMultiColorsUntil(
        schemas = listOf(MyColors.Calendar, MyColors.Calendar2), duration = CLAN_GAME_OPEN_BUTTON_WAIT_MS
    ) ?: return false

    TouchActions.tap(calendar.x, calendar.y)

    var openClanGame = findMultiColorsUntil(schemas = listOf(MyColors.OpenClanGame), duration = CLAN_GAME_OPEN_BUTTON_WAIT_MS)
    var swipeAttempts = 0
    while (openClanGame == null && swipeAttempts < CLAN_GAME_OPEN_BUTTON_MAX_SWIPES) {
        swipeClanGameTaskList()
        openClanGame = findMultiColorsUntil(schemas = listOf(MyColors.OpenClanGame), duration = CLAN_GAME_OPEN_BUTTON_WAIT_MS)
        swipeAttempts++
    }
    if (openClanGame == null) return false

    TouchActions.tap(openClanGame.x, openClanGame.y, delayTime = 1000)
    return true
}

private suspend fun prepareClanGameTaskState(): Boolean {
    val normalTask = findMultiColorsUntil(
        schemas = listOf(MyColors.NormalTasks, MyColors.OnGoingTask, MyColors.TaskCoolDown, MyColors.FailedTask), duration = 3000
    )
    if (normalTask == null) return false

    delayWithMultiplier(300)
    if (findMultiColors(MyColors.FinishedTask) != null) {
        accountMessage("已有任务完成，等待5秒")
        delayWithMultiplier(5000)
    }
    return true
}

private suspend fun getCurrentClanGameTaskState(checkFourThousandPoints: Boolean = true): ClanGameTaskState {
    if (checkFourThousandPoints && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.STOP_CLAN_GAMES_AFTER_4000_POINTS.key) && findMultiColors(MyColors.FourThousandPoints) != null) {
        return ClanGameTaskState.FourThousandPointsReached
    }
    if (findMultiColors(MyColors.OnGoingTask) != null) return ClanGameTaskState.OnGoing
    if (findMultiColors(MyColors.TaskCoolDown) != null) return ClanGameTaskState.CoolDown
    if (findMultiColorsUntil(listOf(MyColors.FailedTask), 500) != null) return ClanGameTaskState.Failed
    return ClanGameTaskState.None
}

private suspend fun closeClanGamePage() {
    clickRightBottom(2)
}

private suspend fun closeClanGamePageAndEnterMainScreen(): Boolean {
    closeClanGamePage()
    return enterMainScreen()
}

private suspend fun clearFailedTask() {
    TouchActions.tap(CLAN_GAME_FIRST_TASK_X, CLAN_GAME_FIRST_TASK_Y)
    TouchActions.tap(
        990, 345, delayTime = 3500
    )
}

private suspend fun detectAndAcceptTaskWithRetry(targetBaseType: BaseType): Boolean {
    if (detectAndAcceptATask(targetBaseType)) return true
    swipeClanGameTaskList()
    return detectAndAcceptATask(targetBaseType)
}

private suspend fun swipeClanGameTaskList() {
    TouchActions.swipe(
        700, 500, 600, 20, delayAfter = CLAN_GAME_SWIPE_DELAY_MS
    )
}

private suspend fun giveUpATask() {
    accountMessage("未找到可接取的任务，放弃第一个任务")
    TouchActions.swipe(
        600, 150, 600, 600, delayAfter = CLAN_GAME_SWIPE_DELAY_MS
    )
    TouchActions.tap(CLAN_GAME_FIRST_TASK_X, CLAN_GAME_FIRST_TASK_Y)
    TouchActions.tap(900, 320, delayTime = 2000)
    val giveUpButton = findMultiColors(MyColors.GiveUpTaskButton)
    if (giveUpButton != null) {
        TouchActions.tap(giveUpButton.x, giveUpButton.y, delayTime = 1000)
        TouchActions.tap(780, 440, delayTime = 1000)
    }
    closeClanGamePage()
}

private suspend fun detectAndAcceptATask(targetBaseType: BaseType): Boolean {
    val screenshot = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
    if (screenshot == null) {
        accountMessage("部落竞赛截图失败，无法检测任务")
        return false
    }

    val cropLeft = CLAN_GAME_DETECT_LEFT.toInt()
    val cropTop = CLAN_GAME_DETECT_TOP.toInt()
    val cropRight = CLAN_GAME_DETECT_RIGHT.toInt()
    val cropBottom = CLAN_GAME_DETECT_BOTTOM.toInt()
    val cropWidth = cropRight - cropLeft
    val cropHeight = cropBottom - cropTop
    if (cropRight > screenshot.width || cropBottom > screenshot.height) {
        accountMessage("部落竞赛截图尺寸异常，无法裁剪检测区域：${screenshot.width}x${screenshot.height}")
        return false
    }

    var clanGameCrop: Bitmap? = null
    if (!AiDetector.loadWeights("clan-game")) return false
    try {
        clanGameCrop = Bitmap.createBitmap(screenshot, cropLeft, cropTop, cropWidth, cropHeight)
        val detections = AiDetector.detect(
            bitmap = clanGameCrop, clearWeightsAfter = false, threshold = 0.65f
        ).onEach { detection ->
            detection.boundingBox.offset(cropLeft.toFloat(), cropTop.toFloat())
        }.filter { detection ->
            val centerX = detection.boundingBox.centerX()
            val centerY = detection.boundingBox.centerY()
            ClanGameTaskClass.fromClassIndex(detection.classIndex) != null && centerX in CLAN_GAME_DETECT_LEFT..CLAN_GAME_DETECT_RIGHT && centerY in CLAN_GAME_DETECT_TOP..CLAN_GAME_DETECT_BOTTOM
        }.sortedWith(compareBy({ it.boundingBox.centerY() }, { it.boundingBox.centerX() }))

        if (detections.isEmpty()) {
            accountMessage("未检测到部落竞赛任务")
            return false
        }

        val taskDecisions = detections.mapNotNull { detection ->
            val taskClass = ClanGameTaskClass.fromClassIndex(detection.classIndex) ?: return@mapNotNull null
            val centerX = detection.boundingBox.centerX().toInt()
            val centerY = detection.boundingBox.centerY().toInt()
            val clanGameBase = detectClanGameBaseType(screenshot, centerX, centerY)
            ClanGameTaskDecision(
                x = centerX, y = centerY, taskClass = taskClass, clanGameBase = clanGameBase, shouldAccept = shouldAcceptClanGameTask(taskClass, clanGameBase, targetBaseType)
            )
        }

        val annotatedSummary = taskDecisions.joinToString(separator = "\n") { decision ->
            val acceptStatus = if (decision.shouldAccept) "符合设置" else "跳过"
            "${decision.taskClass.displayName}[${decision.clanGameBase.displayName}]: ${decision.x}, ${decision.y} ($acceptStatus)"
        }
        accountMessage("部落竞赛任务检测结果：\n$annotatedSummary")

        val targetTask = taskDecisions.firstOrNull { it.shouldAccept }
        if (targetTask == null) {
            accountMessage("未找到符合设置的部落竞赛任务")
            return false
        }

        if (acceptTask(targetTask.x, targetTask.y)) {
            writeClanGameAcceptedTaskBase(targetTask.clanGameBase.acceptedTaskBase)
            accountMessage("已接取部落竞赛${targetTask.clanGameBase.displayName}${targetTask.taskClass.displayName}")
            return true
        }

        accountMessage("未找到部落竞赛接取按钮")
        return false
    } finally {
        clanGameCrop?.recycle()
        AiDetector.clearWeights()
    }
}

private suspend fun acceptTask(x: Int, y: Int): Boolean {
    TouchActions.tap(x, y, delayTime = 500)
    val acceptTaskButton = findMultiColors(MyColors.AcceptTask)
    if (acceptTaskButton != null) {
        TouchActions.tap(
            acceptTaskButton.x, acceptTaskButton.y, isJitter = false, delayTime = 2500
        )
        clickRightBottom(1)
        return true
    }
    return false
}

private fun shouldAcceptClanGameTask(taskClass: ClanGameTaskClass, clanGameBase: ClanGameBase, targetBaseType: BaseType): Boolean {
    if (clanGameBase.baseType != targetBaseType) return false
    val settingKey = taskClass.settingKeyFor(clanGameBase) ?: return false
    return getBooleanConfigRuntime(settingKey)
}

private suspend fun detectClanGameBaseType(screenshot: Bitmap, x: Int, y: Int): ClanGameBase {
    val builderBaseSchema = ColorSchema.rescope(
        MyColors.BuilderBaseClanGame, x - CLAN_GAME_BASE_ICON_DETECT_HALF_WIDTH, y + CLAN_GAME_BASE_ICON_DETECT_TOP_OFFSET, x + CLAN_GAME_BASE_ICON_DETECT_HALF_WIDTH, y + CLAN_GAME_BASE_ICON_DETECT_BOTTOM_OFFSET
    )
    if (findMultiColors(builderBaseSchema, bitmap = screenshot) != null) return ClanGameBase.Builder

    val mainBaseSchema = ColorSchema.rescope(
        MyColors.MainBaseClanGame, x - CLAN_GAME_BASE_ICON_DETECT_HALF_WIDTH, y + CLAN_GAME_BASE_ICON_DETECT_TOP_OFFSET, x + CLAN_GAME_BASE_ICON_DETECT_HALF_WIDTH, y + CLAN_GAME_BASE_ICON_DETECT_BOTTOM_OFFSET
    )
    if (findMultiColors(mainBaseSchema, bitmap = screenshot) != null) return ClanGameBase.Main

    return ClanGameBase.Unknown
}

private enum class ClanGameTaskState {
    FourThousandPointsReached, OnGoing, CoolDown, Failed, None
}

private enum class ClanGameBase(
    val displayName: String, val baseType: BaseType?, val acceptedTaskBase: String
) {
    Main("主世界", BaseType.Main, CLAN_GAME_TASK_BASE_MAIN_WORLD), Builder("Builder Base", BaseType.Builder, CLAN_GAME_TASK_BASE_BUILDER_BASE), Unknown("未知世界", null, CLAN_GAME_TASK_BASE_NONE)
}

private enum class ClanGameTaskClass(
    val classIndex: Int, val displayName: String, private val mainBaseSettingKey: String, private val builderBaseSettingKey: String
) {
    Normal(
        0, "普通任务", Schema.MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_NORMAL_TASK.key, Schema.MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_NORMAL_TASK.key
    ),
    Pro(
        1, "高级任务", Schema.MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_ADVANCED_TASK.key, Schema.MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_ADVANCED_TASK.key
    );

    fun settingKeyFor(clanGameBase: ClanGameBase): String? {
        return when (clanGameBase) {
            ClanGameBase.Main -> mainBaseSettingKey
            ClanGameBase.Builder -> builderBaseSettingKey
            ClanGameBase.Unknown -> null
        }
    }

    companion object {
        fun fromClassIndex(classIndex: Int): ClanGameTaskClass? {
            return entries.firstOrNull { it.classIndex == classIndex }
        }
    }
}

private data class ClanGameTaskDecision(
    val x: Int, val y: Int, val taskClass: ClanGameTaskClass, val clanGameBase: ClanGameBase, val shouldAccept: Boolean
)
