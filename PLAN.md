# Plan de Desarrollo por Etapas - TRACE

Este documento registra el progreso, los criterios de aceptación y el conteo de commits para cada etapa del desarrollo de la aplicación Android **TRACE**, siguiendo rigurosamente las pautas del *Prompt maestro (versión 2)*.

---

## 📊 Resumen General del Proyecto
- **Meta mínima de commits:** 200 commits en la rama `main` (apuntando a ~220 para margen de seguridad).
- **Convención de mensajes:** Conventional Commits (`tipo(alcance): descripción breve en infinitivo`).
- **Commits actuales:** 1
- **Etapa actual:** Etapa 1 - Arquitectura y estructura del proyecto.

---

## 🗺️ Etapas de Desarrollo

| Etapa | Alcance | Criterio de Aceptación | Commits Est. | Estado |
| :---: | :--- | :--- | :---: | :---: |
| **1** | **Arquitectura y estructura del proyecto** | Proyecto vacío compila con Hilt y navegación base. Existen README, .gitignore, PLAN.md y docs/DECISIONES.md. Plan aprobado. | ~16 | **En progreso** |
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

### Tareas y Registro de Commits:
- [x] `chore(project): estructura inicial con README.md y .gitignore` (Commit 1)
- [ ] `docs(plan): crear PLAN.md con seguimiento detallado de todas las etapas y criterios`
- [ ] `docs(decisiones): registrar decisiones técnicas en docs/DECISIONES.md`
- [ ] `docs(arquitectura): documentar arquitectura y flujo de datos en docs/ARQUITECTURA.md`
- [ ] `build(gradle): configurar gradle wrapper y settings.gradle.kts con repositorios estándar`
- [ ] `build(deps): definir catalogo de dependencias en gradle/libs.versions.toml (Compose, Hilt, Room, Retrofit)`
- [ ] `build(app): configurar build.gradle.kts raíz y del módulo app con plugins y compatibilidad Java 17`
- [ ] `build(config): agregar local.properties.example y backend/.env.example para manejo seguro de secretos`
- [ ] `feat(app): configurar clase Application con anotación @HiltAndroidApp y registro en AndroidManifest`
- [ ] `feat(theme): implementar tema Material 3 base (Color, Type, Theme) con soporte claro y oscuro`
- [ ] `feat(resources): configurar strings.xml inicial en español con claves base para la app`
- [ ] `feat(di): crear módulos base de inyección de dependencias con Hilt (CoroutinesDispatchersModule)`
- [ ] `feat(navigation): implementar rutas y estructura de NavHost con Navigation Compose`
- [ ] `feat(presentation): implementar MainActivity y pantalla de bienvenida inicial vinculada a Hilt`
- [ ] `test(app): agregar prueba unitaria de configuración base y verificar compilación exitosa`
- [ ] `docs(plan): actualizar PLAN.md con el cierre formal de la Etapa 1`
