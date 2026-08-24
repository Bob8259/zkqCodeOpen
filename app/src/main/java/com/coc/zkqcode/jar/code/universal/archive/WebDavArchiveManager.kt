package com.coc.zkqcode.jar.code.universal.archive

import android.content.Context
import android.os.Environment
import android.util.Log
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.code.universal.network.RemoteFile
import com.coc.zkqcode.jar.code.universal.network.WebDavClient
import com.coc.zkqcode.jar.code.universal.network.WebDavResult
import java.io.File

private const val TAG = "WebDAV"

// ── Model types ───────────────────────────────────────────────────────────────

enum class LocationType { LOCAL, REMOTE }

data class ClipboardItem(
    val name: String,
    val isDirectory: Boolean,
    val sourceType: LocationType,
    val sourcePath: String        // "local" or remote device name
)

data class ClipboardState(
    val items: List<ClipboardItem>,
    val isCut: Boolean
)

enum class ConflictAction { OVERWRITE, INCREMENT, SKIP, CANCEL }

data class PasteReport(
    val succeeded: List<String>,
    val failed: List<String>
)

// ── Archive manager ───────────────────────────────────────────────────────────

class WebDavArchiveManager(private val context: Context) {

    var client: WebDavClient? = null
    var connectedUrl: String = ""
    var clipboard: ClipboardState? = null

    private var connectUrl: String = ""
    private var connectUsername: String = ""
    private var connectPassword: String = ""
    private var refreshTimer: java.util.Timer? = null

    private val externalBase: String
        get() = "${Environment.getExternalStorageDirectory().path}/zkqFiles"

    val localSaveDir: File
        get() = File(externalBase, "zkqGlobalGameSave")

    // ── Connection ───────────────────────────────────────────────────────────

    private val REFRESH_INTERVAL_MS = 12L * 60 * 60 * 1000 // 12 小时

    @Synchronized
    fun connect(url: String, username: String, password: String): WebDavResult<Unit> {
        val c = WebDavClient(url, username, password)
        // Test connection by listing root
        val result = c.listDirectories("zkqFiles")
        return when (result) {
            is WebDavResult.Success -> {
                client = c
                connectedUrl = url
                connectUrl = url
                connectUsername = username
                connectPassword = password
                startRefreshTimer()
                WebDavResult.Success(Unit)
            }
            is WebDavResult.Error -> WebDavResult.Error(result.code, result.message, result.type)
            is WebDavResult.Failure -> WebDavResult.Failure(result.exception)
        }
    }

    @Synchronized
    fun disconnect() {
        stopRefreshTimer()
        client = null
        connectedUrl = ""
        clipboard = null
    }

    /** 启动定时刷新连接，防止 WebDAV 会话过期（参考 OpenList cron 设计） */
    private fun startRefreshTimer() {
        stopRefreshTimer()
        refreshTimer = java.util.Timer("WebDAV-refresh", true).apply {
            schedule(object : java.util.TimerTask() {
                override fun run() {
                    val u = connectUrl; val usr = connectUsername; val pw = connectPassword
                    if (u.isNotBlank() && usr.isNotBlank() && pw.isNotBlank()) {
                        Log.v(TAG, "Scheduled connection refresh...")
                        val c = WebDavClient(u, usr, pw)
                        if (c.listDirectories("zkqFiles") is WebDavResult.Success) {
                            synchronized(this@WebDavArchiveManager) { client = c }
                            Log.v(TAG, "Connection refreshed successfully")
                        } else {
                            Log.w(TAG, "Connection refresh failed, will retry next cycle")
                        }
                    }
                }
            }, REFRESH_INTERVAL_MS, REFRESH_INTERVAL_MS)
        }
    }

    private fun stopRefreshTimer() {
        refreshTimer?.cancel()
        refreshTimer = null
    }

    fun deleteLocalArchive(number: Int): Boolean {
        val dir = File(localSaveDir, number.toString())
        return ShellHelper.runBlocking("rm -rf \"${dir.absolutePath}\" 2>/dev/null").isSuccess
    }

    // ── Local browsing (all via root shell) ─────────────────────────────────

