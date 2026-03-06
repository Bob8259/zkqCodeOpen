package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

object AllTutorials {

    suspend fun allBaseTutorial() {
        val durationMillis = 300_000L
        val startTime = System.currentTimeMillis()

        while (currentCoroutineContext().isActive) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime

            if (elapsed >= durationMillis) break

            val remainingSeconds = ((durationMillis - elapsed) / 1000).toInt()
            ShowMessage("账户${InGamesVars.currentAccountNumber}\n教程中，还剩${remainingSeconds}秒")

            if (mainBaseTutorial()) break
            if (builderBaseTutorial()) break

            // Maintenance checks
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
            MyColors.SpeakingVillager2,
            MyColors.EnterAge,
            MyColors.BuilderMaster,
            MyColors.CapitalOldMan
        )

        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: LogHelper.logAndStop("failed to take screenshot at close checkIsInTutorial")
        // Iterate through schemas and find the first match to retrieve its coordinates
        val point = targetSchemas.firstNotNullOfOrNull { schema ->
            findMultiColors(byteBuffer = screenBuffer, schema = schema)
        }
        // If a point is found, it means a tutorial element is on screen
        if (point != null) {
            if (times < 10) {
                // New logic: Perform a direct tap if times is less than 10
                TouchActions.tap(point.x, point.y, delayTime = 500)
            } else {
                // Existing logic: Trigger the complex tutorial sequence if times is 10 or more
                allBaseTutorial()
            }
            return true
        }

        return false
    }
}