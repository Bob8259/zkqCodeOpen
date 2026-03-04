package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.data.database.GlobalVars
import android.graphics.Bitmap
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import java.io.File
import java.io.FileOutputStream
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseRemoveObstacles
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.detectInstantBuildCost
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
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
                InGamesVars.currentGamePackage = getConfigOrStop("game_version${InGamesVars.currentAccountNumber}").toInt()

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
//                if (!playMainBase()) {
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
//            enterMainScreen()
            delayWithMultiplier(1000)
            ShowMessage("detect result ${detectInstantBuildCost()}")
            delayWithMultiplier(1000)
        }
    }

    private suspend fun testScreenShot() {
        val upgradeGemIcon = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeGemIcon, MyColors.UpgradeGemIcon2, MyColors.UpgradeGemIcon3), duration = 500)
        if (upgradeGemIcon != null) {
            val startX = upgradeGemIcon.x - 80
            val startY = upgradeGemIcon.y - 45
            val endX = upgradeGemIcon.x + 72
            val endY = upgradeGemIcon.y
            val width = endX - startX
            val height = endY - startY

            if (width > 0 && height > 0) {
                val screenBitmap = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
                if (screenBitmap != null) {
                    if (startX + width <= screenBitmap.width && startY + height <= screenBitmap.height) {
                        val croppedBitmap = Bitmap.createBitmap(screenBitmap, startX, startY, width, height)

                        ScreenCaptureManager.getContext()?.let { context ->
                            try {
                                val folderName = "ScreenShots"
                                val folder = File(context.filesDir, folderName)
                                if (!folder.exists()) {
                                    folder.mkdirs()
                                }
                                val fileName = "upgrade_gem_${System.currentTimeMillis()}.png"
                                val file = File(folder, fileName)
                                FileOutputStream(file).use { out ->
                                    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                croppedBitmap.recycle()
                            }
                        }
                    }
                    screenBitmap.recycle()
                    delayWithMultiplier(10000)
                }
            }
        }

    }
}

