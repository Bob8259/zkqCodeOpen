package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.Schema
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.jar.code.colorschema.MyColors
import timber.log.Timber

suspend fun CheckReconnections(currentAccountNumber: Int) {
    // 1. Capture the screen and cast safely
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return // Exits the function if capture fails

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.Reconnection
    )
    Timber.d(getConfigRuntime(currentAccountNumber, Schema.GLOBAL_SETTINGS.AFTER_KICK_OPTION.key))
    // 3. Run the check (Result is ignored, function returns Unit)
    homeSchemas.any { schema ->
        findMultiColors(byteBuffer = screenBuffer, schema = schema) != null
    }
}