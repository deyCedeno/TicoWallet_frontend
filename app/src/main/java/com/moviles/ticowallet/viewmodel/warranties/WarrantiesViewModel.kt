package com.moviles.ticowallet.viewmodel.warranties

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.common.Constants
import com.moviles.ticowallet.models.Warranty
import com.moviles.ticowallet.network.CreateWarrantyRequest
import com.moviles.ticowallet.network.RetrofitInstance
import com.moviles.ticowallet.network.UpdateWarrantyRequest
import com.moviles.ticowallet.network.WarrantyService
import com.moviles.ticowallet.network.WarrantyStatistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class WarrantiesViewModel : ViewModel() {

    private val apiService: WarrantyService = RetrofitInstance.warrantyApi

    private val _warranties = MutableStateFlow<List<Warranty>>(emptyList())
    val warranties: StateFlow<List<Warranty>> = _warranties.asStateFlow()

    private val _selectedWarranty = MutableStateFlow<Warranty?>(null)
    val selectedWarranty: StateFlow<Warranty?> = _selectedWarranty.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _statistics = MutableStateFlow<WarrantyStatistics?>(null)
    val statistics: StateFlow<WarrantyStatistics?> = _statistics.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        private const val TAG = "WarrantiesViewModel"
    }

    init {
        Log.d(TAG, "ViewModel initialized")
        Log.d(TAG, "AUTH_TOKEN: '${Constants.AUTH_TOKEN}'")
        Log.d(TAG, "API_BASE_URL: '${Constants.API_BASE_URL}'")
        loadWarranties()
    }

    fun loadWarranties() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Loading warranties from API...")
                Log.d(TAG, "Token being used: '${Constants.AUTH_TOKEN}'")

                if (Constants.AUTH_TOKEN.isEmpty()) {
                    Log.w(TAG, "AUTH_TOKEN is empty!")
                    _error.value = "Token de autenticación no encontrado. Inicia sesión nuevamente."
                    _warranties.value = emptyList()
                    return@launch
                }

                val response = apiService.getWarranties()
                Log.d(TAG, "Raw response size: ${response.size}")

                response.forEachIndexed { index, warranty ->
                    Log.d(TAG, "Warranty $index: id=${warranty.idWarranty}, name=${warranty.name}, " +
                            "price=${warranty.price}, expired=${warranty.isExpired}, " +
                            "days=${warranty.daysRemaining}, icon=${warranty.icon}")
                }

                _warranties.value = response
                Log.d(TAG, "Warranties loaded successfully: ${response.size} items")

            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Error loading warranties", e)
                Log.e(TAG, "Error code: ${e.code()}")

                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "Error body: $errorBody")
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not read error body", ex)
                }

                val errorMessage = when (e.code()) {
                    401 -> "No autorizado. Por favor, inicia sesión nuevamente."
                    403 -> "Acceso denegado"
                    404 -> "Servicio de garantías no encontrado"
                    500 -> "Error interno del servidor"
                    else -> "Error HTTP ${e.code()}: ${e.message()}"
                }
                _error.value = errorMessage
                _warranties.value = emptyList()

            } catch (e: com.google.gson.JsonSyntaxException) {
                Log.e(TAG, "JSON parsing error", e)
                _error.value = "Error al procesar la respuesta del servidor"
                _warranties.value = emptyList()

            } catch (e: java.net.SocketTimeoutException) {
                Log.e(TAG, "Network timeout", e)
                _error.value = "Tiempo de espera agotado. Verifica tu conexión."
                _warranties.value = emptyList()

            } catch (e: java.net.ConnectException) {
                Log.e(TAG, "Connection error", e)
                _error.value = "No se puede conectar al servidor. ¿Está ejecutándose?"
                _warranties.value = emptyList()

            } catch (e: java.net.UnknownHostException) {
                Log.e(TAG, "Unknown host error", e)
                _error.value = "Error de conexión. Verifica tu internet."
                _warranties.value = emptyList()

            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error loading warranties", e)
                _error.value = "Error inesperado: ${e.message ?: "Error desconocido"}"
                _warranties.value = emptyList()

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadWarrantyById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Loading warranty by ID: $id")

                val warranty = apiService.getWarrantyById(id)
                _selectedWarranty.value = warranty
                Log.d(TAG, "Warranty loaded successfully: ${warranty.name}")

            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Error loading warranty by id: $id", e)

                _selectedWarranty.value = _warranties.value.find { it.idWarranty == id }

                val errorMessage = when (e.code()) {
                    404 -> "Garantía no encontrada."
                    401 -> "No autorizado."
                    else -> "Error al cargar garantía: HTTP ${e.code()}"
                }
                _error.value = errorMessage

            } catch (e: Exception) {
                Log.e(TAG, "Exception loading warranty by id: $id", e)

                _selectedWarranty.value = _warranties.value.find { it.idWarranty == id }
                _error.value = "Error al cargar garantía: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createWarranty(
        name: String,
        price: Double,
        purchaseDate: Date,
        expirationDate: Date,
        icon: String?,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val request = CreateWarrantyRequest(
                    name = name,
                    price = price,
                    purchaseDate = dateFormatter.format(purchaseDate),
                    expirationDate = dateFormatter.format(expirationDate),
                    icon = icon
                )

                Log.d(TAG, "Creating warranty: $name")
                Log.d(TAG, "Request: $request")

                val newWarranty = apiService.createWarranty(request)

                loadWarranties()
                Log.d(TAG, "Warranty created successfully: ${newWarranty.name}")

                onSuccess?.invoke()

            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Error creating warranty", e)
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e(TAG, "Error body: $errorBody")
                } catch (ex: Exception) {
                    Log.e(TAG, "Could not read error body", ex)
                }

                val errorMessage = when (e.code()) {
                    400 -> "Datos inválidos. Verifica la información."
                    401 -> "No autorizado. Inicia sesión nuevamente."
                    else -> "Error al crear garantía: HTTP ${e.code()}"
                }
                _error.value = errorMessage

            } catch (e: Exception) {
                Log.e(TAG, "Exception creating warranty", e)
                _error.value = "Error al crear garantía: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateWarranty(
        id: Int,
        name: String,
        price: Double,
        purchaseDate: Date,
        expirationDate: Date,
        icon: String?,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val request = UpdateWarrantyRequest(
                    name = name,
                    price = price,
                    purchaseDate = dateFormatter.format(purchaseDate),
                    expirationDate = dateFormatter.format(expirationDate),
                    icon = icon
                )

                Log.d(TAG, "Updating warranty: $id")
                val updatedWarranty = apiService.updateWarranty(id, request)

                loadWarranties()
                _selectedWarranty.value = updatedWarranty
                Log.d(TAG, "Warranty updated successfully: ${updatedWarranty.name}")

                onSuccess?.invoke()

            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Error updating warranty", e)

                val errorMessage = when (e.code()) {
                    404 -> "Garantía no encontrada."
                    400 -> "Datos inválidos."
                    401 -> "No autorizado."
                    else -> "Error al actualizar garantía: HTTP ${e.code()}"
                }
                _error.value = errorMessage

            } catch (e: Exception) {
                Log.e(TAG, "Exception updating warranty", e)
                _error.value = "Error al actualizar garantía: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteWarranty(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Deleting warranty: $id")

                val response = apiService.deleteWarranty(id)

                if (response.isSuccessful) {
                    loadWarranties()
                    _selectedWarranty.value = null
                    Log.d(TAG, "Warranty deleted successfully: $id")
                } else {
                    throw HttpException(response)
                }

            } catch (e: HttpException) {
                Log.e(TAG, "HTTP Error deleting warranty", e)

                val errorMessage = when (e.code()) {
                    404 -> "Garantía no encontrada."
                    401 -> "No autorizado."
                    else -> "Error al eliminar garantía: HTTP ${e.code()}"
                }
                _error.value = errorMessage

            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting warranty", e)
                _error.value = "Error al eliminar garantía: ${e.message}"

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedWarranty() {
        _selectedWarranty.value = null
    }

    fun refreshWarranties() {
        loadWarranties()
    }
}