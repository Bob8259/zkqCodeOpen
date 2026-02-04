package com.coc.zkqcode.core.ui.localcomponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun LocalCustomAlertDialog(
    onDismissRequest: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit
) {
    Popup(
        popupPositionProvider = WindowCenterPositionProvider(),
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                title?.let {
                    Box(modifier = Modifier.padding(bottom = 16.dp)) {
                        it()
                    }
                }
                text?.let {
                    Box(modifier = Modifier.padding(bottom = 24.dp)) {
                        it()
                    }
                }
                Row(modifier = Modifier.align(Alignment.End)) {
                    confirmButton()
                }
            }
        }
    }
}

@Composable
fun CustomNotificationWindow(
    message: String,
    onDismissRequest: () -> Unit
) {
    LaunchedEffect(message) {
        delay(2000)
        onDismissRequest()
    }

    Popup(

        alignment = Alignment.Center,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = false)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp
        ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

class WindowCenterPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val windowCenter = IntOffset(
            windowSize.width / 2,
            windowSize.height / 2
        )
        return IntOffset(
            windowCenter.x - popupContentSize.width / 2,
            windowCenter.y - popupContentSize.height / 2
        )
    }
}


@Composable
fun LocalCustomButton(
    text: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    marginTop: Dp = 8.dp
) {
    Row {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                onClick()
            },
            modifier = Modifier
                .padding(start = 8.dp, top = marginTop)
                .height(32.dp),
            shape = RoundedCornerShape(8.dp),
            enabled = enable,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Azure
            )
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}
