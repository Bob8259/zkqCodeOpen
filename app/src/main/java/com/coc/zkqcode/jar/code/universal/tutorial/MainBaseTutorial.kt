package com.coc.zkqcode.jar.code.universal.tutorial

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.getStaticConfig
import com.coc.zkqcode.jar.code.universal.smalltools.reExtractGameSavings
import com.coc.zkqcode.jar.code.universal.smalltools.setZKQInputMethod
import com.coc.zkqcode.jar.ui.schema.Schema


suspend fun mainBaseTutorial(): Boolean {
    // Standard schemas that follow a simple "find and tap" pattern
    val prioritySchemas = listOf(
        MyColors.PrivacyInfo,
        MyColors.TutorialGoblinAttack,
        MyColors.VillagerAttack,
        MyColors.TutorialTrain,
        MyColors.AttackGoblin,
        MyColors.TutorialMagicalItem,
        MyColors.TutorialMagicalItemInner,
        MyColors.MainBackToCamp
    )

    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: logAndRestart("in isInHomePage, screen capture failed.")

    // 1. Process standard priority schemas (Find -> Tap)
    prioritySchemas.forEach { schema ->
        findMultiColors(byteBuffer = screenBuffer, schema = schema)?.let { point ->
            TouchActions.tap(point.x, point.y, delayTime = 500)
        }
    }

    // Speaking Villager sequence
    findMultiColors(schema = MyColors.SpeakingVillager)?.let {
        delayWithMultiplier(600)
        TouchActions.tap(it.x, it.y, delayTime = 500)
    }
    findMultiColors(schema = MyColors.UpgradeTHArrow)?.let {
        TouchActions.tap(it.x + 50, it.y + 100, delayTime = 500)
        TouchActions.tap(703, 570, delayTime = 500)
    }
    // Important Notice tap
    findMultiColors(schema = MyColors.ImportantNotice)?.let {
        TouchActions.tap(344, 510, delayTime = 500)
    }
    findMultiColors(schema = MyColors.AttackMap)?.let {
        if (findMultiColors(schema = MyColors.TrainTroops) == null && findMultiColors(schema = MyColors.ShopAfterTutorial) == null) {
            TouchActions.tap(it.x, it.y, delayTime = 500)
        }
    }
    // Building logic with Gem speed-up check
    findMultiColors(schema = MyColors.TutorialBuildClick)?.let { point ->
        TouchActions.tap(point.x, point.y, delayTime = 500)
        val isSpeedUp = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_GEM_BUILD.key) == "1"
        if (isSpeedUp) {
            TouchActions.tap(643, 556) // Use gem to speed up
        }
    }

    // Age Entry Workflow
    findMultiColors(schema = MyColors.EnterAge)?.let {
        val sequence = listOf(640 to 347, 640 to 347, 773 to 546)
        sequence.forEach { (x, y) ->
            TouchActions.tap(x, y, delayTime = 500)
        }
    }

    // Shop Navigation
    findMultiColors(schema = MyColors.TutorialShop)?.let {
        if (findMultiColors(schema = MyColors.TrainTroops) == null && findMultiColors(schema = MyColors.ShopAfterTutorial) == null) {
            TouchActions.tap(1193, 632, delayTime = 1500)
        }
    }

    // Dynamic Offset for Inner Shop
    findMultiColors(schema = MyColors.ShopInnerArrow)?.let {
        TouchActions.tap(it.x - 100, it.y + 50, delayTime = 500)

    }

    // Wizard Attack / Blue Troop anti-stuck (Restart App)
    // Check for the multi-color schema before executing the sequence
    findMultiColors(schema = MyColors.TutorialBlueTroop)?.let {
        // Define the tap sequence as a list of pairs (x, y)
        val tapPoints = listOf(
            148 to 650,
            759 to 336,
            840 to 263,
            142 to 173,
            1161 to 168,
            589 to 243,
            735 to 85,
            589 to 243,
            735 to 85,
            589 to 243,
            735 to 85,
            589 to 243,
            735 to 85,
        )
        tapPoints.forEachIndexed { _, (x, y) ->
            TouchActions.tap(x, y, delayTime = 100)
        }
    }
    findMultiColors(schema = MyColors.TutorialUpgradeTownHall)?.let {
        TouchActions.tap(it.x, it.y, delayTime = 500)
        val isSpeedUp = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_GEM_BUILD.key) == "1"
        if (isSpeedUp) {
            TouchActions.tap(708, 549) // Use gem to speed up
        }
    }

    // Troop Training sequence
    findMultiColors(schema = MyColors.TutorialTrainInner)?.let {
        TouchActions.tap(666, 250, delayTime = 1000)
        repeat(25) {
            TouchActions.tap(96, 490, delayTime = 10)
        }
        repeat(3) {
            TouchActions.tap(1231, 64, delayTime = 50) // Close training page
        }
    }

    // Village Naming Logic
    findMultiColors(schema = MyColors.MyVillageIsCalled)?.let {
        setZKQInputMethod()
        TouchActions.tap(625, 297, delayTime = 200)

        var gameName = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_PREFIX.key)
        val addSuffix = getStaticConfig(Schema.GLOBAL_SETTINGS.ADD_SUFFIX_SETTING.key) == "1"

        if (addSuffix) {
            gameName += InGamesVars.currentAccountNumber
        }

        ZKQInputMethodService.instance?.commitGameName(gameName) ?: logAndRestart("获取输入法失败")
        TouchActions.tap(641, 368, delayTime = 300)
    }

    // Tutorial Conclusion and Cleanup
    findMultiColors(schema = MyColors.TrainTroops)?.let {
        ShowMessage("教程结束，即将进行首尾工作")

        // Worker tap sequence
        TouchActions.tap(598, 43, delayTime = 300)
        repeat(5) {
            TouchActions.tap(649, 672, delayTime = 300)
        }

        // Token/Pass tap sequence
        TouchActions.tap(202, 668, delayTime = 300)
        repeat(10) {
            TouchActions.tap(574, 47, delayTime = 300)
        }
        reExtractGameSavings()
        return true
    }
    findMultiColors(schema = MyColors.ReturnAwards)?.let {
        return true
    }

    return false
}

