package com.coc.zkqcode.jar.code.universal

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ClipboardHelper
import com.coc.zkqcode.core.util.basic.delayWithMultiplier
import com.coc.zkqcode.jar.code.universal.push.PushMessage
import com.coc.zkqcode.jar.code.universal.push.PushRouter
import com.coc.zkqcode.jar.code.colorschema.MyColors
import com.coc.zkqcode.jar.code.universal.colors.findMultiColorsUntil
import com.coc.zkqcode.jar.code.universal.smalltools.data.readMemory
import com.coc.zkqcode.jar.code.universal.smalltools.data.writeMemory
import com.coc.zkqcode.jar.code.universal.smalltools.input.TouchActions
import com.coc.zkqcode.jar.ui.LanguageManager
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.schema.Schema
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken

// ── Persistent schedule storage ─────────────────────────────────────────
private data class ScheduledUpgrade(
    val account: Int,
    val dataId: Int,
    val lvl: Int,
    val name: String,
    val categoryLabel: String,
    val completionTs: Long,  // epoch seconds
    var notified: Boolean = false
)

private val gson = Gson()
private const val SCHEDULE_KEY = "upgrade_schedule_all"

/** Save all tracked upgrades to memory.json. */
private suspend fun saveSchedule(items: List<ScheduledUpgrade>) {
    writeMemory(SCHEDULE_KEY, gson.toJson(items))
}

/** Load all tracked upgrades from memory.json. */
private suspend fun loadSchedule(): List<ScheduledUpgrade> {
    val raw = readMemory(SCHEDULE_KEY)
    if (raw.isBlank()) return emptyList()
    return try {
        gson.fromJson(raw, object : TypeToken<List<ScheduledUpgrade>>() {}.type)
    } catch (_: Exception) { emptyList() }
}

// ── Called on game exit: export JSON & save completion timestamps ───────

suspend fun checkUpgradeTimers() {
    if (GlobalVars.configStates[Schema.GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_NOTIFY.key]?.value != "1") return

    val clipboardText = copyGameJson() ?: return
    val jsonObject = try { Gson().fromJson(clipboardText.trim(), JsonObject::class.java) } catch (_: Exception) { return } ?: return
    val timerItems = extractTimerItems(jsonObject)
    if (timerItems.isEmpty()) return

    val nowSec = System.currentTimeMillis() / 1000L
    val jsonTs = jsonObject.get("timestamp")?.asLong ?: nowSec
    val boosts = parseBoosts(jsonObject)
    val helpers = parseHelpers(jsonObject)
    val acc = InGameVars.currentAccountNumber
    val advanceMin = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_ADVANCE.key]?.value?.toIntOrNull() ?: 10

    val catLabel = mapOf(
        "buildings" to "主世界建筑", "heroes" to "英雄", "traps" to "陷阱",
        "guardians" to "守卫", "units" to "实验室", "spells" to "法术",
        "siege_machines" to "攻城机器", "pets" to "战宠",
        "buildings2" to "夜世界建筑", "heroes2" to "夜世界英雄",
        "units2" to "星空实验室", "traps2" to "夜世界陷阱"
    )

    // Build full overview + schedule
    val allEntries = timerItems.map { entry ->
        val completionTs = calcCompletionTs(entry, jsonTs, boosts, helpers)
        val remainSec = (completionTs - nowSec).toInt().coerceAtLeast(1)
        val cat = catLabel[entry.category] ?: entry.category
        val name = getItemName(entry.dataId)
        UpgradeOverview(entry, remainSec, cat, name, completionTs)
    }

    val allFpParts = allEntries.map { "${it.entry.dataId}_${it.entry.lvl}" }.sorted()
    val fingerprint = allFpParts.joinToString(",")
    val sigKey = "upgrade_timer_sig_$acc"
    if (readMemory(sigKey) == fingerprint) return // overview unchanged

    // Build grouped display
    val deviceRemark = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
    val remark = GlobalVars.configStates["remark${acc}"]?.value.orEmpty()
    val remarkText = remark.ifBlank { if (LanguageManager.isChinese) "无" else "N/A" }
    val lines = mutableListOf<String>()
    lines.add(if (LanguageManager.isChinese) "设备：${deviceRemark.ifBlank { "无" }}，账号${acc}，备注:${remarkText}" else "Device: ${deviceRemark.ifBlank { "N/A" }}, Account $acc, Remark: $remarkText")

    val shortest = allEntries.minByOrNull { it.remainSec }!!
    val advanceSec = advanceMin * 60

    for ((header, items) in allEntries.groupBy { "[${it.cat}]" }.toSortedMap()) {
        lines.add(header)
        for (item in items.sortedBy { it.entry.dataId }) {
            val sb = StringBuilder(" · ${item.name} (等级 ${item.entry.lvl}) 剩余 ${formatDuration(item.remainSec)}")
            if (item.remainSec <= advanceSec && item === shortest) {
                sb.append("  *即将在${advanceMin}分钟后完成*")
            }
            lines.add(sb.toString())
        }
    }

    PushRouter.send(PushMessage(title = Strings.serverchanUpgradingTitle, desp = lines.joinToString("\n")))
    writeMemory(sigKey, fingerprint)

    // Save completion timestamps for background checking
    val existing = loadSchedule().toMutableList()
    // Remove old entries for this account
    existing.removeAll { it.account == acc }
    // Add new ones
    for (item in allEntries) {
        existing.add(ScheduledUpgrade(acc, item.entry.dataId, item.entry.lvl, item.name, item.cat, item.completionTs))
    }
    saveSchedule(existing)
}

