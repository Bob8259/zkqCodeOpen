package com.coc.zkqcode.jar.code.mainbase.research

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.colorschema.colorpackage.mainbase.MainBaseResearchColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.details.MainBaseResearchPriorities
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

/** Holds info about an upgradeable main-base research item found on screen. */
private data class ResearchItem(
    val name: String,   // Chinese name from ColorSchema (e.g. "野蛮人")
    val priority: Int,  // User-defined priority (lower = higher priority)
)

suspend fun findAllResearchItems() {
    // Build name → priority-key and name → enable-key lookup maps from the settings definitions.
    val nameToPriorityKey = mutableMapOf<String, String>()
    val nameToEnableKey = mutableMapOf<String, String>()
    for (def in MainBaseResearchPriorities.all) {
        nameToPriorityKey[def.displayName] = def.key
    }
    for (def in MainBaseTroopsAndSpells.all) {
        nameToEnableKey[def.displayName] = def.key
    }

    // Read the user-selected research level target: 0 = Max, 1 = Max-1, 2 = Max-2
    val researchLevelConfig = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESEARCH_LEVEL.key).toIntOrNull() ?: 0

    val upgradeableItems = mutableListOf<ResearchItem>()
    val insufficientItems = mutableListOf<String>()
    val foundNames = mutableSetOf<String>() // Dedup across pages and duplicate schemas

    repeat(8) {
        // Take a single screenshot per swipe page and reuse it for all searches.
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return

        // Scan all research colour schemas on the current page.
        for (schema in MainBaseResearchColors.allResearchColors) {
            val itemName = schema.name ?: continue

            // Skip if already collected or checked on a previous page.
            if (itemName in foundNames) continue

            // Skip if the user has disabled this research item.
            val enableKey = nameToEnableKey[itemName]
            if (enableKey != null && !getBooleanConfigRuntime(enableKey)) continue

            // Search for the research item icon in the current screenshot.
            val itemPos = findMultiColors(
                schema = schema,
                byteBuffer = screenBuffer,
                increment = 1
            ) ?: continue

            // Check for the insufficient-resources indicator in the area below the item icon.
            val insufficientSchema = ColorSchema.rescope(
                MainBaseResearchColors.MainBaseResearchInsufficientColors,
                x1 = (itemPos.x - 35).coerceAtLeast(0),
                y1 = itemPos.y + 59,
                x2 = itemPos.x + 105,
                y2 = itemPos.y + 84
            )
            val insufficient = findMultiColors(
                schema = insufficientSchema,
                byteBuffer = screenBuffer,
                increment = 1
            )

            // Mark as found so we don't re-check on subsequent pages.
            foundNames.add(itemName)

            if (insufficient != null) {
                // Resources insufficient — skip this item.
                if (itemName !in insufficientItems) {
                    insufficientItems.add(itemName)
                }
            } else {
                // Resources sufficient — check if the item is below the target level.
                val maxLevel = MainBaseResearchMaxLevel.getMaxLevel(itemName)
                if (maxLevel != null) {
                    val currentLevel = InGameVars.researchItemLevels[itemName] ?: 0
                    val targetLevel = maxLevel - researchLevelConfig
                    if (currentLevel >= targetLevel) {
                        // Already at or above the target level — skip.
                        continue
                    }
                }
                // Item is eligible — add to the upgradeable list.
                val priorityKey = nameToPriorityKey[itemName]
                val priority = if (priorityKey != null) {
                    getConfigRuntime(priorityKey).toIntOrNull() ?: Int.MAX_VALUE
                } else {
                    Int.MAX_VALUE
                }
                upgradeableItems.add(ResearchItem(itemName, priority))
            }
        }

        // Swipe left to reveal the next page of research items.
        TouchActions.swipe(950, 500, 200, 500, delayAfter = 300)
    }

    // Sort the collected items by user-defined priority (ascending — lower number = higher priority).
    upgradeableItems.sortBy { it.priority }

    // Display the result to the user.
    if (upgradeableItems.isEmpty()) {
        val msg = if (insufficientItems.isEmpty()) {
            "未找到任何可升级的研究项目"
        } else {
            "无可升级项目，${insufficientItems.size}项资源不足: ${insufficientItems.joinToString("、")}"
        }
        accountMessage(msg)
    } else {
        accountMessage("可升级研究(${upgradeableItems.size}项)")
        startResearch(upgradeableItems)
    }
}

