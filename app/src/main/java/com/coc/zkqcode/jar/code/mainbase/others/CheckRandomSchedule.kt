package com.coc.zkqcode.jar.code.mainbase.others

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.buildings.WorkerAndResearch
import com.coc.zkqcode.jar.code.universal.colors.findMultiColors
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigOrStop
import com.coc.zkqcode.jar.ui.schema.Schema
import java.util.Calendar

// X-coordinate thresholds for detecting full storage bars (same as SearchOpponents)
private const val GOLD_FULL_X_THRESHOLD = 1017
private const val ELIXIR_FULL_X_THRESHOLD = 1017
private const val DARK_ELIXIR_FULL_X_THRESHOLD = 1080

// No-worker and full-resources config keys indexed by time group (0=dawn, 1=morning, 2=afternoon, 3=night)
private val NO_WORKER_KEYS = listOf(
    Schema.GLOBAL_SETTINGS.RS_NO_WORKER_DAWN.key,
    Schema.GLOBAL_SETTINGS.RS_NO_WORKER_MORNING.key,
    Schema.GLOBAL_SETTINGS.RS_NO_WORKER_AFTERNOON.key,
    Schema.GLOBAL_SETTINGS.RS_NO_WORKER_NIGHT.key
)

private val FULL_RES_KEYS = listOf(
    Schema.GLOBAL_SETTINGS.RS_FULL_RES_DAWN.key,
    Schema.GLOBAL_SETTINGS.RS_FULL_RES_MORNING.key,
    Schema.GLOBAL_SETTINGS.RS_FULL_RES_AFTERNOON.key,
    Schema.GLOBAL_SETTINGS.RS_FULL_RES_NIGHT.key
)

/**
 * Called inside playMainBase() right after enterMainBase().
 * When random scheduling is active, checks worker availability and resource fullness
 * for the current time group. Sets [InGameVars.isEarlyOffline] and returns false
 * to short-circuit playMainBase when an early-offline condition is met.
 */
suspend fun checkRandomSchedule(): Boolean {
    // Only act when random scheduling mode is enabled
    val isRandomScheduling = getConfigOrStop(Schema.GLOBAL_SETTINGS.RANDOM_SCHEDULING.key) == "1"
    if (!isRandomScheduling) return true

    val groupIndex = getCurrentTimeGroupIndex()

    // Check no-worker condition
    val noWorkerEnabled = GlobalVars.configStates[NO_WORKER_KEYS[groupIndex]]?.value == "1"
    if (noWorkerEnabled) {
        val workerInfo = WorkerAndResearch.detectWorkerNumber(BaseType.Main)
        if (workerInfo.available == 0) {
            accountMessage("主世界无空闲工人，提前下线")
            InGameVars.isEarlyOffline = true
            return false
        }
    }

    // Check full-resources condition
    val fullResEnabled = GlobalVars.configStates[FULL_RES_KEYS[groupIndex]]?.value == "1"
    if (fullResEnabled) {
        val goldFull = findMultiColors(schema = MyColors.GoldColor)?.let { it.x < GOLD_FULL_X_THRESHOLD } ?: false
        val elixirFull = findMultiColors(schema = MyColors.ElixirColor)?.let { it.x < ELIXIR_FULL_X_THRESHOLD } ?: false

        // Dark elixir may not be unlocked yet
        val hasDarkElixir = findMultiColors(schema = MyColors.DarkElixirIcon) != null
        val darkElixirFull = if (hasDarkElixir) {
            findMultiColors(schema = MyColors.DarkElixirColor)?.let { it.x < DARK_ELIXIR_FULL_X_THRESHOLD } ?: false
        } else {
            true // Treat as "full" when not unlocked so it doesn't block the all-full check
        }

        if (goldFull && elixirFull && darkElixirFull) {
            accountMessage("主世界资源已满，提前下线")
            InGameVars.isEarlyOffline = true
            return false
        }
    }

    return true
}

// Determine which time group index (0-3) based on current hour
private fun getCurrentTimeGroupIndex(): Int {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 6 -> 0   // Dawn
        hour < 12 -> 1  // Morning
        hour < 18 -> 2  // Afternoon
        else -> 3        // Night
    }
}
