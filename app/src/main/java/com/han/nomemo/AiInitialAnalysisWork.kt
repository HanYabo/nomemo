package com.han.nomemo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

private const val AI_INITIAL_ANALYSIS_WORK_NAME_PREFIX = "nomemo.ai.initial."
private const val AI_INITIAL_ANALYSIS_RECORD_ID = "record_id"

object AiInitialAnalysisWorkScheduler {
    @JvmStatic
    fun enqueue(context: Context, recordId: String) {
        if (recordId.isBlank()) return
        val request = OneTimeWorkRequestBuilder<AiInitialAnalysisWorker>()
            .setInputData(Data.Builder().putString(AI_INITIAL_ANALYSIS_RECORD_ID, recordId).build())
            .build()
        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            AI_INITIAL_ANALYSIS_WORK_NAME_PREFIX + recordId,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    @JvmStatic
    fun recoverPendingRecords(context: Context) {
        val appContext = context.applicationContext
        val memoryStore = MemoryStore(appContext)
        val settingsStore = SettingsStore(appContext)
        val pendingPolicy = AiAnalysisPolicies.resolve(settingsStore, AiOperationKind.INITIAL_ANALYSIS)
        memoryStore.loadRecords()
            .filter { it.mode == MemoryRecord.MODE_AI }
            .filter { record ->
                record.engine.equals("pending", ignoreCase = true) ||
                    AiAnalysisStateJson.isActive(record.aiAnalysisStateJson)
            }
            .forEach { record ->
                val parsedState = AiAnalysisStateJson.parse(record.aiAnalysisStateJson)
                val shouldNormalizeActiveState = record.aiAnalysisStateJson.isBlank() ||
                    (parsedState?.isActive == true && parsedState.attemptCount <= 0)
                val effectiveAnalysisState = if (record.engine.equals("pending", ignoreCase = true) || shouldNormalizeActiveState) {
                    AiAnalysisStateJson.pending(
                        AiOperationKind.INITIAL_ANALYSIS,
                        parsedState?.costMode ?: pendingPolicy.costMode,
                        parsedState?.attemptLimit ?: pendingPolicy.totalAttemptLimit
                    )
                } else {
                    record.aiAnalysisStateJson
                }
                val normalizedVisualState = when {
                    AiVisualProcessingStateJson.isVisible(record.aiVisualStateJson) -> record.aiVisualStateJson
                    AiAnalysisStateJson.isActive(effectiveAnalysisState) -> {
                        AiVisualProcessingStateJson.parse(record.aiVisualStateJson)?.let {
                            AiVisualProcessingStateJson.active(
                                operationKind = AiOperationKind.INITIAL_ANALYSIS,
                                attemptCount = it.attemptCount,
                                attemptLimit = it.attemptLimit
                            )
                        } ?: AiAnalysisStateJson.parse(effectiveAnalysisState)?.let {
                            AiVisualProcessingStateJson.fromAnalysisState(it)
                        }.orEmpty()
                    }
                    else -> ""
                }
                val normalized = if (
                    record.engine.equals("pending", ignoreCase = true) ||
                    shouldNormalizeActiveState ||
                    effectiveAnalysisState != record.aiAnalysisStateJson ||
                    normalizedVisualState != record.aiVisualStateJson
                ) {
                    MemoryRecord(
                        record.recordId,
                        record.createdAt,
                        record.mode,
                        record.title,
                        record.summary,
                        record.sourceText,
                        record.note,
                        record.imageUri,
                        record.analysis,
                        record.memory,
                        "manual",
                        record.categoryGroupCode,
                        record.categoryCode,
                        record.categoryName,
                        record.reminderAt,
                        record.isReminderDone,
                        record.isArchived,
                        record.structuredFactsJson,
                        effectiveAnalysisState,
                        normalizedVisualState
                    )
                } else {
                    record
                }
                if (normalized !== record) {
                    memoryStore.updateRecord(normalized)
                }
                enqueue(appContext, normalized.recordId)
            }
    }
}

class AiInitialAnalysisWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val recordId = inputData.getString(AI_INITIAL_ANALYSIS_RECORD_ID)?.trim().orEmpty()
        if (recordId.isEmpty()) return Result.success()

