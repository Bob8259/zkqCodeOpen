package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.jar.code.universal.enterMainScreen
import kotlinx.coroutines.delay

object MainBaseScript {
    suspend fun playMainBase(): Boolean {
        if (!enterMainScreen()) return false
        TrainTroops.trainTroops()
        return true
    }

}