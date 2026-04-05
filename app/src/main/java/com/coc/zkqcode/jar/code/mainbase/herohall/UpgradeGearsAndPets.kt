package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.ColorSchema
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import kotlinx.coroutines.delay

suspend fun upgradeGears(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) && !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key) && !getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_PETS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.UPGRADE_GEARS_AND_PETS, InGamesVars.currentAccountNumber
    )

    // Guard: only attempt gear upgrade once per 8 hours (480 minutes)
    if (!checkMemoryFile(storageKey, 480)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号8小时内已检测装备和战宠升级")
//        return true
    }

    ShowMessage("准备升级装备和战宠")
    var result = false
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key)) {
        result = withHeroHall { upgradeGearsAction() }
    }
    // Always record completion and return to main screen
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return result
}

private suspend fun upgradeGearsAction() {
    TouchActions.tap(685, 590, delayTime = 600)//Open Smith
    val upgradeGear = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeGearArrow, MyColors.NewGear), duration = 800)
    if (upgradeGear != null) {
        TouchActions.tap(upgradeGear.x, upgradeGear.y, delayTime = 500)
        repeat(2) { TouchActions.tap(1130, 640, delayTime = 300) }
        TouchActions.tap(1220, 635, delayTime = 300)
    }
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key)) {
        suspend fun tryUpgradeInVisibleRows(): Boolean {
            val firstRowArrow = ColorSchema.rescope(MyColors.UpgradeGearArrow, 80, 425, 1210, 460)
            val firstRowNewGear = ColorSchema.rescope(MyColors.NewGear, 80, 425, 1210, 460)
            val secondRowArrow = ColorSchema.rescope(MyColors.UpgradeGearArrow, 90, 510, 1220, 540)
            val secondRowNewGear = ColorSchema.rescope(MyColors.NewGear, 90, 510, 1220, 540)
            val gear = findMultiColorsUntil(
                schemas = listOf(firstRowArrow, firstRowNewGear, secondRowArrow, secondRowNewGear), duration = 800
            )
            if (gear != null) {
                TouchActions.tap(gear.x, gear.y, delayTime = 500)
                repeat(2) { TouchActions.tap(1130, 640, delayTime = 300) }
                TouchActions.tap(1220, 635, delayTime = 300)
                return true
            }
            return false
        }

        // Scan both visible rows in one pass using four rescoped schemas.
        if (tryUpgradeInVisibleRows()) return

        // Swipe once, then run the same one-pass scan again.
        TouchActions.swipe(1220, 510, 0, 510, delayTime = 300)
        if (tryUpgradeInVisibleRows()) return
    }
    backToHeroHall()
}

private suspend fun backToHeroHall() {
    // Stop retrying after 10 seconds to avoid getting stuck in an endless back loop.
    val deadline = System.currentTimeMillis() + 10_000
    while (System.currentTimeMillis() < deadline) {
        val smithIcon = findMultiColors(MyColors.SmithOreIcon)
        if (smithIcon != null) {
            TouchActions.tap(1222, 80)
        }
        TouchActions.tap(1216, 622)
        delayWithMultiplier(500)
    }
}