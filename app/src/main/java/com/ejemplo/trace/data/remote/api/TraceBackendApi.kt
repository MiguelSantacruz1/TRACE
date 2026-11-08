package com.ejemplo.trace.data.remote.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@Serializable
data class BackendLookupResponse(
    @SerialName("barcode") val barcode: String,
    @SerialName("found") val found: Boolean,
    @SerialName("productName") val productName: String? = null,
    @SerialName("brand") val brand: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("originCountry") val originCountry: String? = null,
    @SerialName("manufacturingPlaces") val manufacturingPlaces: String? = null,
    @SerialName("packaging") val packaging: String? = null,
    @SerialName("imageUrl") val imageUrl: String? = null,
    @SerialName("sources") val sources: List<BackendSourceDto> = emptyList(),
    @SerialName("message") val message: String? = null
)

@Serializable
data class BackendSourceDto(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("reliability") val reliability: String
)

@Serializable
data class BackendSearchRequest(
    @SerialName("query") val query: String,
    @SerialName("barcode") val barcode: String,
    @SerialName("maxResults") val maxResults: Int = 5
)

@Serializable
data class BackendSearchResponse(
    @SerialName("results") val results: List<BackendSearchResultDto> = emptyList()
)

@Serializable
data class BackendSearchResultDto(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("snippet") val snippet: String,
    @SerialName("publishedDate") val publishedDate: String? = null
)

@Serializable
data class BackendChatRequest(
    @SerialName("barcode") val barcode: String,
    @SerialName("question") val question: String,
    @SerialName("verifiedContext") val verifiedContext: String,
    @SerialName("calculatedScore") val calculatedScore: Int? = null
)

@Serializable
data class BackendChatResponse(
    @SerialName("answer") val answer: String,
    @SerialName("citedSources") val citedSources: List<String> = emptyList()
)

interface TraceBackendApi {

    @GET("health")
    suspend fun getHealth(): Map<String, String>

    @GET("v1/lookup")
    suspend fun lookupProduct(
        @Query("barcode") barcode: String
    ): BackendLookupResponse

    @POST("v1/search")
    suspend fun searchWeb(
        @Body request: BackendSearchRequest
    ): BackendSearchResponse

    @POST("v1/ai/chat")
    suspend fun chatWithAi(
        @Body request: BackendChatRequest
    ): BackendChatResponse
}
