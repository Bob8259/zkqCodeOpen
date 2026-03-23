package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.setZKQInputMethod
import com.coc.zkqcode.jar.code.universal.smalltools.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar

suspend fun joinClan(): Boolean {
    val targetTAG = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAN_TAG.key)
    val clanCode = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.CLAN_JOIN_MESSAGE.key)

    // Skip if no clan tag is specified
    if (targetTAG.isBlank()) {
        return enterMainScreen()
    }

    // Once-per-day guard: skip if already checked today for this account
    val storageKey = StorageKeys.withAccountNumber(StorageKeys.JOIN_CLAN, InGamesVars.currentAccountNumber)
    val lastCheckDay = readMemory(storageKey).toIntOrNull()
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    if (lastCheckDay != null && lastCheckDay == currentDay) {
        ShowMessage("账号${InGamesVars.currentAccountNumber}，今日已检测加入部落，暂不重复执行")
        return true
    }

    ShowMessage("正在加入部落: $targetTAG")
    // Open the clan profile via deep link using the correct package name for the current game version
    RunShell.runNoOutput(
        "am start -a android.intent.action.VIEW -d 'clashofclans://action=OpenClanProfile&tag=$targetTAG' -p ${InGamesVars.currentGameVersion.packageName}"
    )
    val joinOrExitButton = findMultiColorsUntil(schemas = listOf(MyColors.JoinClanButton, MyColors.ExitClanButton), duration = 2000)
    if (joinOrExitButton != null) {
        val joinClanButton = findMultiColors(schema = MyColors.JoinClanButton)
        if (joinClanButton != null) {
            setZKQInputMethod()
            TouchActions.tap(joinClanButton.x, joinClanButton.y, delayTime = 500)
            TouchActions.tap(642, 165, delayTime = 300)//input message
            ZKQInputMethodService.instance?.inputText(clanCode) ?: logAndRestart("获取输入法失败")
            delayWithMultiplier(200)
            TouchActions.tap(787, 327, delayTime = 300)//send message
            clickRightBottom(1)
        }
    }
    // Mark today as checked so clan join won't run again today
    writeMemory(storageKey, currentDay.toString())
    return enterMainScreen()
}