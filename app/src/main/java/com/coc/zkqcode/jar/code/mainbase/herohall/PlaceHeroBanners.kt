package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun placeHeroBanners(): Boolean {
    zoomSmallMainBase()

    // First scan: detect and tap hero halls sorted by confidence (highest first)
    val firstScan = findHeroHall()
    for (detection in firstScan) {
        val box = detection.boundingBox
        TouchActions.tap(box.centerX().toInt(), box.centerY().toInt())
    }

    // Swipe to reveal more of the base
    swipe(911, 134, 249, 529)

    // Second scan: detect and tap hero halls in the newly visible area
    val secondScan = findHeroHall()
    for (detection in secondScan) {
        val box = detection.boundingBox
        TouchActions.tap(box.centerX().toInt(), box.centerY().toInt())
    }

    return enterMainScreen()
}