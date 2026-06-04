package com.han.nomemo

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AiAssistantService(context: Context) {
    private val appContext = context.applicationContext
    private val settingsStore = SettingsStore(appContext)
    private val aiMemoryService = AiMemoryService(appContext)

    fun summarize(userText: String, records: List<MemoryRecord>, imageUri: Uri?): String {
        if (imageUri != null) {
            return summarizeWithImage(userText, records, imageUri)
        }
        if (records.isEmpty()) {
            return "没有找到可总结的记忆。你可以换个关键词，或者先在记忆库里新增几条相关内容。"
        }
        if (settingsStore.isAiAvailable()) {
            runCatching {
                withAssistantLiveUpdate(userText, hasImage = false) {
                    requestTextCompletion(
                        systemPrompt = SUMMARY_SYSTEM_PROMPT,
                        userPrompt = buildSummaryUserPrompt(userText, records),
                        maxTokens = if (settingsStore.economyMode) 650 else 1000,
                        temperature = 0.25
                    )
                }
            }.getOrNull()
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?.let { return it }
        }
        return buildLocalSummary(records)
    }

    fun inferRouteByAi(userText: String): AiAssistantRoute? {
        if (!settingsStore.isAiAvailable() || userText.isBlank()) return null
        val content = runCatching {
            withAssistantLiveUpdate(userText, hasImage = false) {
                requestTextCompletion(
                    systemPrompt = INTENT_SYSTEM_PROMPT,
                    userPrompt = userText,
                    maxTokens = 220,
                    temperature = 0.0
                )
            }
        }.getOrNull() ?: return null
        val json = runCatching { JSONObject(extractJsonObject(content)) }.getOrNull() ?: return null
        val intent = runCatching {
            AiAssistantIntent.valueOf(json.optString("intent").trim().uppercase(Locale.ROOT))
        }.getOrDefault(AiAssistantIntent.UNKNOWN)
        if (intent == AiAssistantIntent.UNKNOWN) return null

        val categoryCodes = mutableSetOf<String>()
        val rawCodes = json.optJSONArray("categoryCodes")
        if (rawCodes != null) {
            for (index in 0 until rawCodes.length()) {
                val code = rawCodes.optString(index).trim()
                if (CategoryCatalog.getAllCategories().any { it.categoryCode == code }) {
                    categoryCodes += code
                }
            }
        }
        val recentDays = json.optInt("recentDays", 0).takeIf { it > 0 }
        val query = json.optString("query").trim().ifBlank { userText.trim() }
        return AiAssistantRoute(
            intent = intent,
            query = query,
            categoryCodes = categoryCodes,
            recentDays = recentDays,
            requiresConfirmation = intent == AiAssistantIntent.ARCHIVE_MEMORY ||
                intent == AiAssistantIntent.DELETE_MEMORY,
            reason = "ai_intent_fallback"
        )
    }

    private fun summarizeWithImage(userText: String, records: List<MemoryRecord>, imageUri: Uri): String {
        val prompt = buildString {
            append(userText.ifBlank { "请识别这张图片里的内容，并说明它和我的记忆库是否有关。" })
            if (records.isNotEmpty()) {
                append("\n\n可参考的记忆上下文：\n")
                append(compressRecords(records.take(12)))
            }
        }
        return runCatching {
            withAssistantLiveUpdate(userText, hasImage = true) {
                val result = aiMemoryService.generateEnhancedMemory(
                    prompt,
                    imageUri,
                    compressRecords(records.take(12))
                )
                firstNonBlank(result.analysis, result.memory, result.summary)
            }
        }.getOrNull()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "我收到了图片，但当前没有得到可用的图片识别结果。你可以检查 AI 配置后再试，或把图片里的关键信息用文字发给我。"
    }

    private fun <T> withAssistantLiveUpdate(
        userText: String,
        hasImage: Boolean,
        block: () -> T
    ): T {
        val sessionId = UUID.randomUUID().toString()
        NoMemoLiveUpdateNotifier.notifyAssistantAiAnalysis(
            appContext,
            sessionId,
            userText,
            hasImage
        )
        return try {
            block()
        } finally {
            NoMemoLiveUpdateNotifier.cancelAssistantAiAnalysis(appContext, sessionId)
        }
    }

    private fun buildSummaryUserPrompt(userText: String, records: List<MemoryRecord>): String {
        return buildString {
            append("用户请求：")
            append(userText.ifBlank { "总结这些记忆" })
            append("\n\n请基于下面的 NoMemo 记忆生成自然语言回复。不要编造不存在的事实；不要输出删除、归档、修改等执行指令；如需建议危险操作，只能建议用户确认。\n\n")
            append(compressRecords(records))
        }
    }

    private fun compressRecords(records: List<MemoryRecord>): String {
        if (records.isEmpty()) return "无相关记忆。"
        return records.take(24).joinToString(separator = "\n") { record ->
            val facts = MemoryStructuredFactsJson.parse(record.structuredFactsJson)
            buildString {
                append("- id=")
                append(record.recordId)
                append(" | 时间=")
                append(formatTime(record.createdAt))
                append(" | 分类=")
                append(record.categoryName.orEmpty())
                append(" | 标题=")
                append(compact(record.title, 80))
                append(" | 摘要=")
                append(compact(firstNonBlank(record.summary, record.memory, record.sourceText), 140))
                facts?.let {
                    append(" | 结构化=")
                    append(
                        listOfNotNull(
                            it.merchantOrCompany?.let { value -> "商家/公司=$value" },
                            it.itemName?.let { value -> "物品=$value" },
                            it.pickupCode?.let { value -> "取件/取餐码=$value" },
                            it.location?.let { value -> "地点=$value" },
                            it.amount?.let { value -> "金额=$value" },
                            it.timeWindow?.let { value -> "时间=$value" }
                        ).joinToString("; ").ifBlank { "无" }
                    )
                }
            }
        }
    }

    private fun buildLocalSummary(records: List<MemoryRecord>): String {
        val categoryCounts = records
            .groupingBy { it.categoryName.orEmpty().ifBlank { "未分类" } }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(4)
            .joinToString("、") { "${it.key} ${it.value} 条" }
        val latest = records.take(5).joinToString("\n") { record ->
            "· ${compact(firstNonBlank(record.title, record.summary, record.memory), 36)}"
        }
        return buildString {
            append("我找到了 ${records.size} 条相关记忆。")
            if (categoryCounts.isNotBlank()) {
                append("主要分布在：")
                append(categoryCounts)
                append("。")
            }
            append("\n\n最近几条：\n")
            append(latest)
        }
    }

    private fun requestTextCompletion(
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int,
        temperature: Double
    ): String {
        val baseUrl = settingsStore.resolvedApiBaseUrl()
        val apiKey = settingsStore.resolvedApiKey()
        val model = settingsStore.resolvedTextModel().ifBlank { settingsStore.resolvedApiModel() }
        if (baseUrl.isBlank() || apiKey.isBlank() || model.isBlank()) {
            throw IllegalStateException("AI config unavailable")
        }

        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", systemPrompt))
            .put(JSONObject().put("role", "user").put("content", userPrompt))
        val payload = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("max_tokens", maxTokens.coerceAtLeast(64))
            .put("temperature", temperature)

        val connection = (URL(baseUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 30_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Authorization", "Bearer $apiKey")
        }
        val bytes = payload.toString().toByteArray(StandardCharsets.UTF_8)
        connection.outputStream.use { output ->
            output.write(bytes)
        }
        val responseText = if (connection.responseCode in 200..299) {
            connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        } else {
            val errorText = connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
            throw IllegalStateException("AI request failed: ${connection.responseCode} ${errorText.orEmpty()}")
        }
        val root = JSONObject(responseText)
        return root
            .optJSONArray("choices")
            ?.optJSONObject(0)
            ?.optJSONObject("message")
            ?.optString("content")
            ?.trim()
            ?: ""
    }

    private fun extractJsonObject(content: String): String {
        val trimmed = content.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) return trimmed
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        return if (start >= 0 && end > start) trimmed.substring(start, end + 1) else trimmed
    }

    private fun firstNonBlank(vararg values: String?): String {
        return values.firstNotNullOfOrNull { value ->
            value?.trim()?.takeIf { it.isNotEmpty() }
        }.orEmpty()
    }

    private fun compact(value: String?, maxLength: Int): String {
        val single = value.orEmpty().replace('\n', ' ').replace(Regex("""\s+"""), " ").trim()
        return if (single.length <= maxLength) single else single.substring(0, maxLength) + "..."
    }

    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(Date(timestamp))
    }

    companion object {
        private const val SUMMARY_SYSTEM_PROMPT =
            "You are NoMemo's memory-library Copilot. Answer in Chinese. Use only the supplied memories. " +
                "You may summarize, organize, and point to relevant records, but you must never directly execute or claim to execute delete/archive/update/reminder actions."

        private const val INTENT_SYSTEM_PROMPT =
            "You classify a user's NoMemo assistant request. Return JSON only with fields: " +
                "intent, query, categoryCodes, recentDays. intent must be one of SEARCH_MEMORY, SUMMARIZE_MEMORY, " +
                "CREATE_MEMORY, ARCHIVE_MEMORY, DELETE_MEMORY, SET_REMINDER, OPEN_MEMORY, REANALYZE_MEMORY, UNKNOWN. " +
                "Use category codes only from LIFE_PICKUP, LIFE_DELIVERY, LIFE_CARD, LIFE_TICKET, WORK_TODO, WORK_SCHEDULE, QUICK_NOTE. " +
                "Dangerous intents are classification only; never execute anything."
    }
}
