package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.BelowAnchorPositionProvider
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors

private fun updateConfigValue(key: String, value: String) {
    GlobalVars.configStates[key]?.let {
        it.value = value
    } ?: run {
        GlobalVars.configStates[key] = mutableStateOf(value)
    }
}

private fun batchUpdateByRange(startStr: String, endStr: String, keyPrefix: String, newValue: String) {
    val start = startStr.toIntOrNull()
    val end = endStr.toIntOrNull()
    if (start != null && end != null && start <= end) {
        for (i in start..end) {
            updateConfigValue("$keyPrefix$i", newValue)
        }
    }
}

private fun batchUpdateAllAccounts(keyPrefix: String, valueProducer: (String) -> String) {
    val count = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value?.toIntOrNull() ?: 3
    for (i in 1..count) {
        val key = "$keyPrefix$i"
        val currentValue = GlobalVars.configStates[key]?.value ?: "0"
        updateConfigValue(key, valueProducer(currentValue))
    }
}

@Composable
internal fun SmallTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    width: Dp = 40.dp,
) {
    BasicTextField(
        value = value,
        onValueChange = {
            GlobalVars.isAutoRunEnabled = false
            onValueChange(it)
        },
        modifier = modifier
            .width(width)
            .background(Color.White, RoundedCornerShape(4.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(4.dp)
    )
}

@Composable
internal fun NumberDropdown(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    width: Dp = 80.dp,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (options.contains(selectedValue)) selectedValue else label

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                expanded = true
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(30.dp).width(width),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelMedium
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

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
                        options.forEach { option ->
                            val isSelected = option == selectedValue
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
                                        onValueChange(option)
                                        expanded = false
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

fun LazyListScope.accountSettings() {
    val pinnedState = GlobalVars.configStates.getOrPut("_account_pinned") { mutableStateOf("0") }
    val isPinned = pinnedState.value == "1"
    item {
        SettingSection {
            SettingInputRow(
                key = GLOBAL_SETTINGS.ACCOUNT_COUNT.key,
                afterChange = { newValue ->
                    val newCount = newValue.toIntOrNull()
                    if (newCount != null) {
                        // Clamp to max 5000
                        val clamped = newCount.coerceAtMost(5000)
                        if (clamped != newCount) {
                            GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value = clamped.toString()
                        }
                        ConfigManager.expandAccountConfigs(clamped)
                    }
                }
            )
            Row {
                CustomButton(
                    text = Strings.selectAll,
                    marginTop = 6.dp,
                    onClick = { batchUpdateAllAccounts(ACCOUNT_SETTINGS.ISOPEN.key) { "1" } }
                )
                CustomButton(
                    text = Strings.invertSelection,
                    marginTop = 6.dp,
                    onClick = { batchUpdateAllAccounts(ACCOUNT_SETTINGS.ISOPEN.key) { if (it == "1") "0" else "1" } }
                )
                CustomButton(
                    text = if (isPinned) Strings.unpinEnabledAccounts else Strings.pinEnabledAccounts,
                    marginTop = 6.dp,
                    onClick = { pinnedState.value = if (isPinned) "0" else "1" }
                )
            }
            var configValue by remember { mutableStateOf("") }
            var startAccount by remember { mutableStateOf("") }
            var endAccount by remember { mutableStateOf("") }

            val accountCountForDropdown =
                (GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value?.toIntOrNull() ?: 3).coerceAtLeast(1)
            val accountNumberOptions = (1..accountCountForDropdown).map { it.toString() }

            val configCountForDropdown =
                (GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]?.value?.toIntOrNull() ?: 2).coerceAtLeast(1)
            val configNumberOptions = (1..configCountForDropdown).map { it.toString() }

            var batchVerStartAccount by remember { mutableStateOf("") }
            var batchVerEndAccount by remember { mutableStateOf("") }
            val verOptions = listOf(Strings.cnVersion, Strings.globalVersion, Strings.atrasis)
            var selectedVer by remember { mutableStateOf(verOptions[0]) }

            val startMethodOptions = listOf(Strings.gameSave, Strings.directLaunch, Strings.testMode)
            var selectedStartMethod by remember { mutableStateOf(startMethodOptions[0]) }

            FlowRow(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = Strings.applyConfigLabel,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    options = configNumberOptions,
                    selectedValue = configValue,
                    onValueChange = { configValue = it },
                    label = Strings.configLabel,
                    width = 70.dp,
                )
                Text(
                    text = Strings.toAccounts,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    options = accountNumberOptions,
                    selectedValue = startAccount,
                    onValueChange = { startAccount = it },
                    label = Strings.startAccountLabel,
                    width = 70.dp,
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp),
                    options = accountNumberOptions,
                    selectedValue = endAccount,
                    onValueChange = { endAccount = it },
                    label = Strings.endAccountLabel,
                    width = 70.dp,
                )
                CustomButton(
                    text = Strings.confirm,
                    onClick = {
                        batchUpdateByRange(startAccount, endAccount, ACCOUNT_SETTINGS.ACCOUNT_CONFIG.key, configValue)
                    },
                    marginTop = 0.dp
                )
            }

            FlowRow(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = Strings.applyAccountLabel,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    options = accountNumberOptions,
                    selectedValue = batchVerStartAccount,
                    onValueChange = { batchVerStartAccount = it },
                    label = Strings.startAccountLabel,
                    width = 70.dp,
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    options = accountNumberOptions,
                    selectedValue = batchVerEndAccount,
                    onValueChange = { batchVerEndAccount = it },
                    label = Strings.endAccountLabel,
                    width = 70.dp,
                )
                Text(
                    text = Strings.changeTo,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )

                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp),
                    options = verOptions,
                    selectedValue = selectedVer,
                    onValueChange = { selectedVer = it },
                    label = verOptions[0],
                    width = 110.dp,
                )

                NumberDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp),
                    options = startMethodOptions,
                    selectedValue = selectedStartMethod,
                    onValueChange = { selectedStartMethod = it },
                    label = startMethodOptions[0],
                    width = 110.dp,
                )

                CustomButton(
                    text = Strings.confirm,
                    onClick = {
                        val verIdx = verOptions.indexOf(selectedVer).coerceAtLeast(0)
                        val startIdx = startMethodOptions.indexOf(selectedStartMethod).coerceAtLeast(0)
                        batchUpdateByRange(batchVerStartAccount, batchVerEndAccount, ACCOUNT_SETTINGS.GAME_VERSION.key, verIdx.toString())
                        batchUpdateByRange(batchVerStartAccount, batchVerEndAccount, ACCOUNT_SETTINGS.START_METHOD.key, startIdx.toString())
                    },
                    marginTop = 0.dp
                )
            }
        }
    }

    // Display account configurations based on account_count
    val accountCountStr = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]!!.value
    val currentAccountCount = accountCountStr.toIntOrNull() ?: 3

    // Sort accounts: when isPinned is true, enabled (isopen=1) accounts come first by index ascending,
    // then disabled (isopen=0) accounts by index ascending. When isPinned is false, keep natural order by index.
    val sortedAccountIndices = if (isPinned) {
        (1..currentAccountCount).sortedWith(
            compareBy<Int> { index ->
                GlobalVars.configStates["${ACCOUNT_SETTINGS.ISOPEN.key}${index}"]?.value != "1"
            }.thenBy { it }
        )
    } else {
        (1..currentAccountCount).toList()
    }

    items(count = sortedAccountIndices.size, key = { sortedAccountIndices[it] }) { i ->
        AccountConfig(index = sortedAccountIndices[i])
    }
}
