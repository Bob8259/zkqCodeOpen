package com.coc.zkqcode.jar.ui.pages.nightbase

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.Row
import com.coc.zkqcode.utils.components.CustomCheckBox
import com.coc.zkqcode.utils.components.ExpandableContent
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema

@Composable
fun NightBaseConfig(index: Int) {
    var isNightBaseExpanded by remember { mutableStateOf(true) }
    FlowRow {
        Text(
            text = "以下是夜世界设置",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp),
            textAlign = TextAlign.Start
        )
        // 添加一个按钮来控制缩放
        CustomButton(
            onClick = { isNightBaseExpanded = !isNightBaseExpanded },
            text = if (isNightBaseExpanded) "▼ 缩起夜世界设置" else "▶ 展开夜世界设置"
        )
    }
    ExpandableContent(isNightBaseExpanded) {
        CustomCheckBox(
            checkedState = GlobalVars.configStates["no_builder_base_c${index}"]!!.value,
            onCheckStateChange = {
                GlobalVars.configStates["no_builder_base_c${index}"]!!.value = if (it) "1" else "0"
            },
            text = Schema.NIGHT_BASE_SETTINGS.NO_BUILDER_BASE.displayName,
            explain = "勾选后，紫孔雀将完全不会进入夜世界。换言之，夜世界的所有设置都将失效！\n但因为紫孔雀只会接取夜世界竞赛任务，所以如果接取了部落竞赛的任务，那么就算勾选了不打夜世界，紫孔雀也会打夜世界。"
        )
        ExpandableContent(GlobalVars.configStates["no_builder_base_c${index}"]!!.value == "0") {
            Row {
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["builder_base_farming_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["builder_base_farming_c${index}"]!!.value =
                            if (it) "1" else "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.BUILDER_BASE_FARMING.displayName,
                    explain = "紫孔雀会自动配兵，暂不支持手动配兵。若未勾选“上分模式”和“刷圣水车”，紫孔雀就会根据账号的资源数量，智能选择对战模式。"
                )
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["stop_when_resource_full_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["stop_when_resource_full_c${index}"]!!.value =
                            if (it) "1" else "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.displayName,
                )
            }
            InputRow(
                label = Schema.NIGHT_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLES.displayName,
                value = GlobalVars.configStates["switch_account_after_battles_c${index}"]!!.value,
                onValueChange = {
                    GlobalVars.configStates["switch_account_after_battles_c${index}"]!!.value = it
                }
            )
            Row {
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["trophy_pushing_mode_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["trophy_pushing_mode_c$index"]!!.value =
                            if (it) "1" else "0"
                        GlobalVars.configStates["elixir_cart_farming_c${index}"]!!.value = "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.TROPHY_PUSHING_MODE.displayName,
                    explain = "勾选后，紫孔雀会使用暗夜女巫进行上分，刷圣水效率会显著降低，请谨慎勾选。不可与“刷圣水车”同时勾选。"
                )
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["elixir_cart_farming_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["elixir_cart_farming_c$index"]!!.value =
                            if (it) "1" else "0"
                        GlobalVars.configStates["trophy_pushing_mode_c$index"]!!.value = "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.ELIXIR_CART_FARMING.displayName,
                    explain = "勾选后，夜世界对战时下兵后会立刻投降，因此几乎无法刷金币，请谨慎勾选。不可与“上分模式”同时勾选。"
                )

            }
            CustomCheckBox(
                checkedState = GlobalVars.configStates["builder_base_research_c${index}"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["builder_base_research_c${index}"]!!.value =
                        if (it) "1" else "0"
                },
                text = Schema.NIGHT_BASE_SETTINGS.BUILDER_BASE_RESEARCH.displayName,
            )
            if (GlobalVars.configStates["builder_base_research_c${index}"]!!.value == "1") {
                NightBaseResearchConfigs(index = index)
            }
            FlowRow {
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["night_build_setting_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["night_build_setting_c${index}"]!!.value = if (it) "1" else "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.NIGHT_BUILD_SETTING.displayName,
                )
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["night_wall_upgrade_settings_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["night_wall_upgrade_settings_c${index}"]!!.value = if (it) "1" else "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.NIGHT_WALL_UPGRADE_SETTINGS.displayName,
                )
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["night_remove_obstacles_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["night_remove_obstacles_c${index}"]!!.value = if (it) "1" else "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.NIGHT_REMOVE_OBSTACLES.displayName,
                )
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["night_save_worker_c${index}"]!!.value,
                    onCheckStateChange = {
                        GlobalVars.configStates["night_save_worker_c${index}"]!!.value = if (it) "1" else "0"
                    },
                    text = Schema.NIGHT_BASE_SETTINGS.NIGHT_SAVE_WORKER.displayName,
                )
            }
        }
    }
}