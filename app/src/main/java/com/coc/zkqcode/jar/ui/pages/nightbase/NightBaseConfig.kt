package com.coc.zkqcode.jar.ui.pages.nightbase

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.core.ui.components.CustomButton
import com.coc.zkqcode.core.ui.components.SettingCheckBox
import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.core.ui.components.SettingInputRow
import com.coc.zkqcode.utils.database.Schema.NIGHT_BASE_SETTINGS

fun LazyListScope.NightBaseConfig(
    index: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onNavigateNightPriority: (Int) -> Unit = {}
) {
    item {
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
                onClick = onToggleExpanded,
                text = if (isExpanded) "▼ 缩起夜世界设置" else "▶ 展开夜世界设置"
            )
        }
    }

    if (isExpanded) {
        item {
            SettingCheckBox(
                key = "${NIGHT_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}",
                explain = "勾选后，紫孔雀将完全不会进入夜世界。换言之，夜世界的所有设置都将失效！\n但因为紫孔雀只会接取夜世界竞赛任务，所以如果接取了部落竞赛的任务，那么就算勾选了不打夜世界，紫孔雀也会打夜世界。"
            )
        }

        if (GlobalVars.configStates["${NIGHT_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0") {
            item {
                Row {
                    SettingCheckBox(
                        key = "${NIGHT_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c${index}",
                        explain = "紫孔雀会自动配兵，暂不支持手动配兵。若未勾选“上分模式”和“刷圣水车”，紫孔雀就会根据账号的资源数量，智能选择对战模式。"
                    )
                    SettingCheckBox(key = "${NIGHT_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.key}_c${index}")
                }
            }
            item { SettingInputRow(key = "${NIGHT_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLES.key}_c${index}") }
            item {
                Row {
                    SettingCheckBox(
                        key = "${NIGHT_BASE_SETTINGS.TROPHY_PUSHING_MODE.key}_c${index}",
                        explain = "勾选后，紫孔雀会使用暗夜女巫进行上分，刷圣水效率会显著降低，请谨慎勾选。不可与“刷圣水车”同时勾选。"
                    )
                    SettingCheckBox(
                        key = "${NIGHT_BASE_SETTINGS.ELIXIR_CART_FARMING.key}_c${index}",
                        explain = "勾选后，夜世界对战时下兵后会立刻投降，因此几乎无法刷金币，请谨慎勾选。不可与“上分模式”同时勾选。"
                    )
                }
            }
            item { SettingCheckBox(key = "${NIGHT_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key}_c${index}") }
            
            if (GlobalVars.configStates["${NIGHT_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key}_c${index}"]?.value == "1") {
                item { NightBaseResearchConfigs(index = index) }
            }
            
            item {
                FlowRow {
                    SettingCheckBox(key = "${NIGHT_BASE_SETTINGS.NIGHT_BUILD_SETTING.key}_c${index}")
                    SettingCheckBox(key = "${NIGHT_BASE_SETTINGS.NIGHT_WALL_UPGRADE_SETTINGS.key}_c${index}")
                    SettingCheckBox(key = "${NIGHT_BASE_SETTINGS.NIGHT_REMOVE_OBSTACLES.key}_c${index}")
                    SettingCheckBox(key = "${NIGHT_BASE_SETTINGS.NIGHT_SAVE_WORKER.key}_c${index}")
                }
            }
            
            if (GlobalVars.configStates["${NIGHT_BASE_SETTINGS.NIGHT_BUILD_SETTING.key}_c${index}"]?.value == "1") {
                item { NightBaseUpgradeConfigs(index = index, onNavigatePriority = onNavigateNightPriority) }
            }
        }
    }
}