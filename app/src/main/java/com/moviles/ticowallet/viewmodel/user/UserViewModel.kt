package com.moviles.ticowallet.viewmodel.user

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.ticowallet.DAO.ResetPasswordRequestDto
import com.moviles.ticowallet.common.Constants
import com.moviles.ticowallet.network.RetrofitInstance
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import com.moviles.ticowallet.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.moviles.ticowallet.DAO.UpdateUserProfileDto
import com.moviles.ticowallet.DAO.UpdateUserProfileResponse
import com.moviles.ticowallet.DAO.UpdateImageResponse

data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)



class UserViewModel(application: Application) : AndroidViewModel(application){

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

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
                val token = response.headers()["authorization"]?.removePrefix("Bearer ")?.trim()
                if (!token.isNullOrEmpty()) {
                    Constants.AUTH_TOKEN = token
                    Log.i("ViewModelInfo", "Token saved: $token")
                }

                Log.i("ViewModelInfo", "Response: ${response}")
                val userBody = response.body()
                if (userBody != null) {
                    onSuccess(userBody)
                } else {
                    onError("Respuesta vacía del servidor")
                }
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

    fun getUser(onSuccess: (User) -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = RetrofitInstance.api.getUser()
                Log.i("ViewModelInfo", "Response: ${response}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = response
                )
                onSuccess(response)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMsg = "Usuario no encontrado"
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
            } catch (e: Exception) {
                val errorMsg = "Error inesperado al obtener el usuario"
                Log.e("ViewModelError", "Error: ${e.message}", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
            }
        }
    }


    fun updateUserProfile(name: String, email: String) {
        Log.i("ViewModelInfo", "Updating profile - Name: $name, Email: $email")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            try {
                val updateDto = UpdateUserProfileDto(name = name, email = email)
                Log.i("ViewModelInfo", "Sending DTO: $updateDto")

                val response = RetrofitInstance.api.updateUserProfile(updateDto)
                Log.i("ViewModelInfo", "Response received - Success: ${response.isSuccessful}, Code: ${response.code()}")


                if (response.isSuccessful && response.body() != null) {

                    val updatedUser = response.body()!!.user
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        user = updatedUser,
                        isEditing = false,
                        successMessage = "Perfil actualizado exitosamente"
                    )
                    Log.i("ViewModelInfo", "Profile updated successfully: $updatedUser")
                } else {
                    val errorMsg = "Error al actualizar perfil"
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = errorMsg
                    )
                    Log.e("ViewModelError", "Update profile failed: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                Log.e("ViewModelError", "Exception in updateUserProfile: ${e.message}", e)

                val errorBody = e.response()?.errorBody()?.string()
                val errorMsg = when (e.code()) {
                    400 -> "El email ya está en uso"
                    401 -> "No autorizado"
                    else -> "Error al actualizar perfil"
                }
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = errorMsg
                )
                Log.e("ViewModelError", "HTTP Error updating profile: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = errorMsg
                )
                Log.e("ViewModelError", "Error updating profile: ${e.message}", e)
            }
        }
    }

    fun updateUserImage(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            try {
                val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                val response = RetrofitInstance.api.updateUserImage(imagePart)

                if (response.isSuccessful && response.body() != null) {
                    val imageUrl = response.body()!!.url
                    val currentUser = _uiState.value.user
                    if (currentUser != null) {
                        val updatedUser = currentUser.copy(urlImage = imageUrl)
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            user = updatedUser,
                            successMessage = "Imagen actualizada exitosamente"
                        )
                        Log.i("ViewModelInfo", "Image updated successfully: $imageUrl")
                    }
                } else {
                    val errorMsg = "Error al actualizar imagen"
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = errorMsg
                    )
                    Log.e("ViewModelError", "Update image failed: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMsg = "Error al subir imagen"
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = errorMsg
                )
                Log.e("ViewModelError", "HTTP Error updating image: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.message}"
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = errorMsg
                )
                Log.e("ViewModelError", "Error updating image: ${e.message}", e)
            }
        }
    }

    fun setEditingMode(isEditing: Boolean) {
        _uiState.value = _uiState.value.copy(isEditing = isEditing)
        Log.i("ViewModelInfo", "Editing mode set to: $isEditing")
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
        Log.i("ViewModelInfo", "Messages cleared")
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