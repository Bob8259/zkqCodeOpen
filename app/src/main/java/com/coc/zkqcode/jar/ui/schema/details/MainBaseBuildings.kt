@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object MainBaseBuildings {
    //以下是建造设置
    val TOWN_HALL = SettingDef("town_hall", "大本营", 1, "MAIN_BASE_BUILDINGS")
    val GIGA_TESLA = SettingDef("giga_tesla", "巨型特斯拉", 1, "MAIN_BASE_BUILDINGS")
    val GIGA_INFERNO = SettingDef("giga_inferno", "巨型地狱塔", 1, "MAIN_BASE_BUILDINGS")
    val HERO_ALTAR = SettingDef("hero_altar", "英雄殿堂", 1, "MAIN_BASE_BUILDINGS")
    val LABORATORY = SettingDef("laboratory", "实验室", 1, "MAIN_BASE_BUILDINGS")
    val GOLD_STORAGE = SettingDef("gold_storage", "储金罐", 1, "MAIN_BASE_BUILDINGS")
    val ELIXIR_STORAGE = SettingDef("elixir_storage", "圣水瓶", 1, "MAIN_BASE_BUILDINGS")
    val BARRACKS = SettingDef("barracks", "训练营", 1, "MAIN_BASE_BUILDINGS")
    val SPELL_FACTORY = SettingDef("spell_factory", "法术工厂", 1, "MAIN_BASE_BUILDINGS")
    val BLACKSMITH = SettingDef("blacksmith", "铁匠铺", 1, "MAIN_BASE_BUILDINGS")
    val PET_HOUSE = SettingDef("pet_house", "战宠小屋", 1, "MAIN_BASE_BUILDINGS")
    val ARMY_CAMP = SettingDef("army_camp", "兵营", 1, "MAIN_BASE_BUILDINGS")
    val BARBARIAN_KING = SettingDef("barbarian_king", "蛮王", 1, "MAIN_BASE_BUILDINGS")
    val ARCHER_QUEEN = SettingDef("archer_queen", "女王", 1, "MAIN_BASE_BUILDINGS")
    val MINION_PRINCE = SettingDef("minion_prince", "王子", 1, "MAIN_BASE_BUILDINGS")
    val GUARDIAN = SettingDef("guardian", "守护者", 1, "MAIN_BASE_BUILDINGS")
    val ROYAL_CHAMPION = SettingDef("royal_champion", "飞盾", 1, "MAIN_BASE_BUILDINGS")
    val CLAN_CASTLE = SettingDef("clan_castle", "部落城堡", 1, "MAIN_BASE_BUILDINGS")
    val SIEGE_WORKSHOP = SettingDef("siege_workshop", "攻城机器工坊", 1, "MAIN_BASE_BUILDINGS")
    val ARCHER_TOWER = SettingDef("archer_tower", "箭塔", 1, "MAIN_BASE_BUILDINGS")
    val CANNON = SettingDef("cannon", "加农炮", 1, "MAIN_BASE_BUILDINGS")
    val MORTAR = SettingDef("mortar", "迫击炮", 1, "MAIN_BASE_BUILDINGS")
    val EAGLE_ARTILLERY = SettingDef("eagle_artillery", "天鹰火炮", 1, "MAIN_BASE_BUILDINGS")
    val DARK_ELIXIR_STORAGE = SettingDef("dark_elixir_storage", "黑油罐", 1, "MAIN_BASE_BUILDINGS")
    val XBOW = SettingDef("xbow", "十字连弩", 1, "MAIN_BASE_BUILDINGS")
    val SCATTERSHOT = SettingDef("scattershot", "投石炮", 1, "MAIN_BASE_BUILDINGS")
    val SPELL_TOWER = SettingDef("spell_tower", "法术塔", 1, "MAIN_BASE_BUILDINGS")
    val MONOLITH = SettingDef("monolith", "擎天巨柱", 1, "MAIN_BASE_BUILDINGS")
    val WIZARD_TOWER = SettingDef("wizard_tower", "法师塔", 1, "MAIN_BASE_BUILDINGS")

    val all = listOf(
        TOWN_HALL,
        GIGA_TESLA,
        GIGA_INFERNO,
        HERO_ALTAR,
        LABORATORY,
        GOLD_STORAGE,
        ELIXIR_STORAGE,
        BARRACKS,
        SPELL_FACTORY,
        BLACKSMITH,
        PET_HOUSE,
        ARMY_CAMP,
        BARBARIAN_KING,
        ARCHER_QUEEN,
        MINION_PRINCE,
        GUARDIAN,
        ROYAL_CHAMPION,
        CLAN_CASTLE,
        SIEGE_WORKSHOP,
        ARCHER_TOWER,
        CANNON,
        MORTAR,
        EAGLE_ARTILLERY,
        DARK_ELIXIR_STORAGE,
        XBOW,
        SCATTERSHOT,
        SPELL_TOWER,
        MONOLITH,
        WIZARD_TOWER
    )
}

