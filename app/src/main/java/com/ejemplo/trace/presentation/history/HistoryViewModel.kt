package com.ejemplo.trace.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.usecase.ClearHistoryUseCase
import com.ejemplo.trace.domain.usecase.DeleteHistoryItemUseCase
import com.ejemplo.trace.domain.usecase.GetScanHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Content(val items: List<HistoryProduct>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    getScanHistoryUseCase: GetScanHistoryUseCase,
    private val deleteHistoryItemUseCase: DeleteHistoryItemUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = getScanHistoryUseCase()
        .map { items -> HistoryUiState.Content(items) as HistoryUiState }
        .catch { emit(HistoryUiState.Error(it.message ?: "Error al cargar historial")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState.Loading
        )

    fun deleteItem(barcode: String) {
        viewModelScope.launch {
            deleteHistoryItemUseCase(barcode)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
