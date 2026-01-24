@file:Suppress("ClassName")

package com.coc.zkqcode.utils.database

/**
 * The single source of truth for any setting in the app.
 */
data class SettingDef(
    val key: String, val displayName: String, val defaultValue: Any, val category: String
)

object Schema {
    // --- 1. Global Settings Definitions (Formerly basicConfigs) ---
    object GLOBAL_SETTINGS {
        val CONFIG_COUNT = SettingDef("config_count", "配置文件数量", "3", "GLOBAL_SETTINGS")
        val ACCOUNT_COUNT = SettingDef("account_count", "多开账号数量", "3", "GLOBAL_SETTINGS")
        val AUTO_START = SettingDef("auto_start", "开机自启(仅部分设备有效)", 1, "GLOBAL_SETTINGS")
        val EXTRACT_CN = SettingDef("extract_cn", "提取国服存档到此序号", "1", "GLOBAL_SETTINGS")
        val EXTRACT_GLOBAL =
            SettingDef("extract_global", "提取国际服存档到此序号", "1", "GLOBAL_SETTINGS")

        val EMAIL = SettingDef("email", "邮箱", "", "GLOBAL_SETTINGS")
        val PASSWORD = SettingDef("password", "密码", "", "GLOBAL_SETTINGS")
        val ENTER_GAME_TIMER =
            SettingDef("enter_game_timer", "进入游戏计时", "80", "GLOBAL_SETTINGS")
        val DELAY_MULTIPLIER = SettingDef(
            "delay_multiplier",
            "延时倍率(低性能设备建议设置1.5-2.5)",
            "1",
            "GLOBAL_SETTINGS"
        )
        val DEBUG_MODE = SettingDef("debug_mode", "慢速调试模式", 0, "GLOBAL_SETTINGS")
        val RECORD_PROGRESS = SettingDef("record_progress", "记录账号进度", 1, "GLOBAL_SETTINGS")
        val AUTO_UPDATE = SettingDef("auto_update", "自动更新", "1", "GLOBAL_SETTINGS")
        val BATCH_CREATE_ACCOUNT =
            SettingDef("batch_create_account", "批量创号设置", 0, "GLOBAL_SETTINGS")
        val CREATE_START_ID = SettingDef("create_start_id", "创号开始序号", 0, "GLOBAL_SETTINGS")
        val CREATE_END_ID = SettingDef("create_end_id", "创号结束序号", "10", "GLOBAL_SETTINGS")
        val CREATE_PREFIX = SettingDef("create_prefix", "创号前缀", "紫孔雀", "GLOBAL_SETTINGS")
        val ADD_SUFFIX_SETTING =
            SettingDef("add_suffix_setting", "添加后缀设置", 0, "GLOBAL_SETTINGS")
        val CREATE_GEM_BUILD =
            SettingDef("create_gem_build", "创号时宝石秒建筑", 0, "GLOBAL_SETTINGS")
        val GEM_COUNT = SettingDef("gem_count", "宝石数量", "", "GLOBAL_SETTINGS")
        val AFTER_KICK_OPTION =
            SettingDef("after_kick_option", "顶号后选项", "1", "GLOBAL_SETTINGS")
        val DEVICE_REMARK = SettingDef("device_remark", "设备备注", "", "GLOBAL_SETTINGS")
        val RUNTIME_SCREENSHOT =
            SettingDef("runtime_screenshot", "运行时截图", 0, "GLOBAL_SETTINGS")

        val all = listOf(
            CONFIG_COUNT,
            ACCOUNT_COUNT,
            AUTO_START,
            EXTRACT_CN,
            EXTRACT_GLOBAL,
            EMAIL,
            PASSWORD,
            ENTER_GAME_TIMER,
            DELAY_MULTIPLIER,
            DEBUG_MODE,
            RECORD_PROGRESS,
            AUTO_UPDATE,
            BATCH_CREATE_ACCOUNT,
            CREATE_START_ID,
            CREATE_END_ID,
            CREATE_PREFIX,
            ADD_SUFFIX_SETTING,
            CREATE_GEM_BUILD,
            GEM_COUNT,
            AFTER_KICK_OPTION,
            DEVICE_REMARK,
            RUNTIME_SCREENSHOT
        )
    }

