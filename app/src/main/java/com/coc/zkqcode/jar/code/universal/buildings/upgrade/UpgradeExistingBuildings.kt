package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.mainbase.upgrade.mainBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.buildings.iterateBuilderBaseBuildingUpgradeList
import com.coc.zkqcode.jar.code.universal.buildings.walls.calculateResourcesPercentage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeUpgradeResources
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseBuildings
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseBuildingsPriority
import com.coc.zkqcode.jar.ui.schema.details.MainBaseBuildingPriorities
import com.coc.zkqcode.jar.ui.schema.details.MainBaseBuildings

enum class WallType {
    Gold, Elixir
}

// Result type for upgradeAllExistingBuildings to signal caller loop behavior
enum class UpgradeResult {
    Success,   // Upgrade cycle completed normally (equivalent to old true)
    Failure,   // Navigation/screen failure (equivalent to old false)
    StopLoop   // Signals caller to break its loop (e.g., elixir icon not found)
}

// skipOrdering: when true, bypass getOrderedList filtering/sorting, used for wall-upgrade
//wallType are only used for wall-upgrade
suspend fun upgradeAllExistingBuildings(buildings: List<String>, currentBase: BaseType, skipOrdering: Boolean = false, wallType: WallType = WallType.Gold): UpgradeResult {
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
        "飞龙公爵",
        "多管迫击炮",
        "城墙"
    )
    val orderedList = if (skipOrdering) buildings else getOrderedList(buildings, currentBase)
    for (building in orderedList) {
        val maxAttempts = if (building in uniqueBuildings) 1 else 6

        for (attempt in 1..maxAttempts) {
            // Ensure UI state is clean at the start of each iteration
            clickRightBottom(1)

            // Locate the worker icon
            val worker = if (currentBase == BaseType.Builder) findMultiColorsUntil(
                schemas = listOf(MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2), duration = 1000
            ) else findMultiColorsUntil(schemas = listOf(MyColors.MainBaseWorker, MyColors.MainBaseWorker2, MyColors.MainBaseWorker3), duration = 1000)

            // Pre-condition check: if it cannot continue building or worker not found, skip to next
            // Pass skipOrdering as isWallUpgrade so the saved worker is available for wall upgrades
            if (!checkContinueBuild(currentBase, isWallUpgrade = skipOrdering) || worker == null) {
                return if (enterMainScreen()) UpgradeResult.Success else UpgradeResult.Failure
            }

            TouchActions.tap(worker.x, worker.y, delayTime = 500)

            // Locate the specific building in the UI
            if (skipOrdering) {
                if (!findSpecificBuilding(building, true)) {
                    // Wall not found; clean up UI and signal caller to stop its upgrade loop
                    clickRightBottom(1)
                    enterMainScreen()
                    return UpgradeResult.StopLoop
                }
            } else {
                if (!findSpecificBuilding(building)) {
                    clickRightBottom(1)
                    break // Not found this building anymore, go to next building type
                }
            }
            // Dispatch to the appropriate upgrade handler
            val heroes = setOf(
                "野蛮人之王", "弓箭女皇", "大守护者", "飞盾战神", "飞龙公爵"
            )
            val action = when (building) {
                in heroes -> {
                    upgradeHero(building)
                }

                "城墙" -> {
                    upgradeWalls(currentBase, wallType)
                }

                else -> {
                    upgradeBuilding(building, currentBase, attempt)
                }
            }
            when (action) {
                LoopAction.Break -> break
                LoopAction.Continue -> continue
                LoopAction.Proceed -> {}
                LoopAction.ReturnStop -> {
                    // Propagate stop signal to caller (e.g., elixir icon not found for wall upgrade)
                    clickRightBottom(1)
                    enterMainScreen()
                    return UpgradeResult.StopLoop
                }
            }
        }
    }
    // Final UI reset before returning to main screen
    clickRightBottom(1)
    return if (enterMainScreen()) UpgradeResult.Success else UpgradeResult.Failure
}

