package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS


@Composable
fun BatchCreateAccount() {
    val bcaState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) {
        mutableStateOf("0")
    }
    val isEnabled = bcaState.value == "1"

    SettingSwitchIcon(
        key = GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key,
        explain = Strings.batchCreateExplain,
        afterChange = { checked ->
            if (checked) {
                GlobalVars.configStates[GLOBAL_SETTINGS.AFTER_KICK_OPTION.key]?.value = "0"
                // Mutual exclusion: disable random scheduling when batch create is enabled
                GlobalVars.configStates[GLOBAL_SETTINGS.RANDOM_SCHEDULING.key]?.value = "0"
                // Mutual exclusion: disable upgrade-time scheduling too (ADR-0001 §2.3)
                GlobalVars.configStates[GLOBAL_SETTINGS.UPGRADE_TIME_SCHEDULING.key]?.value = "0"
            }
        })

    AnimatedVisibility(visible = isEnabled) {
        Column {
            FlowRow {
                SettingInputRow(key = GLOBAL_SETTINGS.CREATE_START_ID.key)
                SettingInputRow(key = GLOBAL_SETTINGS.CREATE_END_ID.key)
                SettingInputRow(key = GLOBAL_SETTINGS.CREATE_PREFIX.key)
            }
            FlowRow {
                SettingSwitchIcon(
                    key = GLOBAL_SETTINGS.CREATE_GEM_BUILD.key,
                )
                SettingSwitchIcon(
                    key = GLOBAL_SETTINGS.ADD_SUFFIX_SETTING.key,
                )
            }
        }
    }
}
