package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.details.NightBaseBuildingsPriority

object UpgradeExistingBuildings {
    suspend fun upgradeAllExistingBuildings(buildings: List<String>) {
        val orderedList = getOrderedList(buildings)
        // Implementation for upgrading buildings using orderedList will go here
        ShowMessage(orderedList.toString())
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