package com.coc.zkqcode.jar.code.mainbase.research

/**
 * Maps each research item (troop / spell / siege machine) to its max level.
 * Keys use the Chinese names matching the name field in MainBaseResearchColors' ColorSchema.parse calls.
 *
 * Also maps each Chinese name to its game data ID so levels can be read from the exported JSON data
 * (arrays: units, spells, siege_machines) rather than via color-based badge detection.
 */
object MainBaseResearchMaxLevel {

    // ── Game data ID mapping (from game JSON export) ──
    // Keys: Chinese research name (same as in MainBaseResearchColors name fields).
    // Values: game data ID used in the exported JSON arrays (units, spells, siege_machines).
    private val researchDataIds = mapOf(
        // Troops
        "野蛮人" to 4000000,
        "弓箭手" to 4000001,
        "巨人" to 4000003,
        "哥布林" to 4000002,
        "炸弹人" to 4000004,
        "气球兵" to 4000005,
        "法师" to 4000006,
        "天使" to 4000007,
        "飞龙" to 4000008,
        "皮卡超人" to 4000009,
        "飞龙宝宝" to 4000023,
        "掘地矿工" to 4000024,
        "雷电飞龙" to 4000059,
        "大雪怪" to 4000053,
        "龙骑士" to 4000065,
        "雷霆泰坦" to 4000095,
        "根蔓骑士" to 4000110,
        "巨矛投手" to 4000132,
        "陨石戈仑" to 4000177,
        // Dark Elixir Troops
        "亡灵" to 4000010,
        "野猪骑士" to 4000011,
        "瓦基丽武神" to 4000012,
        "戈仑石人" to 4000013,
        "女巫" to 4000015,
        "熔岩猎犬" to 4000017,
        "巨石投手" to 4000022,
        "戈仑冰人" to 4000058,
        "英雄猎手" to 4000082,
        "守护者学徒" to 4000097,
        "德鲁伊" to 4000123,
        "烈焰熔炉" to 4000150,
        // Elixir Spells
        "雷电法术" to 26000000,
        "疗伤法术" to 26000001,
        "狂暴法术" to 26000002,
        "弹跳法术" to 26000003,
        "冰冻法术" to 26000005,
        "镜像法术" to 26000016,
        "隐形法术" to 26000035,
        "回溯法术" to 26000053,
        "复苏法术" to 26000098,
        "图腾法术" to 26000109,
        // Dark Spells
        "毒药法术" to 26000009,
        "地震法术" to 26000010,
        "急速法术" to 26000011,
        "骷髅法术" to 26000017,
        "蝙蝠法术" to 26000028,
        "蔓生法术" to 26000070,
        "冰障法术" to 26000120,
        // Siege Machines
        "攻城战车" to 4000051,
        "攻城飞艇" to 4000052,
        "攻城气球" to 4000062,
        "攻城训练营" to 4000075,
        "攻城滚木车" to 4000087,
        "攻城烈焰车" to 4000091,
        "攻城钻机" to 4000092,
        "部队发射器" to 4000135,
        "空中战车" to 4000188,
    )

    /** Returns the game data ID for a given research item name, or null if not found. */
    fun getDataId(name: String): Int? = researchDataIds[name]?.takeIf { it > 0 }

    /** All research item name → data ID mappings, for reverse-lookup by GetJsonData. */
    val allResearchDataIds: Map<String, Int> get() = researchDataIds

    // ── Max level mappings ──

    // Troops
    private val troopMaxLevels = mapOf(
        "野蛮人" to 12,
        "弓箭手" to 13,
        "巨人" to 14,
        "哥布林" to 9,
        "炸弹人" to 14,
        "气球兵" to 12,
        "法师" to 14,
        "天使" to 11,
        "飞龙" to 12,
        "皮卡超人" to 13,
        "飞龙宝宝" to 11,
        "掘地矿工" to 12,
        "雷电飞龙" to 9,
        "大雪怪" to 7,
        "龙骑士" to 6,
        "雷霆泰坦" to 4,
        "根蔓骑士" to 3,
        "巨矛投手" to 4,
        "陨石戈仑" to 3,
    )

    // Elixir Spells
    private val spellMaxLevels = mapOf(
        "雷电法术" to 13,
        "疗伤法术" to 12,
        "狂暴法术" to 6,
        "弹跳法术" to 5,
        "冰冻法术" to 7,
        "镜像法术" to 8,
        "隐形法术" to 4,
        "回溯法术" to 6,
        "复苏法术" to 4,
        "图腾法术" to 4,
    )

    // Dark Spells
    private val darkSpellMaxLevels = mapOf(
        "毒药法术" to 12,
        "地震法术" to 5,
        "急速法术" to 7,
        "骷髅法术" to 8,
        "蝙蝠法术" to 8,
        "蔓生法术" to 4,
        "冰障法术" to 5,
    )

    // Dark Elixir Troops
    private val darkTroopMaxLevels = mapOf(
        "亡灵" to 14,
        "野猪骑士" to 14,
        "瓦基丽武神" to 11,
        "戈仑石人" to 14,
        "女巫" to 7,
        "熔岩猎犬" to 7,
        "巨石投手" to 10,
        "戈仑冰人" to 9,
        "英雄猎手" to 3,
        "守护者学徒" to 4,
        "德鲁伊" to 5,
        "烈焰熔炉" to 4,
    )

    // Siege Machines
    private val siegeMaxLevels = mapOf(
        "攻城战车" to 6,
        "攻城飞艇" to 5,
        "攻城气球" to 5,
        "攻城训练营" to 5,
        "攻城滚木车" to 5,
        "攻城烈焰车" to 5,
        "攻城钻机" to 5,
        "部队发射器" to 4,
        "空中战车" to 4,
    )

    /** Combined map of all research items to their max levels. */
    val allMaxLevels: Map<String, Int> by lazy {
        troopMaxLevels + spellMaxLevels + darkSpellMaxLevels + darkTroopMaxLevels + siegeMaxLevels
    }

    /** Returns the max level for a given research item name, or null if not found. */
    fun getMaxLevel(name: String): Int? = allMaxLevels[name]
}
