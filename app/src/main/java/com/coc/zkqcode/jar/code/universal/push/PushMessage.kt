package com.coc.zkqcode.jar.code.universal.push

data class PushMessage(
    val title: String,
    val desp: String = "",
    val imageBytes: ByteArray? = null,
    val brailleArt: String? = null
) {
    val content: String
        get() = if (desp.isBlank()) title else "$title\n$desp"

    val telegramText: String
        get() = if (desp.isBlank()) title else "*$title*\n$desp"
}
