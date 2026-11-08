package com.ejemplo.trace.data.remote.provider

import com.ejemplo.trace.data.remote.api.OpenFoodFactsApi
import com.ejemplo.trace.domain.model.ProductLookupResult
import com.ejemplo.trace.domain.model.ProductSourceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenFoodFactsProvider @Inject constructor(
    private val api: OpenFoodFactsApi
) : ProductSourceProvider {

    override val providerName: String = "Open Food Facts"

    override suspend fun lookupByBarcode(barcode: String): ProductLookupResult? {
        return try {
            val response = api.getProductByBarcode(barcode)
            if (response.status == 1 && response.product != null) {
                val p = response.product
                val name = p.productNameEs ?: p.productName ?: "Producto $barcode"
                ProductLookupResult(
                    barcode = barcode,
                    name = name,
                    brand = p.brands,
                    category = p.categories,
                    originCountry = p.origins ?: p.countries,
                    manufacturingPlaces = p.manufacturingPlaces,
                    ingredientsText = p.ingredientsTextEs ?: p.ingredientsText,
                    packaging = p.packagingTextEs ?: p.packaging,
                    imageUrl = p.imageFrontUrl ?: p.imageUrl,
                    sourceName = providerName,
                    sourceUrl = "https://world.openfoodfacts.org/product/$barcode"
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
