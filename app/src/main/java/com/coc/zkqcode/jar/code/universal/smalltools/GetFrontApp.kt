package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.topjohnwu.superuser.Shell

fun isGameAtFront(gamePackage: String): Boolean {
    // 使用 | 分隔多个 grep 目标，减少进程开启次数
    val combinedCmd =
        "dumpsys activity activities | grep -E 'mResumedActivity|mCurrentFocus|mFocusedApp'"

    val resultList = Shell.cmd(combinedCmd).exec().out
    // 过滤空行并取第一个非空结果
    val rawResult = resultList.firstOrNull { it.isNotBlank() } ?: ""

    // 提取包名/类名的正则
    val frontApp = """([a-zA-Z0-9._]+/[a-zA-Z0-9._$ ]+)""".toRegex()
        .find(rawResult)?.value?.trim() ?: "None"
    if ((frontApp
            .contains("com.supercell.clashofclans") && gamePackage == "1") || (frontApp.contains(
            "com.tencent.tmgp.supercell.clashofclans"
        ) && gamePackage == "0")
    ) {
        return true
    } else {
        ShowMessage(
            "当前前台应用${frontApp}"
        )
        return false
    }
}

fun runApp(packageName: String) {
    Shell.cmd("monkey -p $packageName -c android.intent.category.LAUNCHER 1").exec()
}