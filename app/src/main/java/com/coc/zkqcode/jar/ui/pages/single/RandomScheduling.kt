@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalWindowInfo
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
import kotlinx.coroutines.launch

// Data class holding the config key names for one time group
private data class GroupKeys(
    val battleRatio: String,
    val stopProb: String,
    val continueMin: String,
    val continueMax: String,
    val noWorker: String,
    val fullRes: String,
    val accounts: String
)

// Optimization #1: factory eliminates 4 identical manual GroupKeys(...) blocks
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
    groupKeysFor("dawn"),
    groupKeysFor("morning"),
    groupKeysFor("afternoon"),
    groupKeysFor("night")
)

private val GROUP_TITLES = listOf(
    "凌晨组  0:00 - 6:00",
    "上午组  6:00 - 12:00",
    "下午组  12:00 - 18:00",
    "夜晚组  18:00 - 24:00"
)

private const val MAX_ACCOUNTS_FOR_RS = 100

// Optimization #2: pure mapping extracted from item lambda
private fun gameVersionLabel(value: String?): String = when (value) {
    "0" -> "国服"
    "1" -> "国际服"
    "2" -> "私服"
    else -> ""
}

// Optimization #3: groups the three colour derivations into one call site
private data class AccountCellColors(val bg: Color, val border: Color, val text: Color)

private fun accountCellColors(isSelected: Boolean) = AccountCellColors(
    bg = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.10f),
    border = if (isSelected) AppColors.Primary else AppColors.PrimaryLight,
    text = if (isSelected) Color.White else AppColors.TextPrimary
)

// Optimization #4: eliminates the two identical TextAutoSize.StepBased(...) constructions
private fun stepBasedAutoSize(
    maxFontSize: TextUnit,
    minFontSize: TextUnit = 5.sp
) = TextAutoSize.StepBased(minFontSize = minFontSize, maxFontSize = maxFontSize, stepSize = 1.sp)

