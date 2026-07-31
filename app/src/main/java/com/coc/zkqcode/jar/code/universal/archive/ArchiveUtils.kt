package com.coc.zkqcode.jar.code.universal.archive

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * WebDAV 存档操作共享工具函数。
 * 提取自 WebDavArchiveManager 和 SpecialSettings 中的重复代码。
 * 参考 OpenList 的 pkg/utils 设计，将通用工具函数集中管理。
 */
object ArchiveUtils {

    fun parseArchiveNumber(name: String): Int? {
        val raw = when {
            name.endsWith(".tar.gz") -> name.removeSuffix(".tar.gz")
            name.endsWith(".zip") -> name.removeSuffix(".zip")
            else -> return null
        }
        return raw.trim().toIntOrNull()
    }

    fun zipDirectory(sourceDir: File, targetZip: File) {
        targetZip.parentFile?.mkdirs()
        ZipOutputStream(FileOutputStream(targetZip)).use { zip ->
            sourceDir.walkTopDown().forEach { file ->
                val entryName = sourceDir.parentFile?.toPath()?.relativize(file.toPath())
                    ?.toString()?.replace('\\', '/') ?: return@forEach
                if (file.isDirectory) {
                    val dirName = if (entryName.endsWith('/')) entryName else "$entryName/"
                    zip.putNextEntry(ZipEntry(dirName))
                    zip.closeEntry()
                } else {
                    zip.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { input -> input.copyTo(zip) }
                    zip.closeEntry()
                }
            }
        }
    }

    fun unzipToDirectory(zipFile: File, destinationDir: File): Boolean {
        return try {
            destinationDir.mkdirs()
            ZipInputStream(zipFile.inputStream().buffered()).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val outFile = File(destinationDir, entry.name)
                    if (!outFile.canonicalPath.startsWith(destinationDir.canonicalPath + File.separator)) {
                        zip.closeEntry()
                        entry = zip.nextEntry
                        continue
                    }
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        outFile.outputStream().use { out -> zip.copyTo(out) }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun extractLegacyTarGz(archiveFile: File, destinationDir: File): Boolean {
        destinationDir.mkdirs()
        val command = "tar -xzf \"${archiveFile.absolutePath}\" -C \"${destinationDir.absolutePath}\""
        return com.coc.zkqcode.jar.code.ShellHelper.runBlocking(command).isSuccess
    }
}
