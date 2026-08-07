package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.pinchIn
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.swipe

suspend fun zoomSmallMainBase(isForBuild: Boolean = false, isForAttack: Boolean = false) {
    clickRightBottom(1)
    swipe(200, 300, 950, -800)
    clickRightBottom(1)
    delayWithMultiplier(50)
    pinchIn(140, 420, 1000, 140, 640, 365)
    clickRightBottom(1)
    delayWithMultiplier(50)
    if (isForAttack) {
        repeat(2) {
            swipe(910, 135, 0, 720, delayTime = 100)
        }
    } else {
        swipe(220, 525, 940, 160, delayTime = 100)
    }
    if (isForBuild) {
        delayWithMultiplier(100)
        clickRightBottom(1)
        swipe(690, 550, 590, 710)
    }
}