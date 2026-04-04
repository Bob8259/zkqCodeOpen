package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
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
    TouchActions.tap(685, 590, delayTime = 300)//Open Smith
    val upgradeGear = findMultiColorsUntil(schemas = listOf(MyColors.UpgradeGearArrow, MyColors.NewGear), duration = 1500)
    if (upgradeGear != null) {
        TouchActions.tap(upgradeGear.x, upgradeGear.y, delayTime = 500)
        repeat(2) { TouchActions.tap(1130, 640, delayTime = 300) }
        TouchActions.tap(1220, 635, delayTime = 300)
    }
}