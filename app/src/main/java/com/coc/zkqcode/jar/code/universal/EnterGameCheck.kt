package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.runApp
import kotlinx.coroutines.delay


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
        //测试代码
        runTestCode()

        // 3. Insert your logic to check if the main screen is actually visible
        if (checkUIVisibility()) return true
        if (!checkReconnections()) return false
        ShowMessage("账号${InGamesVars.currentAccountNumber}，倒计时${((timeoutMillis - System.currentTimeMillis() + startTime) / 1000).toInt()}秒\n请手动给主世界和夜世界切换默认场景")
        closeAdvertisements()
        clickRightBottom()
        if (AllTutorials().checkIsInTutorial(mainBaseTutorialElements)) mainBaseTutorialElements++
        // 4. Wait for 1 second before checking again to save CPU cycles
        delay(300)
    }

    // Return false if the loop finishes without finding the main screen
    return false
}

private suspend fun runTestCode() {
    while (true) {
        AllTutorials().allBaseTutorial()
    }
}

suspend fun clickRightBottom() {
    TouchActions.tap(1277, 557)
}

private suspend fun checkUIVisibility(): Boolean {
    if (!isGameAtFront()) {
        when (InGamesVars.currentGamePackage) {
            0 -> {//国服
                runApp("com.tencent.tmgp.supercell.clashofclans")
            }

            1 -> {//国际服
                runApp("com.supercell.clashofclans")
            }

            2 -> {//私服
                runApp("com.supercell.clashofclans1")
            }
        }
    } else {
        if (isInHomePage()) {
            ShowMessage("已进入主界面")
            delay(500)
            if (isInHomePage()) {
                zoomSmallMainBase()
                return true
            }
        }
    }
    return false
}

suspend fun zoomSmallMainBase() {
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(300)
    swipe(218, 523, 939, 162)
}

suspend fun closeAdvertisements() {
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
        MyColors.CNPuppetAd
    )

    // 3. Iterate through schemas
    homeSchemas.forEach { schema ->
        // Check if the current schema exists on the current screenBuffer
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema)

        if (point != null) {
            // If found, perform the tap
            TouchActions.tap(point.x, point.y)

            // Wait for the animation/transition to finish
            delay(500)

            // 4. Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
                ?: return@forEach // Use return@forEach to skip to next if capture fails
        }
    }
}

suspend fun isInHomePage(): Boolean {
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