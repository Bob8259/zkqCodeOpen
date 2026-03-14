package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.builderbase.others.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import kotlinx.coroutines.delay


/**
 * Attempts to enter the Main Base, timing out after 30 seconds.
 */
suspend fun enterMainBase(): Boolean {
    val loopStart = System.currentTimeMillis()
    while (System.currentTimeMillis() - loopStart < 30_000L) {
        val remaining = (30_000L - (System.currentTimeMillis() - loopStart)) / 1000.0
        ShowMessage("尝试进入主世界中，剩余${"%.1f".format(remaining)}秒后退出")
        zoomSmallBuilderBase()
        TouchActions.swipe(750, 150, 750, 550)
        // Tap all grid points in the area (960,35)~(1000,110) to trigger the main base portal
        for (x in 960..1000 step 15) {
            for (y in 35..210 step 30) {
                TouchActions.tap(x, y, isJitter = false, delayTime = 50)
            }
        }
        val workers = findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker, MyColors.GoblinWorker, MyColors.GoblinResearcher), duration = 200)
        if (workers != null) return true
    }
    return false
}

/**
 * Optimizes the transition to the Builder Base with improved polling and logic flow.
 * Times out after 30 seconds and returns false if the transition did not succeed.
 */
suspend fun enterBuilderBase(isCheck: Boolean): Boolean {
    val loopStart = System.currentTimeMillis()
    while (System.currentTimeMillis() - loopStart < 30_000L) {
        val remaining = (30_000L - (System.currentTimeMillis() - loopStart)) / 1000.0
        ShowMessage("尝试进入夜世界中，剩余${"%.1f".format(remaining)}秒后退出")
        // Ensure consistent view before attempting interaction
        zoomSmallMainBase()

        // List of potential boat locations to handle perspective shifts
        val boatLocations = listOf(
            317 to 474, 336 to 512, 313 to 568
        )

        for ((x, y) in boatLocations) {
            TouchActions.tap(x, y)

            if (isCheck) {
                val checkDuration = 500L
                val loopStartTime = System.currentTimeMillis()

                // Polling loop for state transition (0.5s window)
                while (System.currentTimeMillis() - loopStartTime < checkDuration) {

                    // 1. Check for Builder Base success indicator
                    // Checked early to ensure fast return on successful transition
                    if (findMultiColors(schema = MyColors.BuilderBaseWorker) != null) {
                        return true
                    }

                    // 2. Check for Main Base indicator (failure to switch)
                    if (findMultiColors(schema = MyColors.UpgradeToTH6) != null) {
                        return false
                    }

                    // 3. Handle Tutorial / Rebuild state
                    val rebuildPoint = findMultiColors(schema = MyColors.RebuildBuilderBase)
                    if (rebuildPoint != null) {
                        TouchActions.tap(rebuildPoint.x, rebuildPoint.y, delayTime = 300)

                        // Secondary loop: Search for RebuildBoat within a 1s window
                        val boatSearchStartTime = System.currentTimeMillis()
                        while (System.currentTimeMillis() - boatSearchStartTime < 1000L) {
                            val boatPoint = findMultiColors(schema = MyColors.RebuildBoat)
                            if (boatPoint != null) {
                                TouchActions.tap(boatPoint.x, boatPoint.y, delayTime = 500)
                                AllTutorials.allBaseTutorial()
                                break
                            }
                            delay(20) // Tight polling for tutorial interaction
                        }
                    }

                    // Standard delay to maintain performance and avoid high CPU usage
                    delay(100)
                }
            } else {
                // If no check is requested, provide a brief delay before trying next coordinate
                delay(200)
            }
        }
    }
    return false
}
