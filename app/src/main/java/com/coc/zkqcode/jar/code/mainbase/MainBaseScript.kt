package com.coc.zkqcode.jar.code.mainbase


import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseTrainTroops
import com.coc.zkqcode.jar.code.mainbase.others.mainBaseRemoveObstacles
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun playMainBase(): Boolean {
    if (!enterMainScreen()) return false
    if (!mainBaseRemoveObstacles()) return false
    if (!mainBaseTrainTroops()) return false

    return true
}