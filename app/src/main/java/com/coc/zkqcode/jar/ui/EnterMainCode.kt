package com.coc.zkqcode.jar.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coc.zkqcode.interfaces.MainCode
import com.coc.zkqcode.jar.ui.pages.single.HomeScreen
import java.io.File

class EnterMainCode : MainCode {
    @Composable
    override fun ShowMainUI(context: Context, onClose: () -> Unit) {
        val assetDir = File(context.filesDir, "assets")
        val iconFile = File(assetDir, "main_icon.png")

        // 1. 获取本地持久化的初始值
        val sp = remember { context.getSharedPreferences("plugin_prefs", Context.MODE_PRIVATE) }
        var inputText by remember { mutableStateOf(sp.getString("text", "") ?: "") }

        HomeScreen(onSaveSuccess = onClose)
    }
}