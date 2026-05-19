package com.han.nomemo

import android.content.Context
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class GroupAlbumStore(context: Context) {
    private val appContext = context.applicationContext

    data class GroupAlbum(
        val albumId: String,
        val name: String,
        val description: String,
        val createdAt: Long,
        val recordIds: List<String>,
        val organizeStatus: String
    )

    companion object {
        private const val PREF_NAME = "no_memo_group_albums"
        private const val KEY_ALBUMS = "albums"

        const val ORGANIZE_STATUS_IDLE = "idle"
        const val ORGANIZE_STATUS_PROCESSING = "processing"
        const val ORGANIZE_STATUS_COMPLETED = "completed"
        const val ORGANIZE_STATUS_FAILED = "failed"
    }

    private val prefs =
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun loadAlbums(): List<GroupAlbum> {
        val raw = prefs.getString(KEY_ALBUMS, "[]") ?: "[]"
        val json = runCatching { JSONArray(raw) }.getOrElse { JSONArray() }
        val result = mutableListOf<GroupAlbum>()
        for (index in 0 until json.length()) {
            val obj = json.optJSONObject(index) ?: continue
            val id = obj.optString("album_id").trim()
            val name = obj.optString("name").trim()
            if (id.isEmpty() || name.isEmpty()) {
                continue
            }
            result += GroupAlbum(
                albumId = id,
                name = name,
                description = obj.optString("description").trim(),
                createdAt = obj.optLong("created_at"),
                recordIds = parseRecordIds(obj.optJSONArray("record_ids")),
                organizeStatus = normalizeOrganizeStatus(obj.optString("organize_status"))
            )
        }
        return result
    }

    fun addAlbum(
        name: String,
        description: String,
        organizeStatus: String = ORGANIZE_STATUS_IDLE
    ): GroupAlbum {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val album = GroupAlbum(
            albumId = UUID.randomUUID().toString(),
            name = trimmedName,
            description = trimmedDescription,
            createdAt = System.currentTimeMillis(),
            recordIds = emptyList(),
            organizeStatus = normalizeOrganizeStatus(organizeStatus)
        )
        val next = loadAlbums().toMutableList()
        next.add(0, album)
        saveAlbums(next)
        return album
    }

    fun addRecordIds(albumId: String, recordIds: Collection<String>): Boolean {
        if (recordIds.isEmpty()) {
            return false
        }
        val incoming = recordIds.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (incoming.isEmpty()) {
            return false
        }
        val albums = loadAlbums().toMutableList()
        val index = albums.indexOfFirst { it.albumId == albumId }
        if (index < 0) {
            return false
        }
        val current = albums[index]
        val merged = (current.recordIds + incoming).distinct()
        if (merged == current.recordIds) {
            return false
        }
        albums[index] = current.copy(recordIds = merged)
        saveAlbums(albums)
        return true
    }

    fun removeRecordIds(albumId: String, recordIds: Collection<String>): Boolean {
        if (recordIds.isEmpty()) {
            return false
        }
        val removing = recordIds.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        if (removing.isEmpty()) {
            return false
        }
        val albums = loadAlbums().toMutableList()
        val index = albums.indexOfFirst { it.albumId == albumId }
        if (index < 0) {
            return false
        }
        val current = albums[index]
        val nextIds = current.recordIds.filterNot { removing.contains(it) }
        if (nextIds == current.recordIds) {
            return false
        }
        albums[index] = current.copy(recordIds = nextIds)
        saveAlbums(albums)
        return true
    }

    fun updateAlbum(
        albumId: String,
        name: String,
        description: String
    ): Boolean {
        val trimmedId = albumId.trim()
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        if (trimmedId.isEmpty() || trimmedName.isEmpty()) {
            return false
        }
        val albums = loadAlbums().toMutableList()
        val index = albums.indexOfFirst { it.albumId == trimmedId }
        if (index < 0) {
            return false
        }
        val current = albums[index]
        if (
            current.name == trimmedName &&
            current.description == trimmedDescription
        ) {
            return false
        }
        albums[index] = current.copy(
            name = trimmedName,
            description = trimmedDescription
        )
        saveAlbums(albums)
        return true
    }

    fun updateOrganizeStatus(albumId: String, organizeStatus: String): Boolean {
        val trimmedId = albumId.trim()
        if (trimmedId.isEmpty()) {
            return false
        }
        val albums = loadAlbums().toMutableList()
        val index = albums.indexOfFirst { it.albumId == trimmedId }
        if (index < 0) {
            return false
        }
        val normalized = normalizeOrganizeStatus(organizeStatus)
        val current = albums[index]
        if (current.organizeStatus == normalized) {
            return false
        }
        albums[index] = current.copy(organizeStatus = normalized)
        saveAlbums(albums)
        return true
    }

    fun findAlbumById(albumId: String): GroupAlbum? {
        val trimmedId = albumId.trim()
        if (trimmedId.isEmpty()) return null
        return loadAlbums().firstOrNull { it.albumId == trimmedId }
    }

    fun reorderAlbums(albumIdsInOrder: List<String>): Boolean {
        val currentAlbums = loadAlbums()
        if (currentAlbums.size <= 1) {
            return false
        }
        val idToAlbum = currentAlbums.associateBy { it.albumId }
        val requested = albumIdsInOrder
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .mapNotNull { idToAlbum[it] }
        if (requested.isEmpty()) {
            return false
        }
        val remainder = currentAlbums.filterNot { album ->
            requested.any { it.albumId == album.albumId }
        }
        val reordered = requested + remainder
        if (reordered.map { it.albumId } == currentAlbums.map { it.albumId }) {
            return false
        }
        saveAlbums(reordered)
        return true
    }

    fun deleteAlbum(albumId: String): Boolean {
        val trimmedId = albumId.trim()
        if (trimmedId.isEmpty()) {
            return false
        }
        val albums = loadAlbums()
        if (albums.none { it.albumId == trimmedId }) {
            return false
        }
        saveAlbums(albums.filterNot { it.albumId == trimmedId })
        return true
    }

    fun pruneInvalidRecordIds(validRecordIds: Set<String>): Boolean {
        val valid = validRecordIds.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val albums = loadAlbums()
        if (albums.isEmpty()) {
            return false
        }
        var changed = false
        val sanitized = albums.map { album ->
            val nextIds = album.recordIds.filter { valid.contains(it) }.distinct()
            if (nextIds != album.recordIds) {
                changed = true
                album.copy(recordIds = nextIds)
            } else {
                album
            }
        }
        if (!changed) {
            return false
        }
        saveAlbums(sanitized)
        return true
    }

    private fun saveAlbums(albums: List<GroupAlbum>) {
        val payload = JSONArray()
        albums.forEach { album ->
            val recordIdsJson = JSONArray()
            album.recordIds.forEach { recordId ->
                if (recordId.isNotBlank()) {
                    recordIdsJson.put(recordId)
                }
            }
            payload.put(
                JSONObject()
                    .put("album_id", album.albumId)
                    .put("name", album.name)
                    .put("description", album.description)
                    .put("created_at", album.createdAt)
                    .put("record_ids", recordIdsJson)
                    .put("organize_status", normalizeOrganizeStatus(album.organizeStatus))
            )
        }
        prefs.edit().putString(KEY_ALBUMS, payload.toString()).apply()
        GroupAlbumStoreNotifier.notifyChanged(appContext)
    }

    private fun parseRecordIds(raw: JSONArray?): List<String> {
        if (raw == null) {
            return emptyList()
        }
        val result = mutableListOf<String>()
        for (index in 0 until raw.length()) {
            val value = raw.optString(index).trim()
            if (value.isNotEmpty()) {
                result += value
            }
        }
        return result.distinct()
    }

    private fun normalizeOrganizeStatus(raw: String?): String {
        return when (raw?.trim()?.lowercase()) {
            ORGANIZE_STATUS_PROCESSING -> ORGANIZE_STATUS_PROCESSING
            ORGANIZE_STATUS_COMPLETED -> ORGANIZE_STATUS_COMPLETED
            ORGANIZE_STATUS_FAILED -> ORGANIZE_STATUS_FAILED
            else -> ORGANIZE_STATUS_IDLE
        }
    }
}
