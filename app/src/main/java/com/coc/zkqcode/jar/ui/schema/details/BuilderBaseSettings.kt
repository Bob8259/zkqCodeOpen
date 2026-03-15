@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object BuilderBaseSettings {
    val NO_BUILDER_BASE = SettingDef("no_builder_base", "不打夜世界", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_FARMING = SettingDef("builder_base_farming", "夜世界打资源", 1, "BUILDER_BASE_SETTINGS")
    val SWITCH_ACCOUNT_AFTER_BATTLES = SettingDef(
        "switch_account_after_battles", "每次对战以下局数后切号", 2, "BUILDER_BASE_SETTINGS"
    )
    val SWITCH_ACCOUNT_AFTER_BATTLES_WITH_TASKS = SettingDef(
        "switch_account_after_battles_with_tasks", "接取竞赛后，对战以下局数后切号", 4, "BUILDER_BASE_SETTINGS"
    )
    val STOP_WHEN_RESOURCE_FULL = SettingDef("stop_when_resource_full", "资源满后停止对战", 1, "BUILDER_BASE_SETTINGS")
    val TROPHY_PUSHING_MODE = SettingDef("trophy_pushing_mode", "上分模式", 0, "BUILDER_BASE_SETTINGS")
    val ELIXIR_CART_FARMING = SettingDef("elixir_cart_farming", "刷圣水车", 0, "BUILDER_BASE_SETTINGS")
    val BUILDER_BASE_RESEARCH = SettingDef("builder_base_research", "夜世界研究", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_BUILD_SETTING = SettingDef("night_build_setting", "自动建造", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_WALL_UPGRADE_SETTINGS = SettingDef("night_wall_upgrade_settings", "升级城墙", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_REMOVE_OBSTACLES = SettingDef("night_remove_obstacles", "移除障碍物", 1, "BUILDER_BASE_SETTINGS")
    val NIGHT_SAVE_WORKER = SettingDef("night_save_worker", "留1工人升级城墙", 0, "BUILDER_BASE_SETTINGS")

    val all = listOf(
        NO_BUILDER_BASE,
        BUILDER_BASE_FARMING,
        SWITCH_ACCOUNT_AFTER_BATTLES,
        STOP_WHEN_RESOURCE_FULL,
        TROPHY_PUSHING_MODE,
        ELIXIR_CART_FARMING,
        BUILDER_BASE_RESEARCH,
        NIGHT_BUILD_SETTING,
        NIGHT_WALL_UPGRADE_SETTINGS,
        NIGHT_REMOVE_OBSTACLES,
        NIGHT_SAVE_WORKER,
        SWITCH_ACCOUNT_AFTER_BATTLES_WITH_TASKS
    )
}
