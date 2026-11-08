package com.ejemplo.trace.domain.model

import kotlinx.serialization.Serializable

/**
 * Nivel de confianza atribuido a una fuente de información.
 * Sigue la jerarquía de prioridad del Prompt Maestro:
 * 1. Fabricantes y documentación oficial (OFFICIAL)
 * 2. Organismos y certificaciones reconocidas (CERTIFICATION)
 * 3. Bases de datos especializadas (SPECIALIZED_DB)
 * 4. Estudios e investigaciones (RESEARCH)
 * 5. Fuentes secundarias confiables (SECONDARY)
 */
@Serializable
enum class SourcePriority(val rank: Int) {
    OFFICIAL(1),
    CERTIFICATION(2),
    SPECIALIZED_DB(3),
    RESEARCH(4),
    SECONDARY(5),
    UNKNOWN(6)
}

/**
 * Estado de verificación de un dato específico.
 */
@Serializable
enum class VerificationState(val label: String) {
    CONFIRMED("Confirmada"),
    DECLARED("Declarada por el fabricante"),
    ESTIMATED("Estimada"),
    NOT_FOUND("No encontrada"),
    UNVERIFIED_MATCH("Coincidencia no verificada")
}

/**
 * Metadatos obligatorios para cada dato individual recuperado por TRACE.
 * TRACE no inventa información; cada afirmación importante debe estar respaldada.
 */
@Serializable
data class SourceMetadata(
    val sourceName: String,
    val originalUrl: String,
    val queryTimestamp: Long = System.currentTimeMillis(),
    val priority: SourcePriority = SourcePriority.SPECIALIZED_DB,
    val verificationState: VerificationState = VerificationState.CONFIRMED,
    val supportingSnippet: String = ""
)
