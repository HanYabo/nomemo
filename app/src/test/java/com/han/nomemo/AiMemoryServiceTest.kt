package com.han.nomemo

import org.json.JSONObject
import org.junit.Assert.assertEquals
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

    @Test
    fun `ai result normalization fills missing schema fields before validation`() {
        val raw = JSONObject()
            .put("title", "万达取餐")
            .put("summary", "取餐提醒")
            .put("analysis", "保留取餐地点和事项。")
            .put("memory", "万达店取餐")
            .put("suggestedCategoryCode", "LIFE_PICKUP")
            .put(
                "structuredFacts",
                JSONObject()
                    .put("domain", "takeout")
                    .put("pickupCode", 1234)
                    .put("pickupCodeConfidence", "0.82")
            )

        val normalized = AiMemoryService.normalizeResultJsonForValidation(raw)
        val validated = AiResultValidator.validate(normalized)
        val facts = validated.getJSONObject("structuredFacts")

        assertEquals(AiPromptBuilder.PROMPT_VERSION, validated.getString("promptVersion"))
        assertEquals(AiPromptBuilder.SCHEMA_VERSION, validated.getString("schemaVersion"))
        assertEquals("pickup", facts.getString("domain"))
        assertEquals("1234", facts.getString("pickupCode"))
        assertEquals(0.82, facts.getDouble("pickupCodeConfidence"), 0.0001)
        assertTrue(facts.has("locationConfidence"))
        assertTrue(facts.isNull("location"))
    }

    @Test
    fun `ai result normalization preserves provided facts with default confidence`() {
        val raw = JSONObject()
            .put("title", "pickup")
            .put("summary", "pickup summary")
            .put("analysis", "pickup analysis")
            .put("memory", "pickup code 8258 at wanda")
            .put("suggestedCategoryCode", CategoryCatalog.CODE_LIFE_PICKUP)
            .put(
                "structuredFacts",
                JSONObject()
                    .put("domain", "pickup")
                    .put("pickupCode", "8258")
                    .put("location", "wanda")
            )

        val normalized = AiMemoryService.normalizeResultJsonForValidation(raw)
        val facts = AiResultValidator.validate(normalized).getJSONObject("structuredFacts")

        assertEquals(0.78, facts.getDouble("pickupCodeConfidence"), 0.0001)
        assertEquals(0.64, facts.getDouble("locationConfidence"), 0.0001)
    }
}