// ── Background check: run periodically in main loop ─────────────────────

suspend fun checkScheduledUpgradeNotifications() {
    if (GlobalVars.configStates[Schema.GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key]?.value != "1") return
    val advanceMin = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_ADVANCE.key]?.value?.toIntOrNull() ?: return
    if (advanceMin <= 0) return
    val advanceSec = advanceMin * 60L
    val nowSec = System.currentTimeMillis() / 1000L

    val schedule = loadSchedule().toMutableList()
    var changed = false
    val toNotify = mutableListOf<ScheduledUpgrade>()

    for (item in schedule) {
        if (item.notified) continue
        if (item.completionTs <= nowSec) {
            // Already completed — remove from tracking
            changed = true
            continue
        }
        if (item.completionTs - nowSec <= advanceSec) {
            toNotify.add(item)
            item.notified = true
            changed = true
        }
    }

    if (toNotify.isNotEmpty()) {
        // Send one notification per account summarizing what's about to complete
        val byAccount = toNotify.groupBy { it.account }
        for ((acc, items) in byAccount) {
            val deviceRemark = GlobalVars.configStates[Schema.GLOBAL_SETTINGS.DEVICE_REMARK.key]?.value.orEmpty()
            val remark = GlobalVars.configStates["remark${acc}"]?.value.orEmpty()
            val remarkText = remark.ifBlank { if (LanguageManager.isChinese) "无" else "N/A" }
            val summary = items.joinToString("\n") {
                " · ${it.name} (等级 ${it.lvl}) ${formatDuration(((it.completionTs - nowSec).toInt()))}"
            }
            val msg = if (LanguageManager.isChinese)
                "设备：${deviceRemark.ifBlank { "无" }}，账号${acc}，备注:${remarkText}\n即将在${advanceMin}分钟内完成的升级:\n$summary"
            else
                "Device: ${deviceRemark.ifBlank { "N/A" }}, Account $acc, Remark: $remarkText\nUpgrades completing in $advanceMin minutes:\n$summary"
            PushRouter.send(PushMessage(title = Strings.serverchanUpgradeTitle, desp = msg))
        }
    }

    // Remove completed items
    val cleaned = schedule.filter { it.completionTs > nowSec }
    if (cleaned.size != schedule.size || changed) {
        saveSchedule(cleaned)
    }
}

// ── Helper types & functions (private) ──────────────────────────────────

private data class TimerEntry(val dataId: Int, val lvl: Int, val timer: Int, val category: String, val helperTimer: Int = 0)
private data class UpgradeOverview(val entry: TimerEntry, val remainSec: Int, val cat: String, val name: String, val completionTs: Long)

