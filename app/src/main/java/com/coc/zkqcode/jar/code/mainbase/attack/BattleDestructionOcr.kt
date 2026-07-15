package com.coc.zkqcode.jar.code.mainbase.attack

import android.graphics.Bitmap
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.recognizer.Resources
import com.coc.zkqcode.jar.code.universal.recognizer.TextRecognizer
import com.coc.zkqcode.jar.code.universal.recognizer.recognizeResourcesOrNull
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

    data object ExitResourceStale : BattleDestructionOcrExitResult()

    /** Destruction % and opponent resources are both unchanged for the configured interval. */
    data object ExitCombinedStale : BattleDestructionOcrExitResult()

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

class BattleResourceOcrState {
    var lastResources: Resources? = null
    var lastChangeTimeMs: Long = System.currentTimeMillis()
    var ocrFailStartMs: Long? = null
}

data class BattleDetectionStatus(
    val percent: Int?,
    val percentRemainingMs: Long,
    val resources: Resources?,
    val resourceRemainingMs: Long?,
    val percentFailureElapsedMs: Long? = null,
    val resourceFailureElapsedMs: Long? = null
)


/**
 * Reads the on-screen destruction percentage via OCR in [MyColors.BattleDestructionPercentOcrRegion].
 * Returns null when digits cannot be parsed.
 */
suspend fun readBattleDestructionPercent(maxPercent: Int = 100, bitmap: Bitmap? = null): Int? {
    val region = MyColors.BattleDestructionPercentOcrRegion
    val preProcess = true // 与己方资源识别一致，都启用预处理
    val results = TextRecognizer.recognize(
        region.x1,
        region.y1,
        region.x2,
        region.y2,
        useChinese = false,
        applyPreprocess = preProcess,
        threshold = 240,
        screenBuffer = bitmap
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
    resourceState: BattleResourceOcrState? = null,
    enabledKey: String = MainBaseSettings.OCR_EXIT_ENABLED.key,
    targetPercentKey: String = MainBaseSettings.OCR_EXIT_TARGET_PERCENT.key,
    staleSecondsKey: String = MainBaseSettings.OCR_EXIT_STALE_SECONDS.key,
    modeKey: String = MainBaseSettings.OCR_EXIT_MODE.key,
    maxPercent: Int = 100,
    bitmap: Bitmap? = null,
    onStatusUpdate: ((BattleDetectionStatus) -> Unit)? = null
): Pair<Int?, BattleDestructionOcrExitResult> {
    if (!getBooleanConfigRuntime(enabledKey)) {
        return Pair(null, BattleDestructionOcrExitResult.Continue)
    }

    val targetPercent = getConfigRuntime(targetPercentKey)
        .toIntOrNull()?.coerceIn(0, maxPercent) ?: 0
    val staleSeconds = getConfigRuntime(staleSecondsKey)
        .toIntOrNull()?.coerceAtLeast(1) ?: DEFAULT_STALE_SECONDS
    val staleMs = staleSeconds * 1000L
    val mode = getConfigRuntime(modeKey).toIntOrNull()?.coerceIn(0, 2) ?: 0
    val needsPercent = mode == 0 || mode == 2 || targetPercent > 0
    val needsResources = targetPercent == 0 && (mode == 1 || mode == 2)

    val now = System.currentTimeMillis()

    val percent = if (needsPercent) readBattleDestructionPercent(maxPercent, bitmap) else null
    val resources = if (needsResources && resourceState != null) {
        recognizeResourcesOrNull(isOpponent = true, bitmap = bitmap)
    } else {
        null
    }

    if (resources == null && needsResources && resourceState != null) {
        if (resourceState.ocrFailStartMs == null) {
            resourceState.ocrFailStartMs = now
        }
    } else if (resources != null && resourceState != null) {
        resourceState.ocrFailStartMs = null
        val previousResources = resourceState.lastResources
        resourceState.lastResources = resources
        if (previousResources == null || previousResources != resources) {
            resourceState.lastChangeTimeMs = now
        }
    }

    if (needsPercent && percent == null) {
        val failStart = state.ocrFailStartMs
        if (failStart == null) {
            state.ocrFailStartMs = now
        } else {
            if (now - failStart >= OCR_FAIL_SAFE_MS) {
                accountMessage("摧毁率识别连续失败超30秒，退出")
                return Pair(null, BattleDestructionOcrExitResult.ExitOcrFailed)
            }
        }
        val failureStart = state.ocrFailStartMs ?: now
        onStatusUpdate?.invoke(
            BattleDetectionStatus(
                percent = null,
                percentRemainingMs = 0,
                resources = if (resources != null) resourceState?.lastResources else null,
                resourceRemainingMs = if (resources != null) {
                    resourceState?.let { (staleMs - (now - it.lastChangeTimeMs)).coerceAtLeast(0) }
                } else null,
                percentFailureElapsedMs = now - failureStart,
                resourceFailureElapsedMs = resourceState?.ocrFailStartMs?.let { now - it }
            )
        )
        return Pair(null, BattleDestructionOcrExitResult.Continue)
    }

    if (needsPercent) state.ocrFailStartMs = null

    if (needsPercent && percent != null && targetPercent in 1..percent) {
        accountMessage("摧毁率达标(${percent}% >= ${targetPercent}%)")
        return Pair(percent, BattleDestructionOcrExitResult.ExitTargetMet)
    }

    val previous = state.lastPercent
    val percentStale = if (needsPercent && percent != null && previous != null && percent == previous) {
        now - state.lastChangeTimeMs >= staleMs
    } else {
        false
    }
    if (needsPercent && percent != null && previous != null && percent == previous) {
    } else {
        if (needsPercent && percent != null) {
            state.lastPercent = percent
            state.lastChangeTimeMs = now
        }
    }

    if (needsResources && resourceState != null) {
        val resourcesStale = resourceState.lastResources != null &&
                now - resourceState.lastChangeTimeMs >= staleMs
        if (mode == 1 && resourcesStale) {
            accountMessage("对手资源已 ${staleSeconds}s 未变化，退出对战")
            return Pair(percent, BattleDestructionOcrExitResult.ExitResourceStale)
        }
        if (mode == 2 && percentStale && resourcesStale) {
            accountMessage("摧毁率与对手资源均已 ${staleSeconds}s 未变化，退出对战")
            return Pair(percent, BattleDestructionOcrExitResult.ExitCombinedStale)
        }
        onStatusUpdate?.invoke(
            BattleDetectionStatus(
                percent,
                if (needsPercent) (staleMs - (now - state.lastChangeTimeMs)).coerceAtLeast(0) else 0,
                if (resources != null) resourceState.lastResources else null,
                if (resources != null) {
                    (staleMs - (now - resourceState.lastChangeTimeMs)).coerceAtLeast(0)
                } else null,
                resourceFailureElapsedMs = resourceState.ocrFailStartMs?.let { now - it }
            )
        )
    } else if (needsPercent) {
        if (mode == 0 && percentStale) {
            accountMessage("摧毁率已 ${staleSeconds}s 未变化，退出对战")
            return Pair(percent, BattleDestructionOcrExitResult.ExitStale)
        }
        onStatusUpdate?.invoke(
            BattleDetectionStatus(
                percent,
                (staleMs - (now - state.lastChangeTimeMs)).coerceAtLeast(0),
                null,
                null
            )
        )
    }

    return Pair(percent, BattleDestructionOcrExitResult.Continue)
}
