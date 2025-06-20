package com.moviles.ticowallet.viewmodel.scheduledPayment

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.*
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for managing scheduled payment operations
 * Handles CRUD operations and dropdown data for the UI
 */
class ScheduledPaymentViewModel(application: Application) : AndroidViewModel(application) {

    // State for scheduled payments list
    private val _allScheduledPayments = MutableStateFlow<List<ScheduledPayment>>(emptyList())
    val allScheduledPayments: StateFlow<List<ScheduledPayment>> = _allScheduledPayments.asStateFlow()

    // State for selected scheduled payment (detail view)
    private val _selectedScheduledPayment = MutableStateFlow<ScheduledPayment?>(null)
    val selectedScheduledPayment: StateFlow<ScheduledPayment?> = _selectedScheduledPayment.asStateFlow()

    // State for user accounts (dropdown)
    private val _userAccounts = MutableStateFlow<List<Account>>(emptyList())
    val userAccounts: StateFlow<List<Account>> = _userAccounts.asStateFlow()

    // State for categories (dropdown)
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingDropdowns = MutableStateFlow(false)
    val isLoadingDropdowns: StateFlow<Boolean> = _isLoadingDropdowns.asStateFlow()

    // Success states
    private val _createSuccess = MutableStateFlow(false)
    val createSuccess: StateFlow<Boolean> = _createSuccess.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    // Error handling
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Load all scheduled payments for the authenticated user
     * This function uses JWT token automatically via AuthInterceptor
     */
    fun loadScheduledPayments() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Loading scheduled payments for authenticated user...")

                val response = try {
                    RetrofitInstance.api.getScheduledPayments()
                } catch (e: Exception) {
                    if (e.message?.contains("null") == true) {
                        // Si es error de null, asumimos lista vacía
                        emptyList<ScheduledPayment>()
                    } else {
                        throw e // Re-lanzar si es otro tipo de error
                    }
                }

