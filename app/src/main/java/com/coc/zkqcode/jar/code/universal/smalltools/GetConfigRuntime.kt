package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.ui.schema.Schema

fun getConfigRuntime(configName: String): String {
    // When batch create account is enabled, all accounts share config group 1
    val configNumber = if (getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) == "1") {
        "1"
    } else {
        GlobalVars.configStates["account_config${InGameVars.currentAccountNumber}"]?.value ?: logAndRestart(
            "Can not get the config number for account ${InGameVars.currentAccountNumber}",
            isShow = false
        )
    }
    val result = GlobalVars.configStates["${configName}_c$configNumber"]?.value?.trim()
        ?: logAndRestart("Can not get config value for ${configName}_c$configNumber", isShow = false)
    return result
}

fun getBooleanConfigRuntime(configName: String): Boolean {
    return getConfigRuntime(configName) == "1"
}

fun getStaticConfig(key: String): String {
    return (GlobalVars.configStates[key]?.value?.trim()) ?: Schema.getDefaultValue(key)
}