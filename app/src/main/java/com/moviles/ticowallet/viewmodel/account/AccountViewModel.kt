package com.moviles.ticowallet.viewmodel.account

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.HomePageResponse
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(application: Application) : AndroidViewModel(application){
    fun getAllHome(onSuccess: (HomePageResponse) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllHome()
                Log.i("ViewModelInfo", "Response: ${response}")
                onSuccess(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
                onError("No hay registros.")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                onError("Error inesperado al obtener los datos del inicio")
            }
        }
    }
}