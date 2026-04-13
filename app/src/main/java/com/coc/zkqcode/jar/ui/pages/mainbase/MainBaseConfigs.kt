@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingInputRowWithSuffix
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_SETTINGS

fun LazyListScope.MainBaseConfig(
    index: Int,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    isResearchExpanded: Boolean,
    onToggleResearch: () -> Unit,
    isBuildExpanded: Boolean,
    onToggleBuild: () -> Unit,
    isPetExpanded: Boolean,
    onTogglePet: () -> Unit,
    onNavigatePriority: (Int) -> Unit = {}
) {
    // ⚔️ Banner — 核心对战设置（主世界）
    item {
        SectionBanner(
            title = "⚔️ 核心对战设置（主世界）",
            isExpanded = isExpanded,
            onClick = onToggleExpanded
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
                    key = "${MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key}_c$index", explain = "勾选后，辅助会跳过前两个搜到的目标，并且会根据所有搜索到的目标的可获得资源的平均值来搜鱼要求。"
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
                    key = "${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key}_c$index", explain = "勾选后，如果当前处于对战冷却时间，辅助会一直等待到冷却结束。"
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.RESTART_GAME.key}_c$index", explain = "勾选后，部署完所有部队后，辅助会重启游戏。仅对主世界对战有效。"
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index", explain = "勾选后，辅助将不会进入配兵页面，请手动配兵。\n\n注意：辅助不会自动开启超级兵。"
                )
            }
        }
    }

    // Donation & Research toggle
    item {
        SettingSection(visible = isExpanded) {
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key}_c$index")
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index"]?.value == "1") {
                Column {
                    FlowRow {
                        SettingInputRowWithSuffix(
                            key = "${MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.key}_c$index", suffix = "秒钟"
                        )
                        SettingInputRowWithSuffix(
                            key = "${MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.key}_c$index", suffix = "％时开始对战"
                        )
                        SettingInputRowWithSuffix(
                            key = "${MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.key}_c$index", suffix = "％为止"
                        )
                    }
                }
            }
        }
    }

    // 🔬 Banner — 自动研究设置
    item {
        SectionBanner(
            title = "🔬 自动研究设置",
            isExpanded = isResearchExpanded,
            onClick = onToggleResearch
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

    // 🏗️ Banner — 自动建造设置（主世界）
    item {
        SectionBanner(
            title = "🏗️ 自动建造设置（主世界）",
            isExpanded = isBuildExpanded,
            onClick = onToggleBuild
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
                    SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key}_c$index")
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key}_c$index")
                }
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]?.value == "1") {
                MainBaseUpgradeConfigs(index, onNavigatePriority)
            }
        }
    }

    // 🐾 Banner — 战宠&其他发展设置
    item {
        SectionBanner(
            title = "🐾 战宠&其他发展设置",
            isExpanded = isPetExpanded,
            onClick = onTogglePet
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
                    key = "${MAIN_BASE_SETTINGS.HELPER_SETTINGS.key}_c$index", explain = "勾选后，会自动用实验助手以及建筑工人学徒。", afterChange = { checked ->
                        if (!checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index"]?.value = "0"
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index"]?.value = "0"
                        }
                    })
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index", explain = "升级实验助手的优先级低于购买新建筑工人，但高于升级工人学徒。"
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index", explain = "升级工人学徒的优先级低于购买新建筑工人和升级实验助手。"
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
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index", explain = "勾选后，辅助也会自动升级穿戴的装备。",
                    afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key}_c$index"]?.value = "1"
                        }
                    })
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index", explain = "勾选后，当主世界资源金水大于30万时生效。有小概率（约5%）移除稀有物品"
                )
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index"]?.value == "1") {
                    SettingSwitchIcon(
                        key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES_ENHANCEMENT.key}_c$index", explain = "勾选后，移除障碍物时增加随机点击，增大移除稀有物品的概率。\n适合批量挂小号时开启，防止地图建造空间不足。"
                    )
                }
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key}_c$index")
            }
        }
    }

    item {
        SettingSection(visible = isPetExpanded) {
            Column {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index",
                    explain = "勾选此选项后，辅助会自动加部落。若未指定标签，则只有无部落时才加部落。若指定标签，则每天检测一次当前是否加入指定部落。注意：需先建造部落城堡才能使用此功能。"
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
}
