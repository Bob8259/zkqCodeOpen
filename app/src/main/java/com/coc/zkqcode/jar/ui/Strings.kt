package com.coc.zkqcode.jar.ui

object Strings {
    // EnterMainCode
    fun configLoadingMessage(countdown: Int) =
        if (LanguageManager.isChinese) "正在初始化配置文件...\n若长时间卡在此界面，将在 $countdown 秒后自动重启。" else "Initializing config files...\nIf this screen is stuck for too long, the app will automatically restart in $countdown seconds."

    val manualCloseAssistant get() = if (LanguageManager.isChinese) "点击此处手动关闭辅助" else "Tap here to manually close the assistant"

    // HomeScreen tabs
    val homeTab get() = if (LanguageManager.isChinese) "主页设置" else "Home"
    val accountTab get() = if (LanguageManager.isChinese) "账号设置" else "Accounts"
    val extractTab get() = if (LanguageManager.isChinese) "提取存档" else "Save Data"
    val configTab get() = if (LanguageManager.isChinese) "配置文件" else "Profiles"
    val specialTab get() = if (LanguageManager.isChinese) "特殊设置" else "Advanced"

    // Section chips
    val sectionGeneral get() = if (LanguageManager.isChinese) "通用设置" else "General"
    val sectionCoreBattle get() = if (LanguageManager.isChinese) "核心对战" else "Core Battle"
    val sectionClan get() = if (LanguageManager.isChinese) "部落设置" else "Clan"
    val sectionResearch get() = if (LanguageManager.isChinese) "自动研究" else "Research"
    val sectionBuild get() = if (LanguageManager.isChinese) "自动建造" else "Build"
    val sectionPet get() = if (LanguageManager.isChinese) "战宠装备" else "Pets & Equipments"
    val sectionOther get() = if (LanguageManager.isChinese) "其他" else "Others"
    val sectionBuilderBase get() = if (LanguageManager.isChinese) "夜世界" else "Builder Base"

    // HomeScreenBottomBar
    val saveAndRun get() = if (LanguageManager.isChinese) "保存并运行" else "Save&Run"
    val saveAndPause get() = if (LanguageManager.isChinese) "保存并暂停" else "Save&Pause"
    val saveAndExit get() = if (LanguageManager.isChinese) "保存并退出" else "Save&Exit"
    val exitTitle get() = if (LanguageManager.isChinese) "退出提示" else "Exit Confirmation"
    val exitMessage get() = if (LanguageManager.isChinese) "确认要退出吗？" else "Are you sure you want to exit?"
    val cancel get() = if (LanguageManager.isChinese) "取消" else "Cancel"
    val confirm get() = if (LanguageManager.isChinese) "确认" else "Confirm"
    val cleanAllTitle get() = if (LanguageManager.isChinese) "清除全部数据" else "Clear All Data"
    val cleanAllMessage get() = if (LanguageManager.isChinese) "确认要删除所有数据吗？\n此操作不可撤销，将删除辅助设置、账号信息等全部数据。\n\n清除数据后，辅助将自动关闭，请手动重启辅助。" else "Are you sure you want to delete all data?\nThis action cannot be undone and will delete all settings, account info, etc.\n\nAfter clearing, the app will close. Please restart manually."

    // HomeSettings
    fun timerRunning(seconds: Int) = if (LanguageManager.isChinese) "${seconds}秒后运行" else "Running in ${seconds}s"
    val timerStopped get() = if (LanguageManager.isChinese) "计时已停止" else "Timer stopped"
    val stopTimer get() = if (LanguageManager.isChinese) "停止计时" else "Stop Timer"
    val log get() = if (LanguageManager.isChinese) "日志" else "Changelog"
    val manualSwitch get() = if (LanguageManager.isChinese) "手动切号？" else "Switch Account?"
    val clickSwitch get() = if (LanguageManager.isChinese) "点击切号" else "Tap to switch"
    val foundBug get() = if (LanguageManager.isChinese) "发现问题？" else "Found a Bug?"
    val clickFeedback get() = if (LanguageManager.isChinese) "点击反馈" else "Tap to report"
    val customBg get() = if (LanguageManager.isChinese) "自定义背景" else "Custom Background"
    val selectImage get() = if (LanguageManager.isChinese) "选择图片" else "Select Image"
    val resetBg get() = if (LanguageManager.isChinese) "重置背景" else "Reset Background"
    val restoreDefault get() = if (LanguageManager.isChinese) "恢复默认" else "Restore Default"
    val bgResetSuccess get() = if (LanguageManager.isChinese) "背景已重置" else "Background reset"
    val bgResetFail get() = if (LanguageManager.isChinese) "重置失败" else "Reset failed"
    val tipChangeDevice get() = if (LanguageManager.isChinese) "换机前务必清除数据 保护账号安全" else "Clear data before switching devices to protect your account"
    val clearMemory get() = if (LanguageManager.isChinese) "清除账号记忆" else "Clear Account Cache"
    val importOldData get() = if (LanguageManager.isChinese) "导入旧版数据" else "Import Legacy Data"
    val syncTime get() = if (LanguageManager.isChinese) "同步时间" else "Sync Time"
    val autoUpdateOff get() = if (LanguageManager.isChinese) "关闭自动更新" else "Auto-update Off"
    val autoUpdateOnLaunch get() = if (LanguageManager.isChinese) "仅启动时更新" else "Update on Launch"
    val autoUpdateRealtime get() = if (LanguageManager.isChinese) "实时自动更新" else "Real-time Update"
    val afterKickReconnect get() = if (LanguageManager.isChinese) "立刻重连" else "Reconnect Immediately"
    val afterKickSwitch get() = if (LanguageManager.isChinese) "切换账号" else "Switch Account"
    val afterKickWait get() = if (LanguageManager.isChinese) "原地等待" else "Wait in Place"

