package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.buildings.BuildingDetectionResult
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.nightbase.NightBaseUpgradeBuildings.checkContinueBuild
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.details.NightBaseBuildingsPriority

object UpgradeExistingBuildings {
    suspend fun upgradeAllExistingBuildings(buildings: List<String>): Boolean {
        val orderedList = getOrderedList(buildings)
        for (building in orderedList) {
            clickRightBottom()
            val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)
            if (checkContinueBuild() && worker != null) {
                TouchActions.tap(worker.x, worker.y)
                delayWithMultiplier(500)
                if (findSpecificBuilding(building)) {
                    val hammer = findMultiColorsUntil(schema = MyColors.UpgradeHammer, duration = 1000)
                    if (hammer != null) {
                        TouchActions.tap(hammer.x, hammer.y)
                        delayWithMultiplier(500)
                        if (findMultiColors(schema = MyColors.NightBaseInsufficientResources) != null) {
                            ShowMessage("资源不足，退出")
                            clickRightBottom()
                            return false
                        } else {
                            ShowMessage("升级成功")
                            TouchActions.tap(633, 631)
                            delayWithMultiplier(500)
                            clickRightBottom()
                        }
                    }
                }
            }
            clickRightBottom()
        }
        return enterMainScreen()
    }

    private suspend fun findSpecificBuilding(buildingName: String): Boolean {
        var found = false
        ShowMessage("准备寻找$buildingName")
        iterateNightBaseBuildingUpgradeList { result ->
            val building = result.buildings.find { it.name == buildingName }
            if (building != null) {
                TouchActions.tap(building.x, building.y)
                delayWithMultiplier(1500)
                found = true
                true
            } else {
                false
            }
        }
        return found
    }


    private fun getOrderedList(buildings: List<String>): List<String> {
        val priorityMap = NightBaseBuildingsPriority.all.associate { settingDef ->
            val priorityStr = getConfigRuntime(settingDef.key)
            val priority = priorityStr.toIntOrNull() ?: logAndStop("Invalid priority configuration for ${settingDef.displayName}, value: $priorityStr")
            settingDef.displayName to priority
        }

        return buildings.sortedBy {
            priorityMap[it] ?: logAndStop("Building $it not found in upgrade priority list. Please check NightBaseBuildingsPriority configuration.")
        }
    }
}