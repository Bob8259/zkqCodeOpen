# 已注释掉的UI内容

## Login.kt（登录功能）

- 登录功能（用户手动注释）

## HomeScreen.kt（上号器 / 主页设置）

- 上号器功能（用户手动注释）
- 延时倍率（DELAY_MULTIPLIER）
- 慢速调试模式（DEBUG_MODE）
- 自动更新（AUTO_UPDATE）
- 批量创号入口（BatchCreateAccount 调用）

## BatchCreateAccount.kt（特殊设置）

- 所有特殊设置文字提示
- 批量创号（BATCH_CREATE_ACCOUNT）
- 创号开始序号（CREATE_START_ID）
- 创号结束序号（CREATE_END_ID）
- 创号前缀（CREATE_PREFIX）
- 创号时宝石秒建筑（CREATE_GEM_BUILD）
- 添加后缀（ADD_SUFFIX_SETTING）

## MainBaseConfigs.kt（主世界设置）

### 对战相关
- 资源满后停止对战（STOP_BATTLE_AFTER_FULL_RESOURCES）
- 无胜利之星后停止对战（STOP_BATTLE_WHEN_NO_STAR）
- 排位对战（PLAY_LADDER）及切换阵型（CHANGE_BASE）
- 等待对战（WAIT_FOR_BATTLE）
- 部署后重启游戏（RESTART_GAME）
- 手动练兵（MANUAL_TRAINING）
- 随机换英雄（CHANGE_HEROES）

### 战术与AI
- 战术设置（TACTICS_MODE）
- AI下兵（AI_DEPLOY_TROOPS）及闪电相关设置（闪空气炮、闪火箭、闪法师塔、闪迫击炮）
（使用雷电飞龙，雷电雷龙，图腾飞龙，图腾治疗根蔓，低本用胖弓蛮雷电
关键建筑：地狱塔，投石炮，黑油塔，防空火箭，空气炮，复仇塔。
）
### 捐兵与宝库
- 自动捐兵（DONATION_SETTING）及捐兵轮数（DONATION_TIMES）
- 请求增援（REQUEST_REINFORCEMENT_SETTING）
- 领宝库（COLLECT_CLAN_CASTLE）

### 城墙相关
- 改装建筑（BUILDING_CONVERSION_SETTINGS）
- 升级城墙失败后建造（UPGRADE_AFTER_FAIL_WALL_UPGRADE）

### 升级战宠及之后的所有主世界UI
- 升级战宠（UPGRADE_PETS）及战宠选择列表
- 用帮手（HELPER_SETTINGS）
- 升级实验助手（UPGRADE_RESEARCH_HELPER）
- 升级工人学徒（UPGRADE_BUILDER_APPRENTICE）
- 做竞赛任务（DO_CLAN_GAMES）
- 领竞赛奖励（CLAIM_CLAN_GAME_REWARDS）
- 打部落战（PLAY_CLAN_WAR）
- 打联赛（PLAY_LEAGUE）
- 打都城突袭（PLAY_RAID）
- 发起部落战（START_CLAN_WAR_SETTINGS）
- 发起联赛（START_LEAGUE_SETTINGS）
- 发起都城突袭（START_RAID）
- 商店购买相关（突袭币、联赛币、活动币购买各种药水/矿石/戒指等）
- 用研究药水、卖训练药水、用钟楼药水、卖钟楼药水、用工人药水、卖壁垒之戒
- 升穿戴装备（UPGRADE_WEARABLE_GEAR）
- 升所有装备（UPGRADE_ALL_GEAR）
- 领限时活动奖励（CLAIM_TIMED_REWARDS）
- 领令牌奖励（CLAIM_TOKEN_REWARDS）
- 领都城币（CLAIM_CAPITAL_GOLD）
- 捐都城币（DONATE_CAPITAL_GOLD）
- 领商店免费奖励（CLAIM_FREE_SHOP_REWARDS）
- 领成就宝石（CLAIM_ACHIEVEMENT_GEMS）
- 使用临时物品（USE_TEMP_ITEMS）
- 自动加部落（AUTO_JOIN_CLAN）及部落标签、加部落暗号
- 创建连号部落（CREATE_CONSECUTIVE_CLANS）及部落名称、连号数量
- 邀请玩家（INVITE_PLAYERS）

## BuilderBaseConfig.kt（夜世界设置）
- 接取竞赛后，对战以下局数后切号（SWITCH_ACCOUNT_AFTER_BATTLES_WITH_TASKS）