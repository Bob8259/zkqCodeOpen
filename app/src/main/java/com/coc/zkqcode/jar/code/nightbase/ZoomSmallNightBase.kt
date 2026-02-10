package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchOut
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe

suspend fun zoomSmallNightBase(isForBuild: Boolean = false) {
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(200)

    if (isForBuild)
        swipe(950, 194, 322, 1580, delayTime = 800)
    else {
        repeat(3) {
            swipe(981, 485, 162, 202, delayTime = 100)
        }
        delayWithMultiplier(200)
        swipe(981, 485, 519, 278, delayTime = 800)
    }
    delayWithMultiplier(200)
}