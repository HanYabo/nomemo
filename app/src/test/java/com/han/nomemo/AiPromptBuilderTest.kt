package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiPromptBuilderTest {
    @Test
    fun fullPrompt_containsVersionSchemaJsonContractAndForbiddenRules() {
        val spec = AiPromptBuilder.build(
            "TEXT",
            false,
            false,
            "取件码：6124，订单号：123456789012",
            null,
            "{}"
        )
        val prompt = spec.systemPrompt + "\n" + spec.userPrompt

        assertEquals(AiPromptMode.FULL, spec.mode)
        assertEquals(AiPromptBuilder.PROMPT_VERSION, spec.promptVersion)
        assertEquals(AiPromptBuilder.SCHEMA_VERSION, spec.schemaVersion)
        assertNull(spec.maxTokens)
        assertTrue(prompt.contains("structuredFacts"))
        assertTrue(prompt.contains("promptVersion"))
        assertTrue(prompt.contains("schemaVersion"))
        assertTrue(prompt.contains("Do not treat order numbers"))
        assertTrue(prompt.contains("summary is display-only"))
    }

    @Test
    fun economyPrompt_isCandidateSelectionPromptAndShorterThanFull() {
        val candidates = MemoryStructuredFactsJson.toJson(
            MemoryFactExtractor.extractLocalFacts(
                "取餐码：A088\n订单号：123456789012\n门店：瑞幸咖啡",
                null,
                null,
                null,
                null,
                null,
                CategoryCatalog.CODE_LIFE_PICKUP
            )
        )
        val economy = AiPromptBuilder.build(
            "TEXT",
            false,
            true,
            "取餐码：A088\n订单号：123456789012\n门店：瑞幸咖啡",
            null,
            candidates
        )
        val full = AiPromptBuilder.build(
            "TEXT",
            false,
            false,
            "取餐码：A088\n订单号：123456789012\n门店：瑞幸咖啡",
            null,
            candidates
        )

        assertEquals(AiPromptMode.ECONOMY, economy.mode)
        assertTrue(economy.userPrompt.contains("localCandidatesJson"))
        assertTrue(economy.systemPrompt.contains("Use localCandidatesJson first"))
        assertTrue(economy.systemPrompt.contains("Never treat order/tracking/waybill/phone/amount/date/time as pickupCode"))
        assertTrue(economy.maxTokens!! > 0)
        assertTrue(
            economy.systemPrompt.length + economy.userPrompt.length <
                full.systemPrompt.length + full.userPrompt.length
        )
    }

    @Test
    fun reanalyzeEconomy_usesEconomyImageBudgetAndConservativeContextRules() {
        val spec = AiPromptBuilder.build(
            "MULTIMODAL",
            true,
            true,
            "旧文本",
            "现有结构化事实",
            "{}"
        )

        assertEquals(AiPromptMode.REANALYZE_ECONOMY, spec.mode)
        assertEquals(768, spec.imageMaxSize)
        assertEquals(68, spec.imageQuality)
        assertTrue(spec.systemPrompt.contains("current raw evidence wins"))
        assertTrue(spec.userPrompt.contains("compactExistingContext"))
    }

    @Test
    fun repairPrompt_preservesJsonOnlyContract() {
        val system = AiPromptBuilder.repairSystemPrompt()
        val user = AiPromptBuilder.repairUserPrompt("```json { bad } ```")

        assertTrue(system.contains("Return one valid JSON object"))
        assertTrue(user.contains("Required schema"))
        assertTrue(user.contains("structuredFacts"))
    }
}
