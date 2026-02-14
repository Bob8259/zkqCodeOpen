package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchOut
import com.coc.zkqcode.jar.code.builderbase.BuilderBaseTrainTroops
import com.coc.zkqcode.jar.code.builderbase.BuilderBaseUpgradeBuildings
import com.coc.zkqcode.jar.code.builderbase.BuilderBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.builderbase.builderBaseRemoveObstacles
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.FindBuildPosition
import com.coc.zkqcode.jar.code.builderbase.upgradehelper.builderBaseFindBuildButton
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.runApp
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

object MainScript {

    // 辅助函数：快速获取配置值，若为空则触发 logAndStop
    private fun getConfigOrStop(key: String): String {
        return GlobalVars.configStates[key]?.value ?: logAndStop("Failed to get config: $key")
    }

    suspend fun runMainScript() {
        while (currentCoroutineContext().isActive) {
            // 1. 初始化/更新本地内存状态
            val startAccount = readMemory("accountNumber").toIntOrNull() ?: 1
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
            InGamesVars.currentAccountNumber = activeAccount
            while (currentCoroutineContext().isActive) {
                InGamesVars.currentGamePackage =
                    getConfigOrStop("game_version${InGamesVars.currentAccountNumber}").toInt()

                //测试代码
                runTestCode()
                if (!enterMainScreen()) {
                    ShowMessage("进入游戏失败")
                    delay(500)
                    break // 跳出内层循环，重新检查账号状态
                }
                if (!playBuilderBase()) {
                    ShowMessage("夜世界操作失败")
                    delay(500)
                    break // 跳出内层循环，重新检查账号状态
                }
//                if (!MainBaseScript.playMainBase()) {
//                    ShowMessage("主世界操作失败")
//                    delay(500)
//                    break // 跳出内层循环，重新检查账号状态
//                }
            }
            delay(2000)
        }
    }

    private suspend fun runTestCode() {
        while (true) {
            ShowMessage("测试代码开始")
            runApp("com.supercell.clashofclans2")
            enterMainScreen()
            delayWithMultiplier(1000)
            BuilderBaseUpgradeBuildings.upgradeBuildings()
//            val redCross = builderBaseFindBuildButton(type = "Cross")
//            if (redCross != null)
//                FindBuildPosition.tryToFindBuildPosition(redCross.x, redCross.y)
            delayWithMultiplier(1000)
        }
    }
}

