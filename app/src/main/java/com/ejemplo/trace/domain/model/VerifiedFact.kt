package com.ejemplo.trace.domain.model

import kotlinx.serialization.Serializable

/**
 * Dimensión o categoría a la que pertenece un dato de sostenibilidad.
 */
@Serializable
enum class FactDimension(val title: String) {
    IDENTIFICATION("Identificación"),
    RAW_MATERIALS("Materias Primas"),
    MANUFACTURING("Fabricación y Procesos"),
    LOGISTICS("Transporte y Logística"),
    DURABILITY_REPAIR("Uso, Vida Útil y Reparabilidad"),
    END_OF_LIFE("Fin de Vida y Reciclabilidad"),
    ENVIRONMENTAL_IMPACT("Impacto Ambiental Directo")
}

/**
 * Representa un hecho contrastado y respaldado por una fuente identificable.
 */
@Serializable
data class VerifiedFact(
    val field: String,
    val value: String,
    val dimension: FactDimension,
    val metadata: SourceMetadata,
    val conflictingValues: List<ConflictingFact> = emptyList()
)

/**
 * Representa una versión contradictoria de otra fuente para un mismo dato.
 */
@Serializable
data class ConflictingFact(
    val alternativeValue: String,
    val sourceName: String,
    val sourceUrl: String,
    val reason: String
)
