package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.smalltools.data.getBooleanConfigRuntime
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.ui.schema.details.MainBaseSettings

/** Force exit after this many milliseconds of consecutive OCR read failures. */
private const val OCR_FAIL_SAFE_MS = 30_000L

/** Default stale seconds when config is missing or invalid. */
private const val DEFAULT_STALE_SECONDS = 20

/**
 * Result of one destruction-% OCR check during the post-deploy battle poll.
 */
sealed class BattleDestructionOcrExitResult {
    data object Continue : BattleDestructionOcrExitResult()

    /** Destruction % reached the configured target. */
    data object ExitTargetMet : BattleDestructionOcrExitResult()

    /** Destruction % unchanged for the configured stale interval. */
    data object ExitStale : BattleDestructionOcrExitResult()

    /** OCR failed continuously for [OCR_FAIL_SAFE_MS]. */
    data object ExitOcrFailed : BattleDestructionOcrExitResult()
}

/**
 * Mutable state for stale-% and OCR-failure tracking across poll iterations.
 */
class BattleDestructionOcrState {
    var lastPercent: Int? = null
    var lastChangeTimeMs: Long = System.currentTimeMillis()
    var ocrFailStartMs: Long? = null

    /** Reset all fields for a fresh detection cycle (e.g., when transitioning to zone 2 in builder base). */
    fun reset() {
        lastPercent = null
        lastChangeTimeMs = System.currentTimeMillis()
        ocrFailStartMs = null
    }
}

/**
 * Reads the on-screen destruction percentage via OCR in [MyColors.BattleDestructionPercentOcrRegion].
 * Returns null when digits cannot be parsed.
 */
suspend fun readBattleDestructionPercent(maxPercent: Int = 100): Int? {
    val region = MyColors.BattleDestructionPercentOcrRegion
    val preProcess = true // 与己方资源识别一致，都启用预处理
    val results = TextRecognizer.recognize(
        region.x1,
        region.y1,
        region.x2,
        region.y2,
        useChinese = false,
        applyPreprocess = preProcess,
        threshold = 240
    )
    val combined = results.joinToString("") { it.text }
    val cleanValue = extractValue(combined)
    showDebugInfo("BattleDestructionOcr: parsed destruction %=$cleanValue from '$combined'")
    // Only return null if we got 0 and there were no digits in the original text
    val hasDigits = combined.any { it.isDigit() }
    return if (cleanValue == 0 && !hasDigits) {
        null
    } else {
        cleanValue.coerceIn(0, maxPercent)
    }
}

/**
 * Cleans the recognized text and extracts an Int value.
 * Removes spaces, commas, and any non-numeric characters.
 */
internal fun extractValue(text: String): Int {
    // Remove all non-digit characters and handle common misrecognitions
    val digitsOnly = text.replace("G", "6")
        .replace("o", "0").replace("O", "0")
        .replace("s", "5").replace("S", "5")
        .replace("z", "2").replace("Z", "2")
        .replace("I", "1").replace("l", "1")
        .replace(Regex("[^0-9]"), "")
    return digitsOnly.toIntOrNull() ?: 0
}

/**
 * Evaluates OCR destruction-% exit rules once per poll tick.
 * Skipped entirely when [enabledKey] is off.
 * Returns the destruction percent (or null) and exit result.
 *
 * @param enabledKey config key for the OCR exit enabled flag
 * @param targetPercentKey config key for the target destruction percentage
 * @param staleSecondsKey config key for the stale timeout in seconds
 */
suspend fun checkBattleDestructionOcrExit(
    state: BattleDestructionOcrState,
    enabledKey: String = MainBaseSettings.OCR_EXIT_ENABLED.key,
    targetPercentKey: String = MainBaseSettings.OCR_EXIT_TARGET_PERCENT.key,
    staleSecondsKey: String = MainBaseSettings.OCR_EXIT_STALE_SECONDS.key,
    maxPercent: Int = 100
): Pair<Int?, BattleDestructionOcrExitResult> {
    if (!getBooleanConfigRuntime(enabledKey)) {
        return Pair(null, BattleDestructionOcrExitResult.Continue)
    }

    val targetPercent = getConfigRuntime(targetPercentKey)
        .toIntOrNull()?.coerceIn(0, maxPercent) ?: 0
    val staleSeconds = getConfigRuntime(staleSecondsKey)
        .toIntOrNull()?.coerceAtLeast(1) ?: DEFAULT_STALE_SECONDS
    val staleMs = staleSeconds * 1000L

    val percent = readBattleDestructionPercent(maxPercent)
    val now = System.currentTimeMillis()
    
    if (percent == null) {
        val failStart = state.ocrFailStartMs
        if (failStart == null) {
            accountMessage("摧毁率 OCR 识别失败，开始计时")
            state.ocrFailStartMs = now
        } else {
            val failElapsed = (now - failStart) / 1000
            accountMessage("摧毁率识别失败 (已持续${failElapsed}s)")
            if (now - failStart >= OCR_FAIL_SAFE_MS) {
                accountMessage("摧毁率识别连续失败超30秒，退出")
                return Pair(null, BattleDestructionOcrExitResult.ExitOcrFailed)
            }
        }
        return Pair(null, BattleDestructionOcrExitResult.Continue)
    }

    state.ocrFailStartMs = null

    accountMessage("摧毁率: ${percent}% (目标: ${targetPercent}%)")

    if (targetPercent in 1..percent) {
        accountMessage("摧毁率达标(${percent}% >= ${targetPercent}%)")
        return Pair(percent, BattleDestructionOcrExitResult.ExitTargetMet)
    }

    val previous = state.lastPercent
    if (previous != null && percent == previous) {
        val elapsed = (now - state.lastChangeTimeMs) / 1000
        if (now - state.lastChangeTimeMs >= staleMs) {
            accountMessage("摧毁率 ${elapsed}s 未变化，已达退出阈值 ${staleSeconds}s")
            return Pair(percent, BattleDestructionOcrExitResult.ExitStale)
        }
        if (elapsed % 5 == 0L) {
            accountMessage("摧毁率 ${elapsed}s 未变化 (阈值: ${staleSeconds}s)")
        }
    } else {
        if (previous != null) {
            accountMessage("摧毁率变化: ${previous}% → ${percent}%")
        }
        state.lastPercent = percent
        state.lastChangeTimeMs = now
    }

    return Pair(percent, BattleDestructionOcrExitResult.Continue)
}
