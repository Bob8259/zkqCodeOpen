package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.runGame
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import kotlinx.coroutines.delay
import kotlin.random.Random


/**
 * Waits for the game to enter the main screen within a specified timeout.
 * Returns true if successful, false if it times out.
 */
suspend fun enterMainScreen(): Boolean {
    // 1. Initialize the start time
    val startTime = System.currentTimeMillis()
    // 2. Get the timeout duration from GlobalVars (assumed to be in seconds)
    // We multiply by 1000 to compare milliseconds to milliseconds
    val timeoutSeconds = GlobalVars.configStates["enter_game_timer"]?.value?.toIntOrNull()
        ?: logAndStop("enter main game error, can not get game timer")
    val timeoutMillis = timeoutSeconds * 1000L
    var mainBaseTutorialElements = 0
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        // 3. Insert your logic to check if the main screen is actually visible
        if (checkUIVisibility()) return true
        if (!checkReconnections()) return false
        ShowMessage("账号${InGamesVars.currentAccountNumber}，倒计时${((timeoutMillis - System.currentTimeMillis() + startTime) / 1000).toInt()}秒\n请手动给主世界和夜世界切换默认场景")
        closeAdvertisements()

        if (Random.nextDouble() > 0.7) {
            clickRightBottom(3)
        }
        if (AllTutorials.checkIsInTutorial(mainBaseTutorialElements)) mainBaseTutorialElements++
        // 4. Wait before checking again to save CPU cycles
        delay(150)
    }

    // Return false if the loop finishes without finding the main screen
    return false
}


suspend fun clickRightBottom(times: Int, delayTime: Int = 50) {
    repeat(times) {
        TouchActions.tap(1277, 557, delayTime = delayTime)
    }
}

private suspend fun checkUIVisibility(): Boolean {
    if (!isGameAtFront()) {
        runGame()
    } else {
        if (isInHomePage()) {
            delay(800)
            if (isInHomePage()) {
                ShowMessage("已进入主界面")
                return true
            }
        }
    }
    return false
}


private suspend fun closeAdvertisements() {
    // 1. Capture the screen and cast safely (Use 'var' so we can update it)
    var screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: logAndStop("failed to take screenshot at close advertisement")

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
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

        )

    // 3. Iterate through schemas
    homeSchemas.forEach { schema ->
        // Check if the current schema exists on the current screenBuffer
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema)

        if (point != null) {
            // If found, perform the tap
            TouchActions.tap(point.x, point.y, delayTime = 1000)

            // 4. Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
                ?: return@forEach // Use return@forEach to skip to next if capture fails
        }
    }
    findMultiColors(schema = MyColors.ReturnAwards)?.let {
        // Define the coordinate pairs in order of execution
        val tapPoints = listOf(
            257 to 297,
            464 to 307,
            662 to 305,
            267 to 512,
            466 to 511,
            654 to 515,
            882 to 513,
            1077 to 101
        )

        // Iterate through points to reduce code redundancy
        tapPoints.forEach { (x, y) ->
            TouchActions.tap(x, y, delayTime = 100)
        }
    }
    findMultiColors(schema = MyColors.CancelEditMode)?.let {
        TouchActions.tap(it.x, it.y, delayTime = 500)
        TouchActions.tap(788, 464)
    }
    findMultiColors(schema = MyColors.TrainingPage)?.let {
        TouchActions.tap(219, 139, delayTime = 1000)//close training tap
        TouchActions.tap(1232, 65, delayTime = 300)//close training page
    }
}

private suspend fun isInHomePage(): Boolean {
    // 1. Capture the screen and cast safely
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: logAndStop("in isInHomePage, screen capture failed.")

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.TrainTroops
    )

    // 3. Use 'any' for a clean, declarative exit
    return homeSchemas.any { schema ->
        findMultiColors(byteBuffer = screenBuffer, schema = schema) != null
    }
}