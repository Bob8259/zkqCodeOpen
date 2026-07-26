package com.coc.zkqcode.jar.code.universal.smalltools

import android.content.Context
import com.coc.zkqcode.core.util.basic.ShowMessage
import com.coc.zkqcode.core.util.fileactions.LogHelper.showDebugInfo
import com.coc.zkqcode.jar.code.ShellHelper
import com.coc.zkqcode.jar.ui.LanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * 国际服 (Global) Clash of Clans APK 自动下载与 Root 安装工具。
 *
 * 工作流程：
 *   1. 按用户选择的来源顺序尝试解析 APK 直链（APKPure / Supercell CDN / 自定义URL）
 *   2. 使用 OkHttp 将 APK 下载到应用私有文件目录 (filesDir/global_update/)
 *   3. 通过 cat | pm install -r -S 静默安装（绕过 SELinux 私有路径限制）
 *   4. 安装完成后删除临时 APK
 */
object GlobalApkDownloader {

    private const val GLOBAL_PACKAGE_NAME = "com.supercell.clashofclans"

    // ── APK 来源定义 ───────────────────────────────────────────────────────
    private data class Source(val name: String, val pageUrl: String, val altPageUrl: String? = null)

    /**
     * 来源索引（与 UI 下拉选项对齐）：
     *   0 = 无
     *   1 = APKPure
     *   2 = APKMirror
     *   3 = Supercell 官方 CDN
     *   4 = 自定义 URL
     */
    private val SOURCES: List<Source> = listOf(
        Source(
            name = "APKPure",
            pageUrl = "https://apkpure.net/clash-of-clans/$GLOBAL_PACKAGE_NAME/download",
            altPageUrl = "https://apkpure.net/$GLOBAL_PACKAGE_NAME/download"
        ),
        Source(
            name = "APKMirror",
            pageUrl = "https://www.apkmirror.com/apk/supercell/clash-of-clans/",
            altPageUrl = null
        ),
        Source(
            name = "Supercell 官方",
            pageUrl = "https://supercell.com/en/games/clashofclans/",
            altPageUrl = null
        )
    )

    // ── 正则（匹配 HTML/JS 中的 .apk 直链）────────────────────────────────
    private val APK_URL_REGEX: Pattern = Pattern.compile(
        "\"(https?://[^\"'\\s)]+\\.apk[^\"'\\s)]*)\"",
        Pattern.CASE_INSENSITIVE
    )

    private val HREF_APK_REGEX: Pattern = Pattern.compile(
        "href\\s*=\\s*[\"']([^\"']+\\.apk[^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE
    )

    private val DOWNLOAD_URL_JSON_REGEX: Pattern = Pattern.compile(
        "(?:download[Uu]?rl|apk[Uu]?rl|down[Ll]ink)\\s*[:=]\\s*[\"']([^\"']+)[\"']",
        Pattern.CASE_INSENSITIVE
    )

    // APKPure 特定：匹配 data-dt-fake 或类似属性中的下载链接
    private val APKPURE_DL_REGEX: Pattern = Pattern.compile(
        "data-dt-fake\\s*=\\s*[\"']([^\"']+\\.apk[^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE
    )

    // APKPure 特定：匹配 JSON-LD / structured data 中的 downloadUrl
    private val APKPURE_JSONLD_REGEX: Pattern = Pattern.compile(
        "\"downloadUrl\"\\s*:\\s*\"([^\"]+\\.apk[^\"]*)\"",
        Pattern.CASE_INSENSITIVE
    )

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    // ── 临时目录与文件名 ──────────────────────────────────────────────────
    private const val DL_DIR = "global_update"
    private const val APK_FILE_NAME = "global_update.apk"

    // ═════════════════════════════════════════════════════════════════════
    //  公开方法
    // ═════════════════════════════════════════════════════════════════════

