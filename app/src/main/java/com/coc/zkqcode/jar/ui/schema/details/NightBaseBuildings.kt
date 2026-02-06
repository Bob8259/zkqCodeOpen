@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object NightBaseBuildings {
    // 资源与核心建筑
    val BUILDER_HALL = SettingDef("builder_hall", "大本营", 1, "NIGHT_BASE_SETTINGS")
    val GEM_MINE = SettingDef("gem_mine", "宝石矿井", 1, "NIGHT_BASE_SETTINGS")
    val CLOCK_TOWER = SettingDef("clock_tower", "时光钟楼", 1, "NIGHT_BASE_SETTINGS")
    val STAR_LABORATORY = SettingDef("star_laboratory", "星空实验室", 1, "NIGHT_BASE_SETTINGS")
    val NIGHT_GOLD_STORAGE = SettingDef("night_gold_storage", "储金罐", 1, "NIGHT_BASE_SETTINGS")
    val NIGHT_ELIXIR_STORAGE = SettingDef("night_elixir_storage", "圣水瓶", 1, "NIGHT_BASE_SETTINGS")

    // 军队建筑
    val BUILDER_BARRACKS = SettingDef("builder_barracks", "训练营", 1, "NIGHT_BASE_SETTINGS")

    // 英雄/机器
    val BATTLE_MACHINE = SettingDef("battle_machine", "战争机器", 1, "NIGHT_BASE_SETTINGS")
    val BATTLE_COPTER = SettingDef("battle_copter", "战斗直升机", 1, "NIGHT_BASE_SETTINGS")

    // 防御建筑
    val MULTI_MORTAR = SettingDef("multi_mortar", "多管迫击炮", 1, "NIGHT_BASE_SETTINGS")
    val NIGHT_ARCHER_TOWER = SettingDef("night_archer_tower", "夜世界箭塔", 1, "NIGHT_BASE_SETTINGS")
    val DOUBLE_CANNON = SettingDef("double_cannon", "双管加农炮", 1, "NIGHT_BASE_SETTINGS")

    val all = listOf(
        BUILDER_HALL,
        GEM_MINE,
        CLOCK_TOWER,
        STAR_LABORATORY,
        NIGHT_GOLD_STORAGE,
        NIGHT_ELIXIR_STORAGE,
        BUILDER_BARRACKS,
        BATTLE_MACHINE,
        BATTLE_COPTER,
        MULTI_MORTAR,
        NIGHT_ARCHER_TOWER,
        DOUBLE_CANNON
    )
}

object NightBaseBuildingsPriority {
    // 资源与核心建筑
    val BUILDER_HALL_PRIORITY = SettingDef("builder_hall_priority", "大本营", 1, "NIGHT_BASE_SETTINGS_PRIORITY")
    val GEM_MINE_PRIORITY = SettingDef("gem_mine_priority", "宝石矿井", 2, "NIGHT_BASE_SETTINGS_PRIORITY")
    val CLOCK_TOWER_PRIORITY = SettingDef("clock_tower_priority", "时光钟楼", 3, "NIGHT_BASE_SETTINGS_PRIORITY")
    val STAR_LABORATORY_PRIORITY =
        SettingDef("star_laboratory_priority", "星空实验室", 4, "NIGHT_BASE_SETTINGS_PRIORITY")

    // 【修改项】添加了前缀
    val NIGHT_GOLD_STORAGE_PRIORITY =
        SettingDef("night_gold_storage_priority", "储金罐", 5, "NIGHT_BASE_SETTINGS_PRIORITY")
    val NIGHT_ELIXIR_STORAGE_PRIORITY =
        SettingDef("night_elixir_storage_priority", "圣水瓶", 6, "NIGHT_BASE_SETTINGS_PRIORITY")

    // 军队建筑
    val BUILDER_BARRACKS_PRIORITY =
        SettingDef("builder_barracks_priority", "训练营", 7, "NIGHT_BASE_SETTINGS_PRIORITY")

    // 英雄/机器
    val BATTLE_MACHINE_PRIORITY =
        SettingDef("battle_machine_priority", "战争机器", 8, "NIGHT_BASE_SETTINGS_PRIORITY")
    val BATTLE_COPTER_PRIORITY =
        SettingDef("battle_copter_priority", "战斗直升机", 9, "NIGHT_BASE_SETTINGS_PRIORITY")

    // 防御建筑
    val MULTI_MORTAR_PRIORITY =
        SettingDef("multi_mortar_priority", "多管迫击炮", 10, "NIGHT_BASE_SETTINGS_PRIORITY")

    // 【修改项】添加了前缀
    val NIGHT_ARCHER_TOWER_PRIORITY =
        SettingDef("night_archer_tower_priority", "夜世界箭塔", 11, "NIGHT_BASE_SETTINGS_PRIORITY")

    val DOUBLE_CANNON_PRIORITY =
        SettingDef("double_cannon_priority", "双管加农炮", 12, "NIGHT_BASE_SETTINGS_PRIORITY")

    val all = listOf(
        BUILDER_HALL_PRIORITY,
        GEM_MINE_PRIORITY,
        CLOCK_TOWER_PRIORITY,
        STAR_LABORATORY_PRIORITY,
        NIGHT_GOLD_STORAGE_PRIORITY,   // Updated
        NIGHT_ELIXIR_STORAGE_PRIORITY, // Updated
        BUILDER_BARRACKS_PRIORITY,
        BATTLE_MACHINE_PRIORITY,
        BATTLE_COPTER_PRIORITY,
        MULTI_MORTAR_PRIORITY,
        NIGHT_ARCHER_TOWER_PRIORITY,   // Updated
        DOUBLE_CANNON_PRIORITY
    )
}
