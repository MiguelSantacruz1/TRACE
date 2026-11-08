package com.ejemplo.trace.domain.model

/**
 * Interfaz fundamental para incorporar nuevas fuentes de información de productos
 * (Open Food Facts, Open Beauty Facts, Open Products Facts, UPCitemdb, etc.)
 * de forma desacoplada y extensible.
 */
interface ProductSourceProvider {

    /**
     * Nombre identificador del proveedor (ej. "Open Food Facts").
     */
    val providerName: String

    /**
     * Consulta información por código de barras.
     * Retorna el producto encontrado o null si este proveedor no dispone de datos.
     */
    suspend fun lookupByBarcode(barcode: String): ProductLookupResult?

    /**
     * Indica si el proveedor puede procesar la categoría indicada.
     */
    fun supportsCategory(category: String): Boolean = true
}

/**
 * Resultado crudo obtenido por un proveedor de fuentes.
 */
data class ProductLookupResult(
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val category: String? = null,
    val originCountry: String? = null,
    val manufacturingPlaces: String? = null,
    val ingredientsText: String? = null,
    val packaging: String? = null,
    val imageUrl: String? = null,
    val sourceName: String,
    val sourceUrl: String,
    val rawPayload: String? = null
)
