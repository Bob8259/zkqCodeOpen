@file:Suppress("AssignedValueIsNeverRead")

package com.coc.zkqcode.jar.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
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
import com.coc.zkqcode.jar.ui.Strings
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


// Hardcoded SVG info icon (circle with question mark), sourced from SVG Repo.
// viewBox: 0 0 496.158 496.158
private val InfoSvgIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "InfoIcon", defaultWidth = 18.dp, defaultHeight = 18.dp, viewportWidth = 496.158f, viewportHeight = 496.158f
    ).apply {
        // Cyan/teal circle background
        path(
            fill = SolidColor(Color(0xFF25B7D3)), fillAlpha = 1f, pathFillType = PathFillType.NonZero
        ) {
            moveTo(496.158f, 248.085f)
            curveTo(496.158f, 111.063f, 385.089f, 0.003f, 248.083f, 0.003f)
            curveTo(111.07f, 0.003f, 0f, 111.063f, 0f, 248.085f)
            curveTo(0f, 385.086f, 111.07f, 496.155f, 248.083f, 496.155f)
            curveTo(385.089f, 496.155f, 496.158f, 385.086f, 496.158f, 248.085f)
            close()
        }
        // White question mark shape
        path(
            fill = SolidColor(Color(0xFFFFFFFF)), fillAlpha = 1f, pathFillType = PathFillType.NonZero
        ) {
            moveTo(138.216f, 173.592f)
            curveTo(138.216f, 159.677f, 142.683f, 145.577f, 151.619f, 131.295f)
            curveTo(160.552f, 117.013f, 173.592f, 105.185f, 190.730f, 95.809f)
            curveTo(207.869f, 86.436f, 227.864f, 81.747f, 250.715f, 81.747f)
            curveTo(271.953f, 81.747f, 290.705f, 85.668f, 306.965f, 93.502f)
            curveTo(323.225f, 101.340f, 335.783f, 111.997f, 344.648f, 125.472f)
            curveTo(353.509f, 138.951f, 357.941f, 153.597f, 357.941f, 169.417f)
            curveTo(357.941f, 181.869f, 355.414f, 192.784f, 350.360f, 202.156f)
            curveTo(345.306f, 211.532f, 339.298f, 219.625f, 332.342f, 226.435f)
            curveTo(325.383f, 233.247f, 312.896f, 244.710f, 294.879f, 260.823f)
            curveTo(289.898f, 265.365f, 285.904f, 269.358f, 282.904f, 272.799f)
            curveTo(279.900f, 276.242f, 277.665f, 279.391f, 276.202f, 282.246f)
            curveTo(274.736f, 285.103f, 273.599f, 287.959f, 272.796f, 290.816f)
            curveTo(271.989f, 293.671f, 270.781f, 298.691f, 269.171f, 305.867f)
            curveTo(266.387f, 321.103f, 257.670f, 328.719f, 243.024f, 328.719f)
            curveTo(235.406f, 328.719f, 228.996f, 326.230f, 223.798f, 321.248f)
            curveTo(218.597f, 316.269f, 215.998f, 308.871f, 215.998f, 299.056f)
            curveTo(215.998f, 286.751f, 217.900f, 276.094f, 221.711f, 267.086f)
            curveTo(225.519f, 258.076f, 230.572f, 250.166f, 236.872f, 243.356f)
            curveTo(243.168f, 236.544f, 251.666f, 228.452f, 262.360f, 219.076f)
            curveTo(271.733f, 210.874f, 278.510f, 204.684f, 282.685f, 200.509f)
            curveTo(286.860f, 196.334f, 290.375f, 191.686f, 293.232f, 186.556f)
            curveTo(296.088f, 181.430f, 297.517f, 175.865f, 297.517f, 169.857f)
            curveTo(297.517f, 158.139f, 293.157f, 148.252f, 284.443f, 140.194f)
            curveTo(275.726f, 132.140f, 264.482f, 128.109f, 250.715f, 128.109f)
            curveTo(234.599f, 128.109f, 222.734f, 132.174f, 215.119f, 140.304f)
            curveTo(207.501f, 148.434f, 201.057f, 160.409f, 195.783f, 176.229f)
            curveTo(190.802f, 192.784f, 181.353f, 201.058f, 167.438f, 201.058f)
            curveTo(159.232f, 201.058f, 152.311f, 198.167f, 146.674f, 192.379f)
            curveTo(141.035f, 186.593f, 138.216f, 180.331f, 138.216f, 173.592f)
            close()
            moveTo(245.442f, 414.412f)
            curveTo(236.505f, 414.412f, 228.705f, 411.517f, 222.041f, 405.732f)
            curveTo(215.374f, 399.948f, 212.043f, 391.855f, 212.043f, 381.453f)
            curveTo(212.043f, 372.224f, 215.263f, 364.462f, 221.711f, 358.162f)
            curveTo(228.155f, 351.865f, 236.065f, 348.714f, 245.441f, 348.714f)
            curveTo(254.670f, 348.714f, 262.432f, 351.865f, 268.732f, 358.162f)
            curveTo(275.028f, 364.462f, 278.180f, 372.224f, 278.180f, 381.453f)
            curveTo(278.180f, 391.708f, 274.884f, 399.765f, 268.292f, 405.623f)
            curveTo(261.700f, 411.481f, 254.084f, 414.412f, 245.442f, 414.412f)
            close()
        }
    }.build()
}

