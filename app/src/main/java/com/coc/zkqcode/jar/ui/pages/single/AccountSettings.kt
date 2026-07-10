package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.theme.AppColors
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.schema.ConfigManager
import com.coc.zkqcode.jar.ui.components.BelowAnchorPositionProvider
import com.coc.zkqcode.jar.ui.schema.Schema

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
internal fun SmallTextFieldWithDropdown(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    width: Dp = 40.dp,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallTextField(
            value = value,
            onValueChange = onValueChange,
            width = width
        )
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            Button(
                onClick = {
                    GlobalVars.isAutoRunEnabled = false
                    expanded = true
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(25.dp).height(25.dp),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
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
                                val isSelected = option == value
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
}

fun LazyListScope.accountSettings() {
    val pinnedState = GlobalVars.configStates.getOrPut("_account_pinned") { mutableStateOf("0") }
    val isPinned = pinnedState.value == "1"
    val configCount = GlobalVars.configStates[GLOBAL_SETTINGS.CONFIG_COUNT.key]?.value?.toIntOrNull() ?: 2
    val accountCount = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value?.toIntOrNull() ?: 3
    item {
        SettingSection {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                val accountCountState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.ACCOUNT_COUNT.key) { mutableStateOf("3") }
                Text(
                    text = Schema.getDisplayName(GLOBAL_SETTINGS.ACCOUNT_COUNT.key),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(end = 8.dp)
                )
                SmallTextField(
                    value = accountCountState.value,
                    onValueChange = { newValue ->
                        accountCountState.value = newValue
                        val newCount = newValue.toIntOrNull()
                        if (newCount != null) {
                            val clamped = newCount.coerceAtMost(5000)
                            if (clamped != newCount) {
                                accountCountState.value = clamped.toString()
                            }
                            ConfigManager.expandAccountConfigs(clamped)
                        }
                    }
                )
                // Minus button
                Button(
                    onClick = {
                        GlobalVars.isAutoRunEnabled = false
                        val current = accountCountState.value.toIntOrNull() ?: 1
                        if (current > 1) {
                            val newVal = current - 1
                            accountCountState.value = newVal.toString()
                            ConfigManager.expandAccountConfigs(newVal)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(25.dp).height(25.dp).padding(end = 1.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "-",
                        modifier = Modifier.size(18.dp)
                    )
                }
                // Plus button
                Button(
                    onClick = {
                        GlobalVars.isAutoRunEnabled = false
                        val current = accountCountState.value.toIntOrNull() ?: 1
                        if (current < 5000) {
                            val newVal = current + 1
                            accountCountState.value = newVal.toString()
                            ConfigManager.expandAccountConfigs(newVal)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(25.dp).height(25.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "+",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
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

            var batchVerStartAccount by remember { mutableStateOf("") }
            var batchVerEndAccount by remember { mutableStateOf("") }
            var selectedVerIndex by remember { mutableIntStateOf(0) }
            var expandedVer by remember { mutableStateOf(false) }
            val verOptions = listOf(Strings.cnVersion, Strings.globalVersion, Strings.atrasis)

            var selectedStartMethodIndex by remember { mutableIntStateOf(0) }
            var expandedStartMethod by remember { mutableStateOf(false) }
            val startMethodOptions = listOf(Strings.gameSave, Strings.directLaunch, Strings.testMode)

            var batchStatusStart by remember { mutableStateOf("") }
            var batchStatusEnd by remember { mutableStateOf("") }
            var selectedStatusIndex by remember { mutableIntStateOf(0) }
            var expandedStatus by remember { mutableStateOf(false) }
            val statusOptions = listOf(Strings.statusEnabled, Strings.statusDisabled)

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
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = configValue,
                    onValueChange = { configValue = it },
                    options = (1..configCount).map { it.toString() },
                )
                Text(
                    text = Strings.toAccounts,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = startAccount,
                    onValueChange = { startAccount = it },
                    options = (1..accountCount).map { it.toString() },
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically).padding(end = 4.dp),
                    value = endAccount,
                    onValueChange = { endAccount = it },
                    options = (1..accountCount).map { it.toString() },
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
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = batchVerStartAccount,
                    onValueChange = { batchVerStartAccount = it },
                    options = (1..accountCount).map { it.toString() },
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = batchVerEndAccount,
                    onValueChange = { batchVerEndAccount = it },
                    options = (1..accountCount).map { it.toString() },
                )
                Text(
                    text = Strings.changeTo,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )

                // Version Dropdown
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                        .align(Alignment.CenterVertically).padding(end = 4.dp)
                ) {
                    Button(
                        onClick = {
                            GlobalVars.isAutoRunEnabled = false
                            expandedVer = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(verOptions[selectedVerIndex], style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    if (expandedVer) {
                        Popup(
                            popupPositionProvider = BelowAnchorPositionProvider(),
                            onDismissRequest = { expandedVer = false },
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
                                    verOptions.forEachIndexed { idx, option ->
                                        val isSelected = idx == selectedVerIndex
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
                                                    selectedVerIndex = idx
                                                    expandedVer = false
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

                // Start Method Dropdown
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                        .align(Alignment.CenterVertically).padding(end = 4.dp)
                ) {
                    Button(
                        onClick = {
                            GlobalVars.isAutoRunEnabled = false
                            expandedStartMethod = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(startMethodOptions[selectedStartMethodIndex], style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    if (expandedStartMethod) {
                        Popup(
                            popupPositionProvider = BelowAnchorPositionProvider(),
                            onDismissRequest = { expandedStartMethod = false },
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
                                    startMethodOptions.forEachIndexed { idx, option ->
                                        val isSelected = idx == selectedStartMethodIndex
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
                                                    selectedStartMethodIndex = idx
                                                    expandedStartMethod = false
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

                CustomButton(
                    text = Strings.confirm,
                    onClick = {
                        batchUpdateByRange(batchVerStartAccount, batchVerEndAccount, ACCOUNT_SETTINGS.GAME_VERSION.key, selectedVerIndex.toString())
                        batchUpdateByRange(batchVerStartAccount, batchVerEndAccount, ACCOUNT_SETTINGS.START_METHOD.key, selectedStartMethodIndex.toString())
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
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = batchStatusStart,
                    onValueChange = { batchStatusStart = it },
                    options = (1..accountCount).map { it.toString() },
                )
                Text(
                    text = "-",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                SmallTextFieldWithDropdown(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    value = batchStatusEnd,
                    onValueChange = { batchStatusEnd = it },
                    options = (1..accountCount).map { it.toString() },
                )
                Text(
                    text = Strings.statusTo,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                        .align(Alignment.CenterVertically).padding(end = 4.dp)
                ) {
                    Button(
                        onClick = {
                            GlobalVars.isAutoRunEnabled = false
                            expandedStatus = true
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(statusOptions[selectedStatusIndex], style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    if (expandedStatus) {
                        Popup(
                            popupPositionProvider = BelowAnchorPositionProvider(),
                            onDismissRequest = { expandedStatus = false },
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
                                    statusOptions.forEachIndexed { idx, option ->
                                        val isSelected = idx == selectedStatusIndex
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
                                                    selectedStatusIndex = idx
                                                    expandedStatus = false
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
                CustomButton(
                    text = Strings.confirm,
                    onClick = {
                        batchUpdateByRange(batchStatusStart, batchStatusEnd, ACCOUNT_SETTINGS.ISOPEN.key, if (selectedStatusIndex == 0) "1" else "0")
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
