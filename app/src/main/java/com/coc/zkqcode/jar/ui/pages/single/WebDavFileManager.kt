package com.coc.zkqcode.jar.ui.pages.single

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.MotionDurationScale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.ui.Strings
import com.coc.zkqcode.jar.ui.theme.AppColors
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class FileItem(
    val name: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val modified: String = ""
)

private enum class ConflictMode { OVERWRITE, INCREMENT, CANCEL, OVERWRITE_ALL, INCREMENT_ALL }

private data class ConflictRequest(
    val sourcePath: String,
    val destinationPath: String,
    val actionLabel: String,
    val result: CompletableDeferred<ConflictMode?>
)

private data class PanelState(
    val currentPath: String = "/",
    val history: List<String> = listOf("/"),
    val historyIndex: Int = 0,
    val items: List<FileItem> = emptyList(),
    val isLoading: Boolean = false
)

private const val LOCAL_ROOT = "/sdcard/zkqFiles"
private const val REMOTE_WEBDAV_ROOT = "zkqFiles"
private const val LOCAL_STORAGE_LABEL = "本地存储"
private const val REMOTE_STORAGE_LABEL = "远程存储"
private const val WEBDAV_CONFIG_DELIMITER = "|||"

private val LOCAL_ALLOWED_DIRS = listOf("zkqGlobalGameSave", "zkqCnGameSave")
private val LOCAL_CONFIG_FILE = "zkq_config.json"

