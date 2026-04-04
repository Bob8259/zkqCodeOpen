package com.coc.zkqcode.jar.code.mainbase.clan

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun requestReinforcements(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.REQUEST_REINFORCEMENT_SETTING.key)) return true
    ShowMessage("准备请求增援")
    val clanChat = findMultiColors(MyColors.ClanChatIcon)
    if (clanChat != null) {
        for (i in 1..5) {
            val iUnderstandButton = findMultiColors(MyColors.IUnderstand)
            if (iUnderstandButton != null) {
                TouchActions.tap(iUnderstandButton.x, iUnderstandButton.y, delayTime = 500)
                continue
            }
            val requestReinforcement = findMultiColors(MyColors.RequestReinforcement)
            if (requestReinforcement != null) {
                TouchActions.tap(requestReinforcement.x, requestReinforcement.y, delayTime = 500)
                TouchActions.tap(780, 490, delayTime = 500)
                break
            }
        }
    }
    return enterMainScreen()
}