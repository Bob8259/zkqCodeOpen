package com.coc.zkqcode.jar.code.clancapital

import android.graphics.Bitmap
import androidx.core.graphics.get
import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.mainbase.others.zoomSmallMainBase
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.aidetection.DetectionResult
import com.coc.zkqcode.jar.code.universal.aidetection.tiledAiDetect
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.data.StorageKeys
import com.coc.zkqcode.jar.code.universal.smalltools.data.checkMemoryFile
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar
import kotlin.random.Random

suspend fun clanCapitalAttack(): Boolean {
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAN_CAPITAL_ATTACK, InGameVars.currentAccountNumber
    )

    if (!enterClanCapital()) return false
    if (findMultiColors(MyColors.ZeroAttackTimes) != null) {
        // No attack times remaining; record the timestamp so the guard activates next cycle
        writeMemory(storageKey, (System.currentTimeMillis() / 60_000).toString())
        accountMessage("都城攻击次数已用完")
        return false
    }
    if (findMultiColors(MyColors.StartRaid) != null) {
        accountMessage("都城对战未开启")
        return false
    }
    if (findAttackTarget()) {
        val endBattleButton = findMultiColorsUntil(schemas = listOf(MyColors.EndBattle), duration = 40000)
        if (endBattleButton == null) {
            accountMessage("未找到结束战斗按钮")
            killGame()
            delayWithMultiplier(1000)
            runGame()
            if (!enterClanCapital()) return false
        }
        deployClanCapitalTroopsAndSpells()
        killGame()
        delayWithMultiplier(1000)
        runGame()
    } else {
        return false
    }
    return enterMainScreen()
}


suspend fun findAttackTarget(): Boolean {
    val deadline = System.currentTimeMillis() + 10_000L
    while (System.currentTimeMillis() < deadline) {
        if (findMultiColors(MyColors.RaidInformation) != null) {
            break
        }
        accountMessage("进入突袭地图中...")
        val raidMap = findMultiColors(MyColors.RaidMap)
        if (raidMap != null) {
            TouchActions.tap(raidMap.x, raidMap.y)
        }
        delayWithMultiplier(300)
    }
    if (findMultiColors(MyColors.RaidInformation) == null) {
        accountMessage("超时：未找到RaidInformation，退出攻击")
        return false
    }
    TouchActions.swipe(300, 500, 300, 1500, delayAfter = 400)
    // Capture a fresh screenshot to analyse which districts are available to attack.
    val bitmap = ScreenCaptureManager.captureScreenOrRestart<Bitmap>(asBitmap = true) ?: run {
        accountMessage("截图失败，无法分析都城区域")
        return false
    }

    // Iterate districts in order and tap the first one whose white-pixel count is below the
    // threshold, which indicates the district still has open territory to attack.
    for (district in CAPITAL_DISTRICTS) {
        val whiteCount = countBrightPixels(bitmap, district)
        clickRightBottom(1)
        if (whiteCount < WHITE_PIXEL_ATTACK_THRESHOLD) {
            val centerX = (district.x1 + district.x2) / 2
            val centerY = (district.y1 + district.y2) / 2
            // Offset per design: shift left 80px and down 80px to land on the entry button. Y is capped at 719.
            val tapY = minOf(centerY + 80, 719)
            accountMessage(district.name)
            if (district.name == "哥布矿井") {
                TouchActions.tap(840, 715)
            } else {
                TouchActions.tap(centerX - 80, tapY)
            }
            val raidButton = findMultiColorsUntil(schemas = listOf(MyColors.RaidRedAttackButton), duration = 300)
            if (raidButton != null) {
                TouchActions.tap(raidButton.x, raidButton.y)
                TouchActions.tap(1060, 400, delayTime = 1000)//Attack
                TouchActions.tap(770, 460)//Full Medal
                return true
            }
        }
    }

    accountMessage("未找到可攻击的都城区域")
    return false
}

private suspend fun deployClanCapitalTroopsAndSpells() {
    zoomSmallClanCapital()
    delayWithMultiplier(200)
    deploySpells()
    deployTroops()
}

private suspend fun zoomSmallClanCapital() {
    // Start from the same stable zoomed-out view used by other main-base automation flows.
    zoomSmallMainBase()
    // Nudge the camera toward the clan capital battlefield before running detection.
    TouchActions.swipe(900, 250, 500, 320)
}

