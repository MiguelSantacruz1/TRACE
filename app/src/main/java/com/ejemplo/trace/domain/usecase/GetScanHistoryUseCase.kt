package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el historial reactivo de productos escaneados.
 */
class GetScanHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<HistoryProduct>> {
        return historyRepository.getHistory()
    }
}