        val appContext = applicationContext
        val memoryStore = MemoryStore(appContext)
        val currentRecord = memoryStore.findRecordById(recordId) ?: return Result.success()
        if (currentRecord.mode != MemoryRecord.MODE_AI) return Result.success()
        if (!currentRecord.engine.equals("pending", ignoreCase = true) && !AiAnalysisStateJson.isActive(currentRecord.aiAnalysisStateJson)) {
            return Result.success()
        }

        val orchestrator = AiAnalysisOrchestrator(appContext)
        val persistedState = AiAnalysisStateJson.parse(currentRecord.aiAnalysisStateJson)
        val policy = persistedState?.let {
            AiExecutionPolicy(
                AiOperationKind.INITIAL_ANALYSIS,
                it.costMode,
                it.attemptLimit.coerceAtLeast(1),
                false,
                true
            )
        } ?: orchestrator.initialPolicy()
        val input = currentRecord.sourceText?.takeIf { it.isNotBlank() }
            ?: currentRecord.note?.takeIf { it.isNotBlank() }
            ?: ""
        val imageUri = currentRecord.imageUri?.takeIf { it.isNotBlank() }?.let(Uri::parse)
        val initialAttempt = persistedState?.attemptCount?.coerceAtLeast(1) ?: 1
        val initialAttemptLimit = persistedState?.attemptLimit?.coerceAtLeast(1) ?: policy.totalAttemptLimit

