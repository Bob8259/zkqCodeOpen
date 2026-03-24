package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS


@Composable
fun BatchCreateAccount() {
    Text(
        text = "以下是特殊设置，使用前请仔细看教程。",
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
        style = MaterialTheme.typography.labelMedium,
    )
    SettingSwitchIcon(
        key = GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key,
        explain = "勾选批量创号后，辅助将会先检查游戏存档是否全部存在，如果不存在的话就会创号。若所有游戏存档都存在，辅助将会按照设定的序号运行账号。设定的序号就是游戏存档的序号，并且所有账号都会使用配置1的设定。\n" +
                "部分云手机在创号时可能会出异常，如果出异常的话请使用模拟器进行创号。开启批量创号后，掉线重连选项必须选择“立即重连”。",
        afterChange = { checked ->
            if (checked) {
                GlobalVars.configStates[GLOBAL_SETTINGS.AFTER_KICK_OPTION.key]?.value = "0"
            }
        }
    )
    FlowRow {
        SettingInputRow(key = GLOBAL_SETTINGS.CREATE_START_ID.key)
        SettingInputRow(key = GLOBAL_SETTINGS.CREATE_END_ID.key)
        SettingInputRow(key = GLOBAL_SETTINGS.CREATE_PREFIX.key)
        SettingSwitchIcon(
            key = GLOBAL_SETTINGS.CREATE_GEM_BUILD.key,
        )
        SettingSwitchIcon(
            key = GLOBAL_SETTINGS.ADD_SUFFIX_SETTING.key,
        )
    }
}
