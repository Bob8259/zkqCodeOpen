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
 * 国服游戏 APK 自动下载与 Root 安装工具。
 *
 * 工作流程：
 *   1. 按用户选择的来源顺序尝试解析 APK 直链（应用宝 / 腾讯官网）
 *   2. 使用 OkHttp 将 APK 下载到应用私有文件目录 (filesDir/cn_update/)
 *   3. 通过 cat | pm install -r -S 静默安装（绕过 SELinux 私有路径限制）
 *   4. 安装完成后删除临时 APK
 */
object CNApkDownloader {

    private const val CN_PACKAGE_NAME = "com.tencent.tmgp.supercell.clashofclans"

    private val SOURCES: List<Source> = listOf(
        Source(
            name = "应用宝",
            pageUrl = "https://a.app.qq.com/o/simple.jsp?pkgname=$CN_PACKAGE_NAME",
            altPageUrl = "https://sj.qq.com/appdetail/$CN_PACKAGE_NAME"
        ),
        Source(
            name = "腾讯官网",
            pageUrl = "https://coc.qq.com/zlkdatasys/mct/d/play.shtml?device=android",
            altPageUrl = "https://coc.qq.com/"
        )
    )

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

    private val EAGLECFG_JS_REGEX: Pattern = Pattern.compile(
        "<script[^>]+src=[\"']([^\"']*img\\.itop\\.qq\\.com/js/\\d+\\.js[^\"']*)[\"']",
        Pattern.CASE_INSENSITIVE
    )

