package com.moviles.ticowallet.viewmodel.goals

import android.app.Application
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.DAO.CreateGoalRequestDto
import com.moviles.ticowallet.DAO.UpdateGoalRequestDto
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

object GoalStatus {
    const val ACTIVE = "Activo"
    const val PAUSED = "Pausado"
    const val ACHIEVED = "Conseguido"
}

data class GoalsUiState(
    val isLoading: Boolean = false,
    val isCreatingGoal: Boolean = false,
    val isUpdatingGoal: Boolean = false,
    val isDeletingGoal: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitInstance.apiServiceGoals

    private val _allGoals = MutableStateFlow<List<Goal>>(emptyList())
    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    // StateFlows para diferentes estados de objetivos
    val activeGoals: StateFlow<List<Goal>> = _allGoals.map { goals ->
        goals.filter { it.state == GoalStatus.ACTIVE }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pausedGoals: StateFlow<List<Goal>> = _allGoals.map { goals ->
        goals.filter { it.state == GoalStatus.PAUSED }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val achievedGoals: StateFlow<List<Goal>> = _allGoals.map { goals ->
        goals.filter { it.state == GoalStatus.ACHIEVED }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    init {
        loadGoals()
    }

    fun loadGoals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = apiService.getGoals()

                if (response.isSuccessful && response.body() != null) {
                    _allGoals.value = response.body()!!
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    Log.i("GoalsViewModel", "Goals loaded: ${response.body()!!.size} objetivos")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar objetivos"
                    )
                    Log.e("GoalsViewModel", "Error loading goals: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error del servidor: ${e.code()}"
                )
                Log.e("GoalsViewModel", "HTTP Error: ${e.code()}, Body: $errorBody")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
                Log.e("GoalsViewModel", "Error: ${e.message}", e)
            }
        }
    }

    fun createGoal(
        name: String,
        quantity: Double,
        goalDate: Date,
        currentQuantity: Double = 0.0,
        icon: String,
        note: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingGoal = true, errorMessage = null)

            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val formattedDate = dateFormat.format(goalDate)

                val createGoalRequest = CreateGoalRequestDto(
                    name = name,
                    quantity = quantity,
                    goalDate = formattedDate,
                    icon = icon,
                    note = note
                )

                Log.i("GoalsViewModel", "Creating goal with icon: $icon")

                val response = apiService.createGoal(createGoalRequest)

                if (response.isSuccessful && response.body() != null) {
                    val createdGoal = response.body()!!

                    loadGoals()

                    _uiState.value = _uiState.value.copy(
                        isCreatingGoal = false,
                        successMessage = "Objetivo creado exitosamente"
                    )


                } else {
                    _uiState.value = _uiState.value.copy(
                        isCreatingGoal = false,
                        errorMessage = "Error al crear el objetivo"
                    )
                    Log.e("GoalsViewModel", "Error creating goal: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isCreatingGoal = false,
                    errorMessage = "Error del servidor: ${e.code()}"
                )
                Log.e("GoalsViewModel", "HTTP Error: ${e.code()}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCreatingGoal = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
                Log.e("GoalsViewModel", "Error: ${e.message}", e)
            }
        }
    }

    fun updateGoal(
        goalId: Int,
        name: String? = null,
        quantity: Double? = null,
        goalDate: Date? = null,
        currentQuantity: Double? = null,
        icon: String? = null,
        state: String? = null,
        note: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingGoal = true, errorMessage = null)

            try {
                val formattedDate = goalDate?.let {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(it)
                }

                val updateRequest = UpdateGoalRequestDto(
                    name = name,
                    quantity = quantity,
                    goalDate = formattedDate,
                    currentQuantity = currentQuantity,
                    icon = icon,
                    state = state,
                    note = note
                )

                val response = apiService.updateGoal(goalId.toString(), updateRequest)

                if (response.isSuccessful && response.body() != null) {

                    loadGoals()

                    _uiState.value = _uiState.value.copy(
                        isUpdatingGoal = false,
                        successMessage = "Objetivo actualizado exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingGoal = false,
                        errorMessage = "Error al actualizar el objetivo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdatingGoal = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingGoal = true, errorMessage = null)

            try {
                val response = apiService.deleteGoal(goalId.toString())

                if (response.isSuccessful) {

                    loadGoals()

                    _uiState.value = _uiState.value.copy(
                        isDeletingGoal = false,
                        successMessage = "Objetivo eliminado exitosamente"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isDeletingGoal = false,
                        errorMessage = "Error al eliminar el objetivo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeletingGoal = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun updateGoalLocally(updatedGoal: Goal) {
        val currentGoals = _allGoals.value.toMutableList()
        val index = currentGoals.indexOfFirst { it.id == updatedGoal.id }
        if (index != -1) {
            currentGoals[index] = updatedGoal
            _allGoals.value = currentGoals
            Log.i("GoalsViewModel", "Goal updated locally: ${updatedGoal.name}")
        }
    }

    fun loadGoalById(id: Int) {
        _selectedGoal.value = _allGoals.value.find { it.id == id }
    }

    fun clearSelectedGoal() {
        _selectedGoal.value = null
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun refreshGoals() {
        loadGoals()
    }

    fun getIconVector(iconName: String): ImageVector {
        return when (iconName.lowercase()) {
            "home" -> Icons.Filled.Home
            "directions_car", "car" -> Icons.Filled.DirectionsCar
            "savings" -> Icons.Filled.Savings
            "smartphone", "phone" -> Icons.Filled.PhoneAndroid
            "card_giftcard", "gift" -> Icons.Filled.CardGiftcard
            "beach_access", "beach" -> Icons.Filled.BeachAccess
            "school" -> Icons.Filled.School
            "build" -> Icons.Filled.Build
            "flight" -> Icons.Filled.Flight
            "star" -> Icons.Filled.Star
            "favorite", "heart" -> Icons.Filled.Favorite
            "account_balance_wallet", "wallet" -> Icons.Filled.AccountBalanceWallet
            "lightbulb" -> Icons.Filled.Lightbulb
            "pets" -> Icons.Filled.Pets
            "computer" -> Icons.Filled.Computer
            "book" -> Icons.Filled.Book
            "fastfood" -> Icons.Filled.Fastfood
            "fitness_center" -> Icons.Filled.FitnessCenter
            "music_note" -> Icons.Filled.MusicNote
            "local_hospital" -> Icons.Filled.LocalHospital
            "shopping_cart" -> Icons.Filled.ShoppingCart
            else -> Icons.Filled.Flag
        }
    }

    fun getIconName(icon: ImageVector): String {
        return when (icon) {
            Icons.Filled.Home -> "home"
            Icons.Filled.DirectionsCar -> "directions_car"
            Icons.Filled.Savings -> "savings"
            Icons.Filled.PhoneAndroid -> "smartphone"
            Icons.Filled.CardGiftcard -> "card_giftcard"
            Icons.Filled.BeachAccess -> "beach_access"
            Icons.Filled.School -> "school"
            Icons.Filled.Build -> "build"
            Icons.Filled.Flight -> "flight"
            Icons.Filled.Star -> "star"
            Icons.Filled.Favorite -> "favorite"
            Icons.Filled.AccountBalanceWallet -> "account_balance_wallet"
            Icons.Filled.Lightbulb -> "lightbulb"
            Icons.Filled.Pets -> "pets"
            Icons.Filled.Computer -> "computer"
            Icons.Filled.Book -> "book"
            Icons.Filled.Fastfood -> "fastfood"
            Icons.Filled.FitnessCenter -> "fitness_center"
            Icons.Filled.MusicNote -> "music_note"
            Icons.Filled.LocalHospital -> "local_hospital"
            Icons.Filled.ShoppingCart -> "shopping_cart"
            else -> "flag"
        }
    }
}