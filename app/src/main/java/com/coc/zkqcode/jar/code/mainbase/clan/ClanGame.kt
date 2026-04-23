package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions

suspend fun clanGame(): Boolean {
    val calendar = findMultiColors(MyColors.Calendar)
    if (calendar != null) {
        TouchActions.tap(calendar.x, calendar.y)
        for (i in 0..2) {
            val openClanGame = findMultiColorsUntil(schemas = listOf(MyColors.OpenClanGame), duration = 500)
            if (openClanGame != null) {
                ShowMessage("当前部落竞赛未制作完成，正在收集数据中，请稍后。")
                TouchActions.tap(openClanGame.x, openClanGame.y, delayTime = 1000)
                //TODO upload a screenshot to the server
                TouchActions.swipe(700, 500, 600, 20, delayAfter = 300)
                //TODO upload a screenshot to the server again

            }
            TouchActions.swipe(600, 660, 600, 120)
        }
    }
    return enterMainScreen()
}