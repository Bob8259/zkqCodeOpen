package com.coc.zkqcode.jar.code.builderbase.research

import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import kotlinx.coroutines.delay

object BuilderBaseResearch {
    suspend fun research() {
        if (BuilderBaseWorkerAndResearch.detectResearch()) {
            val research = findMultiColors(schema = MyColors.ResearchIcon)
            if (research != null) {
                TouchActions.tap(research.x, research.y)
                delayWithMultiplier(600)
                TouchActions.tap(research.x, research.y + 130)//Open research tab
                val backArrow = findMultiColorsUntil(schemas = listOf(MyColors.BuilderResearchBackArrow), duration = 1000)
                if (backArrow != null) {
                    TouchActions.tap(backArrow.x, backArrow.y)
                    delayWithMultiplier(600)

                }
            }
        }
    }

    suspend fun checkAllResearch() {
        val BuilderResearchElixir: ColorSchema = ColorSchema.parse(
            "FE3093",
            "5|-5|FF1FAA,9|-5|FF4BC6,9|-2|FF6ADC,9|0|FF6FDB,5|2|FF1B8F,4|3|FF1E80,4|6|F62271,8|6|F52473,8|5|FE217B", 0, 0.9, "夜世界研究圣水"
        )
         val InsufficientResources: ColorSchema = ColorSchema.parse(
             "7F88FF", "1|0|7F88FF,2|0|7F88FF,3|0|7F88FF,3|1|7F88FF,2|1|7F88FF,0|1|7F88FF,0|1|7F88FF,0|2|7F88FF,1|2|7F88FF", 0, 0.97, "资源不足"
        )

    }
}