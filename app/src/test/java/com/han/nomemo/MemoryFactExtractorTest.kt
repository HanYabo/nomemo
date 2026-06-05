package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun spacedOcrMealCode_isCollapsedAndExtractedFromImageText() {
        val facts = MemoryFactExtractor.extractLocalFacts(
            userText = null,
            aiRawVisibleText = """
                订单详情
                8 2 5 8
                订单已完成，感谢光顾喜茶，期待您下次光临
                郑州新田360广场店
                订单编号 H1209521040502824961605
                热碎银子糯糯
            """.trimIndent(),
            memory = null,
            analysis = null,
            summary = null,
            title = null,
            categoryCode = CategoryCatalog.CODE_QUICK_NOTE
        )

        assertEquals("8258", facts.pickupCode)
        assertEquals("pickup", facts.domain)
        assertEquals("meal", facts.pickupCodeType)
        assertEquals("H1209521040502824961605", facts.orderNumber)
    }

    @Test
    fun isolatedThreeDigitMealCode_isExtractedOnlyWithStrongTopContext() {
        val facts = MemoryFactExtractor.extractLocalFacts(
            userText = """
                订单详情
                360
                订单已完成，感谢光顾喜茶
                郑州新田广场店
                订单编号 H1209521040502824961605
                热碎银子糯糯
                合计 20.24 元
            """.trimIndent(),
            aiRawVisibleText = null,
            memory = null,
            analysis = null,
            summary = null,
            title = null,
            categoryCode = CategoryCatalog.CODE_QUICK_NOTE
        )

        assertEquals("360", facts.pickupCode)
        assertEquals("pickup", facts.domain)
        assertEquals("meal", facts.pickupCodeType)
    }

    @Test
    fun merchantNumber_withoutIsolatedTopCode_isNotMistakenForPickupCode() {
        val facts = MemoryFactExtractor.extractLocalFacts(
            userText = """
                订单详情
                郑州新田360广场店
                订单已完成，感谢光顾喜茶
                订单编号 H1209521040502824961605
                热碎银子糯糯
            """.trimIndent(),
            aiRawVisibleText = null,
            memory = null,
            analysis = null,
            summary = null,
            title = null,
            categoryCode = CategoryCatalog.CODE_QUICK_NOTE
        )

        assertNull(facts.pickupCode)
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

    @Test
    fun documentRichEmail_doesNotStayTicketCategory() {
        val source = """
            邮件标题：你已受邀参与 Xiaomi MiMo Orbit-百万亿 Token 创造者激励计划
            发件人：Xiaomi MiMo
            这是一封活动邀请邮件，主要介绍计划背景、申请权益、时间安排和使用说明。
        """.trimIndent()

        assertEquals(
            CategoryCatalog.CODE_QUICK_NOTE,
            MemoryFactReconciler.normalizeCategoryCodeForText(
                CategoryCatalog.CODE_LIFE_TICKET,
                source,
                true
            )
        )
    }

    @Test
    fun realCouponTicket_keepsTicketCategory() {
        val source = "优惠券 券码 A88B88 到店核销后可抵扣"

        assertEquals(
            CategoryCatalog.CODE_LIFE_TICKET,
            MemoryFactReconciler.normalizeCategoryCodeForText(
                CategoryCatalog.CODE_LIFE_TICKET,
                source,
                true
            )
        )
    }

    @Test
    fun alignDomainToCategory_manualDeliveryBeatsOldPickupFacts() {
        val pickupFactsJson = MemoryFactReconciler.reconcileToJson(
            userText = """
                取餐码：8258
                店铺：喜茶
            """.trimIndent(),
            aiStructuredFactsJson = "",
            title = null,
            summary = null,
            analysis = null,
            memory = null,
            categoryCode = CategoryCatalog.CODE_LIFE_PICKUP
        )

        val aligned = MemoryFactReconciler.alignDomainToCategory(
            CategoryCatalog.CODE_LIFE_DELIVERY,
            pickupFactsJson
        )
        val facts = MemoryStructuredFactsJson.parse(aligned)

        assertNotNull(facts)
        assertEquals("delivery", facts!!.domain)
        assertEquals(
            CategoryCatalog.CODE_LIFE_DELIVERY,
            MemoryFactReconciler.normalizeCategoryCode(CategoryCatalog.CODE_LIFE_PICKUP, aligned)
        )
    }

    @Test
    fun alignDomainToCategory_manualQuickNoteClearsPickupOverride() {
        val pickupFactsJson = MemoryFactReconciler.reconcileToJson(
            userText = """
                取餐码：8258
                店铺：喜茶
            """.trimIndent(),
            aiStructuredFactsJson = "",
            title = null,
            summary = null,
            analysis = null,
            memory = null,
            categoryCode = CategoryCatalog.CODE_LIFE_PICKUP
        )

        val aligned = MemoryFactReconciler.alignDomainToCategory(
            CategoryCatalog.CODE_QUICK_NOTE,
            pickupFactsJson
        )
        val facts = MemoryStructuredFactsJson.parse(aligned)

        assertNotNull(facts)
        assertEquals("note", facts!!.domain)
        assertEquals(
            CategoryCatalog.CODE_QUICK_NOTE,
            MemoryFactReconciler.normalizeCategoryCode(CategoryCatalog.CODE_QUICK_NOTE, aligned)
        )
    }

    @Test
    fun mimoInvitation_rejectsDateTimeCodeAndProseLocation() {
        val ocr = """
            小米 MiMo 团队
            4/28 19:58
            你已受邀参与 Xiaomi MiMo Orbit-百万亿 Token 创造者激励计划
            这是来自小米MiMo团队的邀请邮件。
            的限时 Token 发放活动。我们将在30天内发放。
        """.trimIndent()
        val badAiFacts = """
            {
              "domain": "delivery",
              "pickupCode": "2819",
              "pickupCodeType": "package",
              "pickupCodeConfidence": 1.0,
              "pickupCodeEvidence": "4/28 19:58",
              "location": "的限时 Token 发放活动。我们门将在30天内发放",
              "locationConfidence": 0.48,
              "locationEvidence": "的限时 Token 发放活动。我们门将在30天内发放",
              "merchantOrCompany": "小米MiMo团队",
              "itemName": "Xiaomi MiMo Orbit-百万亿Token创造者激励计划",
              "orderNumber": null,
              "trackingNumber": null,
              "amount": null,
              "timeWindow": "4/28 19:58",
              "rawVisibleText": ${org.json.JSONObject.quote(ocr)}
            }
        """.trimIndent()

        val factsJson = MemoryFactReconciler.reconcileToJson(
            userText = "",
            aiStructuredFactsJson = badAiFacts,
            title = "小米MiMo团队取件码",
            summary = "取件码 2819｜小米MiMo团队",
            analysis = null,
            memory = null,
            categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
        )
        val facts = MemoryStructuredFactsJson.parse(factsJson)

        assertNotNull(facts)
        assertNull(facts!!.pickupCode)
        assertNull(facts.location)
        assertEquals("note", facts.domain)
        assertEquals(
            CategoryCatalog.CODE_QUICK_NOTE,
            MemoryFactReconciler.normalizeCategoryCode(
                CategoryCatalog.CODE_LIFE_DELIVERY,
                factsJson,
                ocr
            )
        )
    }

    @Test
    fun numericFields_doNotBecomePickupCodesAcrossForbiddenContexts() {
        val cases = listOf(
            "2819" to "快递通知：包裹将在 4/28 19:58 后更新",
            "13800138000" to "快递联系电话：13800138000",
            "659" to "外卖订单实付 659 元",
            "123456" to "快递订单号：123456789012"
        )

        cases.forEach { (code, source) ->
            val factsJson = MemoryFactReconciler.reconcileToJson(
                userText = source,
                aiStructuredFactsJson = """
                    {
                      "domain": "delivery",
                      "pickupCode": "$code",
                      "pickupCodeType": "package",
                      "pickupCodeConfidence": 1.0,
                      "pickupCodeEvidence": "$source",
                      "location": null,
                      "locationConfidence": 0.0,
                      "rawVisibleText": null
                    }
                """.trimIndent(),
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )

            assertNull("Unexpected pickup code from: $source", MemoryStructuredFactsJson.parse(factsJson)?.pickupCode)
        }
    }

    @Test
    fun explicitPickupCodeAndShelfCode_remainValid() {
        val explicit = MemoryStructuredFactsJson.parse(
            MemoryFactReconciler.reconcileToJson(
                userText = "圆通快递已到菜鸟驿站，取件码：71582",
                aiStructuredFactsJson = "",
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )
        val shelf = MemoryStructuredFactsJson.parse(
            MemoryFactReconciler.reconcileToJson(
                userText = "包裹已到丰巢快递柜\n货架号：5-2-101",
                aiStructuredFactsJson = "",
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertEquals("71582", explicit?.pickupCode)
        assertEquals("5-2-101", shelf?.pickupCode)
    }

    @Test
    fun takeoutNumberLabel_andMerchantOrderContextRemainValid() {
        val labeled = MemoryStructuredFactsJson.parse(
            MemoryFactReconciler.reconcileToJson(
                userText = "取餐号：0090\n郑州市中原区中原万达店\n招牌黑猪肉云吞",
                aiStructuredFactsJson = "",
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_LIFE_PICKUP
            )
        )
        val merchantOrder = MemoryStructuredFactsJson.parse(
            MemoryFactReconciler.reconcileToJson(
                userText = """
                    8258
                    订单已完成，感谢光顾喜茶
                    郑州新田360广场店
                    商品总价 ¥23
                """.trimIndent(),
                aiStructuredFactsJson = "",
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_QUICK_NOTE
            )
        )

        assertEquals("0090", labeled?.pickupCode)
        assertEquals("8258", merchantOrder?.pickupCode)
        assertEquals("pickup", merchantOrder?.domain)
    }

    @Test
    fun proseWithMarketOrFakeBuildingWord_isNotLocation() {
        listOf(
            "三星宣布全面退出中国市场",
            "增加边缘色散的楼拟效果"
        ).forEach { source ->
            val factsJson = MemoryFactReconciler.reconcileToJson(
                userText = source,
                aiStructuredFactsJson = """
                    {
                      "domain": "note",
                      "pickupCode": null,
                      "pickupCodeConfidence": 0.0,
                      "location": "$source",
                      "locationConfidence": 1.0,
                      "locationEvidence": "$source",
                      "rawVisibleText": null
                    }
                """.trimIndent(),
                title = null,
                summary = null,
                analysis = null,
                memory = null,
                categoryCode = CategoryCatalog.CODE_QUICK_NOTE
            )

            assertNull(MemoryStructuredFactsJson.parse(factsJson)?.location)
        }
    }

    @Test
    fun manualStructuredEdit_survivesEvidenceSanitization() {
        val edited = MemoryFactReconciler.mergeEditedStructuredFacts(
            structuredFactsJson = "",
            categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY,
            code = "71582",
            primaryValue = "圆通快递",
            secondaryValue = "中原工学院龙湖校区菜鸟驿站",
            locationText = "中原工学院龙湖校区菜鸟驿站"
        )
        val sanitized = MemoryStructuredFactsJson.parse(
            MemoryFactReconciler.sanitizeFactsAgainstEvidence(
                evidenceText = "这是一条手工维护的记忆",
                structuredFactsJson = edited,
                categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY
            )
        )

        assertEquals("71582", sanitized?.pickupCode)
        assertEquals("圆通快递", sanitized?.merchantOrCompany)
        assertEquals("中原工学院龙湖校区菜鸟驿站", sanitized?.location)
        assertEquals("manual_edit", sanitized?.pickupCodeEvidence)
    }

    @Test
    fun historicalMimoRecord_isSafelyAndIdempotentlyRepaired() {
        val ocr = """
            小米MiMo团队
            4/28 19:58
            这是一封来自小米MiMo团队的邀请邮件，邀请用户参与 Xiaomi MiMo Orbit-百万亿 Token 创造者激励计划。
            的限时 Token 发放活动。我们将在30天内发放。
        """.trimIndent()
        val factsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "delivery",
                pickupCode = "2819",
                pickupCodeType = "package",
                pickupCodeConfidence = 1.0,
                pickupCodeEvidence = "4/28 19:58",
                location = "的限时 Token 发放活动。我们门将在30天内发放",
                locationConfidence = 0.48,
                locationEvidence = "的限时 Token 发放活动。我们门将在30天内发放",
                merchantOrCompany = "小米MiMo团队",
                itemName = "Xiaomi MiMo Orbit-百万亿Token创造者激励计划",
                rawVisibleText = ocr
            )
        )
        val original = MemoryRecord(
            "mimo",
            123L,
            MemoryRecord.MODE_AI,
            "小米MiMo团队取件码",
            "取件码 2819｜小米MiMo团队",
            "取件码：\n快递公司：\n取件地址：\n地点：",
            "取件码：\n快递公司：\n取件地址：\n地点：",
            "file:///data/user/0/com.han.nomemo/files/mimo.jpg",
            "这是一封来自小米MiMo团队的邀请邮件，介绍激励计划和申请权益。",
            "保留原始邮件正文",
            "cloud",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_DELIVERY,
            "快递",
            456L,
            true,
            true,
            factsJson,
            "analysis-state",
            "visual-state",
            MemoryRecord.LIVE_STATUS_COMPLETED
        )

        val repaired = MemoryRecordEvidenceNormalizer.normalize(original)
        val repairedAgain = MemoryRecordEvidenceNormalizer.normalize(repaired)
        val repairedFacts = MemoryStructuredFactsJson.parse(repaired.structuredFactsJson)

        assertNull(repairedFacts?.pickupCode)
        assertNull(repairedFacts?.location)
        assertEquals("note", repairedFacts?.domain)
        assertEquals(CategoryCatalog.CODE_QUICK_NOTE, repaired.categoryCode)
        assertEquals("小米MiMo团队邀请邮件", repaired.title)
        assertFalse(repaired.summary.startsWith("取件码"))
        assertEquals("", repaired.sourceText)
        assertEquals("", repaired.note)
        assertEquals(original.imageUri, repaired.imageUri)
        assertEquals(original.analysis, repaired.analysis)
        assertEquals(original.memory, repaired.memory)
        assertEquals(original.reminderAt, repaired.reminderAt)
        assertEquals(original.isReminderDone, repaired.isReminderDone)
        assertEquals(original.isArchived, repaired.isArchived)
        assertEquals(original.liveStatusState, repaired.liveStatusState)
        assertEquals(repaired.toJson().toString(), repairedAgain.toJson().toString())
    }

    @Test
    fun legitimateDeliveryRecord_isNotDamagedByHistoricalNormalization() {
        val source = "圆通快递已到中原工学院龙湖校区菜鸟驿站\n取件码：71582"
        val factsJson = MemoryFactReconciler.reconcileToJson(
            source,
            "",
            null,
            null,
            null,
            null,
            CategoryCatalog.CODE_LIFE_DELIVERY
        )
        val record = MemoryRecord(
            "delivery",
            1L,
            MemoryRecord.MODE_AI,
            "圆通取件码",
            "取件码 71582｜圆通",
            source,
            source,
            "",
            "包裹已经到站。",
            source,
            "cloud",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_DELIVERY,
            "快递",
            0L,
            false,
            false,
            factsJson,
            "",
            "",
            MemoryRecord.LIVE_STATUS_ACTIVE
        )

        val normalized = MemoryRecordEvidenceNormalizer.normalize(record)

        assertEquals("71582", MemoryStructuredFactsJson.parse(normalized.structuredFactsJson)?.pickupCode)
        assertEquals(CategoryCatalog.CODE_LIFE_DELIVERY, normalized.categoryCode)
        assertEquals(record.title, normalized.title)
        assertEquals(record.summary, normalized.summary)
        assertTrue(normalized.isLiveStatusActive)
    }

    @Test
    fun historicalTakeoutRecord_recoversCodeAndCategoryFromRawEvidence() {
        val rawVisibleText = """
            8258
            订单已完成，感谢光顾喜茶
            郑州新田360广场店
            商品总价 ¥23
        """.trimIndent()
        val oldFactsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "ticket",
                pickupCodeType = "meal",
                location = "郑州新田360广场店",
                locationConfidence = 0.9,
                locationEvidence = "郑州新田360广场店",
                merchantOrCompany = "喜茶",
                rawVisibleText = rawVisibleText
            )
        )
        val record = MemoryRecord(
            "takeout",
            1L,
            MemoryRecord.MODE_AI,
            "喜茶订单",
            "喜茶订单",
            "",
            "",
            "",
            "喜茶订单分析",
            "喜茶订单正文",
            "cloud",
            CategoryCatalog.GROUP_QUICK,
            CategoryCatalog.CODE_QUICK_NOTE,
            "小记",
            0L,
            false,
            false,
            oldFactsJson,
            "",
            "",
            MemoryRecord.LIVE_STATUS_COMPLETED
        )

        val normalized = MemoryRecordEvidenceNormalizer.normalize(record)
        val facts = MemoryStructuredFactsJson.parse(normalized.structuredFactsJson)

        assertEquals("8258", facts?.pickupCode)
        assertEquals("pickup", facts?.domain)
        assertEquals(CategoryCatalog.CODE_LIFE_PICKUP, normalized.categoryCode)
        assertTrue(normalized.isLiveStatusCompleted)
    }

    @Test
    fun validatedTakeoutFacts_correctStaleQuickNoteCategory() {
        val rawVisibleText = "8258\n订单已完成，感谢光顾喜茶\n郑州新田360广场店"
        val factsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFactsJson.parse(
                MemoryFactReconciler.reconcileToJson(
                    rawVisibleText,
                    "",
                    null,
                    null,
                    null,
                    null,
                    CategoryCatalog.CODE_QUICK_NOTE
                )
            )!!.copy(rawVisibleText = rawVisibleText)
        )
        val record = MemoryRecord(
            "stale-category",
            1L,
            MemoryRecord.MODE_AI,
            "喜茶订单",
            "取餐码 8258｜喜茶",
            "",
            "",
            "",
            "喜茶订单分析",
            "喜茶订单正文",
            "cloud",
            CategoryCatalog.GROUP_QUICK,
            CategoryCatalog.CODE_QUICK_NOTE,
            "小记",
            0L,
            false,
            false,
            factsJson,
            "",
            "",
            MemoryRecord.LIVE_STATUS_COMPLETED
        )

        assertEquals(
            CategoryCatalog.CODE_LIFE_PICKUP,
            MemoryRecordEvidenceNormalizer.normalize(record).categoryCode
        )
    }
}
