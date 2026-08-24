package com.coc.zkqcode.jar.code.universal.smalltools.data

import android.annotation.SuppressLint
import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.FileHelper
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.launchGameAndHandleDataNumberLogin
import com.coc.zkqcode.jar.code.universal.smalltools.qrlink.QrLinkState
import com.coc.zkqcode.jar.ui.schema.Schema
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
// Helper function: quickly get config value, trigger logAndRestart if empty
fun getConfigOrStop(key: String): String {
    return GlobalVars.configStates[key]?.value ?: logAndRestart("Failed to get config: $key")
}

// Returns the save path name (archive folder suffix) for the current account based on its game version
fun getGameFilePath(): String {
    val index = InGameVars.currentAccountNumber
    if (getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) == "1")
        return index.toString()
    return when (InGameVars.currentGameVersion) {
        GameVersion.CN -> getConfigOrStop("${ACCOUNT_SETTINGS.CN_PATH.key}$index")
        GameVersion.GLOBAL -> getConfigOrStop("${ACCOUNT_SETTINGS.GLOBAL_PATH.key}$index")
        // PRIVATE and any future versions do not have a configured save path
        else -> ""
    }
}

// Wrap content as a shell-safe single-quoted literal so printf can write it exactly.
private fun toSingleQuotedShellLiteral(content: String): String {
    return "'${content.replace("'", "'\"'\"'")}'"
}

// Resolve the selected data-number slot and write only its raw content into loginData/data.txt.
@SuppressLint("SdCardPath")
suspend fun writeDataNumberFile(slot: String): Boolean {
    // Only reset CN shared preferences here so data-number login keeps other app data intact.
    RunShell.runNoOutput("rm -rf \"/data/data/${GameVersion.CN.packageName}/shared_prefs\"/*", false)
    val trimmedSlot = slot.trim()
    if (trimmedSlot.isEmpty()) {
        ShowMessage("数据序号不能为空\n即将跳过当前账号")
        return false
    }

    val sdPath = Environment.getExternalStorageDirectory().path
    val loginDataDir = "$sdPath/loginData"
    val dataJsonPath = "$loginDataDir/data.json"
    val dataTxtPath = "$loginDataDir/data.txt"

    if (!FileHelper.checkExists(dataJsonPath)) {
        ShowMessage("数据文件不存在：$dataJsonPath\n即将跳过当前账号")
        return false
    }

    val dataJson = FileHelper.readJson(dataJsonPath)
    if (dataJson == null) {
        ShowMessage("数据文件格式无效：$dataJsonPath\n即将跳过当前账号")
        return false
    }

    if (!dataJson.has(trimmedSlot)) {
        ShowMessage("数据不存在：$trimmedSlot\n即将跳过当前账号")
        return false
    }

    val contentElement = dataJson.get(trimmedSlot)
    if (contentElement == null || contentElement.isJsonNull) {
        ShowMessage("数据内容无效：$trimmedSlot\n即将跳过当前账号")
        return false
    }

    val dataContent = contentElement.asString
    if (dataContent.isBlank()) {
        ShowMessage("数据内容为空：$trimmedSlot\n即将跳过当前账号")
        return false
    }

    val shellContent = toSingleQuotedShellLiteral(dataContent)
    val writeResult = RunShell.runAndGetFirst(
        """
        mkdir -p "$loginDataDir" &&
        printf %s $shellContent > "$dataTxtPath" &&
        echo success || echo failure
        """.trimIndent(), false
    )
    if (writeResult != "success") {
        ShowMessage("写入数据失败：$dataTxtPath\n即将跳过当前账号")
        return false
    }
    return true
}

// Read the current account's configured data-number slot, then launch the bot-only post-login flow.
private suspend fun writeCurrentAccountDataNumberFile(): Boolean {
    val accountIndex = InGameVars.currentAccountNumber
    val slotKey = "${ACCOUNT_SETTINGS.DATA_CONTENT.key}$accountIndex"
    val slot = getConfigOrStop(slotKey).trim()
    if (slot.isEmpty()) {
        ShowMessage("数据序号不能为空：$slotKey\n即将跳过当前账号")
        return false
    }
    if (!writeDataNumberFile(slot)) return false
    launchGameAndHandleDataNumberLogin(GameVersion.CN)
    return true
}

/**
 * Core helper: copies each subdirectory from the SD card archive into the game's data directory,
 * clears the destination first, then sets permissions to 777.
 * This is called by both [writeGameFiles] and SwitchAccount UI.
 *
 * @param packageName  Game package name (e.g. "com.supercell.clashofclans")
 * @param savePathName Archive folder suffix identifying the account save (e.g. "001")
 * @param folderName   Top-level archive folder on SD card (e.g. "zkqGlobalGameSave")
 * @param subDirs      Subdirectories to copy (e.g. ["shared_prefs", "databases"])
 */
