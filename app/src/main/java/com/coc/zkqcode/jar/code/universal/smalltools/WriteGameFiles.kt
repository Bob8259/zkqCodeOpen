package com.coc.zkqcode.jar.code.universal.smalltools

import android.annotation.SuppressLint
import android.os.Environment
import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.ui.schema.Schema.ACCOUNT_SETTINGS
import com.topjohnwu.superuser.Shell

// Helper function: quickly get config value, trigger logAndStop if empty
fun getConfigOrStop(key: String): String {
    return GlobalVars.configStates[key]?.value ?: logAndStop("Failed to get config: $key")
}

// Returns the save path name (archive folder suffix) for the current account based on its game version
fun getGameFilePath(): String {
    val index = InGamesVars.currentAccountNumber
    return when (InGamesVars.currentGameVersion) {
        GameVersion.CN -> getConfigOrStop("${ACCOUNT_SETTINGS.CN_PATH.key}$index")
        GameVersion.GLOBAL -> getConfigOrStop("${ACCOUNT_SETTINGS.GLOBAL_PATH.key}$index")
        // PRIVATE and any future versions do not have a configured save path
        else -> ""
    }
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
fun writeGameFilesCore(
    packageName: String,
    savePathName: String,
    folderName: String,
    subDirs: List<String>
) {
    val sdPath = Environment.getExternalStorageDirectory().path
    val sourceDir = "$sdPath/zkqFiles/$folderName/$savePathName"

    // Verify that the archive directory exists before attempting to write
    if (!Shell.cmd("[ -d \"$sourceDir\" ]").exec().isSuccess) {
        ShowMessage("存档文件不存在：$sourceDir\n即将跳过当前账号")
        return
    }

    subDirs.forEach { subDir ->
        val destPath = "/data/data/$packageName/$subDir"
        Shell.cmd("rm -rf \"$destPath\"/*").exec()
        Shell.cmd("cp -r \"$sourceDir/$subDir/\"* \"$destPath/\"").exec()
        Shell.cmd("chmod -R 777 \"$destPath\"").exec()
    }

}

/**
 * Writes the game save files for the current account (as tracked by [InGamesVars]) back
 * to the game's data directory, then sets permissions to 777.
 */
fun writeGameFiles() {
    val savePathName = getGameFilePath()
    val version = InGamesVars.currentGameVersion

    val (folderName, subDirs) = when (version) {
        GameVersion.CN -> "zkqCNGameSave" to listOf("shared_prefs", "databases")
        GameVersion.GLOBAL -> "zkqGlobalGameSave" to listOf("shared_prefs")
        // nothing to write
        else -> return
    }

    writeGameFilesCore(version.packageName, savePathName, folderName, subDirs)
}