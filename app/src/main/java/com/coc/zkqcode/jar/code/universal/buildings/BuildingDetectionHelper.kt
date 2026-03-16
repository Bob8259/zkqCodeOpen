package com.coc.zkqcode.jar.code.universal.buildings

import com.coc.zkqcode.core.system.screencapture.ScreenCaptureManager
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
 * Result of building detection containing the list of buildings.
 */
data class BuildingDetectionResult(
    val buildings: List<DetectedBuilding>
)

/**
 * List of benchmark buildings
 */
val ALL_BUILDINGS = listOf(
    "城墙",
    "加农炮",
    "箭塔",
    "迫击炮",
    "防空火箭",
    "法师塔",
    "空气炮",
    "特斯拉电磁塔",
    "炸弹塔",
    "十字连弩",
    "地狱之塔",
    "天鹰火炮",
    "投石炮",
    "建筑工人小屋",
    "法术塔",
    "擎天巨柱",
    "跳弹加农炮",
    "多人箭塔",
    "火焰喷射器",
    "复合机械塔",
    "超级法师塔",
    "复仇之塔",
    "隐形炸弹",
    "隐形弹簧",
    "空中炸弹",
    "巨型炸弹",
    "搜空地雷",
    "骷髅陷阱",
    "飓风陷阱",
    "终极炸弹",
    "金矿",
    "圣水收集器",
    "暗黑重油钻井",
    "储金罐",
    "圣水瓶",
    "暗黑重油罐",
    "部落城堡",
    "兵营",
    "训练营",
    "暗黑训练营",
    "实验室",
    "法术工厂",
    "暗黑法术工厂",
    "攻城机器工坊",
    "战宠小屋",
    "铁匠铺",
    "英雄殿堂",
    "巨型特斯拉电磁塔",
    "巨型地狱之塔",
    "地狱火炮",
    "建筑大师大本营",
    "大本营",
    "奥仔哨站",
    "双管加农炮",
    "防空火炮",
    "撼地巨石",
    "守卫岗哨",
    "空中炸弹发射器",
    "多管迫击炮",
    "熔岩火炮",
    "巨型加农炮",
    "超级特斯拉电磁塔",
    "熔岩发射器",
    "弹射陷阱",
    "地雷",
    "巨型地雷",
    "宝石矿井",
    "建筑大师训练营",
    "星空实验室",
    "预备营",
    "治疗小屋",
    "时光钟楼",
    "战争机器",
    "战斗直升机",
    "大守护者",
    "弓箭女皇",
    "野蛮人之王",
    "飞盾战神",
    "亡灵王子",
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
fun cleanBuildingName(raw: String): String {
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
        "暗果重油罐" to "暗黑重油罐",
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
        "據地" to "撼地",
        "英雄剧穀堂" to "英雄殿堂",
        "暗黑油檯" to "暗黑重油罐",
        "建设升级" to "建议升级",
        "建议升級" to "建议升级",
        "时光钟控类" to "时光钟楼",
        ":" to "",
        "：" to ""
    )

    // Apply all string replacements
    corrections.forEach { (error, correction) ->
        name = name.replace(error, correction)
    }
    // Remove all text starting from 'x' or 'X' (e.g. "建筑xgas6" -> "建筑")
    name = name.replace(Regex("[xX].*"), "")

    // If the name starts with "新", keep it as is to skip fuzzy matching
    if (name.startsWith("新")) {
        return name
    }

    // C. Fuzzy matching logic
    if (name.length >= 3) {
        var bestMatch: String? = null
        var minDistance = Int.MAX_VALUE

        for (building in ALL_BUILDINGS) {
            if (abs(name.length - building.length) > 1) continue

            val distance = levenshteinDistance(name, building)
            if (distance < minDistance) {
                minDistance = distance
                bestMatch = building
            }
        }

        bestMatch?.let {
            val maxLength = max(name.length, it.length)
            val maxAllowedDistance = (maxLength * 2.0 / 3.0).toInt()

            if (minDistance <= maxAllowedDistance) {
                return it
            }
        }
    }

    return name
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