    // AnnouncementDialog
    val changelogTitle get() = if (LanguageManager.isChinese) "更新日志" else "Changelog"
    val fetchingUpdates get() = if (LanguageManager.isChinese) "正在获取更新记录..." else "Fetching updates..."
    fun fetchFailed(error: String?) = if (LanguageManager.isChinese) "获取更新记录失败: $error" else "Failed to fetch updates: $error"
    val checkingVersion get() = if (LanguageManager.isChinese) "正在检查版本信息..." else "Checking version..."
    fun checkFailed(error: String?) = if (LanguageManager.isChinese) "检查版本失败: $error" else "Version check failed: $error"
    fun newVersionDetected(local: Long, server: Long) = if (LanguageManager.isChinese) "检测到新版本!\n当前版本: $local，服务器版本: $server" else "New version detected!\nCurrent: $local, Server: $server"
    fun alreadyLatest(local: Long, server: Long) = if (LanguageManager.isChinese) "当前已是最新版本\n当前版本: $local，服务器版本: $server" else "Already up to date\nCurrent: $local, Server: $server"
    val versionInfoUnavailable get() = if (LanguageManager.isChinese) "无法获取版本信息" else "Version info unavailable"
    val updateLogHeader get() = if (LanguageManager.isChinese) "更新记录:\n" else "Update Log:\n"
    val moreLogsFooter get() = if (LanguageManager.isChinese) "\n\n更多更新记录可前往官网教程文档内查看" else "\n\nFor more update logs, visit the official website"
    val gotIt get() = if (LanguageManager.isChinese) "明白了" else "Got it"

    // AccountSettings
    val selectAll get() = if (LanguageManager.isChinese) "一键全选" else "Select All"
    val invertSelection get() = if (LanguageManager.isChinese) "一键反选" else "Invert All"
    val pinEnabledAccounts get() = if (LanguageManager.isChinese) "置顶开启的账号" else "Pin Enabled"
    val unpinEnabledAccounts get() = if (LanguageManager.isChinese) "取消置顶" else "Unpin"
    val applyConfigLabel get() = if (LanguageManager.isChinese) "调整配置" else "Apply profile"
    val applyAccountLabel get() = if (LanguageManager.isChinese) "将账号" else "Apply account"
    val toAccounts get() = if (LanguageManager.isChinese) "应用到账号" else "to accounts"
    val changeTo get() = if (LanguageManager.isChinese) "改为" else "to"
    val cnVersion get() = if (LanguageManager.isChinese) "国服" else "CN Version"
    val globalVersion get() = if (LanguageManager.isChinese) "国际服" else "Global Version"
    val atrasis get() = "Atrasis"
    val deployModeOriginal get() = if (LanguageManager.isChinese) "单边下兵 (原版)" else "Single Side"
    val deployModeSlider get() = if (LanguageManager.isChinese) "四面下兵 (真滑屏)" else "4-Side Slide"
    val deployModeFast get() = if (LanguageManager.isChinese) "四面下兵 (快速)" else "4-Side Fast"
    val deployPreset get() = if (LanguageManager.isChinese) "预设" else "Preset"
    val selectDeployPreset get() = if (LanguageManager.isChinese) "选择预设" else "Select preset"

    // AccountConfig
    fun account(index: Int) = if (LanguageManager.isChinese) "账号$index" else "Account $index"
    val randomSchedulingRemark get() = if (LanguageManager.isChinese) "已开启随机上号，请在右上角前往特殊设置页面->随机上号，通过调整账号分组来控制该账号是否开启。" else "Random scheduling enabled. Go to Advanced > Random Scheduling to manage account groups."
    val globalNotSupported get() = if (LanguageManager.isChinese) "国际服不支持测试功能，请更换启动游戏方式！" else "Test mode not supported for Global Version! Please change launch method."
    val gameSave get() = if (LanguageManager.isChinese) "游戏存档" else "Game Save"
    val directLaunch get() = if (LanguageManager.isChinese) "直接启动" else "Direct Launch"
    val testMode get() = if (LanguageManager.isChinese) "测试功能" else "Test Mode"
    val statusEnabled get() = if (LanguageManager.isChinese) "开启" else "Enabled"
    val statusDisabled get() = if (LanguageManager.isChinese) "关闭" else "Disabled"
    val statusTo get() = if (LanguageManager.isChinese) "状态改为" else "Status to"
    val batchOperation get() = if (LanguageManager.isChinese) "操作" else "Operation"
    val batchAccount get() = if (LanguageManager.isChinese) "账号" else "Account"
    val batchParam get() = if (LanguageManager.isChinese) "改为" else "to"
    val batchOpVersionMethod get() = if (LanguageManager.isChinese) "版本+方式" else "Version+Method"
    val batchOpStatus get() = if (LanguageManager.isChinese) "状态" else "Status"
    val batchMethodLabel get() = if (LanguageManager.isChinese) "方式" else "Method"

    // ExtractGameSave
    val extractFailedExists get() = if (LanguageManager.isChinese) "提取失败，存档已存在" else "Extract failed - save already exists"
    val extracting get() = if (LanguageManager.isChinese) "提取中..." else "Extracting..."
    val extractSuccess get() = if (LanguageManager.isChinese) "提取成功！" else "Extract successful!"
    val deleteFailedNotExist get() = if (LanguageManager.isChinese) "删除失败，存档不存在" else "Delete failed - save not found"
    val deleting get() = if (LanguageManager.isChinese) "删除中..." else "Deleting..."
    val deleteSuccess get() = if (LanguageManager.isChinese) "删除成功！" else "Delete successful!"
    val deleteFailedPermission get() = if (LanguageManager.isChinese) "删除失败，请检查权限" else "Delete failed - check permissions"
    val errorNoSlot get() = if (LanguageManager.isChinese) "错误：未获取到路径序号" else "Error: slot number not found"
    val errorSlotEmpty get() = if (LanguageManager.isChinese) "错误：序号不能为空" else "Error: slot cannot be empty"
    val errorContentEmpty get() = if (LanguageManager.isChinese) "错误：数据内容不能为空" else "Error: content cannot be empty"
    val saveSuccess get() = if (LanguageManager.isChinese) "保存成功！" else "Save successful!"
    val saveFailedPermission get() = if (LanguageManager.isChinese) "保存失败，请检查权限" else "Save failed - check permissions"
    val deleteDataFailedNotExist get() = if (LanguageManager.isChinese) "删除失败，该序号数据不存在" else "Delete failed - slot data not found"
    fun extractRegionData(region: String) = if (LanguageManager.isChinese) "提取${region}数据" else "Extract $region Data"
    val deleteExistingData get() = if (LanguageManager.isChinese) "删除现有数据" else "Delete Existing Data"
    val saveData get() = if (LanguageManager.isChinese) "保存数据" else "Save Data"
    val deleteData get() = if (LanguageManager.isChinese) "删除数据" else "Delete Data"

