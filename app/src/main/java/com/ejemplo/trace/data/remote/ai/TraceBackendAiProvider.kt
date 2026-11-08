package com.ejemplo.trace.data.remote.ai

import com.ejemplo.trace.data.remote.api.BackendChatRequest
import com.ejemplo.trace.data.remote.api.TraceBackendApi
import com.ejemplo.trace.di.IoDispatcher
import com.ejemplo.trace.domain.ai.AiProvider
import com.ejemplo.trace.domain.ai.AiQueryContext
import com.ejemplo.trace.domain.ai.AiResponse
import com.ejemplo.trace.domain.ai.PromptDefenseSanitizer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de AiProvider que delega las consultas hacia el backend seguro de TRACE.
 * Aplica aislamiento de contexto mediante PromptDefenseSanitizer antes del envío.
 */
@Singleton
class TraceBackendAiProvider @Inject constructor(
    private val backendApi: TraceBackendApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AiProvider {

    override val providerName: String = "TRACE Secure AI Gateway (Gemini/OpenAI)"

    override suspend fun askAi(queryContext: AiQueryContext): Result<AiResponse> = withContext(ioDispatcher) {
        try {
            val sanitizedQuestion = PromptDefenseSanitizer.sanitizeUserInput(queryContext.question)
            if (sanitizedQuestion.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("La pregunta no puede estar vacía"))
            }

            // Detección de intento de inyección de instrucciones en la pregunta
            if (PromptDefenseSanitizer.containsAdversarialAttempt(sanitizedQuestion)) {
                return@withContext Result.success(
                    AiResponse(
                        answer = "Como asistente de sostenibilidad de TRACE, respondo únicamente preguntas relacionadas con el origen, los materiales, la fabricación y el impacto ambiental del producto investigado.",
                        citedSources = emptyList(),
                        suggestions = listOf(
                            "¿De qué materiales está hecho?",
                            "¿Es reciclable el envase?",
                            "¿Qué certificaciones tiene?"
                        )
                    )
                )
            }

            val protectedContext = PromptDefenseSanitizer.wrapUntrustedContext(queryContext.verifiedFactsSummary)

            val request = BackendChatRequest(
                barcode = queryContext.barcode,
                question = sanitizedQuestion,
                verifiedContext = protectedContext,
                calculatedScore = queryContext.calculatedScore
            )

            val response = backendApi.chatWithAi(request)

            Result.success(
                AiResponse(
                    answer = response.answer,
                    citedSources = response.citedSources,
                    suggestions = listOf(
                        "¿Cómo se recicla?",
                        "¿Existen alternativas más sostenibles?",
                        "¿Por qué obtuvo este TRACE SCORE?"
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
