@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object BuilderBaseSettings {
    val NO_BUILDER_BASE = SettingDef("no_builder_base", "关闭夜世界", "Disable Builder Base", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_FARMING = SettingDef("builder_base_farming", "自动进攻", "Auto Attack", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_BATTLE_TIMES = SettingDef(
        "builder_base_battle_times", "对战局数", "Battle Times", "2", "BUILDER_BASE_SETTINGS"
    )
    val BUILDER_BASE_BATTLE_TIMES_WITH_TASKS = SettingDef(
        "builder_base_battle_times_with_tasks", "接取竞赛后对战局数", "Battle Times (with tasks)", "5", "BUILDER_BASE_SETTINGS"
    )
    val BUILDER_BASE_STOP_BATTLE_AFTER_FULL_RESOURCES = SettingDef("builder_base_stop_battle_after_full_resources", "资源满后停止对战", "Stop if resources full", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_STOP_WHEN_GOLD_FULL = SettingDef(
        "builder_base_stop_when_gold_full", "金币满", "Gold Full", 1, "BUILDER_BASE_SETTINGS"
    )
    val BUILDER_BASE_STOP_WHEN_ELIXIR_FULL = SettingDef(
        "builder_base_stop_when_elixir_full", "圣水满", "Elixir Full", 1, "BUILDER_BASE_SETTINGS"
    )
    val TROPHY_PUSHING_MODE = SettingDef("trophy_pushing_mode", "上分模式", "Trophy Pushing", 0, "BUILDER_BASE_SETTINGS")
    val ELIXIR_CART_FARMING = SettingDef("elixir_cart_farming", "刷圣水车", "Elixir Cart Farming", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_RESEARCH = SettingDef("builder_base_research", "夜世界研究", "Builder Base Research", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_BUILD_SETTING = SettingDef("builder_base_build_setting", "自动建造", "Auto Build", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_WALL_UPGRADE_SETTINGS = SettingDef("builder_base_wall_upgrade_settings", "升级城墙", "Upgrade Walls", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_BATCH_WALL_UPGRADE_SETTINGS = SettingDef("builder_base_batch_wall_upgrade_settings", "批量升级城墙", "Batch Upgrade Walls", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_UPGRADE_WALL_THRESHOLD = SettingDef(
        "builder_base_upgrade_wall_threshold", "金水高于以下百分比后升级城墙", "Upgrade walls if resources >", "85", "BUILDER_BASE_SETTINGS"
    )
    val BUILDER_BASE_REMOVE_OBSTACLES = SettingDef("builder_base_remove_obstacles", "移除障碍物", "Remove Obstacles", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_CONTINUOUS_REMOVE_OBSTACLES = SettingDef("builder_base_continuous_remove_obstacles", "连续移除障碍物", "Continuous Removal", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_SAVE_WORKER = SettingDef("builder_base_save_worker", "留1工人", "Save 1 Builder", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_SAVE_WORKER_BUILD_AFTER_WALL_FULL = SettingDef("builder_base_save_worker_build_after_wall_full", "城墙全满后，将保留的工人用于建造", "Use saved builder after walls full", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_ENABLE_CLOCK_TOWER = SettingDef("builder_base_enable_clock_tower", "开启时光钟楼", "Enable Clock Tower", 1, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_STOP_BATTLE_WHEN_NO_STAR = SettingDef("builder_base_stop_battle_when_no_star", "无胜利之星后停止对战", "Stop if no stars", 0, "BUILDER_BASE_SETTINGS")
    val EXIT_ON_ONE_STAR_BUILDER = SettingDef("exit_on_one_star_builder", "一星退出", "Exit on 1-star", 0, "BUILDER_BASE_SETTINGS")

    val all = listOf(
        NO_BUILDER_BASE,
        BUILDER_BASE_FARMING,
        BUILDER_BASE_BATTLE_TIMES,
        BUILDER_BASE_STOP_BATTLE_AFTER_FULL_RESOURCES,
        BUILDER_BASE_STOP_WHEN_GOLD_FULL,
        BUILDER_BASE_STOP_WHEN_ELIXIR_FULL,
        TROPHY_PUSHING_MODE,
        ELIXIR_CART_FARMING,
        BUILDER_BASE_RESEARCH,
        BUILDER_BASE_BUILD_SETTING,
        BUILDER_BASE_WALL_UPGRADE_SETTINGS,
        BUILDER_BASE_BATCH_WALL_UPGRADE_SETTINGS,
        BUILDER_BASE_UPGRADE_WALL_THRESHOLD,
        BUILDER_BASE_REMOVE_OBSTACLES,
        BUILDER_BASE_CONTINUOUS_REMOVE_OBSTACLES,
        BUILDER_BASE_SAVE_WORKER,
        BUILDER_BASE_SAVE_WORKER_BUILD_AFTER_WALL_FULL,
        BUILDER_BASE_ENABLE_CLOCK_TOWER,
        BUILDER_BASE_BATTLE_TIMES_WITH_TASKS,
        BUILDER_BASE_STOP_BATTLE_WHEN_NO_STAR,
        EXIT_ON_ONE_STAR_BUILDER
    )
}
