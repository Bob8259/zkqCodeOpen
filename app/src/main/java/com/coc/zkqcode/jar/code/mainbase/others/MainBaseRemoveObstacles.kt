package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeMyResources
import com.coc.zkqcode.jar.code.universal.remove.enterEditMode
import com.coc.zkqcode.jar.code.universal.remove.removeObstacles
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import java.util.Calendar
import kotlin.math.abs

suspend fun mainBaseRemoveObstacles(): Boolean {
    val resources = recognizeMyResources()
    val storageKey = "MainBaseRemoveObstacles${InGamesVars.currentAccountNumber}"
    val lastCleaningTime = readMemory(storageKey).toIntOrNull()
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    // Simplified time check as requested
    if (lastCleaningTime != null && abs(lastCleaningTime - currentHour) < 8) {
        ShowMessage("距离上次除草不足8小时，暂不除草")
        return true
    }
    if (resources.gold < 300000 || resources.elixir < 300000) {
        ShowMessage("检测金：${resources.gold}，检测水：${resources.elixir}\n不足30万，暂不除草")
        return true
    }
    ShowMessage("准备移除主世界障碍物")
    enterEditMode()
    zoomSmallMainBase()
    removeObstacles()
    return enterMainScreen()
}

private suspend fun removeLowerObstacles(){

}