package com.han.nomemo

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

private const val GROUP_SUMMARY_WORK_NAME_PREFIX = "nomemo.group.summary."
private const val GROUP_SUMMARY_ALBUM_ID = "album_id"

object GroupSummaryWorkScheduler {
    @JvmStatic
    fun enqueue(context: Context, albumId: String) {
        if (albumId.isBlank()) return
        val request = OneTimeWorkRequestBuilder<GroupSummaryWorker>()
            .setInputData(Data.Builder().putString(GROUP_SUMMARY_ALBUM_ID, albumId).build())
            .build()
        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            GROUP_SUMMARY_WORK_NAME_PREFIX + albumId,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    @JvmStatic
    fun cancel(context: Context, albumId: String) {
        if (albumId.isBlank()) return
        WorkManager.getInstance(context.applicationContext)
            .cancelUniqueWork(GROUP_SUMMARY_WORK_NAME_PREFIX + albumId)
    }
}

class GroupSummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val albumId = inputData.getString(GROUP_SUMMARY_ALBUM_ID)?.trim().orEmpty()
        if (albumId.isEmpty()) return Result.success()

        val appContext = applicationContext
        val albumStore = GroupAlbumStore(appContext)
        val memoryStore = MemoryStore(appContext)
        val settingsStore = SettingsStore(appContext)

        Log.d("GroupSummaryWorker", "doWork started albumId=$albumId")

        val album = albumStore.findAlbumById(albumId)
        if (album == null) {
            Log.w("GroupSummaryWorker", "Album not found albumId=$albumId")
            return Result.success()
        }
        if (album.recordIds.isEmpty()) {
            Log.w("GroupSummaryWorker", "Album has no records albumId=$albumId")
            return Result.success()
        }

        val records = memoryStore.loadActiveRecords()
            .filter { album.recordIds.contains(it.recordId) }
        if (records.isEmpty()) {
            Log.w("GroupSummaryWorker", "No matching records found albumId=$albumId recordIds=${album.recordIds}")
            return Result.success()
        }

        Log.d("GroupSummaryWorker", "Found ${records.size} records, checking AI availability")
        albumStore.updateSummaryStatus(albumId, GroupAlbumStore.SUMMARY_STATUS_GENERATING)
        GroupAlbumStoreNotifier.notifyChanged(appContext)
        NoMemoLiveUpdateNotifier.notifyGroupSummaryGenerating(appContext, album)

