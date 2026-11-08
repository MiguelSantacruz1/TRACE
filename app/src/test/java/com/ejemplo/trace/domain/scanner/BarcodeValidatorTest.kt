package com.ejemplo.trace.domain.scanner

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para BarcodeValidator con códigos reales e inválidos.
 */
class BarcodeValidatorTest {

    @Test
    fun verifyEan13Checksum_nutellaRealCode_isValid() {
        val nutellaCode = "3017620422003"
        assertTrue(BarcodeValidator.validateEan13Checksum(nutellaCode))
        assertTrue(BarcodeValidator.isValid(nutellaCode))
        assertEquals(BarcodeFormat.EAN_13, BarcodeValidator.inferFormat(nutellaCode))
    }

    @Test
    fun verifyEan13Checksum_cocaColaRealCode_isValid() {
        val cocaColaCode = "5449000000996"
        assertTrue(BarcodeValidator.validateEan13Checksum(cocaColaCode))
        assertTrue(BarcodeValidator.isValid(cocaColaCode))
        assertEquals(BarcodeFormat.EAN_13, BarcodeValidator.inferFormat(cocaColaCode))
    }

    @Test
    fun verifyEan13Checksum_colombianRealCodePrefix770_isValid() {
        // Código real colombiano: Café Sello Rojo 500g (7702001041404)
        val colombianCode = "7702001041404"
        assertTrue(BarcodeValidator.validateEan13Checksum(colombianCode))
        assertTrue(BarcodeValidator.isValid(colombianCode))
    }

    @Test
    fun verifyEan13Checksum_invalidCheckDigit_isRejected() {
        val corruptedCode = "3017620422009" // Checksum real es 3
        assertFalse(BarcodeValidator.validateEan13Checksum(corruptedCode))
        assertFalse(BarcodeValidator.isValid(corruptedCode))
    }

    @Test
    fun verifyEan8Checksum_validAndInvalid() {
        val validEan8 = "96385074"
        assertTrue(BarcodeValidator.validateEan8Checksum(validEan8))
        assertTrue(BarcodeValidator.isValid(validEan8))
        assertEquals(BarcodeFormat.EAN_8, BarcodeValidator.inferFormat(validEan8))

        val invalidEan8 = "96385071"
        assertFalse(BarcodeValidator.validateEan8Checksum(invalidEan8))
    }

    @Test
    fun verifyUpcAChecksum_validAndInvalid() {
        val validUpcA = "036000291452"
        assertTrue(BarcodeValidator.validateUpcAChecksum(validUpcA))
        assertTrue(BarcodeValidator.isValid(validUpcA))
        assertEquals(BarcodeFormat.UPC_A, BarcodeValidator.inferFormat(validUpcA))

        val invalidUpcA = "036000291459"
        assertFalse(BarcodeValidator.validateUpcAChecksum(invalidUpcA))
    }

    @Test
    fun verifyNullAndBlankCodes_areRejected() {
        assertFalse(BarcodeValidator.isValid(null))
        assertFalse(BarcodeValidator.isValid(""))
        assertFalse(BarcodeValidator.isValid("   "))
        assertFalse(BarcodeValidator.isValid("abc123"))
    }
}
