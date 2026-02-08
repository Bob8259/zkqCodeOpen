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
        106, 70, 1115, 682, "39B6A1", "6|0|1D443D,12|0|A59D9D,18|0|BAB2B2,24|0|2D3533,0|15|169138,6|15|B6B3B1,12|15|C8C2C2,18|15|168C36,24|15|178A35", 0, 0.94, "绿色勾勾1"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "4DFDE2", "6|0|4BFCE1,12|0|48ECD2,18|0|FBFBFB,24|0|F5F8F8,0|15|18BF4C,6|15|FCFCFC,12|15|FBFBFB,18|15|16A443,24|15|17BC4B", 0, 0.94, "绿色勾勾1"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "42EED2", "6|0|37BCA7,13|0|152E2B,19|0|ECECEC,25|0|ECECEC,0|15|15AF44,6|15|0C2314,13|15|EFEFEF,19|15|15AF44,25|15|15B045", 0, 0.94, "绿色勾勾1"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "44F6D9", "6|0|16302B,13|0|EEEEEE,19|0|F9F9F9,25|0|152F2A,0|16|14B143,6|16|0F471F,13|16|0E2314,19|16|14B243,25|16|14B344", 0, 0.94, "绿色勾勾1"
    ),
)

private val nightBaseBuildCrossSchemas = listOf(
    // Note: In MyColors, Cross1 was identical to Tick1. Keeping it for consistency in searching both types.
    ColorSchema.parse(
        106, 70, 1115, 682, "817BED", "3|0|F4F4F4,6|0|0C0C0C,9|0|F6F6F6,12|0|333157,0|8|0D0C49,3|8|F5F5F5,6|8|0E0D64,9|8|F8F8F8,12|8|0D0D1B", 0, 0.9, "红色叉叉1"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "0C0C0C", "3|0|F4F4F4,6|0|161620,9|0|F6F6F6,12|0|242138,0|9|717171,3|9|F5F5F5,6|9|0E0D7F,9|9|F8F8F8,12|9|0D0D0D", 0, 0.9, "红色叉叉2"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "392B4D", "3|0|ADA0A0,7|0|ADA0A0,10|0|150808,13|0|62539C,0|7|ADA0A0,3|7|ADA0A0,7|7|897C7C,10|7|ADA0A0,13|7|16087F", 0, 0.9, "红色叉叉3"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "100F12", "3|0|F2F1F1,6|0|0D0C0C,9|0|EBEAEA,12|0|232034,0|9|9C9B9B,3|9|E5E3E3,6|9|0E0B8A,9|9|DDDBDB,12|9|141111", 0, 0.9, "红色叉叉4"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "4F4889", "3|0|D1CECE,6|0|120F0F,9|0|D3D1D1,12|0|1E1C1C,0|9|0F0A20,3|9|C2BDBD,6|9|0F0A28,9|9|C3BFBF,12|9|615D5D", 0, 0.9, "红色叉叉5"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "24223C", "3|0|F9F9F9,7|0|1D1D1D,10|0|F7F6F6,13|0|49457F,0|9|0D0D0E,3|9|FCFCFC,7|9|0D0D0D,10|9|FAFAFA,13|9|0F0DC7", 0, 0.9, "红色叉叉6"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "30223C", "4|0|ADA0A0,8|0|948787,11|0|ADA0A0,15|0|64539E,0|10|15080B,4|10|ADA0A0,8|10|322525,11|10|ADA0A0,15|10|16087E", 0, 0.9, "红色叉叉7"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "7972D9", "4|0|DBD9D9,8|0|756ED3,12|0|D6D3D3,16|0|746CCE,0|9|100CBF,4|9|E9E8E8,8|9|0D0C0C,12|9|E6E5E5,16|9|0F0CB9", 0, 0.9, "红色叉叉8"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "594B91", "3|0|B1A7A7,7|0|1A1010,10|0|B0A6A6,13|0|322645,0|9|12091F,3|9|B6AEAE,7|9|110909,10|9|B5ADAD,13|9|12091A", 0, 0.9, "红色叉叉9"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "8984FB", "4|0|FFFFFF,8|0|E2E2E2,11|0|FEFEFE,15|0|8883FA,0|9|0E0DB4,4|9|FEFEFE,8|9|0D0D0D,11|9|FEFEFE,15|9|0F0DC7", 0, 0.9, "红色叉叉10"
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
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndStop("failed to take screenshot at close advertisement")
        for (schema in targetSchemas) {
            val point = findMultiColors(schema = schema, byteBuffer = screenBuffer)
            if (point != null) return point
        }
        delayWithMultiplier(200)
    }
    return null
}

