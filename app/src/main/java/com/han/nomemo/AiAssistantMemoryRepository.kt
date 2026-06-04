package com.han.nomemo

import android.content.Context
import android.net.Uri
import java.util.Locale

class AiAssistantMemoryRepository private constructor(
    private val appContext: Context?,
    private val loadActiveRecordsBlock: () -> List<MemoryRecord>,
    private val archiveRecordBlock: (String) -> Unit,
    private val deleteRecordBlock: (String) -> Boolean
) {
    constructor(context: Context) : this(
        appContext = context.applicationContext,
        loadActiveRecordsBlock = {
            MemoryStore(context.applicationContext).loadActiveRecords()
        },
        archiveRecordBlock = { recordId ->
            MemoryStore(context.applicationContext).archiveRecord(recordId, true)
        },
        deleteRecordBlock = { recordId ->
            MemoryStore(context.applicationContext).deleteRecord(recordId)
        }
    )

    internal constructor(
        loadActiveRecords: () -> List<MemoryRecord>,
        archiveRecord: (String) -> Unit = {},
        deleteRecord: (String) -> Boolean = { false }
    ) : this(
        appContext = null,
        loadActiveRecordsBlock = loadActiveRecords,
        archiveRecordBlock = archiveRecord,
        deleteRecordBlock = deleteRecord
    )

    fun loadActiveRecords(): List<MemoryRecord> {
        return loadActiveRecordsBlock()
    }

    fun searchDelivery(limit: Int = DEFAULT_RESULT_LIMIT): List<MemoryRecord> {
        return searchByCategories(setOf(CategoryCatalog.CODE_LIFE_DELIVERY), limit)
    }

    fun searchShopping(limit: Int = DEFAULT_RESULT_LIMIT): List<MemoryRecord> {
        return searchByCategories(AiAssistantActionRouter.SHOPPING_CATEGORY_CODES, limit)
    }

    fun searchByRoute(route: AiAssistantRoute, limit: Int = DEFAULT_RESULT_LIMIT): List<MemoryRecord> {
        if (route.categoryCodes.isNotEmpty()) {
            val categoryMatches = searchByCategories(route.categoryCodes, limit)
            if (route.query.isBlank() || categoryMatches.isNotEmpty()) {
                return categoryMatches
            }
        }
        return fuzzySearch(route.query, limit)
    }

    fun searchByCategories(categoryCodes: Set<String>, limit: Int = DEFAULT_RESULT_LIMIT): List<MemoryRecord> {
        if (categoryCodes.isEmpty()) return emptyList()
        return loadActiveRecords()
            .filter { categoryCodes.contains(it.categoryCode) }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    fun fuzzySearch(query: String, limit: Int = DEFAULT_RESULT_LIMIT): List<MemoryRecord> {
        val normalizedQuery = normalize(query)
        if (normalizedQuery.isBlank()) return emptyList()
        val tokens = normalizedQuery
            .split(Regex("""[\s,.;:!?，。；：！？、/\\|()（）\[\]【】"'`]+"""))
            .map { it.trim() }
            .filter { it.length >= 2 }
            .distinct()
            .ifEmpty { listOf(normalizedQuery) }

        return loadActiveRecords()
            .mapNotNull { record ->
                val haystack = searchableText(record)
                val score = scoreMatch(haystack, normalizedQuery, tokens)
                if (score <= 0) null else record to score
            }
            .sortedWith(
                compareByDescending<Pair<MemoryRecord, Int>> { it.second }
                    .thenByDescending { it.first.createdAt }
            )
            .map { it.first }
            .take(limit)
    }

    fun recentRecords(days: Int, limit: Int = SUMMARY_RECORD_LIMIT): List<MemoryRecord> {
        val now = System.currentTimeMillis()
        val windowMs = days.coerceAtLeast(1) * DAY_MS
        return loadActiveRecords()
            .filter { now - it.createdAt <= windowMs }
            .sortedByDescending { it.createdAt }
            .take(limit)
    }

    fun selectSummaryRecords(route: AiAssistantRoute, query: String): List<MemoryRecord> {
        route.recentDays?.let { days ->
            val recent = recentRecords(days)
            if (recent.isNotEmpty()) return recent
        }
        if (route.categoryCodes.isNotEmpty()) {
            val byCategory = searchByCategories(route.categoryCodes, SUMMARY_RECORD_LIMIT)
            if (byCategory.isNotEmpty()) return byCategory
        }
        val keywordMatches = fuzzySearch(query, SUMMARY_RECORD_LIMIT)
        if (keywordMatches.isNotEmpty()) return keywordMatches
        return loadActiveRecords()
            .sortedByDescending { it.createdAt }
            .take(SUMMARY_RECORD_LIMIT)
    }

    fun findExpiredArchiveCandidates(
        nowMs: Long = System.currentTimeMillis(),
        limit: Int = DEFAULT_RESULT_LIMIT
    ): List<MemoryRecord> {
        return loadActiveRecords()
            .filter { isLikelyExpiredForArchive(it, nowMs) }
            .sortedWith(
                compareByDescending<MemoryRecord> { archivePriority(it, nowMs) }
                    .thenByDescending { it.createdAt }
            )
            .take(limit)
    }

    fun archiveRecords(recordIds: List<String>): Int {
        val uniqueIds = recordIds.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        uniqueIds.forEach { archiveRecordBlock(it) }
        return uniqueIds.size
    }

    fun deleteRecords(recordIds: List<String>): Int {
        return recordIds
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .count { deleteRecordBlock(it) }
    }

    fun createMemory(inputText: String, imageUri: Uri?): MemoryRecord {
        val context = requireNotNull(appContext) {
            "createMemory requires the Android-backed repository constructor"
        }
        val store = MemoryStore(context)
        val settingsStore = SettingsStore(context)
        val quickNoteCategory = CategoryCatalog.getQuickCategories().first()
        val createdAt = System.currentTimeMillis()
        val imageUriText = imageUri?.toString().orEmpty()
        val memoryText = inputText.ifBlank {
            if (imageUriText.isBlank()) "来自 AI 助手的新记忆" else "已保存图片记忆"
        }

        if (!settingsStore.isAiAvailable()) {
            val title = compactTitle(inputText, quickNoteCategory.categoryName)
            val summary = compactSummary(inputText, memoryText)
            val factsJson = MemoryFactReconciler.reconcileToJson(
                inputText,
                "",
                title,
                summary,
                "",
                memoryText,
                quickNoteCategory.categoryCode
            )
            val record = MemoryRecord(
                createdAt,
                MemoryRecord.MODE_NORMAL,
                title,
                MemoryFactReconciler.stableSummary(quickNoteCategory.categoryCode, summary, factsJson),
                inputText,
                inputText,
                imageUriText,
                "",
                memoryText,
                "assistant",
                quickNoteCategory.groupCode,
                quickNoteCategory.categoryCode,
                quickNoteCategory.categoryName,
                0L,
                false,
                false,
                factsJson
            )
            store.prependRecord(record)
            return record
        }

        val initialPolicy = AiAnalysisPolicies.resolve(settingsStore, AiOperationKind.INITIAL_ANALYSIS)
        val placeholder = MemoryRecord(
            createdAt,
            MemoryRecord.MODE_AI,
            if (inputText.isBlank()) "AI 分析中" else compactTitle(inputText, quickNoteCategory.categoryName),
            if (inputText.isBlank()) "图片已添加，AI 正在生成摘要" else "已创建条目，AI 完成后会自动更新",
            inputText,
            inputText,
            imageUriText,
            "AI 分析中...",
            memoryText,
            "manual",
            quickNoteCategory.groupCode,
            quickNoteCategory.categoryCode,
            quickNoteCategory.categoryName,
            0L,
            false,
            false,
            "",
            AiAnalysisStateJson.pending(
                AiOperationKind.INITIAL_ANALYSIS,
                initialPolicy.costMode,
                initialPolicy.totalAttemptLimit
            ),
            AiVisualProcessingStateJson.active(
                operationKind = AiOperationKind.INITIAL_ANALYSIS,
                attemptCount = 1,
                attemptLimit = initialPolicy.totalAttemptLimit,
                nowMs = createdAt
            )
        )
        store.prependRecord(placeholder)
        NoMemoLiveUpdateNotifier.notifyAiAnalysis(
            context,
            placeholder,
            1,
            initialPolicy.totalAttemptLimit
        )
        AiInitialAnalysisWorkScheduler.enqueue(context, placeholder.recordId)
        return placeholder
    }

    private fun isLikelyExpiredForArchive(record: MemoryRecord, nowMs: Long): Boolean {
        val ageMs = nowMs - record.createdAt
        val reminderPast = record.reminderAt > 0L && record.reminderAt < nowMs
        return when (record.categoryCode) {
            CategoryCatalog.CODE_WORK_TODO -> record.isReminderDone || reminderPast || ageMs > TODO_ARCHIVE_AFTER_MS
            CategoryCatalog.CODE_WORK_SCHEDULE -> reminderPast || ageMs > SCHEDULE_ARCHIVE_AFTER_MS
            CategoryCatalog.CODE_LIFE_DELIVERY,
            CategoryCatalog.CODE_LIFE_PICKUP -> reminderPast || ageMs > LIFE_PICKUP_ARCHIVE_AFTER_MS
            else -> reminderPast
        }
    }

    private fun archivePriority(record: MemoryRecord, nowMs: Long): Int {
        val reminderPast = record.reminderAt > 0L && record.reminderAt < nowMs
        return when {
            record.isReminderDone -> 5
            reminderPast -> 4
            record.categoryCode == CategoryCatalog.CODE_LIFE_DELIVERY -> 3
            record.categoryCode == CategoryCatalog.CODE_LIFE_PICKUP -> 3
            record.categoryCode == CategoryCatalog.CODE_WORK_SCHEDULE -> 2
            record.categoryCode == CategoryCatalog.CODE_WORK_TODO -> 2
            else -> 1
        }
    }

    private fun scoreMatch(haystack: String, query: String, tokens: List<String>): Int {
        var score = 0
        if (haystack.contains(query)) score += 12
        tokens.forEach { token ->
            if (haystack.contains(token)) score += 3
        }
        return score
    }

    private fun searchableText(record: MemoryRecord): String {
        return listOf(
            record.title,
            record.summary,
            record.memory,
            record.analysis,
            record.sourceText,
            record.note,
            record.categoryName
        ).joinToString("\n") { it.orEmpty() }
            .let(::normalize)
    }

    private fun normalize(value: String?): String {
        return value.orEmpty()
            .lowercase(Locale.ROOT)
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun compactTitle(text: String, fallback: String): String {
        val value = text.ifBlank { fallback }
        val single = value.replace('\n', ' ').trim()
        return if (single.length <= 18) single else single.substring(0, 18) + "..."
    }

    private fun compactSummary(text: String, fallback: String): String {
        val value = text.ifBlank { fallback }
        val single = value.replace('\n', ' ').trim()
        return if (single.length <= 42) single else single.substring(0, 42) + "..."
    }

    companion object {
        private const val DEFAULT_RESULT_LIMIT = 12
        private const val SUMMARY_RECORD_LIMIT = 24
        private const val DAY_MS = 24L * 60L * 60L * 1000L
        private const val LIFE_PICKUP_ARCHIVE_AFTER_MS = 3L * DAY_MS
        private const val SCHEDULE_ARCHIVE_AFTER_MS = 2L * DAY_MS
        private const val TODO_ARCHIVE_AFTER_MS = 14L * DAY_MS

        fun MemoryRecord.toAssistantCard(): AiAssistantMemoryCard {
            return AiAssistantMemoryCard(
                recordId = recordId,
                title = title.orEmpty().ifBlank { categoryName.orEmpty().ifBlank { "记忆" } },
                summary = summary.orEmpty()
                    .ifBlank { memory.orEmpty() }
                    .ifBlank { sourceText.orEmpty() }
                    .replace('\n', ' ')
                    .trim(),
                categoryCode = categoryCode.orEmpty(),
                categoryName = categoryName.orEmpty().ifBlank { CategoryCatalog.getCategoryName(categoryCode) },
                createdAt = createdAt,
                imageUri = imageUri?.takeIf { it.isNotBlank() }
            )
        }
    }
}