// Unified WebDAV palette
private val WebDavBackground = Color(0xFFF4F7FA)
private val WebDavSurface = Color(0xFFFFFFFF)
private val WebDavSurfaceAlt = Color(0xFFEDF2F7)
private val WebDavBorder = Color(0xFFD6DEE7)
private val WebDavAccent = Color(0xFF3B82F6)
private val WebDavMuted = Color(0xFF64748B)
private val WebDavText = Color(0xFF1F2937)
private val WebDavFolder = Color(0xFFF59E0B)
private val WebDavDanger = Color(0xFFDC2626)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WebDavFileManager(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sdPath = android.os.Environment.getExternalStorageDirectory().path

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            File(context.filesDir, "webdav_dl_temp").deleteRecursively()
            File(context.filesDir, "webdav_stage").deleteRecursively()
        }
    }

    var leftState by remember { mutableStateOf(PanelState()) }
    var rightState by remember { mutableStateOf(PanelState()) }
    var activePanel by remember { mutableIntStateOf(0) } // 0=left, 1=right

    val contextMenuVisible = remember { mutableStateOf(false) }
    val contextMenuItem = remember { mutableStateOf<FileItem?>(null) }
    val contextMenuPos = remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    val newFolderDialogVisible = remember { mutableStateOf(false) }
    val newFolderName = remember { mutableStateOf("") }
    val newFolderTarget = remember { mutableIntStateOf(0) } // 0=left, 1=right

    val renameDialogVisible = remember { mutableStateOf(false) }
    val renameItem = remember { mutableStateOf<FileItem?>(null) }
    val renameNewName = remember { mutableStateOf("") }
    val renameTarget = remember { mutableIntStateOf(0) }

    val deleteDialogVisible = remember { mutableStateOf(false) }
    val deleteItem = remember { mutableStateOf<FileItem?>(null) }
    val deleteTarget = remember { mutableIntStateOf(0) }

    val detailDialogVisible = remember { mutableStateOf(false) }
    val detailItem = remember { mutableStateOf<FileItem?>(null) }
    val detailTarget = remember { mutableIntStateOf(0) }

    val webdavConfigVisible = remember { mutableStateOf(false) }

    // ── Toolbar states ────────────────────────────────────────────────
    val searchVisible = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val selectedLocal = remember { mutableStateOf(emptySet<String>()) }
    val selectedRemote = remember { mutableStateOf(emptySet<String>()) }
    val deleteSelectedDialogVisible = remember { mutableStateOf(false) }
    val batchBusy = remember { mutableStateOf(false) }
    val stopRequested = remember { mutableStateOf(false) }
    val conflictRequest = remember { mutableStateOf<ConflictRequest?>(null) }
    val isRefreshingLeft = remember { mutableStateOf(false) }
    val isRefreshingRight = remember { mutableStateOf(false) }
    // 0=name asc, 1=name desc, 2=size asc, 3=size desc
    val sortMode = remember { mutableIntStateOf(0) }
    val sortMenuVisible = remember { mutableStateOf(false) }
    val moreMenuVisible = remember { mutableStateOf(false) }
    val logsVisible = remember { mutableStateOf(false) }
    val logMessages = remember { mutableStateListOf<String>() }
    fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        // Error messages: display in full detail
        val errorKeywords = listOf("失败", "错误", "取消", "停止", "为空", "不存在", "Error", "error", "failed", "cancel")
        val isError = errorKeywords.any { msg.contains(it) }
        if (isError) {
            logMessages.add(0, "[$time] $msg")
        } else {
            // Process logs: show action summary + filename, strip full path/time/size details
            val action = when {
                msg.contains("开始上传") -> "开始上传"
                msg.contains("开始下载") -> "开始下载"
                msg.contains("正在上传") -> "正在上传"
                msg.contains("正在下载") -> "正在下载"
                msg.contains("正在复制到临时目录") -> "正在复制到临时目录"
                msg.contains("上传完成") -> "上传完成"
                msg.contains("下载文件") -> "正在下载文件"
                msg.contains("目录下载完成") -> "目录下载完成"
                msg.contains("压缩完成") -> "压缩完成"
                msg.contains("创建文件夹") -> "创建文件夹"
                msg.contains("创建成功") -> "创建成功"
                msg.contains("删除") -> "删除"
                msg.contains("删除成功") -> "删除成功"
                msg.contains("重命名") -> "重命名"
                msg.contains("重命名成功") -> "重命名成功"
                msg.contains("读取远程目录") -> "读取远程目录"
                msg.contains("正在解压") || msg.contains("开始解压") -> "解压"
                else -> "操作"
            }
            // Extract filename: content after colon, keep only last path segment, preserve "a → b" for rename
            val fileName = if (msg.contains(":")) {
                msg.substringAfter(": ").trim()
                    .split(" → ")
                    .joinToString(" → ") { it.substringAfterLast("/") }
            } else ""
            logMessages.add(0, "[$time] $action${if (fileName.isNotBlank()) " $fileName" else ""}")
        }
        if (logMessages.size > 100) logMessages.removeAt(logMessages.size - 1)
    }

    val isRightConfigured: Boolean = remember {
        val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
        val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
        val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
        url.isNotBlank() && user.isNotBlank() && pass.isNotBlank()
    }

    fun selectedFor(panel: Int): Set<String> = if (panel == 0) selectedLocal.value else selectedRemote.value

    fun setSelectedFor(panel: Int, names: Set<String>) {
        if (panel == 0) selectedLocal.value = names else selectedRemote.value = names
    }

    fun clearSelectedFor(panel: Int) = setSelectedFor(panel, emptySet())

    suspend fun resolveConflict(sourcePath: String, destinationPath: String, actionLabel: String): ConflictMode? {
        val deferred = CompletableDeferred<ConflictMode?>()
        conflictRequest.value = ConflictRequest(sourcePath, destinationPath, actionLabel, deferred)
        return deferred.await()
    }

    fun navigatePanel(panel: Int, path: String) {
        val currentState = if (panel == 0) leftState else rightState
        val newHistory = currentState.history.subList(0, currentState.historyIndex + 1) + path
        val newState = currentState.copy(
            currentPath = path,
            history = newHistory,
            historyIndex = newHistory.size - 1,
            isLoading = true
        )
        if (panel == 0) leftState = newState else rightState = newState

        scope.launch {
            val items = if (panel == 0) {
                loadLocalItems(path, sdPath)
            } else {
                loadRemoteItems(path)
            }
            val updated = newState.copy(items = items, isLoading = false)
            if (panel == 0) leftState = updated else rightState = updated
        }
    }

    fun goBack(panel: Int) {
        val state = if (panel == 0) leftState else rightState
        if (state.historyIndex > 0) {
            val newIndex = state.historyIndex - 1
            val newPath = state.history[newIndex]
            val newState = state.copy(historyIndex = newIndex, currentPath = newPath, isLoading = true)
            if (panel == 0) leftState = newState else rightState = newState
            scope.launch {
                val items = if (panel == 0) loadLocalItems(newPath, sdPath) else loadRemoteItems(newPath)
                val updated = newState.copy(items = items, isLoading = false)
                if (panel == 0) leftState = updated else rightState = updated
            }
        }
    }

    fun goForward(panel: Int) {
        val state = if (panel == 0) leftState else rightState
        if (state.historyIndex < state.history.size - 1) {
            val newIndex = state.historyIndex + 1
            val newPath = state.history[newIndex]
            val newState = state.copy(historyIndex = newIndex, currentPath = newPath, isLoading = true)
            if (panel == 0) leftState = newState else rightState = newState
            scope.launch {
                val items = if (panel == 0) loadLocalItems(newPath, sdPath) else loadRemoteItems(newPath)
                val updated = newState.copy(items = items, isLoading = false)
                if (panel == 0) leftState = updated else rightState = updated
            }
        }
    }

    fun onItemClick(panel: Int, item: FileItem) {
        activePanel = panel
        if (!item.isDirectory) return
        val state = if (panel == 0) leftState else rightState
        val newPath = buildPath(state.currentPath, item.name)
        navigatePanel(panel, newPath)
    }

    fun canGoBack(panel: Int) = if (panel == 0) leftState.historyIndex > 0 else rightState.historyIndex > 0
    fun canGoForward(panel: Int) = if (panel == 0) leftState.historyIndex < leftState.history.size - 1 else rightState.historyIndex < rightState.history.size - 1

    fun refreshPanel(panel: Int, onDone: () -> Unit = {}) {
        val state = if (panel == 0) leftState else rightState
        scope.launch {
            val items = if (panel == 0) loadLocalItems(state.currentPath, sdPath) else loadRemoteItems(state.currentPath)
            if (panel == 0) leftState = state.copy(items = items) else rightState = state.copy(items = items)
            onDone()
        }
    }

    fun refreshPanelWithIndicator(panel: Int) {
        if (panel == 0) isRefreshingLeft.value = true else isRefreshingRight.value = true
        scope.launch {
            val state = if (panel == 0) leftState else rightState
            val items = if (panel == 0) loadLocalItems(state.currentPath, sdPath) else loadRemoteItems(state.currentPath)
            if (panel == 0) leftState = state.copy(items = items) else rightState = state.copy(items = items)
            // Ensure the spinner is visible for at least a short time to avoid flicker on fast refreshes
            delay(600)
            if (panel == 0) isRefreshingLeft.value = false else isRefreshingRight.value = false
        }
    }

    // Initialize left panel
    remember {
        navigatePanel(0, "/")
        if (isRightConfigured) navigatePanel(1, "/")
    }

    // Display path
    val displayPath = when {
        activePanel == 0 -> formatLocalPath(leftState.currentPath, sdPath)
        isRightConfigured -> formatRemotePath(rightState.currentPath)
        else -> "/"
    }

    Column(modifier = Modifier.fillMaxSize().background(WebDavBackground)) {
        // App header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WebDavSurfaceAlt)
                .border(1.dp, WebDavBorder)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = Strings.webdavClose, modifier = Modifier.size(18.dp))
            }
            Text(
                text = Strings.webdavFileManagerTitle,
                style = MaterialTheme.typography.labelMedium,
                color = WebDavText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "日志",
                color = if (logsVisible.value) WebDavAccent else WebDavText,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { logsVisible.value = !logsVisible.value }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            IconButton(onClick = { webdavConfigVisible.value = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Settings, contentDescription = "WebDAV 配置", modifier = Modifier.size(16.dp), tint = WebDavAccent)
            }
        }

        // Path breadcrumb
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (activePanel == 0) WebDavAccent.copy(alpha = 0.15f) else WebDavAccent.copy(alpha = 0.15f))
                .border(1.dp, if (activePanel == 0) WebDavAccent else WebDavAccent)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PathBreadcrumbs(
                path = displayPath,
                modifier = Modifier.weight(1f),
                onNavigate = { path -> navigatePanel(activePanel, path) }
            )
            IconButton(onClick = {
                searchVisible.value = !searchVisible.value
                if (!searchVisible.value) searchQuery.value = ""
            }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Search, contentDescription = "搜索", modifier = Modifier.size(16.dp), tint = WebDavText)
            }
        }

        // Search row
        if (searchVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WebDavBackground)
                    .border(1.dp, WebDavBorder)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .background(WebDavSurfaceAlt, RoundedCornerShape(4.dp))
                        .border(1.dp, WebDavBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelSmall.copy(color = WebDavText)
                )
                IconButton(onClick = { searchVisible.value = false; searchQuery.value = "" }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }

        if (selectedFor(activePanel).isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WebDavAccent.copy(alpha = 0.12f))
                    .border(1.dp, WebDavBorder)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "已选 ${selectedFor(activePanel).size} 项",
                    style = MaterialTheme.typography.labelSmall,
                    color = WebDavAccent,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "取消",
                    color = WebDavAccent,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { clearSelectedFor(activePanel) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Dual panels
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left panel
                Panel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    state = leftState,
                    isActive = activePanel == 0,
                    isLocal = true,
                    isRefreshing = isRefreshingLeft.value,
                    onItemClick = { onItemClick(0, it) },
                    onItemLongPress = { item, _ ->
                        activePanel = 0
                        setSelectedFor(0, setOf(item.name))
                    },
                    selectionMode = selectedFor(0).isNotEmpty(),
                    onRefresh = { refreshPanelWithIndicator(0) },
                    searchQuery = searchQuery.value,
                    sortMode = sortMode.value,
                    selectedItems = selectedFor(0),
                    onToggleSelect = { name ->
                        setSelectedFor(0, if (name in selectedFor(0)) selectedFor(0) - name else selectedFor(0) + name)
                    },
                )

                // Divider - changes color based on active panel
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(if (activePanel == 0) WebDavAccent else WebDavAccent)
                )

                // Right panel
                Panel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    state = rightState,
                    isActive = activePanel == 1,
                    isLocal = false,
                    isRefreshing = isRefreshingRight.value,
                    onItemClick = { onItemClick(1, it) },
                    onItemLongPress = { item, _ ->
                        activePanel = 1
                        setSelectedFor(1, setOf(item.name))
                    },
                    selectionMode = selectedFor(1).isNotEmpty(),
                    onRefresh = { refreshPanelWithIndicator(1) },
                    isConfigured = isRightConfigured,
                    onConfigure = { webdavConfigVisible.value = true },
                    searchQuery = searchQuery.value,
                    sortMode = sortMode.value,
                    selectedItems = selectedFor(1),
                    onToggleSelect = { name ->
                        setSelectedFor(1, if (name in selectedFor(1)) selectedFor(1) - name else selectedFor(1) + name)
                    },
                )
            }

            // Sort popup: centered overlay on the active panel's half
            if (sortMenuVisible.value) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(if (activePanel == 0) 0.5f else 0.5f)
                        .align(if (activePanel == 0) Alignment.CenterStart else Alignment.CenterEnd)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { sortMenuVisible.value = false },
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WebDavSurfaceAlt,
                        shadowElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(4.dp)) {
                            listOf("名称升序", "名称降序", "大小升序", "大小降序").forEachIndexed { idx, label ->
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (idx == sortMode.value) WebDavAccent else WebDavText,
                                    modifier = Modifier
                                        .clickable { sortMode.value = idx; sortMenuVisible.value = false }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (batchBusy.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WebDavSurfaceAlt)
                    .border(1.dp, WebDavBorder)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "正在操作，停止后等待当前文件完成",
                    style = MaterialTheme.typography.labelSmall,
                    color = WebDavMuted,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { stopRequested.value = true }) { Text("停止", color = WebDavDanger) }
            }
        }

        if (logsVisible.value || batchBusy.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WebDavSurfaceAlt)
                    .border(1.dp, WebDavBorder)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("操作日志", style = MaterialTheme.typography.labelSmall, color = WebDavText, modifier = Modifier.weight(1f))
                    Text(
                        text = "清空",
                        color = WebDavMuted,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { logMessages.clear() }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                if (logMessages.isEmpty()) {
                    Text(
                        text = "暂无操作日志",
                        style = MaterialTheme.typography.labelSmall,
                        color = WebDavMuted,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.heightIn(min = 80.dp, max = 160.dp)) {
                        items(logMessages.take(10)) { msg ->
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall,
                                color = WebDavMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom bar: normal / multi-select
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WebDavSurfaceAlt)
                .border(1.dp, WebDavBorder)
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomAction(modifier = Modifier.weight(1f), icon = Icons.AutoMirrored.Default.ArrowBack, label = "返回", enabled = canGoBack(activePanel)) { goBack(activePanel) }
            BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.ArrowUpward, label = "上级") {
                val current = if (activePanel == 0) leftState.currentPath else rightState.currentPath
                navigatePanel(activePanel, parentPath(current))
            }
            BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Home, label = "主页") { navigatePanel(activePanel, "/") }
            BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Refresh, label = "刷新") { refreshPanelWithIndicator(activePanel) }
            BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.CreateNewFolder, label = "新建") {
                newFolderTarget.value = activePanel
                newFolderName.value = ""
                newFolderDialogVisible.value = true
            }
            BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.SortByAlpha, label = "排序") { sortMenuVisible.value = !sortMenuVisible.value }
        }

        if (selectedFor(activePanel).isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WebDavSurfaceAlt)
                    .border(1.dp, WebDavBorder)
                    .navigationBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.SelectAll, label = "全选") {
                    val state = if (activePanel == 0) leftState else rightState
                    val filtered = getFilteredItems(state.items, searchQuery.value, sortMode.value)
                    setSelectedFor(activePanel, filtered.map { it.name }.toSet())
                }
                BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.InvertColors, label = "反选") {
                    val state = if (activePanel == 0) leftState else rightState
                    val filtered = getFilteredItems(state.items, searchQuery.value, sortMode.value)
                    val allNames = filtered.map { it.name }.toSet()
                    setSelectedFor(activePanel, allNames - selectedFor(activePanel))
                }
                BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.FolderCopy, label = "类选") {
                    val state = if (activePanel == 0) leftState else rightState
                    val filtered = getFilteredItems(state.items, searchQuery.value, sortMode.value)
                    val firstName = selectedFor(activePanel).firstOrNull()
                    val firstItem = filtered.find { it.name == firstName }
                    if (firstItem != null) {
                        val ext = firstItem.name.substringAfterLast('.', "")
                        val matchType = if (firstItem.isDirectory) "dir" else ext
                        setSelectedFor(activePanel, filtered.filter { item -> if (matchType == "dir") item.isDirectory else !item.isDirectory && item.name.substringAfterLast('.', "") == matchType }.map { it.name }.toSet())
                    }
                }
                BottomAction(modifier = Modifier.weight(1f), icon = if (activePanel == 0) Icons.AutoMirrored.Default.ArrowForward else Icons.AutoMirrored.Default.ArrowBack, label = "复制", enabled = !batchBusy.value && (activePanel == 1 || isRightConfigured)) {
                    val names = selectedFor(activePanel).toSet()
                    stopRequested.value = false
                    batchBusy.value = true
                    copySelectedToOtherPanel(context, activePanel, names, false, leftState, rightState, sdPath, scope, { panel, newState -> if (panel == 0) leftState = newState else rightState = newState }, ::addLog, isStopRequested = { stopRequested.value }, onResolveConflict = ::resolveConflict, onDone = { batchBusy.value = false })
                    clearSelectedFor(activePanel)
                }
                BottomAction(modifier = Modifier.weight(1f), icon = if (activePanel == 0) Icons.AutoMirrored.Default.ArrowForward else Icons.AutoMirrored.Default.ArrowBack, label = "移动", enabled = !batchBusy.value && (activePanel == 1 || isRightConfigured)) {
                    val names = selectedFor(activePanel).toSet()
                    stopRequested.value = false
                    batchBusy.value = true
                    copySelectedToOtherPanel(context, activePanel, names, true, leftState, rightState, sdPath, scope, { panel, newState -> if (panel == 0) leftState = newState else rightState = newState }, ::addLog, isStopRequested = { stopRequested.value }, onResolveConflict = ::resolveConflict, onDone = { batchBusy.value = false })
                    clearSelectedFor(activePanel)
                }
                // Show rename when exactly one directory is selected in the active panel.
                if (activePanel == 1 && selectedFor(activePanel).size == 1) {
                    val renameState = if (activePanel == 0) leftState else rightState
                    val renameFiltered = getFilteredItems(renameState.items, searchQuery.value, sortMode.value)
                    val renameSelectedName = selectedFor(activePanel).first()
                    val renameSelectedItem = renameFiltered.find { it.name == renameSelectedName }
                    if (renameSelectedItem != null && renameSelectedItem.isDirectory) {
                        BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.DriveFileRenameOutline, label = "重命名") {
                            renameItem.value = renameSelectedItem
                            renameNewName.value = renameSelectedItem.name
                            renameTarget.value = activePanel
                            renameDialogVisible.value = true
                            clearSelectedFor(activePanel)
                        }
                    }
                }
                BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.Delete, label = "删除", enabled = !batchBusy.value, tint = WebDavDanger) { deleteSelectedDialogVisible.value = true }
                BottomAction(modifier = Modifier.weight(1f), icon = Icons.Default.ClearAll, label = "取消") {
                    clearSelectedFor(activePanel)
                }
            }
        }
    }

    // Context menu
    if (contextMenuVisible.value) {
        val item = contextMenuItem.value
        if (item != null) {
            val isLeftActive = contextTargetPanel == 0
            Popup(
                onDismissRequest = { contextMenuVisible.value = false },
                properties = PopupProperties(focusable = true)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = WebDavSurfaceAlt,
                    shadowElevation = 8.dp,
                    modifier = Modifier.widthIn(min = 200.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        ContextMenuItem(
                            icon = Icons.AutoMirrored.Default.InsertDriveFile,
                            text = item.name,
                            isHeader = true
                        )
                        if (isLeftActive) {
                            ContextMenuItem(
                                icon = Icons.AutoMirrored.Default.ArrowForward,
                                text = Strings.webdavCopyToRight,
                                onClick = {
                                    contextMenuVisible.value = false
                                    copyLocalToRemote(context, item, leftState.currentPath, rightState.currentPath, sdPath, scope, ::addLog)
                                }
                            )
                        } else {
                            ContextMenuItem(
                                icon = Icons.AutoMirrored.Default.ArrowBack,
                                text = Strings.webdavCopyToLeft,
                                onClick = {
                                    contextMenuVisible.value = false
                                    copyRemoteToLocal(context, item, rightState.currentPath, leftState.currentPath, sdPath, scope, ::addLog)
                                }
                            )
                        }
                        if (!item.isDirectory && (item.name.endsWith(".zip") || item.name.endsWith(".tar.gz"))) {
                            ContextMenuItem(
                                icon = Icons.Default.FolderOpen,
                                text = Strings.webdavExtract,
                                onClick = {
                                    contextMenuVisible.value = false
                                    extractArchive(context, item, contextTargetPanel, if (contextTargetPanel == 0) leftState else rightState, sdPath, scope, { panel, newState ->
                                        if (panel == 0) leftState = newState else rightState = newState
                                    }, ::addLog)
                                }
                            )
                        }
                        ContextMenuItem(
                            icon = Icons.Default.DriveFileRenameOutline,
                            text = Strings.webdavRename,
                            onClick = {
                                contextMenuVisible.value = false
                                renameItem.value = item
                                renameNewName.value = item.name
                                renameTarget.value = contextTargetPanel
                                renameDialogVisible.value = true
                            }
                        )
                        ContextMenuItem(
                            icon = Icons.Default.Delete,
                            text = Strings.webdavDelete,
                            onClick = {
                                contextMenuVisible.value = false
                                deleteItem.value = item
                                deleteTarget.value = contextTargetPanel
                                deleteDialogVisible.value = true
                            }
                        )
                        ContextMenuItem(
                            icon = Icons.Default.Info,
                            text = Strings.webdavDetail,
                            onClick = {
                                contextMenuVisible.value = false
                                detailItem.value = item
                                detailTarget.value = contextTargetPanel
                                detailDialogVisible.value = true
                            }
                        )
                    }
                }
            }
        }
    }

    // New folder dialog
    if (newFolderDialogVisible.value) {
        com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
            onDismissRequest = { newFolderDialogVisible.value = false },
            title = { Text(Strings.webdavNewFolder) },
            text = {
                BasicTextField(
                    value = newFolderName.value,
                    onValueChange = { newFolderName.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WebDavSurface, RoundedCornerShape(4.dp))
                        .border(1.dp, WebDavBorder, RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    singleLine = true,
                    decorationBox = { innerField ->
                        Box {
                            if (newFolderName.value.isEmpty()) {
                                Text(Strings.webdavNewFolderNameHint, color = WebDavMuted, style = MaterialTheme.typography.bodyMedium)
                            }
                            innerField()
                        }
                    }
                )
            },
            confirmButton = {
                Row {
                    androidx.compose.material3.TextButton(onClick = { newFolderDialogVisible.value = false }) { Text(Strings.cancel) }
                    androidx.compose.material3.TextButton(onClick = {
                        newFolderDialogVisible.value = false
                        val name = newFolderName.value.trim()
                        if (name.isNotBlank()) {
                            createFolder(newFolderTarget.value, name, leftState, rightState, sdPath, scope, { panel, newState ->
                                if (panel == 0) leftState = newState else rightState = newState
                            }, ::addLog)
                        }
                    }) { Text(Strings.confirm) }
                }
            }
        )
    }

    // Rename dialog
    if (renameDialogVisible.value) {
        val item = renameItem.value
        if (item != null) {
            com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
                onDismissRequest = { renameDialogVisible.value = false },
                title = { Text(Strings.webdavRenameTitle) },
                text = {
                    BasicTextField(
                        value = renameNewName.value,
                        onValueChange = { renameNewName.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WebDavSurface, RoundedCornerShape(4.dp))
                            .border(1.dp, WebDavBorder, RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Row {
                        androidx.compose.material3.TextButton(onClick = { renameDialogVisible.value = false }) { Text(Strings.cancel) }
                        androidx.compose.material3.TextButton(onClick = {
                            renameDialogVisible.value = false
                            val newName = renameNewName.value.trim()
                            if (newName.isNotBlank() && newName != item.name) {
                                performRename(renameTarget.value, item, newName, leftState, rightState, sdPath, scope, { panel, newState ->
                                    if (panel == 0) leftState = newState else rightState = newState
                                }, ::addLog)
                            }
                        }) { Text(Strings.confirm) }
                    }
                }
            )
        }
    }

    // Delete dialog
    if (deleteDialogVisible.value) {
        val item = deleteItem.value
        if (item != null) {
            com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
                onDismissRequest = { deleteDialogVisible.value = false },
                title = { Text(Strings.webdavDeleteConfirm) },
                text = { Text("${Strings.webdavDeleteMsg}\n${item.name}") },
                confirmButton = {
                    Row {
                        androidx.compose.material3.TextButton(onClick = { deleteDialogVisible.value = false }) { Text(Strings.cancel) }
                        androidx.compose.material3.TextButton(onClick = {
                            deleteDialogVisible.value = false
                            performDelete(deleteTarget.value, item, leftState, rightState, sdPath, scope, { panel, newState ->
                                if (panel == 0) leftState = newState else rightState = newState
                            }, ::addLog)
                        }) { Text(Strings.confirm, color = WebDavDanger) }
                    }
                }
            )
        }
    }

    // Detail dialog
    if (detailDialogVisible.value) {
        val item = detailItem.value
        if (item != null) {
            com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
                onDismissRequest = { detailDialogVisible.value = false },
                title = { Text(Strings.webdavDetailTitle) },
                text = {
                    Column {
                        DetailRow("名称", item.name)
                        DetailRow("类型", if (item.isDirectory) "文件夹" else "文件")
                        if (!item.isDirectory) DetailRow("大小", formatSize(item.size))
                        if (item.modified.isNotBlank()) DetailRow("修改时间", item.modified)
                        val basePath = if (detailTarget.value == 0) {
                            val state = leftState.currentPath
                            if (state == "/") "$LOCAL_ROOT/${item.name}" else "$LOCAL_ROOT${state.substringAfter(LOCAL_STORAGE_LABEL)}/${item.name}"
                        } else {
                            "$REMOTE_WEBDAV_ROOT${rightState.currentPath.substringAfter(REMOTE_STORAGE_LABEL, "/")}/${item.name}"
                        }
                        DetailRow("路径", basePath)
                    }
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { detailDialogVisible.value = false }) { Text(Strings.confirm) }
                }
            )
        }
    }

    // Batch delete dialog
    if (deleteSelectedDialogVisible.value) {
        com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
            onDismissRequest = { deleteSelectedDialogVisible.value = false },
            title = { Text("确认批量删除") },
            text = { Text("确认删除选中的 ${selectedFor(activePanel).size} 项？") },
            confirmButton = {
                Row {
                    androidx.compose.material3.TextButton(onClick = { deleteSelectedDialogVisible.value = false }) { Text(Strings.cancel) }
                    androidx.compose.material3.TextButton(onClick = {
                        deleteSelectedDialogVisible.value = false
                        val names = selectedFor(activePanel).toSet()
                        stopRequested.value = false
                        batchBusy.value = true
                        deleteSelectedItems(activePanel, names, leftState, rightState, sdPath, scope, { panel, newState ->
                            if (panel == 0) leftState = newState else rightState = newState
                        }, ::addLog, isStopRequested = { stopRequested.value }, onDone = { batchBusy.value = false })
                        clearSelectedFor(activePanel)
                    }) { Text(Strings.confirm, color = WebDavDanger) }
                }
            }
        )
    }

    conflictRequest.value?.let { request ->
        com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
            onDismissRequest = {
                request.result.complete(null)
                conflictRequest.value = null
            },
            title = { Text("检测到同名") },
            text = {
                Text(
                    "${request.sourcePath}\n${request.actionLabel}到\n${request.destinationPath}\n\n怎么处理？",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WebDavText
                )
            },
            confirmButton = {
                Column(horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            request.result.complete(ConflictMode.OVERWRITE)
                            conflictRequest.value = null
                        }) { Text("覆盖") }
                        TextButton(onClick = {
                            request.result.complete(ConflictMode.INCREMENT)
                            conflictRequest.value = null
                        }) { Text("增量") }
                        TextButton(onClick = {
                            request.result.complete(ConflictMode.CANCEL)
                            conflictRequest.value = null
                        }) { Text("取消", color = WebDavDanger) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            request.result.complete(ConflictMode.OVERWRITE_ALL)
                            conflictRequest.value = null
                        }) { Text("此后一律覆盖", color = WebDavAccent) }
                        TextButton(onClick = {
                            request.result.complete(ConflictMode.INCREMENT_ALL)
                            conflictRequest.value = null
                        }) { Text("此后一律增量", color = WebDavAccent) }
                    }
                }
            }
        )
    }

    // WebDAV config dialog
    if (webdavConfigVisible.value) {
        WebDavConfigDialog(onDismiss = { webdavConfigVisible.value = false }, onConfigured = {
            webdavConfigVisible.value = false
            navigatePanel(1, "/")
        })
    }
}

