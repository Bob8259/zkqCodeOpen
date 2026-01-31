package com.coc.zkqcode.jar.code.mainbase

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

class MainBaseTutorial {
    suspend fun mainBaseTutorial() {
        // 300 seconds converted to milliseconds
        val durationMillis = 300_000L
        val startTime = System.currentTimeMillis()

        // Static list of schemas to iterate through for simple tap actions
        val prioritySchemas = listOf(
            MyColors.SpeakingVillager,
            MyColors.PrivacyInfo,
            MyColors.TutorialBuildClick
        )

        // Using a while loop that checks both time and coroutine lifecycle
        while (currentCoroutineContext().isActive) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime

            // Break the loop if the duration has been exceeded
            if (elapsed >= durationMillis) break

            // Calculate remaining seconds once per iteration to avoid redundant math
            val remainingSeconds = ((durationMillis - elapsed) / 1000).toInt()
            ShowMessage("主世界教程中，还剩${remainingSeconds}秒")

            // Optimization: Handle priority schemas using a loop to reduce code duplication
            for (schema in prioritySchemas) {
                findMultiColors(schema = schema)?.let { point ->
                    TouchActions.tap(point.x, point.y)
                    delayWithMultiplier(500)
                }
            }

            findMultiColors(schema = MyColors.ImportantNotice)?.let {
                TouchActions.tap(344, 510)
                delayWithMultiplier(500)
            }
            // Logic for EnterAge requires specific hardcoded coordinates upon detection
            findMultiColors(schema = MyColors.EnterAge)?.let {
                // Sequential taps for age entry workflow
                TouchActions.tap(640, 347)
                delayWithMultiplier(500)
                TouchActions.tap(633, 539)
                delayWithMultiplier(500)
                TouchActions.tap(773, 546)
                delayWithMultiplier(500)
            }
            findMultiColors(schema = MyColors.ShopArrow)?.let {
                //Enter shop
                TouchActions.tap(1193, 632)
                delayWithMultiplier(2500)
            }

            findMultiColors(schema = MyColors.ShopArrow)?.let {
                //Enter shop
                TouchActions.tap(it.x - 100, it.y + 50)
                delayWithMultiplier(2500)
            }
            checkReconnections()
            delayWithMultiplier(200)
        }
    }

    /**
     * Checks if the current UI state matches known tutorial schemas.
     * If a match is found, initiates the main tutorial sequence.
     */
    suspend fun checkIsInTutorial(times: Int): Boolean {
        val targetSchemas = listOf(
            MyColors.SpeakingVillager,
            MyColors.EnterAge
        )

        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndStop("failed to take screenshot at close checkIsInTutorial")
        // Iterate through schemas and find the first match to retrieve its coordinates
        val point = targetSchemas.firstNotNullOfOrNull { schema ->
            findMultiColors(byteBuffer = screenBuffer, schema = schema)
        }
        // If a point is found, it means a tutorial element is on screen
        if (point != null) {
            if (times < 10) {
                // New logic: Perform a direct tap if times is less than 10
                TouchActions.tap(point.x, point.y)
                delayWithMultiplier(500)
            } else {
                // Existing logic: Trigger the complex tutorial sequence if times is 10 or more
                mainBaseTutorial()
            }
            return true
        }

        return false
    }
}