                _allScheduledPayments.value = response
                Log.i(TAG, "Successfully loaded ${response.size} scheduled payments")
                _error.value = null

            } catch (e: HttpException) {
                val errorMessage = handleHttpException(e)
                _error.value = errorMessage
                Log.e(TAG, "$errorMessage, Code: ${e.code()}")

            } catch (e: Exception) {
                val errorMessage = "Error de conexión: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)

            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * Load accounts and categories for dropdown menus
     * Should be called when navigating to create/edit screens
     */
    fun loadDropdownData() {
        viewModelScope.launch {
            _isLoadingDropdowns.value = true
            _error.value = null

            try {
                Log.d(TAG, "Loading dropdown data...")

                // Load user accounts and categories in parallel
                val accounts = async { RetrofitInstance.api.getUserAccounts() }
                val categories = async { RetrofitInstance.api.getCategories() }

                val accountsResult = accounts.await()
                val categoriesResult = categories.await()

                _userAccounts.value = accountsResult
                _categories.value = categoriesResult

                Log.i(TAG, "Loaded ${accountsResult.size} accounts and ${categoriesResult.size} categories")

            } catch (e: HttpException) {
                val errorMessage = handleHttpException(e)
                _error.value = errorMessage
                Log.e(TAG, "Error loading dropdown data: $errorMessage")

            } catch (e: Exception) {
                val errorMessage = "Error cargando datos: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)

            } finally {
                _isLoadingDropdowns.value = false
            }
        }
    }

    /**
     * Load specific scheduled payment by ID
     */
    fun loadScheduledPaymentById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Loading scheduled payment with id: $id")
                Log.d(TAG, "Current AUTH_TOKEN: '${com.moviles.ticowallet.common.Constants.AUTH_TOKEN}'")
                Log.d(TAG, "Token length: ${com.moviles.ticowallet.common.Constants.AUTH_TOKEN.length}")

                val response = RetrofitInstance.api.getScheduledPaymentById(id)
                _selectedScheduledPayment.value = response
                Log.i(TAG, "Loaded scheduled payment: $response")
            } catch (e: HttpException) {
                val errorMessage = handleHttpException(e)
                _error.value = errorMessage
                Log.e(TAG, "HTTP Error loading scheduled payment: $errorMessage")
                Log.e(TAG, "Response code: ${e.code()}")
                Log.e(TAG, "Response body: ${e.response()?.errorBody()?.string()}")
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, "Exception loading scheduled payment: $errorMessage", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Create new scheduled payment
     */
    fun createScheduledPayment(scheduledPayment: CreateScheduledPaymentDto) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _createSuccess.value = false

            try {
                Log.i(TAG, "Creating scheduled payment: $scheduledPayment")
                val response = RetrofitInstance.api.createScheduledPayment(scheduledPayment)

                if (response.isSuccessful) {
                    Log.i(TAG, "Scheduled payment created successfully")
                    _createSuccess.value = true
                    loadScheduledPayments() // Reload list
                } else {
                    val errorMessage = "Error al crear: ${response.code()}"
                    _error.value = errorMessage
                    Log.e(TAG, errorMessage)
                }

            } catch (e: HttpException) {
                val errorMessage = handleHttpException(e)
                _error.value = errorMessage
                Log.e(TAG, "Error creating scheduled payment: $errorMessage")
            } catch (e: Exception) {
                val errorMessage = "Error al crear: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update existing scheduled payment
     */
    fun updateScheduledPayment(id: Int, scheduledPayment: CreateScheduledPaymentDto) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _updateSuccess.value = false

            try {
                Log.i(TAG, "Updating scheduled payment: $scheduledPayment")
                val response = RetrofitInstance.api.updateScheduledPayment(id, scheduledPayment)

                if (response.isSuccessful) {
                    Log.i(TAG, "Scheduled payment updated successfully")
                    _updateSuccess.value = true
                    loadScheduledPayments() // Reload list
                } else {
                    val errorMessage = "Error al actualizar: ${response.code()}"
                    _error.value = errorMessage
                    Log.e(TAG, errorMessage)
                }

            } catch (e: HttpException) {
                val errorMessage = handleHttpException(e)
                _error.value = errorMessage
                Log.e(TAG, "Error updating scheduled payment: $errorMessage")
            } catch (e: Exception) {
                val errorMessage = "Error al actualizar: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Delete scheduled payment
     */
    fun deleteScheduledPayment(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _deleteSuccess.value = false

            try {
                Log.i(TAG, "Deleting scheduled payment with id: $id")
                val response = RetrofitInstance.api.deleteScheduledPayment(id)

                if (response.isSuccessful) {
                    Log.i(TAG, "Scheduled payment deleted successfully")
                    _deleteSuccess.value = true
                    loadScheduledPayments() // Reload list
                } else {
                    val errorMessage = "Error al eliminar: ${response.code()}"
                    _error.value = errorMessage
                    Log.e(TAG, errorMessage)
                }

            } catch (e: HttpException) {
                val errorMessage = handleHttpException(e)
                _error.value = errorMessage
                Log.e(TAG, "Error deleting scheduled payment: $errorMessage")
            } catch (e: Exception) {
                val errorMessage = "Error al eliminar: ${e.message}"
                _error.value = errorMessage
                Log.e(TAG, errorMessage, e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Reset functions
    fun clearSelectedScheduledPayment() {
        _selectedScheduledPayment.value = null
    }

    fun resetCreateSuccess() {
        _createSuccess.value = false
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }

    // Utility functions for UI formatting
    fun formatCurrency(amount: Double): String {
        return "₡${String.format("%,.0f", amount)}"
    }

    fun formatDateString(dateString: String): String {
        return try {
            // Assuming ISO date format from backend
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString // Return original if parsing fails
        }
    }

    fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    fun getFrequencyText(frequency: String): String {
        return when (frequency.lowercase()) {
            "mensual" -> "Mensual"
            "semanal" -> "Semanal"
            "diario" -> "Diario"
            "anual" -> "Anual"
            else -> frequency
        }
    }

    // Helper functions
    private fun handleHttpException(e: HttpException): String {
        val errorBody = e.response()?.errorBody()?.string()
        return when (e.code()) {
            401 -> "No autorizado - Token inválido o expirado"
            403 -> "Acceso denegado"
            404 -> "Servicio no encontrado"
            500 -> "Error interno del servidor"
            else -> "Error HTTP ${e.code()}: ${e.message()}"
        }
    }

    companion object {
        private const val TAG = "ScheduledPaymentVM"
    }
}