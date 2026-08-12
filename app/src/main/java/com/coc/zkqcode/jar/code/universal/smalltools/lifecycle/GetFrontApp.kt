package com.coc.zkqcode.jar.code.universal.smalltools.lifecycle

import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.jar.code.universal.InGameVars

suspend fun isGameAtFront(): Boolean {
    // Use | to separate multiple grep targets, reducing process creation count
    val combinedCmd =
        "dumpsys activity activities | grep -E 'mResumedActivity|mCurrentFocus|mFocusedApp'"
    val rawResult = RunShell.runAndGetFirst(combinedCmd)

    // Regex to extract package name/class name
    val frontApp =
        """([a-zA-Z0-9._]+/[a-zA-Z0-9._$ ]+)""".toRegex().find(rawResult)?.value?.trim() ?: "None"

    // Check if the front app matches the expected package for the current game version
    return frontApp.contains(InGameVars.currentGameVersion.packageName)
}


