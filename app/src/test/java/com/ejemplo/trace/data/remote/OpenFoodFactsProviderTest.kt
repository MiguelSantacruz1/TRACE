package com.ejemplo.trace.data.remote

import com.ejemplo.trace.data.remote.api.OpenFoodFactsApi
import com.ejemplo.trace.data.remote.dto.OpenFoodFactsProductDto
import com.ejemplo.trace.data.remote.dto.OpenFoodFactsResponse
import com.ejemplo.trace.data.remote.provider.OpenFoodFactsProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias para OpenFoodFactsProvider con datos de prueba controlados (TEST_DATA).
 */
class OpenFoodFactsProviderTest {

    private val api: OpenFoodFactsApi = mockk()
    private val provider = OpenFoodFactsProvider(api)

    @Test
    fun lookupByBarcode_nutellaFound_returnsProductLookupResult() = runTest {
        val testBarcode = "3017620422003"
        val mockDto = OpenFoodFactsResponse(
            code = testBarcode,
            status = 1,
            statusVerbose = "product found",
            product = OpenFoodFactsProductDto(
                productName = "Nutella",
                productNameEs = "Nutella Crema de Avellana",
                brands = "Ferrero",
                categories = "Alimentos, Cremas dulces",
                origins = "Italia",
                manufacturingPlaces = "Villers-Écalles, Francia",
                packaging = "Frasco de vidrio"
            )
        )

        coEvery { api.getProductByBarcode(testBarcode) } returns mockDto

        val result = provider.lookupByBarcode(testBarcode)

        assertNotNull(result)
        assertEquals(testBarcode, result?.barcode)
        assertEquals("Nutella Crema de Avellana", result?.name)
        assertEquals("Ferrero", result?.brand)
        assertEquals("Italia", result?.originCountry)
        assertEquals("Villers-Écalles, Francia", result?.manufacturingPlaces)
        assertEquals("Open Food Facts", result?.sourceName)
    }

    @Test
    fun lookupByBarcode_productNotFound_returnsNull() = runTest {
        val missingBarcode = "9999999999999"
        val mockDto = OpenFoodFactsResponse(
            code = missingBarcode,
            status = 0,
            statusVerbose = "product not found",
            product = null
        )

        coEvery { api.getProductByBarcode(missingBarcode) } returns mockDto

        val result = provider.lookupByBarcode(missingBarcode)

        assertNull(result)
    }

    @Test
    fun lookupByBarcode_networkError_returnsNullSafely() = runTest {
        val failingBarcode = "0000000000000"
        coEvery { api.getProductByBarcode(failingBarcode) } throws RuntimeException("Network timeout")

        val result = provider.lookupByBarcode(failingBarcode)

        assertNull(result)
    }
}
