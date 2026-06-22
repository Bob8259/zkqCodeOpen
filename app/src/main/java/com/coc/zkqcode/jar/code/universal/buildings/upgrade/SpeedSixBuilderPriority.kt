package com.coc.zkqcode.jar.code.universal.buildings.upgrade

import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import com.coc.zkqcode.jar.code.universal.BuildingStatus

/**
 * Speed 6 Builder priority override logic.
 *
 * When the "Speed 6 Builder" mode is enabled, this module boosts the priority of
 * key buildings required for gear-up and 6th builder unlock:
 *   - Main base: Archer Tower → 10, Cannon → 7, Mortar → 8
 *   - Builder base: Multi Mortar → 9 (6th builder req), Archer Tower 6, Double Cannon 4
 *   - Builder base heroes: Battle Machine → 25, Battle Copter → 20
 *
 * Buildings that have been unlocked but have not yet reached their target level get the
 * highest possible priority, ordered as: heroes > Multi Mortar(9) > Archer Tower(6) > Double Cannon(4).
 *
 * A building/hero is considered "unlocked" only when its data entry exists in the JSON.
 * Un-built (unlocked = false) entries are NOT boosted — they only appear in the summary
 * message as "未解锁" so the player knows the building/hero still needs to be constructed.
 */
object SpeedSixBuilderPriority {

    // Main base target levels (per-building)
    private const val MAIN_MORTAR_TARGET_LEVEL = 8
    private const val MAIN_ARCHER_TOWER_TARGET_LEVEL = 10
    private const val MAIN_CANNON_TARGET_LEVEL = 7

    // Builder base target levels (per-building). Multi Mortar is the 6th-builder requirement.
    private const val BB_MULTI_MORTAR_TARGET_LEVEL = 9
    private const val BB_ARCHER_TOWER_TARGET_LEVEL = 6
    private const val BB_DOUBLE_CANNON_TARGET_LEVEL = 4

    private const val BATTLE_MACHINE_TARGET_LEVEL = 25
    private const val BATTLE_COPTER_TARGET_LEVEL = 20

    // Priority order for un-met main base building targets: Archer Tower → Cannon → Mortar
    private val mainBasePriorityOrder = listOf("箭塔", "加农炮", "迫击炮")
    // Builder base priority order: Archer Tower → Double Cannon → Multi Mortar → Battle Machine → Battle Copter.
    // Note: night world has both "双管加农炮" (Double Cannon, data 1000041) and "加农炮" (Cannon, separate building).
    // Heroes (战争机器, 战斗直升机) are ordered here so all five targets can share one sorting rule.
    private val builderBasePriorityOrder = listOf("箭塔", "双管加农炮", "多管迫击炮", "战争机器", "战斗直升机")

    /**
     * Entry describing an un-met Speed 6 Builder target.
     * - [unlocked] = false means the building/hero data is missing in the JSON (not yet built/unlocked).
     * - When [unlocked] is false, [currentLevel] is meaningless (always 0); the display shows "未解锁".
     * - [upgrading] = true means at least one instance is already upgrading (timer > 0 in JSON).
     *   Such entries are NOT boosted in priority (one is already queued), but they still appear in
     *   the summary message so the user sees the real status.
     */
    data class UnmetEntry(
        val name: String,
        val currentLevel: Int,
        val targetLevel: Int,
        val unlocked: Boolean,
        val upgrading: Boolean
    )

    /**
     * Returns true when the building/hero is unlocked AND has not yet reached the
     * Speed 6 Builder target level AND is not already upgrading (timer > 0 in JSON).
     * Used to decide whether a priority boost should be applied. Un-built entries
     * (unlocked = false) are NOT boosted here. Buildings already in upgrade
     * (upgrading = true) are also NOT boosted — no need to queue another
     * of the same type when one is already in progress.
     */
    private fun needsPriorityBoost(unlocked: Boolean, level: Int, target: Int, upgrading: Boolean): Boolean =
        !upgrading && unlocked && level < target

    /**
     * Returns true when the building/hero is un-met for reporting purposes:
     * either not yet unlocked OR unlocked but below the target level.
     */
    private fun isUnmetForReport(unlocked: Boolean, level: Int, target: Int): Boolean =
        !unlocked || level < target

