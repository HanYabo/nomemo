package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MemoryDetailParserTest {
    @Test
    fun deliveryLocation_ignoresEmbeddedStationCodeAndUsesExplicitAddress() {
        val record = deliveryRecord(
            title = "菜鸟驿站取件码 6124",
            summary = "取件码：6124",
            analysis = "地点：上海市徐汇区宜山路700号 2号楼北门菜鸟驿站"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("6124", info!!.code)
        assertEquals("上海市徐汇区宜山路700号 2号楼北门菜鸟驿站", info.navigationQuery)
        assertNotEquals("6124", info.primaryValue)
        assertEquals("上海市徐汇区宜山路700号 2号楼北门菜鸟驿站", info.secondaryValue)
    }

    @Test
    fun deliveryLocation_skipsPickupCodeLineWhenChoosingAddressDetail() {
        val record = deliveryRecord(
            title = "快递已到",
            summary = """
                菜鸟驿站
                取件码：6124
                上海市徐汇区宜山路700号 2号楼北门旁
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("6124", info!!.code)
        assertNotEquals("6124", info.primaryValue)
        assertFalse(info.navigationQuery.isBlank())
    }

    private fun deliveryRecord(
        title: String = "",
        summary: String = "",
        sourceText: String = "",
        analysis: String = "",
        memory: String = "",
        note: String = ""
    ): MemoryRecord {
        return MemoryRecord(
            0L,
            MemoryRecord.MODE_AI,
            title,
            summary,
            sourceText,
            note,
            "",
            analysis,
            memory,
            "test",
            CategoryCatalog.GROUP_LIFE,
            CategoryCatalog.CODE_LIFE_DELIVERY,
            "快递",
            0L,
            false,
            false
        )
    }
}
