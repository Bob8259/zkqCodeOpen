package com.coc.zkqcode.jar.code.universal.smalltools.data

import com.coc.zkqcode.core.system.inputmethod.ZKQInputMethodService
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.jar.code.universal.clickRightBottom
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.code.universal.smalltools.input.setZKQInputMethod
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigRuntime
import com.coc.zkqcode.jar.code.universal.buildings.upgrade.SpeedSixBuilderPriority
import com.coc.zkqcode.jar.ui.schema.Schema
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

// ── Speed 6 Builder: building data IDs in the game JSON ──
// Main base buildings (in "buildings" array)
private const val MAIN_ARCHER_TOWER_DATA_ID = 1000009  // 箭塔
private const val MAIN_CANNON_DATA_ID = 1000008        // 加农炮
private const val MAIN_MORTAR_DATA_ID = 1000013         // 迫击炮
// Builder base buildings (in "buildings2" array)
private const val BB_ARCHER_TOWER_DATA_ID = 1000048    // 箭塔(夜)
private const val BB_CANNON_DATA_ID = 1000041          // 双管加农炮(夜) - for gear-up
private const val BB_MULTI_MORTAR_DATA_ID = 1000045    // 多管迫击炮(夜)
// Builder base heroes (in "heroes2" array, NOT "heroes")
private const val BB_BATTLE_MACHINE_DATA_ID = 28000003 // 战争机器
private const val BB_BATTLE_COPTER_DATA_ID = 28000005  // 战斗直升机
// Pet house building for level detection
private const val PET_HOUSE_DATA_ID = 1000068   // 战宠小屋

// Pet data IDs for level detection (the 5 basic pets)
private const val LASSI_DATA_ID = 73000000       // 莱希
private const val MIGHTY_YAK_DATA_ID = 73000001  // 大牦
private const val ELECTRO_OWL_DATA_ID = 73000002 // 闪枭
private const val UNICORN_DATA_ID = 73000003     // 独角
private const val FROSTY_DATA_ID = 73000009      // 冰牙

