package com.moviles.ticowallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.ticowallet.models.User
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.viewmodel.user.UserViewModel

class RecoverPasswordPTwoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicoWalletTheme {
                val email = intent.getStringExtra("email")
                println("Correo para recuperación recibido: $email")
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
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        IconButton(
            onClick = {
                context.startActivity(Intent(context, LoginActivity::class.java))
                if (context is ComponentActivity) {
                    context.finish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.Start)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
        }

        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Código de verificación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    val recovUser = User(null, email, newPassword, confirmNewPassword, "", "", code )
                    viewModel.resetPassword(
                        user = recovUser,
                        onSuccess = { user ->
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            if (context is ComponentActivity) {
                                context.finish()
                            }
                        },
                        onError = { errorMsg ->
                            println("Error al iniciar sesión: $errorMsg")
                        }
                    )
                    println(
                        "Código: $code, Nueva Contraseña: $newPassword, Confirmar Contraseña: $confirmNewPassword"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Restablecer contraseña")
            }
        }
    }
}
