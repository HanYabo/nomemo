package com.han.nomemo

import android.content.Context
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class GroupAlbumStore(context: Context) {
    data class GroupAlbum(
        val albumId: String,
        val name: String,
        val description: String,
        val createdAt: Long,
        val recordIds: List<String>
    )

    private val prefs =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

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
                recordIds = parseRecordIds(obj.optJSONArray("record_ids"))
            )
        }
        return result.sortedByDescending { it.createdAt }
    }

    fun addAlbum(name: String, description: String): GroupAlbum {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val album = GroupAlbum(
            albumId = UUID.randomUUID().toString(),
            name = trimmedName,
            description = trimmedDescription,
            createdAt = System.currentTimeMillis(),
            recordIds = emptyList()
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
            )
        }
        prefs.edit().putString(KEY_ALBUMS, payload.toString()).apply()
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

    companion object {
        private const val PREF_NAME = "no_memo_group_albums"
        private const val KEY_ALBUMS = "albums"
    }
}
