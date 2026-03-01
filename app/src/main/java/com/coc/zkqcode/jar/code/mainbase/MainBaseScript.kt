package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun playMainBase(): Boolean {
    if (!enterMainScreen()) return false
    mainBaseTrainTroops()

    return true
}