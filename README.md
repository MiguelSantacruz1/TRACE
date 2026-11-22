# TRACE

Aplicación Android para investigar lo que compras. Tomas una foto de un producto (o eliges una de la galería), una IA lo identifica y la app te muestra su puntaje de sostenibilidad, las métricas principales y el recorrido del producto.

## Funciones

- Escáner con cámara real (CameraX) o foto de la galería.
- Identificación y calificación del producto con Gemini (Google AI Studio).
- Pantalla de resultado con puntaje TRACE, impacto ambiental, reparabilidad, reciclabilidad y transporte.
- Investigación del producto: materia prima, fabricación, origen, distribución y vida útil.
- TRACE AI: preguntas rápidas sobre el producto (si vale la pena, alternativas, reciclaje, comparación).
- Historial de productos analizados en la pantalla de inicio.

## Tecnologías

- Kotlin y Jetpack Compose (Material 3)
- Navigation Compose
- CameraX
- Gemini API (generateContent)

## Requisitos

- Android Studio con JDK 17 o superior
- Android 11 (API 30) o superior

## Configuración de la clave de Gemini

1. Crea una clave gratuita en https://aistudio.google.com/apikey
2. Agrega en el archivo `local.properties` de la raíz del proyecto:

```
GEMINI_API_KEY=tu_clave
```

3. Opcional: cambia el modelo con `GEMINI_MODEL=nombre-del-modelo`. Por defecto se usa `gemini-3.5-flash-lite`.

`local.properties` está en `.gitignore` y no se sube al repositorio. La clave se compila dentro del APK, así que no compartas el APK si la clave es personal.

## Ejecutar

```
./gradlew assembleDebug
```

