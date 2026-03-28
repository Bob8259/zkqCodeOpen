package com.coc.zkqcode.core.system.checkpermissions

import com.coc.zkqcode.core.util.fileactions.LogHelper
import com.topjohnwu.superuser.Shell

object AccessibilityPermissionHelper {
    fun enableAccessibilityWithRoot(packageName: String, serviceClassName: String) {
        val serviceComponent = "$packageName/$serviceClassName"

        // 1. 获取当前已经开启的服务列表，防止覆盖掉用户开启的其他服务
        val currentServices = Shell.cmd("settings get secure enabled_accessibility_services").exec().out.joinToString("")

        // 2. 如果当前服务不在列表中，则追加上去
        if (!currentServices.contains(serviceComponent)) {
            val newServices = if (currentServices.isEmpty() || currentServices == "null") {
                serviceComponent
            } else {
                "$currentServices:$serviceComponent"
            }
            // 3. 写入新的服务列表
            Shell.cmd("settings put secure enabled_accessibility_services $newServices").exec()
        }

        // 4. 确保总开关已打开
        Shell.cmd("settings put secure accessibility_enabled 1").exec()
    }
}
