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
import com.coc.zkqcode.core.ui.components.SettingCheckBox
import com.coc.zkqcode.core.ui.components.SettingDropdown
import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.core.ui.components.SettingInputRow
import com.coc.zkqcode.core.data.database.Schema.ACCOUNT_SETTINGS

@Composable
fun AccountConfig(
    index: Int
) {
    Column(modifier = Modifier.padding(horizontal = 2.dp)) {
        Row {
            SettingCheckBox(key = "${ACCOUNT_SETTINGS.ISOPEN.key}${index}")
            SettingInputRow(key = "${ACCOUNT_SETTINGS.REMARK.key}${index}")
        }
        SettingDropdown(
            key = "${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}",
            options = listOf("国服", "国际服")
        )
        SettingInputRow(key = "${ACCOUNT_SETTINGS.ACCOUNT_CONFIG.key}${index}")
        SettingDropdown(
            key = "${ACCOUNT_SETTINGS.START_METHOD.key}${index}",
            options = listOf("游戏存档", "直接启动", "上号器")
        )
        when (GlobalVars.configStates["${ACCOUNT_SETTINGS.START_METHOD.key}${index}"]!!.value) {
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
    if (GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value == "0") {
        Column {
            Text(
                text = "换机或设备到期前，务必清空数据号信息！\n否则有被盗号风险！",
                modifier = Modifier.padding(end = 10.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Red
            )
            SettingInputRow(key = "${ACCOUNT_SETTINGS.DATA_CONTENT.key}${index}")
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
        val currentVersion = GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value
        // 使用 remember 来保存当前选中的选项
        var selectedOption by remember {
            mutableStateOf(
                GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value
            )
        }

        // 监听 currentVersion 的变化，并更新 selectedOption
        LaunchedEffect(currentVersion) {
            selectedOption = GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value
        }
        if (selectedOption == "0") {//0表示国服
            SettingInputRow(key = "${ACCOUNT_SETTINGS.CN_PATH.key}${index}")
        } else {
            SettingInputRow(key = "${ACCOUNT_SETTINGS.GLOBAL_PATH.key}${index}")
        }
    }
}