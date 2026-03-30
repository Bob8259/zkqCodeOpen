package com.coc.zkqcode.jar.code.universal.buildings.walls

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.UpgradeResult
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.WallType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeAllExistingBuildings
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.core.util.basic.ShowMessage
import kotlin.math.roundToInt

suspend fun upgradeWalls(currentBase: BaseType): Boolean {
    // Check whether wall upgrade is enabled for the current base
    val wallUpgradeKey = when (currentBase) {
        BaseType.Main -> Schema.MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key
        BaseType.Builder -> Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_WALL_UPGRADE_SETTINGS.key
    }
    if (!getBooleanConfigRuntime(wallUpgradeKey)) {
        return true
    }
    val workerNumber = WorkerAndResearch.detectWorkerNumber(currentBase)

    if (workerNumber.available < 1 || workerNumber.total < 1) {
        return true
    }
    // Use the appropriate threshold key and display name based on the current base type
    val (wallThresholdKey, wallThresholdName) = when (currentBase) {
        BaseType.Main -> Schema.MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key to
            Schema.MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.displayName
        BaseType.Builder -> Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_UPGRADE_WALL_THRESHOLD.key to
            Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_UPGRADE_WALL_THRESHOLD.displayName
    }
    val thresholds = 25.coerceAtLeast(getConfigRuntime(wallThresholdKey).toIntOrNull()
        ?: logAndRestart("$wallThresholdName 必须是数字，请检查配置"))
    val startTime = System.currentTimeMillis()
    val timeoutMs = 5 * 60 * 1000L // 5 minutes

    // 1. Gold Upgrade Loop
    while (System.currentTimeMillis() - startTime < timeoutMs) {
        val currentResourcePercentage = calculateResourcesPercentage(currentBase)
        ShowMessage("当前金币百分比${currentResourcePercentage.gold}\n设置阈值${thresholds}")
        
        if (currentResourcePercentage.gold < thresholds) break
        
        when (upgradeAllExistingBuildings(listOf("城墙"), currentBase, skipOrdering = true, WallType.Gold)) {
            UpgradeResult.Failure -> return false
            UpgradeResult.StopLoop -> break
            UpgradeResult.Success -> {}
        }
        delayWithMultiplier(300)
    }

    // 2. Elixir Upgrade Loop
    while (System.currentTimeMillis() - startTime < timeoutMs) {
        val currentResourcePercentage = calculateResourcesPercentage(currentBase)
        ShowMessage("当前圣水百分比${currentResourcePercentage.elixir}\n设置阈值${thresholds}")
        
        if (currentResourcePercentage.elixir < thresholds) break
        
        when (upgradeAllExistingBuildings(listOf("城墙"), currentBase, skipOrdering = true, WallType.Elixir)) {
            UpgradeResult.Failure -> return false
            UpgradeResult.StopLoop -> break
            UpgradeResult.Success -> {}
        }
        delayWithMultiplier(300)
    }
    return true
}

data class ResourcesPercentage(
    val gold: Int = 0, val elixir: Int = 0
)

// 100% resource bar position (x coordinate) and 0% position
private const val RESOURCE_FULL_X = 1012
private const val RESOURCE_EMPTY_X = 1261

/**
 * Calculate the resource percentage based on the x coordinate of the resource bar.
 * Linear interpolation: x=1012 -> 100%, x=1268 -> 0%.
 */
private fun calculatePercentage(x: Int): Int {
    return ((RESOURCE_EMPTY_X - x) * 100.0 / (RESOURCE_EMPTY_X - RESOURCE_FULL_X)).roundToInt().coerceIn(0, 100)
}

suspend fun calculateResourcesPercentage(currentBase: BaseType): ResourcesPercentage {
    val goldSchema = if (currentBase == BaseType.Builder) MyColors.BuilderBaseGold else MyColors.GoldColor
    val elixirSchema = if (currentBase == BaseType.Builder) MyColors.BuilderBaseExiler else MyColors.ElixirColor

    // Calculate gold percentage
    val goldPosition = findMultiColors(schema = goldSchema)
    val goldPercent = if (goldPosition != null) calculatePercentage(goldPosition.x) else 0

    // Calculate elixir percentage
    val elixirPosition = findMultiColors(schema = elixirSchema)
    val elixirPercent = if (elixirPosition != null) calculatePercentage(elixirPosition.x) else 0

    return ResourcesPercentage(gold = goldPercent, elixir = elixirPercent)
}