package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseDeployTroops
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseTrainTroops
import com.coc.zkqcode.jar.code.mainbase.others.MainBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.mainbase.research.mainBaseResearch
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

object MainScript {

    // Helper function: quickly get config value, trigger logAndStop if empty
    private fun getConfigOrStop(key: String): String {
        return GlobalVars.configStates[key]?.value ?: logAndStop("Failed to get config: $key")
    }

    suspend fun runMainScript() {
        while (currentCoroutineContext().isActive) {
            // 1. Initialize/update local memory state
            val startAccount = readMemory(StorageKeys.ACCOUNT_NUMBER).toIntOrNull() ?: 1
            val accountTotal = getConfigOrStop(Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key).toInt()

            // 2. Find the first enabled account
            val activeAccount = (startAccount..accountTotal).firstOrNull { id ->
                getConfigOrStop("${Schema.ACCOUNT_SETTINGS.ISOPEN.key}$id") == "1"
            }

            // 3. Handle case when no account is found
            if (activeAccount == null) {
                while (currentCoroutineContext().isActive) {
                    ShowMessage("当前未开启任何账号\n请勾选要开启的账号。")
                    delay(2500)
                }
                return // Exit is theoretically controlled by coroutine
            }

            // 4. Execute main logic loop
            InGamesVars.currentAccountNumber = activeAccount
            while (currentCoroutineContext().isActive) {
                InGamesVars.currentGamePackage = getConfigOrStop("game_version${InGamesVars.currentAccountNumber}").toInt()

                // Test code
                runTestCode()
                if (!enterMainScreen(true)) {
                    ShowMessage("进入游戏失败")
                    delay(500)
                    break // Break inner loop and recheck account status
                }
                if (!playBuilderBase()) {
                    ShowMessage("夜世界操作失败")
                    delay(500)
                    break // Break inner loop and recheck account status
                }
//                if (!playMainBase()) {
//                    ShowMessage("主世界操作失败")
//                    delay(500)
//                    break // Break inner loop and recheck account status
//                }
            }
            delay(2000)
        }
    }

    private suspend fun runTestCode() {
        while (true) {
//            enterMainScreen()
            delayWithMultiplier(1000)
            mainBaseDeployTroops()
//            findMultiColors(schema = MyColors.GiantAtDeployBar)
//            mainBaseTrainTroops()
            delayWithMultiplier(10000000)

        }
    }

}

