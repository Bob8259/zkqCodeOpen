package com.coc.zkqcode.jar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.scale
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
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay


@Composable
fun InputRowWithCheckBox(
    checkBoxKey: String, inputKey: String
) {
    // Use getOrPut to lazily initialize missing config keys with their Schema defaults
    val checkBoxState = GlobalVars.configStates.getOrPut(checkBoxKey) {
        mutableStateOf(Schema.getDefaultValue(checkBoxKey))
    }
    val inputState = GlobalVars.configStates.getOrPut(inputKey) {
        mutableStateOf(Schema.getDefaultValue(inputKey))
    }

    val checkBoxLabel = Schema.getDisplayName(checkBoxKey)
    val inputLabel = Schema.getDisplayName(inputKey)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomSwitch(
            text = checkBoxLabel,
            checkedState = checkBoxState.value,
            onCheckStateChange = { checked ->
                GlobalVars.isAutoRunEnabled = false
                checkBoxState.value = if (checked) "1" else "0"
            },
        )
        BasicTextField(
            value = inputState.value, onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                inputState.value = newValue
            }, modifier = Modifier
                .padding(end = 6.dp, top = 6.dp, start = 6.dp)
                .background(
                    color = AppColors.CardBg, shape = RoundedCornerShape(4.dp)
                )
                .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp))
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState())
        )
        if (inputLabel.isNotEmpty()) {
            Text(
                text = inputLabel, modifier = Modifier
                    .padding(start = 2.dp, top = 7.dp)
                    .align(Alignment.CenterVertically), style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit, title: @Composable (() -> Unit)? = null, text: @Composable (() -> Unit)? = null, confirmButton: @Composable () -> Unit
) {
    Popup(
        popupPositionProvider = WindowCenterPositionProvider(), onDismissRequest = onDismissRequest, properties = PopupProperties(focusable = true)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE8EAED),
            shadowElevation = 8.dp
        ) {
            Column {
                // Orange gradient header for 注意事项 warning style
                title?.let {
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFf9a825), Color(0xFFfbbc05))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(gradient)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        it()
                    }
                }
                // Body content
                text?.let {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        it()
                    }
                }
                // Confirm button row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    confirmButton()
                }
            }
        }
    }
}

@Composable
fun CustomNotificationWindow(
    message: String, onDismissRequest: () -> Unit
) {
    LaunchedEffect(message) {
        delay(2000)
        onDismissRequest()
    }

    Popup(

        alignment = Alignment.Center, onDismissRequest = onDismissRequest, properties = PopupProperties(focusable = false)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp), shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp
        ) {
            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

class WindowCenterPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize
    ): IntOffset {
        val windowCenter = IntOffset(
            windowSize.width / 2, windowSize.height / 2
        )
        return IntOffset(
            windowCenter.x - popupContentSize.width / 2, windowCenter.y - popupContentSize.height / 2
        )
    }
}

// Positions a popup directly below its anchor, aligned to the anchor's left edge.
class BelowAnchorPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val x = anchorBounds.left.coerceAtMost(windowSize.width - popupContentSize.width)
        val y = anchorBounds.bottom
        return IntOffset(x, y)
    }
}

@Composable
fun SettingInputRow(
    key: String, topPadding: Dp = 6.dp, labelVerticalOffset: Dp = 0.dp, afterChange: ((String) -> Unit)? = null
) {
    // Use getOrPut to lazily initialize missing config keys with their Schema defaults
    val state = GlobalVars.configStates.getOrPut(key) {
        mutableStateOf(Schema.getDefaultValue(key))
    }
    val label = Schema.getDisplayName(key)

    Row(
        modifier = Modifier.padding(top = topPadding), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, modifier = Modifier
                .padding(end = 8.dp)
                .offset(y = labelVerticalOffset)
                .align(Alignment.CenterVertically), style = MaterialTheme.typography.labelMedium
        )

        BasicTextField(
            value = state.value, onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                state.value = newValue
                afterChange?.invoke(newValue)
            }, modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = AppColors.CardBg, shape = RoundedCornerShape(4.dp)
                )
                .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp))
                .padding(4.dp)
                .align(Alignment.CenterVertically)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

