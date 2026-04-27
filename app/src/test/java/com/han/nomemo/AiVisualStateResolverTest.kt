package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TEXT_ANALYZING = "分析中"
private const val TEXT_RETRYING = "重试中"

class AiVisualStateResolverTest {
    @Test
    fun `persisted visible visual state renders analyzing`() {
        val record = baseRecord(
            aiVisualStateJson = AiVisualProcessingStateJson.active(
                operationKind = AiOperationKind.INITIAL_ANALYSIS,
                attemptCount = 1,
                attemptLimit = 7,
                nowMs = 1_000L,
                minimumVisibleDurationMs = 2_000L
            )
        )

        val state = AiVisualStateResolver.resolve(record, nowMs = 1_500L)

        assertTrue(state.isProcessing)
        assertEquals(AiVisualPhase.ANALYZING, state.phase)
        assertEquals(1, state.attempt)
        assertEquals(7, state.attemptLimit)
        assertEquals(TEXT_ANALYZING, state.displayText)
    }

    @Test
    fun `persisted visible visual state renders retrying`() {
        val record = baseRecord(
            aiVisualStateJson = AiVisualProcessingStateJson.active(
                operationKind = AiOperationKind.INITIAL_ANALYSIS,
                attemptCount = 2,
                attemptLimit = 7,
                nowMs = 1_000L,
                minimumVisibleDurationMs = 2_000L
            )
        )

        val state = AiVisualStateResolver.resolve(record, nowMs = 1_500L)

        assertTrue(state.isProcessing)
        assertEquals(AiVisualPhase.RETRYING, state.phase)
        assertEquals(TEXT_RETRYING, state.displayText)
    }

    @Test
    fun `active analysis without visual falls back to analyzing`() {
        val record = baseRecord(
            aiAnalysisStateJson = AiAnalysisStateJson.pending(
                AiOperationKind.INITIAL_ANALYSIS,
                AiCostMode.STANDARD,
                7
            )
        )

        val state = AiVisualStateResolver.resolve(record, nowMs = 1_500L)

        assertTrue(state.isProcessing)
        assertEquals(AiVisualPhase.ANALYZING, state.phase)
        assertEquals(TEXT_ANALYZING, state.displayText)
    }

    @Test
    fun `expired visual with active retry analysis falls back to retrying`() {
        val record = baseRecord(
            aiAnalysisStateJson = AiAnalysisStateJson.running(
                AiOperationKind.INITIAL_ANALYSIS,
                AiCostMode.ECONOMY,
                attemptCount = 2,
                attemptLimit = 2
            ),
            aiVisualStateJson = AiVisualProcessingStateJson.active(
                operationKind = AiOperationKind.INITIAL_ANALYSIS,
                attemptCount = 1,
                attemptLimit = 2,
                nowMs = 1_000L,
                minimumVisibleDurationMs = 50L
            )
        )

        val state = AiVisualStateResolver.resolve(record, nowMs = 2_000L)

        assertTrue(state.isProcessing)
        assertEquals(AiVisualPhase.RETRYING, state.phase)
        assertEquals(TEXT_RETRYING, state.displayText)
    }

    @Test
    fun `legacy placeholder renders analyzing`() {
        val record = baseRecord(
            title = "AI 分析中",
            summary = "AI 分析中...",
            analysis = "分析中"
        )

        val state = AiVisualStateResolver.resolve(record, nowMs = 1_500L)

        assertTrue(state.isProcessing)
        assertEquals(AiVisualPhase.ANALYZING, state.phase)
        assertEquals(TEXT_ANALYZING, state.displayText)
    }

    @Test
    fun `idle record renders no processing state`() {
        val state = AiVisualStateResolver.resolve(
            baseRecord(
                mode = MemoryRecord.MODE_NORMAL,
                title = "normal title",
                summary = "normal summary",
                analysis = "normal analysis"
            ),
            nowMs = 1_500L
        )

        assertFalse(state.isProcessing)
        assertEquals(AiVisualPhase.IDLE, state.phase)
        assertEquals("", state.displayText)
    }

    private fun baseRecord(
        mode: String = MemoryRecord.MODE_AI,
        title: String = "title",
        summary: String = "summary",
        analysis: String = "analysis",
        aiAnalysisStateJson: String = "",
        aiVisualStateJson: String = ""
    ): MemoryRecord {
        return MemoryRecord(
            "record-1",
            1L,
            mode,
            title,
            summary,
            "source",
            "",
            "",
            analysis,
            "memory",
            "cloud",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_QUICK_NOTE,
            "quick",
            0L,
            false,
            false,
            "",
            aiAnalysisStateJson,
            aiVisualStateJson
        )
    }
}
