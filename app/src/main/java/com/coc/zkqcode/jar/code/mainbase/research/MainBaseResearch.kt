package com.coc.zkqcode.jar.code.mainbase.research

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.MainBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseResearch(): Boolean {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESEARCH_SETTING.key) && MainBaseWorkerAndResearch.detectResearch()) {
        val researchIcon = findMultiColorsUntil(schemas = listOf(MyColors.ResearchIcon), duration = 1000)
        if (researchIcon != null) {
            TouchActions.tap(researchIcon.x + 20, researchIcon.y, delayTime = 500)
            // Find white number indicating available research and tap it
            val whiteNumber = findMultiColorsUntil(schemas = listOf(MyColors.WhiteNumberColor), duration = 1000)
            if (whiteNumber != null) {
                TouchActions.tap(whiteNumber.x, whiteNumber.y, delayTime = 800)
                TouchActions.tap(1130, 55, delayTime = 500)
                TouchActions.swipe(240, 500, 4000, 500)
                delayWithMultiplier(200)
                findAllResearchItems()
            }
        }
    }
    return enterMainScreen()
}

suspend fun findAllResearchItems(){
    
}