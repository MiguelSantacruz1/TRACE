package com.ejemplo.trace.data.repository

import com.ejemplo.trace.data.local.dao.ProductHistoryDao
import com.ejemplo.trace.data.local.entity.ScannedProductEntity
import com.ejemplo.trace.di.IoDispatcher
import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de HistoryRepository conectada a Room Database.
 */
@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: ProductHistoryDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : HistoryRepository {

    override fun getHistory(): Flow<List<HistoryProduct>> {
        val now = System.currentTimeMillis()
        return dao.getAllHistory().map { entities ->
            entities.map { it.toDomain(now) }
        }
    }

    override suspend fun getProduct(barcode: String): HistoryProduct? = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        dao.getProductByBarcode(barcode)?.toDomain(now)
    }

    override suspend fun saveScan(product: HistoryProduct, rawJson: String?): Unit = withContext(ioDispatcher) {
        val entity = ScannedProductEntity(
            barcode = product.barcode,
            name = product.name,
            brand = product.brand,
            imageUrl = product.imageUrl,
            scannedAt = product.scannedAt,
            lastScore = product.lastScore,
            confidenceLevel = product.confidenceLevel,
            rawJson = rawJson
        )
        dao.insertOrUpdate(entity)
    }

    override suspend fun deleteScan(barcode: String): Unit = withContext(ioDispatcher) {
        dao.deleteByBarcode(barcode)
    }

    override suspend fun clearHistory(): Unit = withContext(ioDispatcher) {
        dao.clearAll()
    }

    private fun ScannedProductEntity.toDomain(now: Long): HistoryProduct {
        val isCacheValid = (scannedAt + cacheTtlMillis) > now
        return HistoryProduct(
            barcode = barcode,
            name = name,
            brand = brand,
            imageUrl = imageUrl,
            scannedAt = scannedAt,
            lastScore = lastScore,
            confidenceLevel = confidenceLevel,
            isCacheValid = isCacheValid
        )
    }
}
