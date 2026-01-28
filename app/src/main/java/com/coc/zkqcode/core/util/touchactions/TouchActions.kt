package com.coc.zkqcode.core.util.touchactions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import kotlinx.coroutines.delay

object TouchActions {
    suspend fun swipe(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        delayTime: Long = 200L,
        isJitter: Boolean = true
    ) {
        val serverActions =
            GlobalVars.serverActions ?: logAndStop("Server actions not found at swipe")

        // Send touchdown at x,y
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to startX.toFloat(),
                "y" to startY.toFloat(),
                "id" to 1
            )
        )

        // Delay for time * 0.7
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: logAndStop("Failed to get delayMultiplier at swipe")
        delay((delayTime * 0.7 * delayMultiplier).toLong())

        // Move to second x,y
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchmove",
                "x" to endX.toFloat(),
                "y" to endY.toFloat(),
                "id" to 1,
                "duration" to (delayTime * 0.5 * delayMultiplier).toInt(), // documentation says touchmove has duration
                "jitter" to isJitter
            )
        )

        // Delay for 'time'
        delay((delayTime * delayMultiplier).toLong())

        // Touch up
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchup",
                "id" to 1
            )
        )
    }

    suspend fun pinchIn(
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        finalX: Int,
        finalY: Int,
        duration: Long = 200L,
        isJitter: Boolean = true
    ) {
        val serverActions =
            GlobalVars.serverActions ?: logAndStop("Failed to get serverAction at pinchIn")
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: logAndStop("Failed to get delayMultiplier at pinchIn")
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "pinchin",
                "x1" to x1,
                "y1" to y1,
                "x2" to x2,
                "y2" to y2,
                "finalX" to finalX,
                "finalY" to finalY,
                "duration" to (duration * delayMultiplier).toInt(),
                "jitter" to isJitter
            )
        )
    }

    suspend fun pinchOut(
        x1: Int,
        y1: Int,
        x2: Int,
        y2: Int,
        finalX: Int,
        finalY: Int,
        duration: Long = 200L,
        isJitter: Boolean = true
    ) {
        val serverActions =
            GlobalVars.serverActions ?: logAndStop("Failed to get serverAction at pinchOut")
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: logAndStop("Failed to get delayMultiplier at pinchOut")
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "pinchout",
                "x1" to x1,
                "y1" to y1,
                "x2" to x2,
                "y2" to y2,
                "finalX" to finalX,
                "finalY" to finalY,
                "duration" to (duration * delayMultiplier).toInt(),
                "jitter" to isJitter
            )
        )
    }
}