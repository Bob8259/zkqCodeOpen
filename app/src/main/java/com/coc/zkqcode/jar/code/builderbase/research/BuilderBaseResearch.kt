package com.coc.zkqcode.jar.code.builderbase.research

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
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
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseTroops


suspend fun builderBaseResearch(): Boolean {
    if (getBooleanConfigRuntime(Schema.BUILDER_BASE_SETTINGS.BUILDER_BASE_RESEARCH.key) && WorkerAndResearch.detectResearch(BaseType.Builder)) {
        val research = findMultiColors(schema = MyColors.ResearchIcon, increment = 1)
            ?: findMultiColors(schema = MyColors.ResearchIcon2, increment = 1)
        if (research != null) {
            TouchActions.tap(research.x, research.y)
            TouchActions.tap(research.x, research.y + 130)//Open research tab
            val backArrow = findMultiColorsUntil(schemas = listOf(MyColors.BuilderResearchBackArrow), duration = 1000, increment = 1)
            if (backArrow != null) {
                TouchActions.tap(backArrow.x, backArrow.y)
                builderBaseCheckAllResearch()
            }
        }
    }
    return enterMainScreen()
}

/** Holds info about an upgradeable builder base research item. */
private data class BuilderUpgradableItem(
    val troopName: String,
    val priority: Int,
    val x: Int,
    val y: Int,
)

suspend fun builderBaseCheckAllResearch() {
    // Sort troops by user-defined research priority
    val orderedTroops = BuilderBaseTroops.all.sortedBy { troop ->
        val priorityKey = "${troop.key}_research_priority"
        getConfigRuntime(priorityKey).toIntOrNull() ?: Int.MAX_VALUE
    }

    // ========== Phase 1: Single capture, scan all grid positions ==========
    val screenBuffer = ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
        ?: return

    val upgradeableItems = mutableListOf<BuilderUpgradableItem>()
    val insufficientItems = mutableListOf<String>()

    for (troop in orderedTroops) {
        if (!getBooleanConfigRuntime(troop.key)) continue

        val i = BuilderBaseTroops.all.indexOf(troop)
        if (i < 0) continue
        val row = i / 6
        val col = i % 6
        val x1 = 310 + col * 140
        val y1 = if (row == 0) 460 else 600
        val x2 = x1 + 40
        val y2 = if (row == 0) 500 else 640

        // Check for elixir icon in this grid cell (reusing single capture)
        val elixirIcon = findMultiColors(
            schema = ColorSchema.rescope(MyColors.BuilderResearchElixir, x1, y1, x2, y2),
            byteBuffer = screenBuffer,
            increment = 1
        ) ?: continue

        // Check resource sufficiency
        val resX1 = (x1 - 100).coerceAtLeast(0)
        val insufficient = findMultiColors(
            schema = ColorSchema.rescope(MyColors.BuilderResearchInsufficientResources, resX1, y1, x1, y2),
            byteBuffer = screenBuffer,
            increment = 1
        )
        if (insufficient != null) {
            insufficientItems.add(troop.displayName)
            continue
        }

        upgradeableItems.add(BuilderUpgradableItem(
            troopName = troop.displayName,
            priority = getConfigRuntime("${troop.key}_research_priority").toIntOrNull() ?: Int.MAX_VALUE,
            x = elixirIcon.x,
            y = elixirIcon.y,
        ))
    }

    // ========== Phase 2: Pick highest priority and upgrade ==========
    if (upgradeableItems.isEmpty()) {
        val msg = if (insufficientItems.isEmpty()) {
            "未找到任何可升级的夜世界研究项目"
        } else {
            "无可升级项目，${insufficientItems.size}项资源不足: ${insufficientItems.joinToString("、")}"
        }
        accountMessage(msg)
        return
    }

    // Sort by priority ascending (lower number = higher priority)
    upgradeableItems.sortBy { it.priority }

    // Build consolidated message: insufficient list + summary + selected target
    val insufficientBlock = if (insufficientItems.isNotEmpty()) {
        "资源不足 (${insufficientItems.size}项):\n${insufficientItems.joinToString("\n") { "  - $it" }}\n"
    } else ""
    val summaryLines = upgradeableItems.mapIndexed { index, item ->
        "${index + 1}. ${item.troopName} (优先级${item.priority})"
    }
    val target = upgradeableItems.first()
    accountMessage("${insufficientBlock}夜世界研究可升级 (共${upgradeableItems.size}项):\n${summaryLines.joinToString("\n")}\n\n→ 升级: ${target.troopName}")

    // Phase 2: Upgrade the highest priority item using coordinates from Phase 1
    TouchActions.tap(target.x, target.y)
    TouchActions.tap(955, 610, delayTime = 200)
    clickRightBottom(2)
}