    // ConfigFileGrid
    val syncProfileLabel get() = if (LanguageManager.isChinese) "将配置文件" else "Sync profile"
    val syncToProfile get() = if (LanguageManager.isChinese) "同步至配置文件" else "to profiles"
    val invalidProfileNumber get() = if (LanguageManager.isChinese) "请输入有效的配置文件序号" else "Please enter valid profile numbers"
    fun sourceOutOfRange(count: Int) = if (LanguageManager.isChinese) "源配置文件序号必须在 1-$count 之间" else "Source profile must be between 1-$count"
    fun targetOutOfRange(count: Int) = if (LanguageManager.isChinese) "目标配置文件序号必须在 1-$count 之间" else "Target profiles must be between 1-$count"
    val startGreaterThanEnd get() = if (LanguageManager.isChinese) "目标配置文件起始序号不能大于结束序号" else "Start cannot be greater than end"
    val noTargetsToSync get() = if (LanguageManager.isChinese) "目标范围内没有可同步的配置文件" else "No profiles to sync in range"
    fun syncSuccess(from: Int, targets: String) = if (LanguageManager.isChinese) "配置文件$from 已同步至 配置文件$targets" else "Profile $from synced to $targets"
    fun skippedSelf(n: Int) = if (LanguageManager.isChinese) "（已跳过配置文件$n）" else " (Skipped profile $n)"
    val profileHint get() = if (LanguageManager.isChinese) "点击方格调整配置文件。调整完成后，回到\"账号设置\"，给每个账号分配所需的配置文件。" else "Tap a cell to edit. Then go to Accounts to assign profiles."
    fun profileFile(i: Int) = if (LanguageManager.isChinese) "配置文件$i" else "Profile $i"
    fun remark(text: String) = if (LanguageManager.isChinese) "备注: $text" else "Remark: $text"

    // Login
    val adFreeMessage get() = if (LanguageManager.isChinese) "登录后免广告，运行更高效！不登陆也可以运行。免广告每天花费0.15卡班积分，用多久扣多久。" else "Login to use. It costs 0.15 points per day, pay as you go."
    val sponsorMessage get() = if (LanguageManager.isChinese) "免费辅助制作不易，希望大家多多赞助。你的赞助是紫孔雀继续发展的唯一动力！" else "Free tool development is hard. Your sponsorship keeps this project going!"
    val welcomeBack get() = if (LanguageManager.isChinese) "欢迎回来，感谢赞助！紫孔雀因你而变得更好！" else "Welcome back! Thank you for sponsoring!"
    fun currentGems(value: Double) = if (LanguageManager.isChinese) "当前积分：%.5f".format(value) else "Current Points: %.5f".format(value)
    val invalidEmail get() = if (LanguageManager.isChinese) "请输入正确的邮箱！" else "Please enter a valid email!"
    val loggingIn get() = if (LanguageManager.isChinese) "登录中，请稍后..." else "Logging in..."
    fun loginFailed(error: String?) = if (LanguageManager.isChinese) "登录失败: $error" else "Login failed: $error"
    fun loginSuccess(value: String) = if (LanguageManager.isChinese) "登录成功！卡班积分数量 $value" else "Login successful! Points: $value"
    val emailPasswordError get() = if (LanguageManager.isChinese) "邮箱或密码错误，请检查后重试" else "Incorrect email or password"
    val password get() = if (LanguageManager.isChinese) "密码" else "Password"
    val login get() = if (LanguageManager.isChinese) "登录" else "Login"
    val donate get() = if (LanguageManager.isChinese) "捐赠" else "Donate"
    val logout get() = if (LanguageManager.isChinese) "退出" else "Logout"
    val logoutSuccess get() = if (LanguageManager.isChinese) "退出成功" else "Logged out"
    val donateInfo get() = if (LanguageManager.isChinese) "前往官网即可注册捐赠，详情请加QQ频道pd92187355，或QQ群954346388 \n" else "Visit the website to register and donate. Join our channel for more information: https://t.me/+cApT2jSTaNtiYTZl"

    // RandomScheduling
    val dawnGroup get() = if (LanguageManager.isChinese) "凌晨组  0:00 - 6:00" else "Dawn 0:00 - 6:00"
    val morningGroup get() = if (LanguageManager.isChinese) "上午组  6:00 - 12:00" else "Morning 6:00 - 12:00"
    val afternoonGroup get() = if (LanguageManager.isChinese) "下午组  12:00 - 18:00" else "Afternoon 12:00 - 18:00"
    val nightGroup get() = if (LanguageManager.isChinese) "夜晚组  18:00 - 24:00" else "Night 18:00 - 24:00"
    val remainingProbBattle get() = if (LanguageManager.isChinese) "剩余概率随机持续对战" else "Remaining probability for continuous battle"
    val to get() = if (LanguageManager.isChinese) "至" else "to"
    val minutesThenOffline get() = if (LanguageManager.isChinese) "分钟后下线" else "min then go offline"
    val selectAccountsForGroup get() = if (LanguageManager.isChinese) "选择加入本组的账号：" else "Select accounts for this group:"
    val tapOrDragHint get() = if (LanguageManager.isChinese) "点击或长按拖拽均可调节账号开启状态。" else "Tap or long-press drag to toggle accounts."

    // BugReport
    val bugReportTitle get() = if (LanguageManager.isChinese) "问题反馈" else "Bug Report"
    val bugReportInstructions get() = if (LanguageManager.isChinese) "提交截图前，请先与作者/客服联系，确认出现问题的页面。\n确认页面后，请进入问题页面，并点击提交截图。\n提交截图后，请告知作者/客服截图已提交(一定要告知，否则截图无效)。" else "Before submitting, contact the author/support to confirm the issue page.\nOnce confirmed, go to that page and tap Submit Screenshot.\nInform support after submitting (required for processing)."
    val submitScreenshot get() = if (LanguageManager.isChinese) "提交截图" else "Submit Screenshot"
    val submitting get() = if (LanguageManager.isChinese) "正在提交..." else "Submitting..."
    val screenshotFailed get() = if (LanguageManager.isChinese) "截图保存失败，截图为空。" else "Screenshot failed - empty capture."
    val submitSuccess get() = if (LanguageManager.isChinese) "截图提交成功！请与作者/客服联系。" else "Screenshot submitted! Please contact the author/support."
    fun submitFailed(error: String?) = if (LanguageManager.isChinese) "提交失败：$error" else "Submit failed: $error"
    val closeWindow get() = if (LanguageManager.isChinese) "关闭窗口" else "Close Window"
    val capturing get() = if (LanguageManager.isChinese) "截屏中，请耐心等待" else "Capturing screenshot, please wait"
    val serverFolderFull get() = if (LanguageManager.isChinese) "服务器截图已满，上传失败。请与作者/客服联系。" else "Server storage full. Please contact support."
    val noFileUploaded get() = if (LanguageManager.isChinese) "未上传文件。" else "No file uploaded."
    val missingImageField get() = if (LanguageManager.isChinese) "表单数据中缺少图片字段。" else "Missing image field in form data."

