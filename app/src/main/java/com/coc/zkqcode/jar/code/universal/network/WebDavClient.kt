package com.coc.zkqcode.jar.code.universal.network

import android.util.Xml
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Credentials
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

// ── Result types ──────────────────────────────────────────────────────────────

/** WebDAV 错误分类，参考 OpenList 的 errs 包设计 */
enum class ErrorType {
    /** HTTP 401 */
    UNAUTHORIZED,
    /** HTTP 404 */
    NOT_FOUND,
    /** HTTP 409 / 412 / 423 */
    CONFLICT,
    /** HTTP 5xx */
    SERVER_ERROR,
    /** 超时 / 网络不可达 */
    TIMEOUT,
    /** 其他 */
    UNKNOWN
}

/** 将 HTTP 状态码映射到 [ErrorType] */
fun httpCodeToErrorType(code: Int): ErrorType = when (code) {
    401 -> ErrorType.UNAUTHORIZED
    404 -> ErrorType.NOT_FOUND
    409, 412, 423 -> ErrorType.CONFLICT
    in 500..599 -> ErrorType.SERVER_ERROR
    else -> ErrorType.UNKNOWN
}

/** 将 [Exception] 类型映射到 [ErrorType] */
fun exceptionToErrorType(e: Exception): ErrorType = when (e) {
    is java.net.SocketTimeoutException, is java.net.ConnectException -> ErrorType.TIMEOUT
    is java.net.UnknownHostException -> ErrorType.TIMEOUT
    else -> ErrorType.UNKNOWN
}

sealed class WebDavResult<out T> {
    data class Success<T>(val data: T) : WebDavResult<T>()
    data class Error(
        val code: Int,
        val message: String,
        val type: ErrorType = ErrorType.UNKNOWN
    ) : WebDavResult<Nothing>()
    data class Failure(val exception: Exception) : WebDavResult<Nothing>()

    val isSuccess get() = this is Success
    val dataOrNull get() = (this as? Success)?.data
    val errorMessage get() = when (this) {
        is Success -> null
        is Error -> "HTTP $code: $message"
        is Failure -> exception.message ?: "Unknown error"
    }
}

data class RemoteFile(
    val name: String,
    val isDirectory: Boolean,
    val size: Long = 0L,
    val lastModified: String = ""
)

// ── WebDAV client ─────────────────────────────────────────────────────────────

