# Arquitectura de TRACE AI y Seguridad de Prompts

Este documento describe la arquitectura, las directivas de seguridad y los mecanismos de defensa implementados en **TRACE AI**, en cumplimiento estricto con las Secciones 7, 9 y 12 del *Prompt Maestro – TRACE (versión 2)*.

---

## 1. Principio Fundamental: Explicador Riguroso, Cero Alucinaciones

TRACE AI no es un oráculo omnisciente ni una base de datos de conocimiento estático. Su función es:
1. **Analizar y explicar** únicamente la evidencia verificada (`VerifiedFact`) recuperada de fuentes externas y el cálculo determinista del **TRACE SCORE**.
2. **Citar las fuentes exactas** de cada afirmación presentada al usuario.
3. **Declarar explícitamente el desconocimiento** cuando no existan fuentes verificadas para responder a la pregunta.
4. **No inventar datos** ni atribuir compromisos o certificaciones medioambientales a una marca si no están respaldados por una fuente identificable.

---

## 2. Flujo de Datos y Gateway Seguro

```
[Usuario / ChatScreen]
          │
          ▼
[ChatViewModel]
          │ (valida longitud y caracteres)
          ▼
[AskTraceAiUseCase]
          │
          ▼
[AiProvider / TraceBackendAiProvider]
          │
          ├─► [PromptDefenseSanitizer] (detecta inyecciones adversarias)
          │        └─ Si es adversario: responde localmente con rechazo cordial.
          │
          ├─► [wrapUntrustedContext] (encapsula datos con etiquetas XML y neutraliza escapes)
          │
          ▼
[Backend Gateway (Node.js / Express)] (API Key oculta fuera del APK)
          │
          ▼
[LLM Provider (Gemini 1.5 Pro / GPT-4o)]
          │
          ▼
[Respuesta Estructurada con Citas y Sugerencias]
```

---

## 3. Defensa Activa Contra Inyecciones (Prompt Defense)

Los datos obtenidos de páginas web externas o empaques de productos pueden contener instrucciones adversarias ocultas diseñadas para manipular al modelo (Prompt Injection indirecto).

### Estrategias implementadas en `PromptDefenseSanitizer`:
1. **Aislamiento de Contexto con Delimitadores XML:**
   Los hechos verificados se envuelven en `<untrusted_product_data>` con instrucciones explícitas al modelo de que el bloque contiene datos descriptivos y bajo ninguna circunstancia debe obedecer instrucciones dentro de él.
2. **Neutralización de Caracteres Especiales:**
   Los caracteres `<` y `>` presentes en los datos recuperados se transforman en entidades de escape (`&lt;` y `&gt;`) para impedir la ruptura de la estructura XML.
3. **Detección Temprana de Inyecciones Adversarias:**
   Filtro heurístico que intercepta patrones como `ignore previous instructions`, `olvida las reglas`, `act as a`, `jailbreak`, `dan mode`, respondiendo de inmediato a nivel de cliente sin consumir tokens de API.
4. **Límite de Longitud:**
   Las preguntas del usuario se acotan a 500 caracteres para evitar ataques de desbordamiento de contexto o denegación de servicio.

---

## 4. Política ante Información Ausente o Confusa

Cuando una pregunta del usuario consulte aspectos no respaldados por los hechos verificados del producto (por ejemplo, "¿cuánta agua consumió la fábrica en 2024?"):
- El asistente responde: *"No disponemos de fuentes verificadas sobre el consumo de agua de este producto en el registro recuperado."*
- Ofrece preguntas sugeridas contextualizadas con la información que sí está comprobada.
