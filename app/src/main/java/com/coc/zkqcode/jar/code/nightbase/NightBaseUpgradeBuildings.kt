package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.buildings.detectBuildingList
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

object NightBaseUpgradeBuildings {
    private val upgradableBuildingsMap = ALL_BUILDINGS.associateWith { false }.toMutableMap()
    private var isNewBuildingDetected = false
    suspend fun detectUpgradableBuildings() {
        upgradableBuildingsMap.keys.forEach { upgradableBuildingsMap[it] = false }
        isNewBuildingDetected = false

        zoomSmallNightBase(true)
        val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)
        if (worker != null) {
            TouchActions.tap(worker.x, worker.y)
            delayWithMultiplier(500)
            TouchActions.swipe(666, 200, 666, -1000)
            loop@ for (i in 1..12) {
                val result = detectBuildingList()
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
                    if (isNewBuildingDetected) break@loop
                }
                if (result.suggestUpgradeDetected) {
                    repeat(2) {
                        TouchActions.swipe(666, 170, 666, 30)
                        delayWithMultiplier(500)
                        val extraResult = detectBuildingList()
                        extraResult.buildings.forEach { building ->
                            if (upgradableBuildingsMap.containsKey(building.name)) {
                                upgradableBuildingsMap[building.name] = true
                            }
                            if (building.name.startsWith("新")) {
                                isNewBuildingDetected = true
                            }
                        }
                        if (extraResult.buildings.isNotEmpty()) {
                            val info = extraResult.buildings.joinToString("\n")
                            ShowMessage("建议升级额外检测到 ${extraResult.buildings.size} 个建筑:\n$info")
                        }
                        if (isNewBuildingDetected) break@loop
                    }
                    break
                }
                TouchActions.swipe(666, 170, 666, 540)
                delayWithMultiplier(500)
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
    }
}