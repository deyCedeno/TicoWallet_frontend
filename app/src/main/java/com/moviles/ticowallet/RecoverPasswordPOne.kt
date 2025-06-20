package com.moviles.ticowallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

class RecoverPasswordPUneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicoWalletTheme {
                val viewModel: UserViewModel = viewModel()
                RecoverPasswordPUneScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordPUneScreen(viewModel: UserViewModel) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
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
            onDismissRequest = { },
            containerColor = Color.White,
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Código Enviado!",
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
                        text = "Revisa tu bandeja de entrada y carpeta de spam.",
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

                        val intent = Intent(context, RecoverPasswordPTwoActivity::class.java)
                        intent.putExtra("email", email)
                        context.startActivity(intent)
                        if (context is ComponentActivity) context.finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Continuar", color = Color.White)
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
                    text = "Error",
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
            .padding(16.dp),
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
                enabled = !authState.isSendingCode
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.White.copy(alpha = if (authState.isSendingCode) 0.5f else 1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Icon(
            imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(72.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Ingresa tu correo electrónico y te enviaremos un código para restablecer tu contraseña.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearAuthMessages()
                },
                label = { Text("Correo electrónico", color = textFieldLabelColor) },
                textStyle = LocalTextStyle.current.copy(color = textFieldTextColor),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = textFieldLabelColor
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = textFieldContainerColor,
                    focusedContainerColor = textFieldContainerColor,
                    unfocusedBorderColor = if (authState.validationErrors.containsKey("email")) Color.Red else Color.White,
                    focusedBorderColor = if (authState.validationErrors.containsKey("email")) Color.Red else Color.White,
                    cursorColor = textFieldCursorColor
                ),
                isError = authState.validationErrors.containsKey("email"),
                enabled = !authState.isSendingCode,
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

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = {
                    viewModel.sendCode(
                        User(null, email, "", "", "", "", ""),
                        onSuccess = {

                        }
                    )
                },
                enabled = !authState.isSendingCode && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackgroundColor,
                    contentColor = onButtonBackgroundColor
                )
            ) {
                if (authState.isSendingCode) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = onButtonBackgroundColor,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando código...")
                } else {
                    Text("Enviar código", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    if (context is ComponentActivity) context.finish()
                },
                enabled = !authState.isSendingCode
            ) {
                Text(
                    text = "Volver al inicio de sesión",
                    color = Color.White.copy(alpha = if (authState.isSendingCode) 0.5f else 1f),
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
                    text = "💡 Consejo",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Si no recibes el código en unos minutos, revisa tu carpeta de spam o correo no deseado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}