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
import com.coc.zkqcode.utils.components.ExpandableContent
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema.MAIN_BASE_SETTINGS

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
    ExpandableContent(isMainBaseExpanded) {
        Text(
            text = "紫孔雀会自动配兵，暂不支持手动配兵。",
            style = MaterialTheme.typography.labelMedium
        )
        CustomCheckBox(
            text = MAIN_BASE_SETTINGS.AUTO_ATTACK.displayName,
            checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index"]!!.value,
            onCheckStateChange = { checked ->
                GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index"]!!.value =
                    if (checked) "1" else "0"
            },
        )
        InputRow(
            label = MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.displayName + "(-1表示默认值):",
            value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key}_c$index"]!!.value,
            onValueChange = { GlobalVars.configStates["${MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key}_c$index"]!!.value = it }
        )
        InputRow(
            label = MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.displayName + "(-1表示默认值):",
            value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key}_c$index"]!!.value,
            onValueChange = {
                GlobalVars.configStates["${MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key}_c$index"]!!.value = it
            }
        )
        InputRow(
            label = MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.displayName + "(-1表示默认值):",
            value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key}_c$index"]!!.value,
            onValueChange = {
                GlobalVars.configStates["${MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key}_c$index"]!!.value = it
            }
        )
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.displayName,
                explain = "勾选后，辅助会跳过前两个搜到的目标，并且会根据所有搜索到的目标的可获得资源的平均值来搜鱼要求。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.displayName,
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.STOP_BATTLE_WHEN_NO_STAR.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.STOP_BATTLE_WHEN_NO_STAR.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.STOP_BATTLE_WHEN_NO_STAR.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LADDER.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LADDER.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.PLAY_LADDER.displayName,
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LADDER.key}_c$index"]!!.value == "1") {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CHANGE_BASE.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CHANGE_BASE.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CHANGE_BASE.displayName,
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.displayName,
                explain = "勾选后，如果当前处于对战冷却时间，则会一直等待到冷却结束。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESTART_GAME.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESTART_GAME.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.RESTART_GAME.displayName,
                explain = "勾选后，部署完所有部队后，辅助会重启游戏。仅对主世界对战有效。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.MANUAL_TRAINING.displayName,
                explain = "勾选后，辅助将不会进入配兵页面，请手动配兵。\n\n注意：辅助不会自动开启超级兵。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CHANGE_HEROES.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CHANGE_HEROES.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CHANGE_HEROES.displayName,
                explain = "勾选后，辅助会随机更换英雄，宠物以及装备。可能影响到升级穿戴装备的功能，请谨慎勾选。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index"]!!.value == "0") {
            DropdownButton(
                options = listOf("单面一字划", "双面一字划", "四面长按"),
                selectedIndex = GlobalVars.configStates["${MAIN_BASE_SETTINGS.TACTICS_MODE.key}_c$index"]!!.value.toIntOrNull()
                    ?: 0,
                onValueChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.TACTICS_MODE.key}_c$index"]!!.value = it.toString()
                },
                label = MAIN_BASE_SETTINGS.TACTICS_MODE.displayName
            )
        }
        CustomCheckBox(
            checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index"]!!.value,
            onCheckStateChange = {
                GlobalVars.configStates["${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index"]!!.value =
                    if (it) "1" else "0"
            },
            text = MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.displayName,
            explain = "勾选后，需配合AI下兵插件才能正常使用。建议使用前仔细阅读官网教程。"
        )
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index"]!!.value == "1") {
            InputRow(
                label = MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_SWEEPER.displayName + ":",
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_SWEEPER.key}_c$index"]!!.value,
                onValueChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_SWEEPER.key}_c$index"]!!.value = it
                }
            )
            InputRow(
                label = MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_DEFENCE.displayName + ":",
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_DEFENCE.key}_c$index"]!!.value,
                onValueChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_DEFENCE.key}_c$index"]!!.value = it
                }
            )
            InputRow(
                label = MAIN_BASE_SETTINGS.LIGHTING_ON_WIZARD_TOWER.displayName + ":",
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_WIZARD_TOWER.key}_c$index"]!!.value,
                onValueChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_WIZARD_TOWER.key}_c$index"]!!.value = it
                }
            )
            InputRow(
                label = MAIN_BASE_SETTINGS.LIGHTING_ON_MORTAR.displayName + ":",
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_MORTAR.key}_c$index"]!!.value,
                onValueChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.LIGHTING_ON_MORTAR.key}_c$index"]!!.value = it
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
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = MAIN_BASE_SETTINGS.DONATION_SETTING.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key}_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = MAIN_BASE_SETTINGS.RESEARCH_SETTING.displayName,
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key}_c$index"]!!.value =
                        if (it) "1" else "0"

                },
                text = MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.displayName,
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index"]!!.value == "1") {
            InputRow(
                label = MAIN_BASE_SETTINGS.DONATION_TIMES.displayName + ":",
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_TIMES.key}_c$index"]!!.value,
                onValueChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_TIMES.key}_c$index"]!!.value = it
                }
            )
        }

        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index"]!!.value == "1") {
            ResearchConfigs(index)
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface
        )

        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.BUILD_SETTING.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.SAVE_WORKER.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.SAVE_WORKER.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.SAVE_WORKER.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILDING_CONVERSION_SETTINGS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILDING_CONVERSION_SETTINGS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.BUILDING_CONVERSION_SETTINGS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_AFTER_FAIL_WALL_UPGRADE.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_AFTER_FAIL_WALL_UPGRADE.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.UPGRADE_AFTER_FAIL_WALL_UPGRADE.displayName,
                explain = "勾选后，若主世界无城墙可升级，则会将所有工人用于建造。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index"]!!.value == "1") {
            FlowRow {
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key}_c$index"]!!.value,
                    onCheckStateChange = { checked ->
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key}_c$index"]!!.value =
                            if (checked) "1" else "0"
                    },
                    text = MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.displayName
                )
                InputRow(
                    label = MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.displayName,
                    value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key}_c$index"]!!.value,
                    onValueChange = {
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key}_c$index"]!!.value = it
                    }
                )
            }
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]!!.value == "1") {
            UpgradeConfigs(index)
        }
        CustomCheckBox(
            checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]!!.value,
            onCheckStateChange = { checked ->
                GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]!!.value =
                    if (checked) "1" else "0"
            },
            text = MAIN_BASE_SETTINGS.UPGRADE_PETS.displayName
        )
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]!!.value == "1") {
            PetConfigs(index)
        }

        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.HELPER_SETTINGS.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.HELPER_SETTINGS.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.HELPER_SETTINGS.displayName,
                explain = "勾选后，会自动用实验助手以及建筑工人学徒。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.displayName,
                explain = "升级实验助手的优先级高于购买建筑工人和升级工人学徒，请谨慎勾选！"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.displayName,
                explain = "升级工人学徒的优先级高于购买建筑工人，请谨慎勾选！"
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.DO_CLAN_GAMES.displayName,
                explain = "勾选后，紫孔雀会接取小部分夜世界任务，例如夜世界摧毁率等。注意重点是\"小部分\"，也就是说并不是所有任务都可以接取，并且只会接取夜世界任务！若没有任务可接取，则会放弃第一个任务。接取任务后，会自动打夜世界。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.displayName,
                explain = "勾选后，部落战会进攻推荐对手。胜率较低，容易黑三，建议谨慎勾选。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LEAGUE.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LEAGUE.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.PLAY_LEAGUE.displayName,
                explain = "勾选后，会进攻最后一位没有被部落成员进攻过的对手。胜率较低，容易黑三，建议谨慎勾选。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_RAID.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_RAID.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.PLAY_RAID.displayName
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_CLAN_WAR_SETTINGS.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_CLAN_WAR_SETTINGS.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.START_CLAN_WAR_SETTINGS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_LEAGUE_SETTINGS.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_LEAGUE_SETTINGS.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.START_LEAGUE_SETTINGS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_RAID.key}_c$index"]!!.value,
                onCheckStateChange = {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_RAID.key}_c$index"]!!.value =
                        if (it) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.START_RAID.displayName
            )
        }
        FlowRow {
            listOf(
                "buy_star_ore_with_raid_medal",
                "buy_clock_tower_potion_with_raid_medal",
                "buy_ring_of_wall_with_raid_medal",
                "buy_research_potion_with_raid_medal",
                "buy_training_potion_with_raid_medal",
                "buy_research_potion_with_league_medal",
                "buy_builder_potion_with_league_medal",
                "buy_star_ore_with_event_medal",
                "buy_builder_potion_with_event_medal",
                "buy_new_equipment_with_event_medal",
                "buy_research_potion_with_event_medal",
                "use_research_potion",
                "sell_training_potion",
                "use_clock_tower_potion",
                "sell_clock_tower_potion",
                "use_builder_potion",
                "sell_ring_of_wall"
            ).forEach { key ->
                CustomCheckBox(
                    checkedState = GlobalVars.configStates["${key}_c$index"]!!.value,
                    onCheckStateChange = { checked ->
                        GlobalVars.configStates["${key}_c$index"]!!.value =
                            if (checked) "1" else "0"
                    },
                    text = MAIN_BASE_SETTINGS.all.first { setting -> setting.key == key }.displayName
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )

        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WEARABLE_GEAR.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WEARABLE_GEAR.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.UPGRADE_WEARABLE_GEAR.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.displayName,
                explain = "勾选后，当主世界奖杯大于500时生效。有极小概率（约1%）移除稀有物品"
            )

            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_TIMED_REWARDS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_TIMED_REWARDS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CLAIM_TIMED_REWARDS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_TOKEN_REWARDS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_TOKEN_REWARDS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CLAIM_TOKEN_REWARDS.displayName,
                explain = "仅在资源全满后才会领取"
            )

            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_CAPITAL_GOLD.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_CAPITAL_GOLD.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CLAIM_CAPITAL_GOLD.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATE_CAPITAL_GOLD.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATE_CAPITAL_GOLD.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.DONATE_CAPITAL_GOLD.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.displayName
            )

            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.displayName
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.displayName,
                explain = "勾选此选项后，紫孔雀不仅会自动加部落，也会自动建造部落城堡。但若不勾选此选项，就既不会加部落，也不会建造部落城堡。注意：加部落功能仅对未加入部落的账号生效。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.USE_TEMP_ITEMS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.USE_TEMP_ITEMS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.USE_TEMP_ITEMS.displayName,
                explain = "勾选此选项后，紫孔雀会使用研究浓汤和建筑工人大餐。并且为了防止重复使用导致道具失效，每次只会使用一个道具。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index"]!!.value == "1") {
            InputRow(
                label = MAIN_BASE_SETTINGS.CLAN_TAG.displayName,
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAN_TAG.key}_c$index"]!!.value,
                onValueChange = { GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAN_TAG.key}_c$index"]!!.value = it }
            )
            InputRow(
                label = MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.displayName,
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key}_c$index"]!!.value,
                onValueChange = { GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key}_c$index"]!!.value = it }
            )
        }
        FlowRow {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.displayName,
                explain = "勾选此选项后，当金币大于总容量80-85%后，紫孔雀将反复创建部落，直到部落标签出现大于或等于用户设定的连续数字或字母为止，或直到金币消耗完为止。\n\n注意：\n必须先建造部落城堡，才能勾选此项，否则会出现异常。\n部分设备使用此功能后，需要手动切换输入法。具体切换方法请参考官网教程。\n部分云手机不支持读取剪贴板，建议在电脑模拟器里使用本功能。"
            )
            CustomCheckBox(
                checkedState = GlobalVars.configStates["${MAIN_BASE_SETTINGS.INVITE_PLAYERS.key}_c$index"]!!.value,
                onCheckStateChange = { checked ->
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.INVITE_PLAYERS.key}_c$index"]!!.value =
                        if (checked) "1" else "0"
                },
                text = MAIN_BASE_SETTINGS.INVITE_PLAYERS.displayName,
                explain = "勾选此选项后，紫孔雀将会从公告栏邀请玩家加入部落。\n\n注意：单次邀请耗时约1小时。\n请先加入部落后再开启本功能。\n请确保账号拥有邀请玩家的权限。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index"]!!.value == "1") {
            InputRow(
                label = MAIN_BASE_SETTINGS.CLAN_NAME.displayName,
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAN_NAME.key}_c$index"]!!.value,
                onValueChange = { GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAN_NAME.key}_c$index"]!!.value = it }
            )
            InputRow(
                label = MAIN_BASE_SETTINGS.CONSECUTIVE_COUNT.displayName,
                value = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CONSECUTIVE_COUNT.key}_c$index"]!!.value,
                onValueChange = { GlobalVars.configStates["${MAIN_BASE_SETTINGS.CONSECUTIVE_COUNT.key}_c$index"]!!.value = it }
            )
        }
    }
}
