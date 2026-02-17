package com.coc.zkqcode.jar.code.builderbase.research

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.builderbase.others.BuilderBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseTroops


suspend fun builderBaseResearch(): Boolean {
    if (BuilderBaseWorkerAndResearch.detectResearch() && getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key)) {
        val research = findMultiColors(schema = MyColors.ResearchIcon)
        if (research != null) {
            TouchActions.tap(research.x, research.y)
            delayWithMultiplier(600)
            TouchActions.tap(research.x, research.y + 130)//Open research tab
            val backArrow = findMultiColorsUntil(schemas = listOf(MyColors.BuilderResearchBackArrow), duration = 1000)
            if (backArrow != null) {
                TouchActions.tap(backArrow.x, backArrow.y)
                delayWithMultiplier(600)
                builderBasecheckAllResearch()
            }
        }
    }
    return enterMainScreen()
}

suspend fun builderBasecheckAllResearch() {
    for (i in BuilderBaseTroops.all.indices) {
        val troop = BuilderBaseTroops.all[i]
        if (getBooleanConfigRuntime(troop.key)) {
            val row = i / 6
            val col = i % 6

            val x1 = 310 + col * 140
            val y1 = if (row == 0) 460 else 600
            val x2 = x1 + 40
            val y2 = if (row == 0) 500 else 640

            val elixirIcon = findMultiColors(schema = MyColors.BuilderResearchElixir.rescope(x1, y1, x2, y2))
            if (elixirIcon != null) {
                val resX1 = x1 - 100
                val insufficient = findMultiColors(schema = MyColors.BuilderResearchInsufficientResources.rescope(resX1, y1, x1, y2))
                if (insufficient == null) {
                    ShowMessage("开始研究 ${troop.displayName}")
                    TouchActions.tap(elixirIcon.x, elixirIcon.y)
                    delayWithMultiplier(500)
                    TouchActions.tap(955, 610)
                    delayWithMultiplier(200)
                    clickRightBottom(2)
                    return
                } else {
                    ShowMessage("${troop.displayName} 资源不足")
                }
            }
        }
    }
}

private fun ColorSchema.rescope(x1: Int, y1: Int, x2: Int, y2: Int): ColorSchema {
    return ColorSchema(x1, y1, x2, y2, mainColor, threshold, offsets, direction, name)
}
