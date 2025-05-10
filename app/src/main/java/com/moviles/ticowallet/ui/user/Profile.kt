package com.moviles.ticowallet.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.ticowallet.common.Constants
import com.moviles.ticowallet.ui.theme.TicoWalletTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(onNavigateBack: () -> Unit = {}, onLogout: () -> Unit = {}) {
    TicoWalletTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ajustes") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            },
            bottomBar = {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0c7b93))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(8.dp)
                )
                TextButton(onClick = { }) {
                    Text("Editar foto", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = Constants.USERNAME,
                    onValueChange = { },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = Constants.EMAIL,
                    onValueChange = {},
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
