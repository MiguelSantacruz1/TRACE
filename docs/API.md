# Contrato de API del Backend Seguro - TRACE

Este documento define la especificación de los endpoints del backend seguro de TRACE (`backend/`), los formatos de solicitud/respuesta, códigos de estado y políticas de seguridad y privacidad.

---

## 🛡️ Principios Generales de Seguridad
1. **Protección de Claves:** Ninguna clave de APIs de búsqueda web (Brave/Tavily) ni de modelos de IA (Gemini/OpenAI) reside en la aplicación Android.
2. **Validación de Entradas:** Códigos de barras validados estrictamente (numéricos, longitudes válidas de 8, 12, 13 dígitos o alfanumérico seguro para Code 128).
3. **Rate Limiting:** Máximo 30 peticiones por minuto por cliente/IP.
4. **Privacidad:** No se almacenan datos personales, conversaciones de chat ni catálogos persistentes de productos.

---

## 📌 Endpoints

### 1. `GET /health`
Verifica el estado del servicio y la disponibilidad de los proveedores configurados.
- **Respuesta (200 OK):**
```json
{
  "status": "healthy",
  "version": "1.0.0",
  "environment": "production",
  "uptime": 12450
}
```

---

### 2. `GET /v1/lookup?barcode={barcode}`
Consulta fuentes externas estructuradas (Open Food Facts, Open Beauty Facts, Open Products Facts) a partir del código de barras.
- **Parámetros:**
  - `barcode` (string, requerido): Código numérico del producto (ej. `3017620422003`).
- **Respuesta (200 OK - Encontrado):**
```json
{
  "barcode": "3017620422003",
  "found": true,
  "productName": "Nutella",
  "brand": "Ferrero",
  "category": "Alimentos / Cremas de cacao",
  "originCountry": "Italia",
  "manufacturingPlaces": "Villers-Écalles, Francia",
  "ingredientsText": "Azúcar, aceite de palma, avellanas (13%), cacao desgrasado (7.4%), leche desnatada en polvo (6.6%), suero lácteo en polvo, emulgentes: lecitinas (soja), vainillina.",
  "packaging": "Frasco de vidrio, tapa de polipropileno",
  "imageUrl": "https://images.openfoodfacts.org/images/products/301/762/042/2003/front_fr.553.400.jpg",
  "sources": [
    {
      "name": "Open Food Facts",
      "url": "https://world.openfoodfacts.org/product/3017620422003",
      "reliability": "HIGH"
    }
  ]
}
```
- **Respuesta (404 Not Found - Código Inexistente):**
```json
{
  "barcode": "9999999999999",
  "found": false,
  "message": "Producto no encontrado en fuentes abiertas"
}
```

---

### 3. `POST /v1/search`
Realiza búsquedas web complementarias para corroborar materiales, cadena de suministro, reportes de sostenibilidad o cuando el producto no está en las bases de datos abiertas.
- **Cuerpo de petición:**
```json
{
  "query": "Nutella Ferrero sostenibilidad materiales empaque",
  "barcode": "3017620422003",
  "maxResults": 5
}
```
- **Respuesta (200 OK):**
```json
{
  "results": [
    {
      "title": "Informe de Sostenibilidad Ferrero - Cadena de Suministro",
      "url": "https://www.ferrero.com/sustainability/reports",
      "snippet": "Ferrero declara el uso de aceite de palma 100% certificado RSPO Segregated y cacao procedente de granjas certificadas Rainforest Alliance...",
      "publishedDate": "2024-03-15"
    }
  ]
}
```

---

### 4. `POST /v1/ai/extract`
Extrae datos estructurados desde los fragmentos de texto recuperados en la web, asignando URL y fragmento de evidencia obligatorio a cada campo.
- **Cuerpo de petición:**
```json
{
  "barcode": "3017620422003",
  "productName": "Nutella",
  "rawSnippets": [
    {
      "url": "https://www.ferrero.com/sustainability/reports",
      "text": "Frasco de vidrio reciclable en un 100%..."
    }
  ]
}
```
- **Respuesta (200 OK):**
```json
{
  "extractedFacts": [
    {
      "category": "PACKAGING",
      "field": "Frasco de vidrio",
      "value": "100% reciclable",
      "evidenceSnippet": "Frasco de vidrio reciclable en un 100%",
      "sourceUrl": "https://www.ferrero.com/sustainability/reports",
      "confidence": "HIGH"
    }
  ]
}
```

---

### 5. `POST /v1/ai/chat`
Endpoint de TRACE AI. Responde preguntas del usuario apoyándose exclusivamente en el contexto verificado del producto e ignora cualquier directiva maliciosa que provenga de los datos web (defensa contra prompt injection).
- **Cuerpo de petición:**
```json
{
  "barcode": "3017620422003",
  "question": "¿Qué materiales componen el envase y cómo se recicla?",
  "verifiedContext": "Envase principal: Vidrio 100% reciclable (Fuente: Open Food Facts). Tapa: Polipropileno PP 05 reciclable.",
  "calculatedScore": 65
}
```
- **Respuesta (200 OK):**
```json
{
  "answer": "El envase de Nutella está compuesto por un frasco de vidrio 100% reciclable y una tapa de polipropileno (PP 05). Para reciclarlo, se recomienda separar la tapa del frasco y depositar el vidrio en el contenedor correspondiente.",
  "citedSources": [
    "Open Food Facts",
    "Ficha de empaque Ferrero"
  ]
}
```
