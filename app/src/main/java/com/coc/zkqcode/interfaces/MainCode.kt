package com.coc.zkqcode.interfaces

import android.content.Context
import androidx.compose.runtime.Composable

interface MainCode {
    @Composable
    fun ShowMainUI(context: Context, onClose: () -> Unit) // 改为传入 Context 和 onClose 回调
}