private suspend fun upgradeWalls(currentBase: BaseType, wallType: WallType): LoopAction {
    // Map wall type to localized resource name
    val wallTypeName = when (wallType) {
        WallType.Gold -> "金币"
        WallType.Elixir -> "圣水"
    }
    ShowMessage("刷墙类型：$wallTypeName")
    TouchActions.tap(1230, 40, delayTime = 500)//Tap gold bar
    TouchActions.tap(1230, 40, delayTime = 500)//Close distractions
    val isBatchUpgrade = if (currentBase == BaseType.Main) {
        getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key)
    } else {
        getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_BATCH_WALL_UPGRADE_SETTINGS.key)
    }
    val searchDirection = if (wallType == WallType.Gold) 0 else 1
    // Rescope the hammer schema to use searchDirection for wall-type-specific search
    val hammerSchema = ColorSchema.rescope(
        MyColors.UpgradeHammer, MyColors.UpgradeHammer.x1, MyColors.UpgradeHammer.y1, MyColors.UpgradeHammer.x2, MyColors.UpgradeHammer.y2, searchDirection
    )
    val hammer = findMultiColorsUntil(schemas = listOf(hammerSchema), duration = 1000) ?: return LoopAction.Continue
    if (wallType == WallType.Elixir) {
        // Select elixir icon color based on base type
        val elixirIconBase = if (currentBase == BaseType.Builder) MyColors.builderBaseSmallElixirUpgradeIcon else MyColors.smallElixirUpgradeIcon
        // Rescope search area for elixir upgrade icon relative to hammer position
        val elixirIconSchema = ColorSchema.rescope(
            elixirIconBase, hammer.x + 45, hammer.y - 60, hammer.x + 80, hammer.y - 10
        )
        val smallElixirUpgradeIcon = findMultiColors(schema = elixirIconSchema)
        if (smallElixirUpgradeIcon == null) {
            ShowMessage("圣水刷墙失败，未找到圣水升级标志")
            delayWithMultiplier(500)
            // Signal caller to stop its wall-upgrade loop since no elixir walls are available
            return LoopAction.ReturnStop
        }
    }
    val resources = recognizeResources()
    val currentResourcePercentage = calculateResourcesPercentage(currentBase)
    TouchActions.tap(hammer.x, hammer.y, delayTime = 500)
    // Check for resource availability immediately after clicking upgrade
    if (findMultiColors(schema = if (currentBase == BaseType.Main) MyColors.MainBaseInsufficientResources else MyColors.BuilderBaseInsufficientResources) != null) {
        clickRightBottom(1)
        return LoopAction.Break
    }
    if (isBatchUpgrade) {
        if ((wallType == WallType.Gold && resources.gold < 10000) || (wallType == WallType.Elixir && resources.elixir < 10000)) {
            // Insufficient resources for batch upgrade, show detected values and fall back to normal upgrade
            ShowMessage("批量刷墙资源不足（金币: ${resources.gold}, 圣水: ${resources.elixir}），回退到普通刷墙")
            TouchActions.tap(980, 635, delayTime = 500)
            return LoopAction.Proceed
        } else {
            val wallCost = recognizeUpgradeResources(currentBase)
            val thresholds = 25.coerceAtLeast(
                getConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.displayName} 必须是数字，请检查配置")
            )
            // Calculate how many walls we can upgrade while keeping resources above the threshold
            val currentResource = if (wallType == WallType.Gold) resources.gold else resources.elixir
            val currentPercent = if (wallType == WallType.Gold) currentResourcePercentage.gold else currentResourcePercentage.elixir
            val fullResource = if (currentPercent > 0) currentResource / (currentPercent / 100.0) else 0.0
            val targetResource = fullResource * (thresholds / 100.0)
            val usableResource = currentResource - targetResource
            val upgradableNumber = if (wallCost > 0) kotlin.math.ceil(usableResource / wallCost).toInt().coerceAtLeast(0) else 0
            ShowMessage("升级单个城墙花费：$wallCost\n当前资源数量：${currentResource}，当前资源百分比：${currentPercent}\n可升级数量：$upgradableNumber")
            TouchActions.tap(1130, 54, delayTime = 500)
            val upgradeCrossMark = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeWallCrossMark), duration = 1000)
            if (upgradeCrossMark != null) {
                TouchActions.tap(upgradeCrossMark.x, upgradeCrossMark.y, delayTime = 500)
                val upgrade10WallsGreenCrossMark = findMultiColorsUntil(schemas = listOf(MyColors.Upgrade10WallsGreenCrossMark), duration = 1000)
                // Rescope to the right of the "10 walls" mark if found, otherwise use the original schema
                val greenCrossMarkSchema = if (upgrade10WallsGreenCrossMark != null) {
                    ColorSchema.rescope(
                        MyColors.UpgradeWallGreenCrossMark, upgrade10WallsGreenCrossMark.x + 10, 490, 1100, 630
                    )
                } else {
                    MyColors.UpgradeWallGreenCrossMark
                }
                val upgradeGreenCrossMark = findMultiColors(schema = greenCrossMarkSchema)
                if (upgradeGreenCrossMark != null) {
                    repeat(upgradableNumber - 1) {
                        TouchActions.tap(upgradeGreenCrossMark.x, upgradeGreenCrossMark.y, delayTime = 50)
                    }
                }

                // Rescope both hammer schemas to match the elixir search direction when needed (same pattern as line 153-155)
                val doubleHammerSchema = ColorSchema.rescope(
                    MyColors.DoubleHammer, MyColors.DoubleHammer.x1, MyColors.DoubleHammer.y1, MyColors.DoubleHammer.x2, MyColors.DoubleHammer.y2, searchDirection
                )
                val upgradeHammerSchema = ColorSchema.rescope(
                    MyColors.UpgradeHammer, MyColors.UpgradeHammer.x1, MyColors.UpgradeHammer.y1, MyColors.UpgradeHammer.x2, MyColors.UpgradeHammer.y2, searchDirection
                )
                val doubleHammer = findMultiColorsUntil(schemas = listOf(doubleHammerSchema, upgradeHammerSchema), duration = 300)

                if (doubleHammer != null) {

                    TouchActions.tap(doubleHammer.x, doubleHammer.y, delayTime = 500)
                    TouchActions.tap(882, 440, delayTime = 300)
                    //Just in case there is only one wall can be upgraded, we need to click the normal upgrade button again
                    if (currentBase == BaseType.Main) TouchActions.tap(980, 635, delayTime = 500)
                    else if (currentBase == BaseType.Builder) TouchActions.tap(640, 640, delayTime = 500)
                    clickRightBottom(1)
                    return LoopAction.Proceed
                }
            }
        }
    } else {
        if (currentBase == BaseType.Main) TouchActions.tap(980, 635, delayTime = 500)
        else if (currentBase == BaseType.Builder) TouchActions.tap(640, 640, delayTime = 500)
        return LoopAction.Proceed
    }
    return LoopAction.Proceed
}

