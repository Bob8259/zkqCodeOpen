package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.ui.pages.single.HomeScreen
import com.coc.zkqcode.jar.ui.pages.single.SwitchAccount
import com.coc.zkqcode.utils.components.GlobalVars
import java.io.File

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
        if (GlobalVars.showManualMode) {
            SwitchAccount(onClose = onClose)
        } else {
            HomeScreen(onSaveSuccess = onClose)
        }
    }
}