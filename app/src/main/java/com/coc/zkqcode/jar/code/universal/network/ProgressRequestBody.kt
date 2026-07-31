package com.coc.zkqcode.jar.code.universal.network

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

/**
 * 带进度回调的 RequestBody，参考 OpenList 的 ReaderUpdatingProgress 设计。
 * 包装文件上传流，在写入 OkHttp 的 sink 时实时上报已上传字节数。
 *
 * @param file       要上传的文件
 * @param contentType MIME 类型，默认 application/octet-stream
 * @param onProgress  进度回调，参数 (已上传字节, 总字节)
 */
class ProgressRequestBody(
    private val file: File,
    private val contentType: MediaType = "application/octet-stream".toMediaType(),
    private val onProgress: (uploaded: Long, total: Long) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType = contentType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val total = contentLength()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uploaded = 0L

        FileInputStream(file).use { input ->
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                sink.write(buffer, 0, read)
                uploaded += read
                onProgress(uploaded, total)
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8 * 1024 // 8KB
    }
}
