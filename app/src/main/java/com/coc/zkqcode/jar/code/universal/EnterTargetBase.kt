package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColorsUntil
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.NightBaseTutorial

class EnterTargetBase {
    suspend fun enterMainBase() {

    }

    suspend fun enterNightBase(isCheck: Boolean) {
        // Initialize view
        zoomSmallMainBase()

        // List of coordinates to cycle through
        val boatLocations = listOf(
            317 to 474,
            336 to 512,
            313 to 568
        )

        for ((x, y) in boatLocations) {
            // Perform the tap action
            TouchActions.tap(x, y)
            // Conditional validation after each tap
            if (isCheck) {
                val point = findMultiColorsUntil(
                    schema = MyColors.RebuildNightBase,
                    duration = 500
                )
                if (point != null) {
                    TouchActions.tap(point.x, point.y)
                    delayWithMultiplier(300)
                    NightBaseTutorial().nightBaseTutorial()
                }
            }
        }
    }


}