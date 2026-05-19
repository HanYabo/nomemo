package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Test

class AiAnalysisStyleRouterTest {
    @Test
    fun pickupCategory_staysTransactional() {
        val style = AiAnalysisStyleRouter.resolve(
            "取餐码 8258\n喜茶订单详情\n热碎银子糯糯",
            null,
            CategoryCatalog.CODE_LIFE_PICKUP,
            true
        )

        assertEquals(AiAnalysisStyleHint.TRANSACTIONAL, style)
    }

    @Test
    fun longEmailLikeContent_usesDocumentRichStyle() {
        val style = AiAnalysisStyleRouter.resolve(
            """
                邮件标题：你已受邀参与 Xiaomi MiMo Orbit 计划
                发件人：MiMo Team
                这是一封活动邀请邮件，介绍计划背景、申请权益、时间安排和使用说明。
                用户可申请参与限时 token 活动，并查看后续说明。
            """.trimIndent(),
            null,
            CategoryCatalog.CODE_QUICK_NOTE,
            false
        )

        assertEquals(AiAnalysisStyleHint.DOCUMENT_RICH, style)
    }

    @Test
    fun mediumLengthExplainerWithNoticeTone_usesDocumentRichStyle() {
        val style = AiAnalysisStyleRouter.resolve(
            """
                这是一份活动说明，主要介绍参与资格、时间安排和使用方式。
                请查看以下详情：本次开放给已完成申请的用户，名额有限，需要在规定时间内领取权益。
                如需后续参与，请按页面提示完成报名并阅读规则说明。
            """.trimIndent(),
            null,
            CategoryCatalog.CODE_QUICK_NOTE,
            false
        )

        assertEquals(AiAnalysisStyleHint.DOCUMENT_RICH, style)
    }

    @Test
    fun longStructuredScreenshotTextWithoutPrimaryKeywords_usesDocumentRichStyle() {
        val style = AiAnalysisStyleRouter.resolve(
            """
                本页主要展示一个计划的背景介绍和参与方式
                当前开放对象为受邀用户，需要在指定时间内完成确认
                页面包含权益说明、后续流程以及使用须知
                请继续查看详情并按步骤完成操作
            """.trimIndent(),
            null,
            CategoryCatalog.CODE_QUICK_NOTE,
            true
        )

        assertEquals(AiAnalysisStyleHint.DOCUMENT_RICH, style)
    }
}
