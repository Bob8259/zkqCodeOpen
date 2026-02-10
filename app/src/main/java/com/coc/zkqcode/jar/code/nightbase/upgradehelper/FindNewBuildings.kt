package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

suspend fun nightBaseFindNewBuildings(): Boolean {
    val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)
    if (worker != null) {
        TouchActions.tap(worker.x, worker.y)
        delayWithMultiplier(500)
        TouchActions.swipe(666, 150, 666, -1200)
        delayWithMultiplier(500)
        var found = false
        iterateNightBaseBuildingUpgradeList { result ->
            val buildings = result.buildings
            if (buildings.isEmpty()) {
                ShowMessage("未检测到建筑")
            } else {
                val newBuilding = buildings.find { it.name.startsWith("新") }
                if (newBuilding != null) {
                    ShowMessage("检测到新建筑: ${newBuilding.name}")
                    TouchActions.tap(newBuilding.x, newBuilding.y)
                    found = true
                    delayWithMultiplier(1500)
                    return@iterateNightBaseBuildingUpgradeList true
                }
                val info = buildings.joinToString("\n")
                ShowMessage("检测到 ${buildings.size} 个建筑:\n$info")
            }
            false
        }
        return found
    }
    return false
}