private fun calcCompletionTs(e: TimerEntry, jsonTs: Long, b: Boosts, h: Helpers): Long {
    var ct = jsonTs + e.timer
    when (e.category) {
        "buildings","heroes","traps","guardians" -> {
            if (e.helperTimer > 0 && h.workerHelperLevel > 0) {
                val red = e.helperTimer * h.workerHelperLevel
                ct = if (red >= e.timer) jsonTs + (e.timer + h.workerHelperLevel) / (h.workerHelperLevel + 1) else jsonTs + e.timer - red
            }
            if (b.builderBoost > 0) { val c = ct - b.builderBoost * 9L; val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 9) / 10 else minOf(ct, c) }
            else if (b.builderConsumable > 0) { val c = ct - b.builderConsumable.toLong(); val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 1) / 2 else minOf(ct, c) }
        }
        "units","siege_machines","spells" -> {
            if (e.helperTimer > 0 && h.labHelperLevel > 0) {
                val red = e.helperTimer * h.labHelperLevel
                ct = if (red >= e.timer) jsonTs + (e.timer + h.labHelperLevel) / (h.labHelperLevel + 1) else jsonTs + e.timer - red
            }
            if (b.labBoost > 0) { val c = ct - b.labBoost * 23L; val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 23) / 24 else minOf(ct, c) }
            else if (b.labConsumable > 0) { val c = ct - b.labConsumable * 3L; val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 3) / 4 else minOf(ct, c) }
        }
        "pets" -> {
            if (b.petBoost > 0) { val c = ct - b.petBoost * 23L; val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 23) / 24 else minOf(ct, c) }
            else if (b.labConsumable > 0) { val c = ct - b.labConsumable * 3L; val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 3) / 4 else minOf(ct, c) }
        }
        "buildings2","traps2","heroes2","units2" -> {
            if (b.clocktowerBoost > 0) { val c = ct - b.clocktowerBoost * 9L; val d = ct - jsonTs; ct = if (c < jsonTs) jsonTs + (d + 9) / 10 else minOf(ct, c) }
        }
    }
    val raw = jsonTs + e.timer
    if (ct < jsonTs) ct = raw
    return ct
}

// ── Private helpers ─────────────────────────────────────────────────────

private suspend fun copyGameJson(): String? {
    if (InGameVars.currentGameVersion == GameVersion.CN) {
        TouchActions.tap(1230, 450)
        delayWithMultiplier(300)
        val settings = findMultiColorsUntil(listOf(MyColors.GameSettings), 1000) ?: return null
        TouchActions.tap(settings.x, settings.y)
    } else {
        TouchActions.tap(1230, 520)
    }
    delayWithMultiplier(300)
    TouchActions.tap(630, 610)
    delayWithMultiplier(400)
    TouchActions.swipe(630, 500, 630, -1500, delayAfter = 400)
    if (InGameVars.currentGameVersion == GameVersion.CN) TouchActions.tap(900, 300) else TouchActions.tap(910, 450)
    delayWithMultiplier(500)
    return ClipboardHelper.readClipboard()
}

private fun extractTimerItems(jsonObject: JsonObject): List<TimerEntry> {
    val result = mutableListOf<TimerEntry>()
    for (cat in listOf("buildings","buildings2","heroes","heroes2","units","units2","spells","siege_machines","pets","traps","traps2","guardians")) {
        val array = try { jsonObject.getAsJsonArray(cat) } catch (_: Exception) { null } ?: continue
        for (i in 0 until array.size()) {
            try {
                val obj = array[i].asJsonObject
                val dataId = obj.get("data")?.asInt ?: continue
                if (dataId == 1000097) { extractRefiningTableModules(obj, cat, result); continue }
                val lvl = obj.get("lvl")?.asInt ?: 0
                val timer = obj.get("timer")?.asInt ?: 0
                if (timer > 0) result.add(TimerEntry(dataId, lvl, timer, cat, obj.get("helper_timer")?.asInt ?: 0))
            } catch (_: Exception) {}
        }
    }
    return result
}

private fun extractRefiningTableModules(obj: JsonObject, cat: String, result: MutableList<TimerEntry>) {
    try {
        val lvl = obj.get("lvl")?.asInt ?: 0
        val types = obj.getAsJsonArray("types") ?: return
        for (t in 0 until types.size()) {
            val modules = types[t].asJsonObject.getAsJsonArray("modules") ?: continue
            for (m in 0 until modules.size()) {
                val mod = modules[m].asJsonObject
                val dataId = mod.get("data")?.asInt ?: continue
                val timer = mod.get("timer")?.asInt ?: 0
                if (timer > 0) result.add(TimerEntry(dataId, mod.get("lvl")?.asInt ?: 0, if (lvl == 2) 86400 else timer, cat, mod.get("helper_timer")?.asInt ?: 0))
            }
        }
    } catch (_: Exception) {}
}

