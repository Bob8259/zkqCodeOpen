package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseClanWarColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.code.mainbase.clan.isInClanLeaguePeriod
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager

suspend fun leagueDonation(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LEAGUE_DONATION.key) || InGameVars.townHallLevel < 3) {
        return true
    }

    if (!isInClanLeaguePeriod()) {
        return true
    }

    accountMessage("开始联赛捐兵")

    if (!enterLeagueInterface()) {
        return enterMainScreen()
    }

    if (!enterLeaguePrepInterface()) {
        return enterMainScreen()
    }

    TouchActions.swipe(640, 300, 640, 10000, delayAfter = 500)

    TouchActions.tap(534, 477, delayTime = 500)

    // Confirm we've entered the first member's details page
    while (true) {
        val firstMemberIndicator = findMultiColors(MyColors.LeagueFirstMemberIndicator)
        if (firstMemberIndicator != null) {
            accountMessage("已进入第一个成员详情页")
            break
        }
        TouchActions.tap(534, 477, delayTime = 500)
    }

    while (true) {
        if (isAllMembersTraversed()) {
            accountMessage("已遍历完全部成员")
            break
        }

        if (shouldSkipMember()) {
            accountMessage("跳过当前成员")
            switchToNextMember()
            continue
        }

        donateToLeagueMember()

        switchToNextMember()

        delayWithMultiplier(200)
    }

    return enterMainScreen()
}

private suspend fun enterLeagueInterface(): Boolean {
    TouchActions.tap(50, 440)
    accountMessage("进入联赛界面")
    return true
}

private suspend fun enterLeaguePrepInterface(): Boolean {
    val preparePhase = findMultiColorsUntil(schemas = listOf(MyColors.LeaguePreparePhase), duration = 5000)
    if (preparePhase != null) {
        accountMessage("检测到联赛准备阶段，点击进入")
        TouchActions.tap(preparePhase.x, preparePhase.y)
        
        val whiteCloud = findMultiColorsUntil(schemas = listOf(MyColors.LeagueWhiteCloud), duration = 5000)
        if (whiteCloud != null) {
            accountMessage("已成功进入联赛准备界面")
            return true
        }
    }
    return false
}

private suspend fun isAllMembersTraversed(): Boolean {
    val grayArrow = findMultiColors(MyColors.LeagueGrayArrow)
    return grayArrow != null
}

private suspend fun shouldSkipMember(): Boolean {
    // Color-based progress bar check — skip when ≥98% full.
    val percentIndicator = findMultiColors(MyColors.LeaguePercentIndicator)
    if (percentIndicator != null) {
        accountMessage("检测到百分比指示器(进度≥98%)，跳过该成员")
        return true
    }
    return false
}

private suspend fun donateToLeagueMember() {
    val reinforceButton = findMultiColors(MyColors.LeagueReinforceRequest)
    if (reinforceButton == null) return

    TouchActions.tap(reinforceButton.x, reinforceButton.y)
    accountMessage("检测到增援请求，开始捐赠")

    val troopList = MainBaseClanWarColors.leagueDonatableTroopList

    // Read all 3 troop configs: (troopIndex, count)
    val configs = listOf(
        Schema.MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_1.key to Schema.MAIN_BASE_SETTINGS.LEAGUE_DONATION_COUNT_1.key,
        Schema.MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_2.key to Schema.MAIN_BASE_SETTINGS.LEAGUE_DONATION_COUNT_2.key,
        Schema.MAIN_BASE_SETTINGS.LEAGUE_DONATION_TROOP_3.key to Schema.MAIN_BASE_SETTINGS.LEAGUE_DONATION_COUNT_3.key,
    )

    // Collect all actively configured donations (skip "无" and count ≤ 0)
    val activeDonations = configs.mapNotNull { (troopKey, countKey) ->
        val configTroopIndex = getConfigRuntime(troopKey).toIntOrNull() ?: 0
        val count = getConfigRuntime(countKey).toIntOrNull() ?: 0
        showDebugInfo("联赛捐兵配置: configTroopIndex=$configTroopIndex, count=$count")

        if (configTroopIndex <= 0) {
            showDebugInfo("联赛捐兵: 选择了'无', 跳过")
            return@mapNotNull null
        }
        if (count <= 0) {
            showDebugInfo("联赛捐兵: count<=0, 跳过")
            return@mapNotNull null
        }

        // UI index 1+ -> troopList index 0+
        val troopIndex = configTroopIndex - 1
        if (troopIndex !in troopList.indices) {
            showDebugInfo("联赛捐兵: troopIndex=$troopIndex 越界, 跳过")
            return@mapNotNull null
        }

        val (troopName, schema) = troopList[troopIndex]
        Triple(troopName, schema, count)
    }

    if (activeDonations.isEmpty()) {
        accountMessage("未配置任何有效捐兵")
        return
    }

    var anyDonated = false
    var remainingDonations = activeDonations.toMutableList()

    // Try up to 2 screens: current screen + 1 swipe
    repeat(2) { screenAttempt ->
        if (remainingDonations.isEmpty()) return@repeat

        // Capture a single screenshot for all troop searches — same as research module
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: run {
                showDebugInfo("联赛捐兵: 截图失败")
                return@repeat
            }

        val stillRemaining = mutableListOf<Triple<String, ColorSchema, Int>>()

        // Search ALL remaining troops against the same screenshot — no priority
        for ((troopName, schema, count) in remainingDonations) {
            val target = findMultiColors(schema = schema, byteBuffer = screenBuffer, increment = 1)
            if (target != null) {
                showDebugInfo("联赛捐兵: 找到 $troopName, 捐赠 x$count")
                accountMessage("捐赠 $troopName x$count")
                repeat(count) {
                    TouchActions.tap(target.x, target.y, delayTime = 20)
                }
                anyDonated = true
            } else {
                showDebugInfo("联赛捐兵: 当前屏未找到 $troopName")
                stillRemaining.add(Triple(troopName, schema, count))
            }
        }

        remainingDonations = stillRemaining

        // If some troops still not found and this is the first attempt, swipe and try again
        if (remainingDonations.isNotEmpty() && screenAttempt == 0) {
            showDebugInfo("联赛捐兵: 滑动后重试剩余 ${remainingDonations.size} 个兵种")
            TouchActions.swipe(998, 290, -500, 290, delayAfter = 400)
        }
    }

    if (!anyDonated) {
        accountMessage("未捐赠任何兵种")
    }

    // Press back to close the donation popup before switching members
    RunShell.runNoOutput("input keyevent 4")
    accountMessage("捐赠完成")
}

private suspend fun switchToNextMember() {
    TouchActions.tap(950, 570, delayTime = 120)
}
