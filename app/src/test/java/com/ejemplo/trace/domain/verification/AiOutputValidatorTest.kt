package com.ejemplo.trace.domain.verification

import com.ejemplo.trace.domain.model.FactDimension
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para AiOutputValidator verificando descarte de alucinaciones
 * y resistencia a prompt injection sin respaldo.
 */
class AiOutputValidatorTest {

    private val originalSourceText = """
        Ferrero publica que el envase de Nutella cuenta con un frasco de vidrio reciclable en un 100%.
        La tapa está fabricada en polipropileno PP 05. Las materias primas de cacao provienen de fincas
        certificadas por Rainforest Alliance.
    """.trimIndent()

    @Test
    fun validateAndFilter_supportedFact_isAccepted() {
        val facts = listOf(
            AiOutputValidator.RawAiFact(
                field = "Material del frasco",
                value = "Vidrio 100% reciclable",
                dimensionName = "END_OF_LIFE",
                sourceUrl = "https://www.ferrero.com/packaging",
                evidenceSnippet = "frasco de vidrio reciclable en un 100%"
            )
        )

        val result = AiOutputValidator.validateAndFilter(facts, originalSourceText)

        assertEquals(1, result.size)
        assertEquals("Material del frasco", result[0].field)
        assertEquals(FactDimension.END_OF_LIFE, result[0].dimension)
    }

    @Test
    fun validateAndFilter_hallucinatedFactNotInSource_isDiscarded() {
        val facts = listOf(
            AiOutputValidator.RawAiFact(
                field = "Neutralidad de carbono",
                value = "Cero emisiones netas alcanzadas",
                dimensionName = "ENVIRONMENTAL_IMPACT",
                sourceUrl = "https://www.ferrero.com/sustainability",
                evidenceSnippet = "la empresa alcanzó cero emisiones netas en 2023" // No existe en el texto fuente
            )
        )

        val result = AiOutputValidator.validateAndFilter(facts, originalSourceText)

        // Debe ser descartado por no existir evidencia en la fuente
        assertTrue(result.isEmpty())
    }

    @Test
    fun validateAndFilter_missingUrlOrEmptySnippet_isDiscarded() {
        val facts = listOf(
            AiOutputValidator.RawAiFact(
                field = "Cacao",
                value = "Rainforest Alliance",
                dimensionName = "RAW_MATERIALS",
                sourceUrl = "", // URL inválida
                evidenceSnippet = "certificadas por Rainforest Alliance"
            ),
            AiOutputValidator.RawAiFact(
                field = "Tapa",
                value = "Polipropileno",
                dimensionName = "END_OF_LIFE",
                sourceUrl = "https://www.ferrero.com/packaging",
                evidenceSnippet = "" // Fragmento vacío
            )
        )

        val result = AiOutputValidator.validateAndFilter(facts, originalSourceText)

        assertTrue(result.isEmpty())
    }
}