private var contextTargetPanel = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Panel(
    modifier: Modifier,
    state: PanelState,
    isActive: Boolean,
    isLocal: Boolean,
    onItemClick: (FileItem) -> Unit,
    onItemLongPress: (FileItem, androidx.compose.ui.geometry.Offset) -> Unit,
    isConfigured: Boolean = true,
    onConfigure: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    selectionMode: Boolean = false,
    searchQuery: String = "",
    sortMode: Int = 0,
    selectedItems: Set<String> = emptySet(),
    onToggleSelect: (String) -> Unit = {}
) {
    val listState = rememberLazyListState()
    Column(
        modifier = modifier
            .background(if (isActive) WebDavAccent.copy(alpha = 0.05f) else Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isActive) WebDavAccent.copy(alpha = 0.14f) else Color.Transparent)
                .border(1.dp, WebDavBorder)
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLocal) Strings.webdavLocalStorage else Strings.webdavRemoteStorage,
                style = MaterialTheme.typography.labelMedium,
                color = WebDavText,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${state.items.count { it.isDirectory }} 个文件夹 · ${state.items.count { !it.isDirectory }} 个文件",
                style = MaterialTheme.typography.labelSmall,
                color = WebDavMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        if (isLocal || isConfigured) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp), color = WebDavAccent)
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        onRefresh()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                        if (state.currentPath != "/") {
                            item {
                                FileRow(
                                    item = FileItem("..", isDirectory = true),
                                    onTap = { if (!selectionMode) onItemClick(FileItem("..", isDirectory = true)) },
                                    onLongPress = {},
                                    showParent = true
                                )
                            }
                        }
                        val filteredItems = getFilteredItems(state.items, searchQuery, sortMode)
                        items(filteredItems) { item ->
                            val itemIndex = filteredItems.indexOf(item)
                            val isItemSelected = item.name in selectedItems
                            FileRow(
                                item = item,
                                onTap = { if (selectionMode) onToggleSelect(item.name) else onItemClick(item) },
                                onLongPress = { offset -> onItemLongPress(item, offset) },
                                isSelected = isItemSelected,
                                onToggleSelect = { onToggleSelect(item.name) },
                                selectionMode = selectionMode
                            )
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Strings.webdavNotConfigured, style = MaterialTheme.typography.bodyMedium, color = WebDavMuted, modifier = Modifier.padding(bottom = 12.dp))
                    Button(onClick = onConfigure, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = WebDavAccent)) {
                        Text(Strings.webdavConfigure)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileRow(
    item: FileItem,
    onTap: () -> Unit,
    onLongPress: (androidx.compose.ui.geometry.Offset) -> Unit,
    showParent: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    selectionMode: Boolean = false,
    locked: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .background(if (isSelected) WebDavAccent.copy(alpha = 0.1f) else Color.Transparent)
            .combinedClickable(
                onClick = { if (!locked) onTap() },
                onLongClick = { onLongPress(androidx.compose.ui.geometry.Offset.Zero) },
                indication = if (locked) null else LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (item.isDirectory) Icons.Default.Folder else Icons.AutoMirrored.Default.InsertDriveFile,
            contentDescription = null,
            tint = if (item.isDirectory) WebDavFolder else WebDavMuted,
            modifier = Modifier.size(20.dp).padding(end = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium, color = WebDavText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = when {
                    showParent -> "上级目录"
                    item.isDirectory && item.modified.isNotBlank() -> "文件夹 · ${item.modified}"
                    item.isDirectory -> "文件夹"
                    item.size > 0 && item.modified.isNotBlank() -> "${formatSize(item.size)} · ${item.modified}"
                    item.size > 0 -> formatSize(item.size)
                    item.modified.isNotBlank() -> item.modified
                    else -> "文件"
                },
                style = MaterialTheme.typography.labelSmall,
                color = WebDavMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PathBreadcrumbs(
    path: String,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    val segments = path.trim('/').split('/').filter { it.isNotBlank() }
    Row(modifier = modifier.horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onNavigate("/") }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Home, contentDescription = "主页", modifier = Modifier.size(16.dp), tint = WebDavText)
        }
        segments.forEachIndexed { index, segment ->
            if (index > 0) Text("›", color = WebDavMuted, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 2.dp))
            Text(
                text = segment,
                style = MaterialTheme.typography.labelSmall,
                color = if (index == segments.lastIndex) WebDavText else WebDavMuted,
                fontWeight = if (index == segments.lastIndex) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.clip(RoundedCornerShape(4.dp)).clickable { onNavigate("/" + segments.subList(0, index + 1).joinToString("/")) }.padding(horizontal = 4.dp, vertical = 4.dp),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BottomAction(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    tint: Color = WebDavText,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(6.dp)).clickable(enabled = enabled, onClick = onClick).padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (enabled) tint else WebDavMuted, modifier = Modifier.size(18.dp))
        Text(label, fontSize = 10.sp, color = if (enabled) tint else WebDavMuted, maxLines = 1)
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = WebDavText)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = WebDavText, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    text: String,
    isHeader: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isHeader) Modifier else Modifier.clickable(onClick = onClick))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (isHeader) WebDavMuted else WebDavText
        )
        Text(
            text = text,
            style = if (isHeader) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodyMedium,
            color = if (isHeader) WebDavMuted else WebDavText,
            modifier = Modifier.padding(start = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("$label: ", style = MaterialTheme.typography.bodySmall, color = WebDavMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, color = WebDavText, maxLines = 3, overflow = TextOverflow.Ellipsis)
    }
}

