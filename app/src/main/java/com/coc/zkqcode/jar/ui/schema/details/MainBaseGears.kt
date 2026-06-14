@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object MainBaseGears {
    // Keep the gear order aligned with the in-game upgrade list.

    // Barbarian King gears
    val BARBARIAN_KING_IRON_FIST = SettingDef("barbarian_king_iron_fist", "铁剑", "Iron Fist", 1, "MAIN_BASE_GEARS")
    val BARBARIAN_KING_PUPPET = SettingDef("barbarian_king_puppet", "野蛮人木偶", "Barbarian Puppet", 1, "MAIN_BASE_GEARS")
    val BARBARIAN_KING_RAGE_VIAL = SettingDef("barbarian_king_rage_vial", "狂暴药水瓶", "Rage Vial", 1, "MAIN_BASE_GEARS")
    val BARBARIAN_KING_EARTHQUAKE_BOOTS = SettingDef("barbarian_king_earthquake_boots", "地震金靴", "Earthquake Boots", 1, "MAIN_BASE_GEARS")
    val BARBARIAN_KING_HEALING_BEARD = SettingDef("barbarian_king_healing_beard", "治疗胡须", "Healing Beard", 1, "MAIN_BASE_GEARS")
    val BARBARIAN_KING_SPIKED_SOCCER = SettingDef("barbarian_king_spiked_soccer", "尖刺足球", "Spiked Soccer", 1, "MAIN_BASE_GEARS")

    // Archer Queen gears
    val ARCHER_QUEEN_CLOAK = SettingDef("archer_queen_cloak", "隐形披风", "Invisibility Cloak", 1, "MAIN_BASE_GEARS")
    val ARCHER_QUEEN_PUPPET = SettingDef("archer_queen_puppet", "弓箭手木偶", "Archer Puppet", 1, "MAIN_BASE_GEARS")
    val ARCHER_QUEEN_GIANT_ARROW = SettingDef("archer_queen_giant_arrow", "巨型箭矢", "Giant Arrow", 1, "MAIN_BASE_GEARS")
    val ARCHER_QUEEN_FROST_ARROW = SettingDef("archer_queen_frost_arrow", "冰冻箭", "Frost Arrow", 1, "MAIN_BASE_GEARS")
    val ARCHER_QUEEN_HEALER_PUPPET = SettingDef("archer_queen_healer_puppet", "天使木偶", "Healer Puppet", 1, "MAIN_BASE_GEARS")
    val ARCHER_QUEEN_CLONE_MIRROR = SettingDef("archer_queen_clone_mirror", "克隆魔镜", "Clone Mirror", 1, "MAIN_BASE_GEARS")

    // Grand Warden gears
    val GRAND_WARDEN_ETERNAL_TOME = SettingDef("grand_warden_eternal_tome", "永恒书卷", "Eternal Tome", 1, "MAIN_BASE_GEARS")
    val GRAND_WARDEN_LIFE_GEM = SettingDef("grand_warden_life_gem", "生命宝石", "Life Gem", 1, "MAIN_BASE_GEARS")
    val GRAND_WARDEN_HEALING_TOME = SettingDef("grand_warden_healing_tome", "治疗书卷", "Healing Tome", 1, "MAIN_BASE_GEARS")
    val GRAND_WARDEN_GIANT_FIREBALL = SettingDef("grand_warden_giant_fireball", "巨大火球", "Giant Fireball", 1, "MAIN_BASE_GEARS")
    val GRAND_WARDEN_RAGE_GEM = SettingDef("grand_warden_rage_gem", "狂暴宝石", "Rage Gem", 1, "MAIN_BASE_GEARS")

    // Royal Champion gears
    val ROYAL_CHAMPION_SEEKING_SHIELD = SettingDef("royal_champion_seeking_shield", "寻踪飞盾", "Seeking Shield", 1, "MAIN_BASE_GEARS")
    val ROYAL_CHAMPION_HOG_PUPPET = SettingDef("royal_champion_hog_puppet", "野猪骑士木偶", "Hog Rider Puppet", 1, "MAIN_BASE_GEARS")
    val ROYAL_CHAMPION_ROYAL_GEM = SettingDef("royal_champion_royal_gem", "皇家宝石", "Royal Gem", 1, "MAIN_BASE_GEARS")
    val ROYAL_CHAMPION_SPEED_VIAL = SettingDef("royal_champion_speed_vial", "急速药水瓶", "Speed Vial", 1, "MAIN_BASE_GEARS")

    // Dragon Duke gears
    val DRAGON_DUKE_FIRE_RING = SettingDef("dragon_duke_fire_ring", "烈焰之心", "Dragon Fire Ring", 1, "MAIN_BASE_GEARS")
    val DRAGON_DUKE_SCALE_ARMOR = SettingDef("dragon_duke_scale_armor", "爆震器", "Dragon Scale Armor", 1, "MAIN_BASE_GEARS")
    val DRAGON_DUKE_COMBUSTOR = SettingDef("dragon_duke_combustor", "助燃器", "Combustor", 1, "MAIN_BASE_GEARS")
    val DRAGON_DUKE_LIGHTNING_FANG = SettingDef("dragon_duke_lightning_fang", "雷电獠牙", "Lightning Fang", 1, "MAIN_BASE_GEARS")

    // Minion Prince gears
    val MINION_PRINCE_GUARDIAN_PUPPET = SettingDef("minion_prince_guardian_puppet", "护卫玩偶", "Guardian Puppet", 1, "MAIN_BASE_GEARS")
    val MINION_PRINCE_IRON_PANTS = SettingDef("minion_prince_iron_pants", "铁甲短裤", "Iron Pants", 1, "MAIN_BASE_GEARS")
    val MINION_PRINCE_NOBLE_DUMBBELL = SettingDef("minion_prince_noble_dumbbell", "贵族哑铃", "Noble Dumbbell", 1, "MAIN_BASE_GEARS")
    val MINION_PRINCE_DARK_ORB = SettingDef("minion_prince_dark_orb", "暗黑魔球", "Dark Orb", 1, "MAIN_BASE_GEARS")
    
    val all = listOf(
        BARBARIAN_KING_IRON_FIST,
        BARBARIAN_KING_PUPPET,
        BARBARIAN_KING_RAGE_VIAL,
        BARBARIAN_KING_EARTHQUAKE_BOOTS,
        BARBARIAN_KING_HEALING_BEARD,
        BARBARIAN_KING_SPIKED_SOCCER,
        ARCHER_QUEEN_CLOAK,
        ARCHER_QUEEN_PUPPET,
        ARCHER_QUEEN_GIANT_ARROW,
        ARCHER_QUEEN_FROST_ARROW,
        ARCHER_QUEEN_HEALER_PUPPET,
        ARCHER_QUEEN_CLONE_MIRROR,
        GRAND_WARDEN_ETERNAL_TOME,
        GRAND_WARDEN_LIFE_GEM,
        GRAND_WARDEN_HEALING_TOME,
        GRAND_WARDEN_GIANT_FIREBALL,
        GRAND_WARDEN_RAGE_GEM,
        ROYAL_CHAMPION_SEEKING_SHIELD,
        ROYAL_CHAMPION_HOG_PUPPET,
        ROYAL_CHAMPION_ROYAL_GEM,
        ROYAL_CHAMPION_SPEED_VIAL,
        DRAGON_DUKE_FIRE_RING,
        DRAGON_DUKE_SCALE_ARMOR,
        DRAGON_DUKE_COMBUSTOR,
        DRAGON_DUKE_LIGHTNING_FANG,
        MINION_PRINCE_GUARDIAN_PUPPET,
        MINION_PRINCE_IRON_PANTS,
        MINION_PRINCE_NOBLE_DUMBBELL,
        MINION_PRINCE_DARK_ORB
    )
    
    // Grouped by hero for UI rendering
    val barbarianKingGears = listOf(BARBARIAN_KING_IRON_FIST, BARBARIAN_KING_PUPPET, BARBARIAN_KING_RAGE_VIAL, BARBARIAN_KING_EARTHQUAKE_BOOTS, BARBARIAN_KING_HEALING_BEARD, BARBARIAN_KING_SPIKED_SOCCER)
    val archerQueenGears = listOf(ARCHER_QUEEN_CLOAK, ARCHER_QUEEN_PUPPET, ARCHER_QUEEN_GIANT_ARROW, ARCHER_QUEEN_FROST_ARROW, ARCHER_QUEEN_HEALER_PUPPET, ARCHER_QUEEN_CLONE_MIRROR)
    val grandWardenGears = listOf(GRAND_WARDEN_ETERNAL_TOME, GRAND_WARDEN_LIFE_GEM, GRAND_WARDEN_HEALING_TOME, GRAND_WARDEN_GIANT_FIREBALL, GRAND_WARDEN_RAGE_GEM)
    val royalChampionGears = listOf(ROYAL_CHAMPION_SEEKING_SHIELD, ROYAL_CHAMPION_HOG_PUPPET, ROYAL_CHAMPION_ROYAL_GEM, ROYAL_CHAMPION_SPEED_VIAL)
    val dragonDukeGears = listOf(DRAGON_DUKE_FIRE_RING, DRAGON_DUKE_SCALE_ARMOR, DRAGON_DUKE_COMBUSTOR, DRAGON_DUKE_LIGHTNING_FANG)
    val minionPrinceGears = listOf(MINION_PRINCE_GUARDIAN_PUPPET, MINION_PRINCE_IRON_PANTS, MINION_PRINCE_NOBLE_DUMBBELL, MINION_PRINCE_DARK_ORB)
    
    // Hero key -> gear list pairs for UI iteration
    val heroGroups = listOf(
        Pair("barbarian_king", barbarianKingGears),
        Pair("archer_queen", archerQueenGears),
        Pair("grand_warden", grandWardenGears),
        Pair("royal_champion", royalChampionGears),
        Pair("dragon_duke", dragonDukeGears),
        Pair("minion_prince", minionPrinceGears)
    )
}
