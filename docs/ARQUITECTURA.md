# Arquitectura del Sistema - TRACE

Este documento describe la arquitectura de software de la aplicación Android TRACE, los límites de sus capas, el flujo de datos para la investigación de productos y las medidas de seguridad adoptadas.

---

## 🏛️ Diagrama General de Capas

```mermaid
graph TD
    subgraph UI ["Capa de Presentación (Jetpack Compose + M3)"]
        HomeScreen["HomeScreen"]
        ScannerScreen["ScannerScreen (CameraX + ML Kit)"]
        ResultScreen["ResultScreen"]
        InvestigationScreen["InvestigationScreen"]
        ChatScreen["ChatScreen (TRACE AI)"]
        CompareScreen["CompareScreen"]
        ViewModels["Hilt ViewModels (StateFlow / UI State)"]
    end

    subgraph Domain ["Capa de Dominio (Kotlin Puro)"]
        UseCases["Casos de Uso (ScanBarcode, Investigate, Compare)"]
        ScoreEngine["TraceScoreCalculator (Algoritmo Determinista)"]
        Verifier["SourceVerifier & FactMatcher (Validación)"]
        DomainModels["Modelos de Dominio (Product, TraceScore, Source)"]
        RepoInterfaces["Interfaces de Repositorio (Product, History, Ai)"]
    end

    subgraph Data ["Capa de Datos"]
        RepoImpl["Implementaciones de Repositorio"]
        
        subgraph Local ["Persistencia Local (Room)"]
            HistoryDao["HistoryDao (Solo Referencias)"]
            CacheDao["CacheDao (Caché Efímera con TTL)"]
        end

        subgraph Remote ["Servicios Remotos (Retrofit + OkHttp)"]
            BackendClient["BackendApiClient (API Segura)"]
            OpenFoodFacts["OpenFoodFactsProvider"]
            OpenBeautyFacts["OpenBeautyFactsProvider"]
        end
    end

    subgraph BackendGateway ["Backend Seguro (Cloudflare / Firebase)"]
        Proxy["Proxy & Rate Limiting"]
        WebSearchAPI["Brave / Tavily Search API"]
        GenAiAPI["Google Gemini / OpenAI API"]
    end

    UI --> ViewModels
    ViewModels --> UseCases
    UseCases --> ScoreEngine
    UseCases --> Verifier
    UseCases --> RepoInterfaces
    RepoImpl --> RepoInterfaces
    RepoImpl --> Local
    RepoImpl --> Remote
    Remote --> BackendGateway
```

---

## 🔄 Flujo de Datos de una Investigación

1. **Captura:** El usuario enfoca un código de barras en `ScannerScreen`. CameraX y Google ML Kit leen el valor numérico (ej. EAN-13: `3017620422003`).
2. **Despacho:** El `ScannerViewModel` delega a `InvestigateProductUseCase`.
3. **Consulta de Fuentes Directas:** El repositorio consulta `OpenFoodFactsProvider` / `OpenProductsFacts` según la categoría detectada.
4. **Búsqueda Web Complementaria:** Si los datos son insuficientes o requieren corroboración de materiales/fabricante, se solicita al `BackendApiClient` una búsqueda estructurada.
5. **Verificación y Coincidencia:** `SourceVerifier` analiza los fragmentos obtenidos, validando que el código o la combinación exacta de marca + nombre coincida con el producto investigado. Los datos no verificados se aíslan.
6. **Cálculo del TRACE SCORE:** `TraceScoreCalculator` recibe los factores verificados (materias primas, fabricación, reparabilidad, reciclabilidad, etc.) y calcula la puntuación ponderada (0–100) y su nivel de confianza (Alto, Medio, Bajo).
7. **Presentación:** Los resultados enriquecidos se emiten mediante `StateFlow` hacia `ResultScreen` e `InvestigationScreen`.
8. **Interacción con TRACE AI:** Si el usuario abre el chat, `ChatViewModel` envía la pregunta junto al contexto estricto de las fuentes verificadas hacia `POST /v1/ai/chat` en el backend seguro.

---

## 🛡️ Límites de Seguridad y Privacidad
- **Cero Claves en el APK:** Las credenciales de APIs de pago o de IA residen únicamente en las variables de entorno del backend (`backend/.env`).
- **Defensa contra Prompt Injection:** Cualquier texto extraído de páginas web o fuentes abiertas se envía a la IA etiquetado como dato no confiable (*untrusted content*), con instrucciones de sistema estrictas que prohíben ejecutar directivas contenidas en los datos.
- **Transparencia Total de Fuentes:** Cada afirmación relevante muestra su URL de origen, fecha de consulta, fragmento citado y estado de verificación (*confirmada*, *declarada*, *estimada*, *no encontrada*).
