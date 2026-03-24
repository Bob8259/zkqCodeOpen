package com.coc.zkqcode.core.ui.floatingwindows

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.localcomponents.LocalCustomAlertDialog
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.exit.AppExitHelper
import com.coc.zkqcode.core.util.exit.AppExitHelper.restoreDefaultInputMethod
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.delay

// Base DPI for consistent physical size across devices
private const val BASE_DPI = 320f

// Base icon size at DPI 320 (38dp)
private val BASE_ICON_SIZE_DP = 38.dp

/**
 * Calculate icon size that maintains consistent physical size across different DPI devices.
 * At DPI 320, the size will be 38dp. On other DPI devices, it scales proportionally.
 */
@Composable
fun getConsistentIconSize(): Dp {
    val density = LocalDensity.current
    val currentDpi = density.density * 160f
    val scaleFactor = BASE_DPI / currentDpi
    return BASE_ICON_SIZE_DP * scaleFactor
}

enum class ControlState {
    COLLAPSED, // State 1
    EXPANDED,  // State 2
    HIDDEN     // State 3
}

@Suppress("AssignedValueIsNeverRead")
@Composable
fun ControlWindow(
    externalInteractionCount: Int = 0,
    isAtRightSide: Boolean = false,
    currentX: Int = 0,
    currentY: Int = 0,
    screenWidth: Int = 0,
    screenHeight: Int = 0,
    onOpenMainUI: () -> Unit = {},
    onSwitchAccount: () -> Unit = {},
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDrag: (Float, Float) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var controlState by remember { mutableStateOf(ControlState.COLLAPSED) }
    var internalInteractionCount by remember { mutableIntStateOf(0) }
    var isPlaying by remember { GlobalVars.isPlaying }
    var showExitConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(internalInteractionCount, externalInteractionCount) {
        delay(2500)
        controlState = ControlState.HIDDEN
    }

    LaunchedEffect(externalInteractionCount) {
        if (externalInteractionCount > 0) {
            controlState = ControlState.COLLAPSED
        }
    }

    val iconSize = getConsistentIconSize()

    val mainIcon = remember {
        context.assets.open("main_icon.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    // Narrower icon (30% width) used in HIDDEN state to reduce touch area
    val mainIconHidden = remember {
        context.assets.open("main_icon_hidden.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    val playIcon = remember {
        context.assets.open("play.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    val pauseIcon = remember {
        context.assets.open("pause.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    val settingIcon = remember {
        context.assets.open("setting.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    val switchAccountIcon = remember {
        context.assets.open("switch.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    val exitIcon = remember {
        context.assets.open("exit.png").use {
            BitmapFactory.decodeStream(it).asImageBitmap()
        }
    }
    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { onDragStart() },
            onDragEnd = { onDragEnd() },
            onDrag = { change, dragAmount ->
                change.consume()
                onDrag(dragAmount.x, dragAmount.y)
            }
        )
    }

    Row(
        modifier = Modifier
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        // Use narrower size in HIDDEN state so the window shrinks and stops blocking touches
        val hiddenIconModifier = Modifier
            .width(iconSize * 0.3f)
            .height(iconSize)
            .padding(vertical = 4.dp)
        val normalIconModifier = Modifier
            .size(iconSize)
            .padding(2.dp)

        if (!isAtRightSide) {
            // Left side: Main icon first, use narrower hidden icon when HIDDEN
            Image(
                bitmap = if (controlState == ControlState.HIDDEN) mainIconHidden else mainIcon,
                contentDescription = "Main Icon",
                modifier = (if (controlState == ControlState.HIDDEN) hiddenIconModifier else normalIconModifier)
                    .then(dragModifier)
                    .clickable {
                        // Pause when expanding from COLLAPSED
                        val wasCollapsed = controlState == ControlState.COLLAPSED
                        controlState = when (controlState) {
                            ControlState.HIDDEN -> ControlState.COLLAPSED
                            ControlState.COLLAPSED -> ControlState.EXPANDED
                            ControlState.EXPANDED -> ControlState.COLLAPSED
                        }
                        if (wasCollapsed) {
                            ShowMessage("检测到悬浮窗展开，辅助已自动暂停，避免干扰用户操作", false)
                            isPlaying = false
                        }
                        internalInteractionCount++
                    }
            )

            if (controlState == ControlState.EXPANDED) {
                Image(
                    bitmap = exitIcon,
                    contentDescription = "Exit",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            // exit the application
                            showExitConfirmation = true
                        }
                )
                Image(
                    bitmap = settingIcon,
                    contentDescription = "Setting",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            AppStateManager.setMode(AppMode.Main)
                            restoreDefaultInputMethod()
                            onOpenMainUI()
                        }
                )
                Image(
                    bitmap = switchAccountIcon,
                    contentDescription = "Switch",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            AppStateManager.setMode(AppMode.SwitchAccount)
                            restoreDefaultInputMethod()
                            onSwitchAccount()
                        }
                )
                Image(
                    bitmap = if (isPlaying) pauseIcon else playIcon,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            isPlaying = !isPlaying
                            if (!isPlaying) {
                                ShowMessage("暂停中，请稍后...")
                                restoreDefaultInputMethod()
                                ShowMessage("已暂停")
                            }
                        }
                )
            }
        } else {
            // Right side: Play/Pause, Switch, Setting, then Main Icon
            if (controlState == ControlState.EXPANDED) {
                Image(
                    bitmap = if (isPlaying) pauseIcon else playIcon,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            isPlaying = !isPlaying
                            if (!isPlaying) {
                                ShowMessage("暂停中，请稍后...")
                                restoreDefaultInputMethod()
                                ShowMessage("已暂停")
                            }
                        }
                )
                Image(
                    bitmap = switchAccountIcon,
                    contentDescription = "Switch",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            AppStateManager.setMode(AppMode.SwitchAccount)
                            restoreDefaultInputMethod()
                            onSwitchAccount()
                        }
                )
                Image(
                    bitmap = settingIcon,
                    contentDescription = "Setting",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            AppStateManager.setMode(AppMode.Main)
                            restoreDefaultInputMethod()
                            onOpenMainUI()
                        }
                )
                Image(
                    bitmap = exitIcon,
                    contentDescription = "Exit",
                    modifier = Modifier
                        .size(iconSize)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            // exit the application
                            showExitConfirmation = true
                        }
                )
            }

            // Right side: use narrower hidden icon when HIDDEN
            Image(
                bitmap = if (controlState == ControlState.HIDDEN) mainIconHidden else mainIcon,
                contentDescription = "Main Icon",
                modifier = (if (controlState == ControlState.HIDDEN) hiddenIconModifier else normalIconModifier)
                    .then(dragModifier)
                    .clickable {
                        // Pause when expanding from COLLAPSED
                        val wasCollapsed = controlState == ControlState.COLLAPSED
                        controlState = when (controlState) {
                            ControlState.HIDDEN -> ControlState.COLLAPSED
                            ControlState.COLLAPSED -> ControlState.EXPANDED
                            ControlState.EXPANDED -> ControlState.COLLAPSED
                        }
                        if (wasCollapsed) {
                            ShowMessage("检测到悬浮窗展开，辅助已自动暂停，避免干扰用户操作", false)
                            isPlaying = false
                        }
                        internalInteractionCount++
                    }
            )
        }
    }

    if (showExitConfirmation) {
        LocalCustomAlertDialog(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    // anchorBounds is the position of the ControlWindow icons relative to the floating window
                    // windowSize is the size of the floating window (which is WRAP_CONTENT, so it fits the icons)

                    // We want to center the popup on the SCREEN
                    // Popup is relative to the anchor (the top-left of the Row in ControlWindow)
                    // The anchor's screen position is (currentX, currentY)

                    val screenCenterX = screenWidth / 2
                    val screenCenterY = screenHeight / 2

                    val targetX = screenCenterX - currentX - popupContentSize.width / 2
                    val targetY = screenCenterY - currentY - popupContentSize.height / 2

                    return IntOffset(targetX, targetY)
                }
            },
            onDismissRequest = { showExitConfirmation = false },
            title = {
                Text(
                    text = "退出提示",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "确认要退出吗？",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Row {
                    TextButton(onClick = { showExitConfirmation = false }) {
                        Text("取消")
                    }
                    TextButton(onClick = {
                        showExitConfirmation = false
                        AppExitHelper.exitApplication(context)
                    }) {
                        Text("确认")
                    }
                }
            }
        )
    }
}