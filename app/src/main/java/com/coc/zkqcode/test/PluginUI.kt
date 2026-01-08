package com.coc.zkqcode.test

import androidx.compose.runtime.Composable

// 放在主 App 和 插件的共同模块中，或者两边各写一份，包名必须一致
interface PluginUI {
    @Composable
    fun ShowIcon(imagePath: String)
}