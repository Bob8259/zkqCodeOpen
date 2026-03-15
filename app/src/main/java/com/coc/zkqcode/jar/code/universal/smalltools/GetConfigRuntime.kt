package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.InGamesVars

fun getConfigRuntime(configName: String): String {
    val configNumber = GlobalVars.configStates["account_config${
        InGamesVars.currentAccountNumber
    }"]?.value ?: logAndRestart(
        "Can not get the config number for account ${InGamesVars.currentAccountNumber}"
    )
    val result = GlobalVars.configStates["${configName}_c$configNumber"]?.value ?: logAndRestart("Can not get the config for ${configName}_c$configNumber")
    return result
}

fun getBooleanConfigRuntime(configName: String): Boolean {
    return getConfigRuntime(configName) == "1"
}

fun getStaticConfig(key: String): String {
    return GlobalVars.configStates[key]?.value ?: logAndRestart("Failed to get configuration for: $key")
}