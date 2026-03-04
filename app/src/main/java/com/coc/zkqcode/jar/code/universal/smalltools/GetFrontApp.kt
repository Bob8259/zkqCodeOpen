package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.InGamesVars

suspend fun isGameAtFront(): Boolean {
    // Use | to separate multiple grep targets, reducing process creation count
    val combinedCmd =
        "dumpsys activity activities | grep -E 'mResumedActivity|mCurrentFocus|mFocusedApp'"
    val gamePackage = InGamesVars.currentGamePackage.toString()
    val rawResult = RunShell.runAndGetFirst(combinedCmd)

    // Regex to extract package name/class name
    val frontApp =
        """([a-zA-Z0-9._]+/[a-zA-Z0-9._$ ]+)""".toRegex().find(rawResult)?.value?.trim() ?: "None"
    if ((frontApp.contains("com.supercell.clashofclans") && gamePackage == "1") ||
        (frontApp.contains("com.tencent.tmgp.supercell.clashofclans") && gamePackage == "0") ||
        (frontApp.contains("com.atrasis.original") && gamePackage == "2")
    ) {
        return true
    } else {
        ShowMessage(
            "当前前台应用${frontApp}"
        )
        return false
    }
}

suspend fun runApp(packageName: String) {
    RunShell.runNoOutput("monkey -p $packageName -c android.intent.category.LAUNCHER 1")
}

suspend fun killApp(packageName: String) {
    RunShell.runNoOutput("am force-stop $packageName")
}