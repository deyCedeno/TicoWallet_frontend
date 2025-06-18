// ui/user/UserProfileScreen.kt
package com.moviles.ticowallet.ui.user

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.moviles.ticowallet.ui.theme.TicoWalletTheme
import com.moviles.ticowallet.ui.theme.colorDarkBlue1
import com.moviles.ticowallet.ui.theme.colorTeal
import com.moviles.ticowallet.ui.theme.colorWhite
import com.moviles.ticowallet.viewmodel.user.UserViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var editedName by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val file = File(context.cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                viewModel.updateUserImage(file)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getUser()
    }

    LaunchedEffect(uiState.user) {
        uiState.user?.let { user ->
            editedName = user.name
            editedEmail = user.email
        }
    }

    TicoWalletTheme {
        Scaffold(
            bottomBar = {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorTeal)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión", fontWeight = FontWeight.Bold, color = colorWhite)
                        Icon(Icons.Filled.Logout, contentDescription = "Cerrar sesión", tint = colorWhite)
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (uiState.isEditing) {

                        IconButton(
                            onClick = { viewModel.updateUserProfile(editedName, editedEmail) },
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = colorTeal,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Filled.Check, contentDescription = "Guardar", tint = colorTeal)
                            }
                        }

                        IconButton(onClick = {
                            viewModel.setEditingMode(false)
                            uiState.user?.let { user ->
                                editedName = user.name
                                editedEmail = user.email
                            }
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancelar", tint = colorDarkBlue1)
                        }
                    } else {
                        IconButton(onClick = { viewModel.setEditingMode(true) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = colorTeal)
                        }
                    }
                }

                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.user?.urlImage?.isNotEmpty() == true && uiState.user?.urlImage != "user_not_found.jpg") {
                        val imageUrl = if (uiState.user?.urlImage?.startsWith("http") == true) {
                            uiState.user?.urlImage
                        } else {
                            "http://10.0.2.2:5237/api/images/${uiState.user?.urlImage}"
                        }

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, colorTeal, CircleShape),
                            onError = {
                                Log.e("UserProfile", "Error loading image: $imageUrl")
                            }
                        )
                    } else {
                        Image(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, colorTeal, CircleShape)
                                .padding(16.dp)
                        )
                    }

                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(120.dp),
                            color = colorTeal,
                            strokeWidth = 3.dp
                        )
                    }
                }

                TextButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = !uiState.isSaving
                ) {
                    Text("Editar foto", fontSize = 14.sp, color = colorTeal)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !uiState.isEditing,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = colorDarkBlue1.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = editedEmail,
                    onValueChange = { editedEmail = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !uiState.isEditing,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorTeal,
                        unfocusedBorderColor = colorDarkBlue1.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                uiState.successMessage?.let { success ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Green.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = success,
                            modifier = Modifier.padding(16.dp),
                            color = Color.Green
                        )
                    }
                }

                LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
                    if (uiState.successMessage != null || uiState.errorMessage != null) {
                        kotlinx.coroutines.delay(3000)
                        viewModel.clearMessages()
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}