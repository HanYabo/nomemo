package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupAutoClassifierTest {
    @Test
    fun description_matches_title_and_structured_fields() {
        val structuredFactsJson = MemoryStructuredFactsJson.toJson(
            MemoryStructuredFacts(
                domain = "pickup",
                merchantOrCompany = "喜茶",
                location = "郑州新田360广场店",
                itemName = "热碎银子糯糯"
            )
        )
        val records = listOf(
            memoryRecord(
                recordId = "match_title",
                title = "喜茶取餐码 8258",
                summary = "喜茶 新田360广场店",
                sourceText = "订单详情"
            ),
            memoryRecord(
                recordId = "match_structured",
                title = "订单详情",
                summary = "",
                sourceText = "",
                structuredFactsJson = structuredFactsJson
            ),
            memoryRecord(
                recordId = "miss",
                title = "天气很好",
                summary = "今天适合散步",
                sourceText = "公园散步"
            )
        )

        val results = GroupAutoClassifier.classify(
            albumName = "喜茶校园",
            albumDescription = "和喜茶、新田360广场店、奶茶取餐相关的记忆",
            records = records,
            existingRecordIds = emptySet()
        )

        assertEquals(listOf("match_title", "match_structured"), results.map { it.record.recordId })
        assertTrue(results.all { it.reasons.isNotEmpty() })
    }

    @Test
    fun existing_records_are_excluded() {
        val records = listOf(
            memoryRecord(
                recordId = "already_inside",
                title = "喜茶取餐码 8258",
                summary = "喜茶 订单",
                sourceText = "订单详情"
            ),
            memoryRecord(
                recordId = "candidate",
                title = "校园咖啡券",
                summary = "咖啡 活动",
                sourceText = "瑞幸 校园店"
            )
        )

        val results = GroupAutoClassifier.classify(
            albumName = "校园咖啡",
            albumDescription = "和校园咖啡相关的记忆",
            records = records,
            existingRecordIds = setOf("already_inside")
        )

        assertEquals(listOf("candidate"), results.map { it.record.recordId })
    }

    @Test
    fun weak_single_hit_does_not_pass_threshold() {
        val records = listOf(
            memoryRecord(
                recordId = "weak",
                title = "今天路过校园",
                summary = "只是随手记一下",
                sourceText = ""
            )
        )

        val results = GroupAutoClassifier.classify(
            albumName = "校园咖啡",
            albumDescription = "和校园咖啡相关的记忆",
            records = records,
            existingRecordIds = emptySet()
        )

        assertTrue(results.isEmpty())
    }

    private fun memoryRecord(
        recordId: String,
        title: String,
        summary: String,
        sourceText: String,
        structuredFactsJson: String = ""
    ): MemoryRecord {
        return MemoryRecord(
            recordId,
            0L,
            MemoryRecord.MODE_AI,
            title,
            summary,
            sourceText,
            "",
            "",
            "",
            "",
            "cloud",
            CategoryCatalog.GROUP_QUICK,
            CategoryCatalog.CODE_QUICK_NOTE,
            "小记",
            0L,
            false,
            false,
            structuredFactsJson,
            "",
            ""
        )
    }
}
