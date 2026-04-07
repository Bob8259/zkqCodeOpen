package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.core.util.touchactions.TouchActions.swipe
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun useMagicItems(): Boolean {
    // Only the explicit magic-item toggles should enable this flow.
    val shouldUseMagicItems = listOf(
        Schema.MAIN_BASE_SETTINGS.USE_RESEARCH_POTION.key,
        Schema.MAIN_BASE_SETTINGS.SELL_TRAINING_POTION.key,
        Schema.MAIN_BASE_SETTINGS.USE_CLOCK_TOWER_POTION.key,
        Schema.MAIN_BASE_SETTINGS.SELL_CLOCK_TOWER_POTION.key,
        Schema.MAIN_BASE_SETTINGS.USE_BUILDER_POTION.key,
        Schema.MAIN_BASE_SETTINGS.SELL_RING_OF_WALL.key
    ).any { configKey ->
        getBooleanConfigRuntime(configKey)
    }
    if (!shouldUseMagicItems) return true
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.USE_MAGIC_ITEMS, InGamesVars.currentAccountNumber
    )

    // Guard the magic item check so each account only scans once per day.
    if (!checkMemoryFile(storageKey, 1440)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号今日已检测魔法物品")
        return true
    }
    ShowMessage("账号${InGamesVars.currentAccountNumber}，正在检测魔法物品")
    clickRightBottom(1)
    zoomSmallMainBase()

    // Scan the current view first, then retry once after swiping upward.
    suspend fun scanAndTapMagicItems(): Boolean {
        val scan = tiledAiDetect(
            modelName = "building-detect", callerTag = "UseMagicItems", classIndex = 0
        )
        for (detection in scan) {
            val box = detection.boundingBox
            TouchActions.tap(box.centerX().toInt(), box.centerY().toInt(), delayTime = 500)
            val magicItemIcon = findMultiColors(MyColors.MagicItemIcon)
            if (magicItemIcon != null) {
                TouchActions.tap(magicItemIcon.x, magicItemIcon.y, delayTime = 500)
                // Record memory immediately after the magic item entry is opened.
                delayWithMultiplier(100000)
                writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
                clickRightBottom(1)
                return true
            }
        }
        return false
    }

    if (!scanAndTapMagicItems()) {
        swipe(900, 130, 0, 720)
        scanAndTapMagicItems()
    }
    return enterMainScreen()
}