@Composable
fun RandomScheduling(lazyListState: LazyListState? = null) {
    val rsState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.RANDOM_SCHEDULING.key) {
        mutableStateOf("0")
    }
    val accountCountStr = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value ?: "2"
    val accountCount = accountCountStr.toIntOrNull()?.coerceAtLeast(1) ?: 2
    val tooManyAccounts = accountCount > MAX_ACCOUNTS_FOR_RS

    // If account count exceeds the limit, force-disable the feature
    if (tooManyAccounts && rsState.value == "1") {
        rsState.value = "0"
    }

    val isEnabled = rsState.value == "1"

    Text(
        text = "（该功能制作中，未完善，请勿使用）",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    if (tooManyAccounts) {
        Text(
            text = "账号数量过多，本功能最多支持${MAX_ACCOUNTS_FOR_RS}个账号。请前往“账号设置”页面调整账号数量。",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    } else {
        SettingSwitchIcon(
            key = GLOBAL_SETTINGS.RANDOM_SCHEDULING.key,
            explain = "启用随机上号后，脚本将根据当前时间段，从对应组的账号中随机选取顺序依次运行。每个账号运行完毕后，根据设定的概率决定是否继续对战或直接下线，并按对战时间比例计算等待时间后再运行下一个账号。\n\n随机上号与批量创号不能同时启用。",
            afterChange = { checked ->
                if (checked) {
                    // Mutual exclusion: disable batch create when random scheduling is enabled
                    GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value = "0"
                }
            }
        )

        AnimatedVisibility(visible = isEnabled) {
            Column {
                GROUP_KEYS.forEachIndexed { index, keys ->
                    TimeGroupSection(
                        title = GROUP_TITLES[index],
                        keys = keys,
                        lazyListState = lazyListState
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeGroupSection(title: String, keys: GroupKeys, lazyListState: LazyListState? = null) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(top = 4.dp)) {
        SectionBanner(title = title, isExpanded = expanded, onClick = { expanded = !expanded })

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {

                // Battle time ratio
                SettingInputRow(key = keys.battleRatio, suffix = "%")

                // Stop probability row
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    SettingInputRow(key = keys.stopProb, topPadding = 0.dp, suffix = "%")
                    Text(
                        text = "剩余概率随机持续对战",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                // Continue battle range row
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp)
                ) {

                    SettingInputRow(key = keys.continueMin, topPadding = 0.dp, suffix = "至")
                    SettingInputRow(key = keys.continueMax, topPadding = 0.dp, suffix = "分钟后下线")
                }

                // No worker / full resources toggles
                SettingSwitchIcon(key = keys.noWorker)
                SettingSwitchIcon(key = keys.fullRes)

                // Account selector
                AccountGroupSelector(accountsKey = keys.accounts, lazyListState = lazyListState)
            }
        }
    }
}

@Composable
private fun AccountGroupSelector(accountsKey: String, lazyListState: LazyListState? = null) {
    val accountsState = GlobalVars.configStates.getOrPut(accountsKey) { mutableStateOf("") }
    val accountCountStr = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value ?: "2"
    val accountCount = accountCountStr.toIntOrNull()?.coerceAtLeast(1) ?: 2

    // Optimization #7: hoist parseSelected to a remembered lambda — no local fun repeated 3×
    val parseSelected: () -> Set<Int> = remember(accountsState) {
        {
            accountsState.value
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
        }
    }

    // Drag-to-select state
    var isDragging by remember { mutableStateOf(false) }
    var dragSelectMode by remember { mutableStateOf(true) } // true = select, false = deselect
    var dragPreview by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val boxBoundsMap = remember { mutableMapOf<Int, Rect>() }
    var flowRowOffset by remember { mutableStateOf(Offset.Zero) }

    // Screen height in pixels for edge-zone detection
    val screenHeightPx = LocalWindowInfo.current.containerSize.height.toFloat()

    // Coroutine scope for auto-scroll
    val scope = rememberCoroutineScope()

    // Reset drag state when not dragging to keep preview clean
    val displayedSelected = if (isDragging) dragPreview else parseSelected()

    // Invalidate cached box bounds whenever account count changes
    SideEffect {
        if (boxBoundsMap.keys.any { it !in 1..accountCount }) {
            boxBoundsMap.keys.toList().forEach { key ->
                if (key !in 1..accountCount) boxBoundsMap.remove(key)
            }
        }
    }

    Column(modifier = Modifier.padding(top = 6.dp)) {
        Text(
            text = "选择加入本组的账号：",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "长按账号后拖动可批量选择。",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            CustomButton(
                text = "一键全选",
                marginTop = 0.dp,
                onClick = {
                    val allIds = (1..accountCount).toList()
                    accountsState.value = allIds.joinToString(",")
                }
            )
            CustomButton(
                text = "一键反选",
                marginTop = 0.dp,
                onClick = {
                    val current = parseSelected()
                    val inverted = (1..accountCount).filter { it !in current }
                    accountsState.value = inverted.joinToString(",")
                }
            )
        }
        // Cell size and spacing constants (must match LazyVerticalGrid below)
        val cellSizeDp = 52.dp
        val spacingDp = 4.dp

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnsCount = maxOf(1, ((maxWidth + spacingDp) / (cellSizeDp + spacingDp)).toInt())
            val rowCount = (accountCount + columnsCount - 1) / columnsCount
            val gridHeight = cellSizeDp * rowCount + spacingDp * (rowCount - 1).coerceAtLeast(0)

            // Wrapping Box carries the onGloballyPositioned + pointerInput so the
            // LazyVerticalGrid (userScrollEnabled=false) never intercepts gestures.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight)
                    .onGloballyPositioned { layoutCoordinates ->
                        val posInRoot = layoutCoordinates.positionInRoot()
                        flowRowOffset = Offset(posInRoot.x, posInRoot.y)
                    }
                    .pointerInput(accountCount) {
                        // Touch slop in pixels: ~10dp
                        val slopPx = 10f * density
                        // Edge zone: 120dp from top/bottom triggers auto-scroll
                        val edgeZonePx = 120f * density
                        // Auto-scroll speed: pixels per tick (every 16ms)
                        val scrollSpeedPx = 40f * density

                        // currentDragRootY/X hold the latest finger position in root coordinates,
                        // updated each pointer event so the scroll coroutine always reads fresh data.
                        var currentDragRootY = 0f
                        var currentDragRootX = 0f

                        // Optimization #5: collapsed symmetric top/bottom scroll branches into one launcher
                        // onTick runs after each scroll step so hit-testing works while the finger is stationary
                        fun launchScrollJob(direction: Float, depthFn: () -> Float, onTick: () -> Unit): Job =
                            scope.launch {
                                while (true) {
                                    lazyListState!!.animateScrollBy(direction * scrollSpeedPx * (1f + depthFn()))
                                    onTick()
                                }
                            }

                        // Starts or stops the auto-scroll coroutine based on drag Y position.
                        // onScrollTick is invoked on each scroll step (used to hit-test while finger is stationary).
                        // Returns the active Job (or null if no scroll needed).
                        fun updateAutoScroll(rootY: Float, currentJob: Job?, onScrollTick: () -> Unit = {}): Job? {
                            currentDragRootY = rootY
                            val inTopZone = rootY < edgeZonePx
                            val inBottomZone = rootY > screenHeightPx - edgeZonePx
                            return when {
                                inBottomZone && lazyListState != null -> {
                                    if (currentJob?.isActive == true) currentJob
                                    else launchScrollJob(1f, {
                                        ((currentDragRootY - (screenHeightPx - edgeZonePx)) / edgeZonePx).coerceIn(0f, 1f)
                                    }, onScrollTick)
                                }

                                inTopZone && lazyListState != null -> {
                                    if (currentJob?.isActive == true) currentJob
                                    else launchScrollJob(-1f, {
                                        ((edgeZonePx - currentDragRootY) / edgeZonePx).coerceIn(0f, 1f)
                                    }, onScrollTick)
                                }

                                else -> {
                                    currentJob?.cancel()
                                    null
                                }
                            }
                        }

                        awaitEachGesture {
                            // Wait for initial finger down without consuming
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downPos = down.position

                            // Long-press detection: wait up to 400ms; if finger moves beyond slop first, abort
                            val longPressConfirmed = withTimeoutOrNull(400L) {
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
                                // If we exited because of movement, return non-null to signal "moved"
                                if (moved) Unit else null
                            }

                            // longPressConfirmed == null means timeout elapsed without movement = long press
                            if (longPressConfirmed == null) {
                                // Optimization #6: extracted coordinate conversion to avoid duplication below
                                fun toRootOffset(pos: Offset) =
                                    Offset(pos.x + flowRowOffset.x, pos.y + flowRowOffset.y)

                                val rootPos = toRootOffset(downPos)

                                val hitId = hitTestBox(rootPos, boxBoundsMap)
                                if (hitId != null) {
                                    GlobalVars.isAutoRunEnabled = false
                                    val baseSelected = parseSelected()
                                    // Determine drag mode: if pressed box was selected -> deselect mode, else -> select mode
                                    dragSelectMode = hitId !in baseSelected
                                    val initial = baseSelected.toMutableSet()
                                    if (dragSelectMode) initial.add(hitId) else initial.remove(hitId)
                                    isDragging = true
                                    dragPreview = initial

                                    // Seed root coords so the tick lambda has valid values from the first scroll step
                                    currentDragRootX = rootPos.x
                                    currentDragRootY = rootPos.y

                                    // currentIdHolder[0] is shared between the event loop and the scroll-tick
                                    // lambda so both always see the latest "last hit" cell.
                                    val currentIdHolder = intArrayOf(hitId)

                                    // Tick lambda: re-run hit-test at the stationary finger position after
                                    // each scroll step so cells scrolling under the finger get selected.
                                    val scrollTickFn: () -> Unit = {
                                        val tickPos = Offset(currentDragRootX, currentDragRootY)
                                        val tickId = hitTestBox(tickPos, boxBoundsMap)
                                        if (tickId != null && tickId != currentIdHolder[0]) {
                                            currentIdHolder[0] = tickId
                                            val updated = dragPreview.toMutableSet()
                                            if (dragSelectMode) updated.add(tickId) else updated.remove(tickId)
                                            dragPreview = updated
                                        }
                                    }

                                    // Immediately start auto-scroll if finger is already in an edge zone,
                                    // so scrolling begins even if the user never moves their finger.
                                    var autoScrollJob: Job? = updateAutoScroll(rootPos.y, null, scrollTickFn)

                                    // Consume subsequent events so scroll doesn't interfere
                                    var event = awaitPointerEvent()
                                    while (event.changes.any { it.pressed }) {
                                        event.changes.forEach { it.consume() }
                                        val change = event.changes.firstOrNull()
                                        if (change != null) {
                                            val movePos = toRootOffset(change.position)
                                            val moveRootY = movePos.y
                                            currentDragRootX = movePos.x

                                            // Update auto-scroll based on current finger Y in root coords
                                            autoScrollJob = updateAutoScroll(moveRootY, autoScrollJob, scrollTickFn)

                                            val newId = hitTestBox(movePos, boxBoundsMap)
                                            if (newId != null && newId != currentIdHolder[0]) {
                                                currentIdHolder[0] = newId
                                                val updated = dragPreview.toMutableSet()
                                                if (dragSelectMode) updated.add(newId) else updated.remove(newId)
                                                dragPreview = updated
                                            }
                                        }
                                        event = awaitPointerEvent()
                                    }

                                    // Drag ended — stop auto-scroll and commit the preview state
                                    autoScrollJob?.cancel()
                                    accountsState.value = dragPreview.sorted().joinToString(",")
                                    isDragging = false
                                    dragPreview = emptySet()
                                }
                            }
                            // If longPressConfirmed != null (finger moved before 400ms), do nothing —
                            // events are unconsumed so the parent scroll container handles the swipe
                        }
                    }
            ) {
                val accountIds = remember(accountCount) { (1..accountCount).toList() }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnsCount),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(gridHeight),
                    horizontalArrangement = Arrangement.spacedBy(spacingDp),
                    verticalArrangement = Arrangement.spacedBy(spacingDp)
                ) {
                    items(accountIds, key = { it }) { id ->
                        val isSelected = id in displayedSelected
                        val colors = accountCellColors(isSelected)   // Optimization #3
                        val versionLabel = gameVersionLabel(          // Optimization #2
                            GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}$id"]?.value
                        )

                        Box(
                            modifier = Modifier
                                .width(cellSizeDp)
                                .height(cellSizeDp)
                                .background(colors.bg, RoundedCornerShape(8.dp))
                                .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                                .padding(horizontal = 2.dp)
                                .onGloballyPositioned { layoutCoordinates ->
                                    val posInRoot = layoutCoordinates.positionInRoot()
                                    val size = layoutCoordinates.size
                                    boxBoundsMap[id] = Rect(
                                        Offset(posInRoot.x, posInRoot.y),
                                        Size(
                                            size.width.toFloat(),
                                            size.height.toFloat()
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = id.toString(),
                                    color = colors.text,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1,
                                    autoSize = stepBasedAutoSize(  // Optimization #4
                                        maxFontSize = MaterialTheme.typography.labelMedium.fontSize,
                                        minFontSize = 6.sp
                                    )
                                )
                                if (versionLabel.isNotEmpty()) {
                                    Text(
                                        text = versionLabel,
                                        color = colors.text,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        autoSize = stepBasedAutoSize(  // Optimization #4
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

// Hit-test: find which account box (if any) contains the given root-coordinate point
private fun hitTestBox(
    rootPosition: Offset,
    boxBounds: Map<Int, Rect>
): Int? {
    for ((id, rect) in boxBounds) {
        if (rootPosition.x in rect.left..rect.right &&
            rootPosition.y in rect.top..rect.bottom
        ) {
            return id
        }
    }
    return null
}
