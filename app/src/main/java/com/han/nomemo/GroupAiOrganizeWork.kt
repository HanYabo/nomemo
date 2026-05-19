package com.han.nomemo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val GROUP_AI_ORGANIZE_WORK_NAME_PREFIX = "nomemo.group.organize."
private const val GROUP_AI_ORGANIZE_ALBUM_ID = "album_id"

object GroupAiOrganizeWorkScheduler {
    @JvmStatic
    fun enqueue(context: Context, albumId: String) {
        if (albumId.isBlank()) return
        val request = OneTimeWorkRequestBuilder<GroupAiOrganizeWorker>()
            .setInputData(Data.Builder().putString(GROUP_AI_ORGANIZE_ALBUM_ID, albumId).build())
            .build()
        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            GROUP_AI_ORGANIZE_WORK_NAME_PREFIX + albumId,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    @JvmStatic
    fun recoverProcessingAlbums(context: Context) {
        val appContext = context.applicationContext
        val albumStore = GroupAlbumStore(appContext)
        albumStore.loadAlbums()
            .filter { it.organizeStatus == GroupAlbumStore.ORGANIZE_STATUS_PROCESSING }
            .forEach { enqueue(appContext, it.albumId) }
    }
}

class GroupAiOrganizeWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val albumId = inputData.getString(GROUP_AI_ORGANIZE_ALBUM_ID)?.trim().orEmpty()
        if (albumId.isEmpty()) return Result.success()

        val appContext = applicationContext
        val albumStore = GroupAlbumStore(appContext)
        val memoryStore = MemoryStore(appContext)
        val album = albumStore.findAlbumById(albumId) ?: return Result.success()
        if (album.organizeStatus != GroupAlbumStore.ORGANIZE_STATUS_PROCESSING) {
            return Result.success()
        }
        if (album.description.isBlank()) {
            albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_FAILED)
            return Result.success()
        }

        return try {
            val candidateRecords = memoryStore.loadActiveRecords()
                .filterNot { album.recordIds.contains(it.recordId) }
            if (candidateRecords.isNotEmpty()) {
                val aiMemoryService = AiMemoryService(appContext)
                val selectedRecordIds = aiMemoryService.selectRecordIdsForAlbum(
                    album.name,
                    album.description,
                    candidateRecords
                )
                if (selectedRecordIds.isNotEmpty()) {
                    albumStore.addRecordIds(albumId, selectedRecordIds)
                }
            }
            albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_COMPLETED)
            Result.success()
        } catch (exception: Exception) {
            Log.e(
                "GroupAiOrganizeWorker",
                "AI organize failed albumId=$albumId name=${album.name}",
                exception
            )
            albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_FAILED)
            Result.success()
        }
    }
}
