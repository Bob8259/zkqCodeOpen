package com.coc.zkqcode.jar.code

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import java.io.File
import java.io.FileOutputStream

//These code are for test only.
suspend fun testScreenShot() {
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