package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.buildings.BuildingDetectionResult
import com.coc.zkqcode.jar.code.universal.buildings.detectBuildingList

/**
 * Iterates through the building upgrade list in Builder Base.
 * Performs swipes and calls [onDetect] for each detection result.
 * If [onDetect] returns true, the iteration stops immediately.
 */
suspend fun iterateBuilderBaseBuildingUpgradeList(currentBase:BaseType, onDetect: suspend (BuildingDetectionResult) -> Boolean) {
    var previousBuildingNames: List<String>? = null
    loop@ for (i in 1..12) {
        val result = detectBuildingList()
        if (onDetect(result)) return

        val currentBuildingNames = result.buildings.map { it.name }.sorted()
        if (previousBuildingNames != null && currentBuildingNames == previousBuildingNames) {
            repeat(2) {
                TouchActions.swipe(666, 170, 666, 300, delayTime = 600)
                delayWithMultiplier(200)
                val extraResult = detectBuildingList()
                if (onDetect(extraResult)) return
            }
            break@loop
        }
        previousBuildingNames = currentBuildingNames
        TouchActions.swipe(666, 500, 666, 120, delayTime = 600)
        delayWithMultiplier(200)
    }
}
