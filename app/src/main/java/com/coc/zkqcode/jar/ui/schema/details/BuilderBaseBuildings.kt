package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object BuilderBaseBuildings {
    // Resources and core buildings
    val BUILDER_HALL = SettingDef("builder_hall", "建筑大师大本营", 1, "BUILDER_BASE_SETTINGS")
    val GEM_MINE = SettingDef("gem_mine", "宝石矿井", 1, "BUILDER_BASE_SETTINGS")
    val CLOCK_TOWER = SettingDef("clock_tower", "时光钟楼", 1, "BUILDER_BASE_SETTINGS")
    val STAR_LABORATORY = SettingDef("star_laboratory", "星空实验室", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_GOLD_STORAGE = SettingDef("night_gold_storage", "储金罐", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_ELIXIR_STORAGE = SettingDef("night_elixir_storage", "圣水瓶", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_GOLD_MINE = SettingDef("night_gold_mine", "金矿", 1, "BUILDER_BASE_SETTINGS")
    val OTTOS_OUTPOST = SettingDef("ottos_outpost", "奥仔哨站", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_ELIXIR_COLLECTOR = SettingDef("night_elixir_collector", "圣水收集器", 1, "BUILDER_BASE_SETTINGS")

 
    // Army buildings
    val BUILDER_BARRACKS = SettingDef("builder_barracks", "建筑大师训练营", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_ARMY_CAMP = SettingDef("night_army_camp", "兵营", 1, "BUILDER_BASE_SETTINGS")
    val REINFORCEMENT_CAMP = SettingDef("reinforcement_camp", "预备营", 1, "BUILDER_BASE_SETTINGS")
    val HEALING_HUT = SettingDef("healing_hut", "治疗小屋", 1, "BUILDER_BASE_SETTINGS")

 
    // Heroes/Machines
    val BATTLE_MACHINE = SettingDef("battle_machine", "战争机器", 1, "BUILDER_BASE_SETTINGS")
    val BATTLE_COPTER = SettingDef("battle_copter", "战斗直升机", 1, "BUILDER_BASE_SETTINGS")
 
    // Defense buildings
    val MULTI_MORTAR = SettingDef("multi_mortar", "多管迫击炮", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_ARCHER_TOWER = SettingDef("night_archer_tower", "箭塔", 1, "BUILDER_BASE_SETTINGS")
    val DOUBLE_CANNON = SettingDef("double_cannon", "双管加农炮", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_CANNON = SettingDef("night_cannon", "加农炮", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_HIDDEN_TESLA = SettingDef("night_hidden_tesla", "特斯拉电磁塔", 1, "BUILDER_BASE_SETTINGS")
    val FIRECRACKERS = SettingDef("firecrackers", "防空火炮", 1, "BUILDER_BASE_SETTINGS")
    val CRUSHER = SettingDef("crusher", "撼地巨石", 1, "BUILDER_BASE_SETTINGS")
    val GUARD_POST = SettingDef("guard_post", "守卫岗哨", 1, "BUILDER_BASE_SETTINGS")
    val AIR_BOMBS = SettingDef("air_bombs", "空中炸弹发射器", 1, "BUILDER_BASE_SETTINGS")
    val ROASTER = SettingDef("roaster", "熔岩火炮", 1, "BUILDER_BASE_SETTINGS")
    val GIANT_CANNON = SettingDef("giant_cannon", "巨型加农炮", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_MEGA_TESLA = SettingDef("night_mega_tesla", "超级特斯拉电磁塔", 1, "BUILDER_BASE_SETTINGS")
    val LAVA_LAUNCHER = SettingDef("lava_launcher", "熔岩发射器", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_X_BOW = SettingDef("night_x_bow", "十字连弩", 1, "BUILDER_BASE_SETTINGS")

    // Traps
    val PUSH_TRAP = SettingDef("push_trap", "弹射陷阱", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_SPRING_TRAP = SettingDef("night_spring_trap", "隐形弹簧", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_MINE = SettingDef("night_mine", "地雷", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_MEGA_MINE = SettingDef("night_mega_mine", "巨型地雷", 1, "BUILDER_BASE_SETTINGS")


    val all = listOf(
        BUILDER_HALL,
        GEM_MINE,
        CLOCK_TOWER,
        STAR_LABORATORY,
        NIGHT_GOLD_STORAGE,
        NIGHT_ELIXIR_STORAGE,
        NIGHT_GOLD_MINE,
        BUILDER_BARRACKS,
        BATTLE_MACHINE,
        BATTLE_COPTER,
        MULTI_MORTAR,
        NIGHT_ARCHER_TOWER,
        DOUBLE_CANNON,
        OTTOS_OUTPOST,
        NIGHT_ELIXIR_COLLECTOR,
        NIGHT_ARMY_CAMP,
        REINFORCEMENT_CAMP,
        HEALING_HUT,
        NIGHT_CANNON,
        NIGHT_HIDDEN_TESLA,
        FIRECRACKERS,
        CRUSHER,
        GUARD_POST,
        AIR_BOMBS,
        ROASTER,
        GIANT_CANNON,
        NIGHT_MEGA_TESLA,
        LAVA_LAUNCHER,
        NIGHT_X_BOW,
        PUSH_TRAP,
        NIGHT_SPRING_TRAP,
        NIGHT_MINE,
        NIGHT_MEGA_MINE
    )
}

object BuilderBaseBuildingsPriority {
    // Resources and core buildings
    val BUILDER_HALL_PRIORITY = SettingDef("builder_hall_priority", "建筑大师大本营", 1, "BUILDER_BASE_SETTINGS_PRIORITY")
    val GEM_MINE_PRIORITY = SettingDef("gem_mine_priority", "宝石矿井", 2, "BUILDER_BASE_SETTINGS_PRIORITY")
    val CLOCK_TOWER_PRIORITY = SettingDef("clock_tower_priority", "时光钟楼", 3, "BUILDER_BASE_SETTINGS_PRIORITY")
    val STAR_LABORATORY_PRIORITY =
        SettingDef("star_laboratory_priority", "星空实验室", 4, "BUILDER_BASE_SETTINGS_PRIORITY")
 
    // [Modification] Added prefix
    val NIGHT_GOLD_STORAGE_PRIORITY =
        SettingDef("night_gold_storage_priority", "储金罐", 5, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_ELIXIR_STORAGE_PRIORITY =
        SettingDef("night_elixir_storage_priority", "圣水瓶", 6, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_GOLD_MINE_PRIORITY =
        SettingDef("night_gold_mine_priority", "金矿", 13, "BUILDER_BASE_SETTINGS_PRIORITY")
    val OTTOS_OUTPOST_PRIORITY = SettingDef("ottos_outpost_priority", "奥仔哨站", 14, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_ELIXIR_COLLECTOR_PRIORITY =
        SettingDef("night_elixir_collector_priority", "圣水收集器", 15, "BUILDER_BASE_SETTINGS_PRIORITY")

 
    // Army buildings
    val BUILDER_BARRACKS_PRIORITY =
        SettingDef("builder_barracks_priority", "建筑大师训练营", 7, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_ARMY_CAMP_PRIORITY = SettingDef("night_army_camp_priority", "兵营", 16, "BUILDER_BASE_SETTINGS_PRIORITY")
    val REINFORCEMENT_CAMP_PRIORITY =
        SettingDef("reinforcement_camp_priority", "预备营", 17, "BUILDER_BASE_SETTINGS_PRIORITY")
    val HEALING_HUT_PRIORITY = SettingDef("healing_hut_priority", "治疗小屋", 18, "BUILDER_BASE_SETTINGS_PRIORITY")

 
    // Heroes/Machines
    val BATTLE_MACHINE_PRIORITY =
        SettingDef("battle_machine_priority", "战争机器", 8, "BUILDER_BASE_SETTINGS_PRIORITY")
    val BATTLE_COPTER_PRIORITY =
        SettingDef("battle_copter_priority", "战斗直升机", 9, "BUILDER_BASE_SETTINGS_PRIORITY")
 
    // Defense buildings
    val MULTI_MORTAR_PRIORITY =
        SettingDef("multi_mortar_priority", "多管迫击炮", 10, "BUILDER_BASE_SETTINGS_PRIORITY")
 
    // [Modification] Added prefix
    val NIGHT_ARCHER_TOWER_PRIORITY =
        SettingDef("night_archer_tower_priority", "箭塔", 11, "BUILDER_BASE_SETTINGS_PRIORITY")
 
    val DOUBLE_CANNON_PRIORITY =
        SettingDef("double_cannon_priority", "双管加农炮", 12, "BUILDER_BASE_SETTINGS_PRIORITY")

    val NIGHT_CANNON_PRIORITY = SettingDef("night_cannon_priority", "加农炮", 19, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_HIDDEN_TESLA_PRIORITY =
        SettingDef("night_hidden_tesla_priority", "特斯拉电磁塔", 20, "BUILDER_BASE_SETTINGS_PRIORITY")
    val FIRECRACKERS_PRIORITY = SettingDef("firecrackers_priority", "防空火炮", 21, "BUILDER_BASE_SETTINGS_PRIORITY")
    val CRUSHER_PRIORITY = SettingDef("crusher_priority", "撼地巨石", 22, "BUILDER_BASE_SETTINGS_PRIORITY")
    val GUARD_POST_PRIORITY = SettingDef("guard_post_priority", "守卫岗哨", 23, "BUILDER_BASE_SETTINGS_PRIORITY")
    val AIR_BOMBS_PRIORITY = SettingDef("air_bombs_priority", "空中炸弹发射器", 24, "BUILDER_BASE_SETTINGS_PRIORITY")
    val ROASTER_PRIORITY = SettingDef("roaster_priority", "熔岩火炮", 25, "BUILDER_BASE_SETTINGS_PRIORITY")
    val GIANT_CANNON_PRIORITY = SettingDef("giant_cannon_priority", "巨型加农炮", 26, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_MEGA_TESLA_PRIORITY =
        SettingDef("night_mega_tesla_priority", "超级特斯拉电磁塔", 27, "BUILDER_BASE_SETTINGS_PRIORITY")
    val LAVA_LAUNCHER_PRIORITY = SettingDef("lava_launcher_priority", "熔岩发射器", 28, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_X_BOW_PRIORITY = SettingDef("night_x_bow_priority", "十字连弩", 29, "BUILDER_BASE_SETTINGS_PRIORITY")

    // Traps
    val PUSH_TRAP_PRIORITY = SettingDef("push_trap_priority", "弹射陷阱", 30, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_SPRING_TRAP_PRIORITY =
        SettingDef("night_spring_trap_priority", "隐形弹簧", 31, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_MINE_PRIORITY = SettingDef("night_mine_priority", "地雷", 32, "BUILDER_BASE_SETTINGS_PRIORITY")
    val NIGHT_MEGA_MINE_PRIORITY = SettingDef("night_mega_mine_priority", "巨型地雷", 33, "BUILDER_BASE_SETTINGS_PRIORITY")


    val all = listOf(
        BUILDER_HALL_PRIORITY,
        GEM_MINE_PRIORITY,
        CLOCK_TOWER_PRIORITY,
        STAR_LABORATORY_PRIORITY,
        NIGHT_GOLD_STORAGE_PRIORITY,   // Updated
        NIGHT_ELIXIR_STORAGE_PRIORITY, // Updated
        NIGHT_GOLD_MINE_PRIORITY,
        BUILDER_BARRACKS_PRIORITY,
        BATTLE_MACHINE_PRIORITY,
        BATTLE_COPTER_PRIORITY,
        MULTI_MORTAR_PRIORITY,
        NIGHT_ARCHER_TOWER_PRIORITY,   // Updated
        DOUBLE_CANNON_PRIORITY,
        OTTOS_OUTPOST_PRIORITY,
        NIGHT_ELIXIR_COLLECTOR_PRIORITY,
        NIGHT_ARMY_CAMP_PRIORITY,
        REINFORCEMENT_CAMP_PRIORITY,
        HEALING_HUT_PRIORITY,
        NIGHT_CANNON_PRIORITY,
        NIGHT_HIDDEN_TESLA_PRIORITY,
        FIRECRACKERS_PRIORITY,
        CRUSHER_PRIORITY,
        GUARD_POST_PRIORITY,
        AIR_BOMBS_PRIORITY,
        ROASTER_PRIORITY,
        GIANT_CANNON_PRIORITY,
        NIGHT_MEGA_TESLA_PRIORITY,
        LAVA_LAUNCHER_PRIORITY,
        NIGHT_X_BOW_PRIORITY,
        PUSH_TRAP_PRIORITY,
        NIGHT_SPRING_TRAP_PRIORITY,
        NIGHT_MINE_PRIORITY,
        NIGHT_MEGA_MINE_PRIORITY
    )
}
