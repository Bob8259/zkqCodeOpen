package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.InputRowWithCheckBox
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors

fun LazyListScope.mainBaseConfig(
    index: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    isClanExpanded: Boolean,
    onToggleClan: () -> Unit,
    isResearchExpanded: Boolean,
    onToggleResearch: () -> Unit,
    isBuildExpanded: Boolean,
    onToggleBuild: () -> Unit,
    isPetExpanded: Boolean,
    onTogglePet: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {}
) {
    // Banner — 对战设置
    item {
        SectionBanner(
            title = "对战设置（主世界）", isExpanded = isExpanded, onClick = onToggleExpanded
        )
    }

    // Attack & Resource requirements
    item {
        SettingSection(visible = isExpanded) {
            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_DAILY_REWARD.key}_c$index")
            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index")
            Text(
                text = "辅助会自动配兵，若想手动配兵，请勾选手动配兵选项。",
                style = MaterialTheme.typography.labelMedium,
            )
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key}_c$index")
        }
    }

    // Battle adjustments
    item {
        SettingSection(visible = isExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key}_c$index", explain = "启用后，辅助将自动跳过前两个搜索目标，并根据所有已搜索目标的可获取资源均值动态调整搜索条件。"
                )
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key}_c$index")
            }
        }
    }

    // Battle behaviour options
    item {
        SettingSection(visible = isExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key}_c$index", explain = "启用后，若当前处于对战冷却期，辅助将持续等待直至冷却结束。"
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.RESTART_GAME.key}_c$index", explain = "启用后，辅助将在部署全部部队后自动重启游戏。此功能仅适用于主世界对战。"
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index", explain = "启用后，辅助将跳过配兵流程，请自行完成兵种配置。\n\n注意：辅助不会自动开启超级兵种。"
                )
            }
        }
    }

    // Banner — 部落设置
    item {
        SectionBanner(
            title = "部落设置", isExpanded = isClanExpanded, onClick = onToggleClan
        )
    }

    // Clan settings content
    item {
        SettingSection(visible = isClanExpanded) {
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key}_c$index")
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index"]?.value == "1") {
                Column {
                    FlowRow {
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.key}_c$index", suffix = "秒钟"
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.key}_c$index", suffix = "％时开始对战"
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.key}_c$index", suffix = "％为止"
                        )
                    }
                }
            }

            Column {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index",
                    explain = "启用后，辅助将自动申请加入部落。若未指定部落标签，仅在当前无部落时执行加入操作；若已指定标签，则每日检测一次是否已加入目标部落。注意：使用此功能前需先建造部落城堡。"
                )
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index"]?.value == "1") {
                    Column {
                        SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_TAG.key}_c$index")
                        SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key}_c$index")
                    }
                }
            }
        }
    }

    // Banner — 研究设置
    item {
        SectionBanner(
            title = "研究设置", isExpanded = isResearchExpanded, onClick = onToggleResearch
        )
    }

    item {
        SettingSection(visible = isResearchExpanded) {
            Column {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index")
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index"]?.value == "1") {
                    MainBaseResearchConfigs(index)
                }
            }
        }
    }

    // Banner — 建造设置（主世界）
    item {
        SectionBanner(
            title = "建造设置（主世界）", isExpanded = isBuildExpanded, onClick = onToggleBuild
        )
    }

    item {
        SettingSection(visible = isBuildExpanded) {
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.SAVE_WORKER.key}_c$index")
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index"]?.value == "1") {
                Column {
                    SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key}_c$index", explain = "此功能对已开启月卡（黄金令牌）的账号不生效。")
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key}_c$index")
                }
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]?.value == "1") {
                Column {
                    CustomButton(
                        onClick = { onNavigatePriority(index) }, text = "点击调整主世界建筑升级", containerColor = AppColors.Teal
                    )
                    InputRowWithCheckBox(
                        checkBoxKey = "${MAIN_BASE_SETTINGS.INSTANT_UPGRADE.key}_c$index", inputKey = "${MAIN_BASE_SETTINGS.INSTANT_UPGRADE_THRESHOLD.key}_c$index"
                    )
                    InputRowWithCheckBox(
                        checkBoxKey = "${MAIN_BASE_SETTINGS.UPGRADE_ANY_BUILDING_AFTER_CONSECUTIVE_FAILURES.key}_c$index",
                        inputKey = "${MAIN_BASE_SETTINGS.UPGRADE_ANY_BUILDING_AFTER_CONSECUTIVE_FAILURES_THRESHOLD.key}_c$index"
                    )
                }
            }
        }
    }

    // Banner — 战宠&其他设置
    item {
        SectionBanner(
            title = "战宠&其他设置", isExpanded = isPetExpanded, onClick = onTogglePet
        )
    }

    // Pet upgrades
    item {
        SettingSection(visible = isPetExpanded) {
            Column {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index")

                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]?.value == "1") {
                    MainBasePetConfigs(index)
                }
            }
        }
    }

    // Helper settings
    item {
        SettingSection(visible = isPetExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.HELPER_SETTINGS.key}_c$index", explain = "启用后，辅助将自动使用实验助手及建筑工人学徒。", afterChange = { checked ->
                        if (!checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index"]?.value = "0"
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index"]?.value = "0"
                        }
                    })
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index", explain = "实验助手的升级优先级低于购买新建筑工人，但高于工人学徒的升级。"
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index", explain = "工人学徒的升级优先级低于购买新建筑工人及升级实验助手。"
                )
            }
        }
    }

    // Clan Games & War
    item {
        SettingSection(visible = isPetExpanded) {
            Column {
                FlowRow {
                    SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.PLAY_RAID.key}_c$index")
                }

                FlowRow {}
            }
        }
    }

    // Shop purchases
    item {
        SettingSection(visible = isPetExpanded) {
            Column {
                FlowRow {
                    listOf(
                        MAIN_BASE_SETTINGS.USE_RESEARCH_POTION,
                        MAIN_BASE_SETTINGS.USE_BUILDER_POTION,
                        MAIN_BASE_SETTINGS.USE_CLOCK_TOWER_POTION,
                        MAIN_BASE_SETTINGS.SELL_RESOURCE_POTION,
                        MAIN_BASE_SETTINGS.SELL_CLOCK_TOWER_POTION,
                        MAIN_BASE_SETTINGS.SELL_RING_OF_WALL
                    ).forEach { setting ->
                        SettingSwitchIcon(key = "${setting.key}_c$index")
                    }
                }
            }
        }
    }

    // Gear & Rewards
    item {
        SettingSection(visible = isPetExpanded) {
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key}_c$index")
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index", explain = "启用后，辅助将同时自动升级已穿戴的装备。", afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key}_c$index"]?.value = "1"
                        }
                    })
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index", explain = "启用后，当主世界金币或圣水超过30万时生效。存在约5%的概率误移除稀有障碍物。"
                )
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index"]?.value == "1") {
                    SettingSwitchIcon(
                        key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES_ENHANCEMENT.key}_c$index",
                        explain = "启用后，移除障碍物时将增加随机点击操作，以提高移除稀有障碍物的概率。\n建议在批量挂机时开启，可有效防止地图建造空间不足。"
                    )
                }
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key}_c$index")
            }
        }
    }

}
