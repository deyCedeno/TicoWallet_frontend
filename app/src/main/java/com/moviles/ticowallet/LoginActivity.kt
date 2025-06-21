package com.moviles.ticowallet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.ticowallet.models.User
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.viewmodel.user.UserViewModel

private val screenBgColor = Color(0xFF27496d)
private val textFieldContainerColor = Color(0xFF122850)
private val textFieldTextColor = Color.White
private val textFieldLabelColor = Color.White.copy(alpha = 0.7f)
private val textFieldCursorColor = Color.White
private val buttonBackgroundColor = Color(0xFF0c7b93)
private val onButtonBackgroundColor = Color.White

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicoWalletTheme {
                val viewModel: UserViewModel = viewModel()
                LoginScreen(viewModel) { destination ->
                    startActivity(Intent(this, destination))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: UserViewModel, onNavigate: (Class<*>) -> Unit) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // Limpiar estados al iniciar
    LaunchedEffect(Unit) {
        viewModel.clearAuthMessages()
    }

    // Mostrar diálogo de éxito
    LaunchedEffect(authState.successMessage) {
        if (authState.successMessage != null) {
            showSuccessDialog = true
        }
    }

    // Mostrar diálogo de error
    LaunchedEffect(authState.errorMessage) {
        if (authState.errorMessage != null) {
            showErrorDialog = true
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        Toast.makeText(context, "Inicio exitoso.", Toast.LENGTH_SHORT).show()
    }

    // Diálogo de error
    if (showErrorDialog) {
        Toast.makeText(context, "Error al iniciar sesión.", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // Campo Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Correo", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("email")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("email")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("email"),
                enabled = !authState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Error de email
            authState.validationErrors["email"]?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Contraseña", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = textFieldLabelColor
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("password")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("password")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("password"),
                enabled = !authState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Error de contraseña
            authState.validationErrors["password"]?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Iniciar Sesión
            Button(
                onClick = {
                    val loginUser = User(null, email, password, "", "", "", "")
                    viewModel.signIn(
                        user = loginUser,
                        onSuccess = { user ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra("user", user)
                            context.startActivity(intent)
                            if (context is ComponentActivity) {
                                context.finish()
                            }
                        }
                    )
                },
                enabled = !authState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = onButtonBackgroundColor
                )
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = onButtonBackgroundColor,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciando sesión...")
                } else {
                    Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
                }
            }

            // Enlace de recuperar contraseña
            TextButton(
                onClick = { onNavigate(RecoverPasswordPUneActivity::class.java) },
                enabled = !authState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = TextDecoration.Underline,
                    color = Color.White.copy(alpha = if (authState.isLoading) 0.5f else 1f)
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                color = Color.White.copy(alpha = 0.5f)
            )

            // Botón Registrarse
            OutlinedButton(
                onClick = { onNavigate(RegisterActivity::class.java) },
                enabled = !authState.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
            ) {
                Text(
                    "¿No tienes una cuenta? Regístrate",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = if (authState.isLoading) 0.5f else 1f)
                )
            }
        }
    }
}