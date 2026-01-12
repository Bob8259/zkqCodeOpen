package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import com.coc.zkqcode.utils.components.CustomCheckBox
import com.coc.zkqcode.utils.database.Schema

@Composable
fun MainBaseConfig(index: Int, configStates: Map<String, MutableState<String>>) {
    Column {
        CustomCheckBox(
            text = Schema.MAIN_BASE_SETTINGS.first { it.key == "auto_attack" }.displayName,
            checkedState = configStates["auto_attack_c$index"]?.value ?: "0",
            onCheckStateChange = { checked ->
                configStates["auto_attack_c$index"]?.value = if (checked) "1" else "0"
            },
        )
    }
}
