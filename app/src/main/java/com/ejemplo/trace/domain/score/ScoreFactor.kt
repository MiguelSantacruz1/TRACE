package com.ejemplo.trace.domain.score

import kotlinx.serialization.Serializable

/**
 * Factores evaluados por el motor de cálculo de TRACE SCORE.
 * Las ponderaciones oficiales suman exactamente 100 puntos.
 */
@Serializable
enum class ScoreFactor(val defaultWeight: Int, val title: String) {
    RAW_MATERIALS(20, "Materias primas"),
    MANUFACTURING(15, "Fabricación"),
    REPAIRABILITY(15, "Reparabilidad"),
    RECYCLABILITY(15, "Reciclabilidad"),
    LOGISTICS(10, "Transporte"),
    DURABILITY(10, "Uso y durabilidad"),
    ENVIRONMENTAL_IMPACT(10, "Impacto ambiental directo"),
    PACKAGING(5, "Empaque")
}

/**
 * Evaluación individual de un factor para un producto.
 * @param score Puntuación de 0 a 100 obtenida según los datos verificados.
 * @param hasData Indica si se cuenta con información respaldada para este factor.
 * @param isThirdPartyVerified Si el dato está auditado por terceros (si es solo declarado por fabricante, pesa menos).
 * @param explanation Explicación técnica de la puntuación asignada.
 */
@Serializable
data class FactorEvaluation(
    val factor: ScoreFactor,
    val score: Int?,
    val hasData: Boolean,
    val isThirdPartyVerified: Boolean = false,
    val explanation: String = ""
)
