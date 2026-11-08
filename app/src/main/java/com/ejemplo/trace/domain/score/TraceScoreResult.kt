package com.ejemplo.trace.domain.score

import kotlinx.serialization.Serializable

/**
 * Resultado completo del cálculo determinista de TRACE SCORE.
 */
@Serializable
data class TraceScoreResult(
    val globalScore: Int?, // 0-100 o null si los datos son insuficientes (<50% cobertura)
    val confidence: ScoreConfidence,
    val coveredWeight: Int, // Peso total cubierto por factores con datos (0 a 100)
    val factorEvaluations: List<FactorEvaluation>,
    val missingFactors: List<ScoreFactor>,
    val legalDisclaimer: String = "Aviso legal: TRACE SCORE es una evaluación analítica propia de sostenibilidad basada en datos públicos y no constituye una certificación ambiental oficial."
) {
    val isCalculable: Boolean
        get() = globalScore != null && coveredWeight >= 50
}
