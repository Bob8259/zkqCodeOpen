package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun startRaid(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.START_RAID.key)) return true
    if (!isWithinRaidWeekend()) {
        return true
    }
    // Guard: only start raid once per 3 days (4320 minutes)
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.START_RAID, InGameVars.currentAccountNumber)
    if (!checkMemoryFile(storageKey, 4320)) {
        accountMessage("3天内已发起过突袭，跳过")
        return true
    }
    accountMessage("准备开启突袭")
    if (!enterClanCapital()) return enterMainScreen()
    val startRaidButton = findMultiColorsUntil(listOf(MyColors.StartRaid), 3000)
    if (startRaidButton != null) {
        TouchActions.tap(startRaidButton.x, startRaidButton.y)
        TouchActions.tap(620, 600)
        // Record the timestamp so the guard activates for the next 3 days
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    }
    return enterMainScreen()
}