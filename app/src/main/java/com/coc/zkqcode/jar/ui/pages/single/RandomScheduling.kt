@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.delay

private data class GroupKeys(
    val battleRatio: String, val stopProb: String, val continueMin: String, val continueMax: String, val noWorker: String, val fullRes: String, val accounts: String
)

private fun groupKeysFor(suffix: String) = GroupKeys(
    battleRatio = "rs_battle_ratio_$suffix",
    stopProb = "rs_stop_prob_$suffix",
    continueMin = "rs_continue_min_$suffix",
    continueMax = "rs_continue_max_$suffix",
    noWorker = "rs_no_worker_$suffix",
    fullRes = "rs_full_res_$suffix",
    accounts = "rs_accounts_$suffix"
)

private val GROUP_KEYS = listOf(
    groupKeysFor("dawn"), groupKeysFor("morning"), groupKeysFor("afternoon"), groupKeysFor("night")
)

private val GROUP_TITLES get() = listOf(
    Strings.dawnGroup, Strings.morningGroup, Strings.afternoonGroup, Strings.nightGroup
)

private val RS_GRID_MAX_HEIGHT: Dp = 320.dp

private fun gameVersionLabel(value: String?): String = when (value) {
    "0" -> Strings.cnVersion
    "1" -> Strings.globalVersion
    "2" -> Strings.atrasis
    else -> ""
}

private data class AccountCellColors(val bg: Color, val border: Color, val text: Color)

private fun accountCellColors(isSelected: Boolean) = AccountCellColors(
    bg = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.10f), border = if (isSelected) AppColors.Primary else AppColors.PrimaryLight, text = if (isSelected) Color.White else AppColors.TextPrimary
)

private fun stepBasedAutoSize(
    maxFontSize: TextUnit, minFontSize: TextUnit = 5.sp
) = TextAutoSize.StepBased(minFontSize = minFontSize, maxFontSize = maxFontSize, stepSize = 1.sp)

@Composable
fun RandomScheduling() {
    val rsState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.RANDOM_SCHEDULING.key) {
        mutableStateOf("0")
    }
    val isEnabled = rsState.value == "1"
    SettingSwitchIcon(
        key = GLOBAL_SETTINGS.RANDOM_SCHEDULING.key,
        explain = Strings.randomSchedulingExplain,
        afterChange = { checked ->
            if (checked) {
                GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
            }
        })

    AnimatedVisibility(visible = isEnabled) {
        Column {
            GROUP_KEYS.forEachIndexed { index, keys ->
                TimeGroupSection(
                    title = GROUP_TITLES[index], keys = keys
                )
            }
        }
    }
}

@Composable
private fun TimeGroupSection(title: String, keys: GroupKeys) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(top = 4.dp)) {
        SectionBanner(title = title, isExpanded = expanded, onClick = { expanded = !expanded })

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
                SettingInputRow(key = keys.battleRatio, suffix = "%")

                FlowRow(
                    verticalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 6.dp)
                ) {
                    SettingInputRow(key = keys.stopProb, topPadding = 0.dp, suffix = "%")
                    Text(
                        text = Strings.remainingProbBattle, style = MaterialTheme.typography.labelMedium, modifier = Modifier
                            .padding(end = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                FlowRow(
                    verticalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 6.dp)
                ) {
                    SettingInputRow(key = keys.continueMin, topPadding = 0.dp, suffix = Strings.to)
                    SettingInputRow(key = keys.continueMax, topPadding = 0.dp, suffix = Strings.minutesThenOffline)
                }

                SettingSwitchIcon(key = keys.noWorker)
                SettingSwitchIcon(key = keys.fullRes)
                AccountGroupSelector(accountsKey = keys.accounts)
            }
        }
    }
}

