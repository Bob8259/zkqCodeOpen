package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.code.universal.push.ServerChanHelper
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun LazyListScope.specialSettings() {
    item { SettingSection { BatchCreateAccount() } }
    item { SettingSection { RandomScheduling() } }
    item {
        SettingSection {
            val state = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.UPDATE_CN_GAME.key) { mutableStateOf("0") }
            val enabled = state.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.UPDATE_CN_GAME.key)
            if (enabled) { SettingInputRow(key = GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.key) }
        }
    }
    item {
        SettingSection {
            val state = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.TEMP_PROTECTION.key) { mutableStateOf("0") }
            val enabled = state.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.TEMP_PROTECTION.key)
            if (enabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_PAUSE_THRESHOLD.key)
                SettingInputRow(key = GLOBAL_SETTINGS.TEMP_RESUME_THRESHOLD.key)
            }
        }
    }
    item {
        SettingSection {
            val state = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SCHEDULED_RESTART.key) { mutableStateOf("0") }
            val enabled = state.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SCHEDULED_RESTART.key)
            if (enabled) { SettingInputRow(key = GLOBAL_SETTINGS.RESTART_INTERVAL.key) }
        }
    }
    // ── Push Notification ─────────────────────────────────────────────────
    item {
        SettingSection {
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(text = Strings.serverchanSectionTitle, style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
            }
            SettingDropdown(key = GLOBAL_SETTINGS.PUSH_PROVIDER.key, options = listOf(Strings.pushProviderServerChan, Strings.pushProviderWxPusher, Strings.pushProviderPushPlus, Strings.pushProviderTelegram))
            val provider = GlobalVars.configStates[GLOBAL_SETTINGS.PUSH_PROVIDER.key]?.value ?: "0"
            when (provider) {
                "0" -> SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_SENDKEY.key)
                "1" -> { SettingInputRow(key = GLOBAL_SETTINGS.WXPUSHER_APP_TOKEN.key); SettingInputRow(key = GLOBAL_SETTINGS.WXPUSHER_UID.key) }
                "2" -> SettingInputRow(key = GLOBAL_SETTINGS.PUSHPLUS_TOKEN.key)
                "3" -> { SettingInputRow(key = GLOBAL_SETTINGS.TELEGRAM_BOT_TOKEN.key); SettingInputRow(key = GLOBAL_SETTINGS.TELEGRAM_CHAT_ID.key) }
            }
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LOGIN_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_KICK_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY.key)
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_NOTIFY.key)
            val scheduleState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key) { mutableStateOf("0") }
            val scheduleEnabled = scheduleState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key)
            if (scheduleEnabled) { SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_ADVANCE.key) }
            val scope = rememberCoroutineScope()
            CustomButton(text = Strings.serverchanTestBtn, marginTop = 6.dp, onClick = { /* push test */ })
        }
    }
    // ── WebDAV Sync ────────────────────────────────────────────────────────
    item {
        SettingSection {
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(text = Strings.webdavSectionTitle, style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
            }
            Text(
                text = Strings.webdavSectionDesc,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextPrimary.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val sdPath = Environment.getExternalStorageDirectory().path
            SettingInputRow(key = GLOBAL_SETTINGS.WEBDAV_URL.key)
            SettingInputRow(key = GLOBAL_SETTINGS.WEBDAV_USERNAME.key)
            SettingInputRow(key = GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)

            fun getConfig(k: String) = GlobalVars.configStates[k]?.value.orEmpty()
            fun readSdFile(p: String): ByteArray? {
                val l = ShellHelper.runBlockingOutput("base64 \"$p\""); if (l.isEmpty()) return null
                return try { java.util.Base64.getDecoder().decode(l.joinToString("")) } catch (_: Exception) { null }
            }
            fun writeSdFile(d: ByteArray, p: String): Boolean {
                return ShellHelper.runBlocking("echo \"${java.util.Base64.getEncoder().encodeToString(d)}\" | base64 -d > \"$p\"").isSuccess
            }

            val deviceList = remember { mutableStateOf(listOf<String>()) }
            val expandedDevices = remember { mutableStateOf(setOf<String>()) }
            val expandedFolders = remember { mutableStateOf(setOf<String>()) }
            val deviceArchiveMap = remember { mutableStateOf(mapOf<String, List<String>>()) }
            val deviceConfigMap = remember { mutableStateOf(mapOf<String, Boolean>()) }
            val checkedItems = remember { mutableStateOf(mapOf<String, Set<String>>()) }
            val isProcessing = remember { mutableStateOf(false) }
            val syncMode = remember { mutableStateOf(0) }
            val overwriteSync = remember { mutableStateOf(false) }
            val incrementalSync = remember { mutableStateOf(false) }

            fun refreshDeviceList() {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                if (url.isBlank()) { ShowMessage("请先填写 WebDAV 服务器信息", isChecking = false); return }
                scope.launch {
                    isProcessing.value = true; ShowMessage("正在扫描远程文件夹...", isChecking = false)
                    val r = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listDirectories(url, username, password, "zkqFiles") }
                    if (r is String) { ShowMessage(r, isChecking = false); isProcessing.value = false; return@launch }
                    @Suppress("UNCHECKED_CAST") val devices = (r as List<String>).sorted(); deviceList.value = devices
                    val am = mutableMapOf<String, List<String>>(); val cm = mutableMapOf<String, Boolean>()
                    for (d in devices) {
                        val fr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listFiles(url, username, password, "zkqFiles/$d/zkqGlobalGameSave") }
                        @Suppress("UNCHECKED_CAST")
                        am[d] = if (fr is List<*>) { val frl = fr as List<String>; frl.filter { it.endsWith(".tar.gz") }.sorted() } else emptyList()
                        val cr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listFiles(url, username, password, "zkqFiles/$d/config") }
                        cm[d] = cr is List<*> && cr.any { it.toString().endsWith("zkq_config.json") }
                    }
                    deviceArchiveMap.value = am; deviceConfigMap.value = cm
                    isProcessing.value = false; ShowMessage("找到 ${devices.size} 个文件夹", isChecking = false)
                }
            }

            fun performUpload() {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                val localSaveDir = "$sdPath/zkqFiles/zkqGlobalGameSave"; val localConfigPath = "$sdPath/zkqFiles/zkq_config.json"; val tempDir = "${context.filesDir.absolutePath}/webdav_tar"; val mode = syncMode.value
                val targets = checkedItems.value.keys.toList()
                checkedItems.value = emptyMap()
                scope.launch {
                    isProcessing.value = true; var suc = 0; var fail = 0
                    for (device in targets) {
                        if (mode != 2) {
                            ShowMessage("上传配置到 $device...", isChecking = false)
                            val cfg = withContext(Dispatchers.IO) { readSdFile(localConfigPath) }
                            if (cfg != null) { val e = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.upload(url, username, password, "zkqFiles/$device/config/zkq_config.json", cfg) }; if (e == null) suc++ else fail++ } else fail++
                        }
                        if (mode != 1) {
                            val dirs = withContext(Dispatchers.IO) { ShellHelper.runBlockingOutput("ls -d \"$localSaveDir\"/*/ 2>/dev/null").map { it.trimEnd('/').substringAfterLast("/") }.filter { it.isNotBlank() } }
                            val inc = incrementalSync.value && mode != 1
                            val startIdx = if (inc) {
                                val rf = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listFiles(url, username, password, "zkqFiles/$device/zkqGlobalGameSave") }
                                @Suppress("UNCHECKED_CAST")
                            if (rf is List<*>) { val rfl = rf as List<String>; val nums = rfl.mapNotNull { it.removeSuffix(".tar.gz").trim().toIntOrNull() }; (nums.maxOrNull() ?: 0) + 1 } else 1
                            } else 1
                            val upl = dirs.mapNotNull { it.trim().toIntOrNull() }.sorted(); var seq = startIdx
                            for (n in upl) {
                                val sn = if (inc) { val s = seq; seq++; s } else n; val ss = sn.toString()
                                ShowMessage("上传 $device / $ss...", isChecking = false)
                                val tf = "$tempDir/$ss.tar.gz"; val tok = withContext(Dispatchers.IO) { ShellHelper.runBlocking("mkdir -p \"$tempDir\" && tar -czf \"$tf\" -C \"$localSaveDir\" \"$n\"") }
                                if (!tok.isSuccess) { fail++; continue }; val d = withContext(Dispatchers.IO) { readSdFile(tf) }
                                if (d == null) { fail++; continue }; val e = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.upload(url, username, password, "zkqFiles/$device/zkqGlobalGameSave/$ss.tar.gz", d) }
                                if (e == null) suc++ else fail++; withContext(Dispatchers.IO) { ShellHelper.runBlockingNoOutput("rm -f \"$tf\"") }
                            }
                        }
                    }
                    ShowMessage(if (fail == 0) "一键上传完成！" else "一键上传完成 ($suc 成功, $fail 失败)", isChecking = false)
                    refreshDeviceList(); isProcessing.value = false
                }
            }

            fun performDownload() {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                val localSaveDir = "$sdPath/zkqFiles/zkqGlobalGameSave"; val localConfigPath = "$sdPath/zkqFiles/zkq_config.json"; val tempDir = "${context.filesDir.absolutePath}/webdav_tar"; val mode = syncMode.value
                val inc = incrementalSync.value && mode != 1
                val targets = checkedItems.value.toList()
                checkedItems.value = emptyMap()
                scope.launch {
                    isProcessing.value = true; var suc = 0; var fail = 0
                    for ((device, items) in targets) {
                        for (itemId in items) {
                            val isCfg = itemId == "config"; if (mode == 1 && !isCfg) continue; if (mode == 2 && isCfg) continue
                            if (isCfg) {
                                ShowMessage("下载配置从 $device...", isChecking = false)
                                val cr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.download(url, username, password, "zkqFiles/$device/config/zkq_config.json") }
                                if (cr is ByteArray) { if (withContext(Dispatchers.IO) { writeSdFile(cr, localConfigPath) }) suc++ else fail++ } else fail++
                            } else {
                                val rn = itemId.removeSuffix(".tar.gz"); val rNum = rn.toIntOrNull()
                                val ldn = if (inc && rNum != null) {
                                    val ld = withContext(Dispatchers.IO) { ShellHelper.runBlockingOutput("ls -d \"$localSaveDir\"/*/ 2>/dev/null").mapNotNull { it.trimEnd('/').substringAfterLast("/").toIntOrNull() } }
                                    ((ld.maxOrNull() ?: 0) + 1).toString()
                                } else rn
                                ShowMessage("下载 $device / $ldn...", isChecking = false)
                                val dr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.download(url, username, password, "zkqFiles/$device/zkqGlobalGameSave/$itemId") }
                                if (dr !is ByteArray) { fail++; continue }; val tf = "$tempDir/$itemId"
                                val wok = withContext(Dispatchers.IO) { ShellHelper.runBlocking("mkdir -p \"$tempDir\" && echo \"${java.util.Base64.getEncoder().encodeToString(dr)}\" | base64 -d > \"$tf\"") }
                                if (!wok.isSuccess) { fail++; continue }
                                if (withContext(Dispatchers.IO) { ShellHelper.runBlocking("mkdir -p \"$localSaveDir\" && tar -xzf \"$tf\" -C \"$localSaveDir\"").isSuccess }) {
                                    if (ldn != rn) { withContext(Dispatchers.IO) { ShellHelper.runBlocking("mv \"$localSaveDir/$rn\" \"$localSaveDir/$ldn\"") } }
                                    suc++
                                } else fail++
                                withContext(Dispatchers.IO) { ShellHelper.runBlockingNoOutput("rm -f \"$tf\"") }
                            }
                        }
                    }
                    ShowMessage(if (fail == 0) "一键下载完成！" else "一键下载完成 ($suc 成功, $fail 失败)", isChecking = false)
                    refreshDeviceList(); isProcessing.value = false
                }
            }

            // ── Login ───────────────────────────────────────────────────────
            CustomButton(text = "WebDAV 登录", marginTop = 6.dp, onClick = {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                if (url.isBlank()) { ShowMessage("请先填写 WebDAV 服务器地址", isChecking = false); return@CustomButton }
                if (username.isBlank() || password.isBlank()) { ShowMessage("请先填写 WebDAV 用户名和密码", isChecking = false); return@CustomButton }
                scope.launch {
                    isProcessing.value = true; ShowMessage("正在连接 WebDAV 服务器...", isChecking = false)
                    val r = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listDirectories(url, username, password, "zkqFiles") }
                    if (r is String) { ShowMessage("连接失败: $r", isChecking = false) }
                    else { ShowMessage("WebDAV 登录成功！", isChecking = false); refreshDeviceList() }
                    isProcessing.value = false
                }
            })

            // ── Create folder ────────────────────────────────────────────────
            val folderName = remember { mutableStateOf("") }
            Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("文件夹名称:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 8.dp))
                BasicTextField(value = folderName.value, onValueChange = { folderName.value = it }, modifier = Modifier.widthIn(min = 80.dp).background(Color.White, RoundedCornerShape(4.dp)).border(1.dp, AppColors.Border, RoundedCornerShape(4.dp)).padding(4.dp).heightIn(max = 120.dp).verticalScroll(rememberScrollState()))
            }
            CustomButton(text = "新建文件夹", onClick = {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key); val name = folderName.value.trim()
                if (url.isBlank()) { ShowMessage("请先填写 WebDAV 服务器地址", isChecking = false); return@CustomButton }
                if (username.isBlank() || password.isBlank()) { ShowMessage("请先填写 WebDAV 用户名和密码", isChecking = false); return@CustomButton }
                if (name.isBlank()) { ShowMessage("请先输入文件夹名称", isChecking = false); return@CustomButton }
                scope.launch {
                    isProcessing.value = true; ShowMessage("正在创建文件夹: $name ...", isChecking = false)
                    var ok = true; var err: String? = null
                    for (p in listOf("zkqFiles/$name/config", "zkqFiles/$name/zkqGlobalGameSave")) {
                        val e = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.ensureRemotePath(url, username, password, p) }
                        if (e != null) { ok = false; err = e }
                    }
                    if (ok) { ShowMessage("文件夹创建成功: $name", isChecking = false); folderName.value = "" } else { ShowMessage("创建失败: $err", isChecking = false) }
                    refreshDeviceList(); isProcessing.value = false
                }
            })

            // ── Device list ──────────────────────────────────────────────────
            if (deviceList.value.isEmpty()) {
                Text("暂无文件夹，请先点击「新建文件夹」创建", style = MaterialTheme.typography.bodySmall, color = AppColors.NavInactive, modifier = Modifier.padding(top = 8.dp, start = 4.dp))
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    deviceList.value.forEach { device ->
                        val archives = deviceArchiveMap.value[device] ?: emptyList(); val hasCfg = deviceConfigMap.value[device] ?: false; val cnt = archives.size
                        val exp = expandedDevices.value.contains(device); val ckd = checkedItems.value[device] ?: emptySet()
                        val ids = mutableListOf<String>(); if (hasCfg) ids.add("config"); ids.addAll(archives)
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            val full = ids.isNotEmpty() && ckd.containsAll(ids); val empty = ids.isEmpty()
                            Checkbox(checked = full || (empty && device in checkedItems.value.keys), enabled = true, onCheckedChange = {
                                if (empty) { checkedItems.value = if (device in checkedItems.value.keys) checkedItems.value - device else checkedItems.value + (device to emptySet()) }
                                else { checkedItems.value = if (full) checkedItems.value - device else checkedItems.value + (device to ids.toSet()) }
                            }, modifier = Modifier.padding(end = 4.dp))
                            Row(modifier = Modifier.weight(1f).clickable(enabled = syncMode.value != 1) { expandedDevices.value = if (exp) expandedDevices.value - device else expandedDevices.value + device }) {
                                Text(text = "${if (exp && syncMode.value != 1) "▼" else if (!exp && syncMode.value != 1) "▶" else ""} $device", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                            }
                            Text(text = "$cnt 个存档", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive, modifier = Modifier.padding(start = 8.dp))
                        }
                        if (exp && syncMode.value != 1) {
                            Column(modifier = Modifier.padding(start = 40.dp)) {
                                if (hasCfg) { val ce = "$device/config" in expandedFolders.value
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                        Checkbox(checked = "config" in ckd, onCheckedChange = { c -> checkedItems.value = if (c) checkedItems.value + (device to (ckd + "config")) else checkedItems.value + (device to (ckd - "config")) }, modifier = Modifier.padding(end = 2.dp))
                                        Row(modifier = Modifier.clickable { expandedFolders.value = if (ce) expandedFolders.value - "$device/config" else expandedFolders.value + "$device/config" }) { Text(text = "${if (ce) "▼" else "▶"} 📁 配置文件", style = MaterialTheme.typography.labelSmall, color = AppColors.TextPrimary) }
                                    }
                                    if (ce) { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                        Checkbox(checked = "config" in ckd, onCheckedChange = { c -> checkedItems.value = if (c) checkedItems.value + (device to (ckd + "config")) else checkedItems.value + (device to (ckd - "config")) }, modifier = Modifier.padding(end = 2.dp))
                                        Text("⚙️ zkq_config.json", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive)
                                    } }
                                }
                                if (hasCfg || archives.isNotEmpty()) { val ae = "$device/archive" in expandedFolders.value
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                        val aac = archives.isNotEmpty() && archives.all { it in ckd }
                                        Checkbox(checked = aac, onCheckedChange = { checkedItems.value = if (aac) checkedItems.value + (device to (ckd - archives.toSet())) else checkedItems.value + (device to (ckd + archives.toSet())) }, modifier = Modifier.padding(end = 2.dp))
                                        Row(modifier = Modifier.clickable { expandedFolders.value = if (ae) expandedFolders.value - "$device/archive" else expandedFolders.value + "$device/archive" }) { Text(text = "${if (ae) "▼" else "▶"} 📁 国际服存档", style = MaterialTheme.typography.labelSmall, color = AppColors.TextPrimary) }
                                    }
                                    if (ae) { if (archives.isEmpty()) { Text("暂无存档", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive, modifier = Modifier.padding(start = 16.dp)) }
                                        else { archives.forEach { a -> Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                            Checkbox(checked = a in ckd, onCheckedChange = { c -> checkedItems.value = if (c) checkedItems.value + (device to (ckd + a)) else checkedItems.value + (device to (ckd - a)) }, modifier = Modifier.padding(end = 2.dp))
                                            Text("📦 $a", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive)
                                        } } }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Sync mode dropdown + option checkboxes ─────────────────────
            if (deviceList.value.isNotEmpty()) {
                val labels = listOf("全部", "配置文件", "国际服存档"); val sde = remember { mutableStateOf(false) }
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("同步:", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(end = 4.dp))
                    androidx.compose.material3.Button(onClick = { sde.value = true }, shape = RoundedCornerShape(8.dp), modifier = Modifier.height(30.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                        Text(labels[syncMode.value], style = MaterialTheme.typography.labelMedium); Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    if (sde.value) {
                        androidx.compose.ui.window.Popup(popupPositionProvider = com.coc.zkqcode.jar.ui.components.BelowAnchorPositionProvider(), onDismissRequest = { sde.value = false }, properties = androidx.compose.ui.window.PopupProperties(focusable = true)) {
                            androidx.compose.material3.Surface(shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp, modifier = Modifier.padding(4.dp)) {
                                Column(modifier = Modifier.padding(8.dp)) { labels.forEachIndexed { idx, label ->
                                    Box(modifier = Modifier.background(if (idx == syncMode.value) AppColors.Primary else AppColors.PrimaryLight.copy(alpha = 0.10f), RoundedCornerShape(8.dp)).border(1.dp, if (idx == syncMode.value) AppColors.Primary else AppColors.PrimaryLight, RoundedCornerShape(8.dp)).clickable {
                                        syncMode.value = idx; sde.value = false
                                        if (idx == 1) { val cur = checkedItems.value; val dl = cur.filter { it.value.isNotEmpty() }.keys.toList(); if (dl.size > 1) { checkedItems.value = mutableMapOf(dl.first() to (cur[dl.first()] ?: emptySet())) } }
                                    }.padding(horizontal = 16.dp, vertical = 8.dp)) { Text(label, color = if (idx == syncMode.value) Color.White else AppColors.TextPrimary, style = MaterialTheme.typography.labelMedium) }
                                } }
                            }
                        }
                    }
                }
                // Vertical checkboxes: 覆盖 and 增量 are mutually exclusive
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = overwriteSync.value, onCheckedChange = { overwriteSync.value = it; if (it) incrementalSync.value = false }, modifier = Modifier.padding(end = 2.dp))
                        Text(text = "覆盖同步", style = MaterialTheme.typography.labelSmall, color = AppColors.TextPrimary)
                    }
                    if (syncMode.value != 1) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = incrementalSync.value, onCheckedChange = { incrementalSync.value = it; if (it) overwriteSync.value = false }, modifier = Modifier.padding(end = 2.dp))
                            Text(text = "增量同步", style = MaterialTheme.typography.labelSmall, color = AppColors.TextPrimary)
                        }
                    }
                    Text(text = "覆盖: 直接覆盖已有文件 / 增量: 跳过已有序号并自动续接编号", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive, modifier = Modifier.padding(start = 40.dp, top = 2.dp, bottom = 4.dp))
                }
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    val sel = if (syncMode.value == 1) overwriteSync.value else (overwriteSync.value || incrementalSync.value)
                    val ue = checkedItems.value.isNotEmpty() && !isProcessing.value && sel
                    val de = checkedItems.value.any { it.value.isNotEmpty() } && !isProcessing.value && sel
                    CustomButton(text = if (isProcessing.value) "处理中..." else "一键上传", marginEnd = 4.dp, enable = ue, onClick = { performUpload() })
                    CustomButton(text = if (isProcessing.value) "处理中..." else "一键下载", marginEnd = 4.dp, enable = de, onClick = { performDownload() })
                }
            }
        }
    }
    legalDisclaimers()
    item {
        val context = LocalContext.current; val cr = rememberCoroutineScope()
        CustomButton(text = Strings.copyLogs, onClick = {
            cr.launch(Dispatchers.IO) {
                val src = "${context.filesDir.absolutePath}/logs"; val sd = Environment.getExternalStorageDirectory().path; val dst = "$sd/zkqFiles"
                ShellHelper.runBlockingNoOutput("mkdir -p \"$dst\" && cp -rf \"$src\" \"$dst/\"")
            }
        })
    }
}