    /**
     * 执行一次完整的国际服更新流程：解析 → 下载 → Root 安装 → 清理。
     *
     * @param context    Android 上下文
     * @param sourceIdx  来源索引（1=APKPure, 2=APKMirror, 3=Supercell官方, 4=自定义）
     * @param customUrl  当 sourceIdx == 4 时，用户提供的自定义 APK URL
     */
    suspend fun runEnhancedUpdate(
        context: Context,
        sourceIdx: Int = 1,
        customUrl: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        showMessage(StringsGlobal.msgUpdateStart)

        // 1. 解析直链
        val apkUrl = if (sourceIdx in 1..SOURCES.size) {
            resolveApkUrl(sourceIdx - 1)
        } else if (sourceIdx == 4 && !customUrl.isNullOrBlank()) {
            customUrl
        } else {
            null
        }

        if (apkUrl.isNullOrBlank()) {
            showMessage(StringsGlobal.msgResolveFailed)
            showDebugInfo("增强更新(国际服)：所有来源均无法解析到 APK 直链")
            return@withContext false
        }
        showMessage(StringsGlobal.msgResolved)
        showDebugInfo("增强更新(国际服)：已解析到 APK 直链 -> $apkUrl")

        // 2. 下载
        val dir = File(context.filesDir, DL_DIR).apply { mkdirs() }
        val apkFile = File(dir, APK_FILE_NAME)
        if (!downloadApk(apkUrl, apkFile)) {
            showMessage(StringsGlobal.msgDownloadFailed)
            apkFile.delete()
            return@withContext false
        }
        showMessage(StringsGlobal.msgDownloadDone(apkFile.length()))
        showDebugInfo("增强更新(国际服)：APK 下载完成，大小 ${apkFile.length()} 字节")

        // 3. Root 安装
        if (!installViaRoot(apkFile)) {
            showMessage(StringsGlobal.msgInstallFailed)
            apkFile.delete()
            return@withContext false
        }
        showMessage(StringsGlobal.msgInstallDone)

        // 4. 清理
        cleanup(apkFile)

        showMessage(StringsGlobal.msgAllDone)
        showDebugInfo("增强更新(国际服)：全部完成")
        return@withContext true
    }

    // ═════════════════════════════════════════════════════════════════════
    //  内部实现
    // ═════════════════════════════════════════════════════════════════════

    /**
     * 根据来源索引（0-based）依次尝试解析 APK 直链。
     */
    private suspend fun resolveApkUrl(sourceIndex: Int): String? {
        if (sourceIndex !in SOURCES.indices) return null
        val source = SOURCES[sourceIndex]
        showDebugInfo("增强更新(国际服)：尝试来源 [${source.name}] ${source.pageUrl}")

        return try {
            // 抓取页面 HTML
            val html = fetchPage(source.pageUrl)
                ?: source.altPageUrl?.let { fetchPage(it) }

            if (html.isNullOrBlank()) {
                showDebugInfo("增强更新(国际服)：${source.name} 页面为空")
                return null
            }
            showDebugInfo("增强更新(国际服)：${source.name} 页面大小 ${html.length} 字符")

            // 按优先级依次尝试所有正则
            val candidates = listOfNotNull(
                // APKPure JSON-LD
                extractFirst(APKPURE_JSONLD_REGEX, html),
                // APKPure data-dt-fake
                extractFirst(APKPURE_DL_REGEX, html),
                // JSON downloadUrl
                extractFirst(DOWNLOAD_URL_JSON_REGEX, html),
                // href 中的 .apk
                extractFirst(HREF_APK_REGEX, html),
                // 引号中的 .apk URL
                extractFirst(APK_URL_REGEX, html)
            )

            val first = candidates.firstOrNull()
            if (first != null) {
                showDebugInfo("增强更新(国际服)：${source.name} 解析到直链: $first")
            } else {
                showDebugInfo("增强更新(国际服)：${source.name} 未匹配到任何 APK 直链")
            }
            first
        } catch (e: Exception) {
            showDebugInfo("增强更新(国际服)：${source.name} 解析异常: ${e.message}")
            null
        }
    }

    /**
     * 用正则从 HTML 中提取第一个匹配组。
     */
    private fun extractFirst(pattern: Pattern, html: String): String? {
        val matcher = pattern.matcher(html)
        return if (matcher.find()) matcher.group(1) else null
    }

