package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.runtime.Composable
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.ui.pages.single.HomeScreen
import com.coc.zkqcode.jar.ui.pages.single.SwitchAccount
import com.coc.zkqcode.utils.components.GlobalVars
import com.coc.zkqcode.utils.state.AppMode
import com.coc.zkqcode.utils.state.AppStateManager

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
        if (AppStateManager.currentMode == AppMode.SwitchAccount) {
            SwitchAccount(onClose = onClose)
        } else if (AppStateManager.currentMode == AppMode.Main) {
            HomeScreen(onSaveSuccess = onClose)
        }
    }
}