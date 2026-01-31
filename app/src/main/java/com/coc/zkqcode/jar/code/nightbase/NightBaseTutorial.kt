package com.coc.zkqcode.jar.code.nightbase

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class NightBaseTutorial {

    suspend fun nightBaseTutorial(){
        val durationMillis = 300 * 1000L // 300 秒转换为毫秒
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < durationMillis && kotlinx.coroutines.currentCoroutineContext().isActive) {
            var point = findMultiColors(schema = MyColors.RebuildBoat)
            if (point!=null){
                TouchActions.tap(point.x,point.y)
                delayWithMultiplier(500)
            }
            point = findMultiColors(schema = MyColors.RebuildBoat)
            if (point!=null){
                TouchActions.tap(point.x,point.y)
                delayWithMultiplier(500)
            }
        }
    }
}