package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.NightBaseTutorial
import kotlinx.coroutines.delay

class EnterTargetBase {
    suspend fun enterMainBase() {

    }

    suspend fun enterNightBase(isCheck: Boolean) {
        ShowMessage("准备进入夜世界")
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

            if (isCheck) {
                val duration = 500L
                val startTime = System.currentTimeMillis()

                // Main validation loop (0.5s window)
                while (System.currentTimeMillis() - startTime < duration) {

                    // 1. Search for RebuildNightBase
                    val point = findMultiColors(schema = MyColors.RebuildNightBase)

                    if (point != null) {
                        TouchActions.tap(point.x, point.y)
                        delayWithMultiplier(300)

                        // Logic: Keep finding MyColors.RebuildBoat for another 1 second
                        val boatSearchStart = System.currentTimeMillis()

                        while (System.currentTimeMillis() - boatSearchStart < 1000L) {
                            val boatPoint = findMultiColors(schema = MyColors.RebuildBoat)
                            if (boatPoint != null) {
                                NightBaseTutorial().nightBaseTutorial()
                                break
                            }
                            delay(20) // Polling interval
                        }

                        // If found, we've handled the tutorial; if not, loop continues normally as requested
                    }

                    // 2. Search for UpgradeToTH6 color schema
                    val th6Point = findMultiColors(schema = MyColors.UpgradeToTH6)

                    if (th6Point != null) {
                        InGamesVars.isNightBaseUnlocked = false
                        return // Exit the entire function
                    }

                    // Standard delay to prevent high CPU usage during the 0.5s window
                    delay(10)
                }
            }
        }
    }


}