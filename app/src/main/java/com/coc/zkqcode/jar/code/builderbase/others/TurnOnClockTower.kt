package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
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
        return true
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
            val freeSpeedUp = findMultiColors(MyColors.ClockTower)
            if (freeSpeedUp != null) {
                TouchActions.tap(freeSpeedUp.x, freeSpeedUp.y, delayTime = 500)
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