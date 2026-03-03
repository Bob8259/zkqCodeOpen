package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.jar.code.universal.InGamesVars

suspend fun isGameAtFront(): Boolean {
    // 使用 | 分隔多个 grep 目标，减少进程开启次数
    val combinedCmd =
        "dumpsys activity activities | grep -E 'mResumedActivity|mCurrentFocus|mFocusedApp'"
    val gamePackage = InGamesVars.currentGamePackage.toString()
    val rawResult = RunShell.runAndGetFirst(combinedCmd)

    // 提取包名/类名的正则
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