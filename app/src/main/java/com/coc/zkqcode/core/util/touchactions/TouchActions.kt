package com.coc.zkqcode.core.util.touchactions

import com.coc.zkqcode.core.data.database.GlobalVars
import kotlinx.coroutines.delay

object TouchActions {
    suspend fun swipe(startX: Int, startY: Int, endX: Int, endY: Int, delayTime: Long = 200L) {
        val connection = GlobalVars.fileActions?.getConnection() ?: return

        // Send touchdown at x,y
        connection.sendAction(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to startX.toFloat(),
                "y" to startY.toFloat(),
                "id" to 1
            )
        )

        // Delay for time * 0.7
        delay((delayTime * 0.7).toLong())

        // Move to second x,y
        connection.sendAction(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchmove",
                "x" to endX.toFloat(),
                "y" to endY.toFloat(),
                "id" to 1,
                "duration" to delayTime // documentation says touchmove has duration
            )
        )

        // Delay for 'time'
        delay(delayTime)

        // Touch up
        connection.sendAction(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchup",
                "id" to 1
            )
        )
    }

    fun pinchIn(x1: Int, y1: Int, x2: Int, y2: Int, finalX: Int, finalY: Int, duration: Long = 200L) {
        val connection = GlobalVars.fileActions?.getConnection() ?: return
        connection.sendAction(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "pinchin",
                "x1" to x1,
                "y1" to y1,
                "x2" to x2,
                "y2" to y2,
                "finalX" to finalX,
                "finalY" to finalY,
                "duration" to duration
            )
        )
    }

    fun pinchOut(x1: Int, y1: Int, x2: Int, y2: Int, finalX: Int, finalY: Int, duration: Long = 200L) {
        val connection = GlobalVars.fileActions?.getConnection() ?: return
        connection.sendAction(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "pinchout",
                "x1" to x1,
                "y1" to y1,
                "x2" to x2,
                "y2" to y2,
                "finalX" to finalX,
                "finalY" to finalY,
                "duration" to duration
            )
        )
    }
}