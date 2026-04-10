package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase

suspend fun clanCapitalAttack() {
    zoomSmallClanCapital()
}

private suspend fun zoomSmallClanCapital() {
    zoomSmallMainBase()
    swipe(900, 250, 500, 350)
}