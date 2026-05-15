package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun collectBuilderBaseResources(): Boolean {
    accountMessage("收集资源中")
    zoomSmallBuilderBase()
    TouchActions.swipe(587, 420, 587, 700, delayTime = 100)
    goldMineAndElixirCollector()
    TouchActions.swipe(587, 420, 587, 700, delayAfter = 100)
    goldMineAndElixirCollector()
    // Use a label so we can break out of both loops when a cart is found
    val elixirCart = findMultiColorsUntil(schemas = listOf(MyColors.ElixirCart1, MyColors.ElixirCart2, MyColors.ElixirCart3), duration = 200)
    if (elixirCart != null) {
        TouchActions.tap(elixirCart.x, elixirCart.y, delayTime = 100)
        val collectButton = findMultiColorsUntil(schemas = listOf(MyColors.CannotCollectExilerCart, MyColors.CollectExilerCart), duration = 200)
        if (collectButton != null) {
            TouchActions.tap(collectButton.x, collectButton.y, delayTime = 300)
            clickRightBottom(1)
            return enterMainScreen()
        }
    } else {
        outerLoop@ for (tx in 745..920 step 40) {
            for (ty in 260..415 step 40) {
                TouchActions.tap(tx, ty, isJitter = false, delayTime = 100)
                val collectButton = findMultiColorsUntil(schemas = listOf(MyColors.CannotCollectExilerCart, MyColors.CollectExilerCart), duration = 200)
                if (collectButton != null) {
                    TouchActions.tap(collectButton.x, collectButton.y, delayTime = 300)
                    clickRightBottom(1)
                    break@outerLoop
                }
            }
        }
    }

    return enterMainScreen()
}

private suspend fun goldMineAndElixirCollector() {
    repeat(2) {
        // Define the resource schemas to iterate through in each cycle
        val resourceSchemas = listOf(
            MyColors.BuilderBaseCollectGem1,
            MyColors.BuilderBaseCollectGold1,
            MyColors.BuilderBaseCollectGold2,
            MyColors.BuilderBaseCollectExiler1,
            MyColors.BuilderBaseCollectExiler2,
            MyColors.BuilderBaseCollectGem2,
            MyColors.BuilderBaseCollectGem3
        )
        // Reuse one captured frame for this pass and refresh it only after a successful collection tap.
        var capturedScreen = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult

        for (schema in resourceSchemas) {
            val resource = findMultiColors(schema = schema, byteBuffer = capturedScreen)
            if (resource != null) {
                // Use existing TouchActions implementation to interact with the resource coordinates
                TouchActions.tap(resource.x, resource.y)
                capturedScreen = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            }
        }
    }
    delayWithMultiplier(200)
}