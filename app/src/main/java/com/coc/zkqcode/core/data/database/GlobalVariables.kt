package com.coc.zkqcode.core.data.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.data.websocket.ServerActions
import com.coc.zkqcode.interfaces.MainCode
import java.util.concurrent.ConcurrentHashMap

object GlobalVars {
    // Basic components
    var serverActions by mutableStateOf<ServerActions?>(null)
    @Volatile var pluginUI: MainCode? = null
    @Volatile var serverPath: String = ""

    @Volatile var isConfigLoaded: Boolean = false

    // Auto-run features
    var isAutoRunEnabled by mutableStateOf(true)
    var autoRunTimer by mutableIntStateOf(0)//测试专用，记得改回60


    // Window positioning
    var absorbEdge by mutableIntStateOf(0) // 1: Left, 0: Right
    var absorbYPercentage by mutableIntStateOf(50) // Percentage of Y axis
    var updateWindowPosition by mutableStateOf(false)

    // Configuration States - Thread-safe map for concurrent access from UI and background threads
    val configStates: MutableMap<String, MutableState<String>> = ConcurrentHashMap()

    // IME management
    @Volatile var defaultInputMethod: String? = null

    //Running state management
    var isPlaying = mutableStateOf(true)
    @Volatile var isSwitchingAccount = false
}
