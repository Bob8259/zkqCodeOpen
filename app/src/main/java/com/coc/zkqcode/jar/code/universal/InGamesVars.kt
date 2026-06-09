package com.coc.zkqcode.jar.code.universal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType

/**
 * Represents the target game version/server type, carrying all package and launch metadata.
 * Use [fromId] to convert the integer stored in config to a [GameVersion].
 */
enum class GameVersion(
    val id: Int, val packageName: String, val launchComponent: String, val folderName: String, val subDirs: List<String>
) {
    CN(
        0, "com.tencent.tmgp.supercell.clashofclans", "com.tencent.tmgp.supercell.clashofclans/com.supercell.titan.tencent.GameAppTencent", "zkqCNGameSave", listOf("shared_prefs", "databases")
    ),
    GLOBAL(
        1, "com.supercell.clashofclans", "com.supercell.clashofclans/com.supercell.titan.GameApp", "zkqGlobalGameSave", listOf("shared_prefs")
    ),
    PRIVATE(
        2, "com.atrasis.original", "com.atrasis.original/com.atrasis.main.AtrasisGameApp", "", emptyList()
    );

    companion object {
        /** Convert the integer id stored in config to a [GameVersion]. */
        fun fromId(id: Int): GameVersion = entries.first { it.id == id }
    }
}

object InGameVars {
    @Volatile
    var currentAccountNumber: Int = 1

    @Volatile
    var currentGameVersion: GameVersion = GameVersion.CN

    @Volatile
    var adTime: Int = 15

    // Set by checkRandomSchedule; read by RandomSchedulingLogic after stepBlock.
    @Volatile
    var isEarlyOffline: Boolean = false

    @Volatile
    var wallUpgradeNoWorkerBase: BaseType? = null

    @Volatile
    var townHallLevel: Int = 20

    @Volatile
    var clockTowerCooldown: Int = 0

    // True means there are upgradable or unbuilt walls; false means walls are complete (full quantity and all at max level).
    @Volatile
    var mainBaseWallsNeedUpgrade: Boolean = false

    // True means there are upgradable or unbuilt builder base walls; false means builder base walls are complete.
    @Volatile
    var builderBaseWallsNeedUpgrade: Boolean = false

    // Compose-reactive auto-run timer (observed by UI for recomposition)
    var autoRunTimer by mutableIntStateOf(30)

    // ── Speed 6 Builder: target building levels parsed from JSON ──
    @Volatile
    var speedSixBuilderEnabled: Boolean = false

    // Main base target levels (gear-up buildings need level 8)
    // unlocked = false means the building does not exist in the JSON (not yet built/unlocked)
    @Volatile
    var mainArcherTowerLevel: Int = 0
    @Volatile
    var mainArcherTowerUnlocked: Boolean = false
    @Volatile
    var mainCannonLevel: Int = 0
    @Volatile
    var mainCannonUnlocked: Boolean = false
    @Volatile
    var mainMortarLevel: Int = 0
    @Volatile
    var mainMortarUnlocked: Boolean = false

    // Builder base target levels
    @Volatile
    var builderArcherTowerLevel: Int = 0
    @Volatile
    var builderArcherTowerUnlocked: Boolean = false
    @Volatile
    var builderCannonLevel: Int = 0
    @Volatile
    var builderCannonUnlocked: Boolean = false
    @Volatile
    var builderMultiMortarLevel: Int = 0
    @Volatile
    var builderMultiMortarUnlocked: Boolean = false
    @Volatile
    var battleMachineLevel: Int = 0
    @Volatile
    var battleMachineUnlocked: Boolean = false
    @Volatile
    var battleCopterLevel: Int = 0
    @Volatile
    var battleCopterUnlocked: Boolean = false

    // Speed 6 Builder: troop choice for level 18 target (暗夜女巫 or 狂暴野蛮人)
    @Volatile
    var speedSixTroopChoice: String = "狂暴野蛮人"
    @Volatile
    var speedSixTroopLevel: Int = 0

    // Speed 6 Builder: snapshot of the user's ORIGINAL priorities (key -> original value)
    // for the keys that Speed 6 Builder mode has boosted this PreCheck cycle.
    // Keys appear here only when SpeedSixBuilderPriority.applyXxxOverride actually rewrote them.
    // They are removed when the corresponding building is successfully upgraded
    // (per spec: "restore the priority after each successful upgrade").
    // Map is keyed by base type (Main / Builder) so the two bases don't collide.
    val speedSixOriginalPriorities: MutableMap<BaseType, MutableMap<String, Int>> = mutableMapOf(
        BaseType.Main to mutableMapOf(),
        BaseType.Builder to mutableMapOf()
    )
}

fun accountMessage(text: String, isChecking: Boolean = true) {
    ShowMessage("账号${InGameVars.currentAccountNumber}，$text", isChecking)
}
