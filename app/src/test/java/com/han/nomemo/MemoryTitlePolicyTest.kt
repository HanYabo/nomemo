package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryTitlePolicyTest {
    @Test
    fun takeoutTitle_prefersItemAndKeepsCodeAndAddressOut() {
        val factsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "pickup",
                pickupCode = "0090",
                pickupCodeConfidence = 1.0,
                merchantOrCompany = "郑州市中原区中原万达店",
                itemName = "招牌黑猪肉云吞（标准份15个）×1",
                location = "郑州市中原区中原万达店"
            )
        )

        val title = MemoryTitlePolicy.resolveGeneratedTitle(
            CategoryCatalog.CODE_LIFE_PICKUP,
            "在郑州市中原区中原万达店下单，取餐号0090，桌号A07",
            "订单详情",
            factsJson
        )

        assertEquals("招牌黑猪肉云吞取餐", title)
        assertFalse(title.contains("0090"))
        assertFalse(title.contains("郑州市"))
    }

    @Test
    fun takeoutTitle_fallsBackToCompactMerchant() {
        val factsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "pickup",
                merchantOrCompany = "郑州市中原区中原万达店"
            )
        )

        val title = MemoryTitlePolicy.resolveGeneratedTitle(
            CategoryCatalog.CODE_LIFE_PICKUP,
            null,
            "订单详情",
            factsJson
        )

        assertEquals("中原万达店取餐", title)
    }

    @Test
    fun deliveryTitle_usesCourierWithoutCode() {
        val factsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "delivery",
                pickupCode = "71582",
                pickupCodeConfidence = 1.0,
                merchantOrCompany = "圆通快递",
                location = "中原工学院龙湖校区菜鸟驿站"
            )
        )

        val title = MemoryTitlePolicy.resolveGeneratedTitle(
            CategoryCatalog.CODE_LIFE_DELIVERY,
            "圆通取件码71582",
            null,
            factsJson
        )

        assertEquals("圆通包裹取件", title)
    }

    @Test
    fun pickupWithoutFacts_keepsConciseModelCandidate() {
        val title = MemoryTitlePolicy.resolveGeneratedTitle(
            CategoryCatalog.CODE_LIFE_PICKUP,
            "瑞幸咖啡取餐",
            "",
            ""
        )

        assertEquals("瑞幸咖啡取餐", title)
    }

    @Test
    fun genericTitle_usesDisplayWidthWithoutEllipsis() {
        val title = MemoryTitlePolicy.resolveGeneratedTitle(
            CategoryCatalog.CODE_QUICK_NOTE,
            "这是一段非常非常非常长的完整句子，用来测试标题不会整段照搬",
            "这是一段非常非常非常长的完整句子，用来测试标题不会整段照搬",
            ""
        )

        assertTrue(MemoryTitlePolicy.displayWidth(title) <= MemoryTitlePolicy.MAX_DISPLAY_UNITS)
        assertFalse(title.endsWith("..."))
        assertFalse(title.contains("，"))
    }

    @Test
    fun manualTitleMetadata_survivesHistoricalNormalization() {
        val factsJson = MemoryTitlePolicy.markManualTitle("")
        val record = record(
            title = "我自己的超长标题，即使很长也不能被迁移覆盖",
            factsJson = factsJson
        )

        val normalized = MemoryTitlePolicy.normalizeHistorical(
            record,
            CategoryCatalog.CODE_QUICK_NOTE,
            factsJson,
            record.sourceText
        )

        assertEquals(record.title, normalized.title)
        assertTrue(MemoryTitlePolicy.isManualTitle(normalized.structuredFactsJson))
    }

    @Test
    fun historicalAiSentenceTitle_isMigratedOnce() {
        val factsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "pickup",
                merchantOrCompany = "郑州市中原区中原万达店",
                itemName = "招牌黑猪肉云吞"
            )
        )
        val record = record(
            title = "在郑州市中原区中原万达店下单，取餐号0090",
            factsJson = factsJson,
            categoryCode = CategoryCatalog.CODE_LIFE_PICKUP,
            sourceText = "中原万达店\n商品：招牌黑猪肉云吞\n取餐号：0090"
        )

        val first = MemoryRecordEvidenceNormalizer.normalize(record)
        val second = MemoryRecordEvidenceNormalizer.normalize(first)
        val normalizedFacts = MemoryStructuredFactsJson.parse(first.structuredFactsJson)

        assertEquals("招牌黑猪肉云吞取餐", first.title)
        assertEquals(MemoryTitlePolicy.CURRENT_VERSION, normalizedFacts?.titlePolicyVersion)
        assertEquals(MemoryTitlePolicy.SOURCE_GENERATED, normalizedFacts?.titleSource)
        assertEquals(first.toJson().toString(), second.toJson().toString())
    }

    private fun record(
        title: String,
        factsJson: String,
        categoryCode: String = CategoryCatalog.CODE_QUICK_NOTE,
        sourceText: String = "在郑州市中原区中原万达店下单，取餐号0090"
    ): MemoryRecord {
        return MemoryRecord(
            "title-policy",
            1L,
            MemoryRecord.MODE_AI,
            title,
            "摘要",
            sourceText,
            "",
            "",
            "分析",
            "记忆",
            "cloud",
            CategoryCatalog.getGroupByCategoryCode(categoryCode),
            categoryCode,
            CategoryCatalog.getCategoryName(categoryCode),
            0L,
            false,
            false,
            factsJson,
            "",
            "",
            MemoryRecord.LIVE_STATUS_INACTIVE
        )
    }
}
