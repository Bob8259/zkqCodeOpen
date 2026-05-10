package com.coc.zkqcode.jar.code.universal.smalltools.lifecycle

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.builderbase.others.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.checkContinueBuild
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import kotlinx.coroutines.delay


/**
 * Attempts to enter the Main Base, timing out after 30 seconds.
 */
suspend fun enterMainBase(): Boolean {
    val loopStart = System.currentTimeMillis()
    clickRightBottom(1)
    while (System.currentTimeMillis() - loopStart < 60_000L) {
        val workers = findMultiColorsUntil(schemas = MyColors.MainBaseWorkerSchemas + listOf(MyColors.GoblinWorker, MyColors.GoblinWorker2, MyColors.GoblinResearcher), duration = 200)
        if (workers != null) {
            accountMessage("已进入主世界")
            clickRightBottom(1)
            delayWithMultiplier(200)
            return true
        }
        val remaining = (60_000L - (System.currentTimeMillis() - loopStart)) / 1000.0
        accountMessage("尝试进入主世界中，剩余${"%.1f".format(remaining)}秒后退出\n请手动给主世界和夜世界切换默认场景")
        val builderBaseStarBonus = findMultiColors(schema = MyColors.BuilderBaseStarBonus)
        if (builderBaseStarBonus != null) {
            TouchActions.tap(builderBaseStarBonus.x + 10, builderBaseStarBonus.y + 10, delayTime = 200)
        }
        if (!checkReconnections()) return false
        zoomSmallBuilderBase()
        TouchActions.swipe(750, 150, 750, 550)
        // Tap all grid points in the area to trigger the main base portal
        for (x in 960..1000 step 50) {
            for (y in 35..210 step 50) {
                TouchActions.tap(x, y, isJitter = false, delayTime = 50)
            }
        }
        for (x in 1000..1050 step 50) {
            for (y in 250..330 step 50) {
                TouchActions.tap(x, y, isJitter = false, delayTime = 50)
            }
        }
        TouchActions.tap(1160, 325)//two extra clicks
        TouchActions.tap(1160, 370)
    }
    return false
}

/**
 * Optimizes the transition to the Builder Base with improved polling and logic flow.
 * Times out after 30 seconds and returns false if the transition did not succeed.
 */
suspend fun enterBuilderBase(isCheck: Boolean): Boolean {
    if (InGameVars.townHallLevel < 6) return false
    val loopStart = System.currentTimeMillis()
    clickRightBottom(1)
    while (System.currentTimeMillis() - loopStart < 60_000L) {
        val remaining = (60_000L - (System.currentTimeMillis() - loopStart)) / 1000.0
        // 1. Check for Builder Base success indicator
        if (findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2), duration = 200) != null) {
            accountMessage("已进入夜世界")
            return true
        }
        accountMessage("尝试进入夜世界中，剩余${"%.1f".format(remaining)}秒后退出\n请手动给主世界和夜世界切换默认场景")
        // Ensure consistent view before attempting interaction
        zoomSmallMainBase()
        clickRightBottom(1)
        if (!checkReconnections()) return false
        // List of potential boat locations to handle perspective shifts
        val boatLocations = listOf(
            317 to 474, 336 to 512, 313 to 568
        )
        val hasBuilder = checkContinueBuild(BaseType.Main)
        for ((x, y) in boatLocations) {
            TouchActions.tap(x, y)

            if (isCheck && hasBuilder) {
                val checkDuration = 500L
                val loopStartTime = System.currentTimeMillis()

                // Polling loop for state transition (0.5s window)
                while (System.currentTimeMillis() - loopStartTime < checkDuration) {
                    // 1. Check for Builder Base success indicator
                    if (findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2), duration = 200) != null) {
                        accountMessage("已进入夜世界")
                        return true
                    }

                    // 2. Check for Main Base indicator (failure to switch)
                    if (findMultiColorsUntil(listOf(MyColors.UpgradeToTH6, MyColors.UpgradeToTH6_2), 100) != null) {
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
                                TouchActions.tap(boatPoint.x, boatPoint.y)
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
