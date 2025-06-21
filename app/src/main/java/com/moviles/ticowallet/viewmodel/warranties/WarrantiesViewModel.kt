package com.moviles.ticowallet.viewmodel.warranties

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.Warranty
import com.moviles.ticowallet.network.CreateWarrantyRequest
import com.moviles.ticowallet.network.UpdateWarrantyRequest
import com.moviles.ticowallet.network.WarrantyService
import com.moviles.ticowallet.network.WarrantyStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WarrantiesViewModel @Inject constructor(
    private val apiService: WarrantyService
) : ViewModel() {

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

    init {
        Log.d("WarrantiesViewModel", "ViewModel initialized")
        loadWarranties()
    }

    fun loadWarranties() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("WarrantiesViewModel", "Attempting to load warranties from API...")

                val response = apiService.getWarranties()
                _warranties.value = response
                Log.d("WarrantiesViewModel", "Warranties loaded successfully: ${response.size} items")

            } catch (e: Exception) {
                Log.e("WarrantiesViewModel", "Error loading warranties from API", e)

                Log.d("WarrantiesViewModel", "Using sample data as fallback")
                _warranties.value = getSampleWarranties()
                _error.value = "No se pudo conectar con el servidor. Mostrando datos de prueba."

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
                Log.d("WarrantiesViewModel", "Loading warranty by ID: $id")

                val warranty = apiService.getWarrantyById(id)
                _selectedWarranty.value = warranty
                Log.d("WarrantiesViewModel", "Warranty loaded successfully: ${warranty.name}")

            } catch (e: Exception) {
                Log.e("WarrantiesViewModel", "Error loading warranty by id: $id", e)

                _selectedWarranty.value = _warranties.value.find { it.idWarranty == id }
                _error.value = "Error al cargar garantía desde el servidor"

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
        icon: String?
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

                Log.d("WarrantiesViewModel", "Creating warranty: $name")

                val newWarranty = apiService.createWarranty(request)
                _warranties.value = _warranties.value + newWarranty
                Log.d("WarrantiesViewModel", "Warranty created successfully: ${newWarranty.name}")

            } catch (e: Exception) {
                Log.e("WarrantiesViewModel", "Error creating warranty", e)

                val newWarranty = createLocalWarranty(name, price, purchaseDate, expirationDate, icon)
                _warranties.value = _warranties.value + newWarranty
                _error.value = "Garantía creada localmente. Error de sincronización con servidor."

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
        icon: String?
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

                Log.d("WarrantiesViewModel", "Updating warranty: $id")
                val updatedWarranty = apiService.updateWarranty(id, request)

                _warranties.value = _warranties.value.map {
                    if (it.idWarranty == id) updatedWarranty else it
                }
                _selectedWarranty.value = updatedWarranty
                Log.d("WarrantiesViewModel", "Warranty updated successfully: ${updatedWarranty.name}")

            } catch (e: Exception) {
                Log.e("WarrantiesViewModel", "Error updating warranty", e)

                updateLocalWarranty(id, name, price, purchaseDate, expirationDate, icon)
                _error.value = "Garantía actualizada localmente. Error de sincronización con servidor."

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
                Log.d("WarrantiesViewModel", "Deleting warranty: $id")
                apiService.deleteWarranty(id)

                _warranties.value = _warranties.value.filter { it.idWarranty != id }
                _selectedWarranty.value = null
                Log.d("WarrantiesViewModel", "Warranty deleted successfully: $id")

            } catch (e: Exception) {
                Log.e("WarrantiesViewModel", "Error deleting warranty", e)

                _warranties.value = _warranties.value.filter { it.idWarranty != id }
                _selectedWarranty.value = null
                _error.value = "Garantía eliminada localmente. Error de sincronización con servidor."

            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun createLocalWarranty(
        name: String,
        price: Double,
        purchaseDate: Date,
        expirationDate: Date,
        icon: String?
    ): Warranty {
        val now = Date()
        val daysRemaining = ((expirationDate.time - now.time) / (1000 * 60 * 60 * 24)).toInt()

        return Warranty(
            idWarranty = (_warranties.value.maxByOrNull { it.idWarranty }?.idWarranty ?: 0) + 1,
            name = name,
            price = price,
            purchaseDate = purchaseDate,
            expirationDate = expirationDate,
            icon = icon,
            userId = 1,
            isExpired = expirationDate.before(now),
            daysRemaining = daysRemaining,
            createdAt = now
        )
    }

    private fun updateLocalWarranty(
        id: Int,
        name: String,
        price: Double,
        purchaseDate: Date,
        expirationDate: Date,
        icon: String?
    ) {
        val currentWarranty = _warranties.value.find { it.idWarranty == id }
        if (currentWarranty != null) {
            val now = Date()
            val daysRemaining = ((expirationDate.time - now.time) / (1000 * 60 * 60 * 24)).toInt()

            val updatedWarranty = currentWarranty.copy(
                name = name,
                price = price,
                purchaseDate = purchaseDate,
                expirationDate = expirationDate,
                icon = icon,
                isExpired = expirationDate.before(now),
                daysRemaining = daysRemaining
            )

            _warranties.value = _warranties.value.map {
                if (it.idWarranty == id) updatedWarranty else it
            }
            _selectedWarranty.value = updatedWarranty
        }
    }

    private fun getSampleWarranties(): List<Warranty> {
        return listOf(
            Warranty(
                idWarranty = 1,
                name = "MSI Cyborg 15 A12U",
                price = 777000.0,
                purchaseDate = Calendar.getInstance().apply { add(Calendar.YEAR, -1) }.time,
                expirationDate = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }.time,
                icon = "computer",
                userId = 1,
                isExpired = false,
                daysRemaining = 365,
                createdAt = Date()
            ),
            Warranty(
                idWarranty = 2,
                name = "Teclado Redragon K552",
                price = 45000.0,
                purchaseDate = Calendar.getInstance().apply { add(Calendar.MONTH, -6) }.time,
                expirationDate = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }.time,
                icon = "keyboard",
                userId = 1,
                isExpired = false,
                daysRemaining = 180,
                createdAt = Date()
            )
        )
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedWarranty() {
        _selectedWarranty.value = null
    }

    fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = apiService.getWarrantyStatistics()
                _statistics.value = stats
            } catch (e: Exception) {
                Log.e("WarrantiesViewModel", "Error loading statistics", e)
                calculateLocalStatistics()
            }
        }
    }

    private fun calculateLocalStatistics() {
        val warranties = _warranties.value
        val active = warranties.count { !it.isExpired }
        val expired = warranties.count { it.isExpired }
        val expiringSoon = warranties.count { !it.isExpired && it.daysRemaining <= 30 }

        _statistics.value = WarrantyStatistics(
            total = warranties.size,
            active = active,
            expired = expired,
            expiringSoon = expiringSoon,
            totalValue = warranties.sumOf { it.price },
            activeValue = warranties.filter { !it.isExpired }.sumOf { it.price },
            expiredValue = warranties.filter { it.isExpired }.sumOf { it.price }
        )
    }
}