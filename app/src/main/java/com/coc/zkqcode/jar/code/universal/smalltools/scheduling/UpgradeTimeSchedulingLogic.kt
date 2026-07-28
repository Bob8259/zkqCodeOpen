package com.coc.zkqcode.jar.code.universal.smalltools.scheduling

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.runAccountStepBlock
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.smalltools.ScheduledUpgrade
import com.coc.zkqcode.jar.code.universal.smalltools.loadSchedule
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killApp
import com.coc.zkqcode.jar.code.universal.smalltools.data.getStaticConfig
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

suspend fun runUpgradeTimeSchedulingScript() {
    ShowMessage("升级时间上号模式已启动")
    var lastFillAccount = 0
    var fillCooldownEnd = 0L  // when the next fill round is allowed
    while (currentCoroutineContext().isActive) {
        val enabledAccounts = resolveEnabledAccounts()
        if (enabledAccounts.isEmpty()) {
            accountMessage("当前未开启任何账号")
            delay(60_000L)
            continue
        }
        val schedule = loadSchedule()
        val accountsWithData = schedule.map { it.account }.toSet()
        val fillCandidates = enabledAccounts.filter { it !in accountsWithData }.sorted()
        val target = schedule
            .filter { it.account in enabledAccounts }
            .minByOrNull { it.completionTs }
        if (target != null) {
            val nowSec = System.currentTimeMillis() / 1000L
            val timeDue = target.completionTs <= nowSec + 2
            if (!timeDue && fillCandidates.isNotEmpty()) {
                val nextAfter = fillCandidates.firstOrNull { it > lastFillAccount }
                if (nextAfter != null) {
                    lastFillAccount = nextAfter
                    InGameVars.currentAccountNumber = nextAfter
                    InGameVars.currentGameVersion = resolveAccountGameVersion(nextAfter)
                    ShowMessage(buildScheduleSummary(schedule, enabledAccounts, nextAfter, "无数据，准备上号"), isChecking = false)
                    runAccountStepBlock()
                    val afterSec = System.currentTimeMillis() / 1000L
                    if (afterSec >= target.completionTs) {
                        accountMessage("目标号 ${target.account} 升级已完成，正在切换...")
                    }
                    continue
                }
                // All fills done, fall through to wait
                fillCooldownEnd = System.currentTimeMillis() + 10 * 60_000L
            }
            // Kill game silently before waiting (no account prefix)
            if (getStaticConfig("disable_restart_game") != "1") {
                killApp(InGameVars.currentGameVersion.packageName)
                RunShell.runNoOutput("am kill-all")
            }
            RunShell.runNoOutput("input keyevent 3")
            val waitMs = ((target.completionTs - System.currentTimeMillis() / 1000L) * 1000).coerceAtLeast(0)
            if (waitMs > 0) {
                val endTime = System.currentTimeMillis() + waitMs
                while (System.currentTimeMillis() < endTime) {
                    if (!currentCoroutineContext().isActive) return
                    // If cooldown expired and enough time remains, restart fill round
                    if (fillCooldownEnd > 0 && System.currentTimeMillis() >= fillCooldownEnd
                        && endTime - System.currentTimeMillis() > 5 * 60_000L) {
                        lastFillAccount = 0
                        break  // exit wait, go back to top to run fills
                    }
                    val currentSchedule = loadSchedule()
                    ShowMessage(buildScheduleSummary(currentSchedule, enabledAccounts), isChecking = false)
                    delay(minOf(1000L, endTime - System.currentTimeMillis()))
                }
            }
            InGameVars.currentAccountNumber = target.account
            InGameVars.currentGameVersion = resolveAccountGameVersion(target.account)
            ShowMessage(buildScheduleSummary(schedule, enabledAccounts, target.account, "升级完成，准备上号"), isChecking = false)
            delay(500L)
            runAccountStepBlock()
            lastFillAccount = 0
            fillCooldownEnd = 0L
        } else {
            accountMessage("暂无升级数据，顺序运行收集数据（账号 $enabledAccounts）")
            val account = enabledAccounts.first()
            InGameVars.currentAccountNumber = account
            InGameVars.currentGameVersion = resolveAccountGameVersion(account)
            runAccountStepBlock()
        }
    }
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

private fun resolveAccountGameVersion(account: Int): GameVersion {
    val versionId = GlobalVars.configStates["${ACCOUNT_SETTINGS.GAME_VERSION.key}$account"]?.value?.toIntOrNull() ?: 1
    return GameVersion.fromId(versionId)
}

private fun buildScheduleSummary(
    schedule: List<ScheduledUpgrade>,
    enabledAccounts: List<Int>,
    preparingAccount: Int = 0,
    preparingLabel: String = ""
): String {
    val nowSec = System.currentTimeMillis() / 1000L
    val sb = StringBuilder()
    sb.append("等待中:\n")
    val byAccount = schedule
        .filter { it.account in enabledAccounts }
        .groupBy { it.account }
        .entries
        .sortedBy { it.value.minOf { v -> v.completionTs } }
    for ((account, items) in byAccount) {
        if (account == preparingAccount) {
            sb.append("${account}号 $preparingLabel\n")
        } else {
            val earliest = items.minBy { it.completionTs }
            val remaining = (earliest.completionTs - nowSec).coerceAtLeast(0)
            if (remaining == 0L) {
                sb.append("${account}号 升级完成，等待启动\n")
            } else {
                sb.append("${account}号 ${earliest.name} Lv${earliest.lvl} ${formatDuration(remaining)}\n")
            }
        }
    }
    val accountsWithData = schedule.map { it.account }.toSet()
    val noDataAccounts = enabledAccounts.filter { it !in accountsWithData }
    if (noDataAccounts.isNotEmpty()) {
        for (id in noDataAccounts.sorted()) {
            if (id == preparingAccount) {
                sb.append("${id}号 $preparingLabel\n")
            } else {
                sb.append("${id}号 无数据\n")
            }
        }
    }
    return sb.toString().trimEnd()
}

private fun formatDuration(totalSeconds: Long): String {
    val d = totalSeconds / 86400
    val h = (totalSeconds % 86400) / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return buildString {
        if (d > 0) append("${d}天")
        if (h > 0) append("${h}时")
        if (m > 0) append("${m}分")
        append("${s}秒")
    }
}
