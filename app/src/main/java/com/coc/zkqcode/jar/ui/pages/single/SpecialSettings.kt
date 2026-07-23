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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.code.universal.push.PushMessage
import com.coc.zkqcode.jar.code.universal.push.PushRouter
import com.coc.zkqcode.jar.ui.LanguageManager
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.components.CustomButton
import com.coc.zkqcode.jar.ui.components.SettingDropdown
import com.coc.zkqcode.jar.ui.components.SettingInputRow
import com.coc.zkqcode.jar.ui.components.SettingSection
import com.coc.zkqcode.jar.ui.components.SettingSwitchIcon
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS
import com.coc.zkqcode.jar.ui.theme.AppColors
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun LazyListScope.specialSettings() {
    item { SettingSection { BatchCreateAccount() } }
    item { SettingSection { RandomScheduling() } }
    item {
        SettingSection {
            val parentState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.UPDATE_CN_GAME.key) { mutableStateOf("0") }
            val parentEnabled = parentState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.UPDATE_CN_GAME.key, afterChange = { checked ->
                if (!checked) {
                    GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key) { mutableStateOf("0") }.value = "0"
                }
            })
            if (parentEnabled) {
                SettingInputRow(key = GLOBAL_SETTINGS.MAX_DOWNLOAD_WAITING_TIME.key)

                val enhancedState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key) { mutableStateOf("0") }
                val enhancedEnabled = enhancedState.value == "1"
                SettingSwitchIcon(key = GLOBAL_SETTINGS.ENHANCED_CN_UPDATE.key)
                if (enhancedEnabled) {
                    Text(
                        text = Strings.enhancedUpdateHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextPrimary.copy(alpha = 0.75f),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                    SettingDropdown(
                        key = GLOBAL_SETTINGS.ENHANCED_CN_UPDATE_SOURCE.key,
                        options = listOf("无", Strings.enhancedSourceAppStore, Strings.enhancedSourceTencent)
                    )
                }
            }
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
            val warNotifyOn = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY.key) { mutableStateOf("0") }.value == "1"
            if (warNotifyOn) {
                SettingDropdown(
                    key = GLOBAL_SETTINGS.SERVERCHAN_WAR_NOTIFY_MODE.key,
                    options = listOf(Strings.notifyModeManual, Strings.notifyModeAuto)
                )
            }
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY.key)
            val leagueNotifyOn = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY.key) { mutableStateOf("0") }.value == "1"
            if (leagueNotifyOn) {
                SettingDropdown(
                    key = GLOBAL_SETTINGS.SERVERCHAN_LEAGUE_NOTIFY_MODE.key,
                    options = listOf(Strings.notifyModeManual, Strings.notifyModeAuto)
                )
            }
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_NOTIFY.key)
            val scheduleState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key) { mutableStateOf("0") }
            val scheduleEnabled = scheduleState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_SCHEDULE_NOTIFY.key)
            if (scheduleEnabled) { SettingInputRow(key = GLOBAL_SETTINGS.SERVERCHAN_UPGRADE_ADVANCE.key) }
            val cloudState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.CLOUD_EXPIRE_NOTIFY.key) { mutableStateOf("0") }
            val cloudEnabled = cloudState.value == "1"
            SettingSwitchIcon(key = GLOBAL_SETTINGS.CLOUD_EXPIRE_NOTIFY.key)
            if (cloudEnabled) {
                val dateState = GlobalVars.configStates.getOrPut(GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key) { mutableStateOf("") }
                val parts = dateState.value.split("-")
                val yearSel = remember { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 0) }
                val monthSel = remember { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }
                val daySel = remember { mutableStateOf(parts.getOrNull(2)?.toIntOrNull() ?: 0) }
                fun writeDate() {
                    if (yearSel.value > 0 && monthSel.value > 0 && daySel.value > 0)
                        dateState.value = "${yearSel.value}-${monthSel.value.toString().padStart(2,'0')}-${daySel.value.toString().padStart(2,'0')}"
                }
                Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = Schema.getDisplayName(GLOBAL_SETTINGS.CLOUD_EXPIRE_DATE.key) + "：", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 4.dp))
                    SimpleDropdown(sel = if (yearSel.value > 0) "${yearSel.value}" else "-", opts = (2025..2040).map { it.toString() }, onSel = { yearSel.value = it.toIntOrNull() ?: yearSel.value; writeDate() })
                    Text(text = "年", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 2.dp))
                    SimpleDropdown(sel = if (monthSel.value > 0) "${monthSel.value}" else "-", opts = (1..12).map { it.toString() }, onSel = { monthSel.value = it.toIntOrNull() ?: monthSel.value; writeDate() })
                    Text(text = "月", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 2.dp))
                    SimpleDropdown(sel = if (daySel.value > 0) "${daySel.value}" else "-", opts = (1..31).map { it.toString() }, onSel = { daySel.value = it.toIntOrNull() ?: daySel.value; writeDate() })
                    Text(text = "日", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 2.dp))
                }
                SettingInputRow(key = GLOBAL_SETTINGS.CLOUD_EXPIRE_RESET_DAYS.key)
                SettingInputRow(key = GLOBAL_SETTINGS.CLOUD_EXPIRE_ADVANCE_DAYS.key)
            }
            val scope = rememberCoroutineScope()
            CustomButton(
                text = Strings.serverchanTestBtn,
                marginTop = 6.dp,
                onClick = {
                    scope.launch {
                        ShowMessage(Strings.serverchanTestSending, isChecking = false)
                        val error = withContext(Dispatchers.IO) {
                            PushRouter.send(
                                PushMessage(
                                    title = Strings.serverchanTestBtn,
                                    desp = if (LanguageManager.isChinese) "这是一条测试通知" else "This is a test notification"
                                )
                            )
                        }
                        if (error == null) {
                            ShowMessage(Strings.serverchanTestSuccess, isChecking = false)
                        } else {
                            ShowMessage(Strings.serverchanTestFailed(error), isChecking = false)
                        }
                    }
                }
            )
        }
    }
    // ── Cloud Expiry Notification is now inside Push Notification section above
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

            // ── WebDAV one-click copy / paste credentials ─────────────────────
            val pasteDialogVisible = remember { mutableStateOf(false) }
            val pasteInput = remember { mutableStateOf("") }

            Row(modifier = Modifier.padding(top = 4.dp)) {
                CustomButton(text = Strings.webdavCopyCredentials, marginEnd = 4.dp, onClick = {
                    val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key)
                    val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key)
                    val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                    val combined = "$url|||$username|||$password"
                    context.getSystemService(ClipboardManager::class.java)?.let { cm ->
                        cm.setPrimaryClip(ClipData.newPlainText("WebDAV", combined))
                        ShowMessage(Strings.webdavCopiedMsg, isChecking = false)
                    }
                })
                CustomButton(text = Strings.webdavPasteCredentials, onClick = { pasteDialogVisible.value = true })
            }

            if (pasteDialogVisible.value) {
                Popup(
                    onDismissRequest = { pasteDialogVisible.value = false; pasteInput.value = "" },
                    properties = PopupProperties(focusable = true)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AppColors.CardBg,
                        shadowElevation = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp).widthIn(min = 240.dp)) {
                            Text(
                                Strings.webdavPasteDialogTitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = AppColors.TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            BasicTextField(
                                value = pasteInput.value,
                                onValueChange = { pasteInput.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 40.dp)
                                    .background(Color.White, RoundedCornerShape(4.dp))
                                    .border(1.dp, AppColors.Border, RoundedCornerShape(4.dp))
                                    .padding(8.dp),
                                singleLine = false
                            )
                            Row(modifier = Modifier.padding(top = 12.dp)) {
                                CustomButton(text = Strings.webdavPasteCancel, marginEnd = 8.dp, onClick = {
                                    pasteDialogVisible.value = false; pasteInput.value = ""
                                })
                                CustomButton(text = Strings.webdavPasteConfirm, onClick = {
                                    val parts = pasteInput.value.split("|||")
                                    if (parts.size >= 3) {
                                        GlobalVars.configStates[GLOBAL_SETTINGS.WEBDAV_URL.key]?.value = parts[0].trim()
                                        GlobalVars.configStates[GLOBAL_SETTINGS.WEBDAV_USERNAME.key]?.value = parts[1].trim()
                                        GlobalVars.configStates[GLOBAL_SETTINGS.WEBDAV_PASSWORD.key]?.value = parts.subList(2, parts.size).joinToString("|||").trim()
                                        ShowMessage(Strings.webdavPastedMsg, isChecking = false)
                                        pasteDialogVisible.value = false; pasteInput.value = ""
                                    } else {
                                        ShowMessage(Strings.webdavPasteInvalidMsg, isChecking = false)
                                    }
                                })
                            }
                        }
                    }
                }
            }

            val folderList = remember { mutableStateOf(listOf<String>()) }
            val expandedFolders = remember { mutableStateOf(setOf<String>()) }
            val localSaveDirPath = "$sdPath/zkqFiles/zkqGlobalGameSave"
            val localHasSaves = remember { mutableStateOf(File(localSaveDirPath).isDirectory() && File(localSaveDirPath).listFiles()?.any { it.isDirectory } == true) }
            val folderArchiveMap = remember { mutableStateOf(mapOf<String, List<String>>()) }
            val folderRenameDrafts = remember { mutableStateOf(mapOf<String, String>()) }
            val folderEditingName = remember { mutableStateOf(setOf<String>()) }
            data class FolderSelection(
                val selectAll: Boolean = false,
                val selectedArchives: Set<String> = emptySet()
            )
            val folderSelections = remember { mutableStateOf(mapOf<String, FolderSelection>()) }
            val isProcessing = remember { mutableStateOf(false) }
            val syncMode = remember { mutableStateOf(0) } // 0=none, 1=overwrite, 2=incremental
            val syncExpanded = remember { mutableStateOf(false) }
            var refreshFolderList: (() -> Unit)? = null

            fun getSelectedTargets(): List<Pair<String, Set<String>>> {
                return folderSelections.value.mapNotNull { (folder, selection) ->
                    val archives = folderArchiveMap.value[folder].orEmpty()
                    val selected = if (selection.selectAll) archives.toSet() else selection.selectedArchives.intersect(archives.toSet())
                    if (selected.isEmpty() && archives.isNotEmpty()) null else folder to selected
                }.sortedBy { it.first }
            }

            fun parseArchiveNumber(name: String): Int? {
                val raw = when {
                    name.endsWith(".tar.gz") -> name.removeSuffix(".tar.gz")
                    name.endsWith(".zip") -> name.removeSuffix(".zip")
                    else -> return null
                }
                return raw.trim().toIntOrNull()
            }

            fun listLocalArchiveNumbers(localSaveDir: File): List<Int> {
                return localSaveDir.listFiles()
                    ?.filter { it.isDirectory }
                    ?.mapNotNull { it.name.trim().toIntOrNull() }
                    ?.sorted()
                    .orEmpty()
            }

            fun zipDirectory(sourceDir: File, targetZip: File) {
                targetZip.parentFile?.mkdirs()
                ZipOutputStream(FileOutputStream(targetZip)).use { zip ->
                    sourceDir.walkTopDown().forEach { file ->
                        val entryName = sourceDir.parentFile?.toPath()?.relativize(file.toPath())?.toString()?.replace('\\', '/') ?: return@forEach
                        if (file.isDirectory) {
                            val dirName = if (entryName.endsWith('/')) entryName else "$entryName/"
                            zip.putNextEntry(ZipEntry(dirName))
                            zip.closeEntry()
                        } else {
                            zip.putNextEntry(ZipEntry(entryName))
                            file.inputStream().use { input -> input.copyTo(zip) }
                            zip.closeEntry()
                        }
                    }
                }
            }

            fun unzipToDirectory(zipFile: File, destinationDir: File): Boolean {
                return try {
                    destinationDir.mkdirs()
                    ZipInputStream(zipFile.inputStream().buffered()).use { zip ->
                        var entry = zip.nextEntry
                        while (entry != null) {
                            val outFile = File(destinationDir, entry.name)
                            if (entry.isDirectory) {
                                outFile.mkdirs()
                            } else {
                                outFile.parentFile?.mkdirs()
                                outFile.outputStream().use { out -> zip.copyTo(out) }
                            }
                            zip.closeEntry()
                            entry = zip.nextEntry
                        }
                    }
                    true
                } catch (_: Exception) {
                    false
                }
            }

            fun extractLegacyTarGzToPrivateStage(archiveFile: File, destinationDir: File): Boolean {
                destinationDir.mkdirs()
                val command = "tar -xzf \"${archiveFile.absolutePath}\" -C \"${destinationDir.absolutePath}\""
                return ShellHelper.runBlocking(command).isSuccess
            }

            fun copyDirectoryToTarget(sourceDir: File, targetDir: File): Boolean {
                return try {
                    if (targetDir.exists()) targetDir.deleteRecursively()
                    targetDir.parentFile?.mkdirs()
                    sourceDir.copyRecursively(targetDir, overwrite = true)
                    true
                } catch (_: Exception) {
                    false
                }
            }

            fun currentSelection(folder: String): FolderSelection {
                return folderSelections.value[folder] ?: FolderSelection()
            }

            fun isFolderChecked(folder: String, archives: List<String>): Boolean {
                val selection = currentSelection(folder)
                return selection.selectAll || (archives.isNotEmpty() && archives.all { it in selection.selectedArchives })
            }

            fun isArchiveChecked(folder: String, archive: String): Boolean {
                val selection = currentSelection(folder)
                return selection.selectAll || archive in selection.selectedArchives
            }

            fun setFolderSelection(folder: String, selection: FolderSelection?) {
                folderSelections.value = if (selection == null) {
                    folderSelections.value - folder
                } else {
                    folderSelections.value + (folder to selection)
                }
            }

            fun startRename(folder: String) {
                folderRenameDrafts.value = folderRenameDrafts.value + (folder to folder)
                folderEditingName.value = folderEditingName.value + folder
            }

            fun cancelRename(folder: String) {
                folderRenameDrafts.value = folderRenameDrafts.value - folder
                folderEditingName.value = folderEditingName.value - folder
            }

            fun saveRename(folder: String) {
                val draft = folderRenameDrafts.value[folder]?.trim().orEmpty()
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                if (draft.isBlank()) { ShowMessage("请先输入文件夹名称", isChecking = false); return }
                if (draft == folder) {
                    cancelRename(folder)
                    return
                }
                scope.launch {
                    isProcessing.value = true
                    val error = withContext(Dispatchers.IO) {
                        com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.movePath(
                            url,
                            username,
                            password,
                            "zkqFiles/$folder",
                            "zkqFiles/$draft"
                        )
                    }
                    if (error == null) {
                        ShowMessage("文件夹重命名成功: $draft", isChecking = false)
                        cancelRename(folder)
                        refreshFolderList?.invoke()
                    } else {
                        ShowMessage("重命名失败: $error", isChecking = false)
                    }
                    isProcessing.value = false
                }
            }

            fun deleteFolder(folder: String) {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                scope.launch {
                    isProcessing.value = true
                    val error = withContext(Dispatchers.IO) {
                        com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.deletePath(
                            url,
                            username,
                            password,
                            "zkqFiles/$folder"
                        )
                    }
                    if (error == null) {
                        ShowMessage("文件夹删除成功: $folder", isChecking = false)
                        cancelRename(folder)
                        refreshFolderList?.invoke()
                    } else {
                        ShowMessage("删除失败: $error", isChecking = false)
                    }
                    isProcessing.value = false
                }
            }

            fun toggleFolder(folder: String, archives: List<String>, checked: Boolean) {
                setFolderSelection(
                    folder,
                    if (checked) FolderSelection(selectAll = true, selectedArchives = archives.toSet())
                    else null
                )
            }

            fun toggleArchive(folder: String, archive: String, checked: Boolean) {
                val current = currentSelection(folder)
                val next = current.copy(
                    selectAll = false,
                    selectedArchives = if (checked) current.selectedArchives + archive else current.selectedArchives - archive
                )
                setFolderSelection(folder, if (next.selectedArchives.isEmpty()) null else next)
            }

            refreshFolderList = {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                if (url.isBlank()) {
                    ShowMessage("请先填写 WebDAV 服务器信息", isChecking = false)
                } else {
                    scope.launch {
                        isProcessing.value = true; ShowMessage("正在扫描远程文件夹...", isChecking = false)
                        val r = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listDirectories(url, username, password, "zkqFiles") }
                        if (r is String) { ShowMessage(r, isChecking = false); isProcessing.value = false; return@launch }
                        @Suppress("UNCHECKED_CAST") val devices = (r as List<String>).sorted(); folderList.value = devices
                        folderRenameDrafts.value = folderRenameDrafts.value.filterKeys { it in devices }
                        val am = mutableMapOf<String, List<String>>()
                        for (d in devices) {
                            val fr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listFiles(url, username, password, "zkqFiles/$d/zkqGlobalGameSave") }
                            @Suppress("UNCHECKED_CAST")
                            am[d] = if (fr is List<*>) {
                                val frl = fr as List<String>
                                frl.filter { parseArchiveNumber(it) != null }.sortedBy { parseArchiveNumber(it) ?: Int.MAX_VALUE }
                            } else emptyList()
                        }
                        folderArchiveMap.value = am
                        localHasSaves.value = withContext(Dispatchers.IO) {
                            File(localSaveDirPath).let { it.isDirectory() && it.listFiles()?.any { f -> f.isDirectory } == true }
                        }
                        isProcessing.value = false; ShowMessage("找到 ${devices.size} 个文件夹", isChecking = false)
                    }
                }
            }

            fun performUpload() {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                val localSaveDir = File(localSaveDirPath)
                val stageRoot = File(context.filesDir, "webdav_stage/upload")
                val sourceStageRoot = File(stageRoot, "source")
                val archiveStageRoot = File(stageRoot, "archive")
                val targets = getSelectedTargets()
                folderSelections.value = emptyMap()
                scope.launch {
                    isProcessing.value = true; var suc = 0; var fail = 0
                    for ((device, selectedArchives) in targets) {
                        val dirs = withContext(Dispatchers.IO) { listLocalArchiveNumbers(localSaveDir) }
                        val inc = syncMode.value == 2
                        val startIdx = if (inc) {
                            val rf = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listFiles(url, username, password, "zkqFiles/$device/zkqGlobalGameSave") }
                            @Suppress("UNCHECKED_CAST")
                            if (rf is List<*>) {
                                val rfl = rf as List<String>
                                val nums = rfl.mapNotNull { parseArchiveNumber(it) }
                                (nums.maxOrNull() ?: 0) + 1
                            } else 1
                        } else 1
                        val upl = if (selectedArchives.isNotEmpty()) {
                            selectedArchives.mapNotNull { parseArchiveNumber(it) }.sorted()
                        } else {
                            dirs
                        }
                        var seq = startIdx
                        for (n in upl) {
                            val sn = if (inc) { val s = seq; seq++; s } else n; val ss = sn.toString()
                            val sourceDir = File(localSaveDir, n.toString())
                            if (!sourceDir.isDirectory) { fail++; continue }
                            ShowMessage("上传 $device / $ss...", isChecking = false)
                            val sourceStageDir = File(sourceStageRoot, ss)
                            val zipFile = File(archiveStageRoot, "$ss.zip")
                            val archive = withContext(Dispatchers.IO) {
                                if (sourceStageDir.exists()) sourceStageDir.deleteRecursively()
                                if (zipFile.exists()) zipFile.delete()
                                sourceDir.copyRecursively(sourceStageDir, overwrite = true)
                                archiveStageRoot.mkdirs()
                                zipDirectory(sourceStageDir, zipFile)
                                zipFile.takeIf { it.isFile }
                            }
                            if (archive == null) { fail++; continue }
                            val e = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.upload(url, username, password, "zkqFiles/$device/zkqGlobalGameSave/$ss.zip", archive) }
                            if (e == null) suc++ else fail++
                            withContext(Dispatchers.IO) {
                                if (zipFile.exists()) zipFile.delete()
                                if (sourceStageDir.exists()) sourceStageDir.deleteRecursively()
                            }
                        }
                    }
                    withContext(Dispatchers.IO) { if (stageRoot.exists()) stageRoot.deleteRecursively() }
                    ShowMessage(if (fail == 0) "一键上传完成！" else "一键上传完成 ($suc 成功, $fail 失败)", isChecking = false)
                    refreshFolderList(); isProcessing.value = false
                }
            }

            fun performDownload() {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key)
                val localSaveDir = File(localSaveDirPath)
                val stageRoot = File(context.filesDir, "webdav_stage/download")
                val archiveStageRoot = File(stageRoot, "archive")
                val extractedStageRoot = File(stageRoot, "extracted")
                val inc = syncMode.value == 2
                val targets = getSelectedTargets()
                folderSelections.value = emptyMap()
                scope.launch {
                    isProcessing.value = true; var suc = 0; var fail = 0
                    for ((device, items) in targets) {
                        for (itemId in items) {
                            val remoteNumber = parseArchiveNumber(itemId)
                            val rn = remoteNumber?.toString() ?: itemId.substringBeforeLast('.')
                            val ldn = if (inc && remoteNumber != null) {
                                val ld = withContext(Dispatchers.IO) { listLocalArchiveNumbers(localSaveDir) }
                                ((ld.maxOrNull() ?: 0) + 1).toString()
                            } else rn
                            ShowMessage("下载 $device / $ldn...", isChecking = false)
                            val dr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.download(url, username, password, "zkqFiles/$device/zkqGlobalGameSave/$itemId") }
                            if (dr !is ByteArray) { fail++; continue }
                            val archiveFile = File(archiveStageRoot, itemId)
                            val extractedStageDir = File(extractedStageRoot, "$device-$ldn")
                            val extractedTargetDir = File(extractedStageDir, ldn)
                            val extractedRemoteDir = File(extractedStageDir, rn)
                            val extracted = withContext(Dispatchers.IO) {
                                archiveStageRoot.mkdirs()
                                if (extractedStageDir.exists()) extractedStageDir.deleteRecursively()
                                if (archiveFile.exists()) archiveFile.delete()
                                archiveFile.writeBytes(dr)
                                if (itemId.endsWith(".zip")) {
                                    unzipToDirectory(archiveFile, extractedStageDir)
                                } else if (itemId.endsWith(".tar.gz")) {
                                    extractLegacyTarGzToPrivateStage(archiveFile, extractedStageDir)
                                } else {
                                    false
                                }
                            }
                            if (!extracted) {
                                fail++
                                withContext(Dispatchers.IO) {
                                    if (archiveFile.exists()) archiveFile.delete()
                                    if (extractedStageDir.exists()) extractedStageDir.deleteRecursively()
                                }
                                continue
                            }
                            val copied = withContext(Dispatchers.IO) {
                                val sourceDir = when {
                                    extractedTargetDir.isDirectory -> extractedTargetDir
                                    extractedRemoteDir.isDirectory -> extractedRemoteDir
                                    else -> extractedStageDir.listFiles()?.firstOrNull { it.isDirectory }
                                }
                                sourceDir?.let { copyDirectoryToTarget(it, File(localSaveDir, ldn)) } == true
                            }
                            if (copied) suc++ else fail++
                            withContext(Dispatchers.IO) {
                                if (archiveFile.exists()) archiveFile.delete()
                                if (extractedStageDir.exists()) extractedStageDir.deleteRecursively()
                            }
                        }
                    }
                    withContext(Dispatchers.IO) { if (stageRoot.exists()) stageRoot.deleteRecursively() }
                    ShowMessage(if (fail == 0) "一键下载完成！" else "一键下载完成 ($suc 成功, $fail 失败)", isChecking = false)
                    refreshFolderList(); isProcessing.value = false
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
                    else { ShowMessage("WebDAV 登录成功！", isChecking = false); refreshFolderList() }
                    isProcessing.value = false
                }
            })

            // ── Create folder ────────────────────────────────────────────────
            val folderName = remember { mutableStateOf("") }
            Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(Strings.folderNameLabel, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(end = 8.dp))
                BasicTextField(value = folderName.value, onValueChange = { folderName.value = it }, modifier = Modifier.widthIn(min = 80.dp).background(Color.White, RoundedCornerShape(4.dp)).border(1.dp, AppColors.Border, RoundedCornerShape(4.dp)).padding(4.dp).heightIn(max = 120.dp).verticalScroll(rememberScrollState()))
            }
            CustomButton(text = "新建文件夹", onClick = {
                val url = getConfig(GLOBAL_SETTINGS.WEBDAV_URL.key); val username = getConfig(GLOBAL_SETTINGS.WEBDAV_USERNAME.key); val password = getConfig(GLOBAL_SETTINGS.WEBDAV_PASSWORD.key); val name = folderName.value.trim()
                if (url.isBlank()) { ShowMessage("请先填写 WebDAV 服务器地址", isChecking = false); return@CustomButton }
                if (username.isBlank() || password.isBlank()) { ShowMessage("请先填写 WebDAV 用户名和密码", isChecking = false); return@CustomButton }
                if (name.isBlank()) { ShowMessage("请先输入文件夹名称", isChecking = false); return@CustomButton }
                scope.launch {
                    isProcessing.value = true; ShowMessage("正在创建文件夹: $name ...", isChecking = false)
                    val p = "zkqFiles/$name/zkqGlobalGameSave"
                    val e = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.ensureRemotePath(url, username, password, p) }
                    if (e == null) { ShowMessage("文件夹创建成功: $name", isChecking = false); folderName.value = "" } else { ShowMessage("创建失败: $e", isChecking = false) }
                    refreshFolderList(); isProcessing.value = false
                }
            })

            // ── Folder list ──────────────────────────────────────────────────
            if (folderList.value.isEmpty()) {
                Text("暂无文件夹，请先点击「新建文件夹」创建", style = MaterialTheme.typography.bodySmall, color = AppColors.NavInactive, modifier = Modifier.padding(top = 8.dp, start = 4.dp))
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    folderList.value.forEach { folder ->
                        val archives = folderArchiveMap.value[folder] ?: emptyList(); val cnt = archives.size
                        val exp = expandedFolders.value.contains(folder)
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp, horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isFolderChecked(folder, archives), onCheckedChange = { checked -> toggleFolder(folder, archives, checked) }, modifier = Modifier.padding(end = 4.dp))
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.weight(1f).clickable { if (folder !in folderEditingName.value) expandedFolders.value = if (exp) expandedFolders.value - folder else expandedFolders.value + folder }) {
                                    if (folder in folderEditingName.value) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            BasicTextField(
                                                value = folderRenameDrafts.value[folder].orEmpty(),
                                                onValueChange = { folderRenameDrafts.value = folderRenameDrafts.value + (folder to it) },
                                                modifier = Modifier.widthIn(min = 60.dp).background(Color.White, RoundedCornerShape(4.dp)).border(1.dp, AppColors.Border, RoundedCornerShape(4.dp)).padding(4.dp)
                                            )
                                            CustomButton(text = Strings.saveFolderName, icon = Icons.Filled.Check, marginEnd = 2.dp, onClick = { saveRename(folder) })
                                            CustomButton(text = Strings.renameFolder, icon = Icons.Filled.Close, marginEnd = 0.dp, onClick = { cancelRename(folder) })
                                        }
                                    } else {
                                        Text(text = "${if (exp) "▼" else "▶"} $folder", style = MaterialTheme.typography.labelMedium, color = AppColors.TextPrimary)
                                    }
                                }
                                Text(text = "$cnt 个存档", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                                CustomButton(text = Strings.deleteFolder, icon = Icons.Filled.Delete, enable = !isProcessing.value, onClick = { deleteFolder(folder) })
                                CustomButton(
                                    text = Strings.renameFolder,
                                    icon = Icons.Filled.Edit,
                                    enable = !isProcessing.value,
                                    onClick = {
                                        startRename(folder)
                                    }
                                )
                            }
                        }
                        if (exp) {
                            Column(modifier = Modifier.padding(start = 40.dp)) {
                                if (archives.isNotEmpty()) { val ae = "$folder/archive" in expandedFolders.value
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                        Checkbox(
                                            checked = archives.all { isArchiveChecked(folder, it) },
                                            onCheckedChange = { c ->
                                                if (c) toggleFolder(folder, archives, true)
                                                else archives.forEach { toggleArchive(folder, it, false) }
                                            },
                                            modifier = Modifier.padding(end = 2.dp)
                                        )
                                        Row(modifier = Modifier.clickable { expandedFolders.value = if (ae) expandedFolders.value - "$folder/archive" else expandedFolders.value + "$folder/archive" }) { Text(text = "${if (ae) "▼" else "▶"} 📁 国际服存档", style = MaterialTheme.typography.labelSmall, color = AppColors.TextPrimary) }
                                    }
                                    if (ae) { if (archives.isEmpty()) { Text("暂无存档", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive, modifier = Modifier.padding(start = 16.dp)) }
                                        else { archives.forEach { a -> Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 16.dp)) {
                                            Checkbox(checked = isArchiveChecked(folder, a), onCheckedChange = { c -> toggleArchive(folder, a, c) }, modifier = Modifier.padding(end = 2.dp))
                                            Text("📦 $a", style = MaterialTheme.typography.labelSmall, color = AppColors.NavInactive)
                                        } } }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Sync mode dropdown ─────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Text(text = Strings.webdavSyncMode, style = MaterialTheme.typography.labelSmall, color = AppColors.TextPrimary, modifier = Modifier.padding(end = 8.dp))
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                    Button(
                        onClick = { syncExpanded.value = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(
                            when (syncMode.value) {
                                1 -> Strings.webdavSyncModeOverwrite
                                2 -> Strings.webdavSyncModeIncremental
                                else -> Strings.webdavSyncModeNone
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    if (syncExpanded.value) {
                        Popup(
                            onDismissRequest = { syncExpanded.value = false },
                            properties = PopupProperties(focusable = true)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AppColors.CardBg,
                                shadowElevation = 4.dp
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    listOf(0, 1, 2).forEach { idx ->
                                        Text(
                                            text = when (idx) {
                                                1 -> Strings.webdavSyncModeOverwrite
                                                2 -> Strings.webdavSyncModeIncremental
                                                else -> Strings.webdavSyncModeNone
                                            },
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (idx == syncMode.value) AppColors.Primary else AppColors.TextPrimary,
                                            modifier = Modifier
                                                .clickable { syncMode.value = idx; syncExpanded.value = false }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (syncMode.value == 1) {
                Text(
                    text = Strings.webdavSyncOverwriteDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            } else if (syncMode.value == 2) {
                Text(
                    text = Strings.webdavSyncIncrementalDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Row(modifier = Modifier.padding(top = 4.dp)) {
                val sel = syncMode.value != 0
                val hasSelection = folderSelections.value.isNotEmpty()
                val anyRemoteHasFiles = folderSelections.value.keys.any { folder -> folderArchiveMap.value[folder]?.isNotEmpty() == true }
                val ue = hasSelection && !isProcessing.value && sel && localHasSaves.value
                val de = hasSelection && !isProcessing.value && sel && anyRemoteHasFiles
                CustomButton(text = if (isProcessing.value) "处理中..." else "一键上传", marginEnd = 4.dp, enable = ue, onClick = { performUpload() })
                CustomButton(text = if (isProcessing.value) "处理中..." else "一键下载", marginEnd = 4.dp, enable = de, onClick = { performDownload() })
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

@Composable
private fun SimpleDropdown(sel: String, opts: List<String>, onSel: (String) -> Unit, mod: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = mod.wrapContentSize(Alignment.TopStart)) {
        Button(onClick = { expanded = true }, shape = RoundedCornerShape(6.dp), modifier = Modifier.height(26.dp), contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
            Text(sel, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.height(16.dp))
        }
        if (expanded) {
            Popup(onDismissRequest = { expanded = false }, properties = PopupProperties(focusable = true)) {
                Surface(shape = RoundedCornerShape(8.dp), color = AppColors.CardBg, shadowElevation = 4.dp, modifier = Modifier.heightIn(max = 200.dp)) {
                    Column(modifier = Modifier.padding(4.dp).verticalScroll(rememberScrollState())) {
                        opts.forEach { opt ->
                            Text(text = opt, style = MaterialTheme.typography.labelSmall, color = if (opt == sel) AppColors.Primary else AppColors.TextPrimary, modifier = Modifier.clickable { onSel(opt); expanded = false }.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}
