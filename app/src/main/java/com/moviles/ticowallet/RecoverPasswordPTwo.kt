package com.moviles.ticowallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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

class RecoverPasswordPTwoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicoWalletTheme {
                val email = intent.getStringExtra("email")
                val viewModel: UserViewModel = viewModel()
                RecoverPasswordPTwoScreen(viewModel, email ?: "")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordPTwoScreen(viewModel: UserViewModel, email: String) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
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
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Contraseña Restablecida!",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = authState.successMessage ?: "",
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ya puedes iniciar sesión con tu nueva contraseña.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.clearAuthMessages()
                        // Navegar al login
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        if (context is ComponentActivity) context.finish()
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
                    text = "Error al Restablecer",
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
                    if (context is ComponentActivity) context.finish()
                },
                enabled = !authState.isResettingPassword
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White.copy(alpha = if (authState.isResettingPassword) 0.5f else 1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(72.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Nueva Contraseña",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Hemos enviado un código a:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {

            OutlinedTextField(
                value = code,
                onValueChange = {
                    code = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Código de verificación", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = null,
                        tint = textFieldLabelColor
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("code")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("code")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("code"),
                enabled = !authState.isResettingPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )


            authState.validationErrors["code"]?.let { error ->
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
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Nueva contraseña", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = textFieldLabelColor
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        enabled = !authState.isResettingPassword
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = textFieldLabelColor.copy(alpha = if (authState.isResettingPassword) 0.5f else 1f)
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
                enabled = !authState.isResettingPassword,
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
                value = confirmNewPassword,
                onValueChange = {
                    confirmNewPassword = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Confirmar contraseña", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = textFieldLabelColor
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                        enabled = !authState.isResettingPassword
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = textFieldLabelColor.copy(alpha = if (authState.isResettingPassword) 0.5f else 1f)
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
                enabled = !authState.isResettingPassword,
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
                    val recovUser = User(null, email, newPassword, confirmNewPassword, "", "", code)
                    viewModel.resetPassword(recovUser, onSuccess = { user ->

                    })
                },
                enabled = !authState.isResettingPassword,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = onButtonBackgroundColor
                )
            ) {
                if (authState.isResettingPassword) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = onButtonBackgroundColor,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restableciendo...")
                } else {
                    Text("Restablecer contraseña", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            TextButton(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    if (context is ComponentActivity) context.finish()
                },
                enabled = !authState.isResettingPassword
            ) {
                Text(
                    text = "Volver al inicio de sesión",
                    color = Color.White.copy(alpha = if (authState.isResettingPassword) 0.5f else 1f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔒 Seguridad",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "El código es válido por 15 minutos. Tu nueva contraseña debe tener al menos 6 caracteres.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}