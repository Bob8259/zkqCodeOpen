package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.universal.clickRightBottom

suspend fun zoomSmallMainBase(isForBuild: Boolean = false, isForAttack: Boolean = false) {
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(200)
    if (isForAttack) {
        swipe(911, 134, 249, 529)
    } else {
        swipe(218, 523, 939, 162)
    }
    if (isForBuild) {
        delayWithMultiplier(200)
        clickRightBottom(1)
        swipe(690, 550, 590, 710, delayTime = 600)
    }
}