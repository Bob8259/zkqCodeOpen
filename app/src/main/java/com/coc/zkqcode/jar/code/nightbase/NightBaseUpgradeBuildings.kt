package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.iterateNightBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindBuildButton
import com.coc.zkqcode.jar.code.nightbase.upgradehelper.nightBaseFindNewBuildings
import com.coc.zkqcode.jar.code.universal.buildings.ALL_BUILDINGS
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
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
        ShowMessage("准备建造新建筑")
        zoomSmallNightBase()
        TouchActions.swipe(620, 443, 620, 720, delayTime = 700)
        var isBuildWalls = false
        if (nightBaseFindNewBuildings()) {
            val shopArrow = findMultiColorsUntil(schema = MyColors.InnerShopArrow, duration = 5000)
            if (shopArrow != null) {
                val isWall = findMultiColors(schema = MyColors.WallInShop)
                if (isWall != null) {
                    isBuildWalls = true
                }
                TouchActions.tap(shopArrow.x - 100, shopArrow.y + 50)
                delayWithMultiplier(500)
                val greenTick = nightBaseFindBuildButton(type = "Tick")
                if (greenTick != null) {
                    TouchActions.tap(greenTick.x, greenTick.y)
                    if (isBuildWalls) {
                        tryToBatchBuildWalls(greenTick.x, greenTick.y)
                    }
                }
            }
        }
    }

    private suspend fun tryToBatchBuildWalls(x: Int, y: Int) {
        val centerX = x - 20
        val centerY = y + 45
        TouchActions.tap(centerX, centerY)
        delayWithMultiplier(500)
        TouchActions.pinchOut(centerX - 300, centerY, centerX + 300, centerY, centerX, centerY)
        delayWithMultiplier(10000)
    }
}