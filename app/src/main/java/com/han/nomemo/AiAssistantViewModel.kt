package com.han.nomemo

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.han.nomemo.AiAssistantMemoryRepository.Companion.toAssistantCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiAssistantViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AiAssistantMemoryRepository(application.applicationContext)
    private val router = AiAssistantActionRouter()
    private val assistantService = AiAssistantService(application.applicationContext)
    private val settingsStore = SettingsStore(application.applicationContext)
    private val pendingConfirmations = mutableMapOf<String, AiAssistantConfirmation>()

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AiAssistantUiEvent>()
    val events: SharedFlow<AiAssistantUiEvent> = _events.asSharedFlow()

    private val prefs = application.applicationContext.getSharedPreferences("nomemo_ai_assistant", Context.MODE_PRIVATE)

    init {
        val sessions = loadSessions()
        android.util.Log.d("AiAssistant", "init: loaded ${sessions.size} sessions")
        _uiState.update { it.copy(sessions = sessions) }
    }

    fun toggleHistoryPanel() {
        _uiState.update { it.copy(showHistoryPanel = !it.showHistoryPanel) }
    }

    fun closeHistoryPanel() {
        _uiState.update { it.copy(showHistoryPanel = false) }
    }

    fun startNewSession() {
        saveCurrentSession()
        _uiState.update { it.copy(messages = emptyList(), currentSessionId = null, showHistoryPanel = false) }
    }

    fun loadSession(sessionId: String) {
        val session = _uiState.value.sessions.find { it.sessionId == sessionId } ?: return
        _uiState.update {
            it.copy(
                messages = session.messages,
                currentSessionId = sessionId,
                showHistoryPanel = false
            )
        }
    }

    fun deleteSession(sessionId: String) {
        _uiState.update { state ->
            val updated = state.sessions.filter { it.sessionId != sessionId }
            val newCurrentId = if (state.currentSessionId == sessionId) null else state.currentSessionId
            val newMessages = if (state.currentSessionId == sessionId) emptyList() else state.messages
            state.copy(sessions = updated, currentSessionId = newCurrentId, messages = newMessages)
        }
        persistSessions(_uiState.value.sessions)
    }

    private fun saveCurrentSession() {
        val state = _uiState.value
        if (state.messages.isEmpty()) {
            android.util.Log.d("AiAssistant", "saveCurrentSession: no messages, skip")
            return
        }
        val firstUserMsg = state.messages.firstOrNull { it.role == AiAssistantRole.USER }?.text.orEmpty()
        val title = firstUserMsg.take(20).ifBlank { "新会话" }
        val sessionId = state.currentSessionId ?: java.util.UUID.randomUUID().toString()
        val session = AiAssistantSession(
            sessionId = sessionId,
            createdAt = state.messages.firstOrNull()?.createdAt ?: System.currentTimeMillis(),
            title = title,
            messages = state.messages
        )
        val updated = listOf(session) + state.sessions.filter { it.sessionId != session.sessionId }
        _uiState.update { it.copy(sessions = updated.take(MAX_SESSIONS), currentSessionId = session.sessionId) }
        android.util.Log.d("AiAssistant", "saveCurrentSession: sessionId=$sessionId, title=$title, msgs=${state.messages.size}, totalSessions=${updated.size}")
        persistSessions(_uiState.value.sessions)
    }

    private fun persistSessions(sessions: List<AiAssistantSession>) {
        val jsonArray = JSONArray()
        for (session in sessions) {
            val msgArray = JSONArray()
            for (msg in session.messages) {
                val msgObj = JSONObject()
                    .put("id", msg.id)
                    .put("role", msg.role.name)
                    .put("text", msg.text)
                    .put("imageUri", msg.imageUri ?: JSONObject.NULL)
                    .put("createdAt", msg.createdAt)
                    .put("isError", msg.isError)
                if (msg.memoryCards.isNotEmpty()) {
                    val cardsArray = JSONArray()
                    for (card in msg.memoryCards) {
                        cardsArray.put(JSONObject()
                            .put("recordId", card.recordId)
                            .put("title", card.title)
                            .put("summary", card.summary)
                            .put("categoryCode", card.categoryCode)
                            .put("categoryName", card.categoryName)
                            .put("createdAt", card.createdAt)
                            .put("imageUri", card.imageUri ?: JSONObject.NULL)
                        )
                    }
                    msgObj.put("memoryCards", cardsArray)
                }
                msgArray.put(msgObj)
            }
            jsonArray.put(JSONObject()
                .put("sessionId", session.sessionId)
                .put("createdAt", session.createdAt)
                .put("title", session.title)
                .put("messages", msgArray)
            )
        }
        val json = jsonArray.toString()
        android.util.Log.d("AiAssistant", "persistSessions: sessions=${sessions.size}, jsonLen=${json.length}")
        prefs.edit().putString(KEY_SESSIONS, json).apply()
    }

    private fun loadSessions(): List<AiAssistantSession> {
        val json = prefs.getString(KEY_SESSIONS, null)
        android.util.Log.d("AiAssistant", "loadSessions: json=${json?.take(100)}")
        if (json == null) return emptyList()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).mapNotNull { i ->
                val obj = array.getJSONObject(i)
                val msgArray = obj.getJSONArray("messages")
                val messages = (0 until msgArray.length()).mapNotNull { j ->
                    val msgObj = msgArray.getJSONObject(j)
                    val cardsArray = msgObj.optJSONArray("memoryCards")
                    val cards = if (cardsArray != null) {
                        (0 until cardsArray.length()).map { k ->
                            val c = cardsArray.getJSONObject(k)
                            AiAssistantMemoryCard(
                                recordId = c.getString("recordId"),
                                title = c.getString("title"),
                                summary = c.optString("summary", ""),
                                categoryCode = c.optString("categoryCode", ""),
                                categoryName = c.optString("categoryName", ""),
                                createdAt = c.optLong("createdAt", 0L),
                                imageUri = c.optString("imageUri", "").ifBlank { null }
                            )
                        }
                    } else emptyList()
                    AiAssistantMessage(
                        id = msgObj.getString("id"),
                        role = AiAssistantRole.valueOf(msgObj.getString("role")),
                        text = msgObj.getString("text"),
                        imageUri = msgObj.optString("imageUri", "").ifBlank { null },
                        createdAt = msgObj.optLong("createdAt", System.currentTimeMillis()),
                        memoryCards = cards,
                        isError = msgObj.optBoolean("isError", false)
                    )
                }
                AiAssistantSession(
                    sessionId = obj.getString("sessionId"),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    title = obj.optString("title", ""),
                    messages = messages
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val KEY_SESSIONS = "ai_assistant_sessions"
        private const val MAX_SESSIONS = 50
    }

    fun sendMessage(inputText: String, selectedImageUri: Uri?) {
        val text = inputText.trim()
        if (text.isBlank() && selectedImageUri == null) return
        if (_uiState.value.isSending) return

        val userMessage = AiAssistantMessage.user(
            text = text,
            imageUri = selectedImageUri?.toString()
        )
        _uiState.update { state ->
            state.copy(
                messages = state.messages + userMessage,
                isSending = true
            )
        }

        viewModelScope.launch {
            val assistantMessage = withContext(Dispatchers.IO) {
                runCatching {
                    val aiAvailable = settingsStore.isAiAvailable()
                    val keywordRoute = router.route(text, selectedImageUri != null)
                    android.util.Log.d("AiAssistant", "sendMessage: aiAvailable=$aiAvailable, keywordIntent=${keywordRoute.intent}")

                    val route = if (aiAvailable) {
                        val chatHistory = assistantService.buildChatMessages(
                            _uiState.value.messages.dropLast(1)
                        )
                        val aiRoute = assistantService.inferIntentWithHistory(text, chatHistory)
                        android.util.Log.d("AiAssistant", "aiRoute=${aiRoute?.intent}, fallback=${(aiRoute ?: keywordRoute).intent}")
                        aiRoute ?: keywordRoute
                    } else {
                        keywordRoute
                    }

                    if (route.intent == AiAssistantIntent.CHAT && aiAvailable) {
                        val chatHistory = assistantService.buildChatMessages(
                            _uiState.value.messages.dropLast(1)
                        )
                        val reply = assistantService.generateChatReply(
                            text, "", emptyList(), chatHistory, selectedImageUri
                        )
                        return@withContext AiAssistantMessage.assistant(text = reply)
                    }

                    val result = executeHandler(route, text, selectedImageUri)

                    val finalText = if (aiAvailable && result.confirmation == null) {
                        val chatHistory = assistantService.buildChatMessages(
                            _uiState.value.messages.dropLast(1)
                        )
                        assistantService.generateChatReply(
                            text, result.text, result.records, chatHistory, selectedImageUri
                        )
                    } else {
                        result.text
                    }

                    AiAssistantMessage.assistant(
                        text = finalText,
                        memoryCards = result.cards,
                        confirmation = result.confirmation
                    )
                }.getOrElse { error ->
                    AiAssistantMessage.assistant(
                        text = "这次处理失败了：${error.message ?: "未知错误"}",
                        isError = true
                    )
                }
            }
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + assistantMessage,
                    isSending = false
                )
            }
            saveCurrentSession()
        }
    }

    fun confirmOperation(confirmationId: String) {
        val confirmation = pendingConfirmations.remove(confirmationId) ?: return
        markConfirmation(confirmationId, AiAssistantConfirmationStatus.CONFIRMED)
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                when (confirmation.action) {
                    AiAssistantDangerousAction.ARCHIVE -> repository.archiveRecords(confirmation.recordIds)
                    AiAssistantDangerousAction.DELETE -> repository.deleteRecords(confirmation.recordIds)
                }
            }
            val actionText = when (confirmation.action) {
                AiAssistantDangerousAction.ARCHIVE -> "归档"
                AiAssistantDangerousAction.DELETE -> "删除"
            }
            appendAssistantMessage("已${actionText} $count 条记忆。")
        }
    }

    fun cancelOperation(confirmationId: String) {
        val removed = pendingConfirmations.remove(confirmationId) ?: return
        markConfirmation(confirmationId, AiAssistantConfirmationStatus.CANCELLED)
        val actionText = when (removed.action) {
            AiAssistantDangerousAction.ARCHIVE -> "归档"
            AiAssistantDangerousAction.DELETE -> "删除"
        }
        appendAssistantMessage("已取消${actionText}操作，没有修改任何记忆。")
    }

    fun openMemory(recordId: String) {
        if (recordId.isBlank()) return
        viewModelScope.launch {
            _events.emit(AiAssistantUiEvent.OpenMemory(recordId))
        }
    }

    private data class HandlerResult(
        val text: String,
        val records: List<MemoryRecord> = emptyList(),
        val cards: List<AiAssistantMemoryCard> = emptyList(),
        val confirmation: AiAssistantConfirmation? = null
    )

    private suspend fun executeHandler(
        route: AiAssistantRoute,
        originalText: String,
        selectedImageUri: Uri?
    ): HandlerResult {
        return when (route.intent) {
            AiAssistantIntent.SEARCH_MEMORY -> executeSearch(route)
            AiAssistantIntent.SUMMARIZE_MEMORY -> executeSummary(route, originalText, selectedImageUri)
            AiAssistantIntent.CREATE_MEMORY -> executeCreate(originalText, selectedImageUri)
            AiAssistantIntent.ARCHIVE_MEMORY -> executeArchive()
            AiAssistantIntent.DELETE_MEMORY -> executeDelete(route, originalText)
            AiAssistantIntent.SET_REMINDER -> executeReminder(route, originalText)
            AiAssistantIntent.OPEN_MEMORY -> executeOpen(route, originalText)
            AiAssistantIntent.REANALYZE_MEMORY -> executeReanalyze(route, originalText)
            AiAssistantIntent.CHAT -> executeUnknown(originalText, selectedImageUri)
            AiAssistantIntent.UNKNOWN -> executeUnknown(originalText, selectedImageUri)
        }
    }

    private fun executeSearch(route: AiAssistantRoute): HandlerResult {
        val records = when {
            route.categoryCodes == setOf(CategoryCatalog.CODE_LIFE_DELIVERY) -> repository.searchDelivery()
            route.categoryCodes == AiAssistantActionRouter.SHOPPING_CATEGORY_CODES -> repository.searchShopping()
            else -> repository.searchByRoute(route)
        }
        val cards = records.map { it.toAssistantCard() }
        val text = when {
            records.isEmpty() && route.categoryCodes == setOf(CategoryCatalog.CODE_LIFE_DELIVERY) ->
                "没有找到未归档的快递记忆。"
            records.isEmpty() && route.categoryCodes == AiAssistantActionRouter.SHOPPING_CATEGORY_CODES ->
                "没有找到取件、快递、卡券或票券等购物消费相关记忆。"
            records.isEmpty() ->
                "没有找到和「${route.query.ifBlank { "这个关键词" }}」相关的记忆。"
            route.categoryCodes == setOf(CategoryCatalog.CODE_LIFE_DELIVERY) ->
                "找到 ${records.size} 条快递记忆。"
            route.categoryCodes == AiAssistantActionRouter.SHOPPING_CATEGORY_CODES ->
                "找到 ${records.size} 条购物消费相关记忆。"
            else ->
                "找到 ${records.size} 条相关记忆。"
        }
        return HandlerResult(text = text, records = records, cards = cards)
    }

    private fun executeSummary(
        route: AiAssistantRoute,
        originalText: String,
        selectedImageUri: Uri?
    ): HandlerResult {
        val records = repository.selectSummaryRecords(route, originalText)
        val reply = assistantService.summarize(originalText, records, selectedImageUri)
        return HandlerResult(
            text = reply,
            records = records,
            cards = records.take(6).map { it.toAssistantCard() }
        )
    }

    private fun executeCreate(originalText: String, selectedImageUri: Uri?): HandlerResult {
        val record = repository.createMemory(originalText, selectedImageUri)
        val text = if (record.mode == MemoryRecord.MODE_AI) {
            "已新建记忆，AI 分析会在后台继续完成。"
        } else {
            "已新建记忆。"
        }
        return HandlerResult(
            text = text,
            records = listOf(record),
            cards = listOf(record.toAssistantCard())
        )
    }

    private fun executeArchive(): HandlerResult {
        val candidates = repository.findExpiredArchiveCandidates()
        if (candidates.isEmpty()) {
            return HandlerResult(
                text = "我没有找到明显过期的提醒、取件、快递或日程记忆，所以没有执行归档。"
            )
        }
        val cards = candidates.map { it.toAssistantCard() }
        val confirmation = AiAssistantConfirmation(
            action = AiAssistantDangerousAction.ARCHIVE,
            title = "确认归档 ${cards.size} 条可能过期的记忆？",
            description = "这些记忆会移入归档，仍可在归档页找回。确认前我不会修改任何内容。",
            records = cards
        )
        pendingConfirmations[confirmation.confirmationId] = confirmation
        return HandlerResult(
            text = "我找到了 ${cards.size} 条可能已经过期的记忆，需要你确认后再归档。",
            records = candidates,
            cards = cards,
            confirmation = confirmation
        )
    }

    private fun executeDelete(route: AiAssistantRoute, originalText: String): HandlerResult {
        val query = route.query.ifBlank { originalText }
        val candidates = repository.fuzzySearch(query)
        if (candidates.isEmpty()) {
            return HandlerResult(
                text = "删除属于危险操作。我没有找到足够明确的候选记忆，所以没有执行删除。你可以说得更具体一点。"
            )
        }
        val cards = candidates.take(8).map { it.toAssistantCard() }
        val confirmation = AiAssistantConfirmation(
            action = AiAssistantDangerousAction.DELETE,
            title = "确认删除 ${cards.size} 条记忆？",
            description = "删除后不可恢复。确认前我不会修改任何内容。",
            records = cards
        )
        pendingConfirmations[confirmation.confirmationId] = confirmation
        return HandlerResult(
            text = "我找到了这些可能要删除的记忆。删除前必须二次确认。",
            records = candidates,
            cards = cards,
            confirmation = confirmation
        )
    }

    private fun executeReminder(route: AiAssistantRoute, originalText: String): HandlerResult {
        val query = route.query.ifBlank { originalText }
        val cards = repository.fuzzySearch(query).take(6).map { it.toAssistantCard() }
        val text = if (cards.isEmpty()) {
            "提醒设置需要明确的记忆和时间。我还没有找到对应记忆，可以先告诉我要提醒哪一条。"
        } else {
            "我先帮你找到了可能要设置提醒的记忆。提醒时间涉及修改记录，请打开对应记忆后确认设置。"
        }
        return HandlerResult(text = text, cards = cards)
    }

    private suspend fun executeOpen(route: AiAssistantRoute, originalText: String): HandlerResult {
        val query = route.query.ifBlank { originalText }
        val records = repository.fuzzySearch(query)
        if (records.size == 1) {
            _events.emit(AiAssistantUiEvent.OpenMemory(records.first().recordId))
        }
        val text = when (records.size) {
            0 -> "没有找到可以打开的相关记忆。"
            1 -> "已为你打开这条记忆。"
            else -> "找到了 ${records.size} 条相关记忆，点卡片可以打开详情。"
        }
        return HandlerResult(text = text, records = records, cards = records.map { it.toAssistantCard() })
    }

    private fun executeReanalyze(route: AiAssistantRoute, originalText: String): HandlerResult {
        val query = route.query.ifBlank { originalText }
        val cards = repository.fuzzySearch(query).take(6).map { it.toAssistantCard() }
        val text = if (cards.isEmpty()) {
            "没有找到要重新分析的记忆。你可以告诉我更具体的标题、内容或分类。"
        } else {
            "我找到了这些可能需要重新分析的记忆。请打开详情页后使用AI重新分析，这样可以保留现有的详情页进度和取消逻辑。"
        }
        return HandlerResult(text = text, cards = cards)
    }

    private fun executeUnknown(originalText: String, selectedImageUri: Uri?): HandlerResult {
        if (selectedImageUri != null) {
            return executeSummary(
                AiAssistantRoute(intent = AiAssistantIntent.SUMMARIZE_MEMORY),
                originalText,
                selectedImageUri
            )
        }
        val records = repository.fuzzySearch(originalText)
        val aiAvailable = settingsStore.isAiAvailable()
        val text = when {
            records.isEmpty() && !aiAvailable ->
                "AI 助手需要配置 API Key 才能进行智能对话。请在设置中开启 AI 并填入 API Key。"
            records.isEmpty() ->
                "我没有理解这个请求，也没有找到相关记忆。你可以换个说法试试。"
            !aiAvailable ->
                "AI 助手需要配置 API Key 才能进行智能对话。我先按关键词帮你找到了这些记忆。"
            else ->
                "我先按关键词帮你找到了这些记忆。"
        }
        return HandlerResult(
            text = text,
            records = records,
            cards = records.map { it.toAssistantCard() }
        )
    }

    private fun appendAssistantMessage(text: String) {
        _uiState.update { state ->
            state.copy(messages = state.messages + AiAssistantMessage.assistant(text))
        }
        saveCurrentSession()
    }

    private fun markConfirmation(
        confirmationId: String,
        status: AiAssistantConfirmationStatus
    ) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { message ->
                    val confirmation = message.confirmation
                    if (confirmation?.confirmationId == confirmationId) {
                        message.copy(confirmation = confirmation.copy(status = status))
                    } else {
                        message
                    }
                }
            )
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AiAssistantViewModel::class.java)) {
                return AiAssistantViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
