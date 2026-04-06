package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.buildings.countPixelsInArea
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun turnOnClockTower(): Boolean {
    if (!getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_ENABLE_CLOCK_TOWER.key)) return true
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.TURN_ON_CLOCK_TOWER, InGamesVars.currentAccountNumber)

    // Guard the Clock Tower check so each account only scans once every twenty-two hours.
    if (!checkMemoryFile(storageKey, 1320)) {
        ShowMessage("22小时内已开启过时光钟楼，暂不检测")
//        return true
    }

    clickRightBottom(1)
    zoomSmallBuilderBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapClockTower(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "TurnOnClockTower", classIndex = 1
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt(), delayTime = 500)
            val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in turnOnClockTower, screen capture failed.")
            val freeSpeedUp = findMultiColors(schema = MyColors.ClockTower, byteBuffer = screenBuffer)
            if (freeSpeedUp != null) {
                // Detect the white text overlay that means no free boost is currently available.
                val whitePixelCount = countPixelsInArea(
                    result = screenBuffer, x1 = freeSpeedUp.x + 7, y1 = freeSpeedUp.y - 46, x2 = freeSpeedUp.x + 72, y2 = freeSpeedUp.y - 9, targetColor = 0xFFFFFF
                )
                delayWithMultiplier(50)
                if (whitePixelCount > 10) {
                    ShowMessage("暂无免费提速机会")
                    clickRightBottom(1)
                    return true
                }
                TouchActions.tap(freeSpeedUp.x, freeSpeedUp.y, delayTime = 800)

                // Double-check the dialog after tapping to avoid consuming gems when free boost is unavailable.
                val dialogScreenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in turnOnClockTower, second screen capture failed.")
                val dialogWhitePixelCount = countPixelsInArea(
                    result = dialogScreenBuffer, x1 = 515, y1 = 408, x2 = 759, y2 = 510, targetColor = 0xFFFFFF
                )
                if (dialogWhitePixelCount > 30) {
                    ShowMessage("暂无免费提速机会")
                    clickRightBottom(1)
                    return true
                }
                TouchActions.tap(650, 464, delayTime = 300)
                // Record memory immediately after the Clock Tower boost is triggered.
                writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
                clickRightBottom(1)
                return true
            }
        }
        return false
    }

    if (!scanAndTapClockTower()) {
        swipe(570, 325, 570, 720)
        scanAndTapClockTower()
    }
    return enterMainScreen()
}