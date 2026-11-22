package com.example.trace.model

import androidx.compose.runtime.mutableStateListOf

data class SourceLink(val title: String, val url: String)

data class Product(
    val id: String,
    val name: String,
    val barcode: String = "",
    val score: Int,
    val category: String,
    val impactTag: String,
    /** Texto corto sin prefijo, p. ej. "7/10". */
    val repairability: String,
    /** 0f..1f, o null si no aplica (p. ej. un consumible). */
    val repairIndex: Float?,
    val recyclability: String,
    val recycleIndex: Float?,
    val transport: String? = null,
    val rawMaterial: String? = null,
    val manufacturing: String? = null,
    val countryOfOrigin: String? = null,
    val destinationCountry: String? = "Colombia",
    val store: String? = null,
    val consumerStage: String? = null,
    /** Resumen del análisis (solo productos identificados por IA). */
    val summary: String? = null,
    val sources: List<SourceLink> = emptyList(),
    val aiGenerated: Boolean = false
)

val sampleProducts = listOf(
    Product(
        id = "1",
        name = "Auriculares XYZ",
        barcode = "7701234567897",
        score = 72,
        category = "Electrónica",
        impactTag = "Huella CO₂ neutra",
        repairability = "7/10",
        repairIndex = 0.7f,
        recyclability = "65%",
        recycleIndex = 0.65f,
        transport = "Carga marítima baja en emisiones",
        rawMaterial = "Plástico reciclado y aluminio",
        manufacturing = "Ensamblado automatizado de precisión",
        countryOfOrigin = "Vietnam",
        destinationCountry = "Colombia",
        store = "Distribuidor certificado TRACE",
        consumerStage = "Ciclo de vida estimado: 4 años"
    ),
    Product(
        id = "2",