// ── Path helpers ──────────────────────────────────────────────────────

private fun buildPath(current: String, name: String): String {
    if (name == "..") return parentPath(current)
    val base = if (current.endsWith("/")) current else "$current/"
    return base + name
}

private fun parentPath(path: String): String {
    if (path == "/") return "/"
    val trimmed = path.trimEnd('/')
    val lastSlash = trimmed.lastIndexOf('/')
    return if (lastSlash <= 0) "/" else trimmed.substring(0, lastSlash)
}

private fun formatLocalPath(path: String, sdPath: String): String {
    if (path == "/") return "/"
    val inner = path.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
    return "/$LOCAL_STORAGE_LABEL/$inner".replace("//", "/")
}

private fun formatRemotePath(path: String): String {
    if (path == "/") return "/"
    val inner = path.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
    return "/$REMOTE_STORAGE_LABEL/$inner".replace("//", "/")
}

// ── Local file loading ────────────────────────────────────────────────

private suspend fun loadLocalItems(path: String, sdPath: String): List<FileItem> = withContext(Dispatchers.IO) {
    when {
        path == "/" -> listOf(FileItem(LOCAL_STORAGE_LABEL, isDirectory = true))
        path.removePrefix("/").startsWith(LOCAL_STORAGE_LABEL) -> {
            val inner = path.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
            if (inner.isBlank()) {
                // List actual directories, only show allowed ones
                val dirOutput = ShellHelper.runBlockingOutput("ls -d \"$LOCAL_ROOT\"/*/ 2>/dev/null")
                val dirs = dirOutput.mapNotNull { line ->
                    val name = line.trim().trimEnd('/').substringAfterLast("/")
                    if (name in LOCAL_ALLOWED_DIRS) FileItem(name, isDirectory = true) else null
                }
                val fileOutput = ShellHelper.runBlockingOutput("ls -d \"$LOCAL_ROOT\"/$LOCAL_CONFIG_FILE 2>/dev/null")
                val files = if (fileOutput.isNotEmpty()) listOf(FileItem(LOCAL_CONFIG_FILE, isDirectory = false)) else emptyList()
                dirs + files
            } else {
                val fullPath = "$LOCAL_ROOT/$inner"
                val output = ShellHelper.runBlockingOutput("ls -la \"$fullPath\" 2>/dev/null")
                parseLsOutput(output, skipFirst = true)
            }
        }
        else -> emptyList()
    }
}

