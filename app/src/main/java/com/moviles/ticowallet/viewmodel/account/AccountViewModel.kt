package com.moviles.ticowallet.viewmodel.account

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.models.Account
import com.moviles.ticowallet.models.User
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AccountViewModel(application: Application) : AndroidViewModel(application){
    fun getAllAccounts(onSuccess: (List<Account>) -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getAllAccounts()
                Log.i("ViewModelInfo", "Response: ${response}")
                onSuccess(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
                onError("No hay registros.")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                onError("Error inesperado al obtener las cuentas.")
            }
        }
    }

    fun deleteAccount(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteAccount(id)
                Log.i("ViewModelInfo", "Response: ${response}")
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
                onError("No hay registros.")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                onError("Error inesperado al eliminar la cuenta.")
            }
        }
    }

    fun addAccount(account: Account) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addAccount(account)
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