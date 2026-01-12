package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.InputRow
import com.coc.zkqcode.utils.database.Schema

fun LazyListScope.AccountSettings(configStates: Map<String, MutableState<String>>) {
    item {
        InputRow(
            label = Schema.GLOBAL_SETTINGS.first { it.key == "config_count" }.displayName,
            value = configStates["config_count"]?.value ?: "",
            onValueChange = { configStates["config_count"]?.value = it },
        )
        InputRow(
            label = Schema.GLOBAL_SETTINGS.first { it.key == "account_count" }.displayName,
            value = configStates["account_count"]?.value ?: "",
            onValueChange = { configStates["account_count"]?.value = it },
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
        )
    }

    // Display account configurations based on account_count
    val accountCountStr = configStates["account_count"]?.value ?: "3"
    val currentAccountCount = accountCountStr.toIntOrNull() ?: 3
    items(count = currentAccountCount, key = { it + 1 }) { i ->
        AccountConfig(configStates = configStates, index = i + 1)
    }
}
