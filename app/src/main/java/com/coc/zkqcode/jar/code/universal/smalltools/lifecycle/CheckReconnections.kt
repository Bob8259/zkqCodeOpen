package com.coc.zkqcode.jar.code.universal.smalltools.lifecycle

import android.graphics.Bitmap
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
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

private const val ANR_DIALOG_LEFT = 160
private const val ANR_DIALOG_TOP = 120
private const val ANR_DIALOG_RIGHT = 1120
private const val ANR_DIALOG_BOTTOM = 640


suspend fun checkReconnections(): Boolean {
    checkAnrDialog()
    checkPrivacy()

    val homeSchemas = listOf(
        MyColors.Reconnection, MyColors.ReconnectionOnCloudPhone, MyColors.ReconnectionOnOppoA57, MyColors.ReconnectionOnLeiDian, MyColors.ReconnectionOnSamsung, MyColors.RatingOnCloudPhone, MyColors.RatingOnCloudPhone2
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

            // Use ML Kit to detect rating/review dialogue text on the full screen.
            val ratingOcrLeft = 0
            val ratingOcrTop = 0
            val ratingBitmap = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)
                ?: logAndRestart("failed to take screenshot for rating OCR")
            val recognizedTexts = TextRecognizer.recognize(
                startX = ratingOcrLeft,
                startY = ratingOcrTop,
                endX = 1280,
                endY = 720,
                useChinese = true,
                applyPreprocess = false,
                screenBuffer = ratingBitmap
            )
            val recognizedTextString = recognizedTexts.joinToString(" ") { it.text }
            val isRatingDialog = recognizedTextString.contains("评分") || recognizedTextString.contains("反馈意见")

            val configKey = Schema.GLOBAL_SETTINGS.AFTER_KICK_OPTION.key
            val action = GlobalVars.configStates[configKey]?.value?.toInt() ?: logAndRestart("Can not get $configKey")
            if (isRatingDialog) {
                accountMessage("检测到评分/反馈对话框，执行重新提取存档，识别文本: $recognizedTextString")
                val neverShowAgainButton = recognizedTexts.firstOrNull { recognizedText ->
                    recognizedText.text.replace(" ", "").contains("不再提示")
                }
                if (neverShowAgainButton?.position != null) {
                    val neverShowAgainX = ratingOcrLeft + neverShowAgainButton.position.centerX()
                    val neverShowAgainY = ratingOcrTop + neverShowAgainButton.position.centerY()
                    TouchActions.tap(neverShowAgainX, neverShowAgainY)
                }
                val laterButton = recognizedTexts.firstOrNull { recognizedText ->
                    recognizedText.text.replace(" ", "").contains("稍后")
                }
                if (laterButton?.position != null) {
                    val laterX = ratingOcrLeft + laterButton.position.centerX()
                    val laterY = ratingOcrTop + laterButton.position.centerY()
                    TouchActions.tap(laterX, laterY)
                }
                // Always tap the hard-coded dismiss position as the final fallback
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
                2 -> delay(1000.milliseconds)            // Wait/Idle
            }
            true
        } else {
            false // No match for this schema, continue checking the remaining ones
        }
    }

    delayWithMultiplier(400)
    return !needReturnFalse
}

private suspend fun captureOrRestart(): ScreenCaptureManager.CaptureResult =
    ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false) ?: logAndRestart("failed to take screenshot at reconnection check")

/**
 * Detects an ANR ("Application Not Responding") dialog on screen using OCR only.
 * If a "Wait" button is recognized, taps it to dismiss the dialog.
 */
private suspend fun checkAnrDialog() {
    // 70% chance to skip the ANR check to reduce OCR overhead
    if (Random.nextInt(100) < 70) return

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

    if (recognizedTexts.isEmpty()) return

    val recognizedTextString = recognizedTexts.joinToString(" ") { it.text }
    val compactText = recognizedTextString.replace(" ", "")
    val isAnrDialog = compactText.contains("没有响应")
            || compactText.contains("无响应")
            || compactText.contains("未响应")
            || compactText.contains("停止响应")
            || (compactText.contains("等待") && (compactText.contains("关闭应用") || compactText.contains("反馈")))

    if (!isAnrDialog) return

    val waitButton = recognizedTexts.firstOrNull { recognizedText ->
        recognizedText.text.replace(" ", "").contains("等待")
    }

    if (waitButton?.position != null) {
        val waitButtonX = ANR_DIALOG_LEFT + waitButton.position.centerX()
        val waitButtonY = ANR_DIALOG_TOP + waitButton.position.centerY()
        accountMessage("检测到游戏ANR，点击等待，识别文本: $recognizedTextString")
        TouchActions.tap(waitButtonX, waitButtonY, delayTime = 800)
        return
    }

    accountMessage("检测到游戏ANR，但未识别到等待按钮，识别文本: $recognizedTextString")
}

/**
 * Counts pixels in the confirmation area (10,10)→(1200,690) where R,G,B > 235 (white).
 * Uses absolute ByteBuffer.get(index) so it is position-independent.
 */
private fun countWhitePixels(screenBuffer: ScreenCaptureManager.CaptureResult): Int {
    val buf = screenBuffer.buffer
    val pixelStride = screenBuffer.pixelStride
    val rowStride = screenBuffer.rowStride
    var count = 0
    for (y in 10 until 690) {
        for (x in 10 until 1200) {
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
