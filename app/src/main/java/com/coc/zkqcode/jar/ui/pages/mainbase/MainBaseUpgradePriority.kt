package com.coc.zkqcode.jar.ui.pages.mainbase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.core.ui.reorderable.ReorderableItem
import com.coc.zkqcode.core.ui.reorderable.rememberReorderableLazyGridState
import kotlinx.coroutines.launch

// Inline equivalent of the private rememberConfigState in Components.kt
private fun getConfigState(key: String): MutableState<String> = GlobalVars.configStates.getOrPut(key) { mutableStateOf(Schema.getDefaultValue(key)) }

@Composable
fun MainBaseUpgradePriority(index: Int, onSaveSuccess: () -> Unit) {
    val allPriorities = Schema.MAIN_BASE_BUILDING_PRIORITIES.all

    // Sort items based on current value in GlobalVars.
    // If a value is missing or invalid, treat it as very low priority (high index).
    // We assume the values are 1-based indices stringified.
    val sortedInitial = remember(index) {
        allPriorities.sortedBy { def ->
            val key = "${def.key}_c$index"
            GlobalVars.configStates[key]?.value?.toIntOrNull() ?: Int.MAX_VALUE
        }
    }

    var list by remember { mutableStateOf(sortedInitial) }

    val lazyGridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(lazyGridState) { from, to ->
        list = list.toMutableList().apply {
            // We use the key to find the index because from/to are LazyGridItemInfo
            // and our list is List<SettingDef>.
            // The LazyVerticalGrid items key is set to def.key.
            val fromIndex = indexOfFirst { it.key == from.key }
            val toIndex = indexOfFirst { it.key == to.key }
            if (fromIndex != -1 && toIndex != -1) {
                add(toIndex, removeAt(fromIndex))
            }
        }
    }

    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(AppColors.Background)
    ) {
        Text(
            text = "长按单元格拖动调整优先度，点击切换启用/禁用。越靠上，越靠左表示优先度越高。", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 8.dp)
        )

        Row(modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)) {
            // Select all
            CustomButton(text = "一键全选", onClick = {
                list.forEach { item ->
                    val buildingKey = item.key.removeSuffix("_priority")
                    val enableKey = "${buildingKey}_c$index"
                    val state = getConfigState(enableKey)
                    state.value = "1"
                }
            })
            // Invert selection
            CustomButton(text = "一键反选", onClick = {
                list.forEach { item ->
                    val buildingKey = item.key.removeSuffix("_priority")
                    val enableKey = "${buildingKey}_c$index"
                    val state = getConfigState(enableKey)
                    state.value = if (state.value == "1") "0" else "1"
                }
            })
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier.weight(1f),
            state = lazyGridState,
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(list, key = { it.key }) { item ->
                ReorderableItem(reorderableState, key = item.key) { _ ->
                    // Derive the building enable key by stripping "_priority" suffix
                    val buildingKey = item.key.removeSuffix("_priority")
                    val enableKey = "${buildingKey}_c$index"
                    val enableState = getConfigState(enableKey)
                    val isEnabled = enableState.value == "1"

                    Card(
                        modifier = Modifier
                            .height(40.dp)
                            .longPressDraggableHandle(), shape = RoundedCornerShape(8.dp), onClick = {
                            GlobalVars.isAutoRunEnabled = false
                            enableState.value = if (isEnabled) "0" else "1"
                        }, colors = CardDefaults.cardColors(
                            containerColor = if (isEnabled) AppColors.PrimaryLight else AppColors.CardBg
                        )
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Text(
                                text = item.displayName,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(horizontal = 6.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isEnabled) Color.White else AppColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }

        CustomButton(
            onClick = {
                isSaving = true
                scope.launch {
                    // Update GlobalVars with new order
                    list.forEachIndexed { i, def ->
                        val key = "${def.key}_c$index"
                        // Priority is 1-based index
                        val priority = (i + 1).toString()

                        // Ensure the GlobalVar entry exists, though it should if loaded from Schema
                        if (GlobalVars.configStates.containsKey(key)) {
                            GlobalVars.configStates[key]?.value = priority
                        } else {
                            // Should not happen if initialized correctly, but as a fallback/safety:
                            GlobalVars.configStates[key] = mutableStateOf(priority)
                        }
                    }
                    isSaving = false
                    onSaveSuccess()
                }
            }, marginBottom = 4.dp, text = if (isSaving) "保存中，请稍候" else "完成", enable = !isSaving
        )
    }
}
