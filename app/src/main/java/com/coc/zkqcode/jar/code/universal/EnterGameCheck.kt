package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.inputmethod.inputTextReliable
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseDeployTroops
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getStaticConfig
import com.coc.zkqcode.jar.code.universal.smalltools.input.setZKQInputMethod
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import com.coc.zkqcode.jar.ui.schema.Schema


/**
 * Waits for the game to enter the main screen within a specified timeout.
 * Returns true if successful, false if it times out.
 */
suspend fun enterMainScreen(isDoubleCheck: Boolean = false): Boolean {
    // 1. Initialize the start time
    val startTime = System.currentTimeMillis()
    // 2. Get the timeout duration from GlobalVars (assumed to be in seconds)
    // We multiply by 1000 to compare milliseconds to milliseconds
    val timeoutSeconds = GlobalVars.configStates["enter_game_timer"]?.value?.toIntOrNull() ?: logAndRestart("enter main game error, can not get game timer")
    val timeoutMillis = timeoutSeconds * 1000L
    var mainBaseTutorialElements = 0
    var lastClickRightBottomTime = startTime - 5000L
    GlobalVars.absorbEdge = 0
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        // 3. Insert your logic to check if the main screen is actually visible
        if (!isGameAtFront()) {
            runGame()
        } else {
            if (isInHomePage()) {
                if (!isDoubleCheck) {
                    accountMessage("已进入主界面")
                    return true
                }
                if (InGameVars.currentGameVersion == GameVersion.CN) delayWithMultiplier(1500)
                else delayWithMultiplier(800)
                if (isInHomePage()) {
                    return true
                }
            }

            accountMessage("倒计时${((timeoutMillis - System.currentTimeMillis() + startTime) / 1000).toInt()}秒")
            closeAdvertisements()
            // After 15s, also dismiss clan league popups
            if (System.currentTimeMillis() - startTime > 15_000L) closeClanLeaguePopups()

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickRightBottomTime >= 5000L) {
                clickRightBottom(10)
                if (!checkReconnections()) return false
                lastClickRightBottomTime = currentTime
            }

            if (AllTutorials.checkIsInTutorial(mainBaseTutorialElements)) mainBaseTutorialElements++
        }
        // 4. Wait before checking again to save CPU cycles
        delayWithMultiplier(500)
    }

    // Return false if the loop finishes without finding the main screen
    killGame()
    return false
}


suspend fun clickRightBottom(times: Int, delayTime: Int = 100) {
    repeat(times) {
        TouchActions.tap(1279, 100, false, delayTime)
    }
}


/**
 * Checks and dismisses clan league related popups (season info, blue info icon, claim reward).
 */
private suspend fun closeClanLeaguePopups() {
    var screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return
    listOf(
        MyColors.ClanLeagueSeasonInfo, MyColors.SmallBlueInfoIcon, MyColors.ClaimClanLeagueReward
    ).forEach { schema ->
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema, increment = 1)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
            // Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return@forEach
        }
    }
}

