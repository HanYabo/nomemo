package com.han.nomemo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiMemoryServiceTest {
    @Test
    fun `finish reason length is treated as token exhaustion`() {
        assertTrue(AiMemoryService.shouldTreatAsTokenExhausted("length", "{\"title\":\"a\""))
    }

    @Test
    fun `truncated json is treated as token exhaustion even without finish reason`() {
        assertTrue(
            AiMemoryService.shouldTreatAsTokenExhausted(
                null,
                "{\"title\":\"测试\",\"summary\":\"摘要\",\"structuredFacts\":{"
            )
        )
    }

    @Test
    fun `complete json is not treated as token exhaustion`() {
        assertFalse(
            AiMemoryService.shouldTreatAsTokenExhausted(
                "stop",
                "{\"title\":\"测试\",\"summary\":\"摘要\",\"analysis\":\"分析\",\"memory\":\"记忆\",\"structuredFacts\":{}}"
            )
        )
    }
}
