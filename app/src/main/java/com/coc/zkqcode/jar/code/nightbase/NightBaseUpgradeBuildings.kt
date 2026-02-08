package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.detectBuildingList
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

object NightBaseUpgradeBuildings {
    suspend fun detectUpgradableBuildings() {
        zoomSmallNightBase(true)
        val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)
        if (worker != null) {
            TouchActions.tap(worker.x, worker.y)
            delayWithMultiplier(500)
            TouchActions.swipe(666, 543, 666, 170)
            val buildings = detectBuildingList()
            if (buildings.isEmpty()) {
                ShowMessage("未检测到建筑")
            } else {
                val info = buildings.joinToString("\n")
                ShowMessage("检测到 ${buildings.size} 个建筑:\n$info")
            }
        } else {
            ShowMessage("未检测到夜世界工人")
        }
    }
}