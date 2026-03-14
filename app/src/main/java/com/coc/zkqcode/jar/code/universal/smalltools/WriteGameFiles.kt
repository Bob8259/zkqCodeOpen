package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop

// Helper function: quickly get config value, trigger logAndStop if empty
fun getConfigOrStop(key: String): String {
    return GlobalVars.configStates[key]?.value ?: logAndStop("Failed to get config: $key")
}

suspend fun writeGameFiles(){

}