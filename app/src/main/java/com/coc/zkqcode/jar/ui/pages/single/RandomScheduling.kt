@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

private val GROUP_TITLES = listOf(
    "凌晨组  0:00 - 6:00", "上午组  6:00 - 12:00", "下午组  12:00 - 18:00", "夜晚组  18:00 - 24:00"
)

private val RS_GRID_MAX_HEIGHT: Dp = 320.dp

private fun gameVersionLabel(value: String?): String = when (value) {
    "0" -> "国服"
    "1" -> "国际服"
    "2" -> "私服"
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

    Text(
        text = "（该功能制作中，未完善，请勿使用）", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp)
    )

    SettingSwitchIcon(
        key = GLOBAL_SETTINGS.RANDOM_SCHEDULING.key,
        explain = "启用随机上号后，脚本将根据当前时间段，从对应组的账号中随机选取顺序依次运行。每个账号运行完毕后，根据设定的概率决定是否继续对战或直接下线，并按对战时间比例计算等待时间后再运行下一个账号。\n\n随机上号与批量创号不能同时启用。",
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
    var expanded by remember { mutableStateOf(true) }

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
                        text = "剩余概率随机持续对战", style = MaterialTheme.typography.labelMedium, modifier = Modifier
                            .padding(end = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                FlowRow(
                    verticalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 6.dp)
                ) {
                    SettingInputRow(key = keys.continueMin, topPadding = 0.dp, suffix = "至")
                    SettingInputRow(key = keys.continueMax, topPadding = 0.dp, suffix = "分钟后下线")
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
    val scope = rememberCoroutineScope()

    val selectedAccounts = remember(accountsState.value) {
        accountsState.value.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragSelectMode by remember { mutableStateOf(true) }
    var dragPreview by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val displayedSelected = if (isDragging) dragPreview else selectedAccounts

    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(
            text = "选择加入本组的账号：", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "长按账号后拖动可批量选择。", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 4.dp)
        ) {
            CustomButton(
                text = "一键全选", marginTop = 0.dp, onClick = {
                    accountsState.value = (1..accountCount).joinToString(",")
                })
            CustomButton(
                text = "一键反选", marginTop = 0.dp, onClick = {
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
                    .pointerInput(accountCount, columnsCount, selectedAccounts) {
                        val slopPx = 10f * density
                        val edgeZonePx = 40f * density
                        val scrollSpeedPx = 6.5f * density
                        var currentPointer = Offset.Zero

                        fun hitTestVisibleItem(position: Offset): Int? {
                            return gridState.layoutInfo.visibleItemsInfo.firstOrNull { item ->
                                item.contains(position)
                            }?.let { it.index + 1 }
                        }

                        fun launchScrollJob(
                            direction: Float, depthFn: () -> Float, onTick: () -> Unit
                        ): Job = scope.launch {
                            while (true) {
                                val consumed = gridState.scrollBy(direction * scrollSpeedPx * (1f + depthFn()))
                                if (consumed != 0f) onTick()
                                delay(16L)
                            }
                        }

                        fun updateAutoScroll(
                            localY: Float, currentJob: Job?, onScrollTick: () -> Unit = {}
                        ): Job? {
                            val viewportHeight = size.height.toFloat()
                            val inTopZone = localY < edgeZonePx
                            val inBottomZone = localY > viewportHeight - edgeZonePx
                            return when {
                                inBottomZone -> {
                                    if (currentJob?.isActive == true) currentJob else launchScrollJob(1f, {
                                        ((localY - (viewportHeight - edgeZonePx)) / edgeZonePx).coerceIn(0f, 1f)
                                    }, onScrollTick)
                                }

                                inTopZone -> {
                                    if (currentJob?.isActive == true) currentJob else launchScrollJob(-1f, {
                                        ((edgeZonePx - localY) / edgeZonePx).coerceIn(0f, 1f)
                                    }, onScrollTick)
                                }

                                else -> {
                                    currentJob?.cancel()
                                    null
                                }
                            }
                        }

                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downPos = down.position

                            val movedBeforeLongPress = withTimeoutOrNull(400L) {
                                var moved = false
                                while (!moved) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    if (!change.pressed) break
                                    val dx = change.position.x - downPos.x
                                    val dy = change.position.y - downPos.y
                                    if (dx * dx + dy * dy > slopPx * slopPx) {
                                        moved = true
                                    }
                                }
                                if (moved) Unit else null
                            }

                            if (movedBeforeLongPress == null) {
                                val hitId = hitTestVisibleItem(downPos)
                                if (hitId != null) {
                                    GlobalVars.isAutoRunEnabled = false
                                    dragSelectMode = hitId !in selectedAccounts
                                    val initial = selectedAccounts.toMutableSet()
                                    if (dragSelectMode) initial.add(hitId) else initial.remove(hitId)
                                    isDragging = true
                                    dragPreview = initial
                                    currentPointer = downPos

                                    val currentIdHolder = intArrayOf(hitId)
                                    val scrollTickFn: () -> Unit = {
                                        val tickId = hitTestVisibleItem(currentPointer)
                                        if (tickId != null && tickId != currentIdHolder[0]) {
                                            currentIdHolder[0] = tickId
                                            val updated = dragPreview.toMutableSet()
                                            if (dragSelectMode) updated.add(tickId) else updated.remove(tickId)
                                            dragPreview = updated
                                        }
                                    }

                                    var autoScrollJob: Job? = updateAutoScroll(downPos.y, null, scrollTickFn)
                                    var event = awaitPointerEvent()
                                    while (event.changes.any { it.pressed }) {
                                        event.changes.forEach { it.consume() }
                                        val change = event.changes.firstOrNull()
                                        if (change != null) {
                                            currentPointer = change.position
                                            autoScrollJob = updateAutoScroll(currentPointer.y, autoScrollJob, scrollTickFn)

                                            val newId = hitTestVisibleItem(currentPointer)
                                            if (newId != null && newId != currentIdHolder[0]) {
                                                currentIdHolder[0] = newId
                                                val updated = dragPreview.toMutableSet()
                                                if (dragSelectMode) updated.add(newId) else updated.remove(newId)
                                                dragPreview = updated
                                            }
                                        }
                                        event = awaitPointerEvent()
                                    }

                                    autoScrollJob?.cancel()
                                    accountsState.value = dragPreview.sorted().joinToString(",")
                                    isDragging = false
                                    dragPreview = emptySet()
                                }
                            }
                        }
                    }) {
                val accountIds = remember(accountCount) { (1..accountCount).toList() }
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(columnsCount),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(spacingDp),
                    verticalArrangement = Arrangement.spacedBy(spacingDp)
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

private fun LazyGridItemInfo.contains(position: Offset): Boolean {
    val left = offset.x.toFloat()
    val top = offset.y.toFloat()
    val right = left + size.width
    val bottom = top + size.height
    return position.x in left..right && position.y in top..bottom
}
