package com.moviles.ticowallet.ui.warranties

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.ticowallet.models.Warranty
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWarrantyScreen(
    warranty: Warranty? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: (String, Double, Date, Date, String) -> Unit = { _, _, _, _, _ -> },
    isLoading: Boolean = false
) {
    val isEditMode = warranty != null
    val context = LocalContext.current

    var name by remember { mutableStateOf(warranty?.name ?: "") }
    var price by remember { mutableStateOf(warranty?.price?.toString() ?: "") }
    var purchaseDate by remember { mutableStateOf(warranty?.purchaseDate ?: Date()) }
    var expirationDate by remember { mutableStateOf(warranty?.expirationDate ?: Date()) }
    var selectedIcon by remember { mutableStateOf(warranty?.icon ?: "computer") }

    var showPurchaseDatePicker by remember { mutableStateOf(false) }
    var showExpirationDatePicker by remember { mutableStateOf(false) }
    var showIconSelector by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val isFormValid = name.isNotBlank() &&
            price.isNotBlank() &&
            price.toDoubleOrNull() != null &&
            price.toDoubleOrNull()!! > 0 &&
            expirationDate.after(purchaseDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF27496D))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isEditMode) "Editar Garantía" else "Nueva Garantía",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        if (isFormValid) {
                            onSaveClick(
                                name,
                                price.toDouble(),
                                purchaseDate,
                                expirationDate,
                                selectedIcon
                            )
                        }
                    },
                    enabled = isFormValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (isEditMode) "Actualizar" else "Guardar",
                            color = if (isFormValid) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Icono",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showIconSelector = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconForName(selectedIcon),
                            contentDescription = "Icono seleccionado",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Toca para cambiar",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = price,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        price = it
                    }
                },
                label = { Text("Precio (₡)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                singleLine = true,
                prefix = { Text("₡ ", color = Color.White.copy(alpha = 0.7f)) }
            )

            OutlinedTextField(
                value = dateFormatter.format(purchaseDate),
                onValueChange = { },
                label = { Text("Fecha de compra") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPurchaseDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            )

            OutlinedTextField(
                value = dateFormatter.format(expirationDate),
                onValueChange = { },
                label = { Text("Fecha de vencimiento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showExpirationDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledBorderColor = Color.White.copy(alpha = 0.5f),
                    disabledLabelColor = Color.White.copy(alpha = 0.7f)
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                isError = !expirationDate.after(purchaseDate),
                supportingText = if (!expirationDate.after(purchaseDate)) {
                    { Text("La fecha de vencimiento debe ser posterior a la de compra", color = Color.Red) }
                } else null
            )

            if (isFormValid) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Formulario válido - Listo para guardar",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    if (showPurchaseDatePicker) {
        CustomDatePickerDialog(
            title = "Fecha de Compra",
            initialDate = purchaseDate,
            maxDate = Date(),
            onDateSelected = { selectedDate ->
                purchaseDate = selectedDate
                showPurchaseDatePicker = false
            },
            onDismiss = { showPurchaseDatePicker = false }
        )
    }

    if (showExpirationDatePicker) {
        CustomDatePickerDialog(
            title = "Fecha de Vencimiento",
            initialDate = expirationDate,
            minDate = purchaseDate,
            onDateSelected = { selectedDate ->
                expirationDate = selectedDate
                showExpirationDatePicker = false
            },
            onDismiss = { showExpirationDatePicker = false }
        )
    }

    if (showIconSelector) {
        IconSelectorDialog(
            selectedIcon = selectedIcon,
            onIconSelected = {
                selectedIcon = it
                showIconSelector = false
            },
            onDismiss = { showIconSelector = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    title: String,
    initialDate: Date,
    minDate: Date? = null,
    maxDate: Date? = null,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }

    val initialDateMillis = initialDate.time
    val minDateMillis = minDate?.time
    val maxDateMillis = maxDate?.time

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val minValid = minDateMillis?.let { utcTimeMillis >= it } ?: true
                val maxValid = maxDateMillis?.let { utcTimeMillis <= it } ?: true
                return minValid && maxValid
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
                    }
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun IconSelectorDialog(
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val icons = listOf(
        "computer" to Icons.Default.Computer,
        "keyboard" to Icons.Default.Keyboard,
        "tv" to Icons.Default.Tv,
        "phone" to Icons.Default.Phone,
        "monitor" to Icons.Default.Monitor,
        "speaker" to Icons.Default.Speaker,
        "fan" to Icons.Default.Air,
        "camera" to Icons.Default.CameraAlt,
        "headphones" to Icons.Default.Headphones,
        "tablet" to Icons.Default.Tablet
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar icono") },
        text = {
            Column {
                icons.chunked(3).forEach { rowIcons ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rowIcons.forEach { (iconName, icon) ->
                            IconButton(
                                onClick = { onIconSelected(iconName) },
                                modifier = Modifier
                                    .background(
                                        color = if (selectedIcon == iconName)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = iconName,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

fun getIconForName(iconName: String): ImageVector {
    return when (iconName) {
        "computer" -> Icons.Default.Computer
        "keyboard" -> Icons.Default.Keyboard
        "tv" -> Icons.Default.Tv
        "phone" -> Icons.Default.Phone
        "monitor" -> Icons.Default.Monitor
        "speaker" -> Icons.Default.Speaker
        "fan" -> Icons.Default.Air
        "camera" -> Icons.Default.CameraAlt
        "headphones" -> Icons.Default.Headphones
        "tablet" -> Icons.Default.Tablet
        else -> Icons.Default.DeviceUnknown
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditWarrantyScreenPreview() {
    MaterialTheme {
        AddEditWarrantyScreen()
    }
}