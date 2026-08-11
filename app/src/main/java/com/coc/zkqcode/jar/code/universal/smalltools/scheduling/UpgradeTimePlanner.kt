package com.coc.zkqcode.jar.code.universal.smalltools.scheduling

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.smalltools.ScheduledUpgrade
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Resolves the enabled account ids (batch-create range, or accounts with
 * ISOPEN=1). Shared by the scheduler (UpgradeTimePlanner) and the status
 * snapshot (StatusSnapshot) so the reported nextUpgrade always matches the
 * account set the scheduler actually considers.
 */
internal fun resolveEnabledAccountIds(): List<Int> {
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

/** Max consecutive failed target logins before the account is demoted to a no-data account. */
const val UPGRADE_TIME_MAX_TARGET_RETRIES = 3

/** Persistent storage key for per-account target login failure retry counts. */
private const val TARGET_RETRY_COUNTS_KEY = "upgrade_time_login_fail_retries"

private val retryGson = Gson()

/** Loads persisted per-account target login failure retry counts (accountId -> count). */
internal suspend fun loadTargetRetryCounts(): Map<Int, Int> {
    val raw = readMemory(TARGET_RETRY_COUNTS_KEY)
    if (raw.isBlank()) return emptyMap()
    return try {
        retryGson.fromJson(raw, object : TypeToken<Map<Int, Int>>() {}.type) ?: emptyMap()
    } catch (_: Exception) {
        emptyMap()
    }
}

/** Persists per-account target login failure retry counts (accountId -> count). */
internal suspend fun saveTargetRetryCounts(counts: Map<Int, Int>) {
    writeMemory(TARGET_RETRY_COUNTS_KEY, retryGson.toJson(counts))
}

/** Increments the persisted retry count for an account; returns true when it should be demoted. */
internal suspend fun recordTargetLoginFailure(account: Int): Boolean {
    val counts = loadTargetRetryCounts().toMutableMap()
    val next = (counts[account] ?: 0) + 1
    if (next >= UPGRADE_TIME_MAX_TARGET_RETRIES) {
        counts.remove(account)
        saveTargetRetryCounts(counts)
        return true
    }
    counts[account] = next
    saveTargetRetryCounts(counts)
    return false
}

/** Clears the persisted retry count for an account after a successful target login. */
internal suspend fun clearTargetRetryCount(account: Int) {
    val counts = loadTargetRetryCounts().toMutableMap()
    if (counts.remove(account) != null) {
        saveTargetRetryCounts(counts)
    }
}

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

    /** Re-reads config + schedule and computes the current plan.
     *  Target priority (by user decision): completed-countdown accounts first
     *  (retryCount == 0), then retry accounts (retryCount 1..MAX-1). When no
     *  entry is due, falls back to the globally earliest entry so the wait/fill
     *  branch still has a target to wait for. Accounts whose persisted retry
     *  count already reached the max are treated as demoted: excluded from
     *  target selection and from the "has data" set so they fall back to being
     *  fill candidates. This self-heals a stale >=MAX count that could
     *  otherwise pin an account as a never-due target (busy-loop) while it
     *  still has schedule data. */
    fun plan(schedule: List<ScheduledUpgrade>, retryCounts: Map<Int, Int> = emptyMap()): UpgradeTimePlan {
        val enabledAccounts = resolveEnabledAccountIds()
        val demoted = retryCounts.filterValues { it >= UPGRADE_TIME_MAX_TARGET_RETRIES }.keys.toSet()
        val accountsWithData = schedule.map { it.account }.toSet() - demoted
        val fillCandidates = enabledAccounts.filter { it !in accountsWithData }
        val nowSec = System.currentTimeMillis() / 1000L
        val entries = schedule.filter { it.account in enabledAccounts && it.account !in demoted }
        val dueEntries = entries.filter { it.completionTs <= nowSec + UPGRADE_TIME_DUE_GRACE_SECONDS }
        // Priority 1: completed-countdown accounts that have never failed.
        val freshTarget = dueEntries
            .filter { (retryCounts[it.account] ?: 0) == 0 }
            .minByOrNull { it.completionTs }
        // Priority 2: retry accounts (still within the retry budget).
        val retryTarget = dueEntries
            .filter { (retryCounts[it.account] ?: 0) in 1 until UPGRADE_TIME_MAX_TARGET_RETRIES }
            .minByOrNull { it.completionTs }
        // When nothing is due, keep the earliest entry as the "waiting" target.
        val target = freshTarget ?: retryTarget ?: entries.minByOrNull { it.completionTs }
        val due = freshTarget != null || retryTarget != null
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
