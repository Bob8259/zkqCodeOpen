package com.coc.zkqcode.jar.code.universal.smalltools.lifecycle

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.tryHandlePrivacyBtnAsUpdateBtn
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import kotlinx.coroutines.delay

private const val DATA_NUMBER_SEARCH_TIMEOUT_MS = 15_000L
private const val DATA_NUMBER_SEARCH_INTERVAL_MS = 300L

// Launch the selected game version for the normal bot data-number flow, then handle the first-entry agreement/login buttons.
suspend fun launchGameAndHandleDataNumberLogin(gameVersion: GameVersion) {
    RunShell.runNoOutput("monkey -p ${gameVersion.packageName} -c android.intent.category.LAUNCHER 1", false)
    RunShell.runNoOutput("am start -n ${gameVersion.launchComponent}", false)
    tapDataNumberEntryButtons()
}

// Reuse the existing colour-recognition pipeline on a short timeout loop after the game launches.
suspend fun tapDataNumberEntryButtons() {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < DATA_NUMBER_SEARCH_TIMEOUT_MS) {
        val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
        if (screenBuffer == null) {
            delay(DATA_NUMBER_SEARCH_INTERVAL_MS)
            continue
        }
        findMultiColors(byteBuffer = screenBuffer, schema = MyColors.AgreePrivacy, increment = 1)?.let { privacyBtn ->
            // Determine if this "privacy agreement" button is actually an update button (yes → entered update wait, stop further processing)
            if (tryHandlePrivacyBtnAsUpdateBtn(privacyBtn)) {
                return
            }
            TouchActions.tap(privacyBtn.x, privacyBtn.y, delayTime = 800)
            continue
        }
        findMultiColors(byteBuffer = screenBuffer, schema = MyColors.AgreeTerms, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 200)
            continue
        }

        findMultiColors(byteBuffer = screenBuffer, schema = MyColors.LogIn, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 6000)
            return
        }

        delay(DATA_NUMBER_SEARCH_INTERVAL_MS)
    }
}
