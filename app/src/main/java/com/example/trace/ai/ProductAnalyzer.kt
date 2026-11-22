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
