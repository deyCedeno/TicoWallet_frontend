// viewmodel/exchangerate/ExchangeRateViewModel.kt
package com.moviles.ticowallet.viewmodel.exchangerate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.network.ExternalRetrofitClient
import com.moviles.ticowallet.network.RetrofitInstance
import com.moviles.ticowallet.models.CreateExchangeRateDto
import com.moviles.ticowallet.models.BCCRExchangeRate
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

                val bccrResponse = bccrService.getCurrentExchangeRate()

                if (bccrResponse.isSuccessful && bccrResponse.body() != null) {
                    val bccrData = bccrResponse.body()!!


                    var eurToUsd = 0.85 // Fallback rate
                    try {
                        val eurResponse = exchangeRateService.getExchangeRatesFromUSD()
                        if (eurResponse.isSuccessful && eurResponse.body() != null) {
                            eurToUsd = eurResponse.body()?.rates?.get("EUR") ?: 0.85
                        }
                    } catch (e: Exception) {

                    }


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


                    saveToBackend(bccrData, eurToUsd)

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

                val bccrResponse = bccrService.getCurrentExchangeRate()

                if (bccrResponse.isSuccessful && bccrResponse.body() != null) {
                    val bccrData = bccrResponse.body()!!

                    var eurToUsd = 0.85
                    try {
                        val eurResponse = exchangeRateService.getExchangeRatesFromUSD()
                        if (eurResponse.isSuccessful && eurResponse.body() != null) {
                            eurToUsd = eurResponse.body()?.rates?.get("EUR") ?: 0.85
                        }
                    } catch (e: Exception) {
                        // Usar tasa fallback para EUR
                    }


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


                    saveToBackend(bccrData, eurToUsd)

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

    // Guardar en base de datos para cache
    private suspend fun saveToBackend(bccrData: BCCRExchangeRate, eurToUsd: Double) {
        try {
            val apiService = RetrofitInstance.apiServiceExchangeRate


            apiService.createExchangeRate(
                CreateExchangeRateDto(
                    fromCurrency = "USD",
                    toCurrency = "CRC",
                    rate = bccrData.compra
                )
            )


            apiService.createExchangeRate(
                CreateExchangeRateDto(
                    fromCurrency = "USD",
                    toCurrency = "CRC",
                    rate = bccrData.venta
                )
            )


            apiService.createExchangeRate(
                CreateExchangeRateDto(
                    fromCurrency = "EUR",
                    toCurrency = "CRC",
                    rate = bccrData.compra * eurToUsd
                )
            )


            apiService.createExchangeRate(
                CreateExchangeRateDto(
                    fromCurrency = "EUR",
                    toCurrency = "CRC",
                    rate = bccrData.venta * eurToUsd
                )
            )

        } catch (e: Exception) {
            // Silencioso - no es crítico si falla el guardado
        }
    }
}