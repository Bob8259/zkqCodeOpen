package com.coc.zkqcode.jar.code.builderbase.herohall

import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun placeHeroBanners(): Boolean {
    findHeroHall()
    return enterMainScreen()
}