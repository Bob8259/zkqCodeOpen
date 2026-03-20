package com.coc.zkqcode.jar.ui.pages.builderbase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingToggleButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.BUILDER_BASE_TROOPS
import kotlin.collections.set

@Composable
fun BuilderBaseResearchConfigs(index: Int) {
    val items = BUILDER_BASE_TROOPS.all
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
    Column {
        Row {
            CustomButton(text = "一键全选", onClick = selectAll)
            CustomButton(text = "一键反选", onClick = invertSelection)
            CustomButton(
                text = if (isExpanded.value) "缩起" else "展开",
                onClick = { isExpanded.value = !isExpanded.value })
        }

        AnimatedVisibility(visible = isExpanded.value) {
            FlowRow {
                items.forEach { item ->
                    SettingToggleButton(key = "${item.key}_c${index}")
                }
            }
        }
    }
}