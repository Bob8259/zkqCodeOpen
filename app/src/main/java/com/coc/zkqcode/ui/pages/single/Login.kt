package com.coc.zkqcode.ui.pages.single

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

}