    /**
     * Apply priority overrides for main base buildings.
     * Returns a modified copy of [originalPriorities] with boosted priorities
     * for gear-up buildings that have been unlocked but haven't reached their
     * per-building target level. Un-built (unlocked = false) entries are NOT
     * boosted here — they only appear in the summary message.
     *
     * Algorithm: shift the entire priority table up by [sortedUnmet.size] so no
     * collision is possible, then assign sorted-unmet targets 1, 2, 3, ...
     * (always lower = higher priority than any user-defined entry, and never
     * producing duplicate values).
     */
    fun applyMainBaseOverride(
        originalPriorities: Map<String, Int>
    ): Map<String, Int> {
        val unmetTargets = mutableListOf<String>()
        val targets = listOf(
            Triple("迫击炮", MAIN_MORTAR_TARGET_LEVEL, InGameVars.mainBaseSpeedSixStatus["迫击炮"]),
            Triple("箭塔", MAIN_ARCHER_TOWER_TARGET_LEVEL, InGameVars.mainBaseSpeedSixStatus["箭塔"]),
            Triple("加农炮", MAIN_CANNON_TARGET_LEVEL, InGameVars.mainBaseSpeedSixStatus["加农炮"])
        )
        for ((name, targetLvl, status) in targets) {
            if (status != null && needsPriorityBoost(status.unlocked, status.level, targetLvl, status.upgrading)) {
                unmetTargets.add(name)
            }
        }

        if (unmetTargets.isEmpty()) return originalPriorities

        // Sort unmet targets by priority order
        val sortedUnmet = unmetTargets.sortedBy {
            mainBasePriorityOrder.indexOf(it).let { idx -> if (idx == -1) Int.MAX_VALUE else idx }
        }

        // Shift every user-defined priority up by the number of un-met targets, so that
        // assigning 1..N to the un-met entries can never collide with the user's values.
        // (The user's priorities are still read in their original order; we just renumber.)
        val shift = sortedUnmet.size
        val result = originalPriorities.toMutableMap()
        val snapshot = InGameVars.speedSixOriginalPriorities
            .getOrPut(BaseType.Main) { mutableMapOf() }
        // Only snapshot keys we are about to override (so restore only touches what we changed).
        sortedUnmet.forEach { name ->
            val original = originalPriorities[name]
            if (original != null && name !in snapshot) {
                snapshot[name] = original
            }
        }
        // Renumber user priorities to avoid colliding with the boosted 1..N range.
        result.replaceAll { _, v -> v + shift }
        // Assign 1, 2, 3, ... to the sorted un-met targets. The lowest (most urgent) target
        // gets priority 1 so it is selected first.
        sortedUnmet.forEachIndexed { index, name ->
            result[name] = index + 1
        }
        return result
    }

