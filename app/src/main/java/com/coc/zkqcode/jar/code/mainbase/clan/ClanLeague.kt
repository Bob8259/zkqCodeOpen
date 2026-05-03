package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions

suspend fun clanLeague(): Boolean {
    val startTime = System.currentTimeMillis()
    val multiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
        ?: logAndRestart("Failed to get delayMultiplier")
    val duration = (10000 * multiplier).toLong()

    val tappableSchemas = listOf(
        MyColors.ClanLeagueSeasonInfo,
        MyColors.SmallBlueInfoIcon,
        MyColors.ClaimClanLeagueReward
    )

    while (System.currentTimeMillis() - startTime < duration) {
        val captured = ScreenCaptureManager.capture(asBitmap = false)
            as? ScreenCaptureManager.CaptureResult

        // Exit the loop once the green war icon appears
        if (findMultiColors(MyColors.ClanLeagueGreenWarIcon, byteBuffer = captured) != null) {
            break
        }

        // Tap each of the three dismissible colors if found
        for (schema in tappableSchemas) {
            findMultiColors(schema, byteBuffer = captured)?.let {
                TouchActions.tap(it.x, it.y)
            }
        }

        delayWithMultiplier(100)
    }

    return enterMainScreen()
}
