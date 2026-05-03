package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.isInHomePage
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun clanLeague(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_LEAGUE.key)) return true

    if (findMultiColors(MyColors.ClanWarSwordIcon) != null) {
        TouchActions.tap(50, 440) // Enter clan war
    } else if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_CLAN_WAR.key)) {
        //If clan war is enabled, then the clan war code will enter the clan league page
        if (isInHomePage()) {//But if the war is not found, then we should return
            return true
        }
    } else {
        return enterMainScreen()
    }


    val tappableSchemas = listOf(
        MyColors.ClanLeagueSeasonInfo, MyColors.SmallBlueInfoIcon, MyColors.ClaimClanLeagueReward
    )
    // Exit the loop once the green war icon appears
    if (findMultiColorsUntil(listOf(MyColors.ClanLeagueGreenWarIcon), 5000) != null) {
        accountMessage("已进入联赛")
        TouchActions.tap(1220, 490)
        TouchActions.tap(760, 60)
        val myPosition = detectMyClanWarPosition()
        TouchActions.swipe(640, 300, 640, 10000, delayAfter = 500)
        TouchActions.tap(740, 450) // Tap the first opponent
        repeat(myPosition - 1) {
            TouchActions.tap(950, 570, delayTime = 120)
        }
        var attackButton = findMultiColorsUntil(listOf(MyColors.ClanWarAttack, MyColors.ClanWarAttack2), duration = 1000)
        if (attackButton != null) {
            accountMessage("已找到目标，尝试进入对战")
            executeClanWarAttack(attackButton.x, attackButton.y)
        } else {
            accountMessage("无法进攻，尝试随机寻找")
            TouchActions.swipe(640, 300, 640, 10000, delayAfter = 500)
            TouchActions.tap(740, 450) // Tap the first opponent
            repeat(30) {
                attackButton = findMultiColorsUntil(listOf(MyColors.ClanWarAttack, MyColors.ClanWarAttack2), duration = 200)
                if (attackButton != null) {
                    executeClanWarAttack(attackButton.x, attackButton.y)
                }
            }
        }

    } else {
        val startTime = System.currentTimeMillis()
        val multiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat() ?: logAndRestart("Failed to get delayMultiplier")
        val duration = (10000 * multiplier).toLong()
        while (System.currentTimeMillis() - startTime < duration) {
            val captured = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            // Tap each of the three dismissible colours if found
            for (schema in tappableSchemas) {
                findMultiColors(schema, byteBuffer = captured)?.let {
                    TouchActions.tap(it.x, it.y)
                }
            }
            clickRightBottom(1)
            delayWithMultiplier(100)
        }
    }


    return enterMainScreen()
}
