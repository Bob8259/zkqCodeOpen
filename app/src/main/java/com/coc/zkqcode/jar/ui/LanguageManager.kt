package com.coc.zkqcode.jar.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.data.database.GlobalVars
import java.util.Locale

/**
 * Manages the app's display language.
 *
 * On first access the manager auto-detects the device locale:
 * - Chinese (zh) → display Chinese
 * - Anything else → default to English
 *
 * After configs are loaded, [syncWithConfig] should be called so that
 * the saved language preference overrides the auto-detected value.
 *
 * Users can toggle the language at runtime via [toggleLanguage],
 * which also persists the choice into [GlobalVars.configStates].
 *
 * UI components can read [toggleButtonText] and [isChinese] (both
 * Compose-reactive) to update themselves automatically.
 */
object LanguageManager {

    /** Config key used to persist the language preference. */
    private const val CONFIG_KEY = "language"

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

    /**
     * Sync the in-memory language state with the persisted config value.
     *
     * If a saved preference exists in [GlobalVars.configStates] it takes
     * precedence over the auto-detected device language. This should be
     * called once after [ConfigManager.initializeAllConfigs] completes.
     */
    fun syncWithConfig() {
        val saved = GlobalVars.configStates[CONFIG_KEY]?.value
        if (saved != null) {
            isChinese = saved == "zh"
        } else {
            // No saved value yet — persist the current (auto-detected) state
            GlobalVars.configStates[CONFIG_KEY] = mutableStateOf(if (isChinese) "zh" else "en")
        }
    }

    /** Toggle between Chinese and English, and persist the choice. */
    fun toggleLanguage() {
        isChinese = !isChinese
        GlobalVars.configStates[CONFIG_KEY] = mutableStateOf(if (isChinese) "zh" else "en")
    }

    /**
     * Text shown on the language-toggle button.
     * - When current language is English → show "中文" (invite switch to Chinese)
     * - When current language is Chinese → show "ENG" (invite switch to English)
     */
    val toggleButtonText: String
        get() = if (isChinese) "ENG" else "中文"
}
