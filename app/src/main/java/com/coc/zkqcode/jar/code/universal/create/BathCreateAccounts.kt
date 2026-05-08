package com.coc.zkqcode.jar.code.universal.create

import android.annotation.SuppressLint
import android.os.Environment
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.universal.accountMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndRestart
import com.coc.zkqcode.jar.code.auth.displayAds
import com.coc.zkqcode.jar.code.universal.GameVersion
import com.coc.zkqcode.jar.code.universal.InGameVars
import com.coc.zkqcode.jar.code.universal.smalltools.data.getConfigOrStop
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.killGame
import com.coc.zkqcode.jar.code.universal.smalltools.lifecycle.runGame
import com.coc.zkqcode.jar.code.universal.tutorial.AllTutorials
import com.coc.zkqcode.jar.ui.schema.Schema
@SuppressLint("SdCardPath")
suspend fun batchCreateAccounts() {
    if (getConfigOrStop(Schema.GLOBAL_SETTINGS.BATCH_CREATE_ACCOUNT.key) != "1") return
    val startID = getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_START_ID.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.CREATE_START_ID.displayName} 必须是数字，请检查配置")
    val endID = getConfigOrStop(Schema.GLOBAL_SETTINGS.CREATE_END_ID.key).toIntOrNull() ?: logAndRestart("${Schema.GLOBAL_SETTINGS.CREATE_END_ID.displayName} 必须是数字，请检查配置")
    val globalPackageName = GameVersion.GLOBAL.packageName

    for (i in startID..endID) {
        val path = "${Environment.getExternalStorageDirectory().path}/zkqFiles/zkqGlobalGameSave/$i"
        // Check if the folder exists using shell command
        val exists = RunShell.runAndGetFirst("[ -d \"$path\" ] && echo true || echo false")
        if (exists != "true") {
            displayAds()
            // Set game version and account number for AllTutorials display and checkReconnections
            InGameVars.currentGameVersion = GameVersion.GLOBAL
            InGameVars.currentAccountNumber = i

            // Retry indefinitely until the tutorial succeeds
            var tutorialPassed = false
            while (!tutorialPassed) {
                // Kill game and clear save data to force a fresh account
                killGame()
                RunShell.runNoOutput("rm -rf \"/data/data/$globalPackageName/shared_prefs\"/*", checkIsPlaying = false)

                // Launch the global version game
                runGame()

                // Run through the tutorial; returns true when completed successfully
                tutorialPassed = AllTutorials.allBaseTutorial()
                if (!tutorialPassed) {
                    accountMessage("账号$i 教程未完成，正在重试...")
                }
            }

            // Extract game files to the save directory
            RunShell.runNoOutput("mkdir -p \"$path/shared_prefs\"", checkIsPlaying = false)
            RunShell.runNoOutput("cp -r /data/data/$globalPackageName/shared_prefs/* \"$path/shared_prefs/\"", checkIsPlaying = false)
            accountMessage("账号$i 创建成功，存档已保存至 $path")
        }
    }
}