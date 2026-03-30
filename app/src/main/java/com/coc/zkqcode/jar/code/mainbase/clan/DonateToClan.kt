package com.coc.zkqcode.jar.code.mainbase.donate

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.walls.calculateResourcesPercentage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.mainbase.attack.mainBaseAttack
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResources
import com.coc.zkqcode.jar.code.universal.smalltools.checkReconnections
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

suspend fun donateToClan(): Boolean {
    val notJoinClan = findMultiColors(schema = MyColors.NotJoinClanFlag, increment = 1)
    if (notJoinClan != null) return true
    val isDonateEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_SETTING.key)
    if (!isDonateEnabled) return true
    val donationTimeInterval =
        getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_DETECT_INTERVAL.displayName} 必须是数字，请检查配置")
    // Clamp lowerThreshold to a minimum of 25
    val lowerThreshold =
        (getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_START_THRESHOLD.displayName} 必须是数字，请检查配置")).coerceAtLeast(25)
    // Clamp higherThreshold to a maximum of 95
    val higherThreshold =
        (getConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.DONATION_FARMING_STOP_THRESHOLD.displayName} 必须是数字，请检查配置")).coerceAtMost(95)

    val startTime = System.currentTimeMillis()
    val intervalMillis = donationTimeInterval * 1000L
    // Track farming mode state across donation loop iterations
    var inFarmingMode = false
    var lastBattleTime = 0L
    while (System.currentTimeMillis() - startTime < intervalMillis) {
        // Capture a single frame buffer and reuse it for all color checks in this iteration
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndRestart("in donateToClan, screen capture failed.")

        val clanChat = findMultiColors(schema = MyColors.ClanChatIcon, byteBuffer = screenBuffer, increment = 1)
        if (clanChat != null) {
            // Tap the clan chat icon to open the chat panel
            TouchActions.tap(clanChat.x, clanChat.y, delayTime = 500)
            TouchActions.tap(40, 608, delayTime = 500)//Goto bottom
            continue
        }
        val iUnderstandButton = findMultiColors(schema = MyColors.IUnderstand, byteBuffer = screenBuffer, increment = 1)
        if (iUnderstandButton != null) {
            TouchActions.tap(iUnderstandButton.x, iUnderstandButton.y, delayTime = 500)
            continue
        }
        val donationButton = findMultiColors(MyColors.DonationButton, byteBuffer = screenBuffer, increment = 1)
        if (donationButton != null) {
            TouchActions.tap(donationButton.x, donationButton.y, delayTime = 500)
            donateActions()
            TouchActions.swipe(1155, 237, -500, 250)//Swipe troops
            TouchActions.swipe(1155, 480, -500, 480)//Spells
            donateActions()
            clickRightBottom(1)
            continue
        } else {
            val previousDonation = findMultiColors(MyColors.PreviousDonation, byteBuffer = screenBuffer, increment = 1)
            if (previousDonation != null) {
                TouchActions.tap(previousDonation.x, previousDonation.y, delayTime = 500)
                continue
            }
        }
        val resources = calculateResourcesPercentage(BaseType.Main)
        // Default to 0; only updated when the dark elixir icon is visible on screen
        var darkElixirPercentage: Int = 0
        val darkElixirIcon = findMultiColors(schema = MyColors.DarkElixirIcon, increment = 1)
        if (darkElixirIcon != null) {
            // darkElixirBar is the rightmost colored point on the dark elixir bar
            val darkElixirBar = findMultiColors(schema = MyColors.DarkElixirColor)
            // x = 1260 → 0%, x = 1079 → 100%; bar shrinks leftward as storage fills, so invert the direction
            darkElixirPercentage = if (darkElixirBar != null) {
                ((1260 - darkElixirBar.x) * 100 / (1260 - 1079)).coerceIn(0, 100)
            } else {
                0
            }
        }
        // Show remaining detection time in seconds
        val remainingSeconds = (intervalMillis - (System.currentTimeMillis() - startTime)) / 1000
        // Display dark elixir as a percentage when unlocked, or as 未解锁 when the icon is absent
        val darkElixirDisplay = if (darkElixirIcon != null) "暗黑重油：${darkElixirPercentage}%" else "暗黑重油：未解锁"
        ShowMessage("账号${InGamesVars.currentAccountNumber}，捐兵检测中，剩余${remainingSeconds}秒结束\n当前资源百分比：\n圣水：${resources.elixir}%\n${darkElixirDisplay}")

        // Check whether resources have recovered above the higher threshold
        val isDarkElixirUnlocked = darkElixirIcon != null
        val bothRecovered = resources.elixir >= higherThreshold &&
                (!isDarkElixirUnlocked || darkElixirPercentage >= higherThreshold)
        if (bothRecovered) {
            inFarmingMode = false
        }
        // Check whether either resource is below the lower threshold
        val isResourceLow = resources.elixir < lowerThreshold ||
                (isDarkElixirUnlocked && darkElixirPercentage < lowerThreshold)
        if (isResourceLow || inFarmingMode) {
            inFarmingMode = true
            // Fire a battle only if 3 minutes have elapsed since the last one;
            // the donation loop keeps running normally between battles
            val timeSinceLastBattle = System.currentTimeMillis() - lastBattleTime
            if (timeSinceLastBattle >= 3 * 60 * 1000L) {
                ShowMessage("账号${InGamesVars.currentAccountNumber}，资源不足，开始刷资源\n圣水：${resources.elixir}%，暗黑重油：${if (isDarkElixirUnlocked) "$darkElixirPercentage%" else "未解锁"}")
                if (!enterMainScreen()) return false
                if (!mainBaseAttack()) return false
                lastBattleTime = System.currentTimeMillis()
            }
        }
        if (!checkReconnections()) return false
        delayWithMultiplier(1500)
    }

    return enterMainScreen()
}

private suspend fun donateActions() {
    // Iterate through all donation types and tap each if found
    val donationSchemas = listOf(
        MyColors.DonateSuperTroops, MyColors.DonateNormalTroops, MyColors.DonateSpells
    )
    var emptyLoopCount = 0
    repeat(10) {
        // Early return if nothing was found in 2 consecutive loops
        if (emptyLoopCount >= 2) return

        var foundAny = false
        for (schema in donationSchemas) {
            val target = findMultiColors(schema, increment = 1)
            if (target != null) {
                foundAny = true
                repeat(3) {
                    TouchActions.tap(target.x, target.y)
                }
            }
        }
        if (foundAny) emptyLoopCount = 0 else emptyLoopCount++
    }
}