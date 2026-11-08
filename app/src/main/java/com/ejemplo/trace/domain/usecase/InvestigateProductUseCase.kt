package com.ejemplo.trace.domain.usecase

import com.ejemplo.trace.domain.model.ProductLookupResult
import com.ejemplo.trace.domain.repository.ProductRepository
import com.ejemplo.trace.domain.scanner.BarcodeValidator
import javax.inject.Inject

/**
 * Caso de uso atómico para investigar un producto.
 * Valida el formato numérico del código de barras antes de emitir peticiones de red.
 */
class InvestigateProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(barcode: String): Result<ProductLookupResult> {
        val trimmed = barcode.trim()
        if (!BarcodeValidator.isValid(trimmed)) {
            return Result.failure(IllegalArgumentException("Código de barras inválido o con formato incorrecto"))
        }
        return repository.investigateProduct(trimmed)
    }
}