// Variant of SettingInputRow that appends a suffix text after the input field,
// allowing sentence-style labels such as: 持续检测 [xx] 秒钟
@Composable
fun SettingInputRowWithSuffix(key: String, suffix: String, afterChange: ((String) -> Unit)? = null) {
    val state = GlobalVars.configStates.getOrPut(key) {
        mutableStateOf(Schema.getDefaultValue(key))
    }
    val label = Schema.getDisplayName(key)

    Row(
        modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label, modifier = Modifier.padding(end = 8.dp), style = MaterialTheme.typography.labelMedium
        )

        BasicTextField(
            value = state.value, onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                state.value = newValue
                afterChange?.invoke(newValue)
            }, modifier = Modifier
                .padding(end = 6.dp)
                .background(
                    color = AppColors.CardBg, shape = RoundedCornerShape(4.dp)
                )
                .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp))
                .padding(4.dp)
                .heightIn(max = 120.dp)
                .verticalScroll(rememberScrollState())
        )

        Text(
            text = suffix, modifier = Modifier.padding(end = 8.dp), style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    explain: String? = null,
    marginTop: Dp = 6.dp,
    marginBottom: Dp = 0.dp,
    containerColor: Color = AppColors.Primary,
) {

    var showExplanation by remember { mutableStateOf(false) }
    Row(modifier = Modifier.padding(top = marginTop, bottom = marginBottom, end = 8.dp)) {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                onClick()
            }, modifier = Modifier.height(32.dp), shape = RoundedCornerShape(8.dp), enabled = enable, colors = ButtonDefaults.buttonColors(
                containerColor = containerColor
            )
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
        Column(Modifier.padding(top = 8.dp)) {
            explain?.let {
                Text(
                    text = "\u2139\uFE0F",
                    modifier = Modifier
                        .height(18.dp)
                        .clickable { showExplanation = true },
                    style = MaterialTheme.typography.labelMedium
                )

                if (showExplanation) {
                    CustomAlertDialog(onDismissRequest = { showExplanation = false }, title = {
                        Text(
                            text = "注意事项", style = MaterialTheme.typography.titleMedium, color = Color.White
                        )
                    }, text = {
                        Text(
                            text = explain, style = MaterialTheme.typography.bodyMedium
                        )
                    }, confirmButton = {
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
fun SettingSwitchIcon(
    key: String, explain: String? = null, textTopPadding: Dp = 0.dp, afterChange: ((Boolean) -> Unit)? = null
) {
    // If key is absent from configStates, create a new state using the Schema default value
    // and register it so subsequent reads are consistent.
    val state = GlobalVars.configStates.getOrPut(key) {
        mutableStateOf(Schema.getDefaultValue(key))
    }

    CustomSwitch(
        text = Schema.getDisplayName(key), checkedState = state.value, onCheckStateChange = { checked ->
            state.value = if (checked) "1" else "0"
            afterChange?.invoke(checked)
        }, explain = explain, textTopPadding = textTopPadding
    )
}

// Lighter blue for research/building toggle buttons to distinguish from action buttons
private val ToggleButtonBlue = AppColors.PrimaryLight

// Selected toggle buttons keep white text on blue backgrounds
private val EnabledSettingToggleTextColor = Color(0xFFFFFFFF)

// Unselected toggle buttons use black text for clearer contrast
private val DisabledSettingToggleTextColor = AppColors.TextPrimary

// Single toggle text button for research/building item lists
@Composable
fun SettingToggleButton(key: String) {
    val state = GlobalVars.configStates.getOrPut(key) {
        mutableStateOf(Schema.getDefaultValue(key))
    }
    val label = Schema.getDisplayName(key)
    val isEnabled = state.value == "1"

    if (isEnabled) {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                state.value = "0"
            }, modifier = Modifier
                .padding(2.dp)
                .height(32.dp), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp), colors = ButtonDefaults.buttonColors(
                containerColor = ToggleButtonBlue, contentColor = EnabledSettingToggleTextColor, disabledContentColor = EnabledSettingToggleTextColor
            )
        ) {
            Text(label, color = EnabledSettingToggleTextColor, style = MaterialTheme.typography.labelMedium)
        }
    } else {
        OutlinedButton(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                state.value = "1"
            }, modifier = Modifier
                .padding(2.dp)
                .height(32.dp), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp), colors = ButtonDefaults.outlinedButtonColors(
                contentColor = DisabledSettingToggleTextColor, disabledContentColor = DisabledSettingToggleTextColor
            )
        ) {
            Text(label, color = DisabledSettingToggleTextColor, style = MaterialTheme.typography.labelMedium)
        }
    }
}

// Shared switch row that always places the toggle before the label content.
@Composable
private fun CustomSwitch(
    text: String, checkedState: String, onCheckStateChange: (Boolean) -> Unit, explain: String? = null, textTopPadding: Dp = 0.dp
) {
    var showExplanation by remember { mutableStateOf(false) }
    val isChecked = checkedState == "1"
    Row(
        modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        // Keep the compact toggle first so all setting rows share the same visual order.
        Switch(
            checked = isChecked, onCheckedChange = { checked ->
                GlobalVars.isAutoRunEnabled = false
                onCheckStateChange(checked)
            }, modifier = Modifier
                .height(18.dp)
                .scale(0.6f)
                .requiredWidth(34.dp), colors = SwitchDefaults.colors(
                checkedTrackColor = AppColors.Success, checkedThumbColor = Color.White
            )
        )
        // Inline the label row so the full switch layout stays in one place.
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .padding(end = 4.dp)
                .clickable {
                    GlobalVars.isAutoRunEnabled = false
                    onCheckStateChange(!isChecked)
                }) {
            Text(
                text = text, modifier = Modifier.padding(top = textTopPadding), style = MaterialTheme.typography.labelMedium
            )

            explain?.let {
                Text(
                    text = "\u2139\uFE0F",
                    modifier = Modifier
                        .height(18.dp)
                        .clickable { showExplanation = true }
                        .padding(end = 2.dp),
                    style = MaterialTheme.typography.labelMedium
                )

                if (showExplanation) {
                    CustomAlertDialog(onDismissRequest = { showExplanation = false }, title = {
                        Text(
                            text = "注意事项", style = MaterialTheme.typography.titleMedium, color = Color.White
                        )
                    }, text = {
                        Text(
                            text = explain, style = MaterialTheme.typography.bodyMedium
                        )
                    }, confirmButton = {
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

// Rounded-corner white card for grouping related settings, with built-in AnimatedVisibility support
@Composable
fun SettingSection(
    modifier: Modifier = Modifier, visible: Boolean = true, content: @Composable () -> Unit
) {
    AnimatedVisibility(visible = visible) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp, tonalElevation = 5.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingDropdown(
    key: String, options: List<String>, afterChange: ((Int) -> Unit)? = null
) {
    // Use getOrPut to lazily initialize missing config keys with their Schema defaults
    val state = GlobalVars.configStates.getOrPut(key) {
        mutableStateOf(Schema.getDefaultValue(key))
    }
    val label = Schema.getDisplayName(key)

    var expanded by remember { mutableStateOf(false) }

    val selectedIndex = state.value.toIntOrNull() ?: 0
    val selectedOption = options.getOrElse(selectedIndex) { options.getOrNull(0) ?: "" }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .padding(top = 6.dp)
            .padding(end = 6.dp)
    ) {
        Text(
            text = label, textAlign = TextAlign.Center, modifier = Modifier.padding(end = 10.dp), style = MaterialTheme.typography.labelMedium
        )

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            // Trigger button
            Button(
                onClick = {
                    GlobalVars.isAutoRunEnabled = false
                    expanded = true
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text(selectedOption, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            // Modern popup panel with blue rounded-square option cards, anchored below the trigger
            if (expanded) {
                Popup(
                    popupPositionProvider = BelowAnchorPositionProvider(),
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.CardBg,
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .heightIn(max = 320.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            options.forEachIndexed { idx, option ->
                                val isSelected = idx == selectedIndex
                                val cardBg = if (isSelected)
                                    AppColors.Primary
                                else
                                    AppColors.PrimaryLight.copy(alpha = 0.10f)
                                val cardBorder = if (isSelected)
                                    AppColors.Primary
                                else
                                    AppColors.PrimaryLight
                                val textColor = if (isSelected)
                                    Color.White
                                else
                                    AppColors.TextPrimary

                                Box(
                                    modifier = Modifier
                                        .background(cardBg, RoundedCornerShape(8.dp))
                                        .border(1.dp, cardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            GlobalVars.isAutoRunEnabled = false
                                            state.value = idx.toString()
                                            expanded = false
                                            afterChange?.invoke(idx)
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = option,
                                        color = textColor,
                                        style = MaterialTheme.typography.labelMedium
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

// Full-width clickable blue gradient banner, matching the HTML .module-header style.
// Used as the collapse/expand toggle for each named section.
@Composable
fun SectionBanner(
    title: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(AppColors.PrimaryLight, AppColors.Primary)
    )
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(gradient, shape)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = if (isExpanded) "▼" else "▶",
            color = Color.White,
            style = MaterialTheme.typography.titleSmall
        )
    }
}