package com.coc.zkqcode.jar.code.builderbase.upgradehelper

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.builderbase.zoomSmallBuilderBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

suspend fun builderBaseFindNewBuildings(): Boolean {

    val worker = findMultiColorsUntil(schemas = listOf(MyColors.BuilderBaseWorker), duration = 1000)
    if (worker != null) {
        TouchActions.tap(worker.x, worker.y)
        delayWithMultiplier(500)
        var found = false
        iterateBuilderBaseBuildingUpgradeList { result ->
            val buildings = result.buildings
            if (buildings.isEmpty()) {
                ShowMessage("未检测到可升级建筑")
            } else {
                val newBuilding = buildings.find { it.name.startsWith("新") }
                if (newBuilding != null) {
                    ShowMessage("检测到新建筑: ${newBuilding.name}")
                    TouchActions.tap(newBuilding.x + 20, newBuilding.y + 20)
                    found = true
                    delayWithMultiplier(1500)
                    return@iterateBuilderBaseBuildingUpgradeList true
                }
            }
            false
        }
        return found
    }
    return false
}
