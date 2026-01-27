package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.topjohnwu.superuser.Shell

fun isGameAtFront(gamePackage: String): Boolean {
    val frontApp = Shell.cmd("dumpsys window displays | grep mCurrentFocus").exec()
    if ((frontApp.out.toString()
            .contains("com.supercell.clashofclans") && gamePackage == "1") || (frontApp.out.toString()
            .contains(
                "com.tencent.tmgp.supercell.clashofclans"
            ) && gamePackage == "0")
    ) {
        return true
    } else {
        ShowMessage(
            "当前前台应用${frontApp.out}"
        )
        return false
    }
}

fun runApp(packageName: String) {
    Shell.cmd("monkey -p $packageName -c android.intent.category.LAUNCHER 1").exec()
}