package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

suspend fun builderBaseFindNewBuildings(currentBase: BaseType): Boolean {
    val worker = when (currentBase) {
        BaseType.Builder ->
            findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker), duration = 1000)

        BaseType.Main ->
            findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker, MyColors.MainBaseWorker2), duration = 1000)
    }
    if (worker != null) {
        TouchActions.tap(worker.x, worker.y, delayTime = 500)
        var found = false
        iterateBuilderBaseBuildingUpgradeList(onDetect = { result ->
            val buildings = result.buildings
            if (buildings.isEmpty()) {
                ShowMessage("未检测到可升级建筑")
            } else {
                val newBuilding = buildings.find { it.name.startsWith("新") }
                if (newBuilding != null) {
                    if (newBuilding.y > 530) return@iterateBuilderBaseBuildingUpgradeList false
                    ShowMessage("检测到新建筑: ${newBuilding.name}, x: ${newBuilding.x}, y: ${newBuilding.y}")
                    TouchActions.tap(newBuilding.x + 20, newBuilding.y + 20, delayTime = 1500)
                    found = true
                    return@iterateBuilderBaseBuildingUpgradeList true
                }
            }
            false
        })
        return found
    }
    return false
}
