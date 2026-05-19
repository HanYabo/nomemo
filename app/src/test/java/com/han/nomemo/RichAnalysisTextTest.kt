package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RichAnalysisTextTest {
    @Test
    fun parseRichAnalysisContent_detectsOverviewAndBlocks() {
        val content = parseRichAnalysisContent(
            """
            这是一封关于小米 MiMo Orbit 激励计划的邀请邮件，主要介绍了计划背景、内容和申请权益。

            📧 邮件概览
            邮件标题为“你已受邀参与 Xiaomi MiMo Orbit-百万亿 Token 创造者激励计划”，由 MiMo 团队发出。

            🎁 计划详情
            该计划面向高质量 AI 用户开放，包含限时 Token 发放和后续申请说明。

            💎 权益价值
            申请通过后可获得较高价值的权益，适合后续持续参与。
            """.trimIndent()
        )

        assertNotNull(content)
        assertEquals("这是一封关于小米 MiMo Orbit 激励计划的邀请邮件，主要介绍了计划背景、内容和申请权益。", content?.overview)
        assertEquals(3, content?.blocks?.size)
        assertEquals("📧 邮件概览", content?.blocks?.get(0)?.heading)
    }

    @Test
    fun parseRichAnalysisContent_returnsNullForTransactionalText() {
        val content = parseRichAnalysisContent("喜茶订单，取餐号8258，郑州新田360广场店，实付20.24元。")
        assertNull(content)
    }
}
