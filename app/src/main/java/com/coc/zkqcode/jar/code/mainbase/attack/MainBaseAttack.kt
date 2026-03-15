package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import kotlinx.coroutines.delay

suspend fun mainBaseAttack(): Boolean {
    searchOpponents()

    val maxDurationMs = 3 * 60 * 1000L // Maximum battle wait time: 3 minutes
    val startTime = System.currentTimeMillis()
    var lastShownMinute = -1

    while (true) {
        val elapsed = System.currentTimeMillis() - startTime

        // Force exit if battle has exceeded the maximum allowed duration
        if (elapsed >= maxDurationMs) {
            killGame()
            break
        }

        // Show remaining minutes message when the minute value changes
        val remainingMinutes = ((maxDurationMs - elapsed) / 60000).toInt()
        if (remainingMinutes != lastShownMinute) {
            ShowMessage("对战中，${remainingMinutes}分钟后强制退出对战")
            lastShownMinute = remainingMinutes
        }

        findMultiColors(schema = MyColors.EndBattle) ?: break

        delay(1000) // Poll every second to avoid busy-waiting
    }

    return enterMainScreen()
}