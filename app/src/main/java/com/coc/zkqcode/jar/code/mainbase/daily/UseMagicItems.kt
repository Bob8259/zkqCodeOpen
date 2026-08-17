package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.aidetection.DetectionRegion
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun useMagicItems(): Boolean {
    // Only the explicit magic-item toggles should enable this flow.
    val shouldUseMagicItems = listOf(
        Schema.MAIN_BASE_SETTINGS.USE_RESEARCH_POTION.key,
        Schema.MAIN_BASE_SETTINGS.USE_BUILDER_POTION.key,
        Schema.MAIN_BASE_SETTINGS.SELL_CLOCK_TOWER_POTION.key,
        Schema.MAIN_BASE_SETTINGS.SELL_RESOURCE_POTION.key,
        Schema.MAIN_BASE_SETTINGS.SELL_RING_OF_WALL.key,
        Schema.MAIN_BASE_SETTINGS.SELL_SHOVEL_OF_OBSTACLES.key
    ).any { configKey ->
        getBooleanConfigRuntime(configKey)
    }
    if (!shouldUseMagicItems) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.USE_MAGIC_ITEMS, InGameVars.currentAccountNumber
    )

    // Guard the magic item check so each account only scans once per day.
    if (!checkMemoryFile(storageKey, 1440)) {
        accountMessage("该账号今日已检测魔法物品")
        return true
    }
    accountMessage("正在检测魔法物品")
    zoomSmallMainBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapMagicItems(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "UseMagicItems", classIndex = 4
        )
        for (detection in scan) {
            val box = detection.boundingBox
            val cx = box.centerX().toInt()
            val cy = box.centerY().toInt()
            if (!DetectionRegion.isInsideRegion(cx, cy)) continue
            TouchActions.tap(cx, cy)
            val magicItemIcon = findMultiColorsUntil(schemas = listOf(MyColors.MagicItemIcon, MyColors.MagicItemIcon2, MyColors.MagicItemIcon3, MyColors.MagicItemIcon4, MyColors.MagicItemIcon5), duration = 200)
            if (magicItemIcon != null) {
                TouchActions.tap(magicItemIcon.x, magicItemIcon.y, delayTime = 800)
                // Record memory immediately after the magic item entry is opened.
                repeat(3) {
                    checkMagicItems()
                    TouchActions.swipe(640, 440, 640, 180, delayAfter = 200)
                }
                writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
                clickRightBottom(1)
                return true
            }
        }
        return false
    }

    if (!scanAndTapMagicItems()) {
        TouchActions.swipe(900, 130, 0, 720)
        scanAndTapMagicItems()
    }
    return enterMainScreen()
}

private suspend fun checkMagicItems() {
    suspend fun handleMagicItem(configKey: String, colorSchema: ColorSchema, action: suspend () -> Unit) {
        if (!getBooleanConfigRuntime(configKey)) return
        val itemPosition = findMultiColors(colorSchema)
        if (itemPosition != null) {
            TouchActions.tap(itemPosition.x, itemPosition.y, delayTime = 200)
            action()
        }
    }
    repeat(3) {
        // Each configured item uses the same interaction pattern after its icon is found.
        handleMagicItem(Schema.MAIN_BASE_SETTINGS.USE_RESEARCH_POTION.key, MyColors.ResearchPotion) {
            usePotion()
        }
        handleMagicItem(Schema.MAIN_BASE_SETTINGS.USE_BUILDER_POTION.key, MyColors.BuilderPotion) {
            usePotion()
        }
        handleMagicItem(Schema.MAIN_BASE_SETTINGS.SELL_RESOURCE_POTION.key, MyColors.ResourcePotion) {
            sellPotion()
        }
        handleMagicItem(Schema.MAIN_BASE_SETTINGS.SELL_CLOCK_TOWER_POTION.key, MyColors.ClockTowerPotion) {
            sellPotion()
        }
        handleMagicItem(Schema.MAIN_BASE_SETTINGS.SELL_RING_OF_WALL.key, MyColors.WallRings) {
            sellPotion()
        }
        handleMagicItem(Schema.MAIN_BASE_SETTINGS.SELL_SHOVEL_OF_OBSTACLES.key, MyColors.ShovelOfObstacles) {
            sellPotion()
        }
    }
}

private suspend fun usePotion() {
    val usePotionButton = findMultiColorsUntil(schemas = listOf(MyColors.UsePotionButton), duration = 600)
    if (usePotionButton != null) {
        TouchActions.tap(usePotionButton.x, usePotionButton.y)
        TouchActions.tap(630, 445)
    }
}

private suspend fun sellPotion() {
    val sellPotionButton = findMultiColorsUntil(schemas = listOf(MyColors.SellPotionButton), duration = 600)
    if (sellPotionButton != null) {
        TouchActions.tap(sellPotionButton.x, sellPotionButton.y)
        TouchActions.tap(775, 515)
    }
}
