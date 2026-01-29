package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.findMultiColors
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import kotlinx.coroutines.delay


suspend fun checkReconnections(): Boolean {
    // 1. Capture the screen and cast safely
    val screenBuffer =
        ScreenCaptureManager.capture(asBitmap = false) as? ScreenCaptureManager.CaptureResult
            ?: return true

    // 2. Define the schemas to check against
    val homeSchemas = listOf(
        MyColors.Reconnection
    )

    // 3. Run the check and capture the result
    // We return the result of 'any' to determine if a reconnection event occurred.
    return homeSchemas.any { schema ->
        val match = findMultiColors(byteBuffer = screenBuffer, schema = schema)

        if (match != null) {
            // Retrieve the configuration state for the specific account
            val configKey = Schema.GLOBAL_SETTINGS.AFTER_KICK_OPTION.key
            val action = GlobalVars.configStates[configKey]?.value?.toInt()
                ?: logAndStop("Can not get $configKey")

            // 4. Implement logic based on the action value
            when (action) {
                0 -> {
                    // Action: Tap the "Reload" button
                    TouchActions.tap(379, 458)
                }

                1 -> {
                    // Action: Signal a need to switch accounts
                    // By returning false here, we tell the caller the check 'failed' or needs a different flow
                    return false
                }

                2 -> {
                    // Action: Wait/Idle
                    delay(1000)
                }
            }
            true // Match found and handled
        } else {
            true// No match for this schema
        }
    }
}