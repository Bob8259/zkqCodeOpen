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
        2, "com.atrasis.live", "com.atrasis.live/com.atrasis.main.AtrasisGameApp", "", emptyList()
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

    // ── Pet upgrade level detection ──
    @Volatile
    var petHouseLevel: Int = 0   // 战宠小屋 (data ID 1000068) level, 0 = unknown

    @Volatile
    var lassiLevel: Int = 0      // 莱希 level (data ID 73000000)
    @Volatile
    var electroOwlLevel: Int = 0 // 闪枭 level (data ID 73000002)
    @Volatile
    var mightyYakLevel: Int = 0  // 大牦 level (data ID 73000001)
    @Volatile
    var unicornLevel: Int = 0    // 独角 level (data ID 73000003)
    @Volatile
    var frostyLevel: Int = 0     // 冰牙 level (data ID 73000009)

    // Pet levels by key (populated by pet upgrade scan), fallback for the individual fields above.
    val petLevels: MutableMap<String, Int> = mutableMapOf()

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
    // upgrading = true means at least one instance of this building is currently upgrading (timer > 0)
    // When upgrading is true, the building is NOT boosted by Speed 6 Builder priority (no need to queue
    // another upgrade of the same type when one is already in progress).
    @Volatile
    var mainArcherTowerLevel: Int = 0
    @Volatile
    var mainArcherTowerUnlocked: Boolean = false
    @Volatile
    var mainArcherTowerUpgrading: Boolean = false
    @Volatile
    var mainCannonLevel: Int = 0
    @Volatile
    var mainCannonUnlocked: Boolean = false
    @Volatile
    var mainCannonUpgrading: Boolean = false
    @Volatile
    var mainMortarLevel: Int = 0
    @Volatile
    var mainMortarUnlocked: Boolean = false
    @Volatile
    var mainMortarUpgrading: Boolean = false

    // Builder base target levels.
    // upgrading = true means this building/hero has a timer > 0 (already upgrading), so skip priority boost.
    @Volatile
    var builderArcherTowerLevel: Int = 0
    @Volatile
    var builderArcherTowerUnlocked: Boolean = false
    @Volatile
    var builderArcherTowerUpgrading: Boolean = false
    @Volatile
    var builderCannonLevel: Int = 0
    @Volatile
    var builderCannonUnlocked: Boolean = false
    @Volatile
    var builderCannonUpgrading: Boolean = false
    @Volatile
    var builderMultiMortarLevel: Int = 0
    @Volatile
    var builderMultiMortarUnlocked: Boolean = false
    @Volatile
    var builderMultiMortarUpgrading: Boolean = false
    @Volatile
    var battleMachineLevel: Int = 0
    @Volatile
    var battleMachineUnlocked: Boolean = false
    @Volatile
    var battleMachineUpgrading: Boolean = false
    @Volatile
    var battleCopterLevel: Int = 0
    @Volatile
    var battleCopterUnlocked: Boolean = false
    @Volatile
    var battleCopterUpgrading: Boolean = false

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
