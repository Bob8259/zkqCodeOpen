package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.system.inputmethod.inputTextReliable
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun joinClan(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.AUTO_JOIN_CLAN.key) || InGameVars.townHallLevel < 3) return true
    val targetTAG = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAN_TAG.key)
    val clanCode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key)

    // Skip if no clan tag is specified
    if (targetTAG.isBlank()) {
        val notJoinClan = findMultiColorsUntil(schemas = listOf(MyColors.NotJoinClanFlag, MyColors.NotJoinClanFlag2), duration = 0)
        if (notJoinClan != null) {
            TouchActions.tap(53, 42)//open profile page
            TouchActions.tap(754, 55)//join clan page
            val searchResult = findMultiColorsUntil(schemas = listOf(MyColors.SearchOptions, MyColors.ApplyClanSetting), duration = 6000)
            if (searchResult != null) {
                repeat(3) {
                    TouchActions.tap(728, 551)
                }
            }
        }
    } else {
        // Once-per-day guard: skip if already checked within 24 hours for this account
        val storageKey = StorageKeys.withAccountNumber(StorageKeys.JOIN_CLAN, InGameVars.currentAccountNumber)
        if (!checkMemoryFile(storageKey, 1440)) {
            accountMessage("今日已检测加入部落，暂不重复执行")
            return true
        }

        accountMessage("正在加入部落: $targetTAG")
        // Open the clan profile via deep link using the correct package name for the current game version
        RunShell.runNoOutput(
            "am start -a android.intent.action.VIEW -d 'clashofclans://action=OpenClanProfile&tag=$targetTAG' -p ${InGameVars.currentGameVersion.packageName}"
        )
        val joinOrExitButton = findMultiColorsUntil(schemas = listOf(MyColors.JoinClanButton, MyColors.ExitClanButton), duration = 2000)
        if (joinOrExitButton != null) {
            val joinClanButton = findMultiColors(schema = MyColors.JoinClanButton)
            if (joinClanButton != null) {
                TouchActions.tap(joinClanButton.x, joinClanButton.y)
                TouchActions.tap(640, 165, delayTime = 300)//input message
                if (!clanCode.isBlank())
                    inputTextReliable(clanCode)
                delayWithMultiplier(200)
                TouchActions.tap(790, 330, delayTime = 300)//send message
                TouchActions.tap(780, 500)//directly join
                clickRightBottom(1)
            }
        }
        // Mark as checked so clan join won't run again within 24 hours
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
    }
    return enterMainScreen()
}
