package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.ComparisonAdvantage
import com.ejemplo.trace.domain.model.HistoryProduct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para CompareProductsUseCase.
 * Valida la regla ética y metodológica de comparación responsable (Prompt Maestro - Sección 10).
 */
class CompareProductsUseCaseTest {

    private val useCase = CompareProductsUseCase()

    @Test
    fun invoke_whenBothProductsHaveScores_isComparableAndEmitsObjectiveVerdict() {
        val prodA = HistoryProduct(
            barcode = "3017620422003",
            name = "Nutella",
            brand = "Ferrero",
            lastScore = 65,
            confidenceLevel = "ALTA"
        )
        val prodB = HistoryProduct(
            barcode = "5449000000996",
            name = "Crema Eco",
            brand = "BioMarca",
            lastScore = 80,
            confidenceLevel = "AUDITADO"
        )

        val result = useCase(prodA, prodB)

        assertTrue(result.isComparable)
        assertTrue(result.analyticalVerdict.contains("Crema Eco presenta un desempeño de sostenibilidad superior"))
        assertTrue(result.analyticalVerdict.contains("+15 puntos"))

        val globalFactor = result.factorComparisons.first { it.factorName.contains("Puntuación Global") }
        assertEquals(ComparisonAdvantage.PRODUCT_B, globalFactor.advantage)
    }

    @Test
    fun invoke_whenScoresAreSimilar_verdictHighlightsClosePerformance() {
        val prodA = HistoryProduct(
            barcode = "111111111111",
            name = "Producto 1",
            lastScore = 70
        )
        val prodB = HistoryProduct(
            barcode = "222222222222",
            name = "Producto 2",
            lastScore = 72
        )

        val result = useCase(prodA, prodB)

        assertTrue(result.isComparable)
        assertTrue(result.analyticalVerdict.contains("desempeño de sostenibilidad muy similar"))
    }

    @Test
    fun invoke_whenOneProductLacksScore_declaresInsufficientDataWithoutArbitraryWinner() {
        val prodA = HistoryProduct(
            barcode = "3017620422003",
            name = "Nutella",
            lastScore = 65
        )
        val prodB = HistoryProduct(
            barcode = "999999999999",
            name = "Producto Sin Datos",
            lastScore = null // Sin score validado
        )

        val result = useCase(prodA, prodB)

        assertFalse(result.isComparable)
        assertTrue(result.analyticalVerdict.contains("No es posible emitir un veredicto"))
        assertTrue(result.analyticalVerdict.contains("falta de información verificada"))

        val globalFactor = result.factorComparisons.first { it.factorName.contains("Puntuación Global") }
        assertEquals(ComparisonAdvantage.INSUFFICIENT_DATA, globalFactor.advantage)
    }

    @Test
    fun invoke_whenBothProductsLackScores_declaresInsufficientData() {
        val prodA = HistoryProduct(
            barcode = "111111111111",
            name = "Producto A",
            lastScore = null
        )
        val prodB = HistoryProduct(
            barcode = "222222222222",
            name = "Producto B",
            lastScore = null
        )

        val result = useCase(prodA, prodB)

        assertFalse(result.isComparable)
        assertEquals(ComparisonAdvantage.INSUFFICIENT_DATA, result.factorComparisons[0].advantage)
    }
}
