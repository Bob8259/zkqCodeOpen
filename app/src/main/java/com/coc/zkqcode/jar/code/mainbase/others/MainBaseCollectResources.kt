package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun mainBaseCollectResources(): Boolean {
    zoomSmallMainBase()
    goldMineAndElixirCollector()
    swipe(900, 130, 0, 720)
    goldMineAndElixirCollector()
    return enterMainScreen()
}

private suspend fun goldMineAndElixirCollector() {
    repeat(2) {
        // Define the resource schemas to iterate through in each cycle
        val resourceSchemas = listOf(
            MyColors.MainBaseCollectGold1, MyColors.MainBaseCollectGold2, MyColors.MainBaseCollectElixir1, MyColors.MainBaseCollectElixir2,
        )
        // Reuse one captured frame for this pass and refresh it only after a successful collection tap.
        var capturedScreen = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult

        for (schema in resourceSchemas) {
            val resource = findMultiColors(schema = schema, byteBuffer = capturedScreen)
            if (resource != null) {
                // Use existing TouchActions implementation to interact with the resource coordinates
                TouchActions.tap(resource.x, resource.y, delayTime = 50)
                capturedScreen = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            }
        }
    }
    delayWithMultiplier(200)
}