package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors

suspend fun collectNightBaseResources() {
    zoomSmallNightBase()
    swipe(587, 420, 587, 700)
    delayWithMultiplier(100)

    repeat(3) {
        // Define the resource schemas to iterate through in each cycle
        val resourceSchemas = listOf(
            MyColors.NightBaseCollectGem1,
            MyColors.NightBaseCollectGold1,
            MyColors.NightBaseCollectExiler1,
            MyColors.NightBaseCollectGem2
        )

        for (schema in resourceSchemas) {
            val resource = findMultiColors(schema = schema)
            if (resource != null) {
                // Use existing TouchActions implementation to interact with the resource coordinates
                TouchActions.tap(resource.x, resource.y)
            }
        }
    }

    // List of coordinates to tap and check for resource carts
    val targets = listOf(
        Pair(769, 43),
        Pair(807, 87),
        Pair(845, 98)
    )

    targets.forEach { (tx, ty) ->
        TouchActions.tap(tx, ty)

        val startTime = System.currentTimeMillis()
        // Loop for 1000ms (1 second) as requested
        while (System.currentTimeMillis() - startTime < 1000) {
            // Check if the cart is already collected or empty
            val cannotCollect = findMultiColors(schema = MyColors.CannotCollectExilerCart)
            if (cannotCollect != null) break

            // Check if the cart is available for collection
            val collectPoint = findMultiColors(schema = MyColors.CollectExilerCart)
            if (collectPoint != null) {
                TouchActions.tap(collectPoint.x, collectPoint.y)
                break
            }
        }
    }
}