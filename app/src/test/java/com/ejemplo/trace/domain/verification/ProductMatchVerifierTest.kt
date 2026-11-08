package com.ejemplo.trace.domain.verification

import com.ejemplo.trace.domain.model.VerificationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para ProductMatchVerifier verificando las reglas del Prompt Maestro.
 */
class ProductMatchVerifierTest {

    @Test
    fun verify_exactBarcodeInContent_returnsConfirmed() {
        val barcode = "3017620422003"
        val content = "Detalle del producto en catálogo oficial. Código EAN: 3017620422003. Frasco de avellanas."

        val result = ProductMatchVerifier.verify(
            targetBarcode = barcode,
            targetBrand = "Ferrero",
            targetName = "Nutella",
            pageContent = content
        )

        assertEquals(VerificationState.CONFIRMED, result.state)
        assertEquals(1.0f, result.confidenceScore, 0.01f)
        assertTrue(result.isEligibleForScore)
    }

    @Test
    fun verify_brandAndNameMatchWithoutBarcode_returnsDeclared() {
        val barcode = "3017620422003"
        val content = "Informe corporativo Ferrero: La crema Nutella de cacao y avellanas emplea energía solar."

        val result = ProductMatchVerifier.verify(
            targetBarcode = barcode,
            targetBrand = "Ferrero",
            targetName = "Nutella Cacao Avellanas",
            pageContent = content
        )

        assertEquals(VerificationState.DECLARED, result.state)
        assertTrue(result.confidenceScore >= 0.8f)
        assertTrue(result.isEligibleForScore)
    }

    @Test
    fun verify_unrelatedContent_returnsUnverifiedMatchAndNotEligibleForScore() {
        val barcode = "3017620422003"
        val content = "Artículo general de noticias sobre el precio internacional del café y el trigo en 2026."

        val result = ProductMatchVerifier.verify(
            targetBarcode = barcode,
            targetBrand = "Ferrero",
            targetName = "Nutella",
            pageContent = content
        )

        assertEquals(VerificationState.UNVERIFIED_MATCH, result.state)
        assertFalse(result.isEligibleForScore)
    }
}
