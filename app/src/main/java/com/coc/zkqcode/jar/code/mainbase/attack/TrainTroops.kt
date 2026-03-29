package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseTrainTroops(): Boolean {
    val isAttackEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_ATTACK.key)
    val isManualTrainEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.MANUAL_TRAINING.key)
    if (!isAttackEnabled || isManualTrainEnabled) return true
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.MAIN_BASE_TRAIN_TROOPS, InGamesVars.currentAccountNumber)

    if (checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，准备训练部队")
        GlobalVars.absorbEdge = 1

        // Open training menu
        var point = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1500)
        if (point != null) {
            TouchActions.tap(point.x, point.y, delayTime = 500)
        } else {
            ShowMessage("账号${InGamesVars.currentAccountNumber}，训练部队失败")
            GlobalVars.absorbEdge = 0
            return enterMainScreen()
        }

        // Verify training page
        point = findMultiColorsUntil(schemas = listOf(MyColors.AttackInTrainingPage, MyColors.AttackInTrainingPage2, MyColors.AttackInTrainingPage3), duration = 1500)
        if (point == null) {
            ShowMessage("账号${InGamesVars.currentAccountNumber}，未找到训练标志")
            GlobalVars.absorbEdge = 0
            return enterMainScreen()
        }

        // Clean Queue 1
        point = findMultiColorsUntil(schemas = listOf(MyColors.DeleteAll1), duration = 1000)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
            delayWithMultiplier(500)
            findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1500)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 500)
            }
        }

        // Train Troops Tab
        TouchActions.tap(891, 234, delayTime = 800)

        for (i in 1..8) {
            // Priority training check
            val dragonPoint = findMultiColors(schema = MyColors.TrainDragon)
            if (dragonPoint != null) {
                repeat(25) { TouchActions.tap(dragonPoint.x, dragonPoint.y, delayTime = 40) }
                break
            }

            findMultiColors(schema = MyColors.TrainGiant)?.let { p ->
                repeat(5) { TouchActions.tap(p.x, p.y, delayTime = 40) }
            }
            findMultiColors(schema = MyColors.TrainArcher)?.let { p ->
                repeat(40) { TouchActions.tap(p.x, p.y, delayTime = 40) }
            }
            findMultiColors(schema = MyColors.TrainBarbarian)?.let { p ->
                repeat(40) { TouchActions.tap(p.x, p.y, delayTime = 40) }
            }

            if (findMultiColors(schema = MyColors.GrayBarbarian) != null) break
            delayWithMultiplier(300)
        }

        // Close tab and Clean Queue 2
        TouchActions.tap(219, 139, delayTime = 1000)
        point = findMultiColorsUntil(schemas = listOf(MyColors.DeleteAll2), duration = 500)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
            delayWithMultiplier(500)
            findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1500)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 500)
            }
        }

        // Spell Tab
        TouchActions.tap(797, 420, delayTime = 1000)
        if (findMultiColorsUntil(schemas = listOf(MyColors.TrainLighteningSpell), duration = 500) != null) {
            // Optimized sequence of taps for lightning spells
            val spellCoords = listOf(351 to 621, 351 to 621, 351 to 621, 220 to 499, 91 to 494, 91 to 494, 91 to 494, 91 to 494)
            for (coord in spellCoords) {
                TouchActions.tap(coord.first, coord.second, delayTime = 50)
            }
        }

        // Close tab and Clean Queue 3
        TouchActions.tap(219, 139, delayTime = 1000)
        point = findMultiColorsUntil(schemas = listOf(MyColors.DeleteAll3), duration = 500)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
            delayWithMultiplier(500)
            findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1500)?.let {
                TouchActions.tap(it.x, it.y, delayTime = 500)
            }
        }

        // Siege Machines Tab
        TouchActions.tap(1126, 423, delayTime = 1000)
        if (findMultiColorsUntil(schemas = listOf(MyColors.TrainSiegeMachine), duration = 500) != null) {
            val siegeCoords = listOf(1047 to 543, 610 to 535, 364 to 536, 138 to 541)
            for (coord in siegeCoords) {
                TouchActions.tap(coord.first, coord.second, delayTime = 50)
            }
        }

        // Final Close
        TouchActions.tap(219, 139, delayTime = 1000)
        TouchActions.tap(1232, 65, delayTime = 300)

        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        GlobalVars.absorbEdge = 0
        return enterMainScreen()
    } else {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号今日已练兵")
        return true
    }
}