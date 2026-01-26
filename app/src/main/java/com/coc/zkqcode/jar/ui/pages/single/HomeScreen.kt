package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.exit.AppExitHelper
import com.coc.zkqcode.core.ui.components.CustomAlertDialog
import com.coc.zkqcode.core.ui.components.CustomButton
import com.coc.zkqcode.core.ui.components.SettingCheckBox
import com.coc.zkqcode.core.ui.components.SettingDropdown
import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.core.ui.components.SettingInputRow
import com.coc.zkqcode.core.data.database.ConfigManager
import com.coc.zkqcode.core.data.database.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager
import com.coc.zkqcode.core.ui.theme.AppColors

@Composable
fun HomeScreen(
    onSaveSuccess: () -> Unit = {},
    onNavigatePriority: (Int) -> Unit = {},
    onNavigateNightPriority: (Int) -> Unit = {}
) {


    // Ensure all keys are initialized if not already (safeguard)
    val actions = GlobalVars.fileActions
    if (actions != null && GlobalVars.configStates.isEmpty()) {
        ConfigManager.initializeAllConfigs(actions)
    }

    // Safety check for configuration state
    val configCountState = GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]
    if (configCountState == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "正在初始化配置...", color = Color.Gray)
        }
        return
    }
    val configCountStr = configCountState.value



    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    
    // Hoisted expansion states keyed by tab index
    // Hoisted expansion states keyed by tab index
    val mainBaseExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }
    val nightBaseExpandedStates = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateMapOf<Int, Boolean>() }

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs =
        listOf("主页设置", "账号设置", "提取存档") + List(configCount) { "配置文件${it + 1}" }

    val saveAndRun = {

        ConfigManager.saveAndRun {
            onSaveSuccess()
        }
    }

    val cleanMemory = {
        // TODO: Implement logic to clean account memory
    }

    val cleanAllData = {
        // TODO: Implement logic to clean all data
    }

    // Exit Confirmation Dialog State
    var showExitConfirmation by rememberSaveable { mutableStateOf(false) }

    // Auto-Run Timer Logic
    LaunchedEffect(GlobalVars.isAutoRunEnabled, GlobalVars.autoRunTimer) {
        if (GlobalVars.isAutoRunEnabled && GlobalVars.autoRunTimer > 0) {
            kotlinx.coroutines.delay(1000L)
            GlobalVars.autoRunTimer--
            if (GlobalVars.autoRunTimer <= 0) {
                AppStateManager.setMode(AppMode.Run)
                saveAndRun()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. 顶部的 Tab 栏
        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            minTabWidth = 0.dp,
            containerColor = AppColors.Azure
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier
                        .wrapContentWidth() // Allows the tab to wrap its content
                        .widthIn(min = 0.dp), // BREAKS the default minimum width constraint
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.White.copy(alpha = 0.75f),
                    content = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp)
                        )
                    }
                )
            }
        }

        // 2. 中间的内容区域 (使用 weight 占据剩余空间)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Auto-Run UI in List
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (GlobalVars.isAutoRunEnabled) "${GlobalVars.autoRunTimer}秒后自动运行" else "计时已停止",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            CustomButton(
                                text = "修改任意配置停止计时",
                                onClick = { GlobalVars.isAutoRunEnabled = false }
                            )
                        }
                        LoginScreen()
                        CustomButton(text = "启动手动切号模式", onClick = {
                            AppStateManager.setMode(AppMode.SwitchAccount)
                            onSaveSuccess()
                        })
                        SettingInputRow(key = GLOBAL_SETTINGS.DELAY_MULTIPLIER.key)
                        SettingInputRow(key = GLOBAL_SETTINGS.ENTER_GAME_TIMER.key)
                        FlowRow {
                            SettingCheckBox(key = GLOBAL_SETTINGS.DEBUG_MODE.key)
                            SettingCheckBox(key = GLOBAL_SETTINGS.RECORD_PROGRESS.key)
                        }

                        SettingDropdown(
                            key = GLOBAL_SETTINGS.AUTO_UPDATE.key,
                            options = listOf("关闭", "仅更新稳定版", "更新测试版")
                        )
                        SettingCheckBox(key = GLOBAL_SETTINGS.AUTO_START.key)
                        SettingDropdown(
                            key = GLOBAL_SETTINGS.AFTER_KICK_OPTION.key,
                            options = listOf("立刻重连", "切换账号", "原地等待")
                        )
                        SettingInputRow(key = GLOBAL_SETTINGS.DEVICE_REMARK.key)
                        Row {
                            CustomButton(
                                text = "清除账号记忆",
                                onClick = { cleanMemory() },
                                explain = "本辅助会记住账号信息，例如记住当前账号是否已完成突袭，是否已完成部落竞赛等等。如果换号后不清空记忆，那么本辅助就会保留先前账号错误的记忆，进而可能发生某些异常操作。"
                            )
                            CustomButton(
                                text = "清空全部数据",
                                onClick = { cleanAllData() },
                                explain = "点击后将删除所有数据，包括辅助设置，保存的账号信息，数据号信息等等，用于保护用户隐私。"
                            )
                        }
                        Text(
                            text = "换机或设备到期前必须清空全部数据！部分云机在设备到期后不会清空用户数据，严重威胁隐私安全！",
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
                        )
                    }

                }

                1 -> {
                    // Display account configurations based on account_count
                    AccountSettings()
                }

                2 -> {
                    // Display account configurations based on account_count
                    ExtractGameSave()
                }

                else -> {
                    // Pass the 1-based index (selectedTabIndex) to MainBaseConfig
                    
                    // State hoisting for expansion
                    val currentMainExpanded = mainBaseExpandedStates[selectedTabIndex] ?: true
                    val currentNightExpanded = nightBaseExpandedStates[selectedTabIndex] ?: true

                    GameConfig(
                        index = selectedTabIndex - 2,
                        isMainExpanded = currentMainExpanded,
                        onToggleMainExpanded = { mainBaseExpandedStates[selectedTabIndex] = !currentMainExpanded },
                        isNightExpanded = currentNightExpanded,
                        onToggleNightExpanded = { nightBaseExpandedStates[selectedTabIndex] = !currentNightExpanded },
                        onNavigatePriority = onNavigatePriority,
                        onNavigateNightPriority = onNavigateNightPriority
                    )
                }
            }
        }

        // 3. 底部的固定按钮区
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            Row {
                CustomButton(
                    text = "保存并运行",
                    onClick = {
                        AppStateManager.setMode(AppMode.Run)
                        saveAndRun()
                    }
                )
                CustomButton(
                    text = "保存并退出",
                    onClick = {
                        showExitConfirmation = true
                    }
                )
            }

        }

        if (showExitConfirmation) {
            CustomAlertDialog(
                onDismissRequest = { showExitConfirmation = false },
                title = {
                    Text(
                        text = "退出提示",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        text = "确认要退出吗？",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    Row {
                        TextButton(onClick = { showExitConfirmation = false }) {
                            Text("取消")
                        }
                        TextButton(onClick = {
                            showExitConfirmation = false
                            AppStateManager.setMode(AppMode.Run)
                            ConfigManager.saveAndRun {
                                AppExitHelper.exitApplication(context)
                            }
                        }) {
                            Text("确认")
                        }
                    }
                }
            )
        }
    }

}
