package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.util.basic.RunShell
import java.net.URLDecoder

/**
 * Utility for converting Clash of Clans CopyArmy links to the
 * [clashofclans://] custom-scheme format, and for launching them.
 */
object CopyArmyLink {

    private const val CUSTOM_SCHEME_PREFIX = "clashofclans://action=CopyArmy&army="
    private const val WEB_HOST = "link.clashofclans.com"

    /**
     * Convert any supported CopyArmy URL to a uniform [clashofclans://] intent string.
     *
     * Supported input formats:
     *   - https://link.clashofclans.com/cn?action=CopyArmy&army=u2x0-3x3-2x2
     *   - https://link.clashofclans.com/en?action=CopyArmy&army=u2x0-3x3-2x2
     *   - clashofclans://action=CopyArmy&army=u2x0-3x3-2x2
     *
     * If the input is already a [clashofclans://] CopyArmy URL it is returned as-is.
     *
     * @param url The input URL (web link or custom-scheme link).
     * @return A [clashofclans://] intent string, or the original [url] if it cannot be parsed.
     */
    fun toClashIntent(url: String): String {
        // Already in the target format
        if (url.startsWith(CUSTOM_SCHEME_PREFIX)) return url

        val army = extractArmy(url) ?: return url
        return "$CUSTOM_SCHEME_PREFIX$army"
    }

    /**
     * Extract the [army] parameter value from a CopyArmy URL.
     *
     * Supports both:
     *   - https://link.clashofclans.com/{locale}?action=CopyArmy&army=...
     *   - clashofclans://action=CopyArmy&army=...
     *
     * @param url The input URL.
     * @return The raw army parameter value, or null if it cannot be extracted.
     */
    fun extractArmy(url: String): String? {
        val queryString: String = when {
            url.startsWith("https://$WEB_HOST/") || url.startsWith("http://$WEB_HOST/") -> {
                val queryIdx = url.indexOf('?')
                if (queryIdx < 0) return null
                url.substring(queryIdx + 1)
            }
            url.startsWith("clashofclans://") -> {
                url.removePrefix("clashofclans://")
            }
            else -> return null
        }

        for (pair in queryString.split("&")) {
            val eqIdx = pair.indexOf('=')
            if (eqIdx < 0) continue
            val key = URLDecoder.decode(pair.substring(0, eqIdx), "UTF-8")
            if (key == "army") {
                return URLDecoder.decode(pair.substring(eqIdx + 1), "UTF-8")
            }
        }
        return null
    }

    /**
     * Launch a CopyArmy link in the game via Android [am start].
     *
     * The input can be either a web link or a custom-scheme link — it will be
     * normalised before launching.
     *
     * Usage (matching the pattern in [com.coc.zkqcode.jar.code.mainbase.clan.joinClan]):
     * ```
     * openCopyArmyLink("https://link.clashofclans.com/cn?action=CopyArmy&army=u2x0-3x3-2x2")
     * ```
     *
     * @param armyLink The CopyArmy URL.
     */
    suspend fun openCopyArmyLink(armyLink: String) {
        val intentUrl = toClashIntent(armyLink)
        RunShell.runNoOutput(
            "am start -a android.intent.action.VIEW -d '$intentUrl' -p ${InGameVars.currentGameVersion.packageName}"
        )
    }
}