// Handles the hero upgrade flow; returns LoopAction to control the caller's loop
private suspend fun upgradeHero(building: String): LoopAction {
    if (building != "飞龙公爵") {
        // Calculate the search area based on hero position
        // Base area for 野蛮人之王: x1=55, y1=500, x2=240, y2=530
        // Each subsequent hero shifts by 228 on x-axis
        val heroIndex = listOf("野蛮人之王", "弓箭女皇", "大守护者", "飞盾战神").indexOf(building)
        ShowMessage("升级英雄：$building，序号$heroIndex")
        val x1 = 55 + heroIndex * 228
        val x2 = 240 + heroIndex * 228

        // Create a ColorSchema with the same color pattern but different search area
        val heroInsufficientResources = ColorSchema.parse(
            x1, 500, x2, 530, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "${building}资源不足"
        )

        if (findMultiColors(schema = heroInsufficientResources) != null) {
            ShowMessage("${building}资源不足，跳过")
            return LoopAction.Break
        }
        TouchActions.tap(x1 + 100, 500, delayTime = 500)//Upgrade Hero
        ShowMessage("等待结束")
        TouchActions.tap(902, 626, delayTime = 500)
        clickRightBottom(times = 3, delayTime = 200)
    } else {
        // The unique logic for Dragon Duke
        ShowMessage("升级英雄：飞龙公爵")
        TouchActions.swipe(1189, 354, 120, 345, delayTime = 300)
        delayWithMultiplier(200)
        val heroInsufficientResources = ColorSchema.parse(
            1020, 480, 1230, 530, "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "${building}资源不足"
        )
        if (findMultiColors(schema = heroInsufficientResources) != null) {
            ShowMessage("${building}资源不足，跳过")
            return LoopAction.Break
        }
        TouchActions.tap(1020 + 100, 480, delayTime = 500)//Upgrade Hero
        TouchActions.tap(902, 626, delayTime = 500)
        clickRightBottom(times = 3, delayTime = 200)
    }
    return LoopAction.Proceed
}

