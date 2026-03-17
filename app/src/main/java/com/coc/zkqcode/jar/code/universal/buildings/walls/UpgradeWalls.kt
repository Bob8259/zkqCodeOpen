package com.coc.zkqcode.jar.code.universal.buildings.walls

import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeAllExistingBuildings
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun upgradeWalls(currentBase: BaseType): Boolean {
    // Check whether wall upgrade is enabled for the current base
    val wallUpgradeKey = when (currentBase) {
        BaseType.Main -> Schema.MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key
        BaseType.Builder -> Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_WALL_UPGRADE_SETTINGS.key
    }
    if (!getBooleanConfigRuntime(wallUpgradeKey)) {
        return true
    }

    return upgradeAllExistingBuildings(listOf("城墙"), currentBase, skipOrdering = true)
}