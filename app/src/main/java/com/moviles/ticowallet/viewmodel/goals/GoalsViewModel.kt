package com.moviles.ticowallet.viewmodel.goals

import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.Goal // Importa el modelo Goal actualizado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar // Para crear fechas de ejemplo
import java.util.Date

class GoalsViewModel : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    init {
        loadGoals()
    }

    fun loadGoals() {
        viewModelScope.launch {
            // --- Simulación de carga de datos ---
            // TODO: Reemplaza esto con tu lógica real de obtención de datos
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, 3) // Fecha objetivo a 3 meses
            val futureDate = calendar.time
            calendar.add(Calendar.MONTH, -6) // Fecha pasada
            val pastDate = calendar.time


            _goals.value = listOf(
                Goal(
                    id = "1",
                    name = "Viaje a la playa",
                    quantity = 500000.0, // Objetivo
                    goalDate = futureDate, // Fecha futura
                    currentQuantity = 150000.0, // Ahorrado
                    icon = "beach_access", // Nombre de icono de Material Icons (ejemplo)
                    state = "Activo",
                    note = "Ahorro para vacaciones en Guanacaste"
                ),
                Goal(
                    id = "2",
                    name = "Nuevo celular",
                    quantity = 800000.0,
                    goalDate = futureDate,
                    currentQuantity = 750000.0, // Casi completado
                    icon = "smartphone",
                    state = "Activo",
                    note = "Ahorro para cambiar de teléfono"
                ),
                Goal(
                    id = "3",
                    name = "Regalo Navidad",
                    quantity = 100000.0,
                    goalDate = pastDate, // Fecha pasada
                    currentQuantity = 100000.0, // Completado
                    icon = "card_giftcard",
                    state = "Completado", // Estado diferente
                    note = "Comprar regalos diciembre"
                ),
                Goal(
                    id = "4",
                    name = "Entrada de casa",
                    quantity = 5000000.0,
                    goalDate = futureDate, // Fecha muy futura
                    currentQuantity = 250000.0,
                    icon = "home",
                    state = "Activo",
                    note = "Prima para la casa propia"
                )
            )
            // --- Fin de simulación ---
        }
    }

    fun loadGoalById(id: String?) {
        viewModelScope.launch {
            _selectedGoal.value = _goals.value.find { it.id == id }
        }
    }

    fun clearSelectedGoal() {
        _selectedGoal.value = null
    }

    // Helper para obtener un Icono de Material basado en el nombre (simplificado)
    // TODO: Implementa una lógica más robusta para mapear nombres a íconos o cargar URLs

    fun getIconVector(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
        return when (iconName.lowercase()) {
            "beach_access" -> androidx.compose.material.icons.Icons.Filled.BeachAccess
            "smartphone" -> androidx.compose.material.icons.Icons.Filled.Smartphone
            "card_giftcard" -> androidx.compose.material.icons.Icons.Filled.CardGiftcard
            "home" -> androidx.compose.material.icons.Icons.Filled.Home
            "directions_car" -> androidx.compose.material.icons.Icons.Filled.DirectionsCar // Ejemplo adicional
            "savings" -> androidx.compose.material.icons.Icons.Filled.Savings // Ejemplo adicional
            // Añade más mapeos según los iconos que uses
            else -> androidx.compose.material.icons.Icons.Filled.Flag // Icono por defecto
        }
    }
}