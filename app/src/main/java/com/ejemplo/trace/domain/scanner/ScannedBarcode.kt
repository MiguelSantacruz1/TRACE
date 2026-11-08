package com.ejemplo.trace.domain.scanner

/**
 * Representa un código de barras capturado con éxito por el escáner.
 */
data class ScannedBarcode(
    val rawValue: String,
    val format: BarcodeFormat,
    val timestamp: Long = System.currentTimeMillis()
)