private fun parseLsOutput(output: List<String>, skipFirst: Boolean = false): List<FileItem> {
    val items = mutableListOf<FileItem>()
    val start = if (skipFirst) 1 else 0
    for (i in start until output.size) {
        val line = output[i].trim()
        if (line.isBlank()) continue
        // Parse ls -la output: permissions links owner size date time name
        val parts = line.split("\\s+".toRegex())
        if (parts.size < 8) continue
        val name = parts.subList(7, parts.size).joinToString(" ")
        if (name == "." || name == "..") continue
        val isDir = parts[0].startsWith("d")
        val size = parts[4].toLongOrNull() ?: 0L
        val dateStr = "${parts[5]} ${parts[6]} ${parts[7]}"
        items.add(FileItem(name, isDir, size, dateStr))
    }
    return items.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenBy { it.name })
}

// ── Remote file loading ───────────────────────────────────────────────

private suspend fun loadRemoteItems(path: String): List<FileItem> = withContext(Dispatchers.IO) {
    val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
    val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
    val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
    if (url.isBlank() || user.isBlank() || pass.isBlank()) return@withContext emptyList()

    when {
        path == "/" -> listOf(FileItem(REMOTE_STORAGE_LABEL, isDirectory = true))
        path.removePrefix("/").startsWith(REMOTE_STORAGE_LABEL) -> {
            val inner = path.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
            val remotePath = if (inner.isBlank()) REMOTE_WEBDAV_ROOT else "$REMOTE_WEBDAV_ROOT/$inner"
            val result = com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listEntries(url, user, pass, remotePath)
            if (result is List<*>) {
                result.filterIsInstance<com.coc.zkqcode.jar.code.universal.network.WebDAVEntry>().map {
                    FileItem(it.name, it.isDirectory, it.size, it.modified)
                }
            } else emptyList()
        }
        else -> emptyList()
    }
}

// ── Config helper ─────────────────────────────────────────────────────

private val GLOBAL_SETTINGS_WEBDAV_URL get() = com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS.WEBDAV_URL.key
private val GLOBAL_SETTINGS_WEBDAV_USERNAME get() = com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS.WEBDAV_USERNAME.key
private val GLOBAL_SETTINGS_WEBDAV_PASSWORD get() = com.coc.zkqcode.jar.ui.schema.Schema.GLOBAL_SETTINGS.WEBDAV_PASSWORD.key

private fun getConfig(key: String): String = GlobalVars.configStates[key]?.value.orEmpty()

// ── File operations ───────────────────────────────────────────────────

private fun createFolder(
    panel: Int, name: String,
    leftState: PanelState, rightState: PanelState,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onStateUpdate: (Int, PanelState) -> Unit,
    onLog: (String) -> Unit = {}
) {
    scope.launch {
        onLog("创建文件夹: $name")
        if (panel == 0) {
            val inner = leftState.currentPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
            val fullPath = if (inner.isBlank()) "$LOCAL_ROOT/$name" else "$LOCAL_ROOT/$inner/$name"
            val result = ShellHelper.runBlocking("mkdir -p \"$fullPath\"")
            if (result.isSuccess) {
                onLog("创建成功: $fullPath")
            } else {
                onLog("创建失败: $fullPath")
            }
        } else {
            val inner = rightState.currentPath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
            val remotePath = if (inner.isBlank()) "$REMOTE_WEBDAV_ROOT/$name" else "$REMOTE_WEBDAV_ROOT/$inner/$name"
            val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
            val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
            val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
            // Avoid NetworkOnMainThreadException for synchronous OkHttp WebDAV call.
            val error = withContext(Dispatchers.IO) {
                com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.createDirectory(url, user, pass, remotePath)
            }
            if (error == null) {
                onLog("创建成功: $remotePath")
            } else {
                onLog("创建失败: $remotePath ($error)")
            }
        }
        // Refresh
        val currentPanelState = if (panel == 0) leftState else rightState
        val items = if (panel == 0) loadLocalItems(currentPanelState.currentPath, sdPath) else loadRemoteItems(currentPanelState.currentPath)
        onStateUpdate(panel, currentPanelState.copy(items = items))
    }
}

private fun performRename(
    panel: Int, item: FileItem, newName: String,
    leftState: PanelState, rightState: PanelState,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onStateUpdate: (Int, PanelState) -> Unit,
    onLog: (String) -> Unit = {}
) {
    scope.launch {
        onLog("重命名: ${item.name} → $newName")
        if (panel == 0) {
            val inner = leftState.currentPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
            val base = if (inner.isBlank()) LOCAL_ROOT else "$LOCAL_ROOT/$inner"
            val source = "$base/${item.name}"
            val target = "$base/$newName"
            val result = ShellHelper.runBlocking("mv \"$source\" \"$target\"")
            if (result.isSuccess) {
                onLog("重命名成功: $source → $target")
            } else {
                onLog("重命名失败: $source → $target")
            }
        } else {
            val inner = rightState.currentPath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
            val base = if (inner.isBlank()) REMOTE_WEBDAV_ROOT else "$REMOTE_WEBDAV_ROOT/$inner"
            val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
            val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
            val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
            val source = "$base/${item.name}"
            val target = "$base/$newName"
            // Avoid NetworkOnMainThreadException for synchronous OkHttp WebDAV call.
            val error = withContext(Dispatchers.IO) {
                com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.movePath(url, user, pass, source, target)
            }
            if (error == null) {
                onLog("重命名成功: $source → $target")
            } else {
                onLog("重命名失败: $source → $target ($error)")
            }
        }
        val currentPanelState = if (panel == 0) leftState else rightState
        val items = if (panel == 0) loadLocalItems(currentPanelState.currentPath, sdPath) else loadRemoteItems(currentPanelState.currentPath)
        onStateUpdate(panel, currentPanelState.copy(items = items))
    }
}

private suspend fun deleteItem(
    panel: Int,
    item: FileItem,
    leftState: PanelState,
    rightState: PanelState,
    onLog: (String) -> Unit = {}
): Boolean {
    onLog("删除: ${item.name}")
    return if (panel == 0) {
        val inner = leftState.currentPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
        val fullPath = if (inner.isBlank()) "$LOCAL_ROOT/${item.name}" else "$LOCAL_ROOT/$inner/${item.name}"
        val cmd = if (item.isDirectory) "rm -rf \"$fullPath\"" else "rm -f \"$fullPath\""
        val result = ShellHelper.runBlocking(cmd)
        if (result.isSuccess) {
            onLog("删除成功: $fullPath")
            true
        } else {
            onLog("删除失败: $fullPath")
            false
        }
    } else {
        val inner = rightState.currentPath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
        val remotePath = if (inner.isBlank()) "$REMOTE_WEBDAV_ROOT/${item.name}" else "$REMOTE_WEBDAV_ROOT/$inner/${item.name}"
        val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
        val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
        val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
        // Avoid NetworkOnMainThreadException for synchronous OkHttp WebDAV call.
        val error = withContext(Dispatchers.IO) {
            com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.deletePath(url, user, pass, remotePath)
        }
        if (error == null) {
            onLog("删除成功: $remotePath")
            true
        } else {
            onLog("删除失败: $remotePath ($error)")
            false
        }
    }
}

private fun performDelete(
    panel: Int,
    item: FileItem,
    leftState: PanelState,
    rightState: PanelState,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onStateUpdate: (Int, PanelState) -> Unit,
    onLog: (String) -> Unit = {}
) {
    scope.launch {
        deleteItem(panel, item, leftState, rightState, onLog)
        val currentPanelState = if (panel == 0) leftState else rightState
        val items = if (panel == 0) loadLocalItems(currentPanelState.currentPath, sdPath) else loadRemoteItems(currentPanelState.currentPath)
        onStateUpdate(panel, currentPanelState.copy(items = items))
    }
}

private suspend fun uploadRemoteSafely(
    url: String,
    user: String,
    pass: String,
    remoteFull: String,
    file: File,
    overwrite: Boolean = false
): String? {
    val tempRemote = "$remoteFull.part"
    val uploadError = withContext(Dispatchers.IO) {
        com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.upload(url, user, pass, tempRemote, file)
    }
    if (uploadError != null) {
        withContext(Dispatchers.IO) {
            com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.deletePath(url, user, pass, tempRemote)
        }
        return uploadError
    }
    val moveError = withContext(Dispatchers.IO) {
        com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.movePath(url, user, pass, tempRemote, remoteFull, overwrite)
    }
    if (moveError != null && overwrite && (moveError.contains("409") || moveError.contains("412"))) {
        // Some WebDAV servers still reject overwrite MOVE with 409/412.
        withContext(Dispatchers.IO) {
            com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.deletePath(url, user, pass, remoteFull)
        }
        val retryError = withContext(Dispatchers.IO) {
            com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.movePath(url, user, pass, tempRemote, remoteFull, true)
        }
        if (retryError != null) {
            withContext(Dispatchers.IO) {
                com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.deletePath(url, user, pass, tempRemote)
            }
        }
        return retryError
    }
    if (moveError != null) {
        withContext(Dispatchers.IO) {
            com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.deletePath(url, user, pass, tempRemote)
        }
    }
    return moveError
}

