package com.coc.zkqcode.jar.ui.pages.single

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.code.Test
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseConfig
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.CustomCheckBox
import com.coc.zkqcode.utils.components.DropdownButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema
import com.coc.zkqcode.utils.database.SchemaExporter
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.theme.AppColors
import com.coc.zkqcode.utils.websocket.ServerConnection

@Composable
fun HomeScreen(onSaveSuccess: () -> Unit = {}) {
    // Initialize FileActions and store in GlobalVars if not already done
    LaunchedEffect(Unit) {
        if (GlobalVars.fileActions == null) {
            val serverConnection = ServerConnection("ws://localhost:6839/zkq")
            GlobalVars.fileActions = FileActions(serverConnection)
        }
    }

    val configStates = remember { mutableMapOf<String, MutableState<String>>() }
    var isLoading by remember { mutableStateOf(true) }

    // Initialize states from Schema
    LaunchedEffect(GlobalVars.fileActions?.configJson) {
        val actions = GlobalVars.fileActions
        if (actions != null && actions.configJson.size() > 0) {
            Schema.GLOBAL_SETTINGS.forEach { def ->
                val savedValue = actions.getValue(def.key)
                if (savedValue != null) {
                    configStates[def.key]?.value = savedValue
                } else if (!configStates.containsKey(def.key)) {
                    configStates[def.key] = mutableStateOf(def.defaultValue.toString())
                }
            }

            // Also initialize account settings if account_count is present
            val accountCount = actions.getValue("account_count")?.toIntOrNull() ?: 3
            for (i in 1..accountCount) {
                Schema.ACCOUNT_SETTINGS.forEach { def ->
                    val key = "${def.key}${i}"
                    val savedValue = actions.getValue(key)
                    if (savedValue != null) {
                        configStates[key]?.value = savedValue
                    } else if (!configStates.containsKey(key)) {
                        configStates[key] = mutableStateOf(def.defaultValue.toString())
                    }
                }
            }

            // Initialize MAIN_BASE_SETTINGS for each config
            val configCount = actions.getValue("config_count")?.toIntOrNull() ?: 3
            for (i in 1..configCount) {
                Schema.MAIN_BASE_SETTINGS.forEach { def ->
                    val key = "${def.key}_c$i"
                    val savedValue = actions.getValue(key)
                    if (savedValue != null) {
                        configStates[key]?.value = savedValue
                    } else if (!configStates.containsKey(key)) {
                        configStates[key] = mutableStateOf(def.defaultValue.toString())
                    }
                }
            }
            isLoading = false
        }
    }

    // Ensure all keys are initialized even if file hasn't loaded yet
    Schema.GLOBAL_SETTINGS.forEach { def ->
        if (!configStates.containsKey(def.key)) {
            val savedValue = GlobalVars.fileActions?.getValue(def.key)
            configStates[def.key] = mutableStateOf(savedValue ?: def.defaultValue.toString())
        }
    }

    val configCountStr = configStates["config_count"]?.value ?: "3"
    val accountCountStr = configStates["account_count"]?.value ?: "3"
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Initialize account settings states dynamically
    val accountCount = accountCountStr.toIntOrNull() ?: 3
    for (i in 1..accountCount) {
        Schema.ACCOUNT_SETTINGS.forEach { def ->
            val key = "${def.key}${i}"
            if (!configStates.containsKey(key)) {
                val savedValue = GlobalVars.fileActions?.getValue(key)
                configStates[key] = mutableStateOf(savedValue ?: def.defaultValue.toString())
            }
        }
    }

    // Initialize MAIN_BASE_SETTINGS dynamically if not loaded from file
    val currentConfigCount = configCountStr.toIntOrNull() ?: 3
    for (i in 1..currentConfigCount) {
        Schema.MAIN_BASE_SETTINGS.forEach { def ->
            val key = "${def.key}_c$i"
            if (!configStates.containsKey(key)) {
                val savedValue = GlobalVars.fileActions?.getValue(key)
                configStates[key] = mutableStateOf(savedValue ?: def.defaultValue.toString())
            }
        }
    }

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs = listOf("主页") + List(configCount) { "配置${it + 1}" }

    val saveAndRun = {
        // 获取外部存储路径 zkqFiles
        val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"

        // 获取当前账户数量
        val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
        // 调用通过服务器保存的方法
        SchemaExporter.saveSchemaViaServer(
            baseDir,
            "global_config.json",
            keys = listOf("GLOBAL_SETTINGS", "ACCOUNT_SETTINGS", "MAIN_BASE_SETTINGS"),
            accountCount = currentAccountCount,
            configCount = configStates["config_count"]?.value?.toIntOrNull() ?: 3,
            configStates = configStates
        )
        // 执行保存后的回调，用于关闭悬浮窗
        onSaveSuccess()
        Test().testcode()
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
                saveAndRun()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "加载配置文件中....",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 1. 顶部的 Tab 栏
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                modifier = Modifier.padding(top = 8.dp),
                containerColor = AppColors.Azure
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f)
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


                         LoginScreen(configStates)


                            InputRow(
                                label = Schema.GLOBAL_SETTINGS.first { it.key == "config_count" }.displayName,
                                value = configStates["config_count"]?.value ?: "",
                                onValueChange = { configStates["config_count"]?.value = it },
                                key = "config_count"
                            )

                            InputRow(
                                label = Schema.GLOBAL_SETTINGS.first { it.key == "account_count" }.displayName,
                                value = configStates["account_count"]?.value ?: "",
                                onValueChange = { configStates["account_count"]?.value = it },
                                key = "account_count"
                            )

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
                            )

                            InputRow(
                                label = "延时倍率（低性能设备调为1.2-2倍）",
                                value = configStates["delay_Multiplier"]?.value ?: "1",
                                onValueChange = { configStates["delay_Multiplier"]?.value = it },
                                key = "delay_Multiplier"
                            )

                            Row {
                                CustomCheckBox(
                                    checkedState = configStates["debug_mode"]?.value ?: "0",
                                    onCheckStateChange = { configStates["debug_mode"]?.value = if (it) "1" else "0" },
                                    key = "debug_mode",
                                    text = "慢速调试模式",
                                )
                                CustomCheckBox(
                                    checkedState = configStates["record_progress"]?.value ?: "0",
                                    onCheckStateChange = { configStates["record_progress"]?.value = if (it) "1" else "0" },
                                    key = "record_progress",
                                    text = "记录账号进度",
                                )
                            }

                            DropdownButton(
                                configStates,
                                filePath = "after_kick_option",
                                options = listOf("立刻重连", "切换账号", "原地等待"),
                                label = "被顶号后"
                            )

                            InputRow(
                                label = "当前设备备注",
                                value = configStates["device_remark"]?.value ?: "",
                                onValueChange = { configStates["device_remark"]?.value = it },
                                key = "device_remark"
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
                        // Display account configurations based on account_count
                        val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
                        items(count = currentAccountCount, key = { it + 1 }) { i ->
                            AccountConfig(configStates = configStates, index = i + 1)
                        }
                    }

                    else -> {
                        // Pass the 1-based index (selectedTabIndex) to MainBaseConfig
                        item {
                            MainBaseConfig(index = selectedTabIndex, configStates = configStates)
                        }
                    }
                }
            }

            // 3. 底部的固定按钮区
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                CustomButton(
                    text = "保存并运行",
                    onClick = { saveAndRun() }
                )
            }
        }
    }
}
