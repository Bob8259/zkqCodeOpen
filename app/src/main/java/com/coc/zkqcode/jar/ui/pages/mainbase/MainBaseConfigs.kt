package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.CustomCheckBox
import com.coc.zkqcode.utils.components.DropdownButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema

@Composable
fun MainBaseConfig(index: Int) {
    var isMainBaseExpanded by remember { mutableStateOf(true) }
    FlowRow {
        Text(
            text = "以下是主世界设置",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Start
        )
        // 添加一个按钮来控制缩放
        CustomButton(
            onClick = { isMainBaseExpanded = !isMainBaseExpanded },
            text = if (isMainBaseExpanded) "▼ 缩起主世界设置" else "▶ 展开主世界设置"
        )
    }
    if (isMainBaseExpanded) {

        Text(
            text = "紫孔雀会自动配兵，暂不支持手动配兵。",
            style = MaterialTheme.typography.labelMedium
        )
        CustomCheckBox(
            text = Schema.MAIN_BASE_SETTINGS.first { it.key == "auto_attack" }.displayName,
            checkedState = GlobalVars.configStates["auto_attack_c$index"]?.value ?: "0",
            onCheckStateChange = { checked ->
                GlobalVars.configStates["auto_attack_c$index"]?.value =
                    if (checked) "1" else "0"
            },
        )
        InputRow(
            label = Schema.MAIN_BASE_SETTINGS.first { it.key == "gold_requirement" }.displayName + "(-1表示默认值):",
            value = GlobalVars.configStates["gold_requirement_c$index"]?.value ?: "-1",
            onValueChange = { GlobalVars.configStates["gold_requirement_c$index"]?.value = it }
        )
        InputRow(
            label = Schema.MAIN_BASE_SETTINGS.first { it.key == "elixir_requirement" }.displayName + "(-1表示默认值):",
            value = GlobalVars.configStates["elixir_requirement_c$index"]?.value ?: "-1",
            onValueChange = {
                GlobalVars.configStates["elixir_requirement_c$index"]?.value = it
            }
        )
        InputRow(
            label = Schema.MAIN_BASE_SETTINGS.first { it.key == "dark_elixir_requirement" }.displayName + "(-1表示默认值):",
            value = GlobalVars.configStates["dark_elixir_requirement_c$index"]?.value ?: "-1",
            onValueChange = {
                GlobalVars.configStates["dark_elixir_requirement_c$index"]?.value = it
            }
        )
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["dynamic_adjustment_c$index"]?.value
                    ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["dynamic_adjustment_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "dynamic_adjustment" }.displayName,
                explain = "勾选后，辅助会跳过前两个搜到的目标，并且会根据所有搜索到的目标的可获得资源的平均值来搜鱼要求。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["stop_battle_after_full_resources_c$index"]?.value
                    ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["stop_battle_after_full_resources_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "stop_battle_after_full_resources" }.displayName,
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["stop_battle_when_no_star_c$index"]?.value
                    ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["stop_battle_when_no_star_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "stop_battle_when_no_star" }.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["play_ladder_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["play_ladder_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "play_ladder" }.displayName,
            )
        }
        if (GlobalVars.configStates["play_ladder_c$index"]?.value == "1") {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["change_base_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["change_base_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "change_base" }.displayName,
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["wait_for_battle_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["wait_for_battle_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "wait_for_battle" }.displayName,
                explain = "勾选后，如果当前处于对战冷却时间，则会一直等待到冷却结束。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["restart_game_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["restart_game_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "restart_game" }.displayName,
                explain = "勾选后，部署完所有部队后，辅助会重启游戏。仅对主世界对战有效。"
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["manual_training_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["manual_training_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "manual_training" }.displayName,
                explain = "勾选后，辅助将不会进入配兵页面，请手动配兵。\n\n注意：辅助不会自动开启超级兵。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["change_heroes_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["change_heroes_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "change_heroes" }.displayName,
                explain = "勾选后，辅助会随机更换英雄，宠物以及装备。可能影响到升级穿戴装备的功能，请谨慎勾选。"
            )
        }
        if (GlobalVars.configStates["ai_deploy_troops_c$index"]!!.value == "0") {
            DropdownButton(
                options = listOf("单面一字划", "双面一字划", "四面长按"),
                selectedIndex = GlobalVars.configStates["tactics_mode_c$index"]?.value?.toIntOrNull()
                    ?: 0,
                onValueChange = {
                    GlobalVars.configStates["tactics_mode_c$index"]?.value = it.toString()
                },
                label = Schema.MAIN_BASE_SETTINGS.first { it.key == "tactics_mode" }.displayName
            )
        }
        CustomCheckBox(
            checkedState = GlobalVars.configStates["ai_deploy_troops_c$index"]?.value ?: "",
            onCheckStateChange = {
                GlobalVars.configStates["ai_deploy_troops_c$index"]!!.value =
                    if (it) "1" else "0"
            },
            text = Schema.MAIN_BASE_SETTINGS.first { it.key == "ai_deploy_troops" }.displayName,
            explain = "勾选后，需配合AI下兵插件才能正常使用。建议使用前仔细阅读官网教程。"
        )
        if (GlobalVars.configStates["ai_deploy_troops_c$index"]!!.value == "1") {
            InputRow(
                label = Schema.MAIN_BASE_SETTINGS.first { it.key == "lighting_on_air_sweeper" }.displayName + ":",
                value = GlobalVars.configStates["lighting_on_air_sweeper_c$index"]?.value
                    ?: "-1",
                onValueChange = {
                    GlobalVars.configStates["lighting_on_air_sweeper_c$index"]!!.value = it
                }
            )
            InputRow(
                label = Schema.MAIN_BASE_SETTINGS.first { it.key == "lighting_on_air_defence" }.displayName + ":",
                value = GlobalVars.configStates["lighting_on_air_defence_c$index"]?.value
                    ?: "-1",
                onValueChange = {
                    GlobalVars.configStates["lighting_on_air_defence_c$index"]!!.value = it
                }
            )
            InputRow(
                label = Schema.MAIN_BASE_SETTINGS.first { it.key == "lighting_on_wizard_tower" }.displayName + ":",
                value = GlobalVars.configStates["lighting_on_wizard_tower_c$index"]?.value
                    ?: "-1",
                onValueChange = {
                    GlobalVars.configStates["lighting_on_wizard_tower_c$index"]!!.value = it
                }
            )
            InputRow(
                label = Schema.MAIN_BASE_SETTINGS.first { it.key == "lighting_on_mortar" }.displayName + ":",
                value = GlobalVars.configStates["lighting_on_mortar_c$index"]?.value ?: "-1",
                onValueChange = {
                    GlobalVars.configStates["lighting_on_mortar_c$index"]!!.value = it
                }
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["donation_setting_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["donation_setting_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "donation_setting" }.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["request_reinforcement_setting_c$index"]?.value
                    ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["request_reinforcement_setting_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "request_reinforcement_setting" }.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["research_setting_c$index"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["research_setting_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = Schema.MAIN_BASE_SETTINGS.first { it.key == "research_setting" }.displayName,
            )
        }
        if (GlobalVars.configStates["donation_setting_c$index"]!!.value == "1") {
            InputRow(
                label = Schema.MAIN_BASE_SETTINGS.first { it.key == "donation_times" }.displayName + ":",
                value = GlobalVars.configStates["donation_times_c$index"]?.value ?: "-1",
                onValueChange = {
                    GlobalVars.configStates["donation_times_c$index"]!!.value = it
                }
            )
        }
    }
}
