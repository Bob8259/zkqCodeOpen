package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.remove.enterEditMode
import com.coc.zkqcode.jar.code.universal.remove.removeAllBuildings
import com.coc.zkqcode.jar.code.universal.remove.removeObstacles
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import java.util.Calendar

suspend fun builderBaseRemoveObstacles(): Boolean {
    val worker = BuilderBaseWorkerAndResearch.detectWorkerNumber()
    val resources = recognizeResources()
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.BUILDER_BASE_REMOVE_OBSTACLES, InGamesVars.currentAccountNumber)
    val lastCleaningTime = readMemory(storageKey).toIntOrNull()
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    // Check if obstacle removal was done today
    if (lastCleaningTime != null && lastCleaningTime == currentDay) {
        ShowMessage("今天已移除障碍物，暂不移除")
        return true
    }

    // Resource threshold check
    if (worker.total == 2) {
        if (resources.gold < 600000 || resources.elixir < 600000) {
            ShowMessage("检测金：${resources.gold}，检测水：${resources.elixir}\n不足60万，暂不移除")
            return true
        }
    } else {
        if (resources.gold < 300000 || resources.elixir < 300000) {
            ShowMessage("检测金：${resources.gold}，检测水：${resources.elixir}\n不足30万，暂不移除")
            return true
        }
    }

    ShowMessage("第一区域准备移除障碍物")
    enterEditMode()
    zoomSmallBuilderBase()
    // First Area Operations
    removeObstacles()
    swipe(1036, 78, 1100, 455, 700)
    removeObstacles()

    // Second Area Operations (conditional on worker count)
    if (worker.total == 2) {
        ShowMessage("当前已解锁第二区域")
        swipe(672, 159, 1206, 420, 700)
        TouchActions.tap(1228, 316, delayTime = 500)
        removeAllBuildings()
        removeObstacles()
        swipe(867, 163, 1211, 450, 700)
        removeObstacles()
    }

    // Update the storage with the current hour after completion
    writeMemory(storageKey, currentDay.toString())
    return enterMainScreen()
}