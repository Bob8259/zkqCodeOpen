package com.coc.zkqcode.jar.code.universal.smalltools.scheduling

import com.coc.zkqcode.jar.code.universal.smalltools.ScheduledUpgrade
import com.coc.zkqcode.jar.ui.Strings

/**
 * Builds the bottom-right status board shown while waiting:
 * one line per enabled account with its current upgrade / state info.
 * All text goes through the localization layer ([Strings]).
 */
object UpgradeTimeStatusBoard {

    fun build(
        schedule: List<ScheduledUpgrade>,
        enabledAccounts: List<Int>,
        preparingAccount: Int = 0,
        preparingLabel: String = ""
    ): String {
        val nowSec = System.currentTimeMillis() / 1000L
        val sb = StringBuilder(Strings.upgradeTimeBoardTitle + "\n")

        val byAccount = schedule
            .filter { it.account in enabledAccounts }
            .groupBy { it.account }
            .entries
            .sortedBy { it.value.minOf { v -> v.completionTs } }
        for ((account, items) in byAccount) {
            if (account == preparingAccount) {
                sb.appendLine(Strings.upgradeTimeBoardPreparing.format(account, preparingLabel))
            } else {
                val earliest = items.minBy { it.completionTs }
                val remaining = (earliest.completionTs - nowSec).coerceAtLeast(0)
                if (remaining == 0L) {
                    sb.appendLine(Strings.upgradeTimeBoardDone.format(account))
                } else {
                    sb.appendLine(
                        Strings.upgradeTimeBoardUpgrading.format(account, earliest.name, earliest.lvl, formatDuration(remaining))
                    )
                }
            }
        }

        val accountsWithData = schedule.map { it.account }.toSet()
        for (id in enabledAccounts.filter { it !in accountsWithData }) {
            if (id == preparingAccount) {
                sb.appendLine(Strings.upgradeTimeBoardPreparing.format(id, preparingLabel))
            } else {
                sb.appendLine(Strings.upgradeTimeBoardNoData.format(id))
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
            if (d > 0) append("$d${Strings.dayUnit}")
            if (h > 0) append("$h${Strings.hourUnit}")
            if (m > 0 || isEmpty()) append("$m${Strings.minuteUnit}")
            append("$s${Strings.secondUnit}")
        }
    }
}
