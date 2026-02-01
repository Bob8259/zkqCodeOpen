package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.getStaticConfig
import com.coc.zkqcode.jar.code.universal.smalltools.killApp
import com.coc.zkqcode.jar.code.universal.smalltools.reExtractGameSavings
import com.coc.zkqcode.jar.code.universal.smalltools.runApp
import com.coc.zkqcode.jar.code.universal.smalltools.setZKQInputMethod
import com.coc.zkqcode.jar.ui.schema.Schema

object MainBaseTutorial {

    private var speakingCount = 0

    suspend fun mainBaseTutorial() {
        // Standard schemas that follow a simple "find and tap" pattern
        val prioritySchemas = listOf(
            MyColors.PrivacyInfo,
            MyColors.TutorialGoblinAttack,
            MyColors.VillagerAttack,
            MyColors.TutorialTrain,
            MyColors.AttackMap,
            MyColors.AttackGoblin,
            MyColors.TutorialMagicalItem,
            MyColors.TutorialMagicalItemInner
        )

        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: LogHelper.logAndStop("in isInHomePage, screen capture failed.")

        // 1. Process standard priority schemas (Find -> Tap)
        prioritySchemas.forEach { schema ->
            findMultiColors(byteBuffer = screenBuffer, schema = schema)?.let { point ->
                TouchActions.tap(point.x, point.y)
                delayWithMultiplier(500)
            }
        }

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
            val point = findMultiColors(schema = MyColors.ShopAfterTutorial)
            if (point == null) {
                TouchActions.tap(1193, 632)
                delayWithMultiplier(1500)
            }
        }

        // Dynamic Offset for Inner Shop
        findMultiColors(schema = MyColors.ShopInnerArrow)?.let {
            TouchActions.tap(it.x - 100, it.y + 50)
            delayWithMultiplier(500)

        }

        // Wizard Attack / Blue Troop anti-stuck (Restart App)
        findMultiColors(schema = MyColors.TutorialBlueTroop)?.let {
            delayWithMultiplier(500)
            killApp("com.supercell.clashofclans")
            delayWithMultiplier(1000)
            runApp("com.supercell.clashofclans")
        }
        findMultiColors(schema = MyColors.TutorialUpgradeTownHall)?.let {
            TouchActions.tap(it.x, it.y)
            delayWithMultiplier(500)
            val isSpeedUp = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_GEM_BUILD.key) == "1"
            if (isSpeedUp) {
                TouchActions.tap(708, 549) // Use gem to speed up
            }
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

            ZKQInputMethodService.instance?.commitGameName(gameName) ?: logAndStop("获取输入法失败")
            delayWithMultiplier(300)
            TouchActions.tap(641, 368)
        }

        // Tutorial Conclusion and Cleanup
        findMultiColors(schema = MyColors.TrainTroops)?.let {
            ShowMessage("教程结束，即将进行首尾工作")

            // Worker tap sequence
            TouchActions.tap(598, 43)
            delayWithMultiplier(300)
            repeat(5) {
                TouchActions.tap(649, 672)
                delayWithMultiplier(300)
            }

            // Token/Pass tap sequence
            TouchActions.tap(202, 668)
            delayWithMultiplier(300)
            repeat(10) {
                TouchActions.tap(574, 47)
                delayWithMultiplier(300)
            }
            reExtractGameSavings()
        }
    }
}
