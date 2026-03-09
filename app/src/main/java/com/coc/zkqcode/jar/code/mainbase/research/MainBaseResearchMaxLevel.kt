package com.coc.zkqcode.jar.code.mainbase.research

/**
 * Maps each research item (troop / spell / siege machine) to its max level.
 * Keys use the Chinese names matching the name field in MainBaseResearchColors' ColorSchema.parse calls.
 */
object MainBaseResearchMaxLevel {

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
    )

    /** Combined map of all research items to their max levels. */
    val allMaxLevels: Map<String, Int> by lazy {
        troopMaxLevels + spellMaxLevels + darkSpellMaxLevels + darkTroopMaxLevels + siegeMaxLevels
    }

    /** Returns the max level for a given research item name, or null if not found. */
    fun getMaxLevel(name: String): Int? = allMaxLevels[name]
}
