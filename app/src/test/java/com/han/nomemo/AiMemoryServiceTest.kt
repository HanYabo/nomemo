package com.han.nomemo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiMemoryServiceTest {
    @Test
    fun generateMemory_nonStrictAllowsLocalFallback() {
        assertTrue(AiMemoryService.shouldAllowLocalFallback(false))
    }

    @Test
    fun generateEnhancedMemoryStrict_disablesLocalFallback() {
        assertFalse(AiMemoryService.shouldAllowLocalFallback(true))
    }

    @Test
    fun economyEnhanced_requestsOneFullPromptRetryBeforeGivingUp() {
        assertTrue(AiMemoryService.shouldRetryWithFullPromptProfile(true, true))
        assertFalse(AiMemoryService.shouldRetryWithFullPromptProfile(false, true))
        assertFalse(AiMemoryService.shouldRetryWithFullPromptProfile(true, false))
    }
}
