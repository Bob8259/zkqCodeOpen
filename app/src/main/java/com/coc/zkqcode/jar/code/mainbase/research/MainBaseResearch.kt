package com.coc.zkqcode.jar.code.mainbase.research

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.MainBaseSettings
import com.coc.zkqcode.jar.ui.schema.details.MainBaseTroopsAndSpells

/**
 * Maps research level numbers to their corresponding color schemas via MyColors.
 * Each entry is a Pair of (levelNumber, list of colorSchemas).
 * A level is considered detected if ANY of its schemas matches.
 * Level 8 is placed before level 3 because level 8's color is a subset of level 3's,
 * so level 8 must be checked first to avoid a false level-3 match.
 * Add new entries here, and add matching properties in MainBaseResearchLevelColors.
 */
val ResearchLevelColors: List<Pair<Int, List<ColorSchema>>> = listOf(
    8 to listOf(MyColors.RESEARCH_LEVEL_8),
    // Levels 10 and 11 are placed before level 9 and 1 because their base color is also white (FFFFFF),
    // so they must be checked first to avoid false matches against the other white-base levels.
    10 to listOf(MyColors.RESEARCH_LEVEL_10),
    11 to listOf(MyColors.RESEARCH_LEVEL_11),
    12 to listOf(MyColors.RESEARCH_LEVEL_12),
    13 to listOf(MyColors.RESEARCH_LEVEL_13),
    9 to listOf(MyColors.RESEARCH_LEVEL_9),
    3 to listOf(MyColors.RESEARCH_LEVEL_3),
    4 to listOf(MyColors.RESEARCH_LEVEL_4),
    2 to listOf(MyColors.RESEARCH_LEVEL_2),
    5 to listOf(MyColors.RESEARCH_LEVEL_5),
    6 to listOf(MyColors.RESEARCH_LEVEL_6),
    7 to listOf(MyColors.RESEARCH_LEVEL_7),
    1 to listOf(MyColors.RESEARCH_LEVEL_1),
)

suspend fun mainBaseResearch(): Boolean {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESEARCH_SETTING.key) && WorkerAndResearch.detectResearch(BaseType.Main)) {
        val researchIcon = findMultiColorsUntil(schemas = listOf(MyColors.ResearchIcon, MyColors.ResearchIcon2), duration = 1000, increment = 1)
        if (researchIcon != null) {
            TouchActions.tap(researchIcon.x + 20, researchIcon.y, delayTime = 500)
            // Find white number indicating available research and tap it
            val whiteNumber = findMultiColorsUntil(schemas = listOf(MyColors.WhiteNumberColor), duration = 1000, increment = 1)
            if (whiteNumber != null) {
                TouchActions.tap(whiteNumber.x, whiteNumber.y, delayTime = 800)
                TouchActions.tap(1130, 55, delayTime = 500)
                TouchActions.swipe(240, 500, 4000, 500)
                delayWithMultiplier(300)
                findAllResearchItems()
            }
        }
    }
    return enterMainScreen()
}

