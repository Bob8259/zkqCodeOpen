package com.coc.zkqcode.jar.code.mainbase.research

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.MainBaseSettings
import com.coc.zkqcode.jar.ui.schema.details.MainBaseTroopsAndSpells

suspend fun mainBaseResearch(): Boolean {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESEARCH_SETTING.key) && WorkerAndResearch.detectResearch(BaseType.Main)) {
        val researchIcon = findMultiColorsUntil(schemas = listOf(MyColors.ResearchIcon, MyColors.ResearchIcon2), duration = 1000, increment = 1)
        if (researchIcon != null) {
            TouchActions.tap(researchIcon.x + 20, researchIcon.y)
            // Find white number indicating available research and tap it
            val whiteNumber = findMultiColorsUntil(schemas = listOf(MyColors.WhiteNumberColor), duration = 1000, increment = 1)
            if (whiteNumber != null) {
                TouchActions.tap(whiteNumber.x, whiteNumber.y, delayTime = 800)
                TouchActions.tap(1130, 55)
                TouchActions.swipe(240, 500, 4000, 500, delayAfter = 800)
                findAllResearchItems()
            }
        }
    }
    return enterMainScreen()
}

/** Holds info about an upgradeable research item found during scouting. */
private data class UpgradableItem(
    val schema: ColorSchema,
    val troopName: String,
    val priority: Int,
    val x: Int,
    val y: Int,
    val detectedLevel: Int,
    val targetLevel: Int,
    val screenIndex: Int
)

