package com.coc.zkqcode.jar.code.universal.buildings.walls

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.WallType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeAllExistingBuildings
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
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

    val thresholds = 25.coerceAtLeast(getConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key).toInt())
    val currentResourcePercentage = calculateResourcesPercentage()
    if (currentResourcePercentage.gold >= thresholds) {
        if (!upgradeAllExistingBuildings(listOf("城墙"), currentBase, skipOrdering = true, currentBase, WallType.Gold)) return false
    }
    if (currentResourcePercentage.elixir >= thresholds) {
        if (!upgradeAllExistingBuildings(listOf("城墙"), currentBase, skipOrdering = true, currentBase, WallType.Elixir)) return false
    }
    return true
}

data class ResourcesPercentage(
    val gold: Int = 0,
    val elixir: Int = 0
)

// 100% resource bar position (x coordinate) and 0% position
private const val RESOURCE_FULL_X = 1012
private const val RESOURCE_EMPTY_X = 1268

/**
 * Calculate the resource percentage based on the x coordinate of the resource bar.
 * Linear interpolation: x=1012 -> 100%, x=1268 -> 0%.
 */
private fun calculatePercentage(x: Int): Int {
    return ((RESOURCE_EMPTY_X - x) * 100.0 / (RESOURCE_EMPTY_X - RESOURCE_FULL_X))
        .roundToInt()
        .coerceIn(0, 100)
}

suspend fun calculateResourcesPercentage(): ResourcesPercentage {
    // Calculate gold percentage
    val goldPosition = findMultiColors(schema = MyColors.GoldColor)
    val goldPercent = if (goldPosition != null) calculatePercentage(goldPosition.x) else 0

    // Calculate elixir percentage
    val elixirPosition = findMultiColors(schema = MyColors.ElixirColor)
    val elixirPercent = if (elixirPosition != null) calculatePercentage(elixirPosition.x) else 0

    return ResourcesPercentage(gold = goldPercent, elixir = elixirPercent)
}