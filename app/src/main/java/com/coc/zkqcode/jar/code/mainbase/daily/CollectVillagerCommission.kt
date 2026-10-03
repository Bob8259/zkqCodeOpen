package com.coc.zkqcode.jar.code.mainbase.daily

import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun collectVillagerCommission(): Boolean {
    // Skip when the commission claim switch is disabled in settings.
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAIM_VILLAGER_COMMISSIONS.key)) return true

    val storageKey = StorageKeys.withAccountNumber(StorageKeys.CLAIM_VILLAGER_COMMISSIONS, InGameVars.currentAccountNumber)

    // Time guard: only run the commission check once per day (1440 minutes) per account.
    if (!checkMemoryFile(storageKey, 1440)) {
        return true
    }

    accountMessage("开始领取村民委托")

    // Open the commission page.
    TouchActions.tap(1215, 360)

    // Keep scanning for up to 5 seconds and tap every "搞定" (Finish) button that appears,
    // so multiple ready commissions are claimed one after another. Each scan round waits up to 1 second.
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < 5000) {
        findMultiColorsUntil(listOf(MyColors.FinishCommission), 100, increment = 1)?.let {
            TouchActions.tap(it.x, it.y)
        }
    }

    // Open the chest to collect the accumulated commission rewards.
    TouchActions.tap(555, 565)

    // Record the guard timestamp so the check only runs once a day.
    writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())

    // Dismiss the commission/chest UI and make sure we are back on the main screen.
    return enterMainScreen()
}