    // SwitchAccount
    val switchTip get() = if (LanguageManager.isChinese) "小提示：在悬浮窗点击此按钮，即可回到切号工具。" else "Tip: Tap this button in the floating window to return to the switcher."
    val switchToNth get() = if (LanguageManager.isChinese) "切换到第" else "Switch to"
    val nthAccount get() = if (LanguageManager.isChinese) "个账号" else "th account"
    val confirmSwitch get() = if (LanguageManager.isChinese) "确认切号" else "Confirm Switch"
    val switchComplete get() = if (LanguageManager.isChinese) "切号完成" else "Switch complete"

    // MainBaseConfigs
    val raidMedalShop get() = if (LanguageManager.isChinese) "突袭币购买设置" else "Raid Medal Shop Settings"
    val leagueMedalShop get() = if (LanguageManager.isChinese) "联赛币购买设置" else "League Medal Shop Settings"
    fun collapse(title: String) = if (LanguageManager.isChinese) "收起$title" else "Collapse $title"
    fun expand(title: String) = if (LanguageManager.isChinese) "展开$title" else "Expand $title"
    val generalSettings get() = if (LanguageManager.isChinese) "通用设置" else "General"
    val battleSettingsMain get() = if (LanguageManager.isChinese) "对战设置（主世界）" else "Battle Settings - Main Base"
    val clanSettings get() = if (LanguageManager.isChinese) "部落设置" else "Clan Settings"
    val researchSettings get() = if (LanguageManager.isChinese) "研究设置" else "Research Settings"
    val buildSettingsMain get() = if (LanguageManager.isChinese) "建造设置（主世界）" else "Build Settings - Main Base"
    val petAndEquipment get() = if (LanguageManager.isChinese) "战宠装备" else "Pets & Equipments"
    val others get() = if (LanguageManager.isChinese) "其他" else "Others"
    val adjustMainBasePriority get() = if (LanguageManager.isChinese) "点击调整主世界建筑升级" else "Adjust Main Base Upgrade Priority"
    val secondsSuffix get() = if (LanguageManager.isChinese) "秒钟" else "seconds"
    val countSuffix get() = if (LanguageManager.isChinese) "数量" else "Count"
    val percentStartBattle get() = if (LanguageManager.isChinese) "％时开始对战" else "% start battle"
    val percentStop get() = if (LanguageManager.isChinese) "％为止" else "% stop"
    val levelAccountsSuffix get() = if (LanguageManager.isChinese) "级的账号" else "level accounts"
    val directStart get() = if (LanguageManager.isChinese) "直接开启" else "Direct Start"
    val recentOnline get() = if (LanguageManager.isChinese) "最近上线" else "Recent Online"

    // BuilderBaseConfig
    val builderBaseSettings get() = if (LanguageManager.isChinese) "夜世界专属设置" else "Builder Base Settings"
    val adjustBuilderBasePriority get() = if (LanguageManager.isChinese) "点击调整夜世界建筑升级" else "Adjust Builder Base Upgrade Priority"
    val adjustMainBaseResearchPriority get() = if (LanguageManager.isChinese) "点击调整主世界研究优先级" else "Adjust Main Base Research Priority"
    val adjustMainBasePetPriority get() = if (LanguageManager.isChinese) "点击调整战宠升级优先级" else "Adjust Pet Upgrade Priority"
    val adjustBuilderBaseResearchPriority get() = if (LanguageManager.isChinese) "点击调整夜世界研究优先级" else "Adjust Builder Base Research Priority"

    // Components
    val noticeTitle get() = if (LanguageManager.isChinese) "注意事项" else "Notice"
    val collapseList get() = if (LanguageManager.isChinese) "缩起列表" else "Collapse List"
    val expandList get() = if (LanguageManager.isChinese) "展开列表" else "Expand List"
    val priorityHint get() = if (LanguageManager.isChinese) "长按单元格拖动调整优先度，点击切换启用/禁用。越靠上，越靠左表示优先度越高。" else "Long-press to reorder. Tap to toggle. Top-left has highest priority."
    val resetPriority get() = if (LanguageManager.isChinese) "重置优先度" else "Reset Priority"
    val savingWait get() = if (LanguageManager.isChinese) "保存中，请稍候" else "Saving, please wait..."
    val done get() = if (LanguageManager.isChinese) "完成" else "Done"

    // Research levels
    val maxLevel get() = if (LanguageManager.isChinese) "满级" else "Max Level"
    val maxLevelMinus1 get() = if (LanguageManager.isChinese) "满级减一" else "Max - 1"
    val maxLevelMinus2 get() = if (LanguageManager.isChinese) "满级减二" else "Max - 2"

    // HomeScreen - Import Dialog
    val importOldTitle get() = if (LanguageManager.isChinese) "导入旧版数据" else "Import Legacy Data"
    val importOldMessage get() = if (LanguageManager.isChinese) "导入旧版数据将覆盖当前配置，是否继续？\n\n因新旧版差距过大，此功能可能无法导入100%的数据。导入完成后，请仔细检查配置是否正确。\n\n导入后请手动重启辅助。" else "Importing legacy data will overwrite current configs. Continue?\n\nDue to major changes, some data might not import correctly. Please check your settings after importing.\n\nRestart manually after completion."
    val scanningImport get() = if (LanguageManager.isChinese) "正在扫描导入并覆盖配置..." else "Scanning and importing configs..."
    val importSuccess get() = if (LanguageManager.isChinese) "旧版本数据导入成功且已保存" else "Legacy data imported and saved"
    val noOldData get() = if (LanguageManager.isChinese) "未找到任何旧版数据" else "No legacy data found"
    val initializing get() = if (LanguageManager.isChinese) "正在初始化配置..." else "Initializing configs..."
    val back get() = if (LanguageManager.isChinese) "◀ 返回" else "◀ Back"
    val searchSettings get() = if (LanguageManager.isChinese) "搜索设置..." else "Search settings..."