    object ACCOUNT_SETTINGS {
        val ISOPEN = SettingDef("isopen", "开启状态", 0, "ACCOUNT_SETTINGS")
        val REMARK = SettingDef("remark", "备注", "", "ACCOUNT_SETTINGS")
        val GAME_VERSION = SettingDef("game_version", "游戏版本", "0", "ACCOUNT_SETTINGS")
        val ACCOUNT_CONFIG = SettingDef("account_config", "配置文件序号", "1", "ACCOUNT_SETTINGS")
        val START_METHOD = SettingDef("start_method", "启动游戏方式", "1", "ACCOUNT_SETTINGS")
        val CN_PATH = SettingDef("cn_path", "国服存档序号", "", "ACCOUNT_SETTINGS")
        val GLOBAL_PATH = SettingDef("global_path", "国际服存档序号", "", "ACCOUNT_SETTINGS")
        val DATA_CONTENT = SettingDef("data_content", "数据号内容", "", "ACCOUNT_SETTINGS")

        val all = listOf(
            ISOPEN,
            REMARK,
            GAME_VERSION,
            ACCOUNT_CONFIG,
            START_METHOD,
            CN_PATH,
            GLOBAL_PATH,
            DATA_CONTENT
        )
    }

    // --- 2. Profile Settings Definitions ---
    object MAIN_BASE_SETTINGS {
        // Common settings
        val AUTO_ATTACK = SettingDef("auto_attack", "自动进攻", 1, "MAIN_BASE_SETTINGS")
        val GOLD_REQUIREMENT =
            SettingDef("gold_requirement", "金币要求", "-1", "MAIN_BASE_SETTINGS")
        val ELIXIR_REQUIREMENT =
            SettingDef("elixir_requirement", "圣水要求", "-1", "MAIN_BASE_SETTINGS")
        val DARK_ELIXIR_REQUIREMENT =
            SettingDef("dark_elixir_requirement", "黑油要求", "-1", "MAIN_BASE_SETTINGS")
        val DYNAMIC_ADJUSTMENT =
            SettingDef("dynamic_adjustment", "动态调节", 1, "MAIN_BASE_SETTINGS")
        val STOP_BATTLE_AFTER_FULL_RESOURCES = SettingDef(
            "stop_battle_after_full_resources",
            "资源满后停止对战",
            1,
            "MAIN_BASE_SETTINGS"
        )
        val MANUAL_TRAINING = SettingDef("manual_training", "手动练兵", 0, "MAIN_BASE_SETTINGS")
        val TACTICS_MODE = SettingDef("tactics_mode", "战术设置", "0", "MAIN_BASE_SETTINGS")
        val DONATION_SETTING = SettingDef("donation_setting", "自动捐兵", 1, "MAIN_BASE_SETTINGS")
        val REQUEST_REINFORCEMENT_SETTING =
            SettingDef("request_reinforcement_setting", "请求增援", 1, "MAIN_BASE_SETTINGS")
        val DONATION_TIMES = SettingDef("donation_times", "捐兵轮数", 1, "MAIN_BASE_SETTINGS")
        val RESEARCH_SETTING = SettingDef("research_setting", "自动研究", 1, "MAIN_BASE_SETTINGS")
        val COLLECT_CLAN_CASTLE =
            SettingDef("collect_clan_castle", "领宝库", 1, "MAIN_BASE_SETTINGS")
        val LIGHTING_ON_AIR_SWEEPER =
            SettingDef("lighting_on_air_sweeper", "闪空气炮次数", "2", "MAIN_BASE_SETTINGS")
        val LIGHTING_ON_AIR_DEFENCE =
            SettingDef("lighting_on_air_defence", "闪火箭次数", "3", "MAIN_BASE_SETTINGS")
        val LIGHTING_ON_MORTAR =
            SettingDef("lighting_on_mortar", "闪迫击炮次数", "4", "MAIN_BASE_SETTINGS")
        val LIGHTING_ON_WIZARD_TOWER =
            SettingDef("lighting_on_wizard_tower", "闪法师塔次数", "4", "MAIN_BASE_SETTINGS")
        val AI_DEPLOY_TROOPS = SettingDef("ai_deploy_troops", "AI下兵", 0, "MAIN_BASE_SETTINGS")
        val CHANGE_HEROES = SettingDef("change_heroes", "随机换英雄", 0, "MAIN_BASE_SETTINGS")

