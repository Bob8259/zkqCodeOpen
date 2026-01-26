package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.components.SettingInputRow
import com.coc.zkqcode.utils.database.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.utils.database.Schema.GLOBAL_SETTINGS

fun LazyListScope.AccountSettings() {
    item {
        SettingInputRow(key = GLOBAL_SETTINGS.CONFIG_COUNT.key)
        SettingInputRow(key = GLOBAL_SETTINGS.ACCOUNT_COUNT.key)
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
        Row {
            CustomButton(
                text = "一键全选",
                marginTop = 6.dp,
                onClick = {
                    val count =
                        GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]!!.value.toIntOrNull() ?: 3
                    for (i in 1..count) {
                        val key = "${ACCOUNT_SETTINGS.ISOPEN.key}$i"
                        if (GlobalVars.configStates.containsKey(key)) {
                            GlobalVars.configStates[key]!!.value = "1"
                        } else {
                            GlobalVars.configStates[key] = mutableStateOf("1")
                        }
                    }
                }
            )
            CustomButton(
                text = "一键反选",
                marginTop = 6.dp,
                onClick = {
                    val count =
                        GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]!!.value.toIntOrNull() ?: 3
                    for (i in 1..count) {
                        val key = "${ACCOUNT_SETTINGS.ISOPEN.key}$i"
                        val currentValue = GlobalVars.configStates[key]!!.value
                        val newValue = if (currentValue == "1") "0" else "1"

                        if (GlobalVars.configStates.containsKey(key)) {
                            GlobalVars.configStates[key]!!.value = newValue
                        } else {
                            GlobalVars.configStates[key] = mutableStateOf(newValue)
                        }
                    }
                }
            )
        }
        var configValue by remember { mutableStateOf("") }
        var startAccount by remember { mutableStateOf("") }
        var endAccount by remember { mutableStateOf("") }
        FlowRow(

            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                text = "将配置",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 4.dp).align(Alignment.CenterVertically)
            )
            BasicTextField(
                value = configValue,
                onValueChange = {
                    GlobalVars.isAutoRunEnabled = false
                    configValue = it
                },
                modifier = Modifier
                    .width(60.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(4.dp).align(Alignment.CenterVertically)
            )
            Text(
                text = "应用到账号",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically)
            )
            BasicTextField(
                value = startAccount,
                onValueChange = {
                    GlobalVars.isAutoRunEnabled = false
                    startAccount = it
                },
                modifier = Modifier
                    .width(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(4.dp).align(Alignment.CenterVertically)
            )
            Text(
                text = "-",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically)
            )
            BasicTextField(
                value = endAccount,
                onValueChange = {
                    GlobalVars.isAutoRunEnabled = false
                    endAccount = it
                },
                modifier = Modifier
                    .width(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(4.dp).align(Alignment.CenterVertically)
            )
            CustomButton(
                text = "确认",
                onClick = {
                    val config = configValue
                    val start = startAccount.toIntOrNull()
                    val end = endAccount.toIntOrNull()
                    if (start != null && end != null && start <= end) {
                        for (i in start..end) {
                            val key = "${ACCOUNT_SETTINGS.ACCOUNT_CONFIG.key}$i"
                            if (GlobalVars.configStates.containsKey(key)) {
                                GlobalVars.configStates[key]!!.value = config
                            } else {
                                GlobalVars.configStates[key] = mutableStateOf(config)
                            }
                        }
                    }
                }
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
    }

    // Display account configurations based on account_count
    val accountCountStr = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]!!.value
    val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
    items(count = currentAccountCount, key = { it + 1 }) { i ->
        AccountConfig(index = i + 1)
    }
}
