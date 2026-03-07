package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import java.util.Calendar

/**
 * Click in a grid pattern around the given base coordinates.
 * Clicks from (x+10, y+80) to (x+30, y+100) with step 8, creating a 3x3 grid.
 */
private suspend fun clickGridPattern(baseX: Int, baseY: Int) {
    for (dx in 10..30 step 8) {
        for (dy in 60..90 step 8) {
            TouchActions.tap(baseX + dx, baseY + dy, delayTime = 100)
        }
    }
}

suspend fun checkNewBuildingArrows(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.CHECK_NEW_BUILDING_ARROWS, InGamesVars.currentAccountNumber)
    val lastCheckTime = readMemory(storageKey).toIntOrNull()
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    // Check if arrow check was done today
    if (lastCheckTime != null && lastCheckTime == currentDay) {
        ShowMessage("今天已检查新建筑箭头，暂不检查")
        return true
    }

    zoomSmallMainBase()
    findMultiColorsUntil(schemas = listOf(MyColors.ArrowPointingDown), duration = 2000)?.let {
        clickGridPattern(it.x, it.y)
        writeMemory(storageKey, currentDay.toString())
        return enterMainScreen()
    }
    TouchActions.swipe(925, 146, 231, 563)
    delayWithMultiplier(200)
    findMultiColorsUntil(schemas = listOf(MyColors.ArrowPointingDown), duration = 2000)?.let {
        clickGridPattern(it.x, it.y)
        writeMemory(storageKey, currentDay.toString())
        return enterMainScreen()
    }
    writeMemory(storageKey, currentDay.toString())
    return enterMainScreen()
}
