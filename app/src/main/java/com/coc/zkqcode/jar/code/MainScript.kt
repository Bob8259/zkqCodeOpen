package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.auth.displayAds
import com.coc.zkqcode.jar.code.auth.userAuth
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.clancapital.clanCapitalAttack
import com.coc.zkqcode.jar.code.clancapital.findAttackTarget
import com.coc.zkqcode.jar.code.clancapital.playClanCapital
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.clan.detectMyClanWarPosition
import com.coc.zkqcode.jar.code.mainbase.playMainBase
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.create.batchCreateAccounts
import com.coc.zkqcode.jar.code.universal.runRandomSchedulingScript
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkAndCoolDown
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeGameFiles
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


suspend fun runMainScript() {
    waitForPlay()
    accountMessage("检测到设置已更新\n即将重新运行")
    delay(200)
    userAuth()
    // Read hot update preference and emit signal if "OnStart" mode is selected
    val updateOption = getConfigOrStop(Schema.GLOBAL_SETTINGS.AUTO_UPDATE.key).toIntOrNull() ?: 0
    if (updateOption == 1 || updateOption == 2) {
        val deferred = CompletableDeferred<Unit>()
        GlobalVars.updateCheckSignal.tryEmit(deferred)
        deferred.await()
    }
    // 1. Check if random scheduling mode is enabled — takes over the entire loop
    val isRandomScheduling = getConfigOrStop(Schema.GLOBAL_SETTINGS.RANDOM_SCHEDULING.key) == "1"
    if (isRandomScheduling) {
        runRandomSchedulingScript()
        return
    }

    // 2. Initialize/update local memory state
    val isBatchCreate = getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) == "1"

    val accountTotal: Int = if (isBatchCreate) {
        getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_END_ID.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.CREATE_END_ID.displayName} 必须是数字，请检查配置")
    } else {
        getConfigOrStop(Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.displayName} 必须是数字，请检查配置")
    }
    // In batch-create mode, the lower bound comes from CREATE_START_ID
    val accountStart: Int = if (isBatchCreate) {
        getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_START_ID.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.CREATE_START_ID.displayName} 必须是数字，请检查配置")
    } else {
        1
    }

    val startAccount = readMemory(StorageKeys.ACCOUNT_NUMBER).toIntOrNull() ?: accountStart
    // Reset startAccount to accountStart if it is out of range (e.g. account count was reduced)
    val safeStartAccount = if (startAccount !in accountStart..accountTotal) accountStart else startAccount

    // 2. Find the first enabled account starting from the saved position, wrapping around
    val initialSearchOrder = (safeStartAccount..accountTotal) + (accountStart until safeStartAccount)
    val activeAccount = findAndActivateAccount(initialSearchOrder, isBatchCreate) ?: return
    // 3. Execute main logic loop
    InGameVars.currentAccountNumber = activeAccount
    // In batch-create mode, force GLOBAL version for all accounts
    InGameVars.currentGameVersion = if (isBatchCreate) {
        GameVersion.GLOBAL
    } else {
        GameVersion.fromId(
            getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGameVars.currentAccountNumber}").toIntOrNull() ?: logAndRestart("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.displayName} 必须是数字，请检查配置")
        )
    }
    InGameVars.adTime = 10.coerceAtLeast(accountTotal * 8)
    batchCreateAccounts()//Create all needed accounts first.
    // Track elapsed time for periodic hot update checks (updateOption 2)
    var lastUpdateCheckTime = System.currentTimeMillis()
    var nextUpdateInterval = (2 * 3600_000L) + (Math.random() * 3600_000L).toLong()
    while (currentCoroutineContext().isActive) {
//        runTestCode()
        runAccountStepBlock()
        // Circularly search for the next enabled account, wrapping back to currentAccountNumber (inclusive)
        // Emit hot update signal at safe point, throttled to once every 2-3 hours randomly
        if (updateOption == 2 && System.currentTimeMillis() - lastUpdateCheckTime >= nextUpdateInterval) {
            val deferred = CompletableDeferred<Unit>()
            GlobalVars.updateCheckSignal.tryEmit(deferred)
            deferred.await()
            lastUpdateCheckTime = System.currentTimeMillis()
            nextUpdateInterval = (2 * 3600_000L) + (Math.random() * 3600_000L).toLong()
        }
        val searchOrder = ((InGameVars.currentAccountNumber + 1)..accountTotal) + (accountStart..InGameVars.currentAccountNumber)
        val nextAccount = findAndActivateAccount(searchOrder, isBatchCreate) ?: return

        InGameVars.currentAccountNumber = nextAccount
        writeMemory(StorageKeys.ACCOUNT_NUMBER, nextAccount.toString())
        // In batch-create mode, force GLOBAL version for all accounts
        InGameVars.currentGameVersion = if (isBatchCreate) {
            GameVersion.GLOBAL
        } else {
            GameVersion.fromId(
                getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGameVars.currentAccountNumber}").toIntOrNull() ?: logAndRestart("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.displayName} 必须是数字，请检查配置")
            )
        }
    }
}

private suspend fun runTestCode() {
    while (true) {
        detectMyClanWarPosition()
//        ShowMessage(findMultiColors(MyColors.SpellColorAtDeploymentBar, increment = 1).toString())
        accountMessage("测试代码结束")
        delay(1000)
    }
}


/**
 * Shared account step block used by both sequential and random scheduling modes.
 * Kills background apps, returns to home screen, then runs the full flow:
 * writeGameFiles → enterMainScreen → playBuilderBase → playMainBase.
 */
suspend fun runAccountStepBlock() {
    checkAndCoolDown()
    displayAds()
    RunShell.runNoOutput("input keyevent 3")
    RunShell.runNoOutput("am kill-all")
    if (!writeGameFiles()) {
        delayWithMultiplier(2000)
        return
    }
    if (!enterMainScreen(true)) {
        accountMessage("进入游戏失败")
        return
    }
    if (!playBuilderBase()) {
        accountMessage("夜世界对战完成，准备进入主世界")
        return
    }
    if (!playMainBase()) {
        accountMessage("主世界对战完成")
        return
    }
}



/**
 * Searches [searchOrder] for the first enabled account.
 * When [skipIsOpenCheck] is true, all accounts are treated as active (used for batch-create mode).
 * If none is found, loops showing a message until the coroutine is canceled,
 * then returns null — the caller should return immediately on null.
 */
private suspend fun findAndActivateAccount(
    searchOrder: Iterable<Int>, skipIsOpenCheck: Boolean = false
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
            accountMessage("当前未开启任何账号\n请勾选要开启的账号。")
            delay(2500)
        }
    }
    return found
}


