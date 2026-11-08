# Auditoría de Seguridad, Rendimiento y Pruebas - TRACE

Este documento compendia las directivas, auditorías y optimizaciones implementadas en la aplicación Android **TRACE** conforme a las Secciones 11 y 12 del *Prompt Maestro – TRACE (versión 2)*.

---

## 1. Auditoría de Seguridad

### A. Gestión de Credenciales y Secretos
- **Cero claves en el código fuente:** Ninguna API key (Google Cloud, OpenAI, Perplexity) está presente en el código Kotlin, Java, XML ni en archivos de recursos compilados en el APK.
- **Backend como Gateway Seguro:** La comunicación con servicios externos que requieran llaves de autenticación se realiza exclusivamente a través del backend (`backend/src/index.js`), el cual gestiona sus variables mediante `.env` fuera del repositorio.
- **Archivos de ejemplo versionados:** Se proporcionan `local.properties.example` y `backend/.env.example` como guías sin credenciales reales.

### B. Seguridad en Comunicaciones de Red (HTTPS)
- **Configuración de Seguridad de Red (`network_security_config.xml`):**
  - Tráfico HTTP plano (`cleartextTraffic`) estrictamente **bloqueado por defecto**.
  - Todo el tráfico hacia APIs externas (Open Food Facts, endpoints de búsqueda) viaja bajo TLS/HTTPS cifrado.
  - Excepción acotada a dominios de depuración local (`10.0.2.2` / `localhost`) para pruebas en emulador contra el backend local.

### C. Seguridad a Nivel de Componentes e IPC
- **Protección de Datos Locales:** `android:allowBackup="false"` configurado en el `AndroidManifest.xml` para evitar extracción de la base de datos Room mediante `adb backup`.
- **Componentes no exportados:** Ningún Activity, Service, Receiver o ContentProvider se expone a otras aplicaciones (`exported="false"`), a excepción estricta de `MainActivity` que requiere el Intent Filter `MAIN` y `LAUNCHER`.
- **Defensa contra Inyecciones (Prompt Injection):** Implementado en `PromptDefenseSanitizer` mediante delimitación XML de datos externos no confiables y neutralización de caracteres especiales.

---

## 2. Optimización de Rendimiento

### A. Jetpack Compose y Recomposiciones
- **Claves Estables en Listas:** Todas las listas `LazyColumn` (historial, sugerencias, factores de score) emplean identificadores estables `key = { it.barcode }` o `key = { it.factorName }` para prevenir recomposiciones redundantes durante scroll y filtrado.
- **Flujos con Ciclo de Vida Consciente:** Consumo de estados en Compose mediante `collectAsStateWithLifecycle()`, deteniendo la emisión de coroutines cuando la pantalla pasa a segundo plano (`SharingStarted.WhileSubscribed(5000)`).

### B. Red y Memoria
- **Caché Efímero con TTL:** Base de datos Room actúa como almacenamiento temporal con caducidad (TTL de 24 horas) para evitar descargas innecesarias y consumo redundante de ancho de banda del usuario.
- **Gestión de Recursos de Hardware:** El analizador de CameraX (`BarcodeAnalyzer`) incluye mecanismo de *debounce* (1000 ms entre lecturas) y desvinculación explícita del ciclo de vida para salvaguardar la batería del dispositivo.

### C. R8 / ProGuard
- Reglas detalladas en `app/proguard-rules.pro` garantizan la preservación de clases de serialización (`kotlinx.serialization`), modelos de Room, CameraX, Coil y Retrofit, reduciendo el tamaño del binario final sin comprometer la reflectividad requerida.

---

## 3. Matriz de Pruebas

| Capa | Herramienta | Alcance | Estado |
| :--- | :--- | :--- | :---: |
| **Algoritmo de Validación** | JUnit 4 | Checksum EAN-13, EAN-8, UPC-A, códigos colombianos 770 | ✅ 100% |
| **TRACE SCORE** | JUnit 4 | Motor determinista, ponderaciones, umbral 50%, confianza | ✅ 100% |
| **Filtros de Seguridad** | JUnit 4 | Detección de inyecciones, sanitización de inputs y delimitación | ✅ 100% |
| **Persistencia Room** | JUnit 4 / Mockk | Mapeo de entidades, TTL de caché y operaciones DAO | ✅ 100% |
| **Comparador** | JUnit 4 | Regla de datos insuficientes y veredicto analítico | ✅ 100% |
| **Navegación / UI** | Compose UI Test | Interacción e integridad de pantallas | ✅ Configurado |