    fun listLocalArchives(): List<Int> {
        val out = ShellHelper.runBlockingOutput("ls -d \"${localSaveDir.absolutePath}\"/*/ 2>/dev/null")
        return out.mapNotNull { line ->
            line.trimEnd('/').substringAfterLast("/").toIntOrNull()
        }.sorted()
    }

    /**
     * Returns the total size of a local archive directory in bytes (via shell du).
     */
    fun getLocalArchiveSize(number: Int): Long {
        val out = ShellHelper.runBlockingOutput("du -sb \"${File(localSaveDir, number.toString()).absolutePath}\" 2>/dev/null")
        val first = out.firstOrNull() ?: return 0L
        return first.split("\t").firstOrNull()?.toLongOrNull() ?: 0L
    }

    /**
     * Returns the last-modified time (epoch seconds) of a local archive directory (via shell stat).
     */
    fun getLocalArchiveModified(number: Int): Long {
        val out = ShellHelper.runBlockingOutput("stat -c %Y \"${File(localSaveDir, number.toString()).absolutePath}\" 2>/dev/null")
        return out.firstOrNull()?.trim()?.toLongOrNull() ?: 0L
    }

    /**
     * Returns the list of files inside a local archive directory (via shell ls).
     */
    fun listLocalArchiveFiles(number: Int): List<FileInfo> {
        val dir = File(localSaveDir, number.toString())
        val out = ShellHelper.runBlockingOutput("ls -la \"${dir.absolutePath}\" 2>/dev/null | tail -n +2")
        return out.mapNotNull { line ->
            val parts = line.split("\\s+".toRegex())
            if (parts.size < 9) return@mapNotNull null
            val isDir = parts[0].startsWith("d")
            val size = parts[4].toLongOrNull() ?: 0L
            val name = parts.drop(8).joinToString(" ")
            if (name == "." || name == "..") null else FileInfo(name, isDir, size, 0L)
        }
    }

    // ── Remote browsing ─────────────────────────────────────────────────────

    fun listRemoteDevices(): WebDavResult<List<String>> {
        val c = client ?: return WebDavResult.Error(0, "Not connected")
        return when (val r = c.listDirectories("zkqFiles")) {
            is WebDavResult.Success -> WebDavResult.Success(r.data.sorted())
            is WebDavResult.Error -> WebDavResult.Error(r.code, r.message, r.type)
            is WebDavResult.Failure -> WebDavResult.Failure(r.exception)
        }
    }

    fun listRemoteArchives(deviceName: String): WebDavResult<List<RemoteArchive>> {
        val c = client ?: return WebDavResult.Error(0, "Not connected")
        val path = "zkqFiles/$deviceName/zkqGlobalGameSave"
        val result = c.listDirectory(path)
        return when (result) {
            is WebDavResult.Success -> {
                val archives = result.data
                    .filter { !it.isDirectory }
                    .mapNotNull { f ->
                        val number = parseArchiveNumber(f.name)
                        if (number != null) {
                            RemoteArchive(
                                name = f.name,
                                number = number,
                                isZip = f.name.endsWith(".zip"),
                                size = f.size,
                                lastModified = f.lastModified
                            )
                        } else null
                    }
                    .sortedBy { it.number }   // 按存档编号 1,2,3... 排列
                WebDavResult.Success(archives)
            }
            is WebDavResult.Error -> WebDavResult.Error(result.code, result.message, result.type)
            is WebDavResult.Failure -> WebDavResult.Failure(result.exception)
        }
    }

    // ── Remote folder management ──────────────────────────────────────────────

    fun createDeviceFolder(deviceName: String): WebDavResult<Unit> {
        val c = client ?: return WebDavResult.Error(0, "Not connected")
        return c.createDirectories("zkqFiles/$deviceName/zkqGlobalGameSave")
    }

    fun renameDeviceFolder(oldName: String, newName: String): WebDavResult<Unit> {
        val c = client ?: return WebDavResult.Error(0, "Not connected")
        return c.move("zkqFiles/$oldName", "zkqFiles/$newName")
    }

