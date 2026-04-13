@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

// Grid of clickable cards for each config file
@Composable
fun ConfigFileGrid(
    configCount: Int,
    onConfigSelected: (Int) -> Unit
) {
    val items = (1..configCount).toList()

    SettingSection {
        Column {
            SettingInputRow(
                key = GLOBAL_SETTINGS.CONFIG_COUNT.key,
                afterChange = { newValue ->
                    val newCount = newValue.toIntOrNull()
                    if (newCount != null) {
                        // Clamp to max 100
                        val clamped = newCount.coerceAtMost(100)
                        if (clamped != newCount) {
                            com.coc.zkqcode.core.data.database.GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]?.value = clamped.toString()
                        }
                        ConfigManager.expandProfileConfigs(clamped)
                    }
                },
            )
            Text(
                text = "点击方格调整配置文件。调整完成后，回到\"账号设置\"，给每个账号分配所需的配置文件。",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val spacing = 8.dp
                val cellWidth = min((maxWidth - spacing * 2) / 3, 120.dp)
                val columnsCount = maxOf(1, ((maxWidth.value + spacing.value) / (cellWidth.value + spacing.value)).toInt())
                val actualRowCount = (items.size + columnsCount - 1) / columnsCount
                val gridHeight = (actualRowCount * 56).dp
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = cellWidth),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    items(items) { i ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.PrimaryLight)
                                .clickable { onConfigSelected(i) }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "配置文件$i",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