    /**
     * HTTP GET 请求获取页面内容。
     */
    private suspend fun fetchPage(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.9")
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    showDebugInfo("增强更新(国际服)：HTTP ${response.code} $url")
                    return@use null
                }
                response.body.string()
            }
        } catch (e: Exception) {
            showDebugInfo("增强更新(国际服)：请求异常 $url -> ${e.message}")
            null
        }
    }

    /**
     * 流式下载 APK 到目标文件。
     */
    private suspend fun downloadApk(url: String, target: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36")
                .header("Accept", "*/*")
                .build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    showDebugInfo("增强更新(国际服)：下载 HTTP ${response.code}")
                    return@withContext false
                }
                val body = response.body
                val contentLength = body.contentLength()
                showDebugInfo("增强更新(国际服)：APK 总大小 ${contentLength} 字节")

                FileOutputStream(target).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Long = 0
                    body.byteStream().use { input ->
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            bytesRead += read
                        }
                    }
                }

                if (target.exists() && target.length() > 0) {
                    showDebugInfo("增强更新(国际服)：下载完成，本地大小 ${target.length()} 字节")
                    true
                } else {
                    showDebugInfo("增强更新(国际服)：下载异常 — 文件为空")
                    false
                }
            }
        } catch (e: Exception) {
            showDebugInfo("增强更新(国际服)：下载异常: ${e.message}")
            false
        }
    }

    /**
     * 通过 Root 权限静默安装 APK。
     *
     * 使用 `cat apk_path | pm install -r -S <size>` 绕过 SELinux 对应用私有
     * 目录的读取限制。
     */
    private suspend fun installViaRoot(apkFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val size = apkFile.length()
            // 先尝试直接 pm install
            val cmd1 = "pm install -r -S $size \"${apkFile.absolutePath}\""
            val result1 = ShellHelper.runBlocking(cmd1)

            if (result1.out.any { it.contains("Success", ignoreCase = true) }) {
                showDebugInfo("增强更新(国际服)：直接 pm install 成功")
                return@withContext true
            }

            // 如果失败（SELinux 限制），用 cat 管道方式
            showDebugInfo("增强更新(国际服)：直接安装失败，尝试 cat 管道安装")
            val cmd2 = "cat \"${apkFile.absolutePath}\" | pm install -r -S $size"
            val result2 = ShellHelper.runBlocking(cmd2)

            if (result2.out.any { it.contains("Success", ignoreCase = true) }) {
                showDebugInfo("增强更新(国际服)：cat 管道安装成功")
                return@withContext true
            }

            showDebugInfo("增强更新(国际服)：Root 安装失败: $result2")
            false
        } catch (e: Exception) {
            showDebugInfo("增强更新(国际服)：Root 安装异常: ${e.message}")
            false
        }
    }

    /**
     * 删除临时 APK 文件。
     */
    private fun cleanup(apkFile: File) {
        try {
            if (apkFile.exists()) apkFile.delete()
        } catch (_: Throwable) {
            ShellHelper.runBlockingNoOutput("rm -f \"${apkFile.absolutePath}\"")
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    //  ShowMessage 快捷包装
    // ═════════════════════════════════════════════════════════════════════
    private fun showMessage(msg: String) {
        ShowMessage(msg)
    }
}

/** 国际服更新专用的中英文字符串（避免与 CNApkDownloader 的 Strings 冲突）。 */
private object StringsGlobal {
    val isZh get() = LanguageManager.isChinese

    val msgUpdateStart get() =
        if (isZh) "国际服增强更新：开始解析 APK 直链..." else "Global Enhanced Update: resolving APK URL..."
    val msgResolveFailed get() =
        if (isZh) "国际服增强更新：解析 APK 直链失败" else "Global Enhanced Update: failed to resolve APK URL"
    val msgResolved get() =
        if (isZh) "国际服增强更新：APK 直链解析成功，开始下载..." else "Global Enhanced Update: URL resolved, downloading..."
    val msgDownloadFailed get() =
        if (isZh) "国际服增强更新：下载失败" else "Global Enhanced Update: download failed"
    fun msgDownloadDone(bytes: Long) =
        if (isZh) "国际服增强更新：下载完成 (%.2f MB)，开始 Root 安装...".format(bytes / 1048576.0)
        else "Global Enhanced Update: download done (%.2f MB), root installing...".format(bytes / 1048576.0)
    val msgInstallFailed get() =
        if (isZh) "国际服增强更新：Root 安装失败，请查看调试信息" else "Global Enhanced Update: root install failed, check debug logs"
    val msgInstallDone get() =
        if (isZh) "国际服增强更新：安装成功，正在清理..." else "Global Enhanced Update: install succeeded, cleaning up..."
    val msgAllDone get() =
        if (isZh) "国际服增强更新：全部完成 ✓" else "Global Enhanced Update: all done ✓"
}
