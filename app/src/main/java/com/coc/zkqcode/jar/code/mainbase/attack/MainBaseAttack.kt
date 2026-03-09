package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun mainBaseAttack(): Boolean {
    searchOpponents()
    return enterMainScreen()
}