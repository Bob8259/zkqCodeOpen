package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun withHeroHall(action: suspend () -> Unit): Boolean {
    clickRightBottom(1)
    zoomSmallMainBase()

    // Shared detection flow: scan once, then swipe and retry once.
    suspend fun scanAndHandle(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "FindHeroHall", classIndex = 3
        )
        for (detection in scan) {
            val box = detection.boundingBox
            if (box.centerY() <= 85) continue
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
    val openHeroHallIcon = findMultiColorsUntil(schemas = listOf(MyColors.OpenHeroHall, MyColors.OpenHeroHall2), duration = 500)
    if (openHeroHallIcon != null) {
        TouchActions.tap(openHeroHallIcon.x, openHeroHallIcon.y)
        return true
    }
    return false
}
