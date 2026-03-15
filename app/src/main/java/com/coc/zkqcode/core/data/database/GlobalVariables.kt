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
    // --- Volatile fields (no Compose reactivity, thread-safe reads/writes) ---
    @Volatile var pluginUI: MainCode? = null
    @Volatile var serverPath: String = ""
    @Volatile var isConfigLoaded: Boolean = false
    @Volatile var defaultInputMethod: String? = null
    @Volatile var isSwitchingAccount: Boolean = false
    @Volatile var absorbEdge: Int = 0 // 1: Left, 0: Right
    @Volatile var absorbYPercentage: Int = 50 // Percentage of Y axis
    @Volatile var updateWindowPosition: Boolean = false

    // --- Compose-reactive fields (observed by UI for recomposition) ---
    var serverActions by mutableStateOf<ServerActions?>(null)
    var isAutoRunEnabled by mutableStateOf(true)
    var autoRunTimer by mutableIntStateOf(60)
    var isPlaying = mutableStateOf(true)

    // --- Thread-safe map for concurrent access from UI and background threads ---
    val configStates: MutableMap<String, MutableState<String>> = ConcurrentHashMap()
}
