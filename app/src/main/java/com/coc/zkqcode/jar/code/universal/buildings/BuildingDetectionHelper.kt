package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.schema.details.BuilderBaseBuildings
import com.coc.zkqcode.jar.ui.schema.details.MainBaseBuildings
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Regex to detect Chinese characters
 */
internal val chineseRegex = Regex("[\u4e00-\u9fff]")

/**
 * Characters commonly misread by OCR that should be replaced with "新"
 */
internal val ocrMisreadPrefixes = listOf("斬", "靳", "斩", "鼾")

/**
 * Data class representing a detected building with its name and position.
 */
data class DetectedBuilding(val name: String, val x: Int, val y: Int)

/**
 * Result of building detection.
 *
 * @param buildings Resource-filtered list of detected buildings (only affordable ones),
 *                  used for upgrade decisions.
 * @param allDetectedBuildings Pre-resource-filter list of all detected buildings regardless
 *                             of affordability, used for scroll-exit checking so that
 *                             unaffordable-only screens are not treated as an empty (stopped) list.
 */
data class BuildingDetectionResult(
    val buildings: List<DetectedBuilding>,
    val allDetectedBuildings: List<DetectedBuilding> = emptyList()
)

/**
 * Returns base-specific benchmark buildings for OCR matching.
 * "城墙" is the only extra entry needed for detection that has no upgrade schema entry.
 */
fun getBuildingNames(baseType: BaseType): List<String> = when (baseType) {
    BaseType.Main -> MainBaseBuildings.all.map { it.displayName }
    BaseType.Builder -> BuilderBaseBuildings.all.map { it.displayName }
} + listOf("城墙")

val ALL_BUILDINGS = getBuildingNames(BaseType.Main)

/**
 * OCR labels that help locate sections in the upgrade list but are not buildings.
 */
private val SPECIAL_OCR_LABELS = listOf(
    "建议升级"
)

/**
 * Counts pixels of a target color within a specified rectangle in a capture result.
 */
fun countPixelsInArea(
    result: ScreenCaptureManager.CaptureResult, x1: Int, y1: Int, x2: Int, y2: Int, targetColor: Int
): Int {
    val buf = result.buffer
    val width = result.width
    val height = result.height
    val pixelStride = result.pixelStride
    val rowStride = result.rowStride

    val left = max(0, min(x1, x2))
    val right = min(width - 1, max(x1, x2))
    val top = max(0, min(y1, y2))
    val bottom = min(height - 1, max(y1, y2))

    val targetR = (targetColor shr 16) and 0xFF
    val targetG = (targetColor shr 8) and 0xFF
    val targetB = targetColor and 0xFF

    var count = 0
    for (y in top..bottom) {
        val rowStart = y * rowStride
        for (x in left..right) {
            val offset = rowStart + x * pixelStride
            if (offset + 2 >= buf.capacity()) continue

            // RGBA_8888 format
            val r = buf.get(offset).toInt() and 0xFF
            val g = buf.get(offset + 1).toInt() and 0xFF
            val b = buf.get(offset + 2).toInt() and 0xFF

            if (r == targetR && g == targetG && b == targetB) {
                count++
            }
        }
    }
    return count
}

/**
 * Counts green pixels within a specified rectangle in a capture result.
 * Green color range: R in [10, 14], G in [245, 255], B in [10, 14]
 */
fun countGreenPixelsInArea(
    result: ScreenCaptureManager.CaptureResult, x1: Int, y1: Int, x2: Int, y2: Int
): Int {
    val buf = result.buffer
    val width = result.width
    val height = result.height
    val pixelStride = result.pixelStride
    val rowStride = result.rowStride

    val left = max(0, min(x1, x2))
    val right = min(width - 1, max(x1, x2))
    val top = max(0, min(y1, y2))
    val bottom = min(height - 1, max(y1, y2))

    var count = 0
    for (y in top..bottom) {
        val rowStart = y * rowStride
        for (x in left..right) {
            val offset = rowStart + x * pixelStride
            if (offset + 2 >= buf.capacity()) continue

            // RGBA_8888 format
            val r = buf.get(offset).toInt() and 0xFF
            val g = buf.get(offset + 1).toInt() and 0xFF
            val b = buf.get(offset + 2).toInt() and 0xFF

            // Check if pixel is green: R in [10, 14], G in [245, 255], B in [10, 14]
            if (r in 10..14 && g in 245..255 && b in 10..14) {
                count++
            }
        }
    }
    return count
}

/**
 * Cleans a raw building name:
 * - Removes all spaces
 * - Replaces OCR-misread prefixes ("斬", "靳", "鼾") with "新"
 * - Replaces OCR-misread "减墙" with "城墙"
 * - Strips trailing "x" / "X" followed by digits (e.g. "储金罐x2" → "储金罐")
 */