        ///////
        val BUILD_SETTING = SettingDef("build_setting", "自动建造", 1, "MAIN_BASE_SETTINGS")
        val WALL_UPGRADE_SETTINGS =
            SettingDef("wall_upgrade_settings", "升级城墙", 1, "MAIN_BASE_SETTINGS")
        val BATCH_WALL_UPGRADE_SETTINGS =
            SettingDef("batch_wall_upgrade_settings", "批量升级城墙", 1, "MAIN_BASE_SETTINGS")
        val SAVE_WORKER = SettingDef("save_worker", "留1工人升级城墙", 0, "MAIN_BASE_SETTINGS")
        val BUILDING_CONVERSION_SETTINGS =
            SettingDef("building_conversion_settings", "改装建筑", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_AFTER_FAIL_WALL_UPGRADE =
            SettingDef("upgrade_after_fail_wall_upgrade", "刷墙失败后建造", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_WALL_THRESHOLD = SettingDef(
            "upgrade_wall_threshold",
            "金水高于以下百分比后刷墙",
            "85",
            "MAIN_BASE_SETTINGS"
        )
        val UPGRADE_PETS = SettingDef("upgrade_pets", "升级战宠", 1, "MAIN_BASE_SETTINGS")
        val STOP_BATTLE_WHEN_NO_STAR =
            SettingDef("stop_battle_when_no_star", "无胜利之星后停止对战", 0, "MAIN_BASE_SETTINGS")
        val RESTART_GAME = SettingDef("restart_game", "重启游戏", 1, "MAIN_BASE_SETTINGS")
        val PLAY_LADDER = SettingDef("play_ladder", "排位对战", 0, "MAIN_BASE_SETTINGS")
        val CHANGE_BASE = SettingDef("change_base", "切换阵型", 1, "MAIN_BASE_SETTINGS")
        val WAIT_FOR_BATTLE = SettingDef("wait_for_battle", "等待对战", 0, "MAIN_BASE_SETTINGS")
        val HELPER_SETTINGS = SettingDef("helper_settings", "用帮手", 1, "MAIN_BASE_SETTINGS")
        val UPGRADE_RESEARCH_HELPER =
            SettingDef("upgrade_research_helper", "升级实验助手", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_BUILDER_APPRENTICE =
            SettingDef("upgrade_builder_apprentice", "升级工人学徒", 0, "MAIN_BASE_SETTINGS")
        val DO_CLAN_GAMES = SettingDef("do_clan_games", "做竞赛任务", 1, "MAIN_BASE_SETTINGS")
        val CLAIM_CLAN_GAME_REWARDS =
            SettingDef("claim_clan_game_rewards", "领竞赛奖励", 0, "MAIN_BASE_SETTINGS")
        val PLAY_CLAN_WAR = SettingDef("play_clan_war", "打部落战", 0, "MAIN_BASE_SETTINGS")
        val PLAY_LEAGUE = SettingDef("play_league", "打联赛", 0, "MAIN_BASE_SETTINGS")
        val PLAY_RAID = SettingDef("play_raid", "打都城突袭", 1, "MAIN_BASE_SETTINGS")
        val START_LEAGUE_SETTINGS =
            SettingDef("start_league_settings", "发起联赛", 0, "MAIN_BASE_SETTINGS")
        val START_CLAN_WAR_SETTINGS =
            SettingDef("start_clan_war_settings", "发起部落战", 0, "MAIN_BASE_SETTINGS")
        val START_RAID = SettingDef("start_raid", "发起都城突袭", 0, "MAIN_BASE_SETTINGS")

        ////////////
        val BUY_STAR_ORE_WITH_RAID_MEDAL =
            SettingDef("buy_star_ore_with_raid_medal", "突袭币买星辉矿石", 0, "MAIN_BASE_SETTINGS")
        val BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL = SettingDef(
            "buy_clock_tower_potion_with_raid_medal",
            "突袭币买钟楼药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_RING_OF_WALL_WITH_RAID_MEDAL = SettingDef(
            "buy_ring_of_wall_with_raid_medal",
            "突袭币买壁垒之戒",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_RESEARCH_POTION_WITH_RAID_MEDAL = SettingDef(
            "buy_research_potion_with_raid_medal",
            "突袭币买研究药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_TRAINING_POTION_WITH_RAID_MEDAL = SettingDef(
            "buy_training_potion_with_raid_medal",
            "突袭币买训练药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL = SettingDef(
            "buy_research_potion_with_league_medal",
            "联赛币买研究药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL = SettingDef(
            "buy_builder_potion_with_league_medal",
            "联赛币买工人药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_STAR_ORE_WITH_EVENT_MEDAL =
            SettingDef("buy_star_ore_with_event_medal", "活动币买星辉矿石", 0, "MAIN_BASE_SETTINGS")
        val BUY_BUILDER_POTION_WITH_EVENT_MEDAL = SettingDef(
            "buy_builder_potion_with_event_medal",
            "活动币买工人药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL = SettingDef(
            "buy_new_equipment_with_event_medal",
            "活动币买新装备",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val BUY_RESEARCH_POTION_WITH_EVENT_MEDAL = SettingDef(
            "buy_research_potion_with_event_medal",
            "活动币买研究药水",
            0,
            "MAIN_BASE_SETTINGS"
        )
        val USE_RESEARCH_POTION =
            SettingDef("use_research_potion", "用研究药水", 0, "MAIN_BASE_SETTINGS")
        val SELL_TRAINING_POTION =
            SettingDef("sell_training_potion", "卖训练药水", 0, "MAIN_BASE_SETTINGS")
        val USE_CLOCK_TOWER_POTION =
            SettingDef("use_clock_tower_potion", "用钟楼药水", 0, "MAIN_BASE_SETTINGS")
        val SELL_CLOCK_TOWER_POTION =
            SettingDef("sell_clock_tower_potion", "卖钟楼药水", 0, "MAIN_BASE_SETTINGS")
        val USE_BUILDER_POTION =
            SettingDef("use_builder_potion", "用工人药水", 0, "MAIN_BASE_SETTINGS")
        val SELL_RING_OF_WALL =
            SettingDef("sell_ring_of_wall", "卖壁垒之戒", 0, "MAIN_BASE_SETTINGS")

        //////////////
        val UPGRADE_WEARABLE_GEAR =
            SettingDef("upgrade_wearable_gear", "升穿戴装备", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_ALL_GEAR = SettingDef("upgrade_all_gear", "升所有装备", 0, "MAIN_BASE_SETTINGS")
        val REMOVE_OBSTACLES = SettingDef("remove_obstacles", "移除障碍物", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_TIMED_REWARDS =
            SettingDef("claim_timed_rewards", "领限时活动奖励", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_TOKEN_REWARDS =
            SettingDef("claim_token_rewards", "领令牌奖励", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_CAPITAL_GOLD =
            SettingDef("claim_capital_gold", "领都城币", 0, "MAIN_BASE_SETTINGS")
        val DONATE_CAPITAL_GOLD =
            SettingDef("donate_capital_gold", "捐都城币", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_FREE_SHOP_REWARDS =
            SettingDef("claim_free_shop_rewards", "领商店免费奖励", 0, "MAIN_BASE_SETTINGS")
        val AUTO_JOIN_CLAN = SettingDef("auto_join_clan", "自动加部落", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_ACHIEVEMENT_GEMS =
            SettingDef("claim_achievement_gems", "领成就宝石", 0, "MAIN_BASE_SETTINGS")
        val CLAN_TAG =
            SettingDef("clan_tag", "加指定部落标签(不填就随机加):", "", "MAIN_BASE_SETTINGS")
        val USE_TEMP_ITEMS = SettingDef("use_temp_items", "使用临时物品", 0, "MAIN_BASE_SETTINGS")
        val CLAN_JOIN_MESSAGE =
            SettingDef("clan_join_message", "加部落暗号:", "", "MAIN_BASE_SETTINGS")
        val CREATE_CONSECUTIVE_CLANS =
            SettingDef("create_consecutive_clans", "创建连号部落", 0, "MAIN_BASE_SETTINGS")
        val INVITE_PLAYERS = SettingDef("invite_players", "邀请玩家", 0, "MAIN_BASE_SETTINGS")
        val CLAN_NAME = SettingDef("clan_name", "部落名称:", "", "MAIN_BASE_SETTINGS")
        val CONSECUTIVE_COUNT =
            SettingDef("consecutive_count", "连号数量:", 3, "MAIN_BASE_SETTINGS")

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
            COLLECT_CLAN_CASTLE,
            LIGHTING_ON_AIR_SWEEPER,
            LIGHTING_ON_AIR_DEFENCE,
            LIGHTING_ON_MORTAR,
            LIGHTING_ON_WIZARD_TOWER,
            AI_DEPLOY_TROOPS,
            CHANGE_HEROES,
            BUILD_SETTING,
            WALL_UPGRADE_SETTINGS,
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

    object MAIN_BASE_TROOPS_AND_SPELLS {
        // Troops
        val TROOP_BARBARIAN =
            SettingDef("troop_barbarian", "野蛮人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ARCHER = SettingDef("troop_archer", "弓箭手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_GIANT = SettingDef("troop_giant", "巨人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_GOBLIN = SettingDef("troop_goblin", "哥布林", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_WALL_BREAKER =
            SettingDef("troop_wall_breaker", "炸弹人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_BALLOON = SettingDef("troop_balloon", "气球兵", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_WIZARD = SettingDef("troop_wizard", "法师", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_HEALER = SettingDef("troop_healer", "天使", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_DRAGON = SettingDef("troop_dragon", "飞龙", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_PEKKA = SettingDef("troop_pekka", "皮卡", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_BABY_DRAGON =
            SettingDef("troop_baby_dragon", "飞龙宝宝", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_MINER = SettingDef("troop_miner", "矿工", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ELECTRO_DRAGON =
            SettingDef("troop_electro_dragon", "雷电飞龙", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_YETI = SettingDef("troop_yeti", "大雪怪", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_DRAGON_RIDER =
            SettingDef("troop_dragon_rider", "龙骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ELECTRO_TITAN =
            SettingDef("troop_electro_titan", "雷霆泰坦", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ROOT_RIDER =
            SettingDef("troop_root_rider", "根蔓骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_SPEAR_THROWER =
            SettingDef("troop_spear_thrower", "巨矛投手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_METOR_GOLEM =
            SettingDef("troop_metor_golem", "陨石戈仑", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        // Spells
        val SPELL_LIGHTNING =
            SettingDef("spell_lightning", "雷电法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_HEALING =
            SettingDef("spell_healing", "治疗法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_RAGE = SettingDef("spell_rage", "狂暴法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_JUMP = SettingDef("spell_jump", "弹跳法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_FREEZE = SettingDef("spell_freeze", "冰冻法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_CLONE = SettingDef("spell_clone", "镜像法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_INVISIBILITY =
            SettingDef("spell_invisibility", "隐形法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_RECALL = SettingDef("spell_recall", "回溯法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_REVIVE = SettingDef("spell_revive", "复苏法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_TOTEM = SettingDef("spell_totem", "图腾法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_HASTE = SettingDef("spell_haste", "急速法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_POISON = SettingDef("spell_poison", "毒药法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_EARTHQUAKE =
            SettingDef("spell_earthquake", "地震法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_SKELETON =
            SettingDef("spell_skeleton", "骷髅法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_BAT = SettingDef("spell_bat", "蝙蝠法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_OVERGROWTH =
            SettingDef("spell_overgrowth", "蔓生法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_IEC_BLOCK =
            SettingDef("spell_iec_block", "冰障法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        // Dark Elixir Troops
        val TROOP_MINION = SettingDef("troop_minion", "亡灵", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_HOG_RIDER =
            SettingDef("troop_hog_rider", "野猪骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_VALKYRIE =
            SettingDef("troop_valkyrie", "瓦基里", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_GOLEM = SettingDef("troop_golem", "戈仑石人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_WITCH = SettingDef("troop_witch", "女巫", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_LAVA_HOUND =
            SettingDef("troop_lava_hound", "熔岩猎犬", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_BOWLER = SettingDef("troop_bowler", "巨石投手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ICE_GOLEM =
            SettingDef("troop_ice_golem", "戈仑冰人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_HEADHUNTER =
            SettingDef("troop_headhunter", "英雄猎手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_APPRENTICE_WARDEN =
            SettingDef("troop_apprentice_warden", "小守护者", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_DRUID = SettingDef("troop_druid", "德鲁伊", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_FURNACE =
            SettingDef("troop_furnace", "烈焰熔炉", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        // Siege Machines
        val SIEGE_WALL_WRECKER =
            SettingDef("siege_wall_wrecker", "攻城攻城车", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_BATTLE_BLIMP =
            SettingDef("siege_battle_blimp", "攻城飞艇", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_STONE_SLAMMER =
            SettingDef("siege_stone_slammer", "攻城气球", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_BARRACKS =
            SettingDef("siege_barracks", "攻城训练营", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_LOG_LAUNCHER =
            SettingDef("siege_log_launcher", "攻城滚木车", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_FLAME_FLINGER =
            SettingDef("siege_flame_flinger", "攻城烈焰车", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_BATTLE_DRILL =
            SettingDef("siege_battle_drill", "攻城钻机", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_TROOP_LAUNCHER =
            SettingDef("siege_troop_launcher", "部队发射器", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        val all = listOf(
            TROOP_BARBARIAN,
            TROOP_ARCHER,
            TROOP_GIANT,
            TROOP_GOBLIN,
            TROOP_WALL_BREAKER,
            TROOP_BALLOON,
            TROOP_WIZARD,
            TROOP_HEALER,
            TROOP_DRAGON,
            TROOP_PEKKA,
            TROOP_BABY_DRAGON,
            TROOP_MINER,
            TROOP_ELECTRO_DRAGON,
            TROOP_YETI,
            TROOP_DRAGON_RIDER,
            TROOP_ELECTRO_TITAN,
            TROOP_ROOT_RIDER,
            TROOP_SPEAR_THROWER,
            TROOP_METOR_GOLEM,
            SPELL_LIGHTNING,
            SPELL_HEALING,
            SPELL_RAGE,
            SPELL_JUMP,
            SPELL_FREEZE,
            SPELL_CLONE,
            SPELL_INVISIBILITY,
            SPELL_RECALL,
            SPELL_REVIVE,
            SPELL_TOTEM,
            SPELL_HASTE,
            SPELL_POISON,
            SPELL_EARTHQUAKE,
            SPELL_SKELETON,
            SPELL_BAT,
            SPELL_OVERGROWTH,
            SPELL_IEC_BLOCK,
            TROOP_MINION,
            TROOP_HOG_RIDER,
            TROOP_VALKYRIE,
            TROOP_GOLEM,
            TROOP_WITCH,
            TROOP_LAVA_HOUND,
            TROOP_BOWLER,
            TROOP_ICE_GOLEM,
            TROOP_HEADHUNTER,
            TROOP_APPRENTICE_WARDEN,
            TROOP_DRUID,
            TROOP_FURNACE,
            SIEGE_WALL_WRECKER,
            SIEGE_BATTLE_BLIMP,
            SIEGE_STONE_SLAMMER,
            SIEGE_BARRACKS,
            SIEGE_LOG_LAUNCHER,
            SIEGE_FLAME_FLINGER,
            SIEGE_BATTLE_DRILL,
            SIEGE_TROOP_LAUNCHER
        )
    }

    object MAIN_BASE_PETS {
        //以下是战宠设置
        val LASSI = SettingDef("lassi", "莱希", 1, "MAIN_BASE_PETS")
        val ELECTRO_OWL = SettingDef("electro_owl", "闪枭", 1, "MAIN_BASE_PETS")
        val MIGHTY_YAK = SettingDef("mighty_yak", "大牦", 1, "MAIN_BASE_PETS")
        val UNICORN = SettingDef("unicorn", "独角", 1, "MAIN_BASE_PETS")
        val FROSTY = SettingDef("frosty", "冰牙", 1, "MAIN_BASE_PETS")
        val DIGGY = SettingDef("diggy", "地兽", 1, "MAIN_BASE_PETS")
        val POISON_LIZARD = SettingDef("poison_lizard", "猛蜥", 1, "MAIN_BASE_PETS")
        val PHOENIX = SettingDef("phoenix", "凤凰", 1, "MAIN_BASE_PETS")
        val SPIRIT_FOX = SettingDef("spirit_fox", "灵狐", 1, "MAIN_BASE_PETS")
        val ANGRY_JELLY = SettingDef("angry_jelly", "愤怒水母", 1, "MAIN_BASE_PETS")
        val SNEEZY = SettingDef("sneezy", "阿啾", 1, "MAIN_BASE_PETS")

        val all = listOf(
            LASSI,
            ELECTRO_OWL,
            MIGHTY_YAK,
            UNICORN,
            FROSTY,
            DIGGY,
            POISON_LIZARD,
            PHOENIX,
            SPIRIT_FOX,
            ANGRY_JELLY,
            SNEEZY
        )
    }

    object MAIN_BASE_BUILDINGS {
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
        val DARK_ELIXIR_STORAGE =
            SettingDef("dark_elixir_storage", "黑油罐", 1, "MAIN_BASE_BUILDINGS")
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
    object MAIN_BASE_BUILDING_PRIORITIES {
        //以下是建造设置
        val TOWN_HALL_PRIORITY = SettingDef("town_hall_priority", "大本营", 1, "MAIN_BASE_BUILDING_PRIORITIES")
        val GIGA_TESLA_PRIORITY = SettingDef("giga_tesla_priority", "巨型特斯拉", 2, "MAIN_BASE_BUILDING_PRIORITIES")
        val GIGA_INFERNO_PRIORITY = SettingDef("giga_inferno_priority", "巨型地狱塔", 3, "MAIN_BASE_BUILDING_PRIORITIES")
        val HERO_ALTAR_PRIORITY = SettingDef("hero_altar_priority", "英雄殿堂", 4, "MAIN_BASE_BUILDING_PRIORITIES")
        val LABORATORY_PRIORITY = SettingDef("laboratory_priority", "实验室", 5, "MAIN_BASE_BUILDING_PRIORITIES")
        val GOLD_STORAGE_PRIORITY = SettingDef("gold_storage_priority", "储金罐", 6, "MAIN_BASE_BUILDING_PRIORITIES")
        val ELIXIR_STORAGE_PRIORITY = SettingDef("elixir_storage_priority", "圣水瓶", 7, "MAIN_BASE_BUILDING_PRIORITIES")
        val BARRACKS_PRIORITY = SettingDef("barracks_priority", "训练营", 8, "MAIN_BASE_BUILDING_PRIORITIES")
        val SPELL_FACTORY_PRIORITY = SettingDef("spell_factory_priority", "法术工厂", 9, "MAIN_BASE_BUILDING_PRIORITIES")
        val BLACKSMITH_PRIORITY = SettingDef("blacksmith_priority", "铁匠铺", 10, "MAIN_BASE_BUILDING_PRIORITIES")
        val PET_HOUSE_PRIORITY = SettingDef("pet_house_priority", "战宠小屋", 11, "MAIN_BASE_BUILDING_PRIORITIES")
        val ARMY_CAMP_PRIORITY = SettingDef("army_camp_priority", "兵营", 12, "MAIN_BASE_BUILDING_PRIORITIES")
        val BARBARIAN_KING_PRIORITY = SettingDef("barbarian_king_priority", "蛮王", 13, "MAIN_BASE_BUILDING_PRIORITIES")
        val ARCHER_QUEEN_PRIORITY = SettingDef("archer_queen_priority", "女王", 14, "MAIN_BASE_BUILDING_PRIORITIES")
        val MINION_PRINCE_PRIORITY = SettingDef("minion_prince_priority", "王子", 15, "MAIN_BASE_BUILDING_PRIORITIES")
        val GUARDIAN_PRIORITY = SettingDef("guardian_priority", "守护者", 16, "MAIN_BASE_BUILDING_PRIORITIES")
        val ROYAL_CHAMPION_PRIORITY = SettingDef("royal_champion_priority", "飞盾", 17, "MAIN_BASE_BUILDING_PRIORITIES")
        val CLAN_CASTLE_PRIORITY = SettingDef("clan_castle_priority", "部落城堡", 18, "MAIN_BASE_BUILDING_PRIORITIES")
        val SIEGE_WORKSHOP_PRIORITY = SettingDef("siege_workshop_priority", "攻城机器工坊", 19, "MAIN_BASE_BUILDING_PRIORITIES")
        val ARCHER_TOWER_PRIORITY = SettingDef("archer_tower_priority", "箭塔", 20, "MAIN_BASE_BUILDING_PRIORITIES")
        val CANNON_PRIORITY = SettingDef("cannon_priority", "加农炮", 21, "MAIN_BASE_BUILDING_PRIORITIES")
        val MORTAR_PRIORITY = SettingDef("mortar_priority", "迫击炮", 22, "MAIN_BASE_BUILDING_PRIORITIES")
        val EAGLE_ARTILLERY_PRIORITY = SettingDef("eagle_artillery_priority", "天鹰火炮", 23, "MAIN_BASE_BUILDING_PRIORITIES")
        val DARK_ELIXIR_STORAGE_PRIORITY = SettingDef("dark_elixir_storage_priority", "黑油罐", 24, "MAIN_BASE_BUILDING_PRIORITIES")
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
    object NIGHT_BASE_SETTINGS {
        val NO_BUILDER_BASE = SettingDef("no_builder_base", "不打夜世界", 0, "NIGHT_BASE_SETTINGS")
        val BUILDER_BASE_FARMING =
            SettingDef("builder_base_farming", "夜世界打资源", 1, "NIGHT_BASE_SETTINGS")
        val SWITCH_ACCOUNT_AFTER_BATTLES = SettingDef(
            "switch_account_after_battles",
            "每次对战以下局数后切号",
            2,
            "NIGHT_BASE_SETTINGS"
        )
        val STOP_WHEN_RESOURCE_FULL =
            SettingDef("stop_when_resource_full", "资源满后停止对战", 1, "NIGHT_BASE_SETTINGS")
        val TROPHY_PUSHING_MODE =
            SettingDef("trophy_pushing_mode", "上分模式", 0, "NIGHT_BASE_SETTINGS")
        val ELIXIR_CART_FARMING =
            SettingDef("elixir_cart_farming", "刷圣水车", 0, "NIGHT_BASE_SETTINGS")
        val BUILDER_BASE_RESEARCH =
            SettingDef("builder_base_research", "夜世界研究", 1, "NIGHT_BASE_SETTINGS")
        val NIGHT_BUILD_SETTING =
            SettingDef("night_build_setting", "自动建造", 1, "NIGHT_BASE_SETTINGS")
        val NIGHT_WALL_UPGRADE_SETTINGS =
            SettingDef("night_wall_upgrade_settings", "升级城墙", 1, "NIGH_BASE_SETTINGS")
        val NIGHT_REMOVE_OBSTACLES =
            SettingDef("night_remove_obstacles", "随缘移除障碍物", 1, "NIGH_BASE_SETTINGS")
        val NIGHT_SAVE_WORKER =
            SettingDef("night_save_worker", "留1工人升级城墙", 1, "NIGH_BASE_SETTINGS")

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
            NIGHT_SAVE_WORKER
        )
    }

    object NIGHT_BASE_TROOPS {
        val RAGED_BARBARIAN = SettingDef("raged_barbarian", "狂暴野蛮人", 1, "MAIN_BASE_SETTINGS")
        val SNEAKY_ARCHER = SettingDef("sneaky_archer", "隐秘弓箭手", 1, "MAIN_BASE_SETTINGS")
        val BETA_MINION = SettingDef("beta_minion", "异变亡灵", 1, "MAIN_BASE_SETTINGS")
        val BOMBER = SettingDef("bomber", "夜世界炸弹兵", 1, "MAIN_BASE_SETTINGS")
        val BABY_DRAGON = SettingDef("baby_dragon", "夜世界龙宝", 1, "MAIN_BASE_SETTINGS")
        val CANNON_CART = SettingDef("cannon_cart", "加农炮战车", 1, "MAIN_BASE_SETTINGS")
        val POWER_PEKKA = SettingDef("power_pekka", "雷霆皮卡", 1, "MAIN_BASE_SETTINGS")
        val BOXER_GIANT = SettingDef("boxer_giant", "巨人拳击手", 1, "MAIN_BASE_SETTINGS")
        val NIGHT_WITCH = SettingDef("night_witch", "暗夜女巫", 1, "MAIN_BASE_SETTINGS")
        val DROP_SHIP = SettingDef("drop_ship", "骷髅气球", 1, "MAIN_BASE_SETTINGS")
        val HOG_GLIDER = SettingDef("hog_glider", "飞猪骑士", 1, "MAIN_BASE_SETTINGS")
        val ELECTROFIRE_WIZARD =
            SettingDef("electrofire_wizard", "电火法师", 1, "MAIN_BASE_SETTINGS")

        val all = listOf(
            RAGED_BARBARIAN,
            SNEAKY_ARCHER,
            BETA_MINION,
            BOMBER,
            BABY_DRAGON,
            CANNON_CART,
            POWER_PEKKA,
            BOXER_GIANT,
            NIGHT_WITCH,
            DROP_SHIP,
            HOG_GLIDER,
            ELECTROFIRE_WIZARD
        )
    }

    object NIGHT_BASE_BUILDINGS {
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

    object NIGHT_BASE_BUILDINGS_PRIORITY {
        // 资源与核心建筑
        val BUILDER_HALL_PRIORITY = SettingDef("builder_hall_priority", "大本营", 1, "NIGHT_BASE_SETTINGS_PRIORITY")
        val GEM_MINE_PRIORITY = SettingDef("gem_mine_priority", "宝石矿井", 2, "NIGHT_BASE_SETTINGS_PRIORITY")
        val CLOCK_TOWER_PRIORITY = SettingDef("clock_tower_priority", "时光钟楼", 3, "NIGHT_BASE_SETTINGS_PRIORITY")
        val STAR_LABORATORY_PRIORITY = SettingDef("star_laboratory_priority", "星空实验室", 4, "NIGHT_BASE_SETTINGS_PRIORITY")

        // 【修改项】添加了前缀
        val NIGHT_GOLD_STORAGE_PRIORITY = SettingDef("night_gold_storage_priority", "储金罐", 5, "NIGHT_BASE_SETTINGS_PRIORITY")
        val NIGHT_ELIXIR_STORAGE_PRIORITY = SettingDef("night_elixir_storage_priority", "圣水瓶", 6, "NIGHT_BASE_SETTINGS_PRIORITY")

        // 军队建筑
        val BUILDER_BARRACKS_PRIORITY = SettingDef("builder_barracks_priority", "训练营", 7, "NIGHT_BASE_SETTINGS_PRIORITY")

        // 英雄/机器
        val BATTLE_MACHINE_PRIORITY = SettingDef("battle_machine_priority", "战争机器", 8, "NIGHT_BASE_SETTINGS_PRIORITY")
        val BATTLE_COPTER_PRIORITY = SettingDef("battle_copter_priority", "战斗直升机", 9, "NIGHT_BASE_SETTINGS_PRIORITY")

        // 防御建筑
        val MULTI_MORTAR_PRIORITY = SettingDef("multi_mortar_priority", "多管迫击炮", 10, "NIGHT_BASE_SETTINGS_PRIORITY")

        // 【修改项】添加了前缀
        val NIGHT_ARCHER_TOWER_PRIORITY = SettingDef("night_archer_tower_priority", "夜世界箭塔", 11, "NIGHT_BASE_SETTINGS_PRIORITY")

        val DOUBLE_CANNON_PRIORITY = SettingDef("double_cannon_priority", "双管加农炮", 12, "NIGHT_BASE_SETTINGS_PRIORITY")

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
}
