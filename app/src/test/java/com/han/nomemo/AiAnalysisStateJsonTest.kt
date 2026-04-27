package com.han.nomemo

import org.junit.Assert.assertEquals
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
}
