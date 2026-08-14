package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun distributeClanLeagueRewards(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DISTRIBUTE_CLAN_LEAGUE_REWARDS.key)) return true
    val trainTroopButton = findMultiColorsUntil(listOf(MyColors.TrainTroops, MyColors.TrainTroops2), 100)
    if (trainTroopButton != null) {
        TouchActions.tap(60, 420)
    }

    // Loop for 10 seconds; scale by the delay multiplier since all waits inside use delayWithMultiplier
    val startTime = System.currentTimeMillis()
    val multiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat() ?: logAndRestart("Failed to get delayMultiplier")
    val duration = (30_000 * multiplier).toLong()
    while (System.currentTimeMillis() - startTime < duration) {
        val distributionButton = findMultiColors(MyColors.DistributeClanLeagueRewards)
        if (distributionButton != null) {
            TouchActions.tap(distributionButton.x, distributionButton.y)
        }
        val midGreenButton = findMultiColorsUntil(listOf(MyColors.MiddleGreenYes, MyColors.MiddleGreenConfirm), 10)
        if (midGreenButton != null) {
            TouchActions.tap(midGreenButton.x, midGreenButton.y)
            return enterMainScreen()
        }
        repeat(3) {
            val selectPlayerButton = findMultiColors(MyColors.SelectPlayerButton)
            if (selectPlayerButton != null) {
                TouchActions.tap(selectPlayerButton.x, selectPlayerButton.y)
            }
        }
        TouchActions.swipe(620, 560, 620, 300)
        delayWithMultiplier(500)
    }
    return enterMainScreen()
}