package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.repository.HistoryRepository
import javax.inject.Inject

/**
 * Caso de uso para registrar o actualizar un escaneo en el historial local del usuario.
 */
class SaveScanToHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(product: HistoryProduct, rawJson: String? = null) {
        historyRepository.saveScan(product, rawJson)
    }
}
