package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiAnalysisStateJsonTest {
    @Test
    fun `pending state round trips`() {
        val raw = AiAnalysisStateJson.pending(
            AiOperationKind.INITIAL_ANALYSIS,
            AiCostMode.ECONOMY,
            2
        )

        val parsed = AiAnalysisStateJson.parse(raw)

        assertTrue(parsed?.isActive == true)
        assertEquals("pending", parsed?.status)
        assertEquals(AiOperationKind.INITIAL_ANALYSIS, parsed?.operationKind)
        assertEquals(AiCostMode.ECONOMY, parsed?.costMode)
        assertEquals(1, parsed?.attemptCount)
        assertEquals(2, parsed?.attemptLimit)
    }

    @Test
    fun `running state escalates to retrying after second attempt`() {
        val raw = AiAnalysisStateJson.running(
            AiOperationKind.INITIAL_ANALYSIS,
            AiCostMode.STANDARD,
            attemptCount = 3,
            attemptLimit = 7
        )

        val parsed = AiAnalysisStateJson.parse(raw)

        assertEquals("retrying", parsed?.status)
        assertEquals(3, parsed?.attemptCount)
        assertEquals(7, parsed?.attemptLimit)
    }

    @Test
    fun `failed state is not active and can be queried`() {
        val raw = AiAnalysisStateJson.failed(
            AiOperationKind.REANALYZE,
            AiCostMode.ECONOMY,
            attemptCount = 2,
            attemptLimit = 3
        )

        val parsed = AiAnalysisStateJson.parse(raw)

        assertEquals("failed", parsed?.status)
        assertEquals(2, parsed?.attemptCount)
        assertEquals(3, parsed?.attemptLimit)
        assertFalse(parsed?.isActive == true)
        assertTrue(AiAnalysisStateJson.isFailed(raw))
    }

    @Test
    fun `dismissed state is not failed`() {
        val raw = AiAnalysisStateJson.dismissed(
            AiOperationKind.REANALYZE,
            AiCostMode.STANDARD,
            attemptCount = 1,
            attemptLimit = 1
        )

        val parsed = AiAnalysisStateJson.parse(raw)

        assertEquals("dismissed", parsed?.status)
        assertFalse(parsed?.isActive == true)
        assertFalse(AiAnalysisStateJson.isFailed(raw))
        assertTrue(AiAnalysisStateJson.isDismissed(raw))
    }
}
