package com.coc.zkqcode.core.util.basic

import com.topjohnwu.superuser.Shell

/**
 * Utility for running shell commands.
 */
object RunShell {
    /**
     * Executes a shell command and returns the output as a list of strings.
     * @param checkIsPlaying if true, waits until the script is playing before executing
     */
    suspend fun run(cmd: String, checkIsPlaying: Boolean = true): List<String> {
        if (checkIsPlaying) {
            waitForPlay()
        }
        return Shell.cmd(cmd).exec().out
    }

    /**
     * Executes a shell command without returning the output.
     * @param checkIsPlaying if true, waits until the script is playing before executing
     */
    suspend fun runNoOutput(cmd: String, checkIsPlaying: Boolean = true) {
        if (checkIsPlaying) {
            waitForPlay()
        }
        Shell.cmd(cmd).exec()
    }

    /**
     * Executes a shell command and returns the first non-blank line of output.
     * @param checkIsPlaying if true, waits until the script is playing before executing
     */
    suspend fun runAndGetFirst(cmd: String, checkIsPlaying: Boolean = true): String {
        if (checkIsPlaying) {
            waitForPlay()
        }
        return run(cmd, checkIsPlaying).firstOrNull { it.isNotBlank() } ?: ""
    }

    /**
     * Executes a shell command and returns the full Shell.Result (blocking, non-suspend).
     * Useful when you need access to isSuccess, out, and err.
     * @param checkIsPlaying if true, waits until the script is playing before executing (ignored in blocking variant)
     */
    fun runBlocking(cmd: String, checkIsPlaying: Boolean = false): Shell.Result {
        return Shell.cmd(cmd).exec()
    }

    /**
     * Blocking variant: executes a shell command and returns the output as a list of strings.
     * @param checkIsPlaying if true, waits until the script is playing before executing (ignored in blocking variant)
     */
    fun runBlockingOutput(cmd: String, checkIsPlaying: Boolean = false): List<String> {
        return Shell.cmd(cmd).exec().out
    }

    /**
     * Blocking variant: executes a shell command without returning the output.
     * @param checkIsPlaying if true, waits until the script is playing before executing (ignored in blocking variant)
     */
    fun runBlockingNoOutput(cmd: String, checkIsPlaying: Boolean = false) {
        Shell.cmd(cmd).exec()
    }
}