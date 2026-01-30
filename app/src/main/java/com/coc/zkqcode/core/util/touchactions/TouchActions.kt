package com.coc.zkqcode.core.util.touchactions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import kotlinx.coroutines.delay
import kotlin.random.Random

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
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: logAndStop("Failed to get delayMultiplier at swipe")

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
        delay((delayTime * 0.7 * delayMultiplier).toLong())

        // Move loop
        val moveDuration = (delayTime * 0.5 * delayMultiplier).toLong()
        performMove(
            duration = moveDuration,
            isJitter = isJitter,
            PointerMove(1, startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat())
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

        // Start P1 and P2
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to x1.toFloat(),
                "y" to y1.toFloat(),
                "id" to 1
            )
        )
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to x2.toFloat(),
                "y" to y2.toFloat(),
                "id" to 2
            )
        )

        val moveDuration = (duration * delayMultiplier).toLong()
        performMove(
            duration = moveDuration,
            isJitter = isJitter,
            PointerMove(1, x1.toFloat(), y1.toFloat(), finalX.toFloat(), finalY.toFloat()),
            PointerMove(2, x2.toFloat(), y2.toFloat(), finalX.toFloat(), finalY.toFloat())
        )

        // End P1 and P2
        serverActions.sendActionSync(
            mapOf("actionType" to "touch_action", "subAction" to "touchup", "id" to 1)
        )
        serverActions.sendActionSync(
            mapOf("actionType" to "touch_action", "subAction" to "touchup", "id" to 2)
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

        // Start P1 and P2 at center
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to finalX.toFloat(),
                "y" to finalY.toFloat(),
                "id" to 1
            )
        )
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to finalX.toFloat(),
                "y" to finalY.toFloat(),
                "id" to 2
            )
        )

        val moveDuration = (duration * delayMultiplier).toLong()
        performMove(
            duration = moveDuration,
            isJitter = isJitter,
            PointerMove(1, finalX.toFloat(), finalY.toFloat(), x1.toFloat(), y1.toFloat()),
            PointerMove(2, finalX.toFloat(), finalY.toFloat(), x2.toFloat(), y2.toFloat())
        )

        // End P1 and P2
        serverActions.sendActionSync(
            mapOf("actionType" to "touch_action", "subAction" to "touchup", "id" to 1)
        )
        serverActions.sendActionSync(
            mapOf("actionType" to "touch_action", "subAction" to "touchup", "id" to 2)
        )
    }

    private class PointerMove(
        val id: Int,
        val fromX: Float,
        val fromY: Float,
        val toX: Float,
        val toY: Float
    )

    private suspend fun performMove(
        duration: Long,
        isJitter: Boolean,
        vararg pointers: PointerMove
    ) {
        val serverActions = GlobalVars.serverActions ?: return
        val stepInterval = 10L
        val steps = maxOf(1, (duration / stepInterval).toInt())

        for (i in 1..steps) {
            val t = i.toFloat() / steps
            pointers.forEach { p ->
                val currentX = p.fromX + (p.toX - p.fromX) * t
                val currentY = p.fromY + (p.toY - p.fromY) * t

                val jitterX = if (isJitter) Random.nextInt(-2, 3) else 0
                val jitterY = if (isJitter) Random.nextInt(-2, 3) else 0

                serverActions.sendActionSync(
                    mapOf(
                        "actionType" to "touch_action",
                        "subAction" to "touchmove",
                        "x" to (currentX + jitterX),
                        "y" to (currentY + jitterY),
                        "id" to p.id
                    )
                )
            }
            delay(stepInterval)
        }
    }

    suspend fun tap(
        x: Int,
        y: Int,
        isJitter: Boolean = true
    ) {
        val serverActions =
            GlobalVars.serverActions ?: logAndStop("Server actions not found at tap")
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: logAndStop("Failed to get delayMultiplier at tap")

        // Send touchdown at x,y
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to x.toFloat(),
                "y" to y.toFloat(),
                "id" to 1
            )
        )

        // Random delay of 20-60 milliseconds
        val randomDelay = Random.nextLong(60, 101)
        delay((randomDelay * delayMultiplier).toLong())

        if (isJitter) {
            val offsetX = Random.nextInt(-3, 3)
            val offsetY = Random.nextInt(-3, 3)
            serverActions.sendActionSync(
                mapOf(
                    "actionType" to "touch_action",
                    "subAction" to "touchmove",
                    "x" to (x + offsetX).toFloat(),
                    "y" to (y + offsetY).toFloat(),
                    "id" to 1,
                    "jitter" to true
                )
            )
        }

        // Touch up
        serverActions.sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchup",
                "id" to 1
            )
        )
    }
}