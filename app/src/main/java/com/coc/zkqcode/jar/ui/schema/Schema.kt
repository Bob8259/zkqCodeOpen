@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema

import com.coc.zkqcode.jar.ui.schema.details.*

/**
 * The single source of truth for any setting in the app.
 */
data class SettingDef(
    val key: String, val displayName: String, val defaultValue: Any, val category: String
)

object Schema {
    private val keyToDisplayName by lazy {
        SchemaRegistry.ALL_MODULES.flatMap { it.settings }.associateBy({ it.key }, { it.displayName })
    }

    fun getDisplayName(fullKey: String): String {
        // Strip profile suffix (_c1, _c2, ...)
        var baseKey = fullKey.replace(Regex("_c\\d+$"), "")
        // Strip account suffix (trailing digits if no _c)
        if (baseKey == fullKey) {
            baseKey = fullKey.replace(Regex("\\d+$"), "")
        }
        return keyToDisplayName[baseKey] ?: fullKey
    }

    // --- 1. Global Settings Definitions (Formerly basicConfigs) ---
    val GLOBAL_SETTINGS = GlobalSettings
    val ACCOUNT_SETTINGS = AccountSettings

    // --- 2. Profile Settings Definitions ---
    val MAIN_BASE_SETTINGS = MainBaseSettings
    val MAIN_BASE_TROOPS_AND_SPELLS = MainBaseTroopsAndSpells
    val MAIN_BASE_PETS = MainBasePets
    val MAIN_BASE_BUILDINGS = MainBaseBuildings
    val MAIN_BASE_BUILDING_PRIORITIES = MainBaseBuildingPriorities

    val NIGHT_BASE_SETTINGS = NightBaseSettings
    val NIGHT_BASE_TROOPS = NightBaseTroops
    val NIGHT_BASE_BUILDINGS = NightBaseBuildings
    val NIGHT_BASE_BUILDINGS_PRIORITY = NightBaseBuildingsPriority
}
