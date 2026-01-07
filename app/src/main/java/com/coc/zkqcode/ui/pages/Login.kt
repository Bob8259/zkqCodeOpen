package com.coc.zkqcode.ui.pages

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.ui.components.InputRow
import com.coc.zkqcode.ui.database.Schema

@Composable
fun LoginScreen(configStates: Map<String, MutableState<String>>) {

    InputRow(
        label = Schema.GLOBAL_SETTINGS.first { it.key == "email" }.displayName,
        value = configStates["email"]?.value ?: "",
        onValueChange = { configStates["email"]?.value = it },
        key = "email"
    )
    InputRow(
        label = Schema.GLOBAL_SETTINGS.first { it.key == "password" }.displayName,
        value = configStates["password"]?.value ?: "",
        onValueChange = { configStates["password"]?.value = it },
        key = "password"
    )
    InputRow(
        label = Schema.GLOBAL_SETTINGS.first { it.key == "config_count" }.displayName,
        value = configStates["config_count"]?.value ?: "",
        onValueChange = { configStates["config_count"]?.value = it },
        key = "config_count"
    )
    Text("这是主页内容", modifier = Modifier.padding(top = 16.dp))
}
