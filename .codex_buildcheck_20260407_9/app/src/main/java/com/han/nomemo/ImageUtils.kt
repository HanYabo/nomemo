package com.han.nomemo

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ImageUtils {
    fun copyUriToCache(context: Context, uri: Uri): String? {
        return copyUriToPrivateStorage(context, uri)
    }

    fun copyUriToPrivateStorage(context: Context, uri: Uri): String? {
        return try {
            val resolver: ContentResolver = context.contentResolver
            val extension = resolveExtension(resolver, uri)
            val privateDir = privateImageDir(context)
            val target = File(privateDir, "nomemo_${System.currentTimeMillis()}.$extension")
            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(target).use { out ->
                    input.copyTo(out)
                }
            } ?: return null
            // return a proper file URI string so callers can parse with Uri.parse()
            Uri.fromFile(target).toString()
        } catch (_: Exception) {
            null
        }
    }

    fun migrateFileUriToPrivateStorage(context: Context, uri: Uri): String? {
        return try {
            if (uri.scheme != "file") return null
            val sourcePath = uri.path ?: return null
            val sourceFile = File(sourcePath)
            if (!sourceFile.exists() || !sourceFile.isFile) return null

            val privateDir = privateImageDir(context)
            if (isInsideDirectory(sourceFile, privateDir)) {
                return Uri.fromFile(sourceFile).toString()
            }

            val extension = sourceFile.extension.takeIf { it.isNotBlank() } ?: "jpg"
            val target = File(privateDir, "nomemo_${System.currentTimeMillis()}.$extension")
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(target).use { out ->
                    input.copyTo(out)
                }
            }
            Uri.fromFile(target).toString()
        } catch (_: Exception) {
            null
        }
    }

    private fun privateImageDir(context: Context): File {
        return File(context.filesDir, "mem_images").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private fun isInsideDirectory(file: File, parentDir: File): Boolean {
        return try {
            val filePath = file.canonicalPath
            val parentPath = parentDir.canonicalPath
            filePath == parentPath || filePath.startsWith("$parentPath${File.separator}")
        } catch (_: Exception) {
            false
        }
    }

    private fun resolveExtension(resolver: ContentResolver, uri: Uri): String {
        return try {
            val mime = resolver.getType(uri) ?: ""
            when {
                mime.contains("png") -> "png"
                mime.contains("webp") -> "webp"
                mime.contains("jpeg") || mime.contains("jpg") -> "jpg"
                else -> queryName(resolver, uri)?.substringAfterLast('.', "jpg") ?: "jpg"
            }
        } catch (_: Exception) {
            queryName(resolver, uri)?.substringAfterLast('.', "jpg") ?: "jpg"
        }
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor: Cursor? = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(0)
            }
        }
        return name
    }
}
