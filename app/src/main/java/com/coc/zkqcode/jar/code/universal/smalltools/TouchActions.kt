package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.data.database.ServerConnectionMethod
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.basic.waitForPlay
import com.coc.zkqcode.core.util.fileactions.LogHelper
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.sqrt
import kotlin.random.Random

object TouchActions {
    private val activePointers = mutableSetOf<Int>()

    private fun getDelayMultiplier(): Float {
        return GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: LogHelper.logAndRestart("Failed to get delayMultiplier")
    }

    private fun getServerActions() = GlobalVars.serverActions ?: LogHelper.logAndRestart("Server actions not found")

    suspend fun touchDown(x: Float, y: Float, id: Int) {
        activePointers.add(id)
        getServerActions().sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchdown",
                "x" to x,
                "y" to y,
                "id" to id
            )
        )
    }

    suspend fun touchMove(x: Float, y: Float, id: Int, isJitter: Boolean = true) {
        var finalX = x
        var finalY = y
        if (isJitter) {
            finalX += Random.Default.nextInt(-1, 2).toFloat()
            finalY += Random.Default.nextInt(-1, 2).toFloat()
        }
        getServerActions().sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchmove",
                "x" to finalX,
                "y" to finalY,
                "id" to id
            )
        )
    }

    suspend fun touchUp(id: Int) {
        activePointers.remove(id)
        getServerActions().sendActionSync(
            mapOf(
                "actionType" to "touch_action",
                "subAction" to "touchup",
                "id" to id
            )
        )
    }

    suspend fun releaseAllPointers() {
        val pointersCopy = activePointers.toSet()
        for (id in pointersCopy) {
            try {
                touchUp(id)
            } catch (_: Exception) {
            }
        }
        activePointers.clear()
    }

    suspend fun moveSmoothly(
        fromX: Float,
        fromY: Float,
        toX: Float,
        toY: Float,
        duration: Int,
        id: Int = 1,
        isJitter: Boolean = false
    ) {
        performMove(
            duration = duration,
            isJitter = isJitter,
            PointerMove(id, fromX, fromY, toX, toY)
        )
    }


    suspend fun swipe(
        startX: Int,
        startY: Int,
        endX: Int,
        endY: Int,
        delayTime: Int? = null,
        isJitter: Boolean = true,
        delayAfter: Int = 100
    ) {
        waitForPlay()
        val actualDelayTime = delayTime ?: Random.Default.nextInt(200, 301)
        val delayMultiplier = getDelayMultiplier()

        touchDown(startX.toFloat(), startY.toFloat(), 1)
        try {
            // Delay for time * 0.7
            delayWithMultiplier((actualDelayTime * 0.7).toInt())

            // Move loop
            val moveDuration = (actualDelayTime * 0.5 * delayMultiplier).toInt()
            performMove(
                duration = moveDuration,
                isJitter = isJitter,
                PointerMove(1, startX.toFloat(), startY.toFloat(), endX.toFloat(), endY.toFloat())
            )

            // Delay for 'time'
            delayWithMultiplier(actualDelayTime)
        } finally {
            withContext(NonCancellable) {
                touchUp(1)
            }
        }
        if (delayAfter > 0) {
            delayWithMultiplier(delayAfter)
        }
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
        waitForPlay()
        val actualDuration = duration ?: Random.Default.nextLong(100, 201)
        val delayMultiplier = getDelayMultiplier()

        touchDown(x1.toFloat(), y1.toFloat(), 1)
        touchDown(x2.toFloat(), y2.toFloat(), 2)
        try {
            val moveDuration = (actualDuration * delayMultiplier).toInt()
            performMove(
                duration = moveDuration,
                isJitter = isJitter,
                PointerMove(1, x1.toFloat(), y1.toFloat(), finalX.toFloat(), finalY.toFloat()),
                PointerMove(2, x2.toFloat(), y2.toFloat(), finalX.toFloat(), finalY.toFloat())
            )
        } finally {
            withContext(NonCancellable) {
                touchUp(1)
                touchUp(2)
            }
        }
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
        waitForPlay()
        val actualDuration = duration ?: Random.Default.nextLong(300, 501)
        val delayMultiplier = getDelayMultiplier()

        touchDown(finalX.toFloat(), finalY.toFloat(), 1)
        touchDown(finalX.toFloat(), finalY.toFloat(), 2)
        try {
            val moveDuration = (actualDuration * delayMultiplier).toInt()
            performMove(
                duration = moveDuration,
                isJitter = isJitter,
                PointerMove(1, finalX.toFloat(), finalY.toFloat(), x1.toFloat(), y1.toFloat()),
                PointerMove(2, finalX.toFloat(), finalY.toFloat(), x2.toFloat(), y2.toFloat())
            )
        } finally {
            withContext(NonCancellable) {
                touchUp(1)
                touchUp(2)
            }
        }
    }

    private class PointerMove(
        val id: Int,
        val fromX: Float,
        val fromY: Float,
        val toX: Float,
        val toY: Float
    )

    private suspend fun performMove(
        duration: Int,
        isJitter: Boolean,
        vararg pointers: PointerMove
    ) {
        GlobalVars.serverActions ?: LogHelper.logAndRestart("Server actions not found at performMove")
        val delayMultiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
            ?: LogHelper.logAndRestart("Failed to get delayMultiplier at performMove")

        // curl/wget transports are slower; reduce step count by 30% to lower command overhead.
        val isSlowTransport = GlobalVars.serverConnectionMethod == ServerConnectionMethod.LIBSU_CURL
                || GlobalVars.serverConnectionMethod == ServerConnectionMethod.LIBSU_WGET

        if (!isJitter) {
            // Non-websocket transports are slower, so a larger step interval reduces command overhead.
            val stepInterval = 10L
            val rawSteps = maxOf(1, (duration / stepInterval).toInt())
            val steps = if (isSlowTransport) maxOf(1, (rawSteps * 0.5f).toInt()) else rawSteps
            for (i in 1..steps) {
                val t = i.toFloat() / steps
                pointers.forEach { p ->
                    val currentX = p.fromX + (p.toX - p.fromX) * t
                    val currentY = p.fromY + (p.toY - p.fromY) * t
                    touchMove(currentX, currentY, p.id, isJitter = false)
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

        // steps = distance / random(10..20)
        val stepDivisor = Random.Default.nextInt(10, 20)
        val rawSteps = maxOf(3, (totalDistance / stepDivisor).toInt())
        val steps = if (isSlowTransport) maxOf(1, (rawSteps * 0.5f).toInt()) else rawSteps
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
                val offsetMag = pDist * Random.Default.nextDouble(0.05, 0.10).toFloat()
                val side = if (Random.Default.nextBoolean()) 1f else -1f
                PointerState(midX + unitPerpX * offsetMag * side, midY + unitPerpY * offsetMag * side)
            } else {
                PointerState(midX, midY)
            }
        }

        var currentTLinear = 0f
        for (i in 1..steps) {
            val idealIncrement = 1f / steps
            // Small drift in time progression progression (t)
            val drift = (Random.Default.nextFloat() - 0.5f) * (idealIncrement * 0.4f)
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
                state.lastNoiseX += (Random.Default.nextFloat() - 0.5f) * 0.4f
                state.lastNoiseY += (Random.Default.nextFloat() - 0.5f) * 0.4f

                touchMove(bX + state.lastNoiseX, bY + state.lastNoiseY, p.id, isJitter = false)
            }

            val varDelay = (avgDelay * Random.Default.nextDouble(0.8, 1.2)).toLong()
            delay((varDelay * delayMultiplier).toLong())
        }
    }

    suspend fun tap(
        x: Int,
        y: Int,
        isJitter: Boolean = true,
        delayTime: Int = 500
    ) {
        waitForPlay()
        val delayMultiplier = getDelayMultiplier()

        // curl/wget transports are slower; reduce post-tap delay to avoid unnecessary wait time.
        val isSlowTransport = GlobalVars.serverConnectionMethod == ServerConnectionMethod.LIBSU_CURL
                || GlobalVars.serverConnectionMethod == ServerConnectionMethod.LIBSU_WGET
        val effectiveDelayTime = if (isSlowTransport) maxOf(0, delayTime - 20) else delayTime

        touchDown(x.toFloat(), y.toFloat(), 1)
        try {
            // Random delay; skipped on slow transports to reduce overhead.
            if (!isSlowTransport) {
                val randomDelay = Random.Default.nextLong(20, 31)
                delay((randomDelay * delayMultiplier).toLong())
            }

            if (isJitter) {
                val offsetX = Random.Default.nextInt(-3, 4)
                val offsetY = Random.Default.nextInt(-3, 4)
                touchMove((x + offsetX).toFloat(), (y + offsetY).toFloat(), 1, isJitter = false)
            }
        } finally {
            withContext(NonCancellable) {
                touchUp(1)
                delayWithMultiplier(effectiveDelayTime)
            }
        }
    }
}