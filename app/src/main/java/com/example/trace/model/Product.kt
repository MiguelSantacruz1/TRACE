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