// Builder base troops (in "units2" array) — for Speed 6 Builder level 18 target
private const val BB_NIGHT_WITCH_DATA_ID = 4000042       // 暗夜女巫
private const val BB_RAGED_BARBARIAN_DATA_ID = 4000031   // 狂暴野蛮人

    suspend fun getJsonData(): Boolean {
        // Check Speed 6 Builder toggle
        InGameVars.speedSixBuilderEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SPEED_SIX_BUILDER.key)
        // Clear any leftover snapshots from a previous account so a stale "original"
        // priority never bleeds into the current account's priority boost.
        if (InGameVars.speedSixBuilderEnabled) {
            SpeedSixBuilderPriority.clearAllSnapshots()
        }
    if (!getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SMART_DECISION.key) && !InGameVars.speedSixBuilderEnabled) return true
    accountMessage("准备读取账号数据，请稍后")
    if (InGameVars.currentGameVersion != GameVersion.CN) {
        TouchActions.tap(1230, 520)//settings
        TouchActions.tap(630, 610)//more settings
        TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
        TouchActions.tap(910, 450)//copy
    } else {
        TouchActions.tap(1230, 450)//arrow
        val settings = findMultiColorsUntil(listOf(MyColors.GameSettings), 1000)
        if (settings != null) {
            TouchActions.tap(settings.x, settings.y)
            TouchActions.tap(630, 610)//more settings
            TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
            TouchActions.tap(900, 300)//copy
        }
    }

    // Wait for clipboard to be populated after copy action
    delayWithMultiplier(500)

    // Read clipboard content via IME service (only IME has clipboard access from background)
    val imeService = ZKQInputMethodService.instance
    if (imeService == null) {
        // IME not bound — try to rebind and retry
        setZKQInputMethod()
    }
    val clipboardText = (ZKQInputMethodService.instance ?: logAndRestart("ZKQInputMethodService instance is null after rebind attempt")).readClipboard()
    if (clipboardText.isNullOrBlank()) {
        // Clipboard is empty — skip updating townHallLevel
        accountMessage("剪贴板为空，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }

    // Parse JSON and extract first building's lvl as townHallLevel
    val clipTrimmed = clipboardText.trim()
    if (!clipTrimmed.startsWith("{")) {
        accountMessage("剪贴板内容不是JSON对象，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val jsonObject = try {
        Gson().fromJson(clipTrimmed, JsonObject::class.java)
    } catch (e: JsonSyntaxException) {
        accountMessage("JSON解析失败: ${e.message}，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    if (jsonObject == null) {
        accountMessage("剪贴板内容解析为null，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val buildings = jsonObject.getAsJsonArray("buildings")
    if (buildings == null || buildings.size() == 0) {
        accountMessage("JSON中未找到buildings数据，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    // Townhall is identified by data == 1000001
    val townHallBuilding = buildings.firstOrNull {
        it.asJsonObject.get("data").asInt == 1000001
    }
    if (townHallBuilding == null) {
        accountMessage("JSON中未找到大本营(1000001)数据，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val firstBuildingLvl = try {
        townHallBuilding.asJsonObject.get("lvl").asInt
    } catch (_: Exception) {
        accountMessage("无法从大本营数据中读取lvl字段，跳过大本营等级检测")
        clickRightBottom(1)
        return enterMainScreen()
    }
    val targetLevel = getConfigRuntime(Schema.MAIN_BASE_SETTINGS.SKIP_TH_LEVEL.key).toIntOrNull() ?: logAndRestart("${Schema.MAIN_BASE_SETTINGS.SKIP_TH_LEVEL.displayName} 必须是数字，请检查配置")
    val thLevelInfo = "大本营等级: $firstBuildingLvl\n设定目标等级：$targetLevel"
    if (firstBuildingLvl >= targetLevel) {
        accountMessage(thLevelInfo)
        return false//force switch account
    }
    InGameVars.townHallLevel = firstBuildingLvl

    // Parse pet house and pet levels from the same JSON data (available even without Smart Decision)
    parsePetLevels(jsonObject)

    // Build account message based on enabled features, separated by blank line
    val smartDecisionEnabled = getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.SMART_DECISION.key)
    val messageParts = mutableListOf<String>()

    if (smartDecisionEnabled) {
        messageParts.add(thLevelInfo)
    }

    if (InGameVars.speedSixBuilderEnabled) {
        parseSpeedSixBuilderLevels(jsonObject)
        val summary = SpeedSixBuilderPriority.buildUnmetSummaryMessage()
        messageParts.add(summary)
    }

    if (messageParts.isNotEmpty()) {
        accountMessage(messageParts.joinToString("\n\n"))
    }

    // Parse clock tower cooldown from boosts (may not exist)
    InGameVars.clockTowerCooldown = try {
        val boosts = jsonObject.getAsJsonObject("boosts")
        boosts?.get("clocktower_cooldown")?.asInt ?: 0
    } catch (_: Exception) {
        0
    }

    // Determine whether the main base still has upgradable or unbuilt walls (data == 1000010 in "buildings").
    // Walls are considered complete (false) only when the built wall count equals the town hall's wall quantity
    // and every wall is already at the town hall's max wall level.
    InGameVars.mainBaseWallsNeedUpgrade = try {
        val (wallQuantity, maxWallLevel) = wallRequirementForTownHall(firstBuildingLvl)
        val wallEntries = buildings.filter { it.asJsonObject.get("data").asInt == 1000010 }
        val builtWallCount = wallEntries.sumOf { it.asJsonObject.get("cnt")?.asInt ?: 1 }
        val allAtMaxLevel = wallEntries.all { it.asJsonObject.get("lvl").asInt >= maxWallLevel }
        val isComplete = builtWallCount >= wallQuantity && allAtMaxLevel
        // Town halls with no walls (quantity 0) are always complete.
        wallQuantity > 0 && !isComplete
    } catch (_: Exception) {
        false
    }

    // Determine whether the builder base still has upgradable or unbuilt walls (data == 1000033 in "buildings2").
    InGameVars.builderBaseWallsNeedUpgrade = try {
        val buildings2 = jsonObject.getAsJsonArray("buildings2")
        if (buildings2 != null && buildings2.size() > 0) {
            // Builder hall is identified by data == 1000046 in "buildings2"
            val builderHallLevel = buildings2.firstOrNull {
                it.asJsonObject.get("data").asInt == 1000046
            }?.asJsonObject?.get("lvl")?.asInt ?: 0
            val (wallQuantity, maxWallLevel) = wallRequirementForBuilderHall(builderHallLevel)
            val wallEntries = buildings2.filter { it.asJsonObject.get("data").asInt == 1000033 }
            val builtWallCount = wallEntries.sumOf { it.asJsonObject.get("cnt")?.asInt ?: 1 }
            val allAtMaxLevel = wallEntries.all { it.asJsonObject.get("lvl").asInt >= maxWallLevel }
            val isComplete = builtWallCount >= wallQuantity && allAtMaxLevel
            wallQuantity > 0 && !isComplete
        } else {
            false
        }
    } catch (_: Exception) {
        false
    }

    clickRightBottom(1, delayTime = 200)
    return enterMainScreen()
}

/**
 * Wall quantity (number of walls) and max wall level for a given town hall level.
 * Returns Pair(wallQuantity, maxWallLevel). Unknown levels fall back to (0, 0).
 */
private fun wallRequirementForTownHall(townHallLevel: Int): Pair<Int, Int> = when (townHallLevel) {
    1 -> 0 to 0
    2 -> 25 to 2
    3 -> 50 to 3
    4 -> 75 to 4
    5 -> 100 to 5
    6 -> 125 to 6
    7 -> 175 to 7
    8 -> 225 to 8
    9 -> 250 to 10
    10 -> 275 to 11
    11 -> 300 to 12
    12 -> 300 to 13
    13 -> 300 to 14
    14 -> 325 to 15
    15 -> 325 to 16
    16 -> 325 to 17
    17 -> 325 to 18
    18 -> 325 to 19
    else -> 0 to 0
}

/**
 * Parse pet house level and the 5 basic pet levels from the game JSON.
 * This data is used by [checkPets] to decide whether a pet at level 10+
 * should be excluded from the upgrade queue when the pet house is ≤ level 5.
 *
 * Pet house (战宠小屋) has data ID 1000068 in the "buildings" array.
 * Pets have data IDs 73000000-73000009 in the "pets" array.
 */
private fun parsePetLevels(jsonObject: JsonObject) {
    // Parse pet house level from "buildings" array
    try {
        val buildings = jsonObject.getAsJsonArray("buildings")
        if (buildings != null) {
            val petHouse = buildings.firstOrNull {
                it.asJsonObject.get("data").asInt == PET_HOUSE_DATA_ID
            }?.asJsonObject
            if (petHouse != null) {
                InGameVars.petHouseLevel = petHouse.get("lvl")?.asInt ?: 0
            }
        }
    } catch (_: Exception) { /* ignore */ }

    // Parse pet levels from "pets" array
    try {
        val pets = jsonObject.getAsJsonArray("pets")
        if (pets != null) {
            // Helper: extract level for a given pet data ID
            fun extractPetLevel(dataId: Int): Int {
                return pets.firstOrNull {
                    it.asJsonObject.get("data").asInt == dataId
                }?.asJsonObject?.get("lvl")?.asInt ?: 0
            }
            InGameVars.lassiLevel = extractPetLevel(LASSI_DATA_ID)
            InGameVars.mightyYakLevel = extractPetLevel(MIGHTY_YAK_DATA_ID)
            InGameVars.electroOwlLevel = extractPetLevel(ELECTRO_OWL_DATA_ID)
            InGameVars.unicornLevel = extractPetLevel(UNICORN_DATA_ID)
            InGameVars.frostyLevel = extractPetLevel(FROSTY_DATA_ID)
        }
    } catch (_: Exception) { /* ignore */ }
}

/**
 * Parse building levels needed for Speed 6 Builder priority calculation.
 * Reads from the "buildings" and "buildings2" (and optionally "heroes") arrays.
 */
private fun parseSpeedSixBuilderLevels(jsonObject: JsonObject) {
    // Parse main base buildings. Detect timer > 0 to know if an instance is already upgrading.
    // When a building already has an instance upgrading, we skip the priority boost for that
    // building type so the upgrade list won't try to queue another one of the same type.
    try {
        val buildings = jsonObject.getAsJsonArray("buildings")
        if (buildings != null) {
            fun extractLevelsAndTimer(dataId: Int): Pair<List<Int>, Boolean> {
                val entries = buildings.filter { it.asJsonObject.get("data").asInt == dataId }
                val levels = entries.mapNotNull { it.asJsonObject.get("lvl")?.asInt }
                val hasUpgrading = entries.any {
                    try { it.asJsonObject.get("timer")?.asInt ?: 0 > 0 } catch (_: Exception) { false }
                }
                return levels to hasUpgrading
            }
            if (MAIN_ARCHER_TOWER_DATA_ID != 0) {
                val (levels, hasUpgrading) = extractLevelsAndTimer(MAIN_ARCHER_TOWER_DATA_ID)
                if (levels.isNotEmpty()) {
                    InGameVars.mainArcherTowerUnlocked = true
                    InGameVars.mainArcherTowerLevel = levels.max()
                    InGameVars.mainArcherTowerUpgrading = hasUpgrading
                }
            }
            if (MAIN_CANNON_DATA_ID != 0) {
                val (levels, hasUpgrading) = extractLevelsAndTimer(MAIN_CANNON_DATA_ID)
                if (levels.isNotEmpty()) {
                    InGameVars.mainCannonUnlocked = true
                    InGameVars.mainCannonLevel = levels.max()
                    InGameVars.mainCannonUpgrading = hasUpgrading
                }
            }
            if (MAIN_MORTAR_DATA_ID != 0) {
                val (levels, hasUpgrading) = extractLevelsAndTimer(MAIN_MORTAR_DATA_ID)
                if (levels.isNotEmpty()) {
                    InGameVars.mainMortarUnlocked = true
                    InGameVars.mainMortarLevel = levels.max()
                    InGameVars.mainMortarUpgrading = hasUpgrading
                }
            }
        }
    } catch (_: Exception) { /* ignore */ }

    // Parse builder base buildings
    try {
        val buildings2 = jsonObject.getAsJsonArray("buildings2")
        if (buildings2 != null) {
            fun extractLevelsAndTimer2(dataId: Int): Pair<List<Int>, Boolean> {
                val entries = buildings2.filter { it.asJsonObject.get("data").asInt == dataId }
                val levels = entries.mapNotNull { it.asJsonObject.get("lvl")?.asInt }
                val hasUpgrading = entries.any {
                    try { it.asJsonObject.get("timer")?.asInt ?: 0 > 0 } catch (_: Exception) { false }
                }
                return levels to hasUpgrading
            }
            if (BB_ARCHER_TOWER_DATA_ID != 0) {
                val (levels, hasUpgrading) = extractLevelsAndTimer2(BB_ARCHER_TOWER_DATA_ID)
                if (levels.isNotEmpty()) {
                    InGameVars.builderArcherTowerUnlocked = true
                    InGameVars.builderArcherTowerLevel = levels.max()
                    InGameVars.builderArcherTowerUpgrading = hasUpgrading
                }
            }
            if (BB_CANNON_DATA_ID != 0) {
                val (levels, hasUpgrading) = extractLevelsAndTimer2(BB_CANNON_DATA_ID)
                if (levels.isNotEmpty()) {
                    InGameVars.builderCannonUnlocked = true
                    InGameVars.builderCannonLevel = levels.max()
                    InGameVars.builderCannonUpgrading = hasUpgrading
                }
            }
            if (BB_MULTI_MORTAR_DATA_ID != 0) {
                val (levels, hasUpgrading) = extractLevelsAndTimer2(BB_MULTI_MORTAR_DATA_ID)
                if (levels.isNotEmpty()) {
                    InGameVars.builderMultiMortarUnlocked = true
                    InGameVars.builderMultiMortarLevel = levels.max()
                    InGameVars.builderMultiMortarUpgrading = hasUpgrading
                }
            }
        }
    } catch (_: Exception) { /* ignore */ }

    // Parse builder base heroes (Battle Machine, Battle Copter) — they are in "heroes2", NOT "heroes"
    try {
        val heroes2 = jsonObject.getAsJsonArray("heroes2")
        if (heroes2 != null) {
            if (BB_BATTLE_MACHINE_DATA_ID != 0) {
                val entry = heroes2
                    .firstOrNull { it.asJsonObject.get("data").asInt == BB_BATTLE_MACHINE_DATA_ID }
                    ?.asJsonObject
                if (entry != null) {
                    InGameVars.battleMachineUnlocked = true
                    InGameVars.battleMachineLevel = entry.get("lvl")?.asInt ?: 0
                    InGameVars.battleMachineUpgrading = try { entry.get("timer")?.asInt ?: 0 > 0 } catch (_: Exception) { false }
                }
            }
            if (BB_BATTLE_COPTER_DATA_ID != 0) {
                val entry = heroes2
                    .firstOrNull { it.asJsonObject.get("data").asInt == BB_BATTLE_COPTER_DATA_ID }
                    ?.asJsonObject
                if (entry != null) {
                    InGameVars.battleCopterUnlocked = true
                    InGameVars.battleCopterLevel = entry.get("lvl")?.asInt ?: 0
                    InGameVars.battleCopterUpgrading = try { entry.get("timer")?.asInt ?: 0 > 0 } catch (_: Exception) { false }
                }
            }
        }
    } catch (_: Exception) { /* ignore */ }

    // Parse builder base troop levels for Speed 6 Builder level-18 target
    // Use Night Witch (暗夜女巫) if unlocked, otherwise fall back to Raged Barbarian (狂暴野蛮人)
    try {
        val units2 = jsonObject.getAsJsonArray("units2")
        if (units2 != null) {
            val nightWitch = units2.firstOrNull {
                it.asJsonObject.get("data").asInt == BB_NIGHT_WITCH_DATA_ID
            }
            if (nightWitch != null) {
                InGameVars.speedSixTroopChoice = "暗夜女巫"
                InGameVars.speedSixTroopLevel = nightWitch.asJsonObject.get("lvl")?.asInt ?: 0
            } else {
                InGameVars.speedSixTroopChoice = "狂暴野蛮人"
                val barbarian = units2.firstOrNull {
                    it.asJsonObject.get("data").asInt == BB_RAGED_BARBARIAN_DATA_ID
                }
                InGameVars.speedSixTroopLevel = barbarian?.asJsonObject?.get("lvl")?.asInt ?: 0
            }
        }
    } catch (_: Exception) { /* ignore */ }
}

/**
 * Wall quantity (number of walls) and max wall level for a given builder hall level.
 * Returns Pair(wallQuantity, maxWallLevel). Unknown levels fall back to (0, 0).
 */
private fun wallRequirementForBuilderHall(builderHallLevel: Int): Pair<Int, Int> = when (builderHallLevel) {
    1 -> 10 to 1
    2 -> 20 to 1
    3 -> 50 to 3
    4 -> 75 to 4
    5 -> 100 to 5
    6 -> 120 to 6
    7 -> 140 to 7
    8 -> 160 to 8
    9 -> 180 to 9
    10 -> 180 to 10
    else -> 0 to 0
}
