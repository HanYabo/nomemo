package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryUnderstandingPipelineTest {
    @Test
    fun explicitFields_storeVersionedEvidenceWithSourceOffsets() {
        val source = """
            取件码：71582
            快递公司：圆通快递
            取件地址：中原工学院龙湖校区菜鸟驿站
        """.trimIndent()

        val facts = MemoryStructuredFactsJson.parse(
            MemoryUnderstandingPipeline.reconcileToJson(
                userText = source,
                aiStructuredFactsJson = "",
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertNotNull(facts)
        assertEquals(MemoryUnderstandingPipeline.CURRENT_PARSER_VERSION, facts?.parserVersion)
        assertEquals("71582", facts?.pickupCode)
        assertEquals("中原工学院龙湖校区菜鸟驿站", facts?.location)
        val codeEvidence = facts?.evidence?.firstOrNull { it.field == "pickupCode" }
        val locationEvidence = facts?.evidence?.firstOrNull { it.field == "location" }
        assertEquals("user_input", codeEvidence?.source)
        assertEquals("user_input", locationEvidence?.source)
        assertTrue((codeEvidence?.start ?: -1) >= 0)
        assertTrue((locationEvidence?.end ?: -1) > (locationEvidence?.start ?: -1))
    }

    @Test
    fun currentUserEvidence_beatsConflictingAiCandidates() {
        val aiFacts = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "delivery",
                pickupCode = "2222",
                pickupCodeType = "package",
                pickupCodeConfidence = 1.0,
                pickupCodeEvidence = "取件码：2222",
                location = "上海市徐汇区宜山路700号菜鸟驿站",
                locationConfidence = 1.0,
                locationEvidence = "地点：上海市徐汇区宜山路700号菜鸟驿站",
                rawVisibleText = "取件码：2222\n地点：上海市徐汇区宜山路700号菜鸟驿站"
            )
        )

        val facts = MemoryStructuredFactsJson.parse(
            MemoryUnderstandingPipeline.reconcileToJson(
                userText = "取件码：1111\n地点：北京市海淀区中关村软件园快递柜",
                aiStructuredFactsJson = aiFacts,
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertEquals("1111", facts?.pickupCode)
        assertEquals("北京市海淀区中关村软件园快递柜", facts?.location)
        assertEquals(
            "user_input",
            facts?.evidence?.firstOrNull { it.field == "pickupCode" }?.source
        )
    }

    @Test
    fun fieldBoundaries_preventCrossFieldContaminationWithoutKnownNoiseToken() {
        val source = """
            取件码：71582
            取件地址：中原工学院龙湖校区菜鸟驿站 qxz 地点：中原工学院龙湖校区菜鸟驿站
            商品：圆通快递包裹（文件）
        """.trimIndent()

        val facts = MemoryStructuredFactsJson.parse(
            MemoryUnderstandingPipeline.reconcileToJson(
                userText = source,
                aiStructuredFactsJson = "",
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertEquals("中原工学院龙湖校区菜鸟驿站", facts?.location)
        assertFalse(facts?.location.orEmpty().contains("qxz"))
        assertFalse(facts?.location.orEmpty().contains("商品"))
        assertFalse(facts?.location.orEmpty().contains("包裹"))
    }

    @Test
    fun rejectedAiCode_cannotReenterThroughLegacyFallback() {
        val source = "快递通知：包裹将在 4/28 19:58 后更新"
        val aiFacts = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "delivery",
                pickupCode = "2819",
                pickupCodeType = "package",
                pickupCodeConfidence = 1.0,
                pickupCodeEvidence = source
            )
        )

        val facts = MemoryStructuredFactsJson.parse(
            MemoryUnderstandingPipeline.reconcileToJson(
                userText = source,
                aiStructuredFactsJson = aiFacts,
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertNull(facts?.pickupCode)
    }

    @Test
    fun manualEdit_remainsHighestPriorityDuringLaterSanitization() {
        val edited = MemoryUnderstandingPipeline.mergeManualEdits(
            structuredFactsJson = "",
            categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY,
            code = "71582",
            primaryValue = "圆通快递",
            secondaryValue = "中原工学院龙湖校区菜鸟驿站",
            locationText = "中原工学院龙湖校区菜鸟驿站"
        )

        val sanitized = MemoryStructuredFactsJson.parse(
            MemoryUnderstandingPipeline.reconcileToJson(
                userText = "取件码：9999\n取件地址：北京市海淀区中关村软件园快递柜",
                aiStructuredFactsJson = edited,
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertEquals("71582", sanitized?.pickupCode)
        assertEquals("中原工学院龙湖校区菜鸟驿站", sanitized?.location)
        assertEquals(
            "manual_edit",
            sanitized?.evidence?.firstOrNull { it.field == "pickupCode" }?.source
        )
    }
}
