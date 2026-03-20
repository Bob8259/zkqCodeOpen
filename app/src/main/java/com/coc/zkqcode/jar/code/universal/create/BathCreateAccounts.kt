package com.coc.zkqcode.jar.code.universal.create

import android.annotation.SuppressLint
import android.os.Environment
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGamesVars
import com.coc.zkqcode.jar.code.universal.smalltools.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.runGame
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import com.coc.zkqcode.jar.ui.schema.Schema
import com.topjohnwu.superuser.Shell

@SuppressLint("SdCardPath")
suspend fun batchCreateAccounts() {
    if (getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) != "1") return
    val startID = getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_START_ID.key).toInt()
    val endID = getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_END_ID.key).toInt()
    val globalPackageName = GameVersion.GLOBAL.packageName

    for (i in startID..endID) {
        val path = "${Environment.getExternalStorageDirectory().path}/zkqFiles/zkqGlobalGameSave/$i"
        // Check if the folder exists using shell command
        val exists = RunShell.runAndGetFirst("[ -d \"$path\" ] && echo true || echo false")
        if (exists != "true") {
            // Set game version and account number for AllTutorials display and checkReconnections
            InGamesVars.currentGameVersion = GameVersion.GLOBAL
            InGamesVars.currentAccountNumber = i

            // Retry indefinitely until the tutorial succeeds
            var tutorialPassed = false
            while (!tutorialPassed) {
                // Kill game and clear save data to force a fresh account
                killGame()
                Shell.cmd("rm -rf \"/data/data/$globalPackageName/shared_prefs\"/*").exec()

                // Launch the global version game
                runGame()

                // Run through the tutorial; returns true when completed successfully
                tutorialPassed = AllTutorials.allBaseTutorial()
                if (!tutorialPassed) {
                    showDebugInfo("账号$i 教程未完成，正在重试...")
                }
            }

            // Extract game files to the save directory
            Shell.cmd("mkdir -p \"$path/shared_prefs\"").exec()
            Shell.cmd("cp -r /data/data/$globalPackageName/shared_prefs/* \"$path/shared_prefs/\"").exec()
            showDebugInfo("账号$i 创建成功，存档已保存至 $path")
        }
    }
}