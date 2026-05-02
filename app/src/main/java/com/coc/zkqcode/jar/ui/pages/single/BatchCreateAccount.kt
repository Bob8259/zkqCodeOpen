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
        explain = "启用批量创号后，辅助将优先检查所有游戏存档是否存在，若存档缺失则自动创建账号；若存档齐全，则按照指定序号依次运行对应账号。序号与游戏存档编号一致，且所有账号均采用配置1的设定。\n" + "部分云手机在创号过程中可能出现异常，建议改用模拟器完成创号操作。启用批量创号时，掉线重连选项须设置为\u201C立即重连\u201D。",
        afterChange = { checked ->
            if (checked) {
                GlobalVars.configStates[GLOBAL_SETTINGS.AFTER_KICK_OPTION.key]?.value = "0"
                // Mutual exclusion: disable random scheduling when batch create is enabled
                GlobalVars.configStates[GLOBAL_SETTINGS.RANDOM_SCHEDULING.key]?.value = "0"
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
