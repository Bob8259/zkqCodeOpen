@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SectionBanner
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors

// Data class holding the 5 config key names for one time group
private data class GroupKeys(
    val battleRatio: String,
    val stopProb: String,
    val continueMin: String,
    val continueMax: String,
    val noWorker: String,
    val fullRes: String,
    val accounts: String
)

private val GROUP_KEYS = listOf(
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_DAWN.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_DAWN.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_DAWN.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_DAWN.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_DAWN.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_DAWN.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_DAWN.key
    ),
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_MORNING.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_MORNING.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_MORNING.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_MORNING.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_MORNING.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_MORNING.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_MORNING.key
    ),
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_AFTERNOON.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_AFTERNOON.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_AFTERNOON.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_AFTERNOON.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_AFTERNOON.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_AFTERNOON.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_AFTERNOON.key
    ),
    GroupKeys(
        battleRatio = GLOBAL_SETTINGS.RS_BATTLE_RATIO_NIGHT.key,
        stopProb = GLOBAL_SETTINGS.RS_STOP_PROB_NIGHT.key,
        continueMin = GLOBAL_SETTINGS.RS_CONTINUE_MIN_NIGHT.key,
        continueMax = GLOBAL_SETTINGS.RS_CONTINUE_MAX_NIGHT.key,
        noWorker = GLOBAL_SETTINGS.RS_NO_WORKER_NIGHT.key,
        fullRes = GLOBAL_SETTINGS.RS_FULL_RES_NIGHT.key,
        accounts = GLOBAL_SETTINGS.RS_ACCOUNTS_NIGHT.key
    )
)

private val GROUP_TITLES = listOf(
    "凌晨组  0:00 - 6:00",
    "上午组  6:00 - 12:00",
    "下午组  12:00 - 18:00",
    "夜晚组  18:00 - 24:00"
)

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
                    keys = keys
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
                // Battle time ratio
                SettingInputRow(key = keys.battleRatio, suffix = "%")

                // Stop probability row
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    SettingInputRow(key = keys.stopProb, topPadding = 0.dp, suffix = "%")
                }

                // Continue battle range row
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = "剩余概率随机持续对战",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                    SettingInputRow(key = keys.continueMin, topPadding = 0.dp)
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                    SettingInputRow(key = keys.continueMax, topPadding = 0.dp)
                    Text(
                        text = "分钟后下线",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.CenterVertically)
                    )
                }

                // No worker / full resources toggles
                SettingSwitchIcon(key = keys.noWorker)
                SettingSwitchIcon(key = keys.fullRes)

                // Account selector
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

    // Parse current selected IDs from comma-separated string
    fun parseSelected(): Set<Int> {
        return accountsState.value
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }

    // Drag-to-select state
    var isDragging by remember { mutableStateOf(false) }
    var dragSelectMode by remember { mutableStateOf(true) } // true = select, false = deselect
    var dragPreview by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val boxBoundsMap = remember { mutableMapOf<Int, Rect>() }
    var flowRowOffset by remember { mutableStateOf(Offset.Zero) }

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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .onGloballyPositioned { layoutCoordinates ->
                    val posInRoot = layoutCoordinates.positionInRoot()
                    flowRowOffset = Offset(posInRoot.x, posInRoot.y)
                }
                .pointerInput(accountCount) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        // Convert local pointer position to root coordinates for hit-testing
                        val rootX = down.position.x + flowRowOffset.x
                        val rootY = down.position.y + flowRowOffset.y
                        val rootPos = Offset(rootX, rootY)

                        // Find which box was pressed
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

                            // Track drag movement
                            var currentId = hitId
                            do {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val moveRootX = change.position.x + flowRowOffset.x
                                    val moveRootY = change.position.y + flowRowOffset.y
                                    val movePos = Offset(moveRootX, moveRootY)
                                    val newId = hitTestBox(movePos, boxBoundsMap)
                                    if (newId != null && newId != currentId) {
                                        currentId = newId
                                        val updated = dragPreview.toMutableSet()
                                        if (dragSelectMode) updated.add(newId) else updated.remove(newId)
                                        dragPreview = updated
                                    }
                                }
                            } while (event.changes.any { it.pressed })

                            // Drag ended — commit the preview state
                            accountsState.value = dragPreview.sorted().joinToString(",")
                            isDragging = false
                            dragPreview = emptySet()
                        }
                    }
                }
        ) {
            for (id in 1..accountCount) {
                val isSelected = id in displayedSelected
                val bgColor = if (isSelected) AppColors.Primary else AppColors.Primary.copy(alpha = 0.10f)
                val borderColor = if (isSelected) AppColors.Primary else AppColors.PrimaryLight
                val textColor = if (isSelected) Color.White else AppColors.TextPrimary

                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .background(bgColor, RoundedCornerShape(8.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp)
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
                    Text(
                        text = id.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium
                    )
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
