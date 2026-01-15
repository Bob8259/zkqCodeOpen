package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.CustomCheckBox
import com.coc.zkqcode.utils.components.DropdownButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema

@Composable
fun AccountConfig(
    index: Int
) {
    Column(modifier = Modifier.padding(horizontal = 2.dp)) {
        Row {
            CustomCheckBox(
                checkedState = GlobalVars.configStates["isopen${index}"]?.value ?: "",
                onCheckStateChange = {
                    GlobalVars.configStates["isopen${index}"]?.value = if (it) "1" else "0"
                },
                text = Schema.ACCOUNT_SETTINGS.first { it.key == "isopen" }.displayName + index,
            )
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "remark" }.displayName,
                value = GlobalVars.configStates["remark${index}"]?.value ?: "",
                onValueChange = { GlobalVars.configStates["remark${index}"]?.value = it },
            )
        }
        DropdownButton(
            options = listOf("国服", "国际服"),
            selectedIndex = GlobalVars.configStates["game_version${index}"]?.value?.toIntOrNull() ?: 0,
            onValueChange = { GlobalVars.configStates["game_version${index}"]?.value = it.toString() },
            label = Schema.ACCOUNT_SETTINGS.first { it.key == "game_version" }.displayName
        )
        InputRow(
            label = Schema.ACCOUNT_SETTINGS.first { it.key == "account_config" }.displayName,
            value = GlobalVars.configStates["account_config${index}"]?.value ?: "",
            onValueChange = { GlobalVars.configStates["account_config${index}"]?.value = it },
        )
        DropdownButton(
            options = listOf("游戏存档", "直接启动", "上号器"),
            selectedIndex = GlobalVars.configStates["start_method${index}"]?.value?.toIntOrNull() ?: 0,
            onValueChange = { GlobalVars.configStates["start_method${index}"]?.value = it.toString() },
            label = Schema.ACCOUNT_SETTINGS.first { it.key == "start_method" }.displayName
        )
        when (GlobalVars.configStates["start_method${index}"]?.value ?: "") {
            "1" -> GameFiles(index)//存档上号
            "2" -> UsePackage(index)//上号器
        }
    }
    HorizontalDivider(
        thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
    )
}

@Composable
fun UsePackage(
    index: Int
) {
    if (GlobalVars.configStates["game_version${index}"]?.value == "0") {
        Column {
            Text(
                text = "换机或设备到期前，务必清空数据号信息！\n否则有被盗号风险！",
                modifier = Modifier.padding(end = 10.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Red
            )
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "data_content" }.displayName,
                value = GlobalVars.configStates["data_content${index}"]?.value ?: "",
                onValueChange = { GlobalVars.configStates["data_content${index}"]?.value = it },
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
    index: Int
) {
    Column {
        Text(
            text = "存档文件路径",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.labelMedium
        )
        // 从 configStates 中获取当前游戏版本
        val currentVersion = GlobalVars.configStates["game_version${index}"]?.value ?: "0"
        // 使用 remember 来保存当前选中的选项
        var selectedOption by remember {
            mutableStateOf(
                GlobalVars.configStates["game_version${index}"]?.value ?: "0"
            )
        }

        // 监听 currentVersion 的变化，并更新 selectedOption
        LaunchedEffect(currentVersion) {
            selectedOption = GlobalVars.configStates["game_version${index}"]?.value ?: "0"
        }
        if (selectedOption == "0") {//0表示国服
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "cn_path" }.displayName,
                value = GlobalVars.configStates["cn_path${index}"]?.value ?: "",
                onValueChange = { GlobalVars.configStates["cn_path${index}"]?.value = it },
            )
        } else {
            InputRow(
                label = Schema.ACCOUNT_SETTINGS.first { it.key == "global_path" }.displayName,
                value = GlobalVars.configStates["global_path${index}"]?.value ?: "",
                onValueChange = { GlobalVars.configStates["global_path${index}"]?.value = it },
            )
        }
    }
}