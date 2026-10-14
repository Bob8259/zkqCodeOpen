package com.coc.zkqcode.jar.code.universal.tutorial

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

object AllTutorials {

    private const val CONFIRM_TEXT = "同意"
    private const val CONFIRM_OCR_INTERVAL_MILLIS = 1_000L
    private var lastConfirmOcrTime = 0L

    suspend fun allBaseTutorial(): Boolean {
        val durationMillis = 500_000L
        val startTime = System.currentTimeMillis()

        while (currentCoroutineContext().isActive) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime

            if (elapsed >= durationMillis) return false

            val remainingSeconds = ((durationMillis - elapsed) / 1000).toInt()
            accountMessage("教程中，还剩${remainingSeconds}秒")
            if (isGameAtFront()) {
                if (mainBaseTutorial()) return true
                if (builderBaseTutorial()) return true

                // Maintenance checks
                if (!checkReconnections()) return false
                // Tutorial text checks
                tapConfirmButtonIfFound()

            } else {
                runGame()
            }
            delayWithMultiplier(200)
        }
        return false
    }

    /**
     * Recognizes the full screen with ML Kit and taps the center of a "确定" button if found.
     */
    private suspend fun tapConfirmButtonIfFound() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastConfirmOcrTime < CONFIRM_OCR_INTERVAL_MILLIS) return
        lastConfirmOcrTime = currentTime

        val screenBitmap = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(
            asBitmap = true, errorMessage = "Tutorial confirm OCR: failed to capture screen"
        )
        val recognizedTexts = TextRecognizer.recognize(
            startX = 0, startY = 0, endX = screenBitmap.width, endY = screenBitmap.height, useChinese = true, applyPreprocess = false, screenBuffer = screenBitmap
        )
        recognizedTexts.forEach { recognizedText ->
            if (!recognizedText.text.replace(" ", "").contains(CONFIRM_TEXT)) return@forEach

            val confirmPosition = recognizedText.position ?: return@forEach
            showDebugInfo("Tutorial confirm OCR: tapping '${recognizedText.text}' at ${confirmPosition.centerX()}, ${confirmPosition.centerY()}")
            TouchActions.tap(confirmPosition.centerX(), confirmPosition.centerY())
        }
    }

    /**
     * Checks if the current UI state matches known tutorial schemas.
     * If a match is found, initiates the main tutorial sequence.
     */
    suspend fun checkIsInTutorial(times: Int): Boolean {
        val targetSchemas = listOf(
            MyColors.SpeakingVillager, MyColors.SpeakingVillager2, MyColors.EnterAge, MyColors.BuilderMaster, MyColors.CapitalOldMan
        )

        val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(
            asBitmap = false, errorMessage = "failed to take screenshot at close checkIsInTutorial"
        )
        // Iterate through schemas and find the first match to retrieve its coordinates
        val point = targetSchemas.firstNotNullOfOrNull { schema ->
            findMultiColors(byteBuffer = screenBuffer, schema = schema)
        }
        // If a point is found, it means a tutorial element is on screen
        if (point != null) {
            if (times < 3) {
                // New logic: Perform a direct tap if times is less than 5
                TouchActions.tap(point.x, point.y)
            } else if (times < 6) {
                TouchActions.tap(1195, 630)//Tap worker, skip new update worker message
            } else {
                // Existing logic: Trigger the complex tutorial sequence if times is 10 or more
                allBaseTutorial()
            }
            return true
        }

        return false
    }
}