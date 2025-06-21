package com.moviles.ticowallet.viewmodel.goals

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.DAO.CreateGoalContributionDto
import com.moviles.ticowallet.DAO.GoalContributionDto
import com.moviles.ticowallet.models.Goal
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

data class GoalDetailUiState(
    val isLoading: Boolean = false,
    val goal: Goal? = null,
    val contributions: List<GoalContributionDto> = emptyList(),
    val showAddContributionDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String? = null,
    val isAddingContribution: Boolean = false,
    val isDeleting: Boolean = false
)

class GoalDetailViewModel : ViewModel() {
    private val apiService = RetrofitInstance.apiServiceGoals

    private val _uiState = MutableStateFlow(GoalDetailUiState())
    val uiState: StateFlow<GoalDetailUiState> = _uiState.asStateFlow()

    private var _onGoalUpdated: ((Goal) -> Unit)? = null
    private var _onGoalDeleted: ((Int) -> Unit)? = null

    fun setCallbacks(
        onGoalUpdated: (Goal) -> Unit,
        onGoalDeleted: (Int) -> Unit
    ) {
        _onGoalUpdated = onGoalUpdated
        _onGoalDeleted = onGoalDeleted
    }

    fun loadGoalDetail(goalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = apiService.getGoalById(goalId)

                if (response.isSuccessful && response.body() != null) {
                    val goalDetail = response.body()!!
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        goal = goalDetail.goal,
                        contributions = goalDetail.contributions
                    )
                    Log.i("GoalDetailViewModel", "Goal loaded: ${goalDetail.goal}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar el objetivo"
                    )
                }
            } catch (e: HttpException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error del servidor: ${e.code()}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun addContribution(goalId: String, amount: Double, description: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAddingContribution = true, errorMessage = null)

            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val currentDate = dateFormat.format(Date())

                val contribution = CreateGoalContributionDto(
                    amount = amount,
                    contributionDate = currentDate,
                    description = description.takeIf { it.isNotBlank() }
                )

                val response = apiService.addContribution(goalId, contribution)

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!


                    val updatedContributions = listOf(result.data) + _uiState.value.contributions
                    _uiState.value = _uiState.value.copy(
                        isAddingContribution = false,
                        goal = result.updatedGoal,
                        contributions = updatedContributions,
                        showAddContributionDialog = false
                    )


                    _onGoalUpdated?.invoke(result.updatedGoal)

                    Log.i("GoalDetailViewModel", "Contribution added successfully")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isAddingContribution = false,
                        errorMessage = "Error al agregar el aporte"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddingContribution = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }


    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)

            try {
                val response = apiService.deleteGoal(goalId.toString())

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        showDeleteDialog = false
                    )

                    _onGoalDeleted?.invoke(goalId)

                    Log.i("GoalDetailViewModel", "Goal deleted successfully")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isDeleting = false,
                        errorMessage = "Error al eliminar el objetivo"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun showAddContributionDialog() {
        _uiState.value = _uiState.value.copy(showAddContributionDialog = true)
    }

    fun hideAddContributionDialog() {
        _uiState.value = _uiState.value.copy(showAddContributionDialog = false)
    }

    fun showEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = true)
    }

    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(showEditDialog = false)
    }

    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }

    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}