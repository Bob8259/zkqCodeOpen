package com.coc.zkqcode.jar.code

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.auth.displayAds
import com.coc.zkqcode.jar.code.auth.userAuth
import com.coc.zkqcode.jar.code.builderbase.playBuilderBase
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.herohall.upgradeEquipmentsAndPets
import com.coc.zkqcode.jar.code.mainbase.playMainBase
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.cloud.checkCloudExpiry
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.create.batchCreateAccounts
import com.coc.zkqcode.jar.code.universal.ctrl.ZkqWsClient
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.preCheck
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.checkScheduledUpgradeNotifications
import com.coc.zkqcode.jar.code.universal.smalltools.checkUpgradeTimers
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeGameFiles
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.tapDataNumberEntryButtons
import com.coc.zkqcode.jar.code.universal.smalltools.scheduling.checkAndCoolDown
import com.coc.zkqcode.jar.code.universal.smalltools.scheduling.runRandomSchedulingScript
import com.coc.zkqcode.jar.code.universal.smalltools.scheduling.runUpgradeTimeSchedulingScript
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.statehelper.AppMode
import com.coc.zkqcode.statehelper.AppStateManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

// Hot-update signal handshake bounds: the host caps one update at 10 minutes,
// so waiting longer than 15 minutes means the host is gone — skip and keep running.
private const val UPDATE_SIGNAL_EMIT_RETRIES = 3
private const val UPDATE_SIGNAL_EMIT_RETRY_DELAY_MS = 5000L
private const val UPDATE_SIGNAL_AWAIT_TIMEOUT_MS = 15 * 60_000L


suspend fun runMainScript() {
    val scriptStartTime = (System.currentTimeMillis() / 1000).toInt()
    if (GlobalVars.runDataNumberEntryOnly) {
        tapDataNumberEntryButtons()
        finishDataNumberEntryOnlyRun()
        return
    }

    waitForPlay()
    // setZKQInputMethod()
    accountMessage("检测到设置已更新\n即将重新运行")
    delay(200.milliseconds)
    userAuth()
    val updateOption = checkHotUpdateAndScheduling() ?: return
    val (isBatchCreate, accountStart, accountTotal) = resolveAccountRange()

    val startAccount = readMemory(StorageKeys.ACCOUNT_NUMBER).toIntOrNull() ?: accountStart
    // Reset startAccount to accountStart if it is out of range (e.g. account count was reduced)
    val safeStartAccount = if (startAccount !in accountStart..accountTotal) accountStart else startAccount

    // 2. Find the first enabled account starting from the saved position, wrapping around
    val initialSearchOrder = (safeStartAccount..accountTotal) + (accountStart until safeStartAccount)
    val activeAccount = findAndActivateAccount(initialSearchOrder, isBatchCreate) ?: return
    // 3. Execute main logic loop
    InGameVars.currentAccountNumber = activeAccount
    ZkqWsClient.notifyStateChanged("上号 #$activeAccount 成功")
    InGameVars.currentGameVersion = resolveGameVersion(isBatchCreate)
    InGameVars.adTime = (accountTotal * 5).coerceIn(10, 30)
    batchCreateAccounts()//Create all needed accounts first.
    // Track elapsed time for periodic hot update checks (updateOption 2)
    var lastUpdateCheckTime = System.currentTimeMillis()
    var nextUpdateInterval = (3600_000L) + (Math.random() * 7200_000L).toLong()
    while (currentCoroutineContext().isActive) {
        if (InGameVars.qrLinkFlowActiveCount > 0) {
            delay(1000.milliseconds)
            continue
        }
       runTestCode()
        runAccountStepBlock()
        // Check scheduled upgrade notifications every cycle (runs in background)
        checkScheduledUpgradeNotifications()
        // Check cloud expiry notification (runs every cycle, 24h cooldown prevents spam)
        checkCloudExpiry()
        // Circularly search for the next enabled account, wrapping back to currentAccountNumber (inclusive)
        // Emit hot update signal at safe point, throttled to once every 2-3 hours randomly
        if (updateOption == 2 && System.currentTimeMillis() - lastUpdateCheckTime >= nextUpdateInterval) {
            requestHotUpdateCheck()
            lastUpdateCheckTime = System.currentTimeMillis()
            nextUpdateInterval = (3600_000L) + (Math.random() * 7200_000L).toLong()
        }
        val searchOrder = ((InGameVars.currentAccountNumber + 1)..accountTotal) + (accountStart..InGameVars.currentAccountNumber)
        val nextAccount = findAndActivateAccount(searchOrder, isBatchCreate) ?: return

        InGameVars.currentAccountNumber = nextAccount
        ZkqWsClient.notifyStateChanged("上号 #$nextAccount 成功")
        writeMemory(StorageKeys.ACCOUNT_NUMBER, nextAccount.toString())
        InGameVars.currentGameVersion = resolveGameVersion(isBatchCreate)
        checkScheduledReboot(scriptStartTime)
    }
}