    /**
     * Apply priority overrides for builder base buildings and heroes.
     * Returns a modified copy of [originalPriorities] with boosted priorities
     * for buildings/heroes that have been unlocked but haven't reached their
     * per-building target level. Un-built (unlocked = false) entries are NOT
     * boosted here — they only appear in the summary message.
     *
     * Algorithm: shift the entire priority table up by [sortedUnmet.size] so no
     * collision is possible, then assign sorted-unmet targets 1, 2, 3, ...
     * (always lower = higher priority than any user-defined entry, and never
     * producing duplicate values).
     */
    fun applyBuilderBaseOverride(
        originalPriorities: Map<String, Int>
    ): Map<String, Int> {
        val unmetTargets = mutableListOf<String>()

        val targets = listOf(
            Triple("战争机器", BATTLE_MACHINE_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["战争机器"]),
            Triple("战斗直升机", BATTLE_COPTER_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["战斗直升机"]),
            Triple("多管迫击炮", BB_MULTI_MORTAR_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["多管迫击炮"]),
            Triple("箭塔", BB_ARCHER_TOWER_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["箭塔"]),
            Triple("双管加农炮", BB_DOUBLE_CANNON_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["双管加农炮"])
        )
        for ((name, targetLvl, status) in targets) {
            if (status != null && needsPriorityBoost(status.unlocked, status.level, targetLvl, status.upgrading)) {
                unmetTargets.add(name)
            }
        }

        if (unmetTargets.isEmpty()) return originalPriorities

        // All targets (buildings + heroes) sorted by the unified priority order list
        val sortedUnmet = unmetTargets.sortedBy {
            builderBasePriorityOrder.indexOf(it).let { idx -> if (idx == -1) Int.MAX_VALUE else idx }
        }

        // Shift user priorities up by the number of un-met targets so the boosted 1..N
        // range can never collide with them. Then assign 1, 2, 3, ... to sorted un-met
        // targets (lowest = most urgent wins).
        val shift = sortedUnmet.size
        val result = originalPriorities.toMutableMap()
        val snapshot = InGameVars.speedSixOriginalPriorities
            .getOrPut(BaseType.Builder) { mutableMapOf() }
        // Only snapshot keys we are about to override.
        sortedUnmet.forEach { name ->
            val original = originalPriorities[name]
            if (original != null && name !in snapshot) {
                snapshot[name] = original
            }
        }
        result.replaceAll { _, v -> v + shift }
        sortedUnmet.forEachIndexed { index, name ->
            result[name] = index + 1
        }
        return result
    }

    /**
     * Collect main base buildings that haven't reached their per-building target level.
     * Returned in the configured [mainBasePriorityOrder]. Un-built entries are included
     * with [UnmetEntry.unlocked] = false. Entries currently upgrading are also included
     * (shown as "升级中" in the summary) but are excluded from priority boosting.
     */
    fun collectMainBaseUnmet(): List<UnmetEntry> {
        val list = mutableListOf<UnmetEntry>()
        val targets = listOf(
            Triple("迫击炮", MAIN_MORTAR_TARGET_LEVEL, InGameVars.mainBaseSpeedSixStatus["迫击炮"]),
            Triple("箭塔", MAIN_ARCHER_TOWER_TARGET_LEVEL, InGameVars.mainBaseSpeedSixStatus["箭塔"]),
            Triple("加农炮", MAIN_CANNON_TARGET_LEVEL, InGameVars.mainBaseSpeedSixStatus["加农炮"])
        )
        for ((name, targetLvl, status) in targets) {
            val unlocked = status?.unlocked ?: false
            val level = status?.level ?: 0
            val upgrading = status?.upgrading ?: false
            if (isUnmetForReport(unlocked, level, targetLvl)) {
                list.add(UnmetEntry(name, level, targetLvl, unlocked, upgrading))
            }
        }
        return list.sortedBy {
            mainBasePriorityOrder.indexOf(it.name).let { idx -> if (idx == -1) Int.MAX_VALUE else idx }
        }
    }

    /**
     * Collect builder base buildings and heroes that haven't reached their per-building target level.
     * Sorted: 战争机器 first, 战斗直升机 second, then by [builderBasePriorityOrder].
     * Un-built entries are included with [UnmetEntry.unlocked] = false. Upgrading entries are
     * shown with "升级中" suffix but are excluded from priority boosting.
     */
    fun collectBuilderBaseUnmet(): List<UnmetEntry> {
        val list = mutableListOf<UnmetEntry>()
        val targets = listOf(
            Triple("战争机器", BATTLE_MACHINE_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["战争机器"]),
            Triple("战斗直升机", BATTLE_COPTER_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["战斗直升机"]),
            Triple("多管迫击炮", BB_MULTI_MORTAR_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["多管迫击炮"]),
            Triple("箭塔", BB_ARCHER_TOWER_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["箭塔"]),
            Triple("双管加农炮", BB_DOUBLE_CANNON_TARGET_LEVEL, InGameVars.builderBaseSpeedSixStatus["双管加农炮"])
        )
        for ((name, targetLvl, status) in targets) {
            val unlocked = status?.unlocked ?: false
            val level = status?.level ?: 0
            val upgrading = status?.upgrading ?: false
            if (isUnmetForReport(unlocked, level, targetLvl)) {
                list.add(UnmetEntry(name, level, targetLvl, unlocked, upgrading))
            }
        }
        return list.sortedBy {
            builderBasePriorityOrder.indexOf(it.name).let { idx -> if (idx == -1) Int.MAX_VALUE else idx }
        }
    }

