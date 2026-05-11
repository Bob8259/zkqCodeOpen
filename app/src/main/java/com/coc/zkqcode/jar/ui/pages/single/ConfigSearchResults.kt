package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.SchemaRegistry
import com.coc.zkqcode.jar.ui.schema.Scope
import com.coc.zkqcode.jar.ui.schema.SettingDef

/**
 * Renders a flat filtered list of settings matching the search query.
 * Only PROFILE-scoped settings are included (config file settings).
 * Priority modules are excluded since they require dedicated navigation pages.
 */
fun LazyListScope.configSearchResults(
    query: String,
    configIndex: Int
) {
    // Collect all PROFILE-scoped settings, excluding priority modules
    val excludedModules = setOf(
        "MAIN_BASE_BUILDING_PRIORITIES",
        "BUILDER_BASE_BUILDINGS_PRIORITY"
    )

    val allProfileSettings: List<Pair<String, SettingDef>> = SchemaRegistry.ALL_MODULES
        .filter { it.scope == Scope.PROFILE && it.name !in excludedModules }
        .flatMap { module -> module.settings.map { module.name to it } }

    // Filter by displayName containing the query (case-insensitive)
    // Skip settings with blank displayName (auxiliary fields)
    val matchedSettings = allProfileSettings.filter { (_, def) ->
        def.displayName.isNotBlank() && def.displayName.contains(query, ignoreCase = true)
    }

    if (matchedSettings.isEmpty()) {
        item {
            SettingSection {
                Text(
                    text = "无匹配结果",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        }
        return
    }

    // Group matched settings by module name for visual clarity
    val grouped = matchedSettings.groupBy { it.first }

    grouped.forEach { (moduleName, settings) ->
        item {
            SettingSection {
                Text(
                    text = getModuleDisplayName(moduleName),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Column {
                    // Separate switches and toggles from input rows for better layout
                    val switches = settings.filter { (_, def) -> def.defaultValue is Int }
                    val inputs = settings.filter { (_, def) -> def.defaultValue is String }

                    if (switches.isNotEmpty()) {
                        FlowRow {
                            switches.forEach { (_, def) ->
                                SettingSwitchIcon(key = "${def.key}_c$configIndex")
                            }
                        }
                    }

                    inputs.forEach { (_, def) ->
                        SettingInputRow(key = "${def.key}_c$configIndex")
                    }
                }
            }
        }
    }
}

// Maps internal module names to user-friendly Chinese display names
private fun getModuleDisplayName(moduleName: String): String {
    return when (moduleName) {
        "MAIN_BASE_SETTINGS" -> "主世界设置"
        "MAIN_BASE_TROOPS_AND_SPELLS" -> "主世界兵种"
        "MAIN_BASE_BUILDINGS" -> "主世界建筑"
        "MAIN_BASE_PETS" -> "主世界战宠"
        "BUILDER_BASE_SETTINGS" -> "夜世界设置"
        "BUILDER_BASE_TROOPS" -> "夜世界兵种"
        "BUILDER_BASE_BUILDINGS" -> "夜世界建筑"
        else -> moduleName
    }
}