// ─── Step 3: Private helper — eliminates repeated getOrPut boilerplate ────────
private fun rememberConfigState(key: String) = GlobalVars.configStates.getOrPut(key) { mutableStateOf(Schema.getDefaultValue(key)) }

// ─── Step 1: Internal composable — eliminates duplicated info-icon + alert block ──
@Composable
internal fun ExplainIcon(explain: String) {
    var showExplanation by remember { mutableStateOf(false) }
    Image(
        imageVector = InfoSvgIcon, contentDescription = "info", modifier = Modifier
            .size(18.dp)
            .clickable { showExplanation = true }
            .padding(end = 2.dp))
    if (showExplanation) {
        CustomAlertDialog(onDismissRequest = { showExplanation = false }, title = {
            Text(
                text = Strings.noticeTitle, style = MaterialTheme.typography.titleMedium, color = AppColors.TextPrimary
            )
        }, text = {
            Text(text = explain, style = MaterialTheme.typography.bodyMedium)
        }, confirmButton = {
            TextButton(onClick = { showExplanation = false }) {
                Text(Strings.gotIt, color = AppColors.Primary)
            }
        })
    }
}

// ─── Step 2: Private composable — eliminates repeated BasicTextField modifier chain ──
@Composable
private fun StyledBasicTextField(
    value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, endPadding: Dp = 6.dp
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .padding(end = endPadding)
            .widthIn(min = 50.dp)
            .width(IntrinsicSize.Min)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
            .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(4.dp))
            .padding(4.dp)
            .heightIn(max = 120.dp)
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun InputRowWithCheckBox(
    checkBoxKey: String, inputKey: String
) {
    val checkBoxState = rememberConfigState(checkBoxKey)
    val inputState = rememberConfigState(inputKey)

    val checkBoxLabel = Schema.getDisplayName(checkBoxKey)
    val inputLabel = Schema.getDisplayName(inputKey)

    FlowRow {
        CustomSwitch(
            text = checkBoxLabel,
            checkedState = checkBoxState.value,
            onCheckStateChange = { checked ->
                GlobalVars.isAutoRunEnabled = false
                checkBoxState.value = if (checked) "1" else "0"
            },
        )
        StyledBasicTextField(
            value = inputState.value, onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                inputState.value = newValue
            }, modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 6.dp, top = 6.dp), endPadding = 6.dp
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
        // Full-screen semi-transparent backdrop overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000))
                .clickable { onDismissRequest() }, contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .padding(16.dp)
                    .border(1.dp, AppColors.GlassBorder, RoundedCornerShape(24.dp))
                    // Consume clicks so they don't propagate to the backdrop
                    .clickable(enabled = false) {}, shape = RoundedCornerShape(24.dp), color = Color(0xF2FFFFFF), shadowElevation = 3.dp
            ) {
                Column {
                    // Title area — plain padding, no coloured header bar
                    title?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            it()
                        }
                    }
                    // Body content
                    text?.let {
                        Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                            it()
                        }
                    }
                    // Confirm button row — right-aligned, primary blue text
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.End
                    ) {
                        confirmButton()
                    }
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
        delay(2000.milliseconds)
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
        anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize
    ): IntOffset {
        val x = anchorBounds.left.coerceAtMost(windowSize.width - popupContentSize.width)
        val y = anchorBounds.bottom
        return IntOffset(x, y)
    }
}