        try {
            AiProcessingStateRegistry.markProcessing(recordId, initialAttempt, initialAttemptLimit)
            updatePendingState(memoryStore, currentRecord, policy, initialAttempt, initialAttemptLimit)
            val outcome = try {
                orchestrator.runInitialAnalysis(input, imageUri) { attempt, limit ->
                    val latest = memoryStore.findRecordById(recordId) ?: currentRecord
                    updatePendingState(memoryStore, latest, policy, attempt, limit)
                }
            } catch (exception: Exception) {
                Log.e(
                    "AiInitialAnalysisWorker",
                    "Initial analysis crashed recordId=$recordId operationKind=${policy.operationKind} costMode=${policy.costMode}",
                    exception
                )
                null
            }
            val latestRecord = memoryStore.findRecordById(recordId) ?: currentRecord
            val resolved = if (outcome != null && outcome.isSuccess && outcome.generationResult != null) {
                buildResolvedRecord(latestRecord, outcome.generationResult!!)
            } else {
                buildLocalFallbackRecord(appContext, latestRecord)
            }
            val updated = memoryStore.updateRecord(resolved)
            if (!updated) {
                memoryStore.prependRecord(resolved)
            }
            AiSummaryNotifier.notifyAnalysisReady(appContext, resolved)
            return Result.success()
        } finally {
            AiProcessingStateRegistry.clearProcessing(recordId)
        }
    }

    private fun updatePendingState(
        memoryStore: MemoryStore,
        record: MemoryRecord,
        policy: AiExecutionPolicy,
        attempt: Int,
        attemptLimit: Int
    ) {
        val updated = MemoryRecord(
            record.recordId,
            record.createdAt,
            record.mode,
            record.title,
            record.summary,
            record.sourceText,
            record.note,
            record.imageUri,
            record.analysis,
            record.memory,
            if (record.engine.equals("pending", ignoreCase = true)) "manual" else record.engine,
            record.categoryGroupCode,
            record.categoryCode,
            record.categoryName,
            record.reminderAt,
            record.isReminderDone,
            record.isArchived,
            record.structuredFactsJson,
            AiAnalysisStateJson.running(
                AiOperationKind.INITIAL_ANALYSIS,
                policy.costMode,
                attemptCount = attempt.coerceAtLeast(1),
                attemptLimit = attemptLimit.coerceAtLeast(1)
            ),
            AiVisualProcessingStateJson.active(
                operationKind = AiOperationKind.INITIAL_ANALYSIS,
                attemptCount = attempt.coerceAtLeast(1),
                attemptLimit = attemptLimit.coerceAtLeast(1)
            )
        )
        AiProcessingStateRegistry.markProcessing(
            record.recordId,
            attempt = attempt.coerceAtLeast(1),
            attemptLimit = attemptLimit.coerceAtLeast(1)
        )
        memoryStore.updateRecord(updated)
    }

    private fun buildResolvedRecord(
        placeholder: MemoryRecord,
        result: AiMemoryService.GenerationResult
    ): MemoryRecord {
        val resolvedCategory = CategoryCatalog.getAllCategories().firstOrNull {
            it.categoryCode == result.suggestedCategoryCode
        } ?: findCategoryByCode(placeholder.categoryCode)
            ?: CategoryCatalog.getQuickCategories().first()
        return MemoryRecord(
            placeholder.recordId,
            placeholder.createdAt,
            placeholder.mode,
            result.title,
            result.summary,
            placeholder.sourceText,
            placeholder.note,
            placeholder.imageUri,
            result.analysis,
            result.memory,
            result.engine,
            resolvedCategory.groupCode,
            resolvedCategory.categoryCode,
            resolvedCategory.categoryName,
            placeholder.reminderAt,
            placeholder.isReminderDone,
            placeholder.isArchived,
            result.structuredFactsJson,
            "",
            AiVisualProcessingStateJson.completionWindow(
                rawVisualState = placeholder.aiVisualStateJson,
                rawAnalysisState = placeholder.aiAnalysisStateJson,
                fallbackOperationKind = AiOperationKind.INITIAL_ANALYSIS
            )
        )
    }

    private fun buildLocalFallbackRecord(context: Context, placeholder: MemoryRecord): MemoryRecord {
        val fallbackCategory = findCategoryByCode(placeholder.categoryCode)
            ?: CategoryCatalog.getQuickCategories().first()
        val sourceText = placeholder.sourceText.orEmpty()
        val fallbackMemory = if (sourceText.isBlank()) {
            if (placeholder.imageUri.isNullOrBlank()) "已记录一条记忆" else "已保存图片记忆"
        } else {
            sourceText
        }
        val fallbackTitle = compactTitle(sourceText, fallbackCategory.categoryName)
        val fallbackSummary = compactSummary(sourceText, fallbackMemory)
        val fallbackFactsJson = MemoryFactReconciler.reconcileToJson(
            sourceText,
            "",
            fallbackTitle,
            fallbackSummary,
            context.getString(R.string.memory_fallback_analysis),
            fallbackMemory,
            fallbackCategory.categoryCode
        )
        return MemoryRecord(
            placeholder.recordId,
            placeholder.createdAt,
            placeholder.mode,
            fallbackTitle,
            MemoryFactReconciler.stableSummary(fallbackCategory.categoryCode, fallbackSummary, fallbackFactsJson),
            placeholder.sourceText,
            placeholder.note,
            placeholder.imageUri,
            context.getString(R.string.memory_fallback_analysis),
            fallbackMemory,
            "local",
            fallbackCategory.groupCode,
            fallbackCategory.categoryCode,
            fallbackCategory.categoryName,
            placeholder.reminderAt,
            placeholder.isReminderDone,
            placeholder.isArchived,
            fallbackFactsJson,
            "",
            AiVisualProcessingStateJson.completionWindow(
                rawVisualState = placeholder.aiVisualStateJson,
                rawAnalysisState = placeholder.aiAnalysisStateJson,
                fallbackOperationKind = AiOperationKind.INITIAL_ANALYSIS
            )
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

    private fun findCategoryByCode(categoryCode: String?): CategoryCatalog.CategoryOption? {
        return CategoryCatalog.getAllCategories().firstOrNull { it.categoryCode == categoryCode }
    }
}
