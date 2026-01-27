package com.coc.zkqcode.jar.code

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.FileHelper.readJson
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.google.gson.JsonObject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


class MainScript {
    internal var localMemory: JsonObject? = null
    internal var currentAccountNumber: Int = 1
    internal lateinit var currentGamePackage: String
    suspend fun runMainScript() {
        while (currentCoroutineContext().isActive) {
            localMemory =
                readJson(Environment.getExternalStorageDirectory().path + "/zkqFiles/memory.json")
            while (currentCoroutineContext().isActive) {//The Main Loop
                currentAccountNumber = localMemory?.get("accountNumber")?.asInt ?: 1
                currentGamePackage =
                    GlobalVars.configStates["game_version$currentAccountNumber"]?.value
                        ?: logAndStop("Can not get game_version$currentAccountNumber")
                if (!enterMainScreen(currentAccountNumber, currentGamePackage)) {
                    ShowMessage("进入游戏失败")
                    delay(500)
                    break
                }else{
                    delay(2000)
                }

            }
        }
    }
}
