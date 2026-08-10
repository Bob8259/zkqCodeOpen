package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object GlobalSettings {
    val CONFIG_COUNT = SettingDef("config_count", "配置文件数量", "Profile Count", "2", "GLOBAL_SETTINGS")
    val ACCOUNT_COUNT = SettingDef("account_count", "多开账号数量", "Account Count", "2", "GLOBAL_SETTINGS")
    val AUTO_START = SettingDef("auto_start", "开机自启(仅部分设备有效)", "Auto Start", 1, "GLOBAL_SETTINGS")
    val DISABLE_RESTART_GAME = SettingDef("disable_restart_game", "禁用重启游戏", "Disable Game Restart", 0, "GLOBAL_SETTINGS")
    val EXTRACT_VERSION = SettingDef("extract_version", "提取存档版本选择", "Extract Version", "0", "GLOBAL_SETTINGS")
    val SWITCH_ACCOUNT_VERSION = SettingDef("switch_account_version", "切号版本选择", "Switch Version", "0", "GLOBAL_SETTINGS")
    val EXTRACT_CN = SettingDef("extract_cn", "提取国服存档到此序号", "Extract CN to Slot", "1", "GLOBAL_SETTINGS")
    val EXTRACT_GLOBAL = SettingDef("extract_global", "提取国际服存档到此序号", "Extract Global to Slot", "1", "GLOBAL_SETTINGS")
    val EXTRACT_DATA_NUMBER = SettingDef("extract_data_number", "保存到此序号", "Save to Slot", "1", "GLOBAL_SETTINGS")
    val EXTRACT_DATA_CONTENT = SettingDef("extract_data_content", "内容", "Content", "", "GLOBAL_SETTINGS")
    val DEPLOY_PRESETS = SettingDef("deploy_presets", "下兵预设数据", "Deploy Preset Data", "[]", "GLOBAL_SETTINGS")

    val EMAIL = SettingDef("email", "邮箱", "Email", "", "GLOBAL_SETTINGS")
    val PASSWORD = SettingDef("password", "密码", "Password", "", "GLOBAL_SETTINGS")
    val ENTER_GAME_TIMER = SettingDef("enter_game_timer", "进入游戏计时", "Login Timer", "80", "GLOBAL_SETTINGS")
    val DELAY_MULTIPLIER = SettingDef(
        "delay_multiplier", "延时倍率(低性能设备建议设置1.5-2.5)", "Delay Multiplier", "1", "GLOBAL_SETTINGS"
    )
    val RECORD_PROGRESS = SettingDef("record_progress", "记录账号进度", "Record Progress", 1, "GLOBAL_SETTINGS")
    val AUTO_UPDATE = SettingDef("auto_update", "自动更新", "Auto Update", "2", "GLOBAL_SETTINGS")
    val UPDATE_CN_GAME = SettingDef("update_cn_game", "更新国服游戏", "Update CN Game", 0, "GLOBAL_SETTINGS")
    val MAX_DOWNLOAD_WAITING_TIME = SettingDef(
        "max_download_waiting_time", "最长下载等待时间(分)", "Maximum Download Waiting Time (m)", "60", "GLOBAL_SETTINGS"
    )
    val ENHANCED_CN_UPDATE = SettingDef("enhanced_cn_update", "增强更新", "Enhanced CN Update", "0", "GLOBAL_SETTINGS")
    val ENHANCED_CN_UPDATE_SOURCE = SettingDef("enhanced_cn_update_source", "下载渠道", "Download Channel", "0", "GLOBAL_SETTINGS")
    val BATCH_CREATE_ACCOUNT = SettingDef("batch_create_account", "批量创号", "Batch Create", 0, "GLOBAL_SETTINGS")
    val CREATE_START_ID = SettingDef("create_start_id", "创号开始序号", "Start ID", 1, "GLOBAL_SETTINGS")
    val CREATE_END_ID = SettingDef("create_end_id", "创号结束序号", "End ID", 10, "GLOBAL_SETTINGS")
    val CREATE_PREFIX = SettingDef("create_prefix", "创号前缀", "Prefix", "紫孔雀", "GLOBAL_SETTINGS")
    val ADD_SUFFIX_SETTING = SettingDef("add_suffix_setting", "添加数字后缀", "Add Suffix", 0, "GLOBAL_SETTINGS")
    val CREATE_GEM_BUILD = SettingDef("create_gem_build", "创号时宝石秒建筑", "Gem Build", 0, "GLOBAL_SETTINGS")
    val GEM_COUNT = SettingDef("gem_count", "宝石数量", "Gems", "", "GLOBAL_SETTINGS")
    val AFTER_KICK_OPTION = SettingDef("after_kick_option", "顶号后选项", "After Kick", "1", "GLOBAL_SETTINGS")
    val DEVICE_REMARK = SettingDef("device_remark", "设备备注", "Device Remark", "", "GLOBAL_SETTINGS")
    val LOG_RETENTION_DAYS = SettingDef("log_retention_days", "日志保留天数", "Log Retention (days)", "3", "GLOBAL_SETTINGS")
    val RUNTIME_SCREENSHOT = SettingDef("runtime_screenshot", "运行时截图", "Runtime Screenshot", 0, "GLOBAL_SETTINGS")
    val LANGUAGE = SettingDef("language", "语言", "Language", "zh", "GLOBAL_SETTINGS")

    // ── Push Notification (Server酱³ / WxPusher / PushPlus / Telegram) ────
    val PUSH_PROVIDER = SettingDef("push_provider", "推送平台", "Push Provider", "0", "GLOBAL_SETTINGS")
    val SERVERCHAN_SENDKEY = SettingDef("serverchan_sendkey", "Server酱³ SendKey", "Server酱³ SendKey", "", "GLOBAL_SETTINGS")
    val WXPUSHER_APP_TOKEN = SettingDef("wxpusher_app_token", "WxPusher AppToken", "WxPusher AppToken", "", "GLOBAL_SETTINGS")
    val WXPUSHER_UID = SettingDef("wxpusher_uid", "WxPusher UID", "WxPusher UID", "", "GLOBAL_SETTINGS")
    val PUSHPLUS_TOKEN = SettingDef("pushplus_token", "PushPlus Token", "PushPlus Token", "", "GLOBAL_SETTINGS")
    val TELEGRAM_BOT_TOKEN = SettingDef("telegram_bot_token", "Telegram BotToken", "Telegram BotToken", "", "GLOBAL_SETTINGS")
    val TELEGRAM_CHAT_ID = SettingDef("telegram_chat_id", "Telegram ChatID", "Telegram ChatID", "", "GLOBAL_SETTINGS")
    val ASTRBOT_BASE_URL = SettingDef("astrbot_base_url", "AstrBot 地址", "AstrBot Address", "", "GLOBAL_SETTINGS")
    val ASTRBOT_API_KEY = SettingDef("astrbot_api_key", "AstrBot API Key", "AstrBot API Key", "", "GLOBAL_SETTINGS")
    val ASTRBOT_TARGET_ID = SettingDef("astrbot_target_id", "AstrBot 目标ID", "AstrBot Target ID", "", "GLOBAL_SETTINGS")
    val ASTRBOT_MSG_TYPE = SettingDef("astrbot_msg_type", "消息类型", "Message Type", "0", "GLOBAL_SETTINGS")
    val ZKQ_ENABLED = SettingDef("zkq_enabled", "启用插件通讯", "Enable Plugin", "1", "GLOBAL_SETTINGS")
    val ZKQ_SERVER_URL = SettingDef("zkq_server_url", "插件连接地址", "Plugin Server URL", "", "GLOBAL_SETTINGS")
    val ZKQ_TOKEN = SettingDef("zkq_token", "共享密钥", "Token", "", "GLOBAL_SETTINGS")
    val SERVERCHAN_LOGIN_NOTIFY = SettingDef("serverchan_login_notify", "辅助账号掉线通知", "Kicked-out Notification", 0, "GLOBAL_SETTINGS")
    val SERVERCHAN_KICK_NOTIFY = SettingDef("serverchan_kick_notify", "进入游戏失败通知", "Kick Notification", 0, "GLOBAL_SETTINGS")
    val SERVERCHAN_WAR_NOTIFY = SettingDef("serverchan_war_notify", "部落战通知", "Clan War Notification", 0, "GLOBAL_SETTINGS")
    val SERVERCHAN_LEAGUE_NOTIFY = SettingDef("serverchan_league_notify", "联赛通知", "League Notification", 0, "GLOBAL_SETTINGS")
    val SERVERCHAN_WAR_NOTIFY_MODE = SettingDef("serverchan_war_notify_mode", "通知模式", "Notify Mode", "0", "GLOBAL_SETTINGS")
    val SERVERCHAN_LEAGUE_NOTIFY_MODE = SettingDef("serverchan_league_notify_mode", "通知模式", "Notify Mode", "0", "GLOBAL_SETTINGS")
    val SERVERCHAN_UPGRADE_NOTIFY = SettingDef("serverchan_upgrade_notify", "退出时推送升级概览", "Upgrade Overview on Exit", 0, "GLOBAL_SETTINGS")
    val SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY = SettingDef("serverchan_upgrade_schedule_notify", "即将完成推送提醒", "Advance Completion Alert", 0, "GLOBAL_SETTINGS")
    val SERVERCHAN_UPGRADE_ADVANCE = SettingDef("serverchan_upgrade_advance", "提前提醒(分钟)", "Advance Minutes", "10", "GLOBAL_SETTINGS")

    // ── Temperature Protection (温度保护) ────────────────────────────────────
    val TEMP_PROTECTION = SettingDef("temp_protection", "温度保护", "Temp Protection", 0, "GLOBAL_SETTINGS")
    val TEMP_PAUSE_THRESHOLD = SettingDef("temp_pause_threshold", "暂停温度(°C)", "Pause Temp (°C)", "45", "GLOBAL_SETTINGS")
    val TEMP_RESUME_THRESHOLD = SettingDef("temp_resume_threshold", "恢复温度(°C)", "Resume Temp (°C)", "38", "GLOBAL_SETTINGS")

    // ── Random Scheduling (随机上号) ──────────────────────────────────────────
    val RANDOM_SCHEDULING = SettingDef("random_scheduling", "随机上号", "Random Scheduling", 0, "GLOBAL_SETTINGS")

    // ── Scheduled Restart (定时重启) ───────────────────────────────────────────
    val SCHEDULED_RESTART = SettingDef("scheduled_restart", "定时重启", "Scheduled Restart", 0, "GLOBAL_SETTINGS")
    val RESTART_INTERVAL = SettingDef("restart_interval", "重启间隔(分钟)", "Restart Interval (min)", "60", "GLOBAL_SETTINGS")

    // ── Upgrade-time Scheduling (升级时间上号) ──────────────────────────────────
    val UPGRADE_TIME_SCHEDULING = SettingDef("upgrade_time_scheduling", "升级时间上号", "Upgrade-time Scheduling", 0, "GLOBAL_SETTINGS")
    val UTS_WAIT_POLL_SECONDS = SettingDef("uts_wait_poll_seconds", "等待刷新间隔(秒)", "Wait Refresh (s)", "5", "GLOBAL_SETTINGS")

    // ── Cloud Expiry Notification (云机到期通知) ────────────────────────────────
    val CLOUD_EXPIRE_NOTIFY = SettingDef("cloud_expire_notify", "云机到期通知", "Cloud Expiry Notify", 0, "GLOBAL_SETTINGS")
    val CLOUD_EXPIRE_AUTO_RESET = SettingDef("cloud_expire_auto_reset", "自动重置", "Auto Reset", 1, "GLOBAL_SETTINGS")
    val CLOUD_EXPIRE_DATE = SettingDef("cloud_expire_date", "到期日期", "Expiry Date", "", "GLOBAL_SETTINGS")
    val CLOUD_EXPIRE_RESET_DAYS = SettingDef("cloud_expire_reset_days", "自动重置(天)", "Auto Reset (Days)", "30", "GLOBAL_SETTINGS")
    val CLOUD_EXPIRE_ADVANCE_DAYS = SettingDef("cloud_expire_advance_days", "提前提醒(天)", "Advance Notice (Days)", "3", "GLOBAL_SETTINGS")

    // ── WebDAV Sync (WebDAV同步) ──────────────────────────────────────────────────
    val WEBDAV_URL = SettingDef("webdav_url", "WebDAV 服务器地址", "WebDAV Server URL", "", "GLOBAL_SETTINGS")
    val WEBDAV_USERNAME = SettingDef("webdav_username", "WebDAV 用户名", "WebDAV Username", "", "GLOBAL_SETTINGS")
    val WEBDAV_PASSWORD = SettingDef("webdav_password", "WebDAV 密码", "WebDAV Password", "", "GLOBAL_SETTINGS")

    // ── Screen Resolution (屏幕分辨率) ────────────────────────────────────────────
    val CHANGE_RESOLUTION_RUNTIME = SettingDef("change_resolution_runtime", "运行时修改分辨率", "Change Resolution During Runtime", 0, "GLOBAL_SETTINGS")

    // Battle time ratio per group
    val RS_BATTLE_RATIO_DAWN = SettingDef("rs_battle_ratio_dawn", "对战时间比例", "Battle Ratio", "80", "GLOBAL_SETTINGS")
    val RS_BATTLE_RATIO_MORNING = SettingDef("rs_battle_ratio_morning", "对战时间比例", "Battle Ratio", "80", "GLOBAL_SETTINGS")
    val RS_BATTLE_RATIO_AFTERNOON = SettingDef("rs_battle_ratio_afternoon", "对战时间比例", "Battle Ratio", "80", "GLOBAL_SETTINGS")
    val RS_BATTLE_RATIO_NIGHT = SettingDef("rs_battle_ratio_night", "对战时间比例", "Battle Ratio", "80", "GLOBAL_SETTINGS")

    // Probability to stop after config flow
    val RS_STOP_PROB_DAWN = SettingDef("rs_stop_prob_dawn", "完成流程下线概率", "Stop Probability", "50", "GLOBAL_SETTINGS")
    val RS_STOP_PROB_MORNING = SettingDef("rs_stop_prob_morning", "完成流程下线概率", "Stop Probability", "50", "GLOBAL_SETTINGS")
    val RS_STOP_PROB_AFTERNOON = SettingDef("rs_stop_prob_afternoon", "完成流程下线概率", "Stop Probability", "50", "GLOBAL_SETTINGS")
    val RS_STOP_PROB_NIGHT = SettingDef("rs_stop_prob_night", "完成流程下线概率", "Stop Probability", "50", "GLOBAL_SETTINGS")

    // Random continue battle min (minutes)
    val RS_CONTINUE_MIN_DAWN = SettingDef("rs_continue_min_dawn", "随机持续对战", "Random Battle", "5", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MIN_MORNING = SettingDef("rs_continue_min_morning", "随机持续对战", "Random Battle", "5", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MIN_AFTERNOON = SettingDef("rs_continue_min_afternoon", "随机持续对战", "Random Battle", "5", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MIN_NIGHT = SettingDef("rs_continue_min_night", "随机持续对战", "Random Battle", "5", "GLOBAL_SETTINGS")

    // Random continue battle max (minutes)
    val RS_CONTINUE_MAX_DAWN = SettingDef("rs_continue_max_dawn", "", "", "25", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MAX_MORNING = SettingDef("rs_continue_max_morning", "", "", "25", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MAX_AFTERNOON = SettingDef("rs_continue_max_afternoon", "", "", "25", "GLOBAL_SETTINGS")
    val RS_CONTINUE_MAX_NIGHT = SettingDef("rs_continue_max_night", "", "", "25", "GLOBAL_SETTINGS")

    // No worker toggle (logic skipped for now)
    val RS_NO_WORKER_DAWN = SettingDef("rs_no_worker_dawn", "主世界无工人后提前下线", "Offline if no worker", 0, "GLOBAL_SETTINGS")
    val RS_NO_WORKER_MORNING = SettingDef("rs_no_worker_morning", "主世界无工人后提前下线", "Offline if no worker", 0, "GLOBAL_SETTINGS")
    val RS_NO_WORKER_AFTERNOON = SettingDef("rs_no_worker_afternoon", "主世界无工人后提前下线", "Offline if no worker", 0, "GLOBAL_SETTINGS")
    val RS_NO_WORKER_NIGHT = SettingDef("rs_no_worker_night", "主世界无工人后提前下线", "Offline if no worker", 0, "GLOBAL_SETTINGS")

    // Full resources toggle (logic skipped for now)
    val RS_FULL_RES_DAWN = SettingDef("rs_full_res_dawn", "主世界满资源后提前下线", "Offline if full resources", 0, "GLOBAL_SETTINGS")
    val RS_FULL_RES_MORNING = SettingDef("rs_full_res_morning", "主世界满资源后提前下线", "Offline if full resources", 0, "GLOBAL_SETTINGS")
    val RS_FULL_RES_AFTERNOON = SettingDef("rs_full_res_afternoon", "主世界满资源后提前下线", "Offline if full resources", 0, "GLOBAL_SETTINGS")
    val RS_FULL_RES_NIGHT = SettingDef("rs_full_res_night", "主世界满资源后提前下线", "Offline if full resources", 0, "GLOBAL_SETTINGS")

    // Account group assignments (comma-separated account IDs)
    val RS_ACCOUNTS_DAWN = SettingDef("rs_accounts_dawn", "账号列表", "Account List", "", "GLOBAL_SETTINGS")
    val RS_ACCOUNTS_MORNING = SettingDef("rs_accounts_morning", "账号列表", "Account List", "", "GLOBAL_SETTINGS")
    val RS_ACCOUNTS_AFTERNOON = SettingDef("rs_accounts_afternoon", "账号列表", "Account List", "", "GLOBAL_SETTINGS")
    val RS_ACCOUNTS_NIGHT = SettingDef("rs_accounts_night", "账号列表", "Account List", "", "GLOBAL_SETTINGS")

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
        DEPLOY_PRESETS,
        EMAIL,
        PASSWORD,
        ENTER_GAME_TIMER,
        DELAY_MULTIPLIER,
        RECORD_PROGRESS,
        AUTO_UPDATE,
        UPDATE_CN_GAME,
        MAX_DOWNLOAD_WAITING_TIME,
        ENHANCED_CN_UPDATE,
        ENHANCED_CN_UPDATE_SOURCE,
        BATCH_CREATE_ACCOUNT,
        CREATE_START_ID,
        CREATE_END_ID,
        CREATE_PREFIX,
        ADD_SUFFIX_SETTING,
        CREATE_GEM_BUILD,
        GEM_COUNT,
        AFTER_KICK_OPTION,
        DEVICE_REMARK,
        LOG_RETENTION_DAYS,
        RUNTIME_SCREENSHOT,
        LANGUAGE,
        PUSH_PROVIDER,
        SERVERCHAN_SENDKEY,
        WXPUSHER_APP_TOKEN,
        WXPUSHER_UID,
        PUSHPLUS_TOKEN,
        TELEGRAM_BOT_TOKEN,
        TELEGRAM_CHAT_ID,
        ASTRBOT_BASE_URL,
        ASTRBOT_API_KEY,
        ASTRBOT_TARGET_ID,
        ASTRBOT_MSG_TYPE,
        ZKQ_ENABLED,
        ZKQ_SERVER_URL,
        ZKQ_TOKEN,
        SERVERCHAN_LOGIN_NOTIFY,
        SERVERCHAN_KICK_NOTIFY,
        SERVERCHAN_WAR_NOTIFY,
        SERVERCHAN_WAR_NOTIFY_MODE,
        SERVERCHAN_LEAGUE_NOTIFY,
        SERVERCHAN_LEAGUE_NOTIFY_MODE,
        SERVERCHAN_UPGRADE_NOTIFY,
        SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY,
        SERVERCHAN_UPGRADE_ADVANCE,
        TEMP_PROTECTION,
        TEMP_PAUSE_THRESHOLD,
        TEMP_RESUME_THRESHOLD,
        RANDOM_SCHEDULING,
        UPGRADE_TIME_SCHEDULING,
        UTS_WAIT_POLL_SECONDS,
        RS_BATTLE_RATIO_DAWN, RS_BATTLE_RATIO_MORNING, RS_BATTLE_RATIO_AFTERNOON, RS_BATTLE_RATIO_NIGHT,
        RS_STOP_PROB_DAWN, RS_STOP_PROB_MORNING, RS_STOP_PROB_AFTERNOON, RS_STOP_PROB_NIGHT,
        RS_CONTINUE_MIN_DAWN, RS_CONTINUE_MIN_MORNING, RS_CONTINUE_MIN_AFTERNOON, RS_CONTINUE_MIN_NIGHT,
        RS_CONTINUE_MAX_DAWN, RS_CONTINUE_MAX_MORNING, RS_CONTINUE_MAX_AFTERNOON, RS_CONTINUE_MAX_NIGHT,
        RS_NO_WORKER_DAWN, RS_NO_WORKER_MORNING, RS_NO_WORKER_AFTERNOON, RS_NO_WORKER_NIGHT,
        RS_FULL_RES_DAWN, RS_FULL_RES_MORNING, RS_FULL_RES_AFTERNOON, RS_FULL_RES_NIGHT,
        RS_ACCOUNTS_DAWN, RS_ACCOUNTS_MORNING, RS_ACCOUNTS_AFTERNOON, RS_ACCOUNTS_NIGHT,
        CLOUD_EXPIRE_NOTIFY, CLOUD_EXPIRE_AUTO_RESET, CLOUD_EXPIRE_DATE, CLOUD_EXPIRE_RESET_DAYS, CLOUD_EXPIRE_ADVANCE_DAYS,
        WEBDAV_URL, WEBDAV_USERNAME, WEBDAV_PASSWORD,
        SCHEDULED_RESTART, RESTART_INTERVAL,
        CHANGE_RESOLUTION_RUNTIME
    )
}

object AccountSettings {
    val ISOPEN = SettingDef("isopen", "开启状态", "Enabled", 0, "ACCOUNT_SETTINGS")
    val REMARK = SettingDef("remark", "备注", "Remark", "", "ACCOUNT_SETTINGS")
    val GAME_VERSION = SettingDef("game_version", "游戏版本", "Game Version", "1", "ACCOUNT_SETTINGS")
    val ACCOUNT_CONFIG = SettingDef("account_config", "配置文件序号", "Profile Slot", "1", "ACCOUNT_SETTINGS")
    val START_METHOD = SettingDef("start_method", "启动方式", "Start Method", "1", "ACCOUNT_SETTINGS")
    val CN_PATH = SettingDef("cn_path", "国服存档序号", "CN Save Slot", "", "ACCOUNT_SETTINGS")
    val GLOBAL_PATH = SettingDef("global_path", "国际服存档序号", "Global Save Slot", "", "ACCOUNT_SETTINGS")
    val DATA_CONTENT = SettingDef("data_content", "测试数据序号", "Test Data Slot", "", "ACCOUNT_SETTINGS")

    val all = listOf(
        ISOPEN, REMARK, GAME_VERSION, ACCOUNT_CONFIG, START_METHOD, CN_PATH, GLOBAL_PATH, DATA_CONTENT
    )
}
