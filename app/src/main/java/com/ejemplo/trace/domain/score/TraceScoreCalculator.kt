package com.ejemplo.trace.domain.score

import kotlin.math.roundToInt

/**
 * Motor de cálculo de TRACE SCORE en Kotlin puro y determinista.
 *
 * Reglas fundamentales (Prompt Maestro - Sección 8):
 * 1. Lo calcula exclusivamente el código, no la IA.
 * 2. Pesos oficiales fijos: Materias primas (20), Fabricación (15), Reparabilidad (15),
 *    Reciclabilidad (15), Transporte (10), Uso y durabilidad (10), Impacto ambiental (10), Empaque (5). Suma = 100.
 * 3. Un factor sin datos no cuenta como 0 ni como 100.
 * 4. El score es el promedio ponderado de los factores con datos reescalado a 100.
 * 5. Solo se calcula si los factores con datos suman al menos el 50% del peso total.
 *    De lo contrario, globalScore = null y confianza = INSUFFICIENT.
 * 6. Datos no auditados (solo declarados por fabricante) reciben factor de descuento (85%).
 */
object TraceScoreCalculator {

    const val MIN_COVERAGE_WEIGHT_REQUIRED = 50
    const val HIGH_CONFIDENCE_THRESHOLD = 75
    const val UNVERIFIED_DECLARATION_DISCOUNT = 0.85f

    fun calculate(evaluations: List<FactorEvaluation>): TraceScoreResult {
        var coveredWeight = 0
        var weightedScoreSum = 0.0
        val missingFactors = mutableListOf<ScoreFactor>()

        // Evaluar cada factor disponible
        for (factor in ScoreFactor.entries) {
            val eval = evaluations.find { it.factor == factor }
            if (eval != null && eval.hasData && eval.score != null) {
                val baseScore = eval.score.coerceIn(0, 100)
                // Si el dato está auditado/confirmado por terceros pesa completo, si solo es declarado por fabricante se descuenta
                val adjustedScore: Double = if (eval.isThirdPartyVerified) {
                    baseScore.toDouble()
                } else {
                    (baseScore * UNVERIFIED_DECLARATION_DISCOUNT).toDouble()
                }

                coveredWeight += factor.defaultWeight
                weightedScoreSum += (adjustedScore * factor.defaultWeight.toDouble())
            } else {
                missingFactors.add(factor)
            }
        }

        // Si la cobertura es menor al 50%, no se emite score
        if (coveredWeight < MIN_COVERAGE_WEIGHT_REQUIRED) {
            return TraceScoreResult(
                globalScore = null,
                confidence = ScoreConfidence.INSUFFICIENT,
                coveredWeight = coveredWeight,
                factorEvaluations = evaluations,
                missingFactors = missingFactors
            )
        }

        // Promedio ponderado reescalado
        val finalScore = (weightedScoreSum / coveredWeight).roundToInt().coerceIn(0, 100)

        val confidence = if (coveredWeight >= HIGH_CONFIDENCE_THRESHOLD) {
            ScoreConfidence.HIGH
        } else {
            ScoreConfidence.MEDIUM
        }

        return TraceScoreResult(
            globalScore = finalScore,
            confidence = confidence,
            coveredWeight = coveredWeight,
            factorEvaluations = evaluations,
            missingFactors = missingFactors
        )
    }
}
