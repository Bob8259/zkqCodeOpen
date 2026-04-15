package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.runAccountStepBlock
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigOrStop
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import kotlin.random.Random

// Represents one of the 4 time groups
private enum class TimeGroup(val label: String) {
    DAWN("凌晨组"),
    MORNING("上午组"),
    AFTERNOON("下午组"),
    NIGHT("夜晚组")
}

// Per-group config key bundles
private data class GroupConfig(
    val battleRatioKey: String,
    val stopProbKey: String,
    val continueMinKey: String,
    val continueMaxKey: String,
    val accountsKey: String
)

private val GROUP_CONFIGS = mapOf(
    TimeGroup.DAWN to GroupConfig(
        battleRatioKey = GLOBAL_SETTINGS.RS_BATTLE_RATIO_DAWN.key,
        stopProbKey = GLOBAL_SETTINGS.RS_STOP_PROB_DAWN.key,
        continueMinKey = GLOBAL_SETTINGS.RS_CONTINUE_MIN_DAWN.key,
        continueMaxKey = GLOBAL_SETTINGS.RS_CONTINUE_MAX_DAWN.key,
        accountsKey = GLOBAL_SETTINGS.RS_ACCOUNTS_DAWN.key
    ),
    TimeGroup.MORNING to GroupConfig(
        battleRatioKey = GLOBAL_SETTINGS.RS_BATTLE_RATIO_MORNING.key,
        stopProbKey = GLOBAL_SETTINGS.RS_STOP_PROB_MORNING.key,
        continueMinKey = GLOBAL_SETTINGS.RS_CONTINUE_MIN_MORNING.key,
        continueMaxKey = GLOBAL_SETTINGS.RS_CONTINUE_MAX_MORNING.key,
        accountsKey = GLOBAL_SETTINGS.RS_ACCOUNTS_MORNING.key
    ),
    TimeGroup.AFTERNOON to GroupConfig(
        battleRatioKey = GLOBAL_SETTINGS.RS_BATTLE_RATIO_AFTERNOON.key,
        stopProbKey = GLOBAL_SETTINGS.RS_STOP_PROB_AFTERNOON.key,
        continueMinKey = GLOBAL_SETTINGS.RS_CONTINUE_MIN_AFTERNOON.key,
        continueMaxKey = GLOBAL_SETTINGS.RS_CONTINUE_MAX_AFTERNOON.key,
        accountsKey = GLOBAL_SETTINGS.RS_ACCOUNTS_AFTERNOON.key
    ),
    TimeGroup.NIGHT to GroupConfig(
        battleRatioKey = GLOBAL_SETTINGS.RS_BATTLE_RATIO_NIGHT.key,
        stopProbKey = GLOBAL_SETTINGS.RS_STOP_PROB_NIGHT.key,
        continueMinKey = GLOBAL_SETTINGS.RS_CONTINUE_MIN_NIGHT.key,
        continueMaxKey = GLOBAL_SETTINGS.RS_CONTINUE_MAX_NIGHT.key,
        accountsKey = GLOBAL_SETTINGS.RS_ACCOUNTS_NIGHT.key
    )
)

// Determine which time group the current hour falls into
private fun getCurrentTimeGroup(): TimeGroup {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 6 -> TimeGroup.DAWN
        hour < 12 -> TimeGroup.MORNING
        hour < 18 -> TimeGroup.AFTERNOON
        else -> TimeGroup.NIGHT
    }
}

// Parse comma-separated account IDs from config
private fun getGroupAccounts(accountsKey: String): List<Int> {
    val raw = GlobalVars.configStates[accountsKey]?.value ?: return emptyList()
    return raw.split(",").mapNotNull { it.trim().toIntOrNull() }.filter { it > 0 }
}

// Read an integer config value with a fallback
private fun getIntConfig(key: String, fallback: Int): Int {
    return GlobalVars.configStates[key]?.value?.toIntOrNull() ?: fallback
}

