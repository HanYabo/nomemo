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
        return parseIntentResponse(content, userText)
    }

    fun inferIntentWithHistory(userText: String, chatHistory: JSONArray): AiAssistantRoute? {
        if (!settingsStore.isAiAvailable() || userText.isBlank()) return null
        val messages = JSONArray()
        messages.put(JSONObject().put("role", "system").put("content", INTENT_SYSTEM_PROMPT))
        for (i in 0 until chatHistory.length()) {
            messages.put(chatHistory.getJSONObject(i))
        }
        messages.put(JSONObject().put("role", "user").put("content", userText))
        val content = try {
            withAssistantLiveUpdate(userText, hasImage = false) {
                requestMultiTurnCompletion(messages, maxTokens = 220, temperature = 0.0)
            }
        } catch (e: Exception) {
            android.util.Log.e("AiAssistant", "inferIntentWithHistory failed", e)
            null
        } ?: return null
        return parseIntentResponse(content, userText)
    }

    fun generateChatReply(
        userText: String,
        handlerResultText: String,
        records: List<MemoryRecord>,
        chatHistory: JSONArray,
        imageUri: Uri? = null
    ): String {
        val prompt = buildString {
            append(userText)
            if (records.isNotEmpty()) {
                append("\n\n以下是和你问题相关的记忆数据：\n")
                append(compressRecords(records.take(12)))
            } else {
                append("\n\n(当前没有找到直接相关的记忆)")
            }
        }
        val reply = try {
            withAssistantLiveUpdate(userText, hasImage = imageUri != null) {
                if (imageUri != null) {
                    aiMemoryService.generateEnhancedMemory(prompt, imageUri, compressRecords(records.take(12)))
                        .let { firstNonBlank(it.analysis, it.memory, it.summary) }
                } else {
                    val messages = JSONArray()
                    messages.put(JSONObject().put("role", "system").put("content", CHAT_REPLY_SYSTEM_PROMPT))
                    for (i in 0 until chatHistory.length()) {
                        messages.put(chatHistory.getJSONObject(i))
                    }
                    messages.put(JSONObject().put("role", "user").put("content", prompt))
                    requestMultiTurnCompletion(messages, maxTokens = 800, temperature = 0.3)
                }
            }.trim().takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            android.util.Log.e("AiAssistant", "generateChatReply failed", e)
            null
        }
        return reply ?: handlerResultText
    }

    fun buildChatMessages(history: List<AiAssistantMessage>, maxTurns: Int = 10): JSONArray {
        val messages = JSONArray()
        val recentHistory = history.takeLast(maxTurns * 2)
        for (msg in recentHistory) {
            if (msg.text.isBlank()) continue
            val role = when (msg.role) {
                AiAssistantRole.USER -> "user"
                AiAssistantRole.ASSISTANT -> "assistant"
            }
            messages.put(JSONObject().put("role", role).put("content", msg.text))
        }
        return messages
    }

    private fun parseIntentResponse(content: String, fallbackQuery: String): AiAssistantRoute? {
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
        val query = json.optString("query").trim().ifBlank { fallbackQuery.trim() }
        return AiAssistantRoute(
            intent = intent,
            query = query,
            categoryCodes = categoryCodes,
            recentDays = recentDays,
            requiresConfirmation = intent == AiAssistantIntent.ARCHIVE_MEMORY ||
                intent == AiAssistantIntent.DELETE_MEMORY,
            reason = "ai_inferred"
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
        val messages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", systemPrompt))
            .put(JSONObject().put("role", "user").put("content", userPrompt))
        return requestMultiTurnCompletion(messages, maxTokens, temperature)
    }

    private fun requestMultiTurnCompletion(
        messages: JSONArray,
        maxTokens: Int,
        temperature: Double
    ): String {
        val baseUrl = resolveChatCompletionsUrl()
        val apiKey = settingsStore.resolvedApiKey()
        val model = settingsStore.resolvedTextModel().ifBlank { settingsStore.resolvedApiModel() }
        android.util.Log.d("AiAssistant", "API call: baseUrl=$baseUrl, model=$model, apiKey=${apiKey.take(8)}...")
        if (baseUrl.isBlank() || apiKey.isBlank() || model.isBlank()) {
            throw IllegalStateException("AI config unavailable")
        }

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

    private fun resolveChatCompletionsUrl(): String {
        var baseUrl = settingsStore.resolvedApiBaseUrl().trim()
        while (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length - 1)
        }
        if (baseUrl.endsWith("/chat/completions")) return baseUrl
        if (baseUrl.endsWith("/responses")) {
            return baseUrl.substring(0, baseUrl.length - "/responses".length) + "/chat/completions"
        }
        return baseUrl + "/chat/completions"
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
                "CREATE_MEMORY, ARCHIVE_MEMORY, DELETE_MEMORY, SET_REMINDER, OPEN_MEMORY, REANALYZE_MEMORY, CHAT. " +
                "Use CHAT for general conversation, greetings, opinions, questions about memories that don't fit other intents, " +
                "or when the user just wants to chat. Use category codes only from LIFE_PICKUP, LIFE_DELIVERY, LIFE_CARD, LIFE_TICKET, WORK_TODO, WORK_SCHEDULE, QUICK_NOTE. " +
                "Dangerous intents are classification only; never execute anything."

        private const val CHAT_REPLY_SYSTEM_PROMPT =
            "你是 NoMemo 的智能记忆助手。用中文和用户自然对话，像朋友一样交流。" +
                "根据用户的问题和提供的记忆数据来回答。如果没有相关记忆就如实告知并给建议。" +
                "可以闲聊、回答问题、分析记忆内容、给出建议。不要编造不存在的记忆数据。" +
                "涉及删除、归档等危险操作时，只能建议用户确认，不要直接声称已执行。"
    }
}
