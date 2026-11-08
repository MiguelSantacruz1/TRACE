package com.ejemplo.trace.data.repository

import com.ejemplo.trace.data.remote.api.TraceBackendApi
import com.ejemplo.trace.data.remote.provider.OpenFoodFactsProvider
import com.ejemplo.trace.di.IoDispatcher
import com.ejemplo.trace.domain.model.ProductLookupResult
import com.ejemplo.trace.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val openFoodFactsProvider: OpenFoodFactsProvider,
    private val backendApi: TraceBackendApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    override suspend fun investigateProduct(barcode: String): Result<ProductLookupResult> = withContext(ioDispatcher) {
        try {
            // 1. Intento primario con Open Food Facts
            val directResult = openFoodFactsProvider.lookupByBarcode(barcode)
            if (directResult != null) {
                return@withContext Result.success(directResult)
            }

            // 2. Consulta de respaldo al backend seguro
            val backendResponse = try {
                backendApi.lookupProduct(barcode)
            } catch (_: Exception) {
                null
            }

            if (backendResponse != null && backendResponse.found) {
                return@withContext Result.success(
                    ProductLookupResult(
                        barcode = backendResponse.barcode,
                        name = backendResponse.productName ?: "Producto $barcode",
                        brand = backendResponse.brand,
                        category = backendResponse.category,
                        originCountry = backendResponse.originCountry,
                        manufacturingPlaces = backendResponse.manufacturingPlaces,
                        packaging = backendResponse.packaging,
                        imageUrl = backendResponse.imageUrl,
                        sourceName = backendResponse.sources.firstOrNull()?.name ?: "TRACE Backend",
                        sourceUrl = backendResponse.sources.firstOrNull()?.url ?: ""
                    )
                )
            }

            Result.failure(NoSuchElementException("Producto no encontrado en fuentes abiertas ni en backend"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