// Calculate rest time: battle_duration_ms * (100 - ratio) / ratio
// ratio is clamped to [1, 99] to avoid division by zero or zero rest
private fun calculateRestTimeMs(battleDurationMs: Long, ratioPercent: Int): Long {
    val clampedRatio = ratioPercent.coerceIn(1, 99)
    return battleDurationMs * (100 - clampedRatio) / clampedRatio
}

/**
 * Main entry point for the random scheduling mode.
 * Replaces the sequential account loop in MainScript when RANDOM_SCHEDULING is enabled.
 */
suspend fun runRandomSchedulingScript() {
    ShowMessage("随机上号模式已启动")

    while (currentCoroutineContext().isActive) {
        val currentGroup = getCurrentTimeGroup()
        val config = GROUP_CONFIGS[currentGroup]!!
        val accounts = getGroupAccounts(config.accountsKey)

        if (accounts.isEmpty()) {
            accountMessage("${currentGroup.label}没有配置账号，等待下一个时间段...")
            // Wait 60s then re-check (time group may have changed)
            delay(60_000L)
            continue
        }

        // Shuffle accounts for this round
        val shuffled = accounts.shuffled()
        ShowMessage("${currentGroup.label}开始运行，账号顺序：$shuffled")

        var groupChanged = false
        for (accountId in shuffled) {
            if (!currentCoroutineContext().isActive) return

            // Check if time group has changed before starting this account
            if (getCurrentTimeGroup() != currentGroup) {
                groupChanged = true
                break
            }

            // Set current account
            InGameVars.currentAccountNumber = accountId
            InGameVars.currentGameVersion = GameVersion.fromId(
                getIntConfig("${Schema.ACCOUNT_SETTINGS.GAME_VERSION.key}$accountId", 0)
            )

            accountMessage("随机上号：运行账号 $accountId")

            // Record start time and run the account
            val startTime = System.currentTimeMillis()
            runAccountStepBlock()

            val battleDurationMs = System.currentTimeMillis() - startTime

            // Read group settings
            val battleRatio = getIntConfig(config.battleRatioKey, 80)
            val stopProb = getIntConfig(config.stopProbKey, 50)
            val continueMin = getIntConfig(config.continueMinKey, 5)
            val continueMax = getIntConfig(config.continueMaxKey, 25)

            // Random decision: stop after config flow or continue battling
            val roll = Random.nextInt(100) // 0..99
            val totalDurationMs: Long

            if (roll < stopProb) {
                // Stop path: rest based on actual battle duration
                totalDurationMs = battleDurationMs
                ShowMessage("账号 $accountId：完成流程后下线（概率命中）")
            } else {
                // Continue path: run full step blocks in a loop until extra time expires.
                // Each stepBlock runs to completion — timeout is only checked between rounds.
                val safeMin = minOf(continueMin, continueMax)
                val safeMax = maxOf(continueMin, continueMax)
                val extraMinutes = if (safeMin == safeMax) safeMin else Random.nextInt(safeMin, safeMax + 1)
                val extraMs = extraMinutes * 60_000L
                ShowMessage("账号 $accountId：继续对战 $extraMinutes 分钟")

                val extraStartTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - extraStartTime < extraMs
                    && currentCoroutineContext().isActive) {
                    runAccountStepBlock()
                }

                totalDurationMs = battleDurationMs + (System.currentTimeMillis() - extraStartTime)
            }

            // Calculate and wait rest time: total * (100 - ratio) / ratio
            val restMs = calculateRestTimeMs(totalDurationMs, battleRatio)
            val restSeconds = restMs / 1000
            accountMessage("账号 $accountId 完成，等待 ${restSeconds}秒 后运行下一个账号")
            delay(restMs)
        }

        // If group changed mid-round, restart the outer loop immediately to pick new group
        if (groupChanged) {
            ShowMessage("时间段已切换，重新评估当前组")
        }
        // Otherwise the round is complete — re-shuffle and repeat the same group
    }
}
