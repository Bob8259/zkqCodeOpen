package com.coc.zkqcode.jar.ui.pages.nightbase

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.components.CustomButton
import com.coc.zkqcode.utils.components.SettingCheckBox
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.database.Schema
import com.coc.zkqcode.utils.database.Schema.MAIN_BASE_BUILDINGS
import kotlin.collections.set

@Composable
fun NightBaseUpgradeConfigs(index: Int, onNavigatePriority: (Int) -> Unit = {}){
    val items = Schema.NIGHT_BASE_BUILDINGS.all
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
        CustomButton(
            onClick = { onNavigatePriority(index) },
            text = "点击调整夜世界升级优先度"
        )
        FlowRow {
            items.forEach { item ->
                SettingCheckBox(key = "${item.key}_c${index}")
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(top = 6.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
    )
}