private fun copyTempToLocalSafely(tempFile: File, finalPath: String): Boolean {
    val partPath = "$finalPath.part"
    val copied = ShellHelper.runBlocking("cp \"${tempFile.absolutePath}\" \"$partPath\"")
    if (!copied.isSuccess) return false
    return ShellHelper.runBlocking("mv \"$partPath\" \"$finalPath\"").isSuccess
}

/**
 * 读取压缩包顶层条目名称（去掉尾部 '/'），
 * 用于判断压缩包是否已含 <slot>/ 顶层目录。zip 用 ZipFile 读取，tar.gz 用 tar -tzf 读取。
 */
private fun archiveTopLevelNames(tempFile: File, isZip: Boolean): Set<String> {
    val tops = mutableSetOf<String>()
    if (isZip) {
        try {
            java.util.zip.ZipFile(tempFile).use { z ->
                val it = z.entries()
                while (it.hasMoreElements()) {
                    val name = it.nextElement().name.trimEnd('/')
                    if (name.isNotBlank()) tops.add(name.substringBefore('/'))
                }
            }
        } catch (_: Exception) {}
    } else {
        val lines = com.coc.zkqcode.jar.code.ShellHelper.runBlocking(
            "tar -tzf \"${tempFile.absolutePath}\" 2>/dev/null | head -200"
        ).out
        for (line in lines) {
            val name = line.trimEnd('/')
            if (name.isNotBlank()) tops.add(name.substringBefore('/'))
        }
    }
    return tops
}

private suspend fun copyLocalToRemoteNow(
    context: android.content.Context,
    item: FileItem,
    localPath: String,
    remotePath: String,
    targetName: String = item.name,
    overwrite: Boolean = false,
    onLog: (String) -> Unit = {}
): Boolean {
    onLog("开始上传: $targetName")
    val inner = localPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
    val localFull = if (inner.isBlank()) "$LOCAL_ROOT/${item.name}" else "$LOCAL_ROOT/$inner/${item.name}"
    val remoteInner = remotePath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
    val remoteFull = if (remoteInner.isBlank()) "$REMOTE_WEBDAV_ROOT/$targetName" else "$REMOTE_WEBDAV_ROOT/$remoteInner/$targetName"
    val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
    val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
    val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
    return if (item.isDirectory) {
        if (!ShellHelper.runBlocking("test -d \"$localFull\"").isSuccess) { onLog("本地文件夹不存在"); false }
        else {
            val stageDir = File(context.filesDir, "webdav_stage/copy")
            stageDir.mkdirs()
            val zipFile = File(stageDir, "$targetName.zip")
            zipFile.delete()
            val stageSource = File(stageDir, targetName)
            stageSource.deleteRecursively()
            onLog("正在复制到临时目录: $localFull")
            val copiedToStage = ShellHelper.runBlocking("rm -rf \"${stageSource.absolutePath}\"; cp -r \"$localFull\" \"${stageSource.absolutePath}\"; chmod -R 777 \"${stageSource.absolutePath}\"")
            val javaZipped = withContext(Dispatchers.IO) { zipDirectory(stageSource, zipFile) }
            val zipped = copiedToStage.isSuccess && stageSource.isDirectory && javaZipped
            withContext(Dispatchers.IO) { stageSource.deleteRecursively() }
            if (!zipped) {
                onLog("压缩失败: $localFull")
                false
            } else {
                onLog("压缩完成: ${zipFile.absolutePath}")
                onLog("正在上传: $remoteFull.zip")
                val error = uploadRemoteSafely(url, user, pass, "$remoteFull.zip", zipFile, overwrite)
                withContext(Dispatchers.IO) { zipFile.delete() }
                if (error == null) {
                    onLog("上传完成: $remoteFull.zip")
                    true
                } else {
                    onLog("上传失败: $remoteFull.zip ($error)")
                    false
                }
            }
        }
    } else {
        if (!ShellHelper.runBlocking("test -f \"$localFull\"").isSuccess) { onLog("本地文件不存在"); false }
        else {
            val tempFile = File(context.filesDir, "webdav_stage/copy/$targetName")
            tempFile.parentFile?.mkdirs()
            tempFile.delete()
            val copied = ShellHelper.runBlocking("cp \"$localFull\" \"${tempFile.absolutePath}\"; chmod 777 \"${tempFile.absolutePath}\"")
            if (!copied.isSuccess) {
                onLog("复制本地文件失败: $localFull")
                false
            } else {
                onLog("正在上传: $remoteFull")
                val error = uploadRemoteSafely(url, user, pass, remoteFull, tempFile, overwrite)
                withContext(Dispatchers.IO) { tempFile.delete() }
                if (error == null) {
                    onLog("上传完成: $remoteFull")
                    true
                } else {
                    onLog("上传失败: $remoteFull ($error)")
                    false
                }
            }
        }
    }
}

private fun copyLocalToRemote(
    context: android.content.Context,
    item: FileItem,
    localPath: String,
    remotePath: String,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onLog: (String) -> Unit = {}
) {
    scope.launch { copyLocalToRemoteNow(context, item, localPath, remotePath, onLog = onLog) }
}

private suspend fun copyRemoteToLocalNow(
    context: android.content.Context,
    item: FileItem,
    remotePath: String,
    localPath: String,
    targetName: String = item.name,
    onLog: (String) -> Unit = {}
): Boolean {
    onLog("开始下载: $targetName")
    val remoteInner = remotePath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
    val remoteFull = if (remoteInner.isBlank()) "$REMOTE_WEBDAV_ROOT/${item.name}" else "$REMOTE_WEBDAV_ROOT/$remoteInner/${item.name}"
    val localInner = localPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
    val localFull = if (localInner.isBlank()) "$LOCAL_ROOT/$targetName" else "$LOCAL_ROOT/$localInner/$targetName"
    val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
    val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
    val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
    return if (item.isDirectory) {
        onLog("读取远程目录: $remoteFull")
        val result = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listFiles(url, user, pass, remoteFull) }
        @Suppress("UNCHECKED_CAST")
        val files = if (result is List<*>) result as List<String> else emptyList()
        if (files.isEmpty()) {
            onLog("远程目录为空: $remoteFull")
            false
        }
        else {
            var suc = 0
            var fail = 0
            val localDir = File(localFull)
            withContext(Dispatchers.IO) { ShellHelper.runBlockingNoOutput("mkdir -p \"${localDir.absolutePath}\"") }
            for (fileName in files) {
                onLog("下载文件: $remoteFull/$fileName")
                val tempFile = withContext(Dispatchers.IO) { File(context.filesDir, "webdav_dl_temp/$fileName") }
                tempFile.parentFile?.mkdirs()
                val dlErr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.downloadToFile(url, user, pass, "$remoteFull/$fileName", tempFile) }
                if (dlErr == null) {
                    if (fileName.endsWith(".zip") || fileName.endsWith(".tar.gz")) {
                        val isZip = fileName.endsWith(".zip")
                        val slotName = fileName.removeSuffix(".zip").removeSuffix(".tar.gz")
                        val topNames = archiveTopLevelNames(tempFile, isZip)
                        val hasWrapper = topNames.isNotEmpty() && topNames == setOf(slotName)
                        val extractDir = if (hasWrapper) localDir.absolutePath else "${localDir.absolutePath}/$slotName"
                        if (!hasWrapper) ShellHelper.runBlockingNoOutput("mkdir -p \"$extractDir\"")
                        val extractCmd = if (isZip) ShellHelper.runBlocking("unzip -o \"${tempFile.absolutePath}\" -d \"$extractDir\"") else ShellHelper.runBlocking("tar -xzf \"${tempFile.absolutePath}\" -C \"$extractDir\"")
                        withContext(Dispatchers.IO) { tempFile.delete() }
                        if (extractCmd.isSuccess) suc++ else fail++
                    } else {
                        val copied = copyTempToLocalSafely(tempFile, "${localDir.absolutePath}/$fileName")
                        withContext(Dispatchers.IO) { tempFile.delete() }
                        if (copied) {
                            suc++
                            if (fileName == LOCAL_CONFIG_FILE) {
                                val count = com.coc.zkqcode.jar.ui.schema.ConfigImporter.importFromFile("${localDir.absolutePath}/$fileName")
                            }
                        } else fail++
                    }
                } else fail++
            }
            onLog("目录下载完成: $suc 成功, $fail 失败")
            fail == 0
        }
    } else {
        onLog("正在下载: $remoteFull")
        val tempFile = withContext(Dispatchers.IO) { File(context.filesDir, "webdav_dl_temp/${item.name}") }
        tempFile.parentFile?.mkdirs()
        val dlErr = withContext(Dispatchers.IO) { com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.downloadToFile(url, user, pass, remoteFull, tempFile) }
        if (dlErr == null) {
            val localParent = File(localFull).parentFile?.absolutePath ?: LOCAL_ROOT
            val ok = if (item.name.endsWith(".zip") || item.name.endsWith(".tar.gz")) {
                val isZip = item.name.endsWith(".zip")
                val slotName = targetName.removeSuffix(".zip").removeSuffix(".tar.gz")
                val topNames = archiveTopLevelNames(tempFile, isZip)
                val hasWrapper = topNames.isNotEmpty() && topNames == setOf(slotName)
                val extractDir = if (hasWrapper) localParent else "$localParent/$slotName"
                if (!hasWrapper) ShellHelper.runBlockingNoOutput("mkdir -p \"$extractDir\"")
                val extractCmd = if (isZip) ShellHelper.runBlocking("unzip -o \"${tempFile.absolutePath}\" -d \"$extractDir\"") else ShellHelper.runBlocking("tar -xzf \"${tempFile.absolutePath}\" -C \"$extractDir\"")
                withContext(Dispatchers.IO) { tempFile.delete() }
                if (extractCmd.isSuccess) {
                    onLog("解压成功: $extractDir"); true
                } else { onLog("解压失败"); false }
            } else {
                ShellHelper.runBlockingNoOutput("mkdir -p \"$localParent\"")
                val copied = copyTempToLocalSafely(tempFile, localFull)
                withContext(Dispatchers.IO) { tempFile.delete() }
                if (copied) {
                    if (item.name == LOCAL_CONFIG_FILE) {
                        val count = com.coc.zkqcode.jar.ui.schema.ConfigImporter.importFromFile(localFull)
                    } else {
                    }
                    true
                } else {
                    false
                }
            }
            ok
        } else { onLog("下载失败: $dlErr"); false }
    }
}

