package com.moviles.ticowallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicoWalletTheme {
                val viewModel: UserViewModel = viewModel()
                RegisterScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: UserViewModel) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.clearAuthMessages()
    }


    LaunchedEffect(authState.successMessage) {
        if (authState.successMessage != null) {
            showSuccessDialog = true
        }
    }


    LaunchedEffect(authState.errorMessage) {
        if (authState.errorMessage != null) {
            showErrorDialog = true
        }
    }


    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {  },
            containerColor = Color.White,
            title = {
                Text(
                    text = "¡Registro Exitoso!",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = authState.successMessage ?: "",
                    color = Color.Black
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.clearAuthMessages()

                        context.startActivity(Intent(context, LoginActivity::class.java))
                        if (context is ComponentActivity) {
                            context.finish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Ir a Iniciar Sesión", color = Color.White)
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            containerColor = Color.White,
            title = {
                Text(
                    text = "Error en el Registro",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = authState.errorMessage ?: "",
                    color = Color.Black
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                        viewModel.clearAuthMessages()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    if (context is ComponentActivity) {
                        context.finish()
                    }
                },
                enabled = !authState.isRegistering
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver atrás",
                    tint = Color.White.copy(alpha = if (authState.isRegistering) 0.5f else 1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Crear Cuenta",
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

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Nombre completo", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("name")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("name")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("name"),
                enabled = !authState.isRegistering,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )


            authState.validationErrors["name"]?.let { error ->
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


            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Correo electrónico", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("email")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("email")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("email"),
                enabled = !authState.isRegistering,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

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
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !authState.isRegistering
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = textFieldLabelColor.copy(alpha = if (authState.isRegistering) 0.5f else 1f)
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
                enabled = !authState.isRegistering,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Confirmar contraseña", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                        enabled = !authState.isRegistering
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = textFieldLabelColor.copy(alpha = if (authState.isRegistering) 0.5f else 1f)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("confirmPassword")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("confirmPassword")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("confirmPassword"),
                enabled = !authState.isRegistering,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            authState.validationErrors["confirmPassword"]?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val registerUser = User(null, email, password, confirmPassword, name, "", "")
                    viewModel.addUser(registerUser)
                },
                enabled = !authState.isRegistering,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = onButtonBackgroundColor
                )
            ) {
                if (authState.isRegistering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = onButtonBackgroundColor,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Crear cuenta", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    if (context is ComponentActivity) {
                        context.finish()
                    }
                },
                enabled = !authState.isRegistering
            ) {
                Text(
                    text = "¿Ya tienes una cuenta? Inicia sesión",
                    color = Color.White.copy(alpha = if (authState.isRegistering) 0.5f else 1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}