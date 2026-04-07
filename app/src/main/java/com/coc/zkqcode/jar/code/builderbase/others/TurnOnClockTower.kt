package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.delay
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
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
        ShowMessage("账号${InGamesVars.currentAccountNumber},22小时内已开启过时光钟楼，暂不检测")
        return true
    }
    ShowMessage("账号${InGamesVars.currentAccountNumber}，正在检测时光钟楼")
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
                // Search for the small gem icon above the speed-up button.
                // Its presence means the boost costs gems — no free opportunity available.
                val gemIconSchema = ColorSchema.rescope(
                    MyColors.SmallGemIcon,
                    x1 = freeSpeedUp.x + 5, y1 = freeSpeedUp.y - 50,
                    x2 = freeSpeedUp.x + 80, y2 = freeSpeedUp.y - 5
                )
                val gemFound = findMultiColors(schema = gemIconSchema, byteBuffer = screenBuffer)
                delayWithMultiplier(50)
                if (gemFound != null) {
                    ShowMessage("检测到宝石图标，暂无免费提速机会 (位置: ${gemFound.x},${gemFound.y})")
                    clickRightBottom(1)
                    return true
                }
                TouchActions.tap(freeSpeedUp.x, freeSpeedUp.y, delayTime = 800)

                // Double-check the dialog after tapping to avoid consuming gems when free boost is unavailable.
                val dialogScreenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in turnOnClockTower, second screen capture failed.")
                val dialogWhitePixelCount = countPixelsInArea(
                    result = dialogScreenBuffer, x1 = 515, y1 = 445, x2 = 759, y2 = 510, targetColor = 0xFFFFFF
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

/**
 * Debug-only helper: scans the given area for pure-white pixels (0xFFFFFF) and
 * returns each matching pixel's (x, y) coordinate. Does NOT modify countPixelsInArea.
 */
private fun debugCollectWhitePixelPositions(
    result: ScreenCaptureManager.CaptureResult,
    x1: Int, y1: Int, x2: Int, y2: Int
): List<Pair<Int, Int>> {
    val buf = result.buffer
    val width = result.width
    val height = result.height
    val pixelStride = result.pixelStride
    val rowStride = result.rowStride

    val left = max(0, min(x1, x2))
    val right = min(width - 1, max(x1, x2))
    val top = max(0, min(y1, y2))
    val bottom = min(height - 1, max(y1, y2))

    val positions = mutableListOf<Pair<Int, Int>>()
    for (y in top..bottom) {
        val rowStart = y * rowStride
        for (x in left..right) {
            val offset = rowStart + x * pixelStride
            if (offset + 2 >= buf.capacity()) continue
            // RGBA_8888 format
            val r = buf.get(offset).toInt() and 0xFF
            val g = buf.get(offset + 1).toInt() and 0xFF
            val b = buf.get(offset + 2).toInt() and 0xFF
            if (r == 0xFF && g == 0xFF && b == 0xFF) {
                positions.add(Pair(x, y))
            }
        }
    }
    return positions
}