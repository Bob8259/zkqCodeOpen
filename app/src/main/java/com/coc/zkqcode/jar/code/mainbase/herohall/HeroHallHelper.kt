package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.yolo.tiledYoloDetect

suspend fun withHeroHall(action: suspend () -> Unit): Boolean {
    clickRightBottom(1)
    zoomSmallMainBase()

    // Shared detection flow: scan once, then swipe and retry once.
    suspend fun scanAndHandle(): Boolean {
        val scan = tiledYoloDetect(
            modelName = "building-detect", callerTag = "FindHeroHall", classIndex = 3
        )
        for (detection in scan) {
            val box = detection.boundingBox
            if (openHeroHall(box.centerX().toInt(), box.centerY().toInt())) {
                action()
                return true
            }
        }
        return false
    }

    if (!scanAndHandle()) {
        swipe(900, 130, 0, 720)
        scanAndHandle()
    }

    return enterMainScreen()
}

suspend fun openHeroHall(x: Int, y: Int): Boolean {
    TouchActions.tap(x, y, delayTime = 300)
    val openHeroHallIcon = findMultiColorsUntil(
        schemas = listOf(MyColors.OpenHeroHall), duration = 500
    )
    if (openHeroHallIcon != null) {
        TouchActions.tap(openHeroHallIcon.x, openHeroHallIcon.y, delayTime = 600)
        return true
    }
    return false
}
