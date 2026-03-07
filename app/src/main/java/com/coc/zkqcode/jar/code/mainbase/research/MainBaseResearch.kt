package com.coc.zkqcode.jar.code.mainbase.research

import com.coc.zkqcode.jar.code.mainbase.others.MainBaseWorkerAndResearch
import com.coc.zkqcode.jar.code.universal.enterMainScreen
import com.coc.zkqcode.jar.code.universal.smalltools.getBooleanConfigRuntime
import com.coc.zkqcode.jar.ui.schema.Schema

suspend fun mainBaseResearch(): Boolean {
    if (getBooleanConfigRuntime(Schema.MAIN_BASE_SETTINGS.RESEARCH_SETTING.key) && MainBaseWorkerAndResearch.detectResearch()) {

    }
    return enterMainScreen()
}