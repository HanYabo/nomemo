package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRecordPresentationTest {
    @Test
    fun analyzedRecord_prefersCompactSummaryOnCard() {
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_NORMAL,
            "邮件邀请",
            "这是一封关于 MiMo Orbit 激励计划的邀请邮件。",
            "原始输入",
            "",
            "",
            "这是一篇关于小米 MiMo Orbit 激励计划的邀请邮件，主要介绍了计划背景、权益和申请流程。\n\n🎁 计划详情\n后续还有更多说明。",
            "memory",
            "cloud",
            CategoryCatalog.GROUP_QUICK,
            CategoryCatalog.CODE_QUICK_NOTE,
            "小记",
            0L,
            false,
            false
        )

        assertTrue(hasPersistedAiAnalysis(record))
        assertEquals("这是一封关于 MiMo Orbit 激励计划的邀请邮件。", preferredCardSummaryText(record))
    }

    @Test
    fun plainManualRecord_isNotTreatedAsAnalyzed() {
        val record = MemoryRecord(
            0L,
            MemoryRecord.MODE_NORMAL,
            "标题",
            "摘要",
            "原始输入",
            "",
            "",
            "",
            "memory",
            "manual",
            CategoryCatalog.GROUP_QUICK,
            CategoryCatalog.CODE_QUICK_NOTE,
            "小记",
            0L,
            false,
            false
        )

        assertTrue(isPlainManualRecord(record))
        assertFalse(hasPersistedAiAnalysis(record))
    }
}
