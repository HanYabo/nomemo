package com.han.nomemo

import java.util.Locale

data class AiAssistantRoute(
    val intent: AiAssistantIntent,
    val query: String = "",
    val categoryCodes: Set<String> = emptySet(),
    val recentDays: Int? = null,
    val requiresConfirmation: Boolean = false,
    val reason: String = ""
)

class AiAssistantActionRouter {
    fun route(inputText: String, hasImage: Boolean): AiAssistantRoute {
        val raw = inputText.trim()
        val normalized = raw.lowercase(Locale.ROOT)
        if (raw.isBlank()) {
            return if (hasImage) {
                AiAssistantRoute(
                    intent = AiAssistantIntent.SUMMARIZE_MEMORY,
                    reason = "image_only"
                )
            } else {
                AiAssistantRoute(intent = AiAssistantIntent.UNKNOWN)
            }
        }

        if (containsAny(normalized, "删除", "删掉", "移除", "清理掉")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.DELETE_MEMORY,
                query = stripCommandWords(raw, DELETE_WORDS),
                requiresConfirmation = true,
                reason = "delete_rule"
            )
        }

        if (containsAny(normalized, "归档", "收起", "归到归档")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.ARCHIVE_MEMORY,
                query = stripCommandWords(raw, ARCHIVE_WORDS),
                categoryCodes = ARCHIVE_CANDIDATE_CATEGORY_CODES,
                requiresConfirmation = true,
                reason = "archive_rule"
            )
        }

        if (containsAny(normalized, "提醒", "到点", "闹钟", "日程提醒")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.SET_REMINDER,
                query = stripCommandWords(raw, REMINDER_WORDS),
                reason = "reminder_rule"
            )
        }

        if (containsAny(normalized, "重新分析", "再分析", "重跑ai", "重跑 ai", "reanalyze")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.REANALYZE_MEMORY,
                query = stripCommandWords(raw, REANALYZE_WORDS),
                reason = "reanalyze_rule"
            )
        }

        if (containsAny(normalized, "新建记忆", "新增记忆", "保存为记忆", "帮我记", "记一条", "记录一下")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.CREATE_MEMORY,
                query = stripCommandWords(raw, CREATE_WORDS).ifBlank { raw },
                reason = "create_rule"
            )
        }

        if (containsAny(normalized, "打开", "查看详情", "进入", "跳转")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.OPEN_MEMORY,
                query = stripCommandWords(raw, OPEN_WORDS),
                reason = "open_rule"
            )
        }

        if (containsAny(normalized, "总结", "汇总", "整理", "最近一周", "近一周", "本周", "这一周")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.SUMMARIZE_MEMORY,
                query = raw,
                categoryCodes = if (looksLikeShoppingQuery(normalized)) SHOPPING_CATEGORY_CODES else emptySet(),
                recentDays = if (containsAny(normalized, "最近一周", "近一周", "本周", "这一周")) 7 else null,
                reason = "summary_rule"
            )
        }

        if (hasImage && containsAny(normalized, "图片", "图里", "截图", "识别", "是什么", "里面有什么")) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.SUMMARIZE_MEMORY,
                query = raw,
                reason = "image_question_rule"
            )
        }

        if (looksLikeDeliveryQuery(normalized)) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.SEARCH_MEMORY,
                query = raw,
                categoryCodes = setOf(CategoryCatalog.CODE_LIFE_DELIVERY),
                reason = "delivery_rule"
            )
        }

        if (looksLikeShoppingQuery(normalized)) {
            return AiAssistantRoute(
                intent = AiAssistantIntent.SEARCH_MEMORY,
                query = raw,
                categoryCodes = SHOPPING_CATEGORY_CODES,
                reason = "shopping_rule"
            )
        }

        return AiAssistantRoute(
            intent = AiAssistantIntent.UNKNOWN,
            query = raw,
            reason = "no_keyword_match"
        )
    }

    private fun looksLikeDeliveryQuery(value: String): Boolean {
        return value.contains("快递") || value.contains("物流") || value.contains("包裹")
    }

    private fun looksLikeShoppingQuery(value: String): Boolean {
        return containsAny(
            value,
            "购物记录",
            "购物",
            "取件",
            "取货",
            "取餐",
            "订单",
            "消费",
            "卡券",
            "票券",
            "券",
            "快递",
            "物流",
            "包裹"
        )
    }

    private fun stripCommandWords(value: String, words: Set<String>): String {
        var result = value.trim()
        words.forEach { word ->
            result = result.replace(word, "", ignoreCase = true)
        }
        return result
            .replace("我的", "")
            .replace("帮我", "")
            .replace("一下", "")
            .replace("这些", "")
            .replace("相关", "")
            .trim()
    }

    private fun containsAny(source: String, vararg keywords: String): Boolean {
        return keywords.any { source.contains(it, ignoreCase = true) }
    }

    companion object {
        val SHOPPING_CATEGORY_CODES: Set<String> = setOf(
            CategoryCatalog.CODE_LIFE_PICKUP,
            CategoryCatalog.CODE_LIFE_DELIVERY,
            CategoryCatalog.CODE_LIFE_CARD,
            CategoryCatalog.CODE_LIFE_TICKET
        )

        val ARCHIVE_CANDIDATE_CATEGORY_CODES: Set<String> = setOf(
            CategoryCatalog.CODE_LIFE_PICKUP,
            CategoryCatalog.CODE_LIFE_DELIVERY,
            CategoryCatalog.CODE_WORK_TODO,
            CategoryCatalog.CODE_WORK_SCHEDULE
        )

        private val DELETE_WORDS = setOf("删除", "删掉", "移除", "清理掉")
        private val ARCHIVE_WORDS = setOf("归档", "收起", "归到归档", "过期的", "过期")
        private val REMINDER_WORDS = setOf("提醒", "到点", "闹钟", "日程提醒", "设置")
        private val REANALYZE_WORDS = setOf("重新分析", "再分析", "重跑ai", "重跑 ai", "reanalyze")
        private val CREATE_WORDS = setOf("新建记忆", "新增记忆", "保存为记忆", "帮我记", "记一条", "记录一下")
        private val OPEN_WORDS = setOf("打开", "查看详情", "进入", "跳转")
    }
}