private data class Boosts(val builderBoost: Int=0, val builderConsumable: Int=0, val labBoost: Int=0, val labConsumable: Int=0, val petBoost: Int=0, val clocktowerBoost: Int=0)
private fun parseBoosts(j: JsonObject): Boosts {
    val b = try { j.getAsJsonObject("boosts") } catch (_: Exception) { null } ?: return Boosts()
    return Boosts(b.get("builder_boost")?.asInt?:0, b.get("builder_consumable")?.asInt?:0, b.get("lab_boost")?.asInt?:0, b.get("lab_consumable")?.asInt?:0, b.get("pet_boost")?.asInt?:0, b.get("clocktower_boost")?.asInt?:0)
}
private data class Helpers(val workerHelperLevel: Int=0, val labHelperLevel: Int=0)
private fun parseHelpers(j: JsonObject): Helpers {
    val arr = try { j.getAsJsonArray("helpers") } catch (_: Exception) { null } ?: return Helpers()
    var w = 0; var l = 0
    for (i in 0 until arr.size()) try {
        val h = arr[i].asJsonObject; val d = h.get("data")?.asInt ?: continue; val lv = h.get("lvl")?.asInt ?: 0
        if (d == 93000000 || d == 124000000) w = lv
        if (d == 93000001 || d == 124000001) l = lv
    } catch (_: Exception) {}
    return Helpers(w, l)
}

