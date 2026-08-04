package com.coc.zkqcode.jar.code.universal.push

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

object PushRouter {

    suspend fun send(message: PushMessage): String? {
        return send(message, null, null)
    }

    suspend fun send(
        message: PushMessage,
        imageBytes: ByteArray?,
        brailleArt: String?
    ): String? {
        val provider = GlobalVars.configStates[GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
        return when (provider) {
            "1" -> WxPusherHelper.sendNotification(message, brailleArt)
            "2" -> PushPlusHelper.sendNotification(message, brailleArt)
            "3" -> TelegramHelper.sendNotification(message, imageBytes)
            "4" -> AstrBotHelper.sendNotification(message, imageBytes, brailleArt)
            else -> ServerChanHelper.send(message, brailleArt)
        }
    }

    /**
     * Platforms that accept real image pushes (screenshots).
     * Single source of truth so callers (e.g. EnterGameCheck) don't hardcode provider ids.
     */
    fun supportsImage(provider: String): Boolean = provider == "3" || provider == "4"
}
