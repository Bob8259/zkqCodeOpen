package com.coc.zkqcode.jar.code.universal.network

import com.coc.zkqcode.core.util.basic.ShowMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Utility for handling URL fallback logic.
 * Tries primary URL first, then falls back to backup URL if primary fails.
 */
object UrlFallbackUtil {

    const val PRIMARY_URL = "https://zkqcoc.store/"
    const val BACKUP_URL = "http://103.85.226.101/"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val shortTimeoutClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    /**
     * Gets the primary base URL
     */
    fun getPrimaryUrl(): String = PRIMARY_URL

    /**
     * Gets the backup base URL
     */
    fun getBackupUrl(): String = BACKUP_URL

    /**
     * Executes a network request with fallback logic.
     * First tries the primary URL, then the backup URL if primary fails.
     *
     * @param endpoint The API endpoint (e.g., "api/pow/challenge")
     * @param requestBuilder Lambda to build the request with a given base URL
     * @param useShortTimeout Whether to use short timeout client (for quick checks)
     * @return The successful Response
     * @throws IOException if both primary and backup URLs fail
     */
    suspend fun executeWithFallback(
        endpoint: String,
        requestBuilder: (baseUrl: String) -> Request,
        useShortTimeout: Boolean = false
    ): Response {
        val primaryUrl = getPrimaryUrl()
        val backupUrl = getBackupUrl()
        val client = if (useShortTimeout) shortTimeoutClient else httpClient

        // Try primary URL first
        val primaryRequest = requestBuilder(primaryUrl)
        val primaryException = try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(primaryRequest).execute()
            }
            if (response.isSuccessful) {
                return response
            } else {
                // Close unsuccessful response and try backup
                response.close()
                null
            }
        } catch (e: SocketTimeoutException) {
            e
        } catch (e: IOException) {
            e
        }

        // Primary failed, try backup URL
        if (backupUrl.isNotBlank()) {
            ShowMessage("主服务器连接失败，正在尝试备用服务器...")

            val backupRequest = requestBuilder(backupUrl)
            try {
                val response = withContext(Dispatchers.IO) {
                    client.newCall(backupRequest).execute()
                }
                if (response.isSuccessful) {
                    return response
                } else {
                    response.close()
                    throw IOException("Backup server returned error: ${response.code}")
                }
            } catch (e: Exception) {
                // Both failed
                throw IOException("Both primary and backup servers failed. Primary: ${primaryException?.message}, Backup: ${e.message}", e)
            }
        } else {
            // No backup URL configured
            throw IOException("Primary server failed and no backup URL configured: ${primaryException?.message}", primaryException)
        }
    }

    /**
     * Executes a simple GET request with fallback logic.
     *
     * @param endpoint The API endpoint (e.g., "api/pow/challenge")
     * @param useShortTimeout Whether to use short timeout client
     * @return The successful Response
     */
    suspend fun executeGetWithFallback(
        endpoint: String,
        useShortTimeout: Boolean = false
    ): Response {
        return executeWithFallback(
            endpoint = endpoint,
            requestBuilder = { baseUrl ->
                Request.Builder()
                    .url("${baseUrl}${endpoint}")
                    .get()
                    .build()
            },
            useShortTimeout = useShortTimeout
        )
    }

    /**
     * Executes a request with fallback and returns the response body as string.
     * Automatically closes the response after reading.
     *
     * @param endpoint The API endpoint
     * @param requestBuilder Lambda to build the request
     * @return Response body as string
     */
    suspend fun executeWithFallbackAndReadBody(
        endpoint: String,
        requestBuilder: (baseUrl: String) -> Request
    ): String {
        return executeWithFallback(endpoint, requestBuilder).use { response ->
            response.body.string()
        }
    }

    /**
     * Executes a GET request with fallback and returns the response body as string.
     *
     * @param endpoint The API endpoint
     * @return Response body as string
     */
    suspend fun executeGetWithFallbackAndReadBody(
        endpoint: String
    ): String {
        return executeGetWithFallback(endpoint).use { response ->
            response.body.string()
        }
    }

    /**
     * Checks if the primary URL is reachable.
     * Uses a HEAD request with short timeout.
     *
     * @return true if primary is reachable, false otherwise
     */
    suspend fun isPrimaryUrlReachable(): Boolean {
        return try {
            val request = Request.Builder()
                .url("${getPrimaryUrl()}")
                .head()
                .build()
            val response = withContext(Dispatchers.IO) {
                shortTimeoutClient.newCall(request).execute()
            }
            response.use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Checks if the backup URL is reachable.
     * Uses a HEAD request with short timeout.
     *
     * @return true if backup is reachable, false otherwise
     */
    suspend fun isBackupUrlReachable(): Boolean {
        val backupUrl = getBackupUrl()
        if (backupUrl.isBlank()) return false

        return try {
            val request = Request.Builder()
                .url(backupUrl)
                .head()
                .build()
            val response = withContext(Dispatchers.IO) {
                shortTimeoutClient.newCall(request).execute()
            }
            response.use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
