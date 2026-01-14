package com.coc.zkqcode.utils.database

/**
 * The single source of truth for any setting in the app.
 */
data class SettingDef(
    val key: String, val displayName: String, val defaultValue: Any, val category: String
)

object Schema {
    // --- 1. Global Settings Definitions (Formerly basicConfigs) ---
    val GLOBAL_SETTINGS = listOf(
        SettingDef("config_count", "配置文件数量", "3", "GLOBAL_SETTINGS"),
        SettingDef("account_count", "多开账号数量", "3", "GLOBAL_SETTINGS"),
        SettingDef("auto_start", "开机自启(仅部分设备有效)", 1, "GLOBAL_SETTINGS"),
        SettingDef("extract_cn", "提取国服存档到此序号", "1", "GLOBAL_SETTINGS"),
        SettingDef("extract_global", "提取国际服存档到此序号", "1", "GLOBAL_SETTINGS"),

        SettingDef("email", "邮箱", "", "GLOBAL_SETTINGS"),
        SettingDef("password", "密码", "", "GLOBAL_SETTINGS"),
        SettingDef("enter_game_timer", "进入游戏计时", "80", "GLOBAL_SETTINGS"),
        SettingDef(
            "delay_multiplier",
            "延时倍率(低性能设备建议设置1.5-2.5)",
            "1",
            "GLOBAL_SETTINGS"
        ),
        SettingDef("debug_mode", "慢速调试模式", 0, "GLOBAL_SETTINGS"),
        SettingDef("record_progress", "记录账号进度", 1, "GLOBAL_SETTINGS"),
        SettingDef("batch_create_account", "批量创号设置", 0, "GLOBAL_SETTINGS"),
        SettingDef("create_start_id", "创号开始序号", 0, "GLOBAL_SETTINGS"),
        SettingDef("create_end_id", "创号结束序号", "10", "GLOBAL_SETTINGS"),
        SettingDef("create_prefix", "创号前缀", "紫孔雀", "GLOBAL_SETTINGS"),
        SettingDef("add_suffix_setting", "添加后缀设置", 0, "GLOBAL_SETTINGS"),
        SettingDef("create_gem_build", "创号时宝石秒建筑", 0, "GLOBAL_SETTINGS"),
        SettingDef("gem_count", "宝石数量", "", "GLOBAL_SETTINGS"),
        SettingDef("after_kick_option", "顶号后选项", "1", "GLOBAL_SETTINGS"),
        SettingDef("disconnect_notify", "掉线后通知", 0, "GLOBAL_SETTINGS"),
        SettingDef("disconnect_screenshot", "掉线后截图", 0, "GLOBAL_SETTINGS"),
        SettingDef("device_remark", "设备备注", "", "GLOBAL_SETTINGS"),
        SettingDef("runtime_screenshot", "运行时截图", 0, "GLOBAL_SETTINGS")
    )
    val ACCOUNT_SETTINGS = listOf(
        SettingDef("isopen", "开启状态", 0, "ACCOUNT_SETTINGS"),
        SettingDef("remark", "备注", "", "ACCOUNT_SETTINGS"),
        SettingDef("game_version", "游戏版本", "0", "ACCOUNT_SETTINGS"),
        SettingDef("account_config", "配置文件序号", "1", "ACCOUNT_SETTINGS"),
        SettingDef("start_method", "启动游戏方式", "1", "ACCOUNT_SETTINGS"),
        SettingDef("cn_path", "国服存档序号", "", "ACCOUNT_SETTINGS"),
        SettingDef("global_path", "国际服存档序号", "", "ACCOUNT_SETTINGS"),
        SettingDef("data_content", "数据号内容", "", "ACCOUNT_SETTINGS")
    )

