package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar
import java.util.TimeZone

// 11v11, 15v15, 30v30 button coordinates on the league registration screen
private const val LEAGUE_11V11_X = 179
private const val LEAGUE_11V11_Y = 558
private const val LEAGUE_15V15_X = 504
private const val LEAGUE_15V15_Y = 558
private const val LEAGUE_30V30_X = 865
private const val LEAGUE_30V30_Y = 558

// Fallback coordinate for confirm registration button (when ColorSchema not matched)
private const val LEAGUE_CONFIRM_X = 1010
private const val LEAGUE_CONFIRM_Y = 641

/**
 * Check if current time is within Clan War League period (1st 16:00 ~ 10th 08:00 Beijing time,
 * or 17th 16:00 ~ 26th 08:00 Beijing time).
 * During this period, normal clan war should be disabled.
 */
fun isInClanLeaguePeriod(): Boolean {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayHour = day * 100 + hour
    return (dayHour >= 116 && dayHour < 1008) || (dayHour >= 1716 && dayHour < 2608)  // 1st 16:00 ~ 10th 08:00, 17th 16:00 ~ 26th 08:00
}

/**
 * Check if current time is within league registration period (1st 16:00 ~ 3rd 08:00 Beijing time,
 * or 17th 16:00 ~ 19th 08:00 Beijing time).
 */
fun isInLeagueRegistrationPeriod(): Boolean {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"))
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val dayHour = day * 100 + hour
    return (dayHour >= 116 && dayHour < 308) || (dayHour >= 1716 && dayHour < 1908)  // 1st 16:00 ~ 3rd 08:00, 17th 16:00 ~ 19th 08:00
}

/**
 * Check whether the clan league entry indicator is visible on the home page.
 * Used to determine whether a league is actually open for registration / participation
 * during the league time window.
 *
 * @return true if the league entry indicator is found; false otherwise.
 */
suspend fun isClanLeagueEntryAvailable(): Boolean {
    return findMultiColors(MyColors.ClanLeagueEntryIndicator) != null || findMultiColors(MyColors.ClanLeagueEntryIndicator2) != null
}

/**
 * Auto-register for Clan War League during the registration period.
 * Steps:
 * ① Navigate to clan war entrance by detecting ClanLeagueEntryIndicator (instead of blind tap)
 * ② Find and tap "开联赛" (Start League) button via ColorSchema
 * ③ Select 11v11, 15v15 or 30v30 based on config (pure coordinate tap)
 * ④ Find and tap confirm registration button
 */
suspend fun startClanLeague(): Boolean {
    // 1. Check if START_CLAN_LEAGUE config is enabled
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_LEAGUE.key)) return true

    // 2. Check if within registration period
    if (!isInLeagueRegistrationPeriod()) return true

    // 3. Cooldown check (once per day, 1440 minutes)
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.START_CLAN_LEAGUE, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("今日已检测开启部落联赛，暂不重复执行")
        return true
    }

    // 4. Detect league entry indicator on the home page — only enter clan war when available
    val trainTroopButton = findMultiColorsUntil(listOf(MyColors.TrainTroops, MyColors.TrainTroops2), 100)
    if (trainTroopButton != null) {
        val leagueIndicator = findMultiColorsUntil(listOf(MyColors.ClanLeagueEntryIndicator, MyColors.ClanLeagueEntryIndicator2), 3000)
        if (leagueIndicator != null) {
            accountMessage("检测到联赛入口标志，点击进入部落战")
            TouchActions.tap(leagueIndicator.x, leagueIndicator.y)
        } else {
            accountMessage("在报名期但未检测到联赛入口标志，当前部落无联赛可开启，跳过")
            return enterMainScreen()
        }
    } else return enterMainScreen()

    // 5. Find and tap "开联赛" (Start League) button via ColorSchema
    val startLeagueButton = findMultiColorsUntil(listOf(MyColors.StartClanLeagueButton), 3000)
    if (startLeagueButton != null) {
        TouchActions.tap(startLeagueButton.x, startLeagueButton.y)
        accountMessage("找到开联赛按钮，点击进入")
    } else {
        accountMessage("未检测到开联赛按钮，可能部落战进行中，跳过本次，下次启动再尝试")
        return enterMainScreen()
    }

    // 6. Select war size (11v11, 15v15 or 30v30) based on config
    //    The dropdown stores the selected index ("0", "1", "2").
    //    For backward compatibility we also accept legacy string values ("11", "15", "30").
    val leagueSize = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_CLAN_LEAGUE_SIZE.key)
    val normalizedSize = when (leagueSize) {
        "0", "11" -> "11"
        "1", "15" -> "15"
        "2", "30" -> "30"
        else -> "15"
    }
    when (normalizedSize) {
        "11" -> {
            accountMessage("选择 11v11 对战人数")
            TouchActions.tap(LEAGUE_11V11_X, LEAGUE_11V11_Y)
        }
        "15" -> {
            accountMessage("选择 15v15 对战人数")
            TouchActions.tap(LEAGUE_15V15_X, LEAGUE_15V15_Y)
        }
        "30" -> {
            accountMessage("选择 30v30 对战人数")
            TouchActions.tap(LEAGUE_30V30_X, LEAGUE_30V30_Y)
        }
    }

    // 7. Find and tap confirm registration button
    val confirmButton = findMultiColorsUntil(listOf(MyColors.ConfirmClanLeagueRegistration), 3000)
    if (confirmButton != null) {
        TouchActions.tap(confirmButton.x, confirmButton.y)
        accountMessage("点击报名确认，联赛报名成功")
    } else {
        // Fallback: use pure coordinate if ColorSchema is not yet provided
        accountMessage("未找到确认按钮（图色待提供），尝试坐标点击")
        TouchActions.tap(LEAGUE_CONFIRM_X, LEAGUE_CONFIRM_Y)
    }

    // 8. Write cooldown timestamp and return to main screen
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return enterMainScreen()
}
