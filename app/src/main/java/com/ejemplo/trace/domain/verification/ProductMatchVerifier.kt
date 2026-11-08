package com.ejemplo.trace.domain.verification

import com.ejemplo.trace.domain.model.VerificationState

/**
 * Resultado del proceso de verificación de coincidencia entre una página/fuente y el producto investigado.
 */
data class MatchVerificationResult(
    val state: VerificationState,
    val matchedCriteria: List<String>,
    val confidenceScore: Float // 0.0 a 1.0
) {
    val isEligibleForScore: Boolean
        get() = state == VerificationState.CONFIRMED || state == VerificationState.DECLARED
}

/**
 * Verificador estricto de coincidencia de producto (Prompt Maestro - Sección 7).
 * Regla: Una página o fuente solo se acepta si cumple al menos un criterio fuerte:
 * 1. El código de barras exacto aparece en la página o en un campo estructurado.
 * O varios criterios combinados fuertes:
 * 2. Marca coincide + Nombre coincide + Presentación coincide.
 * Si hay duda, se clasifica como UNVERIFIED_MATCH y NO entra al cálculo del TRACE SCORE.
 */
object ProductMatchVerifier {

    fun verify(
        targetBarcode: String,
        targetBrand: String?,
        targetName: String?,
        pageContent: String,
        pageUrl: String = ""
    ): MatchVerificationResult {
        val normalizedContent = pageContent.lowercase()
        val normalizedUrl = pageUrl.lowercase()
        val normalizedBarcode = targetBarcode.trim()

        val matchedCriteria = mutableListOf<String>()

        // 1. Criterio Fuerte: Coincidencia exacta de código de barras
        if (normalizedBarcode.isNotBlank() && (normalizedContent.contains(normalizedBarcode) || normalizedUrl.contains(normalizedBarcode))) {
            matchedCriteria.add("Código de barras exacto encontrado ($normalizedBarcode)")
            return MatchVerificationResult(
                state = VerificationState.CONFIRMED,
                matchedCriteria = matchedCriteria,
                confidenceScore = 1.0f
            )
        }

        // 2. Criterios Combinados: Marca + Nombre
        var combinedPoints = 0

        if (!targetBrand.isNullOrBlank()) {
            val normalizedBrand = targetBrand.trim().lowercase()
            if (normalizedContent.contains(normalizedBrand)) {
                matchedCriteria.add("Marca coincidente ($targetBrand)")
                combinedPoints += 40
            }
        }

        if (!targetName.isNullOrBlank()) {
            val normalizedName = targetName.trim().lowercase()
            val words = normalizedName.split(" ").filter { it.length > 3 }
            val matchedWords = words.count { normalizedContent.contains(it) }

            if (words.isNotEmpty()) {
                val ratio = matchedWords.toFloat() / words.size
                if (ratio >= 0.7f) {
                    matchedCriteria.add("Nombre de producto altamente coincidente")
                    combinedPoints += 50
                } else if (ratio >= 0.4f) {
                    matchedCriteria.add("Nombre de producto parcialmente coincidente")
                    combinedPoints += 25
                }
            }
        }

        return when {
            combinedPoints >= 80 -> MatchVerificationResult(
                state = VerificationState.DECLARED,
                matchedCriteria = matchedCriteria,
                confidenceScore = 0.85f
            )
            combinedPoints >= 50 -> MatchVerificationResult(
                state = VerificationState.ESTIMATED,
                matchedCriteria = matchedCriteria,
                confidenceScore = 0.60f
            )
            else -> MatchVerificationResult(
                state = VerificationState.UNVERIFIED_MATCH,
                matchedCriteria = matchedCriteria,
                confidenceScore = 0.20f
            )
        }
    }
}