    /**
     * Build a single multi-line summary message that combines main base and builder base
     * un-met building/hero targets.
     *
     * Unlocked-but-below-target entries are shown as "name current/target".
     * Un-built (not present in JSON) entries are shown as "name 未解锁" so the user can
     * distinguish "need to upgrade" from "need to construct first".
     *
     * Example when both bases have unmet targets:
     *   速6农 未达标（主 2 项 + 夜 3 项）：
     *   [主] 迫击炮 7/8
     *   [主] 箭塔 9/10
     *   [夜] 战争机器 20/25
     *   [夜] 多管迫击炮 8/9
     *   [夜] 战斗直升机 未解锁
     *
     * Example when all targets met:
     *   速6农 全部达标（主 3/3 + 夜 5/5）
     */
    fun buildUnmetSummaryMessage(): String {
        val mainUnmet = collectMainBaseUnmet()
        val bbUnmet = collectBuilderBaseUnmet()
        val mainTotal = 3
        val bbTotal = 5
        val mainMet = mainTotal - mainUnmet.size
        val bbMet = bbTotal - bbUnmet.size

        if (mainUnmet.isEmpty() && bbUnmet.isEmpty()) {
            return "速6农 全部达标（主 $mainMet/$mainTotal + 夜 $bbMet/$bbTotal）"
        }

        val lines = mutableListOf<String>()
        lines.add("速6农 未达标（主 ${mainUnmet.size} 项 + 夜 ${bbUnmet.size} 项）：")
        mainUnmet.forEach { entry ->
            lines.add("[主] ${formatEntry(entry)}")
        }
        bbUnmet.forEach { entry ->
            lines.add("[夜] ${formatEntry(entry)}")
        }
        return lines.joinToString("\n")
    }

    private fun formatEntry(entry: UnmetEntry): String =
        if (!entry.unlocked) {
            "${entry.name} 未解锁"
        } else if (entry.upgrading) {
            "${entry.name} ${entry.currentLevel}/${entry.targetLevel} (升级中)"
        } else {
            "${entry.name} ${entry.currentLevel}/${entry.targetLevel}"
        }

    /**
     * Returns true when [name] was originally bumped (i.e. boosted) by
     * Speed 6 Builder mode for the given [baseType], AND removes the snapshot
     * entry so subsequent calls return false (one-shot).
     *
     * Spec: "after successfully upgrading one of the boosted targets, the
     * priority must be restored to the original priority." This is the hook
     * called by the upgrade flow right after a successful upgrade of [name].
     *
     * IMPORTANT: only the keys that Speed 6 Builder actually rewrote are
     * snapshotted. If a key already had a low priority before the boost (e.g.
     * the user configured it themselves), the snapshot is not taken and this
     * method returns false — so we never restore a priority the user did not
     * expect to be changed by us.
     */
    fun consumeSnapshotForKey(baseType: BaseType, name: String): Boolean {
        val snapshot = InGameVars.speedSixOriginalPriorities[baseType] ?: return false
        return snapshot.remove(name) != null
    }

    /**
     * Returns the originally snapshotted priority for [name] in [baseType],
     * or null if it was not boosted by Speed 6 Builder. Does NOT remove the
     * entry — use [consumeSnapshotForKey] for the one-shot behaviour.
     */
    fun peekSnapshotForKey(baseType: BaseType, name: String): Int? {
        return InGameVars.speedSixOriginalPriorities[baseType]?.get(name)
    }

    /**
     * Clears all snapshotted priorities for both base types. Should be called
     * at the start of each PreCheck cycle (i.e. before [getJsonData] reads the
     * next account) to make sure a previous account's data never bleeds into
     * the current account.
     */
    fun clearAllSnapshots() {
        InGameVars.speedSixOriginalPriorities[BaseType.Main]?.clear()
        InGameVars.speedSixOriginalPriorities[BaseType.Builder]?.clear()
    }
}
