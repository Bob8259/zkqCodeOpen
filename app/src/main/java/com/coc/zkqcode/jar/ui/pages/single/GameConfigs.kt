package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseConfig
import com.coc.zkqcode.jar.ui.pages.nightbase.NightBaseConfig

@Composable
fun GameConfig(index: Int, onNavigatePriority: (Int) -> Unit = {}) {
    MainBaseConfig(index = index, onNavigatePriority = onNavigatePriority)
    NightBaseConfig(index = index)
}