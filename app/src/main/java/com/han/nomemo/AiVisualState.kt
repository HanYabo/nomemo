package com.han.nomemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

private const val AI_VISUAL_ANALYZING = "分析中"
private const val AI_VISUAL_RETRYING = "重试中"
private const val AI_PLACEHOLDER_AI_ANALYZING = "AI 分析中"
private const val AI_PLACEHOLDER_AI_ANALYZING_COMPACT = "AI分析中"
private const val AI_PLACEHOLDER_ANALYZING = "分析中"

enum class AiVisualPhase {
    IDLE,
    ANALYZING,
    RETRYING
}

data class AiVisualState(
    val isProcessing: Boolean,
    val phase: AiVisualPhase,
    val attempt: Int,
    val attemptLimit: Int,
    val displayText: String
) {
    companion object {
        @JvmStatic
        fun idle(): AiVisualState = AiVisualState(
            isProcessing = false,
            phase = AiVisualPhase.IDLE,
            attempt = 0,
            attemptLimit = 0,
            displayText = ""
        )
    }
}

@Composable
fun rememberAiVisualState(record: MemoryRecord): AiVisualState {
    var nowMs by remember(record.recordId, record.aiVisualStateJson) {
        mutableLongStateOf(System.currentTimeMillis())
    }
    LaunchedEffect(record.recordId, record.aiVisualStateJson) {
        nowMs = System.currentTimeMillis()
        val visualState = AiVisualProcessingStateJson.parse(record.aiVisualStateJson)
        val remaining = visualState?.visibleUntilEpochMs?.minus(System.currentTimeMillis()) ?: 0L
        if (remaining > 0L) {
            delay(remaining + 32L)
            nowMs = System.currentTimeMillis()
        }
    }
    return remember(
        record.recordId,
        record.mode,
        record.engine,
        record.aiAnalysisStateJson,
        record.aiVisualStateJson,
        record.analysis,
        record.title,
        record.summary,
        nowMs
    ) {
        AiVisualStateResolver.resolve(record, nowMs)
    }
}

object AiVisualStateResolver {
    @JvmStatic
    fun resolve(
        record: MemoryRecord,
        nowMs: Long = System.currentTimeMillis()
    ): AiVisualState {
        val persistedVisual = AiVisualProcessingStateJson.parse(record.aiVisualStateJson)
        if (persistedVisual?.isVisibleAt(nowMs) == true) {
            return AiVisualState(
                isProcessing = true,
                phase = toVisualPhase(persistedVisual.phase),
                attempt = persistedVisual.attemptCount.coerceAtLeast(1),
                attemptLimit = persistedVisual.attemptLimit.coerceAtLeast(persistedVisual.attemptCount.coerceAtLeast(1)),
                displayText = formatAiVisualDisplayText(toVisualPhase(persistedVisual.phase))
            )
        }

        val persistedAnalysis = AiAnalysisStateJson.parse(record.aiAnalysisStateJson)
        if (persistedAnalysis?.isActive == true) {
            val phase = if (persistedAnalysis.attemptCount.coerceAtLeast(1) >= 2) {
                AiVisualPhase.RETRYING
            } else {
                AiVisualPhase.ANALYZING
            }
            return AiVisualState(
                isProcessing = true,
                phase = phase,
                attempt = persistedAnalysis.attemptCount.coerceAtLeast(1),
                attemptLimit = persistedAnalysis.attemptLimit.coerceAtLeast(persistedAnalysis.attemptCount.coerceAtLeast(1)),
                displayText = formatAiVisualDisplayText(phase)
            )
        }

        if (record.mode == MemoryRecord.MODE_AI && hasLegacyPlaceholder(record)) {
            return AiVisualState(
                isProcessing = true,
                phase = AiVisualPhase.ANALYZING,
                attempt = 1,
                attemptLimit = 1,
                displayText = formatAiVisualDisplayText(AiVisualPhase.ANALYZING)
            )
        }

        return AiVisualState.idle()
    }

    private fun hasLegacyPlaceholder(record: MemoryRecord): Boolean {
        return isAiProcessingPlaceholderText(record.analysis) ||
            isAiProcessingPlaceholderText(record.title) ||
            isAiProcessingPlaceholderText(record.summary)
    }

    private fun toVisualPhase(rawPhase: String): AiVisualPhase {
        return if (rawPhase == AiVisualProcessingStateJson.phaseForAttempt(2)) {
            AiVisualPhase.RETRYING
        } else {
            AiVisualPhase.ANALYZING
        }
    }
}

internal fun formatAiVisualDisplayText(phase: AiVisualPhase): String {
    return when (phase) {
        AiVisualPhase.RETRYING -> AI_VISUAL_RETRYING
        AiVisualPhase.ANALYZING -> AI_VISUAL_ANALYZING
        AiVisualPhase.IDLE -> ""
    }
}

internal fun isAiProcessingPlaceholderText(value: String?): Boolean {
    val normalized = value?.trim().orEmpty()
    if (normalized.isEmpty()) return false
    return normalized == AI_PLACEHOLDER_AI_ANALYZING ||
        normalized == AI_PLACEHOLDER_AI_ANALYZING_COMPACT ||
        normalized == "$AI_PLACEHOLDER_AI_ANALYZING..." ||
        normalized == "$AI_PLACEHOLDER_AI_ANALYZING_COMPACT..." ||
        normalized == AI_PLACEHOLDER_ANALYZING ||
        normalized == "$AI_PLACEHOLDER_ANALYZING..."
}
