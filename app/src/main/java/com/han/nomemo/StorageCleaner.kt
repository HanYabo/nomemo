package com.han.nomemo

import android.content.Context
import java.io.File

data class StorageCleanCategory(
    val type: CleanType,
    val title: String,
    val description: String,
    val files: List<File>,
    val totalSize: Long,
    val recordIds: List<String> = emptyList()
) {
    val count: Int get() = files.size + recordIds.size
    val formattedSize: String get() = formatSize(totalSize)

    enum class CleanType {
        UNUSED_IMAGES,
        BROKEN_IMAGE_REFS,
        APK_FILES,
        CACHE_FILES,
        CRASH_LOGS,
        CORRUPTED_BACKUPS
    }

    companion object {
        fun formatSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
                bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
                else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
            }
        }
    }
}

object StorageCleaner {

    fun scanAll(context: Context, memoryStore: MemoryStore): List<StorageCleanCategory> {
        return listOf(
            scanUnusedImages(context, memoryStore),
            scanBrokenImageRefs(memoryStore),
            scanApkFiles(context),
            scanCacheFiles(context),
            scanCrashLogs(context),
            scanCorruptedBackups(context)
        )
    }

    fun scanUnusedImages(context: Context, memoryStore: MemoryStore): StorageCleanCategory {
        val imageDir = File(context.filesDir, "mem_images")
        if (!imageDir.exists() || !imageDir.isDirectory) {
            return StorageCleanCategory(
                type = StorageCleanCategory.CleanType.UNUSED_IMAGES,
                title = "未使用的图片",
                description = "无未使用的图片",
                files = emptyList(),
                totalSize = 0
            )
        }

        val referencedPaths = mutableSetOf<String>()
        memoryStore.loadRecords().forEach { record ->
            val imageUri = record.imageUri
            if (!imageUri.isNullOrEmpty()) {
                try {
                    val uri = android.net.Uri.parse(imageUri)
                    if (uri.scheme == "file") {
                        uri.path?.let { referencedPaths.add(it) }
                    }
                } catch (_: Exception) {}
            }
        }

        val unusedFiles = imageDir.listFiles()?.filter { file ->
            file.isFile && !referencedPaths.contains(file.absolutePath)
        } ?: emptyList()

        val totalSize = unusedFiles.sumOf { it.length() }
        val description = if (unusedFiles.isEmpty()) "无未使用的图片" else "${unusedFiles.size} 个文件，共 ${StorageCleanCategory.formatSize(totalSize)}"

        return StorageCleanCategory(
            type = StorageCleanCategory.CleanType.UNUSED_IMAGES,
            title = "未使用的图片",
            description = description,
            files = unusedFiles,
            totalSize = totalSize
        )
    }

    fun scanBrokenImageRefs(memoryStore: MemoryStore): StorageCleanCategory {
        val brokenRefs = mutableListOf<Pair<String, String>>()
        val records = memoryStore.loadRecords()

        records.forEach { record ->
            val imageUri = record.imageUri
            if (!imageUri.isNullOrEmpty()) {
                try {
                    val uri = android.net.Uri.parse(imageUri)
                    if (uri.scheme == "file") {
                        val path = uri.path
                        if (path != null) {
                            val file = File(path)
                            if (!file.exists()) {
                                brokenRefs.add(Pair(record.recordId, imageUri))
                            }
                        }
                    }
                } catch (_: Exception) {
                    brokenRefs.add(Pair(record.recordId, imageUri))
                }
            }
        }

        val description = if (brokenRefs.isEmpty()) "无损坏的图片引用" else "${brokenRefs.size} 条记录引用了不存在的图片"

        return StorageCleanCategory(
            type = StorageCleanCategory.CleanType.BROKEN_IMAGE_REFS,
            title = "损坏的图片引用",
            description = description,
            files = emptyList(),
            totalSize = 0,
            recordIds = brokenRefs.map { it.first }
        )
    }

    fun scanApkFiles(context: Context): StorageCleanCategory {
        val apkFiles = mutableListOf<File>()

        val searchDirs = listOf(
            context.filesDir,
            context.cacheDir,
            context.getExternalFilesDir(null),
            context.externalCacheDir
        ).filterNotNull().filter { it.exists() }

        searchDirs.forEach { dir ->
            findApkFiles(dir, apkFiles)
        }

        val totalSize = apkFiles.sumOf { it.length() }
        val description = if (apkFiles.isEmpty()) "无安装包文件" else "${apkFiles.size} 个文件，共 ${StorageCleanCategory.formatSize(totalSize)}"

        return StorageCleanCategory(
            type = StorageCleanCategory.CleanType.APK_FILES,
            title = "安装包文件",
            description = description,
            files = apkFiles,
            totalSize = totalSize
        )
    }

