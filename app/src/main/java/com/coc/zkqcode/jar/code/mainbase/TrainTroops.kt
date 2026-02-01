package com.coc.zkqcode.jar.code.mainbase

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColorsUntil
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import kotlinx.coroutines.time.delay
import java.util.Calendar
import kotlin.math.abs

object TrainTroops {
    suspend fun trainTroops() {
        val lastTrainingTime = readMemory("MainBaseTrainTroops${InGamesVars.currentAccountNumber}").toIntOrNull()
        // 获取 Calendar 实例
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        if (lastTrainingTime == null || abs(lastTrainingTime - dayOfMonth) > 0){
            var point = findMultiColorsUntil(schema = MyColors.TrainTroops, duration = 1500)
            if (point!=null){
                TouchActions.tap(point.x,point.y)
                delayWithMultiplier(500)
            }
            point = findMultiColorsUntil(schema = MyColors.TrainTroops, duration = 1500)
            if (point!=null){
                TouchActions.tap(point.x,point.y)
                delayWithMultiplier(500)
            }

        }
    }
}