    // Explain Texts
    val disableRestartExplain get() = if (LanguageManager.isChinese) "勾选后，所有重启游戏的设定将失效，可能会导致卡界面。建议谨慎使用。" else "Disabling restart may cause stuck screens. Use with caution."
    val batchCreateExplain get() = if (LanguageManager.isChinese) "启用批量创号后，辅助将优先检查所有游戏存档是否存在，若存档缺失则自动创建账号；若存档齐全，则按照指定序号依次运行对应账号。序号与游戏存档编号一致，且所有账号均采用配置1的设定。\n部分云手机在创号过程中可能出现异常，建议改用模拟器完成创号操作。启用批量创号时，掉线重连选项须设置为“立即重连”。" else "Batch creation checks for saves and creates missing ones. It runs accounts in order using Profile 1 settings.\nSome cloud phones may fail; emulators are recommended. Reconnect option must be set to 'Immediately'."
    val randomSchedulingExplain get() = if (LanguageManager.isChinese) "启用随机上号后，辅助将根据当前时间段，从对应组的账号中随机选取顺序依次运行。每个账号运行完毕后，根据设定的概率决定是否继续对战或直接下线，并按对战时间比例计算等待时间后再运行下一个账号。\n\n随机上号与批量创号不能同时启用。" else "Random scheduling runs accounts in random order per time group. After each run, it decides whether to continue or go offline based on probability and wait time.\n\nCannot be used with Batch Creation."
    val smartDecisionExplain get() = if (LanguageManager.isChinese) "开启后，辅助会自动检测大本营等级，并根据大本营等级跳过部分内容（例如升级装备，战宠等）从而提升运行效率。" else "Auto-detect TH level and skip irrelevant upgrades (equipment, pets, etc.) to improve efficiency."
    val tournamentExplain get() = if (LanguageManager.isChinese) "排位赛不保证胜率，请谨慎开启。" else "No win rate guarantee for ranked battles. Use with caution."
    val dynamicAdjustExplain get() = if (LanguageManager.isChinese) "启用后，辅助将自动跳过前两个搜索目标，并根据所有已搜索目标的可获取资源均值动态调整搜索条件。" else "Skip first 2 searches and adjust requirements dynamically based on average loot found."
    val waitForBattleExplain get() = if (LanguageManager.isChinese) "启用后，若当前处于对战冷却期，辅助将持续等待直至冷却结束。无论是否接取竞赛任务都生效。" else "Wait during battle cooldown until ready. Works regardless of clan games tasks."
    val waitForBattleAfterTaskExplain get() = if (LanguageManager.isChinese) "启用后，若已接取主世界竞赛任务且当前处于对战冷却期，辅助将持续等待直至冷却结束。" else "Wait during battle cooldown if a clan games task is active."
    val restartGameExplain get() = if (LanguageManager.isChinese) "启用后，辅助将在部署全部部队后自动重启游戏。此功能仅适用于主世界对战。" else "Restart game after deploying all troops. Main base only."
    val manualTrainingExplain get() = if (LanguageManager.isChinese) "启用后，辅助将跳过配兵流程，请自行完成兵种配置。\n\n注意：辅助不会自动开启超级兵种。" else "Skip troop training - configure manually. Remark: Super troops will not be auto-activated."
    val armyLinkLabel get() = if (LanguageManager.isChinese) "链接配兵" else "Army Link"
    val armyConfigModeLink get() = if (LanguageManager.isChinese) "使用链接" else "Use Link"
    val armyConfigModeManual get() = if (LanguageManager.isChinese) "自选配置" else "Custom Config"
    val claimRewardsExplain get() = if (LanguageManager.isChinese) "优先领取魔法书和宝石，其他随机领取。" else "Prioritize books and gems, others random."
    val claimBadgeEventRewardsExplain get() = if (LanguageManager.isChinese) "启用后，辅助会领取奖章活动（也就是出新装备的活动）以及贡献类活动（例如给部落贡献金币）的奖励。。" else "When enabled, the bot will claim badge events reward (e.g. the evet comes with a new equipment) and donation events reward (e.g. the event where you can contribute gold to your clan)."
    val claimEventShopRewardsExplain get() = if (LanguageManager.isChinese) "启用后，领取奖章活动奖励后自动进入活动商店。检测活动币≥3100时，自动购买新装备。" else "After claiming badge event rewards, enter event shop. When event medals ≥ 3100, auto-buy new equipment."
    val approveCodeExplain get() = if (LanguageManager.isChinese) "启用后，捐兵时会同时检测申请加入的成员。如果暗号匹配则自动批准加入，不匹配则跳过。" else "When enabled, detects join requests during donation. Auto-approves if the code matches, skips otherwise."
    val clanWarExplain get() = if (LanguageManager.isChinese) "部落战其中一次打对位，另一次打推荐对手。\n不保证胜率，请谨慎使用。" else "One attack on mirror, one on recommended. No win rate guarantee."
    val leagueExplain get() = if (LanguageManager.isChinese) "默认打对位，如果对位已被打则随机进攻，不保证胜率。" else "Default attack on mirror. If already hit, attack random. No win rate guarantee."
    val startClanWarExplain get() = if (LanguageManager.isChinese) "直接开启：人数凑够5的倍数就立刻开启\n最近上线：只选择挂绿牌，并且最近上线过的玩家。" else "Direct: start when number of participants is divisible by 5. Recent: only players who were online recently and perfer to join the Clan War."
    val startClanLeagueExplain
        get() = if (LanguageManager.isChinese)
            "每月1号16:00~3号16:00自动报名部落联赛。\n可设置额外联赛开放起始日（11日~22日），联赛期间自动禁用部落战。"
        else "Auto-register for Clan War League monthly 1st 16:00~3rd 16:00.\nSet Extra League Start Day (11~22). CW disabled during league period."
    val autoJoinClanExplain get() = if (LanguageManager.isChinese) "启用后，辅助将自动申请加入部落。若未指定部落标签，仅在当前无部落时执行加入操作；若已指定标签，则每日检测一次是否已加入目标部落。注意：使用此功能前需先建造部落城堡。" else "Auto-join clan. If no tag, joins only if clanless. If tag set, checks daily. Requires Clan Castle."
    val buildSettingExplain get() = if (LanguageManager.isChinese) "建造时会忽略可改装的建筑，并且会优先建造新建筑和新的建筑工人小屋。" else "Ignores gear-up buildings. Prioritizes new buildings and builder huts."
    val gearUpExplain get() = if (LanguageManager.isChinese) "改装建筑属于主世界功能，即使关闭夜世界，也依然会生效。" else "Gear-up is a main base feature and works even if Builder Base is disabled."
    val saveWorkerBuildExplain get() = if (LanguageManager.isChinese) "本功能依赖智能决策，启用本功能后会自动开启智能决策。" else "Requires Smart Decision to be enabled."
    val batchWallExplain get() = if (LanguageManager.isChinese) "此功能对已开启月卡（黄金令牌）的账号不生效。" else "Does not apply to Gold Pass accounts."
    val reverseUpgradeExplain get() = if (LanguageManager.isChinese) "启用后，辅助将从下到上升级建筑。" else "When enabled, the script will upgrade buildings from the bottom to the top."

