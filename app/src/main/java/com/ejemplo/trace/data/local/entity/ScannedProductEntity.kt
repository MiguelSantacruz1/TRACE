package com.ejemplo.trace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de persistencia local para productos escaneados (Prompt Maestro - Sección 8).
 *
 * REGLA ESTRICTA DE ARQUITECTURA:
 * Esta tabla NO representa una base de datos propietaria fija ni un catálogo preinstalado.
 * Es únicamente la libreta local del usuario para:
 * 1. Recordar productos que ya ha escaneado físicamente.
 * 2. Mantener una copia efímera en caché (TTL) para evitar consumo excesivo de red.
 */
@Entity(tableName = "scanned_products")
data class ScannedProductEntity(
    @PrimaryKey
    val barcode: String,
    val name: String,
    val brand: String,
    val imageUrl: String? = null,
    val scannedAt: Long = System.currentTimeMillis(),
    val lastScore: Int? = null,
    val confidenceLevel: String = "",
    val rawJson: String? = null,
    val cacheTtlMillis: Long = 86_400_000L // 24 horas por defecto
)
