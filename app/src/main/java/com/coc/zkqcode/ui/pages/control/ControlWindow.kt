package com.coc.zkqcode.ui.pages

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Define a common icon size constant
val ICON_SIZE = 40.dp

enum class ControlState {
    COLLAPSED, // State 1
    EXPANDED,  // State 2
    HIDDEN     // State 3
}

@Composable
fun ControlWindow(
    externalInteractionCount: Int = 0,
    isAtRightSide: Boolean = false,
    onOpenMainUI: () -> Unit = {},
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDrag: (Float, Float) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var controlState by remember { mutableStateOf(ControlState.COLLAPSED) }
    var internalInteractionCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(internalInteractionCount, externalInteractionCount) {
        delay(2500)
        controlState = ControlState.HIDDEN
    }

    LaunchedEffect(externalInteractionCount) {
        if (externalInteractionCount > 0) {
            controlState = ControlState.COLLAPSED
        }
    }

    val mainIcon = remember {
        val inputStream = context.assets.open("main_icon.png")
        BitmapFactory.decodeStream(inputStream).asImageBitmap()
    }
    val playIcon = remember {
        val inputStream = context.assets.open("play.png")
        BitmapFactory.decodeStream(inputStream).asImageBitmap()
    }
    val pauseIcon = remember {
        val inputStream = context.assets.open("pause.png")
        BitmapFactory.decodeStream(inputStream).asImageBitmap()
    }
    val settingIcon = remember {
        val inputStream = context.assets.open("setting.png")
        BitmapFactory.decodeStream(inputStream).asImageBitmap()
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
            .wrapContentSize()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        if (!isAtRightSide) {
            // Left side: Main icon first
            Image(
                bitmap = mainIcon,
                contentDescription = "Main Icon",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .padding(4.dp)
                    .offset(x = if (controlState == ControlState.HIDDEN) (-20).dp else 0.dp)
                    .then(dragModifier)
                    .clickable {
                        controlState = when (controlState) {
                            ControlState.HIDDEN -> ControlState.COLLAPSED
                            ControlState.COLLAPSED -> ControlState.EXPANDED
                            ControlState.EXPANDED -> ControlState.COLLAPSED
                        }
                        internalInteractionCount++
                    }
            )

            if (controlState == ControlState.EXPANDED) {
                Image(
                    bitmap = settingIcon,
                    contentDescription = "Setting",
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            /* Handle setting */
                            onOpenMainUI()
                        }
                )
                Image(
                    bitmap = pauseIcon,
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            /* Handle pause */
                        }
                )
                Image(
                    bitmap = playIcon,
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            /* Handle play */
                        }
                )
            }
        } else {
            // Right side: Play, Pause, Setting, then Main Icon
            if (controlState == ControlState.EXPANDED) {
                Image(
                    bitmap = playIcon,
                    contentDescription = "Play",
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            /* Handle play */
                        }
                )
                Image(
                    bitmap = pauseIcon,
                    contentDescription = "Pause",
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            /* Handle pause */
                        }
                )
                Image(
                    bitmap = settingIcon,
                    contentDescription = "Setting",
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .padding(4.dp)
                        .clickable {
                            internalInteractionCount++
                            /* Handle setting */
                            onOpenMainUI()
                        }
                )
            }

            Image(
                bitmap = mainIcon,
                contentDescription = "Main Icon",
                modifier = Modifier
                    .size(ICON_SIZE)
                    .padding(4.dp)
                    .offset(x = if (controlState == ControlState.HIDDEN) (20).dp else 0.dp)
                    .then(dragModifier)
                    .clickable {
                        controlState = when (controlState) {
                            ControlState.HIDDEN -> ControlState.COLLAPSED
                            ControlState.COLLAPSED -> ControlState.EXPANDED
                            ControlState.EXPANDED -> ControlState.COLLAPSED
                        }
                        internalInteractionCount++
                    }
            )
        }
    }
}