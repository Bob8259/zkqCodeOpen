package com.coc.zkqcode.jar.ui.pages.nightbase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.core.ui.reorderable.ReorderableItem
import com.coc.zkqcode.core.ui.reorderable.rememberReorderableLazyGridState
import kotlinx.coroutines.launch

@Composable
fun NightBaseUpgradePriority(index: Int, onSaveSuccess: () -> Unit) {
    val allPriorities = Schema.NIGHT_BASE_BUILDINGS_PRIORITY.all

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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)
        .background(Color.White)) {
        Text(
            text = "拖动铅笔符号调整优先度，越靠上，越靠左表示优先度越高。",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 90.dp),
            modifier = Modifier.weight(1f),
            state = lazyGridState,
            contentPadding = PaddingValues(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(list, key = { it.key }) { item ->
                ReorderableItem(reorderableState, key = item.key) { _ ->
                    Card(
                        modifier = Modifier.height(100.dp),
                        onClick = {}
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            // Draggable Handle
                            IconButton(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .draggableHandle(),
                                onClick = {},
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Reorder")
                            }

                            Text(
                                text = item.displayName,
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
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
            },
            text = if (isSaving) "保存中，请稍候" else "完成",
            enable = !isSaving
        )
    }
}
