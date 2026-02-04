package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe


suspend fun nightBaseRemoveObstacles() {
    zoomSmallNightBase()
    swipe(587, 420, 587, 670)
    val worker = NightBaseWorkerAndResearch.detectWorkerNumber()
    if (worker.total == 2) {

    }

}

private fun removeObstacles() {

}