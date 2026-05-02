package com.coc.zkqcode.jar.code

import com.topjohnwu.superuser.Shell

/**
 * JAR-side blocking shell utilities.
 * Duplicates the blocking methods from RunShell so the JAR remains compatible
 * with older host apps that do not have runBlocking / runBlockingOutput / runBlockingNoOutput.
 */
object ShellHelper {

    /**
     * Executes a shell command and returns the full Shell.Result (blocking, non-suspend).
     * Useful when you need access to isSuccess, out, and err.
     */
    fun runBlocking(cmd: String): Shell.Result {
        return Shell.cmd(cmd).exec()
    }

    /**
     * Blocking variant: executes a shell command and returns the output as a list of strings.
     */
    fun runBlockingOutput(cmd: String): List<String> {
        return Shell.cmd(cmd).exec().out
    }

    /**
     * Blocking variant: executes a shell command without returning the output.
     */
    fun runBlockingNoOutput(cmd: String) {
        Shell.cmd(cmd).exec()
    }
}
