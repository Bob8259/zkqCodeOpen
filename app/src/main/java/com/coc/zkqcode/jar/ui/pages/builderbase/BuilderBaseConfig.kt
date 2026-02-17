package com.coc.zkqcode.jar.ui.pages.builderbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingCheckBox
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.BUILDER_BASE_SETTINGS

fun LazyListScope.BuilderBaseConfig(
    index: Int, isExpanded: Boolean, onToggleExpanded: () -> Unit, onNavigateNightPriority: (Int) -> Unit = {},
    onScrollToBottom: () -> Unit = {}
) {
    item {
        FlowRow {
            Text(
                text = "以下是夜世界设置", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Start
            )
            // 添加一个按钮来控制缩放
            CustomButton(
                onClick = onToggleExpanded, text = if (isExpanded) "▼ 缩起夜世界设置" else "▶ 展开夜世界设置"
            )
        }
    }

    item {
        AnimatedVisibility(visible = isExpanded) {
            Column {
                val noBuilderBase = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}"]?.value == "0"
                LaunchedEffect(noBuilderBase) {
                    if (noBuilderBase) onScrollToBottom()
                }
                SettingCheckBox(
                    key = "${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c${index}",
                    explain = "勾选后，紫孔雀将完全不会进入夜世界。换言之，夜世界的所有设置都将失效！\n但因为紫孔雀只会接取夜世界竞赛任务，所以如果接取了部落竞赛的任务，那么就算勾选了不打夜世界，紫孔雀也会打夜世界。"
                )

                AnimatedVisibility(visible = noBuilderBase) {
                    Column {
                        Row {
                            SettingCheckBox(
                                key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c${index}",
                                explain = "辅助会自动配兵，暂不支持手动配兵。若未勾选\u201C上分模式\u201D和\u201C刷圣水车\u201D，辅助就会根据账号的资源数量，智能选择对战模式。"
                            )
                            SettingCheckBox(key = "${BUILDER_BASE_SETTINGS.STOP_WHEN_RESOURCE_FULL.key}_c${index}")
                        }
                        SettingInputRow(key = "${BUILDER_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLES.key}_c${index}")
                        SettingInputRow(key = "${BUILDER_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLES_WITH_TASKS.key}_c${index}")
                        Row {
                            SettingCheckBox(
                                key = "${BUILDER_BASE_SETTINGS.TROPHY_PUSHING_MODE.key}_c${index}",
                                explain = "勾选后，辅助会使用暗夜女巫进行上分，刷圣水效率会显著降低，请谨慎勾选。不可与\u201C刷圣水车\u201D同时勾选。"
                            )
                            SettingCheckBox(
                                key = "${BUILDER_BASE_SETTINGS.ELIXIR_CART_FARMING.key}_c${index}",
                                explain = "勾选后，夜世界对战时下兵后会立刻投降，因此几乎无法刷金币，请谨慎勾选。不可与\u201C上分模式\u201D同时勾选。"
                            )
                        }
                        SettingCheckBox(key = "${BUILDER_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key}_c${index}")

                        AnimatedVisibility(visible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key}_c${index}"]?.value == "1") {
                            BuilderBaseResearchConfigs(index = index)
                        }

                        FlowRow {
                            SettingCheckBox(key = "${BUILDER_BASE_SETTINGS.NIGHT_BUILD_SETTING.key}_c${index}")
                            SettingCheckBox(key = "${BUILDER_BASE_SETTINGS.NIGHT_WALL_UPGRADE_SETTINGS.key}_c${index}")
                            SettingCheckBox(key = "${BUILDER_BASE_SETTINGS.NIGHT_REMOVE_OBSTACLES.key}_c${index}")
                            SettingCheckBox(key = "${BUILDER_BASE_SETTINGS.NIGHT_SAVE_WORKER.key}_c${index}")
                        }

                        val nightBuildVisible = GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NIGHT_BUILD_SETTING.key}_c${index}"]?.value == "1"
                        LaunchedEffect(nightBuildVisible) {
                            if (nightBuildVisible) onScrollToBottom()
                        }
                        AnimatedVisibility(visible = nightBuildVisible) {
                            BuilderBaseUpgradeConfigs(index = index, onNavigatePriority = onNavigateNightPriority)
                        }
                    }
                }
            }
        }
    }
}