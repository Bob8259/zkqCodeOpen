package com.coc.zkqcode.statehelper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppMode {
    Main,
    Run,
    SwitchAccount
}

object AppStateManager {
    var currentMode by mutableStateOf(AppMode.Main)
        private set

    fun setMode(mode: AppMode) {
        currentMode = mode
    }
}
