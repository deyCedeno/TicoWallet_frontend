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


data class UserUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class AuthUiState(
    val isLoading: Boolean = false,
    val isRegistering: Boolean = false,
    val isSendingCode: Boolean = false,
    val isResettingPassword: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val validationErrors: Map<String, String> = emptyMap()
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()


    private fun validateRegistration(user: User): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (user.name.isBlank()) {
            errors["name"] = "El nombre es requerido"
        } else if (user.name.length < 2) {
            errors["name"] = "El nombre debe tener al menos 2 caracteres"
        }

        if (user.email.isBlank()) {
            errors["email"] = "El email es requerido"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
            errors["email"] = "Formato de email inválido"
        }

        if (user.password.isNullOrBlank()) {
            errors["password"] = "La contraseña es requerida"
        } else {
            val password = user.password!!
            when {
                password.length < 6 ->
                    errors["password"] = "La contraseña debe tener al menos 6 caracteres"
                password.length > 50 ->
                    errors["password"] = "La contraseña es demasiado larga (máximo 50 caracteres)"
                !password.any { it.isLetterOrDigit() } ->
                    errors["password"] = "La contraseña debe contener al menos una letra o número"
            }
        }

        if (user.confirmPassword.isNullOrBlank()) {
            errors["confirmPassword"] = "Confirmar contraseña es requerido"
        } else if (user.password != user.confirmPassword) {
            errors["confirmPassword"] = "Las contraseñas no coinciden"
        }

        return errors
    }


    private fun validateLogin(user: User): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (user.email.isBlank()) {
            errors["email"] = "El email es requerido"
        }

        if (user.password.isNullOrBlank()) {
            errors["password"] = "La contraseña es requerida"
        }

        return errors
    }


    private fun validateResetPassword(user: User): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (user.code.isNullOrBlank()) {
            errors["code"] = "El código de verificación es requerido"
        } else if (user.code!!.length < 4) {
            errors["code"] = "El código debe tener al menos 4 dígitos"
        }

        if (user.password.isNullOrBlank()) {
            errors["password"] = "La nueva contraseña es requerida"
        } else {
            val password = user.password!!
            when {
                password.length < 6 ->
                    errors["password"] = "La contraseña debe tener al menos 6 caracteres"
                password.length > 50 ->
                    errors["password"] = "La contraseña es demasiado larga (máximo 50 caracteres)"
                !password.any { it.isLetterOrDigit() } ->
                    errors["password"] = "La contraseña debe contener al menos una letra o número"
            }
        }

        if (user.confirmPassword.isNullOrBlank()) {
            errors["confirmPassword"] = "Confirmar nueva contraseña es requerido"
        } else if (user.password != user.confirmPassword) {
            errors["confirmPassword"] = "Las contraseñas no coinciden"
        }

        return errors
    }


    fun addUser(user: User, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {

            val validationErrors = validateRegistration(user)
            if (validationErrors.isNotEmpty()) {
                _authState.value = _authState.value.copy(
                    validationErrors = validationErrors,
                    errorMessage = "Por favor corrige los errores"
                )
                return@launch
            }

            _authState.value = _authState.value.copy(
                isRegistering = true,
                errorMessage = null,
                validationErrors = emptyMap()
            )

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

                _authState.value = _authState.value.copy(
                    isRegistering = false,
                    successMessage = "Cuenta creada exitosamente. ¡Ya puedes iniciar sesión!"
                )
                onSuccess()
                Log.i("ViewModelInfo", "Registration successful: ${response}")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.code()}, Response Body: $errorBody")


                val errorMsg = try {
                    when {
                        errorBody?.contains("email", ignoreCase = true) == true &&
                                errorBody.contains("already", ignoreCase = true) -> "Este email ya está registrado"

                        errorBody?.contains("password", ignoreCase = true) == true ->
                            "La contraseña no cumple con los requisitos"

                        errorBody?.contains("validation", ignoreCase = true) == true ->
                            "Por favor verifica los datos ingresados"

                        e.code() == 400 -> "Datos inválidos. Verifica la información"
                        e.code() == 422 -> "Error de validación en los datos"
                        e.code() == 409 -> "Este email ya está registrado"
                        else -> "Error al crear cuenta"
                    }
                } catch (ex: Exception) {
                    "Error al crear cuenta"
                }

                _authState.value = _authState.value.copy(
                    isRegistering = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)

            } catch (e: Exception) {
                val errorMsg = "Error de conexión. Verifica tu internet"
                _authState.value = _authState.value.copy(
                    isRegistering = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }


    fun signIn(user: User, onSuccess: (User) -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {

            val validationErrors = validateLogin(user)
            if (validationErrors.isNotEmpty()) {
                _authState.value = _authState.value.copy(
                    validationErrors = validationErrors,
                    errorMessage = "Por favor completa todos los campos"
                )
                return@launch
            }

            _authState.value = _authState.value.copy(
                isLoading = true,
                errorMessage = null,
                validationErrors = emptyMap()
            )

            try {
                Log.i("ViewModelInfo", "User: ${user}")
                val response = RetrofitInstance.api.signIn(user)

                val token = response.headers()["authorization"]?.removePrefix("Bearer ")?.trim()
                if (!token.isNullOrEmpty()) {
                    Constants.AUTH_TOKEN = token
                    Log.i("ViewModelInfo", "Token saved: $token")
                }

                val userBody = response.body()
                if (userBody != null) {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        successMessage = "¡Bienvenido ${userBody.name}!"
                    )
                    onSuccess(userBody)
                } else {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        errorMessage = "Respuesta vacía del servidor"
                    )
                    onError("Respuesta vacía del servidor")
                }

            } catch (e: HttpException) {
                val errorMsg = when (e.code()) {
                    401 -> "Email o contraseña incorrectos"
                    404 -> "Usuario no encontrado"
                    422 -> "Datos inválidos"
                    else -> "Error al iniciar sesión"
                }

                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Code: ${e.code()}")

            } catch (e: Exception) {
                val errorMsg = "Error de conexión. Verifica tu internet"
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }


    fun sendCode(user: User, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            if (user.email.isBlank()) {
                _authState.value = _authState.value.copy(
                    errorMessage = "El email es requerido",
                    validationErrors = mapOf("email" to "El email es requerido")
                )
                return@launch
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                _authState.value = _authState.value.copy(
                    errorMessage = "Email inválido",
                    validationErrors = mapOf("email" to "Email inválido")
                )
                return@launch
            }

            _authState.value = _authState.value.copy(
                isSendingCode = true,
                errorMessage = null,
                validationErrors = emptyMap()
            )

            try {
                Log.i("ViewModelInfo", "Sending code to: ${user.email}")
                val response = RetrofitInstance.api.sendCode(user)

                _authState.value = _authState.value.copy(
                    isSendingCode = false,
                    successMessage = "Código enviado a ${user.email}"
                )
                onSuccess()
                Log.i("ViewModelInfo", "Code sent successfully")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error sending code: ${e.code()}, Body: $errorBody")

                val errorMsg = try {
                    when {
                        errorBody?.contains("email", ignoreCase = true) == true &&
                                errorBody.contains("not found", ignoreCase = true) -> "Email no registrado en el sistema"

                        errorBody?.contains("email", ignoreCase = true) == true &&
                                errorBody.contains("invalid", ignoreCase = true) -> "Formato de email inválido"

                        e.code() == 404 -> "Email no registrado en el sistema"
                        e.code() == 422 -> "Formato de email inválido"
                        e.code() == 429 -> "Demasiados intentos. Espera un momento e intenta de nuevo"
                        else -> "Error al enviar código. Intenta de nuevo"
                    }
                } catch (ex: Exception) {
                    "Error al enviar código"
                }

                _authState.value = _authState.value.copy(
                    isSendingCode = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)

            } catch (e: Exception) {
                val errorMsg = "Error de conexión. Verifica tu internet"
                _authState.value = _authState.value.copy(
                    isSendingCode = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
                Log.e("ViewModelError", "Error sending code: ${e.message}", e)
            }
        }
    }

    fun resetPassword(user: User, onSuccess: (User) -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {

            val validationErrors = validateResetPassword(user)
            if (validationErrors.isNotEmpty()) {
                _authState.value = _authState.value.copy(
                    validationErrors = validationErrors,
                    errorMessage = "Por favor corrige los errores"
                )
                return@launch
            }

            _authState.value = _authState.value.copy(
                isResettingPassword = true,
                errorMessage = null,
                validationErrors = emptyMap()
            )

            try {
                val userResetPasswordDto = ResetPasswordRequestDto(
                    email = user.email,
                    code = user.code ?: "",
                    newPassword = user.password ?: "",
                    confirmPassword = user.confirmPassword ?: ""
                )

                val response = RetrofitInstance.api.resetPassword(userResetPasswordDto)

                _authState.value = _authState.value.copy(
                    isResettingPassword = false,
                    successMessage = "Contraseña restablecida exitosamente"
                )
                onSuccess(response)
                Log.i("ViewModelInfo", "Password reset successful")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error resetting password: ${e.code()}, Body: $errorBody")

                val errorMsg = try {
                    when {
                        errorBody?.contains("code", ignoreCase = true) == true &&
                                (errorBody.contains("invalid", ignoreCase = true) ||
                                        errorBody.contains("expired", ignoreCase = true)) -> "Código inválido o expirado"

                        errorBody?.contains("password", ignoreCase = true) == true ->
                            "Error con la nueva contraseña"

                        e.code() == 400 -> "Código inválido o expirado"
                        e.code() == 404 -> "Usuario no encontrado"
                        e.code() == 422 -> "Datos inválidos. Verifica el código y contraseña"
                        else -> "Error al cambiar la contraseña"
                    }
                } catch (ex: Exception) {
                    "Error al cambiar la contraseña"
                }

                _authState.value = _authState.value.copy(
                    isResettingPassword = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)

            } catch (e: Exception) {
                val errorMsg = "Error de conexión. Verifica tu internet"
                _authState.value = _authState.value.copy(
                    isResettingPassword = false,
                    errorMessage = errorMsg
                )
                onError(errorMsg)
                Log.e("ViewModelError", "Error resetting password: ${e.message}", e)
            }
        }
    }

    fun clearAuthMessages() {
        _authState.value = _authState.value.copy(
            errorMessage = null,
            successMessage = null,
            validationErrors = emptyMap()
        )
    }


    fun clearAllStates() {
        _authState.value = AuthUiState()
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
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