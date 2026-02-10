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
            // Ensure UI state is clean at the start of each iteration
            clickRightBottom()

            // Locate the worker icon
            val worker = findMultiColorsUntil(schema = MyColors.NightBaseWorker, duration = 1000)

            // Pre-condition check: If cannot continue building or worker not found, skip to next
            if (!checkContinueBuild() || worker == null) {
                break
            }

            TouchActions.tap(worker.x, worker.y)
            delayWithMultiplier(500)

            // Locate the specific building in the UI
            if (!findSpecificBuilding(building)) {
                continue
            }

            // Check for the upgrade action (Hammer icon)
            val hammer = findMultiColorsUntil(schema = MyColors.UpgradeHammer, duration = 1000) ?: continue

            TouchActions.tap(hammer.x, hammer.y)
            delayWithMultiplier(500)

            // Check for resource availability immediately after clicking upgrade
            if (findMultiColors(schema = MyColors.NightBaseInsufficientResources) != null) {
                ShowMessage("资源不足，退出")
                clickRightBottom()
                return false // Stop processing if resources are depleted
            }

            // Successful upgrade flow
            ShowMessage("升级成功")
            TouchActions.tap(633, 631) // Confirm upgrade/close dialog
            delayWithMultiplier(500)

        }

        // Final UI reset before returning to main screen
        clickRightBottom()
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