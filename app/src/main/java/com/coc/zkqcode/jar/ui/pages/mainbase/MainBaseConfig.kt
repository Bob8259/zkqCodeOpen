package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.CustomCheckBox

@Composable
fun MainBaseConfig(index: Int, configStates: Map<String, MutableState<String>>) {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        val autoAttackKey = "auto_attack_c$index"
        val autoAttackState = configStates[autoAttackKey]

        // Default to "1" (true) if not set, consistent with Schema default
        val isChecked = (autoAttackState?.value ?: "1") == "1"

        CustomCheckBox(
            text = "自动进攻",
            checkedState = if (isChecked) "1" else "0",
            onCheckStateChange = { checked ->
                autoAttackState?.value = if (checked) "1" else "0"
            },
            key = autoAttackKey
        )
    }
}
