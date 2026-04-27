package com.han.nomemo

import org.json.JSONObject

private const val AI_VISUAL_PHASE_ANALYZING = "analyzing"
private const val AI_VISUAL_PHASE_RETRYING = "retrying"

const val INITIAL_ANALYSIS_VISUAL_MIN_VISIBLE_MS = 1200L
const val REANALYZE_VISUAL_MIN_VISIBLE_MS = 650L

data class AiVisualProcessingState(
    val phase: String,
    val attemptCount: Int,
    val attemptLimit: Int,
    val visibleUntilEpochMs: Long,
    val operationKind: AiOperationKind
) {
    fun isVisibleAt(nowMs: Long): Boolean = visibleUntilEpochMs > nowMs
}

object AiVisualProcessingStateJson {
    @JvmStatic
    fun parse(raw: String?): AiVisualProcessingState? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return runCatching {
            val json = JSONObject(text)
            val operationKind = runCatching {
                AiOperationKind.valueOf(
                    json.optString("operationKind", AiOperationKind.INITIAL_ANALYSIS.name)
                )
            }.getOrDefault(AiOperationKind.INITIAL_ANALYSIS)
            val attemptCount = json.optInt("attemptCount", 1).coerceAtLeast(1)
            val attemptLimit = json.optInt("attemptLimit", attemptCount).coerceAtLeast(attemptCount)
            AiVisualProcessingState(
                phase = normalizePhase(json.optString("phase", "")),
                attemptCount = attemptCount,
                attemptLimit = attemptLimit,
                visibleUntilEpochMs = json.optLong("visibleUntilEpochMs", 0L).coerceAtLeast(0L),
                operationKind = operationKind
            )
        }.getOrNull()
    }

    @JvmStatic
    fun toJson(state: AiVisualProcessingState?): String {
        state ?: return ""
        return JSONObject()
            .put("phase", normalizePhase(state.phase))
            .put("attemptCount", state.attemptCount.coerceAtLeast(1))
            .put("attemptLimit", state.attemptLimit.coerceAtLeast(state.attemptCount.coerceAtLeast(1)))
            .put("visibleUntilEpochMs", state.visibleUntilEpochMs.coerceAtLeast(0L))
            .put("operationKind", state.operationKind.name)
            .toString()
    }

    @JvmStatic
    fun active(
        operationKind: AiOperationKind,
        attemptCount: Int,
        attemptLimit: Int,
        nowMs: Long = System.currentTimeMillis(),
        minimumVisibleDurationMs: Long = minimumVisibleDurationMsFor(operationKind)
    ): String {
        val normalizedAttempt = attemptCount.coerceAtLeast(1)
        val normalizedLimit = attemptLimit.coerceAtLeast(normalizedAttempt)
        return toJson(
            AiVisualProcessingState(
                phase = phaseForAttempt(normalizedAttempt),
                attemptCount = normalizedAttempt,
                attemptLimit = normalizedLimit,
                visibleUntilEpochMs = nowMs.coerceAtLeast(0L) + minimumVisibleDurationMs.coerceAtLeast(0L),
                operationKind = operationKind
            )
        )
    }

    @JvmStatic
    fun fromAnalysisState(
        analysisState: AiAnalysisState,
        nowMs: Long = System.currentTimeMillis(),
        minimumVisibleDurationMs: Long = minimumVisibleDurationMsFor(analysisState.operationKind)
    ): String {
        return active(
            operationKind = analysisState.operationKind,
            attemptCount = analysisState.attemptCount,
            attemptLimit = analysisState.attemptLimit,
            nowMs = nowMs,
            minimumVisibleDurationMs = minimumVisibleDurationMs
        )
    }

    @JvmStatic
    fun completionWindow(
        rawVisualState: String?,
        rawAnalysisState: String?,
        fallbackOperationKind: AiOperationKind,
        nowMs: Long = System.currentTimeMillis(),
        minimumVisibleDurationMs: Long = minimumVisibleDurationMsFor(fallbackOperationKind)
    ): String {
        val visualState = parse(rawVisualState)
        val analysisState = AiAnalysisStateJson.parse(rawAnalysisState)
        val operationKind = analysisState?.operationKind ?: visualState?.operationKind ?: fallbackOperationKind
        val attemptCount = visualState?.attemptCount
            ?: analysisState?.attemptCount
            ?: 1
        val attemptLimit = visualState?.attemptLimit
            ?: analysisState?.attemptLimit
            ?: attemptCount
        return active(
            operationKind = operationKind,
            attemptCount = attemptCount.coerceAtLeast(1),
            attemptLimit = attemptLimit.coerceAtLeast(attemptCount.coerceAtLeast(1)),
            nowMs = nowMs,
            minimumVisibleDurationMs = minimumVisibleDurationMs
        )
    }

    @JvmStatic
    fun retainIfVisible(raw: String?, nowMs: Long = System.currentTimeMillis()): String {
        val state = parse(raw) ?: return ""
        return if (state.isVisibleAt(nowMs)) toJson(state) else ""
    }

    @JvmStatic
    fun isVisible(raw: String?, nowMs: Long = System.currentTimeMillis()): Boolean {
        return parse(raw)?.isVisibleAt(nowMs) == true
    }

    @JvmStatic
    fun minimumVisibleDurationMsFor(operationKind: AiOperationKind): Long {
        return if (operationKind == AiOperationKind.REANALYZE) {
            REANALYZE_VISUAL_MIN_VISIBLE_MS
        } else {
            INITIAL_ANALYSIS_VISUAL_MIN_VISIBLE_MS
        }
    }

    @JvmStatic
    fun phaseForAttempt(attemptCount: Int): String {
        return if (attemptCount.coerceAtLeast(1) >= 2) {
            AI_VISUAL_PHASE_RETRYING
        } else {
            AI_VISUAL_PHASE_ANALYZING
        }
    }

    private fun normalizePhase(raw: String?): String {
        return when (raw?.trim()?.lowercase()) {
            AI_VISUAL_PHASE_RETRYING -> AI_VISUAL_PHASE_RETRYING
            else -> AI_VISUAL_PHASE_ANALYZING
        }
    }
}