private fun copyRemoteToLocal(
    context: android.content.Context,
    item: FileItem,
    remotePath: String,
    localPath: String,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onLog: (String) -> Unit = {}
) {
    scope.launch { copyRemoteToLocalNow(context, item, remotePath, localPath, onLog = onLog) }
}

private fun deleteSelectedItems(
    panel: Int,
    names: Set<String>,
    leftState: PanelState,
    rightState: PanelState,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onStateUpdate: (Int, PanelState) -> Unit,
    onLog: (String) -> Unit = {},
    isStopRequested: () -> Boolean = { false },
    onDone: () -> Unit = {}
) {
    scope.launch {
        val state = if (panel == 0) leftState else rightState
        val items = state.items.filter { it.name in names }
        var success = 0
        var failed = 0
        for (item in items) {
            if (isStopRequested()) {
                onLog("删除已停止，等待当前文件完成")
                break
            }
            if (deleteItem(panel, item, leftState, rightState, onLog)) success++ else failed++
        }
        val currentState = if (panel == 0) leftState else rightState
        val refreshed = if (panel == 0) loadLocalItems(currentState.currentPath, sdPath) else loadRemoteItems(currentState.currentPath)
        onStateUpdate(panel, currentState.copy(items = refreshed))
        val stopped = isStopRequested()
        onLog(if (stopped) "删除已停止（$success 成功，$failed 失败）"
              else if (failed == 0) "删除完成（$success 项）"
              else "删除完成（$success 成功，$failed 失败）")
        onDone()
    }
}

private fun formatOperationPath(panel: Int, statePath: String, name: String): String {
    val label = if (panel == 0) "本地" else "远程"
    val inner = statePath.removePrefix("/").removePrefix(if (panel == 0) LOCAL_STORAGE_LABEL else REMOTE_STORAGE_LABEL).removePrefix("/")
    return if (inner.isBlank()) "$label/$name" else "$label/$inner/$name"
}

private suspend fun targetExists(panel: Int, destinationPath: String, name: String): Boolean = withContext(Dispatchers.IO) {
    if (panel == 0) {
        val inner = destinationPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
        val target = if (inner.isBlank()) "$LOCAL_ROOT/$name" else "$LOCAL_ROOT/$inner/$name"
        ShellHelper.runBlocking("test -e \"$target\"").isSuccess
    } else {
        val inner = destinationPath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
        val parent = if (inner.isBlank()) REMOTE_WEBDAV_ROOT else "$REMOTE_WEBDAV_ROOT/$inner"
        val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
        val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
        val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
        val result = com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listEntries(url, user, pass, parent)
        if (result is List<*>) result.filterIsInstance<com.coc.zkqcode.jar.code.universal.network.WebDAVEntry>().any { it.name == name } else false
    }
}

private suspend fun targetNames(panel: Int, destinationPath: String): List<String> = withContext(Dispatchers.IO) {
    if (panel == 0) {
        val inner = destinationPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
        val parent = if (inner.isBlank()) "$LOCAL_ROOT" else "$LOCAL_ROOT/$inner"
        ShellHelper.runBlockingOutput("ls -a \"$parent\" 2>/dev/null")
            .map { it.trim() }
            .filter { it.isNotBlank() && it != "." && it != ".." }
    } else {
        val inner = destinationPath.removePrefix("/").removePrefix(REMOTE_STORAGE_LABEL).removePrefix("/")
        val parent = if (inner.isBlank()) REMOTE_WEBDAV_ROOT else "$REMOTE_WEBDAV_ROOT/$inner"
        val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
        val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
        val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
        val result = com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.listEntries(url, user, pass, parent)
        if (result is List<*>) result.filterIsInstance<com.coc.zkqcode.jar.code.universal.network.WebDAVEntry>().map { it.name } else emptyList()
    }
}

private suspend fun generateUniqueTargetName(panel: Int, destinationPath: String, name: String): String {
    val existing = targetNames(panel, destinationPath)
    val numbers = existing.mapNotNull { Regex("^(\\d+)(.*)$").find(it)?.groupValues?.get(1)?.toIntOrNull() }
    val suffix = Regex("^(\\d+)(.*)$").find(name)?.groupValues?.get(2)
        ?: name.substringAfterLast('.', missingDelimiterValue = "").let { if (it.isEmpty()) "" else ".$it" }
    var next = maxOf(existing.size, numbers.maxOrNull() ?: 0) + 1
    var candidate = "$next$suffix"
    while (candidate in existing) {
        next++
        candidate = "$next$suffix"
    }
    return candidate
}

private fun copySelectedToOtherPanel(
    context: android.content.Context,
    panel: Int,
    names: Set<String>,
    move: Boolean,
    leftState: PanelState,
    rightState: PanelState,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onStateUpdate: (Int, PanelState) -> Unit,
    onLog: (String) -> Unit = {},
    isStopRequested: () -> Boolean = { false },
    onResolveConflict: suspend (String, String, String) -> ConflictMode? = { _, _, _ -> null },
    onDone: () -> Unit = {}
) {
    scope.launch {
        val srcState = if (panel == 0) leftState else rightState
        val dstState = if (panel == 0) rightState else leftState
        val dstPanel = if (panel == 0) 1 else 0

        val tasks = mutableListOf<Pair<FileItem, FileItem>>()
        for (item in srcState.items) {
            if (item.name !in names) continue
            if (panel == 0 && item.isDirectory) {
                val storageInner = srcState.currentPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
                val isArchiveRoot = storageInner.isBlank()
                if (isArchiveRoot) {
                    val inner = storageInner
                    val localFull = if (inner.isBlank()) "$LOCAL_ROOT/${item.name}" else "$LOCAL_ROOT/$inner/${item.name}"
                    val childrenLines = ShellHelper.runBlockingOutput("ls -d \"$localFull\"/*/ 2>/dev/null")
                    val children = childrenLines.mapNotNull { line ->
                        val name = line.trim().trimEnd('/').substringAfterLast("/")
                        if (name.isNotBlank()) FileItem(name, true) else null
                    }.sortedBy { it.name }
                    if (children.isNotEmpty()) {
                        children.forEach { tasks.add(item to FileItem(it.name, true)) }
                    } else {
                        tasks.add(item to item)
                    }
                } else {
                    tasks.add(item to item)
                }
            } else {
                tasks.add(item to item)
            }
        }

        var success = 0
        var failed = 0
        val moveResults = mutableMapOf<FileItem, Boolean>()
        var autoMode: ConflictMode? = null
        for ((sourceItem, copyItem) in tasks) {
            if (isStopRequested()) {
                onLog("操作已停止，等待当前文件完成")
                break
            }
            var targetName = copyItem.name
            var overwriteTarget = false
            val expandedLocalRoot = panel == 0 && sourceItem != copyItem
            val copyLocalPath = if (expandedLocalRoot) buildPath(srcState.currentPath, sourceItem.name) else srcState.currentPath
            val destBasePath = if (expandedLocalRoot) buildPath(dstState.currentPath, sourceItem.name) else dstState.currentPath
            val remoteName = if (panel == 0 && copyItem.isDirectory) "${copyItem.name}.zip" else copyItem.name
            // 远程存档压缩包（N.zip / N.tar.gz）下载到本地时，本地若已有 N/ 槽位目录也算同名冲突
            val remoteSlot = if (panel == 1 && !copyItem.isDirectory &&
                (copyItem.name.endsWith(".zip") || copyItem.name.endsWith(".tar.gz")))
                copyItem.name.removeSuffix(".zip").removeSuffix(".tar.gz")
            else null
            val literalConflict = targetExists(dstPanel, destBasePath, remoteName)
            val slotConflict = remoteSlot != null && targetExists(dstPanel, destBasePath, remoteSlot)
            if (literalConflict || slotConflict) {
                val sourceDisplay = formatOperationPath(panel, copyLocalPath, copyItem.name)
                // slotConflict already implies remoteSlot != null
                val destDisplay = formatOperationPath(dstPanel, destBasePath, if (slotConflict) remoteSlot else remoteName)
                val mode = autoMode ?: onResolveConflict(sourceDisplay, destDisplay, if (move) "移动" else "复制")
                when (mode) {
                    null, ConflictMode.CANCEL -> {
                        onLog("操作已取消: $sourceDisplay → $destDisplay")
                        onDone()
                        return@launch
                    }
                    ConflictMode.OVERWRITE, ConflictMode.OVERWRITE_ALL -> {
                        overwriteTarget = true
                        if (mode == ConflictMode.OVERWRITE_ALL) autoMode = ConflictMode.OVERWRITE_ALL
                    }
                    ConflictMode.INCREMENT, ConflictMode.INCREMENT_ALL -> {
                        val generated = generateUniqueTargetName(dstPanel, destBasePath, remoteName)
                        targetName = if (panel == 0 && copyItem.isDirectory) generated.removeSuffix(".zip") else generated
                        if (mode == ConflictMode.INCREMENT_ALL) autoMode = ConflictMode.INCREMENT_ALL
                    }
                }
            }
            val copied = if (panel == 0) copyLocalToRemoteNow(context, copyItem, copyLocalPath, destBasePath, targetName, overwriteTarget, onLog)
                         else copyRemoteToLocalNow(context, copyItem, srcState.currentPath, dstState.currentPath, targetName, onLog)
            if (copied) {
                success++
                if (move) moveResults[sourceItem] = (moveResults[sourceItem] ?: true) && copied
            } else {
                failed++
                if (move) moveResults[sourceItem] = false
            }
        }

        if (move && !isStopRequested()) {
            for ((sourceItem, allOk) in moveResults) {
                if (allOk) deleteItem(panel, sourceItem, leftState, rightState, onLog)
            }
        } else if (move && isStopRequested()) {
            onLog("已停止，未删除未确认完成的源文件")
        }

        val leftItems = loadLocalItems(leftState.currentPath, sdPath)
        val rightItems = loadRemoteItems(rightState.currentPath)
        onStateUpdate(0, leftState.copy(items = leftItems))
        onStateUpdate(1, rightState.copy(items = rightItems))
        val action = if (move) "移动" else "复制"
        val stopped = isStopRequested()
        onLog(if (stopped) "${action}已停止（$success 成功，$failed 失败）"
              else "${action}完成（$success 成功，$failed 失败）")
        onDone()
    }
}

