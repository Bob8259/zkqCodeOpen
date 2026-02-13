package com.coc.zkqcode.core.util.bugreporter

import android.os.Environment
import com.coc.zkqcode.core.util.basic.RunShell
import com.coc.zkqcode.core.util.basic.delayWithMultiplier

/**
 * Utility for reporting bugs by capturing screenshots.
 */
object BugReporter {
    private val BUG_REPORT_DIR: String
        get() = "${Environment.getExternalStorageDirectory().path}/zkqFiles/bugReport"

    /**
     * Takes a screenshot and stores it in the bug report directory.
     *
     * @param fileName The name of the file (extension .png will be added if missing).
     */
    suspend fun takeScreenshot(fileName: String) {
        val sanitizedFileName = if (fileName.endsWith(".png")) fileName else "$fileName.png"
        val dir = BUG_REPORT_DIR
        val fullPath = "$dir/$sanitizedFileName"

        // Ensure the directory exists and capture the screenshot
        val command = "mkdir -p $dir && screencap -p $fullPath"
        RunShell.runNoOutput(command)
        delayWithMultiplier(1000)
    }
}
