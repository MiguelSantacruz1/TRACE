package com.ejemplo.trace.domain.verification

import com.ejemplo.trace.domain.model.FactDimension
import com.ejemplo.trace.domain.model.SourceMetadata
import com.ejemplo.trace.domain.model.SourcePriority
import com.ejemplo.trace.domain.model.VerificationState
import com.ejemplo.trace.domain.model.VerifiedFact

/**
 * Validador estricto para salidas de IA estructuradas (Prompt Maestro - Sección 7).
 * Regla de Oro:
 * Todo contenido web se trata como datos no confiables (posible prompt injection).
 * El código valida que cada dato extraído contenga obligatoriamente una URL de origen válida
 * y un fragmento textual que realmente exista dentro del texto original de la fuente.
 * Cualquier afirmación sin respaldo textual directo es DESCARTADA.
 */
object AiOutputValidator {

    data class RawAiFact(
        val field: String,
        val value: String,
        val dimensionName: String,
        val sourceUrl: String,
        val evidenceSnippet: String
    )

    fun validateAndFilter(
        rawFacts: List<RawAiFact>,
        originalSourceText: String,
        sourceName: String = "Web / Documentación"
    ): List<VerifiedFact> {
        val validFacts = mutableListOf<VerifiedFact>()
        val normalizedSourceText = originalSourceText.lowercase()

        for (raw in rawFacts) {
            // 1. Descartar si el campo o valor está vacío
            if (raw.field.isBlank() || raw.value.isBlank()) continue

            // 2. Descartar si no tiene URL de respaldo
            if (raw.sourceUrl.isBlank() || !raw.sourceUrl.startsWith("http")) continue

            // 3. Descartar si el fragmento de evidencia está vacío o es muy corto
            if (raw.evidenceSnippet.trim().length < 5) continue

            // 4. VERIFICACIÓN CRÍTICA: El fragmento debe existir en el texto original recuperado
            val normalizedSnippet = raw.evidenceSnippet.trim().lowercase()
            if (!normalizedSourceText.contains(normalizedSnippet)) {
                // Posible alucinación o dato inventado por la IA -> Descartar
                continue
            }

            val dimension = parseDimension(raw.dimensionName)
            val metadata = SourceMetadata(
                sourceName = sourceName,
                originalUrl = raw.sourceUrl,
                priority = SourcePriority.RESEARCH,
                verificationState = VerificationState.CONFIRMED,
                supportingSnippet = raw.evidenceSnippet.trim()
            )

            validFacts.add(
                VerifiedFact(
                    field = raw.field.trim(),
                    value = raw.value.trim(),
                    dimension = dimension,
                    metadata = metadata
                )
            )
        }

        return validFacts
    }

    private fun parseDimension(name: String): FactDimension {
        return when (name.uppercase()) {
            "RAW_MATERIALS", "MATERIAS_PRIMAS" -> FactDimension.RAW_MATERIALS
            "MANUFACTURING", "FABRICACION" -> FactDimension.MANUFACTURING
            "LOGISTICS", "TRANSPORTE" -> FactDimension.LOGISTICS
            "DURABILITY_REPAIR", "REPARABILIDAD" -> FactDimension.DURABILITY_REPAIR
            "END_OF_LIFE", "RECICLAJE" -> FactDimension.END_OF_LIFE
            "ENVIRONMENTAL_IMPACT", "IMPACTO" -> FactDimension.ENVIRONMENTAL_IMPACT
            else -> FactDimension.IDENTIFICATION
        }
    }
}