private suspend fun deployTroops() {
    TouchActions.swipe(1070, 530, 0, 0)
    repeat(15) {
        val troop = findMultiColorsUntil(schemas = listOf(MyColors.TroopColorAtDeploymentBar), duration = 100)
        if (troop != null) {
            TouchActions.tap(troop.x, troop.y)
            // Scan the trapezoidal deployment bar area row by row with a random step size.
            // The convex quad vertices in clockwise order:
            //   A(205,480) → B(360,560) → D(710,250) → C(615,160) → A
            val trapEdges = listOf(
                Pair(Pair(205, 480), Pair(360, 560)), Pair(Pair(360, 560), Pair(710, 250)), Pair(Pair(710, 250), Pair(615, 160)), Pair(Pair(615, 160), Pair(205, 480))
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
                        TouchActions.tap(x, y, delayTime = Random.nextInt(150, 250))
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
        // Use a stable fallback centre point so the existing spell deployment logic still runs.
        accountMessage("部落都城未检测到可攻击目标，使用默认落点")
        listOf(PlannedClanCapitalTarget(x = 660, y = 280))
    } else {
        planClanCapitalTargets(detections)
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

// ---------------------------------------------------------------------------
// District availability detection
// ---------------------------------------------------------------------------

/** Represents a named screen region that maps to one clan capital district. */
internal data class CapitalDistrict(
    val x1: Int, val y1: Int, val x2: Int, val y2: Int, val name: String
)

/**
 * Screen regions (in game-pixel coordinates) whose white-pixel density reveals
 * whether a district still has destructible buildings to attack.
 */
internal val CAPITAL_DISTRICTS = listOf(
    CapitalDistrict(829, 343, 850, 370, "野蛮兵营"),
    CapitalDistrict(662, 420, 682, 444, "法师山谷"),
    CapitalDistrict(954, 515, 972, 537, "飞龙悬崖"),
    CapitalDistrict(520, 555, 536, 577, "气球环礁"),
    CapitalDistrict(773, 582, 791, 606, "建筑工坊"),
    CapitalDistrict(364, 670, 383, 691, "戈仑石场"),
    CapitalDistrict(616, 709, 638, 719, "骷髅公园"),
    CapitalDistrict(894, 706, 921, 719, "哥布矿井"),
    CapitalDistrict(660, 208, 678, 230, "都城之巅"),
)

/** Districts whose bright-pixel count is below this value are considered attackable. */
private const val WHITE_PIXEL_ATTACK_THRESHOLD = 60

/** Luminance value above which a pixel is treated as "white / bright". */
private const val BRIGHTNESS_THRESHOLD = 250.0

/**
 * Counts pixels in [district] whose luminance (standard ITU-R BT.601 formula) exceeds
 * [BRIGHTNESS_THRESHOLD], indicating a near-white colour associated with UI elements
 * that are present when a district still has structures standing.
 */
private fun countBrightPixels(bitmap: Bitmap, district: CapitalDistrict): Int {
    var count = 0
    for (y in district.y1 until district.y2) {
        for (x in district.x1 until district.x2) {
            val pixel = bitmap[x, y]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            val brightness = 0.299 * r + 0.587 * g + 0.114 * b
            if (brightness > BRIGHTNESS_THRESHOLD) count++
        }
    }
    return count
}

/**
 * Execute the Clan Capital raid loop: validate prerequisites, train troops,
 * check cooldown, and attack up to 6 times.
 */
suspend fun performClanCapitalRaid(): Boolean {
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.PLAY_RAID.key) || InGameVars.townHallLevel < 3) return true
    // All clan capital actions are only relevant during raid weekend
    if (!isWithinRaidWeekend()) {
        accountMessage("当前不在都城突袭时间段，跳过都城对战")
        return true
    }
    if (!clanCapitalTrainTroops()) {
        accountMessage("都城练兵失败，尝试返回主世界")
        return enterMainScreen()
    }
    // Guard: skip if the last attack was less than 3 days (4320 minutes) ago
    val storageKey = StorageKeys.withAccountNumber(
        StorageKeys.CLAN_CAPITAL_ATTACK, InGameVars.currentAccountNumber
    )
    if (!checkMemoryFile(storageKey, 4320)) {
        return enterMainScreen()
    }
    for (i in 0 until 6) {
        if (!clanCapitalAttack()) break
    }
    return enterMainScreen()
}

/**
 * Check if the current device time falls within the Clan Capital raid weekend:
 * Friday 16:00 (inclusive) ~ Monday 16:00 (exclusive).
 */
fun isWithinRaidWeekend(): Boolean {
    val cal = Calendar.getInstance()
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    return when (dayOfWeek) {
        Calendar.FRIDAY -> hour >= 16
        Calendar.SATURDAY, Calendar.SUNDAY -> true
        Calendar.MONDAY -> hour < 16
        else -> false
    }
}

