package com.coc.zkqcode.jar.code.universal.network

import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Simple WebDAV helper using OkHttp.
 * Supports uploading (PUT) and downloading (GET) files with Basic Auth.
 * Operates on byte arrays so callers can handle file I/O via root shell if needed.
 * Automatically creates remote directories via MKCOL before uploading.
 */
object WebDAVHelper {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val octetStream = "application/octet-stream".toMediaType()
    private val xmlMedia = "application/xml".toMediaType()

    /**
     * Upload raw bytes to a remote WebDAV path.
     * Automatically creates parent directories via MKCOL if they don't exist.
     *
     * @param baseUrl    WebDAV server base URL (e.g. "http://192.168.1.100/zkq/")
     * @param username   Basic Auth username
     * @param password   Basic Auth password
     * @param remotePath Relative path under baseUrl (e.g. "zkqFiles/my-device/config/file")
     * @param data       File content as byte array
     * @return null on success, error message string on failure
     */
    fun upload(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String,
        data: ByteArray
    ): String? {
        return upload(baseUrl, username, password, remotePath, data.toRequestBody(octetStream))
    }

    /**
     * Upload a local file to a remote WebDAV path using a streaming request body.
     * Automatically creates parent directories via MKCOL if they don't exist.
     *
     * @return null on success, error message string on failure
     */
    fun upload(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String,
        file: File
    ): String? {
        return upload(baseUrl, username, password, remotePath, file.asRequestBody(octetStream))
    }

    private fun upload(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String,
        body: okhttp3.RequestBody
    ): String? {
        // Ensure parent directories exist
        val dirError = ensureRemotePath(baseUrl, username, password, remotePath)
        if (dirError != null) return dirError

        val credential = Credentials.basic(username, password)
        val url = buildUrl(baseUrl, remotePath)
        val request = Request.Builder()
            .url(url)
            .put(body)
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) null
            else "Upload failed: HTTP ${response.code} ${response.message}"
        } catch (e: Exception) {
            "Upload error: ${e.message}"
        }
    }

    /**
     * Download raw bytes from a remote WebDAV path.
     *
     * @return ByteArray on success, error message string on failure (prefixed with "ERROR: ")
     */
    fun download(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Any {
        val credential = Credentials.basic(username, password)
        val url = buildUrl(baseUrl, remotePath)
        val request = Request.Builder()
            .url(url)
            .get()
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return "ERROR: Download failed: HTTP ${response.code} ${response.message}"
            }
            response.body.bytes()
        } catch (e: Exception) {
            "ERROR: Download error: ${e.message}"
        }
    }

    /**
     * Send a PROPFIND request (Depth: 1) and return parsed entries.
     *
     * @return ParsedPropfindResult on success, error string on failure
     */
    private fun propfind(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Any {
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
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return "ERROR: List failed: HTTP ${response.code} ${response.message}"
            }
            val xml = response.body.string()
            parsePropfindResult(xml, baseUrl, remotePath)
        } catch (e: Exception) {
            "ERROR: List error: ${e.message}"
        }
    }

    private data class ParsedPropfindResult(
        val directories: List<String>,
        val files: List<String>
    )

    /**
     * Parse PROPFIND XML response using regex (handles any namespace prefix).
     * Extracts href values and checks for <collection/> tags.
     */
    private fun parsePropfindResult(
        xml: String,
        baseUrl: String,
        queryPath: String
    ): ParsedPropfindResult {
        val directories = mutableListOf<String>()
        val files = mutableListOf<String>()

        try {
            // Match each <D:response>...</D:response> block
            val responseRegex = Regex(
                "<[^>]*:response[^>]*>(.*?)</[^>]*:response>",
                setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
            )
            val hrefRegex = Regex(
                "<[^>]*:href[^>]*>(.*?)</[^>]*:href>",
                setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
            )
            val collectionRegex = Regex(
                "<[^>]*:collection[^>]*\\s*/>",
                setOf(RegexOption.IGNORE_CASE)
            )

            // Extract the query dir name for self-filtering
            val queryDirName = queryPath.trimEnd('/').substringAfterLast("/")

            val responseMatches = responseRegex.findAll(xml)

            for (respMatch in responseMatches) {
                val respXml = respMatch.groupValues[1]

                // Extract href value
                val hrefMatch = hrefRegex.find(respXml) ?: continue
                val href = java.net.URLDecoder.decode(hrefMatch.groupValues[1].trim(), "UTF-8")

                // Skip the queried directory itself
                val name = href.trimEnd('/').substringAfterLast("/")
                if (name.isBlank() || name == queryDirName) continue

                // Check if it's a collection
                val isCollection = collectionRegex.containsMatchIn(respXml)

                if (isCollection) directories.add(name) else files.add(name)
            }
        } catch (_: Exception) { }

        return ParsedPropfindResult(directories, files)
    }

    /**
     * List subdirectory names under a remote path.
     *
     * @return List<String> of directory names on success, error string on failure
     */
    fun listDirectories(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Any {
        val result = propfind(baseUrl, username, password, remotePath)
        return if (result is ParsedPropfindResult) result.directories
        else result as String
    }

    /**
     * List all files under a remote path.
     *
     * @return List<String> of file names on success, error string on failure
     */
    fun listFiles(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): Any {
        val result = propfind(baseUrl, username, password, remotePath)
        return if (result is ParsedPropfindResult) result.files
        else result as String
    }

    /**
     * Create all parent directories for a given remote path via MKCOL.
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

    /**
     * Delete a remote WebDAV resource.
     *
     * @return null on success, error message string on failure
     */
    fun deletePath(
        baseUrl: String,
        username: String,
        password: String,
        remotePath: String
    ): String? {
        val credential = Credentials.basic(username, password)
        val request = Request.Builder()
            .url(buildUrl(baseUrl, remotePath))
            .delete()
            .header("Authorization", credential)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) null
            else "Delete failed: HTTP ${response.code} ${response.message}"
        } catch (e: Exception) {
            "Delete error: ${e.message}"
        }
    }

    /**
     * Move/rename a remote WebDAV resource.
     *
     * @return null on success, error message string on failure
     */
    fun movePath(
        baseUrl: String,
        username: String,
        password: String,
        sourcePath: String,
        destinationPath: String
    ): String? {
        val credential = Credentials.basic(username, password)
        val sourceUrl = buildUrl(baseUrl, sourcePath)
        val destinationUrl = buildUrl(baseUrl, destinationPath)
        val request = Request.Builder()
            .url(sourceUrl)
            .method("MOVE", null)
            .header("Authorization", credential)
            .header("Destination", destinationUrl)
            .header("Overwrite", "F")
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) null
            else "Move failed: HTTP ${response.code} ${response.message}"
        } catch (e: Exception) {
            "Move error: ${e.message}"
        }
    }

    private fun buildUrl(baseUrl: String, path: String): String {
        val normalizedBase = baseUrl.trimEnd('/') + "/"
        val normalizedPath = path.trimStart('/')
        return normalizedBase + normalizedPath
    }
}
