package com.coc.zkqcode.jar.code.universal

/**
 * Represents the target game version/server type, carrying all package and launch metadata.
 * Use [fromId] to convert the integer stored in config to a [GameVersion].
 */
enum class GameVersion(val id: Int, val packageName: String, val launchComponent: String) {
    CN(
        0,
        "com.tencent.tmgp.supercell.clashofclans",
        "com.tencent.tmgp.supercell.clashofclans/com.supercell.titan.tencent.GameAppTencent"
    ),
    GLOBAL(
        1,
        "com.supercell.clashofclans",
        "com.supercell.clashofclans/com.supercell.titan.GameApp"
    ),
    PRIVATE(
        2,
        "com.atrasis.original",
        "com.atrasis.original/com.atrasis.main.AtrasisGameApp"
    );

    companion object {
        /** Convert the integer id stored in config to a [GameVersion]. */
        fun fromId(id: Int): GameVersion = entries.first { it.id == id }
    }
}

object InGamesVars {
    var currentAccountNumber: Int = 1
    var currentGameVersion: GameVersion = GameVersion.CN
}