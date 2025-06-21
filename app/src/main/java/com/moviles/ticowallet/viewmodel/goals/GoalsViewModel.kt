package com.moviles.ticowallet.viewmodel.goals

import android.app.Application
import android.util.Log
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Smartphone
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.common.Constants
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException
import java.util.Calendar
import java.util.Date


object GoalStatus {
    const val ACTIVE = "Activo"
    const val PAUSED = "Pausado"
    const val ACHIEVED = "Conseguido"
}
class GoalsViewModel(application: Application) : AndroidViewModel(application) {

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
            val calendar = Calendar.getInstance()
            val futureDate: Date
            val pastDate: Date

            calendar.add(Calendar.MONTH, 3)
            futureDate = calendar.time
            calendar.add(Calendar.MONTH, -6)
            pastDate = calendar.time


            try {

                val response = RetrofitInstance.api.getGoals()
                _allGoals.value = response

                Log.i("ViewModelInfo", "Response goals: ${response}")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }


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

    fun createGoal(goal: Goal) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "Goal: ${goal}")

                val response = RetrofitInstance.api.createGoal(
                    goal
                )
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

}
