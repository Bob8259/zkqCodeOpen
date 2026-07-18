package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.CopyArmyLink
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseTrainTroops(): Boolean {
    val isAttackEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_ATTACK.key)
    if (!isAttackEnabled) {
        accountMessage("自动进攻未开启，跳过练兵")
        return true
    }

    val isManualTrainEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.MANUAL_TRAINING.key)
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.MAIN_BASE_TRAIN_TROOPS, InGameVars.currentAccountNumber)

    // Unified cooldown: 24h for both auto and manual/link training
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("该账号今日已练兵")
        return true
    }

    accountMessage("准备训练部队")
    GlobalVars.absorbEdge = 1

    if (isManualTrainEnabled) {
        accountMessage("进行手动配兵")
        val configMode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.ARMY_CONFIG_MODE.key)
        if (configMode == "0") {
            val armyLink = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.ARMY_LINK.key)
            if (armyLink.isNotBlank()) {
                CopyArmyLink.openCopyArmyLink(armyLink)

                findMultiColorsUntil(schemas = listOf(MyColors.SaveArmyButton, MyColors.SaveArmyButton2), duration = 5000)?.let {
                    TouchActions.tap(it.x, it.y)
                }
                findMultiColorsUntil(schemas = listOf(MyColors.UseArmyButton), duration = 5000)?.let {
                    TouchActions.tap(it.x, it.y)
                    delayWithMultiplier(500)
                    findMultiColorsUntil(schemas = listOf(MyColors.ConfirmUseButton), duration = 3000)?.let {
                        TouchActions.tap(it.x, it.y)
                    }
                }
                findMultiColorsUntil(schemas = listOf(MyColors.UseArmyButton2), duration = 5000)?.let {
                    TouchActions.tap(it.x, it.y)
                }
                // First batch: process all currently visible enable buttons
                var enablePoint = findMultiColorsUntil(schemas = listOf(MyColors.EnableSuperTroopButton), duration = 5000)
                while (enablePoint != null) {
                    TouchActions.tap(enablePoint.x, enablePoint.y)
                    delayWithMultiplier(500)
                    findMultiColorsUntil(schemas = listOf(MyColors.UseResourcesButton), duration = 5000)?.let {
                        TouchActions.tap(it.x, it.y)
                        delayWithMultiplier(300)
                    }
                    enablePoint = findMultiColorsUntil(schemas = listOf(MyColors.EnableSuperTroopButton), duration = 2000)
                }
                // Swipe once to reveal remaining super troops
                TouchActions.swipe(1120, 240, 435, 240)
                delayWithMultiplier(1000)
                // Second batch: process buttons revealed by swipe
                enablePoint = findMultiColorsUntil(schemas = listOf(MyColors.EnableSuperTroopButton), duration = 3000)
                while (enablePoint != null) {
                    TouchActions.tap(enablePoint.x, enablePoint.y)
                    delayWithMultiplier(500)
                    findMultiColorsUntil(schemas = listOf(MyColors.UseResourcesButton), duration = 5000)?.let {
                        TouchActions.tap(it.x, it.y)
                        delayWithMultiplier(300)
                    }
                    enablePoint = findMultiColorsUntil(schemas = listOf(MyColors.EnableSuperTroopButton), duration = 2000)
                }
            }
        }
    } else {
        accountMessage("自动配兵模式：开始训练部队")
        // --- Auto training path (original flow) ---
        // Open training menu
        var point = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1500)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
        } else {
            accountMessage("训练部队失败：未找到训练按钮")
            GlobalVars.absorbEdge = 0
            return enterMainScreen()
        }

        // Verify training page
        point = findMultiColorsUntil(schemas = listOf(MyColors.AttackInTrainingPage, MyColors.AttackInTrainingPage2, MyColors.AttackInTrainingPage3), duration = 1500)
        if (point == null) {
            accountMessage("未找到训练标志")
            GlobalVars.absorbEdge = 0
            repeat(2) {
                TouchActions.tap(70, 220)
            }
            return enterMainScreen()
        }

        // Clean Queue 1
        point = findMultiColorsUntil(schemas = listOf(MyColors.DeleteAll1), duration = 1000)
        if (point != null) {
            accountMessage("清空现有兵种队列")
            TouchActions.tap(point.x, point.y)
            findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1500)?.let {
                TouchActions.tap(it.x, it.y)
            }
        }

        // Train Troops Tab
        TouchActions.tap(890, 235, delayTime = 800)

        for (i in 1..8) {
            // Priority training check
            val dragonPoint = findMultiColorsUntil(schemas = listOf(MyColors.TrainDragon, MyColors.TrainDragon2), duration = 100)
            if (dragonPoint != null) {
                accountMessage("训练龙")
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

            if (findMultiColors(schema = MyColors.GrayBarbarian) != null) {
                accountMessage("兵种队列已满")
                break
            }
            delayWithMultiplier(300)
        }

        // Close tab and Clean Queue 2
        TouchActions.tap(220, 140, delayTime = 1000)
        point = findMultiColorsUntil(schemas = listOf(MyColors.DeleteAll2), duration = 500)
        if (point != null) {
            accountMessage("清空现有法术队列")
            TouchActions.tap(point.x, point.y)
            findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1500)?.let {
                TouchActions.tap(it.x, it.y)
            }
        }

        // Spell Tab
        TouchActions.tap(797, 420, delayTime = 1000)
        if (findMultiColorsUntil(schemas = listOf(MyColors.TrainLighteningSpell), duration = 500) != null) {
            accountMessage("训练法术")
            findMultiColors(schema = MyColors.CloneSpell)?.let { p ->
                repeat(4) { TouchActions.tap(p.x, p.y, delayTime = 40) }
            }
            findMultiColors(schema = MyColors.RageSpell)?.let { p ->
                repeat(4) { TouchActions.tap(p.x, p.y, delayTime = 40) }
            }
            repeat(8) { TouchActions.tap(100, 490, delayTime = 40) }
        }

        // Close tab and Clean Queue 3
        TouchActions.tap(220, 140, delayTime = 1000)
        point = findMultiColorsUntil(schemas = listOf(MyColors.DeleteAll3), duration = 500)
        if (point != null) {
            accountMessage("清空现有攻城队列")
            TouchActions.tap(point.x, point.y)
            findMultiColorsUntil(schemas = listOf(MyColors.MiddleGreenYes), duration = 1500)?.let {
                TouchActions.tap(it.x, it.y)
            }
        }

        // Siege Machines Tab
        TouchActions.tap(1125, 425, delayTime = 1000)
        if (findMultiColorsUntil(schemas = listOf(MyColors.TrainSiegeMachine), duration = 500) != null) {
            accountMessage("训练攻城机器")
            val siegeCoords = listOf(1047 to 543, 610 to 535, 364 to 536, 138 to 541)
            for (coord in siegeCoords) {
                TouchActions.tap(coord.first, coord.second, delayTime = 50)
            }
        }

        // Final Close
        TouchActions.tap(220, 140, delayTime = 1000)
        TouchActions.tap(1230, 65, delayTime = 300)
    }

    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    accountMessage("练兵完成，已记录冷却时间")
    GlobalVars.absorbEdge = 0
    return enterMainScreen()
}
