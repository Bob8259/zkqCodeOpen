package com.coc.zkqcode.jar.code.mainbase.research

import android.graphics.Bitmap
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import java.io.File
import java.io.FileOutputStream
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.mainbase.others.MainBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.MainBaseTroopsAndSpells

// White color list for detecting available research items
val ResearchLevelOne = listOf(
    ColorSchema.parse(
        195, 350, 1090, 650,
        "FFFFFF",
        "0|1|FFFFFF,0|2|FFFFFF,0|3|FFFFFF,0|4|FFFFFF,0|5|FFFFFF,0|6|FFFFFF,0|7|FFFFFF,0|8|FFFFFF,0|9|F4F4F4",
        0, 0.92
    )
)

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

suspend fun findAllResearchItems() {
    // Build a map from display name to setting key for filtering enabled items
    val displayNameToKey = MainBaseTroopsAndSpells.all.associate { it.displayName to it.key }

    // Filter research colors to only include enabled items
    val enabledResearchColors = MainBaseResearchColors.allResearchColors.filter { schema ->
        val displayName = schema.name ?: return@filter false
        val key = displayNameToKey[displayName] ?: return@filter false
        getBooleanConfigRuntime(key)
    }

    if (enabledResearchColors.isEmpty()) {
        showDebugInfo("没有启用的研究项目")
        return
    }

    showDebugInfo("已启用 ${enabledResearchColors.size} 个研究项目")

    repeat(8) {
        // Capture screenshot for searching
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndStop("findAllResearchItems: Screen Capture Failed.")

        // Search for enabled research items on current screen
        for (schema in enabledResearchColors) {
            val result = findMultiColors(schema = schema, byteBuffer = screenBuffer)
            if (result != null) {
                // Found an enabled research item — crop and save the region around it for debugging
                val debugBitmap = ScreenCaptureManager.capture(asBitmap = true) as? Bitmap
                if (debugBitmap != null) {
                    val cropLeft = (result.x - 25).coerceAtLeast(0)
                    val cropTop = (result.y + 20).coerceAtLeast(0)
                    val cropWidth = minOf(40, debugBitmap.width - cropLeft)
                    val cropHeight = minOf(40, debugBitmap.height - cropTop)
                    if (cropWidth > 0 && cropHeight > 0) {
                        val croppedBitmap = Bitmap.createBitmap(debugBitmap, cropLeft, cropTop, cropWidth, cropHeight)
                        ScreenCaptureManager.getContext()?.let { context ->
                            try {
                                val file = File(context.filesDir, "test.png")
                                FileOutputStream(file).use { out ->
                                    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                                }
                                showDebugInfo("已保存截图至: ${context.filesDir}/test.png")
                            } catch (e: Exception) {
                                showDebugInfo("保存截图失败: ${e.message}")
                            } finally {
                                croppedBitmap.recycle()
                            }
                        }
                    }
                    debugBitmap.recycle()
                }
                showDebugInfo("找到研究项目: ${schema.name}, 坐标: (${result.x}, ${result.y})")
//                TouchActions.tap(result.x, result.y, delayTime = 500)
                return
            }
        }

        // No enabled item found on current screen, swipe to see more items
        TouchActions.swipe(860, 500, 400, 500, delayTime = 500)
        delayWithMultiplier(100)
    }

    showDebugInfo("未找到任何已启用的研究项目")
}