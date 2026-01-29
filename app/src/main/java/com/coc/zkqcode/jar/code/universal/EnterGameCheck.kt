package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.smalltools.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.runApp
import kotlinx.coroutines.delay


/**
 * Waits for the game to enter the main screen within a specified timeout.
 * Returns true if successful, false if it times out.
 */
suspend fun enterMainScreen(currentAccountNumber: Int, gamePackage: String): Boolean {
    // 1. Initialize the start time
    val startTime = System.currentTimeMillis()
    // 2. Get the timeout duration from GlobalVars (assumed to be in seconds)
    // We multiply by 1000 to compare milliseconds to milliseconds
    val timeoutSeconds = GlobalVars.configStates["enter_game_timer"]?.value?.toIntOrNull()
        ?: logAndStop("enter main game error, can not get game timer")
    val timeoutMillis = timeoutSeconds * 1000L

    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        // 3. Insert your logic to check if the main screen is actually visible
        if (checkUIVisibility(gamePackage)) return true
        ShowMessage("账号$currentAccountNumber，倒计时${((timeoutMillis - System.currentTimeMillis() + startTime) / 1000).toInt()}秒\n请手动给主世界和夜世界切换默认场景")
        // 4. Wait for 1 second before checking again to save CPU cycles
        delay(50)
    }

    // Return false if the loop finishes without finding the main screen
    return false
}

private suspend fun checkUIVisibility(gamePackage: String): Boolean {
    if (!isGameAtFront(gamePackage)) {
        if (gamePackage == "0") {//国服
            runApp("com.tencent.tmgp.supercell.clashofclans")
        } else if (gamePackage == "1") {//国际服
            runApp("com.supercell.clashofclans")
        }
        delay(1000)
    } else {
        if (isInHomePage()) {
            ShowMessage("已进入主界面")
            delay(500)
            if (isInHomePage()) {
                pinchIn(141, 423, 1052, 352, 638, 365)
                delay(600)
                swipe(1047, 519, 260, 113)
                return true
            }
        }
        closeAdvertisements()
        delay(500)
    }
    return false
}

suspend fun closeAdvertisements() {
    // 1. Capture the screen and cast safely
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return // Exits the function if capture fails

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.MainBaseWorker,
        MyColors.NightBaseWorker,
        MyColors.GoblinWorker,
        MyColors.GoblinWorker2
    )

    // 3. Run the check (Result is ignored, function returns Unit)
    homeSchemas.any { schema ->
        findMultiColors(byteBuffer = screenBuffer, schema = schema) != null
    }
}
suspend fun isInHomePage(): Boolean {
    // 1. Capture the screen and cast safely
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndStop("in isInHomePage, screen capture failed.")

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.MainBaseWorker,
        MyColors.NightBaseWorker,
        MyColors.GoblinWorker,
        MyColors.GoblinWorker2
    )

    // 3. Use 'any' for a clean, declarative exit
    return homeSchemas.any { schema ->
        findMultiColors(byteBuffer = screenBuffer, schema = schema) != null
    }
}