/**
 * Iterates through the sorted upgradeable items and attempts to find and tap
 * the first available item on the research screen.
 *
 * For each item: resets the scroll position, then swipes through up to 8 pages
 * looking for it. If found, taps it and stops. If not found after 8 swipes,
 * moves on to the next item in the priority list.
 */
private suspend fun startResearch(upgradeableItems: List<ResearchItem>) {
    // Build a name → schemas lookup so we can search for an item by its name.
    val schemasByName = MainBaseResearchColors.allResearchColors
        .filter { it.name != null }
        .groupBy { it.name!! }

    // Read the fast-research fallback config.
    val isFallbackEnabled = getBooleanConfigRuntime(
        Schema.MAIN_BASE_SETTINGS.RESEARCH_AFTER_CONSECUTIVE_FAILURES.key
    )
    val fallbackThreshold = if (isFallbackEnabled) {
        getConfigRuntime(
            Schema.MAIN_BASE_SETTINGS.RESEARCH_AFTER_CONSECUTIVE_FAILURES_THRESHOLD.key
        ).toIntOrNull() ?: 3
    } else {
        0
    }
    var consecutiveFailureCount = 0

    for (item in upgradeableItems) {
        // If we've hit the fallback threshold, switch to fast research:
        // research the first available item on the current screen.
        if (isFallbackEnabled && consecutiveFailureCount >= fallbackThreshold) {
            accountMessage("连续${consecutiveFailureCount}次未找到研究项目，改为快速研究")
            if (fastResearch()) return
            // If fast research also failed, reset counter and continue trying priority items.
            consecutiveFailureCount = 0
        }

        val schemas = schemasByName[item.name] ?: continue
        var found = false

        // Reset the research list to the beginning.
        TouchActions.swipe(240, 500, 4000, 500, delayAfter = 800)

        for (page in 0 until 8) {
            if (found) break

            // Take a screenshot and search for any of this item's schemas.
            val screenBuffer = ScreenCaptureManager.capture(asBitmap = false)
                as? ScreenCaptureManager.CaptureResult ?: continue

            for (schema in schemas) {
                val pos = findMultiColors(
                    schema = schema,
                    byteBuffer = screenBuffer,
                    increment = 1
                )
                if (pos != null) {
                    // Found the item — tap it and exit.
                    TouchActions.tap(pos.x, pos.y, delayTime = 800)
                    found = true
                    break
                }
            }

            if (!found) {
                // Swipe to the next page.
                TouchActions.swipe(950, 500, 200, 500, delayAfter = 300)
            }
        }

        if (found) {
            accountMessage("开始研究: ${item.name}")
            return
        } else {
            consecutiveFailureCount++
        }
    }

    // If we exhausted all priority items without success, try fast research as a last resort.
    if (isFallbackEnabled && consecutiveFailureCount >= fallbackThreshold) {
        accountMessage("优先级列表全部未找到，改为快速研究")
        fastResearch()
    }
}

/**
 * Fast research fallback: scans the research screen for any enabled item
 * (regardless of priority) and taps the first one found. Used when consecutive
 * failures exceed the configured threshold.
 *
 * @return true if an item was found and tapped, false otherwise.
 */
private suspend fun fastResearch(): Boolean {
    // Build a name → enable-key lookup to respect user-disabled items.
    val nameToEnableKey = mutableMapOf<String, String>()
    for (def in MainBaseTroopsAndSpells.all) {
        nameToEnableKey[def.displayName] = def.key
    }

    // Reset to the beginning.
    TouchActions.swipe(240, 500, 4000, 500, delayAfter = 800)

    for (page in 0 until 8) {
        val screenBuffer = ScreenCaptureManager.capture(asBitmap = false)
            as? ScreenCaptureManager.CaptureResult ?: continue

        for (schema in MainBaseResearchColors.allResearchColors) {
            val itemName = schema.name ?: continue
            val enableKey = nameToEnableKey[itemName]
            if (enableKey != null && !getBooleanConfigRuntime(enableKey)) continue

            val pos = findMultiColors(
                schema = schema,
                byteBuffer = screenBuffer,
                increment = 1
            )
            if (pos != null) {
                TouchActions.tap(pos.x, pos.y, delayTime = 800)
                accountMessage("快速研究: $itemName")
                return true
            }
        }

        TouchActions.swipe(950, 500, 200, 500, delayAfter = 300)
    }
    return false
}