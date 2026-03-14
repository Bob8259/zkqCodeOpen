
package com.coc.zkqcode.jar.ui.schema.details

import com.coc.zkqcode.jar.ui.schema.SettingDef

object GlobalSettings {
    val CONFIG_COUNT = SettingDef("config_count", "配置文件数量", "3", "GLOBAL_SETTINGS")
    val ACCOUNT_COUNT = SettingDef("account_count", "多开账号数量", "3", "GLOBAL_SETTINGS")
    val AUTO_START = SettingDef("auto_start", "开机自启(仅部分设备有效)", 1, "GLOBAL_SETTINGS")
    val EXTRACT_CN = SettingDef("extract_cn", "提取国服存档到此序号", "1", "GLOBAL_SETTINGS")
    val EXTRACT_GLOBAL = SettingDef("extract_global", "提取国际服存档到此序号", "1", "GLOBAL_SETTINGS")

    val EMAIL = SettingDef("email", "邮箱", "", "GLOBAL_SETTINGS")
    val PASSWORD = SettingDef("password", "密码", "", "GLOBAL_SETTINGS")
    val ENTER_GAME_TIMER = SettingDef("enter_game_timer", "进入游戏计时", "80", "GLOBAL_SETTINGS")
    val DELAY_MULTIPLIER = SettingDef(
        "delay_multiplier", "延时倍率(低性能设备建议设置1.5-2.5)", "1", "GLOBAL_SETTINGS"
    )
    val DEBUG_MODE = SettingDef("debug_mode", "慢速调试模式", 0, "GLOBAL_SETTINGS")
    val RECORD_PROGRESS = SettingDef("record_progress", "记录账号进度", 1, "GLOBAL_SETTINGS")
    val AUTO_UPDATE = SettingDef("auto_update", "自动更新", "1", "GLOBAL_SETTINGS")
    val BATCH_CREATE_ACCOUNT = SettingDef("batch_create_account", "批量创号", 0, "GLOBAL_SETTINGS")
    val CREATE_START_ID = SettingDef("create_start_id", "创号开始序号", 1, "GLOBAL_SETTINGS")
    val CREATE_END_ID = SettingDef("create_end_id", "创号结束序号", 10, "GLOBAL_SETTINGS")
    val CREATE_PREFIX = SettingDef("create_prefix", "创号前缀", "紫孔雀", "GLOBAL_SETTINGS")
    val ADD_SUFFIX_SETTING = SettingDef("add_suffix_setting", "添加后缀", 0, "GLOBAL_SETTINGS")
    val CREATE_GEM_BUILD = SettingDef("create_gem_build", "创号时宝石秒建筑", 0, "GLOBAL_SETTINGS")
    val GEM_COUNT = SettingDef("gem_count", "宝石数量", "", "GLOBAL_SETTINGS")
    val AFTER_KICK_OPTION = SettingDef("after_kick_option", "顶号后选项", "1", "GLOBAL_SETTINGS")
    val DEVICE_REMARK = SettingDef("device_remark", "设备备注", "", "GLOBAL_SETTINGS")
    val RUNTIME_SCREENSHOT = SettingDef("runtime_screenshot", "运行时截图", 0, "GLOBAL_SETTINGS")

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

object AccountSettings {
    val ISOPEN = SettingDef("isopen", "开启状态", 0, "ACCOUNT_SETTINGS")
    val REMARK = SettingDef("remark", "备注", "", "ACCOUNT_SETTINGS")
    val GAME_VERSION = SettingDef("game_version", "游戏版本", "0", "ACCOUNT_SETTINGS")
    val ACCOUNT_CONFIG = SettingDef("account_config", "配置文件序号", "1", "ACCOUNT_SETTINGS")
    val START_METHOD = SettingDef("start_method", "启动游戏方式", "0", "ACCOUNT_SETTINGS")
    val CN_PATH = SettingDef("cn_path", "国服存档序号", "", "ACCOUNT_SETTINGS")
    val GLOBAL_PATH = SettingDef("global_path", "国际服存档序号", "", "ACCOUNT_SETTINGS")
    val DATA_CONTENT = SettingDef("data_content", "数据号内容", "", "ACCOUNT_SETTINGS")

    val all = listOf(
        ISOPEN, REMARK, GAME_VERSION, ACCOUNT_CONFIG, START_METHOD, CN_PATH, GLOBAL_PATH, DATA_CONTENT
    )
}