// Handles the regular building upgrade flow; returns LoopAction to control the caller's loop
private suspend fun upgradeBuilding(building: String, currentBase: BaseType, attempt: Int): LoopAction {
    TouchActions.tap(1233, 37)// tap gold to close worker list
    // Check for the upgrade action (Hammer icon)
    val hammer = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeHammer), duration = 1000) ?: return LoopAction.Continue // Should not happen if build was found, but be safe

    TouchActions.tap(hammer.x, hammer.y, delayTime = 500)

    // Check for resource availability immediately after clicking upgrade
    if (findMultiColors(schema = if (currentBase == BaseType.Main) MyColors.MainBaseInsufficientResources else MyColors.BuilderBaseInsufficientResources) != null) {
        clickRightBottom(1)
        return LoopAction.Break // insufficient resources for this building, skip to next building type
    }

    // Successful upgrade flow
    TouchActions.tap(633, 631) // normal upgrade or unlock new buildings
    TouchActions.tap(982, 634, delayTime = 500)// machines
    if (building == "大本营") {
        TouchActions.tap(748, 621, delayTime = 500)//before upgrade
        zoomSmallMainBase()
        var greenTick = mainBaseFindBuildButton(type = BuildButtonType.Tick, duration = 800)
        if (greenTick != null) {
            ShowMessage("合并天鹰火炮")
            TouchActions.tap(greenTick.x, greenTick.y, delayTime = 500)
            return LoopAction.Break
        }
        TouchActions.swipe(922, 202, 298, 505)
        delayWithMultiplier(300)
        greenTick = mainBaseFindBuildButton(type = BuildButtonType.Tick, duration = 800)
        if (greenTick != null) {
            ShowMessage("合并天鹰火炮")
            TouchActions.tap(greenTick.x, greenTick.y, delayTime = 500)
            return LoopAction.Break
        }
    }
    clickRightBottom(2)
    ShowMessage("升级成功: $building (第 $attempt 个)")
    return LoopAction.Proceed
}

private suspend fun findSpecificBuilding(buildingName: String, excludeNewBuildings: Boolean = false): Boolean {
    var found = false
    ShowMessage("准备寻找$buildingName")
    iterateBuilderBaseBuildingUpgradeList(excludeNewBuildings = excludeNewBuildings, onDetect = { result ->
        val building = result.buildings.find { it.name == buildingName }
        if (building != null) {
            ShowMessage("已找到$buildingName")
            TouchActions.tap(building.x + 20, building.y + 20, delayTime = 1000)
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
            val priority = priorityStr.toIntOrNull() ?: logAndRestart("${settingDef.displayName} 必须是数字，请检查配置")
            settingDef.displayName to priority
        }

        BaseType.Builder -> BuilderBaseBuildingsPriority.all.associate { settingDef ->
            val priorityStr = getConfigRuntime(settingDef.key)
            val priority = priorityStr.toIntOrNull() ?: logAndRestart("${settingDef.displayName} 必须是数字，请检查配置")
            settingDef.displayName to priority
        }
    }

    return buildings.filter { it in priorityMap && it in enabledBuildingNames }.sortedBy {
        priorityMap[it]!!
    }
}

// Represents the loop control action returned by extracted upgrade functions
private enum class LoopAction {
    Break, Continue, Proceed, ReturnStop
}

