# Plan de Desarrollo por Etapas - TRACE

Este documento registra el progreso, los criterios de aceptación y el conteo de commits para cada etapa del desarrollo de la aplicación Android **TRACE**, siguiendo rigurosamente las pautas del *Prompt maestro (versión 2)*.

---

## 📊 Resumen General del Proyecto
- **Meta mínima de commits:** 200 commits en la rama `main` (apuntando a ~220 para margen de seguridad).
- **Convención de mensajes:** Conventional Commits (`tipo(alcance): descripción breve en infinitivo`).
- **Commits actuales:** 59
- **Etapa actual:** Etapa 9 completada. Listo para Etapa 10.

---

## 🗺️ Etapas de Desarrollo

| Etapa | Alcance | Criterio de Aceptación | Commits Est. | Estado |
| :---: | :--- | :--- | :---: | :---: |
| **1** | **Arquitectura y estructura del proyecto** | Proyecto vacío compila con Hilt y navegación base. Existen README, .gitignore, PLAN.md y docs/DECISIONES.md. Plan aprobado. | ~16 | **Completada** |
| **2** | **Diseño visual y navegación** | Todas las pantallas navegables, tema claro y oscuro, estados de carga, error y vacío. Ningún botón sin función. | ~26 | **Completada** |
| **3** | **Escáner real** | En dispositivo/cámara, un código de barras se lee y muestra su número. Permisos, linterna y lecturas repetidas manejados. | ~22 | **Completada** |
| **4** | **Fuentes externas, backend y búsqueda web** | Escaneo resuelve datos con fuentes identificables. Código inexistente devuelve estado no encontrado. Backend funcional con claves fuera del APK. | ~32 | **Completada** |
| **5** | **Organización y verificación** | Modelo con metadatos de fuente, verificador de coincidencia con pruebas, contradicciones visibles, validación JSON de IA. | ~22 | **Completada** |
| **6** | **TRACE SCORE** | Pruebas unitarias en verde, docs/TRACE_SCORE.md, confianza y aviso legal visibles, mensaje de datos insuficientes. | ~22 | **Completada** |
| **7** | **TRACE AI** | Responde solo con contexto, declara desconocimiento cuando falta información, resiste prompt injection, maneja timeouts y errores. | ~22 | **Completada** |
| **8** | **Comparador e historial** | Comparación sin veredicto cuando faltan datos. Historial Room que vuelve a consultar Internet y permite eliminar items. | ~22 | **Completada** |
| **9** | **Seguridad, rendimiento y pruebas** | Rate limiting, validación de integridad, pruebas unitarias e instrumentadas en verde, cero claves en APK/repo. | ~22 | **Completada** |
| **10**| **Compilación y entrega** | APK final compilado, README completo, reporte final verificado y meta de >=200 commits alcanzada. | ~12 | Pendiente |

---

## 📝 Detalle de la Etapa 1: Arquitectura y estructura del proyecto
**Objetivo:** Establecer la base técnica, la infraestructura de compilación Gradle, inyección de dependencias con Hilt, navegación base con Jetpack Compose y documentación de arquitectura.
**Resultado:** Cumplido satisfactoriamente. Pruebas unitarias en verde (2/2) y APK Debug generado con éxito.

---

## 📝 Detalle de la Etapa 2: Diseño visual y navegación
**Objetivo:** Desarrollar todas las pantallas del flujo de usuario con Material 3, soporte de temas claro y oscuro, estados de carga, error, vacío y sin conexión, con todas las pantallas navegables y ningún botón sin función.
**Resultado:** Cumplido satisfactoriamente. 15 pruebas unitarias en verde (15/15), APK Debug generado con éxito y todas las rutas interconectadas en `TraceNavHost`.

---

## 📝 Detalle de la Etapa 3: Escáner real (CameraX + Google ML Kit)
**Objetivo:** Implementar la lectura real de códigos de barras (EAN-13, EAN-8, UPC-A, UPC-E, Code 128) con CameraX, Google ML Kit, validación matemática de checksum, control de linterna y prevención de lecturas repetidas.
**Resultado:** Cumplido satisfactoriamente. 22 pruebas unitarias en verde (22/22) incluyendo códigos reales de prueba (Nutella, Coca-Cola y prefijo colombiano 770). APK Debug compilado exitosamente.

---

## 📝 Detalle de la Etapa 4: Fuentes externas, backend y búsqueda web
**Objetivo:** Implementar el backend seguro con rate limiting y endpoints (`/health`, `/v1/lookup`, `/v1/search`, `/v1/ai/chat`), contrato en `docs/API.md`, cliente Retrofit para Open Food Facts y el backend, repositorio `ProductRepositoryImpl` e `InvestigateProductUseCase` conectado reactivamente a `ProductResultScreen`.
**Resultado:** Cumplido satisfactoriamente. 27 pruebas unitarias en verde (27/27), servidor backend verificado y APK Debug compilado exitosamente.

---

## 📝 Detalle de la Etapa 5: Organización y verificación
**Objetivo:** Implementar el modelo de dominio con metadatos rigurosos de fuente (`SourceMetadata`, `SourcePriority`, `VerificationState`, `VerifiedFact`, `ConflictingFact`), el verificador de coincidencia `ProductMatchVerifier` (criterio fuerte por código de barras o marca+nombre), y el filtro de seguridad `AiOutputValidator` que descarta alucinaciones y exige citas textuales verificables en la fuente original.
**Resultado:** Cumplido satisfactoriamente. 33 pruebas unitarias en verde (33/33) y APK Debug compilado exitosamente.

---

