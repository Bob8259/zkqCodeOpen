package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableStateOf
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

fun LazyListScope.specialSettings() {
    item {
        SettingSection {
            BatchCreateAccount()
        }
    }
    item {
        SettingSection {
            RandomScheduling()
        }
    }
    item {
        SettingSection {
            val tempState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.TEMP_PROTECTION.key) {
                mutableStateOf("0")
            }
            val isTempEnabled = tempState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.TEMP_PROTECTION.key)
            if (isTempEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_PAUSE_THRESHOLD.key)
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_RESUME_THRESHOLD.key)
            }
        }
    }
}
