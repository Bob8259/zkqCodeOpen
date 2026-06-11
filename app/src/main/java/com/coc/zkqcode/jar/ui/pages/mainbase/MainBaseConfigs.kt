package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyListScope
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.InputRowWithCheckBox
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanWarColors
import com.coc.zkqcode.jar.ui.schema.Schema.BUILDER_BASE_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors

fun LazyListScope.mainBaseConfig(
    index: Int,
    isGeneralExpanded: Boolean,
    onToggleGeneralExpanded: () -> Unit,
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
    onNavigatePriority: (Int) -> Unit = {},
    onNavigateResearchPriority: (Int) -> Unit = {},
    onNavigatePetPriority: (Int) -> Unit = {}
) {
    // Banner — 通用设置
    item {
        SectionBanner(
            title = Strings.generalSettings, isExpanded = isGeneralExpanded, onClick = onToggleGeneralExpanded
        )
    }

    item {
        SettingSection(visible = isGeneralExpanded) {
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.CONFIG_REMARK.key}_c$index")
            SettingSwitchIcon(
                key = "${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index", explain = Strings.smartDecisionExplain, afterChange = { checked ->
                    if (!checked) {
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.SAVE_WORKER_BUILD_AFTER_WALL_FULL.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_SAVE_WORKER_BUILD_AFTER_WALL_FULL.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.SPEED_SIX_BUILDER.key}_c$index"]?.value = "0"
                    }
                })
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value == "1"
            ) {
                SettingInputRow(key = "${MAIN_BASE_SETTINGS.SKIP_TH_LEVEL.key}_c$index", suffix = Strings.levelAccountsSuffix)
            }
            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.SPEED_SIX_BUILDER.key}_c$index", afterChange = { checked ->
                if (checked) {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value = "1"
                }
            })
        }
    }

    // Banner — 对战设置
    item {
        SectionBanner(
            title = Strings.battleSettingsMain, isExpanded = isExpanded, onClick = onToggleExpanded
        )
    }

    // Attack & Resource requirements
    item {
        SettingSection(visible = isExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index",
                    afterChange = { checked ->
                        if (!checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_NORMAL_TASK.key}_c$index"]?.value = "0"
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_ADVANCED_TASK.key}_c$index"]?.value = "0"
                        }
                    },
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.PLAY_TOURNAMENT.key}_c$index", explain = Strings.tournamentExplain
                )
                AnimatedVisibility(
                    visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_TOURNAMENT.key}_c$index"]?.value == "1"
                ) {
                    SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CHANGE_BASE.key}_c$index")
                }
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.SWITCH_ACCOUNT_AFTER_BATTLE.key}_c$index")
            }
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.BATTLE_TIMES.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.BATTLE_TIMES_WITH_TASKS.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.GOLD_REQUIREMENT.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.ELIXIR_REQUIREMENT.key}_c$index")
            SettingInputRow(key = "${MAIN_BASE_SETTINGS.DARK_ELIXIR_REQUIREMENT.key}_c$index")
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DEPLOY_HEROES_ENABLED.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DEPLOY_SPELLS_ENABLED.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DEPLOY_REINFORCEMENTS_ENABLED.key}_c$index")
            }

        }
    }

    // Battle adjustments
    item {
        SettingSection(visible = isExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.DYNAMIC_ADJUSTMENT.key}_c$index", explain = Strings.dynamicAdjustExplain
                )
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.STOP_BATTLE_AFTER_FULL_RESOURCES.key}_c$index")
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.STOP_BATTLE_WHEN_NO_STAR.key}_c$index",
                    explain = Strings.stopBattleWhenNoStarExplain
                )
            }
        }
    }

    // Battle behaviour options
    item {
        SettingSection(visible = isExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE.key}_c$index", explain = Strings.waitForBattleExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.WAIT_FOR_BATTLE_AFTER_ACCEPT_MAIN_WORLD_TASK.key}_c$index", explain = Strings.waitForBattleAfterTaskExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.RESTART_GAME.key}_c$index", explain = Strings.restartGameExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index", explain = Strings.manualTrainingExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.EXIT_ON_ONE_STAR_MAIN.key}_c$index",
                    explain = Strings.exitOnOneStarExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.OCR_EXIT_ENABLED.key}_c$index",
                    explain = Strings.ocrExitExplain
                )
            }
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.OCR_EXIT_ENABLED.key}_c$index"]?.value == "1"
            ) {
                FlowRow {
                    SettingInputRow(
                        key = "${MAIN_BASE_SETTINGS.OCR_EXIT_TARGET_PERCENT.key}_c$index",
                        suffix = Strings.percentTargetSuffix
                    )
                    SettingInputRow(
                        key = "${MAIN_BASE_SETTINGS.OCR_EXIT_STALE_SECONDS.key}_c$index",
                        suffix = Strings.percentStaleSuffix
                    )
                }
            }
        }
    }

    // Banner — 部落设置
    item {
        SectionBanner(
            title = Strings.clanSettings, isExpanded = isClanExpanded, onClick = onToggleClan
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
                            key = "${MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.key}_c$index", suffix = Strings.secondsSuffix
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.key}_c$index", suffix = Strings.percentStartBattle
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.key}_c$index", suffix = Strings.percentStop
                        )
                    }
                    FlowRow {
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.APPROVE_MEMBER_SETTING.key}_c$index",
                            explain = Strings.approveCodeExplain
                        )
                    }
                    AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.APPROVE_MEMBER_SETTING.key}_c$index"]?.value == "1") {
                        SettingInputRow(key = "${MAIN_BASE_SETTINGS.APPROVE_MEMBER_CODE.key}_c$index")
                    }
                }
            }
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index")
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index"]?.value == "1") {
                Column {
                    FlowRow {
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_NORMAL_TASK.key}_c$index", afterChange = { checked ->
                                if (checked) {
                                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index"]?.value = "1"
                                }
                            })
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_ADVANCED_TASK.key}_c$index", afterChange = { checked ->
                                if (checked) {
                                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index"]?.value = "1"
                                }
                            })
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_NORMAL_TASK.key}_c$index", afterChange = { checked ->
                                if (checked) {
                                    GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c$index"]?.value = "0"
                                    GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c$index"]?.value = "1"
                                }
                            })
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_ADVANCED_TASK.key}_c$index", afterChange = { checked ->
                                if (checked) {
                                    GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c$index"]?.value = "0"
                                    GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c$index"]?.value = "1"
                                }
                            })
                        SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.STOP_CLAN_GAMES_AFTER_4000_POINTS.key}_c$index")
                    }
                }
            }

            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key}_c$index", explain = Strings.claimRewardsExplain)
            // Badge event reward parent switch.
            SettingSwitchIcon(
                key = "${MAIN_BASE_SETTINGS.CLAIM_BADGE_EVENT_REWARDS.key}_c$index",
                explain = Strings.claimBadgeEventRewardsExplain
            )
            // Event Shop reward child switch, visible only when badge event rewards are enabled.
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_BADGE_EVENT_REWARDS.key}_c$index"]?.value == "1"
            ) {
                Column {
                    SettingSwitchIcon(
                        key = "${MAIN_BASE_SETTINGS.CLAIM_EVENT_SHOP_REWARDS.key}_c$index",
                        explain = Strings.claimEventShopRewardsExplain
                    )
                    AnimatedVisibility(
                        visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_EVENT_SHOP_REWARDS.key}_c$index"]?.value == "1"
                    ) {
                        FlowRow {
                            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL.key}_c$index")
                        }
                    }
                }
            }
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key}_c$index", explain = Strings.clanWarExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.PLAY_LEAGUE.key}_c$index", explain = Strings.leagueExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.PLAY_LEAGUE_DONATION.key}_c$index"
                )
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.PLAY_RAID.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CONTRIBUTE_CAPITAL_COINS.key}_c$index", explain = "只支持捐到本部落，不支持偷币，不支持捐到其他部落。都城币需攒够一定数量后才会捐，币太少的话不会捐。")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.COLLECT_CAPITAL_COINS.key}_c$index")
            }

            // League donation troop config (3 groups: dropdown + count)
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LEAGUE_DONATION.key}_c$index"]?.value == "1"
            ) {
                Column {
                    val leagueTroopOptions = MainBaseClanWarColors.leagueDonatableTroopNames
                    val troopKey1 = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_1.key}_c$index"
                    val troopKey2 = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_2.key}_c$index"
                    val troopKey3 = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_3.key}_c$index"
                    // Group 1
                    FlowRow {
                        SettingDropdown(
                            key = troopKey1,
                            options = leagueTroopOptions,
                            afterChange = { selectedIdx ->
                                if (selectedIdx != 0) {
                                    val idx2 = GlobalVars.configStates[troopKey2]?.value?.toIntOrNull() ?: 0
                                    val idx3 = GlobalVars.configStates[troopKey3]?.value?.toIntOrNull() ?: 0
                                    if (idx2 == selectedIdx) GlobalVars.configStates[troopKey2]?.value = "0"
                                    if (idx3 == selectedIdx) GlobalVars.configStates[troopKey3]?.value = "0"
                                }
                            }
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_COUNT_1.key}_c$index",
                            suffix = Strings.countSuffix
                        )
                    }
                    // Group 2
                    FlowRow {
                        SettingDropdown(
                            key = troopKey2,
                            options = leagueTroopOptions,
                            afterChange = { selectedIdx ->
                                if (selectedIdx != 0) {
                                    val idx1 = GlobalVars.configStates[troopKey1]?.value?.toIntOrNull() ?: 0
                                    val idx3 = GlobalVars.configStates[troopKey3]?.value?.toIntOrNull() ?: 0
                                    if (idx1 == selectedIdx) GlobalVars.configStates[troopKey1]?.value = "0"
                                    if (idx3 == selectedIdx) GlobalVars.configStates[troopKey3]?.value = "0"
                                }
                            }
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_COUNT_2.key}_c$index",
                            suffix = Strings.countSuffix
                        )
                    }
                    // Group 3
                    FlowRow {
                        SettingDropdown(
                            key = troopKey3,
                            options = leagueTroopOptions,
                            afterChange = { selectedIdx ->
                                if (selectedIdx != 0) {
                                    val idx1 = GlobalVars.configStates[troopKey1]?.value?.toIntOrNull() ?: 0
                                    val idx2 = GlobalVars.configStates[troopKey2]?.value?.toIntOrNull() ?: 0
                                    if (idx1 == selectedIdx) GlobalVars.configStates[troopKey1]?.value = "0"
                                    if (idx2 == selectedIdx) GlobalVars.configStates[troopKey2]?.value = "0"
                                }
                            }
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_COUNT_3.key}_c$index",
                            suffix = Strings.countSuffix
                        )
                    }
                }
            }

            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.START_CLAN_WAR.key}_c$index", explain = Strings.startClanWarExplain
                )
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.OPEN_RAID.key}_c$index")
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_CLAN_WAR.key}_c$index"]?.value == "1") {
                SettingDropdown(
                    key = "${MAIN_BASE_SETTINGS.START_CLAN_WAR_METHOD.key}_c$index", options = listOf(Strings.directStart, Strings.recentOnline)
                )
            }

            // League registration settings
            SettingSwitchIcon(
                key = "${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE.key}_c$index", explain = Strings.startClanLeagueExplain
            )
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE.key}_c$index"]?.value == "1") {
                SettingDropdown(
                    key = "${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE_SIZE.key}_c$index", options = listOf("15v15", "30v30")
                )
            }

            SettingSwitchIcon(
                key = "${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index", explain = Strings.autoJoinClanExplain
            )
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key}_c$index"]?.value == "1") {
                Column {
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_TAG.key}_c$index")
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key}_c$index")
                }
            }

            // 部落拉人 / 邀请玩家
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.INVITE_PLAYERS.key}_c$index")
                // Commented out: create_consecutive_clans has no functional implementation
                // SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index")
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.INVITE_PLAYERS.key}_c$index"]?.value == "1") {
                Column {
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUNDS.key}_c$index")
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.INVITE_PLAYERS_ROUND_DURATION.key}_c$index")
                }
            }
            // Commented out: create_consecutive_clans has no functional implementation
            // AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CREATE_CONSECUTIVE_CLANS.key}_c$index"]?.value == "1") {
            //     Column {
            //         SettingInputRow(key = "${MAIN_BASE_SETTINGS.CLAN_NAME.key}_c$index")
            //         SettingInputRow(key = "${MAIN_BASE_SETTINGS.CONSECUTIVE_COUNT.key}_c$index")
            //     }
            // }

        }
    }

    // Banner — 研究设置
    item {
        SectionBanner(
            title = Strings.researchSettings, isExpanded = isResearchExpanded, onClick = onToggleResearch
        )
    }

    item {
        SettingSection(visible = isResearchExpanded) {
            Column {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index")
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index"]?.value == "1") {
                    Column {
                        CustomButton(
                            onClick = { onNavigateResearchPriority(index) },
                            text = Strings.adjustMainBaseResearchPriority,
                            containerColor = AppColors.Teal
                        )
                        SettingDropdown(
                            key = "${MAIN_BASE_SETTINGS.RESEARCH_LEVEL.key}_c${index}",
                            options = listOf(Strings.maxLevel, Strings.maxLevelMinus1, Strings.maxLevelMinus2)
                        )
                    }
                }
            }
        }
    }

    // Banner — 建造设置（主世界）
    item {
        SectionBanner(
            title = Strings.buildSettingsMain, isExpanded = isBuildExpanded, onClick = onToggleBuild
        )
    }

    item {
        SettingSection(visible = isBuildExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index", explain = Strings.buildSettingExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.GEAR_UP_SETTINGS.key}_c$index", explain = Strings.gearUpExplain
                )
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.SAVE_WORKER.key}_c$index")
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.SAVE_WORKER_BUILD_AFTER_WALL_FULL.key}_c$index", explain = Strings.saveWorkerBuildExplain, afterChange = { checked ->
                        if (checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value = "1"
                        }
                    })
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.WALL_UPGRADE_SETTINGS.key}_c$index"]?.value == "1") {
                Column {
                    SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.BATCH_WALL_UPGRADE_SETTINGS.key}_c$index", explain = Strings.batchWallExplain)
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.UPGRADE_WALL_THRESHOLD.key}_c$index")
                }
            }

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.BUILD_SETTING.key}_c$index"]?.value == "1") {
                Column {
                    CustomButton(
                        onClick = { onNavigatePriority(index) }, text = Strings.adjustMainBasePriority, containerColor = AppColors.Teal
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
            title = Strings.petAndOther, isExpanded = isPetExpanded, onClick = onTogglePet
        )
    }

    // Pet upgrades
    item {
        SettingSection(visible = isPetExpanded) {

            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index")

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]?.value == "1") {
                CustomButton(
                    onClick = { onNavigatePetPriority(index) },
                    text = Strings.adjustMainBasePetPriority,
                    containerColor = AppColors.Teal
                )
            }
            // Gear upgrade master switch
            SettingSwitchIcon(
                key = "${MAIN_BASE_SETTINGS.UPGRADE_GEAR_MASTER.key}_c$index",
                afterChange = { checked ->
                    if (!checked) {
                        // When master is off, turn off all sub-switches
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index"]?.value = "0"
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_CUSTOM_GEAR.key}_c$index"]?.value = "0"
                    }
                }
            )

            // Sub-switches visible only when master is on
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_GEAR_MASTER.key}_c$index"]?.value == "1") {
                Column {
                    FlowRow {
                        // Gear upgrade: upgrade equipped gear (independent)
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key}_c$index",
                            explain = Strings.upgradeWearGearExplain
                        )

                        // Gear upgrade: upgrade all gear (old mode, mutually exclusive with custom)
                        SettingSwitchIcon(
                            key = "${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index",
                            explain = Strings.upgradeAllGearExplain,
                            afterChange = { checked ->
                                if (checked) {
                                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_CUSTOM_GEAR.key}_c$index"]?.value = "0"
                                }
                            }
                        )
                    }

                    // Gear upgrade: custom gear selection (new mode, mutually exclusive with all)
                    SettingSwitchIcon(
                        key = "${MAIN_BASE_SETTINGS.UPGRADE_CUSTOM_GEAR.key}_c$index",
                        explain = Strings.upgradeGearExplain,
                        afterChange = { checked ->
                            if (checked) {
                                GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key}_c$index"]?.value = "0"
                            }
                        }
                    )

                    // Custom gear selection list visible when UPGRADE_CUSTOM_GEAR is on
                    AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_CUSTOM_GEAR.key}_c$index"]?.value == "1") {
                        MainBaseGearConfigs(index)
                    }
                }
            }
        }
    }

    // Helper settings
    item {
        SettingSection(visible = isPetExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.HELPER_SETTINGS.key}_c$index", explain = Strings.helperExplain, afterChange = { checked ->
                        if (!checked) {
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index"]?.value = "0"
                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index"]?.value = "0"
                        }
                    })
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_RESEARCH_HELPER.key}_c$index", explain = Strings.researchHelperExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_BUILDER_APPRENTICE.key}_c$index", explain = Strings.builderApprenticeExplain
                )
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.PROSPECTOR_BLUE_TO_GOLD.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.PROSPECTOR_BLUE_TO_PURPLE.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.PROSPECTOR_PURPLE_TO_GOLD.key}_c$index")
            }
        }
    }


    // Shop purchases
    item {
        SettingSection(visible = isPetExpanded) {
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
            MainBaseShopConfigs(index)
        }
    }

    // Gear & Rewards
    item {
        SettingSection(visible = isPetExpanded) {
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index", explain = Strings.removeObstaclesExplain
                )
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key}_c$index"]?.value == "1") {
                    Column {
                        FlowRow {
                            SettingSwitchIcon(
                                key = "${MAIN_BASE_SETTINGS.CONTINUOUS_REMOVE_OBSTACLES.key}_c$index", explain = Strings.continuousRemoveExplain
                            )
                            SettingSwitchIcon(
                                key = "${MAIN_BASE_SETTINGS.REMOVE_OBSTACLES_ENHANCEMENT.key}_c$index", explain = Strings.removeObstaclesEnhanceExplain
                            )
                        }
                    }
                }
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_DAILY_REWARD.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_ACHIEVEMENT_GEMS.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_FREE_SHOP_REWARDS.key}_c$index")
            }
        }
    }

}
