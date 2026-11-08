package com.ejemplo.trace.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.trace.domain.ai.AiQueryContext
import com.ejemplo.trace.domain.usecase.AskTraceAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val askTraceAiUseCase: AskTraceAiUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val barcode: String = savedStateHandle.get<String>("barcode") ?: "3017620422003"

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatMessage(
                    id = "init_msg",
                    sender = MessageSender.AI,
                    text = "Hola, soy TRACE AI. He analizado la información disponible para este producto. ¿Qué dudas tienes sobre su origen, materiales o sostenibilidad?",
                    citedSources = listOf("Open Food Facts", "Reporte corporativo")
                )
            )
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(questionText: String) {
        val trimmed = questionText.trim()
        if (trimmed.isBlank() || _uiState.value.isProcessing) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = MessageSender.USER,
            text = trimmed
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isProcessing = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val queryContext = AiQueryContext(
                barcode = barcode,
                question = trimmed,
                verifiedFactsSummary = "Envase: Vidrio 100% reciclable. Tapa: Polipropileno PP 05. Origen: Europa. Cacao con certificación auditada.",
                calculatedScore = 65,
                confidenceLabel = "Media"
            )

            val result = askTraceAiUseCase(queryContext)
            result.fold(
                onSuccess = { response ->
                    val aiMessage = ChatMessage(
                        id = (System.currentTimeMillis() + 1).toString(),
                        sender = MessageSender.AI,
                        text = response.answer,
                        citedSources = response.citedSources
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isProcessing = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        errorMessage = error.message ?: "Error al conectar con TRACE AI"
                    )
                }
            )
        }
    }
}
