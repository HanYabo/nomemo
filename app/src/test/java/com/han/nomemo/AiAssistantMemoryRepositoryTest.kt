package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiAssistantMemoryRepositoryTest {
    @Test
    fun `delivery search only returns active delivery memories`() {
        val repository = AiAssistantMemoryRepository(
            loadActiveRecords = {
                listOf(
                    record("delivery", CategoryCatalog.CODE_LIFE_DELIVERY, "快递取件码 A123"),
                    record("pickup", CategoryCatalog.CODE_LIFE_PICKUP, "奶茶取餐码 8258"),
                    record("note", CategoryCatalog.CODE_QUICK_NOTE, "普通小记")
                )
            }
        )

        val results = repository.searchDelivery()

        assertEquals(listOf("delivery"), results.map { it.recordId })
    }

    @Test
    fun `shopping search returns pickup delivery card and ticket memories`() {
        val repository = AiAssistantMemoryRepository(
            loadActiveRecords = {
                listOf(
                    record("delivery", CategoryCatalog.CODE_LIFE_DELIVERY, "快递"),
                    record("pickup", CategoryCatalog.CODE_LIFE_PICKUP, "取餐"),
                    record("card", CategoryCatalog.CODE_LIFE_CARD, "会员卡"),
                    record("ticket", CategoryCatalog.CODE_LIFE_TICKET, "电影票"),
                    record("note", CategoryCatalog.CODE_QUICK_NOTE, "普通小记")
                )
            }
        )

        val results = repository.searchShopping()

        assertEquals(listOf("delivery", "pickup", "card", "ticket"), results.map { it.recordId })
    }

    @Test
    fun `fuzzy search checks title summary memory analysis source note and category`() {
        val repository = AiAssistantMemoryRepository(
            loadActiveRecords = {
                listOf(
                    record("title", CategoryCatalog.CODE_QUICK_NOTE, "咖啡豆补货"),
                    record("summary", CategoryCatalog.CODE_QUICK_NOTE, "普通标题", summary = "明天买咖啡滤纸"),
                    record("analysis", CategoryCatalog.CODE_QUICK_NOTE, "普通标题", analysis = "和咖啡机维护有关"),
                    record("miss", CategoryCatalog.CODE_QUICK_NOTE, "天气很好")
                )
            }
        )

        val results = repository.fuzzySearch("咖啡")

        assertEquals(listOf("title", "summary", "analysis"), results.map { it.recordId })
    }

    @Test
    fun `archive candidates are not archived until confirmation calls archiveRecords`() {
        val now = 10L * DAY_MS
        val archivedIds = mutableListOf<String>()
        val repository = AiAssistantMemoryRepository(
            loadActiveRecords = {
                listOf(
                    record(
                        id = "expired_delivery",
                        categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY,
                        title = "三天前的快递",
                        createdAt = now - 5L * DAY_MS
                    ),
                    record(
                        id = "fresh_delivery",
                        categoryCode = CategoryCatalog.CODE_LIFE_DELIVERY,
                        title = "今天的快递",
                        createdAt = now
                    )
                )
            },
            archiveRecord = { archivedIds += it }
        )

        val candidates = repository.findExpiredArchiveCandidates(nowMs = now)

        assertEquals(listOf("expired_delivery"), candidates.map { it.recordId })
        assertTrue(archivedIds.isEmpty())

        repository.archiveRecords(candidates.map { it.recordId })

        assertEquals(listOf("expired_delivery"), archivedIds)
    }

    private fun record(
        id: String,
        categoryCode: String,
        title: String,
        createdAt: Long = 0L,
        summary: String = "",
        analysis: String = ""
    ): MemoryRecord {
        return MemoryRecord(
            id,
            createdAt,
            MemoryRecord.MODE_NORMAL,
            title,
            summary,
            title,
            title,
            "",
            analysis,
            title,
            "test",
            CategoryCatalog.getGroupByCategoryCode(categoryCode),
            categoryCode,
            CategoryCatalog.getCategoryName(categoryCode),
            0L,
            false,
            false,
            "",
            "",
            ""
        )
    }

    companion object {
        private const val DAY_MS = 24L * 60L * 60L * 1000L
    }
}
