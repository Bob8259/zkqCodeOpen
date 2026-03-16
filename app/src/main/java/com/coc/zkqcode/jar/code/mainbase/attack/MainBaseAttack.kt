package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

suspend fun mainBaseAttack(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_ATTACK.key)) return true
    searchOpponentsAndDeployTroops()
    val maxDurationMs = 3 * 60 * 1000L // Maximum battle wait time: 3 minutes
    val startTime = System.currentTimeMillis()

    while (true) {
        val elapsed = System.currentTimeMillis() - startTime

        // Force exit if battle has exceeded the maximum allowed duration
        if (elapsed >= maxDurationMs) {
            killGame()
            break
        }

        // Show remaining minutes with 1 decimal place
        val remainingMinutes = (maxDurationMs - elapsed) / 60000.0

        ShowMessage("账号${InGamesVars.currentAccountNumber}，对战中，${"%.1f".format(remainingMinutes)}分钟后强制退出对战")
        val endBattleButton = findMultiColors(schema = MyColors.EndBattle)
        if (endBattleButton == null) {
            ShowMessage("账号${InGamesVars.currentAccountNumber}，未找到放弃按钮，对战结束")
            delayWithMultiplier(1000)
            TouchActions.tap(640, 610, delayTime = 1500)
            break
        }

        delay(1000) // Poll every second to avoid busy-waiting
    }

    return enterMainScreen()
}