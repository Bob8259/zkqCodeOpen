package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop

fun getConfigRuntime(currentAccountNumber: Int, configName: String): String {
    val configNumber = GlobalVars.configStates["account_config$currentAccountNumber"]?.value
        ?: logAndStop("Can not get the config number for account $currentAccountNumber")
    val result = GlobalVars.configStates["${configName}_c$configNumber"]?.value
        ?: logAndStop("Can not get the config for ${configName}_c$configNumber")
    return result
}