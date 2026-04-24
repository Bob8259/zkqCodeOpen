@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

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
    bg = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.10f),
    border = if (isSelected) AppColors.Primary else AppColors.PrimaryLight,
    text = if (isSelected) Color.White else AppColors.TextPrimary
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
    val density = LocalDensity.current
    val viewConfiguration = LocalViewConfiguration.current

    val selectedAccounts = remember(accountsState.value) {
        accountsState.value.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    // 预览选中状态（拖动过程中临时显示）
    var previewSelected by remember { mutableStateOf(selectedAccounts) }
    // 是否处于拖动模式
    var isDragging by remember { mutableStateOf(false) }
    // 当前拖动模式：true=添加，false=移除
    var dragMode by remember { mutableStateOf(true) }
    // 边缘自动滚动的协程 Job
    val edgeScrollJob = remember { mutableStateOf<Job?>(null) }

    // 当数据源 selectedAccounts 变化时（例如按钮全选），同步预览
    LaunchedEffect(selectedAccounts) {
        if (!isDragging) {
            previewSelected = selectedAccounts
        }
    }

    val displayedSelected = if (isDragging) previewSelected else selectedAccounts

    Column(
        modifier = Modifier
            .padding(top = 6.dp)
            .padding(horizontal = 6.dp)
            .border(1.dp, AppColors.PrimaryLight, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Text(
            text = "选择加入本组的账号：", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 4.dp)
        ) {
            CustomButton(text = "一键全选", marginTop = 0.dp, onClick = {
                accountsState.value = (1..accountCount).joinToString(",")
            })
            CustomButton(text = "一键反选", marginTop = 0.dp, onClick = {
                val inverted = (1..accountCount).filter { it !in selectedAccounts }
                accountsState.value = inverted.joinToString(",")
            })
        }

        val cellSizeDp = 52.dp
        val spacingDp = 4.dp
        val cellSizePx = with(density) { cellSizeDp.toPx() }
        val spacingPx = with(density) { spacingDp.toPx() }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnsCount = maxOf(1, ((maxWidth + spacingDp) / (cellSizeDp + spacingDp)).toInt())
            val rowCount = (accountCount + columnsCount - 1) / columnsCount
            val fullGridHeight = cellSizeDp * rowCount + spacingDp * (rowCount - 1).coerceAtLeast(0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = RS_GRID_MAX_HEIGHT)
                    .then(if (fullGridHeight < RS_GRID_MAX_HEIGHT) Modifier.heightIn(min = fullGridHeight) else Modifier)
                    // 手势处理：长按后拖动选择
                    .pointerInput(accountCount, selectedAccounts) {
                        val touchSlop = viewConfiguration.touchSlop
                        val longPressTimeout = 400L

                        // 辅助函数：根据手指在 Box 内的坐标找到账号ID
                        fun findAccountId(fingerPos: Offset): Int? {
                            val layoutInfo = gridState.layoutInfo
                            // viewportStartOffset 是 Int，转为 Float 参与计算
                            val viewportOffset = layoutInfo.viewportStartOffset.toFloat()
                            for (item in layoutInfo.visibleItemsInfo) {
                                val itemLeft = (item.index % columnsCount) * (cellSizePx + spacingPx)
                                val itemRight = itemLeft + cellSizePx
                                val itemTop = item.offset.toFloat() - viewportOffset
                                val itemBottom = itemTop + item.size.toFloat()
                                if (fingerPos.x in itemLeft..itemRight && fingerPos.y in itemTop..itemBottom) {
                                    return item.index + 1 // 账号ID从1开始
                                }
                            }
                            return null
                        }

                        // 边缘自动滚动速度计算
                        fun scrollSpeed(distanceFromEdge: Float, maxDistance: Float): Float {
                            if (distanceFromEdge <= 0) return 0f
                            val factor = (distanceFromEdge / maxDistance).coerceIn(0f, 1f)
                            return 10f * factor
                        }

                        // 等待按下事件
                        while (true) {
                            val downEvent = awaitPointerEvent(PointerEventPass.Main)
                            val down = downEvent.changes.first()
                            if (down.changedToUp()) continue
                            val downPos = down.position
                            var longPressHandled = false

                            // 检测长按或提前移动
                            withTimeoutOrNull<Unit>(longPressTimeout) {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    val change = event.changes.first()
                                    if (change.changedToUp()) {
                                        change.consumeAllChanges()
                                        throw CancellationException("Up before long press")
                                    } else {
                                        val currentPos = change.position
                                        val distance = (currentPos - downPos).getDistance()
                                        if (distance > touchSlop) {
                                            change.consumeAllChanges()
                                            throw CancellationException("Moved too far")
                                        }
                                    }
                                }
                            } ?: run { longPressHandled = true }

                            if (!longPressHandled) continue

                            // ---------- 长按触发，进入拖动选择模式 ----------
                            val initAccount = findAccountId(downPos)
                            val actualSet = selectedAccounts
                            dragMode = initAccount != null && initAccount !in actualSet // true: 添加模式； false: 移除模式
                            previewSelected = actualSet.toMutableSet().apply {
                                if (initAccount != null) {
                                    if (dragMode) add(initAccount) else remove(initAccount)
                                }
                            }.toSet()
                            isDragging = true

                            var currentPos = downPos
                            // 拖动主循环
                            try {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Main)
                                    val change = event.changes.first()
                                    if (change.changedToUp()) {
                                        change.consumeAllChanges()
                                        break
                                    } else {
                                        currentPos = change.position
                                        val account = findAccountId(currentPos)
                                        val mutableSet = previewSelected.toMutableSet()
                                        if (account != null) {
                                            if (dragMode) mutableSet.add(account) else mutableSet.remove(account)
                                            previewSelected = mutableSet.toSet()
                                        }

                                        // 处理边缘自动滚动
                                        val boxHeight = size.height.toFloat()
                                        val edgeHeight = with(density) { 30.dp.toPx() }
                                        edgeScrollJob.value?.cancel()
                                        when {
                                            currentPos.y < edgeHeight -> {
                                                val speed = scrollSpeed(edgeHeight - currentPos.y, edgeHeight)
                                                edgeScrollJob.value = scope.launch {
                                                    while (isActive) {
                                                        gridState.scrollBy(-speed)
                                                        delay(16)
                                                    }
                                                }
                                            }
                                            currentPos.y > boxHeight - edgeHeight -> {
                                                val speed = scrollSpeed(currentPos.y - (boxHeight - edgeHeight), edgeHeight)
                                                edgeScrollJob.value = scope.launch {
                                                    while (isActive) {
                                                        gridState.scrollBy(speed)
                                                        delay(16)
                                                    }
                                                }
                                            }
                                            else -> {
                                                edgeScrollJob.value = null
                                            }
                                        }
                                        change.consumeAllChanges()
                                    }
                                }
                            } finally {
                                edgeScrollJob.value?.cancel()
                                edgeScrollJob.value = null
                                // 保存最终结果
                                accountsState.value = previewSelected.toSortedSet().joinToString(",")
                                isDragging = false
                            }
                        }
                    }
            ) {
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