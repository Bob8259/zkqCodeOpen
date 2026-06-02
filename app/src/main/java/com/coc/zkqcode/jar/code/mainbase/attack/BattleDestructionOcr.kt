package com.coc.zkqcode.jar.code.mainbase.attack

import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.recognizer.cleanOcrDigits
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
}

/**
 * Reads the on-screen destruction percentage via OCR in [MyColors.BattleDestructionPercentOcrRegion].
 * Returns null when digits cannot be parsed.
 */
suspend fun readBattleDestructionPercent(): Int? {
    val region = MyColors.BattleDestructionPercentOcrRegion
    val results = TextRecognizer.recognize(
        startX = region.x1,
        startY = region.y1,
        endX = region.x2,
        endY = region.y2,
        useChinese = false,
        applyPreprocess = true,
        threshold = 200,
        invertBinarization = true,
    )
    val combined = results.joinToString("") { it.text }
    val digitsOnly = cleanOcrDigits(combined).filter { it.isDigit() }
    if (digitsOnly.isEmpty()) {
        showDebugInfo("BattleDestructionOcr: no digits in OCR text='$combined'")
        return null
    }
    val percent = digitsOnly.toIntOrNull()?.coerceIn(0, 100)
    showDebugInfo("BattleDestructionOcr: parsed destruction %=$percent from '$combined'")
    return percent
}

/**
 * Evaluates OCR destruction-% exit rules once per poll tick.
 * Skipped entirely when [MainBaseSettings.OCR_EXIT_ENABLED] is off.
 */
suspend fun checkBattleDestructionOcrExit(state: BattleDestructionOcrState): BattleDestructionOcrExitResult {
    if (!getBooleanConfigRuntime(MainBaseSettings.OCR_EXIT_ENABLED.key)) {
        return BattleDestructionOcrExitResult.Continue
    }

    val targetPercent = getConfigRuntime(MainBaseSettings.OCR_EXIT_TARGET_PERCENT.key)
        .toIntOrNull()?.coerceIn(0, 100) ?: 0
    val staleSeconds = getConfigRuntime(MainBaseSettings.OCR_EXIT_STALE_SECONDS.key)
        .toIntOrNull()?.coerceAtLeast(1) ?: DEFAULT_STALE_SECONDS
    val staleMs = staleSeconds * 1000L

    val percent = readBattleDestructionPercent()
    val now = System.currentTimeMillis()
    accountMessage("当前摧毁率：$percent")
    if (percent == null) {
        val failStart = state.ocrFailStartMs
        if (failStart == null) {
            state.ocrFailStartMs = now
        } else if (now - failStart >= OCR_FAIL_SAFE_MS) {
            return BattleDestructionOcrExitResult.ExitOcrFailed
        }
        return BattleDestructionOcrExitResult.Continue
    }

    state.ocrFailStartMs = null

    if (targetPercent in 1..percent) {
        return BattleDestructionOcrExitResult.ExitTargetMet
    }

    val previous = state.lastPercent
    if (previous != null && percent == previous) {
        if (now - state.lastChangeTimeMs >= staleMs) {
            return BattleDestructionOcrExitResult.ExitStale
        }
    } else {
        state.lastPercent = percent
        state.lastChangeTimeMs = now
    }

    return BattleDestructionOcrExitResult.Continue
}
