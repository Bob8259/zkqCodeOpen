package com.coc.zkqcode.jar.code.builderbase.resources

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.builderbase.others.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun collectBuilderBaseResources(): Boolean {
    zoomSmallBuilderBase()
    swipe(587, 420, 587, 700, delayTime = 100)

    repeat(3) {
        // Define the resource schemas to iterate through in each cycle
        val resourceSchemas = listOf(
            MyColors.BuilderBaseCollectGem1,
            MyColors.BuilderBaseCollectGold1,
            MyColors.BuilderBaseCollectExiler1,
            MyColors.BuilderBaseCollectGem2
        )

        for (schema in resourceSchemas) {
            val resource = findMultiColors(schema = schema)
            if (resource != null) {
                // Use existing TouchActions implementation to interact with the resource coordinates
                TouchActions.tap(resource.x, resource.y)
            }
        }
    }
    swipe(587, 420, 587, 700)
    delayWithMultiplier(100)
    // Use a label so we can break out of both loops when a cart is found
    outerLoop@ for (tx in 805..920 step 20) {
        for (ty in 325..415 step 20) {
            TouchActions.tap(tx, ty, isJitter = false)
            val collectButton = findMultiColorsUntil(schemas = listOf(MyColors.CannotCollectExilerCart, MyColors.CollectExilerCart), duration = 200)
            if (collectButton != null) {
                TouchActions.tap(collectButton.x, collectButton.y)
                clickRightBottom(1)
                break@outerLoop
            }
        }
    }
    return enterMainScreen()
}