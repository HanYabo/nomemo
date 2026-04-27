package com.han.nomemo

import org.json.JSONObject

private const val AI_STATE_PENDING = "pending"
private const val AI_STATE_RUNNING = "running"
private const val AI_STATE_RETRYING = "retrying"

data class AiAnalysisState(
    val status: String,
    val operationKind: AiOperationKind,
    val costMode: AiCostMode,
    val attemptCount: Int,
    val attemptLimit: Int
) {
    val isActive: Boolean
        get() = status == AI_STATE_PENDING || status == AI_STATE_RUNNING || status == AI_STATE_RETRYING
}

object AiAnalysisStateJson {
    @JvmStatic
    fun parse(raw: String?): AiAnalysisState? {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return runCatching {
            val json = JSONObject(text)
            val status = normalizeStatus(json.optString("status", ""))
            val operationKind = runCatching {
                AiOperationKind.valueOf(json.optString("operationKind", AiOperationKind.INITIAL_ANALYSIS.name))
            }.getOrDefault(AiOperationKind.INITIAL_ANALYSIS)
            val costMode = runCatching {
                AiCostMode.valueOf(json.optString("costMode", AiCostMode.STANDARD.name))
            }.getOrDefault(AiCostMode.STANDARD)
            AiAnalysisState(
                status = status,
                operationKind = operationKind,
                costMode = costMode,
                attemptCount = json.optInt("attemptCount", 0).coerceAtLeast(0),
                attemptLimit = json.optInt("attemptLimit", 1).coerceAtLeast(1)
            )
        }.getOrNull()
    }

    @JvmStatic
    fun toJson(state: AiAnalysisState?): String {
        state ?: return ""
        return JSONObject()
            .put("status", normalizeStatus(state.status))
            .put("operationKind", state.operationKind.name)
            .put("costMode", state.costMode.name)
            .put("attemptCount", state.attemptCount.coerceAtLeast(0))
            .put("attemptLimit", state.attemptLimit.coerceAtLeast(1))
            .toString()
    }

    @JvmStatic
    fun pending(operationKind: AiOperationKind, costMode: AiCostMode, attemptLimit: Int): String {
        return toJson(
            AiAnalysisState(
                status = AI_STATE_PENDING,
                operationKind = operationKind,
                costMode = costMode,
                attemptCount = 1,
                attemptLimit = attemptLimit.coerceAtLeast(1)
            )
        )
    }

    @JvmStatic
    fun running(operationKind: AiOperationKind, costMode: AiCostMode, attemptCount: Int, attemptLimit: Int): String {
        val normalizedAttempt = attemptCount.coerceAtLeast(1)
        val status = if (normalizedAttempt >= 2) AI_STATE_RETRYING else AI_STATE_RUNNING
        return toJson(
            AiAnalysisState(
                status = status,
                operationKind = operationKind,
                costMode = costMode,
                attemptCount = normalizedAttempt,
                attemptLimit = attemptLimit.coerceAtLeast(1)
            )
        )
    }

    @JvmStatic
    fun isActive(raw: String?): Boolean = parse(raw)?.isActive == true

    private fun normalizeStatus(raw: String?): String {
        return when (raw?.trim()?.lowercase()) {
            AI_STATE_RUNNING -> AI_STATE_RUNNING
            AI_STATE_RETRYING -> AI_STATE_RETRYING
            else -> AI_STATE_PENDING
        }
    }
}
