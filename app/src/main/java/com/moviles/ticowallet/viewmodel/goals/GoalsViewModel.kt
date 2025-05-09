package com.moviles.ticowallet.viewmodel.goals

import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Smartphone
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


object GoalStatus {
    const val ACTIVE = "Activo"
    const val PAUSED = "Pausado"
    const val ACHIEVED = "Conseguido"
}

class GoalsViewModel : ViewModel() {

    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())


    val activeGoals: StateFlow<List<Goal>> = _allGoals.map { goals ->
        goals.filter { it.state == GoalStatus.ACTIVE }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    val pausedGoals: StateFlow<List<Goal>> = _allGoals.map { goals ->
        goals.filter { it.state == GoalStatus.PAUSED }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())

    val achievedGoals: StateFlow<List<Goal>> = _allGoals.map { goals ->
        goals.filter { it.state == GoalStatus.ACHIEVED }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())


    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    init {
        loadGoals()
    }

    fun loadGoals() {
        viewModelScope.launch {

            // TODO: Reemplaza esto con tu lógica real de obtención de datos desde la BD/API
            val calendar = Calendar.getInstance()
            val futureDate: Date
            val pastDate: Date

            calendar.add(Calendar.MONTH, 3)
            futureDate = calendar.time
            calendar.add(Calendar.MONTH, -6)
            pastDate = calendar.time


            _allGoals.value = listOf(
                Goal(
                    id = "1",
                    name = "Viaje a la playa",
                    quantity = 500000.0,
                    goalDate = futureDate,
                    currentQuantity = 150000.0,
                    icon = "beach_access",
                    state = GoalStatus.ACTIVE,
                    note = "Ahorro para vacaciones en Guanacaste"
                ),
                Goal(
                    id = "2",
                    name = "Nuevo celular",
                    quantity = 800000.0,
                    goalDate = futureDate,
                    currentQuantity = 750000.0,
                    icon = "smartphone",
                    state = GoalStatus.ACTIVE,
                    note = "Ahorro para cambiar de teléfono"
                ),
                Goal(
                    id = "3",
                    name = "Regalo Navidad",
                    quantity = 100000.0,
                    goalDate = pastDate,
                    currentQuantity = 100000.0,
                    icon = "card_giftcard",
                    state = GoalStatus.ACHIEVED,
                    note = "Comprar regalos diciembre"
                ),
                Goal(
                    id = "4",
                    name = "Entrada de casa",
                    quantity = 5000000.0,
                    goalDate = futureDate,
                    currentQuantity = 250000.0,
                    icon = "home",
                    state = GoalStatus.ACTIVE,
                    note = "Prima para la casa propia"
                ),

                Goal(
                    id = "5",
                    name = "Curso de Cocina",
                    quantity = 150000.0,
                    goalDate = futureDate,
                    currentQuantity = 25000.0,
                    icon = "home",
                    state = GoalStatus.PAUSED,
                    note = "Curso de cocina temporalmente en espera"
                )
            )

        }
    }

    fun loadGoalById(id: String?) {
        viewModelScope.launch {

            _selectedGoal.value = _allGoals.value.find { it.id == id }
        }
    }

    fun clearSelectedGoal() {
        _selectedGoal.value = null
    }

    fun getIconVector(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
        return when (iconName.lowercase()) {
            "beach_access" -> androidx.compose.material.icons.Icons.Filled.BeachAccess
            "smartphone" -> androidx.compose.material.icons.Icons.Filled.Smartphone
            "card_giftcard" -> androidx.compose.material.icons.Icons.Filled.CardGiftcard
            "home" -> androidx.compose.material.icons.Icons.Filled.Home
            "directions_car" -> androidx.compose.material.icons.Icons.Filled.DirectionsCar
            "savings" -> androidx.compose.material.icons.Icons.Filled.Savings
            else -> androidx.compose.material.icons.Icons.Filled.Flag
        }
    }
}