package com.coc.zkqcode.jar.code.mainbase

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getStaticConfig
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

        // Standard schemas that follow a simple "find and tap" pattern
        val prioritySchemas = listOf(
            MyColors.PrivacyInfo,
            MyColors.TutorialGoblinAttack,
            MyColors.VillagerAttack,
            MyColors.TutorialTrain,
            MyColors.AttackMap,
            MyColors.AttackGoblin,
            MyColors.TutorialUpgradeTownHall,
            MyColors.TutorialMagicalItem,
            MyColors.TutorialMagicalItemInner
        )
        var speakingCount = 0
        while (currentCoroutineContext().isActive) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime

            if (elapsed >= durationMillis) break

            val remainingSeconds = ((durationMillis - elapsed) / 1000).toInt()
            ShowMessage("主世界教程中，还剩${remainingSeconds}秒")
            val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
                ?: logAndStop("in isInHomePage, screen capture failed.")

            // 1. Process standard priority schemas (Find -> Tap)
            prioritySchemas.forEach { schema ->
                findMultiColors(byteBuffer = screenBuffer, schema = schema)?.let { point ->
                    TouchActions.tap(point.x, point.y)
                    delayWithMultiplier(500)
                }
            }

            // 2. Handle specific UI elements with complex or fixed-coordinate logic

            // Speaking Villager sequence
            findMultiColors(schema = MyColors.SpeakingVillager)?.let {
                speakingCount++
                // speaking count > 5 means that the tutorial is in the middle, not at the beggining. So we might need to attack a goblin
                // Thus, we need to use the new sequence to enter troop training page
                val sequence = if (speakingCount > 5) {
                    listOf(415 to 410, 706 to 554, 670 to 386, 706 to 554)
                } else {
                    listOf(415 to 410)
                }
                sequence.forEach { (x, y) ->
                    TouchActions.tap(x, y)
                    delayWithMultiplier(500)
                }
            }

            // Important Notice tap
            findMultiColors(schema = MyColors.ImportantNotice)?.let {
                TouchActions.tap(344, 510)
                delayWithMultiplier(500)
            }

            // Building logic with Gem speed-up check
            findMultiColors(schema = MyColors.TutorialBuildClick)?.let { point ->
                TouchActions.tap(point.x, point.y)
                delayWithMultiplier(500)
                val isSpeedUp = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_GEM_BUILD.key) == "1"
                if (isSpeedUp) {
                    TouchActions.tap(643, 556) // Use gem to speed up
                }
            }

            // Age Entry Workflow
            findMultiColors(schema = MyColors.EnterAge)?.let {
                delayWithMultiplier(500)
                val sequence = listOf(640 to 347, 640 to 347, 773 to 546)
                sequence.forEach { (x, y) ->
                    TouchActions.tap(x, y)
                    delayWithMultiplier(500)
                }
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

            // Wizard Attack / Blue Troop anti-stuck (Restart App)
            findMultiColors(schema = MyColors.TutorialBlueTroop)?.let {
                killApp("com.supercell.clashofclans")
                delayWithMultiplier(1000)
                runApp("com.supercell.clashofclans")
            }

            // Troop Training sequence
            findMultiColors(schema = MyColors.TutorialTrainInner)?.let {
                TouchActions.tap(666, 250)
                delayWithMultiplier(1000)
                repeat(25) {
                    TouchActions.tap(96, 490)
                    delayWithMultiplier(10)
                }
                repeat(3) {
                    TouchActions.tap(1231, 64) // Close training page
                    delayWithMultiplier(50)
                }
            }

            // Village Naming Logic
            findMultiColors(schema = MyColors.MyVillageIsCalled)?.let {
                setZKQInputMethod()
                TouchActions.tap(625, 297)
                delayWithMultiplier(200)

                var gameName = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_PREFIX.key)
                val addSuffix = getStaticConfig(Schema.GLOBAL_SETTINGS.ADD_SUFFIX_SETTING.key) == "1"

                if (addSuffix) {
                    gameName += InGamesVars.currentAccountNumber
                }

                ZKQInputMethodService.instance?.commitGameName(gameName)
                delayWithMultiplier(300)
                TouchActions.tap(641, 368)
            }

            // Tutorial Conclusion and Cleanup
            findMultiColors(schema = MyColors.TrainTroops)?.let {
                ShowMessage("教程结束，即将进行首尾工作")

                // Worker tap sequence
                TouchActions.tap(598, 43)
                delayWithMultiplier(300)
                repeat(3) {
                    TouchActions.tap(649, 672)
                    delayWithMultiplier(100)
                }

                // Token/Pass tap sequence
                TouchActions.tap(202, 668)
                delayWithMultiplier(300)
                repeat(10) {
                    TouchActions.tap(574, 47)
                    delayWithMultiplier(100)
                }
            }

            // Maintenance checks
            checkReconnections()
            delayWithMultiplier(100)
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
