package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.ui.components.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import kotlinx.coroutines.delay

class EnterGameCheck {

    /**
     * Waits for the game to enter the main screen within a specified timeout.
     * Returns true if successful, false if it times out.
     */
    suspend fun enterMainScreen(): Boolean {
        // 1. Initialize the start time
        val startTime = System.currentTimeMillis()
        // 2. Get the timeout duration from GlobalVars (assumed to be in seconds)
        // We multiply by 1000 to compare milliseconds to milliseconds
        val timeoutSeconds = GlobalVars.configStates["enter_game_timer"]?.value?.toIntOrNull()
            ?: logAndStop("enter main game error, can not get game timer")
        val timeoutMillis = timeoutSeconds * 1000L

        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            // 3. Insert your logic to check if the main screen is actually visible
            val isAtMainScreen = checkUIVisibility()

            if (isAtMainScreen) {
                return true
            }

            // 4. Wait for 1 second before checking again to save CPU cycles
            delay(10)
        }

        // Return false if the loop finishes without finding the main screen
        return false
    }

    private fun checkUIVisibility(): Boolean {
        // TODO: Implement your image recognition or UI automation check here
        return false
    }
}