package com.coc.zkqcode.jar.ui.pages.homescreen

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.pages.single.BatchCreateAccount
import com.coc.zkqcode.jar.ui.pages.single.ConfigFileGrid
import com.coc.zkqcode.jar.ui.pages.single.ExtractGameSave
import com.coc.zkqcode.jar.ui.pages.single.LoginScreen
import com.coc.zkqcode.jar.ui.pages.single.RandomScheduling
import com.coc.zkqcode.jar.ui.pages.single.accountSettings
import com.coc.zkqcode.jar.ui.pages.single.gameConfig
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onSaveSuccess: () -> Unit = {}, onNavigatePriority: (Int) -> Unit = {}, onNavigateBuilderBasePriority: (Int) -> Unit = {}
) {

    // Ensure all keys are initialized if not already (safeguard)
    val actions = GlobalVars.serverActions
    if (actions != null && GlobalVars.configStates.isEmpty()) {
        ConfigManager.initializeAllConfigs(actions)
    }

    // Safety check for configuration state
    val configCountState = GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]
    if (configCountState == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "正在初始化配置...", color = Color.Gray)
        }
        return
    }
    val configCountStr = configCountState.value


    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedConfigIndex by rememberSaveable { mutableStateOf<Int?>(null) }

    // Reset config sub-navigation when switching away from the config tab
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex != 3) {
            selectedConfigIndex = null
        }
    }

    // Hoisted expansion states keyed by config index (1-based)
    val mainBaseExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val researchExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val buildExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val petExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val clanExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val builderBaseExpandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs = listOf("主页设置", "账号设置", "提取存档", "配置文件", "特殊设置")
    val sectionItems = listOf(
        "核心对战" to 0, "部落设置" to 4, "自动研究" to 6, "自动建造" to 8, "战宠设置" to 10, "夜世界" to 16
    )

    val lazyListState = rememberLazyListState()
    val chipListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val activeSectionIndex by remember {
        derivedStateOf {
            val firstVisible = lazyListState.firstVisibleItemIndex
            sectionItems.indexOfLast { (_, itemIndex) -> itemIndex <= firstVisible }.coerceAtLeast(0)
        }
    }

    LaunchedEffect(activeSectionIndex) {
        chipListState.animateScrollToItem(activeSectionIndex)
    }
    val scrollToBottom: () -> Unit = {
        scope.launch {
            // Small delay to let AnimatedVisibility content measure
            delay(100L)
            lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
        }
    }
    // Save configs and explicitly set the playing state before closing the config window
    val saveAndSetPlaying: (Boolean) -> Unit = { play ->
        scope.launch {
            GlobalVars.isPlaying.value = play
            ConfigManager.saveAndRun {
                onSaveSuccess()
            }
        }
    }

    // Floating notification state
    var showNotification by remember { mutableStateOf(false) }
    var notificationMessage by remember { mutableStateOf("") }

    // Show a floating notification and auto-dismiss after 1200ms
    val showMsg: (String) -> Unit = { msg ->
        notificationMessage = msg
        showNotification = true
        scope.launch {
            delay(1200)
            showNotification = false
        }
    }

    val cleanMemory = {
        scope.launch {
            val sdPath = Environment.getExternalStorageDirectory().path
            val memoryPath = "$sdPath/zkqFiles/memory.json"
            val success = FileHelper.deleteJson(memoryPath)
            showMsg(if (success) "记忆文件清除成功" else "清除失败，可能记忆文件不存在")
        }
    }

    // Clean All Data Confirmation Dialogue State
    var showCleanAllConfirmation by rememberSaveable { mutableStateOf(false) }
    var showAnnouncement by remember { mutableStateOf(false) }

    val cleanAllData = {
        showCleanAllConfirmation = true
    }

    // Auto-Run Timer Logic
    LaunchedEffect(GlobalVars.isAutoRunEnabled, GlobalVars.autoRunTimer) {
        if (GlobalVars.isAutoRunEnabled) {
            delay(1000L)
            GlobalVars.autoRunTimer--
            if (GlobalVars.autoRunTimer <= 0) {
                saveAndSetPlaying(true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Floating tab bar overlaying the content area
        Box(modifier = Modifier.weight(1f)) {
            // Layer 1: Scrollable content with top padding to avoid being hidden behind the tab bar
            LazyColumn(
                state = lazyListState, contentPadding = PaddingValues(top = if (selectedTabIndex == 3 && selectedConfigIndex != null) 40.dp else 52.dp), modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Auto-Run UI in List
                        item {
                            SettingSection {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (GlobalVars.isAutoRunEnabled) "${GlobalVars.autoRunTimer}秒后自动运行" else "计时已停止",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.Red,
                                        modifier = Modifier.padding(top = 8.dp, end = 6.dp)
                                    )
                                    CustomButton(
                                        text = "停止计时", onClick = { GlobalVars.isAutoRunEnabled = false })
                                    CustomButton(
                                        text = "公告", onClick = { showAnnouncement = true })
                                }
                                LoginScreen(
                                    onAdFreeClick = {
                                        saveAndSetPlaying(false)
                                    })
                            }
                            // Quick-action two-card row — mirrors the HTML .quick-buttons grid
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                QuickActionCard(
                                    title = "手动切号？", subtitle = "点击切号", dotColor = Color(0xFFED8936), onClick = {
                                        AppStateManager.setMode(AppMode.SwitchAccount)
                                        onSaveSuccess()
                                    }, modifier = Modifier.weight(1f)
                                )
                                QuickActionCard(
                                    title = "发现问题？", subtitle = "点击反馈", dotColor = AppColors.Teal, onClick = {
                                        AppStateManager.setMode(AppMode.BugReport)
                                        onSaveSuccess()
                                    }, modifier = Modifier.weight(1f)
                                )
                            }

                            SettingSection {
                                // Tips section header — orange dot + title, mirrors HTML .tips-card header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFFED8936), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "换机前务必清除数据 保护账号安全", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary
                                    )
                                }
                                // Two-tile grid — mirrors HTML .tips-grid / .tips-item
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    TipsActionTile(
                                        text = "清除账号记忆",
                                        explain = "辅助会缓存账号的运行状态，例如当前账号是否已完成突袭、部落竞赛等任务。切换账号后若未清除缓存，辅助将沿用先前账号的状态信息，可能导致异常操作。",
                                        onClick = { cleanMemory() },
                                        modifier = Modifier.weight(1f)
                                    )
                                    TipsActionTile(
                                        text = "清除全部数据", onClick = { cleanAllData() }, modifier = Modifier.weight(1f)
                                    )
                                }
                                // Centre mixed-height setting items within each FlowRow line so
                                // switch labels and dropdown labels stay aligned after wrapping.
                                FlowRow(
                                    itemVerticalAlignment = Alignment.CenterVertically
                                ) {
                                    SettingInputRow(key = GLOBAL_SETTINGS.DELAY_MULTIPLIER.key)
                                    SettingInputRow(key = GLOBAL_SETTINGS.ENTER_GAME_TIMER.key)
                                    SettingSwitchIcon(key = GLOBAL_SETTINGS.RECORD_PROGRESS.key)

                                    SettingDropdown(
                                        key = GLOBAL_SETTINGS.AUTO_UPDATE.key, options = listOf("关闭自动更新", "仅启动时更新", "实时自动更新")
                                    )
                                    SettingSwitchIcon(key = GLOBAL_SETTINGS.AUTO_START.key)
                                    SettingSwitchIcon(
                                        key = GLOBAL_SETTINGS.DISABLE_RESTART_GAME.key,
                                        explain = "勾选后，所有重启游戏的设定将失效，可能会导致卡界面。建议谨慎使用。"
                                    )
                                    SettingDropdown(
                                        key = GLOBAL_SETTINGS.AFTER_KICK_OPTION.key, options = listOf("立刻重连", "切换账号", "原地等待"),
                                        // Disable batch create when dropdown is not "立刻重连" (index 0)
                                        afterChange = { selectedIndex ->
                                            if (selectedIndex != 0) {
                                                GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
                                            }
                                        })
                                    SettingInputRow(key = GLOBAL_SETTINGS.DEVICE_REMARK.key)

                                }
                            }
                        }
                    }

                    1 -> {
                        // Display account configurations based on account_count
                        accountSettings()
                    }

                    2 -> {
                        // Display account configurations based on account_count
                        ExtractGameSave()
                    }

                    3 -> {
                        val cfgIdx = selectedConfigIndex
                        if (cfgIdx == null) {
                            // Show grid of config files
                            item {
                                ConfigFileGrid(
                                    configCount = configCount, onConfigSelected = { selectedConfigIndex = it })
                            }
                        } else {
                            // State hoisting for expansion keyed by config index
                            val currentMainExpanded = mainBaseExpandedStates[cfgIdx] ?: true
                            val currentClanExpanded = clanExpandedStates[cfgIdx] ?: true
                            val currentResearchExpanded = researchExpandedStates[cfgIdx] ?: true
                            val currentBuildExpanded = buildExpandedStates[cfgIdx] ?: true
                            val currentPetExpanded = petExpandedStates[cfgIdx] ?: true
                            val currentBuilderBaseExpanded = builderBaseExpandedStates[cfgIdx] ?: true

                            gameConfig(
                                index = cfgIdx,
                                isMainExpanded = currentMainExpanded,
                                onToggleMainExpanded = { mainBaseExpandedStates[cfgIdx] = !currentMainExpanded },
                                isClanExpanded = currentClanExpanded,
                                onToggleClan = { clanExpandedStates[cfgIdx] = !currentClanExpanded },
                                isResearchExpanded = currentResearchExpanded,
                                onToggleResearch = { researchExpandedStates[cfgIdx] = !currentResearchExpanded },
                                isBuildExpanded = currentBuildExpanded,
                                onToggleBuild = { buildExpandedStates[cfgIdx] = !currentBuildExpanded },
                                isPetExpanded = currentPetExpanded,
                                onTogglePet = { petExpandedStates[cfgIdx] = !currentPetExpanded },
                                isBuilderBaseExpanded = currentBuilderBaseExpanded,
                                onToggleBuilderBaseExpanded = {
                                    val newExpanded = !currentBuilderBaseExpanded
                                    builderBaseExpandedStates[cfgIdx] = newExpanded
                                    if (newExpanded) scrollToBottom()
                                },
                                onNavigatePriority = onNavigatePriority,
                                onNavigateBuilderBasePriority = onNavigateBuilderBasePriority,
                                onScrollToBottom = scrollToBottom
                            )
                        }
                    }

                    4 -> {
                        item {
                            SettingSection {
                                BatchCreateAccount()
                            }
                        }
                        item {
                            SettingSection {
                                RandomScheduling()
                            }
                        }
                    }
                }
            }

            // Layer 2: Floating tab bar + optional back button overlay
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                val isConfigDetail = selectedTabIndex == 3 && selectedConfigIndex != null
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .then(if (!isConfigDetail) Modifier.padding(top = 6.dp) else Modifier)
                        .border(1.dp, AppColors.Border, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.CardBg.copy(alpha = 0.9f),
                    shadowElevation = 0.dp
                ) {
                    if (isConfigDetail) {
                        // Back button + section chips when a config file is selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomButton(
                                text = "◀ 返回", onClick = { selectedConfigIndex = null }, marginTop = 0.dp
                            )
                            LazyRow(
                                state = chipListState,
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                sectionItems.forEachIndexed { chipIndex, (label, itemIndex) ->
                                    item {
                                        val isActive = chipIndex == activeSectionIndex
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(if (isActive) AppColors.Primary else AppColors.Primary.copy(alpha = 0.15f))
                                                .border(1.dp, AppColors.Primary, RoundedCornerShape(16.dp))
                                                .clickable { scope.launch { lazyListState.animateScrollToItem(itemIndex) } }
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isActive) Color.White else AppColors.Primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Glassmorphism tab bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            tabs.forEachIndexed { index, title ->
                                val isActive = selectedTabIndex == index
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isActive) AppColors.Primary else AppColors.NavInactive,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedTabIndex = index }
                                        .padding(horizontal = 6.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        } // End Box

        // Announcement dialog
        if (showAnnouncement) {
            AnnouncementDialog(onDismiss = { showAnnouncement = false })
        }

        // 3. Bottom fixed button area + dialogs + notification
        HomeScreenBottomBar(
            scope = scope,
            saveAndSetPlaying = saveAndSetPlaying,
            showNotification = showNotification,
            notificationMessage = notificationMessage,
            onDismissNotification = {},
            showCleanAllConfirmation = showCleanAllConfirmation,
            onDismissCleanAll = { showCleanAllConfirmation = false })
    }

}
