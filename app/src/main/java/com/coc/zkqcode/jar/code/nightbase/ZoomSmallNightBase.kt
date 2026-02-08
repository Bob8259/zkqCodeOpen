package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchOut
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe

suspend fun zoomSmallNightBase(forBuildingDetection: Boolean = false) {
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(200)
    repeat(3) {
        swipe(981, 485, 162, 202, delayTime = 100)
    }
    if (!forBuildingDetection) {
        delayWithMultiplier(200)
        swipe(981, 485, 519, 278, delayTime = 800)
        delayWithMultiplier(200)
    } else {
        swipe(972, 86, 696, 730, delayTime = 500)
        delayWithMultiplier(100)
        pinchOut(300, 140, 1500, 140, 900, 140)
        delayWithMultiplier(300)
        swipe(958, 147, -564, 143)
        delayWithMultiplier(200)
    }
}