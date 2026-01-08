package com.coc.zkqcode.ui.pages

import android.os.Environment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.ui.components.CustomButton
import com.coc.zkqcode.ui.components.CustomCheckBox
import com.coc.zkqcode.ui.components.DropdownButton
import com.coc.zkqcode.ui.components.GlobalVars
import com.coc.zkqcode.ui.components.InputRow
import com.coc.zkqcode.ui.database.Schema
import com.coc.zkqcode.ui.database.SchemaExporter
import com.coc.zkqcode.ui.theme.AppColors

@Composable
fun HomeScreen(onSaveSuccess: () -> Unit = {}) {
    val configStates = remember { mutableMapOf<String, MutableState<String>>() }

    // Initialize states from Schema
    LaunchedEffect(GlobalVars.fileActions?.configJson) {
        Schema.GLOBAL_SETTINGS.forEach { def ->
            val savedValue = GlobalVars.fileActions?.getValue(def.key)
            if (savedValue != null) {
                configStates[def.key]?.value = savedValue
            } else if (!configStates.containsKey(def.key)) {
                configStates[def.key] = mutableStateOf(def.defaultValue.toString())
            }
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

    val configCount = configCountStr.toIntOrNull() ?: 1
    val tabs = listOf("主页") + List(configCount) { "配置${it + 1}" }

    Column(modifier = Modifier.fillMaxSize()) {
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
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTabIndex) {
                0 -> {
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
                    // Display account configurations based on account_count
                    val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
                    for (i in 1..currentAccountCount) {
                        AccountConfig(configStates = configStates, index = i)
                    }
                }

                else -> {
                    Text("这是配置 $selectedTabIndex 的内容")
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
                text = "保存所有 Schema",
                onClick = {
                    // 获取外部存储路径 zkqFiles
                    val baseDir = "${Environment.getExternalStorageDirectory().path}/zkqFiles/"

                    // 获取当前账户数量
                    val currentAccountCount = accountCountStr.toIntOrNull() ?: 3

                    // 调用通过服务器保存的方法
                    SchemaExporter.saveSchemaViaServer(
                        baseDir,
                        "global_config.json",
                        keys = listOf("GLOBAL_SETTINGS", "ACCOUNT_SETTINGS"),
                        accountCount = currentAccountCount
                    )
                    // 执行保存后的回调，用于关闭悬浮窗
                    onSaveSuccess()
                }
            )
        }
    }
}

@Composable
fun AccountConfig(
    configStates: Map<String, MutableState<String>>, index: Int
) {
    Column(modifier = Modifier.padding(horizontal = 2.dp)) {
        Row {
            CustomCheckBox(
                checkedState = configStates["prefix${index}"]?.value ?: "",
                onCheckStateChange = {
                    configStates["prefix${index}"]?.value = if (it) "1" else "0"
                },
                key = "prefix${index}",
                text = Schema.ACCOUNT_SETTINGS.first { it.key == "prefix" }.displayName + index,
            )
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "remark" }.displayName,
                value = configStates["remark${index}"]?.value ?: "",
                onValueChange = { configStates["remark${index}"]?.value = it },
                key = "remark${index}"
            )
        }
        DropdownButton(
            index = index,
            configStates = configStates,
            options = listOf("国服", "国际服"),
            key = "game_version",
            label = Schema.ACCOUNT_SETTINGS.first { it.key == "game_version" }.displayName,
            onValueChange = { configStates["game_version${index}"]?.value = it }
        )
        InputRow(
            label = Schema.ACCOUNT_SETTINGS.first { it.key == "account_config" }.displayName,
            value = configStates["account_config${index}"]?.value ?: "",
            onValueChange = { configStates["account_config${index}"]?.value = it },
            key = "account_config${index}"
        )
        DropdownButton(
            index = index,
            configStates = configStates,
            options = listOf("游戏存档", "直接启动", "上号器"),
            key = "start_method",
            label = Schema.ACCOUNT_SETTINGS.first { it.key == "start_method" }.displayName,
            onValueChange = { configStates["start_method${index}"]?.value = it }
        )
        when (configStates["start_method${index}"]?.value ?: "") {
            "游戏存档" -> GameFiles(index, configStates)//存档上号
            "上号器" -> UsePackage(index, configStates)//上号器
        }
    }
    HorizontalDivider(
        thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
    )
}

@Composable
fun UsePackage(
    index: Int, configStates: Map<String, MutableState<String>>
) {
    if (configStates["game_version${index}"]?.value == "国服") {
        Column {
            Text(
                text = "换机或设备到期前，务必清空数据号信息！\n否则有被盗号风险！",
                modifier = Modifier.padding(end = 10.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Red
            )
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "data_content" }.displayName,
                value = configStates["data_content${index}"]?.value ?: "",
                onValueChange = { configStates["data_content${index}"]?.value = it },
                key = "data_content${index}"
            )
        }
    } else {
        Text(
            text = "国际服不支持上号器，请更换启动游戏方式！",
            modifier = Modifier.padding(end = 10.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun GameFiles(
    index: Int, configStates: Map<String, MutableState<String>>
) {
    Column {
        Text(
            text = "存档文件路径",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.labelMedium
        )
        // 从 configStates 中获取当前游戏版本
        val currentVersion = configStates["game_version${index}"]?.value ?: "国服"
        // 使用 remember 来保存当前选中的选项
        var selectedOption by remember { mutableStateOf("国服") }

        // 监听 currentVersion 的变化，并更新 selectedOption
        LaunchedEffect(currentVersion) {
            selectedOption = configStates["game_version${index}"]?.value ?: "国服"
        }
        if (selectedOption == "国服") {//0表示国服
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "cn_path" }.displayName,
                value = configStates["cn_path${index}"]?.value ?: "",
                onValueChange = { configStates["cn_path${index}"]?.value = it },
                key = "cn_path${index}"
            )
        } else {
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "global_path" }.displayName,
                value = configStates["global_path${index}"]?.value ?: "",
                onValueChange = { configStates["global_path${index}"]?.value = it },
                key = "global_path${index}"
            )
        }
    }
}