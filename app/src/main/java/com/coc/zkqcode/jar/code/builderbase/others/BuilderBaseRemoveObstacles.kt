package com.coc.zkqcode.jar.code.builderbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.remove.enterEditMode
import com.coc.zkqcode.jar.code.universal.remove.removeAllBuildings
import com.coc.zkqcode.jar.code.universal.remove.removeObstacles
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun builderBaseRemoveObstacles(): Boolean {
    if (!getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_REMOVE_OBSTACLES.key)) return true
    val worker = WorkerAndResearch.detectWorkerNumber(BaseType.Builder)
    val resources = recognizeResources()
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.BUILDER_BASE_REMOVE_OBSTACLES, InGamesVars.currentAccountNumber)
    // Skip if obstacle removal was done within 24 hours
    if (!checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，今天已移除障碍物，暂不移除")
        return true
    }

    // Resource threshold check
    if (worker.total == 2) {
        if (resources.gold < 600000 || resources.elixir < 600000) {
            ShowMessage("账号${InGamesVars.currentAccountNumber}，检测金：${resources.gold}，检测水：${resources.elixir}\n不足60万，暂不移除")
            return true
        }
    } else {
        if (resources.gold < 300000 || resources.elixir < 300000) {
            ShowMessage("账号${InGamesVars.currentAccountNumber}，检测金：${resources.gold}，检测水：${resources.elixir}\n不足30万，暂不移除")
            return true
        }
    }

    ShowMessage("账号${InGamesVars.currentAccountNumber}，第一区域准备移除障碍物")
    zoomSmallBuilderBase()
    enterEditMode()
    // First Area Operations
    removeObstacles()
    swipe(1036, 78, 1100, 455, 700)
    removeObstacles()

    // Second Area Operations (conditional on worker count)
    if (worker.total == 2) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，当前已解锁第二区域")
        swipe(672, 160, 1206, 430, 700)
        TouchActions.tap(1228, 316, delayTime = 500)
        removeAllBuildings()
        removeObstacles()
        swipe(867, 163, 1211, 460, 700)
        removeObstacles()
    }

    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}