package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions

suspend fun playLadder(): Boolean {
    val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
    if (battleIcon != null) {
        TouchActions.tap(80, 630)
    }
    val joinTournament = findMultiColors(MyColors.JoinTournament)
    if (joinTournament != null) {
        TouchActions.tap(joinTournament.x, joinTournament.y)
    }
    return enterMainScreen()
}