    // Equipment scope options
    val upgradeScopeWear get() = if (LanguageManager.isChinese) "穿戴装备" else "Equipped"
    val upgradeScopeCustom get() = if (LanguageManager.isChinese) "指定装备" else "Custom"

    val upgradeEquipmentsExplain get() = if (LanguageManager.isChinese) "装备列表中的设置不适用于已穿戴的装备。" else "The settings in the equipment list do not apply to equipped equipments."
    val upgradeEquipedEquipmentsExplain get() = if (LanguageManager.isChinese) "启用后，将自动升级当前已穿戴的装备。" else "When enabled, automatically upgrades currently equipped equipments."

    // Hero names for equipment grouping
    val barbarianKing get() = if (LanguageManager.isChinese) "野蛮人之王" else "Barbarian King"
    val archerQueen get() = if (LanguageManager.isChinese) "弓箭女皇" else "Archer Queen"
    val grandWarden get() = if (LanguageManager.isChinese) "大守护者" else "Grand Warden"
    val royalChampion get() = if (LanguageManager.isChinese) "飞盾战神" else "Royal Champion"
    val dragonDuke get() = if (LanguageManager.isChinese) "飞龙公爵" else "Dragon Duke"
    val minionPrince get() = if (LanguageManager.isChinese) "亡灵王子" else "Minion Prince"

    // Helper to get hero display name by key prefix
    fun getHeroName(heroKey: String) = when (heroKey) {
        "barbarian_king" -> barbarianKing
        "archer_queen" -> archerQueen
        "grand_warden" -> grandWarden
        "royal_champion" -> royalChampion
        "dragon_duke" -> dragonDuke
        "minion_prince" -> minionPrince
        else -> heroKey
    }

    val helperExplain get() = if (LanguageManager.isChinese) "启用后，辅助将自动使用实验助手及建筑工人学徒。" else "Auto-use Research Helper and Builder Apprentice."
    val researchHelperExplain get() = if (LanguageManager.isChinese) "实验助手的升级优先级低于购买新建筑工人，但高于工人学徒的升级。" else "Research Helper upgrade priority is below new builders but above Builder Apprentice."
    val builderApprenticeExplain get() = if (LanguageManager.isChinese) "工人学徒的升级优先级低于购买新建筑工人及升级实验助手。" else "Builder Apprentice upgrade priority is below new builders and Research Helper."
    val removeObstaclesExplain get() = if (LanguageManager.isChinese) "启用后，当主世界金币或圣水超过30万时生效。存在约5%的概率误移除稀有障碍物。" else "Active when gold/elixir exceeds 300k. ~5% chance of removing rare obstacles."
    val continuousRemoveExplain get() = if (LanguageManager.isChinese) "启用后，辅助将不再记录移除时间，每次运行都会尝试移除障碍物，实现连续移除。" else "Remove obstacles every run without tracking time."
    val removeObstaclesEnhanceExplain get() = if (LanguageManager.isChinese) "启用后，移除障碍物时将增加随机点击操作，以提高移除稀有障碍物的概率。\n建议在批量挂机时开启，可有效防止地图建造空间不足。" else "Add random clicks to increase rare obstacle chance. Recommended for batch AFK to prevent space issues."
    val noBuilderBaseExplain get() = if (LanguageManager.isChinese) "启用后，辅助将不再进入夜世界，夜世界的所有相关设置均会失效。" else "Disables Builder Base entirely. All related settings will be ignored."
    val builderBaseFarmingExplain get() = if (LanguageManager.isChinese) "辅助将自动完成兵种配置，暂不支持手动配兵。若未启用“上分模式”或“刷圣水车”，辅助将根据当前账号的资源状况智能选择对战模式。" else "Auto-configure troops (manual not supported). If no specific mode set, chooses based on resources."
    val trophyPushingExplain get() = if (LanguageManager.isChinese) "启用后，辅助将使用暗夜女巫进行上分，圣水获取效率会显著下降，请谨慎启用。此选项不可与“刷圣水车”同时开启。" else "Uses Night Witch for trophy pushing. Elixir efficiency will drop. Cannot be used with Elixir Cart mode."
    val elixirCartExplain get() = if (LanguageManager.isChinese) "启用后，夜世界对战中部署兵种后将立即投降，因此几乎无法获取金币，请谨慎启用。此选项不可与“上分模式”同时开启。" else "Deploy then surrender for elixir. Gold gain will be near zero. Cannot be used with Trophy Pushing."
    val builderBaseRemoveObstaclesExplain get() = if (LanguageManager.isChinese) "若尚未解锁第二区域，当金币或圣水超过30万时生效；\n若已解锁第二区域，当金币或圣水超过60万时生效。\n注意：此功能会移除野蛮人雕像，请谨慎使用。" else "Active at 300k gold/elixir (Area 1) or 600k (Area 2). Remark: May remove Barbarian Statue."
    val builderBaseContinuousRemoveExplain get() = if (LanguageManager.isChinese) "启用后，辅助将不再记录移除时间，每次运行都会尝试移除障碍物，实现连续移除。" else "Remove obstacles every run without tracking time."
    val builderBaseSaveWorkerBuildExplain get() = if (LanguageManager.isChinese) "本功能依赖智能决策，启用本功能后会自动开启智能决策。" else "Requires Smart Decision to be enabled."

    // Stop battle conditions & one-star exit
    val stopBattleWhenNoStarExplain
        get() = if (LanguageManager.isChinese)
            "启用后，检测到无胜利之星时将停止对战。可与「资源满后停止对战」同时开启，若同时开启，则只有同时满足2个条件时才停止对战。"
        else "Stop battling when no victory stars are available. Can be used together with the resource-full condition."

    val exitOnOneStarExplain
        get() = if (LanguageManager.isChinese)
            "启用后，主世界对战获得一星后退出对战。"
        else "Continuously checks for a 1-star result while polling after deployment, and exits the battle immediately when detected. Ignored if 'Restart after deploy' or builder base final round restart is active."

