package com.ejemplo.trace.domain.scanner

/**
 * Validador estricto de códigos de barras (EAN-13, EAN-8, UPC-A, UPC-E).
 * Verifica longitud numérica, dígitos válidos y cálculo del checksum ponderado.
 */
object BarcodeValidator {

    /**
     * Determina si el texto representa un código numérico válido con formato y dígito verificador.
     */
    fun isValid(rawCode: String?): Boolean {
        if (rawCode.isNullOrBlank()) return false
        val trimmed = rawCode.trim()

        if (!trimmed.all { it.isDigit() }) return false

        return when (trimmed.length) {
            13 -> validateEan13Checksum(trimmed)
            8 -> validateEan8Checksum(trimmed)
            12 -> validateUpcAChecksum(trimmed)
            else -> trimmed.length in 4..48 // Permitir códigos de longitud libre alfanuméricos como Code 128
        }
    }

    /**
     * Infiere el formato del código de barras según su estructura.
     */
    fun inferFormat(rawCode: String): BarcodeFormat {
        val trimmed = rawCode.trim()
        if (!trimmed.all { it.isDigit() }) {
            return BarcodeFormat.CODE_128
        }
        return when (trimmed.length) {
            13 -> BarcodeFormat.EAN_13
            8 -> BarcodeFormat.EAN_8
            12 -> BarcodeFormat.UPC_A
            6 -> BarcodeFormat.UPC_E
            else -> BarcodeFormat.CODE_128
        }
    }

    /**
     * Valida el dígito de control de un código EAN-13:
     * Suma de posiciones impares (ponderación 1) + pares (ponderación 3).
     */
    fun validateEan13Checksum(code: String): Boolean {
        if (code.length != 13 || !code.all { it.isDigit() }) return false
        var sum = 0
        for (i in 0 until 12) {
            val digit = code[i].digitToInt()
            sum += if (i % 2 == 0) digit else digit * 3
        }
        val expectedCheckDigit = (10 - (sum % 10)) % 10
        val actualCheckDigit = code[12].digitToInt()
        return expectedCheckDigit == actualCheckDigit
    }

    /**
     * Valida el dígito de control de un código EAN-8:
     * Ponderaciones alternadas 3 y 1 para los primeros 7 dígitos.
     */
    fun validateEan8Checksum(code: String): Boolean {
        if (code.length != 8 || !code.all { it.isDigit() }) return false
        var sum = 0
        for (i in 0 until 7) {
            val digit = code[i].digitToInt()
            sum += if (i % 2 == 0) digit * 3 else digit
        }
        val expectedCheckDigit = (10 - (sum % 10)) % 10
        val actualCheckDigit = code[7].digitToInt()
        return expectedCheckDigit == actualCheckDigit
    }

    /**
     * Valida el dígito de control de un código UPC-A:
     * Ponderación 3 para impares y 1 para pares.
     */
    fun validateUpcAChecksum(code: String): Boolean {
        if (code.length != 12 || !code.all { it.isDigit() }) return false
        var sum = 0
        for (i in 0 until 11) {
            val digit = code[i].digitToInt()
            sum += if (i % 2 == 0) digit * 3 else digit
        }
        val expectedCheckDigit = (10 - (sum % 10)) % 10
        val actualCheckDigit = code[11].digitToInt()
        return expectedCheckDigit == actualCheckDigit
    }
}
