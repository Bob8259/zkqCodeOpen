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
        "49F6DB",
        "3|0|49F6DB,7|0|152E2B,10|0|F7F7F7,13|0|F7F7F7,0|8|18BF4C,3|8|F9F9F9,7|8|F9F9F9,10|8|0E2615,13|8|18BE4C",
        0,
        0.9,
        "绿色勾勾"
    ),
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "43F6DA",
        "3|0|43F4D7,6|0|152E2B,9|0|F7F7F7,12|0|F7F7F7,0|9|16BA49,3|9|F9F9F9,6|9|F9F9F9,9|9|105626,12|9|15B948",
        0,
        0.9,
        "绿色勾勾"
    ),
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "399F8D",
        "4|0|389A88,8|0|368375,11|0|ADA0A0,15|0|ADA0A0,0|9|1B782F,4|9|1A1C12,8|9|ADA0A0,11|9|2A2B22,15|9|1B782F",
        0,
        0.9,
        "绿色勾勾"
    ),
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "38AE9A",
        "4|0|255B51,8|0|B1A7A7,11|0|B0A5A5,15|0|282B2A,0|9|197E31,4|9|B1A7A7,8|9|AFA4A4,11|9|1A7A30,15|9|1A782F",
        0,
        0.9,
        "绿色勾勾"
    ),
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "38CEAE",
        "3|0|A0A5A2,7|0|D6D5D5,10|0|D8D7D7,13|0|DAD9D9,0|9|148F37,3|9|3A463B,7|9|B6B6B4,10|9|149238,13|9|149439",
        0,
        0.9,
        "绿色勾勾"
    ),
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "49F6DB",
        "3|0|49F6DB,6|0|152E2B,9|0|F7F7F7,12|0|F7F7F7,0|9|17BB4A,3|9|F9F9F9,6|9|F9F9F9,9|9|12652B,12|9|16BA49",
        0,
        0.9,
        "绿色勾勾"
    ),
)

private val nightBaseBuildCrossSchemas = listOf(
    // Note: In MyColors, Cross1 was identical to Tick1. Keeping it for consistency in searching both types.
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "817BED",
        "3|0|F4F4F4,6|0|0C0C0C,9|0|F6F6F6,12|0|333157,0|8|0D0C49,3|8|F5F5F5,6|8|0E0D64,9|8|F8F8F8,12|8|0D0D1B",
        0,
        0.9,
        "红色叉叉"
    ),
    ColorSchema.parse(
        106,
        70,
        1115,
        682,
        "0C0C0C",
        "3|0|F4F4F4,6|0|161620,9|0|F6F6F6,12|0|242138,0|9|717171,3|9|F5F5F5,6|9|0E0D7F,9|9|F8F8F8,12|9|0D0D0D",
        0,
        0.9,
        "红色叉叉"
    ),
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
        delayWithMultiplier(200)
    }
    return null
}

