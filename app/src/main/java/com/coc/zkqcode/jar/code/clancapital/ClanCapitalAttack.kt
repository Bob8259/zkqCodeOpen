package com.coc.zkqcode.jar.code.clancapital

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.touchactions.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.aidetection.DetectionResult
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.runGame
import kotlin.random.Random

suspend fun clanCapitalAttack() {
    zoomSmallClanCapital()
    delayWithMultiplier(200)
    deploySpells()
    deployTroops()
    killGame()
    delayWithMultiplier(1000)
    runGame()
}


private suspend fun zoomSmallClanCapital() {
    // Start from the same stable zoomed-out view used by other main-base automation flows.
    zoomSmallMainBase()
    // Nudge the camera toward the clan capital battlefield before running AI detection.
    TouchActions.swipe(900, 250, 500, 320)
}

private suspend fun deployTroops() {
    TouchActions.swipe(1070, 530, 0, 0)
    repeat(8) {
        val troop = findMultiColorsUntil(schemas = listOf(MyColors.TroopColorAtDeploymentBar), duration = 100)
        if (troop != null) {
            TouchActions.tap(troop.x, troop.y)
            // Scan the trapezoidal deployment bar area row by row with a random step size.
            // The convex quad vertices in clockwise order:
            //   A(205,480) → B(280,560) → D(710,250) → C(615,160) → A
            val trapEdges = listOf(
                Pair(Pair(205, 480), Pair(280, 560)),
                Pair(Pair(280, 560), Pair(710, 250)),
                Pair(Pair(710, 250), Pair(615, 160)),
                Pair(Pair(615, 160), Pair(205, 480))
            )
            val step = Random.nextInt(40, 81)
            var y = 160
            while (y <= 560) {
                // Find x-range of the quad at this scanline y via edge intersections
                val xIntersections = mutableListOf<Float>()
                for ((p1, p2) in trapEdges) {
                    val y1 = p1.second
                    val y2 = p2.second
                    if (y1 == y2) continue
                    if (y < minOf(y1, y2) || y > maxOf(y1, y2)) continue
                    val x = p1.first + (p2.first - p1.first).toFloat() * (y - y1) / (y2 - y1)
                    xIntersections.add(x)
                }
                if (xIntersections.size >= 2) {
                    var x = xIntersections.min().toInt()
                    val xMax = xIntersections.max().toInt()
                    while (x <= xMax) {
                        TouchActions.tap(x, y, delayTime = 100)
                        x += step
                    }
                }
                y += step
            }
        }
    }
}

private suspend fun deploySpells() {
    val detections = tiledAiDetect(
        modelName = "capital-building-detect", callerTag = "ClanCapitalAttack"
    )

    val plannedTargets = if (detections.isEmpty()) {
        // Use a stable fallback center point so the existing spell deployment logic still runs.
        ShowMessage("部落都城未检测到可攻击目标，使用默认落点")
        listOf(PlannedClanCapitalTarget(x = 660, y = 280))
    } else {
        planClanCapitalTargets(detections)
    }

    if (plannedTargets.isEmpty()) {
        ShowMessage("部落都城检测结果已去重后为空")
        return
    }

    for (target in plannedTargets) {
        repeat(5) {
            val spell = findMultiColorsUntil(schemas = listOf(MyColors.SpellColorAtDeploymentBar), duration = 100) ?: return
            TouchActions.tap(spell.x, spell.y)
            repeat(3) {
                val randomOffsetX = Random.nextInt(-30, 31)
                val randomOffsetY = Random.nextInt(-30, 31)
                val randomX = (target.x + randomOffsetX).coerceIn(100, 1100)
                val randomY = (target.y + randomOffsetY).coerceIn(0, 570)
                TouchActions.tap(randomX, randomY)
            }
        }
    }
}

private data class PlannedClanCapitalTarget(
    val x: Int, val y: Int
)

private fun planClanCapitalTargets(detections: List<DetectionResult>): List<PlannedClanCapitalTarget> {
    val orderedDetections = detections.sortedWith(compareBy<DetectionResult> { classPriority(it.classIndex) }.thenByDescending { it.score })

    val plannedTargets = mutableListOf<PlannedClanCapitalTarget>()
    for (detection in orderedDetections) {
        val centerX = detection.boundingBox.centerX().toInt()
        val centerY = detection.boundingBox.centerY().toInt()
        val candidate = PlannedClanCapitalTarget(
            x = centerX, y = centerY
        )

        // Overlapping boxes often come from the tiled detector, so keep only the strongest point per cluster.
        if (plannedTargets.none { isTooClose(it, candidate) }) {
            plannedTargets.add(candidate)
        }
    }
    return plannedTargets
}

private fun classPriority(classIndex: Int): Int {
    return when (classIndex) {
        1 -> 0
        0 -> 1
        else -> 2
    }
}

private fun isTooClose(
    first: PlannedClanCapitalTarget, second: PlannedClanCapitalTarget
): Boolean {
    val dx = (first.x - second.x).toFloat()
    val dy = (first.y - second.y).toFloat()
    return dx * dx + dy * dy <= TAPPED_TARGET_MIN_DISTANCE * TAPPED_TARGET_MIN_DISTANCE
}

private const val TAPPED_TARGET_MIN_DISTANCE = 56f
