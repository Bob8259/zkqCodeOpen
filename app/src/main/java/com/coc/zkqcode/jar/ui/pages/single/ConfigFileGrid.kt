@file:Suppress("FunctionName")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

// Grid of clickable cards for each config file
@Composable
fun ConfigFileGrid(
    configCount: Int,
    onConfigSelected: (Int) -> Unit,
    onShowMsg: (String) -> Unit = {}
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
            var fromText by remember { mutableStateOf("") }
            var toStartText by remember { mutableStateOf("") }
            var toEndText by remember { mutableStateOf("") }
            FlowRow(modifier = Modifier.padding(top = 6.dp)) {
                Text(
                    text = Strings.syncProfileLabel,
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
                    text = Strings.syncToProfile,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextField(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 4.dp),
                    value = toStartText,
                    onValueChange = { toStartText = it }
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextField(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 4.dp),
                    value = toEndText,
                    onValueChange = { toEndText = it }
                )
                CustomButton(
                    text = Strings.confirm,
                    marginTop = 6.dp,
                    onClick = syncButton@{
                        val from = fromText.toIntOrNull()
                        val toStart = toStartText.toIntOrNull()
                        val toEnd = toEndText.toIntOrNull()
                        if (from == null || toStart == null || toEnd == null) {
                            onShowMsg(Strings.invalidProfileNumber)
                            return@syncButton
                        }
                        if (from !in 1..configCount) {
                            onShowMsg(Strings.sourceOutOfRange(configCount))
                            return@syncButton
                        }
                        if (toStart !in 1..configCount || toEnd !in 1..configCount) {
                            onShowMsg(Strings.targetOutOfRange(configCount))
                            return@syncButton
                        }
                        if (toStart > toEnd) {
                            onShowMsg(Strings.startGreaterThanEnd)
                            return@syncButton
                        }

                        val targets = (toStart..toEnd).filter { it != from }
                        if (targets.isEmpty()) {
                            onShowMsg(Strings.noTargetsToSync)
                            return@syncButton
                        }

                        targets.forEach { to -> ConfigManager.syncProfileConfigs(from, to) }
                        val skippedSelfText = if (from in toStart..toEnd) Strings.skippedSelf(from) else ""
                        onShowMsg(Strings.syncSuccess(from, targets.joinToString("、")) + skippedSelfText)
                    }
                )
            }
            Text(
                text = Strings.profileHint,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val spacing = 8.dp
                val cellWidth = 110.dp
                val columnsCount = maxOf(1, ((maxWidth + spacing) / (cellWidth + spacing)).toInt())
                val actualRowCount = (items.size + columnsCount - 1) / columnsCount
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
                    items(items) { i ->
                        Box(
                            modifier = Modifier
                                .height(itemHeight)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.PrimaryLight)
                                .clickable { onConfigSelected(i) }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val remarkState = com.coc.zkqcode.core.data.database.GlobalVars.configStates["${com.coc.zkqcode.jar.ui.schema.Schema.MAIN_BASE_SETTINGS.CONFIG_REMARK.key}_c$i"]?.value ?: ""
                            if (remarkState.isNotEmpty()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(
                                        text = Strings.profileFile(i),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = Strings.remark(remarkState),
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
                                    text = Strings.profileFile(i),
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
