package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun requestReinforcement(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key) || InGameVars.townHallLevel < 3) return true
    accountMessage("准备请求增援")
    val clanChat = findMultiColors(MyColors.ClanChatIcon)
    if (clanChat != null) {
        // Tap the clan chat icon to open the chat panel
        TouchActions.tap(clanChat.x, clanChat.y)
        if (InGameVars.currentGameVersion == GameVersion.CN) {
            TouchActions.tap(523, 239, delayTime = 200)
            TouchActions.tap(152, 88, delayTime = 200)
        } else {
            TouchActions.tap(523, 98, delayTime = 300)//Global version chat area
        }
        for (i in 1..5) {
            val iUnderstandButton = findMultiColors(MyColors.IUnderstand)
            if (iUnderstandButton != null) {
                TouchActions.tap(iUnderstandButton.x, iUnderstandButton.y)
                continue
            }
            val requestReinforcement = findMultiColors(MyColors.RequestReinforcement)
            if (requestReinforcement != null) {
                TouchActions.tap(requestReinforcement.x, requestReinforcement.y)
                TouchActions.tap(780, 490)
                break
            }
            if (findMultiColors(MyColors.CanNotRequestReinforcement) != null) break
            delayWithMultiplier(200)
        }
    }
    return if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.DONATION_SETTING.key)) true
    else enterMainScreen()
}