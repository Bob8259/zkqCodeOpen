package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.buildings.BuildingDetectionResult
import com.coc.zkqcode.jar.code.universal.buildings.detectBuildingList

/**
 * Iterates through the building upgrade list in Night Base.
 * Performs swipes and calls [onDetect] for each detection result.
 * If [onDetect] returns true, the iteration stops immediately.
 */
suspend fun iterateNightBaseBuildingUpgradeList(onDetect: suspend (BuildingDetectionResult) -> Boolean) {
    loop@ for (i in 1..12) {
        val result = detectBuildingList()
        if (onDetect(result)) return

        if (result.suggestUpgradeDetected) {
            repeat(2) {
                TouchActions.swipe(666, 170, 666, 30)
                delayWithMultiplier(500)
                val extraResult = detectBuildingList()
                if (onDetect(extraResult)) return
            }
            break@loop
        }
        TouchActions.swipe(666, 170, 666, 540)
        delayWithMultiplier(500)
    }
}