private suspend fun findAllResearchItems() {
    // Build a map from display name to setting key for filtering enabled items
    val displayNameToKey = MainBaseTroopsAndSpells.all.associate { it.displayName to it.key }

    // Filter research colors to only include enabled items
    val enabledResearchColors = MainBaseResearchColors.allResearchColors.filter { schema ->
        val displayName = schema.name ?: return@filter false
        val key = displayNameToKey[displayName] ?: return@filter false
        getBooleanConfigRuntime(key)
    }

    if (enabledResearchColors.isEmpty()) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，没有启用的研究项目")
        return
    }

    ShowMessage("账号${InGamesVars.currentAccountNumber}，已启用 ${enabledResearchColors.size} 个研究项目")
    repeat(8) {
        // Capture screenshot for searching
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndRestart("findAllResearchItems: Screen Capture Failed.")

        // Search for enabled research items on current screen
        for (schema in enabledResearchColors) {
            val result = findMultiColors(schema = schema, byteBuffer = screenBuffer, increment = 1)
            if (result != null) {
                val insufficientLeft = (result.x - 25).coerceAtLeast(0)
                val insufficientTop = (result.y + 60).coerceAtLeast(0)
                val insufficientRight = minOf(result.x + 100, screenBuffer.width - 1)
                val insufficientBottom = minOf(result.y + 100, screenBuffer.height - 1)

                val insufficientSchema = ColorSchema.rescope(
                    MyColors.MainBaseResearchInsufficientColors,
                    insufficientLeft, insufficientTop, insufficientRight, insufficientBottom
                )

                if (findMultiColors(byteBuffer = screenBuffer, schema = insufficientSchema, increment = 1) != null) {
                    ShowMessage("账号${InGamesVars.currentAccountNumber}，跳过 ${schema.name}: 资源不足")
                    continue
                }

                // Compute the crop region around the found item (full-screen coordinates)
                val cropLeft = (result.x - 25).coerceAtLeast(0)
                val cropTop = (result.y + 20).coerceAtLeast(0)
                val cropWidth = minOf(40, screenBuffer.width - cropLeft)
                val cropHeight = minOf(40, screenBuffer.height - cropTop)
                if (cropWidth > 0 && cropHeight > 0) {
                    // Iterate through all level entries; each level may have multiple schemas.
                    // A level is matched if ANY of its schemas is found in the crop area.
                    var detectedLevel: Int? = null
                    outer@ for ((level, levelSchemas) in ResearchLevelColors) {
                        for (levelColorSchema in levelSchemas) {
                            val levelSchema = ColorSchema.rescope(
                                levelColorSchema,
                                cropLeft, cropTop, cropLeft + cropWidth - 1, cropTop + cropHeight - 1
                            )
                            val levelResult = findMultiColors(byteBuffer = screenBuffer, schema = levelSchema, increment = 1)
                            if (levelResult != null) {
                                detectedLevel = level
                                break@outer
                            }
                        }
                    }
                    // Read target level offset from config and compute the target level
                    val researchLevelStr = getConfigRuntime(MainBaseSettings.RESEARCH_LEVEL.key)
                    val researchLevelOffset = researchLevelStr.toIntOrNull()
                        ?: logAndRestart("研究等级至 配置值无效: $researchLevelStr")
                    val maxLevel = MainBaseResearchMaxLevel.getMaxLevel(schema.name ?: "")
                        ?: logAndRestart("未找到 ${schema.name} 的最大等级")
                    val targetLevel = maxLevel - researchLevelOffset

                    // Skip items with unknown level (cannot determine if upgrade is needed)
                    if (detectedLevel == null) {
                        ShowMessage("账号${InGamesVars.currentAccountNumber}，跳过 ${schema.name}: 未知等级，无法判断是否需要升级")
                        continue
                    }

                    // Skip items already at or above the target level
                    if (detectedLevel >= targetLevel) {
                        ShowMessage("账号${InGamesVars.currentAccountNumber}，跳过 ${schema.name}: 当前等级$detectedLevel >= 目标等级$targetLevel (最大等级$maxLevel - $researchLevelOffset)")
                        continue
                    }

                    ShowMessage("账号${InGamesVars.currentAccountNumber}，找到研究项目: ${schema.name}, 等级$detectedLevel, 目标等级$targetLevel")
                    TouchActions.tap(result.x, result.y, delayTime = 500)
                    TouchActions.tap(894, 617, delayTime = 500)
                    clickRightBottom(3)
                    return
                }
            }
        }

        // No enabled item found on current screen, swipe to see more items
        TouchActions.swipe(860, 500, 400, 500, delayTime = 500)
        delayWithMultiplier(300)
    }

    ShowMessage("账号${InGamesVars.currentAccountNumber}，未找到任何已启用的研究项目")
    clickRightBottom(3)
}