    // --- 2. Profile Settings Definitions ---
    val MAIN_BASE_SETTINGS = listOf(
        // Common settings
        SettingDef("auto_attack", "自动进攻", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("gold_requirement", "金币要求", "-1", "MAIN_BASE_SETTINGS"),
        SettingDef("elixir_requirement", "圣水要求", "-1", "MAIN_BASE_SETTINGS"),
        SettingDef("dark_elixir_requirement", "黑油要求", "-1", "MAIN_BASE_SETTINGS"),
        SettingDef("dynamic_adjustment", "动态调节", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("spell_setting", "下法术设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("reinforcement_setting", "下援兵设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("super_troop_setting", "开超蛮设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("siege_machine_setting", "下机器设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef(
            "stop_battle_after_full_resources",
            "资源满后停止对战设置",
            1,
            "MAIN_BASE_SETTINGS"
        ),
        SettingDef("donation_setting", "捐兵设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("score_mode_setting", "上分模式设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("request_reinforcement_setting", "求援设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("build_setting", "MAIN_BASE_BUILDINGS", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("research_setting", "研究设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("upgrade_research_setting", "上分研究设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("upgrade_research_helper", "升级实验助手", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("upgrade_builder_apprentice", "升级工人学徒", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("stop_battle_when_no_star", "无胜利之星后停止对战", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("clan_tag", "部落标签", "", "MAIN_BASE_SETTINGS"),
        SettingDef("clan_password", "部落暗号", "", "MAIN_BASE_SETTINGS"),
        SettingDef("collect_clan_castle", "领宝库", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("lighting_on_air_sweeper", "闪空气炮次数", "2", "MAIN_BASE_SETTINGS"),
        SettingDef("lighting_on_air_defence", "闪火箭次数", "3", "MAIN_BASE_SETTINGS"),
        SettingDef("lighting_on_mortar", "闪迫击炮次数", "4", "MAIN_BASE_SETTINGS"),
        SettingDef("lighting_on_wizard_tower", "闪法师塔次数", "4", "MAIN_BASE_SETTINGS"),
        SettingDef("ai_deploy_troops", "AI下兵", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("change_heroes", "随机换英雄", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("random_deploy", "随机下兵", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("create_consecutive_clan_tag", "创建连号部落", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("create_clan_name", "部落名称", "紫孔雀部落", "MAIN_BASE_SETTINGS"),
        SettingDef("donation_times", "捐兵轮数", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("consecutive_number", "连号数量", "5", "MAIN_BASE_SETTINGS"),
        SettingDef("invite_players", "邀请玩家", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("use_temp_item", "使用临时物品", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("restart_game", "重启游戏", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("play_ladder", "排位对战", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("change_base", "切换阵型", 1, "MAIN_BASE_SETTINGS"),

        SettingDef("use_temp_item", "使用临时物品", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("restart_game", "重启游戏", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("play_ladder", "排位对战", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("change_base", "切换阵型", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("helper_settings", "帮手设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("wall_upgrade_settings", "刷墙设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("worker_settings", "留工人设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("new_wall_upgrade_settings", "新版刷墙设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("clan_games_settings", "打竞赛设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("claim_rewards_settings", "领竞赛设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("building_conversion_settings", "改装建筑设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("clan_war_settings", "部落战设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("raid_settings", "突袭设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("league_settings", "打联赛设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("start_raid_settings", "发突袭设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("start_league_settings", "发起联赛设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("start_clan_war_settings", "发起部落战设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("buy_ore_settings", "购买矿石设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("buy_research_potion_settings", "购买研究药水设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef(
            "buy_clock_tower_potion_settings",
            "购买钟楼药水设置",
            0,
            "MAIN_BASE_SETTINGS"
        ),
        SettingDef("buy_training_potion_settings", "购买训练药水设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("buy_wall_ring_settings", "购买壁垒之戒设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef(
            "league_coins_buy_research_potions",
            "联赛币买研究药水",
            0,
            "MAIN_BASE_SETTINGS"
        ),
        SettingDef(
            "league_coins_buy_builder_potions",
            "联赛币买工人药水",
            0,
            "MAIN_BASE_SETTINGS"
        ),
        SettingDef("use_research_potion_settings", "用研究药水设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef(
            "use_clock_tower_potion_settings",
            "用钟楼药水设置",
            0,
            "MAIN_BASE_SETTINGS"
        ),
        SettingDef("use_worker_potion_settings", "用工人药水设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("sell_training_potion_settings", "卖训练药水设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef(
            "sell_clock_tower_potion_settings",
            "卖钟楼药水设置",
            0,
            "MAIN_BASE_SETTINGS"
        ),
        SettingDef("sell_wall_ring_settings", "卖壁垒之戒设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("upgrade_gear_settings", "升装备设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("upgrade_all_gear_settings", "升所有装备设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("upgrade_pets_settings", "MAIN_BASE_PETS", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("remove_obstacles_settings", "除草设置", 1, "MAIN_BASE_SETTINGS"),
        SettingDef("post_failure_build", "刷墙失败后建造", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("auto_join_clan", "自动加部落", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("wait_for_battle", "等待对战", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("no_night_base", "不打夜世界", 0, "MAIN_BASE_SETTINGS"),
        // 活动设置
        SettingDef("event_reward_setting", "活动奖励设置", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("event_coin_buy_yellow_ore", "活动币买星辉矿石", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("event_coin_buy_equipment", "活动币买装备", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("event_coin_buy_worker_potion", "活动币买工人药水", 0, "MAIN_BASE_SETTINGS"),
        SettingDef("event_coin_buy_research_potion", "活动币买研究药水", 0, "MAIN_BASE_SETTINGS"),
    )

    val MAIN_BASE_TROOPS_AND_SPELLS = listOf(
        // Troops
        SettingDef("troop_barbarian", "野蛮人", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_archer", "弓箭手", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_giant", "巨人", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_goblin", "哥布林", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_wall_breaker", "炸弹人", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_balloon", "气球兵", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_wizard", "法师", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_healer", "天使", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_dragon", "飞龙", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_pekka", "皮卡", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_baby_dragon", "飞龙宝宝", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_miner", "矿工", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_electro_dragon", "雷电飞龙", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_yeti", "大雪怪", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_dragon_rider", "龙骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_electro_titan", "雷霆泰坦", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_root_rider", "根蔓骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_spear_thrower", "巨矛投手", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_metor_golem", "陨石戈仑", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),

        // Spells
        SettingDef("spell_lightning", "雷电法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_healing", "治疗法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_rage", "狂暴法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_jump", "弹跳法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_freeze", "冰冻法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_clone", "镜像法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_invisibility", "隐形法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_recall", "回溯法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_revive", "复苏法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_totem", "图腾法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_haste", "急速法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_poison", "毒药法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_earthquake", "地震法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_skeleton", "骷髅法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_bat", "蝙蝠法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_overgrowth", "蔓生法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("spell_iec_block", "冰障法术", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),

        // Dark Elixir Troops
        SettingDef("troop_minion", "亡灵", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_hog_rider", "野猪骑士", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_valkyrie", "瓦基里", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_golem", "戈仑石人", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_witch", "女巫", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_lava_hound", "熔岩猎犬", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_bowler", "巨石投手", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_ice_golem", "戈仑冰人", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_headhunter", "英雄猎手", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_apprentice_warden", "小守护者", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_druid", "德鲁伊", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("troop_furnace", "烈焰熔炉", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),

        // Siege Machines
        SettingDef("siege_wall_wrecker", "攻城攻城车", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_battle_blimp", "攻城飞艇", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_stone_slammer", "攻城气球", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_barracks", "攻城训练营", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_log_launcher", "攻城滚木车", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_flame_flinger", "攻城烈焰车", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_battle_drill", "攻城钻机", 1, "MAIN_BASE_TROOPS_AND_SPELLS"),
        SettingDef("siege_troop_launcher", "部队发射器", 1, "MAIN_BASE_TROOPS_AND_SPELLS")
    )

    val MAIN_BASE_PETS = listOf(
        //以下是战宠设置
        SettingDef("lassi", "莱希", 1, "MAIN_BASE_PETS"),
        SettingDef("electro_owl", "闪枭", 1, "MAIN_BASE_PETS"),
        SettingDef("mighty_yak", "大牦", 1, "MAIN_BASE_PETS"),
        SettingDef("unicorn", "独角", 1, "MAIN_BASE_PETS"),
        SettingDef("frosty", "冰牙", 1, "MAIN_BASE_PETS"),
        SettingDef("diggy", "地兽", 1, "MAIN_BASE_PETS"),
        SettingDef("poison_lizard", "猛蜥", 1, "MAIN_BASE_PETS"),
        SettingDef("phoenix", "凤凰", 1, "MAIN_BASE_PETS"),
        SettingDef("spirit_fox", "灵狐", 1, "MAIN_BASE_PETS"),
        SettingDef("angry_jelly", "愤怒水母", 1, "MAIN_BASE_PETS"),
        SettingDef("sneezy", "阿啾", 1, "MAIN_BASE_PETS"),
    )

    val MAIN_BASE_BUILDINGS = listOf(
        //以下是建造设置
        SettingDef("town_hall", "大本营", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("giga_tesla", "巨型特斯拉", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("giga_inferno", "巨型地狱塔", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("hero_altar", "英雄殿堂", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("laboratory", "实验室", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("gold_storage", "储金罐", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("elixir_storage", "圣水瓶", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("barracks", "训练营", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("spell_factory", "法术工厂", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("blacksmith", "铁匠铺", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("pet_house", "战宠小屋", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("army_camp", "兵营", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("barbarian_king", "蛮王", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("archer_queen", "女王", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("minion_prince", "王子", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("guardian", "守护者", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("royal_champion", "飞盾", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("clan_castle", "部落城堡", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("siege_workshop", "攻城机器工坊", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("archer_tower", "箭塔", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("cannon", "加农炮", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("mortar", "迫击炮", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("eagle_artillery", "天鹰火炮", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("dark_elixir_storage", "黑油罐", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("xbow", "十字连弩", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("scattershot", "投石炮", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("spell_tower", "法术塔", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("monolith", "擎天巨柱", 1, "MAIN_BASE_BUILDINGS"),
        SettingDef("wizard_tower", "法师塔", 1, "MAIN_BASE_BUILDINGS"),
    )

//    val NIGHT_BASE_CONFIG=listOf()
}
