package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions.pinchIn
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe

suspend fun zoomSmallMainBase() {
    pinchIn(141, 423, 1052, 352, 638, 365)
    delayWithMultiplier(200)
    swipe(218, 523, 939, 162)
}