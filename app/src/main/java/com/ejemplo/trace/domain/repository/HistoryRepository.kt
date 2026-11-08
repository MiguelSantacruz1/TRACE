package com.ejemplo.trace.domain.repository

import com.ejemplo.trace.domain.model.HistoryProduct
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para el historial local de escaneos.
 */
interface HistoryRepository {

    /**
     * Flujo reactivo de productos escaneados ordenados del más reciente al más antiguo.
     */
    fun getHistory(): Flow<List<HistoryProduct>>

    /**
     * Obtiene un producto individual por código de barras.
     */
    suspend fun getProduct(barcode: String): HistoryProduct?

    /**
     * Guarda o actualiza un registro en el historial.
     */
    suspend fun saveScan(product: HistoryProduct, rawJson: String? = null)

    /**
     * Elimina un elemento del historial por su código de barras.
     */
    suspend fun deleteScan(barcode: String)

    /**
     * Elimina todos los elementos del historial.
     */
    suspend fun clearHistory()
}