    // OCR destruction-% battle exit
    val percentTargetSuffix get() = if (LanguageManager.isChinese) "％时退出" else "% exit"
    val percentStaleSuffix get() = if (LanguageManager.isChinese) "秒内％未变则退出" else "s if % unchanged"
    val unchangedExitSuffix get() = if (LanguageManager.isChinese) "秒内未变则退出" else "s unchanged, then exit"
    val unchangedDetectionOptions get() = if (LanguageManager.isChinese) listOf("百分比", "对手资源", "百分比+资源") else listOf("Percent", "Opponent resources", "Percent + resources")
    val ocrExitExplain
        get() = if (LanguageManager.isChinese)
            "目标破坏大于 0 时，摧毁率达到目标即退出；目标破坏为 0 时，按下拉选项检测百分比、对手资源或两者。"
        else "If the target is above 0, exit when the destruction target is reached. If it is 0, use the dropdown to detect percent, opponent resources, or both."

    // Battle speed-up button
    val battleSpeedupExplain
        get() = if (LanguageManager.isChinese)
            "启用后，战斗中检测右上角加速按钮并自动点击 1 次加速。"
        else "Detect and click the 2x speed button once during battle."

    // CN auto battle
    val autoBattleCn get() = if (LanguageManager.isChinese) "自动战斗" else "Auto Battle (CN)"
    val autoBattleCnExplain
        get() = if (LanguageManager.isChinese)
            "开启后，布阵界面将检测自动战斗按钮并点击；若未检测到（说明账号无此功能），则使用原有下兵方式兜底。"
        else "When enabled, detects and clicks the auto-battle button on the deploy screen. Falls back to the normal deploy method if the button is not found (account lacks this feature)."

    // Memory cleaning
    val memoryCleanSuccess get() = if (LanguageManager.isChinese) "记忆文件清除成功" else "Memory cache cleared"
    val memoryCleanFail get() = if (LanguageManager.isChinese) "清除失败，可能记忆文件不存在" else "Clear failed - cache may not exist"
    val clearMemoryExplain get() = if (LanguageManager.isChinese) "辅助会缓存账号的运行状态，例如当前账号是否已完成突袭、部落竞赛等任务。切换账号后若未清除缓存，辅助将沿用先前账号的状态信息，可能导致异常操作。" else "The app caches account status (e.g., Raid, Clan Games). If not cleared when switching accounts, it may use old data and cause issues."
    val copyLogs get() = if (LanguageManager.isChinese) "复制日志" else "Copy Logs"
    val deleteFolder get() = if (LanguageManager.isChinese) "删除" else "Delete"
    val renameFolder get() = if (LanguageManager.isChinese) "更改名称" else "Rename"
    val saveFolderName get() = if (LanguageManager.isChinese) "保存" else "Save"
    val folderNameLabel get() = if (LanguageManager.isChinese) "文件夹名称:" else "Folder Name:"

    // Server酱³
    val serverchanLoginFailedTitle get() = if (LanguageManager.isChinese) "辅助账号登录失败" else "Login Failed - Assistant Account"
    val serverchanLoginFailedMsg get() = if (LanguageManager.isChinese) "辅助账号登录失败，请检查邮箱密码或网络连接" else "Assistant account login failed. Please check email/password or network."
    val serverchanSectionTitle get() = if (LanguageManager.isChinese) "推送通知" else "Push Notification"
    val serverchanTestBtn get() = if (LanguageManager.isChinese) "发送测试" else "Send Test"
    val serverchanTestSending get() = if (LanguageManager.isChinese) "正在发送测试通知..." else "Sending test notification..."
    val serverchanTestSuccess get() = if (LanguageManager.isChinese) "测试通知已发送" else "Test notification sent"
    fun serverchanTestFailed(reason: String) = if (LanguageManager.isChinese) "测试通知发送失败：$reason" else "Test notification failed: $reason"
    val serverchanSendKeyEmpty get() = if (LanguageManager.isChinese) "请先填写 Server酱³ SendKey" else "Please enter your Server酱³ SendKey first"
    val serverchanKickTitle get() = if (LanguageManager.isChinese) "进入游戏失败 - 紫孔雀" else "Game Enter Failed - ZKQ"
    fun serverchanKickMsg(deviceRemark: String, accountIndex: Int, remark: String, timeoutSec: Int) =
        if (LanguageManager.isChinese) "设备：${deviceRemark.ifBlank { "无" }}，账号${accountIndex}，备注：${remark.ifBlank { "无" }}。\n倒计时${timeoutSec}结束，进入游戏失败"
        else "Device: ${deviceRemark.ifBlank { "N/A" }}, Account $accountIndex, Remark: ${remark.ifBlank { "N/A" }}.\nCountdown $timeoutSec ended, failed to enter game."

    val serverchanWarTitle get() = if (LanguageManager.isChinese) "部落战已开战 - 紫孔雀" else "Clan War Started - ZKQ"
    fun serverchanWarMsg(deviceRemark: String, accountIndex: Int, remark: String) =
        if (LanguageManager.isChinese) "设备：${deviceRemark.ifBlank { "无" }}，账号${accountIndex}，备注:${remark.ifBlank { "无" }}。部落战已开战"
        else "Device: ${deviceRemark.ifBlank { "N/A" }}, Account $accountIndex, Remark: ${remark.ifBlank { "N/A" }}. Clan war has started."

    val serverchanLeagueTitle get() = if (LanguageManager.isChinese) "联赛已开战 - 紫孔雀" else "League Started - ZKQ"
    fun serverchanLeagueMsg(deviceRemark: String, accountIndex: Int, remark: String) =
        if (LanguageManager.isChinese) "设备：${deviceRemark.ifBlank { "无" }}，账号${accountIndex}，备注:${remark.ifBlank { "无" }}。联赛已开战"
        else "Device: ${deviceRemark.ifBlank { "N/A" }}, Account $accountIndex, Remark: ${remark.ifBlank { "N/A" }}. League has started."

    val serverchanUpgradeTitle get() = if (LanguageManager.isChinese) "升级即将完成 - 紫孔雀" else "Upgrade Complete Soon - ZKQ"
    val serverchanUpgradingTitle get() = if (LanguageManager.isChinese) "正在升级 - 紫孔雀" else "Upgrading - ZKQ"
    val pushProviderServerChan get() = "Server酱³"
    val pushProviderWxPusher get() = "WxPusher"
    val pushProviderPushPlus get() = "PushPlus"
    val pushProviderTelegram get() = "Telegram"

