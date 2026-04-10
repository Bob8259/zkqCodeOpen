package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.universal.clickRightBottom

suspend fun zoomSmallMainBase(isForBuild: Boolean = false, isForAttack: Boolean = false) {
    clickRightBottom(1)
    swipe(200, 500, 950, -500)
    clickRightBottom(1)
    delayWithMultiplier(50)
    pinchIn(141, 423, 1052, 352, 638, 365)
    clickRightBottom(1)
    delayWithMultiplier(50)
    if (isForAttack) {
        repeat(2) {
            swipe(911, 134, 0, 720, delayTime = 100)
        }
    } else {
        swipe(218, 523, 939, 162, delayTime = 100)
    }
    if (isForBuild) {
        delayWithMultiplier(100)
        clickRightBottom(1)
        swipe(690, 550, 590, 710)
    }
}