private suspend fun closeAdvertisements() {
    // 1. Capture the screen and cast safely (Use 'var' so we can update it)
    var screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.ExclusiveGift,
        MyColors.MorePointCoupon,
        MyColors.GreenConfirm,
        MyColors.CNAd,
        MyColors.ClanChat,
        MyColors.OldShopButton,
        MyColors.NewShopButton,
        MyColors.CNProsperity,
        MyColors.MagicalItem,
        MyColors.CNPuppetAd,
        MyColors.CNBackFromAwards,
        MyColors.CollectChest,
        MyColors.EditModeWrench,
        MyColors.MiddleGreenButton,
        MyColors.ExclusiveGift,
        MyColors.MorePointCoupon,
        MyColors.PassSkinAd,
        MyColors.PassAwardAd,
        MyColors.BackToCamp,
        MyColors.SkinAd,
        MyColors.CapitalBackToCamp,
        MyColors.CapitalBackToCamp2,
        MyColors.CapitalAirShip,
        MyColors.CapitalAirShip2,
        MyColors.AgreeTerms,
        MyColors.LogIn,
        MyColors.CloudPhoneShopButton,
        MyColors.TempGameContinue,
        MyColors.IncreaseRank,
        MyColors.ClanLeagueReturnCamp,
        MyColors.UpgradeNotification,
        MyColors.EnterLeague
    )

    // 3. Iterate through schemas
    homeSchemas.forEach { schema ->
        // Check if the current schema exists on the current screenBuffer
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema, increment = 1)

        if (point != null) {
            // If found, perform the tap
            TouchActions.tap(point.x, point.y)
            // 4. Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return@forEach // Use return@forEach to skip to next if capture fails
        }
    }
    // Reuse screenBuffer for remaining checks; refresh after each tap
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.UpgradeTHArrow, increment = 1)?.let {
        TouchActions.tap(it.x + 50, it.y + 100)
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.CollectElixirCartTutorial, increment = 1)?.let {
        TouchActions.tap(644, 377)
        repeat(10) {
            TouchActions.tap(946, 601, delayTime = 300)
        }

    }
    findMultiColorsUntil(byteBuffer = screenBuffer, schemas = listOf(MyColors.ReturnTask, MyColors.ReturnTask2, MyColors.ReturnTask3, MyColors.ReturnTask4), duration = 20, increment = 1)?.let {
        accountMessage("检测到回归教程")
        zoomSmallMainBase()
        mainBaseDeployTroops()
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TencentChildProtection, increment = 1)?.let {
        TouchActions.tap(1010, 85)
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.DailyLoginReward, increment = 1)?.let {
        if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_DAILY_REWARD.key)) {
            TouchActions.tap(622, 492, delayTime = 2000)
        } else {
            TouchActions.tap(it.x, it.y)
        }
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.ReturnAwards, increment = 1)?.let {
        // Define the coordinate pairs in order of execution
        val tapPoints = listOf(
            257 to 297, 464 to 307, 662 to 305, 267 to 512, 466 to 511, 654 to 515, 882 to 513, 1077 to 101
        )

        // Iterate through points to reduce code redundancy
        tapPoints.forEach { (x, y) ->
            TouchActions.tap(x, y, delayTime = 100)
        }
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.CancelEditMode, increment = 1)?.let {
        TouchActions.tap(it.x, it.y)
        TouchActions.tap(788, 464)
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("failed to take screenshot at close advertisement")
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TrainingPage, increment = 1)?.let {
        TouchActions.tap(219, 139, delayTime = 1000)//close training tap
        TouchActions.tap(1232, 65, delayTime = 300)//close training page
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.MyVillageIsCalled, increment = 1)?.let {
        setZKQInputMethod()
        TouchActions.tap(625, 300, delayTime = 200)

        var gameName = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_PREFIX.key)
        val addSuffix = getStaticConfig(Schema.GLOBAL_SETTINGS.ADD_SUFFIX_SETTING.key) == "1"

        if (addSuffix) {
            gameName += InGameVars.currentAccountNumber
        }

        inputTextReliable(gameName)
        TouchActions.tap(640, 370, delayTime = 300)
    }
}

suspend fun isInHomePage(): Boolean {
    // 1. Capture the screen and cast safely
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in isInHomePage, screen capture failed.")

    // 2. Check for the training button presence
    val hasTrainButton = findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TrainTroops, increment = 1) != null
    if (!hasTrainButton) return false

    // 3. Check for any of the worker icons (Main base, Goblin workers, or Builder base)
    val workerSchemas = MyColors.MainBaseWorkerSchemas + listOf(MyColors.GoblinWorker, MyColors.GoblinWorker2, MyColors.GoblinResearcher, MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2)

    return workerSchemas.any { findMultiColors(byteBuffer = screenBuffer, schema = it, increment = 1) != null }
}