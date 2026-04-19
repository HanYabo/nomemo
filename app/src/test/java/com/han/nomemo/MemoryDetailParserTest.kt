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

    @Test
    fun pickupCode_withLetterPrefix_preservedFromExplicitLabel() {
        val record = deliveryRecord(
            summary = "取件码：B-2-6104\n地址：XX小区菜鸟驿站"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("B-2-6104", info!!.code)
    }

    @Test
    fun pickupCode_withOcrGarbledKeyword_stillExtractsCode() {
        val record = deliveryRecord(
            summary = "取件琅：3821\nXX小区丰巢快递柜"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("3821", info!!.code)
    }

    @Test
    fun pickupCode_shortCode_likeTwoDigitPickupNumber() {
        val record = pickupRecord(
            summary = "取餐码：28\n瑞幸咖啡(XX大学店)\n拿铁"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("28", info!!.code)
    }

    @Test
    fun pickupLocation_withStoreNameContainingDian() {
        val record = pickupRecord(
            summary = "取餐码：A088\n星巴克(XX大学店)\n拿铁"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("A088", info!!.code)
        assertFalse(info.navigationQuery.isBlank())
    }

    @Test
    fun deliveryAddress_withCommunityName() {
        val record = deliveryRecord(
            summary = "取件码：6-3-2201\nXX小区菜鸟驿站\n待取件"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("6-3-2201", info!!.code)
        assertFalse(info.navigationQuery.isBlank())
    }

    @Test
    fun deliveryCode_withOcrGarbledMa_stillExtracts() {
        val record = deliveryRecord(
            summary = "取餐玛：156\n蜜雪冰城(XX校区店)"
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("156", info!!.code)
    }

    @Test
    fun pickupStructuredFields_trimOrderBodyNoiseFromLabeledValues() {
        val record = pickupRecord(
            summary = """
                取餐码：7904
                门店：幸运咖（甜心荟店）
                商品：芭乐抹茶椰（冰/标准糖）x1，应付8元；门店：幸运咖（甜心荟店）；地址：河南省郑州市郑州航空港经济综合实验区；门店已接单，请于下单当日及时到店取餐。
                地址：河南省郑州市郑州航空港经济综合实验区，请于下单当日及时到店取餐
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("7904", info!!.code)
        assertEquals("幸运咖（甜心荟店）", info.primaryValue)
        assertEquals("芭乐抹茶椰（冰/标准糖）x1", info.secondaryValue)
        assertEquals("河南省郑州市郑州航空港经济综合实验区", info.navigationQuery)
    }

    @Test
    fun pickupStoreLabel_doesNotSwallowAddress() {
        val record = pickupRecord(
            summary = """
                取餐码：7904
                门店：幸运咖（甜心荟店），地址：河南省郑州市郑州航空港经济综合实验区
                地址：河南省郑州市郑州航空港经济综合实验区
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("7904", info!!.code)
        assertEquals("幸运咖（甜心荟店）", info.primaryValue)
    }

    @Test
    fun pickupStoreLabel_rejectsPseudoLocationSentence() {
        val record = pickupRecord(
            summary = """
                取餐码：A088
                店铺：地址位于河南省郑州市郑州航空港经济综合实验区
                瑞幸咖啡（甜心荟店）
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("A088", info!!.code)
        assertEquals("瑞幸咖啡", info.primaryValue)
    }

    @Test
    fun pickupLocation_trimsInstructionTail() {
        val record = pickupRecord(
            summary = """
                取餐码：7904
                地址：河南省郑州市郑州航空港经济综合实验区，请于下单当日及时到店取餐
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("7904", info!!.code)
        assertEquals("河南省郑州市郑州航空港经济综合实验区", info.navigationQuery)
    }

    @Test
    fun deliveryLocation_trimsInstructionTailWithoutBreakingAddressRegression() {
        val record = deliveryRecord(
            summary = """
                取件码：6124
                地址：上海市徐汇区宜山路700号2号楼北门菜鸟驿站，请在今天18:00前前往领取
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("6124", info!!.code)
        assertEquals("上海市徐汇区宜山路700号2号楼北门菜鸟驿站", info.navigationQuery)
    }

    @Test
    fun pickupLocation_stripsLeadingLocatedAtPrefix() {
        val record = pickupRecord(
            summary = """
                取餐码：7904
                地址：位于河南省郑州市郑州航空港经济综合实验区
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("7904", info!!.code)
        assertEquals("河南省郑州市郑州航空港经济综合实验区", info.navigationQuery)
    }

    @Test
    fun pickupItem_stripsLeadingIsPrefix() {
        val record = pickupRecord(
            summary = """
                取餐码：7904
                商品：为芭乐抹茶椰（冰/标准糖）x1，应付8元
            """.trimIndent()
        )

        val info = MemoryDetailParser.parseStructuredPickupInfo(record)

        assertNotNull(info)
        assertEquals("7904", info!!.code)
        assertEquals("芭乐抹茶椰（冰/标准糖）x1", info.secondaryValue)
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

    private fun pickupRecord(
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
            CategoryCatalog.CODE_LIFE_PICKUP,
            "外卖",
            0L,
            false,
            false
        )
    }
}
