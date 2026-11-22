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
        name = "Zapatillas ABC",
        barcode = "7709876543217",
        score = 61,
        category = "Calzado",
        impactTag = "Materiales reciclados",
        repairability = "5/10",
        repairIndex = 0.5f,
        recyclability = "50%",
        recycleIndex = 0.5f,
        transport = "Transporte multimodal",
        rawMaterial = "Caucho natural y polímeros reciclados",
        manufacturing = null, // Demuestra 'Información no disponible'
        countryOfOrigin = "Indonesia",
        destinationCountry = "Colombia",
        store = "Tienda oficial y distribuidores",
        consumerStage = null // Demuestra 'Información no disponible'
    ),
    Product(
        id = "3",
        name = "Café XYZ",
        barcode = "7705551234562",
        score = 84,
        category = "Alimentos",
        impactTag = "Orgánico & Comercio Justo",
        repairability = "No aplica (consumible)",
        repairIndex = null,
        recyclability = "90%",
        recycleIndex = 0.9f,
        transport = "Transporte terrestre de proximidad",
        rawMaterial = "Grano 100% Arábica de origen sostenible",
        manufacturing = "Tostado y empaque de bajo impacto en origen",
        countryOfOrigin = "Colombia (Eje Cafetero)",
        destinationCountry = "Colombia",
        store = "Comercio local y tiendas ecológicas",
        consumerStage = "Consumo directo y compostaje del empaque"
    )
)

/** Productos de ejemplo + los que la IA identifica al escanear (el más reciente primero). */
object ProductStore {
    val products = mutableStateListOf<Product>().apply { addAll(sampleProducts) }

    fun add(product: Product) {
        products.add(0, product)
    }
}

fun productById(id: String): Product =
    ProductStore.products.find { it.id == id } ?: ProductStore.products.first()
