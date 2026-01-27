package com.coc.zkqcode.core.data.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.util.fileactions.FileActions
import com.coc.zkqcode.interfaces.MainCode

object GlobalVars {
    // Basic componeents
    var fileActions by mutableStateOf<FileActions?>(null)
    var pluginUI: MainCode? = null

    // Auto-run features
    var isAutoRunEnabled by mutableStateOf(true)
    var autoRunTimer by mutableIntStateOf(1)//测试专用，记得改回60


    // Window positioning
    var absorbEdge by mutableIntStateOf(1) // 1: Left, 0: Right
    var absorbYPercentage by mutableIntStateOf(50) // Percentage of Y axis
    var updateWindowPosition by mutableStateOf(false)

    // Configuration States
    val configStates = mutableMapOf<String, MutableState<String>>()

    // IME management
    var defaultInputMethod: String? = null

    //Running state management
    var isPlaying = mutableStateOf(true)
}
