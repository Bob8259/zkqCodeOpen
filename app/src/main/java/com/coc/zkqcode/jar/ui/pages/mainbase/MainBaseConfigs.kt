package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.universal.smalltools.data.DeployPresetManager
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.BelowAnchorPositionProvider
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.InputRowWithCheckBox
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.components.SettingToggleButton
import com.coc.zkqcode.jar.ui.components.ClanGameRewardGroup
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanWarColors
import com.coc.zkqcode.jar.ui.schema.Schema
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
    isOtherExpanded: Boolean,
    onToggleOther: () -> Unit,
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
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_LEVEL.key}_c$index"]?.value = "0"
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
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.GEAR_UP_SETTINGS.key}_c$index"]?.value = "1"
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
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.AUTO_BATTLE_CN.key}_c$index",
                    explain = Strings.autoBattleCnExplain
                )
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
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DEPLOY_SIEGE_MACHINES_ENABLED.key}_c$index")
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
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.BATTLE_SPEEDUP.key}_c$index",
                    explain = Strings.battleSpeedupExplain
                )
            }
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.MANUAL_TRAINING.key}_c$index"]?.value == "1"
            ) {
                var presets by remember { mutableStateOf(DeployPresetManager.listPresets()) }
                var selectedRemark by remember { mutableStateOf("") }
                var remarkInput by remember { mutableStateOf("") }
                var showDropdown by remember { mutableStateOf(false) }

                Column {
                    // Preset bar: matching project dropdown style
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp, end = 6.dp)
                    ) {
                        Text(
                            text = "预设",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(end = 10.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                            Button(
                                onClick = { showDropdown = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                            ) {
                                Text(
                                    text = if (selectedRemark.isNotEmpty()) selectedRemark else "选择预设",
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            if (showDropdown) {
                                Popup(
                                    popupPositionProvider = BelowAnchorPositionProvider(),
                                    onDismissRequest = { showDropdown = false },
                                    properties = PopupProperties(focusable = true)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = AppColors.CardBg,
                                        shadowElevation = 8.dp,
                                        modifier = Modifier.padding(4.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp).heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            presets.forEach { preset ->
                                                val isSelected = preset.remark == selectedRemark
                                                Box(
                                                    modifier = Modifier
                                                        .background(if (isSelected) AppColors.Primary else AppColors.PrimaryLight.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                                                        .border(1.dp, if (isSelected) AppColors.Primary else AppColors.PrimaryLight, RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            selectedRemark = preset.remark
                                                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_REMARK.key}_c$index"]?.value = preset.remark
                                                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ARMY_CONFIG_MODE.key}_c$index"]?.value = preset.armyConfigMode
                                                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.ARMY_LINK.key}_c$index"]?.value = preset.armyLink
                                                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value = preset.deployMode
                                                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_DRAG_SPEED.key}_c$index"]?.value = preset.dragSpeed
                                                            GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_CLICK_DENSITY.key}_c$index"]?.value = preset.clickDensity
                                                            showDropdown = false
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        text = preset.remark,
                                                        color = if (isSelected) Color.White else AppColors.TextPrimary,
                                                        style = MaterialTheme.typography.labelMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        CustomButton(onClick = {
                            val remark = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_REMARK.key}_c$index"]?.value ?: ""
                            if (remark.isBlank()) return@CustomButton
                            val armMode = GlobalVars.configStates["${MAIN_BASE_SETTINGS.ARMY_CONFIG_MODE.key}_c$index"]?.value ?: "0"
                            val armLink = GlobalVars.configStates["${MAIN_BASE_SETTINGS.ARMY_LINK.key}_c$index"]?.value ?: ""
                            val depMode = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value ?: "0"
                            val depSpeed = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_DRAG_SPEED.key}_c$index"]?.value ?: "600"
                            val depDensity = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_CLICK_DENSITY.key}_c$index"]?.value ?: "50"
                            DeployPresetManager.savePreset(remark, armMode, armLink, depMode, depSpeed, depDensity)
                            selectedRemark = remark
                            presets = DeployPresetManager.listPresets()
                        }, text = "保存")
                        Spacer(modifier = Modifier.width(8.dp))
                        CustomButton(onClick = {
                            if (selectedRemark.isNotEmpty()) {
                                DeployPresetManager.deletePreset(selectedRemark)
                                selectedRemark = ""
                                GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_REMARK.key}_c$index"]?.value = ""
                                presets = DeployPresetManager.listPresets()
                            }
                        }, text = "删除", containerColor = Color(0xFFB71C1C))
                    }

                    // Remark — standard SettingInputRow
                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.DEPLOY_REMARK.key}_c$index")

                    // Army config
                    SettingDropdown(
                        key = "${MAIN_BASE_SETTINGS.ARMY_CONFIG_MODE.key}_c$index",
                        options = listOf(Strings.armyConfigModeLink, Strings.armyConfigModeManual)
                    )
                    AnimatedVisibility(
                        visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.ARMY_CONFIG_MODE.key}_c$index"]?.value == "0"
                    ) {
                        SettingInputRow(key = "${MAIN_BASE_SETTINGS.ARMY_LINK.key}_c$index")
                    }

                    // Deploy mode
                    SettingDropdown(
                        key = "${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index",
                        options = listOf(Strings.deployModeOriginal, Strings.deployModeSlider, Strings.deployModeFast)
                    )
                    AnimatedVisibility(
                        visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value != "0"
                    ) {
                        Text(
                            text = "四面下兵在国际服不生效",
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    AnimatedVisibility(
                        visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value != "0"
                    ) {
                        Column {
                            // Speed with +/-
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                                SettingInputRow(key = "${MAIN_BASE_SETTINGS.DEPLOY_DRAG_SPEED.key}_c$index", topPadding = 0.dp)
                                val speedK = "${MAIN_BASE_SETTINGS.DEPLOY_DRAG_SPEED.key}_c$index"
                                Button(onClick = {
                                    val v = (GlobalVars.configStates[speedK]?.value?.toIntOrNull() ?: 600) - 1
                                    if (v >= 1) GlobalVars.configStates[speedK]?.value = v.toString()
                                }, shape = RoundedCornerShape(8.dp), modifier = Modifier.width(25.dp).height(25.dp).padding(end = 1.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                                    Icon(Icons.Default.Remove, "-", modifier = Modifier.size(18.dp))
                                }
                                Button(onClick = {
                                    val v = (GlobalVars.configStates[speedK]?.value?.toIntOrNull() ?: 600) + 1
                                    GlobalVars.configStates[speedK]?.value = v.toString()
                                }, shape = RoundedCornerShape(8.dp), modifier = Modifier.width(25.dp).height(25.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                                    Icon(Icons.Default.Add, "+", modifier = Modifier.size(18.dp))
                                }
                            }

                            // Density with +/-
                            AnimatedVisibility(
                                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DEPLOY_MODE.key}_c$index"]?.value == "2"
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                                    SettingInputRow(key = "${MAIN_BASE_SETTINGS.DEPLOY_CLICK_DENSITY.key}_c$index", topPadding = 0.dp)
                                    val densityK = "${MAIN_BASE_SETTINGS.DEPLOY_CLICK_DENSITY.key}_c$index"
                                    Button(onClick = {
                                        val v = (GlobalVars.configStates[densityK]?.value?.toIntOrNull() ?: 50) - 1
                                        if (v >= 1) GlobalVars.configStates[densityK]?.value = v.toString()
                                    }, shape = RoundedCornerShape(8.dp), modifier = Modifier.width(25.dp).height(25.dp).padding(end = 1.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                                        Icon(Icons.Default.Remove, "-", modifier = Modifier.size(18.dp))
                                    }
                                    Button(onClick = {
                                        val v = (GlobalVars.configStates[densityK]?.value?.toIntOrNull() ?: 50) + 1
                                        GlobalVars.configStates[densityK]?.value = v.toString()
                                    }, shape = RoundedCornerShape(8.dp), modifier = Modifier.width(25.dp).height(25.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                                        Icon(Icons.Default.Add, "+", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.OCR_EXIT_ENABLED.key}_c$index"]?.value == "1"
            ) {
                FlowRow {
                    SettingInputRow(
                        key = "${MAIN_BASE_SETTINGS.OCR_EXIT_TARGET_PERCENT.key}_c$index",
                        suffix = Strings.percentTargetSuffix
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SettingDropdown(
                            key = "${MAIN_BASE_SETTINGS.OCR_EXIT_MODE.key}_c$index",
                            options = Strings.unchangedDetectionOptions,
                            labelOverride = ""
                        )
                        Text(
                            text = "不变",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 6.dp, end = 8.dp)
                        )
                        SettingInputRow(
                            key = "${MAIN_BASE_SETTINGS.OCR_EXIT_STALE_SECONDS.key}_c$index",
                            suffix = Strings.unchangedExitSuffix,
                            labelOverride = ""
                        )
                    }
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
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.COLLECT_CLAN_CASTLE.key}_c$index")
            }
            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DONATION_SETTING.key}_c$index")
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

             // 部落踢出成员
             FlowRow {
                 SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.KICK_CLAN_MEMBERS.key}_c$index")
             }
             AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.KICK_CLAN_MEMBERS.key}_c$index"]?.value == "1") {
                 Column {
                     SettingInputRow(
                         key = "${MAIN_BASE_SETTINGS.KICK_CLAN_MEMBERS_INTERVAL.key}_c$index",
                         suffix = "分钟"
                     )
                 }
             }

        }
        SettingSection(visible = isClanExpanded) {
            Text(
                text = Strings.sectionCapitalSettings,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.PLAY_RAID.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.START_RAID.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CONTRIBUTE_CAPITAL_COINS.key}_c$index", explain = "只支持捐到本部落，不支持偷币，不支持捐到其他部落。都城币需攒够一定数量后才会捐，币太少的话不会捐。")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.COLLECT_CAPITAL_COINS.key}_c$index")
            }
        }
        SettingSection(visible = isClanExpanded) {
            Text(
                text = Strings.sectionWarLeague,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 1.dp)
            )
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
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.START_CLAN_WAR.key}_c$index", explain = Strings.startClanWarExplain
                )
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE.key}_c$index", explain = Strings.startClanLeagueExplain
                )
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_CLAN_WAR.key}_c$index"]?.value == "1") {
                SettingDropdown(
                    key = "${MAIN_BASE_SETTINGS.START_CLAN_WAR_METHOD.key}_c$index", options = listOf(Strings.directStart, Strings.recentOnline)
                )
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE.key}_c$index"]?.value == "1") {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    // 对战人数（仅开联赛专用）：新对战人数开关开启 = 3 按钮 (11v11/15v15/30v30)，关闭 = 2 按钮 (15v15/30v30)
                    val leagueSizeKey = "${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE_SIZE.key}_c$index"
                    val leagueSizeState = GlobalVars.configStates.getOrPut(leagueSizeKey) {
                        mutableStateOf(Schema.getDefaultValue(leagueSizeKey))
                    }
                    val newSizeKey = "${MAIN_BASE_SETTINGS.START_CLAN_LEAGUE_NEW_SIZE.key}_c$index"
                    val newSizeEnabled = GlobalVars.configStates[newSizeKey]?.value == "1"
                    val leagueSizeOptions = if (newSizeEnabled) listOf("新人数", "15v15", "30v30") else listOf("15v15", "30v30")
                    val leagueSizeIndex = (leagueSizeState.value.toIntOrNull() ?: 0).coerceIn(0, leagueSizeOptions.lastIndex)
                    Column(modifier = Modifier.padding(top = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = Schema.getDisplayName(leagueSizeKey),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.labelMedium
                            )
                            SettingSwitchIcon(key = newSizeKey, textTopPadding = 0.dp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            leagueSizeOptions.forEachIndexed { idx, option ->
                                val isSelected = idx == leagueSizeIndex
                                val cardBg = if (isSelected) AppColors.Primary
                                else AppColors.PrimaryLight.copy(alpha = 0.10f)
                                val cardBorder = if (isSelected) AppColors.Primary
                                else AppColors.PrimaryLight
                                val textColor = if (isSelected) Color.White
                                else AppColors.TextPrimary
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(32.dp)
                                        .background(cardBg, RoundedCornerShape(8.dp))
                                        .border(1.dp, cardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            GlobalVars.isAutoRunEnabled = false
                                            leagueSizeState.value = idx.toString()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option, color = textColor, style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                    SettingDropdown(
                        key = "${MAIN_BASE_SETTINGS.EVENT_LEAGUE_START_DAY.key}_c$index",
                        options = listOf("禁用") + (11..22).map { "${it}日" }
                    )
                }
            }

            // League donation troop config
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.PLAY_LEAGUE_DONATION.key}_c$index"]?.value == "1"
            ) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    val leagueTroopOptions = MainBaseClanWarColors.leagueDonatableTroopNames
                    val troopKey1 = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_1.key}_c$index"
                    val troopKey2 = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_2.key}_c$index"
                    val troopKey3 = "${MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_3.key}_c$index"
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
        }
        SettingSection(visible = isClanExpanded) {
            Text(
                text = Strings.sectionClanGames,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 1.dp)
            )
            FlowRow {
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index")
            }
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.DO_CLAN_GAMES.key}_c$index"]?.value == "1") {
                Column {
                    Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppColors.CardBg,
                            modifier = Modifier.weight(1f).border(0.5.dp, Color(0xFF9DB4C2), RoundedCornerShape(6.dp))
                        ) {
                            Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                                Text(text = Strings.mainWorldLabel, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    SettingToggleButton(
                                        key = "${MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_NORMAL_TASK.key}_c$index",
                                        labelOverride = Strings.normalLabel,
                                        afterChange = { checked ->
                                            if (checked) {
                                                GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index"]?.value = "1"
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    SettingToggleButton(
                                        key = "${MAIN_BASE_SETTINGS.ACCEPT_MAIN_WORLD_ADVANCED_TASK.key}_c$index",
                                        labelOverride = Strings.advancedLabel,
                                        afterChange = { checked ->
                                            if (checked) {
                                                GlobalVars.configStates["${MAIN_BASE_SETTINGS.AUTO_ATTACK.key}_c$index"]?.value = "1"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AppColors.CardBg,
                            modifier = Modifier.weight(1f).border(0.5.dp, Color(0xFF9DB4C2), RoundedCornerShape(6.dp))
                        ) {
                            Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                                Text(text = Strings.builderBaseTasks, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    SettingToggleButton(
                                        key = "${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_NORMAL_TASK.key}_c$index",
                                        labelOverride = Strings.normalLabel,
                                        afterChange = { checked ->
                                            if (checked) {
                                                GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c$index"]?.value = "0"
                                                GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c$index"]?.value = "1"
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    SettingToggleButton(
                                        key = "${MAIN_BASE_SETTINGS.ACCEPT_BUILDER_BASE_ADVANCED_TASK.key}_c$index",
                                        labelOverride = Strings.advancedLabel,
                                        afterChange = { checked ->
                                            if (checked) {
                                                GlobalVars.configStates["${BUILDER_BASE_SETTINGS.NO_BUILDER_BASE.key}_c$index"]?.value = "0"
                                                GlobalVars.configStates["${BUILDER_BASE_SETTINGS.BUILDER_BASE_FARMING.key}_c$index"]?.value = "1"
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.STOP_CLAN_GAMES_AFTER_4000_POINTS.key}_c$index")
                    }
                }
            }

            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key}_c$index", explain = Strings.claimRewardsExplain)
            AnimatedVisibility(
                visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.CLAIM_CLAN_GAME_REWARDS.key}_c$index"]?.value == "1"
            ) {
                ClanGameRewardGroup(index)
            }
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
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index", afterChange = { checked ->
                    if (checked) {
                        GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value = "1"
                    }
                })
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.RESEARCH_SETTING.key}_c$index"]?.value == "1") {
                    Column {
                        CustomButton(
                            onClick = { onNavigateResearchPriority(index) },
                            text = Strings.adjustMainBaseResearchPriority,
                            containerColor = AppColors.Teal
                        )
                        SettingDropdown(
                            key = "${MAIN_BASE_SETTINGS.RESEARCH_LEVEL.key}_c${index}",
                            options = listOf(Strings.maxLevel, Strings.maxLevelMinus1, Strings.maxLevelMinus2),
                            afterChange = { selectedIdx ->
                                if (selectedIdx > 0) {
                                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value = "1"
                                }
                            }
                        )
                        InputRowWithCheckBox(
                            checkBoxKey = "${MAIN_BASE_SETTINGS.RESEARCH_AFTER_CONSECUTIVE_FAILURES.key}_c$index",
                            inputKey = "${MAIN_BASE_SETTINGS.RESEARCH_AFTER_CONSECUTIVE_FAILURES_THRESHOLD.key}_c$index"
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
                    SettingSwitchIcon(
                        key = "${MAIN_BASE_SETTINGS.REVERSE_UPGRADE.key}_c$index", explain = Strings.reverseUpgradeExplain
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

    // Banner — 战宠装备
    item {
        SectionBanner(
            title = Strings.petAndEquipment, isExpanded = isPetExpanded, onClick = onTogglePet
        )
    }

    // Pet upgrades
    item {
        SettingSection(visible = isPetExpanded) {

            SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index", afterChange = { checked ->
                if (checked) {
                    GlobalVars.configStates["${MAIN_BASE_SETTINGS.SMART_DECISION.key}_c$index"]?.value = "1"
                }
            })

            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_PETS.key}_c$index"]?.value == "1") {
                CustomButton(
                    onClick = { onNavigatePetPriority(index) },
                    text = Strings.adjustMainBasePetPriority,
                    containerColor = AppColors.Teal
                )
            }
            // Equipment upgrade master switch with equipped-equipments toggle in the same row
            FlowRow {
                SettingSwitchIcon(
                    key = "${MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS.key}_c$index",
                    explain = Strings.upgradeEquipmentsExplain
                )
                // Only visible when Upgrade Equipments is enabled
                AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS.key}_c$index"]?.value == "1") {
                    SettingSwitchIcon(
                        key = "${MAIN_BASE_SETTINGS.UPGRADE_EQUIPED_EQUIPMENTS.key}_c$index",
                        explain = Strings.upgradeEquipedEquipmentsExplain
                    )
                }
            }

            // Custom equipment selection list visible only when master is on
            AnimatedVisibility(visible = GlobalVars.configStates["${MAIN_BASE_SETTINGS.UPGRADE_EQUIPMENTS.key}_c$index"]?.value == "1") {
                MainBaseEquipmentConfigs(index)
            }
        }
    }

    // Banner — 其他
    item {
        SectionBanner(
            title = Strings.others, isExpanded = isOtherExpanded, onClick = onToggleOther
        )
    }

    // Helper settings
    item {
        SettingSection(visible = isOtherExpanded) {
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
        SettingSection(visible = isOtherExpanded) {
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
                    FlowRow {
                        SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL.key}_c$index")
                    }
                }
            }
            MainBaseShopConfigs(index)
        }
    }

    // Equipment & Rewards
    item {
        SettingSection(visible = isOtherExpanded) {
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
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_HERO_HALL_REWARDS.key}_c$index")
                SettingSwitchIcon(key = "${MAIN_BASE_SETTINGS.CLAIM_PASS_REWARDS.key}_c$index")
            }
        }
    }

}
