package com.coc.zkqcode.jar.code.universal.network

import java.io.FilterOutputStream
import java.io.OutputStream

/**
 * 带进度回调的 OutputStream，包装下载文件写入流。
 * 参考 OpenList 的 ReaderUpdatingProgress 设计，在数据写入时实时上报进度。
 *
 * @param delegate    底层输出流
 * @param total       总字节数
 * @param onProgress  进度回调，参数 (已写入字节, 总字节)
 */
class ProgressOutputStream(
    delegate: OutputStream,
    private val total: Long,
    private val onProgress: (downloaded: Long, total: Long) -> Unit
) : FilterOutputStream(delegate) {

    private var written: Long = 0L

    override fun write(b: Int) {
        out.write(b)
        written++
        onProgress(written, total)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        out.write(b, off, len)
        written += len
        onProgress(written, total)
    }
}
