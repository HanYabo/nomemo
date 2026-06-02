package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NoMemoLiveUpdateActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        when (intent.action) {
            NoMemoLiveUpdateNotifier.ACTION_CANCEL_AI_ANALYSIS -> {
                val recordId = intent.getStringExtra(NoMemoLiveUpdateNotifier.EXTRA_RECORD_ID)
                    ?.trim()
                    .orEmpty()
                if (recordId.isNotEmpty()) {
                    AiInitialAnalysisWorkScheduler.cancel(appContext, recordId)
                    cancelInitialAiAnalysisRecord(appContext, recordId)
                    NoMemoLiveUpdateNotifier.cancelAiAnalysis(appContext, recordId)
                }
            }

            NoMemoLiveUpdateNotifier.ACTION_CANCEL_GROUP_ORGANIZE -> {
                val albumId = intent.getStringExtra(NoMemoLiveUpdateNotifier.EXTRA_ALBUM_ID)
                    ?.trim()
                    .orEmpty()
                if (albumId.isNotEmpty()) {
                    GroupAiOrganizeWorkScheduler.cancel(appContext, albumId)
                    NoMemoLiveUpdateNotifier.cancelGroupOrganize(appContext, albumId)
                }
            }

            NoMemoLiveUpdateNotifier.ACTION_DISMISS_NOTIFICATION -> {
                val notificationId = intent.getIntExtra(
                    NoMemoLiveUpdateNotifier.EXTRA_NOTIFICATION_ID,
                    0
                )
                if (notificationId != 0) {
                    NotificationManagerCompat.from(appContext).cancel(notificationId)
                }
            }

            NoMemoLiveUpdateNotifier.ACTION_COMPLETE_MEMORY_LIVE_STATUS -> {
                val recordId = intent.getStringExtra(NoMemoLiveUpdateNotifier.EXTRA_RECORD_ID)
                    ?.trim()
                    .orEmpty()
                if (recordId.isNotEmpty()) {
                    completeMemoryLiveStatus(appContext, recordId)
                }
            }
        }
    }

    private fun completeMemoryLiveStatus(context: Context, recordId: String) {
        val memoryStore = MemoryStore(context)
        val record = memoryStore.findRecordById(recordId) ?: return
        memoryStore.updateRecord(record.withLiveStatusState(MemoryRecord.LIVE_STATUS_COMPLETED))
        NoMemoLiveUpdateNotifier.cancelMemoryLiveStatus(context, recordId)
    }

    private fun cancelInitialAiAnalysisRecord(context: Context, recordId: String) {
        val memoryStore = MemoryStore(context)
        val record = memoryStore.findRecordById(recordId) ?: return
        val state = AiAnalysisStateJson.parse(record.aiAnalysisStateJson) ?: return
        if (!state.isActive || state.operationKind != AiOperationKind.INITIAL_ANALYSIS) {
            return
        }
        AiProcessingStateRegistry.clearProcessing(recordId)
        memoryStore.updateRecord(buildCanceledRecord(record))
    }

    private fun buildCanceledRecord(record: MemoryRecord): MemoryRecord {
        val fallbackCategory = CategoryCatalog.getAllCategories()
            .firstOrNull { it.categoryCode == record.categoryCode }
            ?: CategoryCatalog.getQuickCategories().first()
        val sourceText = record.sourceText?.trim().orEmpty()
        val memoryText = sourceText.ifBlank {
            record.memory
                ?.trim()
                ?.takeIf { it.isNotEmpty() && !it.contains("AI 分析中") }
                ?: if (record.imageUri.isNullOrBlank()) "已记录一条记忆" else "已保存图片记忆"
        }
        val title = sourceText
            .takeIf { it.isNotBlank() }
            ?.let { compactTitle(it, fallbackCategory.categoryName) }
            ?: record.title
                ?.trim()
                ?.takeIf { it.isNotEmpty() && !it.contains("AI 分析中") }
            ?: if (record.imageUri.isNullOrBlank()) fallbackCategory.categoryName else "图片记忆"
        val summary = sourceText
            .takeIf { it.isNotBlank() }
            ?.let { compactSummary(it, memoryText) }
            ?: record.summary
                ?.trim()
                ?.takeIf {
                    it.isNotEmpty() &&
                        !it.contains("AI 正在生成摘要") &&
                        !it.contains("AI 完成后会自动更新")
                }
            ?: memoryText
        val analysis = record.analysis
            ?.trim()
            ?.takeIf { it.isNotEmpty() && !it.contains("AI 分析中") }
            ?: summary
        return MemoryRecord(
            record.recordId,
            record.createdAt,
            record.mode,
            title,
            summary,
            record.sourceText,
            record.note,
            record.imageUri,
            analysis,
            memoryText,
            "manual",
            fallbackCategory.groupCode,
            fallbackCategory.categoryCode,
            fallbackCategory.categoryName,
            record.reminderAt,
            record.isReminderDone,
            record.isArchived,
            record.structuredFactsJson,
            "",
            "",
            record.liveStatusState
        )
    }

    private fun compactTitle(text: String, fallback: String): String {
        val value = if (text.isBlank()) fallback else text
        val single = value.replace('\n', ' ').trim()
        return if (single.length <= 18) single else single.substring(0, 18) + "..."
    }

    private fun compactSummary(text: String, fallback: String): String {
        val value = if (text.isBlank()) fallback else text
        val single = value.replace('\n', ' ').trim()
        return if (single.length <= 42) single else single.substring(0, 42) + "..."
    }
}
