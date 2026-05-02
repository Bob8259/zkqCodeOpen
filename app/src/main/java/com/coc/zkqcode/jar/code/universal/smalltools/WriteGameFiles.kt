package com.coc.zkqcode.jar.code.universal.smalltools

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