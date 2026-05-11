@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

// Grid of clickable cards for each config file
@Composable
fun ConfigFileGrid(
    configCount: Int,
    onConfigSelected: (Int) -> Unit,
    onShowMsg: (String) -> Unit = {}
) {
    val allItems = (1..configCount).toList()
    var searchQuery by remember { mutableStateOf("") }

    // Filter items based on search query (matches index number or remark text)
    val filteredItems = if (searchQuery.isBlank()) allItems else allItems.filter { i ->
        val remark = GlobalVars.configStates["${Schema.MAIN_BASE_SETTINGS.CONFIG_REMARK.key}_c$i"]?.value ?: ""
        i.toString().contains(searchQuery, ignoreCase = true) ||
            remark.contains(searchQuery, ignoreCase = true)
    }

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
                            GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]?.value = clamped.toString()
                        }
                        ConfigManager.expandProfileConfigs(clamped)
                    }
                },
            )
            var fromText by remember { mutableStateOf("") }
            var toText by remember { mutableStateOf("") }
            FlowRow(modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    text = "将配置文件",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextField(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = fromText,
                    onValueChange = { fromText = it }
                )
                Text(
                    text = "的内容同步至配置文件",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextField(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 4.dp),
                    value = toText,
                    onValueChange = { toText = it }
                )
                CustomButton(
                    text = "确认",
                    marginTop = 6.dp,
                    onClick = {
                        val from = fromText.toIntOrNull()
                        val to = toText.toIntOrNull()
                        if (from != null && to != null && from != to
                            && from in 1..configCount && to in 1..configCount
                        ) {
                            ConfigManager.syncProfileConfigs(from, to)
                            onShowMsg("配置文件$from 已同步至 配置文件$to")
                        }
                    }
                )
            }
            Text(
                text = "点击方格调整配置文件。调整完成后，回到\"账号设置\"，给每个账号分配所需的配置文件。",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            // Search bar for filtering config files by index or remark
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔍",
                    modifier = Modifier.padding(end = 6.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "搜索配置文件（编号或备注）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "✕",
                        color = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { searchQuery = "" }
                            .padding(4.dp)
                    )
                }
            }
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val spacing = 8.dp
                val cellWidth = 110.dp
                val columnsCount = maxOf(1, ((maxWidth + spacing) / (cellWidth + spacing)).toInt())
                val actualRowCount = (filteredItems.size + columnsCount - 1) / columnsCount
                val itemHeight = 48.dp
                val gridHeight = itemHeight * actualRowCount + spacing * (actualRowCount - 1).coerceAtLeast(0)

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = cellWidth),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    items(filteredItems) { i ->
                        Box(
                            modifier = Modifier
                                .height(itemHeight)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.PrimaryLight)
                                .clickable { onConfigSelected(i) }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val remarkState = GlobalVars.configStates["${Schema.MAIN_BASE_SETTINGS.CONFIG_REMARK.key}_c$i"]?.value ?: ""
                            if (remarkState.isNotEmpty()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = "配置文件$i",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "备注: $remarkState",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
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

}
