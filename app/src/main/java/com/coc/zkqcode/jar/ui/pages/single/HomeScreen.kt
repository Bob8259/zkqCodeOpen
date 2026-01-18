package com.coc.zkqcode.jar.ui.pages.single

import android.os.Environment
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.code.ScreenShot
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.CustomCheckBox
import com.coc.zkqcode.utils.components.DropdownButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema
import com.coc.zkqcode.utils.database.SchemaExporter
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager
import com.coc.zkqcode.utils.theme.AppColors

@Composable
fun HomeScreen(onSaveSuccess: () -> Unit = {}) {


    // Ensure all keys are initialized even if file hasn't loaded yet
    Schema.GLOBAL_SETTINGS.forEach { def ->
        if (!GlobalVars.configStates.containsKey(def.key)) {
            val savedValue = GlobalVars.fileActions?.getValue(def.key)
            GlobalVars.configStates[def.key] =
                mutableStateOf(savedValue ?: def.defaultValue.toString())
        }
    }

    val configCountStr = GlobalVars.configStates["config_count"]?.value ?: "3"
    val accountCountStr = GlobalVars.configStates["account_count"]?.value ?: "3"
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Initialize account settings states dynamically
    val accountCount = accountCountStr.toIntOrNull() ?: 3
    for (i in 1..accountCount) {
        Schema.ACCOUNT_SETTINGS.forEach { def ->
            val key = "${def.key}${i}"
            if (!GlobalVars.configStates.containsKey(key)) {
                val savedValue = GlobalVars.fileActions?.getValue(key)
                val defaultValue = if (def.key == "global_path" || def.key == "data_content") {
                    i.toString()
                } else {
                    def.defaultValue.toString()
                }
                GlobalVars.configStates[key] = mutableStateOf(savedValue ?: defaultValue)
            }
        }
    }

    // Initialize MAIN_BASE_SETTINGS dynamically if not loaded from file
    val currentConfigCount = configCountStr.toIntOrNull() ?: 3
    for (i in 1..currentConfigCount) {
        // 将所有 Schema 列表合并为一个集合进行迭代
        val allSchemas = listOf(
            Schema.MAIN_BASE_SETTINGS,
            Schema.MAIN_BASE_TROOPS_AND_SPELLS,
            Schema.MAIN_BASE_BUILDINGS,
            Schema.MAIN_BASE_PETS,
            Schema.NIGHT_BASE_SETTINGS,
            Schema.NIGHT_BASE_TROOPS
        )

        allSchemas.forEach { schemaList ->
            schemaList.forEach { def ->
                val key = "${def.key}_c$i"

                // 使用 getOrPut 可以进一步简化“若不存在则存入”的逻辑
                GlobalVars.configStates.getOrPut(key) {
                    val savedValue = GlobalVars.fileActions?.getValue(key)
                    mutableStateOf(savedValue ?: def.defaultValue.toString())
                }
            }
        }
    }

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs = listOf("主页设置", "账号设置", "提取存档") + List(configCount) { "配置文件${it + 1}" }

    val saveAndRun = {
        // 获取外部存储路径 zkqFiles
        val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"

        // 获取当前账户数量
        val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
        // 调用通过服务器保存的方法
        SchemaExporter.saveSchemaViaServer(
            baseDir,
            "zkq_config.json",
            accountCount = currentAccountCount,
            configCount = GlobalVars.configStates["config_count"]?.value?.toIntOrNull() ?: 3
        )
        GlobalVars.updateWindowPosition = true
        // 执行保存后的回调，用于关闭悬浮窗
        onSaveSuccess()
        //run code
        ScreenShot().testcode()
    }

    val cleanMemory = {
        // TODO: Implement logic to clean account memory
    }

    val cleanAllData = {
        // TODO: Implement logic to clean all data
    }

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
                        InputRow(
                            label = Schema.GLOBAL_SETTINGS.first { it.key == "delay_multiplier" }.displayName,
                            value = GlobalVars.configStates["delay_multiplier"]?.value ?: "1",
                            onValueChange = {
                                GlobalVars.configStates["delay_multiplier"]?.value = it
                            },
                        )
                        FlowRow {
                            CustomCheckBox(
                                checkedState = GlobalVars.configStates["debug_mode"]?.value ?: "0",
                                onCheckStateChange = {
                                    GlobalVars.configStates["debug_mode"]?.value =
                                        if (it) "1" else "0"
                                },
                                text = Schema.GLOBAL_SETTINGS.first { it.key == "debug_mode" }.displayName,
                            )
                            CustomCheckBox(
                                checkedState = GlobalVars.configStates["record_progress"]?.value
                                    ?: "0",
                                onCheckStateChange = {
                                    GlobalVars.configStates["record_progress"]?.value =
                                        if (it) "1" else "0"
                                },
                                text = Schema.GLOBAL_SETTINGS.first { it.key == "record_progress" }.displayName,
                            )
                        }

                        DropdownButton(
                            options = listOf("关闭", "仅更新稳定版", "更新测试版"),
                            selectedIndex = GlobalVars.configStates["auto_update"]?.value?.toIntOrNull()
                                ?: 0,
                            label = Schema.GLOBAL_SETTINGS.first { it.key == "auto_update" }.displayName,
                            onValueChange = {
                                GlobalVars.configStates["auto_update"]?.value = it.toString()
                            }
                        )
                        CustomCheckBox(
                            checkedState = GlobalVars.configStates["auto_start"]?.value ?: "0",
                            onCheckStateChange = {
                                GlobalVars.configStates["auto_start"]?.value =
                                    if (it) "1" else "0"
                            },
                            text = Schema.GLOBAL_SETTINGS.first { it.key == "auto_start" }.displayName,
                        )
                        DropdownButton(
                            options = listOf("立刻重连", "切换账号", "原地等待"),
                            selectedIndex = GlobalVars.configStates["after_kick_option"]?.value?.toIntOrNull()
                                ?: 0,
                            label = Schema.GLOBAL_SETTINGS.first { it.key == "after_kick_option" }.displayName,
                            onValueChange = {
                                GlobalVars.configStates["after_kick_option"]?.value = it.toString()
                            }
                        )
                        InputRow(
                            label = Schema.GLOBAL_SETTINGS.first { it.key == "device_remark" }.displayName,
                            value = GlobalVars.configStates["device_remark"]?.value ?: "",
                            onValueChange = {
                                GlobalVars.configStates["device_remark"]?.value = it
                            },
                        )
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
                    item {
                        GameConfig(index = selectedTabIndex - 2)
                    }
                }
            }
        }

        // 3. 底部的固定按钮区
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            CustomButton(
                text = "保存并运行",
                onClick = {
                    AppStateManager.setMode(AppMode.Run)
                    saveAndRun()
                }
            )
        }
    }

}
