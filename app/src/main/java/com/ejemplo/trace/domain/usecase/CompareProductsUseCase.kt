package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.ComparisonAdvantage
import com.ejemplo.trace.domain.model.FactorComparisonItem
import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.model.ProductComparison
import javax.inject.Inject

/**
 * Caso de uso para realizar la comparación lado a lado de dos productos investigados.
 * Aplica estrictamente la regla de ética y responsabilidad (Prompt Maestro - Sección 10):
 * Si alguno de los productos no tiene datos suficientes o carece de score validado,
 * no emite un veredicto definitivo arbitrario.
 */
class CompareProductsUseCase @Inject constructor() {

    operator fun invoke(
        productA: HistoryProduct,
        productB: HistoryProduct
    ): ProductComparison {
        val scoreA = productA.lastScore
        val scoreB = productB.lastScore

        val isComparable = scoreA != null && scoreB != null

        val factorComparisons = buildFactorComparisons(productA, productB)

        val verdict = if (!isComparable) {
            "No es posible emitir un veredicto de sostenibilidad definitivo debido a la falta de información verificada o score calculado en uno de los productos investigados."
        } else {
            generateAnalyticalVerdict(productA, productB, scoreA, scoreB)
        }

        return ProductComparison(
            productA = productA,
            productB = productB,
            factorComparisons = factorComparisons,
            analyticalVerdict = verdict,
            isComparable = isComparable
        )
    }

    private fun buildFactorComparisons(
        productA: HistoryProduct,
        productB: HistoryProduct
    ): List<FactorComparisonItem> {
        val scoreA = productA.lastScore
        val scoreB = productB.lastScore

        return listOf(
            FactorComparisonItem(
                factorName = "Puntuación Global TRACE SCORE",
                scoreA = scoreA,
                scoreB = scoreB,
                detailsA = scoreA?.let { "$it/100" } ?: "Datos insuficientes",
                detailsB = scoreB?.let { "$it/100" } ?: "Datos insuficientes",
                advantage = when {
                    scoreA == null || scoreB == null -> ComparisonAdvantage.INSUFFICIENT_DATA
                    scoreA > scoreB -> ComparisonAdvantage.PRODUCT_A
                    scoreB > scoreA -> ComparisonAdvantage.PRODUCT_B
                    else -> ComparisonAdvantage.TIED
                }
            ),
            FactorComparisonItem(
                factorName = "Transparencia y Certificación",
                scoreA = if (productA.confidenceLevel.isNotBlank()) 1 else null,
                scoreB = if (productB.confidenceLevel.isNotBlank()) 1 else null,
                detailsA = productA.confidenceLevel.ifBlank { "Sin nivel de confianza auditado" },
                detailsB = productB.confidenceLevel.ifBlank { "Sin nivel de confianza auditado" },
                advantage = when {
                    productA.confidenceLevel.isBlank() && productB.confidenceLevel.isBlank() -> ComparisonAdvantage.INSUFFICIENT_DATA
                    productA.confidenceLevel.isNotBlank() && productB.confidenceLevel.isBlank() -> ComparisonAdvantage.PRODUCT_A
                    productA.confidenceLevel.isBlank() && productB.confidenceLevel.isNotBlank() -> ComparisonAdvantage.PRODUCT_B
                    else -> ComparisonAdvantage.TIED
                }
            )
        )
    }

    private fun generateAnalyticalVerdict(
        productA: HistoryProduct,
        productB: HistoryProduct,
        scoreA: Int,
        scoreB: Int
    ): String {
        val diff = Math.abs(scoreA - scoreB)
        return when {
            diff <= 3 -> {
                "Ambos productos presentan un desempeño de sostenibilidad muy similar (diferencia menor a 3 puntos). Se recomienda revisar detalles específicos de empaque y materias primas."
            }
            scoreA > scoreB -> {
                "${productA.name} presenta un desempeño de sostenibilidad superior (+${scoreA - scoreB} puntos en TRACE SCORE) frente a ${productB.name}, con mejor evidencia verificada en su trazabilidad."
            }
            else -> {
                "${productB.name} presenta un desempeño de sostenibilidad superior (+${scoreB - scoreA} puntos en TRACE SCORE) frente a ${productA.name}, con mejor evidencia verificada en su trazabilidad."
            }
        }
    }
}
