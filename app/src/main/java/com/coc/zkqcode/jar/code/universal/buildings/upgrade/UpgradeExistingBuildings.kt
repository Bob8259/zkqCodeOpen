package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseBuildings
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseBuildingsPriority
import com.coc.zkqcode.jar.ui.schema.details.MainBaseBuildingPriorities
import com.coc.zkqcode.jar.ui.schema.details.MainBaseBuildings


suspend fun upgradeAllExistingBuildings(buildings: List<String>, currentBase: BaseType): Boolean {
    val uniqueBuildings = setOf(
        "建筑大师大本营",
        "宝石矿井",
        "时光钟楼",
        "星空实验室",
        "奥仔哨站",
        "建筑大师训练营",
        "治疗小屋",
        "战争机器",
        "战斗直升机",
        "守卫岗哨",
        "空中炸弹发射器",
        "熔岩火炮",
        "巨型加农炮",
        "超级特斯拉电磁塔",
        "熔岩发射器",
        "十字连弩",
        "实验室",
        "天鹰火炮",
        "巨石碑",
        "复合机械塔",
        "复仇之塔",
        "终极炸弹",
        "暗黑重油罐",
        "大本营",
        "部落城堡",
        "训练营",
        "暗黑训练营",
        "法术工厂",
        "暗黑法术工厂",
        "攻城机器工坊",
        "战宠小屋",
        "铁匠铺",
        "英雄殿堂",
        "野蛮人之王",
        "弓箭女皇",
        "大守护者",
        "飞盾战神",
        "飞龙公爵"
    )
    val orderedList = getOrderedList(buildings, currentBase)
    for (building in orderedList) {
        val maxAttempts = if (building in uniqueBuildings) 1 else 6

        for (attempt in 1..maxAttempts) {
            // Ensure UI state is clean at the start of each iteration
            clickRightBottom(1)

            // Locate the worker icon
            val worker = if (currentBase == BaseType.Builder) findMultiColorsUntil(
                schemas = listOf(MyColors.BuilderBaseWorker), duration = 1000
            ) else findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker), duration = 1000)

            // Pre-condition check: If cannot continue building or worker not found, skip to next
            if (!checkContinueBuild(currentBase) || worker == null) {
                return enterMainScreen() // If we can't build anymore, might as well stop everything
            }

            TouchActions.tap(worker.x, worker.y, delayTime = 500)

            // Locate the specific building in the UI
            if (!findSpecificBuilding(building)) {
                TouchActions.tap(1233, 37)// tap gold to close worker list
                break // Not found this building anymore, go to next building type
            }

            TouchActions.tap(1233, 37)// tap gold to close worker list
            // Hero upgrade uses different logic
            val heroes = setOf(
                "野蛮人之王", "弓箭女皇", "大守护者", "飞盾战神", "飞龙公爵"
            )
            if (building in heroes) {
                clickRightBottom(1)

                // Check for insufficient resources for heroes (except 飞龙公爵)
                if (building != "飞龙公爵") {
                    // Calculate the search area based on hero position
                    // Base area for 野蛮人之王: x1=55, y1=500, x2=240, y2=530
                    // Each subsequent hero shifts by 228 on x-axis
                    val heroIndex = listOf("野蛮人之王", "弓箭女皇", "大守护者", "飞盾战神").indexOf(building)
                    val x1 = 55 + heroIndex * 228
                    val x2 = 240 + heroIndex * 228

                    // Create a ColorSchema with the same color pattern but different search area
                    val heroInsufficientResources = ColorSchema.parse(
                        x1, 500, x2, 530,
                        "7F88FF",
                        "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF",
                        0, 0.97,
                        "${building}资源不足"
                    )

                    if (findMultiColors(schema = heroInsufficientResources) != null) {
                        ShowMessage("${building}资源不足，跳过")
                        break
                    }
                }

                // TODO: Implement hero-specific upgrade logic for 飞龙公爵

            } else {

                // Check for the upgrade action (Hammer icon)
                val hammer = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeHammer), duration = 1000) ?: continue // Should not happen if build was found, but be safe

                TouchActions.tap(hammer.x, hammer.y, delayTime = 500)


                // Check for resource availability immediately after clicking upgrade
                if (findMultiColors(schema = if (currentBase == BaseType.Main) MyColors.MainBaseInsufficientResources else MyColors.BuilderBaseInsufficientResources) != null) {
                    clickRightBottom(1)
                    break // insufficient resources for this building, skip to next building type
                }

                // Successful upgrade flow
                TouchActions.tap(633, 631) // normal upgrade or unlock new buildings
                TouchActions.tap(982, 634, delayTime = 500)// machines
                ShowMessage("升级成功: $building (第 $attempt 个)")
            }
        }
    }

    // Final UI reset before returning to main screen
    clickRightBottom(1)
    return enterMainScreen()
}

private suspend fun findSpecificBuilding(buildingName: String): Boolean {
    var found = false
    ShowMessage("准备寻找$buildingName")
    iterateBuilderBaseBuildingUpgradeList(onDetect = { result ->
        val building = result.buildings.find { it.name == buildingName }
        if (building != null) {
            ShowMessage("已找到$buildingName")
            TouchActions.tap(building.x + 20, building.y + 20, delayTime = 1500)
            found = true
            true
        } else {
            false
        }
    })
    return found
}


private fun getOrderedList(buildings: List<String>, baseType: BaseType): List<String> {
    val enabledBuildingNames = when (baseType) {
        BaseType.Main -> MainBaseBuildings.all.filter {
            getBooleanConfigRuntime(it.key)
        }.map { it.displayName }.toSet()

        BaseType.Builder -> BuilderBaseBuildings.all.filter {
            getBooleanConfigRuntime(it.key)
        }.map { it.displayName }.toSet()
    }

    val priorityMap = when (baseType) {
        BaseType.Main -> MainBaseBuildingPriorities.all.associate { settingDef ->
            val priorityStr = getConfigRuntime(settingDef.key)
            val priority = priorityStr.toIntOrNull() ?: logAndStop("Invalid priority configuration for ${settingDef.displayName}, value: $priorityStr")
            settingDef.displayName to priority
        }

        BaseType.Builder -> BuilderBaseBuildingsPriority.all.associate { settingDef ->
            val priorityStr = getConfigRuntime(settingDef.key)
            val priority = priorityStr.toIntOrNull() ?: logAndStop("Invalid priority configuration for ${settingDef.displayName}, value: $priorityStr")
            settingDef.displayName to priority
        }
    }

    return buildings.filter { it in priorityMap && it in enabledBuildingNames }.sortedBy {
        priorityMap[it]!!
    }
}

