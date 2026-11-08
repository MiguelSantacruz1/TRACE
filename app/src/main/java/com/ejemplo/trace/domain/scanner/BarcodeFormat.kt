package com.ejemplo.trace.domain.scanner

/**
 * Formatos de códigos de barras soportados por el escáner de TRACE.
 */
enum class BarcodeFormat(val label: String) {
    EAN_13("EAN-13"),
    EAN_8("EAN-8"),
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    CODE_128("Code 128"),
    CODE_39("Code 39"),
    QR_CODE("Código QR"),
    UNKNOWN("Desconocido")
}
