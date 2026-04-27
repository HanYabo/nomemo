package com.han.nomemo

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class AiResultValidatorTest {
    @Test
    fun `valid schema passes validation`() {
        val validated = AiResultValidator.validate(validPayload())

        assertEquals(AiPromptBuilder.SCHEMA_VERSION, validated.getString("schemaVersion"))
    }

    @Test(expected = IllegalStateException::class)
    fun `missing structured facts fails validation`() {
        val payload = validPayload()
        payload.remove("structuredFacts")

        AiResultValidator.validate(payload)
    }

    @Test(expected = IllegalStateException::class)
    fun `invalid confidence fails validation`() {
        val payload = validPayload()
        payload.getJSONObject("structuredFacts").put("pickupCodeConfidence", 1.5)

        AiResultValidator.validate(payload)
    }

    @Test(expected = IllegalStateException::class)
    fun `invalid category code fails validation`() {
        val payload = validPayload()
        payload.put("suggestedCategoryCode", "UNKNOWN_CODE")

        AiResultValidator.validate(payload)
    }

    private fun validPayload(): JSONObject {
        return JSONObject()
            .put("promptVersion", AiPromptBuilder.PROMPT_VERSION)
            .put("schemaVersion", AiPromptBuilder.SCHEMA_VERSION)
            .put("title", "测试标题")
            .put("summary", "测试摘要")
            .put("analysis", "测试分析")
            .put("memory", "测试记忆")
            .put("suggestedCategoryCode", CategoryCatalog.CODE_LIFE_PICKUP)
            .put(
                "structuredFacts",
                JSONObject()
                    .put("domain", "pickup")
                    .put("pickupCode", "1234")
                    .put("pickupCodeType", "pickup")
                    .put("pickupCodeConfidence", 0.92)
                    .put("pickupCodeEvidence", "取餐码 1234")
                    .put("location", "一楼前台")
                    .put("locationConfidence", 0.85)
                    .put("locationEvidence", "一楼前台")
                    .put("merchantOrCompany", "瑞幸")
                    .put("itemName", JSONObject.NULL)
                    .put("orderNumber", JSONObject.NULL)
                    .put("trackingNumber", JSONObject.NULL)
                    .put("amount", JSONObject.NULL)
                    .put("timeWindow", JSONObject.NULL)
                    .put("rawVisibleText", "取餐码 1234")
            )
    }
}
