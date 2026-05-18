package com.coc.zkqcode.jar.code.universal.smalltools.lifecycle

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

private const val ANR_DIALOG_LEFT = 160
private const val ANR_DIALOG_TOP = 120
private const val ANR_DIALOG_RIGHT = 1120
private const val ANR_DIALOG_BOTTOM = 640
private const val ANR_LOGCAT_CHECK_INTERVAL_MS = 5_000L
private const val ANR_DROPBOX_CHECK_INTERVAL_MS = 20_000L
private const val ANR_OCR_RETRY_INTERVAL_MS = 5_000L
private const val ANR_SIGNAL_RECENT_WINDOW_MS = 120_000L
private const val ANR_LOGCAT_LINE_LIMIT = 300
private const val ANR_DROPBOX_TAIL_LINE_LIMIT = 500

private val anrDetectorStartTime = System.currentTimeMillis()
private val logcatEpochRegex = """^\s*(\d{10})(?:\.(\d{1,9}))?""".toRegex()
private val dropboxHeaderRegex = """^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})(?:\.\d+)?\s+(data_app_anr|system_app_anr)\b""".toRegex()
private val ignoredAnrSignals = mutableSetOf<String>()

private var lastAnrLogcatCheckTime = 0L
private var lastAnrDropboxCheckTime = 0L
private var lastAnrOcrAttemptTime = 0L
private var lastAnrOcrAttemptSignal = ""
private var lastHandledAnrSignal = ""

private data class AnrSignal(
    val source: String,
    val signature: String
)


suspend fun checkReconnections(): Boolean {
    checkAnrDialog()
    checkPrivacy()

    val homeSchemas = listOf(
        MyColors.Reconnection, MyColors.ReconnectionOnCloudPhone, MyColors.ReconnectionOnOppoA57, MyColors.ReconnectionOnLeiDian, MyColors.RatingOnCloudPhone, MyColors.RatingOnCloudPhone2
    )
    var screenBuffer = captureOrRestart()

    // Set to true when action == 1; deferred outside the lambda to avoid non-local return issues
    var needReturnFalse = false

    homeSchemas.any { schema ->
        val match = findMultiColors(byteBuffer = screenBuffer, schema = schema)
        if (match != null) {
            // Re-capture and count white pixels to reject false positives
            delayWithMultiplier(200)
            screenBuffer = captureOrRestart()
            val whiteCount = countWhitePixels(screenBuffer)
            if (whiteCount <= 500) {
                accountMessage("检测到假重连，白色像素个数$whiteCount")
                delayWithMultiplier(200)
                return@any false // Not enough white pixels, continue checking remaining schemas
            }
            accountMessage("检测到重连，白色像素个数$whiteCount")

            // Use ML Kit to detect rating/review dialogue text in the same area as countWhitePixels
            val recognizedTexts = TextRecognizer.recognize(
                startX = 270, startY = 230, endX = 1010, endY = 430, useChinese = true, threshold = 140, applyPreprocess = true, invertBinarization = true
            )
            val recognizedTextString = recognizedTexts.joinToString(" ") { it.text }
            val isRatingDialog = recognizedTextString.contains("评分") || recognizedTextString.contains("反馈意见")

            val configKey = Schema.GLOBAL_SETTINGS.AFTER_KICK_OPTION.key
            val action = GlobalVars.configStates[configKey]?.value?.toInt() ?: logAndRestart("Can not get $configKey")
            if (isRatingDialog) {
                accountMessage("检测到评分/反馈对话框，执行重新提取存档，识别文本: $recognizedTextString")
                TouchActions.tap(793, 459)
                reExtractGameSavings()
                return@any true
            }
            when (action) {
                0 -> {
                    // Tap the reload button, rating notification, and tutorial confirm
                    TouchActions.tap(379, 458)
                    TouchActions.tap(338, 510, delayTime = 10)
                    TouchActions.tap(338, 520)
                }

                1 -> needReturnFalse = true // Signal caller to switch accounts
                2 -> delay(1000)            // Wait/Idle
            }
            true
        } else {
            false // No match for this schema, continue checking the remaining ones
        }
    }

    return !needReturnFalse
}

private suspend fun captureOrRestart(): ScreenCaptureManager.CaptureResult =
    ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at reconnection check")

