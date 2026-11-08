package com.ejemplo.trace.domain.model

/**
 * Ventaja comparativa de un factor específico de sostenibilidad.
 */
enum class ComparisonAdvantage {
    PRODUCT_A,
    PRODUCT_B,
    TIED,
    INSUFFICIENT_DATA
}

/**
 * Detalle comparativo por factor individual.
 */
data class FactorComparisonItem(
    val factorName: String,
    val scoreA: Int?,
    val scoreB: Int?,
    val detailsA: String,
    val detailsB: String,
    val advantage: ComparisonAdvantage
)

/**
 * Resultado completo del análisis comparativo lado a lado (Prompt Maestro - Sección 10).
 */
data class ProductComparison(
    val productA: HistoryProduct,
    val productB: HistoryProduct,
    val factorComparisons: List<FactorComparisonItem>,
    val analyticalVerdict: String,
    val isComparable: Boolean
)
