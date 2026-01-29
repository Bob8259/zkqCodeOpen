package com.coc.zkqcode.jar.code

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.FileHelper.readJson
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.ui.schema.Schema
import com.google.gson.JsonObject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


class MainScript {
    internal var localMemory: JsonObject? = null
    internal var currentAccountNumber: Int = 1
    internal lateinit var currentGamePackage: String
    private var isAccountOpen = false
    suspend fun runMainScript() {
        while (currentCoroutineContext().isActive) {
            localMemory =
                readJson(Environment.getExternalStorageDirectory().path + "/zkqFiles/memory.json")
            currentAccountNumber = localMemory?.get("accountNumber")?.asInt ?: 1
            val accountAmount =
                GlobalVars.configStates[Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value?.toInt()
                    ?: logAndStop("Failed to get ${Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key}")

            for (currentAccount in currentAccountNumber..accountAmount) {
                // Your logic here
                currentAccountNumber = currentAccount
                val isThisAccountOpen =
                    GlobalVars.configStates["${Schema.ACCOUNT_SETTINGS.ISOPEN.key}$currentAccount"]?.value?.toInt()
                        ?: logAndStop("Failed to get ${Schema.ACCOUNT_SETTINGS.ISOPEN.key}$currentAccount")
                if (isThisAccountOpen == 1) {
                    isAccountOpen = true
                    break
                }
            }
            if (!isAccountOpen) {
                while (true) {
                    ShowMessage("当前未开启任何账号\n请勾选要开启的账号。")
                    delay(2500)
                }
            }
            while (currentCoroutineContext().isActive) {//The Main Loop
                currentGamePackage =
                    GlobalVars.configStates["game_version$currentAccountNumber"]?.value
                        ?: logAndStop("Can not get game_version$currentAccountNumber")
                if (!enterMainScreen(currentAccountNumber, currentGamePackage)) {
                    ShowMessage("进入游戏失败")
                    delay(500)
                    break
                } else {
                    delay(2000)
                }

            }
        }
    }
}
