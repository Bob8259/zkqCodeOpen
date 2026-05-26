package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.mutableStateOf
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import android.os.Environment
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val updateCnGameState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.UPDATE_CN_GAME.key) {
                mutableStateOf("0")
            }
            val isUpdateCnGameEnabled = updateCnGameState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.UPDATE_CN_GAME.key)
            if (isUpdateCnGameEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.key)
            }
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
    item {
        SettingSection {
            val restartState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SCHEDULED_RESTART.key) {
                mutableStateOf("0")
            }
            val isRestartEnabled = restartState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SCHEDULED_RESTART.key)
            if (isRestartEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.RESTART_INTERVAL.key)
            }
        }
    }
    legalDisclaimers()
    item {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        CustomButton(
            text = Strings.copyLogs,
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val srcDir = "${context.filesDir.absolutePath}/logs"
                    val sdPath = Environment.getExternalStorageDirectory().path
                    val destDir = "$sdPath/zkqFiles"
                    val copyCmd = "mkdir -p \"$destDir\" && cp -rf \"$srcDir\" \"$destDir/\""
                    ShellHelper.runBlockingNoOutput(copyCmd)
                }
            }
        )
    }
}
