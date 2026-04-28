package com.han.nomemo

fun isAiAnalysisFailedRecord(record: MemoryRecord?): Boolean {
    record ?: return false
    if (record.mode != MemoryRecord.MODE_AI) return false
    if (AiAnalysisStateJson.isActive(record.aiAnalysisStateJson)) return false
    if (AiAnalysisStateJson.isDismissed(record.aiAnalysisStateJson)) return false
    if (AiAnalysisStateJson.isFailed(record.aiAnalysisStateJson)) return true
    if (!record.engine.equals("local", ignoreCase = true)) return false
    val analysis = record.analysis?.trim().orEmpty()
    return analysis.contains("本地兜底") || analysis.contains("AI出现异常")
}
