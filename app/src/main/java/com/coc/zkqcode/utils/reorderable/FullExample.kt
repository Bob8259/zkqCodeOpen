package com.coc.zkqcode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.utils.reorderable.ReorderableItem
import com.coc.zkqcode.utils.reorderable.rememberReorderableLazyGridState

@Composable
fun ReorderableGridDemo() {
    var items by remember { mutableStateOf((1..20).toList()) }
    val gridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(gridState) { from, to ->
        items = items.toMutableList().apply {
            add(items.indexOf(to.key), removeAt(items.indexOf(from.key)))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it }) { item ->
            ReorderableItem(reorderableState, key = item) { isDragging ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDragging) Color.LightGray else MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Item $item", style = MaterialTheme.typography.bodyLarge)
                    
                    // Icon at the top-right corner for dragging
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Reorder",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                            .longPressDraggableHandle()
                    )
                }
            }
        }
    }
}
