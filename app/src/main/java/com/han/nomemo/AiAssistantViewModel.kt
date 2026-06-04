package com.han.nomemo

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
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
    private val pendingConfirmations = mutableMapOf<String, AiAssistantConfirmation>()

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AiAssistantUiEvent>()
    val events: SharedFlow<AiAssistantUiEvent> = _events.asSharedFlow()

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
                    val firstRoute = router.route(text, selectedImageUri != null)
                    val route = if (firstRoute.intent == AiAssistantIntent.UNKNOWN) {
                        assistantService.inferRouteByAi(text) ?: firstRoute
                    } else {
                        firstRoute
                    }
                    handleRoute(route, text, selectedImageUri)
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

    private suspend fun handleRoute(
        route: AiAssistantRoute,
        originalText: String,
        selectedImageUri: Uri?
    ): AiAssistantMessage {
        return when (route.intent) {
            AiAssistantIntent.SEARCH_MEMORY -> buildSearchResponse(route)
            AiAssistantIntent.SUMMARIZE_MEMORY -> buildSummaryResponse(route, originalText, selectedImageUri)
            AiAssistantIntent.CREATE_MEMORY -> buildCreateResponse(originalText, selectedImageUri)
            AiAssistantIntent.ARCHIVE_MEMORY -> buildArchiveConfirmation()
            AiAssistantIntent.DELETE_MEMORY -> buildDeleteConfirmation(route, originalText)
            AiAssistantIntent.SET_REMINDER -> buildReminderResponse(route, originalText)
            AiAssistantIntent.OPEN_MEMORY -> buildOpenResponse(route, originalText)
            AiAssistantIntent.REANALYZE_MEMORY -> buildReanalyzeResponse(route, originalText)
            AiAssistantIntent.UNKNOWN -> buildUnknownResponse(originalText, selectedImageUri)
        }
    }

    private fun buildSearchResponse(route: AiAssistantRoute): AiAssistantMessage {
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
                "没有找到和“${route.query.ifBlank { "这个关键词" }}”相关的记忆。"
            route.categoryCodes == setOf(CategoryCatalog.CODE_LIFE_DELIVERY) ->
                "找到 ${records.size} 条快递记忆。"
            route.categoryCodes == AiAssistantActionRouter.SHOPPING_CATEGORY_CODES ->
                "找到 ${records.size} 条购物消费相关记忆。"
            else ->
                "找到 ${records.size} 条相关记忆。"
        }
        return AiAssistantMessage.assistant(text = text, memoryCards = cards)
    }

    private fun buildSummaryResponse(
        route: AiAssistantRoute,
        originalText: String,
        selectedImageUri: Uri?
    ): AiAssistantMessage {
        val records = repository.selectSummaryRecords(route, originalText)
        val reply = assistantService.summarize(originalText, records, selectedImageUri)
        return AiAssistantMessage.assistant(
            text = reply,
            memoryCards = records.take(6).map { it.toAssistantCard() }
        )
    }

    private fun buildCreateResponse(originalText: String, selectedImageUri: Uri?): AiAssistantMessage {
        val record = repository.createMemory(originalText, selectedImageUri)
        return AiAssistantMessage.assistant(
            text = if (record.mode == MemoryRecord.MODE_AI) {
                "已新建记忆，AI 分析会在后台继续完成。"
            } else {
                "已新建记忆。"
            },
            memoryCards = listOf(record.toAssistantCard())
        )
    }

    private fun buildArchiveConfirmation(): AiAssistantMessage {
        val candidates = repository.findExpiredArchiveCandidates()
        if (candidates.isEmpty()) {
            return AiAssistantMessage.assistant(
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
        return AiAssistantMessage.assistant(
            text = "我找到了 ${cards.size} 条可能已经过期的记忆，需要你确认后再归档。",
            confirmation = confirmation
        )
    }

    private fun buildDeleteConfirmation(route: AiAssistantRoute, originalText: String): AiAssistantMessage {
        val query = route.query.ifBlank { originalText }
        val candidates = repository.fuzzySearch(query)
        if (candidates.isEmpty()) {
            return AiAssistantMessage.assistant(
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
        return AiAssistantMessage.assistant(
            text = "我找到了这些可能要删除的记忆。删除前必须二次确认。",
            confirmation = confirmation
        )
    }

    private fun buildReminderResponse(route: AiAssistantRoute, originalText: String): AiAssistantMessage {
        val query = route.query.ifBlank { originalText }
        val cards = repository.fuzzySearch(query).take(6).map { it.toAssistantCard() }
        return AiAssistantMessage.assistant(
            text = if (cards.isEmpty()) {
                "提醒设置需要明确的记忆和时间。我还没有找到对应记忆，可以先告诉我要提醒哪一条。"
            } else {
                "我先帮你找到了可能要设置提醒的记忆。提醒时间涉及修改记录，请打开对应记忆后确认设置。"
            },
            memoryCards = cards
        )
    }

    private suspend fun buildOpenResponse(route: AiAssistantRoute, originalText: String): AiAssistantMessage {
        val query = route.query.ifBlank { originalText }
        val records = repository.fuzzySearch(query)
        if (records.size == 1) {
            _events.emit(AiAssistantUiEvent.OpenMemory(records.first().recordId))
        }
        return AiAssistantMessage.assistant(
            text = when (records.size) {
                0 -> "没有找到可以打开的相关记忆。"
                1 -> "已为你打开这条记忆。"
                else -> "找到了 ${records.size} 条相关记忆，点卡片可以打开详情。"
            },
            memoryCards = records.map { it.toAssistantCard() }
        )
    }

    private fun buildReanalyzeResponse(route: AiAssistantRoute, originalText: String): AiAssistantMessage {
        val query = route.query.ifBlank { originalText }
        val cards = repository.fuzzySearch(query).take(6).map { it.toAssistantCard() }
        return AiAssistantMessage.assistant(
            text = if (cards.isEmpty()) {
                "没有找到要重新分析的记忆。你可以告诉我更具体的标题、内容或分类。"
            } else {
                "我找到了这些可能需要重新分析的记忆。请打开详情页后使用“AI 重新分析”，这样可以保留现有的详情页进度和取消逻辑。"
            },
            memoryCards = cards
        )
    }

    private fun buildUnknownResponse(originalText: String, selectedImageUri: Uri?): AiAssistantMessage {
        if (selectedImageUri != null) {
            return buildSummaryResponse(
                AiAssistantRoute(intent = AiAssistantIntent.SUMMARIZE_MEMORY),
                originalText,
                selectedImageUri
            )
        }
        val records = repository.fuzzySearch(originalText)
        return if (records.isEmpty()) {
            AiAssistantMessage.assistant(
                text = "我还没理解这个请求，也没有找到相关记忆。你可以试试“查看我的快递”“帮我找购物记录”或“总结最近一周的记忆”。"
            )
        } else {
            AiAssistantMessage.assistant(
                text = "我先按关键词帮你找到了这些记忆。",
                memoryCards = records.map { it.toAssistantCard() }
            )
        }
    }

    private fun appendAssistantMessage(text: String) {
        _uiState.update { state ->
            state.copy(messages = state.messages + AiAssistantMessage.assistant(text))
        }
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
