package com.moviles.ticowallet.viewmodel.movements

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.MovementGet
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MovementViewModel(application: Application) : AndroidViewModel(application){
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
}