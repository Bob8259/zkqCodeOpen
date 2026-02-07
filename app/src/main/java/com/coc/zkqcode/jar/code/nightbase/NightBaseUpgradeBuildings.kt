package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.fileactions.showDebugInfo
import com.coc.zkqcode.jar.code.universal.buildings.DetectedBuilding
import com.coc.zkqcode.jar.code.universal.buildings.detectBuildingList

object NightBaseUpgradeBuildings {
    suspend fun detectUpgradableBuildings(): List<DetectedBuilding> {
        val buildings = detectBuildingList()

    }
}