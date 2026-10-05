package com.example.trace.model

data class Product(
    val id: String,
    val name: String,
    val score: Int,
    val category: String,
    val impactTag: String,
    val repairability: String,
    val recyclability: String,
    val transport: String? = null,
    val rawMaterial: String? = null,
    val manufacturing: String? = null,
    val countryOfOrigin: String? = null,
    val destinationCountry: String? = "Colombia",
    val store: String? = null,
    val consumerStage: String? = null
)

val sampleProducts = listOf(
    Product(
        id = "1",
        name = "Auriculares XYZ",
        score = 72,
        category = "Electrónica",
        impactTag = "Huella CO₂ neutra",
        repairability = "Reparabilidad: 7/10",
        recyclability = "Reciclabilidad: 65%",
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
        score = 61,
        category = "Calzado",
        impactTag = "Materiales reciclados",
        repairability = "Reparabilidad: 5/10",
        recyclability = "Reciclabilidad: 50%",
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
        score = 84,
        category = "Alimentos",
        impactTag = "Orgánico & Comercio Justo",
        repairability = "Empaque biodegradable",
        recyclability = "Reciclabilidad: 90%",
        transport = "Transporte terrestre de proximidad",
        rawMaterial = "Grano 100% Arábica de origen sostenible",
        manufacturing = "Tostado y empaque de bajo impacto en origen",
        countryOfOrigin = "Colombia (Eje Cafetero)",
        destinationCountry = "Colombia",
        store = "Comercio local y tiendas ecológicas",
        consumerStage = "Consumo directo y compostaje del empaque"
    )
)
