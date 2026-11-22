package com.example.trace.ai

import android.util.Base64
import com.example.trace.BuildConfig
import com.example.trace.model.Product
import com.example.trace.model.SourceLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

class AnalysisException(message: String) : Exception(message)

private class QuotaException : Exception()

private class OverloadedException : Exception()

/**
 * Identifica un producto a partir de una foto con Gemini (visión + búsqueda en Google)
 * y devuelve un [Product] ya calificado.
 */
object ProductAnalyzer {

    @Volatile
    private var searchAvailable = true

    private const val SYSTEM = """Eres TRACE AI, analista de sostenibilidad de productos de consumo para Colombia.
Recibes la foto de un producto. Debes:
1) Identificar el producto (marca, modelo, tipo) a partir de la imagen.
2) Usar la búsqueda web para encontrar información REAL: materiales, país de fabricación, certificaciones, reparabilidad, reciclabilidad, huella de carbono, prácticas de la marca.
3) Calificar de 0 a 100 su sostenibilidad, ponderando materiales, reparabilidad, reciclabilidad, transporte/huella y certificaciones. Sé conservador: sin evidencia, no subas el puntaje.
Reglas: no inventes datos; si algo no aparece en tus fuentes, usa null. Responde en español."""

    private const val PROMPT = """Analiza el producto de la foto. Responde ÚNICAMENTE con un objeto JSON válido (sin markdown, sin texto extra) con esta forma exacta:
{
 "identified": true o false,
 "name": "marca y nombre del producto",
 "category": "una o dos palabras (Electrónica, Calzado, Alimentos, Ropa, Hogar...)",
 "score": entero 0-100,
 "impactTag": "frase muy corta (máximo 6 palabras) sobre su impacto ambiental",
 "repairability": "p. ej. 6/10, o 'No aplica (consumible)'",
 "repairIndex": número 0-1 o null,
 "recyclability": "p. ej. 70%",
 "recycleIndex": número 0-1 o null,
 "transport": "texto o null",
 "rawMaterial": "texto o null",
 "manufacturing": "texto o null",
 "countryOfOrigin": "texto o null",
 "store": "dónde se vende, o null",
 "consumerStage": "vida útil / disposición final, o null",
 "summary": "2 o 3 frases: qué encontraste y por qué ese puntaje"
}
Si en la foto no hay un producto identificable, responde {"identified": false}."""

    suspend fun analyze(jpeg: ByteArray): Result<Product> = withContext(Dispatchers.IO) {
        runCatching {
            if (BuildConfig.GEMINI_API_KEY.isBlank()) {
                throw AnalysisException(
                    "Falta la clave de Gemini. Crea una gratis en aistudio.google.com/apikey " +
                        "y agrégala en local.properties como GEMINI_API_KEY=..."
                )
            }
            var response: JSONObject? = null
            for (attempt in 1..4) {
                try {
                    response = send(jpeg)
                    break
                } catch (e: OverloadedException) {
                    // 503: el modelo está saturado, suele pasar en segundos.
                    if (attempt == 4) {
                        throw AnalysisException("Gemini está saturado en este momento. Intenta de nuevo en un minuto.")
                    }
                    delay(2500L * attempt)
                }
            }
            parse(response!!)
        }.recoverCatching {
            throw when (it) {
                is AnalysisException -> it
                is UnknownHostException -> AnalysisException("Sin conexión a internet.")
                is SocketTimeoutException -> AnalysisException("La IA tardó demasiado. Intenta de nuevo.")
                else -> AnalysisException("No se pudo analizar el producto: ${it.message}")
            }
        }
    }

    private fun send(jpeg: ByteArray): JSONObject {
        if (!searchAvailable) return call(jpeg, useSearch = false)
        return try {
            call(jpeg, useSearch = true)
        } catch (e: QuotaException) {
            // La búsqueda web no tiene cuota gratuita en todas las cuentas:
            // se recuerda para no gastar una llamada en cada foto.
            searchAvailable = false
            call(jpeg, useSearch = false)
        }
    }

