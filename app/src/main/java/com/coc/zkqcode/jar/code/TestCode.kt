package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.full.memberProperties

//These code are for test only.
suspend fun takeScreenShotForUpgradeCost() {
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

suspend fun findAllResearchColors() {
    val foundColors = mutableListOf<String>()
    val notFoundColors = mutableListOf<String>()

    val allColors = MainBaseResearchColors.allResearchColors

    ShowMessage("开始查找所有研究颜色，共 ${allColors.size} 个")
    delayWithMultiplier(500)
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndStop("failed to take screenshot at close advertisement")
    for (schema in allColors) {
        val result = findMultiColors(schema = schema, byteBuffer = screenBuffer)

        if (result != null) {
            foundColors.add(schema.name ?: "Unknown")
        } else {
            notFoundColors.add(schema.name ?: "Unknown")
        }
        delayWithMultiplier(50)
    }
}
