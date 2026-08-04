package com.coc.zkqcode.jar.code.universal

import android.os.Environment
import android.graphics.Bitmap
import com.coc.zkqcode.core.app.ZKQApplication
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.inputmethod.inputTextReliable
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseDeployTroops
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.CNApkDownloader
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.push.PixelBlockEncoder
import com.coc.zkqcode.jar.code.universal.push.PushMessage
import com.coc.zkqcode.jar.code.universal.push.PushRouter
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getStaticConfig
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.isGameAtFront
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Locale


/**
 * Tracks per-account push state to avoid redundant notifications.
 * An account is "locked" after we send a kick notification;
 * it gets unlocked when the game enters successfully.
 */
private val accountPushLocked = mutableSetOf<Int>()

/**
 * Waits for the game to enter the main screen within a specified timeout.
 * Returns true if successful, false if it times out.
 */
suspend fun enterMainScreen(isDoubleCheck: Boolean = false): Boolean {
    // 1. Initialize the start time
    val startTime = System.currentTimeMillis()
   
    // Change resolution to 720x1280 / DPI 320 at runtime if enabled
    if (getStaticConfig(Schema.GLOBAL_SETTINGS.CHANGE_RESOLUTION_RUNTIME.key) == "1") {
        RunShell.runNoOutput("wm size 720x1280", checkIsPlaying = false)
        RunShell.runNoOutput("wm density 320", checkIsPlaying = false)
    }
    // 2. Get the timeout duration from GlobalVars (assumed to be in seconds)
    // We multiply by 1000 to compare milliseconds to milliseconds
    val timeoutSeconds = GlobalVars.configStates["enter_game_timer"]?.value?.toIntOrNull() ?: logAndRestart("enter main game error, can not get game timer")
    val timeoutMillis = timeoutSeconds * 1000L
    var mainBaseTutorialElements = 0
    var lastClickRightBottomTime = startTime - 5000L
    fun countdownMessage(): String = "倒计时${((timeoutMillis - System.currentTimeMillis() + startTime) / 1000).toInt()}秒"
    GlobalVars.absorbEdge = 0
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        // 3. Insert your logic to check if the main screen is actually visible
        if (!isGameAtFront()) {
            runGame()
        } else {
            if (isInHomePage(countdownMessage())) {
                if (!isDoubleCheck) {
                    accountMessage("已进入主界面")
                    // Unlock push for this account on successful enter
                    accountPushLocked.remove(InGameVars.currentAccountNumber)
                    return true
                }
                if (InGameVars.currentGameVersion == GameVersion.CN) delayWithMultiplier(1200)
                else delayWithMultiplier(500)
                if (isInHomePage(countdownMessage())) {
                    // Unlock push for this account on successful enter
                    accountPushLocked.remove(InGameVars.currentAccountNumber)
                    return true
                }
            }

            closeAdvertisements()
            // After 15s, also dismiss clan league popups
            if (System.currentTimeMillis() - startTime > 15_000L) closeClanLeaguePopups()

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickRightBottomTime >= 5000L) {
                clickRightBottom(10)
                if (!checkReconnections()) {
                    // Notify before returning so reconnect-fail path also sends kick push
                    sendGameEntryFailNotification(timeoutSeconds)
                    return false
                }
                lastClickRightBottomTime = currentTime
            }

            if (AllTutorials.checkIsInTutorial(mainBaseTutorialElements)) mainBaseTutorialElements++
        }

    }

    // Return false if the loop finishes without finding the main screen
    // Send Server酱³ push notification on game enter timeout if enabled
    // Send push notification if enabled (with cooldown)
    sendGameEntryFailNotification(timeoutSeconds)
    killGame()
    return false
}

private const val PUSH_COOLDOWN_MINUTES = 120

/**
 * Sends a kick/entry-fail push with per-account cooldown.
 * Shared by timeout exit and reconnect-dialog failure paths.
 */
