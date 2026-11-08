package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.ai.AiProvider
import com.ejemplo.trace.domain.ai.AiQueryContext
import com.ejemplo.trace.domain.ai.AiResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para AskTraceAiUseCase.
 */
class AskTraceAiUseCaseTest {

    private val aiProvider: AiProvider = mockk()
    private val useCase = AskTraceAiUseCase(aiProvider)

    @Test
    fun invoke_delegatesQueryToAiProviderAndReturnsResponse() = runTest {
        val queryContext = AiQueryContext(
            barcode = "3017620422003",
            question = "¿De dónde provienen las avellanas de Nutella?",
            verifiedFactsSummary = "Nutella contiene avellanas (13%) y aceite de palma sostenible.",
            calculatedScore = 48,
            confidenceLabel = "CONFIRMADO_EN_ETIQUETA"
        )
        val expectedResponse = AiResponse(
            answer = "Las avellanas provienen principalmente de Turquía e Italia según datos del fabricante.",
            citedSources = listOf("Open Food Facts"),
            suggestions = listOf("¿Qué porcentaje de cacao contiene?", "¿El aceite de palma cuenta con certificación RSPO?")
        )

        coEvery { aiProvider.askAi(queryContext) } returns Result.success(expectedResponse)

        val result = useCase(queryContext)

        assertTrue(result.isSuccess)
        assertEquals(expectedResponse.answer, result.getOrNull()?.answer)
        assertEquals(1, result.getOrNull()?.citedSources?.size)
        coVerify(exactly = 1) { aiProvider.askAi(queryContext) }
    }

    @Test
    fun invoke_whenProviderFails_returnsFailure() = runTest {
        val queryContext = AiQueryContext(
            barcode = "5449000000996",
            question = "¿Es reciclable la botella?",
            verifiedFactsSummary = "Botella de plástico PET 100% reciclable."
        )

        coEvery { aiProvider.askAi(queryContext) } returns Result.failure(RuntimeException("Error de conexión al gateway de IA"))

        val result = useCase(queryContext)

        assertTrue(result.isFailure)
        assertEquals("Error de conexión al gateway de IA", result.exceptionOrNull()?.message)
        coVerify(exactly = 1) { aiProvider.askAi(queryContext) }
    }
}