private suspend fun checkAnrDialog(): Boolean {
    val anrSignal = findRecentAnrSignal() ?: return false
    val signalKey = "${anrSignal.source}:${anrSignal.signature}"
    if (signalKey == lastHandledAnrSignal || ignoredAnrSignals.contains(signalKey)) return false

    val now = System.currentTimeMillis()
    if (signalKey == lastAnrOcrAttemptSignal && now - lastAnrOcrAttemptTime < ANR_OCR_RETRY_INTERVAL_MS) return false
    lastAnrOcrAttemptSignal = signalKey
    lastAnrOcrAttemptTime = now

    val handled = clickWaitOnAnrDialog(anrSignal)
    if (handled) {
        lastHandledAnrSignal = signalKey
    } else {
        rememberIgnoredAnrSignal(signalKey)
    }
    return handled
}

private suspend fun findRecentAnrSignal(): AnrSignal? {
    val packageName = InGameVars.currentGameVersion.packageName
    val now = System.currentTimeMillis()

    if (now - lastAnrLogcatCheckTime >= ANR_LOGCAT_CHECK_INTERVAL_MS) {
        lastAnrLogcatCheckTime = now
        val logcatSignal = findRecentAnrFromLogcat(packageName)
        if (logcatSignal != null) return logcatSignal
    }

    if (now - lastAnrDropboxCheckTime >= ANR_DROPBOX_CHECK_INTERVAL_MS) {
        lastAnrDropboxCheckTime = now
        return findRecentAnrFromDropbox(packageName)
    }

    return null
}

private suspend fun findRecentAnrFromLogcat(packageName: String): AnrSignal? {
    val packageArg = shellSingleQuote(packageName)
    val eventCommand = "logcat -b events -d -t $ANR_LOGCAT_LINE_LIMIT -v epoch 2>/dev/null | grep -F 'am_anr' | grep -F $packageArg | tail -n 1"
    val eventSignature = RunShell.run(eventCommand, checkIsPlaying = false).joinToString(" ").trim()
    if (eventSignature.isNotBlank() && isRecentLogcatLine(eventSignature)) return AnrSignal("logcat-events", eventSignature)

    val textCommand = "logcat -d -t $ANR_LOGCAT_LINE_LIMIT -v epoch 2>/dev/null | grep -F $packageArg | grep -F 'ANR in' | tail -n 1"
    val textSignature = RunShell.run(textCommand, checkIsPlaying = false).joinToString(" ").trim()
    if (textSignature.isNotBlank() && isRecentLogcatLine(textSignature)) return AnrSignal("logcat", textSignature)

    return null
}

private suspend fun findRecentAnrFromDropbox(packageName: String): AnrSignal? {
    val command = "(dumpsys dropbox --print data_app_anr 2>/dev/null; dumpsys dropbox --print system_app_anr 2>/dev/null) | tail -n $ANR_DROPBOX_TAIL_LINE_LIMIT"
    val lines = RunShell.run(command, checkIsPlaying = false)
    val signature = findRecentDropboxSignature(lines, packageName) ?: return null
    return AnrSignal("dropbox", signature)
}

private fun findRecentDropboxSignature(lines: List<String>, packageName: String): String? {
    var currentEntryHeader = ""
    var currentEntryIsRecent = false
    var latestSignature: String? = null

    lines.forEach { line ->
        val headerTime = parseDropboxHeaderTime(line)
        if (headerTime != null) {
            currentEntryHeader = line.trim()
            currentEntryIsRecent = isRecentSignalTime(headerTime)
        }

        if (currentEntryIsRecent && line.contains(packageName)) {
            latestSignature = "$currentEntryHeader | ${line.trim()}"
        }
    }

    return latestSignature?.takeIf { it.isNotBlank() }
}

private fun isRecentLogcatLine(line: String): Boolean {
    val signalTime = parseLogcatEpochTime(line) ?: return false
    return isRecentSignalTime(signalTime)
}

private fun parseLogcatEpochTime(line: String): Long? {
    val match = logcatEpochRegex.find(line) ?: return null
    val seconds = match.groupValues[1].toLongOrNull() ?: return null
    val millis = match.groupValues[2]
        .padEnd(3, '0')
        .take(3)
        .toLongOrNull() ?: 0L
    return seconds * 1000L + millis
}

private fun parseDropboxHeaderTime(line: String): Long? {
    val timestamp = dropboxHeaderRegex.find(line)?.groupValues?.getOrNull(1) ?: return null
    return runCatching {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(timestamp)?.time
    }.getOrNull()
}

private fun isRecentSignalTime(signalTime: Long): Boolean {
    if (signalTime < anrDetectorStartTime) return false
    val age = System.currentTimeMillis() - signalTime
    return age in 0..ANR_SIGNAL_RECENT_WINDOW_MS
}

private fun rememberIgnoredAnrSignal(signalKey: String) {
    if (ignoredAnrSignals.size > 20) ignoredAnrSignals.clear()
    ignoredAnrSignals.add(signalKey)
}

