package com.coc.zkqcode.jar.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Manages the app's display language.
 *
 * On first access the manager auto-detects the device locale:
 * - Chinese (zh) → display Chinese
 * - Anything else → default to English
 *
 * Users can toggle the language at runtime via [toggleLanguage].
 * UI components can read [toggleButtonText] and [isChinese] (both
 * Compose-reactive) to update themselves automatically.
 */
object LanguageManager {

    /** `true` when the current display language is Chinese, `false` for English. */
    var isChinese by mutableStateOf(true)
        private set

    /** Whether the initial device-language detection has been performed. */
    private var detected = false

    /**
     * Detect the device language and set [isChinese] accordingly.
     * Safe to call multiple times — only the first invocation has effect.
     */
    fun detectDeviceLanguage() {
        if (detected) return
        isChinese = Locale.getDefault().language.startsWith("zh")
        detected = true
    }

    /** Toggle between Chinese and English. */
    fun toggleLanguage() {
        isChinese = !isChinese
    }

    /**
     * Text shown on the language-toggle button.
     * - When current language is English → show "中文" (invite switch to Chinese)
     * - When current language is Chinese → show "ENG" (invite switch to English)
     */
    val toggleButtonText: String
        get() = if (isChinese) "ENG" else "中文"
}
