package com.ejemplo.trace.domain.repository

import com.ejemplo.trace.domain.model.ProductLookupResult

/**
 * Repositorio de investigación de productos.
 * Orquesta la consulta en fuentes directas (Open Food Facts) y el backend seguro.
 */
interface ProductRepository {

    /**
     * Investiga un producto por su código de barras.
     */
    suspend fun investigateProduct(barcode: String): Result<ProductLookupResult>
}
