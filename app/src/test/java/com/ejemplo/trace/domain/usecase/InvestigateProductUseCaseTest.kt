package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.ProductLookupResult
import com.ejemplo.trace.domain.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para InvestigateProductUseCase.
 */
class InvestigateProductUseCaseTest {

    private val repository: ProductRepository = mockk()
    private val useCase = InvestigateProductUseCase(repository)

    @Test
    fun invoke_withValidNutellaCode_callsRepositoryAndReturnsSuccess() = runTest {
        val validBarcode = "3017620422003"
        val expectedResult = ProductLookupResult(
            barcode = validBarcode,
            name = "Nutella",
            brand = "Ferrero",
            sourceName = "Open Food Facts",
            sourceUrl = "https://world.openfoodfacts.org/product/$validBarcode"
        )

        coEvery { repository.investigateProduct(validBarcode) } returns Result.success(expectedResult)

        val result = useCase(validBarcode)

        assertTrue(result.isSuccess)
        assertEquals("Nutella", result.getOrNull()?.name)
        coVerify(exactly = 1) { repository.investigateProduct(validBarcode) }
    }

    @Test
    fun invoke_withInvalidBarcode_failsWithoutCallingRepository() = runTest {
        val invalidBarcode = "12345" // Longitud no estándar

        val result = useCase(invalidBarcode)

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { repository.investigateProduct(any()) }
    }
}
