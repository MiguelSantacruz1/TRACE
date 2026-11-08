# Plan de Desarrollo por Etapas - TRACE

Este documento registra el progreso, los criterios de aceptación y el conteo de commits para cada etapa del desarrollo de la aplicación Android **TRACE**, siguiendo rigurosamente las pautas del *Prompt maestro (versión 2)*.

---

## 📊 Resumen General del Proyecto
- **Meta mínima de commits:** 200 commits en la rama `main` (apuntando a ~220 para margen de seguridad).
- **Convención de mensajes:** Conventional Commits (`tipo(alcance): descripción breve en infinitivo`).
- **Commits actuales:** 38
- **Etapa actual:** Etapa 3 completada. Listo para Etapa 4.

---

## 🗺️ Etapas de Desarrollo

| Etapa | Alcance | Criterio de Aceptación | Commits Est. | Estado |
| :---: | :--- | :--- | :---: | :---: |
| **1** | **Arquitectura y estructura del proyecto** | Proyecto vacío compila con Hilt y navegación base. Existen README, .gitignore, PLAN.md y docs/DECISIONES.md. Plan aprobado. | ~16 | **Completada** |
| **2** | **Diseño visual y navegación** | Todas las pantallas navegables, tema claro y oscuro, estados de carga, error y vacío. Ningún botón sin función. | ~26 | **Completada** |
| **3** | **Escáner real** | En dispositivo/cámara, un código de barras se lee y muestra su número. Permisos, linterna y lecturas repetidas manejados. | ~22 | **Completada** |
| **4** | **Fuentes externas, backend y búsqueda web** | Escaneo resuelve datos con fuentes identificables. Código inexistente devuelve estado no encontrado. Backend funcional con claves fuera del APK. | ~32 | Pendiente |
| **5** | **Organización y verificación** | Modelo con metadatos de fuente, verificador de coincidencia con pruebas, contradicciones visibles, validación JSON de IA. | ~22 | Pendiente |
| **6** | **TRACE SCORE** | Pruebas unitarias en verde, docs/TRACE_SCORE.md, confianza y aviso legal visibles, mensaje de datos insuficientes. | ~22 | Pendiente |
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
**Criterio de Aceptación:** En un dispositivo/cámara, un EAN-13 se lee y se muestra su número. Permisos, linterna y lecturas repetidas manejados.
**Resultado:** Cumplido satisfactoriamente. 22 pruebas unitarias en verde (22/22) incluyendo códigos reales de prueba (Nutella, Coca-Cola y prefijo colombiano 770). APK Debug compilado exitosamente.

### Tareas y Registro de Commits:
- [x] `feat(scanner): implementar modelos de escaneo y BarcodeValidator con verificacion estricta de checksum y pruebas` (Commit 35)
- [x] `feat(scanner): implementar BarcodeAnalyzer con CameraX y Google ML Kit con prevencion de lecturas repetidas` (Commit 36)
- [x] `feat(scanner): implementar CameraPreviewView con CameraX ProcessCameraProvider, PreviewView y soporte de linterna` (Commit 37)
- [x] `feat(scanner): integrar CameraPreviewView, gestion de permisos de camara y navegacion a resultado` (Commit 38)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 3` (Commit 39)
