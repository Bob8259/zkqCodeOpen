@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object BuilderBaseSettings {
    val NO_BUILDER_BASE = SettingDef("no_builder_base", "关闭夜世界", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_FARMING = SettingDef("builder_base_farming", "自动进攻", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_BATTLE_TIMES = SettingDef(
        "builder_base_battle_times", "对战局数", "2", "BUILDER_BASE_SETTINGS"
    )
    val BUILDER_BASE_BATTLE_TIMES_WITH_TASKS = SettingDef(
        "builder_base_battle_times_with_tasks", "接取竞赛后对战局数", "3", "BUILDER_BASE_SETTINGS"
    )
    val STOP_WHEN_RESOURCE_FULL = SettingDef("stop_when_resource_full", "资源满后停止对战", 1, "BUILDER_BASE_SETTINGS")
    val TROPHY_PUSHING_MODE = SettingDef("trophy_pushing_mode", "上分模式", 0, "BUILDER_BASE_SETTINGS")
    val ELIXIR_CART_FARMING = SettingDef("elixir_cart_farming", "刷圣水车", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_RESEARCH = SettingDef("builder_base_research", "夜世界研究", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_BUILD_SETTING = SettingDef("builder_base_build_setting", "自动建造", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_WALL_UPGRADE_SETTINGS = SettingDef("builder_base_wall_upgrade_settings", "升级城墙", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_BATCH_WALL_UPGRADE_SETTINGS = SettingDef("builder_base_batch_wall_upgrade_settings", "批量升级城墙", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_UPGRADE_WALL_THRESHOLD = SettingDef(
        "builder_base_upgrade_wall_threshold", "金水高于以下百分比后升级城墙", "85", "BUILDER_BASE_SETTINGS"
    )
    val BUILDER_BASE_REMOVE_OBSTACLES = SettingDef("builder_base_remove_obstacles", "移除障碍物", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_SAVE_WORKER = SettingDef("builder_base_save_worker", "留1工人升级城墙", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_ENABLE_CLOCK_TOWER = SettingDef("builder_base_enable_clock_tower", "开启时光钟楼", 1, "BUILDER_BASE_SETTINGS")

    val all = listOf(
        NO_BUILDER_BASE,
        BUILDER_BASE_FARMING,
        BUILDER_BASE_BATTLE_TIMES,
        STOP_WHEN_RESOURCE_FULL,
        TROPHY_PUSHING_MODE,
        ELIXIR_CART_FARMING,
        BUILDER_BASE_RESEARCH,
        BUILDER_BASE_BUILD_SETTING,
        BUILDER_BASE_WALL_UPGRADE_SETTINGS,
        BUILDER_BASE_BATCH_WALL_UPGRADE_SETTINGS,
        BUILDER_BASE_UPGRADE_WALL_THRESHOLD,
        BUILDER_BASE_REMOVE_OBSTACLES,
        BUILDER_BASE_SAVE_WORKER,
        BUILDER_BASE_ENABLE_CLOCK_TOWER,
        BUILDER_BASE_BATTLE_TIMES_WITH_TASKS
    )
}