    private val EAGLECFG_ANDROID_URL_REGEX: Pattern = Pattern.compile(
        "androidURL\\s*[:=]\\s*['\"]([^'\"]+\\.apk[^'\"]*)['\"]",
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

    private data class Source(val name: String, val pageUrl: String, val altPageUrl: String? = null)

    /**
     * 执行一次完整的增强更新流程：解析 → 下载 → Root 安装 → 清理
     *
     * @param preferredSource 优先解析来源索引（0=应用宝，1=腾讯官网）
     */
    suspend fun runEnhancedUpdate(context: Context, preferredSource: Int = 0): Boolean {
        showMessage(Strings.msgUpdateStart)

        val apkUrl = resolveApkUrl(preferredSource)
        if (apkUrl == null) {
            showMessage(Strings.msgResolveFailed)
            showDebugInfo("增强更新：所有来源均无法解析到 APK 直链")
            return false
        }
        showMessage(Strings.msgResolved)
        showDebugInfo("增强更新：已解析到 APK 直链 -> $apkUrl")

        val dir = File(context.filesDir, "cn_update").apply { mkdirs() }
        val apkFile = File(dir, "update.apk")
        if (!downloadApk(apkUrl, apkFile)) {
            showMessage(Strings.msgDownloadFailed)
            apkFile.delete()
            return false
        }
        showMessage(Strings.msgDownloadDone(apkFile.length()))
        showDebugInfo("增强更新：APK 下载完成，大小 ${apkFile.length()} 字节")

        if (!installViaRoot(apkFile)) {
            showMessage(Strings.msgInstallFailed)
            apkFile.delete()
            return false
        }
        showMessage(Strings.msgInstallDone)

        val cleaned = try {
            apkFile.delete()
            true
        } catch (_: Throwable) {
            ShellHelper.runBlockingNoOutput("rm -f \"${apkFile.absolutePath}\"")
            true
        }
        showDebugInfo("增强更新：APK 清理完成 = $cleaned")
        showMessage(Strings.msgAllDone)
        return true
    }

    private suspend fun resolveApkUrl(preferredSource: Int = 0): String? {
        val orderedSources = if (preferredSource in SOURCES.indices) {
            listOf(SOURCES[preferredSource]) + SOURCES.filterIndexed { i, _ -> i != preferredSource }
        } else SOURCES
        for (source in orderedSources) {
            showDebugInfo("增强更新：尝试解析 ${source.name} (${source.pageUrl})")
            try {
                val html = fetchPage(source.pageUrl)

                if (source.pageUrl.contains("coc.qq.com") || source.pageUrl.contains("zlkdatasys")) {
                    val eagleUrl = resolveFromEagleCfg(html)
                    if (eagleUrl != null) {
                        showDebugInfo("增强更新：从 EAGLECFG 解析到 APK: $eagleUrl")
                        return eagleUrl
                    }
                }

                val url = parseApkUrl(html)
                if (url != null) return url

                if (!source.altPageUrl.isNullOrBlank()) {
                    val html2 = fetchPage(source.altPageUrl)
                    val url2 = parseApkUrl(html2)
                    if (url2 != null) return url2
                }
            } catch (t: Throwable) {
                showDebugInfo("增强更新：解析 ${source.name} 异常: ${t.message}")
            }
        }
        return null
    }

    private suspend fun resolveFromEagleCfg(html: String): String? {
        if (html.isBlank()) return null

        val jsMatcher = EAGLECFG_JS_REGEX.matcher(html)
        if (!jsMatcher.find()) {
            showDebugInfo("增强更新：腾讯官网页面未找到 EAGLECFG JS 引用")
            return null
        }
        val rawJsUrl = jsMatcher.group(1)
        val jsUrl = normalizeUrl(rawJsUrl) ?: run {
            showDebugInfo("增强更新：EAGLECFG JS URL 解析失败: $rawJsUrl")
            return null
        }
        showDebugInfo("增强更新：找到 EAGLECFG JS -> $jsUrl")

        val jsContent = fetchPage(jsUrl)
        if (jsContent.isBlank()) {
            showDebugInfo("增强更新：EAGLECFG JS 内容为空")
            return null
        }

        val urlMatcher = EAGLECFG_ANDROID_URL_REGEX.matcher(jsContent)
        if (!urlMatcher.find()) {
            showDebugInfo("增强更新：EAGLECFG JS 中未找到 androidURL")
            return null
        }
        val apkUrl = normalizeUrl(urlMatcher.group(1))
        showDebugInfo("增强更新：EAGLECFG androidURL -> $apkUrl")
        return apkUrl
    }

    private suspend fun fetchPage(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            .get()
            .build()
        httpClient.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) {
                showDebugInfo("增强更新：fetchPage $url 返回 ${resp.code}")
                return@use ""
            }
            resp.body.string()
        }
    }

    private fun parseApkUrl(html: String): String? {
        if (html.isBlank()) return null

        val jsonMatcher = DOWNLOAD_URL_JSON_REGEX.matcher(html)
        if (jsonMatcher.find()) {
            val url = normalizeUrl(jsonMatcher.group(1))
            if (url != null) return url
        }

        val hrefMatcher = HREF_APK_REGEX.matcher(html)
        if (hrefMatcher.find()) {
            val url = normalizeUrl(hrefMatcher.group(1))
            if (url != null) return url
        }

        val urlMatcher = APK_URL_REGEX.matcher(html)
        if (urlMatcher.find()) {
            val url = normalizeUrl(urlMatcher.group(1))
            if (url != null) return url
        }
        return null
    }

    private fun normalizeUrl(raw: String?): String? {
        if (raw == null || raw.isBlank()) return null
        val cleaned = raw.trim()
            .replace("\\/", "/")
            .replace("&amp;", "&")
        return when {
            cleaned.startsWith("http://") || cleaned.startsWith("https://") -> cleaned
            cleaned.startsWith("//") -> "https:$cleaned"
            else -> null
        }
    }

    private suspend fun downloadApk(url: String, outFile: File): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36")
                .get()
                .build()

            return@withContext httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    showDebugInfo("增强更新：下载 APK 返回 ${response.code}")
                    return@use false
                }

                val body = response.body
                val totalBytes = body.contentLength().let { if (it < 0) 0L else it }

                val parentDir = outFile.parentFile
                if (parentDir != null && !parentDir.exists()) parentDir.mkdirs()
                if (outFile.exists()) outFile.delete()

                var downloaded = 0L
                val buffer = ByteArray(8192)
                var lastProgress = System.currentTimeMillis()

                body.byteStream().use { ins ->
                    FileOutputStream(outFile).use { out ->
                        while (true) {
                            val n = ins.read(buffer)
                            if (n <= 0) break
                            out.write(buffer, 0, n)
                            downloaded += n
                            val now = System.currentTimeMillis()
                            if (now - lastProgress > 3000) {
                                lastProgress = now
                                val mb = downloaded / 1048576.0
                                if (totalBytes > 0) {
                                    val pct = (downloaded * 100 / totalBytes).toInt()
                                    val totalMb = totalBytes / 1048576.0
                                    showDebugInfo("增强更新：下载进度 %.2fMB / %.2fMB (%d%%)".format(mb, totalMb, pct))
                                    if (pct % 25 == 0) {
                                        showMessage(if (LanguageManager.isChinese) "下载中：${pct}%" else "Downloading: ${pct}%")
                                    }
                                } else {
                                    showDebugInfo("增强更新：已下载 %.2fMB".format(mb))
                                }
                            }
                        }
                        out.flush()
                    }
                }

                if (downloaded < 1024) {
                    showDebugInfo("增强更新：下载文件过小 ($downloaded 字节)，可能失败")
                    outFile.delete()
                    return@use false
                }
                true
            }
        } catch (t: Throwable) {
            showDebugInfo("增强更新：下载异常 ${t.javaClass.simpleName}: ${t.message}")
            outFile.delete()
            false
        }
    }

    /**
     * Root install via stdin streaming — bypasses SELinux restrictions on private storage paths.
     * Same pattern as AppUpdateChecker and AiDetector.
     */
    private fun installViaRoot(apkFile: File): Boolean {
        val size = apkFile.length()
        if (size < 1024) {
            showDebugInfo("增强更新：APK 文件过小 ($size 字节)")
            return false
        }
        showDebugInfo("增强更新：Root 安装 cat | pm install -r -S $size")
        val r = ShellHelper.runBlocking("cat \"${apkFile.absolutePath}\" | pm install -r -S $size")
        val output = (r.out + r.err).joinToString("\n")
        showDebugInfo("增强更新：pm install output=$output")
        if (!(r.isSuccess || output.contains("Success", ignoreCase = true))) return false

        val verify = ShellHelper.runBlocking("pm path \"$CN_PACKAGE_NAME\" 2>/dev/null")
        return verify.isSuccess && verify.out.any { it.contains(CN_PACKAGE_NAME) }
    }

    private fun showMessage(text: String) {
        ShowMessage(text, false)
    }

    private object Strings {
        private val isZh: Boolean get() = LanguageManager.isChinese
        val msgUpdateStart: String
            get() = if (isZh) "增强更新：开始解析下载源..." else "Enhanced Update: resolving sources..."
        val msgResolveFailed: String
            get() = if (isZh) "增强更新：未能解析到 APK 下载地址" else "Enhanced Update: failed to resolve APK URL"
        val msgResolved: String
            get() = if (isZh) "增强更新：解析到下载链接" else "Enhanced Update: resolved APK URL"
        val msgDownloadFailed: String
            get() = if (isZh) "增强更新：APK 下载失败" else "Enhanced Update: APK download failed"
        fun msgDownloadDone(bytes: Long): String
            = if (isZh) "增强更新：下载完成 (%.2f MB)，开始 Root 安装...".format(bytes / 1048576.0)
              else "Enhanced Update: download done (%.2f MB), root installing...".format(bytes / 1048576.0)
        val msgInstallFailed: String
            get() = if (isZh) "增强更新：Root 安装失败，请查看调试信息" else "Enhanced Update: root install failed, check debug logs"
        val msgInstallDone: String
            get() = if (isZh) "增强更新：安装成功，正在清理..." else "Enhanced Update: install succeeded, cleaning up..."
        val msgAllDone: String
            get() = if (isZh) "增强更新：全部完成 ✓" else "Enhanced Update: all done ✓"
    }
}