private fun finishDataNumberEntryOnlyRun() {
    GlobalVars.runDataNumberEntryOnly = false
    GlobalVars.isPlaying.value = false
    AppStateManager.setMode(AppMode.Main)
}

private suspend fun runTestCode() {
    if (GlobalVars.configStates[Schema.GLOBAL_SETTINGS.DEBUG_MODE.key]?.value == "1") {
        while (
            currentCoroutineContext().isActive &&
            GlobalVars.configStates[Schema.GLOBAL_SETTINGS.DEBUG_MODE.key]?.value == "1"
        ) {
            ShowMessage("测试代码开始")
            ShowMessage(WorkerAndResearch.detectWorkerNumber(BaseType.Main).toString())
            delay(2000.milliseconds)
        }
    }
}


private suspend fun cleanUp() {
    RunShell.runNoOutput("input keyevent 3") // Press home
    RunShell.runNoOutput("input keyevent 4") // Press back
    // Check resolution and warn if not 720x1280
    val wmOutput = RunShell.run("wm size", checkIsPlaying = false)
    val sizeLine = wmOutput.lastOrNull { it.contains("Override size") } ?: wmOutput.lastOrNull { it.contains("Physical size") }
    val resolution = sizeLine?.substringAfter(":")?.trim() ?: "unknown"
    if (resolution != "720x1280") {
        ShowMessage("当前分辨率${resolution}，最佳运行分辨率720*1280，DPI320。\n该分辨率可能会出现异常，建议调整分辨率以获得最佳运行效果。", isChecking = false)
        delayWithMultiplier(2000)
    }
}

/**
 * Emits the hot-update signal and waits (bounded) for the host to finish.
 * Never hangs the main loop: retries a failed tryEmit (buffer full),
 * self-completes the deferred when no listener consumes the signal, and
 * skips the check on timeout so the bot keeps running.
 */
private suspend fun requestHotUpdateCheck() {
    val deferred = CompletableDeferred<Unit>()
    var emitted = false
    for (attempt in 1..UPDATE_SIGNAL_EMIT_RETRIES) {
        if (GlobalVars.updateCheckSignal.tryEmit(deferred)) {
            emitted = true
            break
        }
        LogHelper.showDebugInfo("MainScript: update signal emit failed (attempt $attempt/$UPDATE_SIGNAL_EMIT_RETRIES), buffer full")
        if (attempt < UPDATE_SIGNAL_EMIT_RETRIES) delay(UPDATE_SIGNAL_EMIT_RETRY_DELAY_MS.milliseconds)
    }
    if (!emitted) {
        // Host listener not consuming the flow: unblock ourselves and skip this check
        deferred.complete(Unit)
        LogHelper.showDebugInfo("MainScript: no hot-update listener consumed the signal, skipping this update check")
        return
    }
    val finished = withTimeoutOrNull(UPDATE_SIGNAL_AWAIT_TIMEOUT_MS.milliseconds) { deferred.await() }
    if (finished == null) {
        // Host did not complete in time: skip this check and keep the main loop alive
        deferred.complete(Unit)
        LogHelper.showDebugInfo("MainScript: hot-update check timed out after ${UPDATE_SIGNAL_AWAIT_TIMEOUT_MS / 60_000L} minutes, skipping")
    }
}

/** Handles hot-update signal on start and random-scheduling gate.
 *  Returns updateOption, or null if random scheduling took over. */
