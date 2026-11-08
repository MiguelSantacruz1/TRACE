package com.ejemplo.trace.data.repository

import com.ejemplo.trace.data.local.dao.ProductHistoryDao
import com.ejemplo.trace.data.local.entity.ScannedProductEntity
import com.ejemplo.trace.domain.model.HistoryProduct
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para HistoryRepositoryImpl.
 */
class HistoryRepositoryImplTest {

    private val dao: ProductHistoryDao = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val repository = HistoryRepositoryImpl(dao, testDispatcher)

    @Test
    fun getHistory_mapsEntitiesToDomainAndCalculatesCacheValidity() = runTest(testDispatcher) {
        val now = System.currentTimeMillis()
        val entities = listOf(
            ScannedProductEntity(
                barcode = "3017620422003",
                name = "Nutella",
                brand = "Ferrero",
                scannedAt = now - 1000,
                lastScore = 65,
                cacheTtlMillis = 86_400_000L
            )
        )

        every { dao.getAllHistory() } returns flowOf(entities)

        val result = repository.getHistory().first()

        assertEquals(1, result.size)
        assertEquals("Nutella", result[0].name)
        assertEquals("Ferrero", result[0].brand)
        assertTrue(result[0].isCacheValid)
    }

    @Test
    fun getProduct_whenExists_returnsDomainProduct() = runTest(testDispatcher) {
        val barcode = "5449000000996"
        val entity = ScannedProductEntity(
            barcode = barcode,
            name = "Coca-Cola",
            brand = "Coca-Cola Company",
            lastScore = 48
        )

        coEvery { dao.getProductByBarcode(barcode) } returns entity

        val result = repository.getProduct(barcode)

        assertNotNull(result)
        assertEquals("Coca-Cola", result?.name)
        coVerify(exactly = 1) { dao.getProductByBarcode(barcode) }
    }

    @Test
    fun saveScan_insertsEntityIntoDao() = runTest(testDispatcher) {
        val product = HistoryProduct(
            barcode = "7702001041404",
            name = "Café Sello Rojo",
            brand = "Nutresa",
            lastScore = 72
        )

        coEvery { dao.insertOrUpdate(any()) } returns Unit

        repository.saveScan(product)

        coVerify(exactly = 1) {
            dao.insertOrUpdate(match { it.barcode == "7702001041404" && it.name == "Café Sello Rojo" })
        }
    }

    @Test
    fun deleteScan_callsDaoDelete() = runTest(testDispatcher) {
        val barcode = "3017620422003"
        coEvery { dao.deleteByBarcode(barcode) } returns Unit

        repository.deleteScan(barcode)

        coVerify(exactly = 1) { dao.deleteByBarcode(barcode) }
    }

    @Test
    fun clearHistory_callsDaoClearAll() = runTest(testDispatcher) {
        coEvery { dao.clearAll() } returns Unit

        repository.clearHistory()

        coVerify(exactly = 1) { dao.clearAll() }
    }
}
