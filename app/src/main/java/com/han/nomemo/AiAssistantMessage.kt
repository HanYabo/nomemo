package com.han.nomemo

import java.util.UUID

enum class AiAssistantRole {
    USER,
    ASSISTANT
}

enum class AiAssistantIntent {
    SEARCH_MEMORY,
    SUMMARIZE_MEMORY,
    CREATE_MEMORY,
    ARCHIVE_MEMORY,
    DELETE_MEMORY,
    SET_REMINDER,
    OPEN_MEMORY,
    REANALYZE_MEMORY,
    UNKNOWN
}

enum class AiAssistantDangerousAction {
    ARCHIVE,
    DELETE
}

enum class AiAssistantConfirmationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

data class AiAssistantMemoryCard(
    val recordId: String,
    val title: String,
    val summary: String,
    val categoryCode: String,
    val categoryName: String,
    val createdAt: Long,
    val imageUri: String? = null
)

data class AiAssistantConfirmation(
    val confirmationId: String = UUID.randomUUID().toString(),
    val action: AiAssistantDangerousAction,
    val title: String,
    val description: String,
    val records: List<AiAssistantMemoryCard>,
    val status: AiAssistantConfirmationStatus = AiAssistantConfirmationStatus.PENDING
) {
    val recordIds: List<String> get() = records.map { it.recordId }
}

data class AiAssistantMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: AiAssistantRole,
    val text: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val memoryCards: List<AiAssistantMemoryCard> = emptyList(),
    val confirmation: AiAssistantConfirmation? = null,
    val isError: Boolean = false
) {
    companion object {
        fun user(text: String, imageUri: String?): AiAssistantMessage {
            return AiAssistantMessage(
                role = AiAssistantRole.USER,
                text = text,
                imageUri = imageUri
            )
        }

        fun assistant(
            text: String,
            memoryCards: List<AiAssistantMemoryCard> = emptyList(),
            confirmation: AiAssistantConfirmation? = null,
            isError: Boolean = false
        ): AiAssistantMessage {
            return AiAssistantMessage(
                role = AiAssistantRole.ASSISTANT,
                text = text,
                memoryCards = memoryCards,
                confirmation = confirmation,
                isError = isError
            )
        }
    }
}

data class AiAssistantUiState(
    val messages: List<AiAssistantMessage> = emptyList(),
    val isSending: Boolean = false
)

sealed class AiAssistantUiEvent {
    data class OpenMemory(val recordId: String) : AiAssistantUiEvent()
}