private suspend fun checkHotUpdateAndScheduling(): Int? {
    val updateOption = getConfigOrStop(Schema.GLOBAL_SETTINGS.AUTO_UPDATE.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.AUTO_UPDATE.displayName} 必须是数字，请检查配置")
    if (updateOption == 1 || updateOption == 2) {
        requestHotUpdateCheck()
    }
    val isRandomScheduling = getConfigOrStop(Schema.GLOBAL_SETTINGS.RANDOM_SCHEDULING.key) == "1"
    val isUpgradeTimeScheduling = getConfigOrStop(Schema.GLOBAL_SETTINGS.UPGRADE_TIME_SCHEDULING.key) == "1"
    if (isUpgradeTimeScheduling) {
        runUpgradeTimeSchedulingScript()
        return null
    }
    if (isRandomScheduling) {
        runRandomSchedulingScript()
        return null
    }
    return updateOption
}

private data class AccountRange(val isBatchCreate: Boolean, val accountStart: Int, val accountTotal: Int)

/** Reads config to determine the account iteration range. */
private fun resolveAccountRange(): AccountRange {
    val isBatchCreate = getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) == "1"
    val accountTotal: Int = if (isBatchCreate) {
        getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_END_ID.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.CREATE_END_ID.displayName} 必须是数字，请检查配置")
    } else {
        getConfigOrStop(Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.ACCOUNT_COUNT.displayName} 必须是数字，请检查配置")
    }
    val accountStart: Int = if (isBatchCreate) {
        getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_START_ID.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.CREATE_START_ID.displayName} 必须是数字，请检查配置")
    } else {
        1
    }
    return AccountRange(isBatchCreate, accountStart, accountTotal)
}

/** Resolves the current account's GameVersion, forcing GLOBAL in batch-create mode. */
private fun resolveGameVersion(isBatchCreate: Boolean): GameVersion {
    return if (isBatchCreate) {
        GameVersion.GLOBAL
    } else {
        GameVersion.fromId(
            getConfigOrStop("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}${InGameVars.currentAccountNumber}").toIntOrNull()
                ?: logAndRestart("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.displayName} 必须是数字，请检查配置")
        )
    }
}


/**
 * Shared account step block used by both sequential and random scheduling modes.
 * Kills background apps, returns to home screen, then runs the full flow:
 * writeGameFiles → enterMainScreen → playBuilderBase → playMainBase.
 *
 * @return true if the full flow completed, false if any step failed early.
 */
suspend fun runAccountStepBlock(): Boolean {
    if (InGameVars.qrLinkFlowActiveCount > 0) return false
    // Reset per-run wall-completeness state so it never leaks across accounts
    InGameVars.wallsCompleteBases.clear()
    checkAndCoolDown()
    cleanUp()
    displayAds()
    RunShell.runNoOutput("input keyevent 3")
    RunShell.runNoOutput("am kill-all")
    if (!writeGameFiles()) {
        delayWithMultiplier(2000)
        return false
    }
    if (!enterMainScreen(true)) {
        accountMessage("进入游戏失败")
        return false
    }
    if (!preCheck()) {
        return false
    }
    if (!playBuilderBase()) {
        return false
    }
    if (!playMainBase()) {
        return false
    }
    // Check upgrade timers and send notification if needed (before exiting the game)
    checkUpgradeTimers()
    return true
}


/**
 * Searches [searchOrder] for the first enabled account.
 * When [skipIsOpenCheck] is true, all accounts are treated as active (used for batch-create mode).
 * If none is found, loops showing a message until the coroutine is cancelled,
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
            delay(2500.milliseconds)
        }
    }
    return found
}

private suspend fun checkScheduledReboot(scriptStartTime: Int) {
    val isScheduledRestartEnabled = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.SCHEDULED_RESTART.key]?.value == "1"
    if (isScheduledRestartEnabled) {
        val intervalMinutes = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.RESTART_INTERVAL.key]?.value?.toLongOrNull() ?: 60L
        val currentTimeSeconds = (System.currentTimeMillis() / 1000).toInt()
        if (currentTimeSeconds - scriptStartTime >= intervalMinutes * 60) {
            accountMessage(Strings.scheduledRebootMessage)
            delay(2000.milliseconds)
            RunShell.runNoOutput("reboot", false)
        }
    }
}

