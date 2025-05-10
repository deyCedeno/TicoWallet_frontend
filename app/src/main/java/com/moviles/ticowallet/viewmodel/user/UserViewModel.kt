package com.moviles.ticowallet.viewmodel.user

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.DAO.ResetPasswordRequestDto
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import com.moviles.ticowallet.models.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UserViewModel(application: Application) : AndroidViewModel(application){
    fun addUser(user: User){
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "User: ${user}")
                val context = getApplication<Application>().applicationContext
                val requestParts = createUserRequestBody(user, context)

                val response = RetrofitInstance.api.addUser(
                    requestParts[0] as RequestBody,
                    requestParts[1] as RequestBody,
                    requestParts[2] as RequestBody,
                    requestParts[3] as RequestBody
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

    fun signIn(user: User, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "User: ${user}")

                val response = RetrofitInstance.api.signIn(user)
                Log.i("ViewModelInfo", "Response: ${response}")
                onSuccess(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
                onError("Credenciales inválidas")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                onError("Error inesperado")
            }
        }
    }

    fun sendCode(user: User) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "User: ${user}")
                val response = RetrofitInstance.api.sendCode(user)
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun resetPassword(user: User, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "User: ${user}")
                val userResetPasswordDto = ResetPasswordRequestDto(
                    email = user.email ?: "",
                    code = user.code ?: "",
                    newPassword = user.password ?: "",
                    confirmPassword = user.confirmPassword ?: ""
                )
                val response = RetrofitInstance.api.resetPassword(userResetPasswordDto)
                onSuccess(response)
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                onError("Error al cambiar la contraseña.")
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

}

fun Uri.getPath(context: Context): String? {
    val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, projection, null, null, null)
    return cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            it.getString(columnIndex)
        } else {
            null
        }
    }
}

fun createUserRequestBody(
    user: User,
    context: Context
): List<Any?> {
    val name = user.name.toRequestBody("text/plain".toMediaTypeOrNull())
    val email = user.email.toRequestBody("text/plain".toMediaTypeOrNull())
    val password = user.password?.toRequestBody("text/plain".toMediaTypeOrNull())
    val confirmPassword = user.confirmPassword?.toRequestBody("text/plain".toMediaTypeOrNull())

    return listOf(name, email, password, confirmPassword)
}

