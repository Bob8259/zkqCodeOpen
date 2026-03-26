package com.coc.zkqcode.core.system.screencapture

import android.graphics.BitmapFactory
import com.coc.zkqcode.core.util.fileactions.LogHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Persistent root shell screen capture.
 * Maintains a long-lived `su` process and uses `screencap -p` to read PNG data
 * directly from stdout by parsing PNG chunks, avoiding all file I/O.
 */
object ShellScreenCapture {

    private var process: Process? = null
    private var shellStdin: OutputStream? = null
    private var shellStdout: BufferedInputStream? = null

    private val connectionLock = Any()
    private val captureMutex = Mutex()

    private const val INIT_MARKER = "__SHELL_READY__"
    private const val STREAM_BUFFER_SIZE = 1024 * 1024 // 1MB read buffer

    /**
     * Ensure the persistent su shell is alive. Recreate if the process is dead.
     */
    private fun ensureConnection() {
        synchronized(connectionLock) {
            if (process != null && isProcessAlive(process!!)) return
            closeConnectionLocked()

            try {
                val pb = ProcessBuilder("su")
                pb.redirectErrorStream(false)
                val proc = pb.start()
                process = proc
                shellStdin = proc.outputStream
                shellStdout = BufferedInputStream(proc.inputStream, STREAM_BUFFER_SIZE)

                // Drain any initial output (e.g. Magisk greeting) by sending a marker
                drainUntilMarker()

                LogHelper.showDebugInfo("ShellScreenCapture: su connection established")
            } catch (e: Exception) {
                LogHelper.showDebugInfo("ShellScreenCapture: Failed to create su connection: ${e.message}")
                closeConnectionLocked()
                throw e
            }
        }
    }

    /**
     * Send a known echo marker and discard all bytes until the marker line appears.
     * This ensures the shell is ready and any startup messages are consumed.
     */
    private fun drainUntilMarker() {
        val stdin = shellStdin ?: return
        val stdout = shellStdout ?: return

        stdin.write("echo $INIT_MARKER\n".toByteArray())
        stdin.flush()

        val lineBuffer = StringBuilder()
        while (true) {
            val b = stdout.read()
            if (b == -1) throw IOException("Shell closed during initialization")
            if (b == '\n'.code) {
                if (lineBuffer.toString().trim() == INIT_MARKER) return
                lineBuffer.clear()
            } else {
                lineBuffer.append(b.toChar())
            }
        }
    }

    private fun closeConnectionLocked() {
        runCatching { shellStdin?.close() }
        runCatching { shellStdout?.close() }
        runCatching { process?.destroy() }
        process = null
        shellStdin = null
        shellStdout = null
    }

    /**
     * Release the persistent shell connection and all resources.
     */
    fun release() {
        synchronized(connectionLock) {
            closeConnectionLocked()
            LogHelper.showDebugInfo("ShellScreenCapture: connection released")
        }
    }

    /**
     * Capture the screen via persistent root shell.
     * Thread-safe; only one capture runs at a time. Retries once on connection failure.
     */
    suspend fun capture(asBitmap: Boolean): Any? = captureMutex.withLock {
        withContext(Dispatchers.IO) {
            captureInternal(asBitmap)
        }
    }

    private fun captureInternal(asBitmap: Boolean): Any? {
        val startTime = System.currentTimeMillis()

        // Retry once: first attempt may fail if the connection died between captures
        repeat(2) { attempt ->
            try {
                ensureConnection()
                val stdin = shellStdin ?: return null
                val stdout = shellStdout ?: return null

                val result = captureViaPng(stdin, stdout, asBitmap)

                if (result != null) {
                    val elapsed = System.currentTimeMillis() - startTime
                    LogHelper.showDebugInfo("ShellScreenCapture: captured in ${elapsed}ms")
                    return result
                }

                return null
            } catch (e: Exception) {
                LogHelper.showDebugInfo("ShellScreenCapture: attempt ${attempt + 1} failed: ${e.message}")
                synchronized(connectionLock) { closeConnectionLocked() }
                if (attempt > 0) return null
            }
        }
        return null
    }

    // ======================== PNG screencap ========================

    /**
     * Capture using `screencap -p` and parse PNG chunks from the stream.
     * PNG structure: 8-byte signature, then chunks until IEND.
     * Each chunk: 4-byte length (big-endian) + 4-byte type + length data + 4-byte CRC.
     */
    private fun captureViaPng(
        stdin: OutputStream,
        stdout: InputStream,
        asBitmap: Boolean
    ): Any? {
        stdin.write("screencap -p\n".toByteArray())
        stdin.flush()

        val pngBytes = readPngFromStream(stdout)

        val bitmap = BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.size)
            ?: run {
                LogHelper.showDebugInfo("ShellScreenCapture: Failed to decode PNG data (${pngBytes.size} bytes)")
                return null
            }

        if (asBitmap) return bitmap

        // Convert Bitmap to CaptureResult (raw RGBA buffer)
        val width = bitmap.width
        val height = bitmap.height
        val pixelStride = 4
        val rowStride = width * pixelStride
        val buffer = ByteBuffer.allocateDirect(height * rowStride)
        bitmap.copyPixelsToBuffer(buffer)
        buffer.flip()
        bitmap.recycle()

        return ScreenCaptureManager.CaptureResult(buffer, width, height, pixelStride, rowStride)
    }

    /**
     * Read a complete PNG from the stream by parsing its chunk structure.
     * Stops after the IEND chunk, leaving the stream positioned for the next command.
     */
    private fun readPngFromStream(stream: InputStream): ByteArray {
        val output = ByteArrayOutputStream()

        // 8-byte PNG signature
        val signature = readExactly(stream, 8)
        output.write(signature)

        // Validate PNG magic bytes
        val expectedSig = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        )
        if (!signature.contentEquals(expectedSig)) {
            throw IOException(
                "Invalid PNG signature: ${signature.joinToString(" ") { "%02X".format(it) }}"
            )
        }

        // Read chunks until IEND
        while (true) {
            // Chunk header: 4-byte data length + 4-byte type
            val chunkHeader = readExactly(stream, 8)
            output.write(chunkHeader)

            val length = ByteBuffer.wrap(chunkHeader, 0, 4).order(ByteOrder.BIG_ENDIAN).getInt()
            val type = String(chunkHeader, 4, 4, Charsets.US_ASCII)

            if (length !in 0..50_000_000) {
                throw IOException("PNG chunk '$type' has unreasonable length: $length")
            }

            // Read chunk data
            if (length > 0) {
                val data = readExactly(stream, length)
                output.write(data)
            }

            // Read 4-byte CRC
            val crc = readExactly(stream, 4)
            output.write(crc)

            if (type == "IEND") break
        }

        return output.toByteArray()
    }

    // ======================== Utility ========================

    /**
     * API 24-compatible check for whether a process is still running.
     * Uses exitValue() which throws IllegalThreadStateException if the process has not yet terminated.
     */
    private fun isProcessAlive(proc: Process): Boolean {
        return try {
            proc.exitValue()
            false
        } catch (_: IllegalThreadStateException) {
            true
        }
    }

    /**
     * Read exactly [size] bytes from [stream], blocking until all bytes are received.
     * Throws IOException if the stream closes before all bytes are read.
     */
    private fun readExactly(stream: InputStream, size: Int): ByteArray {
        val buf = ByteArray(size)
        var offset = 0
        while (offset < size) {
            val read = stream.read(buf, offset, size - offset)
            if (read == -1) throw IOException("Stream closed prematurely, read $offset/$size bytes")
            offset += read
        }
        return buf
    }
}
