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

fun LazyListScope.builderBaseConfig(
    index: Int, isExpanded: Boolean, onToggleExpanded: () -> Unit, onNavigateBuilderBasePriority: (Int) -> Unit = {},
    onScrollToBottom: () -> Unit = {}
) {
    // Banner — 夜世界专属设置
    item {
        SectionBanner(
            title = "夜世界专属设置",
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
                explain = "启用后，辅助将不再进入夜世界，夜世界的所有相关设置均会失效。",
                afterChange = { checked ->
                    if (checked) {
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_NIGHT_WORLD_NORMAL_TASK.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_NIGHT_WORLD_ADVANCED_TASK.key}_c$index"]?.value = "0"
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
                    explain = "辅助将自动完成兵种配置，暂不支持手动配兵。若未启用\u201C上分模式\u201D或\u201C刷圣水车\u201D，辅助将根据当前账号的资源状况智能选择对战模式。",
                    afterChange = { checked ->
                        if (!checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_NIGHT_WORLD_NORMAL_TASK.key}_c$index"]?.value = "0"
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_NIGHT_WORLD_ADVANCED_TASK.key}_c$index"]?.value = "0"
                        }
                    }
                )
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.key}_c${index}")
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
                    explain = "启用后，辅助将使用暗夜女巫进行上分，圣水获取效率会显著下降，请谨慎启用。此选项不可与\u201C刷圣水车\u201D同时开启。",
                    afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates[elixirCartKey]?.value = "0"
                        }
                    }
                )
                SettingSwitchIcon(
                    key = elixirCartKey,
                    explain = "启用后，夜世界对战中部署兵种后将立即投降，因此几乎无法获取金币，请谨慎启用。此选项不可与\u201C上分模式\u201D同时开启。",
                    afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates[trophyKey]?.value = "0"
                        }
                    }
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
                BuilderBaseResearchConfigs(index = index)
            }
        }
    }

    // Build & Upgrade settings
    item {
        val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
        SettingSection(visible = isExpanded && noBuilderBase) {
            FlowRow {
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_BUILD_SETTING.key}_c${index}")
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_WALL_UPGRADE_SETTINGS.key}_c${index}")

                SettingSwitchIcon(
                    key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_REMOVE_OBSTACLES.key}_c${index}",
                    explain = "若尚未解锁第二区域，当金币或圣水超过30万时生效；\n若已解锁第二区域，当金币或圣水超过60万时生效。\n注意：此功能会移除野蛮人雕像，请谨慎使用。"
                )
                SettingSwitchIcon(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_SAVE_WORKER.key}_c${index}")
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
                    text = "点击调整夜世界建筑升级",
                    containerColor = AppColors.Teal
                )
            }
        }
    }
}
