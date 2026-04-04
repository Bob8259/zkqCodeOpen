package com.coc.zkqcode.jar.code.mainbase.herohall

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun upgradeGears(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_ALL_GEAR.key) && getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.UPGRADE_WERA_GEAR.key)) return true

    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.UPGRADE_GEARS, InGamesVars.currentAccountNumber
    )

    // Guard: only attempt gear upgrade once per 8 hours (480 minutes)
    if (!checkMemoryFile(storageKey, 480)) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，该账号8小时内已检测装备升级")
        return true
    }

    ShowMessage("准备升级装备")
    val result = withHeroHall { upgradeGearsAction() }

    // Always record completion and return to main screen
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    return result
}

private suspend fun upgradeGearsAction() {
//TODO, I will do it later
}