// ─── Step 4: Merged SettingInputRow + SettingInputRowWithSuffix ───────────────
// The optional `suffix` parameter replaces the now-deleted SettingInputRowWithSuffix.
@Composable
fun SettingInputRow(
    key: String, topPadding: Dp = 6.dp, labelVerticalOffset: Dp = 0.dp, suffix: String? = null, minWidth: Dp? = null, afterChange: ((String) -> Unit)? = null
) {
    val state = rememberConfigState(key)
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

        StyledBasicTextField(
            value = state.value, onValueChange = { newValue ->
                GlobalVars.isAutoRunEnabled = false
                state.value = newValue
                afterChange?.invoke(newValue)
            }, modifier = Modifier
                .align(Alignment.CenterVertically)
                .then(if (minWidth != null) Modifier.widthIn(min = minWidth) else Modifier), endPadding = if (suffix != null) 2.dp else 16.dp
        )

        suffix?.let {
            Text(
                text = it, modifier = Modifier.padding(end = 8.dp), style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    enable: Boolean = true,
    explain: String? = null,
    marginTop: Dp = 6.dp,
    marginBottom: Dp = 0.dp,
    marginStart: Dp = 0.dp,
    marginEnd: Dp = 8.dp,
    containerColor: Color = AppColors.Primary,
    contentPadding: PaddingValues = if (icon != null) PaddingValues(horizontal = 6.dp, vertical = 0.dp) else PaddingValues(horizontal = 18.dp, vertical = 0.dp),
) {
    Row(modifier = Modifier.padding(top = marginTop, bottom = marginBottom, start = marginStart, end = marginEnd)) {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                onClick()
            }, modifier = if (icon != null) Modifier.size(32.dp) else Modifier.height(32.dp), shape = RoundedCornerShape(8.dp), enabled = enable, colors = ButtonDefaults.buttonColors(
                containerColor = containerColor
            ), contentPadding = contentPadding
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(20.dp))
            } else {
                Text(text, style = MaterialTheme.typography.labelMedium)
            }
        }
        Column(Modifier.padding(top = 8.dp)) {
            explain?.let { ExplainIcon(it) }
        }
    }
}

@Composable
fun SettingSwitchIcon(
    key: String, explain: String? = null, textTopPadding: Dp = 0.dp, afterChange: ((Boolean) -> Unit)? = null
) {
    // If key is absent from configStates, create a new state using the Schema default value
    // and register it so subsequent reads are consistent.
    val state = rememberConfigState(key)

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
fun SettingToggleButton(key: String, isEpic: Boolean = false) {
    val state = rememberConfigState(key)
    val label = Schema.getDisplayName(key)
    val isEnabled = state.value == "1"
    val buttonColor = if (isEpic) AppColors.Epic else ToggleButtonBlue

    if (isEnabled) {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                state.value = "0"
            }, modifier = Modifier
                .padding(2.dp)
                .height(32.dp), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp), colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor, contentColor = EnabledSettingToggleTextColor, disabledContentColor = EnabledSettingToggleTextColor
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

            explain?.let { ExplainIcon(it) }
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
                .padding(vertical = 4.dp)
                .border(1.5.dp, AppColors.GlassBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = AppColors.CardBg,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
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
    val state = rememberConfigState(key)
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
                    popupPositionProvider = BelowAnchorPositionProvider(), onDismissRequest = { expanded = false }, properties = PopupProperties(focusable = true)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp, modifier = Modifier.padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .heightIn(max = 320.dp)
                                .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            options.forEachIndexed { idx, option ->
                                val isSelected = idx == selectedIndex
                                val cardBg = if (isSelected) AppColors.Primary
                                else AppColors.PrimaryLight.copy(alpha = 0.10f)
                                val cardBorder = if (isSelected) AppColors.Primary
                                else AppColors.PrimaryLight
                                val textColor = if (isSelected) Color.White
                                else AppColors.TextPrimary

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
                                        .padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Text(
                                        text = option, color = textColor, style = MaterialTheme.typography.labelMedium
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
    title: String, isExpanded: Boolean, onClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(AppColors.Primary, AppColors.PrimaryLight)
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
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title, color = Color.White, style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = if (isExpanded) "▼" else "▶", color = Color.White, style = MaterialTheme.typography.titleSmall
        )
    }
}