    private fun findApkFiles(dir: File, result: MutableList<File>) {
        try {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    findApkFiles(file, result)
                } else if (file.extension.equals("apk", ignoreCase = true)) {
                    result.add(file)
                }
            }
        } catch (_: Exception) {}
    }

    fun scanCacheFiles(context: Context): StorageCleanCategory {
        val cacheFiles = mutableListOf<File>()

        context.cacheDir?.let { cacheDir ->
            if (cacheDir.exists()) {
                collectFiles(cacheDir, cacheFiles, excludeDir = null)
            }
        }

        context.externalCacheDir?.let { externalCacheDir ->
            if (externalCacheDir.exists()) {
                collectFiles(externalCacheDir, cacheFiles, excludeDir = null)
            }
        }

        val totalSize = cacheFiles.sumOf { it.length() }
        val description = if (cacheFiles.isEmpty()) "无缓存文件" else "${cacheFiles.size} 个文件，共 ${StorageCleanCategory.formatSize(totalSize)}"

        return StorageCleanCategory(
            type = StorageCleanCategory.CleanType.CACHE_FILES,
            title = "缓存文件",
            description = description,
            files = cacheFiles,
            totalSize = totalSize
        )
    }

    fun scanCrashLogs(context: Context): StorageCleanCategory {
        val logFiles = mutableListOf<File>()

        val searchDirs = listOf(
            File(context.filesDir, "crash_logs"),
            File(context.filesDir, "logs"),
            File(context.cacheDir, "crash_logs"),
            context.filesDir,
            context.cacheDir
        ).filter { it.exists() }

        searchDirs.forEach { dir ->
            try {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && (file.extension.equals("log", ignoreCase = true) ||
                            file.name.contains("crash", ignoreCase = true))) {
                        logFiles.add(file)
                    }
                }
            } catch (_: Exception) {}
        }

        val totalSize = logFiles.sumOf { it.length() }
        val description = if (logFiles.isEmpty()) "无崩溃日志" else "${logFiles.size} 个文件，共 ${StorageCleanCategory.formatSize(totalSize)}"

        return StorageCleanCategory(
            type = StorageCleanCategory.CleanType.CRASH_LOGS,
            title = "崩溃日志",
            description = description,
            files = logFiles,
            totalSize = totalSize
        )
    }

    fun scanCorruptedBackups(context: Context): StorageCleanCategory {
        val backupDir = File(context.filesDir, "backups")
        val corruptedFiles = mutableListOf<File>()

        if (backupDir.exists() && backupDir.isDirectory) {
            backupDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    if (file.extension == "tmp" || file.length() == 0L || !isValidBackupFile(file)) {
                        corruptedFiles.add(file)
                    }
                }
            }
        }

        val totalSize = corruptedFiles.sumOf { it.length() }
        val description = if (corruptedFiles.isEmpty()) "无损坏的备份" else "${corruptedFiles.size} 个文件，共 ${StorageCleanCategory.formatSize(totalSize)}"

        return StorageCleanCategory(
            type = StorageCleanCategory.CleanType.CORRUPTED_BACKUPS,
            title = "损坏的备份",
            description = description,
            files = corruptedFiles,
            totalSize = totalSize
        )
    }

    private fun isValidBackupFile(file: File): Boolean {
        return try {
            if (file.extension != "json") return false
            val content = file.readText()
            content.isNotBlank() && (content.startsWith("{") || content.startsWith("["))
        } catch (_: Exception) {
            false
        }
    }

    private fun collectFiles(dir: File, result: MutableList<File>, excludeDir: File?) {
        try {
            dir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    if (file != excludeDir) {
                        collectFiles(file, result, excludeDir)
                    }
                } else {
                    result.add(file)
                }
            }
        } catch (_: Exception) {}
    }

    fun cleanFiles(files: List<File>): Int {
        var deletedCount = 0
        files.forEach { file ->
            try {
                if (file.exists() && file.delete()) {
                    deletedCount++
                }
            } catch (_: Exception) {}
        }
        return deletedCount
    }

    fun cleanBrokenImageRefs(memoryStore: MemoryStore, recordIds: List<String>): Int {
        var cleanedCount = 0
        recordIds.forEach { recordId ->
            try {
                if (memoryStore.clearImageUri(recordId)) {
                    cleanedCount++
                }
            } catch (_: Exception) {}
        }
        return cleanedCount
    }

    fun calculateTotalSize(categories: List<StorageCleanCategory>): Long {
        return categories.sumOf { it.totalSize }
    }
}