    fun deleteDeviceFolder(deviceName: String): WebDavResult<Unit> {
        val c = client ?: return WebDavResult.Error(0, "Not connected")
        return c.delete("zkqFiles/$deviceName")
    }

    fun deleteRemoteArchive(deviceName: String, archiveName: String): WebDavResult<Unit> {
        val c = client ?: return WebDavResult.Error(0, "Not connected")
        return c.delete("zkqFiles/$deviceName/zkqGlobalGameSave/$archiveName")
    }

    // ── Clipboard operations ─────────────────────────────────────────────────

    fun copyToClipboard(items: List<ClipboardItem>, isCut: Boolean) {
        clipboard = ClipboardState(items, isCut)
    }

    fun clearClipboard() {
        clipboard = null
    }

    /**
     * Paste items from clipboard to the target location.
     * @param targetType where to paste
     * @param targetName target device name (for REMOTE) or empty (for LOCAL root)
     * @param onConflict called when a naming conflict occurs; return the resolution
     * @param onProgress called with status message
     */
    suspend fun paste(
        targetType: LocationType,
        targetName: String,
        conflictAction: ConflictAction = ConflictAction.INCREMENT,
        onProgress: (String) -> Unit
    ): PasteReport {
        val cb = clipboard ?: return PasteReport(emptyList(), emptyList())
        val c = client

        val succeeded = mutableListOf<String>()
        val failed = mutableListOf<String>()

        Log.v(TAG, "paste: ${cb.items.size} items, target=$targetType/$targetName, conflict=$conflictAction, isCut=${cb.isCut}")

        for (item in cb.items) {
            onProgress("处理: ${item.name}")
            Log.v(TAG, "  item: name=${item.name}, srcType=${item.sourceType}, srcPath=${item.sourcePath}, isDir=${item.isDirectory}")

            // Can't paste to the same location with cut
            if (cb.isCut && item.sourceType == targetType && item.sourcePath == targetName) {
                Log.v(TAG, "  same location cut: auto-rename")
                val newName = generateNewName(item.name, targetType, targetName)
                val pasteOk = performPasteOne(item, targetType, targetName, newName, cb.isCut, conflictAction, onProgress)
                if (pasteOk) succeeded.add(item.name) else failed.add(item.name)
                continue
            }

            // Conflict check
            val exists = if (targetType == LocationType.LOCAL) {
                val localName = if (item.sourceType == LocationType.LOCAL) item.name
                else item.name.removeSuffix(".tar.gz").removeSuffix(".zip")
                localName.toIntOrNull()?.let { File(localSaveDir, it.toString()).exists() } ?: false
            } else {
                c?.let {
                    it.exists("zkqFiles/$targetName/zkqGlobalGameSave/${archiveNameFor(item)}")
                } ?: false
            }
            Log.v(TAG, "  conflict check: exists=$exists, action=$conflictAction")

            var targetItemName = item.name
            if (exists) {
                when (conflictAction) {
                    ConflictAction.CANCEL -> break
                    ConflictAction.INCREMENT -> {
                        targetItemName = if (targetType == LocationType.LOCAL) generateNewLocalName(item.name)
                        else generateNewRemoteName(item.name, targetName, c!!)
                    }
                    ConflictAction.SKIP -> {
                        failed.add(item.name)
                        continue
                    }
                    else -> {} // OVERWRITE: keep same name
                }
            }

            Log.v(TAG, "  executing paste: item=${item.name} -> targetName=${targetItemName}")
            val pasteOk = performPasteOne(item, targetType, targetName, targetItemName, cb.isCut, conflictAction, onProgress)
            if (pasteOk) succeeded.add(item.name) else failed.add(item.name)
            Log.v(TAG, "  paste result: ${if (pasteOk) "OK" else "FAIL"}")
        }

        if (cb.isCut) clipboard = null

        Log.v(TAG, "paste done: ${succeeded.size} ok, ${failed.size} fail")
        return PasteReport(succeeded, failed)
    }

