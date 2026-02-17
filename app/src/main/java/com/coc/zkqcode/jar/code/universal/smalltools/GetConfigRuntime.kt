package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.InGamesVars

fun getConfigRuntime(configName: String): String {
    val configNumber = GlobalVars.configStates["account_config${
        InGamesVars.currentAccountNumber
    }"]?.value
        ?: logAndStop(
            "Can not get the config number for account ${
                InGamesVars.currentAccountNumber
            }"
        )
    val result = GlobalVars.configStates["${configName}_c$configNumber"]?.value
        ?: logAndStop("Can not get the config for ${configName}_c$configNumber")
    return result
}

fun getBooleanConfigRuntime(configName: String): Boolean {
    return getConfigRuntime(configName) == "1"
}

fun getStaticConfig(key: String): String {
    return GlobalVars.configStates[key]?.value ?: logAndStop("Failed to get configuration for: $key")
}