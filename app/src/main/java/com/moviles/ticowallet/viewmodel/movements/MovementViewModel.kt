package com.moviles.ticowallet.viewmodel.movements

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.models.Category
import com.moviles.ticowallet.models.CreateMovementDto
import com.moviles.ticowallet.models.MovementGet
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MovementViewModel(application: Application) : AndroidViewModel(application){
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _userAccounts = MutableStateFlow<List<Account>>(emptyList())
    val userAccounts: StateFlow<List<Account>> = _userAccounts.asStateFlow()

    fun getAllMovements(onSuccess: (List<MovementGet>) -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllMovements()
                Log.i("ViewModelInfo", "Response: ${response}")
                onSuccess(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
                onError("No hay registros.")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                onError("Error inesperado al obtener los movimientos.")
            }
        }
    }

    fun deleteMovement(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteMovement(id)
                Log.i("ViewModelInfo", "Response: ${response}")
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
                onError("No hay registros.")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                onError("Error inesperado al eliminar el movimiento.")
            }
        }
    }

    fun createMovement(movement: CreateMovementDto) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createMovement(movement)
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun loadData(){
        viewModelScope.launch {

            try {

                // Load user accounts and categories in parallel
                val accounts = async { RetrofitInstance.api.getUserAccounts() }
                val categories = async { RetrofitInstance.api.getCategories() }

                val accountsResult = accounts.await()
                val categoriesResult = categories.await()

                _userAccounts.value = accountsResult
                _categories.value = categoriesResult


            } catch (e: HttpException) {



            } catch (e: Exception) {
                val errorMessage = "Error cargando datos: ${e.message}"


            } finally {

            }
        }
    }


}