@SuppressLint("SdCardPath")
suspend fun writeGameFilesCore(
    packageName: String, savePathName: String, folderName: String, subDirs: List<String>, killGame: Boolean = true
): Boolean {
    val sdPath = Environment.getExternalStorageDirectory().path
    val sourceDir = "$sdPath/zkqFiles/$folderName/$savePathName"

    // Verify that the archive directory exists before attempting to write
    if (!ShellHelper.runBlocking("[ -d \"$sourceDir\" ]").isSuccess) {
        ShowMessage("存档文件不存在：$sourceDir\n即将跳过当前账号")
        return false
    }
    ShowMessage("写入存档文件中，路径：$sourceDir")
    if (killGame) killGame()
    subDirs.forEach { subDir ->
        val destPath = "/data/data/$packageName/$subDir"
        RunShell.runNoOutput("rm -rf \"$destPath\"/*", checkIsPlaying = false)
        RunShell.runNoOutput("cp -r \"$sourceDir/$subDir/\"* \"$destPath/\"", checkIsPlaying = false)
        RunShell.runNoOutput("chmod -R 777 \"$destPath\"", checkIsPlaying = false)
    }
    return true
}

/**
 * Writes the game save files for the current account (as tracked by [InGameVars]) back
 * to the game's data directory, then sets permissions to 777.
 */
suspend fun writeGameFiles(): Boolean {
    // In batch-create mode, always use start_method=0 (game save), GLOBAL version,
    // and account number as save path — skip reading per-account config.
    val isBatchCreate = getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) == "1"
    if (isBatchCreate) {
        killGame()
        val version = GameVersion.GLOBAL
        val savePathName = InGameVars.currentAccountNumber.toString()
        return writeGameFilesCore(version.packageName, savePathName, version.folderName, version.subDirs)
    }

    val startMethod = getConfigOrStop("${ACCOUNT_SETTINGS.START_METHOD.key}${InGameVars.currentAccountNumber}").toIntOrNull() ?: logAndRestart("${ACCOUNT_SETTINGS.START_METHOD.displayName} 必须是数字，请检查配置")
    killGame()
    return when (startMethod) {
        0 -> {
            val savePathName = getGameFilePath()
            val version = InGameVars.currentGameVersion
            // Versions without a configured save folder (e.g. PRIVATE) have nothing to write
            if (version.folderName.isEmpty()) return true
            writeGameFilesCore(version.packageName, savePathName, version.folderName, version.subDirs)
        }
        1 -> true
        2 -> writeCurrentAccountDataNumberFile()
        else -> logAndRestart("${ACCOUNT_SETTINGS.START_METHOD.displayName} 配置错误：$startMethod")
    }
}

/**
 * 提取存档（扫码提取流程用，ADR-0003）：把游戏内当前账号的数据原样复制到
 * SD 卡槽位目录（/sdcard/zkqFiles/{folderName}/{slot}/）。
 *
 * 采用"暂存 → 校验 → 换入"三步，避免覆盖中途失败留下半个槽位：
 * 先把数据复制到 <slot>.tmp 暂存目录，校验确有文件后再删旧槽位、把暂存目录
 * 原子改名换入（同盘 mv）。复制失败、或用户在提取中被取消时只删暂存目录，
 * 旧槽位保持原样，绝不出现"旧档已删、新档没拷完"的不可恢复状态。
 * 目标槽位本身仍无条件覆盖（用户确定的策略——覆盖错了重新登录再提取即可）。
 */
@SuppressLint("SdCardPath")
suspend fun extractGameSave(version: GameVersion, slot: String): Boolean {
    val sdPath = Environment.getExternalStorageDirectory().path
    val gameRootDir = "$sdPath/zkqFiles/${version.folderName}"
    val targetSaveDir = "$gameRootDir/$slot"
    val stagingDir = "$targetSaveDir.tmp"

    if (QrLinkState.isCancelRequested()) return false

    // 清掉上次中断残留的暂存目录
    RunShell.runNoOutput("rm -rf \"$stagingDir\"", checkIsPlaying = false)
    RunShell.runNoOutput("mkdir -p \"$gameRootDir\"", checkIsPlaying = false)
    RunShell.runNoOutput("mkdir -p \"$stagingDir\"", checkIsPlaying = false)

    for (dir in version.subDirs) {
        if (QrLinkState.isCancelRequested()) {
            RunShell.runNoOutput("rm -rf \"$stagingDir\"", checkIsPlaying = false)
            return false
        }
        val destPath = "$stagingDir/$dir"
        RunShell.runNoOutput("mkdir -p \"$destPath\"", checkIsPlaying = false)
        RunShell.runNoOutput("cp -r /data/data/${version.packageName}/$dir/* \"$destPath\"", checkIsPlaying = false)
    }

    // 提取中途被取消：只删暂存，不动旧槽位
    if (QrLinkState.isCancelRequested()) {
        RunShell.runNoOutput("rm -rf \"$stagingDir\"", checkIsPlaying = false)
        return false
    }

    // 校验确有文件被复制，否则视为失败（绝不删旧档、绝不上报成功）
    val fileCount = RunShell.runAndGetFirst(
        "find \"$stagingDir\" -type f | wc -l", false
    ).trim().toIntOrNull() ?: 0
    if (fileCount <= 0) {
        RunShell.runNoOutput("rm -rf \"$stagingDir\"", checkIsPlaying = false)
        return false
    }

    // 换入：删旧槽位后把暂存目录原子改名（staging 与目标同盘）
    if (QrLinkState.isCancelRequested()) {
        RunShell.runNoOutput("rm -rf \"$stagingDir\"", checkIsPlaying = false)
        return false
    }
    RunShell.runNoOutput("[ -d \"$targetSaveDir\" ] && rm -rf \"$targetSaveDir\"", checkIsPlaying = false)
    RunShell.runNoOutput("mv \"$stagingDir\" \"$targetSaveDir\"", checkIsPlaying = false)
    return true
}
