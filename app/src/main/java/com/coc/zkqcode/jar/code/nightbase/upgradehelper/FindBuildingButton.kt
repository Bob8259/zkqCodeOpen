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
        106, 70, 1115, 682, "49F6DB", "3|0|49F6DB,7|0|152E2B,10|0|F7F7F7,13|0|F7F7F7,0|8|18BF4C,3|8|F9F9F9,7|8|F9F9F9,10|8|0E2615,13|8|18BE4C", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "43F6DA", "3|0|43F4D7,6|0|152E2B,9|0|F7F7F7,12|0|F7F7F7,0|9|16BA49,3|9|F9F9F9,6|9|F9F9F9,9|9|105626,12|9|15B948", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "399F8D", "4|0|389A88,8|0|368375,11|0|ADA0A0,15|0|ADA0A0,0|9|1B782F,4|9|1A1C12,8|9|ADA0A0,11|9|2A2B22,15|9|1B782F", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "38AE9A", "4|0|255B51,8|0|B1A7A7,11|0|B0A5A5,15|0|282B2A,0|9|197E31,4|9|B1A7A7,8|9|AFA4A4,11|9|1A7A30,15|9|1A782F", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "38CEAE", "3|0|A0A5A2,7|0|D6D5D5,10|0|D8D7D7,13|0|DAD9D9,0|9|148F37,3|9|3A463B,7|9|B6B6B4,10|9|149238,13|9|149439", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "49F6DB", "3|0|49F6DB,6|0|152E2B,9|0|F7F7F7,12|0|F7F7F7,0|9|17BB4A,3|9|F9F9F9,6|9|F9F9F9,9|9|12652B,12|9|16BA49", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "3C9F8E", "3|0|3C9F8E,7|0|306F63,10|0|ADA0A0,13|0|ADA0A0,0|10|1B772E,3|10|383730,7|10|ADA0A0,10|10|15170D,13|10|1B772E", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "39BAA5", "3|0|37A593,7|0|474A48,11|0|B9B1B1,14|0|B7AFAF,0|9|169A3C,3|9|CDC9C9,7|9|CAC5C5,11|9|179138,14|9|168D37", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "35A794", "3|0|2E8374,7|0|B3ACAC,10|0|B6AFAF,13|0|B9B2B2,0|9|178233,3|9|B6AFAF,7|9|BAB4B4,10|9|178A36,13|9|168C37", 0, 0.9, "绿色勾勾"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "49F8DD", "3|0|49F7DC,7|0|152E2B,10|0|F5F5F5,13|0|F4F4F4,0|10|15B848,3|10|E7E9E8,7|10|F5F5F5,10|10|117831,13|10|15B447", 0, 0.9, "绿色勾勾"
    ),

    )

private val nightBaseBuildCrossSchemas = listOf(
    // Note: In MyColors, Cross1 was identical to Tick1. Keeping it for consistency in searching both types.
    ColorSchema.parse(
        106, 70, 1115, 682, "817BED", "3|0|F4F4F4,6|0|0C0C0C,9|0|F6F6F6,12|0|333157,0|8|0D0C49,3|8|F5F5F5,6|8|0E0D64,9|8|F8F8F8,12|8|0D0D1B", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "0C0C0C", "3|0|F4F4F4,6|0|161620,9|0|F6F6F6,12|0|242138,0|9|717171,3|9|F5F5F5,6|9|0E0D7F,9|9|F8F8F8,12|9|0D0D0D", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "392B4D", "3|0|ADA0A0,7|0|ADA0A0,10|0|150808,13|0|62539C,0|7|ADA0A0,3|7|ADA0A0,7|7|897C7C,10|7|ADA0A0,13|7|16087F", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "100F12", "3|0|F2F1F1,6|0|0D0C0C,9|0|EBEAEA,12|0|232034,0|9|9C9B9B,3|9|E5E3E3,6|9|0E0B8A,9|9|DDDBDB,12|9|141111", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "4F4889", "3|0|D1CECE,6|0|120F0F,9|0|D3D1D1,12|0|1E1C1C,0|9|0F0A20,3|9|C2BDBD,6|9|0F0A28,9|9|C3BFBF,12|9|615D5D", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "24223C", "3|0|F9F9F9,7|0|1D1D1D,10|0|F7F6F6,13|0|49457F,0|9|0D0D0E,3|9|FCFCFC,7|9|0D0D0D,10|9|FAFAFA,13|9|0F0DC7", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "30223C", "4|0|ADA0A0,8|0|948787,11|0|ADA0A0,15|0|64539E,0|10|15080B,4|10|ADA0A0,8|10|322525,11|10|ADA0A0,15|10|16087E", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "7972D9", "4|0|DBD9D9,8|0|756ED3,12|0|D6D3D3,16|0|746CCE,0|9|100CBF,4|9|E9E8E8,8|9|0D0C0C,12|9|E6E5E5,16|9|0F0CB9", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "594B91", "3|0|B1A7A7,7|0|1A1010,10|0|B0A6A6,13|0|322645,0|9|12091F,3|9|B6AEAE,7|9|110909,10|9|B5ADAD,13|9|12091A", 0, 0.9, "红色叉叉"
    ),
    ColorSchema.parse(
        106, 70, 1115, 682, "8984FB", "4|0|FFFFFF,8|0|E2E2E2,11|0|FEFEFE,15|0|8883FA,0|9|0E0DB4,4|9|FEFEFE,8|9|0D0D0D,11|9|FEFEFE,15|9|0F0DC7", 0, 0.9, "红色叉叉"
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

