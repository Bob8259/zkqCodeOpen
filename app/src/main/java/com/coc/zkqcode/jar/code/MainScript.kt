package com.coc.zkqcode.jar.code

import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.FileHelper.readJson
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.mainbase.Precheck
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.ui.schema.Schema
import com.google.gson.JsonObject
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainScript {
    private var localMemory: JsonObject? = null
    private var currentAccountNumber: Int = 1
    private lateinit var currentGamePackage: String

    // 辅助函数：快速获取配置值，若为空则触发 logAndStop
    private fun getConfigOrStop(key: String): String {
        return GlobalVars.configStates[key]?.value ?: logAndStop("Failed to get config: $key")
    }

    suspend fun runMainScript() {
        val memoryPath = "${Environment.getExternalStorageDirectory().path}/zkqFiles/memory.json"

        while (currentCoroutineContext().isActive) {
            // 1. 初始化/更新本地内存状态
            localMemory = readJson(memoryPath)
            val startAccount = localMemory?.get("accountNumber")?.asInt ?: 1
            val accountTotal = getConfigOrStop(Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key).toInt()

            // 2. 查找第一个开启的账号
            val activeAccount = (startAccount..accountTotal).firstOrNull { id ->
                getConfigOrStop("${Schema.ACCOUNT_SETTINGS.ISOPEN.key}$id") == "1"
            }

            // 3. 处理未找到账号的情况
            if (activeAccount == null) {
                while (currentCoroutineContext().isActive) {
                    ShowMessage("当前未开启任何账号\n请勾选要开启的账号。")
                    delay(2500)
                }
                return // 理论上由协程控制退出
            }

            // 4. 执行主逻辑循环
            currentAccountNumber = activeAccount
            while (currentCoroutineContext().isActive) {
                currentGamePackage = getConfigOrStop("game_version$currentAccountNumber")
                if (!enterMainScreen(currentAccountNumber, currentGamePackage)) {
                    ShowMessage("进入游戏失败")
                    delay(500)
                    break // 跳出内层循环，重新检查账号状态
                }
                if (!Precheck().claimAchievement(currentAccountNumber, currentGamePackage)) {
                    ShowMessage("领取成就奖励失败")
                    delay(500)
                    break // 跳出内层循环，重新检查账号状态
                }
                delay(2000)
            }
        }
    }
}