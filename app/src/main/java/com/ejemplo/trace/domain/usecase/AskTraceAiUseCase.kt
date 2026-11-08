package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.ai.AiProvider
import com.ejemplo.trace.domain.ai.AiQueryContext
import com.ejemplo.trace.domain.ai.AiResponse
import javax.inject.Inject

/**
 * Caso de uso atómico para interactuar con TRACE AI.
 */
class AskTraceAiUseCase @Inject constructor(
    private val aiProvider: AiProvider
) {
    suspend operator fun invoke(queryContext: AiQueryContext): Result<AiResponse> {
        return aiProvider.askAi(queryContext)
    }
}
