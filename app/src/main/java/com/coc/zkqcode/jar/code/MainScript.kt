package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.mainbase.clan.joinClan
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.mainbase.playMainBase
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.detectInstantBuildCost
import com.coc.zkqcode.jar.code.universal.buildings.walls.calculateResourcesPercentage
import com.coc.zkqcode.jar.code.universal.create.batchCreateAccounts
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeGameFiles
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


suspend fun runMainScript() {
    batchCreateAccounts()//Create all needed accounts first.
    // 1. Initialize/update local memory state
    val isBatchCreate = getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) == "1"
    val startAccount: Int
    val accountTotal: Int
    if (isBatchCreate) {
        // When batch-create is enabled, use the creation ID range
        startAccount = getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_START_ID.key).toInt()
        accountTotal = getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_END_ID.key).toInt()
    } else {
        startAccount = readMemory(StorageKeys.ACCOUNT_NUMBER).toIntOrNull() ?: 1
        accountTotal = getConfigOrStop(Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key).toInt()
    }
    // Reset startAccount to 1 if it exceeds accountTotal (e.g. account count was reduced)
    val safeStartAccount = if (startAccount > accountTotal) 1 else startAccount

    // 2. Find the first enabled account starting from the saved position, wrapping around
    val initialSearchOrder = (safeStartAccount..accountTotal) + (1 until safeStartAccount)
    val activeAccount = findAndActivateAccount(initialSearchOrder, isBatchCreate) ?: return
    // 3. Execute main logic loop
    InGamesVars.currentAccountNumber = activeAccount
    // In batch-create mode, force GLOBAL version for all accounts
    InGamesVars.currentGameVersion = if (isBatchCreate) {
        GameVersion.GLOBAL
    } else {
        GameVersion.fromId(getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGamesVars.currentAccountNumber}").toInt())
    }

    while (currentCoroutineContext().isActive) {
        // Test code
//        runTestCode()
        // Use labeled block to skip remaining steps on failure
        run stepBlock@{
            if (!writeGameFiles()) {
                delayWithMultiplier(2000)
                return@stepBlock
            }
            if (!enterMainScreen(true)) {
                ShowMessage("进入游戏失败")
                return@stepBlock
            }
            if (!playBuilderBase()) {
                ShowMessage("夜世界对战完成，准备进入主世界")
                return@stepBlock
            }
            if (!playMainBase()) {
                ShowMessage("主世界对战完成，准备切换账号")
                return@stepBlock
            }
        }
        // Circularly search for the next enabled account, wrapping back to currentAccountNumber (inclusive)
        val searchOrder = ((InGamesVars.currentAccountNumber + 1)..accountTotal) + (1..InGamesVars.currentAccountNumber)
        val nextAccount = findAndActivateAccount(searchOrder, isBatchCreate) ?: return

        InGamesVars.currentAccountNumber = nextAccount
        writeMemory(StorageKeys.ACCOUNT_NUMBER, nextAccount.toString())
        // In batch-create mode, force GLOBAL version for all accounts
        InGamesVars.currentGameVersion = if (isBatchCreate) {
            GameVersion.GLOBAL
        } else {
            GameVersion.fromId(getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGamesVars.currentAccountNumber}").toInt())
        }
    }
}

/**
 * Searches [searchOrder] for the first enabled account.
 * When [skipIsOpenCheck] is true, all accounts are treated as active (used for batch-create mode).
 * If none is found, loops showing a message until the coroutine is canceled,
 * then returns null — the caller should return immediately on null.
 */
private suspend fun findAndActivateAccount(
    searchOrder: Iterable<Int>,
    skipIsOpenCheck: Boolean = false
): Int? {
    // In batch-create mode, treat all accounts as active
    val found = if (skipIsOpenCheck) {
        searchOrder.firstOrNull()
    } else {
        searchOrder.firstOrNull { id ->
            getConfigOrStop("${Schema.ACCOUNT_SETTINGS.ISOPEN.key}$id") == "1"
        }
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
//        enterMainScreen()
        joinClan()
//        ShowMessage(detectInstantBuildCost().toString())
        delay(2000000)
        delay(1000)
//        ShowMessage(recognizeUpgradeResources(BaseType.Main).toString())
    }
}

