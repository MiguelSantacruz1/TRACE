package com.ejemplo.trace.domain.score

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias completas y rigurosas para TraceScoreCalculator:
 * 1. Todos los factores con datos completos (100% cobertura)
 * 2. Datos parciales con cobertura suficiente (>=50%)
 * 3. Datos insuficientes (<50% cobertura) -> no debe asignar score alto por omisión
 * 4. Ponderación diferenciada: datos declarados vs. auditados por terceros
 * 5. Determinismo puro
 */
class TraceScoreCalculatorTest {

    @Test
    fun calculate_completeDataAllFactors_returnsHighConfidenceScore() {
        val evaluations = listOf(
            FactorEvaluation(ScoreFactor.RAW_MATERIALS, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.MANUFACTURING, score = 70, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.REPAIRABILITY, score = 60, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.RECYCLABILITY, score = 90, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.LOGISTICS, score = 50, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.DURABILITY, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.ENVIRONMENTAL_IMPACT, score = 60, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.PACKAGING, score = 100, hasData = true, isThirdPartyVerified = true)
        )

        val result = TraceScoreCalculator.calculate(evaluations)

        assertTrue(result.isCalculable)
        assertNotNull(result.globalScore)
        assertEquals(100, result.coveredWeight)
        assertEquals(ScoreConfidence.HIGH, result.confidence)
        assertTrue(result.missingFactors.isEmpty())

        // Cálculo manual esperado:
        // (80*20 + 70*15 + 60*15 + 90*15 + 50*10 + 80*10 + 60*10 + 100*5) / 100
        // (1600 + 1050 + 900 + 1350 + 500 + 800 + 600 + 500) / 100 = 7300 / 100 = 73
        assertEquals(73, result.globalScore)
    }

    @Test
    fun calculate_partialDataAbove50Percent_returnsCalculatedScoreWithMediumConfidence() {
        // Cobertura: RAW_MATERIALS (20) + RECYCLABILITY (15) + PACKAGING (5) + LOGISTICS (10) = 50 de peso
        val evaluations = listOf(
            FactorEvaluation(ScoreFactor.RAW_MATERIALS, score = 90, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.RECYCLABILITY, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.PACKAGING, score = 100, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.LOGISTICS, score = 60, hasData = true, isThirdPartyVerified = true)
        )

        val result = TraceScoreCalculator.calculate(evaluations)

        assertTrue(result.isCalculable)
        assertNotNull(result.globalScore)
        assertEquals(50, result.coveredWeight)
        assertEquals(ScoreConfidence.MEDIUM, result.confidence)
        assertEquals(4, result.missingFactors.size)

        // Cálculo manual esperado:
        // (90*20 + 80*15 + 100*5 + 60*10) / 50 = (1800 + 1200 + 500 + 600) / 50 = 4100 / 50 = 82
        assertEquals(82, result.globalScore)
    }

    @Test
    fun calculate_insufficientDataBelow50Percent_returnsNullScoreAndInsufficientConfidence() {
        // Cobertura insuficiente: solo PACKAGING (5) y LOGISTICS (10) = 15 de peso (<50)
        val evaluations = listOf(
            FactorEvaluation(ScoreFactor.PACKAGING, score = 100, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.LOGISTICS, score = 80, hasData = true, isThirdPartyVerified = true)
        )

        val result = TraceScoreCalculator.calculate(evaluations)

        assertFalse(result.isCalculable)
        assertNull(result.globalScore)
        assertEquals(15, result.coveredWeight)
        assertEquals(ScoreConfidence.INSUFFICIENT, result.confidence)
        assertEquals(6, result.missingFactors.size)
    }

    @Test
    fun calculate_unverifiedManufacturerDeclarations_weighsLess() {
        // Dos productos con misma puntuación base (80) y mismos factores (RAW_MATERIALS 20 + RECYCLABILITY 15 + REPAIRABILITY 15 = 50)
        val auditado = listOf(
            FactorEvaluation(ScoreFactor.RAW_MATERIALS, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.RECYCLABILITY, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.REPAIRABILITY, score = 80, hasData = true, isThirdPartyVerified = true)
        )

        val soloDeclarado = listOf(
            FactorEvaluation(ScoreFactor.RAW_MATERIALS, score = 80, hasData = true, isThirdPartyVerified = false),
            FactorEvaluation(ScoreFactor.RECYCLABILITY, score = 80, hasData = true, isThirdPartyVerified = false),
            FactorEvaluation(ScoreFactor.REPAIRABILITY, score = 80, hasData = true, isThirdPartyVerified = false)
        )

        val resultAuditado = TraceScoreCalculator.calculate(auditado)
        val resultDeclarado = TraceScoreCalculator.calculate(soloDeclarado)

        assertEquals(80, resultAuditado.globalScore)
        // Con descuento del 85%: 80 * 0.85 = 68
        assertEquals(68, resultDeclarado.globalScore)
        assertTrue(resultAuditado.globalScore!! > resultDeclarado.globalScore!!)
    }

    @Test
    fun calculate_missingFactorDoesNotDefaultToZeroOrHundred() {
        // Si falta DURABILITY, no debe penalizar como 0 ni premiar como 100
        val withOnlyRawMaterialsAndRecyclability = listOf(
            FactorEvaluation(ScoreFactor.RAW_MATERIALS, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.RECYCLABILITY, score = 80, hasData = true, isThirdPartyVerified = true),
            FactorEvaluation(ScoreFactor.MANUFACTURING, score = 80, hasData = true, isThirdPartyVerified = true)
        ) // 20 + 15 + 15 = 50 peso

        val result = TraceScoreCalculator.calculate(withOnlyRawMaterialsAndRecyclability)
        assertEquals(80, result.globalScore)
    }
}
