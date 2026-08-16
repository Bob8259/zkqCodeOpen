package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.buildings.countPixelsInArea
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.swipe
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun turnOnClockTower(): Boolean {
    if (!getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_ENABLE_CLOCK_TOWER.key)) return true
    if (InGameVars.clockTowerCooldown > 5 && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SMART_DECISION.key)) return true
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.TURN_ON_CLOCK_TOWER, InGameVars.currentAccountNumber)

    // Guard the Clock Tower check so each account only scans once every twenty-two hours.
    if (!checkMemoryFile(storageKey, 1320)) {
        accountMessage("22小时内已开启过时光钟楼，暂不检测")
        return true
    }
    accountMessage("正在检测时光钟楼")
    clickRightBottom(1)
    zoomSmallBuilderBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapClockTower(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "TurnOnClockTower", classIndex = 1
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt())
            val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
            val freeSpeedUp = findMultiColorsUntil(schemas = listOf(MyColors.ClockTower, MyColors.ClockTower2), duration = 10, byteBuffer = screenBuffer)
            if (freeSpeedUp != null) {
                // Search for the small gem icon above the speed-up button.
                // Its presence means the boost costs gems — no free opportunity available.
                val gemIconSchema = ColorSchema.rescope(
                    MyColors.SmallGemIcon, x1 = freeSpeedUp.x + 5, y1 = freeSpeedUp.y - 50, x2 = freeSpeedUp.x + 80, y2 = freeSpeedUp.y - 5
                )
                val gemFound = findMultiColors(schema = gemIconSchema, byteBuffer = screenBuffer)
                delayWithMultiplier(50)
                if (gemFound != null) {
                    accountMessage("检测到宝石图标，暂无免费提速机会 (位置: ${gemFound.x},${gemFound.y})")
                    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.USE_CLOCK_TOWER_POTION.key)) {
                        val clockTowerPotionButton = findMultiColors(
                            MyColors.ClockTowerPotionOutside
                        )
                        if (clockTowerPotionButton != null) {
                            TouchActions.tap(clockTowerPotionButton.x, clockTowerPotionButton.y)
                            val clockTowerPotionInner = findMultiColorsUntil(listOf(MyColors.ClockTowerPotionInner), 500)
                            if (clockTowerPotionInner != null) {
                                TouchActions.tap(clockTowerPotionInner.x, clockTowerPotionInner.y)
                            }
                        }
                    }
                    clickRightBottom(1)
                    return true
                }
                TouchActions.tap(freeSpeedUp.x, freeSpeedUp.y, delayTime = 800)

                // Double-check the dialogue after tapping to avoid consuming gems when free boost is unavailable.
                val dialogScreenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
                val dialogWhitePixelCount = countPixelsInArea(
                    result = dialogScreenBuffer, x1 = 515, y1 = 445, x2 = 759, y2 = 510, targetColor = 0xFFFFFF
                )
                if (dialogWhitePixelCount > 30) {
                    accountMessage("暂无免费提速机会")
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
