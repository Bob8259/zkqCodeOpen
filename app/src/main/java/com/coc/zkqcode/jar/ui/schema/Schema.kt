@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema

import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
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

    private val keyToDefaultValue by lazy {
        SchemaRegistry.ALL_MODULES.flatMap { it.settings }.associateBy({ it.key }, { it.defaultValue.toString() })
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

    // Returns the Schema-defined default value for a key.
    // Missing defaults indicate a schema/config mismatch, so restart after logging the error.
    // Applies the same suffix-stripping logic as getDisplayName.
    fun getDefaultValue(fullKey: String): String {
        // Strip profile suffix (_c1, _c2, ...)
        var baseKey = fullKey.replace(Regex("_c\\d+$"), "")
        // Strip account suffix (trailing digits if no _c)
        if (baseKey == fullKey) {
            baseKey = fullKey.replace(Regex("\\d+$"), "")
        }
        return keyToDefaultValue[baseKey]
            ?: logAndRestart("Schema default value not found for fullKey=$fullKey, baseKey=$baseKey")
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

    val BUILDER_BASE_SETTINGS = BuilderBaseSettings
    val BUILDER_BASE_TROOPS = BuilderBaseTroops
    val BUILDER_BASE_BUILDINGS = BuilderBaseBuildings
    val BUILDER_BASE_BUILDINGS_PRIORITY = BuilderBaseBuildingsPriority
}
