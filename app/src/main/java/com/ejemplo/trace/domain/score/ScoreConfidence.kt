package com.ejemplo.trace.domain.score

import kotlinx.serialization.Serializable

/**
 * Nivel de confianza del TRACE SCORE según la cobertura del peso con datos
 * y la calidad de las fuentes verificadas.
 */
@Serializable
enum class ScoreConfidence(val label: String, val description: String) {
    HIGH("Alta", "Cobertura superior al 75% con fuentes prioritarias u oficiales."),
    MEDIUM("Media", "Cobertura entre el 50% y 74% de los factores evaluados."),
    INSUFFICIENT("Insuficiente", "Menos del 50% de los factores cuentan con datos. Puntuación no calculable.")
}
