package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions


object FindBuildPosition {
    suspend fun tryToFindBuildPosition(crossX: Int, crossY: Int) {
        val centerX = crossX + 20
        val centerY = crossY + 45
        TouchActions.swipe(centerX, centerY, 620, 720, delayTime = 600)

        delayWithMultiplier(10000)
    }
}