object MainBaseBuildingPriorities {
    //以下是建造设置
    val TOWN_HALL_PRIORITY = SettingDef("town_hall_priority", "大本营", 1, "MAIN_BASE_BUILDING_PRIORITIES")
    val GIGA_TESLA_PRIORITY = SettingDef("giga_tesla_priority", "巨型特斯拉", 2, "MAIN_BASE_BUILDING_PRIORITIES")
    val GIGA_INFERNO_PRIORITY =
        SettingDef("giga_inferno_priority", "巨型地狱塔", 3, "MAIN_BASE_BUILDING_PRIORITIES")
    val HERO_ALTAR_PRIORITY = SettingDef("hero_altar_priority", "英雄殿堂", 4, "MAIN_BASE_BUILDING_PRIORITIES")
    val LABORATORY_PRIORITY = SettingDef("laboratory_priority", "实验室", 5, "MAIN_BASE_BUILDING_PRIORITIES")
    val GOLD_STORAGE_PRIORITY = SettingDef("gold_storage_priority", "储金罐", 6, "MAIN_BASE_BUILDING_PRIORITIES")
    val ELIXIR_STORAGE_PRIORITY =
        SettingDef("elixir_storage_priority", "圣水瓶", 7, "MAIN_BASE_BUILDING_PRIORITIES")
    val BARRACKS_PRIORITY = SettingDef("barracks_priority", "训练营", 8, "MAIN_BASE_BUILDING_PRIORITIES")
    val SPELL_FACTORY_PRIORITY =
        SettingDef("spell_factory_priority", "法术工厂", 9, "MAIN_BASE_BUILDING_PRIORITIES")
    val BLACKSMITH_PRIORITY = SettingDef("blacksmith_priority", "铁匠铺", 10, "MAIN_BASE_BUILDING_PRIORITIES")
    val PET_HOUSE_PRIORITY = SettingDef("pet_house_priority", "战宠小屋", 11, "MAIN_BASE_BUILDING_PRIORITIES")
    val ARMY_CAMP_PRIORITY = SettingDef("army_camp_priority", "兵营", 12, "MAIN_BASE_BUILDING_PRIORITIES")
    val BARBARIAN_KING_PRIORITY = SettingDef("barbarian_king_priority", "蛮王", 13, "MAIN_BASE_BUILDING_PRIORITIES")
    val ARCHER_QUEEN_PRIORITY = SettingDef("archer_queen_priority", "女王", 14, "MAIN_BASE_BUILDING_PRIORITIES")
    val MINION_PRINCE_PRIORITY = SettingDef("minion_prince_priority", "王子", 15, "MAIN_BASE_BUILDING_PRIORITIES")
    val GUARDIAN_PRIORITY = SettingDef("guardian_priority", "守护者", 16, "MAIN_BASE_BUILDING_PRIORITIES")
    val ROYAL_CHAMPION_PRIORITY = SettingDef("royal_champion_priority", "飞盾", 17, "MAIN_BASE_BUILDING_PRIORITIES")
    val CLAN_CASTLE_PRIORITY = SettingDef("clan_castle_priority", "部落城堡", 18, "MAIN_BASE_BUILDING_PRIORITIES")
    val SIEGE_WORKSHOP_PRIORITY =
        SettingDef("siege_workshop_priority", "攻城机器工坊", 19, "MAIN_BASE_BUILDING_PRIORITIES")
    val ARCHER_TOWER_PRIORITY = SettingDef("archer_tower_priority", "箭塔", 20, "MAIN_BASE_BUILDING_PRIORITIES")
    val CANNON_PRIORITY = SettingDef("cannon_priority", "加农炮", 21, "MAIN_BASE_BUILDING_PRIORITIES")
    val MORTAR_PRIORITY = SettingDef("mortar_priority", "迫击炮", 22, "MAIN_BASE_BUILDING_PRIORITIES")
    val EAGLE_ARTILLERY_PRIORITY =
        SettingDef("eagle_artillery_priority", "天鹰火炮", 23, "MAIN_BASE_BUILDING_PRIORITIES")
    val DARK_ELIXIR_STORAGE_PRIORITY =
        SettingDef("dark_elixir_storage_priority", "黑油罐", 24, "MAIN_BASE_BUILDING_PRIORITIES")
    val XBOW_PRIORITY = SettingDef("xbow_priority", "十字连弩", 25, "MAIN_BASE_BUILDING_PRIORITIES")
    val SCATTERSHOT_PRIORITY = SettingDef("scattershot_priority", "投石炮", 26, "MAIN_BASE_BUILDING_PRIORITIES")
    val SPELL_TOWER_PRIORITY = SettingDef("spell_tower_priority", "法术塔", 27, "MAIN_BASE_BUILDING_PRIORITIES")
    val MONOLITH_PRIORITY = SettingDef("monolith_priority", "擎天巨柱", 28, "MAIN_BASE_BUILDING_PRIORITIES")
    val WIZARD_TOWER_PRIORITY = SettingDef("wizard_tower_priority", "法师塔", 29, "MAIN_BASE_BUILDING_PRIORITIES")

    val all = listOf(
        TOWN_HALL_PRIORITY,
        GIGA_TESLA_PRIORITY,
        GIGA_INFERNO_PRIORITY,
        HERO_ALTAR_PRIORITY,
        LABORATORY_PRIORITY,
        GOLD_STORAGE_PRIORITY,
        ELIXIR_STORAGE_PRIORITY,
        BARRACKS_PRIORITY,
        SPELL_FACTORY_PRIORITY,
        BLACKSMITH_PRIORITY,
        PET_HOUSE_PRIORITY,
        ARMY_CAMP_PRIORITY,
        BARBARIAN_KING_PRIORITY,
        ARCHER_QUEEN_PRIORITY,
        MINION_PRINCE_PRIORITY,
        GUARDIAN_PRIORITY,
        ROYAL_CHAMPION_PRIORITY,
        CLAN_CASTLE_PRIORITY,
        SIEGE_WORKSHOP_PRIORITY,
        ARCHER_TOWER_PRIORITY,
        CANNON_PRIORITY,
        MORTAR_PRIORITY,
        EAGLE_ARTILLERY_PRIORITY,
        DARK_ELIXIR_STORAGE_PRIORITY,
        XBOW_PRIORITY,
        SCATTERSHOT_PRIORITY,
        SPELL_TOWER_PRIORITY,
        MONOLITH_PRIORITY,
        WIZARD_TOWER_PRIORITY
    )
}
