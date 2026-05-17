package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

@Composable
fun AccountConfig(
    index: Int
) {
    SettingSection {
        Text(text = Strings.account(index), style = MaterialTheme.typography.labelMedium)
        FlowRow {
            // Remove the switch label's extra text offset so it aligns with the remark label.
            val isRandomScheduling = GlobalVars.configStates[GLOBAL_SETTINGS.RANDOM_SCHEDULING.key]?.value == "1"
            if (isRandomScheduling) {
                Text(
                    text = Strings.randomSchedulingRemark,
                    modifier = Modifier.padding(end = 10.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            } else {
                SettingSwitchIcon(key = "${ACCOUNT_SETTINGS.ISOPEN.key}${index}", textTopPadding = 0.dp)
            }
            // Nudge only the remark label upward because it is centered against a taller text field.
            SettingInputRow(
                key = "${ACCOUNT_SETTINGS.REMARK.key}${index}", topPadding = 4.dp, labelVerticalOffset = (-2).dp
            )
        }
        FlowRow {
            SettingDropdown(
                key = "${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}", options = listOf(Strings.cnServer, Strings.globalServer, Strings.atrasis)
            )
            SettingInputRow(key = "${ACCOUNT_SETTINGS.ACCOUNT_CONFIG.key}${index}")
            SettingDropdown(
                key = "${ACCOUNT_SETTINGS.START_METHOD.key}${index}", options = listOf(Strings.gameSave, Strings.directLaunch, Strings.testMode)
            )
        }
        when (GlobalVars.configStates["${ACCOUNT_SETTINGS.START_METHOD.key}${index}"]!!.value) {
            "0" -> GameFiles(index)//Game save login
            "2" -> UseDataPackage(index)//Account switcher
        }

    }
}

@Composable
fun UseDataPackage(
    index: Int
) {
    if (GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value == "0") {
        Column {
            SettingInputRow(key = "${ACCOUNT_SETTINGS.DATA_CONTENT.key}${index}")
        }
    } else {
        Text(
            text = Strings.globalNotSupported,
            modifier = Modifier.padding(end = 10.dp, bottom = 6.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun GameFiles(
    index: Int
) {
    // Get current game version from configStates
    val currentVersion = GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value
    // Use remember to save currently selected option
    var selectedOption by remember {
        mutableStateOf(
            GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value
        )
    }

    // Listen for changes in currentVersion and update selectedOption
    LaunchedEffect(currentVersion) {
        selectedOption = GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}${index}"]!!.value
    }
    if (selectedOption == "0") {//0 means CN server
        SettingInputRow(key = "${ACCOUNT_SETTINGS.CN_PATH.key}${index}")
    } else {
        SettingInputRow(key = "${ACCOUNT_SETTINGS.GLOBAL_PATH.key}${index}")
    }
}