    private fun call(jpeg: ByteArray, useSearch: Boolean): JSONObject {
        val body = JSONObject()
            .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", SYSTEM))))
            .put(
                "contents", JSONArray().put(
                    JSONObject().put("role", "user").put(
                        "parts", JSONArray()
                            .put(
                                JSONObject().put(
                                    "inline_data",
                                    JSONObject()
                                        .put("mime_type", "image/jpeg")
                                        .put("data", Base64.encodeToString(jpeg, Base64.NO_WRAP))
                                )
                            )
                            .put(JSONObject().put("text", PROMPT))
                    )
                )
            )
            .put("generationConfig", JSONObject().put("temperature", 0.2))
        if (useSearch) body.put("tools", JSONArray().put(JSONObject().put("google_search", JSONObject())))

        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/${BuildConfig.GEMINI_MODEL}:generateContent")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 90_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", BuildConfig.GEMINI_API_KEY)
        }
        try {
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = conn.responseCode
            if (code != 200) {
                val err = conn.errorStream?.bufferedReader()?.readText().orEmpty()
                val msg = runCatching { JSONObject(err).getJSONObject("error").getString("message") }
                    .getOrDefault(err.take(200))
                if (code == 429 && useSearch) throw QuotaException()
                if (code == 503) throw OverloadedException()
                throw AnalysisException(
                    when (code) {
                        400, 403 -> "Clave de Gemini inválida o sin permiso: $msg"
                        429 -> "Se alcanzó el límite gratuito de Gemini. Espera un minuto e intenta de nuevo."
                        else -> "Error $code de Gemini: $msg"
                    }
                )
            }
            return JSONObject(conn.inputStream.bufferedReader().readText())
        } finally {
            conn.disconnect()
        }
    }

    private fun parse(response: JSONObject): Product {
        val candidate = response.optJSONArray("candidates")?.optJSONObject(0)
            ?: throw AnalysisException("La IA no devolvió respuesta (puede haberse bloqueado la imagen).")

        val text = buildString {
            val parts = candidate.optJSONObject("content")?.optJSONArray("parts") ?: JSONArray()
            for (i in 0 until parts.length()) append(parts.getJSONObject(i).optString("text"))
        }
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start < 0 || end <= start) throw AnalysisException("La IA respondió en un formato inesperado.")
        val json = JSONObject(text.substring(start, end + 1))

        if (!json.optBoolean("identified", true) || json.optString("name").isBlank()) {
            throw AnalysisException("No pude identificar un producto en la foto. Acércate y procura buena luz.")
        }

        fun str(key: String): String? =
            if (json.isNull(key)) null
            else json.optString(key).trim().takeIf { it.isNotEmpty() && it != "null" }

        fun idx(key: String): Float? {
            if (!json.has(key) || json.isNull(key)) return null
            val v = json.optDouble(key)
            return if (v.isNaN()) null else v.toFloat().coerceIn(0f, 1f)
        }

        val sources = mutableListOf<SourceLink>()
        val chunks = candidate.optJSONObject("groundingMetadata")?.optJSONArray("groundingChunks")
        if (chunks != null) {
            for (i in 0 until chunks.length()) {
                val web = chunks.getJSONObject(i).optJSONObject("web") ?: continue
                val uri = web.optString("uri")
                if (uri.isNotBlank() && sources.none { it.url == uri }) {
                    sources += SourceLink(web.optString("title").ifBlank { uri }, uri)
                }
            }
        }

        return Product(
            id = "ai-${System.currentTimeMillis()}",
            name = json.getString("name").trim(),
            score = json.optInt("score", 50).coerceIn(0, 100),
            category = str("category") ?: "Producto",
            impactTag = str("impactTag") ?: "Impacto no determinado",
            repairability = str("repairability") ?: "Información no disponible",
            repairIndex = idx("repairIndex"),
            recyclability = str("recyclability") ?: "Información no disponible",
            recycleIndex = idx("recycleIndex"),
            transport = str("transport"),
            rawMaterial = str("rawMaterial"),
            manufacturing = str("manufacturing"),
            countryOfOrigin = str("countryOfOrigin"),
            store = str("store"),
            consumerStage = str("consumerStage"),
            summary = str("summary"),
            sources = sources.take(5),
            aiGenerated = true
        )
    }
}
