# Plan de Desarrollo por Etapas - TRACE

Este documento registra el progreso, los criterios de aceptación y el conteo de commits para cada etapa del desarrollo de la aplicación Android **TRACE**, siguiendo rigurosamente las pautas del *Prompt maestro (versión 2)*.

---

## 📊 Resumen General del Proyecto
- **Meta mínima de commits:** 200 commits en la rama `main` (apuntando a ~220 para margen de seguridad).
- **Convención de mensajes:** Conventional Commits (`tipo(alcance): descripción breve en infinitivo`).
- **Commits actuales:** 16
- **Etapa actual:** Etapa 1 completada. Listo para Etapa 2.

---

## 🗺️ Etapas de Desarrollo

| Etapa | Alcance | Criterio de Aceptación | Commits Est. | Estado |
| :---: | :--- | :--- | :---: | :---: |
| **1** | **Arquitectura y estructura del proyecto** | Proyecto vacío compila con Hilt y navegación base. Existen README, .gitignore, PLAN.md y docs/DECISIONES.md. Plan aprobado. | ~16 | **Completada** |
| **2** | **Diseño visual y navegación** | Todas las pantallas navegables, tema claro y oscuro, estados de carga, error y vacío. Ningún botón sin función. | ~26 | Pendiente |
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

### Tareas y Registro de Commits:
- [x] `chore(project): estructura inicial con README.md y .gitignore` (Commit 1)
- [x] `docs(plan): crear PLAN.md con seguimiento detallado de todas las etapas y criterios` (Commit 2)
- [x] `docs(decisiones): registrar decisiones técnicas en docs/DECISIONES.md` (Commit 3)
- [x] `docs(arquitectura): documentar arquitectura y flujo de datos en docs/ARQUITECTURA.md` (Commit 4)
- [x] `build(config): agregar local.properties.example y backend/.env.example para manejo seguro de secretos` (Commit 5)
- [x] `build(config): incluir local.properties.example y permitirlo en .gitignore` (Commit 6)
- [x] `build(gradle): configurar gradle wrapper y settings.gradle.kts con repositorios estandar` (Commit 7)
- [x] `build(deps): definir catalogo de dependencias en gradle/libs.versions.toml (Compose, Hilt, Room, Retrofit)` (Commit 8)
- [x] `build(app): configurar build.gradle.kts raiz y del modulo app con plugins y compatibilidad Java 17` (Commit 9)
- [x] `feat(app): configurar clase Application con anotacion @HiltAndroidApp y registro en AndroidManifest` (Commit 10)
- [x] `feat(theme): implementar tema Material 3 base (Color, Type, Theme) con soporte claro y oscuro` (Commit 11)
- [x] `feat(resources): configurar strings.xml inicial en espanol con claves base para la app` (Commit 12)
- [x] `feat(di): crear modulos base de inyeccion de dependencias con Hilt (CoroutinesDispatchersModule)` (Commit 13)
- [x] `feat(presentation): implementar MainActivity, NavHost y pantalla de bienvenida base con Hilt` (Commit 14)
- [x] `test(app): agregar prueba unitaria de configuracion base y verificar compilacion exitosa` (Commit 15)
- [x] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 1` (Commit 16)
