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
}
