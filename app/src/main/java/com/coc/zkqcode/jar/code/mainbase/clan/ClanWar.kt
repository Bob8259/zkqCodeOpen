package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil

suspend fun detectMyClanWarPosition() {
    val clanWarInfo = findMultiColors(MyColors.ClanWarInfo)
    if (clanWarInfo != null) {
        TouchActions.tap(clanWarInfo.x, clanWarInfo.y)
        TouchActions.tap(755, 60)
    }
    val ourTeam = findMultiColorsUntil(schemas = listOf(MyColors.OurTeam), duration = 1000)
    if (ourTeam != null) {
        delayWithMultiplier(1000)
        while (true)
            TouchActions.swipe(320, 570, 320, 210)

    }
}