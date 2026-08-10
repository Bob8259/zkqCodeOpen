package com.coc.zkqcode.jar.code.universal.smalltools.scheduling

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.smalltools.ScheduledUpgrade
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS

// ── Fixed scheduling constants (named constants per rewrite spec §6) ──

/** Seconds of grace after a completion timestamp before the account counts as due. */
const val UPGRADE_TIME_DUE_GRACE_SECONDS = 2L

/** Cooldown after one full fill round before fill work may resume. */
const val UPGRADE_TIME_FILL_COOLDOWN_MS = 10 * 60_000L

/** Fill work only resumes while more than this much time remains before the target is due. */
const val UPGRADE_TIME_FILL_RESUME_MIN_REMAINING_MS = 5 * 60_000L

/** How long to wait before re-checking when there is nothing actionable. */
const val UPGRADE_TIME_NO_ACCOUNTS_WAIT_MS = 60_000L

/** Small settling delay before logging into the target account. */
const val UPGRADE_TIME_PRE_LOGIN_DELAY_MS = 500L

/** One planning snapshot: what the scheduling loop should do next. */
data class UpgradeTimePlan(
    val enabledAccounts: List<Int>,
    val target: ScheduledUpgrade?,
    val due: Boolean,
    val fillCandidates: List<Int>
)

/**
 * Pure decision layer for upgrade-time scheduling (no I/O, no UI).
 *
 * Owns the fill cursor which advances through a fill round in ascending account
 * order. The cursor only wraps (reset to the smallest candidate) when a fill
 * round is exhausted (ADR-0001 §2.3): target logins do NOT reset it, so an
 * interrupted fill round resumes where it left off.
 */
class UpgradeTimePlanner {

    private var lastFillAccount: Int = 0

    /** Re-reads config + schedule and computes the current plan. */
    fun plan(schedule: List<ScheduledUpgrade>): UpgradeTimePlan {
        val enabledAccounts = resolveEnabledAccounts()
        val accountsWithData = schedule.map { it.account }.toSet()
        val fillCandidates = enabledAccounts.filter { it !in accountsWithData }
        val target = schedule
            .filter { it.account in enabledAccounts }
            .minByOrNull { it.completionTs }
        val due = target != null &&
            target.completionTs <= System.currentTimeMillis() / 1000L + UPGRADE_TIME_DUE_GRACE_SECONDS
        return UpgradeTimePlan(enabledAccounts, target, due, fillCandidates)
    }

    /**
     * Picks the next fill account in ascending order, advancing the cursor.
     * Returns null when the round is exhausted (every current candidate attempted).
     */
    fun nextFillAccount(candidates: List<Int>): Int? {
        val next = candidates.firstOrNull { it > lastFillAccount } ?: return null
        lastFillAccount = next
        return next
    }

    /** Restarts the fill round so filling resumes from the smallest candidate. */
    fun resetFillRound() {
        lastFillAccount = 0
    }

    /** Resolves the game version configured for an account. */
    fun resolveAccountGameVersion(account: Int): GameVersion {
        val versionId = GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}$account"]?.value?.toIntOrNull() ?: 1
        return GameVersion.fromId(versionId)
    }

    private fun resolveEnabledAccounts(): List<Int> {
        val isBatchCreate = GlobalVars.configStates[GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key]?.value == "1"
        if (isBatchCreate) {
            val start = GlobalVars.configStates[GLOBAL_SETTINGS.CREATE_START_ID.key]?.value?.toIntOrNull() ?: 1
            val end = GlobalVars.configStates[GLOBAL_SETTINGS.CREATE_END_ID.key]?.value?.toIntOrNull() ?: 10
            return (start..end).toList()
        }
        val accountCount = GlobalVars.configStates[GLOBAL_SETTINGS.ACCOUNT_COUNT.key]?.value?.toIntOrNull() ?: 2
        return (1..accountCount).filter { id ->
            GlobalVars.configStates["${ACCOUNT_SETTINGS.ISOPEN.key}$id"]?.value == "1"
        }
    }
}
