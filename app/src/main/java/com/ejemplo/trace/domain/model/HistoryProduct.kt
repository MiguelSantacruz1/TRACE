package com.ejemplo.trace.domain.model

/**
 * Representa un producto previamente investigado y guardado en el historial local del usuario.
 */
data class HistoryProduct(
    val barcode: String,
    val name: String,
    val brand: String = "",
    val imageUrl: String? = null,
    val scannedAt: Long = System.currentTimeMillis(),
    val lastScore: Int? = null,
    val confidenceLevel: String = "",
    val isCacheValid: Boolean = true
)
