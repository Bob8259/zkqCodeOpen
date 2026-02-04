package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.schema.ConfigManager

private fun updateConfigValue(key: String, value: String) {
    GlobalVars.configStates[key]?.let {
        it.value = value
    } ?: run {
        GlobalVars.configStates[key] = mutableStateOf(value)
    }
}

private fun batchUpdateByRange(startStr: String, endStr: String, keyPrefix: String, newValue: String) {
    val start = startStr.toIntOrNull()
    val end = endStr.toIntOrNull()
    if (start != null && end != null && start <= end) {
        for (i in start..end) {
            updateConfigValue("$keyPrefix$i", newValue)
        }
    }
}

private fun batchUpdateAllAccounts(keyPrefix: String, valueProducer: (String) -> String) {
    val count = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value?.toIntOrNull() ?: 3
    for (i in 1..count) {
        val key = "$keyPrefix$i"
        val currentValue = GlobalVars.configStates[key]?.value ?: "0"
        updateConfigValue(key, valueProducer(currentValue))
    }
}

@Composable
private fun SmallTextField(
    value: String,
    onValueChange: (String) -> Unit,
    width: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = {
            GlobalVars.isAutoRunEnabled = false
            onValueChange(it)
        },
        modifier = modifier
            .width(width)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
            .padding(4.dp)
    )
}

fun LazyListScope.AccountSettings() {
    item {
        SettingInputRow(
            key = GLOBAL_SETTINGS.CONFIG_COUNT.key,
            afterChange = { newValue ->
                val newCount = newValue.toIntOrNull()
                if (newCount != null) {
                    ConfigManager.expandProfileConfigs(newCount)
                }
            }
        )
        SettingInputRow(
            key = GLOBAL_SETTINGS.ACCOUNT_COUNT.key,
            afterChange = { newValue ->
                val newCount = newValue.toIntOrNull()
                if (newCount != null) {
                    ConfigManager.expandAccountConfigs(newCount)
                }
            }
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
        Row {
            CustomButton(
                text = "一键全选",
                marginTop = 6.dp,
                onClick = { batchUpdateAllAccounts(ACCOUNT_SETTINGS.ISOPEN.key) { "1" } }
            )
            CustomButton(
                text = "一键反选",
                marginTop = 6.dp,
                onClick = { batchUpdateAllAccounts(ACCOUNT_SETTINGS.ISOPEN.key) { if (it == "1") "0" else "1" } }
            )
        }
        var configValue by remember { mutableStateOf("") }
        var startAccount by remember { mutableStateOf("") }
        var endAccount by remember { mutableStateOf("") }

        var batchVerStartAccount by remember { mutableStateOf("") }
        var batchVerEndAccount by remember { mutableStateOf("") }
        var selectedVerIndex by remember { mutableStateOf(0) }
        var expandedVer by remember { mutableStateOf(false) }
        val verOptions = listOf("国服", "国际服", "私服")

        FlowRow(
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                text = "将配置",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 4.dp).align(Alignment.CenterVertically)
            )
            SmallTextField(
                value = configValue,
                onValueChange = { configValue = it },
                width = 60.dp
            )
            Text(
                text = "应用到账号",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically)
            )
            SmallTextField(
                value = startAccount,
                onValueChange = { startAccount = it }
            )
            Text(
                text = "-",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically)
            )
            SmallTextField(
                value = endAccount,
                onValueChange = { endAccount = it }
            )
            CustomButton(
                text = "确认",
                onClick = {
                    batchUpdateByRange(startAccount, endAccount, ACCOUNT_SETTINGS.ACCOUNT_CONFIG.key, configValue)
                },
                marginTop = 0.dp
            )
        }

        FlowRow(
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Text(
                text = "将账号",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(end = 4.dp).align(Alignment.CenterVertically)
            )
            SmallTextField(
                value = batchVerStartAccount,
                onValueChange = { batchVerStartAccount = it }
            )
            Text(
                text = "-",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically)
            )
            SmallTextField(
                value = batchVerEndAccount,
                onValueChange = { batchVerEndAccount = it }
            )
            Text(
                text = "改为",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically)
            )

            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopStart)
                    .align(Alignment.CenterVertically)
            ) {
                Button(
                    onClick = {
                        GlobalVars.isAutoRunEnabled = false
                        expandedVer = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Azure)
                ) {
                    Text(verOptions[selectedVerIndex], style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expandedVer,
                    onDismissRequest = { expandedVer = false }
                ) {
                    verOptions.forEachIndexed { idx, option ->
                        DropdownMenuItem(
                            text = {
                                Text(option, style = MaterialTheme.typography.labelMedium)
                            },
                            onClick = {
                                GlobalVars.isAutoRunEnabled = false
                                selectedVerIndex = idx
                                expandedVer = false
                            },
                            modifier = Modifier.height(35.dp)
                        )
                    }
                }
            }

            CustomButton(
                text = "确认",
                onClick = {
                    batchUpdateByRange(batchVerStartAccount, batchVerEndAccount, ACCOUNT_SETTINGS.GAME_VERSION.key, selectedVerIndex.toString())
                },
                marginTop = 0.dp
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
