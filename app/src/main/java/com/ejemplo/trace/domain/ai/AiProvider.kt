package com.ejemplo.trace.domain.ai

import kotlinx.serialization.Serializable

/**
 * Consulta contextual enviada a un proveedor de IA.
 */
@Serializable
data class AiQueryContext(
    val barcode: String,
    val question: String,
    val verifiedFactsSummary: String,
    val calculatedScore: Int? = null,
    val confidenceLabel: String = ""
)

/**
 * Respuesta estructurada de TRACE AI respaldada por citas.
 */
@Serializable
data class AiResponse(
    val answer: String,
    val citedSources: List<String> = emptyList(),
    val suggestions: List<String> = emptyList()
)

/**
 * Interfaz fundamental para proveedores de Inteligencia Artificial (Prompt Maestro - Sección 9).
 * Desacopla la aplicación de una API específica (Gemini, OpenAI, etc.).
 * La comunicación se canaliza exclusivamente a través del backend seguro.
 */
interface AiProvider {

    val providerName: String

    /**
     * Responde una pregunta sobre el producto apoyándose únicamente en el contexto verificado.
     * Si no se dispone de información para responder, debe indicarlo con transparencia.
     */
    suspend fun askAi(queryContext: AiQueryContext): Result<AiResponse>
}
