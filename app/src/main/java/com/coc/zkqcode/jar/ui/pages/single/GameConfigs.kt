package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.runtime.Composable
import com.coc.zkqcode.jar.ui.pages.mainbase.MainBaseConfig
import com.coc.zkqcode.jar.ui.pages.nightbase.NightBaseConfig

@Composable
fun GameConfig(index: Int) {
    MainBaseConfig(index = index)
    NightBaseConfig(index = index)
}