private suspend fun sendGameEntryFailNotification(timeoutSeconds: Int) {
    val kickNotify = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.SERVERCHAN_KICK_NOTIFY.key]?.value == "1"
    if (!kickNotify) return

    val account = InGameVars.currentAccountNumber
    if (account in accountPushLocked) return

    if (!checkMemoryFile(
            StorageKeys.withAccountNumber(StorageKeys.KICK_NOTIFY_LAST_SENT, account),
            PUSH_COOLDOWN_MINUTES
        )
    ) return

    accountPushLocked.add(account)
    writeMemory(
        StorageKeys.withAccountNumber(StorageKeys.KICK_NOTIFY_LAST_SENT, account),
        (System.currentTimeMillis() / 60_000).toString()
    )

    // Check push provider: image-capable platforms get a screenshot, others get braille art
    val provider = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
    if (PushRouter.supportsImage(provider)) {
        // Telegram: capture screenshot and send as photo
        val screenshot = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)
        val imageBytes = PixelBlockEncoder.bitmapToJpegBytes(screenshot)
        screenshot.recycle()
        val deviceRemark = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
        val remarkKey = "remark${account}"
        val remark = GlobalVars.configStates[remarkKey]?.value.orEmpty()
        PushRouter.send(
            PushMessage(
                title = Strings.serverchanKickTitle,
                desp = Strings.serverchanKickMsg(deviceRemark, account, remark, timeoutSeconds)
            ),
            imageBytes = imageBytes,
            brailleArt = null
        )
    } else {
        // Other platforms: capture screenshot and generate braille art
        var brailleArt: String?
        val screenshot = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true)
        brailleArt = PixelBlockEncoder.encodeBitmapToBraille(screenshot)
        screenshot.recycle()
        val deviceRemark = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
        val remarkKey = "remark${account}"
        val remark = GlobalVars.configStates[remarkKey]?.value.orEmpty()
        PushRouter.send(
            PushMessage(
                title = Strings.serverchanKickTitle,
                desp = Strings.serverchanKickMsg(deviceRemark, account, remark, timeoutSeconds)
            ),
            imageBytes = null,
            brailleArt = brailleArt
        )
    }
}


suspend fun clickRightBottom(times: Int, delayTime: Int = 100) {
    repeat(times) {
        TouchActions.tap(1279, 100, false, delayTime)
    }
}


/**
 * Checks and dismisses clan league related popups (season info, blue info icon, claim reward).
 */
private suspend fun closeClanLeaguePopups() {
    var screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    listOf(
        MyColors.ClanLeagueSeasonInfo, MyColors.SmallBlueInfoIcon, MyColors.ClaimClanLeagueReward
    ).forEach { schema ->
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema, increment = 1)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
            // Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
        }
    }
}

private fun getCNGameUpdateMaxWaitMillis(): Int {
    val maxWaitMinutes = getStaticConfig(Schema.GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.key).toDoubleOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.displayName} 必须是数字，请检查配置")
    if (maxWaitMinutes <= 0.0) {
        logAndRestart("${Schema.GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.displayName} 必须大于0，请检查配置")
    }
    return (maxWaitMinutes * 60_000).toInt().coerceAtLeast(1)
}

private val cnGameUpdateApkDir: String
    get() = "${Environment.getExternalStorageDirectory().path}/Android/data/com.tencent.tmgp.supercell.clashofclans/files"

private fun shellSingleQuote(value: String): String {
    return "'${value.replace("'", "'\"'\"'")}'"
}

private suspend fun installCNGameUpdateApkIfExists() {
    val apkPath = RunShell.runAndGetFirst(
        "find ${shellSingleQuote(cnGameUpdateApkDir)} -maxdepth 1 -type f -name '*.apk' | sort | head -n 1", checkIsPlaying = true
    )
    if (apkPath.isBlank()) return

    val quotedApkPath = shellSingleQuote(apkPath)
    accountMessage("检测到国服更新安装包，开始安装")
    RunShell.runNoOutput("pm install -r $quotedApkPath; rm -f $quotedApkPath", checkIsPlaying = true)
    accountMessage("国服更新安装包已安装并删除")
}

