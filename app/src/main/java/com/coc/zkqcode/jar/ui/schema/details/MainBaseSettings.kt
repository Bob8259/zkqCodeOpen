@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object MainBaseSettings {
    // Common settings
    val AUTO_ATTACK = SettingDef("auto_attack", "自动进攻", 1, "MAIN_BASE_SETTINGS")
    val GOLD_REQUIREMENT = SettingDef("gold_requirement", "金币要求", "-1", "MAIN_BASE_SETTINGS")
    val ELIXIR_REQUIREMENT = SettingDef("elixir_requirement", "圣水要求", "-1", "MAIN_BASE_SETTINGS")
    val DARK_ELIXIR_REQUIREMENT = SettingDef("dark_elixir_requirement", "黑油要求", "-1", "MAIN_BASE_SETTINGS")
    val DYNAMIC_ADJUSTMENT = SettingDef("dynamic_adjustment", "动态调节", 1, "MAIN_BASE_SETTINGS")
    val STOP_BATTLE_AFTER_FULL_RESOURCES = SettingDef(
        "stop_battle_after_full_resources", "资源满后停止对战", 1, "MAIN_BASE_SETTINGS"
    )
    val MANUAL_TRAINING = SettingDef("manual_training", "手动练兵", 0, "MAIN_BASE_SETTINGS")
    val TACTICS_MODE = SettingDef("tactics_mode", "战术设置", "0", "MAIN_BASE_SETTINGS")
    val DONATION_SETTING = SettingDef("donation_setting", "自动捐兵", 1, "MAIN_BASE_SETTINGS")
    val REQUEST_REINFORCEMENT_SETTING =
        SettingDef("request_reinforcement_setting", "请求增援", 1, "MAIN_BASE_SETTINGS")
    val DONATION_TIMES = SettingDef("donation_times", "捐兵轮数", 1, "MAIN_BASE_SETTINGS")
    val RESEARCH_SETTING = SettingDef("research_setting", "自动研究", 1, "MAIN_BASE_SETTINGS")
    val RESEARCH_LEVEL = SettingDef("research_level", "研究等级至", "0", "MAIN_BASE_SETTINGS")
    val COLLECT_CLAN_CASTLE = SettingDef("collect_clan_castle", "领宝库", 1, "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_AIR_SWEEPER = SettingDef("lighting_on_air_sweeper", "闪空气炮次数", "2", "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_AIR_DEFENCE = SettingDef("lighting_on_air_defence", "闪火箭次数", "3", "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_MORTAR = SettingDef("lighting_on_mortar", "闪迫击炮次数", "4", "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_WIZARD_TOWER = SettingDef("lighting_on_wizard_tower", "闪法师塔次数", "4", "MAIN_BASE_SETTINGS")
    val AI_DEPLOY_TROOPS = SettingDef("ai_deploy_troops", "AI下兵", 0, "MAIN_BASE_SETTINGS")
    val CHANGE_HEROES = SettingDef("change_heroes", "随机换英雄", 0, "MAIN_BASE_SETTINGS")

    ///////
    val BUILD_SETTING = SettingDef("build_setting", "自动建造", 1, "MAIN_BASE_SETTINGS")
    val WALL_UPGRADE_SETTINGS = SettingDef("wall_upgrade_settings", "升级城墙", 1, "MAIN_BASE_SETTINGS")
    val INSTANT_UPGRADE = SettingDef("instant_upgrade", "用宝石升级消耗低于", 0, "MAIN_BASE_SETTINGS")
    val INSTANT_UPGRADE_THRESHOLD = SettingDef("instant_upgrade_threshold", "宝石的建筑", "3", "MAIN_BASE_SETTINGS")

    val BATCH_WALL_UPGRADE_SETTINGS =
        SettingDef("batch_wall_upgrade_settings", "批量升级城墙", 1, "MAIN_BASE_SETTINGS")
    val SAVE_WORKER = SettingDef("save_worker", "留1工人升级城墙", 0, "MAIN_BASE_SETTINGS")
    val BUILDING_CONVERSION_SETTINGS =
        SettingDef("building_conversion_settings", "改装建筑", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_AFTER_FAIL_WALL_UPGRADE =
        SettingDef("upgrade_after_fail_wall_upgrade", "刷墙失败后建造", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_WALL_THRESHOLD = SettingDef(
        "upgrade_wall_threshold", "金水高于以下百分比后刷墙", "85", "MAIN_BASE_SETTINGS"
    )
    val UPGRADE_PETS = SettingDef("upgrade_pets", "升级战宠", 1, "MAIN_BASE_SETTINGS")
    val STOP_BATTLE_WHEN_NO_STAR =
        SettingDef("stop_battle_when_no_star", "无胜利之星后停止对战", 0, "MAIN_BASE_SETTINGS")
    val RESTART_GAME = SettingDef("restart_game", "重启游戏", 1, "MAIN_BASE_SETTINGS")
    val PLAY_LADDER = SettingDef("play_ladder", "排位对战", 0, "MAIN_BASE_SETTINGS")
    val CHANGE_BASE = SettingDef("change_base", "切换阵型", 1, "MAIN_BASE_SETTINGS")
    val WAIT_FOR_BATTLE = SettingDef("wait_for_battle", "等待对战", 0, "MAIN_BASE_SETTINGS")
    val HELPER_SETTINGS = SettingDef("helper_settings", "用帮手", 1, "MAIN_BASE_SETTINGS")
    val UPGRADE_RESEARCH_HELPER = SettingDef("upgrade_research_helper", "升级实验助手", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_BUILDER_APPRENTICE =
        SettingDef("upgrade_builder_apprentice", "升级工人学徒", 0, "MAIN_BASE_SETTINGS")
    val DO_CLAN_GAMES = SettingDef("do_clan_games", "做竞赛任务", 1, "MAIN_BASE_SETTINGS")
    val CLAIM_CLAN_GAME_REWARDS = SettingDef("claim_clan_game_rewards", "领竞赛奖励", 0, "MAIN_BASE_SETTINGS")
    val PLAY_CLAN_WAR = SettingDef("play_clan_war", "打部落战", 0, "MAIN_BASE_SETTINGS")
    val PLAY_LEAGUE = SettingDef("play_league", "打联赛", 0, "MAIN_BASE_SETTINGS")
    val PLAY_RAID = SettingDef("play_raid", "打都城突袭", 1, "MAIN_BASE_SETTINGS")
    val START_LEAGUE_SETTINGS = SettingDef("start_league_settings", "发起联赛", 0, "MAIN_BASE_SETTINGS")
    val START_CLAN_WAR_SETTINGS = SettingDef("start_clan_war_settings", "发起部落战", 0, "MAIN_BASE_SETTINGS")
    val START_RAID = SettingDef("start_raid", "发起都城突袭", 0, "MAIN_BASE_SETTINGS")

    ////////////
    val BUY_STAR_ORE_WITH_RAID_MEDAL =
        SettingDef("buy_star_ore_with_raid_medal", "突袭币买星辉矿石", 0, "MAIN_BASE_SETTINGS")
    val BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL = SettingDef(
        "buy_clock_tower_potion_with_raid_medal", "突袭币买钟楼药水", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RING_OF_WALL_WITH_RAID_MEDAL = SettingDef(
        "buy_ring_of_wall_with_raid_medal", "突袭币买壁垒之戒", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESEARCH_POTION_WITH_RAID_MEDAL = SettingDef(
        "buy_research_potion_with_raid_medal", "突袭币买研究药水", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_TRAINING_POTION_WITH_RAID_MEDAL = SettingDef(
        "buy_training_potion_with_raid_medal", "突袭币买训练药水", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL = SettingDef(
        "buy_research_potion_with_league_medal", "联赛币买研究药水", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL = SettingDef(
        "buy_builder_potion_with_league_medal", "联赛币买工人药水", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_STAR_ORE_WITH_EVENT_MEDAL =
        SettingDef("buy_star_ore_with_event_medal", "活动币买星辉矿石", 0, "MAIN_BASE_SETTINGS")
    val BUY_BUILDER_POTION_WITH_EVENT_MEDAL = SettingDef(
        "buy_builder_potion_with_event_medal", "活动币买工人药水", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL = SettingDef(
        "buy_new_equipment_with_event_medal", "活动币买新装备", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESEARCH_POTION_WITH_EVENT_MEDAL = SettingDef(
        "buy_research_potion_with_event_medal", "活动币买研究药水", 0, "MAIN_BASE_SETTINGS"
    )
    val USE_RESEARCH_POTION = SettingDef("use_research_potion", "用研究药水", 0, "MAIN_BASE_SETTINGS")
    val SELL_TRAINING_POTION = SettingDef("sell_training_potion", "卖训练药水", 0, "MAIN_BASE_SETTINGS")
    val USE_CLOCK_TOWER_POTION = SettingDef("use_clock_tower_potion", "用钟楼药水", 0, "MAIN_BASE_SETTINGS")
    val SELL_CLOCK_TOWER_POTION = SettingDef("sell_clock_tower_potion", "卖钟楼药水", 0, "MAIN_BASE_SETTINGS")
    val USE_BUILDER_POTION = SettingDef("use_builder_potion", "用工人药水", 0, "MAIN_BASE_SETTINGS")
    val SELL_RING_OF_WALL = SettingDef("sell_ring_of_wall", "卖壁垒之戒", 0, "MAIN_BASE_SETTINGS")

    //////////////
    val UPGRADE_WEARABLE_GEAR = SettingDef("upgrade_wearable_gear", "升穿戴装备", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_ALL_GEAR = SettingDef("upgrade_all_gear", "升所有装备", 0, "MAIN_BASE_SETTINGS")
    val REMOVE_OBSTACLES = SettingDef("remove_obstacles", "移除障碍物", 0, "MAIN_BASE_SETTINGS")
    val REMOVE_OBSTACLES_ENHANCEMENT = SettingDef("remove_obstacles_enhancement", "增强移除", 0, "MAIN_BASE_SETTINGS")

    val CLAIM_TIMED_REWARDS = SettingDef("claim_timed_rewards", "领限时活动奖励", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_TOKEN_REWARDS = SettingDef("claim_token_rewards", "领令牌奖励", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_CAPITAL_GOLD = SettingDef("claim_capital_gold", "领都城币", 0, "MAIN_BASE_SETTINGS")
    val DONATE_CAPITAL_GOLD = SettingDef("donate_capital_gold", "捐都城币", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_FREE_SHOP_REWARDS = SettingDef("claim_free_shop_rewards", "领商店免费奖励", 0, "MAIN_BASE_SETTINGS")
    val AUTO_JOIN_CLAN = SettingDef("auto_join_clan", "自动加部落", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_ACHIEVEMENT_GEMS = SettingDef("claim_achievement_gems", "领成就宝石", 0, "MAIN_BASE_SETTINGS")
    val CLAN_TAG = SettingDef("clan_tag", "加指定部落标签(不填就随机加):", "", "MAIN_BASE_SETTINGS")
    val USE_TEMP_ITEMS = SettingDef("use_temp_items", "使用临时物品", 0, "MAIN_BASE_SETTINGS")
    val CLAN_JOIN_MESSAGE = SettingDef("clan_join_message", "加部落暗号:", "", "MAIN_BASE_SETTINGS")
    val CREATE_CONSECUTIVE_CLANS = SettingDef("create_consecutive_clans", "创建连号部落", 0, "MAIN_BASE_SETTINGS")
    val INVITE_PLAYERS = SettingDef("invite_players", "邀请玩家", 0, "MAIN_BASE_SETTINGS")
    val CLAN_NAME = SettingDef("clan_name", "部落名称:", "", "MAIN_BASE_SETTINGS")
    val CONSECUTIVE_COUNT = SettingDef("consecutive_count", "连号数量:", 3, "MAIN_BASE_SETTINGS")

    val all = listOf(
        AUTO_ATTACK,
        GOLD_REQUIREMENT,
        ELIXIR_REQUIREMENT,
        DARK_ELIXIR_REQUIREMENT,
        DYNAMIC_ADJUSTMENT,
        STOP_BATTLE_AFTER_FULL_RESOURCES,
        MANUAL_TRAINING,
        TACTICS_MODE,
        DONATION_SETTING,
        REQUEST_REINFORCEMENT_SETTING,
        DONATION_TIMES,
        RESEARCH_SETTING,
        RESEARCH_LEVEL,
        COLLECT_CLAN_CASTLE,
        LIGHTING_ON_AIR_SWEEPER,
        LIGHTING_ON_AIR_DEFENCE,
        LIGHTING_ON_MORTAR,
        LIGHTING_ON_WIZARD_TOWER,
        AI_DEPLOY_TROOPS,
        CHANGE_HEROES,
        BUILD_SETTING,
        WALL_UPGRADE_SETTINGS,
        INSTANT_UPGRADE,
        INSTANT_UPGRADE_THRESHOLD,
        BATCH_WALL_UPGRADE_SETTINGS,
        SAVE_WORKER,
        BUILDING_CONVERSION_SETTINGS,
        UPGRADE_AFTER_FAIL_WALL_UPGRADE,
        UPGRADE_WALL_THRESHOLD,
        UPGRADE_PETS,
        STOP_BATTLE_WHEN_NO_STAR,
        RESTART_GAME,
        PLAY_LADDER,
        CHANGE_BASE,
        WAIT_FOR_BATTLE,
        HELPER_SETTINGS,
        UPGRADE_RESEARCH_HELPER,
        UPGRADE_BUILDER_APPRENTICE,
        DO_CLAN_GAMES,
        CLAIM_CLAN_GAME_REWARDS,
        PLAY_CLAN_WAR,
        PLAY_LEAGUE,
        PLAY_RAID,
        START_LEAGUE_SETTINGS,
        START_CLAN_WAR_SETTINGS,
        START_RAID,
        BUY_STAR_ORE_WITH_RAID_MEDAL,
        BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL,
        BUY_RING_OF_WALL_WITH_RAID_MEDAL,
        BUY_RESEARCH_POTION_WITH_RAID_MEDAL,
        BUY_TRAINING_POTION_WITH_RAID_MEDAL,
        BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL,
        BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL,
        BUY_STAR_ORE_WITH_EVENT_MEDAL,
        BUY_BUILDER_POTION_WITH_EVENT_MEDAL,
        BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL,
        BUY_RESEARCH_POTION_WITH_EVENT_MEDAL,
        USE_RESEARCH_POTION,
        SELL_TRAINING_POTION,
        USE_CLOCK_TOWER_POTION,
        SELL_CLOCK_TOWER_POTION,
        USE_BUILDER_POTION,
        SELL_RING_OF_WALL,
        UPGRADE_WEARABLE_GEAR,
        UPGRADE_ALL_GEAR,
        REMOVE_OBSTACLES,
        REMOVE_OBSTACLES_ENHANCEMENT,
        CLAIM_TIMED_REWARDS,
        CLAIM_TOKEN_REWARDS,
        CLAIM_CAPITAL_GOLD,
        DONATE_CAPITAL_GOLD,
        CLAIM_FREE_SHOP_REWARDS,
        AUTO_JOIN_CLAN,
        CLAIM_ACHIEVEMENT_GEMS,
        CLAN_TAG,
        USE_TEMP_ITEMS,
        CLAN_JOIN_MESSAGE,
        CREATE_CONSECUTIVE_CLANS,
        INVITE_PLAYERS,
        CLAN_NAME,
        CONSECUTIVE_COUNT
    )
}
