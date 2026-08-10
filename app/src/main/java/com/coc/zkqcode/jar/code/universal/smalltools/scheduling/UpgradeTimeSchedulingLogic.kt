package com.coc.zkqcode.jar.code.universal.smalltools.scheduling

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.runAccountStepBlock
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.ScheduledUpgrade
import com.coc.zkqcode.jar.code.universal.smalltools.data.getStaticConfig
import com.coc.zkqcode.jar.code.universal.smalltools.loadSchedule
import com.coc.zkqcode.jar.code.universal.smalltools.removeExpiredScheduleForAccount
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Upgrade-time scheduling: logs into the account whose upgrade completes first,
 * fills idle time with no-data accounts (collect resources + upgrade data).
 *
 * State machine:
 *   PLANNING → LOGGING_TARGET / FILLING / WAITING
 *
 * Priority rules (behavior kept identical to pre-rewrite):
 *  - Target = enabled account with the earliest upgrade completion, but only
 *    logged in when due (completion + 2s grace).
 *  - While the target is not due, fill no-data accounts in ascending order.
 *  - After a full fill round, apply a cooldown before filling resumes.
 *  - When all accounts have no data, every enabled account goes through the
 *    fill flow (no separate "collect" branch), no cooldown.
 *
 * @see UpgradeTimePlanner for pure decision logic.
 * @see UpgradeTimeWaitController for the wait-until-due loop.
 */
suspend fun runUpgradeTimeSchedulingScript() {
    ShowMessage(Strings.upgradeTimeSchedulingStarted)
    val planner = UpgradeTimePlanner()
    val wait = UpgradeTimeWaitController()
    var fillCooldownEnd = 0L

    while (currentCoroutineContext().isActive) {
        val schedule = loadSchedule()
        val plan = planner.plan(schedule)

        // No enabled accounts: prompt and re-check later.
        if (plan.enabledAccounts.isEmpty()) {
            ShowMessage(Strings.upgradeTimeNoEnabledAccounts, isChecking = false)
            delay(UPGRADE_TIME_NO_ACCOUNTS_WAIT_MS)
            continue
        }

        val candidates = plan.fillCandidates

        // 1) Target is due -> log in, collect data. The fill cursor is NOT
        //    reset here (ADR-0001 §2.3): it only wraps when a fill round is
        //    exhausted (nextFillAccount returns null). The cooldown is cleared
        //    so filling may resume normally after the target flow.
        if (plan.target != null && plan.due) {
            logInTarget(plan.target, plan.enabledAccounts, schedule, planner)
            fillCooldownEnd = 0L
            continue
        }

        // 2) Target exists but not due: fill no-data accounts while waiting.
        if (plan.target != null) {
            if (candidates.isNotEmpty()) {
                val nextFill = planner.nextFillAccount(candidates)
                if (nextFill != null) {
                    logInFill(nextFill, plan.enabledAccounts, schedule, planner)
                    continue
                }
                // Fill round exhausted -> start the cooldown once, then wait.
                if (fillCooldownEnd == 0L) {
                    fillCooldownEnd = System.currentTimeMillis() + UPGRADE_TIME_FILL_COOLDOWN_MS
                }
            }
            val result = wait.await(plan.target, plan.enabledAccounts, readPollMs()) {
                fillCooldownEnd > 0L && System.currentTimeMillis() >= fillCooldownEnd
            }
            when (result) {
                UpgradeTimeWaitResult.FILL_READY -> {
                    planner.resetFillRound()
                    fillCooldownEnd = 0L
                }
                UpgradeTimeWaitResult.TARGET_DUE -> { /* re-plan: target is due now */ }
                UpgradeTimeWaitResult.STOPPED -> return
            }
            continue
        }

        // 3) No target at all (no upgrade data): every enabled account goes
        //    through the fill flow to collect, restarting the round immediately
        //    when exhausted (no cooldown).
        if (candidates.isEmpty()) {
            ShowMessage(UpgradeTimeStatusBoard.build(schedule, plan.enabledAccounts), isChecking = false)
            delay(UPGRADE_TIME_NO_ACCOUNTS_WAIT_MS)
            continue
        }
        val nextFill = planner.nextFillAccount(candidates)
        if (nextFill != null) {
            logInFill(nextFill, plan.enabledAccounts, schedule, planner)
            continue
        }
        planner.resetFillRound()
    }
}

/** Runs the full flow on the due target and protects old schedule data. */
private suspend fun logInTarget(
    target: ScheduledUpgrade,
    enabledAccounts: List<Int>,
    schedule: List<ScheduledUpgrade>,
    planner: UpgradeTimePlanner
) {
    val before = schedule.filter { it.account == target.account }
    InGameVars.currentAccountNumber = target.account
    InGameVars.currentGameVersion = planner.resolveAccountGameVersion(target.account)
    ShowMessage(
        UpgradeTimeStatusBoard.build(schedule, enabledAccounts, target.account, Strings.upgradeTimePreparingTarget),
        isChecking = false
    )
    delay(UPGRADE_TIME_PRE_LOGIN_DELAY_MS)
    runAccountStepBlock()
    // Old-data guard: only clear expired entries when new data was actually
    // collected; otherwise keep the old records so a failed login loses nothing.
    val after = loadSchedule().filter { it.account == target.account }
    if (before != after) {
        removeExpiredScheduleForAccount(target.account)
    }
}

/** Runs the full flow on one no-data fill account (collect resources + data). */
private suspend fun logInFill(
    account: Int,
    enabledAccounts: List<Int>,
    schedule: List<ScheduledUpgrade>,
    planner: UpgradeTimePlanner
) {
    InGameVars.currentAccountNumber = account
    InGameVars.currentGameVersion = planner.resolveAccountGameVersion(account)
    ShowMessage(
        UpgradeTimeStatusBoard.build(schedule, enabledAccounts, account, Strings.upgradeTimePreparingFill),
        isChecking = false
    )
    runAccountStepBlock()
}

/** Reads the wait poll interval setting, clamped to a sane range. */
private fun readPollMs(): Long {
    val seconds = getStaticConfig(GLOBAL_SETTINGS.UTS_WAIT_POLL_SECONDS.key).toLongOrNull() ?: 5L
    return seconds.coerceIn(1L, 300L) * 1000L
}
