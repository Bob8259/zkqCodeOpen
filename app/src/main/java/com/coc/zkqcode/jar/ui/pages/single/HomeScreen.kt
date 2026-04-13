package com.coc.zkqcode.jar.ui.pages.single

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
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
    val mainBaseExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }
    val researchExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }
    val buildExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }
    val petExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }
    val builderBaseExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs = listOf("主页设置", "账号设置", "提取存档", "配置文件", "特殊设置")

    val sectionItems = listOf(
        "⚔️ 核心对战" to 0,
        "🔬 自动研究" to 5,
        "🏗️ 自动建造" to 7,
        "🐾 战宠设置" to 9,
        "🌙 夜世界" to 15
    )

    val lazyListState = rememberLazyListState()
    val chipListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val activeSectionIndex by remember {
        derivedStateOf {
            val firstVisible = lazyListState.firstVisibleItemIndex
            sectionItems.indexOfLast { (_, itemIndex) -> itemIndex <= firstVisible }
                .coerceAtLeast(0)
        }
    }

    LaunchedEffect(activeSectionIndex) {
        chipListState.animateScrollToItem(activeSectionIndex)
    }
    val scrollToBottom: () -> Unit = {
        scope.launch {
            // Small delay to let AnimatedVisibility content measure
            delay(300L)
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
        // 1. Top Tab bar
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex, edgePadding = 0.dp, minTabWidth = 0.dp, containerColor = AppColors.Primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, modifier = Modifier
                        .wrapContentWidth() // Allows the tab to wrap its content
                        .widthIn(min = 0.dp), // BREAKS the default minimum width constraint
                    selectedContentColor = Color.White, unselectedContentColor = Color.White.copy(alpha = 0.75f), content = {
                        Text(
                            text = title, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
                        )
                    })
            }
        }

        // Back button + section outline pinned above the list when a config file is selected
        if (selectedTabIndex == 3 && selectedConfigIndex != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button — fixed, does not scroll
                CustomButton(
                    text = "◀ 返回",
                    onClick = { selectedConfigIndex = null },
                    marginTop = 0.dp
                )
                // Scrollable section outline chips — synced with lazyListState scroll position
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
                                    .background(
                                        if (isActive) AppColors.Primary
                                        else AppColors.Primary.copy(alpha = 0.12f)
                                    )
                                    .border(1.dp, AppColors.Primary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .clickable {
                                        scope.launch {
                                            lazyListState.animateScrollToItem(itemIndex)
                                        }
                                    }
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
        }

        // 2. Middle content area (use weight to occupy remaining space)
        LazyColumn(
            state = lazyListState, modifier = Modifier
                .weight(1f)
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
                                    text = "修改任意配置停止计时", onClick = { GlobalVars.isAutoRunEnabled = false })
                            }
                            LoginScreen(
                                onAdFreeClick = {
                                    saveAndSetPlaying(false)
                                })
                        }
                        SettingSection {
                            FlowRow {
                                CustomButton(text = "启动手动切号模式", onClick = {
                                    AppStateManager.setMode(AppMode.SwitchAccount)
                                    onSaveSuccess()
                                })
                                CustomButton(text = "截屏反馈问题", onClick = {
                                    AppStateManager.setMode(AppMode.BugReport)
                                    onSaveSuccess()
                                })
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
                                    key = GLOBAL_SETTINGS.AUTO_UPDATE.key,
                                    options = listOf("关闭自动更新", "仅启动时更新", "实时自动更新")
                                )
                                SettingSwitchIcon(key = GLOBAL_SETTINGS.AUTO_START.key)
                                SettingDropdown(
                                    key = GLOBAL_SETTINGS.AFTER_KICK_OPTION.key,
                                    options = listOf("立刻重连", "切换账号", "原地等待"),
                                    // Disable batch create when dropdown is not "立刻重连" (index 0)
                                    afterChange = { selectedIndex ->
                                        if (selectedIndex != 0) {
                                            GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
                                        }
                                    }
                                )
                                SettingInputRow(key = GLOBAL_SETTINGS.DEVICE_REMARK.key)

                            }
                            FlowRow {
                                CustomButton(
                                    text = "清除账号记忆",
                                    onClick = { cleanMemory() },
                                    explain = "辅助会记住账号信息，例如记住当前账号是否已完成突袭，是否已完成部落竞赛等等。如果换号后不清空记忆，那么辅助就会保留先前账号错误的记忆，进而可能发生某些异常操作。"
                                )
                                CustomButton(
                                    text = "清除全部数据", onClick = { cleanAllData() }
                                )
                            }
                            Text(
                                text = "换机或设备到期前必须清空全部数据！部分云机在设备到期后不会清空用户数据，严重威胁隐私安全！",
                                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                            )
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
                                configCount = configCount,
                                onConfigSelected = { selectedConfigIndex = it }
                            )
                        }
                    } else {
                        // State hoisting for expansion keyed by config index
                        val currentMainExpanded = mainBaseExpandedStates[cfgIdx] ?: true
                        val currentResearchExpanded = researchExpandedStates[cfgIdx] ?: true
                        val currentBuildExpanded = buildExpandedStates[cfgIdx] ?: true
                        val currentPetExpanded = petExpandedStates[cfgIdx] ?: true
                        val currentBuilderBaseExpanded = builderBaseExpandedStates[cfgIdx] ?: true

                        gameConfig(
                            index = cfgIdx,
                            isMainExpanded = currentMainExpanded,
                            onToggleMainExpanded = { mainBaseExpandedStates[cfgIdx] = !currentMainExpanded },
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
                }
            }
        }

        // 3. Bottom fixed button area + dialogs + notification
        HomeScreenBottomBar(
            scope = scope,
            saveAndSetPlaying = saveAndSetPlaying,
            showNotification = showNotification,
            notificationMessage = notificationMessage,
            onDismissNotification = {},
            showCleanAllConfirmation = showCleanAllConfirmation,
            onDismissCleanAll = { showCleanAllConfirmation = false }
        )
    }

}
