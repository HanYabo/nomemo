package com.han.nomemo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRecordFailureTest {
    @Test
    fun failedAiState_isMarkedAsFailure() {
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_AI,
            "喜茶取餐号8258",
            "AI 分析失败",
            "",
            "",
            "",
            "AI分析失败，点击AI分析按钮可进行重试",
            "8258",
            "cloud",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_PICKUP,
            "取餐",
            0L,
            false,
            false,
            "",
            AiAnalysisStateJson.failed(
                AiOperationKind.REANALYZE,
                AiCostMode.ECONOMY,
                attemptCount = 1,
                attemptLimit = 2
            )
        )

        assertTrue(isAiAnalysisFailedRecord(record))
    }

    @Test
    fun activeAiState_isNotMarkedAsFailure() {
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_AI,
            "喜茶取餐号8258",
            "AI 分析中",
            "",
            "",
            "",
            "分析中",
            "8258",
            "manual",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_PICKUP,
            "取餐",
            0L,
            false,
            false,
            "",
            AiAnalysisStateJson.running(
                AiOperationKind.INITIAL_ANALYSIS,
                AiCostMode.STANDARD,
                attemptCount = 1,
                attemptLimit = 1
            )
        )

        assertFalse(isAiAnalysisFailedRecord(record))
    }

    @Test
    fun dismissedAiState_isNotMarkedAsFailure() {
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_AI,
            "喜茶取餐号8258",
            "AI 分析失败",
            "",
            "",
            "",
            "AI分析失败，点击AI分析按钮可进行重试",
            "8258",
            "cloud",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_PICKUP,
            "取餐",
            0L,
            false,
            false,
            "",
            AiAnalysisStateJson.dismissed(
                AiOperationKind.REANALYZE,
                AiCostMode.ECONOMY,
                attemptCount = 1,
                attemptLimit = 2
            )
        )

        assertFalse(isAiAnalysisFailedRecord(record))
    }

    @Test
    fun legacyLocalFallback_isStillMarkedAsFailure() {
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_AI,
            "喜茶取餐号8258",
            "本地兜底摘要",
            "",
            "",
            "",
            "AI出现异常，已使用本地兜底记忆",
            "8258",
            "local",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_PICKUP,
            "取餐",
            0L,
            false,
            false
        )

        assertTrue(isAiAnalysisFailedRecord(record))
    }
}
