package com.coc.zkqcode.jar.code.mainbase

import android.icu.text.SymbolTable
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.basic.findMultiColorsUntil
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import kotlinx.coroutines.time.delay
import java.util.Calendar
import kotlin.math.abs

object TrainTroops {
    suspend fun trainTroops() {
        val accountKey = "MainBaseTrainTroops${InGamesVars.currentAccountNumber}"
        val lastTrainingTime = readMemory(accountKey).toIntOrNull()

        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Check if training has already been performed today
        if (lastTrainingTime != null && abs(lastTrainingTime - dayOfMonth) == 0) {
            return
        }

        GlobalVars.absorbEdge = 1

        // Step 1: Open Training Menu
        var point = findMultiColorsUntil(schema = MyColors.TrainTroops, duration = 1500)
        if (point == null) return handleTrainingFailure()
        TouchActions.tap(point.x, point.y)
        delayWithMultiplier(500)

        // Step 2: Navigate to Attack/Training Page
        point = findMultiColorsUntil(schema = MyColors.AttackInTrainingPage, duration = 1500)
        if (point == null) return handleTrainingFailure()
        TouchActions.tap(point.x, point.y)
        delayWithMultiplier(500)

        // Step 3: Clear existing troops
        point = findMultiColorsUntil(schema = MyColors.DeleteAll1, duration = 1500)
        if (point == null) return handleTrainingFailure()

        TouchActions.tap(point.x, point.y)
        delayWithMultiplier(500)

        point = findMultiColorsUntil(schema = MyColors.MiddleGreenYes, duration = 1500)
        if (point != null) {
            TouchActions.tap(point.x, point.y)
            delayWithMultiplier(500)

            // Fixed coordinate tap for UI transition
            TouchActions.tap(891, 234)
            delayWithMultiplier(1200)

            // Step 4: Batch train specific units (Dragons)
            repeat(5) {
                point = findMultiColors(schema = MyColors.TrainDragon)
                if (point != null) {
                    repeat(25) {
                        TouchActions.tap(point!!.x, point!!.y)
                        delayWithMultiplier(40)
                    }
                }
            }

            // Save success state to memory
            writeMemory(accountKey, dayOfMonth.toString())
        } else {
            return handleTrainingFailure()
        }

        // Reset state on success
        GlobalVars.absorbEdge = 0
    }

    /**
     * Handles failed attempts by notifying the user and resetting state.
     * Maintains original Chinese strings as requested.
     */
    private fun handleTrainingFailure() {
        ShowMessage("训练部队失败")
        GlobalVars.absorbEdge = 0
    }
}