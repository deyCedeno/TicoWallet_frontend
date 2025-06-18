// viewmodel/exchangerate/ExchangeRateViewModel.kt
package com.moviles.ticowallet.viewmodel.exchangerate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.network.ExternalRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ExchangeRateUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,

    // USD rates
    val usdCompra: Double = 0.0,
    val usdVenta: Double = 0.0,

    // EUR rates
    val eurCompra: Double = 0.0,
    val eurVenta: Double = 0.0,

    val lastUpdated: String = ""
)

class ExchangeRateViewModel : ViewModel() {
    private val bccrService = ExternalRetrofitClient.bccrService
    private val exchangeRateService = ExternalRetrofitClient.exchangeRateService

    private val _uiState = MutableStateFlow(ExchangeRateUiState())
    val uiState: StateFlow<ExchangeRateUiState> = _uiState.asStateFlow()

    init {
        loadExchangeRates()
    }

    fun loadExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Obtener USD del BCCR
                val bccrResponse = bccrService.getCurrentExchangeRate()

                if (bccrResponse.isSuccessful && bccrResponse.body() != null) {
                    val bccrData = bccrResponse.body()!!

                    // Obtener EUR de ExchangeRate-API
                    var eurToUsd = 0.85 // Fallback rate
                    try {
                        val eurResponse = exchangeRateService.getExchangeRatesFromUSD()
                        if (eurResponse.isSuccessful && eurResponse.body() != null) {
                            eurToUsd = eurResponse.body()?.rates?.get("EUR") ?: 0.85
                        }
                    } catch (e: Exception) {
                        // Usar tasa fallback para EUR
                    }

                    // Calcular tasas EUR
                    val eurCompraRate = bccrData.compra * eurToUsd
                    val eurVentaRate = bccrData.venta * eurToUsd

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        usdCompra = bccrData.compra,
                        usdVenta = bccrData.venta,
                        eurCompra = eurCompraRate,
                        eurVenta = eurVentaRate,
                        lastUpdated = formatDate(bccrData.fecha),
                        errorMessage = null
                    )

                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar tipos de cambio del BCCR"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun refreshExchangeRates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)

            try {
                // Obtener USD del BCCR
                val bccrResponse = bccrService.getCurrentExchangeRate()

                if (bccrResponse.isSuccessful && bccrResponse.body() != null) {
                    val bccrData = bccrResponse.body()!!

                    // Obtener EUR de ExchangeRate-API
                    var eurToUsd = 0.85 // Fallback rate
                    try {
                        val eurResponse = exchangeRateService.getExchangeRatesFromUSD()
                        if (eurResponse.isSuccessful && eurResponse.body() != null) {
                            eurToUsd = eurResponse.body()?.rates?.get("EUR") ?: 0.85
                        }
                    } catch (e: Exception) {
                        // Usar tasa fallback para EUR
                    }

                    // Calcular tasas EUR
                    val eurCompraRate = bccrData.compra * eurToUsd
                    val eurVentaRate = bccrData.venta * eurToUsd

                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        usdCompra = bccrData.compra,
                        usdVenta = bccrData.venta,
                        eurCompra = eurCompraRate,
                        eurVenta = eurVentaRate,
                        lastUpdated = formatDate(bccrData.fecha),
                        errorMessage = null
                    )

                } else {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = "Error al actualizar tipos de cambio"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}