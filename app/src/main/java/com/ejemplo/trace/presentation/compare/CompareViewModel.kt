package com.ejemplo.trace.presentation.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.model.ProductComparison
import com.ejemplo.trace.domain.usecase.CompareProductsUseCase
import com.ejemplo.trace.domain.usecase.GetScanHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CompareUiState {
    data object Loading : CompareUiState
    data object EmptyHistory : CompareUiState
    data class Ready(
        val comparison: ProductComparison,
        val availableProducts: List<HistoryProduct>
    ) : CompareUiState
    data class Error(val message: String) : CompareUiState
}

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getScanHistoryUseCase: GetScanHistoryUseCase,
    private val compareProductsUseCase: CompareProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CompareUiState>(CompareUiState.Loading)
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    private var currentProducts: List<HistoryProduct> = emptyList()

    init {
        loadHistoryAndCompare()
    }

    private fun loadHistoryAndCompare() {
        viewModelScope.launch {
            getScanHistoryUseCase().collectLatest { history ->
                currentProducts = history
                if (history.size < 2) {
                    _uiState.value = CompareUiState.EmptyHistory
                } else {
                    val comp = compareProductsUseCase(history[0], history[1])
                    _uiState.value = CompareUiState.Ready(
                        comparison = comp,
                        availableProducts = history
                    )
                }
            }
        }
    }

    fun selectProducts(barcodeA: String, barcodeB: String) {
        val prodA = currentProducts.find { it.barcode == barcodeA }
        val prodB = currentProducts.find { it.barcode == barcodeB }

        if (prodA != null && prodB != null) {
            val comp = compareProductsUseCase(prodA, prodB)
            _uiState.value = CompareUiState.Ready(
                comparison = comp,
                availableProducts = currentProducts
            )
        } else {
            _uiState.value = CompareUiState.Error("No se pudieron encontrar los productos seleccionados")
        }
    }
}
