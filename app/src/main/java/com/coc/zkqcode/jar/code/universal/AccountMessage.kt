package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.util.basic.ShowMessage

fun AccountMessage(text: String, isChecking: Boolean = true) {
    ShowMessage("账号${InGamesVars.currentAccountNumber}，$text", isChecking)
}
