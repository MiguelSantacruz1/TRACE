package com.ejemplo.trace.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsResponse(
    @SerialName("code") val code: String? = null,
    @SerialName("status") val status: Int = 0,
    @SerialName("status_verbose") val statusVerbose: String? = null,
    @SerialName("product") val product: OpenFoodFactsProductDto? = null
)

@Serializable
data class OpenFoodFactsProductDto(
    @SerialName("product_name") val productName: String? = null,
    @SerialName("product_name_es") val productNameEs: String? = null,
    @SerialName("brands") val brands: String? = null,
    @SerialName("categories") val categories: String? = null,
    @SerialName("origins") val origins: String? = null,
    @SerialName("countries") val countries: String? = null,
    @SerialName("manufacturing_places") val manufacturingPlaces: String? = null,
    @SerialName("ingredients_text_es") val ingredientsTextEs: String? = null,
    @SerialName("ingredients_text") val ingredientsText: String? = null,
    @SerialName("packaging") val packaging: String? = null,
    @SerialName("packaging_text_es") val packagingTextEs: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("image_front_url") val imageFrontUrl: String? = null
)
