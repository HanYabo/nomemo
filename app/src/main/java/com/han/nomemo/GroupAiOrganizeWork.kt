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
    fun cancel(context: Context, albumId: String) {
        if (albumId.isBlank()) return
        val appContext = context.applicationContext
        WorkManager.getInstance(appContext)
            .cancelUniqueWork(GROUP_AI_ORGANIZE_WORK_NAME_PREFIX + albumId)
        GroupAlbumStore(appContext)
            .updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_IDLE)
        NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
    }

    @JvmStatic
    fun recoverProcessingAlbums(context: Context) {
        val appContext = context.applicationContext
        val albumStore = GroupAlbumStore(appContext)
        albumStore.loadAlbums()
            .filter { it.organizeStatus == GroupAlbumStore.ORGANIZE_STATUS_PROCESSING }
            .forEach {
                NoMemoLiveUpdateNotifier.notifyGroupOrganizing(appContext, it)
                enqueue(appContext, it.albumId)
            }
    }
}

class GroupAiOrganizeWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    private fun isAlbumStillProcessing(
        albumStore: GroupAlbumStore,
        albumId: String
    ): Boolean {
        return albumStore.findAlbumById(albumId)?.organizeStatus == GroupAlbumStore.ORGANIZE_STATUS_PROCESSING
    }

    private fun shouldAbort(
        albumStore: GroupAlbumStore,
        albumId: String
    ): Boolean {
        return isStopped || !isAlbumStillProcessing(albumStore, albumId)
    }

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
        NoMemoLiveUpdateNotifier.notifyGroupOrganizing(appContext, album)
        if (album.description.isBlank()) {
            albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_FAILED)
            NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
            return Result.success()
        }

        return try {
            if (shouldAbort(albumStore, albumId)) {
                return Result.success()
            }
            val candidateRecords = memoryStore.loadActiveRecords()
                .filterNot { album.recordIds.contains(it.recordId) }
            if (shouldAbort(albumStore, albumId)) {
                return Result.success()
            }
            if (candidateRecords.isEmpty()) {
                if (!shouldAbort(albumStore, albumId)) {
                    albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_COMPLETED)
                }
                NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
                return Result.success()
            }

            val aiMemoryService = AiMemoryService(appContext)
            val selectedRecordIds = aiMemoryService.selectRecordIdsForAlbum(
                album.name,
                album.description,
                candidateRecords
            )
            if (shouldAbort(albumStore, albumId)) {
                return Result.success()
            }
            if (selectedRecordIds.isEmpty()) {
                Log.w(
                    "GroupAiOrganizeWorker",
                    "AI organize returned no matches albumId=$albumId name=${album.name}"
                )
                if (!shouldAbort(albumStore, albumId)) {
                    albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_FAILED)
                }
                NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
                return Result.success()
            }

            val added = albumStore.addRecordIds(albumId, selectedRecordIds)
            if (shouldAbort(albumStore, albumId)) {
                return Result.success()
            }
            if (!added) {
                Log.w(
                    "GroupAiOrganizeWorker",
                    "AI organize selected records but addRecordIds failed albumId=$albumId selected=${selectedRecordIds.size}"
                )
                if (!shouldAbort(albumStore, albumId)) {
                    albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_FAILED)
                }
                NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
                return Result.success()
            }

            if (!shouldAbort(albumStore, albumId)) {
                albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_COMPLETED)
            }
            NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
            Result.success()
        } catch (exception: Exception) {
            Log.e(
                "GroupAiOrganizeWorker",
                "AI organize failed albumId=$albumId name=${album.name}",
                exception
            )
            if (!shouldAbort(albumStore, albumId)) {
                albumStore.updateOrganizeStatus(albumId, GroupAlbumStore.ORGANIZE_STATUS_FAILED)
            }
            NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
            Result.success()
        }
    }
}
