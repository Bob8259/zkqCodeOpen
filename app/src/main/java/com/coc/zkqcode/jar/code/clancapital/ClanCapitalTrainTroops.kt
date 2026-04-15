package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory


suspend fun clanCapitalTrainTroops(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAN_CAPITAL_TRAIN_TROOPS, InGameVars.currentAccountNumber
    )

    // Guard: skip training if the last training was less than 3 days (4320 minutes) ago
    if (!checkMemoryFile(storageKey, 4320)) {
        accountMessage("3天内已训练过都城部队")
        return true
    }

    if (!enterClanCapital()) return enterMainScreen()
    if (findMultiColors(MyColors.ZeroAttackTimes) != null) {
        // No attack times remaining; record the timestamp so the guard activates next cycle
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        accountMessage("都城攻击次数已用完")
        if (!enterMainScreen()) return false
    }
    val trainTroopButton = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1000)
    if (trainTroopButton != null) {
        TouchActions.tap(trainTroopButton.x, trainTroopButton.y)
    }
    var isTrainingSuccess = false
    val trashBinButton = findMultiColorsUntil(schemas = listOf(MyColors.CapitalTrainTroopPage), duration = 300)
    if (trashBinButton != null) {
        TouchActions.tap(505, 300)
        TouchActions.swipe(925, 580, 0, 680)
        delayWithMultiplier(300)
        TouchActions.tap(780, 615, delayTime = 50)//冰霜法术
        repeat(2) {
            TouchActions.tap(940, 610, delayTime = 50)
        }//骷髅法术
        repeat(3) {
            TouchActions.tap(635, 470, delayTime = 50)
        }//治疗法术
        repeat(7) {
            TouchActions.tap(780, 480, delayTime = 50)//雷电法术
        }
        val capitalMiner = findMultiColors(MyColors.CapitalMiner)
        if (capitalMiner != null) {
            isTrainingSuccess = true
            repeat(12) {
                TouchActions.tap(capitalMiner.x, capitalMiner.y, delayTime = 50)
            }
        }

        TouchActions.swipe(140, 680, 1200, 580)
        delayWithMultiplier(300)
        var capitalBarbarianButton = findMultiColorsUntil(schemas = listOf(MyColors.CapitalBarbarian, MyColors.CapitalBarbarian2), duration = 200)
        if (capitalBarbarianButton != null) {
            isTrainingSuccess = true
            repeat(5) {
                capitalBarbarianButton = findMultiColorsUntil(schemas = listOf(MyColors.CapitalBarbarian, MyColors.CapitalBarbarian2), duration = 200)
                // Snapshot into a val so the smart cast holds inside the lambda
                val capitalBarbarianSnapshot = capitalBarbarianButton
                if (capitalBarbarianSnapshot != null) {
                    repeat(10) {
                        TouchActions.tap(capitalBarbarianSnapshot.x, capitalBarbarianSnapshot.y, delayTime = 30)//野蛮人
                    }
                    delayWithMultiplier(300)
                }
            }
            repeat(2) {
                TouchActions.tap(781, 476, delayTime = 30)//飞艇
            }
        }
        TouchActions.tap(870, 300, delayTime = 800)
        // Record the training timestamp (in minutes) after confirming the troop selection
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        accountMessage("都城部队训练成功，已记录时间")
        TouchActions.tap(1120, 35)
    }
    return isTrainingSuccess
}