private suspend fun findAllResearchItems() {
    // Build a map from display name to setting key for filtering enabled items
    val displayNameToKey = MainBaseTroopsAndSpells.all.associate { it.displayName to it.key }

    // Filter research colours to only include enabled items
    val enabledResearchColors = MainBaseResearchColors.allResearchColors.filter { schema ->
        val displayName = schema.name ?: return@filter false
        val key = displayNameToKey[displayName] ?: return@filter false
        getBooleanConfigRuntime(key)
    }

    if (enabledResearchColors.isEmpty()) {
        accountMessage("没有启用的研究项目")
        return
    }

    // Build priority map for each schema
    val schemaPriorityMap = enabledResearchColors.associateWith { schema ->
        val displayName = schema.name ?: return@associateWith Int.MAX_VALUE
        val troopKey = displayNameToKey[displayName] ?: return@associateWith Int.MAX_VALUE
        val priorityKey = "${troopKey}_research_priority"
        getConfigRuntime(priorityKey).toIntOrNull() ?: Int.MAX_VALUE
    }

    accountMessage("已启用 ${enabledResearchColors.map { it.name }.distinct().size} 个研究项目")

    // Read target level offset once (same for all items)
    val researchLevelStr = getConfigRuntime(MainBaseSettings.RESEARCH_LEVEL.key)
    val researchLevelOffset = researchLevelStr.toIntOrNull()
        ?: logAndRestart("研究等级至 配置值无效: $researchLevelStr")

    // ========== Phase 1: Scout all screens, collect upgradeable info ==========
    val upgradeableItems = mutableListOf<UpgradableItem>()
    val seenNames = mutableSetOf<String>() // Avoid duplicate processing of same troop across variant schemas

    for (screenIdx in 0 until 9) {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndRestart("findAllResearchItems: Screen Capture Failed.")

        for (schema in enabledResearchColors) {
            val troopName = schema.name ?: continue
            if (troopName in seenNames) continue // Already processed this troop

            val result = findMultiColors(schema = schema, byteBuffer = screenBuffer, increment = 1) ?: continue

            // Check resource sufficiency
            val insufficientLeft = (result.x - 25).coerceAtLeast(0)
            val insufficientTop = (result.y + 60).coerceAtLeast(0)
            val insufficientRight = minOf(result.x + 100, screenBuffer.width - 1)
            val insufficientBottom = minOf(result.y + 100, screenBuffer.height - 1)
            val insufficientSchema = ColorSchema.rescope(
                MyColors.MainBaseResearchInsufficientColors,
                insufficientLeft, insufficientTop, insufficientRight, insufficientBottom
            )
            if (findMultiColors(byteBuffer = screenBuffer, schema = insufficientSchema, increment = 1) != null) {
                accountMessage("跳过 ${troopName}: 资源不足")
                seenNames.add(troopName)
                continue
            }

            // Detect level from pre-parsed game JSON data (more reliable than color badge matching)
            val detectedLevel = InGameVars.researchItemLevels[troopName]

            if (researchLevelOffset > 0) {
                // When targeting a specific level offset, we need the current level to decide
                if (detectedLevel == null) {
                    accountMessage("跳过 ${troopName}: 数据中无等级信息")
                    seenNames.add(troopName)
                    continue
                }

                val maxLevel = MainBaseResearchMaxLevel.getMaxLevel(troopName)
                    ?: logAndRestart("未找到 $troopName 的最大等级")
                val targetLevel = maxLevel - researchLevelOffset

                if (detectedLevel >= targetLevel) {
                    accountMessage("跳过 ${troopName}: 当前等级$detectedLevel >= 目标等级$targetLevel")
                    seenNames.add(troopName)
                    continue
                }

                seenNames.add(troopName)
                upgradeableItems.add(UpgradableItem(
                    schema = schema,
                    troopName = troopName,
                    priority = schemaPriorityMap[schema] ?: Int.MAX_VALUE,
                    x = result.x,
                    y = result.y,
                    detectedLevel = detectedLevel,
                    targetLevel = targetLevel,
                    screenIndex = screenIdx
                ))
                accountMessage("发现可升级: ${troopName}, 等级$detectedLevel, 页$screenIdx")
            } else {
                // Max level target: no level detection needed, always upgradeable
                val maxLevel = MainBaseResearchMaxLevel.getMaxLevel(troopName)
                    ?: logAndRestart("未找到 $troopName 的最大等级")
                seenNames.add(troopName)
                upgradeableItems.add(UpgradableItem(
                    schema = schema,
                    troopName = troopName,
                    priority = schemaPriorityMap[schema] ?: Int.MAX_VALUE,
                    x = result.x,
                    y = result.y,
                    detectedLevel = 0,
                    targetLevel = maxLevel,
                    screenIndex = screenIdx
                ))
                accountMessage("发现可升级: ${troopName}, 页$screenIdx")
            }
        }

        // Swipe to next page (except after the last page)
        if (screenIdx < 8) {
            TouchActions.swipe(860, 500, 400, 500, delayAfter = 300)
        }
    }

    // ========== Phase 2: Select highest priority item and upgrade ==========
    if (upgradeableItems.isEmpty()) {
        accountMessage("未找到任何已启用且可升级的研究项目")
        clickRightBottom(3)
        return
    }

    // Sort by priority (ascending: lower number = higher priority)
    upgradeableItems.sortBy { it.priority }

    val target = upgradeableItems.first()

    // ========== Phase 2: Navigate back page by page, re-scan and upgrade ==========
    // After Phase 1 we are at screen 8 (the last page).
    // Swipe backwards one page at a time, re-capturing and re-verifying at each page.
    val targetSchemas = enabledResearchColors.filter { it.name == target.troopName }
    var foundAndUpgraded = false
    for (currentPage in 8 downTo 0) {
        val buffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: logAndRestart("Phase 2: Screen capture failed at page $currentPage")

        for (targetSchema in targetSchemas) {
            val result = findMultiColors(schema = targetSchema, byteBuffer = buffer, increment = 1) ?: continue

            // Re-verify resource sufficiency
            val insLeft = (result.x - 25).coerceAtLeast(0)
            val insTop = (result.y + 60).coerceAtLeast(0)
            val insRight = minOf(result.x + 100, buffer.width - 1)
            val insBottom = minOf(result.y + 100, buffer.height - 1)
            val insSchema = ColorSchema.rescope(
                MyColors.MainBaseResearchInsufficientColors,
                insLeft, insTop, insRight, insBottom
            )
            if (findMultiColors(byteBuffer = buffer, schema = insSchema, increment = 1) != null) {
                accountMessage("跳过 ${target.troopName}: 资源不足（阶段二重检）")
                continue
            }

            // Re-verify level is still below target (using pre-parsed game data)
            val reDetectedLevel = InGameVars.researchItemLevels[target.troopName]
            if (reDetectedLevel != null && reDetectedLevel < target.targetLevel) {
                // Still upgradeable — perform the upgrade
                TouchActions.tap(result.x, result.y)
                TouchActions.tap(894, 617)
                clickRightBottom(3)
                foundAndUpgraded = true
                accountMessage("升级: ${target.troopName}, 等级$reDetectedLevel")
                break
            }
        }

        if (foundAndUpgraded) break

        // Target not found on this page — swipe back one more page
        if (currentPage > 0) {
            TouchActions.swipe(400, 500, 860, 500, delayAfter = 300)
        }
    }

    if (!foundAndUpgraded) {
        accountMessage("阶段二未能定位到 ${target.troopName}，跳过")
        clickRightBottom(3)
    }
}