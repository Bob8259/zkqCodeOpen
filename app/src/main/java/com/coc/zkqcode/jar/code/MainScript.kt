package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.mainbase.playMainBase
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeAllExistingBuildings
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.upgradeBuildings
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.enterMainBase
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeGameFiles
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


suspend fun runMainScript() {
    // 1. Initialize/update local memory state
    val startAccount = readMemory(StorageKeys.ACCOUNT_NUMBER).toIntOrNull() ?: 1
    val accountTotal = getConfigOrStop(Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key).toInt()

    // 2. Find the first enabled account starting from the saved position
    val activeAccount = findAndActivateAccount(startAccount..accountTotal) ?: return
    // 3. Execute main logic loop
    InGamesVars.currentAccountNumber = activeAccount
    InGamesVars.currentGameVersion = GameVersion.fromId(getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGamesVars.currentAccountNumber}").toInt())

    while (currentCoroutineContext().isActive) {
        while (currentCoroutineContext().isActive) {

            // Test code
//            runTestCode()
            if (!writeGameFiles()) break
            if (!enterMainScreen(true)) {
                ShowMessage("进入游戏失败")
                break // Break inner loop and recheck account status
            }
            if (!playBuilderBase()) {
                ShowMessage("夜世界对战完成，准备进入主世界")
                break // Break inner loop and recheck account status
            }
            if (!playMainBase()) {
                ShowMessage("主世界对战完成，准备切换账号")
                break // Break inner loop and recheck account status
            }
        }
        // Circularly search for the next enabled account, wrapping back to currentAccountNumber (inclusive)
        val searchOrder = ((InGamesVars.currentAccountNumber + 1)..accountTotal) + (1..InGamesVars.currentAccountNumber)
        val nextAccount = findAndActivateAccount(searchOrder) ?: return
        InGamesVars.currentAccountNumber = nextAccount
        InGamesVars.currentGameVersion = GameVersion.fromId(getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGamesVars.currentAccountNumber}").toInt())

        delay(2000)
    }
}

/**
 * Searches [searchOrder] for the first enabled account.
 * If none is found, loops showing a message until the coroutine is canceled,
 * then returns null — the caller should return immediately on null.
 */
private suspend fun findAndActivateAccount(searchOrder: Iterable<Int>): Int? {
    val found = searchOrder.firstOrNull { id ->
        getConfigOrStop("${Schema.ACCOUNT_SETTINGS.ISOPEN.key}$id") == "1"
    }
    if (found == null) {
        while (currentCoroutineContext().isActive) {
            ShowMessage("当前未开启任何账号\n请勾选要开启的账号。")
            delay(2500)
        }
    }
    return found
}

private suspend fun runTestCode() {
    while (true) {
        enterMainScreen()
        upgradeAllExistingBuildings(listOf("野蛮人之王"), BaseType.Main)
        delayWithMultiplier(10000000)

    }
}

