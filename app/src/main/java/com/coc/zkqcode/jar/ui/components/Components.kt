package com.coc.zkqcode.jar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
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
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.core.ui.theme.AppColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import kotlinx.coroutines.delay


@Composable
fun InputRowWithCheckBox(
    checkBoxKey: String,
    inputKey: String
) {
    val checkBoxState = GlobalVars.configStates[checkBoxKey]
        ?: logAndStop("Config: $checkBoxKey Not Found")
    val inputState = GlobalVars.configStates[inputKey]
        ?: logAndStop("Config: $inputKey Not Found")

    val checkBoxLabel = Schema.getDisplayName(checkBoxKey)
    val inputLabel = Schema.getDisplayName(inputKey)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomCheckBox(
            text = checkBoxLabel,
            checkedState = checkBoxState.value,
            onCheckStateChange = { checked ->
                GlobalVars.isAutoRunEnabled = false
                checkBoxState.value = if (checked) "1" else "0"
            },
        )
        BasicTextField(
            value = inputState.value,
            onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                inputState.value = newValue
            },
            modifier = Modifier
                .padding(end = 6.dp, top = 6.dp, start = 6.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState())
        )
        if (inputLabel.isNotEmpty()) {
            Text(
                text = inputLabel,
                modifier = Modifier
                    .padding(start = 2.dp, top = 7.dp)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.labelMedium
            )
        }
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
fun SettingInputRow(key: String, afterChange: ((String) -> Unit)? = null) {
    // Get state and label (display name)
    val state = GlobalVars.configStates[key]
        ?: logAndStop("Config: $key Not Found")
    val label = Schema.getDisplayName(key)

    Row(
        modifier = Modifier
            .padding(top = 2.dp)
            .padding(bottom = 4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.labelMedium
        )

        BasicTextField(
            value = state.value,
            onValueChange = { newValue ->
                // Automatically handle side effects and state updates
                GlobalVars.isAutoRunEnabled = false
                state.value = newValue
                afterChange?.invoke(newValue)
            },
            modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState())
        )
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
fun SettingCheckBox(
    key: String,
    explain: String? = null
) {
    val state = GlobalVars.configStates[key]
        ?: logAndStop("Config: $key Not Found")

    CustomCheckBox(
        text = Schema.getDisplayName(key),
        checkedState = state.value,
        onCheckStateChange = { checked ->
            state.value = if (checked) "1" else "0"
        },
        explain = explain
    )
}

@Composable
private fun CustomCheckBox(
    text: String,
    checkedState: String,
    onCheckStateChange: (Boolean) -> Unit,
    explain: String? = null
) {

    var showExplanation by remember { mutableStateOf(false) }

    Row(modifier = Modifier.padding(top = 4.dp)) {
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
                modifier = Modifier.padding(top = 2.dp),
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
fun SettingDropdown(
    key: String,
    options: List<String>
) {
    // 1. Get config state and display name
    val state = GlobalVars.configStates[key]
        ?: logAndStop("Config: $key Not Found")
    val label = Schema.getDisplayName(key)

    // 2. Internal UI state
    var expanded by remember { mutableStateOf(false) }

    // 3. Data conversion logic
    val selectedIndex = state.value.toIntOrNull() ?: 0
    val selectedOption = options.getOrElse(selectedIndex) { options.getOrNull(0) ?: "" }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(end = 10.dp),
            style = MaterialTheme.typography.labelMedium
        )

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            // Dropdown trigger button
            Button(
                onClick = {
                    GlobalVars.isAutoRunEnabled = false
                    expanded = true
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Azure)
            ) {
                Text(selectedOption, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { idx, option ->
                    DropdownMenuItem(
                        text = {
                            Text(option, style = MaterialTheme.typography.labelMedium)
                        },
                        onClick = {
                            GlobalVars.isAutoRunEnabled = false
                            state.value = idx.toString() // Update global state
                            expanded = false
                        },
                        modifier = Modifier.height(35.dp) // Slightly increase height for easier clicking
                    )
                }
            }
        }
    }
}