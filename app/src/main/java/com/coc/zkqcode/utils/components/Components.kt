package com.coc.zkqcode.utils.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.utils.fileactions.FileActions
import com.coc.zkqcode.utils.theme.AppColors
import kotlinx.coroutines.delay


object GlobalVars {
    // Basic componeents
    var fileActions by mutableStateOf<FileActions?>(null)
    var pluginUI: MainCode? = null

    // Auto-run features
    var isAutoRunEnabled by mutableStateOf(true)
    var autoRunTimer by mutableIntStateOf(60)


    // Window positioning
    var absorbEdge by mutableIntStateOf(1) // 1: Left, 0: Right
    var absorbYPercentage by mutableIntStateOf(50) // Percentage of Y axis
    var updateWindowPosition by mutableStateOf(false)

    // Configuration States
    val configStates = mutableMapOf<String, MutableState<String>>()

    // IME management
    var defaultInputMethod: String? = null

    //Running state management
    var isPaused = false
}

@Composable
fun ExpandableContent(visible: Boolean, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (visible) {
            // 这里建议包一层 Column 以确保测量稳定
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun InputRowWithCheckBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    checkedState: String,
    onCheckedChange: (Boolean) -> Unit,
    checkBoxPath: String,
    inputPath: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomCheckBox(
            text = label,
            checkedState = checkedState,
            onCheckStateChange = {
                GlobalVars.isAutoRunEnabled = false
                onCheckedChange(it)
            },
        )
        BasicTextField(
            value = value, modifier = Modifier
                .padding(end = 16.dp, top = 6.dp, start = 6.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState()), onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                onValueChange(newValue)
            })
    }
}


@Composable
fun CustomAlertDialog(
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
fun InputRow(
    label: String, value: String, onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.labelMedium
        )
        BasicTextField(
            value = value, modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState()), onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                onValueChange(newValue)
            })
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    explain: String? = null,
    marginTop: Dp = 8.dp
) {

    var showExplanation by remember { mutableStateOf(false) }
    Row {
        Button(
            onClick = {
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
        Column(Modifier.padding(top = 14.dp)) {
            explain?.let {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Notes",
                    modifier = Modifier
                        .height(18.dp)
                        .clickable { showExplanation = true }
                        .padding(top = 2.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                if (showExplanation) {
                    CustomAlertDialog(
                        onDismissRequest = { showExplanation = false },
                        title = {
                            Text(
                                text = "注意事项",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        text = {
                            Text(
                                text = explain,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = { showExplanation = false }) {
                                Text("明白了")
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun CustomCheckBox(
    text: String,
    checkedState: String,
    onCheckStateChange: (Boolean) -> Unit,
    explain: String? = null
) {

    var showExplanation by remember { mutableStateOf(false) }

    Row(modifier = Modifier.padding(top = 6.dp)) {
        Checkbox(
            checked = checkedState == "1", onCheckedChange = { isChecked ->
                GlobalVars.isAutoRunEnabled = false
                onCheckStateChange(isChecked)

            }, modifier = Modifier
                .height(20.dp)
                .width(25.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = AppColors.Azure,
                checkmarkColor = Color.White
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                GlobalVars.isAutoRunEnabled = false
                val newCheckedState = checkedState != "1"
                onCheckStateChange(newCheckedState)

            }) {
            Text(
                text = text,
                modifier = Modifier.padding(top = if (explain != null) 2.dp else 4.dp),
                style = MaterialTheme.typography.labelMedium
            )

            explain?.let {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "注意事项",
                    modifier = Modifier
                        .height(18.dp)
                        .clickable { showExplanation = true }
                        .padding(end = 2.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))

                if (showExplanation) {
                    CustomAlertDialog(
                        onDismissRequest = { showExplanation = false },
                        title = {
                            Text(
                                text = "注意事项",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        text = {
                            Text(
                                text = explain,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = { showExplanation = false }) {
                                Text("明白了")
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun DropdownButton(
    options: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
    label: String
) {
    val selectedOption =
        if (selectedIndex in options.indices) options[selectedIndex] else options.getOrElse(0) { "" }
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.labelMedium
        )

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Azure
                )
            ) {
                Text(selectedOption, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { idx, option ->
                    DropdownMenuItem(text = {
                        Text(
                            option, style = MaterialTheme.typography.labelMedium
                        )
                    }, onClick = {
                        GlobalVars.isAutoRunEnabled = false
                        onValueChange(idx)
                        expanded = false
                    }, modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}