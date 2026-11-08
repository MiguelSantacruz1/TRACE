package com.ejemplo.trace.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.trace.domain.model.HistoryProduct
import com.ejemplo.trace.domain.model.ProductLookupResult
import com.ejemplo.trace.domain.usecase.InvestigateProductUseCase
import com.ejemplo.trace.domain.usecase.SaveScanToHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductResultUiState {
    data object Loading : ProductResultUiState
    data class Success(val product: ProductLookupResult) : ProductResultUiState
    data class NotFound(val barcode: String) : ProductResultUiState
    data class Error(val message: String) : ProductResultUiState
}

@HiltViewModel
class ProductResultViewModel @Inject constructor(
    private val investigateProductUseCase: InvestigateProductUseCase,
    private val saveScanToHistoryUseCase: SaveScanToHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val barcode: String = savedStateHandle.get<String>("barcode") ?: ""

    private val _uiState = MutableStateFlow<ProductResultUiState>(ProductResultUiState.Loading)
    val uiState: StateFlow<ProductResultUiState> = _uiState.asStateFlow()

    init {
        if (barcode.isNotBlank()) {
            loadProduct(barcode)
        } else {
            _uiState.value = ProductResultUiState.Error("Código de barras no especificado")
        }
    }

    fun retry() {
        if (barcode.isNotBlank()) {
            loadProduct(barcode)
        }
    }

    private fun loadProduct(code: String) {
        viewModelScope.launch {
            _uiState.value = ProductResultUiState.Loading
            val result = investigateProductUseCase(code)
            result.fold(
                onSuccess = { product ->
                    _uiState.value = ProductResultUiState.Success(product)
                    saveScanToHistoryUseCase(
                        HistoryProduct(
                            barcode = product.barcode,
                            name = product.name,
                            brand = product.brand ?: "",
                            imageUrl = product.imageUrl,
                            scannedAt = System.currentTimeMillis()
                        )
                    )
                },
                onFailure = { error ->
                    if (error is NoSuchElementException) {
                        _uiState.value = ProductResultUiState.NotFound(code)
                    } else {
                        _uiState.value = ProductResultUiState.Error(
                            error.message ?: "Ocurrió un error al investigar el producto"
                        )
                    }
                }
            )
        }
    }
}
