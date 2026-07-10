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
import androidx.compose.foundation.layout.Spacer
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
    width: Dp = 50.dp,
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
    width: Dp = 50.dp,
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

@Composable
internal fun DropdownSelectButton(
    selectedText: String,
    options: List<String>,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        Button(
            onClick = {
                GlobalVars.isAutoRunEnabled = false
                onToggle()
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(30.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
        ) {
            Text(selectedText, style = MaterialTheme.typography.labelMedium)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
        if (expanded) {
            Popup(
                popupPositionProvider = BelowAnchorPositionProvider(),
                onDismissRequest = onDismiss,
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
                            val isSelected = option == selectedText
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
                                        onSelect(idx)
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
            // ─── Batch operation: single dynamic form ───
            var batchOpType by remember { mutableIntStateOf(0) }
            val batchOpOptions = listOf(Strings.applyConfigLabel, Strings.batchOpVersionMethod, Strings.batchOpStatus)
            var expandedOpType by remember { mutableStateOf(false) }

            var batchStart by remember { mutableStateOf("") }
            var batchEnd by remember { mutableStateOf("") }

            var batchCfgValue by remember { mutableStateOf("") }

            var batchVerIdx by remember { mutableIntStateOf(0) }
            var expandedBatchVer by remember { mutableStateOf(false) }
            var batchMethodIdx by remember { mutableIntStateOf(0) }
            var expandedBatchMethod by remember { mutableStateOf(false) }

            var batchStsIdx by remember { mutableIntStateOf(0) }
            var expandedBatchSts by remember { mutableStateOf(false) }
            val batchStsOptions = listOf(Strings.statusEnabled, Strings.statusDisabled)

            Column(modifier = Modifier.padding(top = 8.dp)) {
                // Row 1: Operation type
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = Strings.batchOperation,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    DropdownSelectButton(
                        selectedText = batchOpOptions[batchOpType],
                        options = batchOpOptions,
                        expanded = expandedOpType,
                        onToggle = { expandedOpType = true },
                        onSelect = { batchOpType = it; expandedOpType = false },
                        onDismiss = { expandedOpType = false }
                    )
                }
                // Row 2: Account range
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.batchAccount,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    SmallTextFieldWithDropdown(
                        value = batchStart,
                        onValueChange = { batchStart = it },
                        options = (1..accountCount).map { it.toString() },
                    )
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    SmallTextFieldWithDropdown(
                        value = batchEnd,
                        onValueChange = { batchEnd = it },
                        options = (1..accountCount).map { it.toString() },
                    )
                }
                // Row 3: Parameter (dynamic based on operation type)
                FlowRow(
                    modifier = Modifier.padding(top = 6.dp),
                ) {
                    Text(
                        text = Strings.batchParam,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 6.dp, end = 6.dp)
                    )
                    when (batchOpType) {
                        0 -> { // Assign config profile
                            Text(
                                text = Strings.applyConfigLabel,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 6.dp, end = 6.dp)
                            )
                            SmallTextFieldWithDropdown(
                                value = batchCfgValue,
                                onValueChange = { batchCfgValue = it },
                                options = (1..configCount).map { it.toString() },
                            )
                        }
                        1 -> { // Version + start method
                            DropdownSelectButton(
                                selectedText = listOf(Strings.cnVersion, Strings.globalVersion, Strings.atrasis)[batchVerIdx],
                                options = listOf(Strings.cnVersion, Strings.globalVersion, Strings.atrasis),
                                expanded = expandedBatchVer,
                                onToggle = { expandedBatchVer = true },
                                onSelect = { batchVerIdx = it; expandedBatchVer = false },
                                onDismiss = { expandedBatchVer = false }
                            )
                            Text(
                                text = Strings.batchMethodLabel,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 6.dp, start = 6.dp, end = 6.dp)
                            )
                            DropdownSelectButton(
                                selectedText = listOf(Strings.gameSave, Strings.directLaunch, Strings.testMode)[batchMethodIdx],
                                options = listOf(Strings.gameSave, Strings.directLaunch, Strings.testMode),
                                expanded = expandedBatchMethod,
                                onToggle = { expandedBatchMethod = true },
                                onSelect = { batchMethodIdx = it; expandedBatchMethod = false },
                                onDismiss = { expandedBatchMethod = false }
                            )
                        }
                        2 -> { // Status
                            DropdownSelectButton(
                                selectedText = batchStsOptions[batchStsIdx],
                                options = batchStsOptions,
                                expanded = expandedBatchSts,
                                onToggle = { expandedBatchSts = true },
                                onSelect = { batchStsIdx = it; expandedBatchSts = false },
                                onDismiss = { expandedBatchSts = false }
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    CustomButton(
                        text = Strings.confirm,
                        onClick = {
                            when (batchOpType) {
                                0 -> batchUpdateByRange(batchStart, batchEnd, ACCOUNT_SETTINGS.ACCOUNT_CONFIG.key, batchCfgValue)
                                1 -> {
                                    batchUpdateByRange(batchStart, batchEnd, ACCOUNT_SETTINGS.GAME_VERSION.key, batchVerIdx.toString())
                                    batchUpdateByRange(batchStart, batchEnd, ACCOUNT_SETTINGS.START_METHOD.key, batchMethodIdx.toString())
                                }
                                2 -> batchUpdateByRange(batchStart, batchEnd, ACCOUNT_SETTINGS.ISOPEN.key, if (batchStsIdx == 0) "1" else "0")
                            }
                        },
                        marginTop = 0.dp
                    )
                }
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
