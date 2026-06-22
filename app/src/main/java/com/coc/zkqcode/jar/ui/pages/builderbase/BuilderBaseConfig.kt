package com.coc.zkqcode.jar.ui.pages.builderbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.BUILDER_BASE_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.Strings

fun LazyListScope.builderBaseConfig(
    index: Int, isExpanded: Boolean, onToggleExpanded: () -> Unit, onNavigateBuilderBasePriority: (Int) -> Unit = {},
    onNavigateBuilderBaseResearchPriority: (Int) -> Unit = {},
    onScrollToBottom: () -> Unit = {}
) {
    // Banner — Builder Base Settings
    item {
        SectionBanner(
            title = Strings.builderBaseSettings,
            isExpanded = isExpanded,
            onClick = onToggleExpanded
        )
    }

    // NO_BUILDER_BASE toggle
    item {
        SettingSection(visible = isExpanded) {
            val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
            val prevNoBuilderBase = remember { mutableStateOf(noBuilderBase) }
            LaunchedEffect(noBuilderBase) {
                if (noBuilderBase && !prevNoBuilderBase.value) onScrollToBottom()
                prevNoBuilderBase.value = noBuilderBase
            }
            SettingSwitchIcon(
                key = "${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}",
                explain = Strings.noBuilderBaseExplain,
                afterChange = { checked ->
                    if (checked) {
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_NORMAL_TASK.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_ADVANCED_TASK.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.NO_MAIN_BASE.key}_c$index"]?.value = "0"
                    }
                }
            )
        }
    }

    // Farming & resource settings
    item {
        val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
        SettingSection(visible = isExpanded && noBuilderBase) {
            Row {
                SettingSwitchIcon(
                    key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c${index}",
                    explain = Strings.builderBaseFarmingExplain,
                    afterChange = { checked ->
                        if (!checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_NORMAL_TASK.key}_c$index"]?.value = "0"
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_ADVANCED_TASK.key}_c$index"]?.value = "0"
                        }
                    }
                )
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_STOP_BATTLE_AFTER_FULL_RESOURCES.key}_c${index}")
                SettingSwitchIcon(
                    key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_STOP_BATTLE_WHEN_NO_STAR.key}_c${index}",
                    explain = Strings.stopBattleWhenNoStarExplain
                )
            }
            SettingInputRow(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES.key}_c${index}")
            SettingInputRow(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_BATTLE_TIMES_WITH_TASKS.key}_c${index}")
        }
    }

    // Trophy & Elixir cart (mutually exclusive)
    item {
        val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
        val trophyKey = "${BUILDER_BASE_SETTINGS.TROPHY_PUSHING_MODE.key}_c${index}"
        val elixirCartKey = "${BUILDER_BASE_SETTINGS.ELIXIR_CART_FARMING.key}_c${index}"
        SettingSection(visible = isExpanded && noBuilderBase) {
            Row {
                SettingSwitchIcon(
                    key = trophyKey,
                    explain = Strings.trophyPushingExplain,
                    afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates[elixirCartKey]?.value = "0"
                        }
                    }
                )
                SettingSwitchIcon(
                    key = elixirCartKey,
                    explain = Strings.elixirCartExplain,
                    afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates[trophyKey]?.value = "0"
                        }
                    }
                )
            }
            Row {
                SettingSwitchIcon(
                    key = "${BUILDER_BASE_SETTINGS.EXIT_ON_ONE_STAR_BUILDER.key}_c${index}"
                )
            }
        }
    }

    // Research
    item {
        val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
        SettingSection(visible = isExpanded && noBuilderBase) {
            SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key}_c${index}")

            AnimatedVisibility(visible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key}_c${index}"]?.value == "1") {
                Column {
                    CustomButton(
                        onClick = { onNavigateBuilderBaseResearchPriority(index) },
                        text = Strings.adjustBuilderBaseResearchPriority,
                        containerColor = AppColors.Teal
                    )
                }
            }
        }
    }

    // Build & Upgrade settings
    item {
        val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
        SettingSection(visible = isExpanded && noBuilderBase) {
            FlowRow {
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_BUILD_SETTING.key}_c${index}")
                AnimatedVisibility(visible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_BUILD_SETTING.key}_c${index}"]?.value == "1") {
                    SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_PURCHASE_BOTO_SHACK.key}_c${index}")
                }
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_WALL_UPGRADE_SETTINGS.key}_c${index}")

                SettingSwitchIcon(
                    key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_REMOVE_OBSTACLES.key}_c${index}",
                    explain = Strings.builderBaseRemoveObstaclesExplain
                )
                AnimatedVisibility(visible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_REMOVE_OBSTACLES.key}_c${index}"]?.value == "1") {
                    Column {
                        FlowRow {
                            SettingSwitchIcon(
                                key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_CONTINUOUS_REMOVE_OBSTACLES.key}_c${index}",
                                explain = Strings.builderBaseContinuousRemoveExplain
                            )
                        }
                    }
                }
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_SAVE_WORKER.key}_c${index}")
                SettingSwitchIcon(
                    key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_SAVE_WORKER_BUILD_AFTER_WALL_FULL.key}_c${index}",
                    explain = Strings.builderBaseSaveWorkerBuildExplain,
                    afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value = "1"
                        }
                    }
                )
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_ENABLE_CLOCK_TOWER.key}_c${index}")
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_WALL_UPGRADE_SETTINGS.key}_c$index"]?.value == "1") {
                Column {
                    SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_BATCH_WALL_UPGRADE_SETTINGS.key}_c$index")
                    SettingInputRow(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_UPGRADE_WALL_THRESHOLD.key}_c$index")
                }
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_BUILD_SETTING.key}_c${index}"]?.value == "1") {
                CustomButton(
                    onClick = { onNavigateBuilderBasePriority(index) },
                    text = Strings.adjustBuilderBasePriority,
                    containerColor = AppColors.Teal
                )
            }
        }
    }
}
