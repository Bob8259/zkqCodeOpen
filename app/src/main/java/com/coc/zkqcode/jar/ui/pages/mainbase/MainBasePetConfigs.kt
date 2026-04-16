package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingToggleButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_PETS

@Composable
fun MainBasePetConfigs(index: Int) {
    // Render pet toggles directly from the schema list so new pets appear automatically.
    val items = MAIN_BASE_PETS.all
    val isExpanded = remember { mutableStateOf(false) }

    // 一键全选
    val selectAll = {
        items.forEach { item ->
            val key = "${item.key}_c${index}"
            if (!GlobalVars.configStates.containsKey(key)) {
                GlobalVars.configStates[key] = mutableStateOf("1")
            } else {
                GlobalVars.configStates[key]!!.value = "1"
            }
        }
    }

    // 一键反选
    val invertSelection = {
        items.forEach { item ->
            val key = "${item.key}_c${index}"
            val currentState = GlobalVars.configStates[key]!!.value
            val newValue = if (currentState == "1") "0" else "1"
            if (!GlobalVars.configStates.containsKey(key)) {
                GlobalVars.configStates[key] = mutableStateOf(newValue)
            } else {
                GlobalVars.configStates[key]!!.value = newValue
            }
        }
    }
    Column {
        CustomButton(text = if (isExpanded.value) "缩起列表" else "展开列表", onClick = { isExpanded.value = !isExpanded.value }, containerColor = AppColors.Teal)

        AnimatedVisibility(visible = isExpanded.value) {
            Column {
                Row {
                    CustomButton(text = "一键全选", onClick = selectAll)
                    CustomButton(text = "一键反选", onClick = invertSelection)
                }
                FlowRow(modifier = Modifier.padding(top = 6.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)).padding(8.dp)) {
                    items.forEach { item ->
                        SettingToggleButton(key = "${item.key}_c${index}")
                    }
                }
            }
        }
    }
}