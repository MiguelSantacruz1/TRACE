# Registro de Decisiones Técnicas (ADR) - TRACE

Este documento registra las decisiones arquitectónicas y técnicas adoptadas en el proyecto TRACE, detallando el contexto, la justificación y las consecuencias de cada elección.

---

## ADR 01: Ausencia de Catálogo Propio y Consulta Viva en Internet
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** Las soluciones tradicionales de análisis de productos dependen de bases de datos estáticas que rápidamente quedan desactualizadas, requieren mantenimiento manual continuo o incurren en sesgos de catálogo.
- **Decisión:** TRACE **no mantiene un catálogo propio ni almacena productos prefabricados**. Cada investigación desencadena una consulta directa a fuentes abiertas (Open Food Facts, Open Beauty Facts, Open Products Facts) y búsqueda web complementaria.
- **Consecuencias:** La aplicación refleja información actualizada en tiempo real y requiere conexión a Internet para nuevas investigaciones. Se implementa una caché efímera con TTL configurable (24 horas) únicamente para respetar cuotas de API y optimizar el consumo de datos móviles.

---

## ADR 02: Estructura de Proyecto Monomódulo Bien Paquetizado (`:app`)
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** Evaluar entre una arquitectura multimódulo Gradle frente a un monomódulo bien estructurado por capas y características.
- **Decisión:** Se elige un único módulo principal `:app` con estricta separación de capas (`di`, `data`, `domain`, `presentation`).
- **Justificación:** Reduce drásticamente la sobrecarga de sincronización de Gradle y la complejidad de configuración de plugins, mientras preserva el desacoplamiento mediante interfaces en la capa `domain`. Si el proyecto crece en el futuro, las capas están delimitadas para una extracción limpia.
- **Consecuencias:** Tiempos de compilación más rápidos, menor fricción de configuración y máxima claridad de navegación entre clases.

---

## ADR 03: Stack de UI con Jetpack Compose, Material 3 y Navigation Compose
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** Se requiere una interfaz moderna, reactiva, accesible y con soporte continuo para temas claro y oscuro.
- **Decisión:** Utilizar Jetpack Compose 100% declarativo junto a Material Design 3 y Navigation Compose con rutas tipadas.
- **Justificación:** Elimina por completo los layouts XML, simplifica la gestión del estado de la UI (`StateFlow` + `collectAsStateWithLifecycle`) y proporciona componentes accesibles por defecto.
- **Consecuencias:** Desarrollo ágil, animaciones fluidas y soporte sin esfuerzo para diferentes densidades y orientaciones de pantalla.

---

## ADR 04: Inyección de Dependencias con Dagger Hilt
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** La app requiere desacoplamiento en red, persistencia, casos de uso y ViewModels para garantizar testeabilidad con MockK y Turbine.
- **Decisión:** Emplear Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`).
- **Justificación:** Es la solución estándar y recomendada por Google para Android, con integración nativa con Jetpack Compose (`hiltViewModel()`) y ciclo de vida de componentes.
- **Consecuencias:** Inyección limpia sin código repetitivo (*boilerplate*), permitiendo sustituir fácilmente implementaciones reales por dobles de prueba.

---

## ADR 05: Backend Intermediario Seguro para APIs Externas e IA
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** El uso de APIs de búsqueda web (Brave, Tavily o Serper) y de Inteligencia Artificial (Gemini, OpenAI) requiere claves privadas que jamás deben ser expuestas en el APK ni decompilables.
- **Decisión:** Implementar un backend ligero en la carpeta `backend/` (desplegable en Cloudflare Workers o Firebase Cloud Functions).
- **Justificación:** Centraliza la seguridad, protege las claves privadas de API, valida las peticiones mediante App Check / Play Integrity y aplica límites de tasa (*rate limiting*) para mitigar abusos y costes.
- **Consecuencias:** La app Android interactúa exclusivamente con los endpoints de este backend seguro.

---

## ADR 06: Motor TRACE SCORE Determinista en Kotlin Puro
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** El cálculo de la puntuación de sostenibilidad debe ser objetivo, transparente, reproducible y verificable por pruebas automatizadas, evitando alucinaciones o variaciones arbitrarias de la IA.
- **Decisión:** `TraceScoreCalculator` es una función/clase pura en Kotlin sin dependencias de Android ni de modelos generativos. La IA sólo interviene para explicar e interpretar el score calculado, nunca para asignar la puntuación.
- **Justificación:** Permite cobertura de pruebas unitarias al 100%, reproducibilidad matemática y confianza para el usuario final.
- **Consecuencias:** Cualquier modificación a las ponderaciones o rúbricas es auditable directamente en código fuente y documentación (`docs/TRACE_SCORE.md`).

---

## ADR 07: Red con Retrofit, OkHttp y kotlinx.serialization
- **Fecha:** 2026-11-07
- **Estado:** Aceptado
- **Contexto:** Se requiere consumir APIs REST seguras con serialización JSON de alto rendimiento y tipado estricto.
- **Decisión:** Combinar Retrofit 2 con OkHttp 4 y el convertidor oficial de `kotlinx.serialization`.
- **Justificación:** `kotlinx.serialization` está optimizado para Kotlin, no requiere reflexión en tiempo de ejecución, respeta la nulabilidad estricta y produce menor huella en R8/ProGuard en comparación con Gson o Jackson.
- **Consecuencias:** Modelos de datos (`@Serializable`) robustos y deserialización segura frente a campos faltantes o inesperados.
