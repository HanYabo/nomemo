package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiAnalysisPoliciesTest {
    @Test
    fun `initial standard policy keeps local fallback and full retry budget`() {
        val policy = AiAnalysisPolicies.resolve(
            false,
            true,
            AiOperationKind.INITIAL_ANALYSIS
        )

        assertEquals(AiCostMode.STANDARD, policy.costMode)
        assertEquals(7, policy.cloudAttemptLimit)
        assertEquals(7, policy.totalAttemptLimit)
        assertTrue(policy.isAllowLocalFallback)
        assertFalse(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `initial economy policy adds one full prompt rescue while keeping local fallback`() {
        val policy = AiAnalysisPolicies.resolve(
            true,
            true,
            AiOperationKind.INITIAL_ANALYSIS
        )

        assertEquals(AiCostMode.ECONOMY, policy.costMode)
        assertEquals(2, policy.cloudAttemptLimit)
        assertEquals(3, policy.totalAttemptLimit)
        assertTrue(policy.isAllowLocalFallback)
        assertTrue(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `reanalyze economy policy uses one attempt when auto retry is off`() {
        val policy = AiAnalysisPolicies.resolve(
            true,
            false,
            AiOperationKind.REANALYZE
        )

        assertEquals(AiCostMode.ECONOMY, policy.costMode)
        assertEquals(1, policy.cloudAttemptLimit)
        assertEquals(1, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertFalse(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `reanalyze standard policy uses one attempt when auto retry is off`() {
        val policy = AiAnalysisPolicies.resolve(
            false,
            false,
            AiOperationKind.REANALYZE
        )

        assertEquals(AiCostMode.STANDARD, policy.costMode)
        assertEquals(1, policy.cloudAttemptLimit)
        assertEquals(1, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertFalse(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `reanalyze standard policy uses three attempts when auto retry is enabled`() {
        val policy = AiAnalysisPolicies.resolve(
            false,
            true,
            AiOperationKind.REANALYZE
        )

        assertEquals(AiCostMode.STANDARD, policy.costMode)
        assertEquals(3, policy.cloudAttemptLimit)
        assertEquals(3, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertFalse(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `reanalyze economy policy adds full prompt rescue when auto retry is enabled`() {
        val policy = AiAnalysisPolicies.resolve(
            true,
            true,
            AiOperationKind.REANALYZE
        )

        assertEquals(AiCostMode.ECONOMY, policy.costMode)
        assertEquals(2, policy.cloudAttemptLimit)
        assertEquals(3, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertTrue(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `restore reconstructs initial economy rescue budget from persisted total attempts`() {
        val policy = AiAnalysisPolicies.restore(
            AiOperationKind.INITIAL_ANALYSIS,
            AiCostMode.ECONOMY,
            3
        )

        assertEquals(2, policy.cloudAttemptLimit)
        assertEquals(3, policy.totalAttemptLimit)
        assertTrue(policy.isAllowFullPromptRescue)
        assertTrue(policy.isAllowLocalFallback)
    }

    @Test
    fun `restore preserves reanalysis retry budget from persisted total attempts`() {
        val policy = AiAnalysisPolicies.restore(
            AiOperationKind.REANALYZE,
            AiCostMode.STANDARD,
            7
        )

        assertEquals(7, policy.cloudAttemptLimit)
        assertEquals(7, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertFalse(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `restore reconstructs reanalysis economy rescue budget from persisted total attempts`() {
        val policy = AiAnalysisPolicies.restore(
            AiOperationKind.REANALYZE,
            AiCostMode.ECONOMY,
            3
        )

        assertEquals(2, policy.cloudAttemptLimit)
        assertEquals(3, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertTrue(policy.isAllowFullPromptRescue)
    }

    @Test
    fun `restore keeps single-attempt economy reanalysis without rescue`() {
        val policy = AiAnalysisPolicies.restore(
            AiOperationKind.REANALYZE,
            AiCostMode.ECONOMY,
            1
        )

        assertEquals(1, policy.cloudAttemptLimit)
        assertEquals(1, policy.totalAttemptLimit)
        assertFalse(policy.isAllowLocalFallback)
        assertFalse(policy.isAllowFullPromptRescue)
    }
}
