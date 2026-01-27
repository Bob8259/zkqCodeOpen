package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.ShowMessage
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay

suspend fun isGameAtFront(): Boolean {
    val frontApp = Shell.cmd("dumpsys window | grep mCurrentFocus").exec()
    if (frontApp.out.contains("com.supercell.clashofclans") || frontApp.out.contains("com.tencent.tmgp.supercell.clashofclans")) return true
    else {
        ShowMessage("当前前台应用${frontApp.out}")
        runApp()
        delay(1000)
        return false
    }
}

fun runApp(package_name: String): Unit{
    Shell.cmd("monkey -p $package_name -c android.intent.category.LAUNCHER 1").exec()
}