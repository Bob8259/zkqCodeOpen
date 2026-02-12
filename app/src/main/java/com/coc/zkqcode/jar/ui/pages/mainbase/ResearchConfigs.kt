package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingCheckBox
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_TROOPS_AND_SPELLS

@Composable
fun ResearchConfigs(index: Int) {
    val items = MAIN_BASE_TROOPS_AND_SPELLS.all
    val isExpanded = remember { mutableStateOf(true) }

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
    Row {
        CustomButton(text = "一键全选", onClick = selectAll)
        CustomButton(text = "一键反选", onClick = invertSelection)
        CustomButton(text = if (isExpanded.value) "缩起" else "展开", onClick = { isExpanded.value = !isExpanded.value })
    }

    if (isExpanded.value) {
        FlowRow {
            items.forEach { item ->
                SettingCheckBox(key = "${item.key}_c${index}")
            }
        }
    }
}