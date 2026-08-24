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
// When stopAfterTerms is true, the entry loop stops right after the terms checkbox
// is clicked (skipping the login button) so callers can take over the navigation
// (used by the 扫码提取 QR-link flow, ADR-0003).
suspend fun launchGameAndHandleDataNumberLogin(gameVersion: GameVersion, stopAfterTerms: Boolean = false) {
    RunShell.runNoOutput("monkey -p ${gameVersion.packageName} -c android.intent.category.LAUNCHER 1", false)
    RunShell.runNoOutput("am start -n ${gameVersion.launchComponent}", false)
    tapDataNumberEntryButtons(stopAfterTerms)
}

// Reuse the existing colour-recognition pipeline on a short timeout loop after the game launches.
suspend fun tapDataNumberEntryButtons(stopAfterTerms: Boolean = false) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < DATA_NUMBER_SEARCH_TIMEOUT_MS) {
        val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
        findMultiColors(byteBuffer = screenBuffer, schema = MyColors.AgreePrivacy, increment = 1)?.let { privacyBtn ->
            // 辨别该"同意隐私"按钮是否实为更新按钮（是 → 已进入更新等待，停止后续处理）
            if (tryHandlePrivacyBtnAsUpdateBtn(privacyBtn)) {
                return
            }
            TouchActions.tap(privacyBtn.x, privacyBtn.y, delayTime = 800)
            continue
        }
        findMultiColors(byteBuffer = screenBuffer, schema = MyColors.AgreeTerms, increment = 1)?.let {
            TouchActions.tap(it.x, it.y, delayTime = 200)
            // 扫码提取流程：点完条款即交给调用方，不再点登录
            if (stopAfterTerms) return
            continue
        }

        if (!stopAfterTerms) {
            findMultiColors(byteBuffer = screenBuffer, schema = MyColors.LogIn, increment = 1)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 6000)
                return
            }
        }

        delay(DATA_NUMBER_SEARCH_INTERVAL_MS)
    }
}