// ── Extract archive ────────────────────────────────────────────────────

private fun extractArchive(
    context: android.content.Context,
    item: FileItem,
    panel: Int,
    panelState: PanelState,
    sdPath: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onStateUpdate: (Int, PanelState) -> Unit,
    onLog: (String) -> Unit = {}
) {
    scope.launch {
        onLog("开始解压: ${item.name}")

        val inner = panelState.currentPath.removePrefix("/").removePrefix(
            if (panel == 0) LOCAL_STORAGE_LABEL else REMOTE_STORAGE_LABEL
        ).removePrefix("/")
        val base = if (panel == 0) LOCAL_ROOT else REMOTE_WEBDAV_ROOT
        val fullBase = if (inner.isBlank()) base else "$base/$inner"

        if (panel == 0) {
            // Local file - extract directly to same directory
            val archivePath = "$fullBase/${item.name}"
            val cmd = if (item.name.endsWith(".zip")) {
                ShellHelper.runBlocking("unzip -o \"$archivePath\" -d \"$fullBase\"")
            } else {
                ShellHelper.runBlocking("tar -xzf \"$archivePath\" -C \"$fullBase\"")
            }
            if (cmd.isSuccess) onLog("解压成功")
            else onLog("解压失败")
        } else {
            // Remote file - download to temp, extract to local
            val url = getConfig(GLOBAL_SETTINGS_WEBDAV_URL)
            val user = getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)
            val pass = getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)
            val remotePath = "$fullBase/${item.name}"
            val tempFile = File(context.filesDir, "webdav_dl_temp/${item.name}")
            tempFile.parentFile?.mkdirs()
            val dlErr = withContext(Dispatchers.IO) {
                com.coc.zkqcode.jar.code.universal.network.WebDAVHelper.downloadToFile(url, user, pass, remotePath, tempFile)
            }
            if (dlErr != null) {
                onLog("下载失败: $dlErr")
                return@launch
            }

            // Extract directly to local directory
            val localInner = panelState.currentPath.removePrefix("/").removePrefix(LOCAL_STORAGE_LABEL).removePrefix("/")
            val localBase = if (localInner.isBlank()) LOCAL_ROOT else "$LOCAL_ROOT/$localInner"
            val extractCmd = if (item.name.endsWith(".zip")) {
                ShellHelper.runBlocking("unzip -o \"${tempFile.absolutePath}\" -d \"$localBase\"")
            } else {
                ShellHelper.runBlocking("tar -xzf \"${tempFile.absolutePath}\" -C \"$localBase\"")
            }
            withContext(Dispatchers.IO) { tempFile.delete() }
            if (extractCmd.isSuccess) onLog("解压成功")
            else onLog("解压失败")
        }

        // Refresh panel
        val items = if (panel == 0) loadLocalItems(panelState.currentPath, sdPath) else loadRemoteItems(panelState.currentPath)
        onStateUpdate(panel, panelState.copy(items = items))
    }
}

// ── Filter & Sort ──────────────────────────────────────────────────────

private fun getFilteredItems(items: List<FileItem>, query: String, sortMode: Int): List<FileItem> {
    val filtered = if (query.isBlank()) items
    else items.filter { it.name.contains(query, ignoreCase = true) }
    return when (sortMode) {
        1 -> filtered.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenByDescending { it.name })
        2 -> filtered.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenBy { it.size })
        3 -> filtered.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenByDescending { it.size })
        else -> filtered.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenBy { it.name })
    }
}

// ── Utility ───────────────────────────────────────────────────────────

private fun formatSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val kb = bytes / 1024.0
    if (kb < 1024) return "%.1f KB".format(kb)
    val mb = kb / 1024.0
    if (mb < 1024) return "%.1f MB".format(mb)
    val gb = mb / 1024.0
    return "%.1f GB".format(gb)
}

// ── WebDAV Config Dialog ──────────────────────────────────────────────

@Composable
private fun WebDavConfigDialog(onDismiss: () -> Unit, onConfigured: () -> Unit) {
    val context = LocalContext.current
    val urlState = remember { mutableStateOf(getConfig(GLOBAL_SETTINGS_WEBDAV_URL)) }
    val userState = remember { mutableStateOf(getConfig(GLOBAL_SETTINGS_WEBDAV_USERNAME)) }
    val passState = remember { mutableStateOf(getConfig(GLOBAL_SETTINGS_WEBDAV_PASSWORD)) }
    var pasteDialogVisible by remember { mutableStateOf(false) }
    var pasteText by remember { mutableStateOf("") }

    fun combinedConfig(): String = listOf(
        urlState.value.trim(),
        userState.value.trim(),
        passState.value
    ).joinToString(WEBDAV_CONFIG_DELIMITER)

    fun applyConfigText(text: String) {
        val parts = text.split(WEBDAV_CONFIG_DELIMITER)
        if (parts.size < 3) {
            ShowMessage("配置格式错误", isChecking = false)
            return
        }
        urlState.value = parts[0].trim()
        userState.value = parts[1].trim()
        passState.value = parts.drop(2).joinToString(WEBDAV_CONFIG_DELIMITER).trim()
        ShowMessage(Strings.webdavPastedMsg, isChecking = false)
    }

    com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.webdavConfigure) },
        text = {
            Column {
                ConfigInputRow("URL", urlState.value) { urlState.value = it }
                ConfigInputRow("用户名", userState.value) { userState.value = it }
                ConfigInputRow("密码", passState.value) { passState.value = it }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "复制",
                        color = WebDavAccent,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                val clip = android.content.ClipData.newPlainText("WebDAV", combinedConfig())
                                context.getSystemService(android.content.ClipboardManager::class.java)?.setPrimaryClip(clip)
                                ShowMessage(Strings.webdavCopiedMsg, isChecking = false)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                    Text(
                        text = "粘贴",
                        color = WebDavAccent,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                pasteText = ""
                                pasteDialogVisible = true
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row {
                androidx.compose.material3.TextButton(onClick = onDismiss) { Text(Strings.cancel) }
                androidx.compose.material3.TextButton(onClick = {
                    val url = urlState.value.trim()
                    val user = userState.value.trim()
                    val pass = passState.value.trim()
                    if (url.isBlank()) { ShowMessage(Strings.webdavUrlEmpty, isChecking = false); return@TextButton }
                    if (user.isBlank() || pass.isBlank()) { ShowMessage(Strings.webdavCredentialEmpty, isChecking = false); return@TextButton }
                    GlobalVars.configStates[GLOBAL_SETTINGS_WEBDAV_URL]?.value = url
                    GlobalVars.configStates[GLOBAL_SETTINGS_WEBDAV_USERNAME]?.value = user
                    GlobalVars.configStates[GLOBAL_SETTINGS_WEBDAV_PASSWORD]?.value = pass
                    onConfigured()
                }) { Text(Strings.confirm) }
            }
        }
    )

    if (pasteDialogVisible) {
        com.coc.zkqcode.jar.ui.components.CustomAlertDialog(
            onDismissRequest = { pasteDialogVisible = false },
            title = { Text("粘贴 WebDAV 配置") },
            text = {
                Column {
                    if (pasteText.isEmpty()) {
                        Text("请粘贴或输入 3 项配置，使用 $WEBDAV_CONFIG_DELIMITER 分隔", style = MaterialTheme.typography.labelSmall, color = WebDavMuted)
                    }
                    BasicTextField(
                        value = pasteText,
                        onValueChange = { pasteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                            .background(WebDavSurface, RoundedCornerShape(4.dp))
                            .border(1.dp, WebDavBorder, RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Row {
                    androidx.compose.material3.TextButton(onClick = { pasteDialogVisible = false }) { Text(Strings.cancel) }
                    androidx.compose.material3.TextButton(onClick = {
                        pasteDialogVisible = false
                        applyConfigText(pasteText)
                    }) { Text(Strings.confirm) }
                }
            }
        )
    }

}

@Composable
private fun ConfigInputRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelMedium,
            color = WebDavText,
            modifier = Modifier.width(60.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .background(WebDavSurface, RoundedCornerShape(4.dp))
                .border(1.dp, WebDavBorder, RoundedCornerShape(4.dp))
                .padding(8.dp),
            singleLine = true
        )
    }
}

private fun zipDirectory(sourceDir: File, targetZip: File): Boolean {
    return try {
        targetZip.parentFile?.mkdirs()
        ZipOutputStream(FileOutputStream(targetZip)).use { zip ->
            sourceDir.walkTopDown().forEach { file ->
                val entryName = sourceDir.toPath().relativize(file.toPath()).toString().replace('\\', '/')
                if (entryName.isBlank()) return@forEach
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
        true
    } catch (_: Exception) { false }
}
