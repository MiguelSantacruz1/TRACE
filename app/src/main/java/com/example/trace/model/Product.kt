package com.example.trace.model

import android.content.Context
import android.content.SharedPreferences
import com.example.trace.i18n.tr
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

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

/** Productos de ejemplo, en el idioma activo. */
fun sampleProducts(): List<Product> = listOf(
    Product(
        id = "1",
        name = tr("Auriculares XYZ", "XYZ Headphones"),
        barcode = "7701234567897",
        score = 72,
        category = tr("Electrónica", "Electronics"),
        impactTag = tr("Huella CO₂ neutra", "Carbon neutral footprint"),
        repairability = "7/10",
        repairIndex = 0.7f,
        recyclability = "65%",
        recycleIndex = 0.65f,
        transport = tr("Carga marítima baja en emisiones", "Low-emission sea freight"),
        rawMaterial = tr("Plástico reciclado y aluminio", "Recycled plastic and aluminum"),
        manufacturing = tr("Ensamblado automatizado de precisión", "Automated precision assembly"),
        countryOfOrigin = tr("Vietnam", "Vietnam"),
        destinationCountry = tr("Colombia", "Colombia"),
        store = tr("Distribuidor certificado TRACE", "TRACE certified distributor"),
        consumerStage = tr("Ciclo de vida estimado: 4 años", "Estimated lifespan: 4 years")
    ),
    Product(
        id = "2",
        name = tr("Zapatillas ABC", "ABC Sneakers"),
        barcode = "7709876543217",
        score = 61,
        category = tr("Calzado", "Footwear"),
        impactTag = tr("Materiales reciclados", "Recycled materials"),
        repairability = "5/10",
        repairIndex = 0.5f,
        recyclability = "50%",
        recycleIndex = 0.5f,
        transport = tr("Transporte multimodal", "Multimodal transport"),
        rawMaterial = tr("Caucho natural y polímeros reciclados", "Natural rubber and recycled polymers"),
        manufacturing = null, // Demuestra 'Información no disponible'
        countryOfOrigin = tr("Indonesia", "Indonesia"),
        destinationCountry = tr("Colombia", "Colombia"),
        store = tr("Tienda oficial y distribuidores", "Official store and distributors"),
        consumerStage = null // Demuestra 'Información no disponible'
    ),
    Product(
        id = "3",
        name = tr("Café XYZ", "XYZ Coffee"),
        barcode = "7705551234562",
        score = 84,
        category = tr("Alimentos", "Food"),
        impactTag = tr("Orgánico & Comercio Justo", "Organic & Fair Trade"),
        repairability = tr("No aplica (consumible)", "Not applicable (consumable)"),
        repairIndex = null,
        recyclability = "90%",
        recycleIndex = 0.9f,
        transport = tr("Transporte terrestre de proximidad", "Local ground transport"),
        rawMaterial = tr("Grano 100% Arábica de origen sostenible", "100% Arabica beans from sustainable sources"),
        manufacturing = tr("Tostado y empaque de bajo impacto en origen", "Low-impact roasting and packaging at origin"),
        countryOfOrigin = tr("Colombia (Eje Cafetero)", "Colombia (Coffee Region)"),
        destinationCountry = tr("Colombia", "Colombia"),
        store = tr("Comercio local y tiendas ecológicas", "Local shops and eco stores"),
        consumerStage = tr("Consumo directo y compostaje del empaque", "Direct consumption and packaging composting")
    )
)

/** Productos de ejemplo + los que la IA identifica al escanear (el más reciente primero). */
object ProductStore {
    /** Productos escaneados con la IA (el más reciente primero). */
    private val scanned = mutableStateListOf<Product>()

    /** Escaneos propios + productos de ejemplo en el idioma activo. */
    val products: List<Product> get() = scanned + sampleProducts()

    private const val PREFS = "trace_store"
    private const val KEY = "scanned"
    private var prefs: SharedPreferences? = null

    /** Carga los escaneos guardados. Se llama una vez al abrir la app. */
    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs = p
        val saved = runCatching { fromJson(JSONArray(p.getString(KEY, "[]"))) }.getOrDefault(emptyList())
        scanned.addAll(saved)
    }

    fun add(product: Product) {
        scanned.add(0, product)
        save()
    }

    private fun save() {
        prefs?.edit()?.putString(KEY, toJson(scanned.toList()).toString())?.apply()
    }

    private fun toJson(list: List<Product>): JSONArray = JSONArray().also { arr ->
        list.forEach { p ->
            arr.put(
                JSONObject()
                    .put("id", p.id).put("name", p.name).put("score", p.score)
                    .put("category", p.category).put("impactTag", p.impactTag)
                    .put("repairability", p.repairability).put("repairIndex", p.repairIndex?.toDouble() ?: JSONObject.NULL)
                    .put("recyclability", p.recyclability).put("recycleIndex", p.recycleIndex?.toDouble() ?: JSONObject.NULL)
                    .put("transport", p.transport ?: JSONObject.NULL)
                    .put("rawMaterial", p.rawMaterial ?: JSONObject.NULL)
                    .put("manufacturing", p.manufacturing ?: JSONObject.NULL)
                    .put("countryOfOrigin", p.countryOfOrigin ?: JSONObject.NULL)
                    .put("store", p.store ?: JSONObject.NULL)
                    .put("consumerStage", p.consumerStage ?: JSONObject.NULL)
                    .put("summary", p.summary ?: JSONObject.NULL)
                    .put(
                        "sources",
                        JSONArray().also { s ->
                            p.sources.forEach { s.put(JSONObject().put("title", it.title).put("url", it.url)) }
                        }
                    )
            )
        }
    }

    private fun fromJson(arr: JSONArray): List<Product> = (0 until arr.length()).map { i ->
        val o = arr.getJSONObject(i)
        fun str(k: String) = if (o.isNull(k)) null else o.getString(k)
        fun idx(k: String) = if (o.isNull(k)) null else o.getDouble(k).toFloat()
        val src = o.optJSONArray("sources") ?: JSONArray()
        Product(
            id = o.getString("id"),
            name = o.getString("name"),
            score = o.getInt("score"),
            category = o.getString("category"),
            impactTag = o.getString("impactTag"),
            repairability = o.getString("repairability"),
            repairIndex = idx("repairIndex"),
            recyclability = o.getString("recyclability"),
            recycleIndex = idx("recycleIndex"),
            transport = str("transport"),
            rawMaterial = str("rawMaterial"),
            manufacturing = str("manufacturing"),
            countryOfOrigin = str("countryOfOrigin"),
            store = str("store"),
            consumerStage = str("consumerStage"),
            summary = str("summary"),
            sources = (0 until src.length()).map {
                SourceLink(src.getJSONObject(it).getString("title"), src.getJSONObject(it).getString("url"))
            },
            aiGenerated = true
        )
    }
}

fun productById(id: String): Product =
    ProductStore.products.find { it.id == id } ?: ProductStore.products.first()