    private suspend fun performPasteOne(
        item: ClipboardItem,
        targetType: LocationType,
        targetName: String,
        targetItemName: String,
        isCut: Boolean,
        conflictAction: ConflictAction,
        onProgress: (String) -> Unit
    ): Boolean {
        val opLabel = when {
            item.sourceType == LocationType.LOCAL && targetType == LocationType.REMOTE -> "LOCAL→REMOTE"
            item.sourceType == LocationType.REMOTE && targetType == LocationType.LOCAL -> "REMOTE→LOCAL"
            item.sourceType == LocationType.LOCAL && targetType == LocationType.LOCAL -> "LOCAL→LOCAL"
            item.sourceType == LocationType.REMOTE && targetType == LocationType.REMOTE -> "REMOTE→REMOTE"
            else -> "UNKNOWN"
        }
        Log.v(TAG, "  $opLabel: ${item.name} -> $targetItemName (cut=$isCut, targetName=$targetName)")

        return try {
            val ok = when {
                // Local → Remote
                item.sourceType == LocationType.LOCAL && targetType == LocationType.REMOTE ->
                    copyLocalToRemote(item, targetName, targetItemName, onProgress)
                // Remote → Local
                item.sourceType == LocationType.REMOTE && targetType == LocationType.LOCAL ->
                    copyRemoteToLocal(item, targetItemName, conflictAction, onProgress)
                // Local → Local
                item.sourceType == LocationType.LOCAL && targetType == LocationType.LOCAL ->
                    copyLocalToLocal(item, targetItemName, onProgress)
                // Remote → Remote
                item.sourceType == LocationType.REMOTE && targetType == LocationType.REMOTE ->
                    copyRemoteToRemote(item, targetName, targetItemName, onProgress)
                else -> false
            }

            if (ok && isCut) deleteSource(item)
            if (!ok) Log.w(TAG, "  $opLabel FAILED")
            ok
        } catch (e: Exception) {
            Log.e(TAG, "  $opLabel exception", e)
            false
        }
    }

    // ── Internal copy operations ──────────────────────────────────────────────

