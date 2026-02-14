package com.coc.zkqcode.jar.code.builderbase

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import java.util.Calendar
import kotlin.math.abs

object BuilderBaseTrainTroops {

    suspend fun trainWithConditions() {
        val storageKey = "BuilderBaseTrainTroops${InGamesVars.currentAccountNumber}"
        val lastTrainingTime = readMemory(storageKey).toIntOrNull()
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        if (lastTrainingTime == null || abs(lastTrainingTime - dayOfMonth) > 0) {
            trainTroops()
            writeMemory(storageKey, dayOfMonth.toString())
        }
    }

    private suspend fun trainTroops() {
        // Attempt to locate the initial training button
        val trainingButton = findMultiColorsUntil(schemas = listOf(MyColors.TrainTroops), duration = 1500)

        if (trainingButton == null) {
            ShowMessage("夜世界练兵失败")
            return
        }

        // Enter training menu
        TouchActions.tap(trainingButton.x, trainingButton.y)
        delayWithMultiplier(400)

        // Clear existing troops if the clear button is present
        val cleanTroops = findMultiColorsUntil(schemas = listOf(MyColors.RedCleanButton), duration = 1000)
        if (cleanTroops != null) {
            TouchActions.tap(cleanTroops.x, cleanTroops.y)
            delayWithMultiplier(500)
        }

        /**
         * Helper to perform repeated taps on a specific coordinate
         */
        suspend fun tapRepeat(x: Int, y: Int, times: Int = 10) {
            repeat(times) {
                TouchActions.tap(x, y)
                delayWithMultiplier(50)
            }
        }

        // Identify troop type and train
        val trainNightWitch = findMultiColors(schema = MyColors.TrainNightWitch)
        if (trainNightWitch != null) {
            // Train Night Witches based on detected location
            ShowMessage("练暗夜女巫")
            tapRepeat(trainNightWitch.x, trainNightWitch.y)
        } else {
            // Fallback to Barbarians using original hardcoded coordinates
            ShowMessage("未检测到暗夜女巫，练野蛮人\n（有暗夜女巫后会练暗夜女巫）")
            tapRepeat(278, 491)
        }

        // Close the training interface
        TouchActions.tap(1152, 102)
        delayWithMultiplier(300)
    }
}

