package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun searchOpponents() {
    val battleIcon = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 500)
    if (battleIcon != null) {
        TouchActions.tap(battleIcon.x, battleIcon.y, delayTime = 500)

    }
}