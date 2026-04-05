package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.remove.enterEditMode
import com.coc.zkqcode.jar.code.universal.remove.removeObstacles
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseRemoveObstacles(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.REMOVE_OBSTACLES.key)) return true
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.MAIN_BASE_REMOVE_OBSTACLES, InGamesVars.currentAccountNumber)
    // Skip if obstacle removal was done within 24 hours
    if (!checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，今天已移除障碍物，暂不移除")
        return true
    }
    val resources = recognizeResources()
    if (resources.gold < 300000 || resources.elixir < 300000) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，检测金：${resources.gold}，检测水：${resources.elixir}\n不足30万，暂不移除")
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        return true
    }
    ShowMessage("账号${InGamesVars.currentAccountNumber}，准备移除主世界障碍物")
    enterEditMode()
    zoomSmallMainBase()
    removeObstacles()
    enhanceRemoveObstacles()
    removeLowerObstacles()
    TouchActions.swipe(981, 86, 290, 470, delayTime = 500)
    removeObstacles()
    enhanceRemoveObstacles()
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}

private suspend fun enhanceRemoveObstacles() {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.REMOVE_OBSTACLES_ENHANCEMENT.key)) {
        repeat(25) {
            val randomX = (100..1180).random()
            val randomY = (1..560).random()
            TouchActions.tap(randomX, randomY, delayTime = 120)
            performRemoveSequence()
        }
    }
}

private suspend fun removeLowerObstacles() {
    val step = 25
    val yStart = 525
    val yEnd = 575

    // Loop through Y coordinates
    for (y in yStart..yEnd step step) {

        // Calculate the percentage of progress from top to bottom (0.0 to 1.0)
        val progress = (y - yStart).toDouble() / (yEnd - yStart)

        // Interpolate the X boundaries for the current Y
        // Left edge moves from 549 to 608
        val currentXStart = (549 + (608 - 549) * progress).toInt()
        // Right edge moves from 794 to 759
        val currentXEnd = (794 + (759 - 794) * progress).toInt()

        // Loop through X coordinates for this specific "row"
        for (x in currentXStart..currentXEnd step step) {
            // 1. Tap the target area inside the trapezoid
            TouchActions.tap(x, y, delayTime = 120)

            // 2. Perform the "Remove" operation sequence
            performRemoveSequence()
        }
    }
}

// Helper function to keep the loop clean
private suspend fun performRemoveSequence() {
    TouchActions.tap(616, 488, delayTime = 50)
    repeat(2) {
        TouchActions.tap(14, 558)
    }
}