    // Notification mode options
    val notifyModeManual get() = if (LanguageManager.isChinese) "手动打(只通知不打)" else "Manual (Notify Only)"
    val notifyModeAuto get() = if (LanguageManager.isChinese) "自动打(通知并攻击)" else "Auto (Notify & Attack)"

    // Enhanced CN Game Update
    val enhancedUpdateHint get() = if (LanguageManager.isChinese)
        "开启后，等待国服游戏内更新超时，将自动从所选渠道下载最新 APK 并通过 Root 静默安装。"
    else
        "When enabled, after in-game update wait times out, auto-download latest CN APK from selected channel and install via root."
    val enhancedSourceAppStore get() = "应用宝 (a.app.qq.com)"
    val enhancedSourceTencent get() = "腾讯官网 (coc.qq.com)"

    // Cloud Expiry Notification
    val cloudExpireSectionTitle get() = if (LanguageManager.isChinese) "云机到期通知" else "Cloud Expiry Notification"
    fun cloudExpireExpiringTitle(deviceRemark: String, daysLeft: Int) =
        if (LanguageManager.isChinese) "云机即将在 ${daysLeft} 天后到期 - ${deviceRemark.ifBlank { "无" }}" else "Cloud Expiring in $daysLeft Days - ${deviceRemark.ifBlank { "N/A" }}"
    fun cloudExpireExpiringMsg(deviceRemark: String, daysLeft: Int, expireDate: String) =
        if (LanguageManager.isChinese) "设备备注：${deviceRemark.ifBlank { "无" }}，将在 $daysLeft 天后到期，到期时间：$expireDate\n请及时备份游戏存档并清除全部数据，保护账号安全。"
        else "Device: ${deviceRemark.ifBlank { "N/A" }}, expires in $daysLeft days, Expiry: $expireDate\nPlease back up game saves and clear all data to protect your account."
    val cloudExpireResetTitle get() = if (LanguageManager.isChinese) "云机已自动重置 - " else "Cloud Expiry Reset - "
    fun cloudExpireResetMsg(deviceRemark: String, resetDays: Int, newExpireDate: String) =
        if (LanguageManager.isChinese) "设备备注：${deviceRemark.ifBlank { "无" }}，已过期，自动重置 $resetDays 天，新到期时间：$newExpireDate"
        else "Device: ${deviceRemark.ifBlank { "N/A" }}, expired. Auto reset $resetDays days, new expiry: $newExpireDate"

    // WebDAV Sync
    val webdavSectionTitle get() = if (LanguageManager.isChinese) "WebDAV 文件夹管理" else "WebDAV Folder Management"
    val webdavSectionDesc get() = if (LanguageManager.isChinese) "此功能可以将国际服存档上传到自己的网盘进行备份与恢复" else "Upload global saves to your own cloud drive for backup and restore"
    val webdavUploadGlobalSave get() = if (LanguageManager.isChinese) "上传国际服存档" else "Upload Global Save"
    val webdavDownloadGlobalSave get() = if (LanguageManager.isChinese) "下载国际服存档" else "Download Global Save"
    val webdavUploading get() = if (LanguageManager.isChinese) "正在上传..." else "Uploading..."
    val webdavDownloading get() = if (LanguageManager.isChinese) "正在下载..." else "Downloading..."
    val webdavUploadSuccess get() = if (LanguageManager.isChinese) "上传成功！" else "Upload successful!"
    val webdavDownloadSuccess get() = if (LanguageManager.isChinese) "下载成功！" else "Download successful!"
    val webdavGlobalSavePath get() = if (LanguageManager.isChinese) "国际服存档" else "Global save"
    val webdavUrlEmpty get() = if (LanguageManager.isChinese) "请先填写 WebDAV 服务器地址" else "Please enter the WebDAV server URL"
    val webdavCredentialEmpty get() = if (LanguageManager.isChinese) "请先填写 WebDAV 用户名和密码" else "Please enter WebDAV username and password"
    val webdavRemarkEmpty get() = if (LanguageManager.isChinese) "请先在「通用设置」中填写设备备注" else "Please set your device remark in General Settings first"
    val webdavEditMode get() = if (LanguageManager.isChinese) "编辑模式" else "Edit Mode"
    val webdavEditModeExit get() = if (LanguageManager.isChinese) "完成编辑" else "Done Editing"
    val webdavSyncMode get() = if (LanguageManager.isChinese) "同步模式" else "Sync Mode"
    val webdavSyncModeNone get() = if (LanguageManager.isChinese) "请选择同步模式" else "Select sync mode"
    val webdavSyncModeOverwrite get() = if (LanguageManager.isChinese) "覆盖同步" else "Overwrite Sync"
    val webdavSyncModeIncremental get() = if (LanguageManager.isChinese) "增量同步" else "Incremental Sync"
    val webdavSyncOverwriteDesc get() = if (LanguageManager.isChinese) "按原始序号直接覆盖远程文件，本地与远程保持完全一致" else "Overwrite remote files using original indices — keeps local and remote identical"
    val webdavSyncIncrementalDesc get() = if (LanguageManager.isChinese) "自动分配新序号上传，本地存档按序号递增排列，避免覆盖已有存档" else "Auto-assign new indices on upload — local archives increment without overwriting existing files"
    val webdavSelectAll get() = if (LanguageManager.isChinese) "全选" else "Select All"

    // WebDAV one-click copy/paste credentials
    val webdavCopyCredentials get() = if (LanguageManager.isChinese) "一键复制" else "Copy"
    val webdavPasteCredentials get() = if (LanguageManager.isChinese) "一键粘贴" else "Paste"
    val webdavPasteDialogTitle get() = if (LanguageManager.isChinese) "粘贴 WebDAV 信息" else "Paste WebDAV Info"
    val webdavPasteConfirm get() = if (LanguageManager.isChinese) "确定" else "OK"
    val webdavPasteCancel get() = if (LanguageManager.isChinese) "取消" else "Cancel"
    val webdavCopiedMsg get() = if (LanguageManager.isChinese) "已复制服务器地址、用户名、密码" else "Copied server URL, username, and password"
    val webdavPastedMsg get() = if (LanguageManager.isChinese) "已填入 WebDAV 信息" else "WebDAV info filled in"
    val webdavPasteInvalidMsg get() = if (LanguageManager.isChinese) "格式错误，请粘贴包含服务器地址、用户名、密码的内容" else "Invalid format — paste content with URL, username, and password"
}