        return try {
            if (!settingsStore.isAiAvailable()) {
                Log.w("GroupSummaryWorker", "AI not available: enabled=${settingsStore.aiEnabled}, " +
                    "baseUrl=${settingsStore.resolvedApiBaseUrl().take(30)}, " +
                    "apiKey=${if (settingsStore.resolvedApiKey().isNotBlank()) "set" else "empty"}, " +
                    "textModel=${settingsStore.resolvedTextModel()}, " +
                    "apiModel=${settingsStore.resolvedApiModel()}")
                albumStore.updateSummaryStatus(albumId, GroupAlbumStore.SUMMARY_STATUS_FAILED)
                GroupAlbumStoreNotifier.notifyChanged(appContext)
                NoMemoLiveUpdateNotifier.cancelGroupSummary(appContext, albumId)
                return Result.success()
            }
            Log.d("GroupSummaryWorker", "Calling AI API...")
            val summary = generateGroupSummary(album, records, settingsStore)
            Log.d("GroupSummaryWorker", "AI response length=${summary.length}")
            if (summary.isNotBlank()) {
                albumStore.updateSummary(albumId, summary)
                albumStore.updateSummaryStatus(albumId, GroupAlbumStore.SUMMARY_STATUS_IDLE)
                GroupAlbumStoreNotifier.notifyChanged(appContext)
                Log.d("GroupSummaryWorker", "Summary saved successfully")
            } else {
                Log.w("GroupSummaryWorker", "AI returned empty summary")
                albumStore.updateSummaryStatus(albumId, GroupAlbumStore.SUMMARY_STATUS_FAILED)
                GroupAlbumStoreNotifier.notifyChanged(appContext)
            }
            NoMemoLiveUpdateNotifier.cancelGroupSummary(appContext, albumId)
            Result.success()
        } catch (exception: Exception) {
            Log.e("GroupSummaryWorker", "Summary generation failed albumId=$albumId", exception)
            albumStore.updateSummaryStatus(albumId, GroupAlbumStore.SUMMARY_STATUS_FAILED)
            GroupAlbumStoreNotifier.notifyChanged(appContext)
            NoMemoLiveUpdateNotifier.cancelGroupSummary(appContext, albumId)
            Result.success()
        }
    }

    private fun resolveChatCompletionsUrl(baseUrl: String): String {
        var normalized = baseUrl.trim()
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length - 1)
        }
        return when {
            normalized.endsWith("/chat/completions") -> normalized
            normalized.endsWith("/responses") -> normalized.substring(0, normalized.length - "/responses".length) + "/chat/completions"
            else -> "$normalized/chat/completions"
        }
    }

    private fun generateGroupSummary(
        album: GroupAlbumStore.GroupAlbum,
        records: List<MemoryRecord>,
        settingsStore: SettingsStore
    ): String {
        val baseUrl = settingsStore.resolvedApiBaseUrl()
        val apiKey = settingsStore.resolvedApiKey()
        val model = settingsStore.resolvedTextModel().ifBlank { settingsStore.resolvedApiModel() }
        if (baseUrl.isBlank() || apiKey.isBlank() || model.isBlank()) {
            throw IllegalStateException("AI config unavailable")
        }
        val apiUrl = resolveChatCompletionsUrl(baseUrl)

        val recordsDescription = buildString {
            records.forEachIndexed { index, record ->
                append("记忆${index + 1}：")
                append("标题=${record.title.orEmpty()}")
                record.summary?.takeIf { it.isNotBlank() }?.let { append("，摘要=$it") }
                record.sourceText?.takeIf { it.isNotBlank() }?.let { append("，原文=$it") }
                record.analysis?.takeIf { it.isNotBlank() }?.let { append("，分析=$it") }
                record.categoryName?.takeIf { it.isNotBlank() }?.let { append("，分类=$it") }
                append("\n")
            }
        }

        val systemPrompt = buildString {
            append("你是 NoMemo 的分组摘要助手。请用中文回答。")
            append("根据用户分组内包含的记忆条目，生成一段有洞察力的分组摘要。")
            append("摘要应该：")
            append("1. 像一位善于观察的朋友在讲述发现，语气自然、有趣、有温度")
            append("2. 找到记忆之间的关联和模式，而不是简单罗列")
            append("3. 指出有趣的细节或值得注意的事情")
            append("4. 如果有时间线上的有趣发现，指出来")
            append("5. 适当加入生活化的感悟")
            append("6. 长度在200-400字之间")
            append("7. 不要使用标题格式，直接用流畅的段落表达")
            append("8. 不要出现'根据这些记忆'、'以下是摘要'等引导语")
        }

        val userPrompt = buildString {
            append("分组名称：${album.name}\n")
            if (album.description.isNotBlank()) {
                append("分组描述：${album.description}\n")
            }
            append("共 ${records.size} 条记忆：\n")
            append(recordsDescription)
        }

        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", systemPrompt))
            .put(JSONObject().put("role", "user").put("content", userPrompt))
        val payload = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("max_tokens", if (settingsStore.economyMode) 650 else 1000)
            .put("temperature", 0.45)

        val connection = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Authorization", "Bearer $apiKey")
        }
        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        connection.outputStream.use { output -> output.write(bytes) }
        val responseText = if (connection.responseCode in 200..299) {
            connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        } else {
            val errorText = connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
            throw IllegalStateException("AI request failed: ${connection.responseCode} ${errorText.orEmpty()}")
        }
        val root = JSONObject(responseText)
        return root
            .optJSONArray("choices")
            ?.optJSONObject(0)
            ?.optJSONObject("message")
            ?.optString("content")
            ?.trim()
            .orEmpty()
    }
}
