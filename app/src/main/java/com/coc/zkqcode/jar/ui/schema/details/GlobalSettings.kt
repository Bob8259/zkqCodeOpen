package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object GlobalSettings {
    val CONFIG_COUNT = SettingDef("config_count", "配置文件数量", "2", "GLOBAL_SETTINGS")
    val ACCOUNT_COUNT = SettingDef("account_count", "多开账号数量", "2", "GLOBAL_SETTINGS")
    val AUTO_START = SettingDef("auto_start", "开机自启(仅部分设备有效)", 1, "GLOBAL_SETTINGS")
    val DISABLE_RESTART_GAME = SettingDef("disable_restart_game", "禁用重启游戏", 0, "GLOBAL_SETTINGS")
    val EXTRACT_VERSION = SettingDef("extract_version", "提取存档版本选择", "0", "GLOBAL_SETTINGS")
    val SWITCH_ACCOUNT_VERSION = SettingDef("switch_account_version", "切号版本选择", "0", "GLOBAL_SETTINGS")
    val EXTRACT_CN = SettingDef("extract_cn", "提取国服存档到此序号", "1", "GLOBAL_SETTINGS")
    val EXTRACT_GLOBAL = SettingDef("extract_global", "提取国际服存档到此序号", "1", "GLOBAL_SETTINGS")
    val EXTRACT_DATA_NUMBER = SettingDef("extract_data_number", "保存到此序号", "1", "GLOBAL_SETTINGS")
    val EXTRACT_DATA_CONTENT = SettingDef("extract_data_content", "内容", "", "GLOBAL_SETTINGS")

    val EMAIL = SettingDef("email", "邮箱", "free@zkq", "GLOBAL_SETTINGS")
    val PASSWORD = SettingDef("password", "密码", "free_for_testing", "GLOBAL_SETTINGS")
    val ENTER_GAME_TIMER = SettingDef("enter_game_timer", "进入游戏计时", "80", "GLOBAL_SETTINGS")
    val DELAY_MULTIPLIER = SettingDef(
        "delay_multiplier", "延时倍率(低性能设备建议设置1.5-2.5)", "1", "GLOBAL_SETTINGS"
    )
    val RECORD_PROGRESS = SettingDef("record_progress", "记录账号进度", 1, "GLOBAL_SETTINGS")
    val AUTO_UPDATE = SettingDef("auto_update", "自动更新", "2", "GLOBAL_SETTINGS")
    val BATCH_CREATE_ACCOUNT = SettingDef("batch_create_account", "批量创号", 0, "GLOBAL_SETTINGS")
    val CREATE_START_ID = SettingDef("create_start_id", "创号开始序号", 1, "GLOBAL_SETTINGS")
    val CREATE_END_ID = SettingDef("create_end_id", "创号结束序号", 10, "GLOBAL_SETTINGS")
    val CREATE_PREFIX = SettingDef("create_prefix", "创号前缀", "紫孔雀", "GLOBAL_SETTINGS")
    val ADD_SUFFIX_SETTING = SettingDef("add_suffix_setting", "添加数字后缀", 0, "GLOBAL_SETTINGS")
    val CREATE_GEM_BUILD = SettingDef("create_gem_build", "创号时宝石秒建筑", 0, "GLOBAL_SETTINGS")
    val GEM_COUNT = SettingDef("gem_count", "宝石数量", "", "GLOBAL_SETTINGS")
    val AFTER_KICK_OPTION = SettingDef("after_kick_option", "顶号后选项", "1", "GLOBAL_SETTINGS")
    val DEVICE_REMARK = SettingDef("device_remark", "设备备注", "", "GLOBAL_SETTINGS")
    val RUNTIME_SCREENSHOT = SettingDef("runtime_screenshot", "运行时截图", 0, "GLOBAL_SETTINGS")

    // ── Temperature Protection (温度保护) ────────────────────────────────────
    val TEMP_PROTECTION = SettingDef("temp_protection", "温度保护", 0, "GLOBAL_SETTINGS")
    val TEMP_PAUSE_THRESHOLD = SettingDef("temp_pause_threshold", "暂停温度(°C)", "45", "GLOBAL_SETTINGS")
    val TEMP_RESUME_THRESHOLD = SettingDef("temp_resume_threshold", "恢复温度(°C)", "38", "GLOBAL_SETTINGS")

    // ── Random Scheduling (随机上号) ──────────────────────────────────────────
    val RANDOM_SCHEDULING = SettingDef("random_scheduling", "随机上号", 0, "GLOBAL_SETTINGS")

    // ── Scheduled Restart (定时重启) ───────────────────────────────────────────
    val SCHEDULED_RESTART = SettingDef("scheduled_restart", "定时重启设备", 0, "GLOBAL_SETTINGS")
    val RESTART_INTERVAL = SettingDef("restart_interval", "重启间隔(分钟)", "60", "GLOBAL_SETTINGS")

    // Battle time ratio per group
    val RS_BATTLE_RATIO_DAWN = SettingDef("rs_battle_ratio_dawn", "对战时间比例", "80", "GLOBAL_SETTINGS")
    val RS_BATTLE_RATIO_MORNING = SettingDef("rs_battle_ratio_morning", "对战时间比例", "80", "GLOBAL_SETTINGS")
    val RS_BATTLE_RATIO_AFTERNOON = SettingDef("rs_battle_ratio_afternoon", "对战时间比例", "80", "GLOBAL_SETTINGS")
    val RS_BATTLE_RATIO_NIGHT = SettingDef("rs_battle_ratio_night", "对战时间比例", "80", "GLOBAL_SETTINGS")

    // Probability to stop after config flow
    val RS_STOP_PROB_DAWN = SettingDef("rs_stop_prob_dawn", "完成流程下线概率", "50", "GLOBAL_SETTINGS")
    val RS_STOP_PROB_MORNING = SettingDef("rs_stop_prob_morning", "完成流程下线概率", "50", "GLOBAL_SETTINGS")
    val RS_STOP_PROB_AFTERNOON = SettingDef("rs_stop_prob_afternoon", "完成流程下线概率", "50", "GLOBAL_SETTINGS")
    val RS_STOP_PROB_NIGHT = SettingDef("rs_stop_prob_night", "完成流程下线概率", "50", "GLOBAL_SETTINGS")

    // Random continue battle min (minutes)
    val RS_CONTINUE_MIN_DAWN = SettingDef("rs_continue_min_dawn", "随机持续对战", "5", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MIN_MORNING = SettingDef("rs_continue_min_morning", "随机持续对战", "5", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MIN_AFTERNOON = SettingDef("rs_continue_min_afternoon", "随机持续对战", "5", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MIN_NIGHT = SettingDef("rs_continue_min_night", "随机持续对战", "5", "GLOBAL_SETTINGS")

    // Random continue battle max (minutes)
    val RS_CONTINUE_MAX_DAWN = SettingDef("rs_continue_max_dawn", "", "25", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MAX_MORNING = SettingDef("rs_continue_max_morning", "", "25", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MAX_AFTERNOON = SettingDef("rs_continue_max_afternoon", "", "25", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MAX_NIGHT = SettingDef("rs_continue_max_night", "", "25", "GLOBAL_SETTINGS")

    // No worker toggle (logic skipped for now)
    val RS_NO_WORKER_DAWN = SettingDef("rs_no_worker_dawn", "主世界无工人后提前下线", 0, "GLOBAL_SETTINGS")
    val RS_NO_WORKER_MORNING = SettingDef("rs_no_worker_morning", "主世界无工人后提前下线", 0, "GLOBAL_SETTINGS")
    val RS_NO_WORKER_AFTERNOON = SettingDef("rs_no_worker_afternoon", "主世界无工人后提前下线", 0, "GLOBAL_SETTINGS")
    val RS_NO_WORKER_NIGHT = SettingDef("rs_no_worker_night", "主世界无工人后提前下线", 0, "GLOBAL_SETTINGS")

    // Full resources toggle (logic skipped for now)
    val RS_FULL_RES_DAWN = SettingDef("rs_full_res_dawn", "主世界满资源后提前下线", 0, "GLOBAL_SETTINGS")
    val RS_FULL_RES_MORNING = SettingDef("rs_full_res_morning", "主世界满资源后提前下线", 0, "GLOBAL_SETTINGS")
    val RS_FULL_RES_AFTERNOON = SettingDef("rs_full_res_afternoon", "主世界满资源后提前下线", 0, "GLOBAL_SETTINGS")
    val RS_FULL_RES_NIGHT = SettingDef("rs_full_res_night", "主世界满资源后提前下线", 0, "GLOBAL_SETTINGS")

    // Account group assignments (comma-separated account IDs)
    val RS_ACCOUNTS_DAWN = SettingDef("rs_accounts_dawn", "账号列表", "", "GLOBAL_SETTINGS")
    val RS_ACCOUNTS_MORNING = SettingDef("rs_accounts_morning", "账号列表", "", "GLOBAL_SETTINGS")
    val RS_ACCOUNTS_AFTERNOON = SettingDef("rs_accounts_afternoon", "账号列表", "", "GLOBAL_SETTINGS")
    val RS_ACCOUNTS_NIGHT = SettingDef("rs_accounts_night", "账号列表", "", "GLOBAL_SETTINGS")

    val all = listOf(
        CONFIG_COUNT,
        ACCOUNT_COUNT,
        AUTO_START,
        DISABLE_RESTART_GAME,
        EXTRACT_VERSION,
        SWITCH_ACCOUNT_VERSION,
        EXTRACT_CN,
        EXTRACT_GLOBAL,
        EXTRACT_DATA_NUMBER,
        EXTRACT_DATA_CONTENT,
        EMAIL,
        PASSWORD,
        ENTER_GAME_TIMER,
        DELAY_MULTIPLIER,
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
        RUNTIME_SCREENSHOT,
        TEMP_PROTECTION,
        TEMP_PAUSE_THRESHOLD,
        TEMP_RESUME_THRESHOLD,
        RANDOM_SCHEDULING,
        RS_BATTLE_RATIO_DAWN, RS_BATTLE_RATIO_MORNING, RS_BATTLE_RATIO_AFTERNOON, RS_BATTLE_RATIO_NIGHT,
        RS_STOP_PROB_DAWN, RS_STOP_PROB_MORNING, RS_STOP_PROB_AFTERNOON, RS_STOP_PROB_NIGHT,
        RS_CONTINUE_MIN_DAWN, RS_CONTINUE_MIN_MORNING, RS_CONTINUE_MIN_AFTERNOON, RS_CONTINUE_MIN_NIGHT,
        RS_CONTINUE_MAX_DAWN, RS_CONTINUE_MAX_MORNING, RS_CONTINUE_MAX_AFTERNOON, RS_CONTINUE_MAX_NIGHT,
        RS_NO_WORKER_DAWN, RS_NO_WORKER_MORNING, RS_NO_WORKER_AFTERNOON, RS_NO_WORKER_NIGHT,
        RS_FULL_RES_DAWN, RS_FULL_RES_MORNING, RS_FULL_RES_AFTERNOON, RS_FULL_RES_NIGHT,
        RS_ACCOUNTS_DAWN, RS_ACCOUNTS_MORNING, RS_ACCOUNTS_AFTERNOON, RS_ACCOUNTS_NIGHT,
        SCHEDULED_RESTART, RESTART_INTERVAL
    )
}

object AccountSettings {
    val ISOPEN = SettingDef("isopen", "开启状态", 0, "ACCOUNT_SETTINGS")
    val REMARK = SettingDef("remark", "备注", "", "ACCOUNT_SETTINGS")
    val GAME_VERSION = SettingDef("game_version", "游戏版本", "0", "ACCOUNT_SETTINGS")
    val ACCOUNT_CONFIG = SettingDef("account_config", "配置文件序号", "1", "ACCOUNT_SETTINGS")
    val START_METHOD = SettingDef("start_method", "启动方式", "0", "ACCOUNT_SETTINGS")
    val CN_PATH = SettingDef("cn_path", "国服存档序号", "", "ACCOUNT_SETTINGS")
    val GLOBAL_PATH = SettingDef("global_path", "国际服存档序号", "", "ACCOUNT_SETTINGS")
    val DATA_CONTENT = SettingDef("data_content", "测试数据序号", "", "ACCOUNT_SETTINGS")

    val all = listOf(
        ISOPEN, REMARK, GAME_VERSION, ACCOUNT_CONFIG, START_METHOD, CN_PATH, GLOBAL_PATH, DATA_CONTENT
    )
}
