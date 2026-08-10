package com.coc.zkqcode.jar.code.universal.smalltools.scheduling

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.ScheduledUpgrade
import com.coc.zkqcode.jar.code.universal.smalltools.data.getStaticConfig
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killApp
import com.coc.zkqcode.jar.code.universal.smalltools.loadSchedule
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/** Outcome of one wait cycle. */
enum class UpgradeTimeWaitResult { TARGET_DUE, FILL_READY, STOPPED }

/**
 * Kills the game while waiting and polls until the target upgrade completes,
 * refreshing the bottom-right status board every poll interval.
 */
class UpgradeTimeWaitController {

    /**
     * Waits until [target] is due. The schedule is re-read every tick so the
     * board reflects early completions and refreshed timers. [restartFillCheck]
     * is evaluated each tick; when it returns true (and enough time remains
     * before the target is due) the wait aborts early with FILL_READY so the
     * scheduler can resume fill work.
     */
    suspend fun await(
        target: ScheduledUpgrade,
        enabledAccounts: List<Int>,
        pollMs: Long,
        restartFillCheck: () -> Boolean
    ): UpgradeTimeWaitResult {
        // Free resources while waiting (unless game restart is globally disabled)
        if (getStaticConfig(GLOBAL_SETTINGS.DISABLE_RESTART_GAME.key) != "1") {
            killApp(InGameVars.currentGameVersion.packageName)
            RunShell.runNoOutput("am kill-all")
        }
        RunShell.runNoOutput("input keyevent 3")

        val nowSec = System.currentTimeMillis() / 1000L
        val waitMs = ((target.completionTs - nowSec) * 1000L).coerceAtLeast(0L)
        val endTime = System.currentTimeMillis() + waitMs
        while (System.currentTimeMillis() < endTime) {
            if (!currentCoroutineContext().isActive) return UpgradeTimeWaitResult.STOPPED
            val remainingMs = endTime - System.currentTimeMillis()
            if (restartFillCheck() && remainingMs > UPGRADE_TIME_FILL_RESUME_MIN_REMAINING_MS) {
                return UpgradeTimeWaitResult.FILL_READY
            }
            val schedule = loadSchedule()
            ShowMessage(UpgradeTimeStatusBoard.build(schedule, enabledAccounts), isChecking = false)
            delay(minOf(pollMs, remainingMs.coerceAtLeast(0L)))
        }
        return UpgradeTimeWaitResult.TARGET_DUE
    }
}