private fun formatDuration(s: Int): String {
    val d = s / 86400; val h = (s % 86400) / 3600; val m = (s % 3600) / 60
    return buildString { if (d > 0) append("${d}天"); if (h > 0) append("${h}时"); if (m > 0 || isEmpty()) append("${m}分") }
}
private fun getItemName(id: Int) = ITEM_NAMES[id] ?: "未知($id)"
private val ITEM_NAMES = mapOf(
    1000000 to "兵营", 1000001 to "大本营", 1000002 to "圣水采集器", 1000003 to "圣水瓶",
    1000004 to "金矿", 1000005 to "储金罐", 1000006 to "训练营", 1000007 to "实验室",
    1000008 to "加农炮", 1000009 to "箭塔", 1000010 to "城墙", 1000011 to "法师塔",
    1000012 to "防空火箭", 1000013 to "迫击炮", 1000014 to "部落城堡", 1000015 to "工人小屋",
    1000019 to "特斯拉电磁塔", 1000020 to "法术工厂", 1000021 to "X连弩",
    1000023 to "暗黑重油钻井", 1000024 to "暗黑重油罐", 1000026 to "暗黑训练营",
    1000027 to "地狱塔", 1000028 to "空气炮", 1000029 to "暗黑法术工厂",
    1000031 to "天鹰火炮", 1000032 to "炸弹塔", 1000059 to "战车工坊",
    1000064 to "小博木屋", 1000067 to "投石炮", 1000068 to "战宠小屋", 1000070 to "铁匠铺",
    1000071 to "英雄殿堂", 1000072 to "法术塔", 1000077 to "擎天巨柱",
    1000079 to "复合机械塔", 1000084 to "多人箭塔", 1000085 to "弹跳加农炮",
    1000086 to "反击之塔", 1000089 to "火焰喷射器", 1000093 to "帮手小屋",
    1000097 to "精制台", 1000102 to "超级法师塔",
    1000033 to "城墙(夜)", 1000034 to "建筑大师大本营", 1000035 to "圣水采集器(夜)",
    1000036 to "圣水瓶(夜)", 1000037 to "金矿(夜)", 1000038 to "储金罐(夜)",
    1000039 to "时光钟楼", 1000040 to "建筑大师训练营", 1000041 to "双管加农炮",
    1000042 to "兵营(夜)", 1000043 to "特斯拉电磁塔(夜)", 1000044 to "加农炮(夜)",
    1000045 to "多管迫击炮", 1000046 to "星空实验室", 1000048 to "箭塔(夜)",
    1000049 to "预备营", 1000050 to "防空火箭(夜)", 1000051 to "守卫哨岗",
    1000052 to "超级特斯拉电磁塔", 1000053 to "奥仔哨站", 1000054 to "空中炸弹发射器(夜)",
    1000055 to "撼地巨石", 1000056 to "熔岩火炮(夜)", 1000057 to "巨型加农炮",
    1000058 to "宝石矿井", 1000063 to "熔岩发射器(夜)", 1000065 to "奥仔小屋",
    1000078 to "底座1", 1000080 to "底座2", 1000081 to "X连弩(夜)", 1000082 to "治疗小屋",
    28000000 to "蛮王", 28000001 to "女王", 28000002 to "永王",
    28000003 to "战斗机器", 28000004 to "王子", 28000005 to "战斗直升机",
    28000006 to "王子", 28000007 to "公爵",
    73000000 to "莱西", 73000001 to "大牦", 73000002 to "闪枭", 73000003 to "独角",
    73000004 to "凤凰", 73000007 to "猛蜥", 73000008 to "挖挖", 73000009 to "冰牙",
    73000010 to "灵狐", 73000011 to "水母", 73000016 to "啾啾", 73000017 to "乌鸦",
    12000000 to "隐形炸弹", 12000001 to "弹簧", 12000002 to "巨型炸弹",
    12000005 to "红雷", 12000006 to "黑雷", 12000008 to "骷髅陷阱",
    12000016 to "飓风陷阱", 12000020 to "终极炸弹",
    12000010 to "隐形弹簧", 12000011 to "弹射陷阱", 12000013 to "地雷(夜)", 12000014 to "巨型地雷(夜)",
    26000000 to "闪电法术", 26000001 to "治疗法术", 26000002 to "狂暴法术",
    26000003 to "弹跳法术", 26000005 to "冰冻法术", 26000009 to "毒药法术",
    26000010 to "地震法术", 26000011 to "极速法术", 26000016 to "镜像法术",
    26000017 to "铁皮法术", 26000028 to "蝙蝠法术", 26000035 to "隐身法术",
    26000053 to "回溯法术", 26000070 to "蔓生法术", 26000098 to "复苏法术",
    26000109 to "图腾法术", 26000120 to "冰障法术",
    4000000 to "野蛮人", 4000001 to "弓箭手", 4000002 to "哥布林", 4000003 to "巨人",
    4000004 to "炸弹人", 4000005 to "气球", 4000006 to "法师", 4000007 to "天使",
    4000008 to "飞龙", 4000009 to "皮卡", 4000010 to "亡灵", 4000011 to "野猪骑士",
    4000012 to "武神", 4000013 to "石头人", 4000015 to "女巫", 4000017 to "熔岩猎犬",
    4000022 to "蓝胖", 4000023 to "龙宝", 4000024 to "矿工", 4000053 to "雪怪",
    4000058 to "冰人", 4000059 to "雷龙", 4000065 to "龙骑", 4000082 to "英雄猎手",
    4000095 to "雷霆泰坦", 4000097 to "守护者学徒", 4000110 to "根蔓骑士",
    4000123 to "伊得鲁", 4000132 to "巨矛", 4000150 to "烈焰熔炉", 4000177 to "陨石格伦",
    4000051 to "攻城战车", 4000052 to "攻城飞艇", 4000062 to "攻城气球",
    4000075 to "战营", 4000087 to "滚木车", 4000091 to "投石车", 4000092 to "钻地机", 4000135 to "部队发射器",
    4000031 to "狂暴野蛮人", 4000032 to "隐秘弓箭手", 4000033 to "异变亡灵",
    4000034 to "巨人拳击手", 4000035 to "炸弹兵", 4000036 to "雷霆皮卡",
    4000037 to "加农炮战车", 4000038 to "骷髅气球", 4000041 to "飞龙宝宝",
    4000042 to "暗夜女巫", 4000070 to "野猪飞骑", 4000106 to "电火法师",
    93000000 to "工人助手", 93000001 to "实验室助手", 93000002 to "炼金术师",
    93000003 to "探矿者", 124000000 to "工人助手", 124000001 to "实验室助手",
    124000002 to "炼金术师", 124000003 to "探矿者",
    161000000 to "远袭者", 161000001 to "粉碎者", 107000000 to "远袭者", 107000001 to "粉碎者",
    152000008 to "熔岩火炮", 103000008 to "熔岩火炮",
    151000024 to "熔岩火炮-血量", 102000024 to "熔岩火炮-血量",
    151000025 to "熔岩火炮-伤害", 102000025 to "熔岩火炮-伤害",
    151000026 to "熔岩火炮-连发数量", 102000026 to "熔岩火炮-连发数量",
    152000009 to "空中炸弹发射器", 103000009 to "空中炸弹发射器",
    151000027 to "空中炸弹发射器-血量", 102000027 to "空中炸弹发射器-血量",
    151000028 to "空中炸弹发射器-伤害", 102000028 to "空中炸弹发射器-伤害",
    151000029 to "空中炸弹发射器-攻速", 102000029 to "空中炸弹发射器-攻速",
    152000010 to "熔岩发射器", 103000010 to "熔岩发射器",
    151000030 to "熔岩发射器-血量", 102000030 to "熔岩发射器-血量",
    151000031 to "熔岩发射器-伤害", 102000031 to "熔岩发射器-伤害",
    151000032 to "熔岩发射器-范围", 102000032 to "熔岩发射器-范围"
)