private suspend fun updateCNGame(maxWaitMillis: Int) {
    val startTime = System.currentTimeMillis()
    var timedOut = false

    while (true) {
        val elapsedMillis = (System.currentTimeMillis() - startTime).toInt()
        if (elapsedMillis >= maxWaitMillis) {
            timedOut = true
            break
        }
        val remainingMinutes = ((maxWaitMillis - elapsedMillis).coerceAtLeast(0)) / 60_000.0
        accountMessage("国服更新等待剩余 ${String.format(Locale.US, "%.2f", remainingMinutes)} 分钟")
        delayWithMultiplier(3000)
        findMultiColors(MyColors.CNGameUpdating) ?: break
    }

    if (timedOut && getStaticConfig(Schema.GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key) == "1") {
        accountMessage("等待超时，开始增强更新流程")
        val channelIdx = getStaticConfig(Schema.GLOBAL_SETTINGS.ENHANCED_CN_UPDATE_SOURCE.key).toIntOrNull() ?: 1
        CNApkDownloader.runEnhancedUpdate(
            ZKQApplication.appContext,
            preferredSource = channelIdx - 1
        )
    } else {
        installCNGameUpdateApkIfExists()
    }
}

private suspend fun closeAdvertisements() {
    if (getStaticConfig(Schema.GLOBAL_SETTINGS.UPDATE_CN_GAME.key) == "1") {
        val updateCNGameButton = findMultiColorsUntil(listOf(MyColors.CNGameUpdate, MyColors.CNGameUpdating), 10)
        if (updateCNGameButton != null) {
            TouchActions.tap(updateCNGameButton.x, updateCNGameButton.y)
            updateCNGame(getCNGameUpdateMaxWaitMillis())
            return
        }
    }
    // 1. Capture the screen and cast safely (Use 'var' so we can update it)
    var screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.ExclusiveGift,
        MyColors.MorePointCoupon,
        MyColors.GreenConfirm,
        MyColors.CNAd,
        MyColors.ClanChat,
        MyColors.OldShopButton,
        MyColors.NewShopButton,
        MyColors.CNProsperity,
        MyColors.MagicalItem,
        MyColors.CNPuppetAd,
        MyColors.CNBackFromAwards,
        MyColors.CollectChest,
        MyColors.EditModeWrench,
        MyColors.MiddleGreenButton,
        MyColors.ExclusiveGift,
        MyColors.MorePointCoupon,
        MyColors.PassSkinAd,
        MyColors.PassAwardAd,
        MyColors.BackToCamp,
        MyColors.SkinAd,
        MyColors.CapitalBackToCamp,
        MyColors.CapitalBackToCamp2,
        MyColors.CapitalAirShip,
        MyColors.CapitalAirShip2,
        MyColors.AgreeTerms,
        MyColors.LogIn,
        MyColors.CloudPhoneShopButton,
        MyColors.TempGameContinue,
        MyColors.IncreaseRank,
        MyColors.ClanLeagueReturnCamp,
        MyColors.UpgradeNotification,
        MyColors.EnterLeague,
        MyColors.ClanWarTutorialArrow,
        MyColors.TopRightClanWarInfo,
        MyColors.ContributionActivity
    )

    // 3. Iterate through schemas
    homeSchemas.forEach { schema ->
        // Check if the current schema exists on the current screenBuffer
        val point = findMultiColors(byteBuffer = screenBuffer, schema = schema, increment = 1)

        if (point != null) {
            // If found, perform the tap
            TouchActions.tap(point.x, point.y)
            // 4. Retake the screenBuffer so the next schema check uses the updated screen
            screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false) // Use return@forEach to skip to next if capture fails
        }
    }
    // Reuse screenBuffer for remaining checks; refresh after each tap
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.UpgradeTHArrow, increment = 1)?.let {
        TouchActions.tap(it.x + 50, it.y + 100)
        screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.CollectElixirCartTutorial, increment = 1)?.let {
        TouchActions.tap(644, 377)
        repeat(10) {
            TouchActions.tap(946, 601, delayTime = 300)
        }

    }
    findMultiColorsUntil(byteBuffer = screenBuffer, schemas = listOf(MyColors.ReturnTask, MyColors.ReturnTask2, MyColors.ReturnTask3, MyColors.ReturnTask4), duration = 20, increment = 1)?.let {
        accountMessage("检测到回归教程")
        zoomSmallMainBase()
        mainBaseDeployTroops()
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TencentChildProtection, increment = 1)?.let {
        TouchActions.tap(1010, 85)
        screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.DailyLoginReward, increment = 1)?.let {
        if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_DAILY_REWARD.key)) {
            TouchActions.tap(622, 492, delayTime = 2000)
        } else {
            TouchActions.tap(it.x, it.y)
        }
        screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.ReturnAwards, increment = 1)?.let {
        // Define the coordinate pairs in order of execution
        val tapPoints = listOf(
            257 to 297, 464 to 307, 662 to 305, 267 to 512, 466 to 511, 654 to 515, 882 to 513, 1077 to 101
        )

        // Iterate through points to reduce code redundancy
        tapPoints.forEach { (x, y) ->
            TouchActions.tap(x, y, delayTime = 100)
        }
        screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.CancelEditMode, increment = 1)?.let {
        TouchActions.tap(it.x, it.y)
        TouchActions.tap(788, 464)
        screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TrainingPage, increment = 1)?.let {
        TouchActions.tap(219, 139, delayTime = 1000)//close training tap
        TouchActions.tap(1232, 65, delayTime = 300)//close training page
    }
    findMultiColors(byteBuffer = screenBuffer, schema = MyColors.MyVillageIsCalled, increment = 1)?.let {
        TouchActions.tap(625, 300, delayTime = 200)

        var gameName = getStaticConfig(Schema.GLOBAL_SETTINGS.CREATE_PREFIX.key)
        val addSuffix = getStaticConfig(Schema.GLOBAL_SETTINGS.ADD_SUFFIX_SETTING.key) == "1"

        if (addSuffix) {
            gameName += InGameVars.currentAccountNumber
        }

        inputTextReliable(gameName)
        TouchActions.tap(640, 370, delayTime = 300)
    }
}

