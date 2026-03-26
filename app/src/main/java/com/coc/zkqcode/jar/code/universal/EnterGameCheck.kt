package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.runGame
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay
import kotlin.random.Random


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
    // Counter to throttle clickRightBottom to roughly every 5 seconds
    var clickRightBottomCounter = 20
    GlobalVars.absorbEdge = 0
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        // 3. Insert your logic to check if the main screen is actually visible
        if (!isGameAtFront()) {
            runGame()
        } else {
            if (isInHomePage()) {
                if (!isDoubleCheck) {
                    ShowMessage("已进入主界面")
                    return true
                }
                delay(800)
                if (isInHomePage()) {
                    return true
                }
            }
            if (!checkReconnections()) return false
            ShowMessage("账号${InGamesVars.currentAccountNumber}，倒计时${((timeoutMillis - System.currentTimeMillis() + startTime) / 1000).toInt()}秒")
            closeAdvertisements()

            // Click right bottom roughly every 5 seconds
            if (clickRightBottomCounter++ >= 15) {
                clickRightBottom(5)
                clickRightBottomCounter = 0
            }

            if (AllTutorials.checkIsInTutorial(mainBaseTutorialElements)) mainBaseTutorialElements++
        }
        // 4. Wait before checking again to save CPU cycles
        delay(1000)
    }

    // Return false if the loop finishes without finding the main screen
    killGame()
    return false
}


suspend fun clickRightBottom(times: Int, delayTime: Int = 50) {
    repeat(times) {
        TouchActions.tap(1279, 100, false, delayTime)
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
    )

    // 3. Iterate through schemas
    homeSchemas.forEach { schema ->
        // Check if the current schema exists on the current screenBuffer
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema, increment = 1)

        if (point != null) {
            // If found, perform the tap
            TouchActions.tap(point.x, point.y, delayTime = 1000)
            // 4. Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return@forEach // Use return@forEach to skip to next if capture fails
        }
    }
    // Reuse screenBuffer for remaining checks; refresh after each tap
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.UpgradeTHArrow, increment = 1)?.let {
        TouchActions.tap(it.x + 50, it.y + 100, delayTime = 500)
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.DailyLoginReward, increment = 1)?.let {
        if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_DAILY_REWARD.key)) {
            TouchActions.tap(622, 492, delayTime = 2000)
        } else {
            TouchActions.tap(it.x, it.y)
        }
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return
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
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.CancelEditMode, increment = 1)?.let {
        TouchActions.tap(it.x, it.y, delayTime = 500)
        TouchActions.tap(788, 464)
        screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: return
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TrainingPage, increment = 1)?.let {
        TouchActions.tap(219, 139, delayTime = 1000)//close training tap
        TouchActions.tap(1232, 65, delayTime = 300)//close training page
    }
}

private suspend fun isInHomePage(): Boolean {
    // 1. Capture the screen and cast safely
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in isInHomePage, screen capture failed.")

    // 2. Check for the training button presence
    val hasTrainButton = findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TrainTroops, increment = 1) != null
    if (!hasTrainButton) return false

    // 3. Check for any of the worker icons (Main base, Goblin workers, or Builder base)
    val workerSchemas = listOf(
        MyColors.MainBaseWorker, MyColors.MainBaseWorker2, MyColors.GoblinWorker, MyColors.GoblinResearcher, MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2
    )

    return workerSchemas.any { findMultiColors(byteBuffer = screenBuffer, schema = it, increment = 1) != null }
}