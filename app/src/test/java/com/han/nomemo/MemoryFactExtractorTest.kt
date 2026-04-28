package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MemoryFactExtractorTest {
    @Test
    fun orderNumber_isNotTreatedAsPickupCode() {
        val facts = MemoryFactExtractor.extractLocalFacts(
            userText = "瑞幸咖啡订单号：123456789012，应付 18 元",
            aiRawVisibleText = null,
            memory = null,
            analysis = null,
            summary = null,
            title = null,
            categoryCode = CategoryCatalog.CODE_LIFE_PICKUP
        )

        assertNull(facts.pickupCode)
        assertEquals("123456789012", facts.orderNumber)
    }

    @Test
    fun deliveryCodeAndLocation_areExtractedFromExplicitLabels() {
        val facts = MemoryFactExtractor.extractLocalFacts(
            userText = """
                取件码：5-2-101
                地址：上海市徐汇区宜山路700号2号楼北门菜鸟驿站
            """.trimIndent(),
            aiRawVisibleText = null,
            memory = null,
            analysis = null,
            summary = null,
            title = null,
            categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
        )

        assertEquals("5-2-101", facts.pickupCode)
        assertEquals("上海市徐汇区宜山路700号2号楼北门菜鸟驿站", facts.location)
    }

    @Test
    fun standaloneMealCode_isExtractedFromMerchantContext() {
        val facts = MemoryFactExtractor.extractLocalFacts(
            userText = """
                订单详情
                8258
                订单已完成，感谢光顾
                喜茶郑州新田360广场店
                热碎银子糯糯
            """.trimIndent(),
            aiRawVisibleText = null,
            memory = null,
            analysis = null,
            summary = null,
            title = null,
            categoryCode = CategoryCatalog.CODE_QUICK_NOTE
        )

        assertEquals("8258", facts.pickupCode)
        assertEquals("pickup", facts.domain)
        assertEquals("meal", facts.pickupCodeType)
    }

    @Test
    fun reconciler_rejectsUnsupportedAiPickupCodeAndFallsBackLocal() {
        val aiFacts = """
            {
              "domain": "delivery",
              "pickupCode": "9999",
              "pickupCodeType": "package",
              "pickupCodeConfidence": 0.95,
              "pickupCodeEvidence": "取件码 9999",
              "location": null,
              "locationConfidence": 0.0,
              "locationEvidence": null,
              "merchantOrCompany": null,
              "itemName": null,
              "orderNumber": null,
              "trackingNumber": null,
              "amount": null,
              "timeWindow": null,
              "rawVisibleText": null
            }
        """.trimIndent()

        val reconciled = MemoryStructuredFactsJson.parse(
            MemoryFactReconciler.reconcileToJson(
                userText = "取件码：6124\n菜鸟驿站",
                aiStructuredFactsJson = aiFacts,
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertNotNull(reconciled)
        assertEquals("6124", reconciled!!.pickupCode)
    }

    @Test
    fun structuredFacts_canDriveDetailCardEvenWhenCategoryWasWrong() {
        val factsJson = MemoryFactReconciler.reconcileToJson(
            userText = "取件码：6124\n地址：XX小区菜鸟驿站",
            aiStructuredFactsJson = "",
            title = null,
            summary = null,
            analysis = null,
            memory = null,
            categoryCode = CategoryCatalog.CODE_QUICK_NOTE
        )
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_AI,
            "快递",
            "取件码 6124",
            "取件码：6124\n地址：XX小区菜鸟驿站",
            "",
            "",
            "",
            "",
            "test",
            CategoryCatalog.GROUP_QUICK,
            CategoryCatalog.CODE_QUICK_NOTE,
            "小记",
            0L,
            false,
            false,
            factsJson
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("取件码", info!!.sectionTitle)
        assertEquals("6124", info.code)
    }

    @Test
    fun takeoutSummary_usesStableStructuredFormat() {
        val factsJson = MemoryFactReconciler.reconcileToJson(
            userText = "取餐码：A088\n门店：瑞幸咖啡(XX大学店)",
            aiStructuredFactsJson = "",
            title = null,
            summary = "瑞幸咖啡待取餐",
            analysis = null,
            memory = null,
            categoryCode = CategoryCatalog.CODE_LIFE_PICKUP
        )

        assertEquals(
            "取餐码 A088｜瑞幸咖啡(XX大学店)",
            MemoryFactReconciler.stableSummary(
                CategoryCatalog.CODE_LIFE_PICKUP,
                "瑞幸咖啡待取餐",
                factsJson
            )
        )
    }

    @Test
    fun deliveryDomain_canOverrideWrongPickupCategory() {
        val factsJson = MemoryFactReconciler.reconcileToJson(
            userText = """
                菜鸟驿站
                取件码：6124
                包裹已到站，请及时领取
            """.trimIndent(),
            aiStructuredFactsJson = "",
            title = null,
            summary = null,
            analysis = null,
            memory = null,
            categoryCode = CategoryCatalog.CODE_LIFE_PICKUP
        )

        val facts = MemoryStructuredFactsJson.parse(factsJson)

        assertNotNull(facts)
        assertEquals("delivery", facts!!.domain)
        assertEquals(
            CategoryCatalog.CODE_LIFE_DELIVERY,
            MemoryFactReconciler.normalizeCategoryCode(CategoryCatalog.CODE_LIFE_PICKUP, factsJson)
        )
        assertEquals(
            "取件码 6124｜菜鸟",
            MemoryFactReconciler.stableSummary(CategoryCatalog.CODE_LIFE_PICKUP, "", factsJson)
        )
    }
}
