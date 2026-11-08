# TRACE - Trazabilidad y Sostenibilidad de Productos

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android%20(minSdk%2026%2B)-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg)](https://developer.android.com/jetpack/compose)
[![Tests](https://img.shields.io/badge/Tests-54%20passed%20(100%25)-brightgreen.svg)]()

**TRACE** es una aplicación Android nativa diseñada para investigar productos en tiempo real a partir del escaneo de su código de barras. La aplicación permite descubrir el origen geográfico, los fabricantes, las materias primas, los procesos de producción y transporte, el impacto ambiental potencial, la reparabilidad y la reciclabilidad de cualquier producto empaquetado.

---

## 🎯 Principio Fundamental: Cero Catálogo Propio
TRACE **no tiene una base de datos propia de productos ni un catálogo precargado o manual**. 
- Cada investigación consulta **fuentes externas vivas** (Open Food Facts, Open Beauty Facts, Open Products Facts, resolución de prefijos GS1 y búsqueda web complementaria).
- El backend seguro orquesta las consultas externas y protege las credenciales de APIs e IA.
- **Room** se utiliza únicamente para el **historial de consultas del usuario** y una **caché temporal efímera con expiración (TTL de 24 horas)** para optimizar consumo y cuotas de API. Al abrir un elemento del historial, los datos se re-consultan en Internet.

---

## 🛠️ Stack Tecnológico
- **Lenguaje:** Kotlin 2.0.21 (JVM 17)
- **UI:** Jetpack Compose con Material 3 (soporte completo de modo Claro y Oscuro) y Navigation Compose
- **Cámara & Escáner:** CameraX 1.4.0 + Google ML Kit Barcode Scanning (EAN-13, EAN-8, UPC-A, UPC-E, Code 128) con validación matemática de checksum.
- **Arquitectura:** Clean Architecture + MVVM + Casos de Uso atómicos
- **Inyección de Dependencias:** Hilt 2.51.1 / Dagger con KSP
- **Red & Serialización:** Retrofit 2.11.0 + OkHttp 4.12.0 + `kotlinx.serialization`
- **Asincronía & Reactividad:** Kotlin Coroutines & Flow (StateFlow / collectAsStateWithLifecycle)
- **Persistencia Local:** Room 2.6.1 (exclusivamente referencias locales y caché TTL efímero)
- **Carga de Imágenes:** Coil Compose 2.7.0
- **Motor de Puntuación:** `TraceScoreCalculator` (Kotlin puro, determinista, 0 a 100, umbral de cobertura 50%)
- **Asistente de IA:** TRACE AI (análisis y explicaciones contextuales con respaldo estricto en fuentes y filtro contra prompt injection)

---

## 📊 Metodología TRACE SCORE (0 a 100)

El cálculo del TRACE SCORE es puramente algorítmico y determinista. No es asignado por una IA ni por criterios subjetivos:

| Dimensión / Factor | Ponderación | Criterio de Evaluación |
| :--- | :---: | :--- |
| **1. Origen de Materias Primas** | 20% | Proximidad geográfica, deforestación cero, certificaciones sostenibles (FSC, RSPO, Fair Trade, Orgánico). |
| **2. Fabricación y Emisiones** | 15% | Emisiones Scope 1 y 2, uso de energías renovables y normativas laborales del país de manufactura. |
| **3. Reparabilidad y Durabilidad** | 15% | Disponibilidad de repuestos, facilidad de desmontaje y vida útil esperada. |
| **4. Reciclabilidad y Circularidad** | 15% | Materiales reciclables (vidrio, aluminio, PET), compatibilidad con sistemas locales y porcentaje post-consumo. |
| **5. Transporte y Distribución** | 10% | Distancia estimada de transporte y modo de distribución internacional vs. local. |
| **6. Uso y Eficiencia** | 10% | Consumo energético (etiqueta A-G), consumo de agua o sustancias nocivas durante su uso. |
| **7. Impacto Ambiental Agregado** | 10% | Huella de carbono validada (Agribalyse / PEF) y huella hídrica certificada. |
| **8. Empaque y Embalaje** | 5% | Relación peso empaque/producto, plástico virgen vs. reciclado y biodegradabilidad. |

### Reglas Éticas de Puntuación:
1. **Umbral de Cobertura Mínima (50%):** Si la evidencia verificada cubre menos del 50% del peso total (menos de 50 puntos de ponderación), **no se asigna un score numérico arbitrario** y se reporta *"Datos insuficientes para calcular TRACE SCORE"*.
2. **Descuento por Afirmaciones No Auditadas:** Toda afirmación proveniente de declaraciones de marketing de la marca sin auditoría independiente recibe un factor de castigo del 15% en su ponderación.
3. **Comparador Responsable:** Si a un producto le faltan datos suficientes, la comparativa muestra los contrastes disponibles pero se abstiene de declarar un ganador sesgado.

---

## 🛡️ Seguridad y Resistencia de Prompts

- **Cero Credenciales en el APK:** Ninguna API key de Gemini, OpenAI o servicios web está en el cliente móvil. Todo se delega al backend seguro.
- **Network Security Config:** Tráfico HTTP en texto plano bloqueado por defecto (`cleartextTrafficPermitted="false"`), forzando HTTPS TLS.
- **Protección contra Prompt Injection (`PromptDefenseSanitizer`):**
  - Los datos externos recuperados de la web se encapsulan dentro de etiquetas XML `<untrusted_product_data>` con instrucciones explícitas al modelo de que son datos descriptivos y no ejecutables.
  - Neutralización de secuencias de escape HTML/XML (`&lt;` y `&gt;`).
  - Detección temprana de directivas adversarias (`ignore previous instructions`, `jailbreak`, `act as a`).
  - Límite de 500 caracteres por pregunta para evitar ataques de denegación de servicio.

---

## 📂 Estructura del Repositorio
```
TRACE/
├── app/                  # Aplicación Android nativa
│   ├── src/main/java/com/ejemplo/trace/
│   │   ├── di/           # Módulos Hilt (NetworkModule, DatabaseModule, AiModule, RepositoryModule)
│   │   ├── domain/       # Entidades, repositorios, casos de uso, score calculator, validadores
│   │   ├── data/         # Implementaciones de repositorios, Room, Retrofit, providers
│   │   └── presentation/ # Pantallas Compose M3, ViewModels, componentes reutilizables
│   ├── src/test/         # 54 pruebas unitarias (100% en verde)
│   └── src/androidTest/  # Pruebas instrumentadas de UI en Compose
├── backend/              # Servicio intermediario seguro (Node.js + Express)
│   ├── src/index.js      # Endpoints /health, /v1/lookup, /v1/search, /v1/ai/chat
│   ├── package.json      # Dependencias con rate limiting
│   └── .env.example      # Plantilla de variables de entorno seguras
├── docs/                 # Documentación técnica exhaustiva
│   ├── ARQUITECTURA.md   # Diagramas de arquitectura y flujo de datos
│   ├── DECISIONES.md     # Registro de Decisiones de Arquitectura (ADR)
│   ├── API.md            # Contrato de endpoints del backend seguro
│   ├── TRACE_SCORE.md    # Metodología y rúbrica del algoritmo determinista
│   ├── AI_ARCHITECTURE.md# Arquitectura de IA y defensa contra inyecciones
│   └── SEGURIDAD_Y_RENDIMIENTO.md # Auditoría de seguridad y optimizaciones
├── PLAN.md               # Seguimiento formal de las 10 etapas de desarrollo
└── gradle/               # Wrapper y catálogo de dependencias centralizado
```

---

## 🚀 Puesta en Marcha y Compilación

### 1. Requisitos Previos
- Android Studio Ladybug | 2024.2+
- OpenJDK 17
- Android SDK 35 (`minSdk 26`, `compileSdk 35`, `targetSdk 35`)
- Node.js 18+ (para el backend seguro)

### 2. Configuración del Entorno
```bash
# 1. Clonar el repositorio
git clone https://github.com/MiguelSantacruz1/TRACE.git
cd TRACE

# 2. Configurar Android SDK local
cp local.properties.example local.properties
# Editar local.properties con la ruta a tu Android SDK (ej. sdk.dir=C:\\Users\\...\\AppData\\Local\\Android\\Sdk)

# 3. Iniciar el Backend Seguro
cd backend
npm install
cp .env.example .env
node src/index.js
# El backend iniciará en http://localhost:3000 con endpoints /health, /v1/lookup, /v1/search y /v1/ai/chat
```

### 3. Ejecución de Pruebas Unitarias
```bash
./gradlew testDebugUnitTest
# Resultado esperado: 54 passed, 0 skipped, 0 failed
```

### 4. Compilación de APK
```bash
# Para compilar APK Debug:
./gradlew assembleDebug

# Para compilar APK Release firmado:
./gradlew assembleRelease
# El APK se genera en: app/build/outputs/apk/release/app-release.apk
```

---

## 📋 Códigos de Barras de Prueba Validados

Para probar la aplicación en emulador o dispositivo real:
- **3017620422003** (Nutella 400g - EAN-13 validado en Open Food Facts)
- **5449000000996** (Coca-Cola Original 330ml - EAN-13 validado)
- **7702001041404** (Café Sello Rojo Colombia - Prefijo GS1 770 validado)
- **8480000123456** (Código de prueba para verificación de integridad de checksum)
