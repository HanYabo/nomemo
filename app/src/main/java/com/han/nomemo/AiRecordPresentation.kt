package com.han.nomemo

fun isPlainManualRecord(record: MemoryRecord?): Boolean {
    record ?: return true
    return record.mode == MemoryRecord.MODE_NORMAL &&
        record.engine.equals("manual", ignoreCase = true)
}

fun hasPersistedAiAnalysis(record: MemoryRecord?): Boolean {
    record ?: return false
    return !isPlainManualRecord(record)
}

fun preferredCardSummaryText(record: MemoryRecord): String {
    return when {
        hasPersistedAiAnalysis(record) && !record.summary.isNullOrBlank() -> record.summary.orEmpty()
        !record.analysis.isNullOrBlank() -> record.analysis.orEmpty()
        !record.summary.isNullOrBlank() -> record.summary.orEmpty()
        !record.memory.isNullOrBlank() -> record.memory.orEmpty()
        !record.sourceText.isNullOrBlank() -> record.sourceText.orEmpty()
        else -> ""
    }
}
