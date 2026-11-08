package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.repository.HistoryRepository
import javax.inject.Inject

/**
 * Caso de uso para limpiar completamente el historial de consultas del usuario.
 */
class ClearHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke() {
        historyRepository.clearHistory()
    }
}
