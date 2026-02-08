package com.coc.zkqcode.jar.code.nightbase.upgradehelper

import android.graphics.Point
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors

private val nightBaseBuildTickSchemas = listOf(
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "43F7D9",
        "3|0|2A423D,7|0|7C8E8B,11|0|F7F7F7,14|0|738380,0|8|17BB4A,3|8|DCE1DE,7|8|F9F9F9,11|8|16BA49,14|8|15BA49",
        0,
        0.9, "绿色勾勾1"
    ),
)

private val nightBaseBuildCrossSchemas = listOf(
    // Note: In MyColors, Cross1 was identical to Tick1. Keeping it for consistency in searching both types.
    ColorSchema.parse(
        94,
        72,
        1193,
        618,
        "3DA08E",
        "3|0|3DA08E,7|0|2E685D,10|0|ADA0A0,13|0|ADA0A0,0|9|1B792F,3|9|A49897,7|9|ADA0A0,10|9|15180D,13|9|1B792F",
        0,
        0.9,
        "红色叉叉1"
    ),
    ColorSchema.parse(
        94,
        72,
        1193,
        618,
        "565398",
        "3|0|EFEFEF,6|0|F1F1F1,9|0|F3F3F3,12|0|847FF0,0|8|B0B0B0,3|8|EDEDED,6|8|0D0C92,9|8|F3F3F3,12|8|0C0C16",
        0,
        0.9,
        "红色叉叉2"
    ),
    ColorSchema.parse(
        94,
        72,
        1193,
        618,
        "191826",
        "3|0|F7F7F7,6|0|1D1C2D,9|0|F6F6F6,12|0|211F33,0|9|515151,3|9|F8F8F8,6|9|0E0DA4,9|9|F7F7F7,12|9|0E0E0E",
        0,
        0.9,
        "红色叉叉3"
    ),
    ColorSchema.parse(
        94,
        72,
        1193,
        618,
        "1F1E32",
        "3|0|F3F3F3,7|0|212033,11|0|F5F5F5,14|0|857FF3,0|9|0D0C76,3|9|F1F1F1,7|9|5B5B5B,11|9|A2A2A3,14|9|0E0DC7",
        0,
        0.9,
        "红色叉叉4"
    )
)

suspend fun nightBaseFindBuildButton(duration: Int = 2000, type: String): Point? {
    val startTime = System.currentTimeMillis()
    val targetSchemas = when (type) {
        "Tick" -> nightBaseBuildTickSchemas
        "Cross" -> nightBaseBuildCrossSchemas
        else -> return null
    }

    while (System.currentTimeMillis() - startTime < duration) {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndStop("failed to take screenshot at close advertisement")
        for (schema in targetSchemas) {
            val point = findMultiColors(schema = schema, byteBuffer = screenBuffer)
            if (point != null) return point
        }
        delayWithMultiplier(100)
    }
    return null
}

