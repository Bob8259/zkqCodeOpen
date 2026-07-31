package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.archive.ClipboardItem
import com.coc.zkqcode.jar.code.universal.archive.ConflictAction
import com.coc.zkqcode.jar.code.universal.archive.LocationType
import com.coc.zkqcode.jar.code.universal.archive.RemoteArchive
import com.coc.zkqcode.jar.code.universal.archive.WebDavArchiveManager
import com.coc.zkqcode.jar.code.universal.network.WebDavResult
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import android.content.ClipData
import android.content.ClipboardManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ── Navigation pages ──────────────────────────────────────────────────────────

private sealed class Page {
    data object Root : Page()
    data object Local : Page()
    data object Remote : Page()
    data class Device(val name: String) : Page()
}

// State holder that survives popup open/close
private var _savedManager: WebDavArchiveManager? = null

// ── Main composable ───────────────────────────────────────────────────────────

@Composable
fun WebDavFileManager(onClose: (() -> Unit)? = null) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val manager = remember { _savedManager ?: WebDavArchiveManager(context).also { _savedManager = it } }

    var history by remember { mutableStateOf(listOf<Page>(Page.Root)) }
    val page = history.last()
    val isRoot = page is Page.Root
    fun push(p: Page) { history = history + p }
    fun pop() { if (history.size > 1) history = history.dropLast(1) }

    var localNums by remember { mutableStateOf(listOf<Int>()) }
    var remoteDevices by remember { mutableStateOf(listOf<String>()) }
    var remoteArchives by remember { mutableStateOf<Map<String, List<RemoteArchive>>>(emptyMap()) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var busy by remember { mutableStateOf(false) }

    var configOpen by remember { mutableStateOf(false) }
    var pasteDialogVisible by remember { mutableStateOf(false) }
    var pasteText by remember { mutableStateOf("") }
    var clipInfo by remember { mutableStateOf<String?>(null) }
    var pasteProgress by remember { mutableStateOf("") }
    var pasteChoiceDialogVisible by remember { mutableStateOf(false) }
    var pendingPasteFn by remember { mutableStateOf<((ConflictAction) -> Unit)?>(null) }

    // ── Rename state ──────────────────────────────────────────────────────────
    var editingDevice by remember { mutableStateOf<String?>(null) }
    var renameDraft by remember { mutableStateOf("") }

    // ── Create folder state ───────────────────────────────────────────────────
    var showNewFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    fun getCfg(k: String) = GlobalVars.configStates[k]?.value.orEmpty()
    fun hasClip() = manager.clipboard != null

    LaunchedEffect(Unit) { localNums = manager.listLocalArchives() }

    fun sizeLabel(bytes: Long) = when {
        bytes > 1_000_000 -> "${bytes / 1_000_000} MB"
        bytes > 1_000 -> "${bytes / 1_000} KB"
        else -> "$bytes B"
    }

    val months = mapOf("Jan" to "01","Feb" to "02","Mar" to "03","Apr" to "04","May" to "05","Jun" to "06",
        "Jul" to "07","Aug" to "08","Sep" to "09","Oct" to "10","Nov" to "11","Dec" to "12")
    fun fmtDate(httpDate: String): String {
        if (httpDate.isBlank()) return ""
        val p = httpDate.split(" ")
        if (p.size < 4) return httpDate.take(10)
        val day = p[1].padStart(2, '0')
        val mon = months[p[2]] ?: "??"
        val year = p[3]
        return "$year-$mon-$day"
    }

    /** 格式化本地目录的修改时间（epoch 秒）为 yyyy-MM-dd */
    fun fmtEpoch(epochSeconds: Long): String {
        if (epochSeconds <= 0) return ""
        return try {
            java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date(epochSeconds * 1000))
        } catch (_: Exception) { "" }
    }

    fun toggleSel(name: String) { selected = if (name in selected) selected - name else selected + name }
    fun selAll(items: Collection<String>) { selected = if (selected.size == items.size) emptySet() else items.toSet() }

    fun doPaste(target: LocationType, targetName: String, action: ConflictAction, after: suspend (suc: Int, fail: Int) -> Unit) {
        scope.launch {
            busy = true
            pasteProgress = ""
            val r = withContext(Dispatchers.IO) {
                manager.paste(target, targetName,
                    conflictAction = action,
                    onProgress = { msg -> scope.launch { pasteProgress = msg } }
                )
            }
            manager.clipboard = null
            clipInfo = null
            pasteProgress = ""
            after(r.succeeded.size, r.failed.size)
            busy = false
        }
    }

    val fullTitle = buildString {
        append("📁  ")
        when (page) {
            is Page.Root -> append("WebDAV 文件管理")
            is Page.Local -> append("本地存储")
            is Page.Remote -> append("远程服务器")
            is Page.Device -> append("远程服务器 / ${page.name}")
        }
    }

    Box(Modifier.fillMaxSize()) {
        Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
            .border(1.5.dp, AppColors.GlassBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.CardBg,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column(Modifier.fillMaxWidth().fillMaxHeight()) {
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp).padding(horizontal = 10.dp).border(1.dp, AppColors.Border, RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (!isRoot) CustomButton(text = "←", marginEnd = 4.dp, onClick = { pop(); selected = emptySet() })
                Text(fullTitle, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                if (isRoot) CustomButton(text = "⚙", onClick = { configOpen = true })
                if (onClose != null) CustomButton(text = "✕", marginEnd = 0.dp, onClick = onClose)
            }

            Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 10.dp)) {
                if (busy && pasteProgress.isNotBlank()) {
                    Text(pasteProgress, style = MaterialTheme.typography.bodySmall, color = AppColors.Primary, modifier = Modifier.padding(vertical = 4.dp))
                }

                // ── Root page ─────────────────────────────────────────────────
                if (isRoot) {
                    Text(Strings.webdavSectionDesc, style = MaterialTheme.typography.bodySmall, color = AppColors.TextPrimary.copy(alpha = 0.7f), modifier = Modifier.padding(bottom = 6.dp))
                    EntryCard("📁  本地存储") { push(Page.Local) }
                    EntryCard("📂  远程服务器") {
                        push(Page.Remote)
                        scope.launch {
                            busy = true
                            if (manager.client == null) {
                                val url = getCfg(GLOBAL_SETTINGS.WEBDAV_URL.key).trim()
                                val u = getCfg(GLOBAL_SETTINGS.WEBDAV_USERNAME.key).trim()
                                val p = getCfg(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                                if (url.isNotBlank() && u.isNotBlank() && p.isNotBlank()) {
                                    withContext(Dispatchers.IO) { manager.connect(url, u, p) }
                                }
                            }
                            val r = withContext(Dispatchers.IO) { manager.listRemoteDevices() }
                            if (r is WebDavResult.Success) remoteDevices = r.data
                            busy = false
                        }
                    }
                    clipInfo?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = AppColors.Primary, modifier = Modifier.padding(top = 4.dp)) }
                }

                // ── Local page (2-column grid) ─────────────────────────────
                if (page is Page.Local) {
                    localNums.forEach { n ->
                        Box(Modifier.fillMaxWidth().padding(2.dp)
                            .border(1.dp, AppColors.Border, RoundedCornerShape(8.dp)).clickable {}.padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = n.toString() in selected, onCheckedChange = { toggleSel(n.toString()) })
                                Text("$n", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(sizeLabel(manager.getLocalArchiveSize(n)), style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive)
                                    Text(fmtEpoch(manager.getLocalArchiveModified(n)), style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive)
                                }
                            }
                        }
                    }
                    if (localNums.isEmpty()) Text("(空)", style = MaterialTheme.typography.bodySmall, color = AppColors.NavInactive)
                }

                // ── Remote devices page ────────────────────────────────────
                if (page is Page.Remote) {
                    remoteDevices.forEach { d ->
                        val isEditing = editingDevice == d
                        Box(Modifier.fillMaxWidth().padding(2.dp)
                            .border(1.dp, AppColors.Border, RoundedCornerShape(8.dp))
                            .clickable(enabled = !isEditing && editingDevice == null) {
                                push(Page.Device(d))
                                scope.launch { busy = true; val r = withContext(Dispatchers.IO) { manager.listRemoteArchives(d) }; if (r is WebDavResult.Success) remoteArchives = mapOf(d to r.data); busy = false }
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)) {
                                    if (isEditing) {
                                        // ── 内联编辑模式 ──
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            BasicTextField(
                                                value = renameDraft,
                                                onValueChange = { renameDraft = it },
                                                modifier = Modifier.weight(1f).background(Color.White, RoundedCornerShape(4.dp))
                                                    .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                            CustomButton(text = "✓", marginEnd = 2.dp, onClick = {
                                                val newName = renameDraft.trim()
                                                if (newName.isNotBlank() && newName != d) {
                                                    scope.launch {
                                                        val error = withContext(Dispatchers.IO) {
                                                            manager.renameDeviceFolder(d, newName)
                                                        }
                                                        if (error.isSuccess) {
                                                            remoteDevices = remoteDevices.map { if (it == d) newName else it }
                                                            ShowMessage("已重命名为 $newName", isChecking = false)
                                                        } else {
                                                            ShowMessage("重命名失败: ${error.errorMessage}", isChecking = false)
                                                        }
                                                        editingDevice = null; renameDraft = ""
                                                    }
                                                } else {
                                                    editingDevice = null; renameDraft = ""
                                                }
                                            })
                                            CustomButton(text = "✕", marginEnd = 0.dp, onClick = { editingDevice = null; renameDraft = "" })
                                        }
                                    } else {
                                        // ── 普通查看模式 ──
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = d in selected, onCheckedChange = { toggleSel(d) })
                                            Text(d, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                                            if (editingDevice == null) {
                                                CustomButton(text = "✏️", marginEnd = 2.dp, onClick = { editingDevice = d; renameDraft = d })
                                            }
                                            CustomButton(text = "🗑", marginEnd = 0.dp, onClick = {
                                                scope.launch {
                                                    val error = withContext(Dispatchers.IO) { manager.deleteDeviceFolder(d) }
                                                    if (error.isSuccess) {
                                                        remoteDevices = remoteDevices - d
                                                        ShowMessage("已删除 $d", isChecking = false)
                                                    } else {
                                                        ShowMessage("删除失败: ${error.errorMessage}", isChecking = false)
                                                    }
                                                }
                                            })
                                        }
                                    }
                                }
                            }
                    if (remoteDevices.isEmpty()) Text("(无)", style = MaterialTheme.typography.bodySmall, color = AppColors.NavInactive)
                }
                // ── Device archives page (2-column grid) ────────────────────
                if (page is Page.Device) {
                    val archs = remoteArchives[page.name].orEmpty()
                    archs.forEach { a ->
                        Box(Modifier.fillMaxWidth().padding(2.dp)
                            .border(1.dp, AppColors.Border.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 6.dp)) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = a.name in selected, onCheckedChange = { toggleSel(a.name) })
                                    Text(a.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                                }
                                Text("${fmtDate(a.lastModified)}  ${if (a.size > 0) sizeLabel(a.size) else if (!a.isZip) "旧格式" else ""}", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                    if (archs.isEmpty()) Text("(空)", style = MaterialTheme.typography.bodySmall, color = AppColors.NavInactive)
                }
            }

            // ── Bottom action bar (fixed) ─────────────────────────────
            if (!isRoot) {
                Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 32.dp)) {
                    if (page is Page.Local) {
                        Row(Modifier.fillMaxWidth()) {
                            CustomButton(text = "全选", marginEnd = 2.dp, onClick = { selAll(localNums.map { it.toString() }) })
                            CustomButton(text = "复制", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = {
                                manager.copyToClipboard(selected.map { ClipboardItem(it, true, LocationType.LOCAL, "local") }, false)
                                clipInfo = "已复制 ${selected.size} 个"
                            })
                            CustomButton(text = "剪切", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = {
                                manager.copyToClipboard(selected.map { ClipboardItem(it, true, LocationType.LOCAL, "local") }, true)
                                clipInfo = "已剪切 ${selected.size} 个"
                            })
                            if (hasClip()) {
                                CustomButton(text = "📋 粘贴", marginEnd = 2.dp, onClick = {
                                    pendingPasteFn = { action ->
                                        doPaste(LocationType.LOCAL, "local", action) { ok, fail ->
                                            localNums = manager.listLocalArchives()
                                            ShowMessage("$ok 成功, $fail 失败", isChecking = false)
                                        }
                                    }
                                    pasteChoiceDialogVisible = true
                                })
                            }
                            CustomButton(text = "删除", marginEnd = 0.dp, enable = selected.isNotEmpty(), onClick = {
                                scope.launch {
                                    selected.forEach { n ->
                                        manager.deleteLocalArchive(n.toInt())
                                    }
                                    localNums = manager.listLocalArchives()
                                    selected = emptySet()
                                }
                            })
                        }
                    }
                    if (page is Page.Remote) {
                        Row(Modifier.fillMaxWidth()) {
                            CustomButton(text = "全选", marginEnd = 2.dp, onClick = { selAll(remoteDevices) })
                            CustomButton(text = "复制", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = {
                                manager.copyToClipboard(selected.map { ClipboardItem(it, true, LocationType.REMOTE, it) }, false)
                                clipInfo = "已复制 ${selected.size} 个设备"
                            })
                            CustomButton(text = "剪切", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = {
                                manager.copyToClipboard(selected.map { ClipboardItem(it, true, LocationType.REMOTE, it) }, true)
                                clipInfo = "已剪切 ${selected.size} 个设备"
                            })
                            if (hasClip()) {
                                CustomButton(text = "📋 粘贴", marginEnd = 2.dp, enable = selected.firstOrNull() != null, onClick = {
                                    pendingPasteFn = { action ->
                                        doPaste(LocationType.REMOTE, selected.first()!!, action) { ok, fail ->
                                            scope.launch { busy = true; val r = withContext(Dispatchers.IO) { manager.listRemoteDevices() }; if (r is WebDavResult.Success) remoteDevices = r.data; busy = false }
                                            ShowMessage("$ok 成功, $fail 失败", isChecking = false)
                                        }
                                    }
                                    pasteChoiceDialogVisible = true
                                })
                            }
                            CustomButton(text = "新建", marginEnd = 0.dp, onClick = { showNewFolderDialog = true; newFolderName = "" })
                        }
                    }
                    if (page is Page.Device) {
                        val archs = remoteArchives[page.name].orEmpty()
                        Row(Modifier.fillMaxWidth()) {
                            CustomButton(text = "全选", marginEnd = 2.dp, onClick = { selAll(archs.map { it.name }) })
                            CustomButton(text = "复制", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = {
                                manager.copyToClipboard(selected.map { ClipboardItem(it, false, LocationType.REMOTE, page.name) }, false)
                                clipInfo = "已复制 ${selected.size} 个"
                            })
                            CustomButton(text = "剪切", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = {
                                manager.copyToClipboard(selected.map { ClipboardItem(it, false, LocationType.REMOTE, page.name) }, true)
                                clipInfo = "已剪切 ${selected.size} 个"
                            })
                            if (hasClip()) {
                                CustomButton(text = "📋 粘贴", marginEnd = 2.dp, onClick = {
                                    pendingPasteFn = { action ->
                                        doPaste(LocationType.REMOTE, page.name, action) { ok, fail ->
                                            scope.launch { busy = true; val r = withContext(Dispatchers.IO) { manager.listRemoteArchives(page.name) }; if (r is WebDavResult.Success) remoteArchives = mapOf(page.name to r.data); busy = false }
                                            ShowMessage("$ok 成功, $fail 失败", isChecking = false)
                                        }
                                    }
                                    pasteChoiceDialogVisible = true
                                })
                            }
                            CustomButton(text = "删除", marginEnd = 0.dp, enable = selected.isNotEmpty(), onClick = {
                                scope.launch {
                                    selected.forEach { a ->
                                        manager.deleteRemoteArchive(page.name, a)
                                    }
                                    val r = withContext(Dispatchers.IO) { manager.listRemoteArchives(page.name) }
                                    if (r is WebDavResult.Success) remoteArchives = mapOf(page.name to r.data)
                                    selected = emptySet()
                                }
                            })
                        }
                    }

            }
        }
    }
}
    }

    // ── Paste choice dialog ────────────────────────────────────────────────
    if (pasteChoiceDialogVisible) {
        Popup(onDismissRequest = { pasteChoiceDialogVisible = false; pendingPasteFn = null }, properties = PopupProperties(focusable = true), alignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { pasteChoiceDialogVisible = false; pendingPasteFn = null }, contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp) {
                    Column(Modifier.padding(16.dp).widthIn(min = 200.dp)) {
                        Text("选择粘贴方式", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
                        CustomButton(text = "覆盖粘贴", marginEnd = 0.dp, onClick = {
                            pendingPasteFn?.invoke(ConflictAction.OVERWRITE)
                            pasteChoiceDialogVisible = false; pendingPasteFn = null
                        })
                        Spacer(Modifier.height(8.dp))
                        CustomButton(text = "增量粘贴", marginEnd = 0.dp, onClick = {
                            pendingPasteFn?.invoke(ConflictAction.INCREMENT)
                            pasteChoiceDialogVisible = false; pendingPasteFn = null
                        })
                        Spacer(Modifier.height(12.dp))
                        CustomButton(text = "取消", marginEnd = 0.dp, onClick = { pasteChoiceDialogVisible = false; pendingPasteFn = null })
                    }
                }
            }
        }
    }

    // ── Create folder dialog ─────────────────────────────────────────────────
    if (showNewFolderDialog) {
        Popup(onDismissRequest = { showNewFolderDialog = false }, properties = PopupProperties(focusable = true), alignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { showNewFolderDialog = false }, contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp) {
                    Column(Modifier.padding(16.dp).widthIn(min = 240.dp)) {
                        Text("新建远程文件夹", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary, modifier = Modifier.padding(bottom = 8.dp))
                        BasicTextField(
                            value = newFolderName,
                            onValueChange = { newFolderName = it },
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(4.dp))
                                .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row {
                            CustomButton(text = "取消", marginEnd = 8.dp, onClick = { showNewFolderDialog = false })
                            CustomButton(text = "确定", onClick = {
                                val name = newFolderName.trim()
                                if (name.isNotBlank()) {
                                    scope.launch {
                                        showNewFolderDialog = false
                                        val error = withContext(Dispatchers.IO) { manager.createDeviceFolder(name) }
                                        if (error.isSuccess) {
                                            remoteDevices = (remoteDevices + name).sorted()
                                            ShowMessage("已创建 $name", isChecking = false)
                                        } else {
                                            ShowMessage("创建失败: ${error.errorMessage}", isChecking = false)
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    // ── Config popup ──────────────────────────────────────────────────────
    if (configOpen) {
        Popup(onDismissRequest = { configOpen = false }, properties = PopupProperties(focusable = true), alignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { configOpen = false }, contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp) {
                    Column(Modifier.padding(16.dp).widthIn(min = 280.dp)) {
                        Text("⚙  WebDAV 配置", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary, modifier = Modifier.padding(bottom = 8.dp))
                        SettingInputRow(key = GLOBAL_SETTINGS.WEBDAV_URL.key)
                        SettingInputRow(key = GLOBAL_SETTINGS.WEBDAV_USERNAME.key)
                        SettingInputRow(key = GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                        Row(Modifier.padding(top = 4.dp)) {
                            CustomButton(text = "📋 复制", marginEnd = 4.dp, onClick = {
                                val s = listOf(getCfg(GLOBAL_SETTINGS.WEBDAV_URL.key), getCfg(GLOBAL_SETTINGS.WEBDAV_USERNAME.key), getCfg(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)).joinToString("|||")
                                context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(ClipData.newPlainText("WebDAV", s)); ShowMessage("已复制", isChecking = false)
                            })
                            CustomButton(text = "📋 粘贴", onClick = { pasteDialogVisible = true })
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            CustomButton(text = "连接", marginEnd = 4.dp, onClick = {
                                val url = getCfg(GLOBAL_SETTINGS.WEBDAV_URL.key).trim(); val u = getCfg(GLOBAL_SETTINGS.WEBDAV_USERNAME.key).trim(); val p = getCfg(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                                if (url.isBlank() || u.isBlank() || p.isBlank()) { ShowMessage("请填完整", isChecking = false); return@CustomButton }
                                scope.launch { busy = true; val r = withContext(Dispatchers.IO) { manager.connect(url, u, p) }; ShowMessage(if (r.isSuccess) "✅ 连接成功" else "❌ ${r.errorMessage}", isChecking = false); busy = false }
                            })
                            CustomButton(text = "保存", onClick = { configOpen = false })
                        }
                        if (manager.client != null) {
                            Text("✅ 已连接", style = MaterialTheme.typography.labelSmall, color = AppColors.Primary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                            CustomButton(text = "断开", onClick = { manager.disconnect() })
                        }
                    }
                }
            }
        }
    }

    // ── Paste dialog ─────────────────────────────────────────────────────────
    if (pasteDialogVisible) {
        Popup(onDismissRequest = { pasteDialogVisible = false; pasteText = "" }, properties = PopupProperties(focusable = true), alignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { pasteDialogVisible = false; pasteText = "" }, contentAlignment = Alignment.Center) {
                Surface(shape = RoundedCornerShape(12.dp), color = AppColors.CardBg, shadowElevation = 8.dp) {
                    Column(Modifier.padding(16.dp).widthIn(min = 240.dp)) {
                        Text("粘贴 WebDAV 信息", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 8.dp))
                        BasicTextField(value = pasteText, onValueChange = { pasteText = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp).background(Color.White, RoundedCornerShape(4.dp)).border(1.dp, AppColors.Border, RoundedCornerShape(4.dp)).padding(8.dp))
                        Row(Modifier.padding(top = 8.dp)) {
                            CustomButton(text = "取消", marginEnd = 8.dp, onClick = { pasteDialogVisible = false; pasteText = "" })
                            CustomButton(text = "确定", onClick = {
                                val p = pasteText.split("|||")
                                if (p.size >= 3) {
                                    GlobalVars.configStates[GLOBAL_SETTINGS.WEBDAV_URL.key]?.value = p[0].trim()
                                    GlobalVars.configStates[GLOBAL_SETTINGS.WEBDAV_USERNAME.key]?.value = p[1].trim()
                                    GlobalVars.configStates[GLOBAL_SETTINGS.WEBDAV_PASSWORD.key]?.value = p.drop(2).joinToString("|||").trim()
                                    ShowMessage("已填入", isChecking = false); pasteDialogVisible = false; pasteText = ""
                                } else ShowMessage("格式错误", isChecking = false)
                            })
                        }
                    }
                }
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
private fun EntryCard(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onClick)
        .border(1.dp, if (enabled) AppColors.Border else AppColors.Border.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (enabled) AppColors.TextPrimary else AppColors.NavInactive)
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun FileOpsBar(
    allItems: List<String>,
    selected: Set<String>,
    onSelectAll: (Collection<String>) -> Unit,
    onToggle: (String) -> Unit,
    onClipboard: (isCut: Boolean) -> Unit
) {
    Spacer(Modifier.height(4.dp))
    Row(Modifier.fillMaxWidth()) {
        CustomButton(text = "全选", marginEnd = 2.dp, onClick = { onSelectAll(allItems) })
        CustomButton(text = "复制", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = { onClipboard(false) })
        CustomButton(text = "剪切", marginEnd = 2.dp, enable = selected.isNotEmpty(), onClick = { onClipboard(true) })
    }
}