private suspend fun clickWaitOnAnrDialog(anrSignal: AnrSignal): Boolean {
    val recognizedTexts = TextRecognizer.recognize(
        startX = ANR_DIALOG_LEFT,
        startY = ANR_DIALOG_TOP,
        endX = ANR_DIALOG_RIGHT,
        endY = ANR_DIALOG_BOTTOM,
        useChinese = true,
        threshold = 180,
        applyPreprocess = false,
        invertBinarization = true
    )

    if (recognizedTexts.isEmpty()) {
        accountMessage("检测到最近游戏ANR(${anrSignal.source})，但OCR未识别到文本")
        return false
    }

    val recognizedTextString = recognizedTexts.joinToString(" ") { it.text }
    val compactText = recognizedTextString.replace(" ", "")
    val isAnrDialog = compactText.contains("没有响应")
            || compactText.contains("无响应")
            || compactText.contains("未响应")
            || compactText.contains("停止响应")
            || (compactText.contains("等待") && (compactText.contains("关闭应用") || compactText.contains("反馈")))

    if (!isAnrDialog) {
        accountMessage("检测到最近游戏ANR(${anrSignal.source})，但当前屏幕不是ANR对话框，识别文本: $recognizedTextString")
        return false
    }

    val waitButton = recognizedTexts.firstOrNull { recognizedText ->
        recognizedText.text.replace(" ", "").contains("等待")
    }

    if (waitButton?.position != null) {
        val waitButtonX = ANR_DIALOG_LEFT + waitButton.position.centerX()
        val waitButtonY = ANR_DIALOG_TOP + waitButton.position.centerY()
        accountMessage("检测到游戏ANR(${anrSignal.source})，点击等待，识别文本: $recognizedTextString")
        TouchActions.tap(waitButtonX, waitButtonY, delayTime = 800)
        return true
    }

    accountMessage("检测到游戏ANR(${anrSignal.source})，但未识别到等待按钮，识别文本: $recognizedTextString")
    return false
}

private fun shellSingleQuote(value: String): String {
    return "'" + value.replace("'", "'\\''") + "'"
}

/**
 * Counts pixels in the confirmation area (270,230)→(1010,430) where R,G,B > 235 (white).
 * Uses absolute ByteBuffer.get(index) so it is position-independent.
 */
private fun countWhitePixels(screenBuffer: ScreenCaptureManager.CaptureResult): Int {
    val buf = screenBuffer.buffer
    val pixelStride = screenBuffer.pixelStride
    val rowStride = screenBuffer.rowStride
    var count = 0
    for (y in 230 until 430) {
        for (x in 270 until 1010) {
            val offset = y * rowStride + x * pixelStride
            val r = buf.get(offset).toInt() and 0xFF
            val g = buf.get(offset + 1).toInt() and 0xFF
            val b = buf.get(offset + 2).toInt() and 0xFF
            if (r > 235 && g > 235 && b > 235) count++
        }
    }
    return count
}

private suspend fun checkPrivacy() {
    val point = findMultiColors(schema = MyColors.PrivacyInfo)
    if (point != null) {
        TouchActions.tap(point.x, point.y, delayTime = 200)
        reExtractGameSavings()
    }
}

suspend fun reExtractGameSavings() {
    accountMessage("重新提取存档中")
    // 1. Configuration items
    val packageName = "com.supercell.clashofclans"
    val folderName = "zkqGlobalGameSave"
    val targetSubDirs = listOf("shared_prefs")

    // 2. Environment path preparation
    val suffix = InGameVars.currentAccountNumber
    val sdPath = Environment.getExternalStorageDirectory().path

    val gameRootDir = "$sdPath/zkqFiles/$folderName"
    val targetSaveDir = "$gameRootDir/$suffix"

    // 3. Core modification: if folder exists, delete it directly for re-extraction
    // Use [ -d ] to check if directory exists, if yes execute rm -rf
    val cleanCmd = "[ -d \"$targetSaveDir\" ] && rm -rf \"$targetSaveDir\""
    RunShell.run(cleanCmd)

    // 4. Create directory structure (need to recreate after rm)
    RunShell.run("mkdir -p \"$targetSaveDir\"")

    // 5. Execute data copy
    targetSubDirs.forEach { dir ->
        val destPath = "$targetSaveDir/$dir"
        // Create target subdirectory
        RunShell.run("mkdir -p \"$destPath\"")

        // 使用 Root 权限从 /data/data/ 复制到 SD 卡
        // 注意：这里拷贝的是内容，建议保留目录权限或结构
        val copyCmd = "cp -r /data/data/$packageName/$dir/* \"$destPath\""
        RunShell.run(copyCmd)
    }
}
