package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.mlkit.ChineseTextRecognizer
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.nightbase.collectNightBaseResources
import com.coc.zkqcode.jar.code.nightbase.playNightBase
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.isInHomePage
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
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
                //测试代码
                runTestCode()

                InGamesVars.currentGamePackage =
                    getConfigOrStop("game_version${InGamesVars.currentAccountNumber}").toInt()
                if (!enterMainScreen()) {
                    ShowMessage("进入游戏失败")
                    delay(500)
                    break // 跳出内层循环，重新检查账号状态
                }
                if (!playNightBase()) {
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
            delay(3000)
            val screenBuffer = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
                ?: logAndStop("in isInHomePage, screen capture failed.")
            
            // Define crop area: (1004, 21) to (1217, 126)
            val left = 1004
            val top = 21
            val width = 1217 - 1004 // 213
            val height = 126 - 21   // 105
            
            try {
                // Ensure crop area is within bitmap bounds
                if (left + width <= screenBuffer.width && top + height <= screenBuffer.height) {
                    val croppedBitmap = Bitmap.createBitmap(screenBuffer, left, top, width, height)
                    
                    // Use ChineseTextRecognizer to recognize text and digits
                    ChineseTextRecognizer.recognizeChineseText(croppedBitmap)
                } else {
                    logAndStop("Crop area ($left, $top, $width, $height) is out of bitmap bounds (${screenBuffer.width}x${screenBuffer.height})")
                }
            } catch (e: Exception) {
                logAndStop("Error during cropping or recognition: ${e.message}")
            }

            ShowMessage("测试代码结束")
            delay(5000)
        }

    }
}

