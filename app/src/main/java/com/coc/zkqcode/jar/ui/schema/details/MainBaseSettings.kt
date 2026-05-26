@file:Suppress("ClassName")

package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object MainBaseSettings {
    // Common settings
    val CLAIM_DAILY_REWARD = SettingDef("claim_daily_reward", "领取国服签到奖励", "Claim CN Daily Reward", 1, "MAIN_BASE_SETTINGS")
    val CONFIG_REMARK = SettingDef("config_remark", "备注:", "Remark:", "", "MAIN_BASE_SETTINGS")
    val SMART_DECISION = SettingDef("smart_decision", "智能决策", "Smart Decision", 1, "MAIN_BASE_SETTINGS")
    val SKIP_TH_LEVEL = SettingDef("skip_th_level", "跳过大本营等级达到", "Skip if TH level reaches", "20", "MAIN_BASE_SETTINGS")
    val AUTO_ATTACK = SettingDef("auto_attack", "自动进攻", "Auto Attack", 1, "MAIN_BASE_SETTINGS")
    val BATTLE_TIMES = SettingDef("battle_times", "对战局数", "Battle Times", "1", "MAIN_BASE_SETTINGS")
    val BATTLE_TIMES_WITH_TASKS = SettingDef("battle_times_with_tasks", "接取竞赛后对战局数", "Battle Times (with tasks)", "5", "MAIN_BASE_SETTINGS")
    val GOLD_REQUIREMENT = SettingDef("gold_requirement", "金币要求", "Gold Required", "100", "MAIN_BASE_SETTINGS")
    val ELIXIR_REQUIREMENT = SettingDef("elixir_requirement", "圣水要求", "Elixir Required", "100", "MAIN_BASE_SETTINGS")
    val DARK_ELIXIR_REQUIREMENT = SettingDef("dark_elixir_requirement", "黑油要求", "Dark Elixir Required", "10", "MAIN_BASE_SETTINGS")
    val DYNAMIC_ADJUSTMENT = SettingDef("dynamic_adjustment", "动态调节", "Dynamic Adjustment", 1, "MAIN_BASE_SETTINGS")
    val STOP_BATTLE_AFTER_FULL_RESOURCES = SettingDef(
        "stop_battle_after_full_resources", "资源满后停止对战", "Stop if resources full", 1, "MAIN_BASE_SETTINGS"
    )
    val MANUAL_TRAINING = SettingDef("manual_training", "手动配兵", "Manual Training", 0, "MAIN_BASE_SETTINGS")
    val TACTICS_MODE = SettingDef("tactics_mode", "战术设置", "Tactics Mode", "0", "MAIN_BASE_SETTINGS")
    val DONATION_SETTING = SettingDef("donation_setting", "自动捐兵", "Auto Donation", 1, "MAIN_BASE_SETTINGS")
    val REQUEST_REINFORCEMENT_SETTING = SettingDef("request_reinforcement_setting", "请求增援", "Request Reinforcements", 1, "MAIN_BASE_SETTINGS")

    // Resource farming trigger settings for auto-donation
    val DONATION_DETECT_INTERVAL = SettingDef("donation_detect_interval", "持续检测", "Detect Interval", "5", "MAIN_BASE_SETTINGS")
    val DONATION_FARMING_START_THRESHOLD = SettingDef("donation_farming_start_threshold", "当资源低于", "Start when resources <", "30", "MAIN_BASE_SETTINGS")
    val DONATION_FARMING_STOP_THRESHOLD = SettingDef("donation_farming_stop_threshold", "直到资源超过", "Stop when resources >", "80", "MAIN_BASE_SETTINGS")
    val RESEARCH_SETTING = SettingDef("research_setting", "自动研究", "Auto Research", 1, "MAIN_BASE_SETTINGS")
    val RESEARCH_LEVEL = SettingDef("research_level", "研究等级至", "Research Level to", "0", "MAIN_BASE_SETTINGS")
    val COLLECT_CLAN_CASTLE = SettingDef("collect_clan_castle", "领取部落城堡资源", "Collect Clan Castle Loot", 1, "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_AIR_SWEEPER = SettingDef("lighting_on_air_sweeper", "闪空气炮次数", "Lightning on Air Sweeper", "2", "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_AIR_DEFENCE = SettingDef("lighting_on_air_defence", "闪火箭次数", "Lightning on Air Defense", "3", "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_MORTAR = SettingDef("lighting_on_mortar", "闪迫击炮次数", "Lightning on Mortar", "4", "MAIN_BASE_SETTINGS")
    val LIGHTING_ON_WIZARD_TOWER = SettingDef("lighting_on_wizard_tower", "闪法师塔次数", "Lightning on Wizard Tower", "4", "MAIN_BASE_SETTINGS")
    val AI_DEPLOY_TROOPS = SettingDef("ai_deploy_troops", "AI下兵", "AI Deploy", 0, "MAIN_BASE_SETTINGS")
    val CHANGE_HEROES = SettingDef("change_heroes", "随机换英雄", "Randomize Heroes", 0, "MAIN_BASE_SETTINGS")

    ///////
    val BUILD_SETTING = SettingDef("build_setting", "自动建造", "Auto Build", 1, "MAIN_BASE_SETTINGS")
    val WALL_UPGRADE_SETTINGS = SettingDef("wall_upgrade_settings", "升级城墙", "Upgrade Walls", 1, "MAIN_BASE_SETTINGS")
    val INSTANT_UPGRADE = SettingDef("instant_upgrade", "用宝石升级消耗低于", "Instant Upgrade if <", 0, "MAIN_BASE_SETTINGS")
    val INSTANT_UPGRADE_THRESHOLD = SettingDef("instant_upgrade_threshold", "宝石的建筑", "Gems", "3", "MAIN_BASE_SETTINGS")

    // Fallback trigger after repeated upgrade failures.
    val UPGRADE_ANY_BUILDING_AFTER_CONSECUTIVE_FAILURES = SettingDef(
        "upgrade_any_building_after_consecutive_failures", "连续升级失败", "After consecutive failures", 0, "MAIN_BASE_SETTINGS"
    )
    val UPGRADE_ANY_BUILDING_AFTER_CONSECUTIVE_FAILURES_THRESHOLD = SettingDef(
        "upgrade_any_building_after_consecutive_failures_threshold", "次后，改为快速升级", "times, use fast upgrade", "3", "MAIN_BASE_SETTINGS"
    )

    val BATCH_WALL_UPGRADE_SETTINGS = SettingDef("batch_wall_upgrade_settings", "批量升级城墙", "Batch Upgrade Walls", 1, "MAIN_BASE_SETTINGS")
    val SAVE_WORKER = SettingDef("save_worker", "留1工人", "Save 1 Builder", 0, "MAIN_BASE_SETTINGS")
    val SAVE_WORKER_BUILD_AFTER_WALL_FULL = SettingDef("save_worker_build_after_wall_full", "城墙全满后，将保留的工人用于建造", "Use saved builder after walls full", 0, "MAIN_BASE_SETTINGS")
    val GEAR_UP_SETTINGS = SettingDef("gear_up_settings", "改装建筑", "Gear Up", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_AFTER_FAIL_WALL_UPGRADE = SettingDef("upgrade_after_fail_wall_upgrade", "升级城墙失败后建造", "Build if wall upgrade fails", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_WALL_THRESHOLD = SettingDef(
        "upgrade_wall_threshold", "金水高于以下百分比后升级城墙", "Upgrade walls if resources >", "85", "MAIN_BASE_SETTINGS"
    )
    val UPGRADE_PETS = SettingDef("upgrade_pets", "升级战宠", "Upgrade Pets", 1, "MAIN_BASE_SETTINGS")
    val STOP_BATTLE_WHEN_NO_STAR = SettingDef("stop_battle_when_no_star", "无胜利之星后停止对战", "Stop if no stars", 0, "MAIN_BASE_SETTINGS")
    val RESTART_GAME = SettingDef("restart_game", "部署后重启游戏", "Restart after deploy", 1, "MAIN_BASE_SETTINGS")
    val PLAY_TOURNAMENT = SettingDef("play_tournament", "排位对战", "Ranked Battle", 0, "MAIN_BASE_SETTINGS")
    val CHANGE_BASE = SettingDef("change_base", "调整援兵/阵型", "Adjust Reinforcements/Base", 1, "MAIN_BASE_SETTINGS")
    val SWITCH_ACCOUNT_AFTER_BATTLE = SettingDef("switch_account_after_battle", "对战后直接切号", "Switch account after battle", 0, "MAIN_BASE_SETTINGS")
    val WAIT_FOR_BATTLE = SettingDef("wait_for_battle", "等待对战", "Wait for Battle", 0, "MAIN_BASE_SETTINGS")
    val WAIT_FOR_BATTLE_AFTER_ACCEPT_MAIN_WORLD_TASK = SettingDef(
        "wait_for_battle_after_accept_main_world_task", "接取任务后等待对战", "Wait for battle after task", 1, "MAIN_BASE_SETTINGS"
    )
    val HELPER_SETTINGS = SettingDef("helper_settings", "用工人学徒和实验助手", "Use Helpers", 1, "MAIN_BASE_SETTINGS")
    val UPGRADE_RESEARCH_HELPER = SettingDef("upgrade_research_helper", "升级实验助手", "Upgrade Research Helper", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_BUILDER_APPRENTICE = SettingDef("upgrade_builder_apprentice", "升级工人学徒", "Upgrade Builder Apprentice", 0, "MAIN_BASE_SETTINGS")
    val PROSPECTOR_BLUE_TO_GOLD = SettingDef("prospector_blue_to_gold", "探矿者蓝矿转黄矿", "Prospector Blue to Gold", 0, "MAIN_BASE_SETTINGS")
    val PROSPECTOR_BLUE_TO_PURPLE = SettingDef("prospector_blue_to_purple", "探矿者蓝矿转紫矿", "Prospector Blue to Purple", 0, "MAIN_BASE_SETTINGS")
    val PROSPECTOR_PURPLE_TO_GOLD = SettingDef("prospector_purple_to_gold", "探矿者紫矿转黄矿", "Prospector Purple to Gold", 0, "MAIN_BASE_SETTINGS")
    val DO_CLAN_GAMES = SettingDef("do_clan_games", "做部落竞赛任务", "Do Clan Games", 1, "MAIN_BASE_SETTINGS")
    val CLAIM_CLAN_GAME_REWARDS = SettingDef("claim_clan_game_rewards", "领竞赛奖励", "Claim Clan Game Rewards", 0, "MAIN_BASE_SETTINGS")
    val PLAY_CLAN_WAR = SettingDef("play_clan_war", "打部落战", "Play Clan War", 0, "MAIN_BASE_SETTINGS")
    val PLAY_LEAGUE = SettingDef("play_league", "打部落联赛", "Play League", 0, "MAIN_BASE_SETTINGS")
    val PLAY_RAID = SettingDef("play_raid", "打都城突袭", "Play Raid", 1, "MAIN_BASE_SETTINGS")
    val START_LEAGUE_SETTINGS = SettingDef("start_league_settings", "发起联赛", "Start League", 0, "MAIN_BASE_SETTINGS")
    val START_CLAN_WAR_SETTINGS = SettingDef("start_clan_war_settings", "发起部落战", "Start Clan War", 0, "MAIN_BASE_SETTINGS")
    val START_CLAN_WAR = SettingDef("start_clan_war", "开部落战", "Open Clan War", 0, "MAIN_BASE_SETTINGS")
    val START_CLAN_WAR_METHOD = SettingDef("start_clan_war_method", "开启方式", "Start Method", "0", "MAIN_BASE_SETTINGS")
    val START_RAID = SettingDef("start_raid", "发起都城突袭", "Start Raid", 0, "MAIN_BASE_SETTINGS")

    ////////////
    val BUY_GOLD_ORE_WITH_RAID_MEDAL = SettingDef("buy_gold_ore_with_raid_medal", "突袭币买黄矿", "Buy Gold Ore (Raid)", 0, "MAIN_BASE_SETTINGS")
    val BUY_PURPLE_ORE_WITH_RAID_MEDAL = SettingDef("buy_purple_ore_with_raid_medal", "突袭币买紫矿", "Buy Purple Ore (Raid)", 0, "MAIN_BASE_SETTINGS")

    val BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL = SettingDef(
        "buy_clock_tower_potion_with_raid_medal", "突袭币买钟楼药水", "Buy Clock Potion (Raid)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RING_OF_WALL_WITH_RAID_MEDAL = SettingDef(
        "buy_ring_of_wall_with_raid_medal", "突袭币买壁垒之戒", "Buy Wall Ring (Raid)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESEARCH_POTION_WITH_RAID_MEDAL = SettingDef(
        "buy_research_potion_with_raid_medal", "突袭币买研究药水", "Buy Research Potion (Raid)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESOURCE_POTION_WITH_RAID_MEDAL = SettingDef(
        "buy_resource_potion_with_raid_medal", "突袭币买资源药水", "Buy Resource Potion (Raid)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL = SettingDef(
        "buy_research_potion_with_league_medal", "联赛币买研究药水", "Buy Research Potion (League)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL = SettingDef(
        "buy_builder_potion_with_league_medal", "联赛币买工人药水", "Buy Builder Potion (League)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_GOLD_ORE_WITH_EVENT_MEDAL = SettingDef("buy_gold_ore_with_event_medal", "活动币买星辉矿石", "Buy Gold Ore (Event)", 0, "MAIN_BASE_SETTINGS")
    val BUY_BUILDER_POTION_WITH_EVENT_MEDAL = SettingDef(
        "buy_builder_potion_with_event_medal", "活动币买工人药水", "Buy Builder Potion (Event)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL = SettingDef(
        "buy_new_equipment_with_event_medal", "活动币买新装备", "Buy New Equipment (Event)", 0, "MAIN_BASE_SETTINGS"
    )
    val BUY_RESEARCH_POTION_WITH_EVENT_MEDAL = SettingDef(
        "buy_research_potion_with_event_medal", "活动币买研究药水", "Buy Research Potion (Event)", 0, "MAIN_BASE_SETTINGS"
    )
    val USE_RESEARCH_POTION = SettingDef("use_research_potion", "用研究药水", "Use Research Potion", 0, "MAIN_BASE_SETTINGS")
    val SELL_RESOURCE_POTION = SettingDef("sell_resource_potion", "卖资源药水", "Sell Resource Potion", 0, "MAIN_BASE_SETTINGS")
    val USE_CLOCK_TOWER_POTION = SettingDef("use_clock_tower_potion", "用钟楼药水", "Use Clock Potion", 0, "MAIN_BASE_SETTINGS")
    val SELL_CLOCK_TOWER_POTION = SettingDef("sell_clock_tower_potion", "卖钟楼药水", "Sell Clock Potion", 0, "MAIN_BASE_SETTINGS")
    val USE_BUILDER_POTION = SettingDef("use_builder_potion", "用工人药水", "Use Builder Potion", 0, "MAIN_BASE_SETTINGS")
    val SELL_RING_OF_WALL = SettingDef("sell_ring_of_wall", "卖壁垒之戒", "Sell Wall Ring", 0, "MAIN_BASE_SETTINGS")

    //////////////
    val UPGRADE_WERA_GEAR = SettingDef("upgrade_wear_gear", "升级穿戴装备", "Upgrade Equipped Gear", 0, "MAIN_BASE_SETTINGS")
    val UPGRADE_ALL_GEAR = SettingDef("upgrade_all_gear", "升所有装备", "Upgrade All Gear", 0, "MAIN_BASE_SETTINGS")
    val REMOVE_OBSTACLES = SettingDef("remove_obstacles", "移除障碍物", "Remove Obstacles", 0, "MAIN_BASE_SETTINGS")
    val CONTINUOUS_REMOVE_OBSTACLES = SettingDef("continuous_remove_obstacles", "连续移除障碍物", "Continuous Removal", 0, "MAIN_BASE_SETTINGS")
    val REMOVE_OBSTACLES_ENHANCEMENT = SettingDef("remove_obstacles_enhancement", "增强移除", "Enhanced Removal", 0, "MAIN_BASE_SETTINGS")

    val CLAIM_TIMED_REWARDS = SettingDef("claim_timed_rewards", "领限时活动奖励", "Claim Timed Rewards", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_TOKEN_REWARDS = SettingDef("claim_token_rewards", "领令牌奖励", "Claim Token Rewards", 0, "MAIN_BASE_SETTINGS")
    val COLLECT_CAPITAL_COINS = SettingDef("collect_capital_coins", "领都城币", "Collect Capital Coins", 0, "MAIN_BASE_SETTINGS")
    val DONATE_CAPITAL_GOLD = SettingDef("donate_capital_gold", "捐都城币", "Donate Capital Gold", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_FREE_SHOP_REWARDS = SettingDef("claim_free_shop_rewards", "领每周精选免费奖励", "Claim Weekly Free Rewards", 1, "MAIN_BASE_SETTINGS")
    val AUTO_JOIN_CLAN = SettingDef("auto_join_clan", "自动加部落", "Auto Join Clan", 0, "MAIN_BASE_SETTINGS")
    val CLAIM_ACHIEVEMENT_GEMS = SettingDef("claim_achievement_gems", "领取成就宝石", "Claim Achievement Gems", 0, "MAIN_BASE_SETTINGS")
    val CLAN_TAG = SettingDef("clan_tag", "加指定部落标签(不填就随机加):", "Clan Tag:", "", "MAIN_BASE_SETTINGS")
    val USE_TEMP_ITEMS = SettingDef("use_temp_items", "使用临时物品", "Use Temp Items", 0, "MAIN_BASE_SETTINGS")
    val CLAN_JOIN_MESSAGE = SettingDef("clan_join_message", "加部落暗号:", "Join Message:", "", "MAIN_BASE_SETTINGS")
    val CREATE_CONSECUTIVE_CLANS = SettingDef("create_consecutive_clans", "创建连号部落", "Create Consecutive Clans", 0, "MAIN_BASE_SETTINGS")
    val INVITE_PLAYERS = SettingDef("invite_players", "邀请玩家", "Invite Players", 0, "MAIN_BASE_SETTINGS")
    val CLAN_NAME = SettingDef("clan_name", "部落名称:", "Clan Name:", "", "MAIN_BASE_SETTINGS")
    val CONSECUTIVE_COUNT = SettingDef("consecutive_count", "连号数量:", "Consecutive Count:", 3, "MAIN_BASE_SETTINGS")

    val ACCEPT_MAIN_WORLD_NORMAL_TASK = SettingDef("accept_main_world_normal_task", "接取主世界普通任务", "Accept Main Base Normal Task", 1, "MAIN_BASE_SETTINGS")
    val ACCEPT_MAIN_WORLD_ADVANCED_TASK = SettingDef("accept_main_world_advanced_task", "接取主世界高级任务", "Accept Main Base Advanced Task", 0, "MAIN_BASE_SETTINGS")
    val ACCEPT_BUILDER_BASE_NORMAL_TASK = SettingDef("accept_builder_base_normal_task", "接取夜世界普通任务", "Accept Builder Base Normal Task", 1, "MAIN_BASE_SETTINGS")
    val ACCEPT_BUILDER_BASE_ADVANCED_TASK = SettingDef("accept_builder_base_advanced_task", "接取夜世界高级任务", "Accept Builder Base Advanced Task", 0, "MAIN_BASE_SETTINGS")
    val STOP_CLAN_GAMES_AFTER_4000_POINTS = SettingDef(
        "stop_clan_games_after_4000_points", "积分到达4000后，停止竞赛", "Stop at 4000 points", 0, "MAIN_BASE_SETTINGS"
    )

    val all = listOf(
        CLAIM_DAILY_REWARD,
        CONFIG_REMARK,
        SMART_DECISION,
        SKIP_TH_LEVEL,
        AUTO_ATTACK,
        BATTLE_TIMES,
        BATTLE_TIMES_WITH_TASKS,
        GOLD_REQUIREMENT,
        ELIXIR_REQUIREMENT,
        DARK_ELIXIR_REQUIREMENT,
        DYNAMIC_ADJUSTMENT,
        STOP_BATTLE_AFTER_FULL_RESOURCES,
        MANUAL_TRAINING,
        TACTICS_MODE,
        DONATION_SETTING,
        REQUEST_REINFORCEMENT_SETTING,
        DONATION_DETECT_INTERVAL,
        DONATION_FARMING_START_THRESHOLD,
        DONATION_FARMING_STOP_THRESHOLD,
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
        UPGRADE_ANY_BUILDING_AFTER_CONSECUTIVE_FAILURES,
        UPGRADE_ANY_BUILDING_AFTER_CONSECUTIVE_FAILURES_THRESHOLD,
        BATCH_WALL_UPGRADE_SETTINGS,
        SAVE_WORKER,
        SAVE_WORKER_BUILD_AFTER_WALL_FULL,
        GEAR_UP_SETTINGS,
        UPGRADE_AFTER_FAIL_WALL_UPGRADE,
        UPGRADE_WALL_THRESHOLD,
        UPGRADE_PETS,
        STOP_BATTLE_WHEN_NO_STAR,
        RESTART_GAME,
        PLAY_TOURNAMENT,
        CHANGE_BASE,
        SWITCH_ACCOUNT_AFTER_BATTLE,
        WAIT_FOR_BATTLE,
        WAIT_FOR_BATTLE_AFTER_ACCEPT_MAIN_WORLD_TASK,
        HELPER_SETTINGS,
        UPGRADE_RESEARCH_HELPER,
        UPGRADE_BUILDER_APPRENTICE,
        PROSPECTOR_BLUE_TO_GOLD,
        PROSPECTOR_BLUE_TO_PURPLE,
        PROSPECTOR_PURPLE_TO_GOLD,
        DO_CLAN_GAMES,
        CLAIM_CLAN_GAME_REWARDS,
        PLAY_CLAN_WAR,
        PLAY_LEAGUE,
        PLAY_RAID,
        START_LEAGUE_SETTINGS,
        START_CLAN_WAR_SETTINGS,
        START_CLAN_WAR,
        START_CLAN_WAR_METHOD,
        START_RAID,
        BUY_GOLD_ORE_WITH_RAID_MEDAL,
        BUY_PURPLE_ORE_WITH_RAID_MEDAL,
        BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL,
        BUY_RING_OF_WALL_WITH_RAID_MEDAL,
        BUY_RESEARCH_POTION_WITH_RAID_MEDAL,
        BUY_RESOURCE_POTION_WITH_RAID_MEDAL,
        BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL,
        BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL,
        BUY_GOLD_ORE_WITH_EVENT_MEDAL,
        BUY_BUILDER_POTION_WITH_EVENT_MEDAL,
        BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL,
        BUY_RESEARCH_POTION_WITH_EVENT_MEDAL,
        USE_RESEARCH_POTION,
        SELL_RESOURCE_POTION,
        USE_CLOCK_TOWER_POTION,
        SELL_CLOCK_TOWER_POTION,
        USE_BUILDER_POTION,
        SELL_RING_OF_WALL,
        UPGRADE_WERA_GEAR,
        UPGRADE_ALL_GEAR,
        REMOVE_OBSTACLES,
        CONTINUOUS_REMOVE_OBSTACLES,
        REMOVE_OBSTACLES_ENHANCEMENT,
        CLAIM_TIMED_REWARDS,
        CLAIM_TOKEN_REWARDS,
        COLLECT_CAPITAL_COINS,
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
        CONSECUTIVE_COUNT,
        ACCEPT_MAIN_WORLD_NORMAL_TASK,
        ACCEPT_MAIN_WORLD_ADVANCED_TASK,
        ACCEPT_BUILDER_BASE_NORMAL_TASK,
        ACCEPT_BUILDER_BASE_ADVANCED_TASK,
        STOP_CLAN_GAMES_AFTER_4000_POINTS
    )
}