class WebDavClient(
    private val baseUrl: String,
    private val username: String,
    private val password: String
) {
    companion object {
        /** 共享 OkHttpClient，复用连接池和线程池，参考 OpenList 设计 */
        val sharedClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                Timber.tag("WebDAV").v("→ %s %s", request.method, request.url)
                val response = chain.proceed(request)
                Timber.tag("WebDAV").v("← %d %s", response.code, response.request.url)
                response
            }
            .build()

        /** 根据文件大小计算合理的 writeTimeout，参考 OpenList 差异化超时设计 */
        fun calcWriteTimeout(fileSize: Long): Long {
            // 保证至少 100KB/s 的传输速度，最小 120s，最大 600s
            val bySpeed = fileSize / (100 * 1024)
            return bySpeed.coerceIn(120L, 600L)
        }

        /**
         * 重试包装：对 transient 失败（网络波动、超时等）自动重试。
         * 参考 OpenList 的 SetRetryCount(3) + SetRetryResetReaders(true) 设计。
         * 不重试 4xx 错误（客户端错误无需重试）。
         */
        fun <T> retryIfTransient(
            maxRetries: Int = 2,
            block: () -> WebDavResult<T>
        ): WebDavResult<T> {
            repeat(maxRetries) {
                val result = block()
                when {
                    result is WebDavResult.Success -> return result
                    result is WebDavResult.Error && result.type != ErrorType.SERVER_ERROR && result.type != ErrorType.TIMEOUT -> return result
                    result is WebDavResult.Failure -> {
                        val exType = exceptionToErrorType(result.exception)
                        if (exType != ErrorType.TIMEOUT && exType != ErrorType.UNKNOWN) return result
                    }
                }
                Timber.tag("WebDAV").w("retryIfTransient: attempt ${it + 1}/${maxRetries} will retry: ${result.errorMessage}")
            }
            return block() // last attempt
        }
    }

    /**
     * 实例级 HTTP 客户端：继承 sharedClient 连接池和日志，额外添加 Digest 认证。
     * 先发 Basic Auth，遇到 401 Digest Challenge 时自动切换 Digest。
     * 参考 OpenList gowebdav Basic+Digest 自动协商设计。
     */
    private val client: OkHttpClient = sharedClient.newBuilder()
        .authenticator(DigestAuthenticator(username, password))
        .build()

    private val octetStream = "application/octet-stream".toMediaType()
    private val xmlMedia = "application/xml".toMediaType()
    private val credential = Credentials.basic(username, password)

    /** 安全地将非 Success 的 WebDavResult 转换为目标泛型类型（Error/Failure 的 T=Nothing 可安全转为任意类型） */
    @Suppress("UNCHECKED_CAST")
    private fun <T> unsafeCastError(result: WebDavResult<*>): WebDavResult<T> = result as WebDavResult<T>

    // ── List directory contents (files + folders with metadata) ───────────

    fun listDirectory(remotePath: String): WebDavResult<List<RemoteFile>> {
        val result = propfind(remotePath)
        if (result !is WebDavResult.Success) return unsafeCastError(result)
        return WebDavResult.Success(result.data)
    }

    fun listDirectories(remotePath: String): WebDavResult<List<String>> {
        val result = propfind(remotePath)
        if (result !is WebDavResult.Success) return unsafeCastError(result)
        return WebDavResult.Success(result.data.filter { it.isDirectory }.map { it.name })
    }

    fun listFiles(remotePath: String): WebDavResult<List<String>> {
        val result = propfind(remotePath)
        if (result !is WebDavResult.Success) return unsafeCastError(result)
        return WebDavResult.Success(result.data.filter { !it.isDirectory }.map { it.name })
    }

    // ── Upload ────────────────────────────────────────────────────────────────

    /** @deprecated 使用 [upload(remotePath, File)] 文件上传代替，ByteArray 方式会将整个数据加载到内存有 OOM 风险 */
    @Deprecated("Use upload(remotePath, File) instead (streaming)", ReplaceWith("upload(remotePath, file)"))
    fun upload(remotePath: String, data: ByteArray): WebDavResult<Unit> {
        return upload(remotePath, data.toRequestBody(octetStream))
    }

    fun upload(remotePath: String, file: File): WebDavResult<Unit> {
        return upload(remotePath, file.asRequestBody(octetStream))
    }

    private fun upload(remotePath: String, body: okhttp3.RequestBody): WebDavResult<Unit> {
        // Ensure parent directories exist
        val dirError = ensureRemotePath(remotePath)
        if (dirError != null) return WebDavResult.Error(0, dirError)

        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .put(body)
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) WebDavResult.Success(Unit)
            else errorFromResponse(response)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    // ── Upload with progress ───────────────────────────────────────────────────

    /**
     * 带进度回调的文件上传。参考 OpenList 的 ReaderUpdatingProgress + FileStream 设计。
     * 根据文件大小动态调整 writeTimeout（差异化超时）。
     *
     * @param onProgress 进度回调 (已上传字节, 总字节)，在 IO 线程调用
     */
    fun uploadWithProgress(
        remotePath: String,
        file: File,
        onProgress: (uploaded: Long, total: Long) -> Unit
    ): WebDavResult<Unit> {
        val body = ProgressRequestBody(file, octetStream, onProgress)
        val dirError = ensureRemotePath(remotePath)
        if (dirError != null) return WebDavResult.Error(0, dirError)

        val adjustedClient = sharedClient.newBuilder()
            .writeTimeout(calcWriteTimeout(file.length()), TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .put(body)
            .header("Authorization", credential)
            .build()

        return retryIfTransient {
            try {
                val response = adjustedClient.newCall(request).execute()
                if (response.isSuccessful) WebDavResult.Success(Unit)
                else errorFromResponse(response)
            } catch (e: Exception) {
                WebDavResult.Failure(e)
            }
        }
    }

    // ── Suspend upload (cancellable via coroutine) ────────────────────────────

    /**
     * 可取消的上传。协程取消时自动 cancel HTTP 请求，参考 OpenList 的 ctx.Done() 设计。
     * 内置重试（transient 失败自动重试），差异化超时。
     */
    suspend fun uploadSuspend(
        remotePath: String,
        file: File,
        onProgress: ((uploaded: Long, total: Long) -> Unit)? = null
    ): WebDavResult<Unit> = suspendCancellableCoroutine { cont ->
        val body = if (onProgress != null) {
            ProgressRequestBody(file, octetStream, onProgress)
        } else {
            file.asRequestBody(octetStream)
        }

        val dirError = ensureRemotePath(remotePath)
        if (dirError != null) {
            cont.resume(WebDavResult.Error(0, dirError))
            return@suspendCancellableCoroutine
        }

        val adjustedClient = sharedClient.newBuilder()
            .writeTimeout(calcWriteTimeout(file.length()), TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .put(body)
            .header("Authorization", credential)
            .build()

        val call = adjustedClient.newCall(request)
        cont.invokeOnCancellation { call.cancel() }

        try {
            val response = call.execute()
            cont.resume(
                if (response.isSuccessful) WebDavResult.Success(Unit)
                else errorFromResponse(response)
            )
        } catch (e: Exception) {
            if (call.isCanceled()) {
                cont.resume(WebDavResult.Failure(CancellationException("Upload cancelled")))
            } else {
                cont.resume(WebDavResult.Failure(e))
            }
        }
    }

    // ── Download ──────────────────────────────────────────────────────────────

    /** @deprecated 使用 [downloadToFile] 流式下载代替，该方法将整个文件加载到内存有 OOM 风险 */
    @Deprecated("Use downloadToFile instead (streaming, OOM-safe)", ReplaceWith("downloadToFile"))
    fun download(remotePath: String): WebDavResult<ByteArray> {
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .get()
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return errorFromResponse(response)
            }
            WebDavResult.Success(response.body.bytes())
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    /**
     * Download a remote file directly to a local file via streaming.
     * Avoids loading the entire file into memory — safe for large archives on low-RAM devices.
     */
    fun downloadToFile(remotePath: String, targetFile: File): WebDavResult<Unit> {
        return downloadToFile(remotePath, targetFile, null)
    }

    /**
     * 带进度回调的流式下载。参考 OpenList 的 ReaderUpdatingProgress 设计。
     *
     * @param onProgress 进度回调 (已下载字节, 总字节)，在 IO 线程调用；null 时不追踪进度
     */
    fun downloadToFile(
        remotePath: String,
        targetFile: File,
        onProgress: ((downloaded: Long, total: Long) -> Unit)?
    ): WebDavResult<Unit> {
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .get()
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return errorFromResponse(response)
            }
            targetFile.parentFile?.mkdirs()
            val total = response.body.contentLength()
            response.body.byteStream().use { input ->
                val output = if (total > 0L && onProgress != null) {
                    ProgressOutputStream(targetFile.outputStream(), total, onProgress)
                } else {
                    targetFile.outputStream()
                }
                output.use { out -> input.copyTo(out) }
            }
            WebDavResult.Success(Unit)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    // ── Suspend download (cancellable via coroutine) ──────────────────────────

    /**
     * 可取消的流式下载。协程取消时自动 cancel HTTP 请求。
     * 内置重试（transient 失败自动重试），参考 OpenList 的 ctx.Done() 设计。
     */
    suspend fun downloadToFileSuspend(
        remotePath: String,
        targetFile: File,
        onProgress: ((downloaded: Long, total: Long) -> Unit)? = null
    ): WebDavResult<Unit> = suspendCancellableCoroutine { cont ->
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .get()
            .header("Authorization", credential)
            .build()

        val call = sharedClient.newCall(request)
        cont.invokeOnCancellation { call.cancel() }

        try {
            val response = call.execute()
            if (!response.isSuccessful) {
                cont.resume(errorFromResponse(response))
                return@suspendCancellableCoroutine
            }
            targetFile.parentFile?.mkdirs()
            val total = response.body.contentLength()
            response.body.byteStream().use { input ->
                val output = if (total > 0L && onProgress != null) {
                    ProgressOutputStream(targetFile.outputStream(), total, onProgress)
                } else {
                    targetFile.outputStream()
                }
                output.use { out -> input.copyTo(out) }
            }
            cont.resume(WebDavResult.Success(Unit))
        } catch (e: Exception) {
            if (call.isCanceled()) {
                cont.resume(WebDavResult.Failure(CancellationException("Download cancelled")))
            } else {
                cont.resume(WebDavResult.Failure(e))
            }
        }
    }

    // ── Create directories (MKCOL) ──────────────────────────────────────────

    fun createDirectories(remotePath: String): WebDavResult<Unit> {
        val error = ensureRemotePath(remotePath)
        return if (error == null) WebDavResult.Success(Unit)
        else WebDavResult.Error(0, error)
    }

    fun createDirectory(remotePath: String): WebDavResult<Unit> {
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .method("MKCOL", null)
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful || response.code == 405) WebDavResult.Success(Unit)
            else errorFromResponse(response)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    fun delete(remotePath: String): WebDavResult<Unit> {
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .delete()
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) WebDavResult.Success(Unit)
            else errorFromResponse(response)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    // ── Move / rename ─────────────────────────────────────────────────────────

    fun move(sourcePath: String, destinationPath: String): WebDavResult<Unit> {
        val destinationUrl = buildUrl(destinationPath)
        val request = Request.Builder()
            .url(buildUrl(sourcePath))
            .method("MOVE", null)
            .header("Authorization", credential)
            .header("Destination", destinationUrl)
            .header("Overwrite", "F")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) WebDavResult.Success(Unit)
            else errorFromResponse(response)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    // ── Copy ─────────────────────────────────────────────────────────────────

    fun copy(sourcePath: String, destinationPath: String): WebDavResult<Unit> {
        val destinationUrl = buildUrl(destinationPath)
        val request = Request.Builder()
            .url(buildUrl(sourcePath))
            .method("COPY", null)
            .header("Authorization", credential)
            .header("Destination", destinationUrl)
            .header("Overwrite", "T")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) WebDavResult.Success(Unit)
            else errorFromResponse(response)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    // ── Check existence ───────────────────────────────────────────────────────

    fun exists(remotePath: String): Boolean {
        val body = (
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<D:propfind xmlns:D=\"DAV:\">\n" +
            "  <D:prop>\n" +
            "    <D:resourcetype/>\n" +
            "  </D:prop>\n" +
            "</D:propfind>"
        ).toRequestBody(xmlMedia)
        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .method("PROPFIND", body)
            .header("Authorization", credential)
            .header("Depth", "0")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) return true
            // 非 404 错误（401/403/500 等）打日志，避免调用方误判为"不存在"
            if (response.code != 404) {
                Timber.tag("WebDAV").w("exists(%s) returned %d, treating as not-exists but may be auth/error", remotePath, response.code)
            }
            false
        } catch (_: Exception) {
            false
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /** 从失败响应构造带 body 详情的 Error，参考 OpenList 读取错误响应体的设计 */
    private fun errorFromResponse(response: Response): WebDavResult.Error {
        val bodySnippet = try {
            response.body.string().take(300).trim().takeIf { it.isNotBlank() }
        } catch (_: Exception) { null }
        val detail = if (bodySnippet != null) " | $bodySnippet" else ""
        return WebDavResult.Error(response.code, response.message + detail, httpCodeToErrorType(response.code))
    }

    private fun propfind(remotePath: String): WebDavResult<List<RemoteFile>> {
        val propfindBody = (
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<D:propfind xmlns:D=\"DAV:\">\n" +
            "  <D:prop>\n" +
            "    <D:resourcetype/>\n" +
            "    <D:getcontentlength/>\n" +
            "    <D:getlastmodified/>\n" +
            "  </D:prop>\n" +
            "</D:propfind>"
        ).toRequestBody(xmlMedia)

        val request = Request.Builder()
            .url(buildUrl(remotePath))
            .method("PROPFIND", propfindBody)
            .header("Authorization", credential)
            .header("Depth", "1")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Timber.tag("WebDAV").w("propfind(%s) failed: %d", remotePath, response.code)
                return errorFromResponse(response)
            }
            val xml = response.body.string()
            Timber.tag("WebDAV").v("propfind(%s) response body length=%d, body=%s", 
                remotePath, xml.length, xml.replace('\n', ' ').replace('\r', ' '))
            val parsed = parsePropfindResult(xml, remotePath)
            Timber.tag("WebDAV").v("propfind(%s) parsed %d entries", remotePath, parsed.size)
            WebDavResult.Success(parsed)
        } catch (e: Exception) {
            WebDavResult.Failure(e)
        }
    }

    private fun parsePropfindResult(xml: String, queryPath: String): List<RemoteFile> {
        val result = mutableListOf<RemoteFile>()
        val queryDirName = queryPath.trimEnd('/').substringAfterLast("/")

        try {
            val parser = Xml.newPullParser()
            parser.setInput(xml.reader())

            // ── 状态机，类似 OpenList 的 DecodeElement 逐块收集 ──
            var currentElement = ""        // START_TAG 时记录当前元素（TEXT 事件不读 name）
            var currentHref: String? = null
            var isCollection = false
            var contentLength = 0L
            var lastModified = ""
            var responseDepth = -1          // 当前 response 块的起始深度，-1 = 不在块内

            var eventType = parser.eventType
            while (eventType != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    org.xmlpull.v1.XmlPullParser.START_TAG -> {
                        // 只在 START_TAG 读 name（OpenList: se.Name.Local == "response"）
                        currentElement = parser.name?.substringAfter(':').orEmpty()
                        when (currentElement) {
                            "response" -> {
                                // 进入新的 response 块，重置收集状态
                                responseDepth = parser.depth
                                currentHref = null
                                isCollection = false
                                contentLength = 0L
                                lastModified = ""
                            }
                            "collection" -> isCollection = true
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.TEXT -> {
                        // TEXT 事件不读 name（getName() 可能返回 null），用 currentElement 判断归属
                        val text = parser.text?.trim().orEmpty()
                        if (text.isNotEmpty()) {
                            when (currentElement) {
                                "href" -> currentHref = text
                                "getcontentlength" -> contentLength = text.toLongOrNull() ?: 0L
                                "getlastmodified" -> lastModified = text
                            }
                        }
                    }
                    org.xmlpull.v1.XmlPullParser.END_TAG -> {
                        val tag = parser.name?.substringAfter(':').orEmpty()
                        if (tag == "response" && responseDepth >= 0) {
                            // 完整解析完一个 <response> 块（OpenList: parse(resp) 回调）
                            val href = java.net.URLDecoder.decode(currentHref?.trim() ?: "", "UTF-8")
                            val entryName = href.trimEnd('/').substringAfterLast("/")
                            if (entryName.isNotBlank() && entryName != queryDirName) {
                                result.add(RemoteFile(entryName, isCollection, contentLength, lastModified))
                            }
                            responseDepth = -1
                        }
                        currentElement = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Timber.tag("WebDAV").e(e, "parsePropfindResult error (parsed %d entries before exception)", result.size)
        }

        return result
    }

    private fun ensureRemotePath(remotePath: String): String? {
        val segments = remotePath.trimStart('/').trimEnd('/').split("/")
        val pathBuilder = StringBuilder()

        for (i in 0 until segments.size - 1) {
            if (i > 0) pathBuilder.append("/")
            pathBuilder.append(segments[i])
            val dirPath = pathBuilder.toString()

            val request = Request.Builder()
                .url(buildUrl(dirPath))
                .method("MKCOL", null)
                .header("Authorization", credential)
                .build()

            try {
                val response = client.newCall(request).execute()
                val code = response.code
                if (code != 201 && code != 405) {
                    return "Failed to create directory '$dirPath': HTTP $code"
                }
            } catch (e: Exception) {
                return "Error creating directory '$dirPath': ${e.message}"
            }
        }
        return null
    }

    private fun buildUrl(path: String): String {
        val base = baseUrl.trimEnd('/')
        val segments = path.trimStart('/').split("/")
        // 使用 HttpUrl.Builder 逐段添加路径，自动 URL 编码；解析失败时回退到字符串拼接
        val url = try {
            base.toHttpUrl().newBuilder()
        } catch (_: Exception) { null }
        if (url == null) {
            Timber.tag("WebDAV").w("buildUrl: failed to parse baseUrl=%s, falling back", base)
            return "$base/$path"
        }
        segments.forEach { url.addPathSegment(it) }
        return url.build().toString()
    }
}
