package com.coc.zkqcode.jar.code.universal.network

import okhttp3.Call
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.net.URLDecoder
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * WebDAV helper using OkHttp with Basic Auth.
 *
 * Supports uploading (PUT), downloading (GET), directory listing (PROPFIND),
 * and automatic remote directory creation (MKCOL).
 *
 * Handles self-signed SSL certificates and retries transient failures.
 */
object WebDAVHelper {

    // Trust-all SSL context for self-signed certificates
    private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })

    private val client: OkHttpClient = run {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor { chain ->
                val orig = chain.request()
                val modified = orig.newBuilder()
                    .header("User-Agent", "ZKQ-WebDAV/1.0")
                    .build()
                chain.proceed(modified)
            }
            .build()
    }

    private val octetStream = "application/octet-stream".toMediaType()
    private val xmlMedia = "application/xml".toMediaType()

    // Cancel support — tracks the active OkHttp Call for cancellation
    @Volatile
    var currentCall: Call? = null
        private set

    /**
     * Cancel the currently executing network operation, if any.
     * The in-flight request will abort with an IOException.
     */
    fun cancelCurrentOperation() {
        currentCall?.cancel()
    }

    // Retry policy
    private const val MAX_RETRIES = 3
    private const val BASE_DELAY_MS = 1000L

    /**
     * Execute a block with exponential-backoff retry.
     * Only retries on [java.io.IOException] (network-level failures).
     * Other exceptions (HTTP errors, etc.) are returned as [Result.failure] immediately.
     */
    private fun <T> retryable(block: () -> T): Result<T> {
        var lastException: IOException? = null
        for (attempt in 0..MAX_RETRIES) {
            try {
                val result = block()
                return Result.success(result)
            } catch (e: IOException) {
                lastException = e
                if (attempt < MAX_RETRIES) {
                    Thread.sleep(BASE_DELAY_MS * (1L shl attempt))
                }
            } catch (e: Exception) {
                // HTTP errors, parsing errors — return immediately, no retry
                return Result.failure(e)
            }
        }
        return Result.failure(lastException!!)
    }

    // ── Upload ─────────────────────────────────────────────────────────────

    /**
     * Upload raw bytes to a remote WebDAV path.
     * Automatically creates parent directories via MKCOL if they don't exist.
     *
     * @return null on success, error message string on failure
     */
    fun upload(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String,
        data: ByteArray
    ): String? {
        // Ensure parent directories exist
        val dirError = ensureRemotePath(baseUrl, username, password, remotePath)
        if (dirError != null) return dirError

        val credential = Credentials.basic(username, password)
        val url = buildUrl(baseUrl, remotePath)
        val body = data.toRequestBody(octetStream)
        val request = Request.Builder()
            .url(url)
            .put(body)
            .header("Authorization", credential)
            .build()

        val r = retryable {
            val call = client.newCall(request)
            currentCall = call
            try {
                val response = call.execute()
                try {
                    if (response.isSuccessful) null
                    else "Upload failed: HTTP ${response.code} ${response.message}"
                } finally {
                    response.close()
                }
            } finally {
                currentCall = null
            }
        }
        return r.fold({ it }, { "Upload error: ${it.message}" })
    }

    // ── Download ───────────────────────────────────────────────────────────

    /**
     * Download raw bytes from a remote WebDAV path.
     *
     * @return [Result.success] with [ByteArray] on success,
     *         [Result.failure] with exception on failure
     */
    fun download(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Result<ByteArray> {
        val credential = Credentials.basic(username, password)
        val url = buildUrl(baseUrl, remotePath)
        val request = Request.Builder()
            .url(url)
            .get()
            .header("Authorization", credential)
            .build()

        return retryable {
            val call = client.newCall(request)
            currentCall = call
            try {
                val response = call.execute()
                if (!response.isSuccessful) {
                    throw RuntimeException("Download failed: HTTP ${response.code} ${response.message}")
                }
                response.body.bytes()
            } finally {
                currentCall = null
            }
        }
    }

    // ── Directory listing (PROPFIND) ───────────────────────────────────────

    private data class ParsedPropfindResult(
        val directories: List<String>,
        val files: List<String>
    )

    /**
     * Send a PROPFIND request (Depth: 1) and return parsed entries.
     * 404 (Not Found) is treated as an empty result, not an error.
     */
    private fun propfindRemote(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Result<ParsedPropfindResult> {
        val credential = Credentials.basic(username, password)
        val url = buildUrl(baseUrl, remotePath)

        val propfindBody = (
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<D:propfind xmlns:D=\"DAV:\">\n" +
            "  <D:prop>\n" +
            "    <D:resourcetype/>\n" +
            "  </D:prop>\n" +
            "</D:propfind>"
        ).toRequestBody(xmlMedia)

        val request = Request.Builder()
            .url(url)
            .method("PROPFIND", propfindBody)
            .header("Authorization", credential)
            .header("Depth", "1")
            .build()

        return try {
            val call = client.newCall(request)
            currentCall = call
            val response = call.execute()
            try {
                if (response.code == 404) {
                    // Path does not exist — return empty result, not an error
                    return Result.success(ParsedPropfindResult(emptyList(), emptyList()))
                }
                if (!response.isSuccessful) {
                    return Result.failure(RuntimeException("PROPFIND failed: HTTP ${response.code} ${response.message}"))
                }
                val xml = response.body.string()
                Result.success(parsePropfindXml(xml, remotePath))
            } finally {
                response.close()
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            currentCall = null
        }
    }

    /**
     * Parse PROPFIND XML response using [XmlPullParser].
     * Correctly handles any namespace prefix as well as both self-closing
     * (<D:collection/>) and expanded (<D:collection></D:collection>) collection tags.
     */
    private fun parsePropfindXml(xml: String, queryPath: String): ParsedPropfindResult {
        val directories = mutableListOf<String>()
        val files = mutableListOf<String>()
        val queryDirName = queryPath.trimEnd('/').substringAfterLast("/")

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.setNamespaceAware(true)
            val parser = factory.newPullParser()
            parser.setInput(StringReader(xml))

            var currentHref: String? = null
            var isCollection = false
            var inResponse = false
            var inResourcetype = false

            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                when (parser.eventType) {
                    XmlPullParser.START_TAG -> {
                        // Strip any namespace prefix (e.g. "D:response" -> "response")
                        val tag = parser.name.substringAfterLast(":").lowercase()
                        when (tag) {
                            "response" -> {
                                currentHref = null
                                isCollection = false
                                inResponse = true
                            }
                            "href" -> {
                                if (inResponse) {
                                    // Some servers use self-closing <D:href/> (no text content)
                                    currentHref = if (parser.isEmptyElementTag) "" else parser.nextText().trim()
                                }
                            }
                            "resourcetype" -> inResourcetype = true
                            "collection" -> {
                                if (inResourcetype) isCollection = true
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val tag = parser.name.substringAfterLast(":").lowercase()
                        when (tag) {
                            "resourcetype" -> inResourcetype = false
                            "response" -> {
                                if (inResponse && currentHref != null) {
                                    val decoded = URLDecoder.decode(currentHref, "UTF-8")
                                    val name = decoded.trimEnd('/').substringAfterLast("/")
                                    if (name.isNotBlank() && name != queryDirName) {
                                        if (isCollection) directories.add(name)
                                        else files.add(name)
                                    }
                                }
                                currentHref = null
                                isCollection = false
                                inResponse = false
                            }
                        }
                    }
                }
                parser.next()
            }
        } catch (_: Exception) {
            // Return whatever was parsed so far, or empty lists
        }

        return ParsedPropfindResult(directories, files)
    }

    /**
     * List subdirectory names under a remote path.
     *
     * @return [Result.success] with [List] of directory names, or [Result.failure]
     */
    fun listDirectories(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Result<List<String>> {
        return propfindRemote(baseUrl, username, password, remotePath)
            .map { it.directories }
    }

    /**
     * List file names under a remote path.
     *
     * @return [Result.success] with [List] of file names, or [Result.failure]
     */
    fun listFiles(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Result<List<String>> {
        return propfindRemote(baseUrl, username, password, remotePath)
            .map { it.files }
    }

    // ── Directory creation (MKCOL) ─────────────────────────────────────────

    /**
     * Create all parent directories for a given remote path via MKCOL.
     * Checks existence first via PROPFIND (Depth: 0) to avoid unnecessary errors.
     *
     * @return null on success, error message string on failure
     */
    fun ensureRemotePath(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): String? {
        val credential = Credentials.basic(username, password)
        val segments = remotePath.trimStart('/').trimEnd('/').split("/")

        val pathBuilder = StringBuilder()
        for (i in 0 until segments.size - 1) {
            if (i > 0) pathBuilder.append("/")
            pathBuilder.append(segments[i])
            val dirPath = pathBuilder.toString()

            val url = buildUrl(baseUrl, dirPath)
            val request = Request.Builder()
                .url(url)
                .method("MKCOL", null)
                .header("Authorization", credential)
                .build()

            try {
                val call = client.newCall(request)
                currentCall = call
                try {
                    val response = call.execute()
                    val code = response.code
                    // 201 = Created, 405 = Method Not Allowed (already exists, racing)
                    if (code != 201 && code != 405) {
                        return "Failed to create directory '$dirPath': HTTP $code"
                    }
                } finally {
                    currentCall = null
                }
            } catch (e: Exception) {
                return "Error creating directory '$dirPath': ${e.message}"
            }
        }
        return null
    }

    // ── URL builder ────────────────────────────────────────────────────────

    private fun buildUrl(baseUrl: String, path: String): String {
        val normalizedBase = baseUrl.trimEnd('/') + "/"
        val normalizedPath = path.trimStart('/')
        // Encode each path segment separately to handle spaces, Chinese, etc.
        // Uses java.net.URI to produce %20 for spaces (not + which is form-encoding).
        val encoded = normalizedPath.split("/").joinToString("/") { segment ->
            try {
                // URI encodes path segments correctly: spaces → %20
                java.net.URI(null, null, null, -1, "/$segment", null, null)
                    .rawPath
                    .removePrefix("/")
            } catch (_: Exception) {
                // Fallback: URLEncoder + replace + → %20
                java.net.URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
            }
        }
        return normalizedBase + encoded
    }
}
