# TRACE - Trazabilidad y Sostenibilidad de Productos

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android%20(minSdk%2026%2B)-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)](https://developer.android.com/jetpack/compose)

TRACE es una aplicación Android nativa diseñada para investigar productos en tiempo real a partir del escaneo de su código de barras. La aplicación permite descubrir el origen, los fabricantes, los materiales, los procesos de producción y transporte, el impacto ambiental potencial, la reparabilidad y la reciclabilidad de los productos.

---

## 🎯 Principio Fundamental: Cero Catálogo Propio
TRACE **no tiene una base de datos propia de productos ni un catálogo precargado o manual**. 
- Cada investigación consulta fuentes externas vivas (Open Food Facts, Open Beauty Facts, Open Products Facts, resolución de códigos y búsqueda web complementaria).
- El backend seguro orquesta las consultas externas y protege las credenciales de APIs e IA.
- Room se utiliza únicamente para el **historial de consultas** (referencias) y una **caché temporal con expiración (TTL)** para optimizar consumo y cuotas de API. Al abrir un elemento del historial, los datos se re-consultan en Internet.

---

## 🛠️ Stack Tecnológico
- **Lenguaje:** Kotlin 2.x (JVM 17)
- **UI:** Jetpack Compose con Material 3 y Navigation Compose
- **Cámara & Escáner:** CameraX + Google ML Kit Barcode Scanning
- **Arquitectura:** Clean Architecture + MVVM + Casos de Uso
- **Inyección de Dependencias:** Hilt / Dagger
- **Red & Serialización:** Retrofit 2 + OkHttp 4 + `kotlinx.serialization`
- **Asincronía & Reactividad:** Kotlin Coroutines & Flow
- **Persistencia Local:** Room (solo historial y caché efímera)
- **Carga de Imágenes:** Coil Compose
- **Motor de Puntuación:** `TraceScoreCalculator` (Kotlin puro, determinista, 0 a 100)
- **Asistente de IA:** TRACE AI (análisis y explicaciones contextuales con respaldo estricto en fuentes y resistencia a prompt injection)

---

## 📂 Estructura del Proyecto
```
TRACE/
├── app/                  # Aplicación Android nativa
│   ├── src/main/java/com/ejemplo/trace/
│   │   ├── di/           # Módulos Hilt
│   │   ├── domain/       # Entidades, repositorios, casos de uso, score calculator
│   │   ├── data/         # Implementaciones de repositorios, Room, Retrofit
│   │   └── presentation/ # Pantallas Compose, ViewModels, tema M3
├── backend/              # Servicio intermediario seguro para APIs externas e IA
├── docs/                 # Documentación técnica de arquitectura y decisiones
│   ├── ARQUITECTURA.md   # Diagramas y flujo de datos
│   ├── DECISIONES.md     # Registro de decisiones técnicas (ADR)
│   ├── API.md            # Contrato de endpoints del backend
│   └── TRACE_SCORE.md    # Rúbrica y metodología del algoritmo de puntuación
├── PLAN.md               # Seguimiento detallado por etapas de desarrollo
└── gradle/               # Wrapper y catálogo de dependencias (libs.versions.toml)
```

---

## 🚀 Requisitos y Configuración
- Android Studio Ladybug | 2024.2+ o superior
- JDK 17
- Android SDK 35 (`compileSdk 35`, `targetSdk 35`, `minSdk 26`)
- Para ejecutar en emulador o dispositivo real:
  - Copiar `local.properties.example` a `local.properties` y configurar `sdk.dir`.
  - Copiar `backend/.env.example` a `backend/.env` para las credenciales del backend.

---

## 📄 Licencia
Proyecto desarrollado bajo estándares de código limpio, seguridad y sostenibilidad digital.
