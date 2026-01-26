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
import com.coc.zkqcode.utils.components.SettingCheckBox
import com.coc.zkqcode.utils.components.SettingDropdown
import com.coc.zkqcode.utils.components.ExpandableContent
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.SettingInputRow
import com.coc.zkqcode.utils.database.Schema.MAIN_BASE_SETTINGS
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun MainBaseConfig(index: Int, onNavigatePriority: (Int) -> Unit = {}) {
    var isMainBaseExpanded by rememberSaveable { mutableStateOf(true) }
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
        SettingCheckBox(key = "${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index")
        SettingInputRow(key = "${MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key}_c$index")
        SettingInputRow(key = "${MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key}_c$index")
        SettingInputRow(key = "${MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key}_c$index")
        FlowRow {
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key}_c$index",
                explain = "勾选后，辅助会跳过前两个搜到的目标，并且会根据所有搜索到的目标的可获得资源的平均值来搜鱼要求。"
            )
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key}_c$index")
        }
        FlowRow {
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.STOP_BATTLE_WHEN_NO_STAR.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.PLAY_LADDER.key}_c$index")
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LADDER.key}_c$index"]!!.value == "1") {
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.CHANGE_BASE.key}_c$index")
        }
        FlowRow {
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key}_c$index",
                explain = "勾选后，如果当前处于对战冷却时间，则会一直等待到冷却结束。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.RESTART_GAME.key}_c$index",
                explain = "勾选后，部署完所有部队后，辅助会重启游戏。仅对主世界对战有效。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index",
                explain = "勾选后，辅助将不会进入配兵页面，请手动配兵。\n\n注意：辅助不会自动开启超级兵。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.CHANGE_HEROES.key}_c$index",
                explain = "勾选后，辅助会随机更换英雄，宠物以及装备。可能影响到升级穿戴装备的功能，请谨慎勾选。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index"]!!.value == "0") {
            SettingDropdown(
                key = "${MAIN_BASE_SETTINGS.TACTICS_MODE.key}_c$index",
                options = listOf("单面一字划", "双面一字划", "四面长按")
            )
        }
        SettingCheckBox(
            key = "${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index",
            explain = "勾选后，需配合AI下兵插件才能正常使用。建议使用前仔细阅读官网教程。"
        )
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.AI_DEPLOY_TROOPS.key}_c$index"]!!.value == "1") {
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_SWEEPER.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.LIGHTING_ON_AIR_DEFENCE.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.LIGHTING_ON_WIZARD_TOWER.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.LIGHTING_ON_MORTAR.key}_c$index")
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
        FlowRow {
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key}_c$index")
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index"]!!.value == "1") {
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.DONATION_TIMES.key}_c$index")
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
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.SAVE_WORKER.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.BUILDING_CONVERSION_SETTINGS.key}_c$index")
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.UPGRADE_AFTER_FAIL_WALL_UPGRADE.key}_c$index",
                explain = "勾选后，若主世界无城墙可升级，则会将所有工人用于建造。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index"]!!.value == "1") {

            FlowRow {
                SettingCheckBox(key = "${MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key}_c$index")
                SettingInputRow(key = "${MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key}_c$index")
            }
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]!!.value == "1") {

            UpgradeConfigs(index, onNavigatePriority)
        }
        SettingCheckBox(key = "${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index")
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]!!.value == "1") {
            PetConfigs(index)
        }

        FlowRow {
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.HELPER_SETTINGS.key}_c$index",
                explain = "勾选后，会自动用实验助手以及建筑工人学徒。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index",
                explain = "升级实验助手的优先级高于购买建筑工人和升级工人学徒，请谨慎勾选！"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index",
                explain = "升级工人学徒的优先级高于购买建筑工人，请谨慎勾选！"
            )
        }
        FlowRow {
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index",
                explain = "勾选后，紫孔雀会接取小部分夜世界任务，例如夜世界摧毁率等。注意重点是\"小部分\"，也就是说并不是所有任务都可以接取，并且只会接取夜世界任务！若没有任务可接取，则会放弃第一个任务。接取任务后，会自动打夜世界。"
            )
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key}_c$index")
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key}_c$index",
                explain = "勾选后，部落战会进攻推荐对手。胜率较低，容易黑三，建议谨慎勾选。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.PLAY_LEAGUE.key}_c$index",
                explain = "勾选后，会进攻最后一位没有被部落成员进攻过的对手。胜率较低，容易黑三，建议谨慎勾选。"
            )
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.PLAY_RAID.key}_c$index")
        }
        FlowRow {
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.START_CLAN_WAR_SETTINGS.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.START_LEAGUE_SETTINGS.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.START_RAID.key}_c$index")
        }
        FlowRow {
            listOf(
                MAIN_BASE_SETTINGS.BUY_STAR_ORE_WITH_RAID_MEDAL,
                MAIN_BASE_SETTINGS.BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL,
                MAIN_BASE_SETTINGS.BUY_RING_OF_WALL_WITH_RAID_MEDAL,
                MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_RAID_MEDAL,
                MAIN_BASE_SETTINGS.BUY_TRAINING_POTION_WITH_RAID_MEDAL,
                MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL,
                MAIN_BASE_SETTINGS.BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL,
                MAIN_BASE_SETTINGS.BUY_STAR_ORE_WITH_EVENT_MEDAL,
                MAIN_BASE_SETTINGS.BUY_BUILDER_POTION_WITH_EVENT_MEDAL,
                MAIN_BASE_SETTINGS.BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL,
                MAIN_BASE_SETTINGS.BUY_RESEARCH_POTION_WITH_EVENT_MEDAL,
                MAIN_BASE_SETTINGS.USE_RESEARCH_POTION,
                MAIN_BASE_SETTINGS.SELL_TRAINING_POTION,
                MAIN_BASE_SETTINGS.USE_CLOCK_TOWER_POTION,
                MAIN_BASE_SETTINGS.SELL_CLOCK_TOWER_POTION,
                MAIN_BASE_SETTINGS.USE_BUILDER_POTION,
                MAIN_BASE_SETTINGS.SELL_RING_OF_WALL
            ).forEach { setting ->
                SettingCheckBox(key = "${setting.key}_c$index")
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 6.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )

        FlowRow {
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.UPGRADE_WEARABLE_GEAR.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index")
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index",
                explain = "勾选后，当主世界奖杯大于500时生效。有极小概率（约1%）移除稀有物品"
            )

            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.CLAIM_TIMED_REWARDS.key}_c$index")
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.CLAIM_TOKEN_REWARDS.key}_c$index",
                explain = "仅在资源全满后才会领取"
            )

            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.CLAIM_CAPITAL_GOLD.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.DONATE_CAPITAL_GOLD.key}_c$index")
            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.key}_c$index")

            SettingCheckBox(key = "${MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key}_c$index")
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index",
                explain = "勾选此选项后，紫孔雀不仅会自动加部落，也会自动建造部落城堡。但若不勾选此选项，就既不会加部落，也不会建造部落城堡。注意：加部落功能仅对未加入部落的账号生效。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.USE_TEMP_ITEMS.key}_c$index",
                explain = "勾选此选项后，紫孔雀会使用研究浓汤和建筑工人大餐。并且为了防止重复使用导致道具失效，每次只会使用一个道具。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index"]!!.value == "1") {
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_TAG.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key}_c$index")
        }
        FlowRow {
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index",
                explain = "勾选此选项后，当金币大于总容量80-85%后，紫孔雀将反复创建部落，直到部落标签出现大于或等于用户设定的连续数字或字母为止，或直到金币消耗完为止。\n\n注意：\n必须先建造部落城堡，才能勾选此项，否则会出现异常。\n部分设备使用此功能后，需要手动切换输入法。具体切换方法请参考官网教程。\n部分云手机不支持读取剪贴板，建议在电脑模拟器里使用本功能。"
            )
            SettingCheckBox(
                key = "${MAIN_BASE_SETTINGS.INVITE_PLAYERS.key}_c$index",
                explain = "勾选此选项后，紫孔雀将会从公告栏邀请玩家加入部落。\n\n注意：单次邀请耗时约1小时。\n请先加入部落后再开启本功能。\n请确保账号拥有邀请玩家的权限。"
            )
        }
        ExpandableContent(GlobalVars.configStates["${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index"]!!.value == "1") {
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_NAME.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.CONSECUTIVE_COUNT.key}_c$index")
        }
    }
}
