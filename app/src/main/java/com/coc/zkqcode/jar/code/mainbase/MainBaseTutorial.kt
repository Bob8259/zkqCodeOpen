package com.coc.zkqcode.jar.code.mainbase

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.killApp
import com.coc.zkqcode.jar.code.universal.smalltools.runApp
import com.coc.zkqcode.jar.code.universal.smalltools.setZKQInputMethod
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

class MainBaseTutorial {
    suspend fun mainBaseTutorial() {
        val durationMillis = 300_000L
        val startTime = System.currentTimeMillis()

        // Expanded list of schemas that follow the standard find -> tap(point) pattern
        val prioritySchemas = listOf(
            MyColors.PrivacyInfo,
            MyColors.TutorialBuildClick,
            MyColors.TutorialGoblinAttack,
            MyColors.VillagerAttack,
            MyColors.TutorialTrain,
            MyColors.AttackMap,
            MyColors.AttackGoblin
        )

        while (currentCoroutineContext().isActive) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime

            if (elapsed >= durationMillis) break

            val remainingSeconds = ((durationMillis - elapsed) / 1000).toInt()
            ShowMessage("主世界教程中，还剩${remainingSeconds}秒")

            // 1. Process standard priority schemas
            for (schema in prioritySchemas) {
                findMultiColors(schema = schema)?.let { point ->
                    TouchActions.tap(point.x, point.y)
                    delayWithMultiplier(500)
                }
            }
            // Important Notice
            findMultiColors(schema = MyColors.SpeakingVillager)?.let {
                TouchActions.tap(415, 410)
                delayWithMultiplier(500)
                TouchActions.tap(706, 554)
                delayWithMultiplier(500)
            }
            // 2. Handle specific UI elements with fixed offset/coordinate requirements

            // Important Notice
            findMultiColors(schema = MyColors.ImportantNotice)?.let {
                TouchActions.tap(344, 510)
                delayWithMultiplier(500)
            }

            // Age Entry Workflow
            findMultiColors(schema = MyColors.EnterAge)?.let {
                TouchActions.tap(640, 347)
                delayWithMultiplier(500)
                TouchActions.tap(633, 539)
                delayWithMultiplier(500)
                TouchActions.tap(773, 546)
                delayWithMultiplier(500)
            }

            // Shop Navigation
            findMultiColors(schema = MyColors.TutorialShop)?.let {
                TouchActions.tap(1193, 632)
                delayWithMultiplier(1500)
            }

            // Dynamic Offset for Inner Shop
            findMultiColors(schema = MyColors.ShopInnerArrow)?.let { point ->
                TouchActions.tap(point.x - 100, point.y + 50)
                delayWithMultiplier(1500)
            }

            // Wizard Attack Sequence (Multiple taps for rapid interaction)
            findMultiColors(schema = MyColors.TutorialBlueTroop)?.let {
                killApp("com.supercell.clashofclans")
                delayWithMultiplier(1000)
                runApp("com.supercell.clashofclans")
            }


            findMultiColors(schema = MyColors.TutorialTrainInner)?.let {
                TouchActions.tap(666, 250)
                delayWithMultiplier(1000)
                repeat(25) {
                    TouchActions.tap(96, 490)
                    delayWithMultiplier(10)
                }
                repeat(3) {
                    TouchActions.tap(1231, 64)//close training page
                    delayWithMultiplier(50)
                }
            }
            findMultiColors(schema = MyColors.MyVillageIsCalled)?.let {
                setZKQInputMethod()
                TouchActions.tap(625, 297)
                delayWithMultiplier(200)
                val gameName = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.CREATE_PREFIX.key]?.value
                    ?: logAndStop("Can not get config for ${Schema.GLOBAL_SETTINGS.CREATE_PREFIX.key}")
                ZKQInputMethodService.instance?.commitGameName(gameName)
                delayWithMultiplier(300)
                TouchActions.tap(641, 368)
            }

            // 3. Maintenance checks
            checkReconnections()
            clickRightBottom()
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