suspend fun isInHomePage(countdownMessage: String? = null): Boolean {
    // 1. Capture the screen and cast safely
    val screenBuffer = ScreenCaptureManager.captureScreenOrRestart<ScreenCaptureManager.CaptureResult>(asBitmap = false)

    suspend fun homePageCheckFailed(message: String): Boolean {
        accountMessage(if (countdownMessage == null) message else "$countdownMessage，$message")
        delayWithMultiplier(500)
        return false
    }

    // 2. Check for the training button presence
    if (findMultiColors(byteBuffer = screenBuffer, schema = MyColors.TrainTroops, increment = 1) == null) {
        return homePageCheckFailed("未找到练兵按钮")
    }
    if (findMultiColors(byteBuffer = screenBuffer, schema = MyColors.ShopButton, increment = 1) == null) {
        return homePageCheckFailed("未找到商店按钮")
    }

    // 3. Check for any of the worker icons (Main base, Goblin workers, or Builder base)
    val workerSchemas =
        MyColors.MainBaseWorkerSchemas + listOf(MyColors.GoblinWorker, MyColors.GoblinWorker2, MyColors.GoblinResearcher, MyColors.BuilderBaseWorker, MyColors.BuilderBaseWorker2, MyColors.BuilderBaseWorker3)

    val hasWorker = workerSchemas.any { findMultiColors(byteBuffer = screenBuffer, schema = it, increment = 1) != null }
    if (!hasWorker) {
        return homePageCheckFailed("未找到工人")
    }
    return true
}