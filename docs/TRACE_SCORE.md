# Metodología y Algoritmo de Cálculo: TRACE SCORE

Este documento define la metodología, las ponderaciones, la rúbrica de puntuación, el tratamiento de datos faltantes y las limitaciones del **TRACE SCORE**, la puntuación de sostenibilidad de 0 a 100 de la aplicación TRACE.

---

## 🎯 Principio Fundamental: Determinismo Puro en Código
El TRACE SCORE **lo calcula exclusivamente el código Kotlin**, mediante la clase pura `TraceScoreCalculator`. 
- **La IA no calcula ni altera el score**: Únicamente lo explica y contextualiza basándose en el resultado numérico y las fuentes verificadas.
- **Sin datos no hay puntuación arbitraria**: Un factor sin datos no suma 0 ni 100. Permanece en estado `SIN DATOS`.
- **Umbral de Cobertura Mínima (50%)**: El score total **solo se calcula si los factores con datos verificados suman al menos el 50% del peso total**. Si no se alcanza este umbral, la app muestra de forma transparente:
  > *«Información insuficiente para calcular un TRACE SCORE confiable»*, junto con el desglose de los factores ausentes.

---

## ⚖️ Factores y Ponderación Oficial

| Factor | Peso Teórico | Qué Evalúa | Criterios para Puntuación Alta (80–100 pts) | Criterios para Puntuación Baja (0–40 pts) |
| :--- | :---: | :--- | :--- | :--- |
| **Materias primas** | **20** | Origen de materias primas, certificaciones éticas/ambientales (Fairtrade, Rainforest Alliance, RSPO, Orgánico/Bio). | 100% de ingredientes clave con certificación auditada de sostenibilidad. | Ingredientes asociados a deforestación sin certificación o de origen desconocido. |
| **Fabricación** | **15** | Procesos de producción, uso de energías renovables en plantas, gestión de residuos industriales y auditorías ambientales. | Plantas con certificación ISO 14001, >80% energía renovable declarada y auditada. | Procesos de alta intensidad fósil sin planes de mitigación publicados. |
| **Reparabilidad** | **15** | Disponibilidad de repuestos, manuales públicos, facilidad de desmontaje o índice oficial de reparabilidad (Francia/UE). | Índice de reparabilidad >8.0/10, piezas reemplazables con herramientas estándar. | Productos sellados, baterías pegadas, sin repuestos disponibles (o no aplica a consumibles). |
| **Reciclabilidad** | **15** | Materiales del producto y su facilidad de incorporación en corrientes estándar de reciclaje municipal o circular. | Monomateriales altamente reciclables (Vidrio, Aluminio, Cartón FSC, PET/HDPE limpio). | Multicapas plásticos complejos no reciclables, plásticos mezclados difíciles de separar. |
| **Transporte** | **10** | Distancia geográfica entre lugar de fabricación y mercado destino, modo de transporte (marítimo/terrestre vs. aéreo). | Producción local o regional (<1,000 km) o logística optimizada baja en carbono. | Transporte aéreo de larga distancia o rutas logísticas fragmentadas (>10,000 km). |
| **Uso y durabilidad** | **10** | Vida útil proyectada, eficiencia energética (etiqueta A/B), garantías extendidas del fabricante y mantenimiento. | Garantía superior a la legal (>3 años), alta durabilidad documentada o alta eficiencia. | Obsolescencia programada, baja durabilidad documentada o alto consumo en uso. |
| **Impacto ambiental directo** | **10** | Huella de carbono publicada por unidad de producto (Scope 1, 2, 3) o Análisis de Ciclo de Vida (LCA) verificado. | LCA completo publicado por terceros con huella neta baja o neutral certificada. | Huella de carbono muy superior al promedio de la categoría sin compensación. |
| **Empaque** | **5** | Reducción de material de empaque, eliminación de plásticos de un solo uso, contenido reciclado post-consumo (PCR). | Empaque 100% reciclado, compostable o libre de plástico innecesario. | Sobreempaque plástico no reciclable o envases mixtos no separables. |
| **TOTAL** | **100** | | | |

---

## 🧮 Fórmula de Cálculo

Sea $F$ el conjunto de todos los factores y $F_{\text{con\_datos}} \subseteq F$ el subconjunto de factores que cuentan con información verificada:

$$\text{Peso Con Datos} = \sum_{i \in F_{\text{con\_datos}}} w_i$$

1. Si $\text{Peso Con Datos} < 50$, el score global no es calculable:
   $$\text{TRACE SCORE} = \text{null}$$

2. Si $\text{Peso Con Datos} \ge 50$, el score es el promedio ponderado reescalado a 100:
   $$\text{TRACE SCORE} = \text{round}\left( \frac{\sum_{i \in F_{\text{con\_datos}}} (s_i \times w_i)}{\text{Peso Con Datos}} \right)$$

Donde:
- $w_i$ es el peso del factor $i$ (por ejemplo, 20 para materias primas).
- $s_i \in [0, 100]$ es la puntuación individual asignada al factor según los datos verificados. Si el dato es únicamente *declarado por el fabricante* sin auditoría externa, se aplica un factor de ponderación conservador del 85% sobre $s_i$.

---

## 🚦 Niveles de Confianza del Score

El nivel de confianza depende de la proporción de peso cubierto y la calidad de las fuentes:
- **Confianza Alta:** $\text{Peso Con Datos} \ge 75$ y al menos el 50% de las fuentes son de prioridad *Oficial*, *Certificación* o *Bases de Datos Especializadas*.
- **Confianza Media:** $50 \le \text{Peso Con Datos} < 75$.
- **Confianza Baja / Insuficiente:** $\text{Peso Con Datos} < 50$ (no se emite score numérico global).

---

## 📊 Interpretación del Resultado
- **81 – 100:** Desempeño excelente (Impacto ambiental muy favorable).
- **61 – 80:** Buen desempeño.
- **41 – 60:** Desempeño moderado.
- **21 – 40:** Desempeño desfavorable.
- **0 – 20:** Desempeño crítico (Impacto ambiental muy desfavorable).

---

## ⚖️ Aviso Legal Visible en la Aplicación
> *«TRACE SCORE es una evaluación analítica propia de sostenibilidad basada en datos públicos y no constituye una certificación ambiental oficial.»*
