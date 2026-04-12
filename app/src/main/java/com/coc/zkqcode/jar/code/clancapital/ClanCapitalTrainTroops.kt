package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen

suspend fun enterClanCapital(): Boolean {
    zoomSmallMainBase()
    clickRightBottom(1)
    TouchActions.tap(443, 582, delayTime = 800)
    return findMultiColorsUntil(schemas = listOf(MyColors.CapitalCoin), duration = 500) != null
}

suspend fun clanCapitalTrainTroops(): Boolean {
    if (!enterClanCapital()) return enterMainScreen()
    val trainTroopButton = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1000)
    if (trainTroopButton != null) {
        TouchActions.tap(trainTroopButton.x, trainTroopButton.y, delayTime = 300)
    }
    val trashBinButton = findMultiColorsUntil(schemas = listOf(MyColors.TrashBin), duration = 300)
    if (trashBinButton != null) {
        TouchActions.tap(trashBinButton.x, trashBinButton.y, delayTime = 300)
        TouchActions.swipe(924, 584, 143, 682)
        delayWithMultiplier(300)
        TouchActions.tap(782, 614)//冰霜法术
        repeat(2) {
            TouchActions.tap(943, 611)
        }//骷髅法术
        repeat(3) {
            TouchActions.tap(634, 473)
        }//治疗法术
        repeat(7) {
            TouchActions.tap(777, 480)//雷电法术
        }
        repeat(12) {
            TouchActions.tap(355, 620)//矿工
        }
        TouchActions.swipe(143, 682, 924, 584)
        delayWithMultiplier(300)
        val capitalBarbarianButton = findMultiColorsUntil(schemas = listOf(MyColors.CapitalBarbarian), duration = 300)
        if (capitalBarbarianButton != null) {
            repeat(20) {
                TouchActions.tap(capitalBarbarianButton.x, capitalBarbarianButton.y)//野蛮人
            }
            repeat(2) {
                TouchActions.tap(781, 476)//飞艇
            }
        }
        TouchActions.tap(870, 230)
        TouchActions.tap(1120, 35)
    }
    return true
}
