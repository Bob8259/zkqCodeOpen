package com.coc.zkqcode.utils.database

import kotlin.reflect.full.memberProperties

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
        val EXTRACT_GLOBAL = SettingDef("extract_global", "提取国际服存档到此序号", "1", "GLOBAL_SETTINGS")

        val EMAIL = SettingDef("email", "邮箱", "", "GLOBAL_SETTINGS")
        val PASSWORD = SettingDef("password", "密码", "", "GLOBAL_SETTINGS")
        val ENTER_GAME_TIMER = SettingDef("enter_game_timer", "进入游戏计时", "80", "GLOBAL_SETTINGS")
        val DELAY_MULTIPLIER = SettingDef(
            "delay_multiplier",
            "延时倍率(低性能设备建议设置1.5-2.5)",
            "1",
            "GLOBAL_SETTINGS"
        )
        val DEBUG_MODE = SettingDef("debug_mode", "慢速调试模式", 0, "GLOBAL_SETTINGS")
        val RECORD_PROGRESS = SettingDef("record_progress", "记录账号进度", 1, "GLOBAL_SETTINGS")
        val AUTO_UPDATE = SettingDef("auto_update", "自动更新", "1", "GLOBAL_SETTINGS")
        val BATCH_CREATE_ACCOUNT = SettingDef("batch_create_account", "批量创号设置", 0, "GLOBAL_SETTINGS")
        val CREATE_START_ID = SettingDef("create_start_id", "创号开始序号", 0, "GLOBAL_SETTINGS")
        val CREATE_END_ID = SettingDef("create_end_id", "创号结束序号", "10", "GLOBAL_SETTINGS")
        val CREATE_PREFIX = SettingDef("create_prefix", "创号前缀", "紫孔雀", "GLOBAL_SETTINGS")
        val ADD_SUFFIX_SETTING = SettingDef("add_suffix_setting", "添加后缀设置", 0, "GLOBAL_SETTINGS")
        val CREATE_GEM_BUILD = SettingDef("create_gem_build", "创号时宝石秒建筑", 0, "GLOBAL_SETTINGS")
        val GEM_COUNT = SettingDef("gem_count", "宝石数量", "", "GLOBAL_SETTINGS")
        val AFTER_KICK_OPTION = SettingDef("after_kick_option", "顶号后选项", "1", "GLOBAL_SETTINGS")
        val DISCONNECT_NOTIFY = SettingDef("disconnect_notify", "掉线后通知", 0, "GLOBAL_SETTINGS")
        val DISCONNECT_SCREENSHOT = SettingDef("disconnect_screenshot", "掉线后截图", 0, "GLOBAL_SETTINGS")
        val DEVICE_REMARK = SettingDef("device_remark", "设备备注", "", "GLOBAL_SETTINGS")
        val RUNTIME_SCREENSHOT = SettingDef("runtime_screenshot", "运行时截图", 0, "GLOBAL_SETTINGS")

        val all: List<SettingDef> by lazy {
            GLOBAL_SETTINGS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(GLOBAL_SETTINGS) as? SettingDef }
        }
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

        val all: List<SettingDef> by lazy {
            ACCOUNT_SETTINGS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(ACCOUNT_SETTINGS) as? SettingDef }
        }
    }

    // --- 2. Profile Settings Definitions ---
    object MAIN_BASE_SETTINGS {
        // Common settings
        val AUTO_ATTACK = SettingDef("auto_attack", "自动进攻", 1, "MAIN_BASE_SETTINGS")
        val GOLD_REQUIREMENT = SettingDef("gold_requirement", "金币要求", "-1", "MAIN_BASE_SETTINGS")
        val ELIXIR_REQUIREMENT = SettingDef("elixir_requirement", "圣水要求", "-1", "MAIN_BASE_SETTINGS")
        val DARK_ELIXIR_REQUIREMENT = SettingDef("dark_elixir_requirement", "黑油要求", "-1", "MAIN_BASE_SETTINGS")
        val DYNAMIC_ADJUSTMENT = SettingDef("dynamic_adjustment", "动态调节", 1, "MAIN_BASE_SETTINGS")
        val SPELL_SETTING = SettingDef("spell_setting", "下法术", 1, "MAIN_BASE_SETTINGS")
        val REINFORCEMENT_SETTING = SettingDef("reinforcement_setting", "下援兵", 1, "MAIN_BASE_SETTINGS")
        val STOP_BATTLE_AFTER_FULL_RESOURCES = SettingDef(
            "stop_battle_after_full_resources",
            "资源满后停止对战",
            1,
            "MAIN_BASE_SETTINGS"
        )
        val MANUAL_TRAINING = SettingDef("manual_training", "手动练兵", 0, "MAIN_BASE_SETTINGS")
        val TACTICS_MODE = SettingDef("tactics_mode", "战术设置", "0", "MAIN_BASE_SETTINGS")
        val DONATION_SETTING = SettingDef("donation_setting", "自动捐兵", 1, "MAIN_BASE_SETTINGS")
        val REQUEST_REINFORCEMENT_SETTING = SettingDef("request_reinforcement_setting", "请求增援", 1, "MAIN_BASE_SETTINGS")
        val DONATION_TIMES = SettingDef("donation_times", "捐兵轮数", 1, "MAIN_BASE_SETTINGS")
        val RESEARCH_SETTING = SettingDef("research_setting", "自动研究", 1, "MAIN_BASE_SETTINGS")
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
        val BATCH_WALL_UPGRADE_SETTINGS = SettingDef("batch_wall_upgrade_settings", "批量升级城墙", 1, "MAIN_BASE_SETTINGS")
        val SAVE_WORKER = SettingDef("save_worker", "留1工人升级城墙", 0, "MAIN_BASE_SETTINGS")
        val BUILDING_CONVERSION_SETTINGS = SettingDef("building_conversion_settings", "改装建筑", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_AFTER_FAIL_WALL_UPGRADE = SettingDef("upgrade_after_fail_wall_upgrade", "刷墙失败后建造", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_WALL_THRESHOLD = SettingDef(
            "upgrade_wall_threshold",
            "金水高于以下百分比后刷墙",
            "85",
            "MAIN_BASE_SETTINGS"
        )
        val UPGRADE_PETS = SettingDef("upgrade_pets", "升级战宠", 1, "MAIN_BASE_SETTINGS")
        val STOP_BATTLE_WHEN_NO_STAR = SettingDef("stop_battle_when_no_star", "无胜利之星后停止对战", 0, "MAIN_BASE_SETTINGS")
        val RESTART_GAME = SettingDef("restart_game", "重启游戏", 1, "MAIN_BASE_SETTINGS")
        val PLAY_LADDER = SettingDef("play_ladder", "排位对战", 0, "MAIN_BASE_SETTINGS")
        val CHANGE_BASE = SettingDef("change_base", "切换阵型", 1, "MAIN_BASE_SETTINGS")
        val WAIT_FOR_BATTLE = SettingDef("wait_for_battle", "等待对战", 0, "MAIN_BASE_SETTINGS")
        val HELPER_SETTINGS = SettingDef("helper_settings", "用帮手", 1, "MAIN_BASE_SETTINGS")
        val UPGRADE_RESEARCH_HELPER = SettingDef("upgrade_research_helper", "升级实验助手", 0, "MAIN_BASE_SETTINGS")
        val UPGRADE_BUILDER_APPRENTICE = SettingDef("upgrade_builder_apprentice", "升级工人学徒", 0, "MAIN_BASE_SETTINGS")
        val DO_CLAN_GAMES = SettingDef("do_clan_games", "做竞赛任务", 1, "MAIN_BASE_SETTINGS")
        val CLAIM_CLAN_GAME_REWARDS = SettingDef("claim_clan_game_rewards", "领竞赛奖励", 0, "MAIN_BASE_SETTINGS")
        val PLAY_CLAN_WAR = SettingDef("play_clan_war", "打部落战", 0, "MAIN_BASE_SETTINGS")
        val PLAY_LEAGUE = SettingDef("play_league", "打联赛", 0, "MAIN_BASE_SETTINGS")
        val PLAY_RAID = SettingDef("play_raid", "打都城突袭", 1, "MAIN_BASE_SETTINGS")
        val START_LEAGUE_SETTINGS = SettingDef("start_league_settings", "发起联赛", 0, "MAIN_BASE_SETTINGS")
        val START_CLAN_WAR_SETTINGS = SettingDef("start_clan_war_settings", "发起部落战", 0, "MAIN_BASE_SETTINGS")
        val START_RAID = SettingDef("start_raid", "发起都城突袭", 0, "MAIN_BASE_SETTINGS")
        ////////////
        val BUY_STAR_ORE_WITH_RAID_MEDAL = SettingDef("buy_star_ore_with_raid_medal", "突袭币买星辉矿石", 0, "MAIN_BASE_SETTINGS")
        val BUY_CLOCK_TOWER_POTION_WITH_RAID_MEDAL = SettingDef("buy_clock_tower_potion_with_raid_medal", "突袭币买钟楼药水", 0, "MAIN_BASE_SETTINGS")
        val BUY_RING_OF_WALL_WITH_RAID_MEDAL = SettingDef("buy_ring_of_wall_with_raid_medal", "突袭币买壁垒之戒", 0, "MAIN_BASE_SETTINGS")
        val BUY_RESEARCH_POTION_WITH_RAID_MEDAL = SettingDef("buy_research_potion_with_raid_medal", "突袭币买研究药水", 0, "MAIN_BASE_SETTINGS")
        val BUY_TRAINING_POTION_WITH_RAID_MEDAL = SettingDef("buy_training_potion_with_raid_medal", "突袭币买训练药水", 0, "MAIN_BASE_SETTINGS")
        val BUY_RESEARCH_POTION_WITH_LEAGUE_MEDAL = SettingDef("buy_research_potion_with_league_medal", "联赛币买研究药水", 0, "MAIN_BASE_SETTINGS")
        val BUY_BUILDER_POTION_WITH_LEAGUE_MEDAL = SettingDef("buy_builder_potion_with_league_medal", "联赛币买工人药水", 0, "MAIN_BASE_SETTINGS")
        val BUY_STAR_ORE_WITH_EVENT_MEDAL = SettingDef("buy_star_ore_with_event_medal", "活动币买星辉矿石", 0, "MAIN_BASE_SETTINGS")
        val BUY_BUILDER_POTION_WITH_EVENT_MEDAL = SettingDef("buy_builder_potion_with_event_medal", "活动币买工人药水", 0, "MAIN_BASE_SETTINGS")
        val BUY_NEW_EQUIPMENT_WITH_EVENT_MEDAL = SettingDef("buy_new_equipment_with_event_medal", "活动币买新装备", 0, "MAIN_BASE_SETTINGS")
        val BUY_RESEARCH_POTION_WITH_EVENT_MEDAL = SettingDef("buy_research_potion_with_event_medal", "活动币买研究药水", 0, "MAIN_BASE_SETTINGS")
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
        val CLAIM_TIMED_REWARDS = SettingDef("claim_timed_rewards", "领限时活动奖励", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_TOKEN_REWARDS = SettingDef("claim_token_rewards", "领令牌奖励", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_CAPITAL_GOLD = SettingDef("claim_capital_gold", "领都城币", 0, "MAIN_BASE_SETTINGS")
        val DONATE_CAPITAL_GOLD = SettingDef("donate_capital_gold", "捐都城币", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_FREE_SHOP_REWARDS = SettingDef("claim_free_shop_rewards", "领商店免费奖励", 0, "MAIN_BASE_SETTINGS")
        val AUTO_JOIN_CLAN = SettingDef("auto_join_clan", "自动加部落", 0, "MAIN_BASE_SETTINGS")
        val CLAIM_ACHIEVEMENT_GEMS = SettingDef("claim_achievement_gems", "领成就宝石", 0, "MAIN_BASE_SETTINGS")
        val CLAN_TAG = SettingDef("clan_tag", "加指定部落标签(不填就随机加):","", "MAIN_BASE_SETTINGS")
        val USE_TEMP_ITEMS = SettingDef("use_temp_items", "使用临时物品", 0, "MAIN_BASE_SETTINGS")
        val CLAN_JOIN_MESSAGE = SettingDef("clan_join_message", "加部落暗号:", "", "MAIN_BASE_SETTINGS")
        val CREATE_CONSECUTIVE_CLANS = SettingDef("create_consecutive_clans", "创建连号部落", 0, "MAIN_BASE_SETTINGS")
        val INVITE_PLAYERS = SettingDef("invite_players", "邀请玩家", 0, "MAIN_BASE_SETTINGS")
        val CLAN_NAME = SettingDef("clan_name", "部落名称:", "", "MAIN_BASE_SETTINGS")
        val CONSECUTIVE_COUNT = SettingDef("consecutive_count", "连号数量:", 3, "MAIN_BASE_SETTINGS")

        val all: List<SettingDef> by lazy {
            MAIN_BASE_SETTINGS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(MAIN_BASE_SETTINGS) as? SettingDef }
        }
    }

    object MAIN_BASE_TROOPS_AND_SPELLS {
        // Troops
        val TROOP_BARBARIAN = SettingDef("troop_barbarian", "野蛮人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ARCHER = SettingDef("troop_archer", "弓箭手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_GIANT = SettingDef("troop_giant", "巨人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_GOBLIN = SettingDef("troop_goblin", "哥布林", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_WALL_BREAKER = SettingDef("troop_wall_breaker", "炸弹人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_BALLOON = SettingDef("troop_balloon", "气球兵", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_WIZARD = SettingDef("troop_wizard", "法师", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_HEALER = SettingDef("troop_healer", "天使", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_DRAGON = SettingDef("troop_dragon", "飞龙", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_PEKKA = SettingDef("troop_pekka", "皮卡", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_BABY_DRAGON = SettingDef("troop_baby_dragon", "飞龙宝宝", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_MINER = SettingDef("troop_miner", "矿工", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ELECTRO_DRAGON = SettingDef("troop_electro_dragon", "雷电飞龙", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_YETI = SettingDef("troop_yeti", "大雪怪", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_DRAGON_RIDER = SettingDef("troop_dragon_rider", "龙骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ELECTRO_TITAN = SettingDef("troop_electro_titan", "雷霆泰坦", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ROOT_RIDER = SettingDef("troop_root_rider", "根蔓骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_SPEAR_THROWER = SettingDef("troop_spear_thrower", "巨矛投手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_METOR_GOLEM = SettingDef("troop_metor_golem", "陨石戈仑", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        // Spells
        val SPELL_LIGHTNING = SettingDef("spell_lightning", "雷电法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_HEALING = SettingDef("spell_healing", "治疗法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_RAGE = SettingDef("spell_rage", "狂暴法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_JUMP = SettingDef("spell_jump", "弹跳法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_FREEZE = SettingDef("spell_freeze", "冰冻法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_CLONE = SettingDef("spell_clone", "镜像法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_INVISIBILITY = SettingDef("spell_invisibility", "隐形法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_RECALL = SettingDef("spell_recall", "回溯法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_REVIVE = SettingDef("spell_revive", "复苏法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_TOTEM = SettingDef("spell_totem", "图腾法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_HASTE = SettingDef("spell_haste", "急速法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_POISON = SettingDef("spell_poison", "毒药法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_EARTHQUAKE = SettingDef("spell_earthquake", "地震法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_SKELETON = SettingDef("spell_skeleton", "骷髅法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_BAT = SettingDef("spell_bat", "蝙蝠法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_OVERGROWTH = SettingDef("spell_overgrowth", "蔓生法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SPELL_IEC_BLOCK = SettingDef("spell_iec_block", "冰障法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        // Dark Elixir Troops
        val TROOP_MINION = SettingDef("troop_minion", "亡灵", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_HOG_RIDER = SettingDef("troop_hog_rider", "野猪骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_VALKYRIE = SettingDef("troop_valkyrie", "瓦基里", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_GOLEM = SettingDef("troop_golem", "戈仑石人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_WITCH = SettingDef("troop_witch", "女巫", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_LAVA_HOUND = SettingDef("troop_lava_hound", "熔岩猎犬", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_BOWLER = SettingDef("troop_bowler", "巨石投手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_ICE_GOLEM = SettingDef("troop_ice_golem", "戈仑冰人", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_HEADHUNTER = SettingDef("troop_headhunter", "英雄猎手", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_APPRENTICE_WARDEN = SettingDef("troop_apprentice_warden", "小守护者", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_DRUID = SettingDef("troop_druid", "德鲁伊", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val TROOP_FURNACE = SettingDef("troop_furnace", "烈焰熔炉", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        // Siege Machines
        val SIEGE_WALL_WRECKER = SettingDef("siege_wall_wrecker", "攻城攻城车", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_BATTLE_BLIMP = SettingDef("siege_battle_blimp", "攻城飞艇", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_STONE_SLAMMER = SettingDef("siege_stone_slammer", "攻城气球", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_BARRACKS = SettingDef("siege_barracks", "攻城训练营", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_LOG_LAUNCHER = SettingDef("siege_log_launcher", "攻城滚木车", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_FLAME_FLINGER = SettingDef("siege_flame_flinger", "攻城烈焰车", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_BATTLE_DRILL = SettingDef("siege_battle_drill", "攻城钻机", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
        val SIEGE_TROOP_LAUNCHER = SettingDef("siege_troop_launcher", "部队发射器", 1, "MAIN_BASE_TROOPS_AND_SPELLS")

        val all: List<SettingDef> by lazy {
            MAIN_BASE_TROOPS_AND_SPELLS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(MAIN_BASE_TROOPS_AND_SPELLS) as? SettingDef }
        }
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

        val all: List<SettingDef> by lazy {
            MAIN_BASE_PETS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(MAIN_BASE_PETS) as? SettingDef }
        }
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
        val DARK_ELIXIR_STORAGE = SettingDef("dark_elixir_storage", "黑油罐", 1, "MAIN_BASE_BUILDINGS")
        val XBOW = SettingDef("xbow", "十字连弩", 1, "MAIN_BASE_BUILDINGS")
        val SCATTERSHOT = SettingDef("scattershot", "投石炮", 1, "MAIN_BASE_BUILDINGS")
        val SPELL_TOWER = SettingDef("spell_tower", "法术塔", 1, "MAIN_BASE_BUILDINGS")
        val MONOLITH = SettingDef("monolith", "擎天巨柱", 1, "MAIN_BASE_BUILDINGS")
        val WIZARD_TOWER = SettingDef("wizard_tower", "法师塔", 1, "MAIN_BASE_BUILDINGS")

        val all: List<SettingDef> by lazy {
            MAIN_BASE_BUILDINGS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(MAIN_BASE_BUILDINGS) as? SettingDef }
        }
    }

    object NIGHT_BASE_SETTINGS {
        val NO_BUILDER_BASE = SettingDef("no_builder_base", "不打夜世界", 0, "NIGHT_BASE_SETTINGS")
        val BUILDER_BASE_FARMING = SettingDef("builder_base_farming", "夜世界打资源", 1, "NIGHT_BASE_SETTINGS")
        val SWITCH_ACCOUNT_AFTER_BATTLES = SettingDef("switch_account_after_battles", "每次对战以下局数后切号", 2, "NIGHT_BASE_SETTINGS")
        val STOP_WHEN_RESOURCE_FULL = SettingDef("stop_when_resource_full", "资源满后停止对战", 1, "NIGHT_BASE_SETTINGS")
        val TROPHY_PUSHING_MODE = SettingDef("trophy_pushing_mode", "上分模式", 0, "NIGHT_BASE_SETTINGS")
        val ELIXIR_CART_FARMING = SettingDef("elixir_cart_farming", "刷圣水车", 0, "NIGHT_BASE_SETTINGS")
        val BUILDER_BASE_RESEARCH = SettingDef("builder_base_research", "夜世界研究", 1, "NIGHT_BASE_SETTINGS")
        val NIGHT_BUILD_SETTING = SettingDef("night_build_setting", "自动建造", 1, "NIGHT_BASE_SETTINGS")
        val NIGHT_WALL_UPGRADE_SETTINGS = SettingDef("night_wall_upgrade_settings", "升级城墙", 1, "NIGH_BASE_SETTINGS")
        val NIGHT_REMOVE_OBSTACLES = SettingDef("night_remove_obstacles", "随缘移除障碍物", 1, "NIGH_BASE_SETTINGS")
        val NIGHT_SAVE_WORKER = SettingDef("night_save_worker", "留1工人升级城墙", 1, "NIGH_BASE_SETTINGS")

        val all: List<SettingDef> by lazy {
            NIGHT_BASE_SETTINGS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(NIGHT_BASE_SETTINGS) as? SettingDef }
        }
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
        val ELECTROFIRE_WIZARD = SettingDef("electrofire_wizard", "电火法师", 1, "MAIN_BASE_SETTINGS")

        val all: List<SettingDef> by lazy {
            NIGHT_BASE_TROOPS::class.memberProperties
                .filter { it.name != "all" }
                .mapNotNull { it.getter.call(NIGHT_BASE_TROOPS) as? SettingDef }
        }
    }
}
