package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.touchactions.TouchActions
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
    
}