package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.repository.HistoryRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar un producto individual del historial.
 */
class DeleteHistoryItemUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(barcode: String) {
        historyRepository.deleteScan(barcode)
    }
}
