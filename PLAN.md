# Plan de Desarrollo por Etapas - TRACE

Este documento registra el progreso, los criterios de aceptación y el conteo de commits para cada etapa del desarrollo de la aplicación Android **TRACE**, siguiendo rigurosamente las pautas del *Prompt maestro (versión 2)*.

---

## 📊 Resumen General del Proyecto
- **Meta mínima de commits:** 200 commits en la rama `main` (apuntando a ~220 para margen de seguridad).
- **Convención de mensajes:** Conventional Commits (`tipo(alcance): descripción breve en infinitivo`).
- **Commits actuales:** 51
- **Etapa actual:** Etapa 6 completada. Listo para Etapa 7.

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
| **7** | **TRACE AI** | Responde solo con contexto, declara desconocimiento cuando falta información, resiste prompt injection, maneja timeouts y errores. | ~22 | Pendiente |
| **8** | **Comparador e historial** | Comparación sin veredicto cuando faltan datos. Historial Room que vuelve a consultar Internet y permite eliminar items. | ~22 | Pendiente |
| **9** | **Seguridad, rendimiento y pruebas** | Rate limiting, validación de integridad, pruebas unitarias e instrumentadas en verde, cero claves en APK/repo. | ~22 | Pendiente |
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
