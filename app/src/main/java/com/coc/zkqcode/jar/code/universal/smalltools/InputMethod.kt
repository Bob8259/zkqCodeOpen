package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.util.basic.RunShell

suspend fun setZKQInputMethod() {
    val imeId = "com.coc.zkqcode/.core.system.inputmethod.ZKQInputMethodService"

    // 1. 尝试激活（即使已激活再执行一次也没关系）
    RunShell.run("ime enable $imeId")

    // 2. 获取当前默认输入法进行对比
    val currentIme = RunShell.runAndGetFirst("settings get secure default_input_method")

    if (currentIme != imeId) {
        // 3. 执行设置
        RunShell.run("ime set $imeId")
        // 验证是否切换成功
        println("Input method switched to $imeId")
    } else {
        println("Input method is already default.")
    }
}