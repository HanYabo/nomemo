package com.han.nomemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiAssistantActionRouterTest {
    private val router = AiAssistantActionRouter()

    @Test
    fun `delivery query routes to delivery search`() {
        val route = router.route("查看我的快递", hasImage = false)

        assertEquals(AiAssistantIntent.SEARCH_MEMORY, route.intent)
        assertEquals(setOf(CategoryCatalog.CODE_LIFE_DELIVERY), route.categoryCodes)
    }

    @Test
    fun `shopping query routes to life consumption categories`() {
        val route = router.route("帮我找一下购物记录", hasImage = false)

        assertEquals(AiAssistantIntent.SEARCH_MEMORY, route.intent)
        assertEquals(AiAssistantActionRouter.SHOPPING_CATEGORY_CODES, route.categoryCodes)
    }

    @Test
    fun `archive expired memories requires confirmation`() {
        val route = router.route("帮我归档过期的记忆", hasImage = false)

        assertEquals(AiAssistantIntent.ARCHIVE_MEMORY, route.intent)
        assertTrue(route.requiresConfirmation)
        assertTrue(route.categoryCodes.contains(CategoryCatalog.CODE_LIFE_DELIVERY))
        assertTrue(route.categoryCodes.contains(CategoryCatalog.CODE_WORK_SCHEDULE))
    }
}