fun cleanBuildingName(raw: String, candidateBuildings: List<String> = ALL_BUILDINGS): String {
    // Remove whitespace and pipes immediately
    var name = raw.replace(" ", "").replace("|", "")

    // Handle OCR misread prefixes
    for (char in ocrMisreadPrefixes) {
        if (name.startsWith(char)) {
            name = "新" + name.removePrefix(char)
            break
        }
    }

    // Mapping of common OCR errors to correct building names
    val corrections = mapOf(
        "减墙" to "城墙",
        "部落械堡" to "部落城堡",
        "部落城堡加" to "部落城堡",
        "大宁护者" to "大守护者",
        "引箭女皇" to "弓箭女皇",
        "天厲火炬" to "天鹰火炮",
        "天腰火炮" to "天鹰火炮",
        "天鹿火炮" to "天鹰火炮",
        "陷供" to "陷阱",
        "陷件" to "陷阱",
        "炸弹培" to "炸弹塔",
        "十字连學" to "十字连弩",
        "十字達弩" to "十字连弩",
        "十字達弩" to "十字连弩",
        "暗果" to "暗黑",
        "女箭塔" to "箭塔",
        "铁匠镇" to "铁匠铺",
        "宝石矿共" to "宝石矿井",
        "室中炸弹发射器" to "空中炸弹发射器",
        "室中炸弹发射" to "空中炸弹发射器",
        "室气炮" to "空气炮",
        "战争机" to "战争机器",
        "战争机器器" to "战争机器",
        "據地巨石" to "撼地巨石",
        "想地巨石" to "撼地巨石",
        "远装者" to "远袭者",
        "红迫炮" to "迫击炮",
        "建议升级:" to "建议升级",
        "建议升级：" to "建议升级",
        "建议升级及:" to "建议升级",
        "乒营" to "兵营",
        "箭增" to "箭塔",
        "節塔" to "箭塔",
        "據地" to "撼地",
        "英雄剧穀堂" to "英雄殿堂",
        "暗黑油檯" to "暗黑重油罐",
        "建设升级" to "建议升级",
        "建议升級" to "建议升级",
        "时光钟控类" to "时光钟楼",
        ":" to "",
        "：" to "",
        "十字连違弩" to "十字连弩",
        "加衣炮" to "加农炮"
    )

    // Apply all string replacements
    corrections.forEach { (error, correction) ->
        name = name.replace(error, correction)
    }
    // Remove all text starting from 'x' or 'X' (e.g. "建筑xgas6" -> "建筑")
    name = name.replace(Regex("[xX].*"), "")

    val prefix = when {
        name.startsWith("新") -> "新"
        name.startsWith("改装") -> "改装"
        else -> ""
    }
    if (prefix.isNotEmpty()) {
        name = name.removePrefix(prefix)
    }

    // Filter to keep only Chinese characters before fuzzy matching to reduce false positives
    val chineseOnly = name.filter { it in '\u4e00'..'\u9fff' }

    // If no Chinese characters remain after filtering, return the original name
    if (chineseOnly.isEmpty()) {
        return name
    }

    // Check special OCR labels before buildings so section markers are normalized early.
    if (prefix.isEmpty()) {
        findBestFuzzyMatch(chineseOnly, SPECIAL_OCR_LABELS)?.let { return it }
    }

    // Match building names last to avoid treating section labels as real buildings.
    findBestFuzzyMatch(chineseOnly, candidateBuildings)?.let { return prefix + it }

    return prefix + name
}

/**
 * Finds the closest fuzzy match from the provided OCR target list.
 */
private fun findBestFuzzyMatch(name: String, candidates: List<String>): String? {
    if (name.length < 3) return null

    var bestMatch: String? = null
    var minDistance = Int.MAX_VALUE

    for (candidate in candidates) {
        if (abs(name.length - candidate.length) > 1) continue

        val distance = levenshteinDistance(name, candidate)
        if (distance < minDistance) {
            minDistance = distance
            bestMatch = candidate
        }
    }

    return bestMatch?.takeIf { candidate ->
        val maxLength = max(name.length, candidate.length)
        val maxAllowedDistance = (maxLength * 2.0 / 3.0).toInt()

        // "英雄殿堂" and "建议升级" require a stricter match because OCR often confuses them with other labels.
        val effectiveMaxDistance = if (candidate == "英雄殿堂" || candidate == "建议升级") 1 else maxAllowedDistance
        minDistance <= effectiveMaxDistance
    }
}

/**
 * Calculates the edit distance (Levenshtein Distance) between two strings
 */
fun levenshteinDistance(s1: String, s2: String): Int {
    val dp = IntArray(s2.length + 1) { it }
    for (i in 1..s1.length) {
        var prev = dp[0]
        dp[0] = i
        for (j in 1..s2.length) {
            val temp = dp[j]
            if (s1[i - 1] == s2[j - 1]) {
                dp[j] = prev
            } else {
                dp[j] = min(min(dp[j - 1], dp[j]), prev) + 1
            }
            prev = temp
        }
    }
    return dp[s2.length]
}

suspend fun tapGoldBar(times: Int = 1) {
    repeat(times) {
        TouchActions.tap(1235, 35, delayTime = 50)
    }
}