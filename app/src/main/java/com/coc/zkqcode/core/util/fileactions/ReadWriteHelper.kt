package com.coc.zkqcode.core.util.fileactions

import com.coc.zkqcode.core.ui.components.GlobalVars

object ReadWriteHelper {
    fun writeJson(path: String, content: String) {
        val writeAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "write",
            "path" to path,
            "content" to content
        )
        GlobalVars.fileActions!!.getConnection().sendAction(writeAction)
    }

    fun readJson(path: String) {
        val readAction = mapOf(
            "actionType" to "file_action",
            "subAction" to "read",
            "path" to path
        )
        GlobalVars.fileActions!!.getConnection().sendAction(readAction)
    }
}