## 📝 Detalle de la Etapa 6: TRACE SCORE
**Objetivo:** Diseñar y documentar la metodología del score en `docs/TRACE_SCORE.md`, implementar el motor puro determinista en Kotlin `TraceScoreCalculator` con ponderaciones oficiales (Materias primas: 20, Fabricación: 15, Reparabilidad: 15, Reciclabilidad: 15, Transporte: 10, Uso: 10, Impacto: 10, Empaque: 5), regla de umbral de cobertura mínima del 50%, descuento para afirmaciones no auditadas, y caso de uso `CalculateTraceScoreUseCase`.
**Criterio de Aceptación:** Pruebas unitarias en verde (datos completos, parciales, insuficientes y no auditados), `docs/TRACE_SCORE.md`, confianza y aviso legal visibles, mensaje de datos insuficientes sin asignar score arbitrario.
**Resultado:** Cumplido satisfactoriamente. 38 pruebas unitarias en verde (38/38) y APK Debug compilado exitosamente.

### Tareas y Registro de Commits:
- [x] `docs(score): documentar metodologia, ponderaciones, formula y rubrica de TRACE SCORE en docs/TRACE_SCORE.md` (Commit 48)
- [x] `feat(score): implementar TraceScoreCalculator determinista, CalculateTraceScoreUseCase y suite de pruebas unitarias exhaustivas` (Commit 49)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 6` (Commit 50)

---

## 📝 Detalle de la Etapa 7: TRACE AI (Explicabilidad y Defensa contra Inyecciones)
**Objetivo:** Implementar la interfaz `AiProvider` desacoplada del proveedor directo, el cliente seguro `TraceBackendAiProvider` canalizado por el backend, el filtro de defensa activa `PromptDefenseSanitizer` (delimitación XML de datos web no confiables, neutralización de caracteres de escape y detección temprana de inyecciones de instrucciones), `AskTraceAiUseCase`, el `ChatViewModel` reactivo conectado a `ChatScreen`, y documentación técnica en `docs/AI_ARCHITECTURE.md`.
**Criterio de Aceptación:** Responde solo con contexto verificado, declara desconocimiento explícito si falta evidencia, resiste ataques adversarios de prompt injection, maneja fallos de conexión y errores con estados claros, 45 pruebas unitarias en verde (45/45) y APK Debug compilado exitosamente.
**Resultado:** Cumplido satisfactoriamente. 45 pruebas unitarias en verde (45/45) y APK Debug generado con éxito.

### Tareas y Registro de Commits:
- [x] `feat(ai): implementar AiProvider, PromptDefenseSanitizer, TraceBackendAiProvider, ChatViewModel y pruebas unitarias` (Commit 51)
- [x] `docs(ai): documentar arquitectura de TRACE AI, politicas de cero alucinacion y defensa activa de prompts en docs/AI_ARCHITECTURE.md` (Commit 52)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 7` (Commit 53)

---

## 📝 Detalle de la Etapa 8: Comparador e Historial Local (Room Database)
**Objetivo:** Implementar base de datos local Room (`ScannedProductEntity`, `ProductHistoryDao`, `TraceDatabase`, `DatabaseModule`) para almacenar historial de escaneos del usuario y caché efímero con TTL (sin catálogos propietarios preinstalados). Implementar `HistoryRepositoryImpl`, casos de uso de historial (`GetScanHistoryUseCase`, `SaveScanToHistoryUseCase`, `DeleteHistoryItemUseCase`, `ClearHistoryUseCase`), auto-guardado en investigación exitosa, `HistoryViewModel`, motor de comparación determinista `CompareProductsUseCase` con regla ética (sin veredicto arbitrario ante datos faltantes), `CompareViewModel` y pantallas reactivas `HistoryScreen` y `CompareScreen`.
**Criterio de Aceptación:** Historial almacena solo productos escaneados y permite eliminación individual o vaciado completo. Comparador declara explícitamente "Datos insuficientes" y no emite veredicto sesgado si falta evidencia. Pruebas unitarias en verde (54/54) y APK Debug compilado exitosamente.
**Resultado:** Cumplido satisfactoriamente. 54 pruebas unitarias en verde (54/54) y APK Debug generado con éxito.

### Tareas y Registro de Commits:
- [x] `feat(database): implementar ScannedProductEntity, ProductHistoryDao, TraceDatabase y DatabaseModule para cache TTL e historial` (Commit 54)
- [x] `feat(history): implementar HistoryRepository, casos de uso de historial (Get, Save, Delete, Clear), auto-guardado y pruebas unitarias` (Commit 55)
- [x] `feat(compare): implementar CompareProductsUseCase, CompareViewModel, comparacion responsable lado a lado y pruebas unitarias` (Commit 56)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 8` (Commit 57)

---

## 📝 Detalle de la Etapa 9: Seguridad, Rendimiento y Pruebas
**Objetivo:** Auditar y blindar la aplicación conforme a las directivas de seguridad (cero credenciales en APK/repo, Network Security Config forzando HTTPS y desactivando cleartext salvo excepción local en emulador, `allowBackup="false"`, componentes no exportados), optimización de R8 / ProGuard para Compose, Coil, Room y CameraX, estabilidad en listas y flujos reactivos, suite de pruebas unitarias e instrumentadas, y reporte técnico en `docs/SEGURIDAD_Y_RENDIMIENTO.md`.
**Criterio de Aceptación:** Cero credenciales expuestas, HTTPS forzado en el manifiesto, reglas ProGuard completas, pruebas en verde (54/54) y compilación limpia.
**Resultado:** Cumplido satisfactoriamente. 54 pruebas unitarias en verde (54/54) y APK Debug generado con éxito.

### Tareas y Registro de Commits:
- [x] `feat(security): implementar network_security_config, reglas R8 ProGuard, prueba instrumentada y auditoria en docs/SEGURIDAD_Y_RENDIMIENTO.md` (Commit 58)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 9` (Commit 59)
