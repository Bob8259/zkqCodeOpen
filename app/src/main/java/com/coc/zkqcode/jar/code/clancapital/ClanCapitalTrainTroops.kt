package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory



suspend fun clanCapitalTrainTroops(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAN_CAPITAL_TRAIN_TROOPS, InGamesVars.currentAccountNumber
    )

    // Guard: skip training if the last training was less than 3 days (4320 minutes) ago
    if (!checkMemoryFile(storageKey, 4320)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，3天内已训练过都城部队")
        return true
    }

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
        // Record the training timestamp (in minutes) after confirming the troop selection
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        ShowMessage("账号${InGamesVars.currentAccountNumber}，首都部队训练成功，已记录时间")
        TouchActions.tap(1120, 35)
    }
    return true
}
