package com.coc.zkqcode.jar.code.builderbase.upgrade

import android.graphics.Point
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors

private val builderBaseBuildTickSchemas = listOf(
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "58FEE7",
        "2|1|55FFE7,3|1|55FFE7,-1|1|55FEE6,-2|3|4CFEE3,-2|17|15BA4C,2|17|14B949,8|17|14B94A,13|17|14BB4B,13|13|16BD49",
        0,
        0.92,
        "绿色勾勾1"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "45B5A5",
        "4|0|45B2A2,1|2|40B7A4,0|1|44B6A5,-1|1|44B7A6,-1|15|14973A,3|17|149C41,11|17|14943E,12|15|138C35,14|12|168D37",
        0,
        0.92,
        "绿色勾勾2"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "55FBE3",
        "3|0|54FAE2,3|-1|56FAE3,1|-1|57FAE3,15|-2|5CF3DF,14|12|15B747,9|16|14B74A,0|16|14BA4C,-1|14|14B644,-1|13|15B845",
        0,
        0.92,
        "绿色勾勾3"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "42A091",
        "-2|-1|44A092,1|-1|44A092,-1|0|42A091,14|7|289D6D,14|12|1B772E,7|17|1A7732,0|15|1A732B,-3|15|1A732C,-3|14|1A732B",
        0,
        0.92,
        "绿色勾勾4"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "43C3B0",
        "2|-2|48BFAE,-3|-2|4BCAB8,-3|1|42CAB5,14|6|28A777,13|12|187D30,12|16|167E34,7|15|168032,-1|15|158B35,-4|12|169439",
        0,
        0.92,
        "绿色勾勾5"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "47DFC8",
        "0|-3|52E5D1,3|-3|52E6D1,6|-2|50E6D1,5|-1|4BE3CD,0|12|148F36,2|14|138F3A,10|14|12913A,14|14|12933C,14|10|149D3D",
        0,
        0.92,
        "绿色勾勾6"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "50E2CE",
        "3|0|51E5D0,5|0|52E7D2,5|1|4FE8D2,1|2|4AE6CF,0|13|15B445,2|17|14B74B,7|17|15B84B,15|17|14BA4D,15|14|15B344",
        0,
        0.92,
        "绿色勾勾7"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "42A192",
        "3|0|42A192,3|-2|46A092,5|-2|46A092,5|0|42A192,0|13|17792E,1|16|167D33,8|16|167C32,13|16|177A32,14|14|17762D",
        0,
        0.92,
        "绿色勾勾8"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "53F9E2",
        "1|0|53F8E1,6|0|50EDD7,2|-1|56F6E0,0|-2|5AF9E4,2|15|14AC42,7|17|15A947,13|14|149639,15|17|159640,17|13|159037",
        0,
        0.92,
        "绿色勾勾9"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "49C6B4",
        "1|0|4AC9B7,2|0|49CCBA,3|0|4ACEBB,3|1|4ACFBC,-1|17|129E40,8|18|14AA4A,11|18|14AF4C,11|16|13A640,14|14|14AE42",
        0,
        0.92,
        "绿色勾勾10"
    ),
)

private val builderBaseBuildCrossSchemas = listOf(
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "8880F7",
        "5|0|867EF4,13|1|847DF1,14|10|827FEB,-1|9|8380F0,-2|16|0F0DC1,-1|20|0F0DC9,6|19|0E0DC2,13|19|0E0DC3,15|15|0F0DC3",
        0,
        0.92,
        "红色叉叉1"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "6D61BB",
        "5|0|6E62BD,11|0|6F63BF,14|5|7269C7,13|8|736DCB,14|14|0F0BB1,11|17|0F0CB3,7|17|0F0CB3,2|18|0F0CB6,-2|16|0E0BAD",
        0,
        0.92,
        "红色叉叉2"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "8C84FF",
        "5|0|8C84FF,9|0|8C84FF,14|0|8C84FF,15|8|8784F7,16|14|0F0DCA,15|17|0E0DC6,9|18|0E0DC8,1|18|0E0DC8,-1|16|0E0DC5",
        0,
        0.92,
        "红色叉叉3"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "6558AA",
        "5|-1|6555A5,12|-1|6454A2,15|8|61549C,15|-1|6453A1,16|13|150881,14|17|14087E,5|18|120985,-1|18|11098D,-2|14|11098B",
        0,
        0.92,
        "红色叉叉4"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "8C84FF",
        "7|0|8A83FE,13|0|8A83FD,17|0|8A82FB,16|8|8582F3,17|13|0F0DC8,16|18|0E0DC4,9|18|0E0DC6,2|18|0E0DC7,-1|14|0F0DCA",
        0,
        0.92,
        "红色叉叉5"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "6A62B9",
        "0|-8|756ACE,7|-8|766DD4,13|-8|7A70D9,14|0|6D67C2,15|6|110A95,13|9|100A91,6|9|11098E,1|9|12098C,-1|7|11098B",
        0,
        0.92,
        "红色叉叉6"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "635AA9",
        "0|-8|6557A9,6|-8|675AAE,14|-8|6A5FB7,15|-1|6D65BE,16|7|0F0BA3,15|10|0F0BA6,10|10|0F0A9E,2|10|0F0A93,-1|8|10098C",
        0,
        0.92,
        "红色叉叉7"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "7774DA",
        "0|-7|7872DD,5|-8|7A73DF,12|-8|7E77E6,16|-1|7D7AE5,17|6|0E0CBD,14|10|0D0CBA,9|10|0D0CB8,5|10|0D0CB8,-1|8|0D0CB2",
        0,
        0.92,
        "红色叉叉8"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "6860B4",
        "0|-7|685BAF,6|-7|6659AB,16|-7|6556A6,15|0|645AA8,16|7|11098E,13|10|0F0A94,8|10|0F0A98,3|10|0F0A9D,-1|7|100A9C",
        0,
        0.92,
        "红色叉叉9"
    ),
    ColorSchema.parse(
        105,
        70,
        1115,
        680,
        "6D60BA",
        "3|0|6E61BB,7|0|6D61BC,10|0|6E62BD,15|8|726BC9,16|15|0F0BAD,13|18|0F0CB2,5|18|0F0CB2,2|18|0E0CB1,-1|16|0E0BAB",
        0,
        0.92,
        "红色叉叉9"
    ),
)

suspend fun builderBaseFindBuildButton(duration: Int = 1200, type: String): Point? {
    val startTime = System.currentTimeMillis()
    val targetSchemas = when (type) {
        "Tick" -> builderBaseBuildTickSchemas
        "Cross" -> builderBaseBuildCrossSchemas
        else -> return null
    }

    while (System.currentTimeMillis() - startTime < duration) {
        val screenBuffer =
            ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
                ?: logAndStop("failed to take screenshot at close advertisement")
        for (schema in targetSchemas) {
            val point = findMultiColors(schema = schema, byteBuffer = screenBuffer)
            if (point != null) return point
        }
        delayWithMultiplier(20)
    }
    return null
}

