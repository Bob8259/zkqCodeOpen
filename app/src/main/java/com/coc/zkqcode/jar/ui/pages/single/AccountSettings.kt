package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.database.Schema

fun LazyListScope.AccountSettings() {
    item {
        InputRow(
            label = Schema.GLOBAL_SETTINGS.first { it.key == "config_count" }.displayName,
            value = GlobalVars.configStates["config_count"]?.value ?: "",
            onValueChange = { GlobalVars.configStates["config_count"]?.value = it },
        )
        InputRow(
            label = Schema.GLOBAL_SETTINGS.first { it.key == "account_count" }.displayName,
            value = GlobalVars.configStates["account_count"]?.value ?: "",
            onValueChange = { GlobalVars.configStates["account_count"]?.value = it },
        )
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
                        GlobalVars.configStates["account_count"]?.value?.toIntOrNull() ?: 3
                    for (i in 1..count) {
                        val key = "isopen$i"
                        if (GlobalVars.configStates.containsKey(key)) {
                            GlobalVars.configStates[key]?.value = "1"
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
                        GlobalVars.configStates["account_count"]?.value?.toIntOrNull() ?: 3
                    for (i in 1..count) {
                        val key = "isopen$i"
                        val currentValue = GlobalVars.configStates[key]?.value
                        val newValue = if (currentValue == "1") "0" else "1"

                        if (GlobalVars.configStates.containsKey(key)) {
                            GlobalVars.configStates[key]?.value = newValue
                        } else {
                            GlobalVars.configStates[key] = mutableStateOf(newValue)
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
    val accountCountStr = GlobalVars.configStates["account_count"]?.value ?: "3"
    val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
    items(count = currentAccountCount, key = { it + 1 }) { i ->
        AccountConfig(index = i + 1)
    }
}
