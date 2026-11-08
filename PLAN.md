# Plan de Desarrollo por Etapas - TRACE

Este documento registra el progreso, los criterios de aceptación y el conteo de commits para cada etapa del desarrollo de la aplicación Android **TRACE**, siguiendo rigurosamente las pautas del *Prompt maestro (versión 2)*.

---

## 📊 Resumen General del Proyecto
- **Meta mínima de commits:** 200 commits en la rama `main` (apuntando a ~220 para margen de seguridad).
- **Convención de mensajes:** Conventional Commits (`tipo(alcance): descripción breve en infinitivo`).
- **Commits actuales:** 30
- **Etapa actual:** Etapa 2 completada. Listo para Etapa 3.

---

## 🗺️ Etapas de Desarrollo

| Etapa | Alcance | Criterio de Aceptación | Commits Est. | Estado |
| :---: | :--- | :--- | :---: | :---: |
| **1** | **Arquitectura y estructura del proyecto** | Proyecto vacío compila con Hilt y navegación base. Existen README, .gitignore, PLAN.md y docs/DECISIONES.md. Plan aprobado. | ~16 | **Completada** |
| **2** | **Diseño visual y navegación** | Todas las pantallas navegables, tema claro y oscuro, estados de carga, error y vacío. Ningún botón sin función. | ~26 | **Completada** |
| **3** | **Escáner real** | En dispositivo/cámara, un código de barras se lee y muestra su número. Permisos, linterna y lecturas repetidas manejados. | ~22 | Pendiente |
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
**Criterio de Aceptación:** Proyecto vacío compila con Hilt y navegación base. Existen README.md, .gitignore, PLAN.md y docs/DECISIONES.md. Plan aprobado por el usuario.
**Resultado:** Cumplido satisfactoriamente. Pruebas unitarias en verde (2/2) y APK Debug generado con éxito.

---

## 📝 Detalle de la Etapa 2: Diseño visual y navegación
**Objetivo:** Desarrollar todas las pantallas del flujo de usuario con Material 3, soporte de temas claro y oscuro, estados de carga, error, vacío y sin conexión, con todas las pantallas navegables y ningún botón sin función.
**Criterio de Aceptación:** Todas las pantallas navegables, tema claro y oscuro, estados de carga, error y vacío. Ningún botón sin función.
**Resultado:** Cumplido satisfactoriamente. 15 pruebas unitarias en verde (15/15), APK Debug generado con éxito y todas las rutas interconectadas en `TraceNavHost`.

### Tareas y Registro de Commits:
- [x] `feat(ui): implementar componente reutilizable LoadingState con animacion y soporte accesible` (Commit 17)
- [x] `feat(ui): implementar componente reutilizable ErrorState con reintento configurable` (Commit 18)
- [x] `feat(ui): implementar componente reutilizable EmptyState con soporte para accion primaria opcional` (Commit 19)
- [x] `feat(ui): implementar componente reutilizable OfflineState para manejo de desconexion` (Commit 20)
- [x] `feat(ui): implementar componente ScoreBadge con semantica accesible y soporte multinivel` (Commit 21)
- [x] `feat(ui): implementar ScannerScreen con visor animado, control de linterna e instrucciones` (Commit 22)
- [x] `feat(ui): implementar ProductResultScreen con ScoreBadge, metricas clave y aviso legal` (Commit 23)
- [x] `feat(ui): implementar InvestigationScreen con 6 dimensiones de trazabilidad y estados de verificacion` (Commit 24)
- [x] `feat(ui): implementar ChatScreen con interfaz conversacional para TRACE AI y citas de fuentes` (Commit 25)
- [x] `feat(ui): implementar CompareScreen con comparativa lado a lado por dimensiones y evaluacion responsable` (Commit 26)
- [x] `feat(ui): implementar HistoryScreen con lista de consultas previas, re-investigacion y eliminacion` (Commit 27)
- [x] `feat(navigation): conectar todas las pantallas en TraceNavHost con paso de argumentos y acciones reales` (Commit 28)
- [x] `test(navigation): agregar pruebas unitarias para rutas de navegacion y clasificacion de niveles de ScoreBadge` (Commit 29)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 2` (Commit 30)
