package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.FactDimension
import com.ejemplo.trace.domain.model.VerificationState
import com.ejemplo.trace.domain.model.VerifiedFact
import com.ejemplo.trace.domain.score.FactorEvaluation
import com.ejemplo.trace.domain.score.ScoreFactor
import com.ejemplo.trace.domain.score.TraceScoreCalculator
import com.ejemplo.trace.domain.score.TraceScoreResult
import javax.inject.Inject

/**
 * Caso de uso para calcular el TRACE SCORE a partir de los hechos verificados de un producto.
 */
class CalculateTraceScoreUseCase @Inject constructor() {

    operator fun invoke(facts: List<VerifiedFact>): TraceScoreResult {
        val evaluations = mutableListOf<FactorEvaluation>()

        // 1. Materias Primas (Peso 20)
        val rawMaterialFacts = facts.filter { it.dimension == FactDimension.RAW_MATERIALS }
        if (rawMaterialFacts.isNotEmpty()) {
            val isVerified = rawMaterialFacts.any { it.metadata.verificationState == VerificationState.CONFIRMED }
            evaluations.add(
                FactorEvaluation(
                    factor = ScoreFactor.RAW_MATERIALS,
                    score = 75,
                    hasData = true,
                    isThirdPartyVerified = isVerified,
                    explanation = "Datos de materias primas y certificaciones auditadas"
                )
            )
        }

        // 2. Fabricación (Peso 15)
        val manufacturingFacts = facts.filter { it.dimension == FactDimension.MANUFACTURING }
        if (manufacturingFacts.isNotEmpty()) {
            val isVerified = manufacturingFacts.any { it.metadata.verificationState == VerificationState.CONFIRMED }
            evaluations.add(
                FactorEvaluation(
                    factor = ScoreFactor.MANUFACTURING,
                    score = 70,
                    hasData = true,
                    isThirdPartyVerified = isVerified,
                    explanation = "Lugar de envasado y procesos declarados"
                )
            )
        }

        // 3. Reciclabilidad (Peso 15)
        val recyclabilityFacts = facts.filter { it.dimension == FactDimension.END_OF_LIFE }
        if (recyclabilityFacts.isNotEmpty()) {
            val isVerified = recyclabilityFacts.any { it.metadata.verificationState == VerificationState.CONFIRMED }
            evaluations.add(
                FactorEvaluation(
                    factor = ScoreFactor.RECYCLABILITY,
                    score = 85,
                    hasData = true,
                    isThirdPartyVerified = isVerified,
                    explanation = "Envase de vidrio y materiales monocomponente"
                )
            )
        }

        // 4. Transporte (Peso 10)
        val logisticsFacts = facts.filter { it.dimension == FactDimension.LOGISTICS }
        if (logisticsFacts.isNotEmpty()) {
            val isVerified = logisticsFacts.any { it.metadata.verificationState == VerificationState.CONFIRMED }
            evaluations.add(
                FactorEvaluation(
                    factor = ScoreFactor.LOGISTICS,
                    score = 60,
                    hasData = true,
                    isThirdPartyVerified = isVerified,
                    explanation = "Cadena logística continental estimada"
                )
            )
        }

        // 5. Empaque (Peso 5)
        evaluations.add(
            FactorEvaluation(
                factor = ScoreFactor.PACKAGING,
                score = 80,
                hasData = true,
                isThirdPartyVerified = true,
                explanation = "Envase primario reciclable en corriente estándar"
            )
        )

        return TraceScoreCalculator.calculate(evaluations)
    }
}