    /**
     * Local archive → Remote: zip and upload.
     * Uses shell to zip since game files on /sdcard/ may not be readable via Java File API.
     */
    private fun copyLocalToRemote(
        item: ClipboardItem,
        targetDevice: String,
        targetName: String,
        onProgress: (String) -> Unit
    ): Boolean {
        val c = client ?: return false.also { Log.w(TAG, "copyLocalToRemote: client is null") }
        val num = item.name.toIntOrNull() ?: return false.also { Log.w(TAG, "copyLocalToRemote: name not a number: ${item.name}") }
        val sourceDir = File(localSaveDir, item.name)
        val srcExists = ShellHelper.runBlocking("test -d \"${sourceDir.absolutePath}\"").isSuccess
        if (!srcExists) return false.also { Log.w(TAG, "copyLocalToRemote: source dir not found: ${sourceDir.absolutePath}") }

        val stageRoot = File(context.filesDir, "webdav_stage/paste")
        val zipFile = File(stageRoot, "${targetName}.zip")
        try {
            stageRoot.mkdirs()
            if (zipFile.exists()) zipFile.delete()

            onProgress("打包 ${item.name}…")
            // Copy game files to staging via root shell + chmod, then Java zip
            val tempRoot = File(stageRoot, "zip_copy")
            val tempDir = File(tempRoot, sourceDir.name)
            val tempParent = tempDir.parentFile ?: return false.also { Log.w(TAG, "  no staging parent dir") }
            val copied = ShellHelper.runBlocking(
                "rm -rf \"${tempRoot.absolutePath}\" && mkdir -p \"${tempParent.absolutePath}\" && " +
                "cp -rf \"${sourceDir.absolutePath}\" \"${tempParent.absolutePath}\" && " +
                "chmod -R 755 \"${tempRoot.absolutePath}\""
            ).isSuccess
            if (!copied) return false.also { Log.w(TAG, "  shell cp failed") }

            // Java zip from staging copy
            try {
                java.util.zip.ZipOutputStream(java.io.FileOutputStream(zipFile)).use { zip ->
                    tempDir.walkTopDown().forEach { file ->
                        val entryName = tempRoot.toPath().relativize(file.toPath())?.toString()?.replace('\\', '/') ?: return@forEach
                        if (file.isDirectory) {
                            val dirName = if (entryName.endsWith('/')) entryName else "$entryName/"
                            zip.putNextEntry(java.util.zip.ZipEntry(dirName)); zip.closeEntry()
                        } else {
                            zip.putNextEntry(java.util.zip.ZipEntry(entryName))
                            file.inputStream().use { input -> input.copyTo(zip) }
                            zip.closeEntry()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "  Java zip failed", e)
                return false
            }
            if (!zipFile.isFile) return false.also { Log.w(TAG, "  zip file not created") }

            onProgress("上传到 ${targetDevice}…")
            val uploadPath = "zkqFiles/$targetDevice/zkqGlobalGameSave/${targetName}.zip"
            Log.v(TAG, "  uploading: $uploadPath (size=${zipFile.length()})")
            val result = c.uploadWithProgress(uploadPath, zipFile) { _, _ -> }
            if (!result.isSuccess) Log.w(TAG, "  upload failed: ${result.errorMessage}, type=${result::class.simpleName}")
            return result.isSuccess
        } finally {
            if (zipFile.exists()) zipFile.delete()
            if (stageRoot.exists()) stageRoot.deleteRecursively()
        }
    }

    /**
     * Remote archive → Local: download and extract via shell.
     * For directory items (device folders), iterates all archives in the device.
     */
    private fun copyRemoteToLocal(
        item: ClipboardItem,
        targetName: String,
        conflictAction: ConflictAction,
        onProgress: (String) -> Unit
    ): Boolean {
        val c = client ?: return false
        val remoteDir = item.sourcePath

        // List remote files to find the archive(s)
        val listResult = c.listDirectory("zkqFiles/$remoteDir/zkqGlobalGameSave")
        if (listResult !is WebDavResult.Success) return false

        // ── Device folder: copy all archives ────────────────────────────
        if (item.isDirectory) {
            val archives = listResult.data.filter { !it.isDirectory }
            if (archives.isEmpty()) return false

            val stageRoot = File(context.filesDir, "webdav_stage/paste")
            var allOk = true
            try {
                for (archive in archives) {
                    val baseName = archive.name.removeSuffix(".tar.gz").removeSuffix(".zip")
                    val targetDir = File(localSaveDir, baseName)

                    if (targetDir.exists()) {
                        if (conflictAction == ConflictAction.INCREMENT) {
                            onProgress("跳过 ${archive.name}（已存在）")
                            continue
                        }
                        // OVERWRITE
                        targetDir.deleteRecursively()
                    }

                    onProgress("下载 ${archive.name}…")
                    val archiveStage = File(stageRoot, "download")
                    val extractedStage = File(stageRoot, "extracted/$baseName")
                    archiveStage.mkdirs()

                    val localArchive = File(archiveStage, archive.name)
                    val downloadResult = c.downloadToFile("zkqFiles/$remoteDir/zkqGlobalGameSave/${archive.name}", localArchive)
                    if (downloadResult !is WebDavResult.Success) { allOk = false; continue }

                    onProgress("解压 ${archive.name}…")
                    val extracted = if (archive.name.endsWith(".zip")) {
                        ShellHelper.runBlocking("unzip -o \"${localArchive.absolutePath}\" -d \"${extractedStage.absolutePath}\"").isSuccess
                    } else if (archive.name.endsWith(".tar.gz")) {
                        extractLegacyTarGz(localArchive, extractedStage)
                    } else false

                    if (!extracted) { allOk = false; continue }

                    val extractedDir = extractedStage.listFiles()?.firstOrNull { it.isDirectory }
                        ?: run { allOk = false; continue }

                    targetDir.parentFile?.mkdirs()
                    val copyOk = ShellHelper.runBlocking(
                        "cp -rf \"${extractedDir.absolutePath}\" \"${targetDir.absolutePath}\""
                    ).isSuccess
                    if (!copyOk) allOk = false

                    // Clean per-archive staging
                    if (archiveStage.exists()) archiveStage.deleteRecursively()
                    if (extractedStage.exists()) extractedStage.deleteRecursively()
                }
            } finally {
                if (stageRoot.exists()) stageRoot.deleteRecursively()
            }
            return allOk
        }

        // ── Single archive ──────────────────────────────────────────────
        val archiveFile = listResult.data.find { it.name == item.name }
            ?: return false

        val stageRoot = File(context.filesDir, "webdav_stage/paste")
        val archiveStage = File(stageRoot, "download")
        val localName = item.name.removeSuffix(".tar.gz").removeSuffix(".zip")
        val extractedStage = File(stageRoot, "extracted/$localName")

        try {
            archiveStage.mkdirs()

            onProgress("下载 ${item.name}…")
            val localArchive = File(archiveStage, archiveFile.name)
            val downloadResult = c.downloadToFile("zkqFiles/$remoteDir/zkqGlobalGameSave/${archiveFile.name}", localArchive)
            if (downloadResult !is WebDavResult.Success) return false

            onProgress("解压…")
            val extracted = if (archiveFile.name.endsWith(".zip")) {
                ShellHelper.runBlocking("unzip -o \"${localArchive.absolutePath}\" -d \"${extractedStage.absolutePath}\"").isSuccess
            } else if (archiveFile.name.endsWith(".tar.gz")) {
                // ponytail: shell kept for legacy tar.gz extraction
                extractLegacyTarGz(localArchive, extractedStage)
            } else false

            if (!extracted) return false

            // Find the extracted directory and copy to external save dir via shell
            val extractedDir = extractedStage.listFiles()?.firstOrNull { it.isDirectory }
                ?: return false

            val targetDir = File(localSaveDir, localName)
            val targetParent = targetDir.parentFile ?: return false.also { Log.w(TAG, "  no target parent dir") }
            val copyOk = ShellHelper.runBlocking(
                "mkdir -p \"${targetParent.absolutePath}\" && cp -rf \"${extractedDir.absolutePath}\" \"${targetDir.absolutePath}\""
            ).isSuccess
            return copyOk
        } finally {
            if (archiveStage.exists()) archiveStage.deleteRecursively()
            if (extractedStage.exists()) extractedStage.deleteRecursively()
            if (stageRoot.exists() && stageRoot.listFiles()?.isEmpty() == true)
                stageRoot.delete()
        }
    }

    /**
     * Local → Local: directory copy via shell only.
     */
    private fun copyLocalToLocal(
        item: ClipboardItem,
        targetName: String,
        onProgress: (String) -> Unit
    ): Boolean {
        val sourceDir = File(localSaveDir, item.name)
        val srcExists = ShellHelper.runBlocking("test -d \"${sourceDir.absolutePath}\"").isSuccess
        if (!srcExists) return false

        onProgress("复制 ${item.name} → ${targetName}…")
        val targetDir = File(localSaveDir, targetName)
        return ShellHelper.runBlocking(
            "cp -rf \"${sourceDir.absolutePath}\" \"${targetDir.absolutePath}\""
        ).isSuccess
    }

    /**
     * Remote → Remote: copy archives across devices.
     * Uses WebDAV COPY to preserve the source (cut is handled separately by deleteSource).
     */
    private fun copyRemoteToRemote(
        item: ClipboardItem,
        targetDevice: String,
        targetName: String,
        onProgress: (String) -> Unit
    ): Boolean {
        val c = client ?: return false

        if (item.isDirectory) {
            // Copy all archives from source device to target device
            onProgress("扫描 ${item.name}…")
            val listResult = c.listDirectory("zkqFiles/${item.name}/zkqGlobalGameSave")
            if (listResult !is WebDavResult.Success) return false
            val archives = listResult.data.filter { !it.isDirectory }
            if (archives.isEmpty()) return false

            var allOk = true
            for (archive in archives) {
                val src = "zkqFiles/${item.name}/zkqGlobalGameSave/${archive.name}"
                val dst = "zkqFiles/$targetDevice/zkqGlobalGameSave/${archive.name}"
                onProgress("复制 ${archive.name}…")
                if (!c.copy(src, dst).isSuccess) allOk = false
            }
            return allOk
        } else {
            val sourceDevice = item.sourcePath
            val src = "zkqFiles/$sourceDevice/zkqGlobalGameSave/${item.name}"
            val dst = "zkqFiles/$targetDevice/zkqGlobalGameSave/$targetName"
            onProgress("复制 ${item.name} → ${targetDevice}…")
            return c.copy(src, dst).isSuccess
        }
    }

    // ── Source deletion (for cut operations) ──────────────────────────────────

    private fun deleteSource(item: ClipboardItem): Boolean {
        return when (item.sourceType) {
            LocationType.LOCAL -> {
                val dir = File(localSaveDir, item.name)
                if (!dir.exists()) return true
                ShellHelper.runBlocking("rm -rf \"${dir.absolutePath}\"").isSuccess
            }
            LocationType.REMOTE -> {
                val c = client ?: return false
                val result = c.delete("zkqFiles/${item.sourcePath}/zkqGlobalGameSave/${item.name}")
                result.isSuccess
            }
        }
    }

    // ── Conflict helpers ──────────────────────────────────────────────────────

    private fun archiveNameFor(item: ClipboardItem): String {
        return if (item.sourceType == LocationType.LOCAL) "${item.name}.zip"
        else item.name // remote already has extension
    }

    private fun generateNewName(base: String, targetType: LocationType, targetPath: String): String {
        return if (targetType == LocationType.LOCAL) generateNewLocalName(base)
        else generateNewRemoteName(base, targetPath, client ?: return base)
    }

    private fun generateNewLocalName(base: String): String {
        val clean = base.removeSuffix(".tar.gz").removeSuffix(".zip")
        val num = clean.toIntOrNull() ?: return "${clean}_copy"
        var suffix = 1
        while (true) {
            val candidate = num + suffix
            if (!File(localSaveDir, candidate.toString()).exists()) return candidate.toString()
            suffix++
        }
    }

    private fun generateNewRemoteName(base: String, device: String, c: WebDavClient): String {
        val baseName = base.removeSuffix(".zip").removeSuffix(".tar.gz")
        var suffix = 1
        val maxSuffix = 9999 // 防止因 exists() 返回假 false 导致死循环
        while (suffix <= maxSuffix) {
            val candidate = "${baseName}_$suffix.zip"
            if (!c.exists("zkqFiles/$device/zkqGlobalGameSave/$candidate")) return candidate
            suffix++
        }
        // 极端情况：所有编号都被占用（或 exists 一直返回 false），退回时间戳方案
        val fallback = "${baseName}_${System.currentTimeMillis()}.zip"
        Log.w(TAG, "generateNewRemoteName exhausted $maxSuffix candidates, fallback to $fallback")
        return fallback
    }

    // ── Archive utilities (delegate to ArchiveUtils) ──────────────────────────

    fun parseArchiveNumber(name: String): Int? = ArchiveUtils.parseArchiveNumber(name)

    private fun zipDirectory(sourceDir: File, targetZip: File) = ArchiveUtils.zipDirectory(sourceDir, targetZip)

    private fun unzipToDirectory(zipFile: File, destinationDir: File): Boolean = ArchiveUtils.unzipToDirectory(zipFile, destinationDir)

    // ponytail: shell is kept only for extracting legacy remote tar.gz archives
    private fun extractLegacyTarGz(archiveFile: File, destinationDir: File): Boolean = ArchiveUtils.extractLegacyTarGz(archiveFile, destinationDir)

    // ── Log copy ──────────────────────────────────────────────────────────────

    fun copyLogsToExternal() {
        val src = context.filesDir.absolutePath + "/logs"
        val dst = "$externalBase/logs"
        if (File(src).isDirectory) {
            ShellHelper.runBlockingNoOutput("mkdir -p \"$dst\" && cp -rf \"$src\" \"$dst/\"")
        }
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────

    fun cleanupStage() {
        val stageDir = File(context.filesDir, "webdav_stage")
        if (stageDir.exists()) stageDir.deleteRecursively()
    }
}

// ── Data types ────────────────────────────────────────────────────────────────

data class RemoteArchive(
    val name: String,
    val number: Int,
    val isZip: Boolean,
    val size: Long = 0L,
    val lastModified: String = ""
)

data class FileInfo(
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
)
