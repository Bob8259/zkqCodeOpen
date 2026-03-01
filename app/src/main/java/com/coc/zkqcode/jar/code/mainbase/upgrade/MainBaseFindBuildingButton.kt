package com.coc.zkqcode.jar.code.mainbase.upgrade

import android.graphics.Point
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors

private val mainBaseBuildTickSchemas = listOf(
    ColorSchema.parse(
        105, 70, 1115, 680, "FFFFFF", "-2|-8|58FFE8,-5|-8|58FFE8,-3|-7|54FFE7,-1|-7|54FFE7,-6|4|14B745,-5|5|14B948,2|6|15BF51,4|6|15BF51,6|4|14B745", 0, 0.92, "绿色勾勾1"
    ),
)

private val mainBaseBuildCrossSchemas = listOf(
    ColorSchema.parse(
        105, 70, 1115, 680, "FFFFFF", "-6|-1|8884F8,-4|-6|8B84FF,7|0|8784F7,7|-1|8884F8,7|5|0F0DC7,6|8|0E0DCB,1|8|0E0DCA,-4|8|0E0DCA,-7|6|0E0DC5", 0, 0.92, "红色叉叉1"
    ),
)

suspend fun mainBaseFindBuildButton(duration: Int = 1200, type: String): Point? {
    val startTime = System.currentTimeMillis()
    val targetSchemas = when (type) {
        "Tick" -> mainBaseBuildTickSchemas
        "Cross" -> mainBaseBuildCrossSchemas
        else -> return null
    }

    while (System.currentTimeMillis() - startTime < duration) {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult ?: logAndStop("failed to take screenshot at close advertisement")
        for (schema in targetSchemas) {
            val point = findMultiColors(schema = schema, byteBuffer = screenBuffer)
            if (point != null) return point
        }
        delayWithMultiplier(20)
    }
    return null
}

