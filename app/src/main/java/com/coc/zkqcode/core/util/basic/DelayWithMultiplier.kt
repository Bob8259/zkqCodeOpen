package com.coc.zkqcode.core.util.basic

import com.coc.zkqcode.core.data.database.GlobalVars
import com.coc.zkqcode.core.util.fileactions.LogHelper.logAndStop
import kotlinx.coroutines.delay // 更改导入，使用标准的 delay

suspend fun delayWithMultiplier(delayTime: Int) {
    // 1. 获取倍率并直接转换，如果失败则停止
    val multiplier = GlobalVars.configStates["delay_multiplier"]?.value?.toFloat()
        ?: logAndStop("Failed to get delayMultiplier")

    // 2. 计算结果并转为 Long (delay 函数需要的类型)
    delay((delayTime * multiplier).toLong())
}