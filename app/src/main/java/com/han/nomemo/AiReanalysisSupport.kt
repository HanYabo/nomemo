package com.han.nomemo

import java.util.Locale

fun buildFailedReanalysisRecord(
    baseRecord: MemoryRecord,
    costMode: AiCostMode,
    attemptCount: Int,
    attemptLimit: Int,
    failureStage: AiFailureStage? = null,
    failureMessage: String? = null
): MemoryRecord {
    return MemoryRecord(
        baseRecord.recordId,
        baseRecord.createdAt,
        baseRecord.mode,
        baseRecord.title,
        baseRecord.summary,
        baseRecord.sourceText,
        baseRecord.note,
        baseRecord.imageUri,
        baseRecord.analysis,
        baseRecord.memory,
        baseRecord.engine,
        baseRecord.categoryGroupCode,
        baseRecord.categoryCode,
        baseRecord.categoryName,
        baseRecord.reminderAt,
        baseRecord.isReminderDone,
        baseRecord.isArchived,
        baseRecord.structuredFactsJson,
        AiAnalysisStateJson.failed(
            AiOperationKind.REANALYZE,
            costMode,
            attemptCount = attemptCount.coerceAtLeast(1),
            attemptLimit = attemptLimit.coerceAtLeast(1),
            failureStage = failureStage,
            failureMessage = failureMessage
        ),
        "",
        baseRecord.liveStatusState
    )
}

fun describeAiReanalysisFailure(
    failureStage: AiFailureStage?,
    failureMessage: String?
): String {
    val message = failureMessage.orEmpty().lowercase(Locale.ROOT)
    return when {
        failureStage == AiFailureStage.CONFIGURATION ||
            message.contains("config unavailable") -> "AI 配置不可用，已保留原记忆"

        failureStage == AiFailureStage.IMAGE_INPUT ||
            message.contains("image content is unavailable") -> "原图片无法读取，已保留原记忆"

        failureStage == AiFailureStage.MODEL_CAPABILITY ||
            message.contains("image_input_unsupported") ||
            message.contains("does not support image") -> "当前图片模型不支持图片输入，已保留原记忆"

        failureStage == AiFailureStage.TOKEN_EXHAUSTED -> "AI 输出被截断，已保留原记忆"

        failureStage == AiFailureStage.JSON_PARSE ||
            failureStage == AiFailureStage.SCHEMA_VALIDATE ||
            failureStage == AiFailureStage.JSON_REPAIR -> "AI 返回格式异常，已保留原记忆"

        message.contains("timeout") ||
            message.contains("temporar") ||
            message.contains("rate limit") ||
            message.contains("too many requests") -> "AI 服务暂时不可用，已保留原记忆"

        else -> "重新分析失败，已保留原记忆"
    }
}

fun describeAiAnalysisFailureDialog(state: AiAnalysisState?): String {
    val attempts = state?.let {
        "已尝试 ${it.attemptCount.coerceAtLeast(1)}/${it.attemptLimit.coerceAtLeast(1)} 次。"
    }.orEmpty()
    val stage = state?.failureStage
    val message = state?.failureMessage.orEmpty()
    val httpStatus = extractHttpStatus(message)
    val detail = when {
        stage == AiFailureStage.CONFIGURATION -> "AI 配置不可用，请检查 Base URL、API Key 和模型设置。"
        stage == AiFailureStage.IMAGE_INPUT -> "原图片无法读取，重试不会改变图片文件状态。"
        stage == AiFailureStage.MODEL_CAPABILITY -> "当前模型不支持图片输入，请切换到支持图片/多模态的模型。"
        stage == AiFailureStage.TOKEN_EXHAUSTED -> "AI 输出被截断，系统已尝试使用更稳妥的提示重新生成。"
        stage == AiFailureStage.JSON_PARSE ||
            stage == AiFailureStage.SCHEMA_VALIDATE ||
            stage == AiFailureStage.JSON_REPAIR -> "AI 返回格式不符合记忆结构要求，系统已完成可用重试。"

        httpStatus >= 500 ||
            httpStatus == 408 ||
            httpStatus == 409 ||
            httpStatus == 425 ||
            httpStatus == 429 -> "服务返回 $httpStatus，属于可重试的临时错误；系统已按重试策略尝试。"

        httpStatus >= 400 -> "服务返回 $httpStatus，通常是鉴权、模型、接口地址或请求参数问题，盲目重试不会修复。"

        stage == AiFailureStage.CLOUD_REQUEST -> "云端请求失败，可能与网络、服务状态或接口配置有关。"
        else -> "AI 分析没有生成可用结果。"
    }
    return (attempts + detail + " 点击 AI 分析按钮可再次手动重试。").trim()
}

private fun extractHttpStatus(message: String): Int {
    return Regex("""httpStatus=(\d+)""")
        .find(message)
        ?.groupValues
        ?.getOrNull(1)
        ?.toIntOrNull()
        ?: 0
}
