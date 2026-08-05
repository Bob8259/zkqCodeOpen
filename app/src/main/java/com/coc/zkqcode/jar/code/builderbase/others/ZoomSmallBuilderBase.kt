package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.pinchIn
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions.swipe
import com.coc.zkqcode.jar.code.universal.clickRightBottom

suspend fun zoomSmallBuilderBase(isForBuild: Boolean = false) {
    clickRightBottom(1)
    swipe(200, 300, 950, -800)
    clickRightBottom(1)
    delayWithMultiplier(50)
    pinchIn(140, 420, 1270, 125, 640, 365)
    delayWithMultiplier(200)
    repeat(2) {
        swipe(980, 485, 0, 0, delayTime = 120)
    }
    delayWithMultiplier(200)
    swipe(980, 485, 520, 280, delayTime = 800)
    if (isForBuild) {
        clickRightBottom(1)
        swipe(575, 430, 575, 710, 800)
    }
    delayWithMultiplier(200)
}