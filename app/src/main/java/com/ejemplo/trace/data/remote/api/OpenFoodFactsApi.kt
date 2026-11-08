package com.ejemplo.trace.data.remote.api

import com.ejemplo.trace.data.remote.dto.OpenFoodFactsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Cliente Retrofit para la API abierta v0/v2 de Open Food Facts.
 */
interface OpenFoodFactsApi {

    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): OpenFoodFactsResponse
}
