package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiVisualProcessingStateJsonTest {
    @Test
    fun `active visual state round trips with analyzing phase`() {
        val raw = AiVisualProcessingStateJson.active(
            operationKind = AiOperationKind.INITIAL_ANALYSIS,
            attemptCount = 1,
            attemptLimit = 7,
            nowMs = 1_000L,
            minimumVisibleDurationMs = 2_000L
        )

        val parsed = AiVisualProcessingStateJson.parse(raw)

        assertEquals("analyzing", parsed?.phase)
        assertEquals(1, parsed?.attemptCount)
        assertEquals(7, parsed?.attemptLimit)
        assertEquals(3_000L, parsed?.visibleUntilEpochMs)
        assertEquals(AiOperationKind.INITIAL_ANALYSIS, parsed?.operationKind)
    }

    @Test
    fun `retrying visual state expires correctly`() {
        val raw = AiVisualProcessingStateJson.active(
            operationKind = AiOperationKind.REANALYZE,
            attemptCount = 2,
            attemptLimit = 2,
            nowMs = 1_000L,
            minimumVisibleDurationMs = 50L
        )

        assertTrue(AiVisualProcessingStateJson.isVisible(raw, nowMs = 1_010L))
        assertFalse(AiVisualProcessingStateJson.isVisible(raw, nowMs = 1_100L))
        assertEquals("", AiVisualProcessingStateJson.retainIfVisible(raw, nowMs = 1_100L))
    }

    @Test
    fun `completion window refreshes visibility from latest attempt`() {
        val running = AiAnalysisStateJson.running(
            operationKind = AiOperationKind.INITIAL_ANALYSIS,
            costMode = AiCostMode.STANDARD,
            attemptCount = 2,
            attemptLimit = 7
        )

        val refreshed = AiVisualProcessingStateJson.completionWindow(
            rawVisualState = "",
            rawAnalysisState = running,
            fallbackOperationKind = AiOperationKind.INITIAL_ANALYSIS,
            nowMs = 5_000L,
            minimumVisibleDurationMs = 800L
        )

        val parsed = AiVisualProcessingStateJson.parse(refreshed)

        assertEquals("retrying", parsed?.phase)
        assertEquals(2, parsed?.attemptCount)
        assertEquals(7, parsed?.attemptLimit)
        assertEquals(5_800L, parsed?.visibleUntilEpochMs)
    }
}