@Composable
private fun AccountGroupSelector(accountsKey: String) {
    val accountsState = GlobalVars.configStates.getOrPut(accountsKey) { mutableStateOf("") }
    val accountCountStr = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value ?: "2"
    val accountCount = accountCountStr.toIntOrNull()?.coerceAtLeast(1) ?: 2
    val gridState = rememberLazyGridState()
    val hapticFeedback = LocalHapticFeedback.current

    val selectedAccounts = remember(accountsState.value) {
        accountsState.value.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    // --- 拖动选择相关的状态 ---
    var isDragging by remember { mutableStateOf(false) }
    var isAdding by remember { mutableStateOf(true) }
    var previewSelection by remember { mutableStateOf(setOf<Int>()) }
    var pointerPosition by remember { mutableStateOf(Offset.Zero) }

    // 如果正在拖动，显示预览状态；否则显示实际保存的数据状态
    val displayedSelected = if (isDragging) previewSelection else selectedAccounts

    val toggleAccount = { id: Int ->
        val updatedSelection = if (id in selectedAccounts) {
            selectedAccounts - id
        } else {
            selectedAccounts + id
        }
        accountsState.value = updatedSelection.sorted().joinToString(",")
    }

    // 根据坐标获取账号ID并更新预览状态
    val updateSelectionAt = { offset: Offset ->
        val itemInfo = gridState.layoutInfo.visibleItemsInfo.find { item ->
            offset.x >= item.offset.x && offset.x <= (item.offset.x + item.size.width) &&
            offset.y >= item.offset.y && offset.y <= (item.offset.y + item.size.height)
        }
        if (itemInfo != null) {
            val id = itemInfo.index + 1 // 因为 items 数据是 1..accountCount，索引加 1 即为 ID
            if (id in 1..accountCount) {
                previewSelection = if (isAdding) {
                    previewSelection + id
                } else {
                    previewSelection - id
                }
            }
        }
    }

    // --- 边缘自动滚动检测 ---
    LaunchedEffect(isDragging) {
        while (isDragging) {
            val y = pointerPosition.y
            val viewportHeight = gridState.layoutInfo.viewportSize.height.toFloat()
            val threshold = 150f // 触发边缘滚动的距离阈值 (px)
            var scrollAmount = 0f

            if (viewportHeight > 0f) {
                if (y < threshold) {
                    // 靠近顶部，向上滚动 (负值)
                    scrollAmount = (y - threshold) * 0.2f
                } else if (y > viewportHeight - threshold) {
                    // 靠近底部，向下滚动 (正值)
                    scrollAmount = (y - (viewportHeight - threshold)) * 0.2f
                }
            }

            if (scrollAmount != 0f) {
                gridState.scrollBy(scrollAmount)
                // 滚动后，列表项位置改变，需重新计算手指下方的格子
                updateSelectionAt(pointerPosition)
            }
            delay(16L) // 约 60fps 的刷新率持续检测
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 6.dp)
            .padding(horizontal = 6.dp)
            .border(1.dp, AppColors.PrimaryLight, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text(
            text = Strings.selectAccountsForGroup, style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = Strings.tapOrDragHint, color = Color.Gray, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 4.dp)
        ) {
            CustomButton(
                text = Strings.selectAll, marginTop = 0.dp, onClick = {
                    accountsState.value = (1..accountCount).joinToString(",")
                })
            CustomButton(
                text = Strings.invertSelection, marginTop = 0.dp, onClick = {
                    val inverted = (1..accountCount).filter { it !in selectedAccounts }
                    accountsState.value = inverted.joinToString(",")
                })
        }

        val cellSizeDp = 52.dp
        val spacingDp = 4.dp

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnsCount = maxOf(1, ((maxWidth + spacingDp) / (cellSizeDp + spacingDp)).toInt())
            val rowCount = (accountCount + columnsCount - 1) / columnsCount
            val fullGridHeight = cellSizeDp * rowCount + spacingDp * (rowCount - 1).coerceAtLeast(0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = RS_GRID_MAX_HEIGHT)
                    .then(if (fullGridHeight < RS_GRID_MAX_HEIGHT) Modifier.heightIn(min = fullGridHeight) else Modifier)
                    // 在此处拦截手势并处理长按拖动
                    .pointerInput(accountCount, selectedAccounts) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                isDragging = true
                                pointerPosition = offset
                                previewSelection = selectedAccounts

                                // 长按震动反馈
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

                                // 判断起始格子状态以决定是"添加模式"还是"移除模式"
                                val startItemInfo = gridState.layoutInfo.visibleItemsInfo.find { item ->
                                    offset.x >= item.offset.x && offset.x <= (item.offset.x + item.size.width) &&
                                    offset.y >= item.offset.y && offset.y <= (item.offset.y + item.size.height)
                                }
                                if (startItemInfo != null) {
                                    val startId = startItemInfo.index + 1
                                    isAdding = startId !in selectedAccounts
                                    updateSelectionAt(offset)
                                } else {
                                    isAdding = true
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                pointerPosition = change.position
                                updateSelectionAt(pointerPosition)
                            },
                            onDragEnd = {
                                isDragging = false
                                // 拖动结束，将预览结果保存到全局状态
                                accountsState.value = previewSelection.sorted().joinToString(",")
                            },
                            onDragCancel = {
                                isDragging = false
                                previewSelection = selectedAccounts // 恢复原状
                            }
                        )
                    }
            ) {
                val accountIds = remember(accountCount) { (1..accountCount).toList() }
                
                // 将 userScrollEnabled 绑定，确保拖动时不要被默认滚动拦截
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columnsCount),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(spacingDp),
                    verticalArrangement = Arrangement.spacedBy(spacingDp),
                    userScrollEnabled = !isDragging 
                ) {
                    items(accountIds, key = { it }) { id ->
                        val isSelected = id in displayedSelected
                        val colors = accountCellColors(isSelected)
                        val versionLabel = gameVersionLabel(
                            GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}$id"]?.value
                        )

                        Box(
                            modifier = Modifier
                                .width(cellSizeDp)
                                .pointerInput(id, isDragging, selectedAccounts) {
                                    detectTapGestures(
                                        onTap = {
                                            if (!isDragging) {
                                                toggleAccount(id)
                                            }
                                        }
                                    )
                                }
                                .background(colors.bg, RoundedCornerShape(8.dp))
                                .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                                .padding(horizontal = 2.dp)
                                .heightIn(min = cellSizeDp), contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = id.toString(), color = colors.text, style = MaterialTheme.typography.labelMedium, maxLines = 1, autoSize = stepBasedAutoSize(
                                        maxFontSize = MaterialTheme.typography.labelMedium.fontSize, minFontSize = 6.sp
                                    )
                                )
                                if (versionLabel.isNotEmpty()) {
                                    Text(
                                        text = versionLabel, color = colors.text, style = MaterialTheme.typography.labelSmall, maxLines = 1, autoSize = stepBasedAutoSize(
                                            MaterialTheme.typography.labelSmall.fontSize
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}