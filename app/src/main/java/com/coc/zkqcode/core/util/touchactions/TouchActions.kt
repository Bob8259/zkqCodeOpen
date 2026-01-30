package com.coc.zkqcode.core.util.touchactions

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import kotlinx.coroutines.delay
import kotlin.random.Random

import kotlin.math.sqrt

object TouchActions {
    suspend fun swipe(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        delayTime: Long? = null,
        isJitter: Boolean = true
    ) {
        val actualDelayTime = delayTime ?: Random.nextLong(300, 501)
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
        delay((actualDelayTime * 0.7 * delayMultiplier).toLong())

        // Move loop
        val moveDuration = (actualDelayTime * 0.5 * delayMultiplier).toLong()
        performMove(
            duration = moveDuration,
            isJitter = isJitter,
            PointerMove(1, startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat())
        )

        // Delay for 'time'
        delay((actualDelayTime * delayMultiplier).toLong())

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
        duration: Long? = null,
        isJitter: Boolean = true
    ) {
        val actualDuration = duration ?: Random.nextLong(300, 501)
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

        val moveDuration = (actualDuration * delayMultiplier).toLong()
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
        duration: Long? = null,
        isJitter: Boolean = true
    ) {
        val actualDuration = duration ?: Random.nextLong(300, 501)
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

        val moveDuration = (actualDuration * delayMultiplier).toLong()
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
        val serverActions = GlobalVars.serverActions ?: logAndStop("Server actions not found at performMove")
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: logAndStop("Failed to get delayMultiplier at performMove")

        if (!isJitter) {
            val stepInterval = 10L
            val steps = maxOf(1, (duration / stepInterval).toInt())
            for (i in 1..steps) {
                val t = i.toFloat() / steps
                pointers.forEach { p ->
                    val currentX = p.fromX + (p.toX - p.fromX) * t
                    val currentY = p.fromY + (p.toY - p.fromY) * t
                    serverActions.sendActionSync(
                        mapOf(
                            "actionType" to "touch_action",
                            "subAction" to "touchmove",
                            "x" to currentX,
                            "y" to currentY,
                            "id" to p.id
                        )
                    )
                }
                delay((stepInterval * delayMultiplier).toLong())
            }
            return
        }

        // Advanced Humanoid Logic (isJitter = true)
        val refP = pointers[0]
        val dx = refP.toX - refP.fromX
        val dy = refP.toY - refP.fromY
        val totalDistance = sqrt(dx * dx + dy * dy).toDouble()
        
        // steps = distance / random(30..35)
        val stepDivisor = Random.nextInt(10, 20)
        val steps = maxOf(3, (totalDistance / stepDivisor).toInt())
        val avgDelay = duration.toFloat() / steps
        
        class PointerState(
            val p1X: Float,
            val p1Y: Float,
            var lastNoiseX: Float = 0f,
            var lastNoiseY: Float = 0f
        )
        
        val pointerStates = pointers.map { p ->
            val pDx = p.toX - p.fromX
            val pDy = p.toY - p.fromY
            val pDist = sqrt(pDx * pDx + pDy * pDy)
            val midX = (p.fromX + p.toX) / 2f
            val midY = (p.fromY + p.toY) / 2f
            
            val perpX = -pDy
            val perpLen = sqrt(perpX * perpX + pDx * pDx)
            
            if (perpLen > 0) {
                val unitPerpX = perpX / perpLen
                val unitPerpY = pDx / perpLen
                val offsetMag = pDist * Random.nextDouble(0.05, 0.10).toFloat()
                val side = if (Random.nextBoolean()) 1f else -1f
                PointerState(midX + unitPerpX * offsetMag * side, midY + unitPerpY * offsetMag * side)
            } else {
                PointerState(midX, midY)
            }
        }

        var currentTLinear = 0f
        for (i in 1..steps) {
            val idealIncrement = 1f / steps
            // Small drift in time progression progression (t)
            val drift = (Random.nextFloat() - 0.5f) * (idealIncrement * 0.4f)
            currentTLinear += (idealIncrement + drift)
            val t = if (i == steps) 1f else currentTLinear.coerceIn(0f, 1f)
            
            // Ease-in-Ease-out progression: weighted blend of linear and 3t^2-2t^3
            val easedT = (t * 0.2f) + ((3 * t * t - 2 * t * t * t) * 0.8f)
            
            pointers.forEachIndexed { index, p ->
                val state = pointerStates[index]
                // Quadratic Bezier: (1-t)^2*P0 + 2(1-t)t*P1 + t^2*P2
                val invT = 1f - easedT
                val bX = invT * invT * p.fromX + 2 * invT * easedT * state.p1X + easedT * easedT * p.toX
                val bY = invT * invT * p.fromY + 2 * invT * easedT * state.p1Y + easedT * easedT * p.toY
                
                // Continuous micro-offsets
                state.lastNoiseX += (Random.nextFloat() - 0.5f) * 0.4f
                state.lastNoiseY += (Random.nextFloat() - 0.5f) * 0.4f
                
                serverActions.sendActionSync(
                    mapOf(
                        "actionType" to "touch_action",
                        "subAction" to "touchmove",
                        "x" to (bX + state.lastNoiseX),
                        "y" to (bY + state.lastNoiseY),
                        "id" to p.id
                    )
                )
            }
            
            val varDelay = (avgDelay * Random.nextDouble(0.8, 1.2)).toLong()
            delay((varDelay * delayMultiplier).toLong())
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
            val offsetX = Random.nextInt(-3, 4)
            val offsetY = Random.nextInt(-3, 4)
            serverActions.sendActionSync(
                mapOf(
                    "actionType" to "touch_action",
                    "subAction" to "touchmove",
                    "x" to (x + offsetX).toFloat(),
                    "y" to (y + offsetY).toFloat(),
                    "id" to 1,
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