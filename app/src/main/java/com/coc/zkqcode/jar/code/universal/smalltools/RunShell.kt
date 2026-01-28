package com.coc.zkqcode.jar.code.universal.smalltools

import com.coc.zkqcode.core.data.database.GlobalVars
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay


/**
 * Utility for running shell commands.
 */
object RunShell {
    /**
     * Executes a shell command and returns the output as a list of strings.
     */
    suspend fun run(cmd: String): List<String> {
        while (!GlobalVars.isPlaying.value) {
            delay(1000)//the user paused the script, then we should also stop
        }
        return Shell.cmd(cmd).exec().out
    }

    /**
     * Executes a shell command without returning the output.
     */
    suspend fun runNoOutput(cmd: String) {
        while (!GlobalVars.isPlaying.value) {
            delay(1000)//the user paused the script, then we should also stop
        }
        Shell.cmd(cmd).exec()
    }

    /**
     * Executes a shell command and returns the first non-blank line of output.
     */
    suspend fun runAndGetFirst(cmd: String): String {
        while (!GlobalVars.isPlaying.value) {
            delay(1000)//the user paused the script, then we should also stop
        }
        return run(cmd).firstOrNull { it.isNotBlank() } ?: ""
    }
}
