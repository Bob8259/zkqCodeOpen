package com.coc.zkqcode.jar.code.universal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.buildings.BaseType
import java.util.concurrent.ConcurrentHashMap

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

data class BuildingStatus(
    val level: Int = 0,
    val unlocked: Boolean = false,
    val upgrading: Boolean = false
)

object InGameVars {
    @Volatile
    var currentAccountNumber: Int = 1

    @Volatile
    var currentGameVersion: GameVersion = GameVersion.CN

    @Volatile
    var qrLinkFlowActiveCount: Int = 0

    @Volatile
    var adTime: Int = 15

    // Set by checkRandomSchedule; read by RandomSchedulingLogic after stepBlock.
    @Volatile
    var isEarlyOffline: Boolean = false

    @Volatile
    var wallUpgradeNoWorkerBase: BaseType? = null

    // Bases of the CURRENT account whose walls are complete (both gold & elixir wall-upgrade
    // loops searched with sufficient resources and found no upgradable walls). Used by
    // checkContinueBuild to release the saved worker when "build after wall full" is enabled.
    // Cleared at the start of each runAccountStepBlock() so it never leaks across accounts.
    val wallsCompleteBases: MutableSet<BaseType> = ConcurrentHashMap.newKeySet()

    @Volatile
    var townHallLevel: Int = 20

    @Volatile
    var clockTowerCooldown: Int = 0

    // ── Pet upgrade level detection ──
    @Volatile
    var petHouseLevel: Int = 0   // 战宠小屋 (data ID 1000068) level, 0 = unknown

    // Map from pet key/ID (e.g. MainBasePets.LASSI.key) to its current level.
    @Volatile
    var petLevels: Map<String, Int> = emptyMap()



    // Compose-reactive auto-run timer (observed by UI for recomposition)
    var autoRunTimer by mutableIntStateOf(30)

    // ── Speed 6 Builder: target building levels parsed from JSON ──
    @Volatile
    var speedSixBuilderEnabled: Boolean = false

    // Speed 6 Builder structures status (level, unlocked, upgrading) keyed by building name/key.
    @Volatile
    var mainBaseSpeedSixStatus: Map<String, BuildingStatus> = emptyMap()

    @Volatile
    var builderBaseSpeedSixStatus: Map<String, BuildingStatus> = emptyMap()

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

    // ── Research item levels parsed from game JSON data ──
    // Map from Chinese research name (e.g. "野蛮人") to its current level.
    // Populated by GetJsonData.getJsonData() from the exported game JSON arrays (units, spells, siege_machines).
    // Used by MainBaseResearch to avoid color-based badge level detection.
    @Volatile
    var researchItemLevels: Map<String, Int> = emptyMap()
}

fun accountMessage(text: String, isChecking: Boolean = true) {
    ShowMessage("账号${InGameVars.currentAccountNumber}，$text", isChecking)
}
