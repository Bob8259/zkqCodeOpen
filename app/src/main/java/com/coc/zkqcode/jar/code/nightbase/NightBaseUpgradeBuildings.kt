package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.iterateNightBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindNewBuildings
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

object NightBaseUpgradeBuildings {
    private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()
    private var isNewBuildingDetected = false
    suspend fun upgradeBuildings() {
        upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
        isNewBuildingDetected = false
        zoomSmallNightBase(true)
        val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)
        if (worker != null) {
            TouchActions.tap(worker.x, worker.y)
            delayWithMultiplier(500)
            TouchActions.swipe(666, 200, 666, -1000)
            iterateNightBaseBuildingUpgradeList { result ->
                val buildings = result.buildings
                if (buildings.isEmpty()) {
                    ShowMessage("未检测到建筑")
                } else {
                    buildings.forEach { building ->
                        if (upgradableBuildingsMap.containsKey(building.name)) {
                            upgradableBuildingsMap[building.name] = true
                        }
                        if (building.name.startsWith("新")) {
                            isNewBuildingDetected = true
                        }
                    }
                    val info = buildings.joinToString("\n")
                    ShowMessage("检测到 ${buildings.size} 个建筑:\n$info")
                }
                isNewBuildingDetected
            }
            if (isNewBuildingDetected) {
                buildAllNewBuildings()
            }
            val summary = upgradableBuildingsMap.filter { it.value }.keys.joinToString(", ")
            ShowMessage("所有可升级建筑: $summary")
        } else {
            ShowMessage("未检测到夜世界工人")
        }
    }

    suspend fun buildAllNewBuildings() {
        ShowMessage("准备建造新建造")
        zoomSmallNightBase()
        val isBuildWalls = false
        if (nightBaseFindNewBuildings()) {

        }
    }
}